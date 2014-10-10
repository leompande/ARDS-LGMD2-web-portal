package org.hisp.dhis.alert.tdb.action;

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

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.alert.util.AlertUtility;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;

import com.opensymphony.xwork2.Action;

/**
 * @author Samta Bajpai
 * 
 * @version TrackerDashBoardAction.java May 28, 2012 11:47:12 AM
 */

public class TrackerDashBoardAction implements Action
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

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
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

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private AlertUtility alertUtility;
    
    public void setAlertUtility( AlertUtility alertUtility )
    {
        this.alertUtility = alertUtility;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    // ---------------------------------------------------------------
    // Input & Output
    // ---------------------------------------------------------------

    private String resultString;

    public String getResultString()
    {
        return resultString;
    }

    List<OrganisationUnit> immChildrenList;

    public List<OrganisationUnit> getImmChildrenList()
    {
        return immChildrenList;
    }

    Map<String, Integer> orgUnit_ProgramMap;
    
    public Map<String, Integer> getOrgUnit_ProgramMap()
    {
        return orgUnit_ProgramMap;
    }

    Map<String, Integer> totalEnrollCountMap;

    public Map<String, Integer> getTotalEnrollCountMap()
    {
        return totalEnrollCountMap;
    }

    Map<String, Integer> totalEnrollCountForSelDateMap;
    
    public Map<String, Integer> getTotalEnrollCountForSelDateMap()
    {
        return totalEnrollCountForSelDateMap;
    }

    Integer totalRegCountForSelDate = 0;
    
    public Integer getTotalRegCountForSelDate()
    {
        return totalRegCountForSelDate;
    }

    Integer totalRegCount = 0;
    
    public Integer getTotalRegCount()
    {
        return totalRegCount;
    }

    List<Integer> totalRegCountList;
    
    public List<Integer> getTotalRegCountList()
    {
        return totalRegCountList;
    }

    List<Integer> totalRegCountListForSelDate;
    
    public List<Integer> getTotalRegCountListForSelDate()
    {
        return totalRegCountListForSelDate;
    }

    private List<Program> programList;

    public List<Program> getProgramList()
    {
        return programList;
    }

    String rootOrgUnitName;

    public String getRootOrgUnitName()
    {
        return rootOrgUnitName;
    }

    List<Integer> rootOrgUnitEnrollCountList;
    
    public List<Integer> getRootOrgUnitEnrollCountList()
    {
        return rootOrgUnitEnrollCountList;
    }

    String drillDownOrgUnitId;
    
    public void setDrillDownOrgUnitId( String drillDownOrgUnitId )
    {
        this.drillDownOrgUnitId = drillDownOrgUnitId;
    }
    
    public String getDrillDownOrgUnitId()
    {
        return drillDownOrgUnitId;
    }

    String navigationString;
    
    public String getNavigationString()
    {
        return navigationString;
    }

    private String toDaysDate;
    
    public String getToDaysDate()
    {
        return toDaysDate;
    }
    
    // ---------------------------------------------------------------
    // Action Implementation
    // ---------------------------------------------------------------

    public String execute()
        throws Exception
    {
        statementManager.initialise();

        immChildrenList = new ArrayList<OrganisationUnit>();
        totalEnrollCountMap = new HashMap<String, Integer>();
        programList = new ArrayList<Program>();
        rootOrgUnitEnrollCountList = new ArrayList<Integer>();
        totalRegCountList = new ArrayList<Integer>();
        totalRegCountListForSelDate = new ArrayList<Integer>();
        totalEnrollCountForSelDateMap = new HashMap<String, Integer>();
        orgUnit_ProgramMap = new HashMap<String, Integer>();
        
        resultString = "";
        
        navigationString = "Tracker Dashboard";
        
        Date toDay = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime( toDay );
        //cal.roll( Calendar.DATE, false );
        cal.add( Calendar.DATE, -1 );
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        toDaysDate = simpleDateFormat.format( cal.getTime() );
        
        // getIndicatorValues();

        programList.addAll( programService.getAllPrograms() );

        if( programList != null && programList.size() > 0 )
        {
            
            Iterator<Program> progIterator = programList.iterator();
            while( progIterator.hasNext() )
            {
                Program prg = progIterator.next();
                
                //System.out.println( "--- Program name : "+ prg.getName() + "-- Program Type : "  + prg.getType() );
                if( prg.getOrganisationUnits() == null || prg.getOrganisationUnits().size() <= 0 || prg.getType() == Program.SINGLE_EVENT_WITHOUT_REGISTRATION )
                //if( prg.getOrganisationUnits() == null || prg.getOrganisationUnits().size() <= 0 )
                {
                    progIterator.remove();
                }
            }

            List<OrganisationUnit> rootOrgUnitList = new ArrayList<OrganisationUnit>( );
            if( drillDownOrgUnitId != null )
            {
                rootOrgUnitList.add( organisationUnitService.getOrganisationUnit( Integer.parseInt( drillDownOrgUnitId ) ) );
                List<OrganisationUnit> orgUnitBrach = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitBranch( Integer.parseInt( drillDownOrgUnitId ) ) );
                int flag = 1;
                for( OrganisationUnit orgUnit : orgUnitBrach )
                {
                    if( currentUserService.getCurrentUser().getOrganisationUnits().contains( orgUnit) )
                    {
                        flag = 2;
                    }
                    if( flag == 2)
                    {
                        navigationString += " -> <a href=\"trackerDashboardPage.action?drillDownOrgUnitId="+orgUnit.getId()+"\">" + orgUnit.getName() +"</a>";
                    }
                }
            }
            else
            {
                
                rootOrgUnitList.addAll( currentUserService.getCurrentUser().getOrganisationUnits() );
            }
            
            for( OrganisationUnit orgUnit : rootOrgUnitList )
            {
                rootOrgUnitName = orgUnit.getName() + ", ";
                List<OrganisationUnit> tempOuList = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
                Collections.sort( tempOuList, new IdentifiableObjectNameComparator() );
    
                immChildrenList.addAll( tempOuList );
    
                for( OrganisationUnit ou : tempOuList )
                {
                    List<OrganisationUnit> childTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( ou.getId() ) );
                    String orgUnitIdsByComma = getCommaDelimitedString( getIdentifiers( OrganisationUnit.class, childTree ) );
    
                    Map<Integer, Integer> enrollCountMap = alertUtility.getTotalEnrolledNumber( orgUnitIdsByComma );
                    
                    Map<Integer, Integer> enrollCountForSelDateMap = alertUtility.getTotalEnrolledNumberForSelectedDate( orgUnitIdsByComma, toDaysDate );
                    if ( enrollCountMap != null )
                    {
                        for ( Program program : programList )
                        {
                            if( program.getOrganisationUnits().contains( ou ) )
                            {
                                orgUnit_ProgramMap.put( program.getId()+":"+ou.getId(), 1 );
                            }
                            else
                            {
                                orgUnit_ProgramMap.put( program.getId()+":"+ou.getId(), 0 );
                            }
                            
                            Integer tempResult = enrollCountMap.get( program.getId() );
                            if ( tempResult == null )
                            {
                                tempResult = 0;
                            }
                            totalEnrollCountMap.put( program.getId()+":"+ou.getId(), tempResult );
                            Integer tempInteger = totalEnrollCountMap.get( rootOrgUnitName+":"+program.getId() );
                            if( tempInteger != null )
                            {
                                totalEnrollCountMap.put( rootOrgUnitName+":"+program.getId(), tempResult+tempInteger );
                            }
                            else
                            {
                                totalEnrollCountMap.put( rootOrgUnitName+":"+program.getId(), tempResult );
                            }
                            
                            Integer tempResult1 = enrollCountForSelDateMap.get( program.getId() );
                            if( tempResult1 == null )
                            {
                                tempResult1 = 0;
                            }
                            totalEnrollCountForSelDateMap.put( program.getId()+":"+ou.getId(), tempResult1 );
                            Integer tempInteger1 = totalEnrollCountForSelDateMap.get( rootOrgUnitName+":"+program.getId() );
                            if( tempInteger1 != null )
                            {
                                totalEnrollCountForSelDateMap.put( rootOrgUnitName+":"+program.getId(), tempResult1+tempInteger1 );
                            }
                            else
                            {
                                totalEnrollCountForSelDateMap.put( rootOrgUnitName+":"+program.getId(), tempResult1 );
                            }
                        }
                    }
                    
                    Integer regCount = alertUtility.getTotalRegisteredCount( orgUnitIdsByComma );
                    
                    totalRegCountList.add( regCount  );
                    
                    totalRegCount += regCount;
                    
                    Integer regCountForSelDate = alertUtility.getTotalRegisteredCountForSelDate( orgUnitIdsByComma, toDaysDate );
                    
                    totalRegCountListForSelDate.add( regCountForSelDate );
                    
                    totalRegCountForSelDate += regCountForSelDate;
                }
            }
        }
        
        statementManager.destroy();

        return SUCCESS;
    }


    @SuppressWarnings( "unused" )
    private void getIndicatorValues()
    {
        // OrgUnit Info
        
        User curUser = currentUserService.getCurrentUser();
        Collection<OrganisationUnit> ouList = curUser.getOrganisationUnits();
        OrganisationUnit orgUnit;
        if ( ouList == null || ouList.isEmpty() )
        {
            ouList = organisationUnitService.getOrganisationUnitsAtLevel( 1 );
            if ( ouList == null || ouList.isEmpty() )
            {
                System.out.println( " There are no OrgUnits " );
                resultString = "There are no OrgUnits";
                return;
            }
            else
            {
                orgUnit = ouList.iterator().next();
            }
        }
        else
        {
            orgUnit = ouList.iterator().next();
        }

        // Indicator Info
        
        Collection<Indicator> indicatorList = indicatorService.getAllIndicators();

        if ( indicatorList == null || indicatorList.isEmpty() )
        {
            System.out.println( " There are no Indicators " );
            resultString = "There are no Indicators";
            return;
        }

        // Period Info
        
        Date sysDate = new Date();
        Period selPeriod = getPreviousPeriod( sysDate );

        if ( selPeriod == null )
        {
            System.out.println( " There are no Period " );
            resultString = "There are no Period";
            return;
        }

        for ( Indicator ind : indicatorList )
        {
            double aggVal = aggregationService.getAggregatedIndicatorValue( ind, selPeriod.getStartDate(), selPeriod
                .getEndDate(), orgUnit );

            if ( aggVal == -1 )
                aggVal = 0.0;

            aggVal = Math.round( aggVal * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );

            if ( aggVal > 0 )
                resultString += "** " + ind.getName() + " ( " + aggVal + " ) ";
        }

        if ( resultString.trim().equals( "" ) )
            resultString = "NONE";

    }

    public Period getPreviousPeriod( Date sDate )
    {
        Period period = new Period();
        Calendar tempDate = Calendar.getInstance();
        tempDate.setTime( sDate );
        if ( tempDate.get( Calendar.MONTH ) == Calendar.JANUARY )
        {
            tempDate.set( Calendar.MONTH, Calendar.DECEMBER );
            tempDate.roll( Calendar.YEAR, -1 );
        }
        else
        {
            tempDate.roll( Calendar.MONTH, -1 );
        }
        PeriodType monthlyPeriodType = new MonthlyPeriodType();
        period = getPeriodByMonth( tempDate.get( Calendar.MONTH ), tempDate.get( Calendar.YEAR ), monthlyPeriodType );

        return period;
    }

    public Period getPeriodByMonth( int month, int year, PeriodType periodType )
    {
        int monthDays[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

        Calendar cal = Calendar.getInstance();
        cal.set( year, month, 1, 0, 0, 0 );
        Date firstDay = new Date( cal.getTimeInMillis() );

        if ( periodType.getName().equals( "Monthly" ) )
        {
            cal.set( year, month, 1, 0, 0, 0 );
            if ( year % 4 == 0 )
            {
                cal.set( Calendar.DAY_OF_MONTH, monthDays[month] + 1 );
            }
            else
            {
                cal.set( Calendar.DAY_OF_MONTH, monthDays[month] );
            }
        }
        else if ( periodType.getName().equals( "Yearly" ) )
        {
            cal.set( year, Calendar.DECEMBER, 31 );
        }
        Date lastDay = new Date( cal.getTimeInMillis() );        
        Period newPeriod = new Period();
        newPeriod = periodService.getPeriod( firstDay, lastDay, periodType );
        return newPeriod;
    }
}
