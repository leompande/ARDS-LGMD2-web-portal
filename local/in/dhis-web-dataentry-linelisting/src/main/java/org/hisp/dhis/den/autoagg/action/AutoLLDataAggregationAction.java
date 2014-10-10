package org.hisp.dhis.den.autoagg.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.den.api.LLDataSets;
import org.hisp.dhis.den.api.LLDataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

public class AutoLLDataAggregationAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private LLDataValueService lldataValueService;

    public void setLldataValueService( LLDataValueService lldataValueService )
    {
        this.lldataValueService = lldataValueService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer sDateLB;

    public void setSDateLB( Integer dateLB )
    {
        sDateLB = dateLB;
    }

    private Integer eDateLB;

    public void setEDateLB( Integer dateLB )
    {
        eDateLB = dateLB;
    }

    private Integer selOrgUnitId;
    
    public void setSelOrgUnitId( Integer selOrgUnitId )
    {
        this.selOrgUnitId = selOrgUnitId;
    }
    
    private String resultStatus;

    public String getResultStatus()
    {
        return resultStatus;
    }

    //private Map<Integer, String> llValueMap;

    //private Map<Integer, Integer> liDEMap;
    
    private Map<String, String> lldeValueMap;
    
    private OrganisationUnit selOrgUnit;
    
    private List<OrganisationUnit> orgUnitList;
    
    private List<Period> periodList;
    
    private String storedBy = "llagg";
    
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    
    public String execute() throws Exception
    {
    
        //llValueMap = new HashMap<Integer, String>();
        
        //liDEMap = new HashMap<Integer, Integer>();
        
        lldeValueMap = new HashMap<String, String>();
                
        resultStatus = " ";
        
        // Orgunit
        
        selOrgUnit = organisationUnitService.getOrganisationUnit( selOrgUnitId );
        
        orgUnitList = new ArrayList<OrganisationUnit>( getChildOrgUnitTree( selOrgUnit ) );
        
        // Period
        
        Period startPeriod = periodService.getPeriod( sDateLB );
        
        Period endPeriod = periodService.getPeriod( eDateLB );
        
        PeriodType monthlyPeriodType = new MonthlyPeriodType();
        
        periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( monthlyPeriodType, startPeriod.getStartDate(), endPeriod.getEndDate() ) );
       

        // Linelisting DataSets
        
        List<DataSet> llDataSets = new ArrayList<DataSet>();
        
        llDataSets.add( dataSetService.getDataSetByName( LLDataSets.LL_BIRTHS ) );
        llDataSets.add( dataSetService.getDataSetByName( LLDataSets.LL_DEATHS ) );
        llDataSets.add( dataSetService.getDataSetByName( LLDataSets.LL_MATERNAL_DEATHS ) );
        
        System.out.println( "Aggregation Start Time is : \t" + new Date() );
        
        // Calculation
        List<String> aggDeList;
        for( OrganisationUnit orgUnit : orgUnitList )
        {
            List<DataSet> orgUnitDataSets = new ArrayList<DataSet>( orgUnit.getDataSets() );
            
            int flag = 1;
            for( Period period : periodList )
            {
                // LLB
                if( orgUnitDataSets.contains( llDataSets.get( 0 ) ) )
                {
                    System.out.println( "inside ----- " + LLDataSets.LL_BIRTHS );
                    
                    lldeValueMap = new HashMap<String, String>( lldataValueService.processLineListBirths( orgUnit, period ) );
                    aggDeList = new ArrayList<String>( lldeValueMap.keySet() );
                    for ( String aggde : aggDeList )
                    {
                        String aggDeVal = lldeValueMap.get( aggde );
                        //if( Integer.parseInt( aggDeVal ) > 0 )
                        {
                            flag = 2;
                            saveData( orgUnit, period, aggde, aggDeVal );
                        }
                    }
                }
                
                //LLD
                if( orgUnitDataSets.contains( llDataSets.get( 1 ) ) )
                {
                    
                    System.out.println( "inside ----- " + LLDataSets.LL_DEATHS );
                    
                    lldeValueMap = new HashMap<String, String>( lldataValueService.processLineListDeaths( orgUnit, period ) );
                    aggDeList = new ArrayList<String>( lldeValueMap.keySet() );
                    for ( String aggde : aggDeList )
                    {
                        String aggDeVal = lldeValueMap.get( aggde );
                        //if( Integer.parseInt( aggDeVal ) > 0 )
                        {
                            flag = 2;
                            saveData( orgUnit, period, aggde, aggDeVal );
                        }
                    }
                }
                
                //LLMD
                if( orgUnitDataSets.contains( llDataSets.get( 2 ) ) )
                {    
                    System.out.println( "inside ----- " + LLDataSets.LL_MATERNAL_DEATHS );
                    
                    lldeValueMap = new HashMap<String, String>( lldataValueService.processLineListMaternalDeaths( orgUnit, period ) );
                    aggDeList = new ArrayList<String>( lldeValueMap.keySet() );
                    for ( String aggde : aggDeList )
                    {
                        String aggDeVal = lldeValueMap.get( aggde );
                        //if( Integer.parseInt( aggDeVal ) > 0 )
                        {
                            flag = 2;
                            saveData( orgUnit, period, aggde, aggDeVal );
                        }
                    }
                }
                
                if(flag != 1)
                {
                    //resultStatus += orgUnit.getName() + " : " + period.getStartDate() + " Imported.<br>";
                    
                    resultStatus += "<font color=red><strong>"+ orgUnit.getName()+ " :  From : " + period.getStartDate() + " To : "  + period.getEndDate() + " Imported.<br></font></strong>";
                }
            }
            
        }
        
        System.out.println( "Aggregation End  Time is : \t" + new Date() );
        
        return SUCCESS;
    }
    
    
    // Returns the OrgUnitTree for which Root is the orgUnit
    public List<OrganisationUnit> getChildOrgUnitTree( OrganisationUnit orgUnit )
    {
        List<OrganisationUnit> orgUnitTree = new ArrayList<OrganisationUnit>();
        orgUnitTree.add( orgUnit );

        List<OrganisationUnit> children = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
        Collections.sort( children, new IdentifiableObjectNameComparator() );

        Iterator<OrganisationUnit> childIterator = children.iterator();
        OrganisationUnit child;
        while ( childIterator.hasNext() )
        {
            child = (OrganisationUnit) childIterator.next();
            orgUnitTree.addAll( getChildOrgUnitTree( child ) );
        }
        return orgUnitTree;
    }// getChildOrgUnitTree end


    private void saveData( OrganisationUnit organisationUnit, Period period, String deString, String value )
    {
        String partsOfdeString[] = deString.split( ":" );

        int dataElementId = Integer.parseInt( partsOfdeString[0] );
        int optionComboId = Integer.parseInt( partsOfdeString[1] );

        DataElement dataElement = dataElementService.getDataElement( dataElementId );
        DataElementCategoryOptionCombo optionCombo = dataElementCategoryService
            .getDataElementCategoryOptionCombo( optionComboId );

        if ( dataElement == null || optionCombo == null )
        {

        }
        else
        {
            DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, period, optionCombo );
            if ( dataValue == null )
            {
                if ( value != null )
                {
                    dataValue = new DataValue( dataElement, period, organisationUnit, value, storedBy, new Date(),
                        null, optionCombo );

                    dataValueService.addDataValue( dataValue );
                    //llValueMap.put( dataElement.getId(), value );
                    //liDEMap.put( dataElement.getId(), optionCombo.getId() );
                }
            }
            else
            {
                dataValue.setValue( value );
                dataValue.setTimestamp( new Date() );
                dataValue.setStoredBy( storedBy );

                dataValueService.updateDataValue( dataValue );
                //llValueMap.put( dataElement.getId(), value );
                //liDEMap.put( dataElement.getId(), optionCombo.getId() );
            }
        }

    }

}
