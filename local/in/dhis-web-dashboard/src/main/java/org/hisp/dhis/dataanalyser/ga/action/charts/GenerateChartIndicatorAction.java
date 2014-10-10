/*
 * Copyright (c) 2004-2012, University of Oslo
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
package org.hisp.dhis.dataanalyser.ga.action.charts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.amplecode.quick.StatementManager;
import org.apache.struts2.ServletActionContext;
import org.apache.velocity.tools.generic.ListTool;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.dataanalyser.util.DashBoardService;
import org.hisp.dhis.dataanalyser.util.IndicatorChartResult;
import org.hisp.dhis.expression.ExpressionService;
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
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.hisp.dhis.period.SixMonthlyPeriodType;
import org.hisp.dhis.period.WeeklyPeriodType;
import org.hisp.dhis.period.YearlyPeriodType;
import org.hisp.dhis.reports.ReportService;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;

/**
 * @author Mithilesh Kumar Thakur
 * 
 * @version GenerateChartIdicatorAction.java Nov 3, 2010 12:41:31 PM
 */
public class GenerateChartIndicatorAction
    implements Action
{

    private final String PERIODWISE = "period";

    private final String CHILDREN = "children";

    private final String SELECTED = "random";

    // IndicatorChartResult indicatorChartResult = new IndicatorChartResult() ;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private DashBoardService dashBoardService;

    public void setDashBoardService( DashBoardService dashBoardService )
    {
        this.dashBoardService = dashBoardService;
    }

    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

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

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }
    
    // --------------------------------------------------------------------------
    // Parameters
    // --------------------------------------------------------------------------
    private HttpSession session;

    public HttpSession getSession()
    {
        return session;
    }

    private List<Object> selectedServiceList;

    public List<Object> getSelectedServiceList()
    {
        return selectedServiceList;
    }

    private List<OrganisationUnit> selOUList;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private List<String> selectedIndicators;

    public void setSelectedIndicators( List<String> selectedIndicators )
    {
        this.selectedIndicators = selectedIndicators;
    }

    private String ougGroupSetCB;

    public void setOugGroupSetCB( String ougGroupSetCB )
    {
        this.ougGroupSetCB = ougGroupSetCB;
    }

    public String getOugGroupSetCB()
    {
        return ougGroupSetCB;
    }

    private List<String> orgUnitListCB;

    public void setOrgUnitListCB( List<String> orgUnitListCB )
    {
        this.orgUnitListCB = orgUnitListCB;
    }

    private String categoryLB;

    public String getCategoryLB()
    {
        return categoryLB;
    }

    public void setCategoryLB( String categoryLB )
    {
        this.categoryLB = categoryLB;
    }

    private String selectedButton;

    public String getSelectedButton()
    {
        return selectedButton;
    }

    public void setSelectedButton( String selectedButton )
    {
        this.selectedButton = selectedButton;
    }

    private List<String> orgUnitGroupList;

    public List<String> getOrgUnitGroupList()
    {
        return orgUnitGroupList;
    }

    public void setOrgUnitGroupList( List<String> orgUnitGroupList )
    {
        this.orgUnitGroupList = orgUnitGroupList;
    }

    private String aggDataCB;

    public void setAggDataCB( String aggDataCB )
    {
        this.aggDataCB = aggDataCB;
    }

    private String periodTypeLB;

    public void setPeriodTypeLB( String periodTypeLB )
    {
        this.periodTypeLB = periodTypeLB;
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

    private List<String> periodNames;

    private List<Date> selStartPeriodList;

    private List<Date> selEndPeriodList;

    private IndicatorChartResult indicatorChartResult;

    public IndicatorChartResult getIndicatorChartResult()
    {
        return indicatorChartResult;
    }

    List<Indicator> yseriesList;

    public List<Indicator> getYseriesList()
    {
        return yseriesList;
    }

    List<String> numeratorDEList;

    public List<String> getNumeratorDEList()
    {
        return numeratorDEList;
    }

    List<String> denominatorDEList;

    public List<String> getDenominatorDEList()
    {
        return denominatorDEList;
    }

    ListTool listTool;

    public ListTool getListTool()
    {
        return listTool;
    }

    private OrganisationUnit selectedOrgUnit;

    private OrganisationUnitGroup selOrgUnitGroup;

    private List<OrganisationUnit> selOUGroupMemberList = new ArrayList<OrganisationUnit>();

    List<String> numDataElements;

    public List<String> getNumDataElements()
    {
        return numDataElements;
    }
    
    List<String> denumDataElements;
    
    public List<String> getDenumDataElements()
    {
        return denumDataElements;
    }
    private List<String> selectedDrillDownData;
    
    public List<String> getSelectedDrillDownData()
    {
        return selectedDrillDownData;
    }
    
    
    private String drillDownPeriodStartDate;
    private String drillDownPeriodEndDate;
    private String drillDownPeriodNames;
    private String aggChecked;
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    public String execute()
        throws Exception
    {

        statementManager.initialise();

        listTool = new ListTool();

        selOUList = new ArrayList<OrganisationUnit>();
        System.out.println( "selected orgUnit  size : " + orgUnitListCB.size() );

        System.out.println( "selected Year  size : " + yearLB.size() );

        // System.out.println( "selected Period  size : " + periodLB.size() );

        System.out.println( "selected Indicators : " + selectedIndicators );

        System.out.println( "selected Indicators size : " + selectedIndicators.size() );
        /*
         * double d = 4.57767; System.out.println(Math.round(d));
         */
        
        selectedDrillDownData = new ArrayList<String>();//drillDown for periodWise to OrgChildWise and OrgChildWise to periodWise
        
        
        aggChecked = "";
        
        if( aggDataCB != null )
        {
            aggChecked = "1";
        }
        else
        {
            aggChecked = "0";
        }
        
        if( aggDataCB.equalsIgnoreCase( "false" ))
        {
            aggDataCB = null;
        }
        
        if( ougGroupSetCB.equalsIgnoreCase( "false" ))
        {
            ougGroupSetCB = null;
        }
        
        
        // int flag = 0;
        // selOUList = new ArrayList<OrganisationUnit>();
        selStartPeriodList = new ArrayList<Date>();
        selEndPeriodList = new ArrayList<Date>();

        yseriesList = new ArrayList<Indicator>();

        numeratorDEList = new ArrayList<String>();
        denominatorDEList = new ArrayList<String>();
        
        numDataElements = new ArrayList<String>();
        denumDataElements = new ArrayList<String>();
        // ouChildCountMap = new HashMap<OrganisationUnit, Integer>();

       // String monthOrder[] = { "04", "05", "06", "07", "08", "09", "10", "11", "12", "01", "02", "03" };
        //int monthDays[] = { 30, 31, 30, 31, 31, 30, 31, 30, 31, 31, 28, 31 };
        
        /* Period Info */
        String monthOrder[] = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
        int monthDays[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

        // for financial year 
        String financialMonthOrder[] = { "04", "05", "06", "07", "08", "09", "10", "11", "12", "01", "02", "03" };
        int financialMonthDays[] = { 30, 31, 30, 31, 31, 30, 31, 30, 31, 31, 28, 31 };

        String startD = "";
        String endD = "";
        
        drillDownPeriodStartDate = "";
        drillDownPeriodEndDate = "";
        drillDownPeriodNames = "";
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );

        periodNames = new ArrayList<String>();
        
        // for weekly period
        if ( periodTypeLB.equalsIgnoreCase( WeeklyPeriodType.NAME ) )
        {
          //  System.out.println( " Inside  weekly" );
            int periodCount = 0;
            for ( String periodStr : periodLB )
            {
                String  startWeekDate = periodStr.split( "To" )[0] ; //for start week
                String  endWeekDate = periodStr.split( "To" )[1] ; //for end week
                
                startD = startWeekDate.trim();
                endD = endWeekDate.trim();
                
                // for DrillDown Period String
                if( periodCount == periodLB.size()-1 )
                {
                    drillDownPeriodStartDate += startD;
                    drillDownPeriodEndDate += endD;
                    drillDownPeriodNames += periodStr;
                }
                else
                {
                    drillDownPeriodStartDate += startD + ";";
                    drillDownPeriodEndDate += endD + ";";
                    drillDownPeriodNames += periodStr + ";";
                }
                
                selStartPeriodList.add( format.parseDate( startD ) );
                selEndPeriodList.add( format.parseDate( endD ) );
                
                periodNames.add( periodStr );
                periodCount++;
                //System.out.println( startD + " : " + endD );
            }
        }
        // for FinancialAprilPeriodType
        else if ( periodTypeLB.equalsIgnoreCase( FinancialAprilPeriodType.NAME ) )
        {
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
                            endD = "" + (selYear + 1) + "-" + financialMonthOrder[period] + "-" + ( financialMonthDays[period] + 1);
                        }
                    }
                    else
                    {
                        startD = "" + selYear + "-" + financialMonthOrder[period] + "-01";
                        endD = "" + selYear + "-" + financialMonthOrder[period] + "-" + financialMonthDays[period];
                    }
                    
                    drillDownPeriodStartDate += startD + ";";
                    drillDownPeriodEndDate += endD + ";";
                    drillDownPeriodNames += simpleDateFormat.format( format.parseDate( startD ) ) + ";";
                    selStartPeriodList.add( format.parseDate( startD ) );
                    selEndPeriodList.add( format.parseDate( endD ) );
                    periodNames.add( simpleDateFormat.format( format.parseDate( startD ) ) );
                }
            }

        }
        else
        {   int periodCount = 0;    
            for ( String year : yearLB )
            {
               // int selYear = Integer.parseInt( year.split( "-" )[0] );
                int selYear = Integer.parseInt( year );
        
                if ( periodTypeLB.equalsIgnoreCase( YearlyPeriodType.NAME ) )
                {
                    startD = "" + selYear + "-01-01";
                    endD = "" + selYear  + "-12-31";
                    
                   
                     // for DrillDown Period String
                    if( periodCount == yearLB.size()-1 )
                    {
                        drillDownPeriodStartDate += startD;
                        drillDownPeriodEndDate += endD;
                        drillDownPeriodNames += selYear;
                    }
                    else
                    {
                        drillDownPeriodStartDate += startD + ";";
                        drillDownPeriodEndDate += endD + ";";
                        drillDownPeriodNames += selYear + ";";
                    }
                    
                    selStartPeriodList.add( format.parseDate( startD ) );
                    selEndPeriodList.add( format.parseDate( endD ) );
        
                    //periodNames.add( "" + selYear + "-" + (selYear + 1) );
                    
                    periodNames.add( "" + selYear );
        
                    continue;
        
                }
        
                for ( String periodStr : periodLB )
                {
                    //int period = Integer.parseInt( periodStr );
        
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
                        
                        drillDownPeriodStartDate += startD + ";";
                        drillDownPeriodEndDate += endD + ";";
                        drillDownPeriodNames += simpleDateFormat.format( format.parseDate( startD ) ) + ";";
                        
                        selStartPeriodList.add( format.parseDate( startD ) );
                        selEndPeriodList.add( format.parseDate( endD ) );
                        periodNames.add( simpleDateFormat.format( format.parseDate( startD ) ) );
                        //System.out.println( "Start Date : " + startD + " , End Date : " + endD );
                    }
                    else if ( periodTypeLB.equalsIgnoreCase( QuarterlyPeriodType.NAME ) )
                    {
                        int period = Integer.parseInt( periodStr );
                        if ( period == 0 )
                        {
                            startD = "" + selYear + "-01-01";
                            endD = "" + selYear + "-03-31";
                            periodNames.add( selYear + "-Q1" );
                            drillDownPeriodNames += selYear + "-Q1" + ";";
                        }
                        else if ( period == 1 )
                        {
                            startD = "" + selYear + "-04-01";
                            endD = "" + selYear + "-06-30";
                            periodNames.add( selYear + "-Q2" );
                            drillDownPeriodNames += selYear + "-Q2" + ";";
                        }
                        else if ( period == 2 )
                        {
                            startD = "" + selYear + "-07-01";
                            endD = "" + selYear + "-09-30";
                            periodNames.add( selYear + "-Q3" );
                            drillDownPeriodNames += selYear + "-Q3" + ";";
                        }
                        else
                        {
                            startD = "" + selYear + "-10-01";
                            endD = "" + selYear + "-12-31";
                            periodNames.add( (selYear) + "-Q4" );
                            drillDownPeriodNames += selYear + "-Q4" + ";";
                        }
                       
                        
                        // for DrillDown Period String
                        if( periodCount == periodLB.size()-1 )
                        {
                            drillDownPeriodStartDate += startD;
                            drillDownPeriodEndDate += endD;
                        }
                        else
                        {
                            drillDownPeriodStartDate += startD + ";";
                            drillDownPeriodEndDate += endD + ";";
                        }
                        
                        
                        selStartPeriodList.add( format.parseDate( startD ) );
                        selEndPeriodList.add( format.parseDate( endD ) );
                        //System.out.println( "Start Date : " + startD + " , End Date : " + endD );
                    }
                    else if ( periodTypeLB.equalsIgnoreCase( SixMonthlyPeriodType.NAME ) )
                    {
                        int period = Integer.parseInt( periodStr );
                        if ( period == 0 )
                        {
                            startD = "" + selYear + "-01-01";
                            endD = "" + selYear + "-06-30";
                            periodNames.add( selYear + "-HY1" );
                            drillDownPeriodNames += selYear + "-HY1" + ";";
                        }
                        else
                        {
                            startD = "" + selYear + "-07-01";
                            endD = "" + selYear + "-12-31";
                            periodNames.add( selYear + "-HY2" );
                            drillDownPeriodNames += selYear + "-HY2" + ";";
                        }
                        
                        drillDownPeriodStartDate += startD + ";";
                        drillDownPeriodEndDate += endD + ";";
                        
                        selStartPeriodList.add( format.parseDate( startD ) );
                        selEndPeriodList.add( format.parseDate( endD ) );
                    }
                    
                    else if ( periodTypeLB.equalsIgnoreCase( DailyPeriodType.NAME ) )
                    {
                       String  month = periodStr.split( "-" )[0] ;
                      
                       String  date = periodStr.split( "-" )[1] ;
                        
                        startD = selYear + "-" + periodStr;
                        endD = selYear + "-" + periodStr ;
                      
                        if ( selYear  % 4 != 0 && month.trim().equalsIgnoreCase( "02" )  && date.trim().equalsIgnoreCase( "29" ) )
                        {
                            startD = selYear + "-" + month + "-" + date;
                            endD = selYear + "-" + month + "-" + date;
                            continue;
                        }
                        if ( (( selYear % 400 == 0) || (( selYear % 100 != 0 && selYear % 4 == 0))) && month.trim().equalsIgnoreCase( "02" )  && date.trim().equalsIgnoreCase( "29" ) ); 
                        {
                            startD = selYear + "-" + month + "-" + date;
                            endD = selYear  + "-" + month + "-" + date;
                        }
                        
                      // for DrillDown Period String
                        if( periodCount == periodLB.size()-1 )
                        {
                            drillDownPeriodStartDate += startD;
                            drillDownPeriodEndDate += endD;
                            drillDownPeriodNames += startD;
                        }
                        else
                        {
                            drillDownPeriodStartDate += startD + ";";
                            drillDownPeriodEndDate += endD + ";";
                            drillDownPeriodNames += startD + ";";
                        }
                        
                        selStartPeriodList.add( format.parseDate( startD ) );
                        selEndPeriodList.add( format.parseDate( endD ) );
                        
                        periodNames.add( startD );
                       // System.out.println( startD + " : " + endD );
                    }
                    System.out.println( startD + " : " + endD );
                }
                periodCount++;
            }
    }
        // Indicator Information
        List<Indicator> indicatorList = new ArrayList<Indicator>();
        Iterator deIterator = selectedIndicators.iterator();
        while ( deIterator.hasNext() )
        {
            // String indicatorId = (String) deIterator.next();
            int serviceID = Integer.parseInt( (String) deIterator.next() );
            Indicator indicator = indicatorService.getIndicator( serviceID );
            
            // for numeratorDataElement,denominatorDataElement
            String numeratorDataElement = expressionService.getExpressionDescription( indicator.getNumerator());
            String denominatorDataElement = expressionService.getExpressionDescription( indicator.getDenominator());
            
            numDataElements.add( numeratorDataElement );
            denumDataElements.add( denominatorDataElement );
            
            indicatorList.add( indicator );

        }

        selectedServiceList = new ArrayList<Object>( indicatorList );

        // OrgUnit Information

        for ( String ouStr : orgUnitListCB )
        {
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( ouStr ) );
            selOUList.add( orgUnit );
        }

        // calling individual Function
        if ( categoryLB.equalsIgnoreCase( PERIODWISE ) && ougGroupSetCB == null )
        {

            System.out.println( "Inside PeriodWise Chart Data and group not selected" );
            //System.out.println( "\n\nsize of OrgUnit List : " + selOUList.size() + " , size of Indicator List : " + indicatorList.size() );
            System.out.println( "Chart Generation Start Time is : \t" + new Date() );
            indicatorChartResult = generateChartDataPeriodWise( selStartPeriodList, selEndPeriodList, periodNames, indicatorList, selOUList.iterator().next() );
            
           /*
            for( String drillDown : selectedDrillDownData )
            {
                System.out.println( "drill Down value is :" + drillDown );
                System.out.println( "---------");
            }
            */
           // dataElementChartResult.getSeries()

        }
        else if ( categoryLB.equalsIgnoreCase( CHILDREN ) && ougGroupSetCB == null )
        {

            System.out.println( "Inside ChildWise Chart Data" );
            System.out.println( "Chart Generation Start Time is : \t" + new Date() );

            selectedOrgUnit = new OrganisationUnit();
            selectedOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );

            List<OrganisationUnit> childOrgUnitList = new ArrayList<OrganisationUnit>();
            childOrgUnitList = new ArrayList<OrganisationUnit>( selectedOrgUnit.getChildren() );

            indicatorChartResult = generateChartDataWithChildrenWise( selStartPeriodList, selEndPeriodList,
                periodNames, indicatorList, childOrgUnitList );

        }
        else if ( categoryLB.equalsIgnoreCase( SELECTED ) && ougGroupSetCB == null )
        {

            System.out.println( "Inside SelectedOrgUnit Chart Data" );
            System.out.println( "Chart Generation Start Time is : \t" + new Date() );

            indicatorChartResult = generateChartDataSelectedOrgUnitWise( selStartPeriodList, selEndPeriodList,
                periodNames, indicatorList, selOUList );

        }

        else if ( categoryLB.equalsIgnoreCase( PERIODWISE ) && ougGroupSetCB != null )
        {

            System.out.println( "Inside ChildWise With OrgGroup Chart Data" );
            System.out.println( "Chart Generation Start Time is : \t" + new Date() );

            selectedOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
            List<OrganisationUnit> orgUnitChildList = new ArrayList<OrganisationUnit>( organisationUnitService
                .getOrganisationUnitWithChildren( selectedOrgUnit.getId() ) );

            // System.out.println( "oug Group Set is  = " + orgUnitGroupList );

            selOrgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( Integer.parseInt( orgUnitGroupList
                .get( 0 ) ) );

            selOUGroupMemberList = new ArrayList<OrganisationUnit>( selOrgUnitGroup.getMembers() );

            selOUGroupMemberList.retainAll( orgUnitChildList );

            indicatorChartResult = generateChartDataOrgGroupPeriodWise( selStartPeriodList, selEndPeriodList, periodNames, indicatorList, selOUGroupMemberList );

        }

        else if ( categoryLB.equalsIgnoreCase( CHILDREN ) && ougGroupSetCB != null )
        {

            System.out.println( "Inside ChildWise With OrgGroup Chart Data" );
            System.out.println( "Chart Generation Start Time is : \t" + new Date() );

            selectedOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
            List<OrganisationUnit> orgUnitChildList = new ArrayList<OrganisationUnit>( organisationUnitService
                .getOrganisationUnitWithChildren( selectedOrgUnit.getId() ) );

            // int groupCount = 0;
           // System.out.println( "\n\n ++++++++++++++++++++++ \n orgUnitGroup : " + orgUnitGroupList );
            for ( String orgUnitGroupId : orgUnitGroupList )
            {
                OrganisationUnitGroup selOrgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( Integer
                    .parseInt( orgUnitGroupId ) );

                List<OrganisationUnit> selectedOUGroupMemberList = new ArrayList<OrganisationUnit>( selOrgUnitGroup
                    .getMembers() );

                selOUGroupMemberList.addAll( selectedOUGroupMemberList );

                // groupCount++;

            }

            selOUGroupMemberList.retainAll( orgUnitChildList );

            indicatorChartResult = generateChartDataSelectedOrgUnitWise( selStartPeriodList, selEndPeriodList, periodNames, indicatorList, selOUGroupMemberList );

        }

        else if ( categoryLB.equalsIgnoreCase( SELECTED ) && ougGroupSetCB != null )
        {

            System.out.println( "Inside SelectedOrgUnit With OrgGroup Chart Data" );
            System.out.println( "Chart Generation Start Time is : \t" + new Date() );

            Map<OrganisationUnitGroup, List<OrganisationUnit>> orgUnitGroupMap = new HashMap<OrganisationUnitGroup, List<OrganisationUnit>>();

            selectedOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
            List<OrganisationUnit> orgUnitChildList = new ArrayList<OrganisationUnit>( organisationUnitService
                .getOrganisationUnitWithChildren( selectedOrgUnit.getId() ) );

            for ( String orgUnitGroupId : orgUnitGroupList )
            {
                OrganisationUnitGroup selOrgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( Integer
                    .parseInt( orgUnitGroupId ) );
                List<OrganisationUnit> selectedOUGroupMemberList = new ArrayList<OrganisationUnit>( selOrgUnitGroup
                    .getMembers() );

                selectedOUGroupMemberList.retainAll( orgUnitChildList );

                orgUnitGroupMap.put( selOrgUnitGroup, selectedOUGroupMemberList );
                // selOUGroupMemberList.addAll( selectedOUGroupMemberList );
            }

            indicatorChartResult = generateChartDataSelectedOrgUnitGroupWise( selStartPeriodList, selEndPeriodList,
                periodNames, indicatorList, orgUnitGroupMap );

        }

        ActionContext ctx = ActionContext.getContext();
        HttpServletRequest req = (HttpServletRequest) ctx.get( ServletActionContext.HTTP_REQUEST );

        session = req.getSession();

        session.setAttribute( "data1", indicatorChartResult.getData() );
        session.setAttribute( "numDataArray", indicatorChartResult.getNumDataArray() );
        session.setAttribute( "denumDataArray", indicatorChartResult.getDenumDataArray() );
        session.setAttribute( "series1", indicatorChartResult.getSeries() );
        session.setAttribute( "categories1", indicatorChartResult.getCategories() );
        session.setAttribute( "chartTitle", indicatorChartResult.getChartTitle() );
        session.setAttribute( "xAxisTitle", indicatorChartResult.getXAxis_Title() );
        session.setAttribute( "yAxisTitle", indicatorChartResult.getYAxis_Title() );

        statementManager.destroy();
        System.out.println( "Chart Generation End Time is : \t" + new Date() );
        return SUCCESS;

    } // execute end

    // Supporting Methods
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data only Period Wise start
    // -------------------------------------------------------------------------

    public IndicatorChartResult generateChartDataPeriodWise( List<Date> selStartPeriodList, List<Date> selEndPeriodList, List<String> periodNames, List<Indicator> indicatorList, OrganisationUnit orgUnit )throws Exception
    {

        IndicatorChartResult indicatorChartResult;

        String[] series = new String[indicatorList.size()];
        String[] categories = new String[selStartPeriodList.size()];
        Double[][] data = new Double[indicatorList.size()][selStartPeriodList.size()];

        Double[][] numDataArray = new Double[indicatorList.size()][selStartPeriodList.size()];
        Double[][] denumDataArray = new Double[indicatorList.size()][selStartPeriodList.size()];

        // Map<Integer, List<Double>> numData = new HashMap<Integer, List<Double>>();
        // Map<Integer, List<Double>> denumData = new HashMap<Integer, List<Double>>();

        String chartTitle = "OrganisationUnit : " + orgUnit.getShortName();
        String xAxis_Title = "Time Line";
        String yAxis_Title = "Value";

        int serviceCount = 0;
        for ( Indicator indicator : indicatorList )
        {
            series[serviceCount] = indicator.getName();
            yseriesList.add( indicator );

            numeratorDEList.add( indicator.getNumeratorDescription() );
            denominatorDEList.add( indicator.getDenominatorDescription() );

            // List<Double> numeratorValueList = new ArrayList<Double>();
            // List<Double> denumeratorValueList = new ArrayList<Double>();

            int periodCount = 0;
            for ( Date startDate : selStartPeriodList )
            {
                Date endDate = selEndPeriodList.get( periodCount );
                String drillDownPeriodName = periodNames.get( periodCount );

                categories[periodCount] = periodNames.get( periodCount );
                
                String tempStartDate = format.formatDate( startDate );
                String tempEndDate   = format.formatDate( endDate );

                Double aggIndicatorValue = 0.0;
                Double aggIndicatorNumValue = 0.0;
                Double aggIndicatorDenumValue = 0.0;
                String drillDownData = orgUnit.getId() + ":" + "0" + ":" + indicator.getId() + ":" + periodTypeLB + ":" + tempStartDate + ":" + tempEndDate + ":" + drillDownPeriodName + ":" + aggChecked;
                selectedDrillDownData.add( drillDownData );
                
                if ( aggDataCB != null )
                {
                    aggIndicatorValue = aggregationService.getAggregatedIndicatorValue( indicator, startDate, endDate, orgUnit );

                    aggIndicatorNumValue = aggregationService.getAggregatedNumeratorValue( indicator, startDate, endDate, orgUnit );
                    aggIndicatorDenumValue = aggregationService.getAggregatedDenominatorValue( indicator, startDate, endDate, orgUnit );

                    if ( aggIndicatorValue == null ) aggIndicatorValue = 0.0;

                }
                else
                {
                    aggIndicatorValue = dashBoardService.getIndividualIndicatorValue( indicator, orgUnit, startDate, endDate );

                   // System.out.println( " \nIndicator Numerator value  : " + indicator.getNumerator()
                      //  + ", Start Date :- " + startDate + ", End Date :- " + endDate + ", Org Unit :- " + orgUnit );

                    String tempStr = reportService.getIndividualResultDataValue( indicator.getNumerator(), startDate, endDate, orgUnit, "" );
                   // System.out.println( " \nIndicatorNumerator valu is " + tempStr );

                    try
                    {
                        aggIndicatorNumValue = Double.parseDouble( tempStr );
                    }
                    catch ( Exception e )
                    {
                        aggIndicatorNumValue = 0.0;
                    }

                    tempStr = reportService.getIndividualResultDataValue( indicator.getDenominator(), startDate, endDate, orgUnit, "" );

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
                // numeratorValueList.add( aggIndicatorNumValue );
                // denumeratorValueList.add( aggIndicatorDenumValue );

                periodCount++;
            }

            // numData.put( serviceCount, numeratorValueList );
            // denumData.put( serviceCount, denumeratorValueList );

            serviceCount++;
        }

        indicatorChartResult = new IndicatorChartResult( series, categories, data, numDataArray, denumDataArray,chartTitle, xAxis_Title, yAxis_Title );
        return indicatorChartResult;

    }

    // -------------------------------------------------------------------------
    // Methods for getting Chart Data only Period Wise end
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Methods for getting Chart Data With Children Wise start
    // -------------------------------------------------------------------------

    public IndicatorChartResult generateChartDataWithChildrenWise( List<Date> selStartPeriodList,
        List<Date> selEndPeriodList, List<String> periodNames, List<Indicator> indicatorList,
        List<OrganisationUnit> childOrgUnitList )
        throws Exception
    {
        IndicatorChartResult indicatorChartResult;

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
            yseriesList.add( indicator );

            numeratorDEList.add( indicator.getNumeratorDescription() );
            denominatorDEList.add( indicator.getDenominatorDescription() );

            int childCount = 0;
            for ( OrganisationUnit orgChild : childOrgUnitList )
            {

                categories[childCount] = orgChild.getName();
                
                String drillDownData = orgChild.getId() + ":" + "0" + ":" + indicator.getId() + ":"+ periodTypeLB + ":" + drillDownPeriodStartDate + ":" + drillDownPeriodEndDate + ":" + drillDownPeriodNames + ":" + aggChecked;
                selectedDrillDownData.add( drillDownData );

                Double aggIndicatorValue = 0.0;
                Double aggIndicatorNumValue = 0.0;
                Double aggIndicatorDenumValue = 0.0;
                int periodCount = 0;
                for ( Date startDate : selStartPeriodList )
                {
                    Date endDate = selEndPeriodList.get( periodCount );

                    if ( aggDataCB != null )
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
                        String tempStr = reportService.getIndividualResultDataValue( indicator.getNumerator(),
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

                        tempStr = reportService.getIndividualResultDataValue( indicator.getDenominator(), startDate,
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
                    // aggIndicatorValue = ( aggIndicatorNumValue /
                    // aggIndicatorDenumValue )*
                    // indicator.getIndicatorType().getFactor();
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
                // data[serviceCount][childCount] = aggDataValue;
                childCount++;
            }

            serviceCount++;
        }

        indicatorChartResult = new IndicatorChartResult( series, categories, data, numDataArray, denumDataArray,chartTitle, xAxis_Title, yAxis_Title );
        return indicatorChartResult;
    }

    // -------------------------------------------------------------------------
    // Methods for getting Chart Data With Children Wise end
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Methods for getting Chart Data With Selected Wise start
    // -------------------------------------------------------------------------

    public IndicatorChartResult generateChartDataSelectedOrgUnitWise( List<Date> selStartPeriodList,
        List<Date> selEndPeriodList, List<String> periodNames, List<Indicator> indicatorList,
        List<OrganisationUnit> selOUList )
        throws Exception
    {
        IndicatorChartResult indicatorChartResult;

        String[] series = new String[indicatorList.size()];
        String[] categories = new String[selOUList.size()];

        Double[][] numDataArray = new Double[indicatorList.size()][selOUList.size()];
        Double[][] denumDataArray = new Double[indicatorList.size()][selOUList.size()];
        Double[][] data = new Double[indicatorList.size()][selOUList.size()];
        String chartTitle = "OrganisationUnit : -----";

        String xAxis_Title = "Facilities";
        String yAxis_Title = "Value";

        int serviceCount = 0;

        for ( Indicator indicator : indicatorList )
        {
            series[serviceCount] = indicator.getName();
            yseriesList.add( indicator );

            numeratorDEList.add( indicator.getNumeratorDescription() );
            denominatorDEList.add( indicator.getDenominatorDescription() );

            int orgUnitCount = 0;
            for ( OrganisationUnit orgUnit : selOUList )
            {
                categories[orgUnitCount] = orgUnit.getName();
                String drillDownData = orgUnit.getId() + ":" + "0" + ":" + indicator.getId() + ":" + periodTypeLB + ":" + drillDownPeriodStartDate + ":" + drillDownPeriodEndDate + ":" + drillDownPeriodNames + ":" + aggChecked;
                selectedDrillDownData.add( drillDownData );

                Double aggIndicatorValue = 0.0;
                Double aggIndicatorNumValue = 0.0;
                Double aggIndicatorDenumValue = 0.0;

                int periodCount = 0;
                for ( Date startDate : selStartPeriodList )
                {
                    Date endDate = selEndPeriodList.get( periodCount );

                    if ( aggDataCB != null )
                    {
                        Double tempAggIndicatorNumValue = aggregationService.getAggregatedNumeratorValue( indicator,
                            startDate, endDate, orgUnit );
                        Double tempAggIndicatorDenumValue = aggregationService.getAggregatedDenominatorValue(
                            indicator, startDate, endDate, orgUnit );

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

                        String tempStr = reportService.getIndividualResultDataValue( indicator.getNumerator(),
                            startDate, endDate, orgUnit, "" );
                        try
                        {
                            tempAggIndicatorNumValue = Double.parseDouble( tempStr );
                        }
                        catch ( Exception e )
                        {
                            tempAggIndicatorNumValue = 0.0;
                        }
                        aggIndicatorNumValue += tempAggIndicatorNumValue;

                        // System.out.println( " \nAggIndicator Num Value : " +
                        // aggIndicatorNumValue );

                        Double tempAggIndicatorDenumValue = 0.0;

                        tempStr = reportService.getIndividualResultDataValue( indicator.getDenominator(), startDate,
                            endDate, orgUnit, "" );
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
                        
                        // System.out.println( " \nAggIndicator Denum Value : "
                        // + aggIndicatorDenumValue );

                    }

                    periodCount++;
                }

                try
                {
                    // aggIndicatorValue = ( aggIndicatorNumValue /
                    // aggIndicatorDenumValue )*
                    // indicator.getIndicatorType().getFactor();
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
                data[serviceCount][orgUnitCount] = aggIndicatorValue;
                data[serviceCount][orgUnitCount] = Math.round( data[serviceCount][orgUnitCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );

                numDataArray[serviceCount][orgUnitCount] = aggIndicatorNumValue;
                numDataArray[serviceCount][orgUnitCount] = Math.round( numDataArray[serviceCount][orgUnitCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );
                
                denumDataArray[serviceCount][orgUnitCount] = aggIndicatorDenumValue;
                denumDataArray[serviceCount][orgUnitCount] = Math.round( denumDataArray[serviceCount][orgUnitCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );

                // data[serviceCount][orgUnitCount] = aggDataValue;
                orgUnitCount++;

            }

            serviceCount++;
        }

        indicatorChartResult = new IndicatorChartResult( series, categories, data, numDataArray, denumDataArray, chartTitle, xAxis_Title, yAxis_Title );
        return indicatorChartResult;
    }

    // -------------------------------------------------------------------------
    // Methods for getting Chart Data With Selected Wise end
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Methods for getting Chart Data OrgGroup Period Wise start
    // -------------------------------------------------------------------------

    public IndicatorChartResult generateChartDataOrgGroupPeriodWise( List<Date> selStartPeriodList,
        List<Date> selEndPeriodList, List<String> periodNames, List<Indicator> indicatorList,
        List<OrganisationUnit> selOUGroupMemberList )
        throws Exception
    {
        IndicatorChartResult indicatorChartResult;

        String[] series = new String[indicatorList.size()];
        String[] categories = new String[selStartPeriodList.size()];

        Double[][] numDataArray = new Double[indicatorList.size()][selStartPeriodList.size()];
        Double[][] denumDataArray = new Double[indicatorList.size()][selStartPeriodList.size()];
        Double[][] data = new Double[indicatorList.size()][selStartPeriodList.size()];

        String chartTitle = "OrganisationUnit : " + selectedOrgUnit.getShortName() + "( Group - " + selOrgUnitGroup.getName() + " )";
        String xAxis_Title = "Time Line";
        String yAxis_Title = "Value";

        int serviceCount = 0;

        for ( Indicator indicator : indicatorList )
        {
            series[serviceCount] = indicator.getName();
            yseriesList.add( indicator );

            numeratorDEList.add( indicator.getNumeratorDescription() );
            denominatorDEList.add( indicator.getDenominatorDescription() );

            Double aggIndicatorValue = 0.0;
            Double aggIndicatorNumValue = 0.0;
            Double aggIndicatorDenumValue = 0.0;

            int periodCount = 0;
            for ( Date startDate : selStartPeriodList )
            {
                Date endDate = selEndPeriodList.get( periodCount );
                categories[periodCount] = periodNames.get( periodCount );
                
                String tempStartDate = format.formatDate( startDate );
                String tempEndDate   = format.formatDate( endDate );
                String drillDownPeriodName = periodNames.get( periodCount );
                
                
                String drillDownData = selectedOrgUnit.getId() + ":"+ selOrgUnitGroup.getId() + ":" + indicator.getId() + ":"  + periodTypeLB + ":" + tempStartDate + ":" + tempEndDate + ":" + drillDownPeriodName + ":" + aggChecked;
                //selectedDrillDownData
                selectedDrillDownData.add( drillDownData );
                int orgGroupCount = 0;

                for ( OrganisationUnit orgUnit : selOUGroupMemberList )
                {
                    if ( aggDataCB != null )
                    {
                        Double tempAggIndicatorNumValue = aggregationService.getAggregatedNumeratorValue( indicator,
                            startDate, endDate, orgUnit );
                        Double tempAggIndicatorDenumValue = aggregationService.getAggregatedDenominatorValue(
                            indicator, startDate, endDate, orgUnit );

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

                        String tempStr = reportService.getIndividualResultDataValue( indicator.getNumerator(),
                            startDate, endDate, orgUnit, "" );
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

                        // tempStr =
                        // reportService.getIndividualResultIndicatorValue(
                        // indicator.getDenominator(), startDate, endDate,
                        // orgUnit );
                        tempStr = reportService.getIndividualResultDataValue( indicator.getDenominator(), startDate,
                            endDate, orgUnit, "" );
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
                    // aggIndicatorValue = ( aggIndicatorNumValue /
                    // aggIndicatorDenumValue )*
                    // indicator.getIndicatorType().getFactor();
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

        indicatorChartResult = new IndicatorChartResult( series, categories, data, numDataArray, denumDataArray,
            chartTitle, xAxis_Title, yAxis_Title );
        return indicatorChartResult;

    }

    // -------------------------------------------------------------------------
    // Methods for getting Chart Data OrgGroup Period Wise end
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Methods for getting Chart Data OrgGroup Selected orgUnit Wise start
    // -------------------------------------------------------------------------

    public IndicatorChartResult generateChartDataSelectedOrgUnitGroupWise( List<Date> selStartPeriodList,
        List<Date> selEndPeriodList, List<String> periodNames, List<Indicator> indicatorList,
        Map<OrganisationUnitGroup, List<OrganisationUnit>> orgUnitGroupMap )
        throws Exception
    {
        IndicatorChartResult indicatorChartResult;

        String[] series = new String[indicatorList.size()];
        String[] categories = new String[orgUnitGroupMap.keySet().size()];

        Double[][] numDataArray = new Double[indicatorList.size()][orgUnitGroupMap.keySet().size()];
        Double[][] denumDataArray = new Double[indicatorList.size()][orgUnitGroupMap.keySet().size()];
        Double[][] data = new Double[indicatorList.size()][orgUnitGroupMap.keySet().size()];

        // Double[][] data = new
        // Double[dataElementList.size()][orgUnitGroupMap.keySet().size()];
        String chartTitle = "OrganisationUnit : " + selectedOrgUnit.getShortName() + " - ";
        String xAxis_Title = "Organisation Unit Group";
        String yAxis_Title = "Value";

        int serviceCount = 0;

        for ( Indicator indicator : indicatorList )
        {

            series[serviceCount] = indicator.getName();

            yseriesList.add( indicator );

            numeratorDEList.add( indicator.getNumeratorDescription() );
            denominatorDEList.add( indicator.getDenominatorDescription() );

            int orgGroupCount = 0;
            for ( OrganisationUnitGroup orgUnitGroup : orgUnitGroupMap.keySet() )
            {
                Double aggIndicatorValue = 0.0;
                Double aggIndicatorNumValue = 0.0;
                Double aggIndicatorDenumValue = 0.0;

                categories[orgGroupCount] = orgUnitGroup.getName();
                
                String drillDownData = selectedOrgUnit.getId() + ":" + orgUnitGroup.getId() + ":" + indicator.getId() + ":" + periodTypeLB + ":" + drillDownPeriodStartDate + ":" + drillDownPeriodEndDate + ":" + drillDownPeriodNames + ":"  + aggChecked;
                selectedDrillDownData.add( drillDownData );

                if ( serviceCount == 0 )
                {
                    chartTitle += orgUnitGroup.getName() + ",";
                }
                Collection<OrganisationUnit> orgUnitGroupMembers = orgUnitGroupMap.get( orgUnitGroup );
                if ( orgUnitGroupMembers == null || orgUnitGroupMembers.size() == 0 )
                {
                    numDataArray[serviceCount][orgGroupCount] = aggIndicatorNumValue;
                    denumDataArray[serviceCount][orgGroupCount] = aggIndicatorDenumValue;
                    data[serviceCount][orgGroupCount] = aggIndicatorValue;

                    orgGroupCount++;
                    continue;
                }
                for ( OrganisationUnit orgUnit : orgUnitGroupMembers )
                {
                    int periodCount = 0;
                    for ( Date startDate : selStartPeriodList )
                    {
                        Date endDate = selEndPeriodList.get( periodCount );

                        if ( aggDataCB != null )
                        {
                            Double tempAggIndicatorNumValue = aggregationService.getAggregatedNumeratorValue(
                                indicator, startDate, endDate, orgUnit );
                            Double tempAggIndicatorDenumValue = aggregationService.getAggregatedDenominatorValue(
                                indicator, startDate, endDate, orgUnit );

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

                            // System.out.println(
                            // " \ninside aggdata not checked" );
                            Double tempAggIndicatorNumValue = 0.0;

                            String tempStr = reportService.getIndividualResultDataValue( indicator.getNumerator(),
                                startDate, endDate, orgUnit, "" );
                            try
                            {
                                tempAggIndicatorNumValue = Double.parseDouble( tempStr );
                            }
                            catch ( Exception e )
                            {
                                tempAggIndicatorNumValue = 0.0;
                            }
                            aggIndicatorNumValue += tempAggIndicatorNumValue;
                            // System.out.println(
                            // " \nAggIndicator Num Value : " +
                            // aggIndicatorNumValue );

                            Double tempAggIndicatorDenumValue = 0.0;

                            tempStr = reportService.getIndividualResultDataValue( indicator.getDenominator(),
                                startDate, endDate, orgUnit, "" );
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
                            
                            // System.out.println(
                            // " \nAggIndicator Denum Value : " +
                            // aggIndicatorDenumValue );
                        }

                        periodCount++;
                    }

                }// orgunit member
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
                data[serviceCount][orgGroupCount] = aggIndicatorValue;
                data[serviceCount][orgGroupCount] = Math.round( data[serviceCount][orgGroupCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );

                numDataArray[serviceCount][orgGroupCount] = aggIndicatorNumValue;
                numDataArray[serviceCount][orgGroupCount] = Math.round( numDataArray[serviceCount][orgGroupCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );
                
                denumDataArray[serviceCount][orgGroupCount] = aggIndicatorDenumValue;
                denumDataArray[serviceCount][orgGroupCount] = Math.round( denumDataArray[serviceCount][orgGroupCount] * Math.pow( 10, 1 ) )/ Math.pow( 10, 1 );
                
                orgGroupCount++;
            }

            serviceCount++;
        }

        indicatorChartResult = new IndicatorChartResult( series, categories, data, numDataArray, denumDataArray,
            chartTitle, xAxis_Title, yAxis_Title );

        return indicatorChartResult;

    }
    // -------------------------------------------------------------------------
    // Methods for getting Chart Data OrgGroup Selected orgUnit Wise end
    // -------------------------------------------------------------------------

}