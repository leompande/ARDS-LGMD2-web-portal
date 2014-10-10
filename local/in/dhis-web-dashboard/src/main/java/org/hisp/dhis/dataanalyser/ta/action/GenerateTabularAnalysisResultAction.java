package org.hisp.dhis.dataanalyser.ta.action;

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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.FinancialAprilPeriodType;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.hisp.dhis.period.SixMonthlyPeriodType;
import org.hisp.dhis.period.WeeklyPeriodType;
import org.hisp.dhis.period.YearlyPeriodType;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.system.util.MathUtils;

import com.opensymphony.xwork2.Action;

public class GenerateTabularAnalysisResultAction
    implements Action
{

    private final String ORGUNITSELECTED = "orgUnitSelectedRadio";

    private final String ORGUNITGRP = "orgUnitGroupRadio";

    private final String ORGUNITLEVEL = "orgUnitLevelRadio";
    
    private final String GENERATEAGGDATA = "generateaggdata";

    private final String USEEXISTINGAGGDATA = "useexistingaggdata";

    private final String USECAPTUREDDATA = "usecaptureddata";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    // @SuppressWarnings("unused")
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }
/*
    private ConfigurationService configurationService;

    public void setConfigurationService( ConfigurationService configurationService )
    {
        this.configurationService = configurationService;
    }
*/
    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    private List<OrganisationUnit> selOUList;

    private List<Date> selStartPeriodList;

    private List<Date> selEndPeriodList;

    private OrganisationUnit selOrgUnit;

    private List<String> selectedServices;

    public void setSelectedServices( List<String> selectedServices )
    {
        this.selectedServices = selectedServices;
    }

    private String deSelection;

    public void setDeSelection( String deSelection )
    {
        this.deSelection = deSelection;
    }

    private List<String> orgUnitListCB;

    public void setOrgUnitListCB( List<String> orgUnitListCB )
    {
        this.orgUnitListCB = orgUnitListCB;
    }

    private Integer orgUnitLevelCB;

    public void setOrgUnitLevelCB( Integer orgUnitLevelCB )
    {
        this.orgUnitLevelCB = orgUnitLevelCB;
    }

    private String periodTypeLB;

    public void setPeriodTypeLB( String periodTypeLB )
    {
        this.periodTypeLB = periodTypeLB;
    }

    private String aggPeriodCB;

    public void setAggPeriodCB( String aggPeriodCB )
    {
        this.aggPeriodCB = aggPeriodCB;
    }

    private List<String> yearLB;

    public void setYearLB( List<String> yearLB )
    {
        this.yearLB = yearLB;
    }

    private List<String> periodLB;

    public void setPeriodLB( List<String> periodLB )
    {
        this.periodLB = periodLB;
    }

    private String orgUnitSelListCB;
    
    public void setOrgUnitSelListCB(String orgUnitSelListCB) 
    {
        this.orgUnitSelListCB = orgUnitSelListCB;
    }

    private String aggData;
    
    public void setAggData( String aggData )
    {
        this.aggData = aggData;
    }

    List<String> periodNames;

    //private Map<OrganisationUnit, Integer> ouChildCountMap;

    String dataElementIdsByComma;
    
    String periodIdsByComma;
    
    String orgUnitIdsByComma;
    
    List<DataElement> dataElementList;
    List<Indicator> indicatorList;
    List<String> serviceTypeList;
    Map<Integer, List<Integer>> periodMap;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        /* Initialization */
        statementManager.initialise();

        selOUList = new ArrayList<OrganisationUnit>();
        selStartPeriodList = new ArrayList<Date>();
        selEndPeriodList = new ArrayList<Date>();
        dataElementList = new ArrayList<DataElement>();
        indicatorList = new ArrayList<Indicator>();
        serviceTypeList = new ArrayList<String>();
        periodMap = new HashMap<Integer, List<Integer>>();
       // ouChildCountMap = new HashMap<OrganisationUnit, Integer>();
        
        /* Period Info */
        String monthOrder[] = {  "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
        int monthDays[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        
        // for financial year 
        String financialMonthOrder[] = { "04", "05", "06", "07", "08", "09", "10", "11", "12", "01", "02", "03" };
        int financialMonthDays[] = { 30, 31, 30, 31, 31, 30, 31, 30, 31, 31, 28, 31 };
        
        String startD = "";
        String endD = "";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
       
        periodNames = new ArrayList<String>();

        // for weekly period
        if ( periodTypeLB.equalsIgnoreCase( WeeklyPeriodType.NAME ) )
        {
            Integer pCount = 0;
            for ( String periodStr : periodLB )
            {
                String  startWeekDate = periodStr.split( "To" )[0] ; //for start week
                String  endWeekDate = periodStr.split( "To" )[1] ; //for end week
                
                startD = startWeekDate.trim();
                endD = endWeekDate.trim();
                
                Date sDate = format.parseDate( startD );
                Date eDate = format.parseDate( endD );
                selStartPeriodList.add( sDate );
                selEndPeriodList.add( eDate );
                
                List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate, eDate ) );
                List<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );
                periodMap.put( pCount, periodIds );
                
                pCount++;
                
                periodNames.add( periodStr );
            }
        }
        // for FinancialAprilPeriodType
        else if ( periodTypeLB.equalsIgnoreCase( FinancialAprilPeriodType.NAME ) )
        {
            Integer pCount = 0;
            for ( String year : yearLB )
            {
                int selYear = Integer.parseInt( year.split( "-" )[0] );
                
                for ( String periodStr : periodLB )
                {
                    int period = Integer.parseInt( periodStr );
                    
                    simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
                    
                    if ( period >= 9 )
                    {
                        startD = "" + (selYear + 1) + "-" + financialMonthOrder[period] + "-01";
                        endD = "" + (selYear + 1) + "-" + financialMonthOrder[period] + "-" + financialMonthDays[period];
                        
                        if ( (((selYear + 1) % 400 == 0) || (((selYear + 1) % 100 != 0 && (selYear + 1) % 4 == 0))) && period == 10 )
                        {
                            endD = "" + (selYear + 1) + "-" + financialMonthOrder[period] + "-" + ( financialMonthDays[period] + 1 );
                        }
                    }
                    else
                    {
                        startD = "" + selYear + "-" + financialMonthOrder[period] + "-01";
                        endD = "" + selYear + "-" + financialMonthOrder[period] + "-" + financialMonthDays[period];
                    }
                    
                    Date sDate = format.parseDate( startD );
                    Date eDate = format.parseDate( endD );
                    
                    List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate, eDate ) );
                    List<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );
                    
                    selStartPeriodList.add( format.parseDate( startD ) );
                    selEndPeriodList.add( format.parseDate( endD ) );
                    
                    periodMap.put( pCount, periodIds );
                    pCount++;
                    
                    periodNames.add( simpleDateFormat.format( format.parseDate( startD ) ) );
                }
            }

        }
        
        else
        {
            Integer pCount = 0;
            for ( String year : yearLB )
            {
                int selYear = Integer.parseInt( year );
        
                if ( periodTypeLB.equalsIgnoreCase( YearlyPeriodType.NAME ) )
                {
                    startD = "" + selYear + "-01-01";
                    endD = "" + selYear  + "-12-31";
                    
                    Date sDate = format.parseDate( startD );
                    Date eDate = format.parseDate( endD );
                    selStartPeriodList.add( sDate );
                    selEndPeriodList.add( eDate );

                    List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate, eDate ) );
                    List<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );
                    periodMap.put( pCount, periodIds );
                    
                    pCount++;

                    periodNames.add( "" + selYear );
        
                    continue;
                }
        
                for ( String periodStr : periodLB )
                {
                    if ( periodTypeLB.equalsIgnoreCase( MonthlyPeriodType.NAME ) )
                    {
                        int period = Integer.parseInt( periodStr );
                        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
                        
                        startD = "" + selYear + "-" + monthOrder[period] + "-01";
                        endD = "" + selYear  + "-" + monthOrder[period] + "-" + monthDays[period];
                    
                        //check for leapYear
                        if ( ((( selYear ) % 400 == 0 ) || ((( selYear) % 100 != 0 && ( selYear ) % 4 == 0))) && period == 1 )
                        {
                            endD = "" + selYear  + "-" + monthOrder[period] + "-" + ( monthDays[period] + 1 );
                        } 

                        Date sDate = format.parseDate( startD );
                        Date eDate = format.parseDate( endD );
                        selStartPeriodList.add( sDate );
                        selEndPeriodList.add( eDate );

                        List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate, eDate ) );
                        List<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );
                        periodMap.put( pCount, periodIds );
                        pCount++;
                        
                        periodNames.add( simpleDateFormat.format( format.parseDate( startD ) ) );
                    }
                    else if ( periodTypeLB.equalsIgnoreCase( QuarterlyPeriodType.NAME ) )
                    {
                        int period = Integer.parseInt( periodStr );
                        if ( period == 0 )
                        {
                            startD = "" + selYear + "-01-01";
                            endD = "" + selYear + "-03-31";
                            periodNames.add( selYear + "-Q1" );
                        }
                        else if ( period == 1 )
                        {
                            startD = "" + selYear + "-04-01";
                            endD = "" + selYear + "-06-30";
                            periodNames.add( selYear + "-Q2" );
                        }
                        else if ( period == 2 )
                        {
                            startD = "" + selYear + "-07-01";
                            endD = "" + selYear + "-09-30";
                            periodNames.add( selYear + "-Q3" );
                        }
                        else
                        {
                            startD = "" + selYear + "-10-01";
                            endD = "" + selYear + "-12-31";
                            periodNames.add( (selYear) + "-Q4" );
                        }
                        
                        Date sDate = format.parseDate( startD );
                        Date eDate = format.parseDate( endD );
                        selStartPeriodList.add( sDate );
                        selEndPeriodList.add( eDate );

                        List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate, eDate ) );
                        List<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );
                        periodMap.put( pCount, periodIds );
                        pCount++;

                    }
                    else if ( periodTypeLB.equalsIgnoreCase( SixMonthlyPeriodType.NAME ) )
                    {
                        int period = Integer.parseInt( periodStr );
                        if ( period == 0 )
                        {
                            startD = "" + selYear + "-01-01";
                            endD = "" + selYear + "-06-30";
                            periodNames.add( selYear + "-HY1" );
                        }
                        else
                        {
                            startD = "" + selYear + "-07-01";
                            endD = "" + selYear + "-12-31";
                            periodNames.add( selYear + "-HY2" );
                        }
                       
                        Date sDate = format.parseDate( startD );
                        Date eDate = format.parseDate( endD );
                        selStartPeriodList.add( sDate );
                        selEndPeriodList.add( eDate );

                        List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate, eDate ) );
                        List<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );
                        periodMap.put( pCount, periodIds );
                        pCount++;
                    }
                    else if ( periodTypeLB.equalsIgnoreCase( DailyPeriodType.NAME ) )
                    {
                       String  month = periodStr.split( "-" )[0] ;
                       String  date = periodStr.split( "-" )[1] ;
                      
                       startD = selYear + "-" + periodStr;
                       endD = selYear + "-" + periodStr ;
                       
                       if( selYear  % 4 != 0 && month.trim().equalsIgnoreCase( "02" )  && date.trim().equalsIgnoreCase( "29" ) )
                       {
                           continue;
                       }

                       startD = selYear + "-" + month + "-" + date;
                       endD = selYear  + "-" + month + "-" + date;
                       
                       Date sDate = format.parseDate( startD );
                       Date eDate = format.parseDate( endD );
                       selStartPeriodList.add( sDate );
                       selEndPeriodList.add( eDate );

                       List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate, eDate ) );
                       List<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );
                       periodMap.put( pCount, periodIds );
                       pCount++;

                       System.out.println( startD + " *** " + endD );
                       periodNames.add( startD );
                    }
                }
            }
        }

        initialize();
        
        // calling diffrent functions       
        if ( orgUnitSelListCB.equalsIgnoreCase( ORGUNITSELECTED ) )
        {
            if( aggData.equalsIgnoreCase( USECAPTUREDDATA ) && aggPeriodCB == null )
            {
                System.out.println("Inside generateSelectedOrgUnitData_UseCapturedData_Periodwise method");
                generateSelectedOrgUnitData_UseCapturedData_Periodwise();
            }
            else if( aggData.equalsIgnoreCase( USECAPTUREDDATA ) && aggPeriodCB != null )
            {
                System.out.println("Inside generateSelectedOrgUnitData_UseCapturedData_AggPeriods method");
                generateSelectedOrgUnitData_UseCapturedData_AggPeriods();
            }
            else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) && aggPeriodCB != null )
            {
                System.out.println("Inside generateSelectedOrgUnitData_GenerateAggregateData_AggPeriods method");
                generateSelectedOrgUnitData_GenerateAggregateData_AggPeriods();
            }
            else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) && aggPeriodCB == null )
            {
                System.out.println("Inside generateSelectedOrgUnitData_GenerateAggregateData_Periodwise method");
                generateSelectedOrgUnitData_GenerateAggregateData_Periodwise();
            }
            else if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) && aggPeriodCB == null )
            {
                System.out.println("Inside generateSelectedOrgUnitData_UseExisting_Periodwise method");
                generateSelectedOrgUnitData_UseExisting_Periodwise();
            }
            else if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) && aggPeriodCB != null )
            {
                System.out.println("Inside generateSelectedOrgUnitData_UseExisting_AggPeriods method");
                generateSelectedOrgUnitData_UseExisting_AggPeriods();
            }
            else
            {
                generateOrgUnitSelected();
            }
        }
        else if ( orgUnitSelListCB.equalsIgnoreCase( ORGUNITGRP ) )
        {
            if ( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) && aggPeriodCB == null )
            {
                System.out.println("Inside generateOrgUnitGroupData_UseExisting_Periodwise method");
                generateOrgUnitGroupData_UseExisting_Periodwise();
            }
            else if ( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) && aggPeriodCB != null )
            {
                System.out.println("Inside generateOrgUnitGroupData_UseExisting_AggPeriods method");
                generateOrgUnitGroupData_UseExisting_AggPeriods();
            }
            else if ( aggData.equalsIgnoreCase( GENERATEAGGDATA ) && aggPeriodCB == null )
            {
                System.out.println("Inside generateOrgUnitGroupData_GenerateAggregateData_Periodwise method");
                generateOrgUnitGroupData_GenerateAggregateData_Periodwise();
            }
            else if ( aggData.equalsIgnoreCase( GENERATEAGGDATA ) && aggPeriodCB != null )
            {
                System.out.println("Inside generateOrgUnitGroupData_GenerateAggregateData_AggPeriods method");
                generateOrgUnitGroupData_GenerateAggregateData_AggPeriods();
            }
            else if ( aggData.equalsIgnoreCase( USECAPTUREDDATA ) && aggPeriodCB == null )
            {
                System.out.println("Inside generateOrgUnitGroupData_UseCapturedData_Periodwise method");
                generateOrgUnitGroupData_UseCapturedData_Periodwise();
            }
            else if ( aggData.equalsIgnoreCase( USECAPTUREDDATA ) && aggPeriodCB != null )
            {
                System.out.println("Inside generateOrgUnitGroupData_UseCapturedData_AggPeriods method");
                generateOrgUnitGroupData_UseCapturedData_AggPeriods();
            }
            else
            {
                System.out.println( ORGUNITGRP + " Report Generation Start Time is : \t" + new Date() );
                generateOrgUnitGroup();
            }
        }
        else if ( orgUnitSelListCB.equalsIgnoreCase( ORGUNITLEVEL ) )
        {
            if ( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) && aggPeriodCB == null )
            {
                System.out.println("Inside generateOrgUnitLevelData_UseExisting_Periodwise method");
                generateOrgUnitLevelData_UseExisting_Periodwise();
            }
            else if ( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) && aggPeriodCB != null )
            {
                System.out.println("Inside generateOrgUnitLevelData_UseExisting_AggPeriods method");
                generateOrgUnitLevelData_UseExisting_AggPeriods();
            }
            else if ( aggData.equalsIgnoreCase( GENERATEAGGDATA ) && aggPeriodCB == null )
            {
                System.out.println("Inside generateOrgUnitLevelData_GenerateAggregateData_Periodwise method");
                generateOrgUnitLevelData_GenerateAggregateData_Periodwise();
            }
            else if ( aggData.equalsIgnoreCase( GENERATEAGGDATA ) && aggPeriodCB != null )
            {
                System.out.println("Inside generateOrgUnitLevelData_GenerateAggregateData_AggPeriods method");
                generateOrgUnitLevelData_GenerateAggregateData_AggPeriods();
            }
            else if ( aggData.equalsIgnoreCase( USECAPTUREDDATA ) && aggPeriodCB == null )
            {
                System.out.println("Inside generateOrgUnitLevelData_UseCapturedData_Periodwise method");
                generateOrgUnitLevelData_UseCapturedData_Periodwise();
            }
            else if ( aggData.equalsIgnoreCase( USECAPTUREDDATA ) && aggPeriodCB != null )
            {
                System.out.println("Inside generateOrgUnitLevelData_UseCapturedData_AggPeriods method");
                generateOrgUnitLevelData_UseCapturedData_AggPeriods();
            }
            else
            {
                generateOrgUnitLevel();
            }
        }
    
        statementManager.destroy();
    
        System.out.println( "Report Generation End Time is : \t" + new Date() );
    
        return SUCCESS;
    }
    
    
    public void initialize()
    {
        dataElementIdsByComma = "-1";
        
        List<Period> periods = new ArrayList<Period>();
        int periodCount = 0;
        for ( Date sDate : selStartPeriodList )
        {
            Date eDate = selEndPeriodList.get( periodCount );
            List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate, eDate ) );
            
            if( periodList != null && periodList.size() > 0 )
                periods.addAll( periodList );
            periodCount++;
        }
        Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periods ) );
        periodIdsByComma = getCommaDelimitedString( periodIds );
        
        System.out.println("PeriodIds: "+ periodIdsByComma );
        
        for ( String service : selectedServices )
        {
            String partsOfService[] = service.split( ":" );
            if ( partsOfService[0].equalsIgnoreCase( "D" ) )
            {
                dataElementIdsByComma += ","+partsOfService[1];
                DataElement de = dataElementService.getDataElement( Integer.parseInt( partsOfService[1] ) );
                dataElementList.add( de );
                serviceTypeList.add( "D" );
            }
            else
            {
                Indicator indicator = indicatorService.getIndicator( Integer.parseInt( partsOfService[1] ) );
                indicatorList.add( indicator );
                serviceTypeList.add( "I" );
            }
        }
        
        String indicaotrDes = reportService.getDataelementIdsAsString( indicatorList );
        dataElementIdsByComma += "," + indicaotrDes;
    }

    // -------------------------------------------------------------------------
    // Method for getting OrgUnit Level wise data in Excel Sheet 
    //     - UseCapturedData - Period Aggregation 
    // -------------------------------------------------------------------------
    public void generateOrgUnitLevelData_UseCapturedData_AggPeriods() throws Exception
    {
        int headerRow = 0;
        int headerCol = 0;

        //String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();
        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";
        
        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "TabularAnalysis", 0 );

        sheet0.addCell( new Label( headerCol, headerRow, "Sl.No.", getCellFormat1() ) );

        selOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
        selOUList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selOrgUnit.getId() ) );

        Map<Integer, Integer> orgunitLevelMap = new HashMap<Integer, Integer>( reportService.getOrgunitLevelMap() );
    
        Iterator<OrganisationUnit> ouIterator = selOUList.iterator();
        while ( ouIterator.hasNext() )
        {
            OrganisationUnit orgU = ouIterator.next();
            
            Integer level = orgunitLevelMap.get( orgU.getId() );
            if( level == null )
                level = organisationUnitService.getLevelOfOrganisationUnit( orgU.getId() );
            if ( level > orgUnitLevelCB )
            {
                ouIterator.remove();
            }
        }

        int minOULevel = 1;
        int maxOuLevel = 1;
        if ( selOUList != null && selOUList.size() > 0 )
        {
            minOULevel = organisationUnitService.getLevelOfOrganisationUnit( selOUList.get( 0 ).getId() );
        }
        maxOuLevel = orgUnitLevelCB;

        int c1 = headerCol + 1;
        for ( int i = minOULevel; i <= maxOuLevel; i++ )
        {
            sheet0.addCell( new Label( c1, headerRow, "Level " + i, getCellFormat1() ) );
            c1++;
        }

        /* Service Info */
        Indicator selIndicator = new Indicator();
        DataElement selDataElement = new DataElement();
        DataElementCategoryOptionCombo selDecoc = new DataElementCategoryOptionCombo();
        int flag = 0;

        List<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers(OrganisationUnit.class, selOUList ) );
        orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
        
        Map<String, String> aggDataMap = new HashMap<String, String>( reportService.getDataFromDataValueTableByPeriodAgg( orgUnitIdsByComma, dataElementIdsByComma, periodIdsByComma ) );
    
        /* Calculation Part */
        int rowCount = 1;
        int colCount = 0;
        for( OrganisationUnit ou : selOUList )
        {
            System.out.println("Entered into orgunitloop :"+new Date());
            sheet0.addCell( new Number( headerCol, headerRow + rowCount, rowCount, getCellFormat2() ) );
            
            Integer level = orgunitLevelMap.get( ou.getId() );
            if( level == null )
                level = organisationUnitService.getLevelOfOrganisationUnit( ou.getId() );
            
            colCount = 1 + level - minOULevel;
            sheet0.addCell( new Label( colCount, headerRow + rowCount, ou.getName(), getCellFormat2() ) );

            colCount = c1;
            int deListCount = 0;
            int indListCount = 0;
            int serviceListCount = 0;
            for( String serviceType : serviceTypeList )
            {
                String tempStr = "";
                Double indValue = 0.0;
                Double dataValue = 0.0;
                
                if ( serviceType.equalsIgnoreCase( "I" ) )
                {
                    Double numValue = 0.0;
                    Double denValue = 0.0;

                    flag = 1;
                    selIndicator = indicatorList.get( indListCount );
                    indListCount++;
                    if ( rowCount == 1 )
                    {
                        sheet0.addCell( new Label( colCount, headerRow, selIndicator.getName(), getCellFormat1() ) );
                    }
                    
                    try
                    {
                        numValue = Double.parseDouble( getAggValByOrgUnit( selIndicator.getNumerator(), aggDataMap, ou.getId() ) );
                    }
                    catch( Exception e )
                    {
                    }
                    
                    try
                    {
                        denValue = Double.parseDouble( getAggValByOrgUnit( selIndicator.getDenominator(), aggDataMap, ou.getId() ) );
                    }
                    catch( Exception e )
                    {
                    }
                    
                    try
                    {
                        if( denValue != 0.0 )
                        {
                            indValue = ( numValue / denValue ) * selIndicator.getIndicatorType().getFactor();
                        }
                        else
                        {
                            indValue = 0.0;
                        }
                    }
                    catch( Exception e )
                    {
                        indValue = 0.0;
                    }
                    
                    indValue = Math.round( indValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                }
                else
                {
                    flag = 2;
                    selDataElement = dataElementList.get( deListCount );
                    deListCount++;
                    if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                    {
                        selDecoc = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer.parseInt( selectedServices.get( serviceListCount ).split( ":" )[2] ) );
                        if ( rowCount == 1 )
                        {
                            sheet0.addCell( new Label( colCount, headerRow, selDataElement.getName() + "-" + selDecoc.getName(), getCellFormat1() ) );
                        }
                        
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            tempStr = aggDataMap.get( ou.getId()+":"+selDataElement.getId()+":"+selDecoc.getId() );
                            if( tempStr != null )
                            {
                                try
                                {
                                    dataValue = Double.parseDouble( tempStr );
                                }
                                catch( Exception e )
                                {
                                    dataValue = 0.0;
                                }
                            }
                        }
                        else
                        {
                            dataValue = 0.0;
                        }
                    }
                    else
                    {
                        if ( rowCount == 1 )
                        {
                            sheet0.addCell( new Label( colCount, headerRow, selDataElement.getName(), getCellFormat1() ) );
                        }
                        List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>( selDataElement.getCategoryCombo().getOptionCombos() );
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            for( DataElementCategoryOptionCombo optionCombo : optionCombos )
                            {
                                tempStr = aggDataMap.get( ou.getId()+":"+selDataElement.getId()+":"+optionCombo.getId() );
                                if( tempStr != null )
                                {
                                    try
                                    {
                                        dataValue += Double.parseDouble( tempStr );
                                    }
                                    catch( Exception e )
                                    {
                                    }
                                }
                            }
                        }
                        else
                        {
                            dataValue = 0.0;
                        }
                    }
                }

                if ( flag == 1 )
                {
                    sheet0.addCell( new Number( colCount, headerRow + rowCount, indValue, getCellFormat2() ) );
                }
                else
                {
                    sheet0.addCell( new Number( colCount, headerRow + rowCount, dataValue, getCellFormat2() ) );
                }

                colCount++;
                serviceListCount++;
            }// Service loop end
            rowCount++;
        }// Orgunit loop end
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "TabularAnalysis.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
    
        outputReportFile.deleteOnExit();
    }

    // -------------------------------------------------------------------------
    // Method for getting OrgUnit Group wise data in Excel Sheet 
    //     - UseCapturedData - Period Aggregation 
    // -------------------------------------------------------------------------
    public void generateOrgUnitGroupData_UseCapturedData_AggPeriods() throws Exception
    {
        int headerRow = 0;
        int headerCol = 0;

        //String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();
        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "TabularAnalysis", 0 );

        sheet0.addCell( new Label( headerCol, headerRow, "Sl.No.", getCellFormat1() ) );

        selOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
        selOUList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selOrgUnit.getId() ) );

        OrganisationUnitGroup selOrgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgUnitLevelCB );
        List<OrganisationUnit> orgUnitList1 = new ArrayList<OrganisationUnit>( selOrgUnitGroup.getMembers() );

        selOUList.retainAll( orgUnitList1 );
        
        int minOULevel = 1;
        int maxOULevel = organisationUnitService.getNumberOfOrganisationalLevels();

        int c1 = headerCol + 1;

        for( int i = minOULevel; i <= maxOULevel; i++ )
        {
            sheet0.addCell( new Label( c1, headerRow, "Level- "+i, getCellFormat1() ) );
            c1++;
        }

        Map<Integer, Integer> orgunitLevelMap = new HashMap<Integer, Integer>( reportService.getOrgunitLevelMap() );

        /* Service Info */
        Indicator selIndicator = new Indicator();
        DataElement selDataElement = new DataElement();
        DataElementCategoryOptionCombo selDecoc = new DataElementCategoryOptionCombo();
        int flag = 0;

        List<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers(OrganisationUnit.class, selOUList ) );
        orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
        
        Map<String, String> aggDataMap = new HashMap<String, String>( reportService.getDataFromDataValueTableByPeriodAgg( orgUnitIdsByComma, dataElementIdsByComma, periodIdsByComma ) );
    
        /* Calculation Part */
        int rowCount = 1;
        int colCount = 0;
        for( OrganisationUnit ou : selOUList )
        {
            System.out.println("Entered into orgunitloop :"+new Date());
            sheet0.addCell( new Number( headerCol, headerRow + rowCount, rowCount, getCellFormat2() ) );
            
            Integer level = orgunitLevelMap.get( ou.getId() );
            if( level == null )
                level = organisationUnitService.getLevelOfOrganisationUnit( ou.getId() );
            
            colCount = 1 + level - minOULevel;
            sheet0.addCell( new Label( colCount, headerRow + rowCount, ou.getName(), getCellFormat2() ) );

            OrganisationUnit orgUnit = new OrganisationUnit();
            orgUnit = ou;
            int count1=1;
            while( orgUnit.getParent() != null )
            {
                orgUnit = orgUnit.getParent();
                sheet0.addCell( new Label( colCount-count1, headerRow + rowCount, orgUnit.getName(), getCellFormat2() ) );
                count1++;
            }

            colCount = c1;
            int deListCount = 0;
            int indListCount = 0;
            int serviceListCount = 0;
            for( String serviceType : serviceTypeList )
            {
                String tempStr = "";
                Double indValue = 0.0;
                Double dataValue = 0.0;
                
                if ( serviceType.equalsIgnoreCase( "I" ) )
                {
                    Double numValue = 0.0;
                    Double denValue = 0.0;

                    flag = 1;
                    selIndicator = indicatorList.get( indListCount );
                    indListCount++;
                    if ( rowCount == 1 )
                    {
                        sheet0.addCell( new Label( colCount, headerRow, selIndicator.getName(), getCellFormat1() ) );
                    }
                    
                    try
                    {
                        numValue = Double.parseDouble( getAggValByOrgUnit( selIndicator.getNumerator(), aggDataMap, ou.getId() ) );
                    }
                    catch( Exception e )
                    {
                    }
                    
                    try
                    {
                        denValue = Double.parseDouble( getAggValByOrgUnit( selIndicator.getDenominator(), aggDataMap, ou.getId() ) );
                    }
                    catch( Exception e )
                    {
                    }
                    
                    try
                    {
                        if( denValue != 0.0 )
                        {
                            indValue = ( numValue / denValue ) * selIndicator.getIndicatorType().getFactor();
                        }
                        else
                        {
                            indValue = 0.0;
                        }
                    }
                    catch( Exception e )
                    {
                        indValue = 0.0;
                    }
                    
                    indValue = Math.round( indValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                }
                else
                {
                    flag = 2;
                    selDataElement = dataElementList.get( deListCount );
                    deListCount++;
                    if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                    {
                        selDecoc = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer.parseInt( selectedServices.get( serviceListCount ).split( ":" )[2] ) );
                        if ( rowCount == 1 )
                        {
                            sheet0.addCell( new Label( colCount, headerRow, selDataElement.getName() + "-" + selDecoc.getName(), getCellFormat1() ) );
                        }
                        
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            tempStr = aggDataMap.get( ou.getId()+":"+selDataElement.getId()+":"+selDecoc.getId() );
                            if( tempStr != null )
                            {
                                try
                                {
                                    dataValue = Double.parseDouble( tempStr );
                                }
                                catch( Exception e )
                                {
                                    dataValue = 0.0;
                                }
                            }
                        }
                        else
                        {
                            dataValue = 0.0;
                        }
                    }
                    else
                    {
                        if ( rowCount == 1 )
                        {
                            sheet0.addCell( new Label( colCount, headerRow, selDataElement.getName(), getCellFormat1() ) );
                        }
                        List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>( selDataElement.getCategoryCombo().getOptionCombos() );
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            for( DataElementCategoryOptionCombo optionCombo : optionCombos )
                            {
                                tempStr = aggDataMap.get( ou.getId()+":"+selDataElement.getId()+":"+optionCombo.getId() );
                                if( tempStr != null )
                                {
                                    try
                                    {
                                        dataValue += Double.parseDouble( tempStr );
                                    }
                                    catch( Exception e )
                                    {
                                    }
                                }
                            }
                        }
                        else
                        {
                            dataValue = 0.0;
                        }
                    }
                }

                if ( flag == 1 )
                {
                    sheet0.addCell( new Number( colCount, headerRow + rowCount, indValue, getCellFormat2() ) );
                }
                else
                {
                    sheet0.addCell( new Number( colCount, headerRow + rowCount, dataValue, getCellFormat2() ) );
                }

                colCount++;
                serviceListCount++;
            }// Service loop end
            rowCount++;
        }// Orgunit loop end
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "TabularAnalysis.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
    
        outputReportFile.deleteOnExit();
    }

    // -------------------------------------------------------------------------
    // Method for getting Selected OrgUnit(s) data in Excel Sheet 
    //     - UseCapturedData - Period wise 
    // -------------------------------------------------------------------------
    public void generateSelectedOrgUnitData_UseCapturedData_Periodwise() throws Exception
    {
        int startRow = 0;
        int headerRow = 0;
        int headerCol = 0;

        //String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();
        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "TabularAnalysis", 0 );

        sheet0.mergeCells( headerCol, headerRow, headerCol, headerRow + 1 );
        sheet0.addCell( new Label( headerCol, headerRow, "Sl.No.", getCellFormat1() ) );

        for ( String ouStr : orgUnitListCB )
        {
            OrganisationUnit ou = organisationUnitService.getOrganisationUnit( Integer.parseInt( ouStr ) );
            selOUList.add( ou );
        }

        int c1 = headerCol + 1;
        sheet0.mergeCells( c1, headerRow, c1, headerRow + 1 );
        sheet0.addCell( new Label( c1, headerRow, "Facility", getCellFormat1() ) );
        c1++;
        
        /* Service Info */
        Indicator selIndicator = new Indicator();
        DataElement selDataElement = new DataElement();
        DataElementCategoryOptionCombo selDecoc = new DataElementCategoryOptionCombo();
        int flag = 0;

        List<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers(OrganisationUnit.class, selOUList ) );
        orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
        
        Map<String, String> aggDataMap = new HashMap<String, String>( reportService.getDataFromDataValueTable( orgUnitIdsByComma, dataElementIdsByComma, periodIdsByComma ) );
    
        /* Calculation Part */
        int rowCount = 1;
        int colCount = 0;
        for( OrganisationUnit ou : selOUList )
        {
            System.out.println(ou.getName() + " : " +new Date());
            sheet0.addCell( new Number( headerCol, headerRow + 1 + rowCount, rowCount, getCellFormat2() ) );
            sheet0.addCell( new Label( 1, rowCount + 1, ou.getName(), getCellFormat2() ) );

            colCount = c1;
            int deListCount = 0;
            int indListCount = 0;
            int serviceListCount = 0;
            for( String serviceType : serviceTypeList )
            {
                if ( serviceType.equalsIgnoreCase( "I" ) )
                {
                    flag = 1;
                    selIndicator = indicatorList.get( indListCount );
                    indListCount++;
                    if ( rowCount == 1 )
                    {
                        sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                        sheet0.addCell( new Label( colCount, startRow, selIndicator.getName(), getCellFormat1() ) );
                    }
                }
                else
                {
                    flag = 2;
                    selDataElement = dataElementList.get( deListCount );
                    deListCount++;
                    if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                    {
                        selDecoc = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer.parseInt( selectedServices.get( serviceListCount ).split( ":" )[2] ) );
                        if ( rowCount == 1 )
                        {
                            sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                            sheet0.addCell( new Label( colCount, startRow, selDataElement.getName() + "-" + selDecoc.getName(), getCellFormat1() ) );
                        }
                    }
                    else
                    {
                        if ( rowCount == 1 )
                        {
                            sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                            sheet0.addCell( new Label( colCount, startRow, selDataElement.getName(), getCellFormat1() ) );
                        }
                    }
                }

                int periodCount = 0;
                for ( Date sDate : selStartPeriodList )
                {
                    Date eDate = selEndPeriodList.get( periodCount );
                
                    Collection<Integer> periodIds = new ArrayList<Integer>( periodMap.get( periodCount ) );

                    double pwdvAggValue = 0.0;
                    double pwdAggIndValue = 0.0;

                    String tempStr = "";
                    if ( flag == 1 )
                    {
                        Double numValue = 0.0;
                        Double denValue = 0.0;
                        Double indValue = 0.0;
                        for( Integer periodId : periodIds )
                        {
                            try
                            {
                                numValue += Double.parseDouble( getAggVal( selIndicator.getNumerator(), aggDataMap, ou.getId(), periodId ) );
                            }
                            catch( Exception e )
                            {
                                numValue = 0.0;
                            }
                            Double tempDenValue = 0.0;
                            try
                            {
                                //denValue += Double.parseDouble( getAggVal( selIndicator.getDenominator(), aggDataMap, ou.getId(), periodId ) );
                                tempDenValue = Double.parseDouble( getAggVal( selIndicator.getDenominator(), aggDataMap, ou.getId(), periodId ) );
                            }
                            catch( Exception e )
                            {
                                tempDenValue = 1.0;
                            }
                            if( !selIndicator.getDenominator().trim().equals( "1" ) )
                            {
                                denValue += tempDenValue;
                            }
                            else 
                            {
                                denValue = 1.0;
                            }
                        }
                        
                        try
                        {
                            if( denValue != 0.0 )
                            {
                                indValue = ( numValue / denValue ) * selIndicator.getIndicatorType().getFactor();
                            }
                            else
                            {
                                indValue = 0.0;
                            }
                        }
                        catch( Exception e )
                        {
                            indValue = 0.0;
                        }

                        pwdAggIndValue = indValue;
                        pwdAggIndValue = Math.round( pwdAggIndValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        tempStr = "" + pwdAggIndValue;
                    }
                    else if ( flag == 2 )
                    {
                        if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                        {
                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                for( Integer periodId : periodIds )
                                {
                                    tempStr = aggDataMap.get( ou.getId()+":"+selDataElement.getId()+":"+selDecoc.getId()+":"+periodId );
                                    if( tempStr != null )
                                    {
                                        try
                                        {
                                            pwdvAggValue += Double.parseDouble( tempStr );
                                        }
                                        catch( Exception e )
                                        {
                                        }
                                    }
                                }
                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                if ( tempPeriod != null )
                                {
                                    DataValue dataValue = dataValueService.getDataValue( ou, selDataElement, tempPeriod, selDecoc );

                                    if ( dataValue != null && dataValue.getValue() != null )
                                    {
                                        tempStr = dataValue.getValue();
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                                else
                                {
                                    tempStr = " ";
                                }
                            }
                        }
                        else
                        {
                            List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>( selDataElement.getCategoryCombo().getOptionCombos() );

                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                for( DataElementCategoryOptionCombo optionCombo : optionCombos )
                                {
                                    for( Integer periodId : periodIds )
                                    {
                                        tempStr = aggDataMap.get( ou.getId()+":"+selDataElement.getId()+":"+optionCombo.getId()+":"+periodId );
                                        if( tempStr != null )
                                        {
                                            try
                                            {
                                                pwdvAggValue += Double.parseDouble( tempStr );
                                            }
                                            catch( Exception e )
                                            {
                                            }
                                        }
                                    }
                                }
                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                                while ( optionComboIterator.hasNext() )
                                {
                                    DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator.next();

                                    PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                    Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                    if( tempPeriod != null )
                                    {
                                        DataValue dataValue = dataValueService.getDataValue( ou, selDataElement, tempPeriod, decoc1 );

                                        if ( dataValue != null )
                                        {
                                            tempStr += dataValue.getValue() + " : ";
                                        }
                                        else
                                        {
                                            tempStr = "  ";
                                        }
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                            }
                        }
                    }

                    if ( rowCount == 1 )
                    {
                        sheet0.addCell( new Label( colCount, startRow + 1, periodNames.get( periodCount ), getCellFormat1() ) );
                    }

                    if ( flag == 1 )
                    {
                        sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, pwdAggIndValue, getCellFormat2() ) );
                    }
                    else
                    {
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, (int) pwdvAggValue, getCellFormat2() ) );
                        }
                        else
                        {
                            sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, tempStr, getCellFormat2() ) );
                        }
                    }

                    colCount++;
                    periodCount++;
                }// Period Loop
                serviceListCount++;
            }// Service loop end
            rowCount++;
        }// Orgunit loop end
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "TabularAnalysis.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
    
        outputReportFile.deleteOnExit();
    }

    // -------------------------------------------------------------------------
    // Methods for getting OrgUnitSelected wise List in Excel Sheet
    // -------------------------------------------------------------------------
    public void generateOrgUnitSelected()
        throws Exception
    {

        int startRow = 0;
        int headerRow = 0;
        int headerCol = 0;


        //String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER )
        //    .getValue();
        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator
        //    + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "TabularAnalysis", 0 );
        
        sheet0.mergeCells( headerCol, headerRow, headerCol, headerRow + 1 );
        sheet0.addCell( new Label( headerCol, headerRow, "Sl.No.", getCellFormat1() ) );

        for ( String ouStr : orgUnitListCB )
        {
            OrganisationUnit ou = organisationUnitService.getOrganisationUnit( Integer.parseInt( ouStr ) );
            selOUList.add( ou );
        }

        int c1 = headerCol + 1;
        sheet0.mergeCells( c1, headerRow, c1, headerRow + 1 );
        sheet0.addCell( new Label( c1, headerRow, "Facility", getCellFormat1() ) );
        c1++;

        int rowCount = 1;
        int colCount = 0;

        for ( OrganisationUnit ou : selOUList )
        {
            sheet0.addCell( new Number( headerCol, headerRow + 1 + rowCount, rowCount, getCellFormat2() ) );

            sheet0.addCell( new Label( 1, rowCount + 1, ou.getName(), getCellFormat2() ) );

            /* Service Info */
            Indicator selIndicator = new Indicator();
            DataElement selDataElement = new DataElement();
            DataElementCategoryOptionCombo selDecoc = new DataElementCategoryOptionCombo();
            int flag = 0;
            colCount = c1;
            for ( String service : selectedServices )
            {
                String partsOfService[] = service.split( ":" );
                if ( partsOfService[0].equalsIgnoreCase( "I" ) )
                {
                    flag = 1;
                    selIndicator = indicatorService.getIndicator( Integer.parseInt( partsOfService[1] ) );
                    if ( rowCount == 1 )
                    {
                        if ( aggPeriodCB == null )
                        {
                            sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                        }
                        else
                        {
                            sheet0.mergeCells( colCount, startRow, colCount, startRow + 1 );
                        }

                        sheet0.addCell( new Label( colCount, startRow, selIndicator.getName(), getCellFormat1() ) );
                    }
                }
                else
                {
                    flag = 2;
                    selDataElement = dataElementService.getDataElement( Integer.parseInt( partsOfService[1] ) );

                    if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                    {
                        selDecoc = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer
                            .parseInt( partsOfService[2] ) );
                        if ( rowCount == 1 )
                        {
                            if ( aggPeriodCB == null )
                            {
                                sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1,
                                    startRow );
                            }
                            else
                            {
                                sheet0.mergeCells( colCount, startRow, colCount, startRow + 1 );
                            }

                            sheet0.addCell( new Label( colCount, startRow, selDataElement.getName() + "-"
                                + selDecoc.getName(), getCellFormat1() ) );
                        }
                    }
                    else
                    {
                        if ( rowCount == 1 )
                        {
                            if ( aggPeriodCB == null )
                            {
                                sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1,
                                    startRow );
                            }
                            else
                            {
                                sheet0.mergeCells( colCount, startRow, colCount, startRow + 1 );
                            }

                            sheet0
                                .addCell( new Label( colCount, startRow, selDataElement.getName(), getCellFormat1() ) );
                        }
                    }
                }

                int periodCount = 0;
                double numAggValue = 0.0;
                double denAggValue = 0.0;
                double dvAggValue = 0.0;
                String aggStrValue = "";

                for ( Date sDate : selStartPeriodList )
                {
                    Date eDate = selEndPeriodList.get( periodCount );
                    
                    List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate, eDate ) );
                    Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );
                    String periodIdsByComma = getCommaDelimitedString( periodIds );

                    double pwnumAggValue = 0.0;
                    double pwdenAggValue = 0.0;
                    double pwdvAggValue = 0.0;
                    double pwdAggIndValue = 0.0;

                    String tempStr = "";

                    Double tempAggVal;
                    if ( flag == 1 )
                    {
                        tempAggVal = aggregationService.getAggregatedNumeratorValue( selIndicator, sDate, eDate, ou );
                        if ( tempAggVal == null )
                            tempAggVal = 0.0;
                        pwnumAggValue = tempAggVal;
                        tempAggVal = aggregationService.getAggregatedDenominatorValue( selIndicator, sDate, eDate, ou );
                        if ( tempAggVal == null )
                            tempAggVal = 0.0;
                        pwdenAggValue = tempAggVal;

                        tempAggVal = aggregationService.getAggregatedIndicatorValue( selIndicator, sDate, eDate, ou );
                        if ( tempAggVal == null )
                            tempAggVal = 0.0;
                        pwdAggIndValue = tempAggVal;

                        pwdAggIndValue = Math.round( pwdAggIndValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        tempStr = "" + pwdAggIndValue;

                    }
                    else if ( flag == 2 )
                    {
                        if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                        {
                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                tempAggVal = null;
                                if ( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
                                {
                                        
                                }
                                else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
                                {
                                    tempAggVal = aggregationService.getAggregatedDataValue( selDataElement, selDecoc, sDate, eDate, ou );
                                }
                                else if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
                                {
                                        Map<String, String> aggDeMap = new HashMap<String, String>();
                                        aggDeMap.putAll( reportService.getResultDataValueFromAggregateTable( ou.getId(), ""+selDataElement.getId(), periodIdsByComma ) );
                                        tempStr = aggDeMap.get(selDataElement.getId()+"."+selDecoc.getId());
                                        if( tempStr != null )
                                        {
                                            try
                                            {
                                                tempAggVal = Double.parseDouble( tempStr );
                                            }
                                            catch( Exception e )
                                            {
                                                tempAggVal = null;
                                            }
                                        }
                                }

                                if ( tempAggVal == null )
                                    tempAggVal = 0.0;
                                pwdvAggValue = tempAggVal;

                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                if ( tempPeriod != null )
                                {
                                    DataValue dataValue = dataValueService.getDataValue( ou, selDataElement,
                                        tempPeriod, selDecoc );

                                    if ( dataValue != null )
                                    {
                                        tempStr = dataValue.getValue();
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                                else
                                {
                                    tempStr = " ";
                                }
                            }
                        }
                        else
                        {
                            List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>(
                                selDataElement.getCategoryCombo().getOptionCombos() );

                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                if ( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
                                {
                                        
                                }
                                else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
                                {
                                    Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                                    while ( optionComboIterator.hasNext() )
                                    {
                                        DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator
                                            .next();

                                        tempAggVal = aggregationService.getAggregatedDataValue( selDataElement, decoc1,
                                            sDate, eDate, ou );
                                        if ( tempAggVal == null )
                                            tempAggVal = 0.0;
                                        pwdvAggValue += tempAggVal;
                                    }
                                }
                                else if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
                                {
                                        Map<String, String> aggDeMap = new HashMap<String, String>();
                                        aggDeMap.putAll( reportService.getResultDataValueFromAggregateTable( ou.getId(), ""+selDataElement.getId(), periodIdsByComma ) );
                                        for( String aggDe : aggDeMap.keySet() )
                                        {
                                                String temp = aggDeMap.get( aggDe );
                                                try
                                                {
                                                        tempAggVal = Double.parseDouble( temp );
                                                }
                                                catch( Exception e )
                                                {
                                                        tempAggVal = 0.0;
                                                }
                                                pwdvAggValue += tempAggVal;
                                        }

                                }

                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                                while ( optionComboIterator.hasNext() )
                                {
                                    DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator
                                        .next();

                                    PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                    Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                    if ( tempPeriod != null )
                                    {
                                        DataValue dataValue = dataValueService.getDataValue( ou, selDataElement,
                                            tempPeriod, decoc1 );

                                        if ( dataValue != null )
                                        {
                                            tempStr += dataValue.getValue() + " : ";
                                        }
                                        else
                                        {
                                            tempStr = "  ";
                                        }
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                            }
                        }
                    }

                    if ( aggPeriodCB == null )
                    {
                        if ( rowCount == 1 )
                        {
                            sheet0.addCell( new Label( colCount, startRow + 1, periodNames.get( periodCount ),
                                getCellFormat1() ) );
                        }

                        if ( flag == 1 )
                        {
                            sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, pwdAggIndValue,
                                getCellFormat2() ) );
                        }
                        else
                        {
                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, (int) pwdvAggValue,
                                    getCellFormat2() ) );
                            }
                            else
                            {
                                sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, tempStr,
                                    getCellFormat2() ) );
                            }
                        }

                        colCount++;
                    }
                    else
                    {
                        numAggValue += pwnumAggValue;
                        denAggValue += pwdenAggValue;
                        dvAggValue += pwdvAggValue;
                        aggStrValue += tempStr + " - ";
                    }

                    periodCount++;
                }// Period Loop

                if ( aggPeriodCB != null )
                {
                    if ( flag == 1 )
                    {
                        try
                        {
                            double tempDouble = Double.parseDouble( selIndicator.getDenominator().trim() );

                            // if(tempInt == 1) denAggValue = 1;
                            denAggValue = tempDouble;
                        }
                        catch ( Exception e )
                        {
                            System.out.println( "Denominator is not expression" );
                        }

                        double aggIndValue = (numAggValue / denAggValue) * selIndicator.getIndicatorType().getFactor();
                        aggIndValue = Math.round( aggIndValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        sheet0
                            .addCell( new Number( colCount, headerRow + 1 + rowCount, aggIndValue, getCellFormat2() ) );
                    }
                    else if ( flag == 2 )
                    {
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, (int) dvAggValue,
                                getCellFormat2() ) );
                        }
                        else
                        {
                            sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, aggStrValue,
                                getCellFormat2() ) );
                        }
                    }

                    colCount++;
                }
                // colCount++;
            }// Service loop end

            rowCount++;
        }// Orgunit loop end

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "TabularAnalysis.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();
    }
    
    // -------------------------------------------------------------------------
    // Methods for getting OrgUnitGroup wise List in Excel Sheet
    // -------------------------------------------------------------------------
    public void generateOrgUnitGroup()
        throws Exception
    {
        int startRow = 0;
        int headerRow = 0;
        int headerCol = 0;

        System.out.println( "inside the generateOrgUnitGroup" );

       // String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER )
       //     .getValue();
       // String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator
       //     + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "TabularAnalysis", 0 );

        sheet0.mergeCells( headerCol, headerRow, headerCol, headerRow + 1 );
        sheet0.addCell( new Label( headerCol, headerRow, "Sl.No.", getCellFormat1() ) );

        selOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
        List<OrganisationUnit> selOUList = new ArrayList<OrganisationUnit>( organisationUnitService
            .getOrganisationUnitWithChildren( selOrgUnit.getId() ) );
        OrganisationUnitGroup selOrgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgUnitLevelCB );
        List<OrganisationUnit> orgUnitList1 = new ArrayList<OrganisationUnit>( selOrgUnitGroup.getMembers() );

        selOUList.retainAll( orgUnitList1 );

        int minOULevel = 1;
        int maxOULevel = organisationUnitService.getNumberOfOrganisationalLevels();
        
        int c1 = headerCol + 1;

        for( int i = minOULevel; i <= maxOULevel; i++ )
        {
            sheet0.mergeCells( c1, headerRow, c1, headerRow + 1 );
            sheet0.addCell( new Label( c1, headerRow, "Level- "+i, getCellFormat1() ) );
            c1++;
        }

        /* Calculation Part */
        int rowCount = 1;
        int colCount = 0;
        for ( OrganisationUnit ou : selOUList )
        {
            sheet0.addCell( new Number( headerCol, headerRow + 1 + rowCount, rowCount, getCellFormat2() ) );
            colCount = 1 + organisationUnitService.getLevelOfOrganisationUnit( ou.getId() ) - minOULevel;
            sheet0.addCell( new Label( colCount, rowCount + 1, ou.getName(), getCellFormat2() ) );
            
            OrganisationUnit orgUnit = new OrganisationUnit();
            orgUnit = ou;
            int count1=1;
            while( orgUnit.getParent() != null )
            {
                orgUnit = orgUnit.getParent();
                sheet0.addCell( new Label( colCount-count1, rowCount + 1, orgUnit.getName(), getCellFormat2() ) );
                count1++;
            }

            /* Service Info */
            Indicator selIndicator = new Indicator();
            DataElement selDataElement = new DataElement();
            DataElementCategoryOptionCombo selDecoc = new DataElementCategoryOptionCombo();
            int flag = 0;
            colCount = c1;

            for ( String service : selectedServices )
            {
                String partsOfService[] = service.split( ":" );
                if ( partsOfService[0].equalsIgnoreCase( "I" ) )
                {
                    flag = 1;
                    selIndicator = indicatorService.getIndicator( Integer.parseInt( partsOfService[1] ) );
                    if ( rowCount == 1 )
                    {
                        if ( aggPeriodCB == null )
                        {
                            sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                        }
                        else
                        {
                            sheet0.mergeCells( colCount, startRow, colCount, startRow + 1 );
                        }

                        sheet0.addCell( new Label( colCount, startRow, selIndicator.getName(), getCellFormat1() ) );
                    }
                }
                else
                {
                    flag = 2;
                    selDataElement = dataElementService.getDataElement( Integer.parseInt( partsOfService[1] ) );

                    if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                    {
                        selDecoc = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer
                            .parseInt( partsOfService[2] ) );
                        if ( rowCount == 1 )
                        {
                            if ( aggPeriodCB == null )
                            {
                                sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1,
                                    startRow );
                            }
                            else
                            {
                                sheet0.mergeCells( colCount, startRow, colCount, startRow + 1 );
                            }

                            sheet0.addCell( new Label( colCount, startRow, selDataElement.getName() + "-"
                                + selDecoc.getName(), getCellFormat1() ) );
                        }
                    }
                    else
                    {
                        if ( rowCount == 1 )
                        {
                            if ( aggPeriodCB == null )
                            {
                                sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1,
                                    startRow );
                            }
                            else
                            {
                                sheet0.mergeCells( colCount, startRow, colCount, startRow + 1 );
                            }

                            sheet0
                                .addCell( new Label( colCount, startRow, selDataElement.getName(), getCellFormat1() ) );
                        }
                    }
                }

                int periodCount = 0;
                double numAggValue = 0.0;
                double denAggValue = 0.0;
                double dvAggValue = 0.0;
                String aggStrValue = "";

                for ( Date sDate : selStartPeriodList )
                {
                    Date eDate = selEndPeriodList.get( periodCount );
                    double pwnumAggValue = 0.0;
                    double pwdenAggValue = 0.0;
                    double pwdvAggValue = 0.0;
                    double pwdAggIndValue = 0.0;

                    String tempStr = "";

                    Double tempAggVal;
                    if ( flag == 1 )
                    {
                        tempAggVal = aggregationService.getAggregatedNumeratorValue( selIndicator, sDate, eDate, ou );
                        if ( tempAggVal == null )
                            tempAggVal = 0.0;
                        pwnumAggValue = tempAggVal;
                        tempAggVal = aggregationService.getAggregatedDenominatorValue( selIndicator, sDate, eDate, ou );
                        if ( tempAggVal == null )
                            tempAggVal = 0.0;
                        pwdenAggValue = tempAggVal;

                        tempAggVal = aggregationService.getAggregatedIndicatorValue( selIndicator, sDate, eDate, ou );
                        if ( tempAggVal == null )
                            tempAggVal = 0.0;
                        pwdAggIndValue = tempAggVal;

                        pwdAggIndValue = Math.round( pwdAggIndValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        tempStr = "" + pwdAggIndValue;

                    }
                    else if ( flag == 2 )
                    {
                        if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                        {
                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                tempAggVal = aggregationService.getAggregatedDataValue( selDataElement, selDecoc,
                                    sDate, eDate, ou );
                                if ( tempAggVal == null )
                                    tempAggVal = 0.0;
                                pwdvAggValue = tempAggVal;

                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                if ( tempPeriod != null )
                                {
                                    DataValue dataValue = dataValueService.getDataValue( ou, selDataElement,
                                        tempPeriod, selDecoc );

                                    if ( dataValue != null )
                                    {
                                        tempStr = dataValue.getValue();
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                                else
                                {
                                    tempStr = " ";
                                }
                            }
                        }
                        else
                        {
                            List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>(
                                selDataElement.getCategoryCombo().getOptionCombos() );

                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                                while ( optionComboIterator.hasNext() )
                                {
                                    DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator
                                        .next();

                                    tempAggVal = aggregationService.getAggregatedDataValue( selDataElement, decoc1,
                                        sDate, eDate, ou );
                                    if ( tempAggVal == null )
                                        tempAggVal = 0.0;
                                    pwdvAggValue += tempAggVal;
                                }

                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                                while ( optionComboIterator.hasNext() )
                                {
                                    DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator
                                        .next();

                                    PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                    Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                    if ( tempPeriod != null )
                                    {
                                        DataValue dataValue = dataValueService.getDataValue( ou, selDataElement,
                                            tempPeriod, decoc1 );

                                        if ( dataValue != null )
                                        {
                                            tempStr += dataValue.getValue() + " : ";
                                        }
                                        else
                                        {
                                            tempStr = "  ";
                                        }
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                            }
                        }
                    }

                    if ( aggPeriodCB == null )
                    {
                        if ( rowCount == 1 )
                        {
                            sheet0.addCell( new Label( colCount, startRow + 1, periodNames.get( periodCount ),
                                getCellFormat1() ) );
                        }

                        if ( flag == 1 )
                        {
                            sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, pwdAggIndValue,
                                getCellFormat2() ) );
                        }
                        else
                        {
                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, (int) pwdvAggValue,
                                    getCellFormat2() ) );
                            }
                            else
                            {
                                sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, tempStr,
                                    getCellFormat2() ) );
                            }
                        }

                        colCount++;
                    }
                    else
                    {
                        numAggValue += pwnumAggValue;
                        denAggValue += pwdenAggValue;
                        dvAggValue += pwdvAggValue;
                        aggStrValue += tempStr + " - ";
                    }

                    periodCount++;
                }// Period Loop

                if ( aggPeriodCB != null )
                {
                    if ( flag == 1 )
                    {
                        try
                        {
                            double tempDouble = Double.parseDouble( selIndicator.getDenominator().trim() );

                            // if(tempInt == 1) denAggValue = 1;
                            denAggValue = tempDouble;
                        }
                        catch ( Exception e )
                        {
                            System.out.println( "Denominator is not expression" );
                        }

                        double aggIndValue = (numAggValue / denAggValue) * selIndicator.getIndicatorType().getFactor();
                        aggIndValue = Math.round( aggIndValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        sheet0
                            .addCell( new Number( colCount, headerRow + 1 + rowCount, aggIndValue, getCellFormat2() ) );
                    }
                    else if ( flag == 2 )
                    {
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, (int) dvAggValue,
                                getCellFormat2() ) );
                        }
                        else
                        {
                            sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, aggStrValue,
                                getCellFormat2() ) );
                        }
                    }

                    colCount++;
                }
                // colCount++;
            }// Service loop end

            rowCount++;
        }// Orgunit loop end

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "TabularAnalysis.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();

    }

    // -------------------------------------------------------------------------
    // Method for getting OrgUnit Level wise data in Excel Sheet 
    //     - UseCapturedData - Period wise 
    // -------------------------------------------------------------------------
    public void generateOrgUnitLevelData_UseCapturedData_Periodwise() throws Exception
    {
        int startRow = 0;
        int headerRow = 0;
        int headerCol = 0;

        //String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();
        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "TabularAnalysis", 0 );

        sheet0.mergeCells( headerCol, headerRow, headerCol, headerRow + 1 );
        sheet0.addCell( new Label( headerCol, headerRow, "Sl.No.", getCellFormat1() ) );

        selOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
        selOUList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selOrgUnit.getId() ) );

        Map<Integer, Integer> orgunitLevelMap = new HashMap<Integer, Integer>( reportService.getOrgunitLevelMap() );
    
        Iterator<OrganisationUnit> ouIterator = selOUList.iterator();
        while ( ouIterator.hasNext() )
        {
            OrganisationUnit orgU = ouIterator.next();
            
            Integer level = orgunitLevelMap.get( orgU.getId() );
            if( level == null )
                level = organisationUnitService.getLevelOfOrganisationUnit( orgU.getId() );
            if ( level > orgUnitLevelCB )
            {
                ouIterator.remove();
            }
        }

        int minOULevel = 1;
        int maxOuLevel = 1;
        if ( selOUList != null && selOUList.size() > 0 )
        {
            minOULevel = organisationUnitService.getLevelOfOrganisationUnit( selOUList.get( 0 ).getId() );
        }
        maxOuLevel = orgUnitLevelCB;

        int c1 = headerCol + 1;
        for ( int i = minOULevel; i <= maxOuLevel; i++ )
        {
            sheet0.mergeCells( c1, headerRow, c1, headerRow + 1 );
            sheet0.addCell( new Label( c1, headerRow, "Level " + i, getCellFormat1() ) );
            c1++;
        }

        /* Service Info */
        Indicator selIndicator = new Indicator();
        DataElement selDataElement = new DataElement();
        DataElementCategoryOptionCombo selDecoc = new DataElementCategoryOptionCombo();
        int flag = 0;

        List<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers(OrganisationUnit.class, selOUList ) );
        orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
        
        Map<String, String> aggDataMap = new HashMap<String, String>( reportService.getDataFromDataValueTable( orgUnitIdsByComma, dataElementIdsByComma, periodIdsByComma ) );
    
        /* Calculation Part */
        int rowCount = 1;
        int colCount = 0;
        for( OrganisationUnit ou : selOUList )
        {
            System.out.println("Entered into orgunitloop :"+new Date());
            sheet0.addCell( new Number( headerCol, headerRow + 1 + rowCount, rowCount, getCellFormat2() ) );
            
            Integer level = orgunitLevelMap.get( ou.getId() );
            if( level == null )
                level = organisationUnitService.getLevelOfOrganisationUnit( ou.getId() );
            
            colCount = 1 + level - minOULevel;
            sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, ou.getName(), getCellFormat2() ) );

            colCount = c1;
            int deListCount = 0;
            int indListCount = 0;
            int serviceListCount = 0;
            for( String serviceType : serviceTypeList )
            {
                if ( serviceType.equalsIgnoreCase( "I" ) )
                {
                    flag = 1;
                    selIndicator = indicatorList.get( indListCount );
                    indListCount++;
                    if ( rowCount == 1 )
                    {
                        sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                        sheet0.addCell( new Label( colCount, startRow, selIndicator.getName(), getCellFormat1() ) );
                    }
                }
                else
                {
                    flag = 2;
                    selDataElement = dataElementList.get( deListCount );
                    deListCount++;
                    if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                    {
                        selDecoc = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer.parseInt( selectedServices.get( serviceListCount ).split( ":" )[2] ) );
                        if ( rowCount == 1 )
                        {
                            sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                            sheet0.addCell( new Label( colCount, startRow, selDataElement.getName() + "-" + selDecoc.getName(), getCellFormat1() ) );
                        }
                    }
                    else
                    {
                        if ( rowCount == 1 )
                        {
                            sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                            sheet0.addCell( new Label( colCount, startRow, selDataElement.getName(), getCellFormat1() ) );
                        }
                    }
                }

                int periodCount = 0;
                for ( Date sDate : selStartPeriodList )
                {
                    Date eDate = selEndPeriodList.get( periodCount );
                
                    Collection<Integer> periodIds = new ArrayList<Integer>( periodMap.get( periodCount ) );
                    System.out.println( periodIds );

                    double pwdvAggValue = 0.0;
                    double pwdAggIndValue = 0.0;

                    String tempStr = "";
                    if ( flag == 1 )
                    {
                        Double numValue = 0.0;
                        Double denValue = 0.0;
                        Double indValue = 0.0;
                        for( Integer periodId : periodIds )
                        {
                            try
                            {
                                numValue += Double.parseDouble( getAggVal( selIndicator.getNumerator(), aggDataMap, ou.getId(), periodId ) );
                            }
                            catch( Exception e )
                            {
                                numValue = 0.0;
                            }
                            Double tempDenValue = 0.0;
                            try
                            {
                                //denValue += Double.parseDouble( getAggVal( selIndicator.getDenominator(), aggDataMap, ou.getId(), periodId ) );
                                tempDenValue = Double.parseDouble( getAggVal( selIndicator.getDenominator(), aggDataMap, ou.getId(), periodId ) );
                            }
                            catch( Exception e )
                            {
                                tempDenValue =1.0;
                            }
                            if( !selIndicator.getDenominator().trim().equals( "1" ) )
                            {
                                denValue += tempDenValue;
                            }
                            else 
                            {
                                denValue = 1.0;
                            }
                        }
                        
                        try
                        {
                            if( denValue != 0.0 )
                            {
                                indValue = ( numValue / denValue ) * selIndicator.getIndicatorType().getFactor();
                            }
                            else
                            {
                                indValue = 0.0;
                            }
                        }
                        catch( Exception e )
                        {
                            indValue = 0.0;
                        }

                        pwdAggIndValue = indValue;
                        pwdAggIndValue = Math.round( pwdAggIndValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        tempStr = "" + pwdAggIndValue;
                    }
                    else if ( flag == 2 )
                    {
                        if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                        {
                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                for( Integer periodId : periodIds )
                                {
                                    tempStr = aggDataMap.get( ou.getId()+":"+selDataElement.getId()+":"+selDecoc.getId()+":"+periodId );
                                    if( tempStr != null )
                                    {
                                        try
                                        {
                                            pwdvAggValue += Double.parseDouble( tempStr );
                                        }
                                        catch( Exception e )
                                        {
                                        }
                                    }
                                }
                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                if ( tempPeriod != null )
                                {
                                    DataValue dataValue = dataValueService.getDataValue( ou, selDataElement, tempPeriod, selDecoc );

                                    if ( dataValue != null && dataValue.getValue() != null )
                                    {
                                        tempStr = dataValue.getValue();
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                                else
                                {
                                    tempStr = " ";
                                }
                            }
                        }
                        else
                        {
                            List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>( selDataElement.getCategoryCombo().getOptionCombos() );

                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                for( DataElementCategoryOptionCombo optionCombo : optionCombos )
                                {
                                    for( Integer periodId : periodIds )
                                    {
                                        tempStr = aggDataMap.get( ou.getId()+":"+selDataElement.getId()+":"+optionCombo.getId()+":"+periodId );
                                        if( tempStr != null )
                                        {
                                            try
                                            {
                                                pwdvAggValue += Double.parseDouble( tempStr );
                                            }
                                            catch( Exception e )
                                            {
                                            }
                                        }
                                    }
                                }
                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                                while ( optionComboIterator.hasNext() )
                                {
                                    DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator.next();

                                    PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                    Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                    if( tempPeriod != null )
                                    {
                                        DataValue dataValue = dataValueService.getDataValue( ou, selDataElement, tempPeriod, decoc1 );

                                        if ( dataValue != null )
                                        {
                                            tempStr += dataValue.getValue() + " : ";
                                        }
                                        else
                                        {
                                            tempStr = "  ";
                                        }
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                            }
                        }
                    }

                    if ( rowCount == 1 )
                    {
                        sheet0.addCell( new Label( colCount, startRow + 1, periodNames.get( periodCount ), getCellFormat1() ) );
                    }

                    if ( flag == 1 )
                    {
                        sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, pwdAggIndValue, getCellFormat2() ) );
                    }
                    else
                    {
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, (int) pwdvAggValue, getCellFormat2() ) );
                        }
                        else
                        {
                            sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, tempStr, getCellFormat2() ) );
                        }
                    }

                    colCount++;
                    periodCount++;
                }// Period Loop
                serviceListCount++;
            }// Service loop end
            rowCount++;
        }// Orgunit loop end
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "TabularAnalysis.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
    
        outputReportFile.deleteOnExit();
    }

    // -------------------------------------------------------------------------
    // Method for getting OrgUnit Group wise data in Excel Sheet 
    //     - UseCapturedData - Period wise 
    // -------------------------------------------------------------------------
    public void generateOrgUnitGroupData_UseCapturedData_Periodwise() throws Exception
    {
        int startRow = 0;
        int headerRow = 0;
        int headerCol = 0;

        //String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();
        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "TabularAnalysis", 0 );

        sheet0.mergeCells( headerCol, headerRow, headerCol, headerRow + 1 );
        sheet0.addCell( new Label( headerCol, headerRow, "Sl.No.", getCellFormat1() ) );

        selOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
        selOUList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selOrgUnit.getId() ) );
        
        OrganisationUnitGroup selOrgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgUnitLevelCB );
        List<OrganisationUnit> orgUnitList1 = new ArrayList<OrganisationUnit>( selOrgUnitGroup.getMembers() );

        selOUList.retainAll( orgUnitList1 );

        int minOULevel = 1;
        int maxOULevel = organisationUnitService.getNumberOfOrganisationalLevels();
        int c1 = headerCol + 1;

        for( int i = minOULevel; i <= maxOULevel; i++ )
        {
            sheet0.mergeCells( c1, headerRow, c1, headerRow + 1 );
            sheet0.addCell( new Label( c1, headerRow, "Level- "+i, getCellFormat1() ) );
            c1++;
        }

        Map<Integer, Integer> orgunitLevelMap = new HashMap<Integer, Integer>( reportService.getOrgunitLevelMap() );
    
        /* Service Info */
        Indicator selIndicator = new Indicator();
        DataElement selDataElement = new DataElement();
        DataElementCategoryOptionCombo selDecoc = new DataElementCategoryOptionCombo();
        int flag = 0;

        List<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers(OrganisationUnit.class, selOUList ) );
        orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
        
        Map<String, String> aggDataMap = new HashMap<String, String>( reportService.getDataFromDataValueTable( orgUnitIdsByComma, dataElementIdsByComma, periodIdsByComma ) );
    
        /* Calculation Part */
        int rowCount = 1;
        int colCount = 0;
        for( OrganisationUnit ou : selOUList )
        {
            System.out.println("Entered into orgunitloop :"+new Date());
            sheet0.addCell( new Number( headerCol, headerRow + 1 + rowCount, rowCount, getCellFormat2() ) );
            
            Integer level = orgunitLevelMap.get( ou.getId() );
            if( level == null )
                level = organisationUnitService.getLevelOfOrganisationUnit( ou.getId() );
            
            colCount = 1 + level - minOULevel;
            sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, ou.getName(), getCellFormat2() ) );
            
            OrganisationUnit orgUnit = new OrganisationUnit();
            orgUnit = ou;
            int count1=1;
            while( orgUnit.getParent() != null )
            {
                orgUnit = orgUnit.getParent();
                sheet0.addCell( new Label( colCount-count1, headerRow + rowCount + 1, orgUnit.getName(), getCellFormat2() ) );
                count1++;
            }

            colCount = c1;
            int deListCount = 0;
            int indListCount = 0;
            int serviceListCount = 0;
            for( String serviceType : serviceTypeList )
            {
                if ( serviceType.equalsIgnoreCase( "I" ) )
                {
                    flag = 1;
                    selIndicator = indicatorList.get( indListCount );
                    indListCount++;
                    if ( rowCount == 1 )
                    {
                        sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                        sheet0.addCell( new Label( colCount, startRow, selIndicator.getName(), getCellFormat1() ) );
                    }
                }
                else
                {
                    flag = 2;
                    selDataElement = dataElementList.get( deListCount );
                    deListCount++;
                    if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                    {
                        selDecoc = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer.parseInt( selectedServices.get( serviceListCount ).split( ":" )[2] ) );
                        if ( rowCount == 1 )
                        {
                            sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                            sheet0.addCell( new Label( colCount, startRow, selDataElement.getName() + "-" + selDecoc.getName(), getCellFormat1() ) );
                        }
                    }
                    else
                    {
                        if ( rowCount == 1 )
                        {
                            sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                            sheet0.addCell( new Label( colCount, startRow, selDataElement.getName(), getCellFormat1() ) );
                        }
                    }
                }

                int periodCount = 0;
                for ( Date sDate : selStartPeriodList )
                {
                    Date eDate = selEndPeriodList.get( periodCount );
                
                    Collection<Integer> periodIds = new ArrayList<Integer>( periodMap.get( periodCount ) );
                    System.out.println( periodIds );

                    double pwdvAggValue = 0.0;
                    double pwdAggIndValue = 0.0;

                    String tempStr = "";
                    if ( flag == 1 )
                    {
                        Double numValue = 0.0;
                        Double denValue = 0.0;
                        Double indValue = 0.0;
                        for( Integer periodId : periodIds )
                        {
                            try
                            {
                                numValue += Double.parseDouble( getAggVal( selIndicator.getNumerator(), aggDataMap, ou.getId(), periodId ) );
                            }
                            catch( Exception e )
                            {
                            }
                            Double tempDenValue = 0.0;
                            try
                            {
                                //denValue += Double.parseDouble( getAggVal( selIndicator.getDenominator(), aggDataMap, ou.getId(), periodId ) );
                                tempDenValue = Double.parseDouble( getAggVal( selIndicator.getDenominator(), aggDataMap, ou.getId(), periodId ) );
                            }
                            catch( Exception e )
                            {
                                tempDenValue = 1.0;
                            }
                            if( !selIndicator.getDenominator().trim().equals( "1" ) )
                            {
                                denValue += tempDenValue;
                            }
                            else 
                            {
                                denValue = 1.0;
                            }
                        }
                        
                        try
                        {
                            if( denValue != 0.0 )
                            {
                                indValue = ( numValue / denValue ) * selIndicator.getIndicatorType().getFactor();
                            }
                            else
                            {
                                indValue = 0.0;
                            }
                        }
                        catch( Exception e )
                        {
                            indValue = 0.0;
                        }

                        pwdAggIndValue = indValue;
                        pwdAggIndValue = Math.round( pwdAggIndValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        tempStr = "" + pwdAggIndValue;
                    }
                    else if ( flag == 2 )
                    {
                        if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                        {
                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                for( Integer periodId : periodIds )
                                {
                                    tempStr = aggDataMap.get( ou.getId()+":"+selDataElement.getId()+":"+selDecoc.getId()+":"+periodId );
                                    if( tempStr != null )
                                    {
                                        try
                                        {
                                            pwdvAggValue += Double.parseDouble( tempStr );
                                        }
                                        catch( Exception e )
                                        {
                                        }
                                    }
                                }
                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                if ( tempPeriod != null )
                                {
                                    DataValue dataValue = dataValueService.getDataValue( ou, selDataElement, tempPeriod, selDecoc );

                                    if ( dataValue != null && dataValue.getValue() != null )
                                    {
                                        tempStr = dataValue.getValue();
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                                else
                                {
                                    tempStr = " ";
                                }
                            }
                        }
                        else
                        {
                            List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>( selDataElement.getCategoryCombo().getOptionCombos() );

                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                for( DataElementCategoryOptionCombo optionCombo : optionCombos )
                                {
                                    for( Integer periodId : periodIds )
                                    {
                                        tempStr = aggDataMap.get( ou.getId()+":"+selDataElement.getId()+":"+optionCombo.getId()+":"+periodId );
                                        if( tempStr != null )
                                        {
                                            try
                                            {
                                                pwdvAggValue += Double.parseDouble( tempStr );
                                            }
                                            catch( Exception e )
                                            {
                                            }
                                        }
                                    }
                                }
                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                                while ( optionComboIterator.hasNext() )
                                {
                                    DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator.next();

                                    PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                    Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                    if( tempPeriod != null )
                                    {
                                        DataValue dataValue = dataValueService.getDataValue( ou, selDataElement, tempPeriod, decoc1 );

                                        if ( dataValue != null )
                                        {
                                            tempStr += dataValue.getValue() + " : ";
                                        }
                                        else
                                        {
                                            tempStr = "  ";
                                        }
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                            }
                        }
                    }

                    if ( rowCount == 1 )
                    {
                        sheet0.addCell( new Label( colCount, startRow + 1, periodNames.get( periodCount ), getCellFormat1() ) );
                    }

                    if ( flag == 1 )
                    {
                        sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, pwdAggIndValue, getCellFormat2() ) );
                    }
                    else
                    {
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, (int) pwdvAggValue, getCellFormat2() ) );
                        }
                        else
                        {
                            sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, tempStr, getCellFormat2() ) );
                        }
                    }

                    colCount++;
                    periodCount++;
                }// Period Loop
                serviceListCount++;
            }// Service loop end
            rowCount++;
        }// Orgunit loop end
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "TabularAnalysis.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
    
        outputReportFile.deleteOnExit();
    }

    // -------------------------------------------------------------------------
    // Method for getting Selected OrgUnit(s) data in Excel Sheet 
    //     - UseCapturedData - Aggregation of Periods 
    // -------------------------------------------------------------------------
    public void generateSelectedOrgUnitData_UseCapturedData_AggPeriods() throws Exception
    {
        int headerRow = 0;
        int headerCol = 0;

        //String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();
        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "TabularAnalysis", 0 );

        sheet0.addCell( new Label( headerCol, headerRow, "Sl.No.", getCellFormat1() ) );

        for ( String ouStr : orgUnitListCB )
        {
            OrganisationUnit ou = organisationUnitService.getOrganisationUnit( Integer.parseInt( ouStr ) );
            selOUList.add( ou );
        }

        int c1 = headerCol + 1;
        sheet0.addCell( new Label( c1, headerRow, "Facility", getCellFormat1() ) );
        c1++;

        /* Service Info */
        Indicator selIndicator = new Indicator();
        DataElement selDataElement = new DataElement();
        DataElementCategoryOptionCombo selDecoc = new DataElementCategoryOptionCombo();
        int flag = 0;

        List<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers(OrganisationUnit.class, selOUList ) );
        orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
        
        Map<String, String> aggDataMap = new HashMap<String, String>( reportService.getDataFromDataValueTableByPeriodAgg( orgUnitIdsByComma, dataElementIdsByComma, periodIdsByComma ) );
    
        /* Calculation Part */
        int rowCount = 1;
        int colCount = 0;
        for( OrganisationUnit ou : selOUList )
        {
            System.out.println(ou.getName() + " : "+new Date());
            sheet0.addCell( new Number( headerCol, headerRow + rowCount, rowCount, getCellFormat2() ) );
            sheet0.addCell( new Label( 1, rowCount, ou.getName(), getCellFormat2() ) );
            
            colCount = c1;
            int deListCount = 0;
            int indListCount = 0;
            int serviceListCount = 0;
            for( String serviceType : serviceTypeList )
            {
                String tempStr = "";
                Double indValue = 0.0;
                Double dataValue = 0.0;
                
                if ( serviceType.equalsIgnoreCase( "I" ) )
                {
                    Double numValue = 0.0;
                    Double denValue = 0.0;

                    flag = 1;
                    selIndicator = indicatorList.get( indListCount );
                    indListCount++;
                    if ( rowCount == 1 )
                    {
                        sheet0.addCell( new Label( colCount, headerRow, selIndicator.getName(), getCellFormat1() ) );
                    }
                    
                    try
                    {
                        numValue = Double.parseDouble( getAggValByOrgUnit( selIndicator.getNumerator(), aggDataMap, ou.getId() ) );
                    }
                    catch( Exception e )
                    {
                        numValue = 0.0;
                    }
                    if( !selIndicator.getDenominator().trim().equals( "1" ) )
                    {
                        try
                        {
                            denValue = Double.parseDouble( getAggValByOrgUnit( selIndicator.getDenominator(), aggDataMap, ou.getId() ) );
                        }
                        catch( Exception e )
                        {
                            denValue = 1.0;
                        }
                    }
                    else 
                    {
                        denValue = 1.0;
                    }
                    /*
                    try
                    {
                        denValue = Double.parseDouble( getAggValByOrgUnit( selIndicator.getDenominator(), aggDataMap, ou.getId() ) );
                    }
                    catch( Exception e )
                    {
                    }
                    */
                    try
                    {
                        if( denValue != 0.0 )
                        {
                            indValue = ( numValue / denValue ) * selIndicator.getIndicatorType().getFactor();
                        }
                        else
                        {
                            indValue = 0.0;
                        }
                    }
                    catch( Exception e )
                    {
                        indValue = 0.0;
                    }
                    
                    indValue = Math.round( indValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                }
                else
                {
                    flag = 2;
                    selDataElement = dataElementList.get( deListCount );
                    deListCount++;
                    if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                    {
                        selDecoc = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer.parseInt( selectedServices.get( serviceListCount ).split( ":" )[2] ) );
                        if ( rowCount == 1 )
                        {
                            sheet0.addCell( new Label( colCount, headerRow, selDataElement.getName() + "-" + selDecoc.getName(), getCellFormat1() ) );
                        }
                        
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            tempStr = aggDataMap.get( ou.getId()+":"+selDataElement.getId()+":"+selDecoc.getId() );
                            if( tempStr != null )
                            {
                                try
                                {
                                    dataValue = Double.parseDouble( tempStr );
                                }
                                catch( Exception e )
                                {
                                    dataValue = 0.0;
                                }
                            }
                        }
                        else
                        {
                            dataValue = 0.0;
                        }
                    }
                    else
                    {
                        if ( rowCount == 1 )
                        {
                            sheet0.addCell( new Label( colCount, headerRow, selDataElement.getName(), getCellFormat1() ) );
                        }
                        List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>( selDataElement.getCategoryCombo().getOptionCombos() );
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            for( DataElementCategoryOptionCombo optionCombo : optionCombos )
                            {
                                tempStr = aggDataMap.get( ou.getId()+":"+selDataElement.getId()+":"+optionCombo.getId() );
                                if( tempStr != null )
                                {
                                    try
                                    {
                                        dataValue += Double.parseDouble( tempStr );
                                    }
                                    catch( Exception e )
                                    {
                                    }
                                }
                            }
                        }
                        else
                        {
                            dataValue = 0.0;
                        }
                    }
                }

                if ( flag == 1 )
                {
                    sheet0.addCell( new Number( colCount, headerRow + rowCount, indValue, getCellFormat2() ) );
                }
                else
                {
                    sheet0.addCell( new Number( colCount, headerRow + rowCount, dataValue, getCellFormat2() ) );
                }

                colCount++;
                serviceListCount++;
            }// Service loop end
            rowCount++;
        }// Orgunit loop end
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "TabularAnalysis.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
    
        outputReportFile.deleteOnExit();
    }

    // -------------------------------------------------------------------------
    // Method for getting Selected OrgUnit(s) data in Excel Sheet 
    //     - GenerateAggregatedData - Aggregation of Periods 
    // -------------------------------------------------------------------------
    public void generateSelectedOrgUnitData_GenerateAggregateData_AggPeriods() throws Exception
    {
        int headerRow = 0;
        int headerCol = 0;

        //String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();
        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "TabularAnalysis", 0 );

        sheet0.addCell( new Label( headerCol, headerRow, "Sl.No.", getCellFormat1() ) );

        for ( String ouStr : orgUnitListCB )
        {
            OrganisationUnit ou = organisationUnitService.getOrganisationUnit( Integer.parseInt( ouStr ) );
            selOUList.add( ou );
        }

        int c1 = headerCol + 1;
        sheet0.addCell( new Label( c1, headerRow, "Facility", getCellFormat1() ) );
        c1++;

        /* Service Info */
        Indicator selIndicator = new Indicator();
        DataElement selDataElement = new DataElement();
        DataElementCategoryOptionCombo selDecoc = new DataElementCategoryOptionCombo();
        int flag = 0;

        /* Calculation Part */
        int rowCount = 1;
        int colCount = 0;
        for( OrganisationUnit ou : selOUList )
        {
            System.out.println("Entered into orgunitloop :"+new Date());
            sheet0.addCell( new Number( headerCol, headerRow + rowCount, rowCount, getCellFormat2() ) );
            sheet0.addCell( new Label( 1, headerRow + rowCount, ou.getName(), getCellFormat2() ) );
            
            List<OrganisationUnit> ouChildList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( ou.getId() ) );
            List<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers(OrganisationUnit.class, ouChildList ) );
            orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
            Map<String, String> aggDataMap = new HashMap<String, String>( reportService.getAggDataFromDataValueTable( orgUnitIdsByComma, dataElementIdsByComma, periodIdsByComma ) );
            
            colCount = c1;
            int deListCount = 0;
            int indListCount = 0;
            int serviceListCount = 0;
            for( String serviceType : serviceTypeList )
            {
                String tempStr = "";
                Double indValue = 0.0;
                Double dataValue = 0.0;
                
                if ( serviceType.equalsIgnoreCase( "I" ) )
                {
                    Double numValue = 0.0;
                    Double denValue = 0.0;

                    flag = 1;
                    selIndicator = indicatorList.get( indListCount );
                    indListCount++;
                    if ( rowCount == 1 )
                    {
                        sheet0.addCell( new Label( colCount, headerRow, selIndicator.getName(), getCellFormat1() ) );
                    }
                    
                    try
                    {
                        numValue = Double.parseDouble( reportService.getAggVal( selIndicator.getNumerator(), aggDataMap ) );
                    }
                    catch( Exception e )
                    {
                        numValue = 0.0;
                    }
                    if( !selIndicator.getDenominator().trim().equals( "1" ) )
                    {
                        try
                        {
                            denValue = Double.parseDouble( reportService.getAggVal( selIndicator.getDenominator(), aggDataMap ) );
                        }
                        catch( Exception e )
                        {
                            denValue = 1.0;
                        }
                    }
                    else 
                    {
                        denValue = 1.0;
                    }
                    /*
                    try
                    {
                        denValue = Double.parseDouble( reportService.getAggVal( selIndicator.getDenominator(), aggDataMap ) );
                    }
                    catch( Exception e )
                    {
                    }
                    */
                    try
                    {
                        if( denValue != 0.0 )
                        {
                            indValue = ( numValue / denValue ) * selIndicator.getIndicatorType().getFactor();
                        }
                        else
                        {
                            indValue = 0.0;
                        }
                    }
                    catch( Exception e )
                    {
                        indValue = 0.0;
                    }
                    
                    indValue = Math.round( indValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                }
                else
                {
                    flag = 2;
                    selDataElement = dataElementList.get( deListCount );
                    deListCount++;
                    if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                    {
                        selDecoc = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer.parseInt( selectedServices.get( serviceListCount ).split( ":" )[2] ) );
                        if ( rowCount == 1 )
                        {
                            sheet0.addCell( new Label( colCount, headerRow, selDataElement.getName() + "-" + selDecoc.getName(), getCellFormat1() ) );
                        }
                        
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            tempStr = aggDataMap.get( selDataElement.getId()+"."+selDecoc.getId() );
                            if( tempStr != null )
                            {
                                try
                                {
                                    dataValue = Double.parseDouble( tempStr );
                                }
                                catch( Exception e )
                                {
                                    dataValue = 0.0;
                                }
                            }
                        }
                        else
                        {
                            dataValue = 0.0;
                        }
                    }
                    else
                    {
                        if ( rowCount == 1 )
                        {
                            sheet0.addCell( new Label( colCount, headerRow, selDataElement.getName(), getCellFormat1() ) );
                        }
                        List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>( selDataElement.getCategoryCombo().getOptionCombos() );
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            for( DataElementCategoryOptionCombo optionCombo : optionCombos )
                            {
                                tempStr = aggDataMap.get( selDataElement.getId()+"."+optionCombo.getId() );
                                if( tempStr != null )
                                {
                                    try
                                    {
                                        dataValue += Double.parseDouble( tempStr );
                                    }
                                    catch( Exception e )
                                    {
                                    }
                                }
                            }
                        }
                        else
                        {
                            dataValue = 0.0;
                        }
                    }
                }

                if ( flag == 1 )
                {
                    sheet0.addCell( new Number( colCount, headerRow + rowCount, indValue, getCellFormat2() ) );
                }
                else
                {
                    sheet0.addCell( new Number( colCount, headerRow + rowCount, dataValue, getCellFormat2() ) );
                }

                colCount++;
                serviceListCount++;
            }// Service loop end
            rowCount++;
        }// Orgunit loop end
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "TabularAnalysis.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
    
        outputReportFile.deleteOnExit();    
    }

    // -------------------------------------------------------------------------
    // Method for getting OrgUnit Level wise data in Excel Sheet 
    //     - GenerateAggregatedData - Aggregation of Periods 
    // -------------------------------------------------------------------------
    public void generateOrgUnitLevelData_GenerateAggregateData_AggPeriods() throws Exception
    {
        int headerRow = 0;
        int headerCol = 0;

        //String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();
        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "TabularAnalysis", 0 );

        sheet0.addCell( new Label( headerCol, headerRow, "Sl.No.", getCellFormat1() ) );

        selOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
        selOUList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selOrgUnit.getId() ) );

        System.out.println( "Before getting orgunitlevelmap "+new Date() );
        Map<Integer, Integer> orgunitLevelMap = new HashMap<Integer, Integer>( reportService.getOrgunitLevelMap() );
        System.out.println( "After getting orgunitlevelmap "+new Date() );
    
        Iterator<OrganisationUnit> ouIterator = selOUList.iterator();
        while ( ouIterator.hasNext() )
        {
            OrganisationUnit orgU = ouIterator.next();
            
            Integer level = orgunitLevelMap.get( orgU.getId() );
            if( level == null )
                level = organisationUnitService.getLevelOfOrganisationUnit( orgU.getId() );
            if ( level > orgUnitLevelCB )
            {
                ouIterator.remove();
            }
        }

        int minOULevel = 1;
        int maxOuLevel = 1;
        if ( selOUList != null && selOUList.size() > 0 )
        {
            minOULevel = organisationUnitService.getLevelOfOrganisationUnit( selOUList.get( 0 ).getId() );
        }
        maxOuLevel = orgUnitLevelCB;

        int c1 = headerCol + 1;
        for ( int i = minOULevel; i <= maxOuLevel; i++ )
        {
            sheet0.addCell( new Label( c1, headerRow, "Level " + i, getCellFormat1() ) );
            c1++;
        }

        /* Service Info */
        Indicator selIndicator = new Indicator();
        DataElement selDataElement = new DataElement();
        DataElementCategoryOptionCombo selDecoc = new DataElementCategoryOptionCombo();
        int flag = 0;

        /* Calculation Part */
        int rowCount = 1;
        int colCount = 0;
        for( OrganisationUnit ou : selOUList )
        {
            System.out.println("Entered into orgunitloop :"+new Date());
            sheet0.addCell( new Number( headerCol, headerRow + rowCount, rowCount, getCellFormat2() ) );
            
            Integer level = orgunitLevelMap.get( ou.getId() );
            if( level == null )
                level = organisationUnitService.getLevelOfOrganisationUnit( ou.getId() );
            
            colCount = 1 + level - minOULevel;
            sheet0.addCell( new Label( colCount, headerRow + rowCount, ou.getName(), getCellFormat2() ) );

            List<OrganisationUnit> ouChildList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( ou.getId() ) );
            List<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers(OrganisationUnit.class, ouChildList ) );
            orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
            Map<String, String> aggDataMap = new HashMap<String, String>( reportService.getAggDataFromDataValueTable( orgUnitIdsByComma, dataElementIdsByComma, periodIdsByComma ) );
            colCount = c1;
            int deListCount = 0;
            int indListCount = 0;
            int serviceListCount = 0;
            for( String serviceType : serviceTypeList )
            {
                String tempStr = "";
                Double indValue = 0.0;
                Double dataValue = 0.0;
                
                if ( serviceType.equalsIgnoreCase( "I" ) )
                {
                    Double numValue = 0.0;
                    Double denValue = 0.0;

                    flag = 1;
                    selIndicator = indicatorList.get( indListCount );
                    indListCount++;
                    if ( rowCount == 1 )
                    {
                        sheet0.addCell( new Label( colCount, headerRow, selIndicator.getName(), getCellFormat1() ) );
                    }
                    
                    try
                    {
                        numValue = Double.parseDouble( reportService.getAggVal( selIndicator.getNumerator(), aggDataMap ) );
                    }
                    catch( Exception e )
                    {
                        numValue = 0.0;
                    }
                   
                    if( !selIndicator.getDenominator().trim().equals( "1" ) )
                    {
                        try
                        {
                            denValue = Double.parseDouble( reportService.getAggVal( selIndicator.getDenominator(), aggDataMap ) );
                        }
                        catch( Exception e )
                        {
                            denValue = 1.0;
                        }
                    }
                    else 
                    {
                        denValue = 1.0;
                    }
                   
                   /*
                    try
                    {
                        denValue = Double.parseDouble( reportService.getAggVal( selIndicator.getDenominator(), aggDataMap ) );
                    }
                    catch( Exception e )
                    {
                    }
                   */
                    try
                    {
                        if( denValue != 0.0 )
                        {
                            indValue = ( numValue / denValue ) * selIndicator.getIndicatorType().getFactor();
                        }
                        else
                        {
                            indValue = 0.0;
                        }
                    }
                    catch( Exception e )
                    {
                        indValue = 0.0;
                    }
                    
                    indValue = Math.round( indValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                }
                else
                {
                    flag = 2;
                    selDataElement = dataElementList.get( deListCount );
                    deListCount++;
                    if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                    {
                        selDecoc = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer.parseInt( selectedServices.get( serviceListCount ).split( ":" )[2] ) );
                        if ( rowCount == 1 )
                        {
                            sheet0.addCell( new Label( colCount, headerRow, selDataElement.getName() + "-" + selDecoc.getName(), getCellFormat1() ) );
                        }
                        
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            tempStr = aggDataMap.get( selDataElement.getId()+"."+selDecoc.getId() );
                            if( tempStr != null )
                            {
                                try
                                {
                                    dataValue = Double.parseDouble( tempStr );
                                }
                                catch( Exception e )
                                {
                                    dataValue = 0.0;
                                }
                            }
                        }
                        else
                        {
                            dataValue = 0.0;
                        }
                    }
                    else
                    {
                        if ( rowCount == 1 )
                        {
                            sheet0.addCell( new Label( colCount, headerRow, selDataElement.getName(), getCellFormat1() ) );
                        }
                        List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>( selDataElement.getCategoryCombo().getOptionCombos() );
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            for( DataElementCategoryOptionCombo optionCombo : optionCombos )
                            {
                                tempStr = aggDataMap.get( selDataElement.getId()+"."+optionCombo.getId() );
                                if( tempStr != null )
                                {
                                    try
                                    {
                                        dataValue += Double.parseDouble( tempStr );
                                    }
                                    catch( Exception e )
                                    {
                                    }
                                }
                            }
                        }
                        else
                        {
                            dataValue = 0.0;
                        }
                    }
                }

                if ( flag == 1 )
                {
                    sheet0.addCell( new Number( colCount, headerRow + rowCount, indValue, getCellFormat2() ) );
                }
                else
                {
                    sheet0.addCell( new Number( colCount, headerRow + rowCount, dataValue, getCellFormat2() ) );
                }

                colCount++;
                serviceListCount++;
            }// Service loop end
            rowCount++;
        }// Orgunit loop end
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "TabularAnalysis.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
    
        outputReportFile.deleteOnExit();    
    }

    // -------------------------------------------------------------------------
    // Method for getting OrgUnit Group wise data in Excel Sheet 
    //     - GenerateAggregatedData - Aggregation of Periods 
    // -------------------------------------------------------------------------
    public void generateOrgUnitGroupData_GenerateAggregateData_AggPeriods() throws Exception
    {
        int headerRow = 0;
        int headerCol = 0;

        //String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();
        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "TabularAnalysis", 0 );

        sheet0.addCell( new Label( headerCol, headerRow, "Sl.No.", getCellFormat1() ) );

        selOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
        selOUList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selOrgUnit.getId() ) );

        OrganisationUnitGroup selOrgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgUnitLevelCB );
        List<OrganisationUnit> orgUnitList1 = new ArrayList<OrganisationUnit>( selOrgUnitGroup.getMembers() );

        selOUList.retainAll( orgUnitList1 );
        
        int minOULevel = 1;
        int maxOULevel = organisationUnitService.getNumberOfOrganisationalLevels();

        int c1 = headerCol + 1;

        for( int i = minOULevel; i <= maxOULevel; i++ )
        {
            sheet0.addCell( new Label( c1, headerRow, "Level- "+i, getCellFormat1() ) );
            c1++;
        }

        Map<Integer, Integer> orgunitLevelMap = new HashMap<Integer, Integer>( reportService.getOrgunitLevelMap() );

        /* Service Info */
        Indicator selIndicator = new Indicator();
        DataElement selDataElement = new DataElement();
        DataElementCategoryOptionCombo selDecoc = new DataElementCategoryOptionCombo();
        int flag = 0;

        /* Calculation Part */
        int rowCount = 1;
        int colCount = 0;
        for( OrganisationUnit ou : selOUList )
        {
            System.out.println("Entered into orgunitloop :"+new Date());
            sheet0.addCell( new Number( headerCol, headerRow + rowCount, rowCount, getCellFormat2() ) );
            
            Integer level = orgunitLevelMap.get( ou.getId() );
            if( level == null )
                level = organisationUnitService.getLevelOfOrganisationUnit( ou.getId() );
            
            colCount = 1 + level - minOULevel;
            sheet0.addCell( new Label( colCount, headerRow + rowCount, ou.getName(), getCellFormat2() ) );

            OrganisationUnit orgUnit = new OrganisationUnit();
            orgUnit = ou;
            int count1=1;
            while( orgUnit.getParent() != null )
            {
                orgUnit = orgUnit.getParent();
                sheet0.addCell( new Label( colCount-count1, headerRow + rowCount, orgUnit.getName(), getCellFormat2() ) );
                count1++;
            }

            List<OrganisationUnit> ouChildList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( ou.getId() ) );
            List<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers(OrganisationUnit.class, ouChildList ) );
            orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
            Map<String, String> aggDataMap = new HashMap<String, String>( reportService.getAggDataFromDataValueTable( orgUnitIdsByComma, dataElementIdsByComma, periodIdsByComma ) );
            
            colCount = c1;
            int deListCount = 0;
            int indListCount = 0;
            int serviceListCount = 0;
            for( String serviceType : serviceTypeList )
            {
                String tempStr = "";
                Double indValue = 0.0;
                Double dataValue = 0.0;
                
                if ( serviceType.equalsIgnoreCase( "I" ) )
                {
                    Double numValue = 0.0;
                    Double denValue = 0.0;

                    flag = 1;
                    selIndicator = indicatorList.get( indListCount );
                    indListCount++;
                    if ( rowCount == 1 )
                    {
                        sheet0.addCell( new Label( colCount, headerRow, selIndicator.getName(), getCellFormat1() ) );
                    }
                    
                    try
                    {
                        numValue = Double.parseDouble( reportService.getAggVal( selIndicator.getNumerator(), aggDataMap ) );
                    }
                    catch( Exception e )
                    {
                        numValue = 0.0;
                    }
                    if( !selIndicator.getDenominator().trim().equals( "1" ) )
                    {
                        try
                        {
                            denValue = Double.parseDouble( reportService.getAggVal( selIndicator.getDenominator(), aggDataMap ) );
                        }
                        catch( Exception e )
                        {
                            denValue = 1.0;
                        }
                    }
                    else 
                    {
                        denValue = 1.0;
                    }
                    /*
                    try
                    {
                        denValue = Double.parseDouble( reportService.getAggVal( selIndicator.getDenominator(), aggDataMap ) );
                    }
                    catch( Exception e )
                    {
                    }
                    */
                    
                    try
                    {
                        if( denValue != 0.0 )
                        {
                            indValue = ( numValue / denValue ) * selIndicator.getIndicatorType().getFactor();
                        }
                        else
                        {
                            indValue = 0.0;
                        }
                    }
                    catch( Exception e )
                    {
                        indValue = 0.0;
                    }
                    
                    indValue = Math.round( indValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                }
                else
                {
                    flag = 2;
                    selDataElement = dataElementList.get( deListCount );
                    deListCount++;
                    if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                    {
                        selDecoc = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer.parseInt( selectedServices.get( serviceListCount ).split( ":" )[2] ) );
                        if ( rowCount == 1 )
                        {
                            sheet0.addCell( new Label( colCount, headerRow, selDataElement.getName() + "-" + selDecoc.getName(), getCellFormat1() ) );
                        }
                        
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            tempStr = aggDataMap.get( selDataElement.getId()+"."+selDecoc.getId() );
                            if( tempStr != null )
                            {
                                try
                                {
                                    dataValue = Double.parseDouble( tempStr );
                                }
                                catch( Exception e )
                                {
                                    dataValue = 0.0;
                                }
                            }
                        }
                        else
                        {
                            dataValue = 0.0;
                        }
                    }
                    else
                    {
                        if ( rowCount == 1 )
                        {
                            sheet0.addCell( new Label( colCount, headerRow, selDataElement.getName(), getCellFormat1() ) );
                        }
                        List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>( selDataElement.getCategoryCombo().getOptionCombos() );
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            for( DataElementCategoryOptionCombo optionCombo : optionCombos )
                            {
                                tempStr = aggDataMap.get( selDataElement.getId()+"."+optionCombo.getId() );
                                if( tempStr != null )
                                {
                                    try
                                    {
                                        dataValue += Double.parseDouble( tempStr );
                                    }
                                    catch( Exception e )
                                    {
                                    }
                                }
                            }
                        }
                        else
                        {
                            dataValue = 0.0;
                        }
                    }
                }

                if ( flag == 1 )
                {
                    sheet0.addCell( new Number( colCount, headerRow + rowCount, indValue, getCellFormat2() ) );
                }
                else
                {
                    sheet0.addCell( new Number( colCount, headerRow + rowCount, dataValue, getCellFormat2() ) );
                }

                colCount++;
                serviceListCount++;
            }// Service loop end
            rowCount++;
        }// Orgunit loop end
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "TabularAnalysis.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
    
        outputReportFile.deleteOnExit();    
    }
    
    // -------------------------------------------------------------------------
    // Method for getting OrgUnit Level wise data in Excel Sheet 
    //     - GenerateAggregatedData - Periodwise 
    // -------------------------------------------------------------------------
    public void generateOrgUnitLevelData_GenerateAggregateData_Periodwise() throws Exception
    {
        int startRow = 0;
        int headerRow = 0;
        int headerCol = 0;

        //String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();
        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "TabularAnalysis", 0 );

        sheet0.mergeCells( headerCol, headerRow, headerCol, headerRow + 1 );
        sheet0.addCell( new Label( headerCol, headerRow, "Sl.No.", getCellFormat1() ) );

        selOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
        selOUList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selOrgUnit.getId() ) );

        Map<Integer, Integer> orgunitLevelMap = new HashMap<Integer, Integer>( reportService.getOrgunitLevelMap() );
    
        Iterator<OrganisationUnit> ouIterator = selOUList.iterator();
        while ( ouIterator.hasNext() )
        {
            OrganisationUnit orgU = ouIterator.next();
            
            Integer level = orgunitLevelMap.get( orgU.getId() );
            if( level == null )
                level = organisationUnitService.getLevelOfOrganisationUnit( orgU.getId() );
            if ( level > orgUnitLevelCB )
            {
                ouIterator.remove();
            }
        }

        int minOULevel = 1;
        int maxOuLevel = 1;
        if ( selOUList != null && selOUList.size() > 0 )
        {
            minOULevel = organisationUnitService.getLevelOfOrganisationUnit( selOUList.get( 0 ).getId() );
        }
        maxOuLevel = orgUnitLevelCB;

        int c1 = headerCol + 1;
        for ( int i = minOULevel; i <= maxOuLevel; i++ )
        {
            sheet0.mergeCells( c1, headerRow, c1, headerRow + 1 );
            sheet0.addCell( new Label( c1, headerRow, "Level " + i, getCellFormat1() ) );
            c1++;
        }

        /* Service Info */
        Indicator selIndicator = new Indicator();
        DataElement selDataElement = new DataElement();
        DataElementCategoryOptionCombo selDecoc = new DataElementCategoryOptionCombo();
        int flag = 0;

        /* Calculation Part */
        int rowCount = 1;
        int colCount = 0;
        for( OrganisationUnit ou : selOUList )
        {
            System.out.println( ou.getName() +" : "+ new Date());
            sheet0.addCell( new Number( headerCol, headerRow + 1 + rowCount, rowCount, getCellFormat2() ) );
            
            Integer level = orgunitLevelMap.get( ou.getId() );
            if( level == null )
                level = organisationUnitService.getLevelOfOrganisationUnit( ou.getId() );
            
            colCount = 1 + level - minOULevel;
            sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, ou.getName(), getCellFormat2() ) );

            List<OrganisationUnit> ouChildList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( ou.getId() ) );
            List<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers(OrganisationUnit.class, ouChildList ) );
            orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
            Map<String, String> aggDataMap = new HashMap<String, String>( reportService.getAggDataFromDataValueTableByDeAndPeriodwise( orgUnitIdsByComma, dataElementIdsByComma, periodIdsByComma ) );
            
            colCount = c1;
            int deListCount = 0;
            int indListCount = 0;
            int serviceListCount = 0;
            for( String serviceType : serviceTypeList )
            {
                if ( serviceType.equalsIgnoreCase( "I" ) )
                {
                    flag = 1;
                    selIndicator = indicatorList.get( indListCount );
                    indListCount++;
                    if ( rowCount == 1 )
                    {
                        sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                        sheet0.addCell( new Label( colCount, startRow, selIndicator.getName(), getCellFormat1() ) );
                    }
                }
                else
                {
                    flag = 2;
                    selDataElement = dataElementList.get( deListCount );
                    deListCount++;
                    if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                    {
                        selDecoc = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer.parseInt( selectedServices.get( serviceListCount ).split( ":" )[2] ) );
                        if ( rowCount == 1 )
                        {
                            sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                            sheet0.addCell( new Label( colCount, startRow, selDataElement.getName() + "-" + selDecoc.getName(), getCellFormat1() ) );
                        }
                    }
                    else
                    {
                        if ( rowCount == 1 )
                        {
                            sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                            sheet0.addCell( new Label( colCount, startRow, selDataElement.getName(), getCellFormat1() ) );
                        }
                    }
                }

                int periodCount = 0;
                for ( Date sDate : selStartPeriodList )
                {
                    Date eDate = selEndPeriodList.get( periodCount );
                
                    Collection<Integer> periodIds = new ArrayList<Integer>( periodMap.get( periodCount ) );

                    double pwdvAggValue = 0.0;
                    double pwdAggIndValue = 0.0;

                    String tempStr = "";
                    if ( flag == 1 )
                    {
                        Double numValue = 0.0;
                        Double denValue = 0.0;
                        Double indValue = 0.0;
                        
                        for( Integer periodId : periodIds )
                        {
                            try
                            {
                                numValue += Double.parseDouble( getAggValByPeriod( selIndicator.getNumerator(), aggDataMap, periodId ) );
                            }
                            catch( Exception e )
                            {
                                numValue = 0.0;
                            }
                            Double tempDenValue = 0.0;
                            try
                            {
                               // denValue += Double.parseDouble( getAggValByPeriod( selIndicator.getDenominator(), aggDataMap, periodId ) );
                                tempDenValue = Double.parseDouble( getAggValByPeriod( selIndicator.getDenominator(), aggDataMap, periodId ) );
                            }
                            catch( Exception e )
                            {
                                tempDenValue = 1.0;
                            }
                            if ( !selIndicator.getDenominator().trim().equals( "1" ) )
                            {
                                denValue += tempDenValue;
                            }
                            else 
                            {
                                denValue = 1.0;
                            }
                        }
                        
                        try
                        {
                            if( denValue != 0.0 )
                            {
                                indValue = ( numValue / denValue ) * selIndicator.getIndicatorType().getFactor();
                            }
                            else
                            {
                                indValue = 0.0;
                            }
                        }
                        catch( Exception e )
                        {
                            indValue = 0.0;
                        }

                        pwdAggIndValue = indValue;
                        pwdAggIndValue = Math.round( pwdAggIndValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        tempStr = "" + pwdAggIndValue;
                    }
                    else if ( flag == 2 )
                    {
                        if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                        {
                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                for( Integer periodId : periodIds )
                                {
                                    tempStr = aggDataMap.get( selDataElement.getId()+":"+selDecoc.getId()+":"+periodId );
                                    if( tempStr != null )
                                    {
                                        try
                                        {
                                            pwdvAggValue += Double.parseDouble( tempStr );
                                        }
                                        catch( Exception e )
                                        {
                                        }
                                    }
                                }
                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                if ( tempPeriod != null )
                                {
                                    DataValue dataValue = dataValueService.getDataValue( ou, selDataElement, tempPeriod, selDecoc );

                                    if ( dataValue != null && dataValue.getValue() != null )
                                    {
                                        tempStr = dataValue.getValue();
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                                else
                                {
                                    tempStr = " ";
                                }
                            }
                        }
                        else
                        {
                            List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>( selDataElement.getCategoryCombo().getOptionCombos() );

                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                for( DataElementCategoryOptionCombo optionCombo : optionCombos )
                                {
                                    for( Integer periodId : periodIds )
                                    {
                                        tempStr = aggDataMap.get( selDataElement.getId()+":"+optionCombo.getId()+":"+periodId );
                                        if( tempStr != null )
                                        {
                                            try
                                            {
                                                pwdvAggValue += Double.parseDouble( tempStr );
                                            }
                                            catch( Exception e )
                                            {
                                            }
                                        }
                                    }
                                }
                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                                while ( optionComboIterator.hasNext() )
                                {
                                    DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator.next();

                                    PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                    Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                    if( tempPeriod != null )
                                    {
                                        DataValue dataValue = dataValueService.getDataValue( ou, selDataElement, tempPeriod, decoc1 );

                                        if ( dataValue != null )
                                        {
                                            tempStr += dataValue.getValue() + " : ";
                                        }
                                        else
                                        {
                                            tempStr = "  ";
                                        }
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                            }
                        }
                    }

                    if ( rowCount == 1 )
                    {
                        sheet0.addCell( new Label( colCount, startRow + 1, periodNames.get( periodCount ), getCellFormat1() ) );
                    }

                    if ( flag == 1 )
                    {
                        sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, pwdAggIndValue, getCellFormat2() ) );
                    }
                    else
                    {
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, (int) pwdvAggValue, getCellFormat2() ) );
                        }
                        else
                        {
                            sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, tempStr, getCellFormat2() ) );
                        }
                    }

                    colCount++;
                    periodCount++;
                }// Period Loop
                serviceListCount++;
            }// Service loop end
            rowCount++;
        }// Orgunit loop end
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "TabularAnalysis.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
    
        outputReportFile.deleteOnExit();
    }
    
    // -------------------------------------------------------------------------
    // Method for getting OrgUnit Group wise data in Excel Sheet 
    //     - GenerateAggregatedData - Periodwise 
    // -------------------------------------------------------------------------
    public void generateOrgUnitGroupData_GenerateAggregateData_Periodwise() throws Exception
    {
        int startRow = 0;
        int headerRow = 0;
        int headerCol = 0;

        //String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();
        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "TabularAnalysis", 0 );

        sheet0.mergeCells( headerCol, headerRow, headerCol, headerRow + 1 );
        sheet0.addCell( new Label( headerCol, headerRow, "Sl.No.", getCellFormat1() ) );

        selOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
        selOUList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selOrgUnit.getId() ) );

        OrganisationUnitGroup selOrgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgUnitLevelCB );
        List<OrganisationUnit> orgUnitList1 = new ArrayList<OrganisationUnit>( selOrgUnitGroup.getMembers() );

        selOUList.retainAll( orgUnitList1 );

        int minOULevel = 1;
        int maxOULevel = organisationUnitService.getNumberOfOrganisationalLevels();

        int c1 = headerCol + 1;

        for( int i = minOULevel; i <= maxOULevel; i++ )
        {
            sheet0.mergeCells( c1, headerRow, c1, headerRow + 1 );
            sheet0.addCell( new Label( c1, headerRow, "Level- "+i, getCellFormat1() ) );
            c1++;
        }

        Map<Integer, Integer> orgunitLevelMap = new HashMap<Integer, Integer>( reportService.getOrgunitLevelMap() );

        /* Service Info */
        Indicator selIndicator = new Indicator();
        DataElement selDataElement = new DataElement();
        DataElementCategoryOptionCombo selDecoc = new DataElementCategoryOptionCombo();
        int flag = 0;

        /* Calculation Part */
        int rowCount = 1;
        int colCount = 0;
        for( OrganisationUnit ou : selOUList )
        {
            System.out.println( ou.getName() +" : "+ new Date());
            sheet0.addCell( new Number( headerCol, headerRow + 1 + rowCount, rowCount, getCellFormat2() ) );
            
            Integer level = orgunitLevelMap.get( ou.getId() );
            if( level == null )
                level = organisationUnitService.getLevelOfOrganisationUnit( ou.getId() );
            
            colCount = 1 + level - minOULevel;
            sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, ou.getName(), getCellFormat2() ) );
            
            OrganisationUnit orgUnit = new OrganisationUnit();
            orgUnit = ou;
            int count1=1;
            while( orgUnit.getParent() != null )
            {
                orgUnit = orgUnit.getParent();
                sheet0.addCell( new Label( colCount-count1, headerRow + rowCount + 1, orgUnit.getName(), getCellFormat2() ) );
                count1++;
            }

            List<OrganisationUnit> ouChildList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( ou.getId() ) );
            List<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers(OrganisationUnit.class, ouChildList ) );
            orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
            Map<String, String> aggDataMap = new HashMap<String, String>( reportService.getAggDataFromDataValueTableByDeAndPeriodwise( orgUnitIdsByComma, dataElementIdsByComma, periodIdsByComma ) );
            
            colCount = c1;
            int deListCount = 0;
            int indListCount = 0;
            int serviceListCount = 0;
            for( String serviceType : serviceTypeList )
            {
                if ( serviceType.equalsIgnoreCase( "I" ) )
                {
                    flag = 1;
                    selIndicator = indicatorList.get( indListCount );
                    indListCount++;
                    if ( rowCount == 1 )
                    {
                        sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                        sheet0.addCell( new Label( colCount, startRow, selIndicator.getName(), getCellFormat1() ) );
                    }
                }
                else
                {
                    flag = 2;
                    selDataElement = dataElementList.get( deListCount );
                    deListCount++;
                    if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                    {
                        selDecoc = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer.parseInt( selectedServices.get( serviceListCount ).split( ":" )[2] ) );
                        if ( rowCount == 1 )
                        {
                            sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                            sheet0.addCell( new Label( colCount, startRow, selDataElement.getName() + "-" + selDecoc.getName(), getCellFormat1() ) );
                        }
                    }
                    else
                    {
                        if ( rowCount == 1 )
                        {
                            sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                            sheet0.addCell( new Label( colCount, startRow, selDataElement.getName(), getCellFormat1() ) );
                        }
                    }
                }

                int periodCount = 0;
                for ( Date sDate : selStartPeriodList )
                {
                    Date eDate = selEndPeriodList.get( periodCount );
                
                    Collection<Integer> periodIds = new ArrayList<Integer>( periodMap.get( periodCount ) );

                    double pwdvAggValue = 0.0;
                    double pwdAggIndValue = 0.0;

                    String tempStr = "";
                    if ( flag == 1 )
                    {
                        Double numValue = 0.0;
                        Double denValue = 0.0;
                        Double indValue = 0.0;
                        for( Integer periodId : periodIds )
                        {
                            try
                            {
                                numValue += Double.parseDouble( getAggValByPeriod( selIndicator.getNumerator(), aggDataMap, periodId ) );
                            }
                            catch( Exception e )
                            {
                                numValue = 0.0;
                            }
                            Double tempDenValue = 0.0;
                            try
                            {
                                //denValue += Double.parseDouble( getAggValByPeriod( selIndicator.getDenominator(), aggDataMap, periodId ) );
                                tempDenValue = Double.parseDouble( getAggValByPeriod( selIndicator.getDenominator(), aggDataMap, periodId ) );
                            }
                            catch( Exception e )
                            {
                                tempDenValue = 1.0;
                            }
                            if ( !selIndicator.getDenominator().trim().equals( "1" ) )
                            {
                                denValue += tempDenValue;
                            }
                            else 
                            {
                                denValue = 1.0;
                            }
                        }
                        
                        try
                        {
                            if( denValue != 0.0 )
                            {
                                indValue = ( numValue / denValue ) * selIndicator.getIndicatorType().getFactor();
                            }
                            else
                            {
                                indValue = 0.0;
                            }
                        }
                        catch( Exception e )
                        {
                            indValue = 0.0;
                        }

                        pwdAggIndValue = indValue;
                        pwdAggIndValue = Math.round( pwdAggIndValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        tempStr = "" + pwdAggIndValue;
                    }
                    else if ( flag == 2 )
                    {
                        if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                        {
                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                for( Integer periodId : periodIds )
                                {
                                    tempStr = aggDataMap.get( selDataElement.getId()+":"+selDecoc.getId()+":"+periodId );
                                    if( tempStr != null )
                                    {
                                        try
                                        {
                                            pwdvAggValue += Double.parseDouble( tempStr );
                                        }
                                        catch( Exception e )
                                        {
                                        }
                                    }
                                }
                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                if ( tempPeriod != null )
                                {
                                    DataValue dataValue = dataValueService.getDataValue( ou, selDataElement, tempPeriod, selDecoc );

                                    if ( dataValue != null && dataValue.getValue() != null )
                                    {
                                        tempStr = dataValue.getValue();
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                                else
                                {
                                    tempStr = " ";
                                }
                            }
                        }
                        else
                        {
                            List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>( selDataElement.getCategoryCombo().getOptionCombos() );

                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                for( DataElementCategoryOptionCombo optionCombo : optionCombos )
                                {
                                    for( Integer periodId : periodIds )
                                    {
                                        tempStr = aggDataMap.get( selDataElement.getId()+":"+optionCombo.getId()+":"+periodId );
                                        if( tempStr != null )
                                        {
                                            try
                                            {
                                                pwdvAggValue += Double.parseDouble( tempStr );
                                            }
                                            catch( Exception e )
                                            {
                                            }
                                        }
                                    }
                                }
                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                                while ( optionComboIterator.hasNext() )
                                {
                                    DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator.next();

                                    PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                    Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                    if( tempPeriod != null )
                                    {
                                        DataValue dataValue = dataValueService.getDataValue( ou, selDataElement, tempPeriod, decoc1 );

                                        if ( dataValue != null )
                                        {
                                            tempStr += dataValue.getValue() + " : ";
                                        }
                                        else
                                        {
                                            tempStr = "  ";
                                        }
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                            }
                        }
                    }

                    if ( rowCount == 1 )
                    {
                        sheet0.addCell( new Label( colCount, startRow + 1, periodNames.get( periodCount ), getCellFormat1() ) );
                    }

                    if ( flag == 1 )
                    {
                        sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, pwdAggIndValue, getCellFormat2() ) );
                    }
                    else
                    {
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, (int) pwdvAggValue, getCellFormat2() ) );
                        }
                        else
                        {
                            sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, tempStr, getCellFormat2() ) );
                        }
                    }

                    colCount++;
                    periodCount++;
                }// Period Loop
                serviceListCount++;
            }// Service loop end
            rowCount++;
        }// Orgunit loop end
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "TabularAnalysis.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
    
        outputReportFile.deleteOnExit();
    }

    // -------------------------------------------------------------------------
    // Method for getting Selected OrgUnit data in Excel Sheet 
    //     - GenerateAggregatedData - Periodwise 
    // -------------------------------------------------------------------------
    public void generateSelectedOrgUnitData_GenerateAggregateData_Periodwise() throws Exception
    {
        int startRow = 0;
        int headerRow = 0;
        int headerCol = 0;

        //String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();
        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "TabularAnalysis", 0 );

        sheet0.mergeCells( headerCol, headerRow, headerCol, headerRow + 1 );
        sheet0.addCell( new Label( headerCol, headerRow, "Sl.No.", getCellFormat1() ) );

        for ( String ouStr : orgUnitListCB )
        {
            OrganisationUnit ou = organisationUnitService.getOrganisationUnit( Integer.parseInt( ouStr ) );
            selOUList.add( ou );
        }

        int c1 = headerCol + 1;
        sheet0.mergeCells( c1, headerRow, c1, headerRow + 1 );
        sheet0.addCell( new Label( c1, headerRow, "Facility", getCellFormat1() ) );
        c1++;
        
        /* Service Info */
        Indicator selIndicator = new Indicator();
        DataElement selDataElement = new DataElement();
        DataElementCategoryOptionCombo selDecoc = new DataElementCategoryOptionCombo();
        int flag = 0;

        /* Calculation Part */
        int rowCount = 1;
        int colCount = 0;
        for( OrganisationUnit ou : selOUList )
        {
            System.out.println( ou.getName() +" : "+ new Date());
            sheet0.addCell( new Number( headerCol, headerRow + 1 + rowCount, rowCount, getCellFormat2() ) );
            sheet0.addCell( new Label( 1, rowCount + 1, ou.getName(), getCellFormat2() ) );

            List<OrganisationUnit> ouChildList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( ou.getId() ) );
            List<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers(OrganisationUnit.class, ouChildList ) );
            orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
            Map<String, String> aggDataMap = new HashMap<String, String>( reportService.getAggDataFromDataValueTableByDeAndPeriodwise( orgUnitIdsByComma, dataElementIdsByComma, periodIdsByComma ) );
            
            colCount = c1;
            int deListCount = 0;
            int indListCount = 0;
            int serviceListCount = 0;
            for( String serviceType : serviceTypeList )
            {
                if ( serviceType.equalsIgnoreCase( "I" ) )
                {
                    flag = 1;
                    selIndicator = indicatorList.get( indListCount );
                    indListCount++;
                    if ( rowCount == 1 )
                    {
                        sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                        sheet0.addCell( new Label( colCount, startRow, selIndicator.getName(), getCellFormat1() ) );
                    }
                }
                else
                {
                    flag = 2;
                    selDataElement = dataElementList.get( deListCount );
                    deListCount++;
                    if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                    {
                        selDecoc = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer.parseInt( selectedServices.get( serviceListCount ).split( ":" )[2] ) );
                        if ( rowCount == 1 )
                        {
                            sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                            sheet0.addCell( new Label( colCount, startRow, selDataElement.getName() + "-" + selDecoc.getName(), getCellFormat1() ) );
                        }
                    }
                    else
                    {
                        if ( rowCount == 1 )
                        {
                            sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                            sheet0.addCell( new Label( colCount, startRow, selDataElement.getName(), getCellFormat1() ) );
                        }
                    }
                }

                int periodCount = 0;
                for ( Date sDate : selStartPeriodList )
                {
                    Date eDate = selEndPeriodList.get( periodCount );
                
                    Collection<Integer> periodIds = new ArrayList<Integer>( periodMap.get( periodCount ) );

                    double pwdvAggValue = 0.0;
                    double pwdAggIndValue = 0.0;

                    String tempStr = "";
                    if ( flag == 1 )
                    {
                        Double numValue = 0.0;
                        Double denValue = 0.0;
                        Double indValue = 0.0;
                        for( Integer periodId : periodIds )
                        {
                            try
                            {
                                numValue += Double.parseDouble( getAggValByPeriod( selIndicator.getNumerator(), aggDataMap, periodId ) );
                            }
                            catch( Exception e )
                            {
                                numValue = 0.0;
                            }
                            Double tempDenValue = 0.0;
                            try
                            {
                                //denValue += Double.parseDouble( getAggValByPeriod( selIndicator.getDenominator(), aggDataMap, periodId ) );
                                tempDenValue = Double.parseDouble( getAggValByPeriod( selIndicator.getDenominator(), aggDataMap, periodId ) );
                            }
                            catch( Exception e )
                            {
                                tempDenValue = 1.0;
                            }
                            if ( !selIndicator.getDenominator().trim().equals( "1" ) )
                            {
                                denValue += tempDenValue;
                            }
                            else 
                            {
                                denValue = 1.0;
                            }
                        }
                        
                        try
                        {
                            if( denValue != 0.0 )
                            {
                                indValue = ( numValue / denValue ) * selIndicator.getIndicatorType().getFactor();
                            }
                            else
                            {
                                indValue = 0.0;
                            }
                        }
                        catch( Exception e )
                        {
                            indValue = 0.0;
                        }

                        pwdAggIndValue = indValue;
                        pwdAggIndValue = Math.round( pwdAggIndValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        tempStr = "" + pwdAggIndValue;
                    }
                    else if ( flag == 2 )
                    {
                        if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                        {
                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                for( Integer periodId : periodIds )
                                {
                                    tempStr = aggDataMap.get( selDataElement.getId()+":"+selDecoc.getId()+":"+periodId );
                                    if( tempStr != null )
                                    {
                                        try
                                        {
                                            pwdvAggValue += Double.parseDouble( tempStr );
                                        }
                                        catch( Exception e )
                                        {
                                        }
                                    }
                                }
                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                if ( tempPeriod != null )
                                {
                                    DataValue dataValue = dataValueService.getDataValue( ou, selDataElement, tempPeriod, selDecoc );

                                    if ( dataValue != null && dataValue.getValue() != null )
                                    {
                                        tempStr = dataValue.getValue();
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                                else
                                {
                                    tempStr = " ";
                                }
                            }
                        }
                        else
                        {
                            List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>( selDataElement.getCategoryCombo().getOptionCombos() );

                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                for( DataElementCategoryOptionCombo optionCombo : optionCombos )
                                {
                                    for( Integer periodId : periodIds )
                                    {
                                        tempStr = aggDataMap.get( selDataElement.getId()+":"+optionCombo.getId()+":"+periodId );
                                        if( tempStr != null )
                                        {
                                            try
                                            {
                                                pwdvAggValue += Double.parseDouble( tempStr );
                                            }
                                            catch( Exception e )
                                            {
                                            }
                                        }
                                    }
                                }
                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                                while ( optionComboIterator.hasNext() )
                                {
                                    DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator.next();

                                    PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                    Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                    if( tempPeriod != null )
                                    {
                                        DataValue dataValue = dataValueService.getDataValue( ou, selDataElement, tempPeriod, decoc1 );

                                        if ( dataValue != null )
                                        {
                                            tempStr += dataValue.getValue() + " : ";
                                        }
                                        else
                                        {
                                            tempStr = "  ";
                                        }
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                            }
                        }
                    }

                    if ( rowCount == 1 )
                    {
                        sheet0.addCell( new Label( colCount, startRow + 1, periodNames.get( periodCount ), getCellFormat1() ) );
                    }

                    if ( flag == 1 )
                    {
                        sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, pwdAggIndValue, getCellFormat2() ) );
                    }
                    else
                    {
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, (int) pwdvAggValue, getCellFormat2() ) );
                        }
                        else
                        {
                            sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, tempStr, getCellFormat2() ) );
                        }
                    }

                    colCount++;
                    periodCount++;
                }// Period Loop
                serviceListCount++;
            }// Service loop end
            rowCount++;
        }// Orgunit loop end
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "TabularAnalysis.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
    
        outputReportFile.deleteOnExit();
    }
    
    // -------------------------------------------------------------------------
    // Method for getting OrgUnit Level wise data in Excel Sheet 
    //     - UseExistingData - Periodwise 
    // -------------------------------------------------------------------------
    public void generateOrgUnitLevelData_UseExisting_Periodwise() throws Exception
    {
        int startRow = 0;
        int headerRow = 0;
        int headerCol = 0;

        //String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();
        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "TabularAnalysis", 0 );

        sheet0.mergeCells( headerCol, headerRow, headerCol, headerRow + 1 );
        sheet0.addCell( new Label( headerCol, headerRow, "Sl.No.", getCellFormat1() ) );

        selOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
        selOUList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selOrgUnit.getId() ) );

        System.out.println( "Before getting orgunitlevelmap "+new Date() );
        Map<Integer, Integer> orgunitLevelMap = new HashMap<Integer, Integer>( reportService.getOrgunitLevelMap() );
        System.out.println( "After getting orgunitlevelmap "+new Date() );
    
        Iterator<OrganisationUnit> ouIterator = selOUList.iterator();
        while ( ouIterator.hasNext() )
        {
            OrganisationUnit orgU = ouIterator.next();
            
            Integer level = orgunitLevelMap.get( orgU.getId() );
            if( level == null )
                level = organisationUnitService.getLevelOfOrganisationUnit( orgU.getId() );
            if ( level > orgUnitLevelCB )
            {
                ouIterator.remove();
            }
        }

        int minOULevel = 1;
        int maxOuLevel = 1;
        if ( selOUList != null && selOUList.size() > 0 )
        {
            minOULevel = organisationUnitService.getLevelOfOrganisationUnit( selOUList.get( 0 ).getId() );
        }
        maxOuLevel = orgUnitLevelCB;

        int c1 = headerCol + 1;
        for ( int i = minOULevel; i <= maxOuLevel; i++ )
        {
            sheet0.mergeCells( c1, headerRow, c1, headerRow + 1 );
            sheet0.addCell( new Label( c1, headerRow, "Level " + i, getCellFormat1() ) );
            c1++;
        }

        /* Service Info */
        Indicator selIndicator = new Indicator();
        DataElement selDataElement = new DataElement();
        DataElementCategoryOptionCombo selDecoc = new DataElementCategoryOptionCombo();
        int flag = 0;

        List<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers(OrganisationUnit.class, selOUList ) );
        orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
        
        System.out.println( "Before getting aggdatamap "+new Date() );
        Map<String, String> aggDataMap = new HashMap<String, String>( reportService.getResultDataValueFromAggregateTable( orgUnitIdsByComma, dataElementIdsByComma, periodIdsByComma ) );
        System.out.println( "Before getting aggdatamap "+new Date() );
    
        /* Calculation Part */
        int rowCount = 1;
        int colCount = 0;
        for( OrganisationUnit ou : selOUList )
        {
            System.out.println("Entered into orgunitloop :"+new Date());
            sheet0.addCell( new Number( headerCol, headerRow + 1 + rowCount, rowCount, getCellFormat2() ) );
            
            Integer level = orgunitLevelMap.get( ou.getId() );
            if( level == null )
                level = organisationUnitService.getLevelOfOrganisationUnit( ou.getId() );
            
            colCount = 1 + level - minOULevel;
            sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, ou.getName(), getCellFormat2() ) );

            colCount = c1;
            int deListCount = 0;
            int indListCount = 0;
            int serviceListCount = 0;
            for( String serviceType : serviceTypeList )
            {
                if ( serviceType.equalsIgnoreCase( "I" ) )
                {
                    flag = 1;
                    selIndicator = indicatorList.get( indListCount );
                    indListCount++;
                    if ( rowCount == 1 )
                    {
                        sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                        sheet0.addCell( new Label( colCount, startRow, selIndicator.getName(), getCellFormat1() ) );
                    }
                }
                else
                {
                    flag = 2;
                    selDataElement = dataElementList.get( deListCount );
                    deListCount++;
                    if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                    {
                        selDecoc = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer.parseInt( selectedServices.get( serviceListCount ).split( ":" )[2] ) );
                        if ( rowCount == 1 )
                        {
                            sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                            sheet0.addCell( new Label( colCount, startRow, selDataElement.getName() + "-" + selDecoc.getName(), getCellFormat1() ) );
                        }
                    }
                    else
                    {
                        if ( rowCount == 1 )
                        {
                            sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                            sheet0.addCell( new Label( colCount, startRow, selDataElement.getName(), getCellFormat1() ) );
                        }
                    }
                }

                int periodCount = 0;
                for ( Date sDate : selStartPeriodList )
                {
                    Date eDate = selEndPeriodList.get( periodCount );
                
                    Collection<Integer> periodIds = new ArrayList<Integer>( periodMap.get( periodCount ) );
                    System.out.println( periodIds );

                    double pwdvAggValue = 0.0;
                    double pwdAggIndValue = 0.0;

                    String tempStr = "";
                    if ( flag == 1 )
                    {
                        Double numValue = 0.0;
                        Double denValue = 0.0;
                        Double indValue = 0.0;
                        for( Integer periodId : periodIds )
                        {
                            try
                            {
                                numValue += Double.parseDouble( getAggVal( selIndicator.getNumerator(), aggDataMap, ou.getId(), periodId ) );
                            }
                            catch( Exception e )
                            {
                                numValue = 0.0;
                            }
                            
                            Double tempDenValue = 0.0;
                            try
                            {
                                //denValue += Double.parseDouble( getAggVal( selIndicator.getDenominator(), aggDataMap, ou.getId(), periodId ) );
                                tempDenValue = Double.parseDouble( getAggVal( selIndicator.getDenominator(), aggDataMap, ou.getId(), periodId ) );
                            }
                            catch( Exception e )
                            {
                                tempDenValue = 1.0;
                            }
                            if ( !selIndicator.getDenominator().trim().equals( "1" ) )
                            {
                                denValue += tempDenValue;
                            }
                            else 
                            {
                                denValue = 1.0;
                            }
                        }
                        
                        try
                        {
                            if( denValue != 0.0 )
                            {
                                indValue = ( numValue / denValue ) * selIndicator.getIndicatorType().getFactor();
                            }
                            else
                            {
                                indValue = 0.0;
                            }
                        }
                        catch( Exception e )
                        {
                            indValue = 0.0;
                        }

                        pwdAggIndValue = indValue;
                        pwdAggIndValue = Math.round( pwdAggIndValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        tempStr = "" + pwdAggIndValue;
                    }
                    else if ( flag == 2 )
                    {
                        if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                        {
                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                for( Integer periodId : periodIds )
                                {
                                    tempStr = aggDataMap.get( ou.getId()+":"+selDataElement.getId()+":"+selDecoc.getId()+":"+periodId );
                                    if( tempStr != null )
                                    {
                                        try
                                        {
                                            pwdvAggValue += Double.parseDouble( tempStr );
                                        }
                                        catch( Exception e )
                                        {
                                        }
                                    }
                                }
                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                if ( tempPeriod != null )
                                {
                                    DataValue dataValue = dataValueService.getDataValue( ou, selDataElement, tempPeriod, selDecoc );

                                    if ( dataValue != null && dataValue.getValue() != null )
                                    {
                                        tempStr = dataValue.getValue();
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                                else
                                {
                                    tempStr = " ";
                                }
                            }
                        }
                        else
                        {
                            List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>( selDataElement.getCategoryCombo().getOptionCombos() );

                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                for( DataElementCategoryOptionCombo optionCombo : optionCombos )
                                {
                                    for( Integer periodId : periodIds )
                                    {
                                        tempStr = aggDataMap.get( ou.getId()+":"+selDataElement.getId()+":"+optionCombo.getId()+":"+periodId );
                                        if( tempStr != null )
                                        {
                                            try
                                            {
                                                pwdvAggValue += Double.parseDouble( tempStr );
                                            }
                                            catch( Exception e )
                                            {
                                            }
                                        }
                                    }
                                }
                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                                while ( optionComboIterator.hasNext() )
                                {
                                    DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator.next();

                                    PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                    Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                    if( tempPeriod != null )
                                    {
                                        DataValue dataValue = dataValueService.getDataValue( ou, selDataElement, tempPeriod, decoc1 );

                                        if ( dataValue != null )
                                        {
                                            tempStr += dataValue.getValue() + " : ";
                                        }
                                        else
                                        {
                                            tempStr = "  ";
                                        }
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                            }
                        }
                    }

                    if ( rowCount == 1 )
                    {
                        sheet0.addCell( new Label( colCount, startRow + 1, periodNames.get( periodCount ), getCellFormat1() ) );
                    }

                    if ( flag == 1 )
                    {
                        sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, pwdAggIndValue, getCellFormat2() ) );
                    }
                    else
                    {
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, (int) pwdvAggValue, getCellFormat2() ) );
                        }
                        else
                        {
                            sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, tempStr, getCellFormat2() ) );
                        }
                    }

                    colCount++;
                    periodCount++;
                }// Period Loop
                serviceListCount++;
            }// Service loop end
            rowCount++;
        }// Orgunit loop end
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "TabularAnalysis.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
    
        outputReportFile.deleteOnExit();
    }

    // -------------------------------------------------------------------------
    // Method for getting OrgUnit Group wise data in Excel Sheet 
    //     - UseExistingData - Periodwise 
    // -------------------------------------------------------------------------
    public void generateOrgUnitGroupData_UseExisting_Periodwise() throws Exception
    {
        int startRow = 0;
        int headerRow = 0;
        int headerCol = 0;

        //String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();
        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "TabularAnalysis", 0 );

        sheet0.mergeCells( headerCol, headerRow, headerCol, headerRow + 1 );
        sheet0.addCell( new Label( headerCol, headerRow, "Sl.No.", getCellFormat1() ) );

        selOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
        selOUList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selOrgUnit.getId() ) );
        OrganisationUnitGroup selOrgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgUnitLevelCB );
        List<OrganisationUnit> orgUnitList1 = new ArrayList<OrganisationUnit>( selOrgUnitGroup.getMembers() );
        selOUList.retainAll( orgUnitList1 );
        
        int minOULevel = 1;
        int maxOULevel = organisationUnitService.getNumberOfOrganisationalLevels();

        int c1 = headerCol + 1;

        for( int i = minOULevel; i <= maxOULevel; i++ )
        {
            sheet0.mergeCells( c1, headerRow, c1, headerRow + 1 );
            sheet0.addCell( new Label( c1, headerRow, "Level- "+i, getCellFormat1() ) );
            c1++;
        }

        Map<Integer, Integer> orgunitLevelMap = new HashMap<Integer, Integer>( reportService.getOrgunitLevelMap() );
    
        /* Service Info */
        Indicator selIndicator = new Indicator();
        DataElement selDataElement = new DataElement();
        DataElementCategoryOptionCombo selDecoc = new DataElementCategoryOptionCombo();
        int flag = 0;

        List<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers(OrganisationUnit.class, selOUList ) );
        orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
        
        System.out.println( "Before getting aggdatamap "+new Date() );
        Map<String, String> aggDataMap = new HashMap<String, String>( reportService.getResultDataValueFromAggregateTable( orgUnitIdsByComma, dataElementIdsByComma, periodIdsByComma ) );
        System.out.println( "Before getting aggdatamap "+new Date() );
    
        /* Calculation Part */
        int rowCount = 1;
        int colCount = 0;
        for( OrganisationUnit ou : selOUList )
        {
            System.out.println("Entered into orgunitloop :"+new Date());
            sheet0.addCell( new Number( headerCol, headerRow + 1 + rowCount, rowCount, getCellFormat2() ) );
            
            Integer level = orgunitLevelMap.get( ou.getId() );
            if( level == null )
                level = organisationUnitService.getLevelOfOrganisationUnit( ou.getId() );
            
            colCount = 1 + level - minOULevel;
            sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, ou.getName(), getCellFormat2() ) );
            
            OrganisationUnit orgUnit = new OrganisationUnit();
            orgUnit = ou;
            int count1=1;
            while( orgUnit.getParent() != null )
            {
                orgUnit = orgUnit.getParent();
                sheet0.addCell( new Label( colCount-count1, rowCount + 1, orgUnit.getName(), getCellFormat2() ) );
                count1++;
            }

            colCount = c1;
            int deListCount = 0;
            int indListCount = 0;
            int serviceListCount = 0;
            for( String serviceType : serviceTypeList )
            {
                if ( serviceType.equalsIgnoreCase( "I" ) )
                {
                    flag = 1;
                    selIndicator = indicatorList.get( indListCount );
                    indListCount++;
                    if ( rowCount == 1 )
                    {
                        sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                        sheet0.addCell( new Label( colCount, startRow, selIndicator.getName(), getCellFormat1() ) );
                    }
                }
                else
                {
                    flag = 2;
                    selDataElement = dataElementList.get( deListCount );
                    deListCount++;
                    if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                    {
                        selDecoc = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer.parseInt( selectedServices.get( serviceListCount ).split( ":" )[2] ) );
                        if ( rowCount == 1 )
                        {
                            sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                            sheet0.addCell( new Label( colCount, startRow, selDataElement.getName() + "-" + selDecoc.getName(), getCellFormat1() ) );
                        }
                    }
                    else
                    {
                        if ( rowCount == 1 )
                        {
                            sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                            sheet0.addCell( new Label( colCount, startRow, selDataElement.getName(), getCellFormat1() ) );
                        }
                    }
                }

                int periodCount = 0;
                for ( Date sDate : selStartPeriodList )
                {
                    Date eDate = selEndPeriodList.get( periodCount );
                
                    Collection<Integer> periodIds = new ArrayList<Integer>( periodMap.get( periodCount ) );
                    System.out.println( periodIds );

                    double pwdvAggValue = 0.0;
                    double pwdAggIndValue = 0.0;

                    String tempStr = "";
                    if ( flag == 1 )
                    {
                        Double numValue = 0.0;
                        Double denValue = 0.0;
                        Double indValue = 0.0;
                        for( Integer periodId : periodIds )
                        {
                            try
                            {
                                numValue += Double.parseDouble( getAggVal( selIndicator.getNumerator(), aggDataMap, ou.getId(), periodId ) );
                            }
                            catch( Exception e )
                            {
                                numValue = 0.0;
                            }
                            Double tempDenValue = 0.0;
                            try
                            {
                                //denValue += Double.parseDouble( getAggVal( selIndicator.getDenominator(), aggDataMap, ou.getId(), periodId ) );
                                tempDenValue = Double.parseDouble( getAggVal( selIndicator.getDenominator(), aggDataMap, ou.getId(), periodId ) );
                            }
                            catch( Exception e )
                            {
                                tempDenValue = 1.0;
                            }
                            if ( !selIndicator.getDenominator().trim().equals( "1" ) )
                            {
                                denValue += tempDenValue;
                            }
                            else 
                            {
                                denValue = 1.0;
                            }
                        }
                        
                        try
                        {
                            if( denValue != 0.0 )
                            {
                                indValue = ( numValue / denValue ) * selIndicator.getIndicatorType().getFactor();
                            }
                            else
                            {
                                indValue = 0.0;
                            }
                        }
                        catch( Exception e )
                        {
                            indValue = 0.0;
                        }

                        pwdAggIndValue = indValue;
                        pwdAggIndValue = Math.round( pwdAggIndValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        tempStr = "" + pwdAggIndValue;
                    }
                    else if ( flag == 2 )
                    {
                        if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                        {
                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                for( Integer periodId : periodIds )
                                {
                                    tempStr = aggDataMap.get( ou.getId()+":"+selDataElement.getId()+":"+selDecoc.getId()+":"+periodId );
                                    if( tempStr != null )
                                    {
                                        try
                                        {
                                            pwdvAggValue += Double.parseDouble( tempStr );
                                        }
                                        catch( Exception e )
                                        {
                                        }
                                    }
                                }
                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                if ( tempPeriod != null )
                                {
                                    DataValue dataValue = dataValueService.getDataValue( ou, selDataElement, tempPeriod, selDecoc );

                                    if ( dataValue != null && dataValue.getValue() != null )
                                    {
                                        tempStr = dataValue.getValue();
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                                else
                                {
                                    tempStr = " ";
                                }
                            }
                        }
                        else
                        {
                            List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>( selDataElement.getCategoryCombo().getOptionCombos() );

                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                for( DataElementCategoryOptionCombo optionCombo : optionCombos )
                                {
                                    for( Integer periodId : periodIds )
                                    {
                                        tempStr = aggDataMap.get( ou.getId()+":"+selDataElement.getId()+":"+optionCombo.getId()+":"+periodId );
                                        if( tempStr != null )
                                        {
                                            try
                                            {
                                                pwdvAggValue += Double.parseDouble( tempStr );
                                            }
                                            catch( Exception e )
                                            {
                                            }
                                        }
                                    }
                                }
                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                                while ( optionComboIterator.hasNext() )
                                {
                                    DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator.next();

                                    PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                    Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                    if( tempPeriod != null )
                                    {
                                        DataValue dataValue = dataValueService.getDataValue( ou, selDataElement, tempPeriod, decoc1 );

                                        if ( dataValue != null )
                                        {
                                            tempStr += dataValue.getValue() + " : ";
                                        }
                                        else
                                        {
                                            tempStr = "  ";
                                        }
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                            }
                        }
                    }

                    if ( rowCount == 1 )
                    {
                        sheet0.addCell( new Label( colCount, startRow + 1, periodNames.get( periodCount ), getCellFormat1() ) );
                    }

                    if ( flag == 1 )
                    {
                        sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, pwdAggIndValue, getCellFormat2() ) );
                    }
                    else
                    {
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, (int) pwdvAggValue, getCellFormat2() ) );
                        }
                        else
                        {
                            sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, tempStr, getCellFormat2() ) );
                        }
                    }

                    colCount++;
                    periodCount++;
                }// Period Loop
                serviceListCount++;
            }// Service loop end
            rowCount++;
        }// Orgunit loop end
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "TabularAnalysis.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
    
        outputReportFile.deleteOnExit();
    }
    
    // -------------------------------------------------------------------------
    // Method for getting Selected OrgUnit Data in Excel Sheet 
    //     - UseExistingData - Periodwise 
    // -------------------------------------------------------------------------
    public void generateSelectedOrgUnitData_UseExisting_Periodwise() throws Exception
    {
        int startRow = 0;
        int headerRow = 0;
        int headerCol = 0;

        //String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();
        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "TabularAnalysis", 0 );

        sheet0.mergeCells( headerCol, headerRow, headerCol, headerRow + 1 );
        sheet0.addCell( new Label( headerCol, headerRow, "Sl.No.", getCellFormat1() ) );

        for ( String ouStr : orgUnitListCB )
        {
            OrganisationUnit ou = organisationUnitService.getOrganisationUnit( Integer.parseInt( ouStr ) );
            selOUList.add( ou );
        }

        int c1 = headerCol + 1;
        sheet0.mergeCells( c1, headerRow, c1, headerRow + 1 );
        sheet0.addCell( new Label( c1, headerRow, "Facility", getCellFormat1() ) );
        c1++;

        /* Service Info */
        Indicator selIndicator = new Indicator();
        DataElement selDataElement = new DataElement();
        DataElementCategoryOptionCombo selDecoc = new DataElementCategoryOptionCombo();
        int flag = 0;

        List<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers(OrganisationUnit.class, selOUList ) );
        orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
        
        System.out.println( "Before getting aggdatamap "+new Date() );
        Map<String, String> aggDataMap = new HashMap<String, String>( reportService.getResultDataValueFromAggregateTable( orgUnitIdsByComma, dataElementIdsByComma, periodIdsByComma ) );
        System.out.println( "Before getting aggdatamap "+new Date() );
    
        /* Calculation Part */
        int rowCount = 1;
        int colCount = 0;
        for( OrganisationUnit ou : selOUList )
        {
            System.out.println(ou.getName() + " : " +new Date());
            sheet0.addCell( new Number( headerCol, headerRow + 1 + rowCount, rowCount, getCellFormat2() ) );
            sheet0.addCell( new Label( 1, rowCount + 1, ou.getName(), getCellFormat2() ) );
            
            colCount = c1;
            int deListCount = 0;
            int indListCount = 0;
            int serviceListCount = 0;
            for( String serviceType : serviceTypeList )
            {
                if ( serviceType.equalsIgnoreCase( "I" ) )
                {
                    flag = 1;
                    selIndicator = indicatorList.get( indListCount );
                    indListCount++;
                    if ( rowCount == 1 )
                    {
                        sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                        sheet0.addCell( new Label( colCount, startRow, selIndicator.getName(), getCellFormat1() ) );
                    }
                }
                else
                {
                    flag = 2;
                    selDataElement = dataElementList.get( deListCount );
                    deListCount++;
                    if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                    {
                        selDecoc = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer.parseInt( selectedServices.get( serviceListCount ).split( ":" )[2] ) );
                        if ( rowCount == 1 )
                        {
                            sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                            sheet0.addCell( new Label( colCount, startRow, selDataElement.getName() + "-" + selDecoc.getName(), getCellFormat1() ) );
                        }
                    }
                    else
                    {
                        if ( rowCount == 1 )
                        {
                            sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                            sheet0.addCell( new Label( colCount, startRow, selDataElement.getName(), getCellFormat1() ) );
                        }
                    }
                }

                int periodCount = 0;
                for ( Date sDate : selStartPeriodList )
                {
                    Date eDate = selEndPeriodList.get( periodCount );
                
                    Collection<Integer> periodIds = new ArrayList<Integer>( periodMap.get( periodCount ) );
                    System.out.println( periodIds );

                    double pwdvAggValue = 0.0;
                    double pwdAggIndValue = 0.0;

                    String tempStr = "";
                    if ( flag == 1 )
                    {
                        Double numValue = 0.0;
                        Double denValue = 0.0;
                        Double indValue = 0.0;
                        for( Integer periodId : periodIds )
                        {
                            try
                            {
                                numValue += Double.parseDouble( getAggVal( selIndicator.getNumerator(), aggDataMap, ou.getId(), periodId ) );
                            }
                            catch( Exception e )
                            {
                                numValue = 0.0;
                            }
                            Double tempDenValue = 0.0;
                            try
                            {
                                //denValue += Double.parseDouble( getAggVal( selIndicator.getDenominator(), aggDataMap, ou.getId(), periodId ) );                                
                                tempDenValue = Double.parseDouble( getAggVal( selIndicator.getDenominator(), aggDataMap, ou.getId(), periodId ) );
                            }
                            catch( Exception e )
                            {
                                tempDenValue = 1.0;
                            }
                            if( !selIndicator.getDenominator().trim().equals( "1" ) )
                            {
                                denValue += tempDenValue;
                            }
                            else 
                            {
                                denValue = 1.0;
                            }
                        }
                        
                        try
                        {
                            if( denValue != 0.0 )
                            {
                                indValue = ( numValue / denValue ) * selIndicator.getIndicatorType().getFactor();
                            }
                            else
                            {
                                indValue = 0.0;
                            }
                        }
                        catch( Exception e )
                        {
                            indValue = 0.0;
                        }

                        pwdAggIndValue = indValue;
                        pwdAggIndValue = Math.round( pwdAggIndValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        tempStr = "" + pwdAggIndValue;
                    }
                    else if ( flag == 2 )
                    {
                        if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                        {
                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                for( Integer periodId : periodIds )
                                {
                                    tempStr = aggDataMap.get( ou.getId()+":"+selDataElement.getId()+":"+selDecoc.getId()+":"+periodId );
                                    if( tempStr != null )
                                    {
                                        try
                                        {
                                            pwdvAggValue += Double.parseDouble( tempStr );
                                        }
                                        catch( Exception e )
                                        {
                                        }
                                    }
                                }
                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                if ( tempPeriod != null )
                                {
                                    DataValue dataValue = dataValueService.getDataValue( ou, selDataElement, tempPeriod, selDecoc );

                                    if ( dataValue != null && dataValue.getValue() != null )
                                    {
                                        tempStr = dataValue.getValue();
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                                else
                                {
                                    tempStr = " ";
                                }
                            }
                        }
                        else
                        {
                            List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>( selDataElement.getCategoryCombo().getOptionCombos() );

                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                for( DataElementCategoryOptionCombo optionCombo : optionCombos )
                                {
                                    for( Integer periodId : periodIds )
                                    {
                                        tempStr = aggDataMap.get( ou.getId()+":"+selDataElement.getId()+":"+optionCombo.getId()+":"+periodId );
                                        if( tempStr != null )
                                        {
                                            try
                                            {
                                                pwdvAggValue += Double.parseDouble( tempStr );
                                            }
                                            catch( Exception e )
                                            {
                                            }
                                        }
                                    }
                                }
                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                                while ( optionComboIterator.hasNext() )
                                {
                                    DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator.next();

                                    PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                    Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                    if( tempPeriod != null )
                                    {
                                        DataValue dataValue = dataValueService.getDataValue( ou, selDataElement, tempPeriod, decoc1 );

                                        if ( dataValue != null )
                                        {
                                            tempStr += dataValue.getValue() + " : ";
                                        }
                                        else
                                        {
                                            tempStr = "  ";
                                        }
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                            }
                        }
                    }

                    if ( rowCount == 1 )
                    {
                        sheet0.addCell( new Label( colCount, startRow + 1, periodNames.get( periodCount ), getCellFormat1() ) );
                    }

                    if ( flag == 1 )
                    {
                        sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, pwdAggIndValue, getCellFormat2() ) );
                    }
                    else
                    {
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, (int) pwdvAggValue, getCellFormat2() ) );
                        }
                        else
                        {
                            sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, tempStr, getCellFormat2() ) );
                        }
                    }

                    colCount++;
                    periodCount++;
                }// Period Loop
                serviceListCount++;
            }// Service loop end
            rowCount++;
        }// Orgunit loop end
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "TabularAnalysis.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
    
        outputReportFile.deleteOnExit();
    }
    
    // -------------------------------------------------------------------------
    // Method for getting OrgUnit Level wise data in Excel Sheet 
    //     - UseExistingData - Aggregation of Periods
    // -------------------------------------------------------------------------
    public void generateOrgUnitLevelData_UseExisting_AggPeriods() throws Exception
    {
        int headerRow = 0;
        int headerCol = 0;

        //String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();
        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "TabularAnalysis", 0 );

        sheet0.addCell( new Label( headerCol, headerRow, "Sl.No.", getCellFormat1() ) );

        selOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
        selOUList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selOrgUnit.getId() ) );

        System.out.println( "Before getting orgunitlevelmap "+new Date() );
        Map<Integer, Integer> orgunitLevelMap = new HashMap<Integer, Integer>( reportService.getOrgunitLevelMap() );
        System.out.println( "After getting orgunitlevelmap "+new Date() );
    
        Iterator<OrganisationUnit> ouIterator = selOUList.iterator();
        while ( ouIterator.hasNext() )
        {
            OrganisationUnit orgU = ouIterator.next();
            
            Integer level = orgunitLevelMap.get( orgU.getId() );
            if( level == null )
                level = organisationUnitService.getLevelOfOrganisationUnit( orgU.getId() );
            if ( level > orgUnitLevelCB )
            {
                ouIterator.remove();
            }
        }

        int minOULevel = 1;
        int maxOuLevel = 1;
        if ( selOUList != null && selOUList.size() > 0 )
        {
            minOULevel = organisationUnitService.getLevelOfOrganisationUnit( selOUList.get( 0 ).getId() );
        }
        maxOuLevel = orgUnitLevelCB;

        int c1 = headerCol + 1;
        for ( int i = minOULevel; i <= maxOuLevel; i++ )
        {
            sheet0.addCell( new Label( c1, headerRow, "Level " + i, getCellFormat1() ) );
            c1++;
        }

        /* Service Info */
        Indicator selIndicator = new Indicator();
        DataElement selDataElement = new DataElement();
        DataElementCategoryOptionCombo selDecoc = new DataElementCategoryOptionCombo();
        int flag = 0;

        List<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers(OrganisationUnit.class, selOUList ) );
        orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
        
        System.out.println( "Before getting aggdatamap "+new Date() );
        Map<String, String> aggDataMap = new HashMap<String, String>( reportService.getResultDataValueFromAggregateTableByPeriodAgg( orgUnitIdsByComma, dataElementIdsByComma, periodIdsByComma ) );
        System.out.println( "After getting aggdatamap "+new Date() );
    
        /* Calculation Part */
        int rowCount = 1;
        int colCount = 0;
        for( OrganisationUnit ou : selOUList )
        {
            System.out.println("Entered into orgunitloop :"+new Date());
            sheet0.addCell( new Number( headerCol, headerRow + rowCount, rowCount, getCellFormat2() ) );
            
            Integer level = orgunitLevelMap.get( ou.getId() );
            if( level == null )
                level = organisationUnitService.getLevelOfOrganisationUnit( ou.getId() );
            
            colCount = 1 + level - minOULevel;
            sheet0.addCell( new Label( colCount, headerRow + rowCount, ou.getName(), getCellFormat2() ) );

            colCount = c1;
            int deListCount = 0;
            int indListCount = 0;
            int serviceListCount = 0;
            for( String serviceType : serviceTypeList )
            {
                String tempStr = "";
                Double indValue = 0.0;
                Double dataValue = 0.0;
                
                if ( serviceType.equalsIgnoreCase( "I" ) )
                {
                    Double numValue = 0.0;
                    Double denValue = 0.0;

                    flag = 1;
                    selIndicator = indicatorList.get( indListCount );
                    indListCount++;
                    if ( rowCount == 1 )
                    {
                        sheet0.addCell( new Label( colCount, headerRow, selIndicator.getName(), getCellFormat1() ) );
                    }
                    
                    try
                    {
                        numValue = Double.parseDouble( getAggValByOrgUnit( selIndicator.getNumerator(), aggDataMap, ou.getId() ) );
                    }
                    catch( Exception e )
                    {
                        numValue = 0.0;
                    }
                    if( !selIndicator.getDenominator().trim().equals( "1" ) )
                    {
                        try
                        {
                            denValue = Double.parseDouble( getAggValByOrgUnit( selIndicator.getDenominator(), aggDataMap, ou.getId() ) );
                        }
                        catch( Exception e )
                        {
                            denValue = 1.0;
                        }
                    }
                    else 
                    {
                        denValue = 1.0;
                    }
                    /*
                    try
                    {
                        denValue = Double.parseDouble( getAggValByOrgUnit( selIndicator.getDenominator(), aggDataMap, ou.getId() ) );
                    }
                    catch( Exception e )
                    {
                    }
                    */
                    try
                    {
                        if( denValue != 0.0 )
                        {
                            indValue = ( numValue / denValue ) * selIndicator.getIndicatorType().getFactor();
                        }
                        else
                        {
                            indValue = 0.0;
                        }
                    }
                    catch( Exception e )
                    {
                        indValue = 0.0;
                    }
                    
                    indValue = Math.round( indValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                }
                else
                {
                    flag = 2;
                    selDataElement = dataElementList.get( deListCount );
                    deListCount++;
                    if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                    {
                        selDecoc = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer.parseInt( selectedServices.get( serviceListCount ).split( ":" )[2] ) );
                        if ( rowCount == 1 )
                        {
                            sheet0.addCell( new Label( colCount, headerRow, selDataElement.getName() + "-" + selDecoc.getName(), getCellFormat1() ) );
                        }
                        
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            tempStr = aggDataMap.get( ou.getId()+":"+selDataElement.getId()+":"+selDecoc.getId() );
                            if( tempStr != null )
                            {
                                try
                                {
                                    dataValue = Double.parseDouble( tempStr );
                                }
                                catch( Exception e )
                                {
                                    dataValue = 0.0;
                                }
                            }
                        }
                        else
                        {
                            dataValue = 0.0;
                        }
                    }
                    else
                    {
                        if ( rowCount == 1 )
                        {
                            sheet0.addCell( new Label( colCount, headerRow, selDataElement.getName(), getCellFormat1() ) );
                        }
                        List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>( selDataElement.getCategoryCombo().getOptionCombos() );
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            for( DataElementCategoryOptionCombo optionCombo : optionCombos )
                            {
                                tempStr = aggDataMap.get( ou.getId()+":"+selDataElement.getId()+":"+optionCombo.getId() );
                                if( tempStr != null )
                                {
                                    try
                                    {
                                        dataValue += Double.parseDouble( tempStr );
                                    }
                                    catch( Exception e )
                                    {
                                    }
                                }
                            }
                        }
                        else
                        {
                            dataValue = 0.0;
                        }
                    }
                }

                if ( flag == 1 )
                {
                    sheet0.addCell( new Number( colCount, headerRow + rowCount, indValue, getCellFormat2() ) );
                }
                else
                {
                    sheet0.addCell( new Number( colCount, headerRow + rowCount, dataValue, getCellFormat2() ) );
                }

                colCount++;
                serviceListCount++;
            }// Service loop end
            rowCount++;
        }// Orgunit loop end
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "TabularAnalysis.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
    
        outputReportFile.deleteOnExit();    
    }

    // -------------------------------------------------------------------------
    // Method for getting OrgUnit Group wise data in Excel Sheet 
    //     - UseExistingData - Aggregation of Periods
    // -------------------------------------------------------------------------
    public void generateOrgUnitGroupData_UseExisting_AggPeriods() throws Exception
    {
        int headerRow = 0;
        int headerCol = 0;

        //String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();
        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "TabularAnalysis", 0 );

        sheet0.addCell( new Label( headerCol, headerRow, "Sl.No.", getCellFormat1() ) );

        selOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
        selOUList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selOrgUnit.getId() ) );

        OrganisationUnitGroup selOrgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgUnitLevelCB );
        List<OrganisationUnit> orgUnitList1 = new ArrayList<OrganisationUnit>( selOrgUnitGroup.getMembers() );

        selOUList.retainAll( orgUnitList1 );

        int minOULevel = 1;
        int maxOULevel = organisationUnitService.getNumberOfOrganisationalLevels();

        int c1 = headerCol + 1;

        for( int i = minOULevel; i <= maxOULevel; i++ )
        {
            sheet0.addCell( new Label( c1, headerRow, "Level- "+i, getCellFormat1() ) );
            c1++;
        }

        Map<Integer, Integer> orgunitLevelMap = new HashMap<Integer, Integer>( reportService.getOrgunitLevelMap() );


        /* Service Info */
        Indicator selIndicator = new Indicator();
        DataElement selDataElement = new DataElement();
        DataElementCategoryOptionCombo selDecoc = new DataElementCategoryOptionCombo();
        int flag = 0;

        List<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers(OrganisationUnit.class, selOUList ) );
        orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
        
        System.out.println( "Before getting aggdatamap "+new Date() );
        Map<String, String> aggDataMap = new HashMap<String, String>( reportService.getResultDataValueFromAggregateTableByPeriodAgg( orgUnitIdsByComma, dataElementIdsByComma, periodIdsByComma ) );
        System.out.println( "After getting aggdatamap "+new Date() );
    
        /* Calculation Part */
        int rowCount = 1;
        int colCount = 0;
        for( OrganisationUnit ou : selOUList )
        {
            System.out.println("Entered into orgunitloop :"+new Date());
            sheet0.addCell( new Number( headerCol, headerRow + rowCount, rowCount, getCellFormat2() ) );
            
            Integer level = orgunitLevelMap.get( ou.getId() );
            if( level == null )
                level = organisationUnitService.getLevelOfOrganisationUnit( ou.getId() );
            
            colCount = 1 + level - minOULevel;
            sheet0.addCell( new Label( colCount, headerRow + rowCount, ou.getName(), getCellFormat2() ) );
            
            OrganisationUnit orgUnit = new OrganisationUnit();
            orgUnit = ou;
            int count1=1;
            while( orgUnit.getParent() != null )
            {
                orgUnit = orgUnit.getParent();
                sheet0.addCell( new Label( colCount-count1, headerRow + rowCount, orgUnit.getName(), getCellFormat2() ) );
                count1++;
            }

            colCount = c1;
            int deListCount = 0;
            int indListCount = 0;
            int serviceListCount = 0;
            for( String serviceType : serviceTypeList )
            {
                String tempStr = "";
                Double indValue = 0.0;
                Double dataValue = 0.0;
                
                if ( serviceType.equalsIgnoreCase( "I" ) )
                {
                    Double numValue = 0.0;
                    Double denValue = 0.0;

                    flag = 1;
                    selIndicator = indicatorList.get( indListCount );
                    indListCount++;
                    if ( rowCount == 1 )
                    {
                        sheet0.addCell( new Label( colCount, headerRow, selIndicator.getName(), getCellFormat1() ) );
                    }
                    
                    try
                    {
                        numValue = Double.parseDouble( getAggValByOrgUnit( selIndicator.getNumerator(), aggDataMap, ou.getId() ) );
                    }
                    catch( Exception e )
                    {
                        numValue = 0.0;
                    }
                    if( !selIndicator.getDenominator().trim().equals( "1" ) )
                    {
                        try
                        {
                            denValue = Double.parseDouble( getAggValByOrgUnit( selIndicator.getDenominator(), aggDataMap, ou.getId() ) );
                        }
                        catch( Exception e )
                        {
                            denValue = 1.0;
                        }
                    }
                    else 
                    {
                        denValue = 1.0;
                    }
                    /*
                    try
                    {
                        denValue = Double.parseDouble( getAggValByOrgUnit( selIndicator.getDenominator(), aggDataMap, ou.getId() ) );
                    }
                    catch( Exception e )
                    {
                    }
                    */
                    try
                    {
                        if( denValue != 0.0 )
                        {
                            indValue = ( numValue / denValue ) * selIndicator.getIndicatorType().getFactor();
                        }
                        else
                        {
                            indValue = 0.0;
                        }
                    }
                    catch( Exception e )
                    {
                        indValue = 0.0;
                    }
                    
                    indValue = Math.round( indValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                }
                else
                {
                    flag = 2;
                    selDataElement = dataElementList.get( deListCount );
                    deListCount++;
                    if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                    {
                        selDecoc = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer.parseInt( selectedServices.get( serviceListCount ).split( ":" )[2] ) );
                        if ( rowCount == 1 )
                        {
                            sheet0.addCell( new Label( colCount, headerRow, selDataElement.getName() + "-" + selDecoc.getName(), getCellFormat1() ) );
                        }
                        
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            tempStr = aggDataMap.get( ou.getId()+":"+selDataElement.getId()+":"+selDecoc.getId() );
                            if( tempStr != null )
                            {
                                try
                                {
                                    dataValue = Double.parseDouble( tempStr );
                                }
                                catch( Exception e )
                                {
                                    dataValue = 0.0;
                                }
                            }
                        }
                        else
                        {
                            dataValue = 0.0;
                        }
                    }
                    else
                    {
                        if ( rowCount == 1 )
                        {
                            sheet0.addCell( new Label( colCount, headerRow, selDataElement.getName(), getCellFormat1() ) );
                        }
                        List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>( selDataElement.getCategoryCombo().getOptionCombos() );
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            for( DataElementCategoryOptionCombo optionCombo : optionCombos )
                            {
                                tempStr = aggDataMap.get( ou.getId()+":"+selDataElement.getId()+":"+optionCombo.getId() );
                                if( tempStr != null )
                                {
                                    try
                                    {
                                        dataValue += Double.parseDouble( tempStr );
                                    }
                                    catch( Exception e )
                                    {
                                    }
                                }
                            }
                        }
                        else
                        {
                            dataValue = 0.0;
                        }
                    }
                }

                if ( flag == 1 )
                {
                    sheet0.addCell( new Number( colCount, headerRow + rowCount, indValue, getCellFormat2() ) );
                }
                else
                {
                    sheet0.addCell( new Number( colCount, headerRow + rowCount, dataValue, getCellFormat2() ) );
                }

                colCount++;
                serviceListCount++;
            }// Service loop end
            rowCount++;
        }// Orgunit loop end
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "TabularAnalysis.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
    
        outputReportFile.deleteOnExit();    
    }
    
    // -------------------------------------------------------------------------
    // Method for getting Selected OrgUnit data in Excel Sheet 
    //     - UseExistingData - Aggregation of Periods
    // -------------------------------------------------------------------------
    public void generateSelectedOrgUnitData_UseExisting_AggPeriods() throws Exception
    {
        int headerRow = 0;
        int headerCol = 0;

        //String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();
        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "TabularAnalysis", 0 );

        sheet0.addCell( new Label( headerCol, headerRow, "Sl.No.", getCellFormat1() ) );
        
        for ( String ouStr : orgUnitListCB )
        {
            OrganisationUnit ou = organisationUnitService.getOrganisationUnit( Integer.parseInt( ouStr ) );
            selOUList.add( ou );
        }

        int c1 = headerCol + 1;
        sheet0.addCell( new Label( c1, headerRow, "Facility", getCellFormat1() ) );
        c1++;

        /* Service Info */
        Indicator selIndicator = new Indicator();
        DataElement selDataElement = new DataElement();
        DataElementCategoryOptionCombo selDecoc = new DataElementCategoryOptionCombo();
        int flag = 0;

        List<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers(OrganisationUnit.class, selOUList ) );
        orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
        
        System.out.println( "Before getting aggdatamap "+new Date() );
        Map<String, String> aggDataMap = new HashMap<String, String>( reportService.getResultDataValueFromAggregateTableByPeriodAgg( orgUnitIdsByComma, dataElementIdsByComma, periodIdsByComma ) );
        System.out.println( "After getting aggdatamap "+new Date() );
    
        /* Calculation Part */
        int rowCount = 1;
        int colCount = 0;
        for( OrganisationUnit ou : selOUList )
        {
            System.out.println("Entered into orgunitloop :"+new Date());
            sheet0.addCell( new Number( headerCol, headerRow + rowCount, rowCount, getCellFormat2() ) );
            sheet0.addCell( new Label( headerCol+1, headerRow + rowCount, ou.getName(), getCellFormat2() ) );

            colCount = c1;
            int deListCount = 0;
            int indListCount = 0;
            int serviceListCount = 0;
            for( String serviceType : serviceTypeList )
            {
                String tempStr = "";
                Double indValue = 0.0;
                Double dataValue = 0.0;
                
                if ( serviceType.equalsIgnoreCase( "I" ) )
                {
                    Double numValue = 0.0;
                    Double denValue = 0.0;

                    flag = 1;
                    selIndicator = indicatorList.get( indListCount );
                    indListCount++;
                    if ( rowCount == 1 )
                    {
                        sheet0.addCell( new Label( colCount, headerRow, selIndicator.getName(), getCellFormat1() ) );
                    }
                    
                    try
                    {
                        numValue = Double.parseDouble( getAggValByOrgUnit( selIndicator.getNumerator(), aggDataMap, ou.getId() ) );
                    }
                    catch( Exception e )
                    {
                        numValue = 0.0;
                    }
                    if( !selIndicator.getDenominator().trim().equals( "1" ) )
                    {
                        try
                        {
                            denValue = Double.parseDouble( getAggValByOrgUnit( selIndicator.getDenominator(), aggDataMap, ou.getId() ) );
                        }
                        catch( Exception e )
                        {
                            denValue = 1.0;
                        }
                    }
                    else 
                    {
                        denValue = 1.0;
                    }
                    /*
                    try
                    {
                        denValue = Double.parseDouble( getAggValByOrgUnit( selIndicator.getDenominator(), aggDataMap, ou.getId() ) );
                    }
                    catch( Exception e )
                    {
                    }
                    */
                    try
                    {
                        if( denValue != 0.0 )
                        {
                            indValue = ( numValue / denValue ) * selIndicator.getIndicatorType().getFactor();
                        }
                        else
                        {
                            indValue = 0.0;
                        }
                    }
                    catch( Exception e )
                    {
                        indValue = 0.0;
                    }
                    
                    indValue = Math.round( indValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                }
                else
                {
                    flag = 2;
                    selDataElement = dataElementList.get( deListCount );
                    deListCount++;
                    if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                    {
                        selDecoc = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer.parseInt( selectedServices.get( serviceListCount ).split( ":" )[2] ) );
                        if ( rowCount == 1 )
                        {
                            sheet0.addCell( new Label( colCount, headerRow, selDataElement.getName() + "-" + selDecoc.getName(), getCellFormat1() ) );
                        }
                        
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            tempStr = aggDataMap.get( ou.getId()+":"+selDataElement.getId()+":"+selDecoc.getId() );
                            if( tempStr != null )
                            {
                                try
                                {
                                    dataValue = Double.parseDouble( tempStr );
                                }
                                catch( Exception e )
                                {
                                    dataValue = 0.0;
                                }
                            }
                        }
                        else
                        {
                            dataValue = 0.0;
                        }
                    }
                    else
                    {
                        if ( rowCount == 1 )
                        {
                            sheet0.addCell( new Label( colCount, headerRow, selDataElement.getName(), getCellFormat1() ) );
                        }
                        List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>( selDataElement.getCategoryCombo().getOptionCombos() );
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            for( DataElementCategoryOptionCombo optionCombo : optionCombos )
                            {
                                tempStr = aggDataMap.get( ou.getId()+":"+selDataElement.getId()+":"+optionCombo.getId() );
                                if( tempStr != null )
                                {
                                    try
                                    {
                                        dataValue += Double.parseDouble( tempStr );
                                    }
                                    catch( Exception e )
                                    {
                                    }
                                }
                            }
                        }
                        else
                        {
                            dataValue = 0.0;
                        }
                    }
                }

                if ( flag == 1 )
                {
                    sheet0.addCell( new Number( colCount, headerRow + rowCount, indValue, getCellFormat2() ) );
                }
                else
                {
                    sheet0.addCell( new Number( colCount, headerRow + rowCount, dataValue, getCellFormat2() ) );
                }

                colCount++;
                serviceListCount++;
            }// Service loop end
            rowCount++;
        }// Orgunit loop end
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "TabularAnalysis.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
    
        outputReportFile.deleteOnExit();    
    }
    
    // -------------------------------------------------------------------------
    // Method for getting OrgUnit Level wise List in Excel Sheet
    // -------------------------------------------------------------------------
    public void generateOrgUnitLevel()
        throws Exception
    {
        int startRow = 0;
        int headerRow = 0;
        int headerCol = 0;

        //String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();
        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "TabularAnalysis", 0 );

        sheet0.mergeCells( headerCol, headerRow, headerCol, headerRow + 1 );
        sheet0.addCell( new Label( headerCol, headerRow, "Sl.No.", getCellFormat1() ) );

        selOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
        selOUList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selOrgUnit.getId() ) );

        System.out.println( "Before getting orgunitlevelmap "+new Date() );
        Map<Integer, Integer> orgunitLevelMap = new HashMap<Integer, Integer>( reportService.getOrgunitLevelMap() );
        System.out.println( "After getting orgunitlevelmap "+new Date() );
        
        Iterator<OrganisationUnit> ouIterator = selOUList.iterator();
        while ( ouIterator.hasNext() )
        {
            OrganisationUnit orgU = ouIterator.next();
            
            Integer level = orgunitLevelMap.get( orgU.getId() );
            if( level == null )
                level = organisationUnitService.getLevelOfOrganisationUnit( orgU.getId() );
            if ( level > orgUnitLevelCB )
            {
                ouIterator.remove();
            }
        }

        int minOULevel = 1;
        int maxOuLevel = 1;
        if ( selOUList != null && selOUList.size() > 0 )
        {
            minOULevel = organisationUnitService.getLevelOfOrganisationUnit( selOUList.get( 0 ).getId() );
        }
        maxOuLevel = orgUnitLevelCB;

        int c1 = headerCol + 1;
        for ( int i = minOULevel; i <= maxOuLevel; i++ )
        {
            sheet0.mergeCells( c1, headerRow, c1, headerRow + 1 );
            sheet0.addCell( new Label( c1, headerRow, "Level " + i, getCellFormat1() ) );
            c1++;
        }

        /* Service Info */
        Indicator selIndicator = new Indicator();
        DataElement selDataElement = new DataElement();
        DataElementCategoryOptionCombo selDecoc = new DataElementCategoryOptionCombo();
        int flag = 0;

        List<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers(OrganisationUnit.class, selOUList ) );
        orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
        
        System.out.println( "Before getting aggdatamap "+new Date() );
        Map<String, String> aggDataMap = new HashMap<String, String>( reportService.getResultDataValueFromAggregateTable( orgUnitIdsByComma, dataElementIdsByComma, periodIdsByComma ) );
        System.out.println( "Before getting aggdatamap "+new Date() );
        
        /* Calculation Part */
        int rowCount = 1;
        int colCount = 0;
        for ( OrganisationUnit ou : selOUList )
        {
            sheet0.addCell( new Number( headerCol, headerRow + 1 + rowCount, rowCount, getCellFormat2() ) );
            colCount = 1 + organisationUnitService.getLevelOfOrganisationUnit( ou.getId() ) - minOULevel;
            sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, ou.getName(), getCellFormat2() ) );

            colCount = c1;
            for ( String service : selectedServices )
            {
                String partsOfService[] = service.split( ":" );
                if ( partsOfService[0].equalsIgnoreCase( "I" ) )
                {
                    flag = 1;
                    selIndicator = indicatorService.getIndicator( Integer.parseInt( partsOfService[1] ) );
                    if ( rowCount == 1 )
                    {
                        if ( aggPeriodCB == null )
                        {
                            sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                        }
                        else
                        {
                            sheet0.mergeCells( colCount, startRow, colCount, startRow + 1 );
                        }
                        sheet0.addCell( new Label( colCount, startRow, selIndicator.getName(), getCellFormat1() ) );
                    }
                }
                else
                {
                    flag = 2;
                    selDataElement = dataElementService.getDataElement( Integer.parseInt( partsOfService[1] ) );

                    if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                    {
                        selDecoc = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer.parseInt( partsOfService[2] ) );
                        if ( rowCount == 1 )
                        {
                            if ( aggPeriodCB == null )
                            {
                                sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                            }
                            else
                            {
                                sheet0.mergeCells( colCount, startRow, colCount, startRow + 1 );
                            }
                            sheet0.addCell( new Label( colCount, startRow, selDataElement.getName() + "-" + selDecoc.getName(), getCellFormat1() ) );
                        }
                    }
                    else
                    {
                        if ( rowCount == 1 )
                        {
                            if ( aggPeriodCB == null )
                            {
                                sheet0.mergeCells( colCount, startRow, colCount + selStartPeriodList.size() - 1, startRow );
                            }
                            else
                            {
                                sheet0.mergeCells( colCount, startRow, colCount, startRow + 1 );
                            }
                            sheet0.addCell( new Label( colCount, startRow, selDataElement.getName(), getCellFormat1() ) );
                        }
                    }
                }

                int periodCount = 0;
                double numAggValue = 0.0;
                double denAggValue = 0.0;
                double dvAggValue = 0.0;
                String aggStrValue = "";

                for ( Date sDate : selStartPeriodList )
                {
                    Date eDate = selEndPeriodList.get( periodCount );
                    
                    List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate, eDate ) );
                    Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );
                    //String periodIdsByComma = getCommaDelimitedString( periodIds );

                    double pwnumAggValue = 0.0;
                    double pwdenAggValue = 0.0;
                    double pwdvAggValue = 0.0;
                    double pwdAggIndValue = 0.0;

                    String tempStr = "";

                    Double tempAggVal;
                    if ( flag == 1 )
                    {
                        tempAggVal = aggregationService.getAggregatedNumeratorValue( selIndicator, sDate, eDate, ou );
                        if ( tempAggVal == null )
                            tempAggVal = 0.0;
                        pwnumAggValue = tempAggVal;
                        
                        tempAggVal = aggregationService.getAggregatedDenominatorValue( selIndicator, sDate, eDate, ou );
                        if ( tempAggVal == null )
                            tempAggVal = 0.0;
                        pwdenAggValue = tempAggVal;

                        tempAggVal = aggregationService.getAggregatedIndicatorValue( selIndicator, sDate, eDate, ou );
                        if ( tempAggVal == null )
                            tempAggVal = 0.0;
                        pwdAggIndValue = tempAggVal;

                        pwdAggIndValue = Math.round( pwdAggIndValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        tempStr = "" + pwdAggIndValue;
                    }
                    else if ( flag == 2 )
                    {
                        if ( deSelection.equalsIgnoreCase( "optioncombo" ) )
                        {
                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                tempAggVal = null;
                                if ( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
                                {
                                        
                                }
                                else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
                                {
                                    tempAggVal = aggregationService.getAggregatedDataValue( selDataElement, selDecoc, sDate, eDate, ou );
                                }
                                else if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
                                {
                                    tempAggVal = 0.0;
                                    for( Integer periodId : periodIds )
                                    {
                                        tempStr = aggDataMap.get( ou.getId()+":"+selDataElement.getId()+":"+selDecoc.getId()+":"+periodId );
                                        if( tempStr != null )
                                        {
                                            try
                                            {
                                                tempAggVal += Double.parseDouble( tempStr );
                                            }
                                            catch( Exception e )
                                            {
                                               
                                            }
                                        }
                                    }
                                }

                                if ( tempAggVal == null )
                                    tempAggVal = 0.0;
                                pwdvAggValue = tempAggVal;

                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                if ( tempPeriod != null )
                                {
                                    DataValue dataValue = dataValueService.getDataValue( ou, selDataElement, tempPeriod, selDecoc );

                                    if ( dataValue != null && dataValue.getValue() != null )
                                    {
                                        tempStr = dataValue.getValue();
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                                else
                                {
                                    tempStr = " ";
                                }
                            }
                        }
                        else
                        {
                            List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>(
                                selDataElement.getCategoryCombo().getOptionCombos() );

                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                if ( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
                                {
                                        
                                }
                                else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
                                {
                                    Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                                    while ( optionComboIterator.hasNext() )
                                    {
                                        DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator
                                            .next();

                                        tempAggVal = aggregationService.getAggregatedDataValue( selDataElement, decoc1,
                                            sDate, eDate, ou );
                                        if ( tempAggVal == null )
                                            tempAggVal = 0.0;
                                        pwdvAggValue += tempAggVal;
                                    }
                                }
                                else if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
                                {
                                    tempAggVal = 0.0;
                                    for( DataElementCategoryOptionCombo optionCombo : optionCombos )
                                    {
                                        for( Integer periodId : periodIds )
                                        {
                                            tempStr = aggDataMap.get( ou.getId()+":"+selDataElement.getId()+":"+optionCombo.getId()+":"+periodId );
                                            if( tempStr != null )
                                            {
                                                try
                                                {
                                                    tempAggVal += Double.parseDouble( tempStr );
                                                }
                                                catch( Exception e )
                                                {
                                                   
                                                }
                                            }
                                        }
                                    }
                                    pwdvAggValue += tempAggVal;
                                    System.out.println( ou.getName()+" : "+selDataElement.getName()+" : "+pwdvAggValue );
                                }

                                tempStr = "" + (int) pwdvAggValue;
                            }
                            else
                            {
                                Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                                while ( optionComboIterator.hasNext() )
                                {
                                    DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator
                                        .next();

                                    PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );
                                    Period tempPeriod = periodService.getPeriod( sDate, eDate, periodType );
                                    if ( tempPeriod != null )
                                    {
                                        DataValue dataValue = dataValueService.getDataValue( ou, selDataElement,
                                            tempPeriod, decoc1 );

                                        if ( dataValue != null )
                                        {
                                            tempStr += dataValue.getValue() + " : ";
                                        }
                                        else
                                        {
                                            tempStr = "  ";
                                        }
                                    }
                                    else
                                    {
                                        tempStr = " ";
                                    }
                                }
                            }
                        }
                    }

                    if ( aggPeriodCB == null )
                    {
                        if ( rowCount == 1 )
                        {
                            sheet0.addCell( new Label( colCount, startRow + 1, periodNames.get( periodCount ),
                                getCellFormat1() ) );
                        }

                        if ( flag == 1 )
                        {
                            sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, pwdAggIndValue,
                                getCellFormat2() ) );
                        }
                        else
                        {
                            if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                            {
                                sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, (int) pwdvAggValue,
                                    getCellFormat2() ) );
                            }
                            else
                            {
                                sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, tempStr,
                                    getCellFormat2() ) );
                            }
                        }

                        colCount++;
                    }
                    else
                    {
                        numAggValue += pwnumAggValue;
                        denAggValue += pwdenAggValue;
                        dvAggValue += pwdvAggValue;
                        aggStrValue += tempStr + " - ";
                    }

                    periodCount++;
                }// Period Loop

                if ( aggPeriodCB != null )
                {
                    if ( flag == 1 )
                    {
                        try
                        {
                            double tempDouble = Double.parseDouble( selIndicator.getDenominator().trim() );

                            // if(tempInt == 1) denAggValue = 1;
                            denAggValue = tempDouble;
                        }
                        catch ( Exception e )
                        {
                            System.out.println( "Denominator is not expression" );
                        }

                        double aggIndValue = (numAggValue / denAggValue) * selIndicator.getIndicatorType().getFactor();
                        aggIndValue = Math.round( aggIndValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                        sheet0
                            .addCell( new Number( colCount, headerRow + 1 + rowCount, aggIndValue, getCellFormat2() ) );
                    }
                    else if ( flag == 2 )
                    {
                        if ( selDataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                        {
                            sheet0.addCell( new Number( colCount, headerRow + 1 + rowCount, (int) dvAggValue,
                                getCellFormat2() ) );
                        }
                        else
                        {
                            sheet0.addCell( new Label( colCount, headerRow + 1 + rowCount, aggStrValue,
                                getCellFormat2() ) );
                        }
                    }

                    colCount++;
                }
                // colCount++;
            }// Service loop end

            rowCount++;
        }// Orgunit loop end
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "TabularAnalysis.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();
    }

    public WritableCellFormat getCellFormat1() throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();
        
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setBackground( Colour.GRAY_25 );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setWrap( true );

        return wCellformat;
    }

    public WritableCellFormat getCellFormat2() throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setWrap( true );

        return wCellformat;
    }
    
    public String getAggVal( String expression, Map<String, String> aggDataMap, Integer orgUnitId, Integer periodId )
    {
        try
        {
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( expression );
            StringBuffer buffer = new StringBuffer();

            String resultValue = "";

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );

                String keyString = orgUnitId + ":" + replaceString.replaceAll( "\\.", ":" ) + ":" + periodId;
                
                replaceString = aggDataMap.get( keyString );
                
                if( replaceString == null )
                {
                    replaceString = "0";
                }
                
                matcher.appendReplacement( buffer, replaceString );

                resultValue = replaceString;
            }

            matcher.appendTail( buffer );
            
            double d = 0.0;
            try
            {
                d = MathUtils.calculateExpression( buffer.toString() );
            }
            catch ( Exception e )
            {
                d = 0.0;
                resultValue = "";
            }
            
            resultValue = "" + (double) d;

            return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }


    public String getAggValByPeriod( String expression, Map<String, String> aggDataMap, Integer periodId )
    {
        try
        {
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( expression );
            StringBuffer buffer = new StringBuffer();

            String resultValue = "";

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );

                String keyString = replaceString.replaceAll( "\\.", ":" ) + ":" + periodId;
                
                replaceString = aggDataMap.get( keyString );
                
                if( replaceString == null )
                {
                    replaceString = "0";
                }
                
                matcher.appendReplacement( buffer, replaceString );

                resultValue = replaceString;
            }

            matcher.appendTail( buffer );
            
            double d = 0.0;
            try
            {
                d = MathUtils.calculateExpression( buffer.toString() );
            }
            catch ( Exception e )
            {
                d = 0.0;
                resultValue = "";
            }
            
            resultValue = "" + (double) d;

            return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }

    public String getAggValByOrgUnit( String expression, Map<String, String> aggDataMap, Integer orgUnitId )
    {
        try
        {
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( expression );
            StringBuffer buffer = new StringBuffer();

            String resultValue = "";

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );

                String keyString = orgUnitId + ":" + replaceString.replaceAll( "\\.", ":" );
                
                replaceString = aggDataMap.get( keyString );
                
                if( replaceString == null )
                {
                    replaceString = "0";
                }
                
                matcher.appendReplacement( buffer, replaceString );

                resultValue = replaceString;
            }

            matcher.appendTail( buffer );
            
            double d = 0.0;
            try
            {
                d = MathUtils.calculateExpression( buffer.toString() );
            }
            catch ( Exception e )
            {
                d = 0.0;
                resultValue = "";
            }
            
            resultValue = "" + (double) d;

            return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }
    
}// class end
