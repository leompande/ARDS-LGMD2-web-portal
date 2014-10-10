package org.hisp.dhis.api.controller;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.interpretation.Interpretation;
import org.hisp.dhis.interpretation.InterpretationComment;
import org.hisp.dhis.interpretation.InterpretationService;
import org.hisp.dhis.mapping.Map;
import org.hisp.dhis.mapping.MappingService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.reporttable.ReportTableService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping( value = InterpretationController.RESOURCE_PATH )
public class InterpretationController
    extends AbstractCrudController<Interpretation>
{
    public static final String RESOURCE_PATH = "/interpretations";

    @Autowired
    private InterpretationService interpretationService;

    @Autowired
    private ChartService chartService;

    @Autowired
    private ReportTableService reportTableService;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private MappingService mappingService;

    @Autowired
    private CurrentUserService currentUserService;

    @Override
    protected List<Interpretation> getEntityList( WebMetaData metaData, WebOptions options )
    {
        List<Interpretation> entityList;

        Date lastUpdated = options.getLastUpdated();

        if ( lastUpdated != null )
        {
            entityList = new ArrayList<Interpretation>( interpretationService.getInterpretations( lastUpdated ) );
        }
        else if ( options.hasPaging() )
        {
            int count = manager.getCount( getEntityClass() );

            Pager pager = new Pager( options.getPage(), count );
            metaData.setPager( pager );

            entityList = new ArrayList<Interpretation>( interpretationService.getInterpretations( pager.getOffset(), pager.getPageSize() ) );
        }
        else
        {
            entityList = new ArrayList<Interpretation>( interpretationService.getInterpretations() );
        }

        return entityList;
    }

    @RequestMapping( value = "/chart/{uid}", method = RequestMethod.POST, consumes = { "text/html", "text/plain" } )
    public void shareChartInterpretation(
        @PathVariable( "uid" ) String chartUid,
        @RequestBody String text, HttpServletResponse response )
    {
        Chart chart = chartService.getChart( chartUid );

        if ( chart == null )
        {
            ContextUtils.conflictResponse( response, "Chart identifier not valid: " + chartUid );
            return;
        }

        User user = currentUserService.getCurrentUser();

        // ---------------------------------------------------------------------
        // When chart has user org unit, store current user org unit with
        // interpretation so chart will refer to the original org unit later
        // ---------------------------------------------------------------------

        OrganisationUnit unit = chart.hasUserOrgUnit() && user.hasOrganisationUnit() ? user.getOrganisationUnit() : null;

        Interpretation interpretation = new Interpretation( chart, unit, text );

        interpretationService.saveInterpretation( interpretation );

        ContextUtils.createdResponse( response, "Interpretation created", InterpretationController.RESOURCE_PATH + "/" + interpretation.getUid() );
    }

    @RequestMapping( value = "/map/{uid}", method = RequestMethod.POST, consumes = { "text/html", "text/plain" } )
    public void shareMapInterpretation(
        @PathVariable( "uid" ) String mapUid,
        @RequestBody String text, HttpServletResponse response )
    {
        Map map = mappingService.getMap( mapUid );

        if ( map == null )
        {
            ContextUtils.conflictResponse( response, "Map identifier not valid: " + mapUid );
            return;
        }

        Interpretation interpretation = new Interpretation( map, text );

        interpretationService.saveInterpretation( interpretation );

        ContextUtils.createdResponse( response, "Interpretation created", InterpretationController.RESOURCE_PATH + "/" + interpretation.getUid() );
    }

    @RequestMapping( value = "/reportTable/{uid}", method = RequestMethod.POST, consumes = { "text/html", "text/plain" } )
    public void shareReportTableInterpretation(
        @PathVariable( "uid" ) String reportTableUid,
        @RequestParam( value = "pe", required = false ) String isoPeriod,
        @RequestParam( value = "ou", required = false ) String orgUnitUid,
        @RequestBody String text, HttpServletResponse response )
    {
        ReportTable reportTable = reportTableService.getReportTable( reportTableUid );

        if ( reportTable == null )
        {
            ContextUtils.conflictResponse( response, "Report table identifier not valid: " + reportTableUid );
            return;
        }

        Period period = PeriodType.getPeriodFromIsoString( isoPeriod );

        OrganisationUnit orgUnit = null;

        if ( orgUnitUid != null )
        {
            orgUnit = organisationUnitService.getOrganisationUnit( orgUnitUid );

            if ( orgUnit == null )
            {
                ContextUtils.conflictResponse( response, "Organisation unit identifier not valid: " + orgUnitUid );
                return;
            }
        }

        Interpretation interpretation = new Interpretation( reportTable, period, orgUnit, text );

        interpretationService.saveInterpretation( interpretation );

        ContextUtils.createdResponse( response, "Interpretation created", InterpretationController.RESOURCE_PATH + "/" + interpretation.getUid() );
    }

    @RequestMapping( value = "/dataSetReport/{uid}", method = RequestMethod.POST, consumes = { "text/html", "text/plain" } )
    public void shareDataSetReportInterpretation(
        @PathVariable( "uid" ) String dataSetUid,
        @RequestParam( "pe" ) String isoPeriod,
        @RequestParam( "ou" ) String orgUnitUid,
        @RequestBody String text, HttpServletResponse response )
    {
        DataSet dataSet = dataSetService.getDataSet( dataSetUid );

        if ( dataSet == null )
        {
            ContextUtils.conflictResponse( response, "Data set identifier not valid: " + dataSetUid );
            return;
        }

        Period period = PeriodType.getPeriodFromIsoString( isoPeriod );

        if ( period == null )
        {
            ContextUtils.conflictResponse( response, "Period identifier not valid: " + isoPeriod );
            return;
        }

        OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( orgUnitUid );

        if ( orgUnit == null )
        {
            ContextUtils.conflictResponse( response, "Organisation unit identifier not valid: " + orgUnitUid );
            return;
        }

        Interpretation interpretation = new Interpretation( dataSet, period, orgUnit, text );

        interpretationService.saveInterpretation( interpretation );

        ContextUtils.createdResponse( response, "Interpretation created", InterpretationController.RESOURCE_PATH + "/" + interpretation.getUid() );
    }

    @Override
    public void deleteObject( HttpServletResponse response, HttpServletRequest request, @PathVariable( "uid" ) String uid ) throws Exception
    {
        Interpretation interpretation = interpretationService.getInterpretation( uid );

        if ( interpretation == null )
        {
            ContextUtils.conflictResponse( response, "Interpretation does not exist: " + uid );
            return;
        }

        if ( !currentUserService.getCurrentUser().equals( interpretation.getUser() ) &&
            !currentUserService.currentUserIsSuper() )
        {
            throw new AccessDeniedException( "You are not allowed to delete this interpretation." );
        }

        interpretationService.deleteInterpretation( interpretation );
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT )
    public void updateInterpretation( @PathVariable( "uid" ) String uid, HttpServletResponse response, @RequestBody String content )
    {
        Interpretation interpretation = interpretationService.getInterpretation( uid );

        if ( interpretation == null )
        {
            ContextUtils.conflictResponse( response, "Interpretation does not exist: " + uid );
            return;
        }

        if ( !currentUserService.getCurrentUser().equals( interpretation.getUser() ) &&
            !currentUserService.currentUserIsSuper() )
        {
            throw new AccessDeniedException( "You are not allowed to update this interpretation." );
        }

        System.err.println( content );

        interpretation.setText( content );

        interpretationService.updateInterpretation( interpretation );
    }

    @RequestMapping( value = "/{uid}/comments/{cuid}", method = RequestMethod.DELETE )
    public void deleteComment( @PathVariable( "uid" ) String uid, @PathVariable( "cuid" ) String cuid, HttpServletResponse response )
    {
        Interpretation interpretation = interpretationService.getInterpretation( uid );

        if ( interpretation == null )
        {
            ContextUtils.conflictResponse( response, "Interpretation does not exist: " + uid );
            return;
        }

        Iterator<InterpretationComment> iterator = interpretation.getComments().iterator();

        while ( iterator.hasNext() )
        {
            InterpretationComment comment = iterator.next();

            if ( comment.getUid().equals( cuid ) )
            {
                if ( !currentUserService.getCurrentUser().equals( comment.getUser() ) &&
                    !currentUserService.currentUserIsSuper() )
                {
                    throw new AccessDeniedException( "You are not allowed to delete this comment." );
                }

                iterator.remove();
            }
        }

        interpretationService.updateInterpretation( interpretation );
    }

    @RequestMapping( value = "/{uid}/comments/{cuid}", method = RequestMethod.PUT )
    public void updateComment( @PathVariable( "uid" ) String uid, @PathVariable( "cuid" ) String cuid, HttpServletResponse response,
        @RequestBody String content )
    {
        Interpretation interpretation = interpretationService.getInterpretation( uid );

        if ( interpretation == null )
        {
            ContextUtils.conflictResponse( response, "Interpretation does not exist: " + uid );
            return;
        }

        for ( InterpretationComment comment : interpretation.getComments() )
        {
            if ( comment.getUid().equals( cuid ) )
            {
                if ( !currentUserService.getCurrentUser().equals( comment.getUser() ) &&
                    !currentUserService.currentUserIsSuper() )
                {
                    throw new AccessDeniedException( "You are not allowed to update this comment." );
                }

                comment.setText( content );
            }
        }

        interpretationService.updateInterpretation( interpretation );
    }

    @RequestMapping( value = { "/{uid}/comment", "/{uid}/comments" }, method = RequestMethod.POST, consumes = { "text/html", "text/plain" } )
    public void postComment(
        @PathVariable( "uid" ) String uid,
        @RequestBody String text, HttpServletResponse response )
    {
        Interpretation interpretation = interpretationService.getInterpretation( uid );

        if ( interpretation == null )
        {
            ContextUtils.conflictResponse( response, "Interpretation does not exist: " + uid );
            return;
        }

        InterpretationComment comment = interpretationService.addInterpretationComment( uid, text );

        StringBuilder builder = new StringBuilder();
        builder.append( InterpretationController.RESOURCE_PATH ).append( "/" ).append( uid );
        builder.append( "/comments/" ).append( comment.getUid() );

        ContextUtils.createdResponse( response, "Commented created", builder.toString() );
    }
}
