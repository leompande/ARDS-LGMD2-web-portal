package org.hisp.dhis.dataanalyser.ds.action;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
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

public class GenerateSummaryDataStatusResultAction
    implements Action
{
    private final String SUMMARYSTATUSVARIABLE = "Summarystatusvariable";
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
    
    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    
    private List<Constant> constants;

    // ---------------------------------------------------------------
    // Output Parameters
    // ---------------------------------------------------------------

    private Map<OrganisationUnit, Integer> ouMapForColor;
    
    public Map<OrganisationUnit, Integer> getOuMapForColor()
    {
        return ouMapForColor;
    }

    private Map<OrganisationUnit, Integer> ouMapForChildDSAssociation;
    
    public Map<OrganisationUnit, Integer> getOuMapForChildDSAssociation()
    {
        return ouMapForChildDSAssociation;
    }

    private Map<OrganisationUnit, List<Integer>> ouMapStatusResult;

    public Map<OrganisationUnit, List<Integer>> getOuMapStatusResult()
    {
        return ouMapStatusResult;
    }

    private Map<OrganisationUnit, List<Integer>> ouMapSummaryStatusResult;

    public Map<OrganisationUnit, List<Integer>> getOuMapSummaryStatusResult()
    {
        return ouMapSummaryStatusResult;
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

    private String ouId;

    public void setOuId( String ouId )
    {
        this.ouId = ouId;
    }

    private String immChildOption;

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

    private String selectedDataSets;

    public void setSelectedDataSets( String selectedDataSets )
    {
        this.selectedDataSets = selectedDataSets;
    }

    private int minOULevel;

    public int getMinOULevel()
    {
        return minOULevel;
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
    
    Set<OrganisationUnit> dso;

    Map<String, Double> tempOuMapResult;
    
    Double constValue = 0.0;
    String constName = "";
    // ---------------------------------------------------------------
    // Action Implementation
    // ---------------------------------------------------------------
    public String execute()
        throws Exception
    {
        orgUnitCount = 0;
        dataViewName = "";

        // Intialization
        
        Constant constant = constantService.getConstantByName( SUMMARYSTATUSVARIABLE );
        constName =  constant.getName();
        constValue = constant.getValue();
        
        
        /*
        constants = new ArrayList<Constant>( constantService.getAllConstants());
        
        for( Constant constant : constants )
        {
            //String name = constant.getName();
            //Double value = constant.getValue();
            if( constant.getName().equalsIgnoreCase( "temp" ))
            {
                constName =  constant.getName();
                constValue = constant.getValue();
                break;
            }
        }
        */
        System.out.println( "------Constant Name is : ---- " + constName + ",------ Constant Value is : "  + constValue );
        tempOuMapResult = new HashMap<String, Double>();
        ouMapForChildDSAssociation = new HashMap<OrganisationUnit, Integer>();
        ouMapForColor =  new HashMap<OrganisationUnit, Integer>();
        ouMapStatusResult = new HashMap<OrganisationUnit, List<Integer>>();
        periodNameList = new ArrayList<String>();
        ouMapSummaryStatusResult = new HashMap<OrganisationUnit, List<Integer>>();
        maxOULevel = 1;
        minOULevel = organisationUnitService.getNumberOfOrganisationalLevels();
        
        if( includeZeros.equalsIgnoreCase( "false" ))
        {
            includeZeros = null;
        }
        
        if ( immChildOption != null && immChildOption.equalsIgnoreCase( "yes" ) )
        {
            orgUnitListCB = new ArrayList<String>();
            orgUnitListCB.add( ouId );
            facilityLB = "immChildren";
            selectedDataSets = dsId;
        }

        // DataSet Related Info
        deInfo = "-1";
        DataSet dSet = dataSetService.getDataSet( Integer.parseInt( selectedDataSets ) );
        selDataSet = dSet;
        Collection<DataElement> dataElements = new ArrayList<DataElement>();
        dataElements = selDataSet.getDataElements();
        int dataSetMemberCount1 = 0;
        for ( DataElement de1 : dataElements )
        {
            dataSetMemberCount1 += de1.getCategoryCombo().getOptionCombos().size();
        }
        Collection<Integer> dataElementIds = new ArrayList<Integer>( getIdentifiers(DataElement.class, dataElements ) );
        deInfo = getCommaDelimitedString( dataElementIds );
        //deInfo = getDEInfo( dataElements );
        
        // OrgUnit Related Info
        OrganisationUnit selectedOrgUnit = new OrganisationUnit();
        orgUnitList = new ArrayList<OrganisationUnit>();
        if ( facilityLB.equals( "children" ) )
        {
            selectedOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
            orgUnitList.addAll( organisationUnitService.getOrganisationUnitWithChildren( selectedOrgUnit.getId() ) );
        }
        else if ( facilityLB.equals( "immChildren" ) )
        {
            selectedOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
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
        }

        Set<OrganisationUnit> dSetSource = selDataSet.getSources();      
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
        periodList = periodService.getPeriodsBetweenDates( dataSetPeriodType, startPeriod.getStartDate(), endPeriod.getEndDate() );

        periodInfo = "-1";
        for ( Period p : periodList )
        {
            periodInfo += "," + p.getId();
        }
        
        dataViewName = createDataView( orgUnitInfo, deInfo, periodInfo );
        String query = "";
        String query2 = "";

        query = "SELECT COUNT(*) FROM " + dataViewName + " WHERE dataelementid IN (?) AND sourceid IN (?) AND periodid IN (?)";

        query2 = "SELECT COUNT(*) FROM " + dataViewName + " WHERE dataelementid IN (?) AND sourceid = ? AND periodid IN (?)";

        Iterator<OrganisationUnit> orgUnitListIterator = orgUnitList.iterator();
        OrganisationUnit o;
        dso = new HashSet<OrganisationUnit>();
        Iterator<Period> periodIterator;
        dso = selDataSet.getSources();
        String orgUnitId = "";
        
        Map<Integer, Integer> orgunitLevelMap = new HashMap<Integer, Integer>( dashBoardService.getOrgunitLevelMap() );
        
        while ( orgUnitListIterator.hasNext() )
        {
            o = (OrganisationUnit) orgUnitListIterator.next();
            orgUnitInfo = "" + o.getId();

            Integer ouL = orgunitLevelMap.get( o.getId() );
            if( ouL == null )
            {
                ouL = organisationUnitService.getLevelOfOrganisationUnit( o.getId() );
            }
            if( maxOULevel < ouL )
            {
                maxOULevel = ouL;
            }
            if( minOULevel > ouL )
            {
                minOULevel = ouL;
            }
            
            periodIterator = periodList.iterator();
            Period p;
            double dataStatusPercentatge;
            List<Integer> dsResults = new ArrayList<Integer>();
            List<Integer> dsSummaryResults = new ArrayList<Integer>();
           
            List<OrganisationUnit> childOrgUnits = new ArrayList<OrganisationUnit>();
            if ( !dso.contains( o ) )
            {
                childOrgUnits = filterChildOrgUnitsByDataSet( selDataSet, o );
                ouMapForChildDSAssociation.put( o, childOrgUnits.size() );
                ouMapForColor.put( o, 0 );
            }
            else
            {
                ouMapForChildDSAssociation.put( o, -1 );
                ouMapForColor.put( o, 1 );
            }
            
            while ( periodIterator.hasNext() )
            {
                p = (Period) periodIterator.next();
                periodInfo = "" + p.getId();

                if ( dso == null )
                {
                    dsResults.add( -1 );
                    dsSummaryResults.add( -1 );
                    ouMapForChildDSAssociation.put( o, -1 );
                    continue;
                }
                else if ( !dso.contains( o ) )
                {
                    //List<OrganisationUnit> childOrgUnits = new ArrayList<OrganisationUnit>();
                    //childOrgUnits = filterChildOrgUnitsByDataSet( selDataSet, o );
                    
                    int dataStatusCount = 0;
                    Iterator<OrganisationUnit> assignedChildrenIterator = childOrgUnits.iterator();
                    while ( assignedChildrenIterator.hasNext() )
                    {
                        OrganisationUnit cUnit = (OrganisationUnit) assignedChildrenIterator.next();
                        orgUnitInfo = "-1";
                        dataStatusPercentatge = 0.0;
                        //orgUnitId = "-1,";
                        //orgUnitId += String.valueOf( cUnit.getId() );
                        orgUnitId = ""+ cUnit.getId();
                        orgUnitCount = 0;
                        getOrgUnitInfo( o, dso );

                        if ( includeZeros == null )
                        {
                            query2 = "SELECT COUNT(*) FROM " + dataViewName + " WHERE dataelementid IN (" + deInfo
                                + ") AND sourceid IN (" + orgUnitId + ") AND periodid IN (" + periodInfo
                                + ") and value <> 0";
                        }
                        else
                        {
                            query2 = "SELECT COUNT(*) FROM " + dataViewName + " WHERE dataelementid IN (" + deInfo
                                + ") AND sourceid IN (" + orgUnitId + ") AND periodid IN (" + periodInfo + ")";
                        }

                        Double tempDataStatusCount = tempOuMapResult.get( orgUnitId+":"+periodInfo );
                        if( tempDataStatusCount == null )
                        {
                            SqlRowSet sqlResultSet = jdbcTemplate.queryForRowSet( query2 );
    
                            if ( sqlResultSet.next() )
                            {
                                try
                                {
                                    dataStatusPercentatge = ((double) sqlResultSet.getInt( 1 ) / (double) (dataSetMemberCount1)) * 100.0;
                                }
                                catch ( Exception e )
                                {
                                    dataStatusPercentatge = 0.0;
                                }
                            }
                            else
                            {
                                dataStatusPercentatge = 0.0;
                            }
    
                            if( dataStatusPercentatge > 100.0 )
                            {
                                dataStatusPercentatge = 100;
                            }
    
                            dataStatusPercentatge = Math.round( dataStatusPercentatge * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
                            
                            tempOuMapResult.put( orgUnitId+":"+periodInfo, dataStatusPercentatge );
    
                        }
                        else
                        {
                            dataStatusPercentatge = tempDataStatusCount;
                        }
                        //if ( dataStatusPercentatge >= 5.0 )
                        if ( dataStatusPercentatge >= constValue )
                        {
                            dataStatusCount += 1;
                        }

                    }
                    
                    dsSummaryResults.add( dataStatusCount );
                    Double tempDouble = ( (double) dataStatusCount /(double) childOrgUnits.size() ) * 100.0;
                    tempDouble = Math.round( tempDouble * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
                    dsResults.add( tempDouble.intValue() );
                    continue;
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
                        dataStatusPercentatge = ((double) sqlResultSet.getInt( 1 ) / (double) dataSetMemberCount1) * 100.0;
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
                //if ( dataStatusPercentatge >= 5.0 )
                if ( dataStatusPercentatge >= constValue )
                {
                    dsSummaryResults.add( 1 );
                }
                else
                {
                    dsSummaryResults.add( 0 );
                }
                
                dsResults.add( -1 );
            }
            
            ouMapSummaryStatusResult.put( o, dsSummaryResults );
            ouMapStatusResult.put( o, dsResults );
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
        
        return SUCCESS;
    }

    public void getDataSetAssignedOrgUnitCount( OrganisationUnit organisationUnit, Set<OrganisationUnit> dso )
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
	    jdbcTemplate.update( query );
            System.out.println( "View " + dataViewName + " dropped Successfully (if exists) " );

            query = "CREATE VIEW " + dataViewName + " AS "
                + " SELECT sourceid,dataelementid,periodid,value FROM datavalue " + " WHERE dataelementid in ("
                + deInfo + ") AND " + " sourceid in (" + orgUnitInfo + ") AND " + " periodid in (" + periodInfo + ")";

            jdbcTemplate.update( query );

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
            jdbcTemplate.update( query );
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

    private void getOrgUnitInfo( OrganisationUnit organisationUnit, Set<OrganisationUnit> dso )
    {
        Collection<OrganisationUnit> children = organisationUnit.getChildren();
        Iterator<OrganisationUnit> childIterator = children.iterator();
        OrganisationUnit child;
        
        while ( childIterator.hasNext() )
        {
            child = childIterator.next();
            if ( dso.contains( child ) )
            {
                orgUnitInfo += "," + child.getId();
                orgUnitCount++;
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

    private List<OrganisationUnit> filterChildOrgUnitsByDataSet( DataSet selectedDataSet,
        OrganisationUnit selectedOrganisationUnit )
    {
        List<OrganisationUnit> filteredOrganisationUnits = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selectedOrganisationUnit.getId() ) );
        filteredOrganisationUnits.retainAll( dso );
        return filteredOrganisationUnits;
    }

}// class end
