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
 * @version GenerateDrillDownPeriodToOrgChildChartIndicatorResultAction.java Jan 7, 2011 3:00:03 PM
 */
public class GenerateDrillDownPeriodToOrgChildChartIndicatorResultAction implements Action
{
    // private final String PERIODWISE = "period";

    private final String CHILDREN = "children";

    private final String SELECTED = "random";
    
    //private final String OPTIONCOMBO = "optioncombo";
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

    private Indicator indicator;

    //private DataElementCategoryOptionCombo categoryCombo;
    
    private HttpSession session;

    public HttpSession getSession()
    {
        return session;
    }
    
    private List<Date> selStartPeriodList;

    private List<Date> selEndPeriodList;
    
    private String selectedButton;
    
    public String getSelectedButton()
    {
        return selectedButton;
    }

    public void setSelectedButton( String selectedButton )
    {
        this.selectedButton = selectedButton;
    }
    
    public String[] startDateArray;
    public String[] endDateArray;
    public String[] priodNameArray;
    
    private  List<String> periodNames;
    
    private String categoryLB;

    public void setCategoryLB( String categoryLB )
    {
        this.categoryLB = categoryLB;
    }

    public String getCategoryLB()
    {
        return categoryLB;
    }
    private List<String> selectedDrillDownData;
    
    public List<String> getSelectedDrillDownData()
    {
        return selectedDrillDownData;
    }
    
   // private String drillDownPeriodStartDate;
   // private String drillDownPeriodEndDate;
   
    //private String drillDownPeriodNames;
    
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
    
    private String drillDownPeriodStartDate;
    private String drillDownPeriodEndDate;
    private String drillDownPeriodNames;
    
    // -------------------------------------------------------------------------
    // Action implements
    // -------------------------------------------------------------------------
    
    public String execute()throws Exception
    {
        
        statementManager.initialise();
        
        selectedDrillDownData = new ArrayList<String>();//drillDown for periodWise to OrgChild wise indicator Data
       
        listTool = new ListTool();
        
        values = selDrillDownData.split( ":" );
        
        int orgunit =Integer.parseInt( values[0] );
        int orgUnitGroup = Integer.parseInt( values[1]);
        //System.out.println( " Group Id is " + orgUnitGroup );
        
        if ( orgUnitGroup != 0 )
        {
            selectedOrgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgUnitGroup );
            //System.out.println( " Group Name is " + selectedOrgUnitGroup.getName() );
        }
        
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( orgunit );
        
        int indicatorId = Integer.parseInt( values[2] );
        indicator = indicatorService.getIndicator( indicatorId );
        List<Indicator> indicatorList = new ArrayList<Indicator>();
        indicatorList.add( indicator ); 
        
        
        
        String periodTypeLB = values[3];
       // String startD = values[5];
        //String endD = values[6];
        
        selStartPeriodList = new ArrayList<Date>();
        selEndPeriodList = new ArrayList<Date>();
        periodNames = new ArrayList<String>();
        
        drillDownPeriodStartDate = values[4];
        drillDownPeriodEndDate = values[5];
        drillDownPeriodNames = values[6];
        
        
        
        startDateArray = values[4].split( ";" );
        //String startDateArray[] = startDateString.split(";");  
        
        for ( int i = 0 ; i < startDateArray.length ; i++ )
        {
            String startD = startDateArray[i];
            selStartPeriodList.add( format.parseDate( startD ) );
           // System.out.println( "Start date " + startD );
        }
        
        //String endDateString = values[6];
        endDateArray = values[5].split( ";" );
        
        for ( int i = 0 ; i < endDateArray.length ; i++ )
        {
            String startD = endDateArray[i];
            selEndPeriodList.add( format.parseDate( startD ) );
           // System.out.println( "End date " + startD );
        }
       // selStartPeriodList.add( format.parseDate( startD ) );
        //selEndPeriodList.add( format.parseDate( endD ) );
        
        priodNameArray = values[6].split( ";" );
        
        for ( int i = 0 ; i < priodNameArray.length ; i++ )
        {
            String periodName = priodNameArray[i];
            periodNames.add( periodName );
        }
       // selStartPeriodList.add( format.parseDate( startD ) );
        //selEndPeriodList.add( format.parseDate( endD ) );
        
       
        String aggDataCB = values[7];
        
        //String drillDownData = orgUnit.getId() + ":"+ dataElement.getId() + ":"+ decoc.getId() + ":"  + periodType + ":" + tempStartDate + ":" + tempEndDate + ":" + deSelection + ":" + aggDataCB;
        
        //System.out.println( selStartPeriodList + ":" + selEndPeriodList + ":" + periodTypeLB + ":" +  indicatorList+ ":" + selectedOrgUnit + ":" + aggDataCB );
        System.out.println( "Chart Generation Start Time is for drillDown: \t" + new Date() );
       
        
        if( orgUnitGroup == 0 && ( categoryLB.equalsIgnoreCase( CHILDREN ) || ( categoryLB.equalsIgnoreCase( SELECTED )) )) 
        {
           List<OrganisationUnit> childOrgUnitList = new ArrayList<OrganisationUnit>();
           childOrgUnitList = new ArrayList<OrganisationUnit>( selectedOrgUnit.getChildren());
           
           for( OrganisationUnit orgChild : childOrgUnitList )
           {
               String drillDownData = orgChild.getId() + ":" + "0" + ":" + indicator.getId() + ":" + periodTypeLB + ":" + drillDownPeriodStartDate + ":" + drillDownPeriodEndDate + ":" + drillDownPeriodNames + ":" + aggDataCB;
               selectedDrillDownData.add( drillDownData );
           }
        }
       
        if ( orgUnitGroup != 0 && ( categoryLB.equalsIgnoreCase( CHILDREN ) || ( categoryLB.equalsIgnoreCase( SELECTED )) )) 
        {
           // List<OrganisationUnit> childOrgUnitList = new ArrayList<OrganisationUnit>();
            //childOrgUnitList = new ArrayList<OrganisationUnit>( selectedOrgUnit.getChildren());
            
            List<OrganisationUnit> selectedOUGroupMemberList = new ArrayList<OrganisationUnit>( selectedOrgUnitGroup.getMembers() );
            
            List<OrganisationUnit> childOrgUnitList = new ArrayList<OrganisationUnit>();
            childOrgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selectedOrgUnit.getId() ) );
           
            selectedOUGroupMemberList.retainAll( childOrgUnitList );
            
           for( OrganisationUnit orgChild : selectedOUGroupMemberList )
           {
                String drillDownData = orgChild.getId() + ":" + selectedOrgUnitGroup.getId() + ":" + indicator.getId() + ":" + periodTypeLB + ":" + drillDownPeriodStartDate + ":" + drillDownPeriodEndDate + ":" + drillDownPeriodNames + ":"  + aggDataCB;
                selectedDrillDownData.add( drillDownData );
           }
            
        } 
/*
        if ( orgUnitGroup != 0 &&  categoryLB.equals( SELECTED ) )
           {
               String drillDownData = selectedOrgUnit.getId() + ":" + selectedOrgUnitGroup.getId() + ":" + dataElement.getId() + ":"+ categoryCombo.getId() + ":"  + periodTypeLB + ":" + drillDownPeriodStartDate + ":" + drillDownPeriodEndDate + ":" + drillDownPeriodNames + ":" + deSelection + ":" + aggDataCB;
               selectedDrillDownData.add( drillDownData );
            
           }
            
*/       
        if ( orgUnitGroup != 0 )
        {
            indicatorChartResult = dashBoardService.generateIndicatorChartDataWithGroupMemberWise( selStartPeriodList, selEndPeriodList, periodTypeLB, indicatorList,  selectedOrgUnit, selectedOrgUnitGroup ,aggDataCB );
        }
        else
        {
            indicatorChartResult = dashBoardService.generateIndicatorChartDataWithChildrenWise( selStartPeriodList, selEndPeriodList, periodTypeLB, indicatorList,selectedOrgUnit, aggDataCB );
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


