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

import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.common.view.BasicView;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.CompleteDataSetRegistrations;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.hisp.dhis.api.utils.ContextUtils.CONTENT_TYPE_JSON;
import static org.hisp.dhis.api.utils.ContextUtils.CONTENT_TYPE_XML;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = CompleteDataSetRegistrationController.RESOURCE_PATH )
public class CompleteDataSetRegistrationController
{
    public static final String RESOURCE_PATH = "/completeDataSetRegistrations";

    @Autowired
    private CompleteDataSetRegistrationService completeDataSetRegistrationService;

    @Autowired
    private PeriodService periodService;

    @Autowired
    private IdentifiableObjectManager manager;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @RequestMapping( method = RequestMethod.GET, produces = CONTENT_TYPE_XML )
    public void getCompleteDataSetRegistrationsXml(
        @RequestParam Set<String> dataSet,
        @RequestParam( required = false ) String period,
        @RequestParam @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date startDate,
        @RequestParam @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date endDate,
        @RequestParam Set<String> orgUnit,
        @RequestParam( required = false ) boolean children,
        HttpServletResponse response ) throws IOException
    {
        response.setContentType( CONTENT_TYPE_XML );
        CompleteDataSetRegistrations completeDataSetRegistrations = getCompleteDataSetRegistrations( dataSet, period, startDate, endDate, orgUnit, children );

        JacksonUtils.toXmlWithView( response.getOutputStream(), completeDataSetRegistrations, BasicView.class );
    }

    @RequestMapping( method = RequestMethod.GET, produces = CONTENT_TYPE_JSON )
    public void getCompleteDataSetRegistrationsJson(
        @RequestParam Set<String> dataSet,
        @RequestParam( required = false ) String period,
        @RequestParam @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date startDate,
        @RequestParam @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date endDate,
        @RequestParam Set<String> orgUnit,
        @RequestParam( required = false ) boolean children,
        HttpServletResponse response ) throws IOException
    {
        response.setContentType( CONTENT_TYPE_JSON );
        CompleteDataSetRegistrations completeDataSetRegistrations = getCompleteDataSetRegistrations( dataSet, period, startDate, endDate, orgUnit, children );

        JacksonUtils.toJsonWithView( response.getOutputStream(), completeDataSetRegistrations, BasicView.class );
    }

    private CompleteDataSetRegistrations getCompleteDataSetRegistrations( Set<String> dataSet, String period, Date startDate, Date endDate, Set<String> orgUnit, boolean children )
    {
        Set<Period> periods = new HashSet<Period>();
        Set<DataSet> dataSets = new HashSet<DataSet>();
        Set<OrganisationUnit> organisationUnits = new HashSet<OrganisationUnit>();

        PeriodType periodType = periodService.getPeriodTypeByName( period );

        if ( periodType != null )
        {
            periods.addAll( periodService.getPeriodsBetweenDates( periodType, startDate, endDate ) );
        }
        else
        {
            periods.addAll( periodService.getPeriodsBetweenDates( startDate, endDate ) );
        }

        if ( children )
        {
            organisationUnits.addAll( organisationUnitService.getOrganisationUnitsWithChildren( orgUnit ) );
        }
        else
        {
            organisationUnits.addAll( organisationUnitService.getOrganisationUnitsByUid( orgUnit ) );
        }

        dataSets.addAll( manager.getByUid( DataSet.class, dataSet ) );

        CompleteDataSetRegistrations completeDataSetRegistrations = new CompleteDataSetRegistrations();
        completeDataSetRegistrations.setCompleteDataSetRegistrationList( new ArrayList<CompleteDataSetRegistration>(
            completeDataSetRegistrationService.getCompleteDataSetRegistrations( dataSets, organisationUnits, periods ) ) );

        return completeDataSetRegistrations;
    }
}
