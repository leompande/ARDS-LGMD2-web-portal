package org.hisp.dhis.dataanalyser.dsMart.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataanalyser.util.DashBoardService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.opensymphony.xwork2.Action;

public class GenerateDataEntryStatusResultAction implements Action
{
    // ---------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    public OrganisationUnitService getOrganisationUnitService()
    {
        return organisationUnitService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private DashBoardService dashBoardService;

    public void setDashBoardService( DashBoardService dashBoardService )
    {
        this.dashBoardService = dashBoardService;
    }
    
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    /*
    private DisplayPropertyHandler displayPropertyHandler;

    public void setDisplayPropertyHandler( DisplayPropertyHandler displayPropertyHandler )
    {
        this.displayPropertyHandler = displayPropertyHandler;
    }
    */
    // ---------------------------------------------------------------
    // Input/Output Parameters
    // ---------------------------------------------------------------
    
    private Map<String , Integer> ouMapDataEntryStatusResult;
    
    public Map<String, Integer> getOuMapDataEntryStatusResult()
    {
        return ouMapDataEntryStatusResult;
    }

    private Map<String , Integer> ouMapDataElementCount;
    
    public Map<String, Integer> getOuMapDataElementCount()
    {
        return ouMapDataElementCount;
    }

    private List<Integer> results;

    public List<Integer> getResults()
    {
        return results;
    }
    
    private String includeZeros;

    public void setIncludeZeros( String includeZeros )
    {
        this.includeZeros = includeZeros;
    }

    public String getIncludeZeros()
    {
        return includeZeros;
    }
    
    private int sDateLB;

    public void setSDateLB( int dateLB )
    {
        sDateLB = dateLB;
    }

    public int getSDateLB()
    {
        return sDateLB;
    }

    private int eDateLB;

    public void setEDateLB( int dateLB )
    {
        eDateLB = dateLB;
    }

    public int getEDateLB()
    {
        return eDateLB;
    }
   
    private DataSet selDataSet;

    public DataSet getSelDataSet()
    {
        return selDataSet;
    }
    
    private Collection<Period> periodList;

    public Collection<Period> getPeriodList()
    {
        return periodList;
    }
    
    private List<String> periodNameList;

    public List<String> getPeriodNameList()
    {
        return periodNameList;
    }

    private Integer selectedDataSets;

    public void setSelectedDataSets( Integer selectedDataSets )
    {
        this.selectedDataSets = selectedDataSets;
    }

    private String dataSetName;
    
    public String getDataSetName()
    {
        return dataSetName;
    }
    
    private List<OrganisationUnit> orgUnitList;

    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }
    
    private String facilityLB;

    public void setFacilityLB( String facilityLB )
    {
        this.facilityLB = facilityLB;
    }
    
    private List<String> orgUnitListCB;

    public void setOrgUnitListCB( List<String> orgUnitListCB )
    {
        this.orgUnitListCB = orgUnitListCB;
    }
    
    private int maxOULevel;

    public int getMaxOULevel()
    {
        return maxOULevel;
    }
    
    private int minOULevel;

    public int getMinOULevel()
    {
        return minOULevel;
    }
    
    List<String> levelNames;

    public List<String> getLevelNames()
    {
        return levelNames;
    }
    
    private int totalDataElementCount;
    
    public int getTotalDataElementCount()
    {
        return totalDataElementCount;
    }
    
    private List<OrganisationUnit> dataSetSources;
    
    public List<OrganisationUnit> getDataSetSources()
    {
        return dataSetSources;
    }
    
    private String immChildOption;

    public void setImmChildOption( String immChildOption )
    {
        this.immChildOption = immChildOption;
    }
    
    private String ouId;

    public void setOuId( String ouId )
    {
        this.ouId = ouId;
    }
    private String dsId;

    public void setDsId( String dsId )
    {
        this.dsId = dsId;
    }
    
    private String selectedButton;

    public void setselectedButton( String selectedButton )
    {
        this.selectedButton = selectedButton;
    }

    public String getSelectedButton()
    {
        return selectedButton;
    }

    String orgUnitInfo;

    String periodInfo;

    int orgUnitCount;
    // ---------------------------------------------------------------
    // Action Implementation
    // ---------------------------------------------------------------
    public String execute() throws Exception
    {
        System.out.println( "Data Entry Status  Start Time  : " + new Date() );
        periodNameList = new ArrayList<String>();
        maxOULevel = 1;
        minOULevel = organisationUnitService.getNumberOfOrganisationalLevels();
        
        dataSetSources = new ArrayList<OrganisationUnit>();
        
        ouMapDataElementCount = new HashMap<String, Integer>();// Map for DataElement count
        
        ouMapDataEntryStatusResult = new HashMap<String, Integer>();// Map for Results 
        results = new ArrayList<Integer>();//for Results
        
        System.out.println( "immChildOption : "  + immChildOption + ", Ou Id is : " + ouId + ", DS id is : " + dsId );
        
        if( includeZeros.equalsIgnoreCase( "false" ))
        {
            includeZeros = null;
        }
        
        if ( immChildOption != null && immChildOption.equalsIgnoreCase( "yes" ) )
        {
            System.out.println( "Inside Drill Down" );
            orgUnitListCB = new ArrayList<String>();
            orgUnitListCB.add( ouId );

            facilityLB = "immChildren";

            selectedDataSets = Integer.parseInt( dsId );
        }
        
        selDataSet = dataSetService.getDataSet( selectedDataSets );
        dataSetName = selDataSet.getName();
        
        Collection<DataElement> dataElements = new ArrayList<DataElement>();
        dataElements = selDataSet.getDataElements();
        totalDataElementCount = 0;
        for ( DataElement de1 : dataElements )
        {
            totalDataElementCount += de1.getCategoryCombo().getOptionCombos().size();
        }
        
        dataSetSources = new ArrayList<OrganisationUnit>( selDataSet.getSources() );
        
        // Period Related Info
        Period startPeriod = periodService.getPeriod( sDateLB );
        Period endPeriod = periodService.getPeriod( eDateLB );

        PeriodType dataSetPeriodType = selDataSet.getPeriodType();
        periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( dataSetPeriodType, startPeriod.getStartDate(), endPeriod.getEndDate() ) );
        
        periodNameList = dashBoardService.getPeriodNamesByPeriodType( dataSetPeriodType, periodList );
        
        Iterator<Period> periodIterator = periodList.iterator();
        Period p;
        periodInfo = "-1";
        while( periodIterator.hasNext() )
        {
            p = (Period) periodIterator.next();
            periodInfo += "," + p.getId();
        }
        
        // OrgUnit Related Info
        OrganisationUnit selectedOrgUnit = new OrganisationUnit();
        orgUnitList = new ArrayList<OrganisationUnit>();
        if ( facilityLB.equals( "children" ) )
        {
            selectedOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
            orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selectedOrgUnit.getId() ) );
        }
        else if ( facilityLB.equals( "immChildren" ) )
        {
            selectedOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
            orgUnitList = new ArrayList<OrganisationUnit>();

            Iterator<String> orgUnitIterator = orgUnitListCB.iterator();
            while ( orgUnitIterator.hasNext() )
            {
                OrganisationUnit o = organisationUnitService.getOrganisationUnit( Integer.parseInt( (String) orgUnitIterator.next() ) );
                orgUnitList.add( o );
                List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>( o.getChildren() );
                Collections.sort( organisationUnits, new IdentifiableObjectNameComparator() );
                orgUnitList.addAll( organisationUnits );
            }
        }
        else
        {
            Iterator<String> orgUnitIterator = orgUnitListCB.iterator();
            OrganisationUnit o;
            while ( orgUnitIterator.hasNext() )
            {
                o = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitIterator.next() ) );
                orgUnitList.add( o );
            }
            Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
            //displayPropertyHandler.handle( orgUnitList );
        }
        
        Iterator<OrganisationUnit> ouIt = orgUnitList.iterator();
        while ( ouIt.hasNext() )
        {
            OrganisationUnit ou = ouIt.next();

            if ( !dataSetSources.contains( ou ) )
            {
                List<OrganisationUnit> ouList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( ou.getId() ) );
                ouList.retainAll( dataSetSources );
                
                if ( ouList == null || ouList.size() <= 0 )
                {
                    ouIt.remove();
                }
            }
        }

        orgUnitInfo = "-1";
        Iterator<OrganisationUnit> orgUnitListIterator = orgUnitList.iterator();
        while ( orgUnitListIterator.hasNext() )
        {
            OrganisationUnit ou = orgUnitListIterator.next();
            orgUnitInfo += "," + ou.getId();
            
            if ( maxOULevel < organisationUnitService.getLevelOfOrganisationUnit( ou.getId() ) )
                maxOULevel = organisationUnitService.getLevelOfOrganisationUnit( ou.getId() );

            if ( minOULevel > organisationUnitService.getLevelOfOrganisationUnit( ou.getId() ) )
                minOULevel = organisationUnitService.getLevelOfOrganisationUnit( ou.getId() );
        }

        // For Level Names
        String ouLevelNames[] = new String[organisationUnitService.getNumberOfOrganisationalLevels() + 1];
        for ( int i = 0; i < ouLevelNames.length; i++ )
        {
            ouLevelNames[i] = "Level" + i;
        }

        List<OrganisationUnitLevel> ouLevels = new ArrayList<OrganisationUnitLevel>( organisationUnitService.getFilledOrganisationUnitLevels() );
        for ( OrganisationUnitLevel ouL : ouLevels )
        {
            ouLevelNames[ouL.getLevel()] = ouL.getName();
        }

        levelNames = new ArrayList<String>();
        int count1 = minOULevel;
        while ( count1 <= maxOULevel )
        {
            levelNames.add( ouLevelNames[count1] );
            count1++;
        }
        
        String query = "";
        if ( includeZeros == null )
        {
            query = "SELECT organisationunitid, periodid, value FROM dataentrystatus  WHERE datasetid = " + selDataSet.getId() +  " AND organisationunitid IN (" + orgUnitInfo + ") AND periodid IN (" + periodInfo + ") and includezero ='N' ";
        }
        else
        {
            query = "SELECT organisationunitid, periodid, value FROM dataentrystatus  WHERE datasetid = " + selDataSet.getId() +  " AND organisationunitid IN (" + orgUnitInfo + ") AND periodid IN (" + periodInfo + ") and includezero ='Y' ";
        }
        
        SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
        
        double value ;
        while ( rs.next() )
        {                
            Integer orgUnitId = rs.getInt( 1 );
            Integer periodId = rs.getInt( 2 );
            String tempValue =  rs.getString( 3 );
            
            try
            {
                value = Double.parseDouble( tempValue );
            }
            catch ( Exception e )
            {
                value = 0.0;
            }
            
            String orgIdPeriodId =  orgUnitId + ":" + periodId;
            double dataElementCount = ( value * (double) totalDataElementCount ) / 100;
            
            value = Math.round( value * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
            dataElementCount = Math.round( dataElementCount * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
            
            ouMapDataEntryStatusResult.put( orgIdPeriodId, (int)value );
            ouMapDataElementCount.put( orgIdPeriodId, (int)dataElementCount );
        }

        System.out.println( "Data Entry Status  End Time  : " + new Date() );
        
        return SUCCESS;
    }    
}
