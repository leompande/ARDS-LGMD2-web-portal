package org.hisp.dhis.alert.db.action;

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
import java.util.List;

import org.hisp.dhis.alert.util.AlertUtility;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Samta Bajpai
 * 
 * @version TrackerDashBoardAction.java May 28, 2012 12:45 PM
 */

public class DashBoardAction implements Action
{

    private final String DASHBOARD_DATASET = "DASHBOARD";
    
    // ---------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private AlertUtility alertUtility;

    public void setAlertUtility( AlertUtility alertUtility )
    {
        this.alertUtility = alertUtility;
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

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // ---------------------------------------------------------------
    // Input & Output
    // ---------------------------------------------------------------
    
    private String customDataEntryFormCode;

    public String getCustomDataEntryFormCode()
    {
        return customDataEntryFormCode;
    }
    
    String drillDownOrgUnitId;
    
    public void setDrillDownOrgUnitId( String drillDownOrgUnitId )
    {
        this.drillDownOrgUnitId = drillDownOrgUnitId;
    }

    String navigationString;
    
    public String getNavigationString()
    {
        return navigationString;
    }
    
    List<OrganisationUnit> orgUnitList;
    
    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }
    
    OrganisationUnit selOrgUnit;
    
    public OrganisationUnit getSelOrgUnit()
    {
        return selOrgUnit;
    }

    String aggOption;
    
    public void setAggOption( String aggOption )
    {
        this.aggOption = aggOption;
    }

    // ---------------------------------------------------------------
    // Action Implementation
    // ---------------------------------------------------------------
    
    public String execute() throws Exception
    {
        int monthDays[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        
        navigationString = "Dashboard";
        orgUnitList = new ArrayList<OrganisationUnit>();
        
        if( aggOption == null || aggOption.trim().equals( "" ) )
        {
            aggOption = AlertUtility.USEEXISTINGAGGDATA;
        }

        // Period Info
        
        Date toDay = new Date();
        Calendar endCal = Calendar.getInstance();
        endCal.setTime( toDay );
        endCal.add( Calendar.MONTH, -1 );
        endCal.set( Calendar.DATE, 1 );
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //String periodId = "Monthly_"+simpleDateFormat.format( cal.getTime() )+"_";
                
        if ( ( endCal.get(  Calendar.YEAR ) % 400 == 0 || endCal.get( Calendar.YEAR ) % 4 == 0 ) && endCal.get( Calendar.MONTH ) == 1 )
        {
            endCal.set( Calendar.DATE, monthDays[Calendar.MONTH] + 1 );
        }
        else
        {
            endCal.set( Calendar.DATE, monthDays[Calendar.MONTH] );
        }
        Date eDate = endCal.getTime();
        
        
        if ( endCal.get( Calendar.MONTH ) < Calendar.APRIL )
        {
            endCal.roll( Calendar.YEAR, -1 );
        }
        endCal.set( Calendar.MONTH, Calendar.APRIL );
        endCal.set( Calendar.DATE, 1 );
        
        //periodId += simpleDateFormat.format( cal.getTime() );
        Date sDate = endCal.getTime();
        
        
        List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate, eDate ) );
        
        Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );
        
        String periodIdsByComma = getCommaDelimitedString( periodIds );        
        
        //Period selectedPeriod = periodService.getPeriodByExternalId( periodId );
        
        DataSet selectedDataSet = dataSetService.getDataSetByCode( DASHBOARD_DATASET );
        
        List<OrganisationUnit> rootOrgUnitList = new ArrayList<OrganisationUnit>( );
        rootOrgUnitList.addAll( currentUserService.getCurrentUser().getOrganisationUnits() );

        if( drillDownOrgUnitId == null )
        {
            if( rootOrgUnitList != null && rootOrgUnitList.size() > 0 )
            {
                navigationString += " -> " + rootOrgUnitList.get( 0 ).getName();
                selOrgUnit = rootOrgUnitList.get( 0 );
            }
            else
            {
                navigationString += " -> NO FACILITY";
            }
        }
        else
        {
            selOrgUnit = organisationUnitService.getOrganisationUnit(  Integer.parseInt( drillDownOrgUnitId ) );
            navigationString += " -> " + selOrgUnit.getName();
        }
        
        navigationString += " ( " + simpleDateFormat.format( sDate ) + " TO " + simpleDateFormat.format( eDate ) + " )";
        
        for( OrganisationUnit orgUnit : rootOrgUnitList )
        {            
            List<OrganisationUnit> tempOuList = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
            Collections.sort( tempOuList, new IdentifiableObjectNameComparator() );

            orgUnitList.add( orgUnit );
            orgUnitList.addAll( tempOuList );
        }
        
        
        if( selectedDataSet == null || selOrgUnit == null || periodIdsByComma == null )
        {
            customDataEntryFormCode = " ";
        }
        else
        {
            customDataEntryFormCode = alertUtility.getCustomDataSetReport( selectedDataSet, selOrgUnit, periodIdsByComma, aggOption, format );
        }
        
        
        return SUCCESS;
    }
}
