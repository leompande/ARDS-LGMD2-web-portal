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

import org.hisp.dhis.aggregation.AggregatedDataValue;
import org.hisp.dhis.aggregation.AggregatedDataValueService;
import org.hisp.dhis.aggregation.AggregatedIndicatorValue;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.i18n.I18nManagerException;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.RelativePeriods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = AggregatedValueController.RESOURCE_PATH )
public class AggregatedValueController
{
    public static final String RESOURCE_PATH = "/aggregatedValues";

    @Autowired
    private AggregatedDataValueService aggregatedDataValueService;

    @Autowired
    private IndicatorService indicatorService;

    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private PeriodService periodService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private I18nManager i18nManager;

    //--------------------------------------------------------------------------
    // GET
    //--------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.GET )
    public void getAggregatedValues(
        @RequestParam( value = "de", required = false ) List<String> dataElementUids,
        @RequestParam( value = "in", required = false ) List<String> indicatorUids,
        @RequestParam( value = "ou" ) List<String> organisationUnitsUids,
        @RequestParam( required = false ) boolean lastMonth,
        @RequestParam( required = false ) boolean monthsThisYear,
        @RequestParam( required = false ) boolean monthsLastYear,
        @RequestParam( required = false ) boolean lastQuarter,
        @RequestParam( required = false ) boolean quartersThisYear,
        @RequestParam( required = false ) boolean quartersLastYear,
        @RequestParam( required = false ) boolean thisYear,
        @RequestParam( required = false ) boolean lastYear,
        @RequestParam( required = false ) boolean lastFiveYears,
        HttpServletResponse response
    ) throws IOException, I18nManagerException
    {
        RelativePeriods rp = new RelativePeriods();
        rp.setReportingMonth( lastMonth );
        rp.setMonthsThisYear( monthsThisYear );
        rp.setMonthsLastYear( monthsLastYear );
        rp.setReportingQuarter( lastQuarter );
        rp.setQuartersThisYear( quartersThisYear );
        rp.setQuartersLastYear( quartersLastYear );
        rp.setThisYear( thisYear );
        rp.setLastYear( lastYear );
        rp.setLast5Years( lastFiveYears );

        Collection<Period> periods = periodService.reloadPeriods( rp.getRelativePeriods() );

        Collection<Integer> periodIds = new ArrayList<Integer>();

        for ( Period period : periods )
        {
            periodIds.add( period.getId() );
        }

        List<Integer> organisationUnitIds = new ArrayList<Integer>();

        for ( String uid : organisationUnitsUids )
        {
            organisationUnitIds.add( organisationUnitService.getOrganisationUnit( uid ).getId() );
        }

        List<AggregatedIndicatorValue> indicatorValues = null;
        List<AggregatedDataValue> dataElementValues = null;

        if ( indicatorUids != null )
        {
            List<Integer> indicatorIds = new ArrayList<Integer>();

            for ( String uid : indicatorUids )
            {
                indicatorIds.add( indicatorService.getIndicator( uid ).getId() );
            }

            indicatorValues = new ArrayList<AggregatedIndicatorValue>( aggregatedDataValueService.
                getAggregatedIndicatorValues( indicatorIds, periodIds, organisationUnitIds ) );

            for ( AggregatedIndicatorValue value : indicatorValues )
            {
                value.setIndicatorName( indicatorService.getIndicator( value.getIndicatorId() ).getShortName() );
                value.setPeriodName( i18nManager.getI18nFormat().formatPeriod( periodService.getPeriod( value.getPeriodId() ) ) );
                value.setOrganisationUnitName( organisationUnitService.getOrganisationUnit(
                    value.getOrganisationUnitId() ).getName() );
            }
        }
        else if ( dataElementUids != null )
        {
            List<Integer> dataElementIds = new ArrayList<Integer>();

            for ( String uid : dataElementUids )
            {
                dataElementIds.add( dataElementService.getDataElement( uid ).getId() );
            }

            dataElementValues = new ArrayList<AggregatedDataValue>( aggregatedDataValueService.
                getAggregatedDataValueTotals( dataElementIds, periodIds, organisationUnitIds ) );

            for ( AggregatedDataValue value : dataElementValues )
            {
                value.setDataElementName( dataElementService.getDataElement( value.getDataElementId() ).getShortName() );
                value.setPeriodName( i18nManager.getI18nFormat().formatPeriod( periodService.getPeriod( value.getPeriodId() ) ) );
                value.setOrganisationUnitName( organisationUnitService.getOrganisationUnit(
                    value.getOrganisationUnitId() ).getName() );
            }
        }

        List<Object> valueList = new ArrayList<Object>();

        if ( indicatorValues != null )
        {
            for ( AggregatedIndicatorValue indicatorValue : indicatorValues )
            {
                List<Object> values = new ArrayList<Object>();
                values.add( indicatorValue.getValue() );
                values.add( indicatorValue.getIndicatorName() );
                values.add( indicatorValue.getPeriodName() );
                values.add( indicatorValue.getOrganisationUnitName() );

                valueList.add( values );
            }
        }
        else if ( dataElementValues != null )
        {
            for ( AggregatedDataValue dataValue : dataElementValues )
            {
                List<Object> values = new ArrayList<Object>();
                values.add( dataValue.getValue() );
                values.add( dataValue.getDataElementName() );
                values.add( dataValue.getPeriodName() );
                values.add( dataValue.getOrganisationUnitName() );

                valueList.add( values );
            }
        }

        JacksonUtils.toJson( response.getOutputStream(), valueList );

        response.setContentType( ContextUtils.CONTENT_TYPE_JSON );
        response.setStatus( HttpServletResponse.SC_OK );
    }
}
