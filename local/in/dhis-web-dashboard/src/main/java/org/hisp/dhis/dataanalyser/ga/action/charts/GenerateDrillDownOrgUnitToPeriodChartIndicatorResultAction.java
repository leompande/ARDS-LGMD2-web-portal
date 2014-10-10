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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.amplecode.quick.StatementManager;
import org.apache.struts2.ServletActionContext;
import org.apache.velocity.tools.generic.ListTool;
import org.hisp.dhis.dataanalyser.util.DashBoardService;
import org.hisp.dhis.dataanalyser.util.IndicatorChartResult;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version GenerateDrillDownOrgUnitToPeriodChartIndicatorResultAction.java Jan 8, 2011 5:49:39 PM
 */
public class GenerateDrillDownOrgUnitToPeriodChartIndicatorResultAction implements Action
{
    private final String PERIODWISE = "period";

    //private final String CHILDREN = "children";

   // private final String OPTIONCOMBO = "optioncombo";
    //private final String SELECTED = "random";
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

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private OrganisationUnitService organisationUnitService;

    public OrganisationUnitService getOrganisationUnitService()
    {
        return organisationUnitService;
    }

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private OrganisationUnitGroupService organisationUnitGroupService;
    
    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------
    
    private String categoryLB;

    public void setCategoryLB( String categoryLB )
    {
        this.categoryLB = categoryLB;
    }

    public String getCategoryLB()
    {
        return categoryLB;
    }

    /* 
    private String ougGroupSetCB;
    
    
    public void setOugGroupSetCB( String ougGroupSetCB )
    {
        this.ougGroupSetCB = ougGroupSetCB;
    }
    */
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    private String selDrillDownData;
    
    public void setSelDrillDownData( String selDrillDownData )
    {
        this.selDrillDownData = selDrillDownData;
    }

    public String[] values;
    
    ListTool listTool;

    public ListTool getListTool()
    {
        return listTool;
    }
    
    private IndicatorChartResult indicatorChartResult;

    public IndicatorChartResult getIndicatorChartResult()
    {
        return indicatorChartResult;
    }
    
    private OrganisationUnit selectedOrgUnit;
    
    private OrganisationUnitGroup selectedOrgUnitGroup;

    private HttpSession session;

    public HttpSession getSession()
    {
        return session;
    }
    
    private List<Date> selStartPeriodList;

    private List<Date> selEndPeriodList;
    
    private Indicator indicator;
    
    private String selectedButton;
    
    public String getSelectedButton()
    {
        return selectedButton;
    }

    public void setSelectedButton( String selectedButton )
    {
        this.selectedButton = selectedButton;
    }
   /*
    private String aggDataCB;
    
    public void setAggDataCB( String aggDataCB )
    {
        this.aggDataCB = aggDataCB;
    }
    
    public String getAggDataCB()
    {
        return aggDataCB;
    }
    */
    public String[] startDateArray;
    public String[] endDateArray;
    public String[] priodNameArray;
    
    private  List<String> periodNames;
    
    private List<String> selectedDrillDownData;
    
    public List<String> getSelectedDrillDownData()
    {
        return selectedDrillDownData;
    }
    
    //private String drillDownPeriodStartDate;
    //private String drillDownPeriodEndDate;
   // private String drillDownPeriodNames;
    
    // -------------------------------------------------------------------------
    // Action implements
    // -------------------------------------------------------------------------
    
    public String execute()throws Exception
    {
        System.out.println( "Inside Generate DrillDown OrgUnit To Period Indicator Chart  Result Action " );
        
        statementManager.initialise();
        
        selectedDrillDownData = new ArrayList<String>();//drillDown for periodWise
        listTool = new ListTool();
        
        values = selDrillDownData.split( ":" );
        
        int orgunit =Integer.parseInt( values[0] );
        int orgUnitGroup = Integer.parseInt( values[1]);
        //System.out.println( " Group Id is " + orgUnitGroup );
        if ( orgUnitGroup != 0 )
        {
            selectedOrgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgUnitGroup );
            System.out.println( " Group Name is " + selectedOrgUnitGroup.getName() );
        }
        
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( orgunit );
        
        int indicatorId = Integer.parseInt( values[2] );
        indicator = indicatorService.getIndicator( indicatorId );
        List<Indicator> indicatorList = new ArrayList<Indicator>();
        indicatorList.add( indicator );
        
        String periodTypeLB = values[3];
        
        selStartPeriodList = new ArrayList<Date>();
        selEndPeriodList = new ArrayList<Date>();
        periodNames = new ArrayList<String>();
        
       // drillDownPeriodStartDate = values[5];
        //drillDownPeriodEndDate = values[6];
       // drillDownPeriodNames = values[6];
        
        startDateArray = values[4].split( ";" );
        //String startDateArray[] = startDateString.split(";");  
        
        for ( int i = 0 ; i < startDateArray.length ; i++ )
        {
            String startD = startDateArray[i];
            selStartPeriodList.add( format.parseDate( startD ) );
            //System.out.println( "Start date " + startD );
        }
        
        //String endDateString = values[6];
        endDateArray = values[5].split( ";" );
        
        for ( int i = 0 ; i < endDateArray.length ; i++ )
        {
            String startD = endDateArray[i];
            selEndPeriodList.add( format.parseDate( startD ) );
            //System.out.println( "End date " + startD );
        }
       // selStartPeriodList.add( format.parseDate( startD ) );
        //selEndPeriodList.add( format.parseDate( endD ) );
        
        priodNameArray = values[6].split( ";" );
        
        for ( int i = 0 ; i < priodNameArray.length ; i++ )
        {
            String startD = priodNameArray[i];
            periodNames.add( startD );
        }
        String aggDataCB = values[7];
        
        //String drillDownData = orgUnit.getId() + ":"+ dataElement.getId() + ":"+ decoc.getId() + ":"  + periodType + ":" + tempStartDate + ":" + tempEndDate + ":" + deSelection + ":" + aggDataCB;
        
        //System.out.println( selStartPeriodList + ":" + selEndPeriodList + ":" + periodNames  + ":"  + periodTypeLB + ":" +  indicatorList+ ":" + selectedOrgUnit + ":" + aggDataCB );
       // System.out.println( selStartPeriodList.size() + ":" + selEndPeriodList.size() );
        System.out.println( "Chart Generation Start Time is for drillDown: \t" + new Date() );
       
        if( orgUnitGroup == 0 && ( categoryLB.equalsIgnoreCase( PERIODWISE )) ) 
        {
            int periodCount = 0;
            for( Date startDate : selStartPeriodList )
            {
               
                String drillDownPeriodName = periodNames.get( periodCount );
                Date endDate = selEndPeriodList.get( periodCount );
                String tempStartDate = format.formatDate( startDate );
                String tempEndDate   = format.formatDate( endDate );
                String drillDownData = selectedOrgUnit.getId() + ":" + "0" + ":" + indicator.getId() +  ":"  + periodTypeLB + ":" + tempStartDate + ":" + tempEndDate + ":" + drillDownPeriodName + ":" + aggDataCB;
                selectedDrillDownData.add( drillDownData );
                periodCount++;
            }
            //System.out.println( "hhhiiiiiiiiiiiiii-------------hhhhhhhhhh" );
            
        }
          
        if( orgUnitGroup != 0 && ( categoryLB.equalsIgnoreCase( PERIODWISE )) ) 
        {
            int periodCount = 0;
            for( Date startdate : selStartPeriodList )
            {
                String drillDownPeriodName = periodNames.get( periodCount );
                Date endDate = selEndPeriodList.get( periodCount );
                String tempStartDate = format.formatDate( startdate );
                String tempEndDate   = format.formatDate( endDate );
                String drillDownData = selectedOrgUnit.getId() + ":" + "0" + ":" + indicator.getId() + ":" + periodTypeLB + ":" + tempStartDate + ":" + tempEndDate + ":" + drillDownPeriodName + ":" + aggDataCB;
                selectedDrillDownData.add( drillDownData );
                periodCount++;
            }
        }
       
        
        if( orgUnitGroup != 0 )
        //if( orgUnitGroup != 0 && categoryLB.equalsIgnoreCase( SELECTED ) )
        {
            //System.out.println( "Inside the method when orgUnit view by selected and group checked" );
           
            indicatorChartResult = dashBoardService.generateIndicatorChartDataWithGroupToPeriodWise( selStartPeriodList, selEndPeriodList, periodNames ,periodTypeLB, indicatorList, selectedOrgUnit, selectedOrgUnitGroup ,aggDataCB );
        }
        else
        {
            indicatorChartResult = dashBoardService.generateIndicatorChartDataWithPeriodWise( selStartPeriodList, selEndPeriodList,periodNames, periodTypeLB, indicatorList, selectedOrgUnit, aggDataCB );
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
    }

    
}


