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
import org.hisp.dhis.caseaggregation.CaseAggregationCondition;
import org.hisp.dhis.caseaggregation.CaseAggregationConditionService;
import org.hisp.dhis.dataanalyser.util.DataElementChartResult;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.FinancialAprilPeriodType;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.hisp.dhis.period.SixMonthlyPeriodType;
import org.hisp.dhis.period.WeeklyPeriodType;
import org.hisp.dhis.period.YearlyPeriodType;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;

/**
 * @author Mithilesh Kumar Thakur
 * 
 * @version GenerateChartDataElementAction.java Oct 25, 2010 12:20:22 PM
 */
public class GenerateChartDataElementAction
    implements Action
{

    private final String PERIODWISE = "period";

    private final String CHILDREN = "children";

    private final String SELECTED = "random";

    private final String OPTIONCOMBO = "optioncombo";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
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

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
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

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private CaseAggregationConditionService caseAggregationConditionService;

    public void setCaseAggregationConditionService( CaseAggregationConditionService caseAggregationConditionService )
    {
        this.caseAggregationConditionService = caseAggregationConditionService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
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

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    //private List<String> selectedDataElements;
    
    List<String> selectedDataElements = new ArrayList<String>();

    public void setSelectedDataElements( List<String> selectedDataElements )
    {
        this.selectedDataElements = selectedDataElements;
    }

    private String deSelection;

    public void setDeSelection( String deSelection )
    {
        this.deSelection = deSelection;
    }

    public String getDeSelection()
    {
        return deSelection;
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

    public String getAggDataCB()
    {
        return aggDataCB;
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

    List<String> periodLB = new ArrayList<String>();
    //private List<String> periodLB;

    public void setPeriodLB( List<String> periodLB )
    {
        this.periodLB = periodLB;
    }

    private DataElementChartResult dataElementChartResult;

    public DataElementChartResult getDataElementChartResult()
    {
        return dataElementChartResult;
    }

    List<String> yseriesList;

    public List<String> getYseriesList()
    {
        return yseriesList;
    }

    private List<String> selectedValues;

    public List<String> getSelectedValues()
    {
        return selectedValues;
    }

    private List<String> selectedStatus;

    public List<String> getSelectedStatus()
    {
        return selectedStatus;
    }

    private List<String> selectedDrillDownData;

    public List<String> getSelectedDrillDownData()
    {
        return selectedDrillDownData;
    }

    ListTool listTool;

    public ListTool getListTool()
    {
        return listTool;
    }

    private List<OrganisationUnit> selOUList;

    private List<DataElementCategoryOptionCombo> selectedOptionComboList;

    private OrganisationUnit selectedOrgUnit;

    private OrganisationUnitGroup selOrgUnitGroup;

    private List<OrganisationUnit> selOUGroupMemberList = new ArrayList<OrganisationUnit>();

    private List<String> periodNames;

    private List<Date> selStartPeriodList;

    private List<Date> selEndPeriodList;

    private String drillDownPeriodStartDate;

    private String drillDownPeriodEndDate;

    private String drillDownPeriodNames;

    private String aggChecked;

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        statementManager.initialise();

        listTool = new ListTool();
        selectedOptionComboList = new ArrayList<DataElementCategoryOptionCombo>();
        selOUList = new ArrayList<OrganisationUnit>();
        selStartPeriodList = new ArrayList<Date>();
        selEndPeriodList = new ArrayList<Date>();
        yseriesList = new ArrayList<String>();
        selectedValues = new ArrayList<String>();
        selectedStatus = new ArrayList<String>();
        selectedDrillDownData = new ArrayList<String>();
        
        
        if( aggDataCB.equalsIgnoreCase( "false" ))
        {
            aggDataCB = null;
        }
        
        aggChecked = "";

        if ( aggDataCB != null )
        {
            aggChecked = "1";
        }
        else
        {
            aggChecked = "0";
        }
        
        System.out.println( orgUnitListCB );
        
      
        System.out.println( ougGroupSetCB );
        
        if( ougGroupSetCB.equalsIgnoreCase( "false" ))
        {
            ougGroupSetCB = null;
        }
        
        System.out.println( "selectedDataElements= " + selectedDataElements + "orgUnitGroupList= " + orgUnitGroupList + "orgUnitListCB= " + orgUnitListCB );
        System.out.println( "yearLB= " + yearLB + "periodLB= " + periodLB + "deSelection= " + deSelection );
        System.out.println( "categoryLB= " + categoryLB + "periodTypeLB= " + periodTypeLB + "ougGroupSetCB= " + ougGroupSetCB );
        System.out.println( "aggDataCB= " + aggDataCB );
        
        // ----------------------------------------------------------------------
        // Period Info
        // ----------------------------------------------------------------------
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

        if ( periodTypeLB.equalsIgnoreCase( WeeklyPeriodType.NAME ) )
        {
            int periodCount = 0;
            for ( String periodStr : periodLB )
            {
                //startD = periodStr.split( "To" )[0].trim();
                //endD = periodStr.split( "To" )[1].trim();
                
                String  startWeekDate = periodStr.split( "To" )[0] ; //for start week
                String  endWeekDate = periodStr.split( "To" )[1] ; //for end week
                
                startD = startWeekDate.trim();
                endD = endWeekDate.trim();
                
                
                if ( periodCount == periodLB.size() - 1 )
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
        {
            int periodCount = 0;
            for ( String year : yearLB )
            {
                int selYear = Integer.parseInt( year );
                if ( periodTypeLB.equalsIgnoreCase( YearlyPeriodType.NAME ) )
                {
                    startD = "" + selYear + "-01-01";
                    endD = "" + selYear + "-12-31";

                    if ( periodCount == yearLB.size() - 1 )
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
                        endD = "" + selYear + "-" + monthOrder[period] + "-" + monthDays[period];

                        if ( (((selYear) % 400 == 0) || (((selYear) % 100 != 0 && (selYear) % 4 == 0))) && period == 1 )
                        {
                            endD = "" + selYear + "-" + monthOrder[period] + "-" + (monthDays[period] + 1);
                        }

                        drillDownPeriodStartDate += startD + ";";
                        drillDownPeriodEndDate += endD + ";";
                        drillDownPeriodNames += simpleDateFormat.format( format.parseDate( startD ) ) + ";";
                        selStartPeriodList.add( format.parseDate( startD ) );
                        selEndPeriodList.add( format.parseDate( endD ) );
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

                        if ( periodCount == periodLB.size() - 1 )
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
                        String month = periodStr.split( "-" )[0];
                        String date = periodStr.split( "-" )[1];
                        startD = selYear + "-" + periodStr;
                        endD = selYear + "-" + periodStr;

                        if ( selYear % 4 != 0 && month.trim().equalsIgnoreCase( "02" )
                            && date.trim().equalsIgnoreCase( "29" ) )
                        {
                            startD = selYear + "-" + month + "-" + date;
                            endD = selYear + "-" + month + "-" + date;
                            continue;
                        }

                        if ( ((selYear % 400 == 0) || ((selYear % 100 != 0 && selYear % 4 == 0)))
                            && month.trim().equalsIgnoreCase( "02" ) && date.trim().equalsIgnoreCase( "29" ) )
                            ;
                        {
                            startD = selYear + "-" + month + "-" + date;
                            endD = selYear + "-" + month + "-" + date;
                        }

                        if ( periodCount == periodLB.size() - 1 )
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
                    }
                }
                periodCount++;
            }
        }

        // ----------------------------------------------------------------------
        // DataElement Information
        // ----------------------------------------------------------------------
        List<DataElement> dataElementList = new ArrayList<DataElement>();

        if ( deSelection == null )
        {
            System.out.println( "deOptionValue is null" );
            return null;
        }

        if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
        {
            System.out.println( "In side deOptionCombo "  + deSelection );
            Iterator<String> deIterator = selectedDataElements.iterator();
            while ( deIterator.hasNext() )
            {
                String serviceId = (String) deIterator.next();
                String partsOfServiceId[] = serviceId.split( ":" );
                int dataElementId = Integer.parseInt( partsOfServiceId[0] );
                DataElement dataElement = dataElementService.getDataElement( dataElementId );
                dataElementList.add( dataElement );
                int optionComboId = Integer.parseInt( partsOfServiceId[1] );
                DataElementCategoryOptionCombo decoc = dataElementCategoryService
                    .getDataElementCategoryOptionCombo( optionComboId );
                selectedOptionComboList.add( decoc );
            }
        }
        else
        {
            Iterator<String> deIterator = selectedDataElements.iterator();
            while ( deIterator.hasNext() )
            {
                int serviceID = Integer.parseInt( (String) deIterator.next() );
                DataElement dataElement = dataElementService.getDataElement( serviceID );
                dataElementList.add( dataElement );
            }
        }

        selectedServiceList = new ArrayList<Object>( dataElementList );
        
        System.out.println( "Size of selected OptionCombo List is " + selectedOptionComboList.size() );
        // ----------------------------------------------------------------------
        // OrgUnit Information
        // ----------------------------------------------------------------------
        for ( String ouStr : orgUnitListCB )
        {
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( ouStr ) );
            selOUList.add( orgUnit );
        }

        if ( categoryLB.equalsIgnoreCase( PERIODWISE ) && ougGroupSetCB == null )
        {
            System.out.println( "Chart Generation Start Time is : " + new Date() );

            dataElementChartResult = generateChartDataPeriodWise( selStartPeriodList, selEndPeriodList, periodNames,
                dataElementList, selectedOptionComboList, selOUList.iterator().next() );
        }
        else if ( categoryLB.equalsIgnoreCase( CHILDREN ) && ougGroupSetCB == null )
        {
            System.out.println( "Chart Generation Start Time is : " + new Date() );

            selectedOrgUnit = new OrganisationUnit();
            selectedOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );

            List<OrganisationUnit> childOrgUnitList = new ArrayList<OrganisationUnit>();
            childOrgUnitList = new ArrayList<OrganisationUnit>( selectedOrgUnit.getChildren() );

            dataElementChartResult = generateChartDataWithChildrenWise( selStartPeriodList, selEndPeriodList,
                periodNames, dataElementList, selectedOptionComboList, childOrgUnitList );
        }
        else if ( categoryLB.equalsIgnoreCase( SELECTED ) && ougGroupSetCB == null )
        {
            System.out.println( "Chart Generation Start Time is : " + new Date() );

            dataElementChartResult = generateChartDataSelectedOrgUnitWise( selStartPeriodList, selEndPeriodList,
                periodNames, dataElementList, selectedOptionComboList, selOUList );
        }
        else if ( categoryLB.equalsIgnoreCase( PERIODWISE ) && ougGroupSetCB != null )
        {
            System.out.println( "Chart Generation Start Time is : " + new Date() );

            selectedOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
            List<OrganisationUnit> orgUnitChildList = new ArrayList<OrganisationUnit>( organisationUnitService
                .getOrganisationUnitWithChildren( selectedOrgUnit.getId() ) );

            selOrgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( Integer.parseInt( orgUnitGroupList
                .get( 0 ) ) );
            selOUGroupMemberList = new ArrayList<OrganisationUnit>( selOrgUnitGroup.getMembers() );
            selOUGroupMemberList.retainAll( orgUnitChildList );

            dataElementChartResult = generateChartDataOrgGroupPeriodWise( selStartPeriodList, selEndPeriodList,
                periodNames, dataElementList, selectedOptionComboList, selOUGroupMemberList );
        }
        else if ( categoryLB.equalsIgnoreCase( CHILDREN ) && ougGroupSetCB != null )
        {
            System.out.println( "Chart Generation Start Time is : " + new Date() );

            selectedOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
            List<OrganisationUnit> orgUnitChildList = new ArrayList<OrganisationUnit>( organisationUnitService
                .getOrganisationUnitWithChildren( selectedOrgUnit.getId() ) );

            int groupCount = 0;
            for ( String orgUnitGroupId : orgUnitGroupList )
            {
                OrganisationUnitGroup selOrgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( Integer
                    .parseInt( orgUnitGroupId ) );
                List<OrganisationUnit> selectedOUGroupMemberList = new ArrayList<OrganisationUnit>( selOrgUnitGroup
                    .getMembers() );
                selOUGroupMemberList.addAll( selectedOUGroupMemberList );
                groupCount++;
            }

            selOUGroupMemberList.retainAll( orgUnitChildList );

            dataElementChartResult = generateChartDataSelectedOrgUnitWise( selStartPeriodList, selEndPeriodList,
                periodNames, dataElementList, selectedOptionComboList, selOUGroupMemberList );
        }
        else if ( categoryLB.equalsIgnoreCase( SELECTED ) && ougGroupSetCB != null )
        {
            System.out.println( "Chart Generation Start Time is : " + new Date() );

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
            }

            dataElementChartResult = generateChartDataSelectedOrgUnitGroupWise( selStartPeriodList, selEndPeriodList,
                periodNames, dataElementList, selectedOptionComboList, orgUnitGroupMap );
        }

        ActionContext ctx = ActionContext.getContext();
        HttpServletRequest req = (HttpServletRequest) ctx.get( ServletActionContext.HTTP_REQUEST );

        session = req.getSession();

        session.setAttribute( "data1", dataElementChartResult.getData() );
        session.setAttribute( "series1", dataElementChartResult.getSeries() );
        session.setAttribute( "categories1", dataElementChartResult.getCategories() );
        session.setAttribute( "chartTitle", dataElementChartResult.getChartTitle() );
        session.setAttribute( "xAxisTitle", dataElementChartResult.getXAxis_Title() );
        session.setAttribute( "yAxisTitle", dataElementChartResult.getYAxis_Title() );

        System.out.println( "Chart Generation End Time is : " + new Date() );
        statementManager.destroy();

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Methods for getting Chart Data only Period Wise start
    // -------------------------------------------------------------------------
    public DataElementChartResult generateChartDataPeriodWise( List<Date> selStartPeriodList,
        List<Date> selEndPeriodList, List<String> periodNames, List<DataElement> dataElementList,
        List<DataElementCategoryOptionCombo> decocList, OrganisationUnit orgUnit )
        throws Exception
    {
        DataElementChartResult dataElementChartResult;

        String[] series = new String[dataElementList.size()];
        String[] categories = new String[selStartPeriodList.size()];
        Double[][] data = new Double[dataElementList.size()][selStartPeriodList.size()];
        String chartTitle = "OrganisationUnit : " + orgUnit.getShortName();
        String xAxis_Title = "Time Line";
        String yAxis_Title = "Value";

        int serviceCount = 0;

        for ( DataElement dataElement : dataElementList )
        {
            DataElementCategoryOptionCombo decoc;

            DataElementCategoryCombo dataElementCategoryCombo = dataElement.getCategoryCombo();

            List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>(
                dataElementCategoryCombo.getOptionCombos() );

            if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
            {
                decoc = decocList.get( serviceCount );

                series[serviceCount] = dataElement.getName() + " : " + decoc.getName();
                
                CaseAggregationCondition caseAggregationCondition = caseAggregationConditionService
                    .getCaseAggregationCondition( dataElement, decoc );

                if ( caseAggregationCondition == null )
                {
                    selectedStatus.add( "no" );
                }
                else
                {
                    selectedStatus.add( "yes" );
                }

                yseriesList.add( dataElement.getName() + " : " + decoc.getName() );
            }
            else
            {
                decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
                series[serviceCount] = dataElement.getName();

                CaseAggregationCondition caseAggregationCondition = caseAggregationConditionService
                    .getCaseAggregationCondition( dataElement, decoc );

                if ( caseAggregationCondition == null )
                {
                    selectedStatus.add( "no" );
                }
                else
                {
                    selectedStatus.add( "yes" );
                }

                yseriesList.add( dataElement.getName() );
            }

            int periodCount = 0;
            for ( Date startDate : selStartPeriodList )
            {
                Date endDate = selEndPeriodList.get( periodCount );
                String drillDownPeriodName = periodNames.get( periodCount );
                String tempStartDate = format.formatDate( startDate );
                String tempEndDate = format.formatDate( endDate );

                categories[periodCount] = periodNames.get( periodCount );
                //PeriodType periodType = periodService.getPeriodTypeByName( periodTypeLB );

                String values = orgUnit.getId() + ":" + dataElement.getId() + ":" + decoc.getId() + ":" + periodTypeLB
                    + ":" + tempStartDate + ":" + tempEndDate;
                selectedValues.add( values );

                String drillDownData = orgUnit.getId() + ":" + "0" + ":" + dataElement.getId() + ":" + decoc.getId()
                    + ":" + periodTypeLB + ":" + tempStartDate + ":" + tempEndDate + ":" + drillDownPeriodName + ":"
                    + deSelection + ":" + aggChecked;
                selectedDrillDownData.add( drillDownData );

                Double aggDataValue = 0.0;
                
                if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
                {
                    //System.out.println( " Inside deSelection.equalsIgnoreCase( OPTIONCOMBO ) "  );
                    if ( aggDataCB != null )
                    {
                        Double temp = aggregationService.getAggregatedDataValue( dataElement, decoc, startDate,
                            endDate, orgUnit );
                        if ( temp != null )
                            aggDataValue += temp;
                            //aggDataValue = temp;
                    }
                    else
                    {
                        Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );
                        for ( Period period : periods )
                        {
                            DataValue dataValue = dataValueService.getDataValue( orgUnit, dataElement, period, decoc );
                            try
                            {
                                aggDataValue += Double.parseDouble( dataValue.getValue() );
                            }
                            catch ( Exception e )
                            {
                            }
                        }
                    }
                }
                else
                {
                    //System.out.println( " Inside not deSelection.equalsIgnoreCase( OPTIONCOMBO ) "  );
                    Iterator<DataElementCategoryOptionCombo> optionComboIterator = optionCombos.iterator();
                    while ( optionComboIterator.hasNext() )
                    {
                        DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator.next();

                        if ( aggDataCB != null )
                        {
                            Double temp = aggregationService.getAggregatedDataValue( dataElement, decoc1, startDate,
                                endDate, orgUnit );
                            if ( temp != null )
                                aggDataValue += temp;
                                //aggDataValue = temp;
                        }
                        else
                        {
                            Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );
                            for ( Period period : periods )
                            {
                                DataValue dataValue = dataValueService.getDataValue( orgUnit, dataElement, period, decoc1 );
                                try
                                {
                                    aggDataValue += Double.parseDouble( dataValue.getValue() );
                                }
                                catch ( Exception e )
                                {
                                }
                            }
                        }
                    }
                }
                //System.out.println( " Data is  : " + aggDataValue );
                data[serviceCount][periodCount] = aggDataValue;

                if ( dataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                {
                    if ( dataElement.getNumberType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                    {
                        data[serviceCount][periodCount] = Math.round( data[serviceCount][periodCount]
                            * Math.pow( 10, 0 ) )
                            / Math.pow( 10, 0 );
                    }
                    else
                    {
                        data[serviceCount][periodCount] = Math.round( data[serviceCount][periodCount]
                            * Math.pow( 10, 1 ) )
                            / Math.pow( 10, 1 );
                    }
                }
                periodCount++;
            }

            serviceCount++;
        }
        dataElementChartResult = new DataElementChartResult( series, categories, data, chartTitle, xAxis_Title,
            yAxis_Title );

        return dataElementChartResult;
    }

    // -------------------------------------------------------------------------
    // Methods for getting Chart Data With Children Wise start
    // -------------------------------------------------------------------------

    public DataElementChartResult generateChartDataWithChildrenWise( List<Date> selStartPeriodList,
        List<Date> selEndPeriodList, List<String> periodNames, List<DataElement> dataElementList,
        List<DataElementCategoryOptionCombo> decocList, List<OrganisationUnit> childOrgUnitList )
        throws Exception
    {
        DataElementChartResult dataElementChartResult;

        String[] series = new String[dataElementList.size()];
        String[] categories = new String[childOrgUnitList.size()];
        Double[][] data = new Double[dataElementList.size()][childOrgUnitList.size()];
        String chartTitle = "OrganisationUnit : " + selectedOrgUnit.getShortName();

        String xAxis_Title = "Facilities";
        String yAxis_Title = "Value";

        int serviceCount = 0;

        for ( DataElement dataElement : dataElementList )
        {
            DataElementCategoryOptionCombo decoc;

            DataElementCategoryCombo dataElementCategoryCombo = dataElement.getCategoryCombo();
            List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>(
                dataElementCategoryCombo.getOptionCombos() );

            if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
            {
                decoc = decocList.get( serviceCount );

                series[serviceCount] = dataElement.getName() + " : " + decoc.getName();
                yseriesList.add( dataElement.getName() + " : " + decoc.getName() );
            }
            else
            {
                decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
                series[serviceCount] = dataElement.getName();

                yseriesList.add( dataElement.getName() );
            }

            int childCount = 0;
            for ( OrganisationUnit orgChild : childOrgUnitList )
            {
                categories[childCount] = orgChild.getName();

                String drillDownData = orgChild.getId() + ":" + "0" + ":" + dataElement.getId() + ":" + decoc.getId()
                    + ":" + periodTypeLB + ":" + drillDownPeriodStartDate + ":" + drillDownPeriodEndDate + ":"
                    + drillDownPeriodNames + ":" + deSelection + ":" + aggChecked;
                selectedDrillDownData.add( drillDownData );

                Double aggDataValue = 0.0;

                int periodCount = 0;
                for ( Date startDate : selStartPeriodList )
                {
                    Date endDate = selEndPeriodList.get( periodCount );
                    Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );

                    if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
                    {
                        if ( aggDataCB != null )
                        {
                            Double tempAggDataValue = aggregationService.getAggregatedDataValue( dataElement, decoc, startDate, endDate, orgChild );
                            if ( tempAggDataValue != null )
                                aggDataValue += tempAggDataValue;
                        }
                        else
                        {
                            for ( Period period : periods )
                            {
                                DataValue dataValue = dataValueService.getDataValue( orgChild, dataElement, period, decoc );
                                try
                                {
                                    aggDataValue += Double.parseDouble( dataValue.getValue() );
                                }
                                catch ( Exception e )
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
                            DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator
                                .next();

                            if ( aggDataCB != null )
                            {
                                Double tempAggDataValue = aggregationService.getAggregatedDataValue( dataElement, decoc1,
                                    startDate, endDate, orgChild );
                                if ( tempAggDataValue != null )
                                    aggDataValue += tempAggDataValue;
                            }
                            else
                            {
                                for ( Period period : periods )
                                {
                                    DataValue dataValue = dataValueService.getDataValue( orgChild, dataElement, period,
                                        decoc1 );
                                    try
                                    {
                                        aggDataValue += Double.parseDouble( dataValue.getValue() );
                                    }
                                    catch ( Exception e )
                                    {
                                    }
                                }
                            }
                        }
                    }
                    periodCount++;
                }

                data[serviceCount][childCount] = aggDataValue;

                if ( dataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                {
                    if ( dataElement.getNumberType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                    {
                        data[serviceCount][childCount] = Math
                            .round( data[serviceCount][childCount] * Math.pow( 10, 0 ) )
                            / Math.pow( 10, 0 );
                    }
                    else
                    {
                        data[serviceCount][childCount] = Math
                            .round( data[serviceCount][childCount] * Math.pow( 10, 1 ) )
                            / Math.pow( 10, 1 );
                    }
                }
                childCount++;
            }

            serviceCount++;
        }

        dataElementChartResult = new DataElementChartResult( series, categories, data, chartTitle, xAxis_Title,
            yAxis_Title );
        return dataElementChartResult;
    }

    // -------------------------------------------------------------------------
    // Methods for getting Chart Data With Selected Wise start
    // -------------------------------------------------------------------------
    public DataElementChartResult generateChartDataSelectedOrgUnitWise( List<Date> selStartPeriodList,
        List<Date> selEndPeriodList, List<String> periodNames, List<DataElement> dataElementList,
        List<DataElementCategoryOptionCombo> decocList, List<OrganisationUnit> selOUList )
        throws Exception
    {
        DataElementChartResult dataElementChartResult;

        String[] series = new String[dataElementList.size()];
        String[] categories = new String[selOUList.size()];
        Double[][] data = new Double[dataElementList.size()][selOUList.size()];
        String chartTitle = "OrganisationUnit : -----";
        String xAxis_Title = "Facilities";
        String yAxis_Title = "Value";

        int serviceCount = 0;

        for ( DataElement dataElement : dataElementList )
        {
            DataElementCategoryOptionCombo decoc;
            DataElementCategoryCombo dataElementCategoryCombo = dataElement.getCategoryCombo();
            List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>(
                dataElementCategoryCombo.getOptionCombos() );

            if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
            {
                decoc = decocList.get( serviceCount );
                series[serviceCount] = dataElement.getName() + " : " + decoc.getName();
                yseriesList.add( dataElement.getName() + " : " + decoc.getName() );
            }
            else
            {
                decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
                series[serviceCount] = dataElement.getName();
                yseriesList.add( dataElement.getName() );
            }

            int orgUnitCount = 0;
            for ( OrganisationUnit orgunit : selOUList )
            {
                categories[orgUnitCount] = orgunit.getName();
                String drillDownData = orgunit.getId() + ":" + "0" + ":" + dataElement.getId() + ":" + decoc.getId()
                    + ":" + periodTypeLB + ":" + drillDownPeriodStartDate + ":" + drillDownPeriodEndDate + ":"
                    + drillDownPeriodNames + ":" + deSelection + ":" + aggChecked;
                selectedDrillDownData.add( drillDownData );

                Double aggDataValue = 0.0;

                int periodCount = 0;
                for ( Date startDate : selStartPeriodList )
                {
                    Date endDate = selEndPeriodList.get( periodCount );
                    Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );
                    
                    if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
                    {
                        if ( aggDataCB != null )
                        {
                            Double tempAggDataValue = aggregationService.getAggregatedDataValue( dataElement, decoc,
                                startDate, endDate, orgunit );
                            if ( tempAggDataValue != null )
                                aggDataValue += tempAggDataValue;
                        }
                        else
                        {
                            for ( Period period : periods )
                            {
                                DataValue dataValue = dataValueService.getDataValue( orgunit, dataElement, period,
                                    decoc );
                                try
                                {
                                    aggDataValue += Double.parseDouble( dataValue.getValue() );
                                }
                                catch ( Exception e )
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
                            DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator
                                .next();

                            if ( aggDataCB != null )
                            {
                                Double tempAggDataValue = aggregationService.getAggregatedDataValue( dataElement, decoc1,
                                    startDate, endDate, orgunit );
                                if ( tempAggDataValue != null )
                                    aggDataValue += tempAggDataValue;
                            }
                            else
                            {
                                for ( Period period : periods )
                                {
                                    DataValue dataValue = dataValueService.getDataValue( orgunit, dataElement, period,
                                        decoc1 );
                                    try
                                    {
                                        aggDataValue += Double.parseDouble( dataValue.getValue() );
                                    }
                                    catch ( Exception e )
                                    {
                                    }
                                }
                            }
                        }
                    }
                    periodCount++;
                }

                data[serviceCount][orgUnitCount] = aggDataValue;

                if ( dataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                {
                    if ( dataElement.getNumberType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                    {
                        data[serviceCount][orgUnitCount] = Math.round( data[serviceCount][orgUnitCount]
                            * Math.pow( 10, 0 ) )
                            / Math.pow( 10, 0 );
                    }
                    else
                    {
                        data[serviceCount][orgUnitCount] = Math.round( data[serviceCount][orgUnitCount]
                            * Math.pow( 10, 1 ) )
                            / Math.pow( 10, 1 );
                    }
                }
                orgUnitCount++;
            }
            serviceCount++;
        }

        dataElementChartResult = new DataElementChartResult( series, categories, data, chartTitle, xAxis_Title,
            yAxis_Title );

        return dataElementChartResult;
    }

    // -------------------------------------------------------------------------
    // Methods for getting Chart Data OrgGroup Period Wise start
    // -------------------------------------------------------------------------

    public DataElementChartResult generateChartDataOrgGroupPeriodWise( List<Date> selStartPeriodList,
        List<Date> selEndPeriodList, List<String> periodNames, List<DataElement> dataElementList,
        List<DataElementCategoryOptionCombo> decocList, List<OrganisationUnit> selOUGroupMemberList )
        throws Exception
    {
        DataElementChartResult dataElementChartResult;

        String[] series = new String[dataElementList.size()];
        String[] categories = new String[selStartPeriodList.size()];
        Double[][] data = new Double[dataElementList.size()][selStartPeriodList.size()];
        String chartTitle = "OrganisationUnit : " + selectedOrgUnit.getShortName() + "( Group- "
            + selOrgUnitGroup.getName() + " )";
        String xAxis_Title = "Time Line";
        String yAxis_Title = "Value";

        int serviceCount = 0;

        for ( DataElement dataElement : dataElementList )
        {
            DataElementCategoryOptionCombo decoc;
            DataElementCategoryCombo dataElementCategoryCombo = dataElement.getCategoryCombo();
            List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>(
                dataElementCategoryCombo.getOptionCombos() );

            if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
            {
                decoc = decocList.get( serviceCount );
                series[serviceCount] = dataElement.getName() + " : " + decoc.getName();
                yseriesList.add( dataElement.getName() + " : " + decoc.getName() );
            }
            else
            {
                decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
                series[serviceCount] = dataElement.getName();
                yseriesList.add( dataElement.getName() );
            }

            int periodCount = 0;
            for ( Date startDate : selStartPeriodList )
            {
                Date endDate = selEndPeriodList.get( periodCount );
                categories[periodCount] = periodNames.get( periodCount );
                Double aggDataValue = 0.0;

                String tempStartDate = format.formatDate( startDate );
                String tempEndDate = format.formatDate( endDate );
                String drillDownPeriodName = periodNames.get( periodCount );

                Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );
                String drillDownData = selectedOrgUnit.getId() + ":" + selOrgUnitGroup.getId() + ":"
                    + dataElement.getId() + ":" + decoc.getId() + ":" + periodTypeLB + ":" + tempStartDate + ":"
                    + tempEndDate + ":" + drillDownPeriodName + ":" + deSelection + ":" + aggChecked;
                selectedDrillDownData.add( drillDownData );

                int orgGroupCount = 0;
                for ( OrganisationUnit orgUnit : selOUGroupMemberList )
                {
                    
                    if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
                    {
                        if ( aggDataCB != null )
                        {
                            Double tempAggDataValue = aggregationService.getAggregatedDataValue( dataElement, decoc,
                                startDate, endDate, orgUnit );
                            if ( tempAggDataValue != null )
                                aggDataValue += tempAggDataValue;
                        }
                        else
                        {
                            for ( Period period : periods )
                            {
                                DataValue dataValue = dataValueService.getDataValue( orgUnit, dataElement, period,
                                    decoc );
                                try
                                {
                                    aggDataValue += Double.parseDouble( dataValue.getValue() );
                                }
                                catch ( Exception e )
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
                            DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator
                                .next();

                            if ( aggDataCB != null )
                            {
                                Double tempAggDataValue = aggregationService.getAggregatedDataValue( dataElement, decoc1,
                                    startDate, endDate, orgUnit );
                                if ( tempAggDataValue != null )
                                    aggDataValue += tempAggDataValue;
                            }
                            else
                            {
                                for ( Period period : periods )
                                {
                                    DataValue dataValue = dataValueService.getDataValue( orgUnit, dataElement, period,
                                        decoc1 );
                                    try
                                    {
                                        aggDataValue += Double.parseDouble( dataValue.getValue() );
                                    }
                                    catch ( Exception e )
                                    {
                                    }
                                }
                            }
                        }
                    }
                    orgGroupCount++;
                }

                data[serviceCount][periodCount] = aggDataValue;

                if ( dataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                {
                    if ( dataElement.getNumberType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                    {
                        data[serviceCount][periodCount] = Math.round( data[serviceCount][periodCount]
                            * Math.pow( 10, 0 ) )
                            / Math.pow( 10, 0 );
                    }
                    else
                    {
                        data[serviceCount][periodCount] = Math.round( data[serviceCount][periodCount]
                            * Math.pow( 10, 1 ) )
                            / Math.pow( 10, 1 );
                    }
                }
                periodCount++;
            }

            serviceCount++;
        }

        dataElementChartResult = new DataElementChartResult( series, categories, data, chartTitle, xAxis_Title,
            yAxis_Title );

        return dataElementChartResult;

    }

    // -------------------------------------------------------------------------
    // Methods for getting Chart Data OrgGroup Selected orgUnit Wise start
    // -------------------------------------------------------------------------

    public DataElementChartResult generateChartDataSelectedOrgUnitGroupWise( List<Date> selStartPeriodList,
        List<Date> selEndPeriodList, List<String> periodNames, List<DataElement> dataElementList,
        List<DataElementCategoryOptionCombo> decocList,
        Map<OrganisationUnitGroup, List<OrganisationUnit>> orgUnitGroupMap )
        throws Exception
    {
        DataElementChartResult dataElementChartResult;

        String[] series = new String[dataElementList.size()];
        String[] categories = new String[orgUnitGroupMap.keySet().size()];
        Double[][] data = new Double[dataElementList.size()][orgUnitGroupMap.keySet().size()];
        String chartTitle = "OrganisationUnit : " + selectedOrgUnit.getShortName() + " - ";
        String xAxis_Title = "Organisation Unit Group";
        String yAxis_Title = "Value";

        int serviceCount = 0;

        for ( DataElement dataElement : dataElementList )
        {
            DataElementCategoryOptionCombo decoc;
            DataElementCategoryCombo dataElementCategoryCombo = dataElement.getCategoryCombo();
            List<DataElementCategoryOptionCombo> optionCombos = new ArrayList<DataElementCategoryOptionCombo>(
                dataElementCategoryCombo.getOptionCombos() );

            if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
            {
                decoc = decocList.get( serviceCount );
                series[serviceCount] = dataElement.getName() + " : " + decoc.getName();
                yseriesList.add( dataElement.getName() + " : " + decoc.getName() );
            }
            else
            {
                decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
                series[serviceCount] = dataElement.getName();
                yseriesList.add( dataElement.getName() );
            }

            int orgGroupCount = 0;
            for ( OrganisationUnitGroup orgUnitGroup : orgUnitGroupMap.keySet() )
            {
                Double aggDataValue = 0.0;

                categories[orgGroupCount] = orgUnitGroup.getName();
                String drillDownData = selectedOrgUnit.getId() + ":" + orgUnitGroup.getId() + ":" + dataElement.getId()
                    + ":" + decoc.getId() + ":" + periodTypeLB + ":" + drillDownPeriodStartDate + ":"
                    + drillDownPeriodEndDate + ":" + drillDownPeriodNames + ":" + deSelection + ":" + aggChecked;
                selectedDrillDownData.add( drillDownData );

                if ( serviceCount == 0 )
                {
                    chartTitle += orgUnitGroup.getName() + ",";
                }
                Collection<OrganisationUnit> orgUnitGroupMembers = orgUnitGroupMap.get( orgUnitGroup );

                if ( orgUnitGroupMembers == null || orgUnitGroupMembers.size() == 0 )
                {
                    data[serviceCount][orgGroupCount] = aggDataValue;
                    orgGroupCount++;
                    continue;
                }
                for ( OrganisationUnit orgUnit : orgUnitGroupMembers )
                {
                    int periodCount = 0;
                    for ( Date startDate : selStartPeriodList )
                    {
                        Date endDate = selEndPeriodList.get( periodCount );

                        Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );
                        
                        if ( deSelection.equalsIgnoreCase( OPTIONCOMBO ) )
                        {
                            if ( aggDataCB != null )
                            {
                                Double tempAggDataValue = aggregationService.getAggregatedDataValue( dataElement,
                                    decoc, startDate, endDate, orgUnit );
                                if ( tempAggDataValue != null )
                                    aggDataValue += tempAggDataValue;
                            }
                            else
                            {
                                for ( Period period : periods )
                                {
                                    DataValue dataValue = dataValueService.getDataValue( orgUnit, dataElement, period,
                                        decoc );

                                    try
                                    {
                                        aggDataValue += Double.parseDouble( dataValue.getValue() );
                                    }
                                    catch ( Exception e )
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
                                DataElementCategoryOptionCombo decoc1 = (DataElementCategoryOptionCombo) optionComboIterator
                                    .next();

                                if ( aggDataCB != null )
                                {
                                    Double tempAggDataValue = aggregationService.getAggregatedDataValue( dataElement,
                                        decoc1, startDate, endDate, orgUnit );
                                    if ( tempAggDataValue != null )
                                        aggDataValue += tempAggDataValue;
                                }
                                else
                                {
                                    for ( Period period : periods )
                                    {
                                        DataValue dataValue = dataValueService.getDataValue( orgUnit, dataElement, period,
                                            decoc1 );

                                        try
                                        {
                                            aggDataValue += Double.parseDouble( dataValue.getValue() );
                                        }
                                        catch ( Exception e )
                                        {

                                        }
                                    }
                                }
                            }
                        }
                        periodCount++;
                    }
                }
                data[serviceCount][orgGroupCount] = aggDataValue;

                if ( dataElement.getType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                {
                    if ( dataElement.getNumberType().equalsIgnoreCase( DataElement.VALUE_TYPE_INT ) )
                    {
                        data[serviceCount][orgGroupCount] = Math.round( data[serviceCount][orgGroupCount]
                            * Math.pow( 10, 0 ) )
                            / Math.pow( 10, 0 );
                    }
                    else
                    {
                        data[serviceCount][orgGroupCount] = Math.round( data[serviceCount][orgGroupCount]
                            * Math.pow( 10, 1 ) )
                            / Math.pow( 10, 1 );
                    }
                }

                orgGroupCount++;
            }

            serviceCount++;
        }

        dataElementChartResult = new DataElementChartResult( series, categories, data, chartTitle, xAxis_Title,
            yAxis_Title );

        return dataElementChartResult;

    }

}
