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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version GenerateLastUpdatedMobileDataSetResultAction.java Nov 25, 2010 12:02:35 PM
 */
public class GenerateLastUpdatedMobileDataSetResultAction
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
/*    
    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }
*/
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
  
    private UserStore userStore;
    
    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }
*/      
    private UserService userService;
    
    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }
      
    @SuppressWarnings("unused")
    private Comparator<OrganisationUnit> orgUnitComparator;

    public void setOrgUnitComparator( Comparator<OrganisationUnit> orgUnitComparator )
    {
        this.orgUnitComparator = orgUnitComparator;
    }
    
    // ---------------------------------------------------------------
    // Output Parameters
    // ---------------------------------------------------------------

    private Map<OrganisationUnit, List<String>> ouMapDataStatusResult;

    public Map<OrganisationUnit, List<String>> getOuMapDataStatusResult()
    {
        return ouMapDataStatusResult;
    }
    
    private Map<OrganisationUnit, String> ouMapUserPhoneNo;
    
    public Map<OrganisationUnit, String> getOuMapUserPhoneNo()
    {
        return ouMapUserPhoneNo;
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
    
    private String userPhoneNo;
    
    public String getUserPhoneNo()
    {
        return userPhoneNo;
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

//    private String dataViewName;

    // ---------------------------------------------------------------
    // Action Implementation
    // ---------------------------------------------------------------
//    @SuppressWarnings( { "deprecation", "unchecked" } )
    public String execute()
        throws Exception
    {
        
        orgUnitCount = 0;
//        dataViewName = "";

        // Intialization
        periodNameList = new ArrayList<String>();
        ouMapDataStatusResult = new HashMap<OrganisationUnit, List<String>>();
        ouMapUserPhoneNo = new HashMap<OrganisationUnit,String>();//for User PhoneNo Map
        
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

        // DataSet Related Info
        dataSetList = new ArrayList<DataSet>();

        deInfo = "-1";
        if ( selectedDataSets == null )
        {
            System.out.println( "slectedDataSets is empty" );
        }
        else
        {
            //System.out.println( "slectedDataSets is not empty" );
        }
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
        periodList = new ArrayList<Period>( periodService.getIntersectingPeriodsByPeriodType( dataSetPeriodType,
            startPeriod.getStartDate(), endPeriod.getEndDate() ) );

        periodInfo = "-1";
        for ( Period p : periodList )
            periodInfo += "," + p.getId();
           
        Collection<DataElement> dataElements = new ArrayList<DataElement>();
        dataElements = selDataSet.getDataElements();
        
        int dataSetMemberCount1 = 0;
        for ( DataElement de1 : dataElements )
        {
            dataSetMemberCount1 += de1.getCategoryCombo().getOptionCombos().size();
        }

        deInfo = getDEInfo( dataElements );

        Iterator<OrganisationUnit> orgUnitListIterator = orgUnitList.iterator();
        OrganisationUnit o;
        Set<OrganisationUnit> dso = new HashSet<OrganisationUnit>();
        Iterator<Period> periodIterator;
        dso = selDataSet.getSources();
        
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

            periodIterator = periodList.iterator();

            Period p;

            String storedby = null;           
           // String userDetailInfo = null;
            List<String> dsResults = new ArrayList<String>();
            while ( periodIterator.hasNext() )
            {
                p = (Period) periodIterator.next();
                periodInfo = "" + p.getId();

                if ( dso == null )
                {
                    dsResults.add( -1, dsId);
                    continue;
                }
                else if ( !dso.contains( o ) )
                {
                    orgUnitInfo = "-1";
       
                    dsResults.add( " " );
                    continue;
                }

                orgUnitInfo = "" + o.getId();

                SqlRowSet sqlResultSet = jdbcTemplate.queryForRowSet( "SELECT DISTINCT(storedby) FROM datavalue WHERE dataelementid IN (" + deInfo
                    + ") AND sourceid IN (" + orgUnitInfo + ") AND periodid IN (" + periodInfo + ")" );

                storedby = " ";
                while ( sqlResultSet.next() )
                {                   
                    
                    try
                    {
                        String tempUserName = sqlResultSet.getString( 1 );
                        
                        if( tempUserName != null)
                        {                      
                            //UserCredentials userCredentials = userStore.getUserCredentialsByUsername( tempUserName );
                            UserCredentials userCredentials  =  userService.getUserCredentialsByUsername( tempUserName );
                            //User user = userStore.getByName( tempUserName );
                            if( userCredentials != null )
                            {
                                //User user = userStore.getUser( userCredentials.getId() );
                                User user = userService.getUser( userCredentials.getId() );
                                if( user != null )
                                {
                                    storedby += user.getFirstName() + " "+ user.getSurname() + " , ";
                                }
                                else
                                {
                                    storedby +=  tempUserName + " , ";
                                }
                            }
                            else
                            {
                                storedby +=  tempUserName + " , ";
                            }
                        }
                        else
                        {
                            storedby = " ";
                        }
                                               
                    }
                    catch ( Exception e )
                    {
                        storedby = "not known";
                    }
                } 
                
                if( storedby.trim().equals( "" ) )
                {                   
                }
                else
                {
                    storedby = storedby.substring( 0, storedby.length()-2 );
                }
                
                SqlRowSet sqlResultSet1 = jdbcTemplate.queryForRowSet( "SELECT DISTINCT(lastupdated) FROM datavalue WHERE dataelementid IN (" + deInfo
                    + ") AND sourceid IN (" + orgUnitInfo + ") AND periodid IN (" + periodInfo + ")" );

                String lastupdated = " ";
                while ( sqlResultSet1.next() )
                {                   
                    try
                    {
                        if( lastupdated.compareTo( sqlResultSet1.getString( 1 ) ) < 0 )
                            lastupdated = sqlResultSet1.getString( 1 );
                    }
                    catch ( Exception e )
                    {
                        lastupdated = "Not Known";
                    }
                }
                
                if( storedby.trim().equals( "" ) && lastupdated.trim().equals( "" ) )
                {
                    dsResults.add( " " );
                }
                else
                {
                    dsResults.add( storedby + " : " + lastupdated );
                }
            }

            ouMapDataStatusResult.put( o, dsResults );
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
                //deleteDataView( dataViewName );               
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

    @SuppressWarnings( "unused" )
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

}// class end

