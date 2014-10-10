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

import static org.hisp.dhis.api.utils.ContextUtils.DATE_PATTERN;
import static org.hisp.dhis.common.DimensionalObjectUtils.getUniqueDimensions;
import static org.hisp.dhis.common.DimensionalObjectUtils.toDimension;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.api.utils.ContextUtils.CacheStrategy;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.common.DimensionService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.user.UserService;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping( value = ChartController.RESOURCE_PATH )
public class ChartController
    extends AbstractCrudController<Chart>
{
    public static final String RESOURCE_PATH = "/charts";

    @Autowired
    private ChartService chartService;

    @Autowired
    private UserService userService;

    @Autowired
    private DataElementService dataElementService;
    
    @Autowired
    private DataElementCategoryService categoryService;
    
    @Autowired
    private IndicatorService indicatorService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private DimensionService dimensionService;
    
    @Autowired
    private I18nManager i18nManager;

    @Autowired
    private ContextUtils contextUtils;

    //--------------------------------------------------------------------------
    // CRUD
    //--------------------------------------------------------------------------

    @Override
    @RequestMapping( method = RequestMethod.POST, consumes = "application/json" )
    public void postJsonObject( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        Chart chart = JacksonUtils.fromJson( input, Chart.class );
        
        mergeChart( chart );
        
        chartService.addChart( chart );
        
        ContextUtils.createdResponse( response, "Chart created", RESOURCE_PATH + "/" + chart.getUid() );
    }

    @Override
    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, consumes = "application/json" )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putJsonObject( HttpServletResponse response, HttpServletRequest request, @PathVariable( "uid" ) String uid, InputStream input ) throws Exception
    {
        Chart chart = chartService.getChart( uid );
        
        if ( chart == null )
        {
            ContextUtils.notFoundResponse( response, "Chart does not exist: " + uid );
            return;
        }
        
        Chart newChart = JacksonUtils.fromJson( input, Chart.class );
        
        mergeChart( newChart );
        
        chart.mergeWith( newChart );
        
        chartService.updateChart( chart );
    }

    @Override
    @RequestMapping( value = "/{uid}", method = RequestMethod.DELETE )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void deleteObject( HttpServletResponse response, HttpServletRequest request, @PathVariable( "uid" ) String uid ) throws Exception
    {
        Chart chart = chartService.getChart( uid );
        
        if ( chart == null )
        {
            ContextUtils.notFoundResponse( response, "Chart does not exist: " + uid );
            return;
        }
        
        chartService.deleteChart( chart );
    }
    
    //--------------------------------------------------------------------------
    // Get data
    //--------------------------------------------------------------------------

    @RequestMapping( value = { "/{uid}/data", "/{uid}/data.png" }, method = RequestMethod.GET )
    public void getChart( 
        @PathVariable( "uid" ) String uid,
        @RequestParam( value = "date", required = false ) @DateTimeFormat( pattern = DATE_PATTERN ) Date date,
        @RequestParam( value = "ou", required = false ) String ou,
        @RequestParam( value = "width", defaultValue = "800", required = false ) int width,
        @RequestParam( value = "height", defaultValue = "500", required = false ) int height,
        HttpServletResponse response ) throws IOException
    {
        Chart chart = chartService.getChartNoAcl( uid );

        if ( chart == null )
        {
            ContextUtils.notFoundResponse( response, "Chart does not exist: " + uid );
            return;
        }
        
        OrganisationUnit unit = ou != null ? organisationUnitService.getOrganisationUnit( ou ) : null;
        
        JFreeChart jFreeChart = chartService.getJFreeChart( chart, date, unit, i18nManager.getI18nFormat() );

        String filename = CodecUtils.filenameEncode( chart.getName() ) + ".png";

        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_PNG, CacheStrategy.RESPECT_SYSTEM_SETTING, filename, false );

        ChartUtilities.writeChartAsPNG( response.getOutputStream(), jFreeChart, width, height );
    }

    @RequestMapping( value = { "/data", "/data.png" }, method = RequestMethod.GET )
    public void getChart( 
        @RequestParam( value = "in" ) String indicatorUid,
        @RequestParam( value = "ou" ) String organisationUnitUid,
        @RequestParam( value = "periods", required = false ) boolean periods,
        @RequestParam( value = "width", defaultValue = "800", required = false ) int width,
        @RequestParam( value = "height", defaultValue = "500", required = false ) int height,
        @RequestParam( value = "skipTitle", required = false ) boolean skipTitle,
        HttpServletResponse response ) throws IOException
    {
        Indicator indicator = indicatorService.getIndicator( indicatorUid );
        OrganisationUnit unit = organisationUnitService.getOrganisationUnit( organisationUnitUid );

        JFreeChart chart = null;

        if ( periods )
        {
            chart = chartService.getJFreePeriodChart( indicator, unit, !skipTitle, i18nManager.getI18nFormat() );
        }
        else
        {
            chart = chartService.getJFreeOrganisationUnitChart( indicator, unit, !skipTitle, i18nManager.getI18nFormat() );
        }

        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_PNG, CacheStrategy.RESPECT_SYSTEM_SETTING, "chart.png", false );

        ChartUtilities.writeChartAsPNG( response.getOutputStream(), chart, width, height );
    }

    @RequestMapping( value = { "/history/data", "/history/data.png" }, method = RequestMethod.GET )
    public void getHistoryChart(
        @RequestParam String de,
        @RequestParam String co,
        @RequestParam String pe,
        @RequestParam String ou,
        @RequestParam( defaultValue = "525", required = false ) int width,
        @RequestParam( defaultValue = "300", required = false ) int height,
        HttpServletResponse response ) throws IOException
    {
        DataElement dataElement = dataElementService.getDataElement( de );
        
        if ( dataElement == null )
        {
            ContextUtils.conflictResponse( response, "Data element does not exist: " + de );
            return;
        }
        
        DataElementCategoryOptionCombo categoryOptionCombo = categoryService.getDataElementCategoryOptionCombo( co );
        
        if ( categoryOptionCombo == null )
        {
            ContextUtils.conflictResponse( response, "Category option combo does not exist: " + co );
            return;
        }
        
        Period period = PeriodType.getPeriodFromIsoString( pe );
        
        if ( period == null )
        {
            ContextUtils.conflictResponse( response, "Period does not exist: " + pe );
            return;
        }
        
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( ou );
        
        if ( organisationUnit == null )
        {
            ContextUtils.conflictResponse( response, "Organisation unit does not exist: " + ou );
            return;
        }        

        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_PNG, CacheStrategy.RESPECT_SYSTEM_SETTING, "chart.png", false );

        JFreeChart chart = chartService.getJFreeChartHistory( dataElement, categoryOptionCombo, period, organisationUnit, 13, i18nManager.getI18nFormat() );
        
        ChartUtilities.writeChartAsPNG( response.getOutputStream(), chart, width, height );
    }

    //--------------------------------------------------------------------------
    // Hooks
    //--------------------------------------------------------------------------

    @Override
    public void postProcessEntity( Chart chart ) throws Exception
    {
        chart.populateAnalyticalProperties();
        
        for ( OrganisationUnit organisationUnit : chart.getOrganisationUnits() )
        {
            chart.getParentGraphMap().put( organisationUnit.getUid(), organisationUnit.getParentGraph() );
        }
        
        if ( chart.getPeriods() != null && !chart.getPeriods().isEmpty() )
        {
            I18nFormat format = i18nManager.getI18nFormat();
            
            for ( Period period : chart.getPeriods() )
            {
                period.setName( format.formatPeriod( period ) );
            }
        }
    }

    //--------------------------------------------------------------------------
    // Supportive methods
    //--------------------------------------------------------------------------

    private void mergeChart( Chart chart )
    {
        dimensionService.mergeAnalyticalObject( chart );
        
        chart.getFilterDimensions().clear();
        
        if ( chart.getColumns() != null )
        {
            chart.setSeries( toDimension( chart.getColumns().get( 0 ).getDimension() ) );
        }
        
        if ( chart.getRows() != null )
        {
            chart.setCategory( toDimension( chart.getRows().get( 0 ).getDimension() ) );
        }
        
        chart.getFilterDimensions().addAll( getUniqueDimensions( chart.getFilters() ) );
    }
}
