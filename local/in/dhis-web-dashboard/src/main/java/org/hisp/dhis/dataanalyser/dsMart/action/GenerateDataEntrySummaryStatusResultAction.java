package org.hisp.dhis.dataanalyser.dsMart.action;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataanalyser.util.DashBoardService;
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

public class GenerateDataEntrySummaryStatusResultAction implements Action
{
    // ---------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private DashBoardService dashBoardService;

    public void setDashBoardService( DashBoardService dashBoardService )
    {
        this.dashBoardService = dashBoardService;
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
    /*
    private DisplayPropertyHandler displayPropertyHandler;

    public void setDisplayPropertyHandler( DisplayPropertyHandler displayPropertyHandler )
    {
        this.displayPropertyHandler = displayPropertyHandler;
    }
    */
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    // ---------------------------------------------------------------
    // Input/Output Parameters
    // ---------------------------------------------------------------
    
    private Integer selectedDataSets;

    public void setSelectedDataSets( Integer selectedDataSets )
    {
        this.selectedDataSets = selectedDataSets;
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
    
    private DataSet selDataSet;

    public DataSet getSelDataSet()
    {
        return selDataSet;
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

    public int getEDateLB()
    {
        return eDateLB;
    }

    private int eDateLB;

    public void setEDateLB( int dateLB )
    {
        eDateLB = dateLB;
    }
    
    private String facilityLB;

    public void setFacilityLB( String facilityLB )
    {
        this.facilityLB = facilityLB;
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
    
    private List<String> orgUnitListCB;

    public void setOrgUnitListCB( List<String> orgUnitListCB )
    {
        this.orgUnitListCB = orgUnitListCB;
    }
    
    private String dataSetName;
    
    public String getDataSetName()
    {
        return dataSetName;
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
    
    private List<OrganisationUnit> orgUnitList;

    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
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
    
    private List<OrganisationUnit> dataSetSources;
    
    public List<OrganisationUnit> getDataSetSources()
    {
        return dataSetSources;
    }
   
    private int totalDataElementCount;
    
    public int getTotalDataElementCount()
    {
        return totalDataElementCount;
    }
    
    List<String> levelNames;

    public List<String> getLevelNames()
    {
        return levelNames;
    }
    
    private Map<String , Integer> ouMapDataEntryStatusResult;
    
    public Map<String, Integer> getOuMapDataEntryStatusResult()
    {
        return ouMapDataEntryStatusResult;
    }
    
    
    String orgUnitInfo;

    String deInfo;

    int orgUnitCount;
    
    Set<OrganisationUnit> dso;
    
    List<Integer> childOrgUnitTreeIds;
    
    String childOrgUnitsByComma;
    
    List<OrganisationUnit> tempOrgUnitList;
    
    String periodIdsByComma;
    
    private Map<String , Double> ouPeriodMapForResult;
    
    public Map<String, Double> getOuPeriodMapForResult()
    {
        return ouPeriodMapForResult;
    }

    private Map<String , Integer> ouPeriodMapForPercentageResult;
    
    public Map<String, Integer> getOuPeriodMapForPercentageResult()
    {
        return ouPeriodMapForPercentageResult;
    }
    
    private Map<OrganisationUnit, Integer> ouMapForChildDSAssociation;
    
    public Map<OrganisationUnit, Integer> getOuMapForChildDSAssociation()
    {
        return ouMapForChildDSAssociation;
    }

    // ---------------------------------------------------------------
    // Action Implementation
    // ---------------------------------------------------------------
    public String execute() throws Exception
    {
       
        System.out.println( "Data Entry Summary  Status Using Mart Start Time  : " + new Date() );
        maxOULevel = 1;
        minOULevel = organisationUnitService.getNumberOfOrganisationalLevels();
        
        dataSetSources = new ArrayList<OrganisationUnit>();
        
        childOrgUnitTreeIds = new ArrayList<Integer>();
        childOrgUnitsByComma = "";
        tempOrgUnitList = new ArrayList<OrganisationUnit>();
        periodIdsByComma = "";
        ouPeriodMapForResult = new HashMap<String, Double>();
        
        
        ouMapDataEntryStatusResult = new HashMap<String, Integer>();// Map for Results 
        ouPeriodMapForPercentageResult = new HashMap<String, Integer>();// Map for percentage Results 
        ouMapForChildDSAssociation = new HashMap<OrganisationUnit, Integer>();
        
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
        
        // Data Set Related Information
        selDataSet = dataSetService.getDataSet( selectedDataSets );
        dataSetName = selDataSet.getName();        
        dataSetSources = new ArrayList<OrganisationUnit>( selDataSet.getSources() );
        
        // Period Related Info
        Period startPeriod = periodService.getPeriod( sDateLB );
        Period endPeriod = periodService.getPeriod( eDateLB );

        PeriodType dataSetPeriodType = selDataSet.getPeriodType();
        periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( dataSetPeriodType, startPeriod.getStartDate(), endPeriod.getEndDate() ) );
        
        periodNameList = dashBoardService.getPeriodNamesByPeriodType( dataSetPeriodType, periodList );
        
        // Period Information for map
        Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );
        periodIdsByComma = getCommaDelimitedString( periodIds );
        
        // OrgUnit Related Info
        OrganisationUnit selectedOrgUnit = new OrganisationUnit();
        orgUnitList = new ArrayList<OrganisationUnit>();
        if ( facilityLB.equals( "children" ) )
        {
            selectedOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
            orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selectedOrgUnit.getId() ) );
        
            tempOrgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selectedOrgUnit.getId() ) );            
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
                
                //for Map
                List<OrganisationUnit> childOrgUnitTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( o.getId() ) );
                
                tempOrgUnitList.addAll( childOrgUnitTree );
                //childOrgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, tempOrgUnitList ) );
                //childOrgUnitsByComma = getCommaDelimitedString( childOrgUnitTreeIds );
                
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
                
                //for Map
                List<OrganisationUnit> childOrgUnitTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( o.getId() ) );
                
                tempOrgUnitList.addAll( childOrgUnitTree );
                //childOrgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, tempOrgUnitList ) );
                //childOrgUnitsByComma = getCommaDelimitedString( childOrgUnitTreeIds );
                
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
        
        // for orgUnit which are assign to data set
        
        //Set<OrganisationUnit> dataSetOrganisationUnits = selDataSet.getSources();
        tempOrgUnitList.retainAll( dataSetSources );
        childOrgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, tempOrgUnitList ) );
        childOrgUnitsByComma = getCommaDelimitedString( childOrgUnitTreeIds );
        
        /*
        List<OrganisationUnit> childOrgUnitTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( currentOrgUnit.getId() ) );
        List<Integer> childOrgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, childOrgUnitTree ) );
        String childOrgUnitsByComma = getCommaDelimitedString( childOrgUnitTreeIds );
        */
        
        String tempQuery = "";
        if ( includeZeros == null )
        {
            tempQuery = "SELECT organisationunitid, periodid, value FROM dataentrystatus  WHERE datasetid = " + selDataSet.getId() +  " AND organisationunitid IN (" + childOrgUnitsByComma + ") AND periodid IN (" + periodIdsByComma + ") and includezero ='N' ";
        }
        else
        {
            tempQuery = "SELECT organisationunitid, periodid, value FROM dataentrystatus  WHERE datasetid = " + selDataSet.getId() +  " AND organisationunitid IN (" + childOrgUnitsByComma + ") AND periodid IN (" + periodIdsByComma + ") and includezero ='Y' ";
        }
        
        SqlRowSet rs = jdbcTemplate.queryForRowSet( tempQuery );
        
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
            /*
            if ( value >= 5.0 )
            {
                result = 1;
            }
            
            else
            {
                result = 0;
            }
           */
            String orgIdPeriodId =  orgUnitId + ":" + periodId;
            ouPeriodMapForResult.put( orgIdPeriodId, value );
        }
        
        System.out.println( "Size of ouPeriodMapForResult is  : " + ouPeriodMapForResult.size() );
        
        dso =  new HashSet<OrganisationUnit>();
        dso = selDataSet.getSources();
        
        if( ouPeriodMapForResult.size() != 0 )
        {
            Iterator<Period> periodIterator;
            
            Iterator<OrganisationUnit> orgUnitListIterator = orgUnitList.iterator();
            while ( orgUnitListIterator.hasNext() )
            {
                OrganisationUnit ou = orgUnitListIterator.next();
                //orgUnitInfo += "," + ou.getId();
                //orgUnitInfo += "" + ou.getId();
                String orgIdPeriodId = "";
                
                //List<OrganisationUnit> childOrgUnits = new ArrayList<OrganisationUnit>();
                
                if ( maxOULevel < organisationUnitService.getLevelOfOrganisationUnit( ou.getId() ) )
                    maxOULevel = organisationUnitService.getLevelOfOrganisationUnit( ou.getId() );

                if ( minOULevel > organisationUnitService.getLevelOfOrganisationUnit( ou.getId() ) )
                    minOULevel = organisationUnitService.getLevelOfOrganisationUnit( ou.getId() );
                
                int result1 = 0;
                int percentageResult = 0;
                Double value1;
                periodIterator = periodList.iterator();
                Period p;
                
                List<OrganisationUnit> childOrgUnits = new ArrayList<OrganisationUnit>();
                if ( !dso.contains( ou ) )
                {
                    childOrgUnits = filterChildOrgUnitsByDataSet( selDataSet, ou );
                    ouMapForChildDSAssociation.put( ou, childOrgUnits.size() );
                }
                else
                {
                    ouMapForChildDSAssociation.put( ou, -1 );
                }
                while ( periodIterator.hasNext() )
                {
                    p = (Period) periodIterator.next();
                    //periodInfo = "" + p.getId();

                    if ( dso == null )
                    {
                        //result1 = -1;
                        continue;
                    }
                    else if ( !dso.contains( ou ) )
                    {
                      
                        int dataStatusCount = 0;
                        Iterator<OrganisationUnit> assignedChildrenIterator = childOrgUnits.iterator();
                        while ( assignedChildrenIterator.hasNext() )
                        {
                            OrganisationUnit cUnit = (OrganisationUnit) assignedChildrenIterator.next();
                            orgIdPeriodId = cUnit.getId() + ":" + p.getId();
                            value1 =  ouPeriodMapForResult.get( orgIdPeriodId );
                            
                            if ( value1 != null && value1 >=5.0 )
                            {
                                dataStatusCount += 1;
                            }
                            //else if ( value1 == null )
                            //{
                                //result1 = -1;
                                //percentageResult = -1;
                                //dataStatusCount = -1;
                            //}
                        }
                        
                        //System.out.println( " orgId and PeriodId is : " + ou.getId() + ":" + p.getId() + " ,dataStatus Count is  : " + dataStatusCount );
                        result1 = dataStatusCount;
                        
                        Double tempDouble = ( (double) dataStatusCount /(double) childOrgUnits.size() ) * 100.0;
                        tempDouble = Math.round( tempDouble * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
                        percentageResult = tempDouble.intValue();
                        
                        ouMapDataEntryStatusResult.put( ou.getId() + ":" + p.getId() , result1 );
                        ouPeriodMapForPercentageResult.put( ou.getId() + ":" + p.getId(), percentageResult );
                        //ouMapDataEntryStatusResult.put( orgIdPeriodId, result1 );
                        continue;
                    }
                    
                    orgIdPeriodId = ou.getId() + ":" + p.getId();
                    value1 =  ouPeriodMapForResult.get( orgIdPeriodId );
                    
                    if ( value1 != null && value1 >=5.0 )
                    {
                        result1 = 1;
                    }
                    else if ( value1 != null && value1 <=5.0 )
                    {
                        result1 = 0;
                    }
                    else 
                    {
                        result1 = -1;
                    }
                    
                    percentageResult = -1;
                    ouMapDataEntryStatusResult.put( orgIdPeriodId, result1 );
                    ouPeriodMapForPercentageResult.put( orgIdPeriodId, percentageResult );
                }
                //ouMapDataEntryStatusResult.put( orgIdPeriodId, result1 );
                
            }
        }

        System.out.println( "Size of ouMapDataEntryStatusResult is  : " + ouMapDataEntryStatusResult.size() );
        /*
        for( String orgPeriod : ouMapDataEntryStatusResult.keySet() )
        {
            System.out.println( " key is  : " + orgPeriod  );
            System.out.println( "Value is : " + ouMapDataEntryStatusResult.get( orgPeriod ) );
        }
       */
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
                
        System.out.println( "Data Entry Summary  Status Using Mart End Time  : " + new Date() );
        return SUCCESS;
    }
    
    private List<OrganisationUnit> filterChildOrgUnitsByDataSet( DataSet selectedDataSet,
        OrganisationUnit selectedOrganisationUnit )
    {
        List<OrganisationUnit> filteredOrganisationUnits = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selectedOrganisationUnit.getId() ) );
        filteredOrganisationUnits.retainAll( dso );
        return filteredOrganisationUnits;
    }
    
}
