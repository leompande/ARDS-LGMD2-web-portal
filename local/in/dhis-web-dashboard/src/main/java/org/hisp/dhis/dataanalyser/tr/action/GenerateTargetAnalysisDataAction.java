package org.hisp.dhis.dataanalyser.tr.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.amplecode.quick.StatementManager;
import org.apache.struts2.ServletActionContext;
import org.apache.velocity.tools.generic.ListTool;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.dataanalyser.util.SurveyChartResult;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.detarget.DeTarget;
import org.hisp.dhis.detarget.DeTargetMember;
import org.hisp.dhis.detarget.DeTargetService;
import org.hisp.dhis.detargetdatavalue.DeTargetDataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.YearlyPeriodType;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;

public class GenerateTargetAnalysisDataAction implements Action
{
    // ---------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
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
    
    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DeTargetService deTargetService;

    public void setDeTargetService( DeTargetService deTargetService )
    {
        this.deTargetService = deTargetService;
    }

    private DeTargetDataValueService deTargetDataValueService;
    
    public void setDeTargetDataValueService( DeTargetDataValueService deTargetDataValueService )
    {
        this.deTargetDataValueService = deTargetDataValueService;
    }
    
    private OrganisationUnitGroupService organisationUnitGroupService;
    
    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    
    // ---------------------------------------------------------------
    // Input & Output
    // ---------------------------------------------------------------

    private SurveyChartResult surveyChartResult;
    
    public SurveyChartResult getSurveyChartResult()
    {
        return surveyChartResult;
    }


    private ListTool listTool;

    public ListTool getListTool()
    {
        return listTool;
    }

    private String availableDataElements;

    public void setAvailableDataElements( String availableDataElements )
    {
        this.availableDataElements = availableDataElements;
    }

    private int ouIDTB;

    public void setOuIDTB( int ouIDTB )
    {
        this.ouIDTB = ouIDTB;
    }

    private int sDateLB;

    public void setSDateLB( int dateLB )
    {
        sDateLB = dateLB;
    }

    private int eDateLB;

    public void setEDateLB( int dateLB )
    {
        eDateLB = dateLB;
    }

    private String ougGroupSetCB;

    public void setOugGroupSetCB( String ougGroupSetCB )
    {
        this.ougGroupSetCB = ougGroupSetCB;
    }

    private Integer orgUnitGroupList;
    
    public void setOrgUnitGroupList( Integer orgUnitGroupList )
    {
        this.orgUnitGroupList = orgUnitGroupList;
    }

    private String selButton;
    
    public void setSelButton( String selButton )
    {
        this.selButton = selButton;
    }

    private List<DeTargetMember> deTargetMemberList;

    private List<Period> monthlyPeriods;

    private OrganisationUnit selectedOrgUnit;

    private DataElement selectedDataElement;
    
    private DataElementCategoryOptionCombo selDECOptCombo;
    
    private Period startPeriod;
    
    private Period endPeriod;

    private String[] series1;
    private String[] categories1;
    private String[] series2;
    private String[] categories2;
    String chartTitle;
    String xAxis_Title;
    String yAxis_Title;
    Double data1[][];
    Double data2[][];
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        statementManager.initialise();

        listTool = new ListTool();
        
        // OrgUnit Related Info
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
        
        
        if( ougGroupSetCB.equalsIgnoreCase( "false" ))
        {
            ougGroupSetCB = null;
        }
        // Service Related Info
        String[] partsOfDEandOptionCombo = availableDataElements.split(":");
        selectedDataElement = dataElementService.getDataElement( Integer.parseInt( partsOfDEandOptionCombo[0] ) );
        selDECOptCombo = dataElementCategoryService.getDataElementCategoryOptionCombo( Integer.parseInt( partsOfDEandOptionCombo[1] ) );
        
        if( ougGroupSetCB != null )
        {
            OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgUnitGroupList );
            chartTitle = "Facility : " + selectedOrgUnit.getShortName() + " ( " + orgUnitGroup.getName() + " ) " ;
            chartTitle += "\nDataElement : " + selectedDataElement.getName() + " " + selDECOptCombo.getName();
        }
        else
        {
            chartTitle = "Facility : " + selectedOrgUnit.getShortName();
            chartTitle += "\nDataElement : " + selectedDataElement.getName() + " " + selDECOptCombo.getName();
        }
        
       // chartTitle = "Facility : " + selectedOrgUnit.getShortName();
       // chartTitle += "\nDataElement : " + selectedDataElement.getName() + " " + selDECOptCombo.getName();

        deTargetMemberList = new ArrayList<DeTargetMember>( deTargetService.getDeTargetsByDataElementAndCategoryOptionCombo( selectedDataElement, selDECOptCombo ) );

        // Period Related Info
        startPeriod = periodService.getPeriod( sDateLB );
        endPeriod = periodService.getPeriod( eDateLB );

        monthlyPeriods = new ArrayList<Period>( periodService.getPeriodsBetweenDates( new MonthlyPeriodType(), startPeriod.getStartDate(), endPeriod.getEndDate() ) );
        List<Period> yearlyPeriods = new ArrayList<Period>( periodService.getIntersectingPeriodsByPeriodType( new YearlyPeriodType(), startPeriod.getStartDate(), endPeriod.getEndDate() ) );
        
        data1 = getServiceValuesByPeriod();
        
        xAxis_Title = "Period";
        yAxis_Title = "DataElement";
       
        if( deTargetMemberList == null || deTargetMemberList.size() <= 0 )
        {
            data2 = new Double[1][monthlyPeriods.size()];
            series2 = new String[1];
            for ( int i = 0; i < data2.length; i++ )
            {
                series2[i] = "No Targets";
                
                for ( int j = 0; j < data2[i].length; j++ )
                {
                    data2[i][j] = 0.0;
                }
            }
        }
        else
        {
            data2 = new Double[deTargetMemberList.size()][monthlyPeriods.size()];
            series2 = new String[deTargetMemberList.size()];
            for ( int i = 0; i < data2.length; i++ )
            {
                DeTargetMember deTargetMember = deTargetMemberList.get( i );
                DeTarget deTarget = deTargetMember.getDetarget();
                
                Double deTargetAggVal = 0.0;
                for( Period period : yearlyPeriods )
                {
                    Double deTargetAggValue = deTargetDataValueService.getAggregatedDeTargetDataValue( selectedOrgUnit, deTarget, period, selectedDataElement, selDECOptCombo );
                 
                    if ( deTargetAggValue != null )
                    {
                        deTargetAggVal +=  deTargetAggValue;
                    }
                }
                
                deTargetAggVal = deTargetAggVal/ (12 * yearlyPeriods.size());
                deTargetAggVal = Math.round( deTargetAggVal * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                
                series2[i] = deTarget.getName();
                
                for ( int j = 0; j < data2[i].length; j++ )
                {
                    Double tempdeTargetAggVal = deTargetAggVal;
                    if( selButton.equalsIgnoreCase( "VIEWCCHART" ) )
                    {
                        tempdeTargetAggVal = deTargetAggVal * (j+1);
                        tempdeTargetAggVal = Math.round( tempdeTargetAggVal * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                    }
                    
                    data2[i][j] = tempdeTargetAggVal;

                    /*
                    if( selButton.equalsIgnoreCase( "VIEWCCHART" ) )
                    {
                        deTargetAggVal = deTargetAggVal * (j+1);
                        deTargetAggVal = Math.round( deTargetAggVal * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                    }
                    
                    data2[i][j] = deTargetAggVal;
                    */
                }
            }
        }
        
        surveyChartResult = new SurveyChartResult( series1, series2,categories1, categories2, data1, data2, null, null, chartTitle, xAxis_Title, yAxis_Title );
        
        ActionContext ctx = ActionContext.getContext();
        HttpServletRequest req = (HttpServletRequest) ctx.get( ServletActionContext.HTTP_REQUEST );

        HttpSession session = req.getSession();
        session.setAttribute( "data1", data1 );
        session.setAttribute( "data2", data2 );
        session.setAttribute( "series1", series1 );
        session.setAttribute( "categories1", categories1 );
        session.setAttribute( "series2", series2 );
        session.setAttribute( "categories2", categories2 );
        session.setAttribute( "chartTitle", chartTitle );
        session.setAttribute( "xAxisTitle", xAxis_Title );
        session.setAttribute( "yAxisTitle", yAxis_Title );
        
        statementManager.destroy();

        return SUCCESS;
    }// execute end
    
    public Double[][] getServiceValuesByPeriod()
    {
        Double[][] serviceValues = new Double[1][monthlyPeriods.size()];
        series1 = new String[1];
        categories1 = new String[monthlyPeriods.size()];
        categories2 = new String[monthlyPeriods.size()];
        
        int countForServiceList = 0;
        int countForPeriodList = 0;
        Double aggDataValue = 0.0;

        series1[countForServiceList] = selectedDataElement.getName();
        
        List<OrganisationUnit> orgUnitList = null;
        if( ougGroupSetCB != null )
        {
            OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgUnitGroupList );
            orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selectedOrgUnit.getId()) );
            orgUnitList.retainAll( orgUnitGroup.getMembers() );
        }

        Iterator<Period> periodListIterator = monthlyPeriods.iterator();
        while ( periodListIterator.hasNext() )
        {
            aggDataValue = 0.0;
            Period p = (Period) periodListIterator.next();

            if( ougGroupSetCB != null )
            {
                Double tempValue = 0.0;
                for( OrganisationUnit orgUnit : orgUnitList )
                {
                    if( selButton.equalsIgnoreCase( "VIEWCCHART" ) )
                    {
                        tempValue = aggregationService.getAggregatedDataValue( selectedDataElement, selDECOptCombo, startPeriod.getStartDate(), p.getEndDate(), orgUnit );
                    }
                    else
                    {
                        tempValue = aggregationService.getAggregatedDataValue( selectedDataElement, selDECOptCombo, p.getStartDate(), p.getEndDate(), orgUnit );
                    }
                    
                    if( tempValue != null ) aggDataValue += tempValue;
                }
            }
            else
            {
                if( selButton.equalsIgnoreCase( "VIEWCCHART" ) )
                {
                    aggDataValue = aggregationService.getAggregatedDataValue( selectedDataElement, selDECOptCombo, startPeriod.getStartDate(), p.getEndDate(), selectedOrgUnit );
                }
                else
                {
                    aggDataValue = aggregationService.getAggregatedDataValue( selectedDataElement, selDECOptCombo, p.getStartDate(), p.getEndDate(), selectedOrgUnit );
                }
            }
            
            if( aggDataValue == null ) aggDataValue = 0.0;
            
            serviceValues[countForServiceList][countForPeriodList] = aggDataValue;
            
            serviceValues[countForServiceList][countForPeriodList] = Math.round( serviceValues[countForServiceList][countForPeriodList] * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
            
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
            categories1[countForPeriodList] = simpleDateFormat.format( p.getStartDate() );
            categories2[countForPeriodList] = simpleDateFormat.format( p.getStartDate() );

            countForPeriodList++;
        }// periodList loop end

        return serviceValues;
    }// getServiceValues method end

}
