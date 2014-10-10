package org.hisp.dhis.dataanalyser.util;

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

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.WeeklyPeriodType;
import org.hisp.dhis.reports.ReportService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class DashBoardService
{
    private final String OPTIONCOMBO = "optioncombo";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private ReportService reportservice ;
    
    public void setReportservice( ReportService reportservice )
      {
          this.reportservice = reportservice;
      }
    
    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }
    
    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
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


    public String getRAFolderName()
    {
        return reportservice.getRAFolderName();
    }
    

    public Map<Integer, Integer> getOrgunitLevelMap( )
    {
        Map<Integer, Integer> orgUnitLevelMap = new HashMap<Integer, Integer>();
        try
        {
            String query = "SELECT organisationunitid,level FROM _orgunitstructure";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            
            while ( rs.next() )
            {                
                Integer orgUnitId = rs.getInt( 1 );
                Integer level = rs.getInt( 2 );
                
                orgUnitLevelMap.put( orgUnitId, level );
            }
            
            return orgUnitLevelMap;
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Illegal DataElement id", e );
        }
    }

    public String getPeriodIdForIDSPPopulation( )
    {
        String periodIdResult = "-1";

        try
        {
            Date toDay = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
            String toDaysDate = simpleDateFormat.format( toDay );
            
            String query = "SELECT periodid FROM period WHERE periodtypeid = 6 AND " +
                                " startdate <= '" + toDaysDate + "' AND enddate >= '"+ toDaysDate +"'";
    
            SqlRowSet rs1 = jdbcTemplate.queryForRowSet( query );
            if ( rs1 != null && rs1.next() )
            {
                periodIdResult = ""+rs1.getInt( 1 );
            }
        }
        catch( Exception e )
        {
            throw new RuntimeException( e );
        }
        
        System.out.println( "PeriodId : " +periodIdResult );
        return periodIdResult;
            
    }
   
    public Integer getConfirmedCount( String orgUnitIdsByComma, String dataSetId, String periodId )
    {
        Integer confirmedCount = 0;
        
        try
        {
            String query = "SELECT COUNT(*) FROM completedatasetregistration " +
                                " WHERE sourceid IN ("+ orgUnitIdsByComma +") AND " +
                                " datasetid = "+ dataSetId +" AND " +
                                " periodid = "+periodId;
    
            SqlRowSet rs1 = jdbcTemplate.queryForRowSet( query );
    
            if ( rs1 != null && rs1.next() )
            {
                double temp = rs1.getDouble( 1 ); 
                
                confirmedCount =  (int) temp;
            }
        }
        catch( Exception e )
        {
            throw new RuntimeException( e );
        }
        
        return confirmedCount;
    }
   
    public String getPeriodIdForIDSPOutBreak( )
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
            
            String query = "SELECT periodid, startdate, enddate FROM period WHERE periodtypeid = 2 AND " +
                                " startdate <= '" + toDaysDate + "' AND enddate >= '"+ toDaysDate +"'";
    
            SqlRowSet rs1 = jdbcTemplate.queryForRowSet( query );
            if ( rs1 != null && rs1.next() )
            {
                periodId = rs1.getInt( 1 );
                startDate = rs1.getString( 2 );
                endDate = rs1.getString( 3 ); 
                
                System.out.println( periodId + " : " + startDate + " : " + endDate + " : " +  toDaysDate );
                
                if( !endDate.equalsIgnoreCase(  toDaysDate ) )
                {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime( toDay );
                    cal.add( Calendar.DATE, -7 );
                    toDaysDate = simpleDateFormat.format( cal.getTime() );
                    
                    query = "SELECT periodid, startdate, enddate FROM period WHERE periodtypeid = 2 AND " +
                                " startdate <= '" + toDaysDate + "' AND enddate >= '"+ toDaysDate +"'";
                    SqlRowSet rs2 = jdbcTemplate.queryForRowSet( query );
                    if ( rs2 != null && rs2.next() )
                    {
                        periodId = rs2.getInt( 1 );    
                        startDate = rs2.getString( 2 );
                        endDate = rs2.getString( 3 );
                    }
                    System.out.println( periodId + " : " +  toDaysDate );
                }

                periodIdResult = ""+ periodId+"::"+startDate+" TO "+endDate;
            }
            else
            {
                Calendar cal = Calendar.getInstance();
                cal.setTime( toDay );
                cal.add( Calendar.DATE, -7 );
                toDaysDate = simpleDateFormat.format( cal.getTime() );
                
                query = "SELECT periodid, startdate, enddate FROM period WHERE periodtypeid = 2 AND " +
                            " startdate <= '" + toDaysDate + "' AND enddate >= '"+ toDaysDate +"'";
                SqlRowSet rs2 = jdbcTemplate.queryForRowSet( query );
                if ( rs2 != null && rs2.next() )
                {
                    periodId = rs2.getInt( 1 );
                    startDate = rs2.getString( 2 );
                    endDate = rs2.getString( 3 );
                }
                periodIdResult = ""+ periodId+"::"+startDate+" TO "+endDate;
            }
        }
        catch( Exception e )
        {
            throw new RuntimeException( e );
        }
        
        System.out.println( "PeriodId : " +periodIdResult );
        return periodIdResult;
    }
   
    public Integer getAggregatedData( String orgUnitIdsByComma, String deIdsByComma, String periodId )
    {
        Integer aggData = 0;

        try
        {
            String query = "SELECT SUM(value) FROM datavalue " +
                                " WHERE sourceid IN ("+ orgUnitIdsByComma +") AND " +
                                " dataelementid IN ("+ deIdsByComma +") AND " +
                                " periodid = "+periodId;
    
            SqlRowSet rs1 = jdbcTemplate.queryForRowSet( query );
    
            if ( rs1 != null && rs1.next() )
            {
                double temp = rs1.getDouble( 1 ); 
                
                aggData =  (int) temp;
            }
        }
        catch( Exception e )
        {
            throw new RuntimeException( e );
        }
        
        return aggData;
    }
    
    public String getProgramwiseSummarySMS( OrganisationUnit orgUnit )
    {
        List<OrganisationUnit> childTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( orgUnit.getId() ) );
        String orgUnitIdsByComma = getCommaDelimitedString( getIdentifiers( OrganisationUnit.class, childTree ) );
        String prgWiseSummaryMsg = "";
     
        try
        {
            String query = "SELECT COUNT(*) FROM patient " +
                                " WHERE organisationunitid IN ("+ orgUnitIdsByComma +")";
    
            SqlRowSet rs1 = jdbcTemplate.queryForRowSet( query );
    
            if ( rs1 != null && rs1.next() )
            {
                Integer totalRegCount = rs1.getInt( 1 );
                
                prgWiseSummaryMsg = orgUnit.getShortName()+";TotalReg:"+totalRegCount+";";
            }
            
            query = "SELECT program.name, COUNT(*) FROM programinstance " +
                        " INNER JOIN patient ON programinstance.patientid = patient.patientid " +
                        " INNER JOIN program ON programinstance.programid = program.programid " +
                        " WHERE patient.organisationunitid IN ("+ orgUnitIdsByComma +") GROUP BY program.programid";
            
            SqlRowSet rs2 = jdbcTemplate.queryForRowSet( query );
            
            while ( rs2.next() )
            {
                String programName = rs2.getString( 1 );
                Integer totalCount = rs2.getInt( 2 );
                
                prgWiseSummaryMsg += programName+":"+totalCount+";";
            }
        }
        catch( Exception e )
        {
            throw new RuntimeException( e );
        }
        
        return prgWiseSummaryMsg;
    }
    
    public Map<Integer, Integer> getTotalEnrolledNumber( String orgUnitIdsByComma )
    {
        Map<Integer, Integer> aggDeMap = new HashMap<Integer, Integer>();
        try
        {
            String query = "SELECT programinstance.programid, COUNT(*) FROM programinstance INNER JOIN patient " +
                                        " ON programinstance.patientid = patient.patientid " +
                                        " WHERE patient.organisationunitid IN ("+ orgUnitIdsByComma +") GROUP BY programid";

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
        catch( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public Map<Integer, Integer> getTotalEnrolledNumberForSelectedDate( String orgUnitIdsByComma, String toDaysDate )
    {
        Map<Integer, Integer> aggDeMap = new HashMap<Integer, Integer>();
        try
        {
            String query = "SELECT programinstance.programid, COUNT(*) FROM programinstance INNER JOIN patient " +
                                        " ON programinstance.patientid = patient.patientid " +
                                        " WHERE patient.organisationunitid IN ("+ orgUnitIdsByComma +") AND " +
                                        " patient.registrationdate LIKE '"+ toDaysDate+"%' GROUP BY programid";

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
        catch( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public Integer getTotalRegisteredCount( String orgUnitIdsByComma )
    {
        Integer totalRegCount = 0;
        try
        {
            String query = "SELECT COUNT(*) FROM patient " +    " WHERE organisationunitid IN ("+ orgUnitIdsByComma+")";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            
            if ( rs != null && rs.next() )
            {
                totalRegCount = rs.getInt( 1 );
            }
            
            return totalRegCount;
        }
        catch( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public Integer getTotalRegisteredCountForSelDate( String orgUnitIdsByComma, String selDate )
    {
        Integer totalRegCount = 0;
        try
        {
            String query = "SELECT COUNT(*) FROM patient " +
                               " WHERE organisationunitid IN ("+ orgUnitIdsByComma +") AND " +
                                " registrationdate LIKE '"+ selDate+"%'";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            
            if ( rs != null && rs.next() )
            {
                totalRegCount = rs.getInt( 1 );
            }
            
            return totalRegCount;
        }
        catch( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    
    public  Collection<HashMap> getDueDates(int programInstance,int organisationUnitId,String startDate,String endDate)
    {
        String name=null;
        Date date=null;
        int instId;
        Collection<HashMap> data=new ArrayList<HashMap>();
        try
        {
            String query = "select patient.firstname,patient.organisationunitid, programstageinstance.duedate,programstageinstance.programstageid from programstageinstance inner join programinstance on programinstance.programinstanceid = programstageinstance.programinstanceid inner join patient on patient.patientid = programinstance.patientid  where" +" "+
                               " programinstance.programid ='"+programInstance+"' and patient.organisationunitid = '"+organisationUnitId+"' and duedate >= '"+startDate+"' and duedate <= '"+endDate+"' and programinstance.completed = false ";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
         //  System.out.println(rs.toString());
            while (    rs.next() )
            {
                  name= rs.getString( 1);
                  int temp=rs.getInt( 2);
                  date=rs.getDate( 3);
                  instId=rs.getInt( 4);
        HashMap aggDeMap = new HashMap();
         
            
                    aggDeMap.put( "name", name);
                    aggDeMap.put( "date", date);
                    aggDeMap.put( "id", instId);
            data.add( aggDeMap );
            
            }
            
         return  data;
        }
        catch( Exception e )
        {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    
    public List<Period> getMonthlyPeriods( Date start, Date end )
    {
        PeriodType monthlyPeriodType = PeriodType.getByNameIgnoreCase( "monthly" );

        List<Period> monthlyPeriodList = new ArrayList<Period>();
        for ( Period period : monthlyPeriodList )
        {
            if ( period.getPeriodType().getId() == monthlyPeriodType.getId() )
            {
                monthlyPeriodList.add( period );
            }
        }
        return monthlyPeriodList;
    }

    /*
     * Returns the Period Object of the given date For ex:- if the month is 3,
     * year is 2006 and periodType Object of type Monthly then it returns the
     * corresponding Period Object
     */
    public Period getPeriodByMonth( int month, int year, PeriodType periodType )
    {
        int monthDays[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

        Calendar cal = Calendar.getInstance();
        cal.set( year, month, 1, 0, 0, 0 );
        Date firstDay = new Date( cal.getTimeInMillis() );

        if ( periodType.getName().equalsIgnoreCase( "Monthly" ) )
        {
            cal.set( year, month, 1, 0, 0, 0 );
            if ( year % 4 == 0 && month == 1 )
            {
                cal.set( Calendar.DAY_OF_MONTH, monthDays[month] + 1 );
            }
            else
            {
                cal.set( Calendar.DAY_OF_MONTH, monthDays[month] );
            }
        }
        else if ( periodType.getName().equalsIgnoreCase( "Yearly" ) )
        {
            cal.set( year, Calendar.DECEMBER, 31 );
        }

        Date lastDay = new Date( cal.getTimeInMillis() );

        Period newPeriod = periodService.getPeriod( firstDay, lastDay, periodType );

        return newPeriod;
    }

    public String createDataTable( String orgUnitInfo, String deInfo, String periodInfo, Connection con )
    {
        Statement st1 = null;
        Statement st2 = null;

        String dataTableName = "data" + UUID.randomUUID().toString();
        dataTableName = dataTableName.replaceAll( "-", "" );

        String query = "DROP TABLE IF EXISTS " + dataTableName;

        try
        {
            st1 = con.createStatement();
            st2 = con.createStatement();

            st1.executeUpdate( query );

            System.out.println( "Table " + dataTableName + " dropped Successfully (if exists) " );

            query = "CREATE table " + dataTableName + " AS "
                + " SELECT sourceid,dataelementid,periodid,value FROM datavalue " + " WHERE dataelementid in ("
                + deInfo + ") AND " + " sourceid in (" + orgUnitInfo + ") AND " + " periodid in (" + periodInfo + ")";

            st2.executeUpdate( query );

            System.out.println( "Table " + dataTableName + " created Successfully" );
        } // try block end
        catch ( Exception e )
        {
            System.out.println( "SQL Exception : " + e.getMessage() );
            return null;
        }
        finally
        {
            try
            {
                if ( st1 != null )
                    st1.close();
                if ( st2 != null )
                    st2.close();
                if ( con != null )
                    con.close();
            }
            catch ( Exception e )
            {
                System.out.println( "SQL Exception : " + e.getMessage() );
                return null;
            }
        }// finally block end

        return dataTableName;
    }

    public List<String> getPeriodNamesByPeriodType( PeriodType periodType, Collection<Period> periods )
    {
        SimpleDateFormat simpleDateFormat1;

        SimpleDateFormat simpleDateFormat2;

        List<String> periodNameList = new ArrayList<String>();

        if ( periodType.getName().equalsIgnoreCase( "monthly" ) )
        {
            simpleDateFormat1 = new SimpleDateFormat( "MMM-yyyy" );
            for ( Period p1 : periods )
            {
                periodNameList.add( simpleDateFormat1.format( p1.getStartDate() ) );
            }
        }
        else if ( periodType.getName().equalsIgnoreCase( "quarterly" ) )
        {
            simpleDateFormat1 = new SimpleDateFormat( "MMM" );
            simpleDateFormat2 = new SimpleDateFormat( "MMM-yyyy" );

            for ( Period p1 : periods )
            {
                String tempPeriodName = simpleDateFormat1.format( p1.getStartDate() ) + " - "
                    + simpleDateFormat2.format( p1.getEndDate() );
                periodNameList.add( tempPeriodName );
            }
        }
        else if ( periodType.getName().equalsIgnoreCase( "yearly" ) )
        {
            simpleDateFormat1 = new SimpleDateFormat( "yyyy" );
            int year;
            for ( Period p1 : periods )
            {
                year = Integer.parseInt( simpleDateFormat1.format( p1.getStartDate() ) ) + 1;
                periodNameList.add( simpleDateFormat1.format( p1.getStartDate() ) + "-" + year );
            }
        }
        else if( periodType.getName().equalsIgnoreCase( "daily" ) )
        {
            simpleDateFormat1 = new SimpleDateFormat( "yyyy-MM-dd" );
            for ( Period p1 : periods )
            {
                String tempPeriodName = simpleDateFormat1.format( p1.getStartDate() );
                periodNameList.add( tempPeriodName );
            }
        }
        else
        {
            simpleDateFormat1 = new SimpleDateFormat( "yyyy-MM-dd" );
            for ( Period p1 : periods )
            {
                String tempPeriodName = simpleDateFormat1.format( p1.getStartDate() ) + " - "
                    + simpleDateFormat1.format( p1.getEndDate() );
                periodNameList.add( tempPeriodName );
            }
        }

        return periodNameList;
    }
    
    public double getIndividualIndicatorValue( Indicator indicator, OrganisationUnit orgunit, Date startDate, Date endDate ) 
    {
        String numeratorExp = indicator.getNumerator();
        String denominatorExp = indicator.getDenominator();
        int indicatorFactor = indicator.getIndicatorType().getFactor();
        String reportModelTB = "";
        String numeratorVal = reportservice.getIndividualResultDataValue( numeratorExp, startDate, endDate, orgunit, reportModelTB  );
        String denominatorVal = reportservice.getIndividualResultDataValue( denominatorExp, startDate, endDate, orgunit, reportModelTB );

        double numeratorValue;
        try
        {
            numeratorValue = Double.parseDouble( numeratorVal );
        } 
        catch ( Exception e )
        {
            numeratorValue = 0.0;
        }

        double denominatorValue;
        try
        {
            denominatorValue = Double.parseDouble( denominatorVal );
        } 
        catch ( Exception e )
        {
            denominatorValue = 1.0;
        }

        double aggregatedValue;
        try
        {
            if( denominatorValue == 0 )
            {
                aggregatedValue = 0.0;
            }
            else
            {
                aggregatedValue = ( numeratorValue / denominatorValue ) * indicatorFactor;
            }
        } 
        catch ( Exception e )
        {
            System.out.println( "Exception while calculating Indicator value for Indicaotr " + indicator.getName() );
            aggregatedValue = 0.0;
        }
        
        return aggregatedValue;
    }

    // -------------------------------------------------------------------------
    // Methods for getting Chart Data With Children Wise start 
    // ( this method is called when view by -> periodWise and group not selected )
    // -------------------------------------------------------------------------
    public DataElementChartResult generateDataElementChartDataWithChildrenWise( List<Date> selStartPeriodList,List<Date> selEndPeriodList,String  periodTypeLB ,List<DataElement> dataElementList, String deSelection, List<DataElementCategoryOptionCombo> decocList, OrganisationUnit selectedOrgUnit , String aggDataCB ) throws Exception
    {
       System.out.println( "inside Dashboard Service generateChartDataWithChildrenWise " );
        
       DataElementChartResult dataElementChartResult;
       
       List<OrganisationUnit> childOrgUnitList = new ArrayList<OrganisationUnit>();
       childOrgUnitList = new ArrayList<OrganisationUnit>( selectedOrgUnit.getChildren());
       
       String[] series = new String[dataElementList.size()];
       String[] categories = new String[childOrgUnitList.size()];
       Double[][] data = new Double[dataElementList.size()][childOrgUnitList.size()];
       String chartTitle = "OrganisationUnit : " + selectedOrgUnit.getShortName();
       
       String xAxis_Title = "Facilities";
       String yAxis_Title = "Value";
    
       int serviceCount = 0;     
     
       for( DataElement dataElement : dataElementList )
       {
           DataElementCategoryOptionCombo decoc;
           DataElementCategoryCombo dataElementCategoryCombo = dataElement.getCategoryCombo();

           List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>( dataElementCategoryCombo.getOptionCombos() );

           if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
           {
               decoc = decocList.get( serviceCount );
               series[serviceCount] = dataElement.getName() + " : " + decoc.getName();
           }
           else
           {
               decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
               series[serviceCount] = dataElement.getName();
           }
           
           int childCount = 0;
           for( OrganisationUnit orgChild : childOrgUnitList )
           {
               categories[childCount] = orgChild.getName();
               
               Double aggDataValue = 0.0;

               int periodCount = 0;
               for( Date startDate : selStartPeriodList )
               {
                   Date endDate = selEndPeriodList.get( periodCount );
                   PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                   Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );
                   
                   //System.out.println( periods.size() + ":" + periodType + ":" + startDate + ":" +  endDate );
                   
                   int aggChecked = Integer.parseInt( aggDataCB );
                   
                   if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
                   {
                       if( aggChecked == 1 )
                       {
                           Double tempAggDataValue = aggregationService.getAggregatedDataValue( dataElement, decoc, startDate, endDate, orgChild );
                           if( tempAggDataValue != null ) aggDataValue += tempAggDataValue;
                       }
                       else
                       {
                           for( Period period : periods )
                           {
                               DataValue dataValue = dataValueService.getDataValue( orgChild, dataElement, period, decoc );                               
                               try
                               {
                                   aggDataValue += Double.parseDouble( dataValue.getValue() );
                               }
                               catch( Exception e )
                               {                                   
                               }
                           }
                       }
                   }
                   
                   else
                   {
                       Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                       while ( optionComboIterator.hasNext() )
                       {
                           DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator.next();

                           if( aggChecked == 1 )
                           {
                               Double tempAggDataValue = aggregationService.getAggregatedDataValue( dataElement, decoc1, startDate, endDate, orgChild );
                               if( tempAggDataValue != null ) aggDataValue += tempAggDataValue;
                           }
                           else
                           {
                               for( Period period : periods )
                               {
                                   DataValue dataValue = dataValueService.getDataValue( orgChild, dataElement, period, decoc1 );                               
                                   try
                                   {
                                       aggDataValue += Double.parseDouble( dataValue.getValue() );
                                   }
                                   catch( Exception e )
                                   {                                   
                                   }
                               }
                           }
                       }
                   }

                   periodCount++;
               }
 
               data[serviceCount][childCount] = aggDataValue;
               
               if( dataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
               {
                  if ( dataElement.getNumberType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                  {
                      data[serviceCount][childCount] = Math.round( data[serviceCount][childCount] * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
                  }
                  else
                  {
                      data[serviceCount][childCount] = Math.round( data[serviceCount][childCount] * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                  }
               }
               childCount++;
           }
           
           serviceCount++;          
       }
    
       dataElementChartResult = new DataElementChartResult( series, categories, data, chartTitle, xAxis_Title, yAxis_Title );
       return dataElementChartResult;
    }
    
    
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data With groupMember Wise start 
    // ( this method is called when view by -> periodWise and group  selected )
    // -------------------------------------------------------------------------
    
    public DataElementChartResult generateDataElementChartDataWithGroupMemberWise( List<Date> selStartPeriodList,List<Date> selEndPeriodList,String  periodTypeLB ,List<DataElement> dataElementList, String deSelection, List<DataElementCategoryOptionCombo> decocList, OrganisationUnit selectedOrgUnit , OrganisationUnitGroup selectedOrgUnitGroup , String aggDataCB ) throws Exception
    {
        System.out.println( "inside Dashboard Service generateChartDataWithGroupMemberWise " );
        
        DataElementChartResult dataElementChartResult;
        
        List<OrganisationUnit> selectedOUGroupMemberList = new ArrayList<OrganisationUnit>( selectedOrgUnitGroup.getMembers() );
       
        List<OrganisationUnit> childOrgUnitList = new ArrayList<OrganisationUnit>();
        childOrgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selectedOrgUnit.getId() ) );
       
        selectedOUGroupMemberList.retainAll( childOrgUnitList );
        
        String[] series = new String[dataElementList.size()];
        String[] categories = new String[selectedOUGroupMemberList.size()];
        Double[][] data = new Double[dataElementList.size()][selectedOUGroupMemberList.size()];
        String chartTitle = "OrganisationUnit : " + selectedOrgUnit.getShortName()+ "(" + selectedOrgUnitGroup.getName() +  ")";
       
        String xAxis_Title = "Facilities";
        String yAxis_Title = "Value";
    
       int serviceCount = 0;     
     
       for( DataElement dataElement : dataElementList )
       {
           DataElementCategoryOptionCombo decoc;
           DataElementCategoryCombo dataElementCategoryCombo = dataElement.getCategoryCombo();
           List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>( dataElementCategoryCombo.getOptionCombos() );
          
           if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
           {
               decoc = decocList.get( serviceCount );
               series[serviceCount] = dataElement.getName() + " : " + decoc.getName();
           }
           else
           {
               decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
               series[serviceCount] = dataElement.getName();
           }
           
           int GroupMemberCount = 0;
           for( OrganisationUnit orgUnit : selectedOUGroupMemberList )
           {
               categories[GroupMemberCount] = orgUnit.getName();
               
               Double aggDataValue = 0.0;

               int periodCount = 0;
               for( Date startDate : selStartPeriodList )
               {
                   Date endDate = selEndPeriodList.get( periodCount );
                   Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );
                       
                   int aggChecked = Integer.parseInt( aggDataCB );
                   
                   if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
                   {
                       if( aggChecked == 1 )
                       {
                           Double tempAggDataValue = aggregationService.getAggregatedDataValue( dataElement, decoc, startDate, endDate, orgUnit );
                           if( tempAggDataValue != null ) aggDataValue += tempAggDataValue;
                       }
                       else
                       {
                           for( Period period : periods )
                           {
                               DataValue dataValue = dataValueService.getDataValue( orgUnit, dataElement, period, decoc );
                               try
                               {
                                   aggDataValue += Double.parseDouble( dataValue.getValue() );
                               }
                               catch( Exception e )
                               {
                               }
                           }
                       }
                   }
                   else
                   {
                       Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                       while ( optionComboIterator.hasNext() )
                       {
                           DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator.next();

                           if( aggChecked == 1 )
                           {
                               Double tempAggDataValue = aggregationService.getAggregatedDataValue( dataElement, decoc1, startDate, endDate, orgUnit );
                               if( tempAggDataValue != null ) aggDataValue += tempAggDataValue;
                           }
                           else
                           {
                               for( Period period : periods )
                               {
                                   DataValue dataValue = dataValueService.getDataValue( orgUnit, dataElement, period, decoc1 );
                                   try
                                   {
                                       aggDataValue += Double.parseDouble( dataValue.getValue() );
                                   }
                                   catch( Exception e )
                                   {
                                   }
                               }
                           }
                       }
                   }

                   periodCount++;
               }
 
               data[serviceCount][GroupMemberCount] = aggDataValue;
               
               if( dataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
               {
                  if ( dataElement.getNumberType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                  {
                      data[serviceCount][GroupMemberCount] = Math.round( data[serviceCount][GroupMemberCount] * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
                  }
                  else
                  {
                      data[serviceCount][GroupMemberCount] = Math.round( data[serviceCount][GroupMemberCount] * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                  }
               }
               GroupMemberCount++;
           }
           
           serviceCount++;          
       }
    
       dataElementChartResult = new DataElementChartResult( series, categories, data, chartTitle, xAxis_Title, yAxis_Title );

       return dataElementChartResult;
    }
    
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data only Period Wise start ( this method is called when view by ->Selected + children and  Group not selected,and view by -> children and group selected )
    // -------------------------------------------------------------------------
    
    public DataElementChartResult generateDataElementChartDataWithPeriodWise( List<Date> selStartPeriodList,List<Date> selEndPeriodList,List<String> periodNames,String  periodTypeLB ,List<DataElement> dataElementList, String deSelection, List<DataElementCategoryOptionCombo> decocList, OrganisationUnit selectedOrgUnit , String aggDataCB ) throws Exception
    {
       DataElementChartResult dataElementChartResult;
       
       System.out.println( "inside Dashboard Service generate Chart Data With Period Wise " );
       
       String[] series = new String[dataElementList.size()];
       String[] categories = new String[selStartPeriodList.size()];
       Double[][] data = new Double[dataElementList.size()][selStartPeriodList.size()];
       String chartTitle = "OrganisationUnit : " + selectedOrgUnit.getShortName();
       String xAxis_Title = "Time Line";
       String yAxis_Title = "Value";
    
       int serviceCount = 0;     
     
       for( DataElement dataElement : dataElementList )
       {
           DataElementCategoryOptionCombo decoc;
           DataElementCategoryCombo dataElementCategoryCombo = dataElement.getCategoryCombo();
           List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>( dataElementCategoryCombo.getOptionCombos() );

           if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
           {
               decoc = decocList.get( serviceCount );
               series[serviceCount] = dataElement.getName() + " : " + decoc.getName();
           }
           else
           {
               decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
               series[serviceCount] = dataElement.getName();
           }
           
           int periodCount = 0;
           for( Date startDate : selStartPeriodList )
           {
               Date endDate = selEndPeriodList.get( periodCount );
               
               categories[periodCount] = periodNames.get( periodCount );
               
               Double aggDataValue = 0.0;
               int aggChecked = Integer.parseInt( aggDataCB );
               
               if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
               {
                   if( aggChecked == 1 )
                   {
                       Double tempAggDataValue = aggregationService.getAggregatedDataValue( dataElement, decoc, startDate, endDate, selectedOrgUnit );
                       if( tempAggDataValue != null ) aggDataValue += tempAggDataValue;
                   }
                   else
                   {
                       Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );
                       for( Period period : periods )
                       {
                           DataValue dataValue = dataValueService.getDataValue( selectedOrgUnit, dataElement, period, decoc );
                           try
                           {
                               aggDataValue += Double.parseDouble( dataValue.getValue() );
                           }
                           catch( Exception e )
                           {
                           }
                       }
                   }
               }
               else
               {
                   Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                   while ( optionComboIterator.hasNext() )
                   {
                       DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator.next();

                       if( aggChecked == 1 )
                       {
                           Double tempAggDataValue = aggregationService.getAggregatedDataValue( dataElement, decoc1, startDate, endDate, selectedOrgUnit );
                           if( tempAggDataValue != null ) aggDataValue += tempAggDataValue;
                       }
                       else
                       {
                           Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );
                           for( Period period : periods )
                           {
                               DataValue dataValue = dataValueService.getDataValue( selectedOrgUnit, dataElement, period, decoc1 );
                               try
                               {
                                   aggDataValue += Double.parseDouble( dataValue.getValue() );
                               }
                               catch( Exception e )
                               {
                               }
                           }
                       }
                   }
               }
               data[serviceCount][periodCount] = aggDataValue;
               
               if( dataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
               {
                  if ( dataElement.getNumberType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                  {
                      data[serviceCount][periodCount] = Math.round( data[serviceCount][periodCount] * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
                  }
                  else
                  {
                      data[serviceCount][periodCount] = Math.round( data[serviceCount][periodCount] * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                  }
               }
               periodCount++;
           }
           
           serviceCount++;          
       }
       
       dataElementChartResult = new DataElementChartResult( series, categories, data, chartTitle, xAxis_Title, yAxis_Title );

       return dataElementChartResult;
    }
    
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data OrgGroup Period Wise start
    // -------------------------------------------------------------------------
       
    public DataElementChartResult generateDataElementChartDataWithGroupToPeriodWise( List<Date> selStartPeriodList,List<Date> selEndPeriodList,List<String> periodNames,String  periodTypeLB ,List<DataElement> dataElementList, String deSelection, List<DataElementCategoryOptionCombo> decocList, OrganisationUnit selectedOrgUnit , OrganisationUnitGroup selectedOrgUnitGroup , String aggDataCB ) throws Exception
    {
       DataElementChartResult dataElementChartResult;
       
       List<OrganisationUnit> selectedOUGroupMemberList = new ArrayList<OrganisationUnit>( selectedOrgUnitGroup.getMembers() );
       
       List<OrganisationUnit> childOrgUnitList = new ArrayList<OrganisationUnit>();
       childOrgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selectedOrgUnit.getId() ) );
      
       selectedOUGroupMemberList.retainAll( childOrgUnitList );

       String[] series = new String[dataElementList.size()];
       String[] categories = new String[selStartPeriodList.size()];
       Double[][] data = new Double[dataElementList.size()][selStartPeriodList.size()];
       String chartTitle = "OrganisationUnit : " + selectedOrgUnit.getShortName()+ "( Group - " + selectedOrgUnitGroup.getName() +  " )";
       String xAxis_Title = "Time Line";
       String yAxis_Title = "Value";
       
       int serviceCount = 0;     
     
       for( DataElement dataElement : dataElementList )
       {
           DataElementCategoryOptionCombo decoc;
           DataElementCategoryCombo dataElementCategoryCombo = dataElement.getCategoryCombo();
           List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>( dataElementCategoryCombo.getOptionCombos() );

           if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
           {
               decoc = decocList.get( serviceCount );
               series[serviceCount] = dataElement.getName() + " : " + decoc.getName();
           }
           else
           {
               decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
               series[serviceCount] = dataElement.getName();
           }

           int periodCount = 0;
           for( Date startDate : selStartPeriodList )
           {
               Date endDate = selEndPeriodList.get( periodCount );
               categories[periodCount] = periodNames.get( periodCount );
               Double aggDataValue = 0.0;
               Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );
                  
               int orgGroupCount = 0;
                       
               for( OrganisationUnit orgUnit : selectedOUGroupMemberList )
               {
                   int aggChecked = Integer.parseInt( aggDataCB );
                   
                   if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
                   {
                       if( aggChecked == 1 )
                       {
                           Double tempAggDataValue = aggregationService.getAggregatedDataValue( dataElement, decoc, startDate, endDate, orgUnit );
                           if(tempAggDataValue != null ) aggDataValue += tempAggDataValue;
                       }
                       else
                       {
                           for( Period period : periods )
                           {
                               DataValue dataValue = dataValueService.getDataValue( orgUnit, dataElement, period, decoc );
                               try
                               {
                                   aggDataValue += Double.parseDouble( dataValue.getValue() );
                               }
                               catch( Exception e )
                               {
                               }
                           }
                       }
                   }
                   else
                   {
                       Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                       while ( optionComboIterator.hasNext() )
                       {
                           DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator.next();

                           if( aggChecked == 1 )
                           {
                               Double tempAggDataValue = aggregationService.getAggregatedDataValue( dataElement, decoc1, startDate, endDate, orgUnit );
                               if(tempAggDataValue != null ) aggDataValue += tempAggDataValue;
                           }
                           else
                           {
                               for( Period period : periods )
                               {
                                   DataValue dataValue = dataValueService.getDataValue( orgUnit, dataElement, period, decoc1 );
                                   try
                                   {
                                       aggDataValue += Double.parseDouble( dataValue.getValue() );
                                   }
                                   catch( Exception e )
                                   {
                                   }
                               }
                           }
                       }
                   }
                   orgGroupCount++;
               }
   
           data[serviceCount][periodCount] = aggDataValue;
               
           if( dataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
           {
               if ( dataElement.getNumberType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
               {
                   data[serviceCount][periodCount] = Math.round( data[serviceCount][periodCount] * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
               }
               else
               {
                   data[serviceCount][periodCount] = Math.round( data[serviceCount][periodCount] * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
               }
               }
               periodCount++;    
           }
           
           serviceCount++;          
       }
    
       dataElementChartResult = new DataElementChartResult( series, categories, data, chartTitle, xAxis_Title, yAxis_Title );
       
      return dataElementChartResult;
    
   }
   
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data With Children Wise start 
    // ( this method is called when view by -> periodWise and group not selected ) -Indicator Wise
    // -------------------------------------------------------------------------
        
    public IndicatorChartResult generateIndicatorChartDataWithChildrenWise( List<Date> selStartPeriodList,List<Date> selEndPeriodList, String  periodTypeLB, List<Indicator> indicatorList, OrganisationUnit selectedOrgUnit , String aggDataCB ) throws Exception
    {
        System.out.println( "inside Dashboard Service generate Chart Data With Children Wise " );
        
        IndicatorChartResult indicatorChartResult;
        
        List<OrganisationUnit> childOrgUnitList = new ArrayList<OrganisationUnit>();
        childOrgUnitList = new ArrayList<OrganisationUnit>( selectedOrgUnit.getChildren());
        
        String[] series = new String[indicatorList.size()];
        String[] categories = new String[childOrgUnitList.size()];

        Double[][] numDataArray = new Double[indicatorList.size()][childOrgUnitList.size()];
        Double[][] denumDataArray = new Double[indicatorList.size()][childOrgUnitList.size()];
        Double[][] data = new Double[indicatorList.size()][childOrgUnitList.size()];

        String chartTitle = "OrganisationUnit : " + selectedOrgUnit.getShortName();

        String xAxis_Title = "Facilities";
        String yAxis_Title = "Value";

        int serviceCount = 0;

        for ( Indicator indicator : indicatorList )
        {
            series[serviceCount] = indicator.getName();

            int childCount = 0;
            for ( OrganisationUnit orgChild : childOrgUnitList )
            {
                categories[childCount] = orgChild.getName();

                Double aggIndicatorValue = 0.0;
                Double aggIndicatorNumValue = 0.0;
                Double aggIndicatorDenumValue = 0.0;
                int periodCount = 0;
                for ( Date startDate : selStartPeriodList )
                {
                    Date endDate = selEndPeriodList.get( periodCount );
                    int aggChecked = Integer.parseInt( aggDataCB );
                        
                    if( aggChecked == 1 )
                    {
                        Double tempAggIndicatorNumValue = aggregationService.getAggregatedNumeratorValue( indicator, startDate, endDate, orgChild );
                        Double tempAggIndicatorDenumValue = aggregationService.getAggregatedDenominatorValue( indicator, startDate, endDate, orgChild );

                        if ( tempAggIndicatorNumValue != null )
                        {
                            aggIndicatorNumValue += tempAggIndicatorNumValue;
                        }
                        
                        if ( tempAggIndicatorDenumValue != null )
                        {
                            if( !indicator.getDenominator().trim().equals( "1" ) )
                            {
                                aggIndicatorDenumValue += tempAggIndicatorDenumValue;
                            }
                            else 
                            {
                                aggIndicatorDenumValue = 1.0;
                            }
                        }
                        /*
                        if ( tempAggIndicatorDenumValue != null )
                        {
                            aggIndicatorDenumValue += tempAggIndicatorDenumValue;
                        }
                        */
                    }
                    else
                    {
                        Double tempAggIndicatorNumValue = 0.0;
                        String tempStr = reportservice.getIndividualResultDataValue( indicator.getNumerator(), startDate, endDate, orgChild, "" );
                        try
                        {
                            tempAggIndicatorNumValue = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            tempAggIndicatorNumValue = 0.0;
                        }
                        aggIndicatorNumValue += tempAggIndicatorNumValue;

                        Double tempAggIndicatorDenumValue = 0.0;

                        tempStr = reportservice.getIndividualResultDataValue( indicator.getDenominator(), startDate, endDate, orgChild, "" );
                        try
                        {
                            tempAggIndicatorDenumValue = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            tempAggIndicatorDenumValue = 0.0;
                        }
                        
                        if( !indicator.getDenominator().trim().equals( "1" ) )
                        {
                            aggIndicatorDenumValue += tempAggIndicatorDenumValue;
                        }
                        else 
                        {
                            aggIndicatorDenumValue = 1.0;
                        }
                        
                        //aggIndicatorDenumValue += tempAggIndicatorDenumValue;

                    }

                    periodCount++;
                }
                try
                {
                    if ( aggIndicatorDenumValue == 0 )
                    {
                        aggIndicatorValue = 0.0;
                    }
                    else
                    {
                        aggIndicatorValue = (aggIndicatorNumValue / aggIndicatorDenumValue) * indicator.getIndicatorType().getFactor();
                    }
                }
                catch ( Exception e )
                {
                    aggIndicatorValue = 0.0;
                }

                // rounding indicator value ,Numenetor,denumenetor
                data[serviceCount][childCount] = aggIndicatorValue;
                data[serviceCount][childCount] = Math.round( data[serviceCount][childCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );

                numDataArray[serviceCount][childCount] = aggIndicatorNumValue;
                numDataArray[serviceCount][childCount] = Math.round( numDataArray[serviceCount][childCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );
                
                denumDataArray[serviceCount][childCount] = aggIndicatorDenumValue;
                denumDataArray[serviceCount][childCount] = Math.round( denumDataArray[serviceCount][childCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );
                childCount++;
            }

            serviceCount++;
        }

        indicatorChartResult = new IndicatorChartResult( series, categories, data, numDataArray, denumDataArray,chartTitle, xAxis_Title, yAxis_Title );
        return indicatorChartResult;
    }

    // -------------------------------------------------------------------------
    // Methods for getting Chart Data With groupMember Wise start ( this method is called when view by -> periodWise and group  selected ) --- indicator Wise
    // -------------------------------------------------------------------------
 
    public IndicatorChartResult generateIndicatorChartDataWithGroupMemberWise( List<Date> selStartPeriodList,List<Date> selEndPeriodList,String  periodTypeLB ,List<Indicator> indicatorList, OrganisationUnit selectedOrgUnit , OrganisationUnitGroup selectedOrgUnitGroup , String aggDataCB ) throws Exception
    {
        System.out.println( " inside Dashboard Service generate Indicator Chart Data With Group Member Wise " );
        
        IndicatorChartResult indicatorChartResult;
        
        List<OrganisationUnit> selectedOUGroupMemberList = new ArrayList<OrganisationUnit>( selectedOrgUnitGroup.getMembers() );
       
        List<OrganisationUnit> childOrgUnitList = new ArrayList<OrganisationUnit>();
        childOrgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selectedOrgUnit.getId() ) );
       
        selectedOUGroupMemberList.retainAll( childOrgUnitList );
        
        String[] series = new String[indicatorList.size()];
        String[] categories = new String[selectedOUGroupMemberList.size()];

        Double[][] numDataArray = new Double[indicatorList.size()][selectedOUGroupMemberList.size()];
        Double[][] denumDataArray = new Double[indicatorList.size()][selectedOUGroupMemberList.size()];
        Double[][] data = new Double[indicatorList.size()][selectedOUGroupMemberList.size()];
        String chartTitle = "OrganisationUnit : " + selectedOrgUnit.getShortName()+ "( Group - " + selectedOrgUnitGroup.getName() +  ")";
        String xAxis_Title = "Facilities";
        String yAxis_Title = "Value";
    
       
        int serviceCount = 0;

        for ( Indicator indicator : indicatorList )
        {
            series[serviceCount] = indicator.getName();

            int childCount = 0;
            for ( OrganisationUnit orgChild : selectedOUGroupMemberList )
            {
                categories[childCount] = orgChild.getName();
                Double aggIndicatorValue = 0.0;
                Double aggIndicatorNumValue = 0.0;
                Double aggIndicatorDenumValue = 0.0;
                int periodCount = 0;

                for ( Date startDate : selStartPeriodList )
                {
                    Date endDate = selEndPeriodList.get( periodCount );

                    int aggChecked = Integer.parseInt( aggDataCB );
                    
                    if( aggChecked == 1 )
                    {

                        Double tempAggIndicatorNumValue = aggregationService.getAggregatedNumeratorValue( indicator,
                            startDate, endDate, orgChild );
                        Double tempAggIndicatorDenumValue = aggregationService.getAggregatedDenominatorValue(
                            indicator, startDate, endDate, orgChild );

                        if ( tempAggIndicatorNumValue != null )
                        {
                            aggIndicatorNumValue += tempAggIndicatorNumValue;
                        }
                        
                        if ( tempAggIndicatorDenumValue != null )
                        {
                            if( !indicator.getDenominator().trim().equals( "1" ) )
                            {
                                aggIndicatorDenumValue += tempAggIndicatorDenumValue;
                            }
                            else 
                            {
                                aggIndicatorDenumValue = 1.0;
                            }
                        }
                        /*
                        if ( tempAggIndicatorDenumValue != null )
                        {
                            aggIndicatorDenumValue += tempAggIndicatorDenumValue;
                        }
                        */
                    }
                    else
                    {
                        Double tempAggIndicatorNumValue = 0.0;
                        String tempStr = reportservice.getIndividualResultDataValue( indicator.getNumerator(),
                            startDate, endDate, orgChild, "" );
                        try
                        {
                            tempAggIndicatorNumValue = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            tempAggIndicatorNumValue = 0.0;
                        }
                        aggIndicatorNumValue += tempAggIndicatorNumValue;

                        Double tempAggIndicatorDenumValue = 0.0;

                        tempStr = reportservice.getIndividualResultDataValue( indicator.getDenominator(), startDate,
                            endDate, orgChild, "" );
                        try
                        {
                            tempAggIndicatorDenumValue = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            tempAggIndicatorDenumValue = 0.0;
                        }
                        
                        if( !indicator.getDenominator().trim().equals( "1" ) )
                        {
                            aggIndicatorDenumValue += tempAggIndicatorDenumValue;
                        }
                        else 
                        {
                            aggIndicatorDenumValue = 1.0;
                        }
                        
                        
                        //aggIndicatorDenumValue += tempAggIndicatorDenumValue;
                    }

                    periodCount++;
                }
                try
                {
                    if ( aggIndicatorDenumValue == 0 )
                    {
                        aggIndicatorValue = 0.0;
                    }
                    else
                    {
                        aggIndicatorValue = (aggIndicatorNumValue / aggIndicatorDenumValue)
                            * indicator.getIndicatorType().getFactor();
                    }
                }
                catch ( Exception e )
                {
                    aggIndicatorValue = 0.0;
                }

                // rounding indicator value ,Numenetor,denumenetor
                data[serviceCount][childCount] = aggIndicatorValue;
                data[serviceCount][childCount] = Math.round( data[serviceCount][childCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );

                numDataArray[serviceCount][childCount] = aggIndicatorNumValue;
                numDataArray[serviceCount][childCount] = Math.round( numDataArray[serviceCount][childCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );
                
                denumDataArray[serviceCount][childCount] = aggIndicatorDenumValue;
                denumDataArray[serviceCount][childCount] = Math.round( denumDataArray[serviceCount][childCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );
                childCount++;
            }

            serviceCount++;
        }

        indicatorChartResult = new IndicatorChartResult( series, categories, data, numDataArray, denumDataArray,chartTitle, xAxis_Title, yAxis_Title );
        return indicatorChartResult;
    }

    // -------------------------------------------------------------------------
    // for Indicator
    // Methods for getting Chart Data only Period Wise start 
    // ( this method is called when view by ->Selected + children and  Group not selected,and view by -> children and group selected )
    // -------------------------------------------------------------------------
    
    public IndicatorChartResult generateIndicatorChartDataWithPeriodWise( List<Date> selStartPeriodList,List<Date> selEndPeriodList,List<String> periodNames,String  periodTypeLB ,List<Indicator> indicatorList,  OrganisationUnit selectedOrgUnit , String aggDataCB ) throws Exception
    {
       System.out.println( "inside Dashboard Service generate Chart Data With Period Wise " );
       
       IndicatorChartResult indicatorChartResult;

       String[] series = new String[indicatorList.size()];
       String[] categories = new String[selStartPeriodList.size()];
       Double[][] data = new Double[indicatorList.size()][selStartPeriodList.size()];

       Double[][] numDataArray = new Double[indicatorList.size()][selStartPeriodList.size()];
       Double[][] denumDataArray = new Double[indicatorList.size()][selStartPeriodList.size()];

       String chartTitle = "OrganisationUnit : " + selectedOrgUnit.getShortName();
       String xAxis_Title = "Time Line";
       String yAxis_Title = "Value";

       int serviceCount = 0;
       for ( Indicator indicator : indicatorList )
       {
           series[serviceCount] = indicator.getName();

           int periodCount = 0;
           for ( Date startDate : selStartPeriodList )
           {
               Date endDate = selEndPeriodList.get( periodCount );

               categories[periodCount] = periodNames.get( periodCount );

               Double aggIndicatorValue = 0.0;
               Double aggIndicatorNumValue = 0.0;
               Double aggIndicatorDenumValue = 0.0;
               
               int aggChecked = Integer.parseInt( aggDataCB );
               
               if( aggChecked == 1 )
               {
                   aggIndicatorValue = aggregationService.getAggregatedIndicatorValue( indicator, startDate, endDate, selectedOrgUnit );

                   aggIndicatorNumValue = aggregationService.getAggregatedNumeratorValue( indicator, startDate, endDate, selectedOrgUnit );
                   aggIndicatorDenumValue = aggregationService.getAggregatedDenominatorValue( indicator, startDate, endDate, selectedOrgUnit );

                   if ( aggIndicatorValue == null ) aggIndicatorValue = 0.0;
               }
               else
               {
                   aggIndicatorValue = getIndividualIndicatorValue( indicator, selectedOrgUnit, startDate, endDate );
                   String tempStr = reportservice.getIndividualResultDataValue( indicator.getNumerator(), startDate, endDate, selectedOrgUnit, "" );

                   try
                   {
                       aggIndicatorNumValue = Double.parseDouble( tempStr );
                   }
                   catch ( Exception e )
                   {
                       aggIndicatorNumValue = 0.0;
                   }

                   tempStr = reportservice.getIndividualResultDataValue( indicator.getDenominator(), startDate, endDate, selectedOrgUnit, "" );

                   try
                   {
                       aggIndicatorDenumValue = Double.parseDouble( tempStr );
                   }
                   catch ( Exception e )
                   {
                       aggIndicatorDenumValue = 0.0;
                   }
               }

               // rounding indicator value ,Numenetor,denumenetor
               data[serviceCount][periodCount] = aggIndicatorValue;
               data[serviceCount][periodCount] = Math.round( data[serviceCount][periodCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );

               numDataArray[serviceCount][periodCount] = aggIndicatorNumValue;
               numDataArray[serviceCount][periodCount] = Math.round( numDataArray[serviceCount][periodCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );
               
               denumDataArray[serviceCount][periodCount] = aggIndicatorDenumValue;
               denumDataArray[serviceCount][periodCount] = Math.round( denumDataArray[serviceCount][periodCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );

               periodCount++;
           }

           serviceCount++;
       }

       indicatorChartResult = new IndicatorChartResult( series, categories, data, numDataArray, denumDataArray,chartTitle, xAxis_Title, yAxis_Title );

       return indicatorChartResult;
    }
    
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data OrgGroup Period Wise start - IndicatorWise
    // -------------------------------------------------------------------------
    
    public IndicatorChartResult generateIndicatorChartDataWithGroupToPeriodWise( List<Date> selStartPeriodList,List<Date> selEndPeriodList,List<String> periodNames,String  periodTypeLB ,List<Indicator> indicatorList, OrganisationUnit selectedOrgUnit , OrganisationUnitGroup selectedOrgUnitGroup , String aggDataCB )
        throws Exception
    {
        IndicatorChartResult indicatorChartResult;

        List<OrganisationUnit> selectedOUGroupMemberList = new ArrayList<OrganisationUnit>( selectedOrgUnitGroup.getMembers() );
        
        List<OrganisationUnit> childOrgUnitList = new ArrayList<OrganisationUnit>();
        childOrgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selectedOrgUnit.getId() ) );
       
        selectedOUGroupMemberList.retainAll( childOrgUnitList );
        
        String[] series = new String[indicatorList.size()];
        String[] categories = new String[selStartPeriodList.size()];

        Double[][] numDataArray = new Double[indicatorList.size()][selStartPeriodList.size()];
        Double[][] denumDataArray = new Double[indicatorList.size()][selStartPeriodList.size()];
        Double[][] data = new Double[indicatorList.size()][selStartPeriodList.size()];

        String chartTitle = "OrganisationUnit : " + selectedOrgUnit.getShortName() + "( Group - " + selectedOrgUnitGroup.getName() + " )";
        String xAxis_Title = "Time Line";
        String yAxis_Title = "Value";

        int serviceCount = 0;

        for ( Indicator indicator : indicatorList )
        {
            series[serviceCount] = indicator.getName();

            Double aggIndicatorValue = 0.0;
            Double aggIndicatorNumValue = 0.0;
            Double aggIndicatorDenumValue = 0.0;

            int periodCount = 0;
            for ( Date startDate : selStartPeriodList )
            {
                Date endDate = selEndPeriodList.get( periodCount );
                categories[periodCount] = periodNames.get( periodCount );
                
                int orgGroupCount = 0;

                for ( OrganisationUnit orgUnit : selectedOUGroupMemberList )
                {
                    int aggChecked = Integer.parseInt( aggDataCB );
                    
                    if( aggChecked == 1 )
                    {
                        Double tempAggIndicatorNumValue = aggregationService.getAggregatedNumeratorValue( indicator, startDate, endDate, orgUnit );
                        Double tempAggIndicatorDenumValue = aggregationService.getAggregatedDenominatorValue( indicator, startDate, endDate, orgUnit );

                        if ( tempAggIndicatorNumValue != null )
                        {
                            aggIndicatorNumValue += tempAggIndicatorNumValue;
                        }
                        
                        if ( tempAggIndicatorDenumValue != null )
                        {
                            if( !indicator.getDenominator().trim().equals( "1" ) )
                            {
                                aggIndicatorDenumValue += tempAggIndicatorDenumValue;
                            }
                            else 
                            {
                                aggIndicatorDenumValue = 1.0;
                            }
                        }
                        
                        /*
                        if ( tempAggIndicatorDenumValue != null )
                        {
                            aggIndicatorDenumValue += tempAggIndicatorDenumValue;
                        }
                        */
                    }
                    else
                    {
                        Double tempAggIndicatorNumValue = 0.0;

                        String tempStr = reportservice.getIndividualResultDataValue( indicator.getNumerator(), startDate, endDate, orgUnit, "" );
                        try
                        {
                            tempAggIndicatorNumValue = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            tempAggIndicatorNumValue = 0.0;
                        }
                        aggIndicatorNumValue += tempAggIndicatorNumValue;

                        Double tempAggIndicatorDenumValue = 0.0;

                        tempStr = reportservice.getIndividualResultDataValue( indicator.getDenominator(), startDate, endDate, orgUnit, "" );
                        try
                        {
                            tempAggIndicatorDenumValue = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            tempAggIndicatorDenumValue = 0.0;
                        }
                       
                        if( !indicator.getDenominator().trim().equals( "1" ) )
                        {
                            aggIndicatorDenumValue += tempAggIndicatorDenumValue;
                        }
                        else 
                        {
                            aggIndicatorDenumValue = 1.0;
                        }
                        
                        
                        //aggIndicatorDenumValue += tempAggIndicatorDenumValue;

                    }
                    orgGroupCount++;
                }

                try
                {
                    if ( aggIndicatorDenumValue == 0 )
                    {
                        aggIndicatorValue = 0.0;
                    }
                    else
                    {
                        aggIndicatorValue = (aggIndicatorNumValue / aggIndicatorDenumValue)
                            * indicator.getIndicatorType().getFactor();
                    }
                }
                catch ( Exception e )
                {
                    aggIndicatorValue = 0.0;
                }

                // rounding indicator value ,Numenetor,denumenetor
                data[serviceCount][periodCount] = aggIndicatorValue;
                data[serviceCount][periodCount] = Math.round( data[serviceCount][periodCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );

                numDataArray[serviceCount][periodCount] = aggIndicatorNumValue;
                numDataArray[serviceCount][periodCount] = Math.round( numDataArray[serviceCount][periodCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );
                
                denumDataArray[serviceCount][periodCount] = aggIndicatorDenumValue;
                denumDataArray[serviceCount][periodCount] = Math.round( denumDataArray[serviceCount][periodCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );
                
                periodCount++;
            }

            serviceCount++;
        }

        indicatorChartResult = new IndicatorChartResult( series, categories, data, numDataArray, denumDataArray, chartTitle, xAxis_Title, yAxis_Title );

        return indicatorChartResult;
    }
    
} // class end
