package org.hisp.dhis.dataanalyser.ds.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.WritableCellFormat;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
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

public class GenerateDataStatusDataSetWiseResultAction implements Action
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
    // Input Parameters
    // ---------------------------------------------------------------
    
    private String periodTypeId;
    
    public void setPeriodTypeId( String periodTypeId )
    {
        this.periodTypeId = periodTypeId;
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
    
    private String includeZeros;

    public void setIncludeZeros( String includeZeros )
    {
        this.includeZeros = includeZeros;
    }
    
    public String getIncludeZeros()
    {
        return includeZeros;
    }

    private List<OrganisationUnit> orgUnitList;

    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }
    /*
    private DisplayPropertyHandler displayPropertyHandler;

    public void setDisplayPropertyHandler( DisplayPropertyHandler displayPropertyHandler )
    {
        this.displayPropertyHandler = displayPropertyHandler;
    }
    */
    private Collection<Period> periodList;

    public Collection<Period> getPeriodList()
    {
        return periodList;
    }
    
    private List<DataSet> dataSetList;
    
    public List<DataSet> getDataSetList()
    {
        return dataSetList;
    }

    private List<String> periodNameList;

    public List<String> getPeriodNameList()
    {
        return periodNameList;
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

    String orgUnitInfo;
    
    
    List<String> levelNames;

    public List<String> getLevelNames()
    {
        return levelNames;
    }
    
    String deInfo;
    

    String periodInfo;
    
    private int dataSetMemberCount1;
    
    public int getDataSetMemberCount1()
    {
        return dataSetMemberCount1;
    }
    
    //Set<Source> dso;
    
    private Map<OrganisationUnit, Integer> ouMapForChildDSAssociation;
    
    public Map<OrganisationUnit, Integer> getOuMapForChildDSAssociation()
    {
        return ouMapForChildDSAssociation;
    }
    
    private Map<OrganisationUnit, Integer> ouMapForColor;
    
    public Map<OrganisationUnit, Integer> getOuMapForColor()
    {
        return ouMapForColor;
    }
    
    int orgUnitCount;
    
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
    private Map<OrganisationUnit, List<Integer>> ouDataSetMapColor;
    
    public Map<OrganisationUnit, List<Integer>> getOuDataSetMapColor()
    {
        return ouDataSetMapColor;
    }
    
    private Map<OrganisationUnit, Integer> ouMaporgChildCount;
    
    public Map<OrganisationUnit, Integer> getOuMaporgChildCount()
    {
        return ouMaporgChildCount;
    }

    public void setOuMaporgChildCount( Map<OrganisationUnit, Integer> ouMaporgChildCount )
    {
        this.ouMaporgChildCount = ouMaporgChildCount;
    }
    
    Double constValue = 0.0;
    String constName = "";
    // ---------------------------------------------------------------
    // Action Implementation
    // ---------------------------------------------------------------

    public String execute() throws Exception
    {
        
        System.out.println( "DataSet Wise Data Status Generation Start Time is : " + new Date() );
        // Intialization
        
        periodNameList = new ArrayList<String>();
        //dataViewName = "";
        
        
        Constant constant = constantService.getConstantByName( SUMMARYSTATUSVARIABLE );
        constName =  constant.getName();
        constValue = constant.getValue();
        
        constants = new ArrayList<Constant>( constantService.getAllConstants());
        /*
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
        if( includeZeros.equalsIgnoreCase( "false" ))
        {
            includeZeros = null;
        }
        
        System.out.println( "periodTypeId= " + periodTypeId + "----facilityLB= " + facilityLB + "----orgUnitListCB= " + orgUnitListCB );
        System.out.println( "sDateLB= " + sDateLB + "-----eDateLB= " + eDateLB + "-----includeZeros= " + includeZeros );
        
        ouMapForChildDSAssociation = new HashMap<OrganisationUnit, Integer>();
        ouMapForColor =  new HashMap<OrganisationUnit, Integer>();
        orgUnitCount = 0;
        
        ouMapStatusResult = new HashMap<OrganisationUnit, List<Integer>>();
        ouMapSummaryStatusResult = new HashMap<OrganisationUnit, List<Integer>>();
        //dsMapSummaryStatusResult = new HashMap<DataSet, Map<OrganisationUnit, List<Integer>>>();
        
        ouDataSetMapColor = new HashMap<OrganisationUnit, List<Integer>>();
        ouMaporgChildCount = new HashMap<OrganisationUnit, Integer>();
        
        
        // Period Related Info
        PeriodType periodType = periodService.getPeriodTypeByName( periodTypeId );
        
        Period startPeriod = periodService.getPeriod( sDateLB );
        Period endPeriod = periodService.getPeriod( eDateLB );
        periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( periodType, startPeriod.getStartDate(), endPeriod.getEndDate() ));
        
        periodNameList = dashBoardService.getPeriodNamesByPeriodType( periodType, periodList );
        
        maxOULevel = 1;
        minOULevel = organisationUnitService.getNumberOfOrganisationalLevels();
        
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
                //Collections.sort( organisationUnits, new OrganisationUnitShortNameComparator() );
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
                //Collections.sort( orgUnitList, new OrganisationUnitShortNameComparator() );
                Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
                //displayPropertyHandler.handle( orgUnitList );
            }
        }
        
        // OrgUnit Related Info for Data Set
        List<OrganisationUnit> tempOrGUnitList = new ArrayList<OrganisationUnit>();
        Iterator<String> orgUnitIterator = orgUnitListCB.iterator();
        while( orgUnitIterator.hasNext())
        {
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitIterator.next() ) );
            List<OrganisationUnit> orgUnitChildList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( orgUnit.getId() ) );
            tempOrGUnitList.addAll( orgUnitChildList );
        }
        
        // DataSet Related Info
        dataSetList = new ArrayList<DataSet>( dataSetService.getDataSetsByPeriodType( periodType ) );
        
        String query = "";
        String query2 = "";
        String orgUnitId = "";
        Iterator<DataSet> dsIterator = dataSetList.iterator();
        while( dsIterator.hasNext() )
        {
            DataSet dSet = (DataSet) dsIterator.next();
            
            // Remove Line Listing data sets
            if ( dSet.getId() == 8 || dSet.getId() == 9 || dSet.getId() == 10 || dSet.getId() == 14 || dSet.getId() == 15 || dSet.getId() == 35 || dSet.getId() == 36 || dSet.getId() == 37 || dSet.getId() == 38 )
            {
                dsIterator.remove();
                continue;
            }       
            //List<Source> dso = new ArrayList<Source>( dSet.getSources() );
            List<OrganisationUnit> dso = new ArrayList<OrganisationUnit>( dSet.getSources() );

            if( dso == null || dso.size() == 0 )
            {
                dsIterator.remove();
                continue;
            }

            int flag = 1;
            for( OrganisationUnit orgUnit : tempOrGUnitList )
            {
                if( dso.contains( orgUnit) )
                {
                        flag = 2;
                        break;
                }
            }
            
            if( flag == 1 )
            {
                dsIterator.remove();
                continue;
            }

            dataSetMemberCount1 = 0;
            for ( DataElement de : dSet.getDataElements() )
            {
                deInfo += "," + de.getId();
                dataSetMemberCount1 += de.getCategoryCombo().getOptionCombos().size();
            }

            Iterator<OrganisationUnit> orgUnitListIterator = orgUnitList.iterator();
            while ( orgUnitListIterator.hasNext() )
            {
                OrganisationUnit orgUnit = orgUnitListIterator.next();
                
                ouMaporgChildCount.put( orgUnit, orgUnit.getChildren().size() );
                //orgUnit.getChildren().size();
                
                orgUnitInfo = "" + orgUnit.getId();
                if ( maxOULevel < organisationUnitService.getLevelOfOrganisationUnit( orgUnit.getId() ) )
                    maxOULevel = organisationUnitService.getLevelOfOrganisationUnit( orgUnit.getId() );
    
                if ( minOULevel > organisationUnitService.getLevelOfOrganisationUnit( orgUnit.getId() ) )
                    minOULevel = organisationUnitService.getLevelOfOrganisationUnit( orgUnit.getId() );
                
                Iterator<Period> periodIterator = periodList.iterator();
                Period p;
                double dataStatusPercentatge;
                List<Integer> dsResults = new ArrayList<Integer>();
                List<Integer> dsSummaryResults = new ArrayList<Integer>();
                
                List<Integer> dsColors = new ArrayList<Integer>();
                
                List<OrganisationUnit> childOrgUnits = new ArrayList<OrganisationUnit>();
                
                if ( !dso.contains( orgUnit ) )
                {
                    childOrgUnits = filterChildOrgUnitsByDataSet( orgUnit, dso );
                    ouMapForChildDSAssociation.put( orgUnit, childOrgUnits.size() );
                    //ouMapForColor.put( orgUnit, 0 );
                    
                }
                else
                {
                    ouMapForChildDSAssociation.put( orgUnit, -1 );
                    //ouMapForColor.put( orgUnit, 1 );
                }
                
                while ( periodIterator.hasNext() )
                {
                    p = (Period) periodIterator.next();
                    periodInfo = "" + p.getId();

                    if ( dso == null )
                    {
                        dsResults.add( -1 );
                        dsSummaryResults.add( -1 );
                        ouMapForChildDSAssociation.put( orgUnit, -1 );
                        
                        dsColors.add( 1 );
                        continue;
                    }
                    else if ( !dso.contains( orgUnit ) )
                    {
                        if( childOrgUnits == null || childOrgUnits.size() <= 0 )
                        {
                            dsResults.add( -2 );
                            dsSummaryResults.add( -2 );
                            ouMapForChildDSAssociation.put( orgUnit, -1 );
                            
                            dsColors.add( 1 );
                            continue;
                        }
                        
                        int dataStatusCount = 0;
                        Iterator<OrganisationUnit> assignedChildrenIterator = childOrgUnits.iterator();
                        while ( assignedChildrenIterator.hasNext() )
                        {
                            OrganisationUnit cUnit = (OrganisationUnit) assignedChildrenIterator.next();
                            orgUnitInfo = "-1";
                            orgUnitId = "-1,";
                            orgUnitId += String.valueOf( cUnit.getId() );
                            orgUnitCount = 0;
                            getOrgUnitInfo( orgUnit, dso );
                            if ( includeZeros == null )
                            {
                                query = "SELECT COUNT(*) FROM datavalue  WHERE dataelementid IN (" + deInfo
                                    + ") AND sourceid IN (" + orgUnitId + ") AND periodid IN (" + periodInfo
                                    + ") and value <> 0";
                            }
                            else
                            {
                                query = "SELECT COUNT(*) FROM datavalue WHERE dataelementid IN (" + deInfo
                                    + ") AND sourceid IN (" + orgUnitId + ") AND periodid IN (" + periodInfo + ")";
                            }

                            SqlRowSet sqlResultSet = jdbcTemplate.queryForRowSet( query );

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
                                dataStatusPercentatge = 0.0;

                            if ( dataStatusPercentatge > 100.0 )
                                dataStatusPercentatge = 100;

                            dataStatusPercentatge = Math.round( dataStatusPercentatge * Math.pow( 10, 0 ) )
                                / Math.pow( 10, 0 );

                          //  if ( dataStatusPercentatge >= 5.0 )
                            if ( dataStatusPercentatge >= constValue )
                            {
                                dataStatusCount += 1;
                            }
                        }
                        
                        dsSummaryResults.add( dataStatusCount );
                        Double tempDouble = ( (double) dataStatusCount /(double) childOrgUnits.size() ) * 100.0;
                        tempDouble = Math.round( tempDouble * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
                        dsResults.add( tempDouble.intValue() );
                        
                        dsColors.add( 0 );
                        continue;
                    }
                    orgUnitInfo = "" + orgUnit.getId();
                    
                    if ( includeZeros == null )
                    {
                        query2 = "SELECT COUNT(*) FROM datavalue WHERE dataelementid IN (" + deInfo
                            + ") AND sourceid IN (" + orgUnitInfo + ") AND periodid IN (" + periodInfo + ") and value <> 0";
                    }
                    else
                    {
                        query2 = "SELECT COUNT(*) FROM datavalue WHERE dataelementid IN (" + deInfo
                            + ") AND sourceid IN (" + orgUnitInfo + ") AND periodid IN (" + periodInfo + ")";
                    }

                    SqlRowSet sqlResultSet = jdbcTemplate.queryForRowSet( query2 );
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
                    dsColors.add( 1 );
                    
                }
                
                List<Integer> tempIntList = ouMapSummaryStatusResult.get( orgUnit );
                if( tempIntList != null )
                {
                    tempIntList.addAll( dsSummaryResults );
                }
                else
                {
                    tempIntList = dsSummaryResults;
                }
                
                List<Integer> tempdsResultList = ouMapStatusResult.get( orgUnit );
                if( tempdsResultList != null )
                {
                    tempdsResultList.addAll( dsResults );
                }
                else
                {
                    tempdsResultList = dsResults;
                }
                
                List<Integer> tempDsColorList = ouDataSetMapColor.get( orgUnit );
                if( tempDsColorList != null )
                {
                    tempDsColorList.addAll( dsColors );
                }
                else
                {
                    tempDsColorList = dsColors;
                }
                
                
                ouMapSummaryStatusResult.put( orgUnit, tempIntList );
                ouMapStatusResult.put( orgUnit, tempdsResultList );
                ouDataSetMapColor.put( orgUnit, tempDsColorList );
                }
                
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
        
        //System.out.println("Min Level is : " + minOULevel  + ", Max Level is " + maxOULevel );
        
        while ( count1 <= maxOULevel )
        {
            //System.out.println("Inside Level name Assign Loop" );
            levelNames.add( ouLevelNames[count1] );
            count1++;
        }
        System.out.println( "DataSet Wise Data Status Generation End Time is : " + new Date() );
        return SUCCESS;
        
    }
    
    // Returns the OrgUnitTree for which Root is the orgUnit
    @SuppressWarnings( "unchecked" )
    public List<OrganisationUnit> getChildOrgUnitTree( OrganisationUnit orgUnit )
    {
        List<OrganisationUnit> orgUnitTree = new ArrayList<OrganisationUnit>();
        orgUnitTree.add( orgUnit );

        List<OrganisationUnit> children = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
        Collections.sort( children, new IdentifiableObjectNameComparator() );
        //Collections.sort( children, new OrganisationUnitNameComparator() );

        Iterator childIterator = children.iterator();
        OrganisationUnit child;
        while ( childIterator.hasNext() )
        {
            child = (OrganisationUnit) childIterator.next();
            orgUnitTree.addAll( getChildOrgUnitTree( child ) );
        }
        return orgUnitTree;
    }// getChildOrgUnitTree end
    
    private List<OrganisationUnit> filterChildOrgUnitsByDataSet( OrganisationUnit selectedOrganisationUnit, List<OrganisationUnit> dso )
    {
        List<OrganisationUnit> filteredOrganisationUnits = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selectedOrganisationUnit.getId() ) );
        filteredOrganisationUnits.retainAll( dso );
        return filteredOrganisationUnits;
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
                orgUnitInfo += "," + child.getId();
                orgUnitCount++;
            }
            getOrgUnitInfo( child, dso );
        }
    }
    
    public WritableCellFormat getCellFormat1() throws Exception
    {
            WritableCellFormat wCellformat = new WritableCellFormat();
            
            wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
            wCellformat.setAlignment( Alignment.CENTRE );
            wCellformat.setBackground( Colour.GRAY_25 );
            wCellformat.setWrap( true );
        
            return wCellformat;
    }
    
}
