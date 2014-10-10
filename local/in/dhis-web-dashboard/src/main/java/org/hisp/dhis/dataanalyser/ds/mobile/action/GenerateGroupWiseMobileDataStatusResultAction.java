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
package org.hisp.dhis.dataanalyser.ds.mobile.action;

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

import org.hibernate.SessionFactory;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataanalyser.util.DashBoardService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.dataset.comparator.SectionOrderComparator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version GenerateGroupWiseMobileDataStatusResultAction.java Nov 25, 2010 11:00:07 AM
 */
public class GenerateGroupWiseMobileDataStatusResultAction
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

    @SuppressWarnings("unused")
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

    private DataElementService dataElementService;

    public DataElementService getDataElementService()
    {
        return dataElementService;
    }

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
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
/*    
    private UserStore userStore;

    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }
*/    
    // ---------------------------------------------------------------
    // Output Parameters
    // ---------------------------------------------------------------

    private Map<OrganisationUnit, List<Integer>> ouMapDataStatusResult;

    private Collection<Period> periodList;

    private List<OrganisationUnit> orgUnitList;
    
    
    private Map<OrganisationUnit, String> ouMapUserPhoneNo;

    public Map<OrganisationUnit, String> getOuMapUserPhoneNo()
    {
        return ouMapUserPhoneNo;
    }

    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }
/*    
    private Map<DataElementGroup, Integer> deMapGroupCount;

    public Map<DataElementGroup, Integer> getDeMapGroupCount()
    {
        return deMapGroupCount;
    }
*/
    
    private Map<Section, Integer> deMapGroupCount;

    public Map<Section, Integer> getDeMapGroupCount()
    {
        return deMapGroupCount;
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
/*
    private List<DataElementGroup> dataElementGroups;

    public List<DataElementGroup> getDataElementGroups()
    {
        return dataElementGroups;
    }
*/
    private List<DataElementGroup> applicableDataElementGroups;

    public List<DataElementGroup> getApplicableDataElementGroups()
    {
        return applicableDataElementGroups;
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

    @SuppressWarnings("unused")
    private String selectedButton;

    public void setselectedButton( String selectedButton )
    {
        this.selectedButton = selectedButton;
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
    
    private List<String> periodNameList;
    
    public List<String> getPeriodNameList() 
    {
        return periodNameList;
    }

    private String includeZeros;

    public void setIncludeZeros(String includeZeros) 
    {
        this.includeZeros = includeZeros;
    }
    
    public String getIncludeZeros()
    {
        return includeZeros;
    }

    String orgUnitInfo;

    String periodInfo;

    String deInfo;

    int orgUnitCount;

    private String dataViewName;
    
    private DataSet selDataSet;

    public DataSet getSelDataSet()
    {
        return selDataSet;
    }
    private Integer dsSize;
     
    public Integer getDsSize()
    {
        return dsSize;
    }

    private String userPhoneNo;
    
    public String getUserPhoneNo()
    {
        return userPhoneNo;
    }
    
    private List<Section> sections;
    
    public Collection<Section> getSections()
    {
        return sections;
    }
    // ---------------------------------------------------------------
    // Action Implementation
    // ---------------------------------------------------------------
    


    //    @SuppressWarnings({ "deprecation", "unchecked" })
    public String execute()
        throws Exception
    {
        orgUnitCount = 0;
        dataViewName = "";

        // Intialization
        ouMapDataStatusResult = new HashMap<OrganisationUnit, List<Integer>>();
        ouMapUserPhoneNo = new HashMap<OrganisationUnit,String>();//for User PhoneNo Map
        //deMapGroupCount = new HashMap<DataElementGroup, Integer>(); // dataelement Group Count
        
        deMapGroupCount = new HashMap<Section, Integer>(); // dataelement Group Count
        
        results = new ArrayList<Integer>();
        maxOULevel = 1;
        minOULevel = organisationUnitService.getNumberOfOrganisationalLevels();

        if ( immChildOption != null && immChildOption.equalsIgnoreCase( "yes" ) )
        {
            orgUnitListCB = new ArrayList<String>();
            orgUnitListCB.add( ouId );
            facilityLB = "immChildren";
            selectedDataSets = new ArrayList<String>();
            selectedDataSets.add( dsId );
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
            @SuppressWarnings("unused")
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
            }
        }

        for ( String ds : selectedDataSets )
        {
            DataSet dSet = dataSetService.getDataSet( Integer.parseInt( ds ) );
            selDataSet = dSet;
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

        
        selectedPeriodList = new ArrayList<Period>( periodService.getIntersectingPeriods( startPeriod.getStartDate(),
            endPeriod.getEndDate() ) );

        periodInfo = "-1";
        for ( Period p : selectedPeriodList )
            periodInfo += "," + p.getId();

        // DataSet Related Info
        dataSetList = new ArrayList<DataSet>();
        deInfo = "-1";
        selDataSet = new DataSet();      
        selDataSet = dataSetService.getDataSet( Integer.parseInt( selectedDataSets.get( 0 ) ) );
        
        //for size of dataset( no of dataElement of Selected dataset)
        dsSize = selDataSet.getDataElements().size(); 
        
        // Data Element Group Related Info
        /*
        dataElementGroups = new ArrayList<DataElementGroup>();
        
        dataElementGroups.addAll( getApplicableDataElementGroups( selDataSet ) );
        Collections.sort( dataElementGroups, new DataElementGroupNameComparator() );

        for ( DataElementGroup deGroup : dataElementGroups )
        {
            for ( DataElement de : deGroup.getMembers() )
                deInfo += "," + de.getId();
        }
        
        */
        // for dataSet Sections
        
        //Collection<Section> sections = selDataSet.getSections();
        sections = new ArrayList<Section>();
        //sections = new ArrayList<Section>( sectionService.getAllSections() );
        sections = new ArrayList<Section>( selDataSet.getSections() );
        Collections.sort( sections, new SectionOrderComparator() );
        
        for ( Section section : sections )
        {
            for ( DataElement de : section.getDataElements() )
                deInfo += "," + de.getId();
        }
        
        System.out.println( "Section Size is: " + sections.size() );
        
        dataViewName = createDataView( orgUnitInfo, deInfo, periodInfo );
       
        String query = "";
        query = "SELECT COUNT(*) FROM " + dataViewName
            + " WHERE dataelementid IN (?) AND sourceid IN (?) AND periodid IN (?)";

        Collection<DataElement> dataElements = new ArrayList<DataElement>();
        PeriodType dataSetPeriodType;
        periodList = new ArrayList<Period>();

        dataElements = selDataSet.getDataElements();
        dataSetPeriodType = selDataSet.getPeriodType();
        periodList = periodService.getPeriodsBetweenDates( dataSetPeriodType, startPeriod.getStartDate(),
            endPeriod.getEndDate() );
        dataSetPeriods = new HashMap<DataSet, Collection<Period>>();
        //Iterator<DataElementGroup> dataElementGroupIterator = dataElementGroups.iterator();

        
        DataSet ds;
        //DataElementGroup deg;
        
        Section dataSetSection;
        Iterator<Section> sectionIterator = sections.iterator();
        
        while ( sectionIterator.hasNext() )
        {
            ds = dataSetService.getDataSet( Integer.valueOf( selectedDataSets.get( 0 ) ) );
            //deg = (DataElementGroup) dataElementGroupIterator.next();
            
            dataSetSection = (Section) sectionIterator.next();
            //dataElements = deg.getMembers();
            dataElements = dataSetSection.getDataElements();
            
            dataElements.retainAll( ds.getDataElements() );
            
           // System.out.println( "dataElementGroup Size  : " + dataElements.size() );
            
            int deGroupMemberCount1 = 0;
            for ( DataElement de1 : dataElements )
            {
                deGroupMemberCount1 += de1.getCategoryCombo().getOptionCombos().size();
            }

            // detaElement Group member Count
            
            //Integer deGroupMemberCount = dataElements.size();
            
            //deMapGroupCount.put( deg, deGroupMemberCount );
            
            deMapGroupCount.put( dataSetSection, deGroupMemberCount1 );
            deInfo = getDEInfo( dataElements );

            dataSetPeriodType = ds.getPeriodType();

            periodList = periodService.getPeriodsBetweenDates( dataSetPeriodType, startPeriod.getStartDate(),
                endPeriod.getEndDate() );
            dataSetPeriods.put( ds, periodList );

            Iterator<OrganisationUnit> orgUnitListIterator = orgUnitList.iterator();
            OrganisationUnit o;
            Set<OrganisationUnit> dso = new HashSet<OrganisationUnit>();
            Iterator<Period> periodIterator;

            while ( orgUnitListIterator.hasNext() )
            {
                o = (OrganisationUnit) orgUnitListIterator.next();
                
                // user phone no
                userPhoneNo = "";
                
                List<User> users = new ArrayList<User>( o.getUsers() );
                
                for ( User user : users )
                {
                    if ( user != null && user.getPhoneNumber() != null  && !user.getPhoneNumber().trim().equalsIgnoreCase( "" ) )
                    {
                        userPhoneNo += user.getPhoneNumber() + ", ";
                    }
                }    
                
                ouMapUserPhoneNo.put( o, userPhoneNo );      
                
                orgUnitInfo = "" + o.getId();

                if ( maxOULevel < organisationUnitService.getLevelOfOrganisationUnit( o.getId() ) )
                    maxOULevel = organisationUnitService.getLevelOfOrganisationUnit( o.getId() );

                if ( minOULevel > organisationUnitService.getLevelOfOrganisationUnit( o.getId() ) )
                    minOULevel = organisationUnitService.getLevelOfOrganisationUnit( o.getId() );

                dso = ds.getSources();
                periodIterator = periodList.iterator();

                Period p;
               // @SuppressWarnings("unused")
                //Collection dataValueResult;
                double dataStatusPercentatge;

                while ( periodIterator.hasNext() )
                {
                    p = (Period) periodIterator.next();
                    periodInfo = "" + p.getId();

                    if ( dso == null )
                    {
                        results.add( -1 );
                        continue;
                    }
                    else if ( !dso.contains( o ) )
                    {
                        orgUnitInfo = "-1";
                        orgUnitCount = 0;
                        getOrgUnitInfo( o, dso );

                        if(includeZeros == null)
                        {
                            query = "SELECT COUNT(*) FROM " + dataViewName + " WHERE dataelementid IN (" + deInfo
                            + ") AND sourceid IN (" + orgUnitInfo + ") AND periodid IN (" + periodInfo + ") AND value <> 0";
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
                                dataStatusPercentatge = ((double) sqlResultSet.getInt( 1 ) / (double) (deGroupMemberCount1 * orgUnitCount)) * 100.0;
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

                        results.add( (int) dataStatusPercentatge );
                        continue;
                    }

                    orgUnitInfo = "" + o.getId();

                    if(includeZeros == null)
                        {
                            query = "SELECT COUNT(*) FROM " + dataViewName + " WHERE dataelementid IN (" + deInfo
                        + ") AND sourceid IN (" + orgUnitInfo + ") AND periodid IN (" + periodInfo + ") AND value <> 0";
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
                            dataStatusPercentatge = ((double) sqlResultSet.getInt( 1 ) / (double) deGroupMemberCount1) * 100.0;
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

                    results.add( (int) dataStatusPercentatge );
                }
            }
        }
      
 /*       
        for( DataElementGroup orgUnitGroup : deMapGroupCount.keySet() )
        {
            System.out.print( orgUnitGroup.getName()+ " : " );
            for( Integer gCount : deMapGroupCount.values())
            {
                System.out.print( " - "+ gCount );
            }
            System.out.println("");
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

        periodNameList = dashBoardService.getPeriodNamesByPeriodType( dataSetPeriodType, periodList );
        
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
            @SuppressWarnings("unused")
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
            @SuppressWarnings("unused")
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
    @SuppressWarnings("unchecked")
    public List<OrganisationUnit> getChildOrgUnitTree( OrganisationUnit orgUnit )
    {
        List<OrganisationUnit> orgUnitTree = new ArrayList<OrganisationUnit>();
        orgUnitTree.add( orgUnit );
        List<OrganisationUnit> children = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
       // Collections.sort( children, new OrganisationUnitNameComparator() );
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

    public Map<OrganisationUnit, List<Integer>> getOuMapDataStatusResult()
    {
        return ouMapDataStatusResult;
    }

    public Collection<Period> getPeriodList()
    {
        return periodList;
    }

    @SuppressWarnings("unchecked")
    public List<DataElementGroup> getApplicableDataElementGroups( DataSet selectedDataSet )
    {
        List<DataElement> dataSetMembers = new ArrayList<DataElement>();
        dataSetMembers.addAll( selectedDataSet.getDataElements() );
        List<DataElementGroup> allDataElementGroups = new ArrayList<DataElementGroup>( dataElementService
            .getAllDataElementGroups() );
        List<DataElementGroup> applicableDataElementGroups = new ArrayList<DataElementGroup>();
        Iterator degIterator = allDataElementGroups.iterator();

        while ( degIterator.hasNext() )
        {
            DataElementGroup deg = (DataElementGroup) degIterator.next();
            List<DataElement> checkDataElement = new ArrayList<DataElement>( deg.getMembers() );
            checkDataElement.retainAll( dataSetMembers );

            if ( checkDataElement != null && checkDataElement.size() > 0 )
            {
                applicableDataElementGroups.add( deg );
            }
        }
        return applicableDataElementGroups;
    }

}// class end

