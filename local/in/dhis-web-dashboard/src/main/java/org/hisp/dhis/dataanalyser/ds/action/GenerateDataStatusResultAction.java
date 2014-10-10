package org.hisp.dhis.dataanalyser.ds.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.SessionFactory;
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

public class GenerateDataStatusResultAction
    implements Action
{
    // ---------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    @SuppressWarnings( "unused" )
    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
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

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    public DataSetService getDataSetService()
    {
        return dataSetService;
    }

    private DashBoardService dashBoardService;

    public void setDashBoardService( DashBoardService dashBoardService )
    {
        this.dashBoardService = dashBoardService;
    }
    /*
    private DisplayPropertyHandler displayPropertyHandler;

    public void setDisplayPropertyHandler( DisplayPropertyHandler displayPropertyHandler )
    {
        this.displayPropertyHandler = displayPropertyHandler;
    }
    */
    @SuppressWarnings( "unused" )
    private Comparator<OrganisationUnit> orgUnitComparator;

    public void setOrgUnitComparator( Comparator<OrganisationUnit> orgUnitComparator )
    {
        this.orgUnitComparator = orgUnitComparator;
    }

/*    
    private DataEntryStatusService dataEntryStatusService;
    
    public void setDataEntryStatusService( DataEntryStatusService dataEntryStatusService )
    {
        this.dataEntryStatusService = dataEntryStatusService;
    }
*/    



    // ---------------------------------------------------------------
    // Output Parameters
    // ---------------------------------------------------------------

    private Map<OrganisationUnit, List<Integer>> ouMapDataStatusResult;

    public Map<OrganisationUnit, List<Integer>> getOuMapDataStatusResult()
    {
        return ouMapDataStatusResult;
    }

    private Map<OrganisationUnit, List<Integer>> ouMapDataElementCount;

    public Map<OrganisationUnit, List<Integer>> getOuMapDataElementCount()
    {
        return ouMapDataElementCount;
    }

    private Collection<Period> periodList;

    public Collection<Period> getPeriodList()
    {
        return periodList;
    }

    private List<OrganisationUnit> orgUnitList;

    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }

    private List<DataSet> dataSetList;

    public List<DataSet> getDataSetList()
    {
        return dataSetList;
    }

    private List<Integer> results;

    public List<Integer> getResults()
    {
        return results;
    }

    private Map<DataSet, Map<OrganisationUnit, List<Integer>>> dataStatusResult;

    public Map<DataSet, Map<OrganisationUnit, List<Integer>>> getDataStatusResult()
    {
        return dataStatusResult;
    }

    private Map<DataSet, Collection<Period>> dataSetPeriods;

    public Map<DataSet, Collection<Period>> getDataSetPeriods()
    {
        return dataSetPeriods;
    }

    List<Period> selectedPeriodList;

    public List<Period> getSelectedPeriodList()
    {
        return selectedPeriodList;
    }

    List<String> levelNames;

    public List<String> getLevelNames()
    {
        return levelNames;
    }

    private int maxOULevel;

    public int getMaxOULevel()
    {
        return maxOULevel;
    }

    // ---------------------------------------------------------------
    // Input Parameters
    // ---------------------------------------------------------------

    private String dsId;

    public void setDsId( String dsId )
    {
        this.dsId = dsId;
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

    private String selectedButton;

    public void setselectedButton( String selectedButton )
    {
        this.selectedButton = selectedButton;
    }

    public String getSelectedButton()
    {
        return selectedButton;
    }

    private String ouId;

    public void setOuId( String ouId )
    {
        this.ouId = ouId;
    }

    private String immChildOption;
    
    public String getImmChildOption()
    {
        return immChildOption;
    }

    public void setImmChildOption( String immChildOption )
    {
        this.immChildOption = immChildOption;
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

    private List<String> selectedDataSets;

    public void setSelectedDataSets( List<String> selectedDataSets )
    {
        this.selectedDataSets = selectedDataSets;
    }

    public List<String> getSelectedDataSets()
    {
        return selectedDataSets;
    }

    private int minOULevel;

    public int getMinOULevel()
    {
        return minOULevel;
    }

    private int number;

    public int getNumber()
    {
        return number;
    }

    private DataSet selDataSet;

    public DataSet getSelDataSet()
    {
        return selDataSet;
    }

    private List<String> periodNameList;

    public List<String> getPeriodNameList()
    {
        return periodNameList;
    }

    String orgUnitInfo;

    String periodInfo;

    String deInfo;

    int orgUnitCount;

    private String dataViewName;

    private int dataSetMemberCount1;

    public int getDataSetMemberCount1()
    {
        return dataSetMemberCount1;
    }

    private Integer dataElementCount;

    public Integer getDataElementCount()
    {
        return dataElementCount;
    }

    private List<OrganisationUnit> dso;
    
    public List<OrganisationUnit> getDso()
    {
        return dso;
    }

    // ---------------------------------------------------------------
    // Action Implementation
    // ---------------------------------------------------------------
    // @SuppressWarnings( { "deprecation", "unchecked" } )
    public String execute()
        throws Exception
    {
        System.out.println( "Data Entry Status  Start Time  : " + new Date() );
        orgUnitCount = 0;
        dataViewName = "";

        ouMapDataElementCount = new HashMap<OrganisationUnit, List<Integer>>();// Map for DataElement count Intialization
        periodNameList = new ArrayList<String>();
        ouMapDataStatusResult = new HashMap<OrganisationUnit, List<Integer>>();
        results = new ArrayList<Integer>();
        maxOULevel = 1;
        minOULevel = organisationUnitService.getNumberOfOrganisationalLevels();
        
        
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

            selectedDataSets = new ArrayList<String>();
            selectedDataSets.add( dsId );
        }
        
        System.out.println( "selectedDataSets= " + selectedDataSets + "---,orgUnitListCB= " + orgUnitListCB + "---,,,,includeZeros= " + includeZeros );
        System.out.println( "sDateLB= " + sDateLB + "---,eDateLB= " + eDateLB + ",,------facilityLB= " + facilityLB );
        
        
        // DataSet Related Info
        dataSetList = new ArrayList<DataSet>();

        deInfo = "-1";
        for ( String ds : selectedDataSets )
        {
            DataSet dSet = dataSetService.getDataSet( Integer.parseInt( ds ) );
            selDataSet = dSet;
            for ( DataElement de : dSet.getDataElements() )
                deInfo += "," + de.getId();
        }

        // OrgUnit Related Info
        OrganisationUnit selectedOrgUnit = new OrganisationUnit();
        orgUnitList = new ArrayList<OrganisationUnit>();
        if ( facilityLB.equals( "children" ) )
        {
            selectedOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
            orgUnitList = getChildOrgUnitTree( selectedOrgUnit );
        }
        else if ( facilityLB.equals( "immChildren" ) )
        {
            @SuppressWarnings( "unused" )
            int number;

            selectedOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
            number = selectedOrgUnit.getChildren().size();
            orgUnitList = new ArrayList<OrganisationUnit>();

            Iterator<String> orgUnitIterator = orgUnitListCB.iterator();
            while ( orgUnitIterator.hasNext() )
            {
                OrganisationUnit o = organisationUnitService.getOrganisationUnit( Integer
                    .parseInt( (String) orgUnitIterator.next() ) );
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
                Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
                //displayPropertyHandler.handle( orgUnitList );
            }
        }

        // Set<OrganisationUnit> dSetSource = selDataSet.getSources();
        List<OrganisationUnit> dSetSource = new ArrayList<OrganisationUnit>( selDataSet.getSources() );
        orgUnitInfo = "-1";
        Iterator<OrganisationUnit> ouIt = orgUnitList.iterator();
        while ( ouIt.hasNext() )
        {
            OrganisationUnit ou = ouIt.next();

            orgUnitCount = 0;
            if ( !dSetSource.contains( ou ) )
            {
                getDataSetAssignedOrgUnitCount( ou, dSetSource );

                if ( orgUnitCount > 0 )
                {
                    orgUnitInfo += "," + ou.getId();
                    getOrgUnitInfo( ou );
                }
                else
                {
                    ouIt.remove();
                }
            }
            else
            {
                orgUnitInfo += "," + ou.getId();
            }
        }

        // Period Related Info
        Period startPeriod = periodService.getPeriod( sDateLB );
        Period endPeriod = periodService.getPeriod( eDateLB );

        PeriodType dataSetPeriodType = selDataSet.getPeriodType();
        periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( dataSetPeriodType, startPeriod
            .getStartDate(), endPeriod.getEndDate() ) );

        periodInfo = "-1";
        for ( Period p : periodList )
            periodInfo += "," + p.getId();

        dataViewName = createDataView( orgUnitInfo, deInfo, periodInfo );

        String query = "";
        query = "SELECT COUNT(*) FROM " + dataViewName
            + " WHERE dataelementid IN (?) AND sourceid IN (?) AND periodid IN (?)";

        Collection<DataElement> dataElements = new ArrayList<DataElement>();
        dataElements = selDataSet.getDataElements();

        dataSetMemberCount1 = 0;
        for ( DataElement de1 : dataElements )
        {
            dataSetMemberCount1 += de1.getCategoryCombo().getOptionCombos().size();
        }

        

        deInfo = getDEInfo( dataElements );

        Iterator<OrganisationUnit> orgUnitListIterator = orgUnitList.iterator();
        OrganisationUnit o;
        // Set<OrganisationUnit> dso = new HashSet<OrganisationUnit>();
        dso = new ArrayList<OrganisationUnit>( selDataSet.getSources() );
        Iterator<Period> periodIterator;
        // dso = selDataSet.getSources();

        while ( orgUnitListIterator.hasNext() )
        {
            o = orgUnitListIterator.next();
            orgUnitInfo = "" + o.getId();
            
            if ( maxOULevel < organisationUnitService.getLevelOfOrganisationUnit( o.getId() ) )
                maxOULevel = organisationUnitService.getLevelOfOrganisationUnit( o.getId() );

            if ( minOULevel > organisationUnitService.getLevelOfOrganisationUnit( o.getId() ) )
                minOULevel = organisationUnitService.getLevelOfOrganisationUnit( o.getId() );

            periodIterator = periodList.iterator();

            Period p;

            double dataStatusPercentatge;

            List<Integer> dsResults = new ArrayList<Integer>();
            List<Integer> deCounts = new ArrayList<Integer>();
            while ( periodIterator.hasNext() )
            {
                p = (Period) periodIterator.next();
                periodInfo = "" + p.getId();
                dataElementCount = 0;

                    if ( dso == null )
                    {
                        dsResults.add( -1 );
                        deCounts.add( -1 );
                        continue;
                    }
                    else if ( !dso.contains( o ) )
                    {
                        System.out.println("Dataset : " + selDataSet.getName() + " not assign to " + o.getName() );
                        List<OrganisationUnit> childOrgUnits = new ArrayList<OrganisationUnit>();
                        childOrgUnits = filterChildOrgUnitsByDataSet( o, dso );

                        
                        if( childOrgUnits == null || childOrgUnits.size() <= 0 )
                        {
                            dsResults.add( -2 );
                            continue;
                        }
                        else
                        {
                            orgUnitInfo = "-1";
                            orgUnitCount = 0;
                            getOrgUnitInfo( o, dso );
        
                            if ( includeZeros == null )
                            {
                                query = "SELECT COUNT(*) FROM " + dataViewName + " WHERE dataelementid IN (" + deInfo
                                    + ") AND sourceid IN (" + orgUnitInfo + ") AND periodid IN (" + periodInfo
                                    + ") and value <> 0";
                                
                            }
                            else
                            {
                                query = "SELECT COUNT(*) FROM " + dataViewName + " WHERE dataelementid IN (" + deInfo
                                    + ") AND sourceid IN (" + orgUnitInfo + ") AND periodid IN (" + periodInfo + ")";
                            }
        
                            System.out.println("Used Query is :::::::" + query );
                            SqlRowSet sqlResultSet = jdbcTemplate.queryForRowSet( query );
        
                            if ( sqlResultSet.next() )
                            {
                                try
                                {
                                    //System.out.println( "Result is : \t" + sqlResultSet.getLong( 1 ) );
                                    dataStatusPercentatge = ((double) sqlResultSet.getInt( 1 ) / (double) (dataSetMemberCount1 * orgUnitCount)) * 100.0;
                                    
                                }
                                catch ( Exception e )
                                {
                                    dataStatusPercentatge = 0.0;
                                }
                            }
                            else
                                dataStatusPercentatge = 0.0;
        
                            if ( dataStatusPercentatge > 100.0 )
                                dataStatusPercentatge = 100;
        
                            dataStatusPercentatge = Math.round( dataStatusPercentatge * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
                           
                            dsResults.add( (int) dataStatusPercentatge );
                            //dsResults.add( -1 );
                            dataElementCount = sqlResultSet.getInt( 1 );
                            //deCounts.add( -1 );
                            deCounts.add( dataElementCount );
                            continue;
                        }
                    }

                orgUnitInfo = "" + o.getId();

                if ( includeZeros == null )
                {
                    query = "SELECT COUNT(*) FROM " + dataViewName + " WHERE dataelementid IN (" + deInfo
                        + ") AND sourceid IN (" + orgUnitInfo + ") AND periodid IN (" + periodInfo + ") and value <> 0";
                }
                else
                {
                    query = "SELECT COUNT(*) FROM " + dataViewName + " WHERE dataelementid IN (" + deInfo
                        + ") AND sourceid IN (" + orgUnitInfo + ") AND periodid IN (" + periodInfo + ")";
                }

                SqlRowSet sqlResultSet = jdbcTemplate.queryForRowSet( query );

                if ( sqlResultSet.next() )
                {
                    try
                    {
                        dataElementCount = sqlResultSet.getInt( 1 );
                        dataStatusPercentatge = ((double) sqlResultSet.getInt( 1 ) / (double) dataSetMemberCount1) * 100.0;

                    }
                    catch ( Exception e )
                    {
                        dataElementCount = -1;
                        dataStatusPercentatge = 0.0;
                    }
                }
                else
                {
                    dataStatusPercentatge = 0.0;
                    dataElementCount = -1;
                }

                    
                if ( dataStatusPercentatge > 100.0 )
                    dataStatusPercentatge = 100;

                dataStatusPercentatge = Math.round( dataStatusPercentatge * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );                
                
                dsResults.add( (int) dataStatusPercentatge );
                deCounts.add( dataElementCount );
                //String dataEntryStatusValue =  dataEntryStatusService.getValue( selDataSet.getId(), o.getId(), p.getId() );
         }
            
            ouMapDataStatusResult.put( o, dsResults );
            ouMapDataElementCount.put( o, deCounts );
     }


        // For Level Names
        String ouLevelNames[] = new String[organisationUnitService.getNumberOfOrganisationalLevels() + 1];
        for ( int i = 0; i < ouLevelNames.length; i++ )
        {
            ouLevelNames[i] = "Level" + i;
        }

        List<OrganisationUnitLevel> ouLevels = new ArrayList<OrganisationUnitLevel>( organisationUnitService
            .getFilledOrganisationUnitLevels() );
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

        try
        {

        }
        finally
        {
            try
            {
                deleteDataView( dataViewName );
            }
            catch ( Exception e )
            {
                System.out.println( "Exception while closing DB Connections : " + e.getMessage() );
            }
        }// finally block end

        periodNameList = dashBoardService.getPeriodNamesByPeriodType( dataSetPeriodType, periodList );

        // System.out.println("OrgUnit Size is :" + ouMapDataStatusResult.size()
        // );
        System.out.println( "Data Entry Status  End Time  : " + new Date() );
        return SUCCESS;
    }

    public void getDataSetAssignedOrgUnitCount( OrganisationUnit organisationUnit, List<OrganisationUnit> dso )
    {
        Collection<OrganisationUnit> children = organisationUnit.getChildren();

        Iterator<OrganisationUnit> childIterator = children.iterator();
        OrganisationUnit child;
        while ( childIterator.hasNext() )
        {
            child = childIterator.next();
            if ( dso.contains( child ) )
            {
                orgUnitCount++;
            }
            getDataSetAssignedOrgUnitCount( child, dso );
        }
    }

    public String createDataView( String orgUnitInfo, String deInfo, String periodInfo )
    {
        String dataViewName = "_ds_" + UUID.randomUUID().toString();
        dataViewName = dataViewName.replaceAll( "-", "" );

        String query = "DROP VIEW IF EXISTS " + dataViewName;

        try
        {
            @SuppressWarnings( "unused" )
            int sqlResult = jdbcTemplate.update( query );

            System.out.println( "View " + dataViewName + " dropped Successfully (if exists) " );

            query = "CREATE view " + dataViewName + " AS "
                + " SELECT sourceid,dataelementid,periodid,value FROM datavalue " + " WHERE dataelementid in ("
                + deInfo + ") AND " + " sourceid in (" + orgUnitInfo + ") AND " + " periodid in (" + periodInfo + ")";

            sqlResult = jdbcTemplate.update( query );

            System.out.println( "View " + dataViewName + " created Successfully" );
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
            }
            catch ( Exception e )
            {
                System.out.println( "SQL Exception : " + e.getMessage() );
                return null;
            }
        }// finally block end

        return dataViewName;
    }

    public void deleteDataView( String dataViewName )
    {
        String query = "DROP VIEW IF EXISTS " + dataViewName;

        try
        {
            @SuppressWarnings( "unused" )
            int sqlResult = jdbcTemplate.update( query );
            System.out.println( "View " + dataViewName + " dropped Successfully" );
        } // try block end
        catch ( Exception e )
        {
            System.out.println( "SQL Exception : " + e.getMessage() );
        }
        finally
        {
            try
            {
            }
            catch ( Exception e )
            {
                System.out.println( "SQL Exception : " + e.getMessage() );
            }
        }// finally block end
    }

    // Returns the OrgUnitTree for which Root is the orgUnit
    @SuppressWarnings( "unchecked" )
    public List<OrganisationUnit> getChildOrgUnitTree( OrganisationUnit orgUnit )
    {
        List<OrganisationUnit> orgUnitTree = new ArrayList<OrganisationUnit>();
        orgUnitTree.add( orgUnit );

        List<OrganisationUnit> children = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
        Collections.sort( children, new IdentifiableObjectNameComparator() );

        Iterator childIterator = children.iterator();
        OrganisationUnit child;
        while ( childIterator.hasNext() )
        {
            child = (OrganisationUnit) childIterator.next();
            orgUnitTree.addAll( getChildOrgUnitTree( child ) );
        }
        return orgUnitTree;
    }// getChildOrgUnitTree end

    private void getOrgUnitInfo( OrganisationUnit organisationUnit )
    {
        Collection<OrganisationUnit> children = organisationUnit.getChildren();

        Iterator<OrganisationUnit> childIterator = children.iterator();
        OrganisationUnit child;
        while ( childIterator.hasNext() )
        {
            child = childIterator.next();
            orgUnitInfo += "," + child.getId();
            getOrgUnitInfo( child );
        }
    }

    
     private void getOrgUnitInfo( OrganisationUnit organisationUnit, List<OrganisationUnit> dso ) 
     { 
         Collection<OrganisationUnit> children = organisationUnit.getChildren();
      
         Iterator<OrganisationUnit> childIterator = children.iterator();
         OrganisationUnit child; 
         while ( childIterator.hasNext() ) 
         { 
             child = childIterator.next(); 
             if ( dso.contains( child ) ) 
             { 
                 orgUnitInfo += "," + child.getId(); orgUnitCount++; 
             } 
             getOrgUnitInfo( child, dso ); 
         } 
     }
     
    private String getDEInfo( Collection<DataElement> dataElements )
    {
        StringBuffer deInfo = new StringBuffer( "-1" );

        for ( DataElement de : dataElements )
        {
            deInfo.append( "," ).append( de.getId() );
        }
        return deInfo.toString();
    }
    
     private List<OrganisationUnit> filterChildOrgUnitsByDataSet( OrganisationUnit selectedOrganisationUnit, List<OrganisationUnit> dso ) 
     {
         List<OrganisationUnit> filteredOrganisationUnits = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selectedOrganisationUnit.getId() ) );
         filteredOrganisationUnits.retainAll( dso ); return
         filteredOrganisationUnits; 
     }
    
}// class end
