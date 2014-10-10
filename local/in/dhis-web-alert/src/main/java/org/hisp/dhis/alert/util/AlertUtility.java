package org.hisp.dhis.alert.util;

/*
 * Copyright (c) 2004-2007, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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

import static org.hisp.dhis.dataentryform.DataEntryFormService.DATAELEMENT_TOTAL_PATTERN;
import static org.hisp.dhis.dataentryform.DataEntryFormService.IDENTIFIER_PATTERN;
import static org.hisp.dhis.dataentryform.DataEntryFormService.INDICATOR_PATTERN;
import static org.hisp.dhis.dataentryform.DataEntryFormService.INPUT_PATTERN;
import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.reports.ReportService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * @author Samta Bajpai
 * 
 * @version TrackerDashBoardAction.java May 28, 2012 11:47:12 AM
 */

public class AlertUtility
{
    private static final String NULL_REPLACEMENT = "";

    private static final String SEPARATOR = ".";

    public static final String GENERATEAGGDATA = "generateaggdata";

    public static final String USEEXISTINGAGGDATA = "useexistingaggdata";

    public static final String USECAPTUREDDATA = "usecaptureddata";

    // ---------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ---------------------------------------------------------------
    // Supporting Methods
    // ---------------------------------------------------------------
    public String getCustomDataSetReport( DataSet dataSet, OrganisationUnit unit, String periodIdsByComma,
        String aggOption, I18nFormat format )
    {
        // Map<String, String> aggregatedDataValueMap = getAggregatedValueMap(
        // dataSet, unit, period, selectedUnitOnly,
        // format );

        Map<String, String> aggregatedDataValueMap = getAggregatedValueMap( dataSet, unit, periodIdsByComma, aggOption );

        // Map<Integer, String> aggregatedIndicatorMap =
        // getAggregatedIndicatorValueMap( dataSet, unit, period, format );

        Map<Integer, String> aggregatedIndicatorMap = getAggregatedIndicatorValueMap( dataSet, unit, periodIdsByComma,
            aggOption );

        return prepareReportContent( dataSet.getDataEntryForm(), aggregatedDataValueMap, aggregatedIndicatorMap );
    }

    private String prepareReportContent( DataEntryForm dataEntryForm, Map<String, String> dataValues,
        Map<Integer, String> indicatorValues )
    {
        StringBuffer buffer = new StringBuffer();

        Matcher inputMatcher = INPUT_PATTERN.matcher( dataEntryForm.getHtmlCode() );

        // ---------------------------------------------------------------------
        // Iterate through all matching data element fields.
        // ---------------------------------------------------------------------

        while ( inputMatcher.find() )
        {
            // -----------------------------------------------------------------
            // Get input HTML code
            // -----------------------------------------------------------------

            String inputHtml = inputMatcher.group( 1 );

            Matcher identifierMatcher = IDENTIFIER_PATTERN.matcher( inputHtml );
            Matcher dataElementTotalMatcher = DATAELEMENT_TOTAL_PATTERN.matcher( inputHtml );
            Matcher indicatorMatcher = INDICATOR_PATTERN.matcher( inputHtml );

            // -----------------------------------------------------------------
            // Find existing data or indicator value and replace input tag
            // -----------------------------------------------------------------

            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                Integer dataElementId = Integer.parseInt( identifierMatcher.group( 1 ) );
                Integer optionComboId = Integer.parseInt( identifierMatcher.group( 2 ) );

                String dataValue = dataValues.get( dataElementId + SEPARATOR + optionComboId );

                dataValue = dataValue != null ? dataValue : NULL_REPLACEMENT;

                inputMatcher.appendReplacement( buffer, dataValue );
            }
            else if ( dataElementTotalMatcher.find() && dataElementTotalMatcher.groupCount() > 0 )
            {
                Integer dataElementId = Integer.parseInt( dataElementTotalMatcher.group( 1 ) );

                String dataValue = dataValues.get( String.valueOf( dataElementId ) );

                dataValue = dataValue != null ? dataValue : NULL_REPLACEMENT;

                inputMatcher.appendReplacement( buffer, dataValue );
            }
            else if ( indicatorMatcher.find() && indicatorMatcher.groupCount() > 0 )
            {
                Integer indicatorId = Integer.parseInt( indicatorMatcher.group( 1 ) );

                String indicatorValue = indicatorValues.get( indicatorId );

                indicatorValue = indicatorValue != null ? indicatorValue : NULL_REPLACEMENT;

                inputMatcher.appendReplacement( buffer, indicatorValue );
            }
        }

        inputMatcher.appendTail( buffer );

        return buffer.toString();
    }

    private Map<Integer, String> getAggregatedIndicatorValueMap( DataSet dataSet, OrganisationUnit unit,
        String periodIdsByComma, String aggOption )
    {
        Map<Integer, String> aggMap = new HashMap<Integer, String>();

        List<Indicator> indicatorList = new ArrayList<Indicator>( dataSet.getIndicators() );
        String dataElmentIdsByComma = reportService.getDataelementIdsAsString( indicatorList );

        Map<String, String> aggDeMap = new HashMap<String, String>();
        if ( aggOption.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
        {
            aggDeMap.putAll( reportService.getResultDataValueFromAggregateTable( unit.getId(), dataElmentIdsByComma,
                periodIdsByComma ) );
        }
        else if ( aggOption.equalsIgnoreCase( GENERATEAGGDATA ) )
        {
            List<OrganisationUnit> childOrgUnitTree = new ArrayList<OrganisationUnit>( organisationUnitService
                .getOrganisationUnitWithChildren( unit.getId() ) );
            List<Integer> childOrgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class,
                childOrgUnitTree ) );
            String childOrgUnitsByComma = getCommaDelimitedString( childOrgUnitTreeIds );

            aggDeMap.putAll( reportService.getAggDataFromDataValueTable( childOrgUnitsByComma, dataElmentIdsByComma,
                periodIdsByComma ) );
        }
        else if ( aggOption.equalsIgnoreCase( USECAPTUREDDATA ) )
        {
            aggDeMap.putAll( reportService.getAggDataFromDataValueTable( "" + unit.getId(), dataElmentIdsByComma,
                periodIdsByComma ) );
        }

        for ( Indicator indicator : indicatorList )
        {
            Double numValue = 0.0;
            Double denValue = 0.0;
            Double indValue = 0.0;

            try
            {
                numValue = Double.parseDouble( reportService.getAggVal( indicator.getNumerator(), aggDeMap ) );
            }
            catch ( Exception e )
            {
                numValue = 0.0;
            }

            try
            {
                denValue = Double.parseDouble( reportService.getAggVal( indicator.getDenominator(), aggDeMap ) );
            }
            catch ( Exception e )
            {
                denValue = 0.0;
            }

            try
            {
                if ( denValue != 0.0 )
                {
                    indValue = (numValue / denValue) * indicator.getIndicatorType().getFactor();
                }
                else
                {
                    indValue = 0.0;
                }
            }
            catch ( Exception e )
            {
                indValue = 0.0;
            }

            indValue = Math.round( indValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );

            aggMap.put( indicator.getId(), indValue.toString() );
        }

        return aggMap;
    }

    private Map<String, String> getAggregatedValueMap( DataSet dataSet, OrganisationUnit unit, String periodIdsByComma,
        String aggOption )
    {
        Map<String, String> aggDeMap = new HashMap<String, String>();

        List<DataElement> dataElementList = new ArrayList<DataElement>( dataSet.getDataElements() );
        Collection<Integer> dataElementIds = new ArrayList<Integer>(
            getIdentifiers( DataElement.class, dataElementList ) );
        String dataElmentIdsByComma = getCommaDelimitedString( dataElementIds );

        if ( aggOption.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
        {
            aggDeMap.putAll( reportService.getResultDataValueFromAggregateTable( unit.getId(), dataElmentIdsByComma,
                periodIdsByComma ) );
        }
        else if ( aggOption.equalsIgnoreCase( GENERATEAGGDATA ) )
        {
            List<OrganisationUnit> childOrgUnitTree = new ArrayList<OrganisationUnit>( organisationUnitService
                .getOrganisationUnitWithChildren( unit.getId() ) );
            List<Integer> childOrgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class,
                childOrgUnitTree ) );
            String childOrgUnitsByComma = getCommaDelimitedString( childOrgUnitTreeIds );

            aggDeMap.putAll( reportService.getAggDataFromDataValueTable( childOrgUnitsByComma, dataElmentIdsByComma,
                periodIdsByComma ) );
        }
        else if ( aggOption.equalsIgnoreCase( USECAPTUREDDATA ) )
        {
            aggDeMap.putAll( reportService.getAggDataFromDataValueTable( "" + unit.getId(), dataElmentIdsByComma,
                periodIdsByComma ) );
        }

        return aggDeMap;
    }

    public Map<Integer, Integer> getTotalEnrolledNumber( String orgUnitIdsByComma )
    {
        Map<Integer, Integer> aggDeMap = new HashMap<Integer, Integer>();
        try
        {
            String query = "SELECT programinstance.programid, COUNT(*) FROM programinstance INNER JOIN patient "
                + " ON programinstance.patientid = patient.patientid " + " WHERE patient.organisationunitid IN ("
                + orgUnitIdsByComma + ") GROUP BY programid";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Integer programId = rs.getInt( 1 );
                Integer totalCount = rs.getInt( 2 );
                {
                    aggDeMap.put( programId, totalCount );
                }
            }

            return aggDeMap;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public Map<Integer, Integer> getTotalEnrolledNumberForSelectedDate( String orgUnitIdsByComma, String toDaysDate )
    {
        Map<Integer, Integer> aggDeMap = new HashMap<Integer, Integer>();
        try
        {
            String query = "SELECT programinstance.programid, COUNT(*) FROM programinstance INNER JOIN patient "
                + " ON programinstance.patientid = patient.patientid " + " WHERE patient.organisationunitid IN ("
                + orgUnitIdsByComma + ") AND " + " patient.registrationdate LIKE '" + toDaysDate
                + "%' GROUP BY programid";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Integer programId = rs.getInt( 1 );
                Integer totalCount = rs.getInt( 2 );
                {
                    aggDeMap.put( programId, totalCount );
                }
            }

            return aggDeMap;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public Integer getTotalRegisteredCount( String orgUnitIdsByComma )
    {
        Integer totalRegCount = 0;
        try
        {
            String query = "SELECT COUNT(*) FROM patient " + " WHERE organisationunitid IN (" + orgUnitIdsByComma + ")";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            if ( rs != null && rs.next() )
            {
                totalRegCount = rs.getInt( 1 );
            }

            return totalRegCount;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public Integer getTotalRegisteredCountForSelDate( String orgUnitIdsByComma, String selDate )
    {
        Integer totalRegCount = 0;
        try
        {
            String query = "SELECT COUNT(*) FROM patient " + " WHERE organisationunitid IN (" + orgUnitIdsByComma
                + ") AND " + " registrationdate LIKE '" + selDate + "%'";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            if ( rs != null && rs.next() )
            {
                totalRegCount = rs.getInt( 1 );
            }

            return totalRegCount;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    // methods for IDSP OUTBREAK
    public String getPeriodIdForIDSPOutBreak()
    {
        String periodIdResult = "-1";
        String startDate = " ";
        String endDate = " ";
        try
        {
            Date toDay = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
            String toDaysDate = simpleDateFormat.format( toDay );

            int periodId = -1;

            String query = "SELECT periodid, startdate, enddate FROM period WHERE periodtypeid = 2 AND "
                + " startdate <= '" + toDaysDate + "' AND enddate >= '" + toDaysDate + "'";

            SqlRowSet rs1 = jdbcTemplate.queryForRowSet( query );
            if ( rs1 != null && rs1.next() )
            {
                periodId = rs1.getInt( 1 );
                startDate = rs1.getString( 2 );
                endDate = rs1.getString( 3 );

                // System.out.println( periodId + " : " + startDate + " : " +
                // endDate + " : " + toDaysDate );

                if ( !endDate.equalsIgnoreCase( toDaysDate ) )
                {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime( toDay );
                    cal.add( Calendar.DATE, -7 );
                    toDaysDate = simpleDateFormat.format( cal.getTime() );

                    query = "SELECT periodid, startdate, enddate FROM period WHERE periodtypeid = 2 AND "
                        + " startdate <= '" + toDaysDate + "' AND enddate >= '" + toDaysDate + "'";
                    SqlRowSet rs2 = jdbcTemplate.queryForRowSet( query );
                    if ( rs2 != null && rs2.next() )
                    {
                        periodId = rs2.getInt( 1 );
                        startDate = rs2.getString( 2 );
                        endDate = rs2.getString( 3 );
                    }
                    // System.out.println( periodId + " : " + toDaysDate );
                }

                periodIdResult = "" + periodId + "::" + startDate + " TO " + endDate;
            }
            else
            {
                Calendar cal = Calendar.getInstance();
                cal.setTime( toDay );
                cal.add( Calendar.DATE, -7 );
                toDaysDate = simpleDateFormat.format( cal.getTime() );

                query = "SELECT periodid, startdate, enddate FROM period WHERE periodtypeid = 2 AND "
                    + " startdate <= '" + toDaysDate + "' AND enddate >= '" + toDaysDate + "'";
                SqlRowSet rs2 = jdbcTemplate.queryForRowSet( query );
                if ( rs2 != null && rs2.next() )
                {
                    periodId = rs2.getInt( 1 );
                    startDate = rs2.getString( 2 );
                    endDate = rs2.getString( 3 );
                }
                periodIdResult = "" + periodId + "::" + startDate + " TO " + endDate;
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }

        // System.out.println( "PeriodId : " +periodIdResult );
        return periodIdResult;
    }

    public String getPeriodIdForIDSPPopulation()
    {
        String periodIdResult = "-1";

        try
        {
            Date toDay = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
            String toDaysDate = simpleDateFormat.format( toDay );

            String query = "SELECT periodid FROM period WHERE periodtypeid = 6 AND " + " startdate <= '" + toDaysDate
                + "' AND enddate >= '" + toDaysDate + "'";

            SqlRowSet rs1 = jdbcTemplate.queryForRowSet( query );
            if ( rs1 != null && rs1.next() )
            {
                periodIdResult = "" + rs1.getInt( 1 );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }

        // System.out.println( "PeriodId : " +periodIdResult );
        return periodIdResult;

    }

    public Integer getAggregatedData( String orgUnitIdsByComma, String deIdsByComma, String periodId )
    {
        Integer aggData = 0;

        try
        {
            String query = "SELECT SUM(value) FROM datavalue " + " WHERE sourceid IN (" + orgUnitIdsByComma + ") AND "
                + " dataelementid IN (" + deIdsByComma + ") AND " + " periodid = " + periodId;

            SqlRowSet rs1 = jdbcTemplate.queryForRowSet( query );

            if ( rs1 != null && rs1.next() )
            {
                double temp = rs1.getDouble( 1 );

                aggData = (int) temp;
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }

        return aggData;
    }

    public Integer getConfirmedCount( String orgUnitIdsByComma, String dataSetId, String periodId )
    {
        Integer confirmedCount = 0;

        try
        {
            String query = "SELECT COUNT(*) FROM completedatasetregistration " + " WHERE sourceid IN ("
                + orgUnitIdsByComma + ") AND " + " datasetid = " + dataSetId + " AND " + " periodid = " + periodId;

            SqlRowSet rs1 = jdbcTemplate.queryForRowSet( query );

            if ( rs1 != null && rs1.next() )
            {
                double temp = rs1.getDouble( 1 );

                confirmedCount = (int) temp;
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }

        return confirmedCount;
    }

    public String getRAFolderName()
    {
        return reportService.getRAFolderName();
    }
}
