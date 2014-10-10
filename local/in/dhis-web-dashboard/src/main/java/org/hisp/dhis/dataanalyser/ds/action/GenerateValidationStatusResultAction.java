package org.hisp.dhis.dataanalyser.ds.action;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataanalyser.util.DashBoardService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

/**
 * @author brajesh murari
 *
 */
public class GenerateValidationStatusResultAction 
   implements Action
{
    // ---------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------
   
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
    
    private CompleteDataSetRegistrationService registrationService;

    public void setRegistrationService( CompleteDataSetRegistrationService registrationService )
    {
        this.registrationService = registrationService;
    }

    // ---------------------------------------------------------------
    // Output Parameters
    // ---------------------------------------------------------------

    private Map<OrganisationUnit, List<Integer>> ouMapValidationPassStatusResult;

    public Map<OrganisationUnit, List<Integer>> getOuMapValidationPassStatusResult()
    {
        return ouMapValidationPassStatusResult;
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
    
    /*
    private String includeZeros;

    public void setIncludeZeros( String includeZeros )
    {
        this.includeZeros = includeZeros;
    }
    
    public String getIncludeZeros()
    {
        return includeZeros;
    }
    */
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

    @SuppressWarnings( { "unchecked" } )
    public String execute()
        throws Exception
    {
        orgUnitCount = 0;
        
        // Intialization
        periodNameList = new ArrayList<String>();
        ouMapValidationPassStatusResult = new HashMap<OrganisationUnit, List<Integer>>();
        results = new ArrayList<Integer>();
        maxOULevel = 1;
        minOULevel = organisationUnitService.getNumberOfOrganisationalLevels();
        /*
        if( includeZeros.equalsIgnoreCase( "false" ))
        {
            includeZeros = null;
        }
        */
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
                List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>( o.getChildren() );
                //Collections.sort( organisationUnits, new OrganisationUnitShortNameComparator() );
                Collections.sort( organisationUnits, new IdentifiableObjectNameComparator() );
                orgUnitList.addAll( organisationUnits );
                orgUnitList.add( 0, o );
            }
            //System.out.println( "Selected is immediate children" );
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
        periodList = periodService.getPeriodsBetweenDates( dataSetPeriodType, startPeriod.getStartDate(),
            endPeriod.getEndDate() );

        periodInfo = "-1";
        for ( Period p : periodList )
            periodInfo += "," + p.getId();

        Iterator<OrganisationUnit> orgUnitListIterator = orgUnitList.iterator();
        OrganisationUnit o;
        Set<OrganisationUnit> dso = new HashSet<OrganisationUnit>();
        Iterator periodIterator;
        dso = selDataSet.getSources();
        String orgUnitId = "";
        
        while ( orgUnitListIterator.hasNext() )
        {
            //System.out.println( "Getting into first orgunit loop" );
            o = orgUnitListIterator.next();
            orgUnitInfo = "" + o.getId();

            if ( maxOULevel < organisationUnitService.getLevelOfOrganisationUnit( o.getId() ) )
                maxOULevel = organisationUnitService.getLevelOfOrganisationUnit( o.getId() );

            if ( minOULevel > organisationUnitService.getLevelOfOrganisationUnit( o.getId() ) )
                minOULevel = organisationUnitService.getLevelOfOrganisationUnit( o.getId() );

            periodIterator = periodList.iterator();
            Period p;
            //List<Integer> dsResults = new ArrayList<Integer>();
            List<Integer> dsValidationPassResults = new ArrayList<Integer>();
           
            while ( periodIterator.hasNext() )
            {
                p = (Period) periodIterator.next();
                periodInfo = "" + p.getId();
                //System.out.println( "Getting into period loop periodInfo = "+periodInfo );

                if ( dso == null )
                {
                    //dsResults.add( -1 );
                    dsValidationPassResults.add( -1 );
                    continue;
                }
                else if ( !dso.contains( o ) )
                {
                    List<OrganisationUnit> childOrgUnits = new ArrayList<OrganisationUnit>();
                    childOrgUnits = filterChildOrgUnitsByDataSet( dataSetService.getDataSet( Integer
                        .valueOf( selectedDataSets.get( 0 ) ) ), o );
                    Iterator assignedChildrenIterator = childOrgUnits.iterator();
                    Integer dataStatusCount = 0;

                    while ( assignedChildrenIterator.hasNext() )
                    {
                        OrganisationUnit cUnit = (OrganisationUnit) assignedChildrenIterator.next();
                        orgUnitInfo = "-1";
                        orgUnitId = "-1,";
                        orgUnitId += String.valueOf( cUnit.getId() );
                        orgUnitCount = 0;
                        getOrgUnitInfo( o, dso );

                        CompleteDataSetRegistration completeDataSetRegistration = registrationService.getCompleteDataSetRegistration( selDataSet, p, cUnit );

                        if ( completeDataSetRegistration != null )
                        {
                            dataStatusCount += 1;
                        }
                        
                    }
                    //System.out.println("\ndataStatusCount : " + dataStatusCount);
                    //System.out.println(o.getName()+ " : "+dataStatusCount);
                    dsValidationPassResults.add( dataStatusCount );

                    continue;
                }

               // System.out.println("\no = "+o.getName() + " dsValidationPassResults size = "+dsValidationPassResults.size());
                
                orgUnitInfo = "" + o.getId();

                CompleteDataSetRegistration completeDataSetRegistration = registrationService.getCompleteDataSetRegistration( selDataSet, p, o );
                
                if ( completeDataSetRegistration != null )
                {
                    dsValidationPassResults.add( new Integer(1) );
                   // System.out.println(o.getName()+ " : 1");
                }
                else
                {
                    dsValidationPassResults.add( new Integer(0) );
                    //System.out.println(o.getName()+ " : 0");
                    
                }
                
            }
            //System.out.println("o = "+o.getName() + " dsValidationPassResults size = "+dsValidationPassResults.size());
            ouMapValidationPassStatusResult.put( o, dsValidationPassResults );
        }
        
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

    // Returns the OrgUnitTree for which Root is the orgUnit
    @SuppressWarnings( "unchecked" )
    public List<OrganisationUnit> getChildOrgUnitTree( OrganisationUnit orgUnit )
    {
        List<OrganisationUnit> orgUnitTree = new ArrayList<OrganisationUnit>();
        orgUnitTree.add( orgUnit );
        List<OrganisationUnit> children = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
        //Collections.sort( children, new OrganisationUnitNameComparator() );
        Collections.sort( children, new IdentifiableObjectNameComparator() );
        Iterator<OrganisationUnit> childIterator = children.iterator();
        OrganisationUnit child;
        
        while ( childIterator.hasNext() )
        {
            child = childIterator.next();
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

    private List<OrganisationUnit> filterChildOrgUnitsByDataSet( DataSet selectedDataSet,
        OrganisationUnit selectedOrganisationUnit )
    {
        List<OrganisationUnit> filteredOrganisationUnits = getChildOrgUnitTree( selectedOrganisationUnit );

        @SuppressWarnings( "unused" )
        List<OrganisationUnit> assignedOrganisationUnits = new ArrayList<OrganisationUnit>();
        Set<OrganisationUnit> assignedSources = selectedDataSet.getSources();
        filteredOrganisationUnits.retainAll( assignedSources );
        return filteredOrganisationUnits;
    }

}// class end
