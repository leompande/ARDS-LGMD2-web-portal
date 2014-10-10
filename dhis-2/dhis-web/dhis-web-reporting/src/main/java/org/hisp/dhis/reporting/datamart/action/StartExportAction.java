package org.hisp.dhis.reporting.datamart.action;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.analytics.scheduling.AnalyticsTableTask;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.resourcetable.scheduling.ResourceTableTask;
import org.hisp.dhis.scheduling.DataMartTask;
import org.hisp.dhis.scheduling.ScheduledTasks;
import org.hisp.dhis.scheduling.TaskCategory;
import org.hisp.dhis.scheduling.TaskId;
import org.hisp.dhis.system.notification.Notifier;
import org.hisp.dhis.system.scheduling.Scheduler;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 */
public class StartExportAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CurrentUserService currentUserService;
    
    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private Scheduler scheduler;
    
    public void setScheduler( Scheduler scheduler )
    {
        this.scheduler = scheduler;
    }

    private ResourceTableTask resourceTableTask;
        
    public void setResourceTableTask( ResourceTableTask resourceTableTask )
    {
        this.resourceTableTask = resourceTableTask;
    }

    private AnalyticsTableTask analyticsTableTask;
    
    public void setAnalyticsTableTask( AnalyticsTableTask analyticsTableTask )
    {
        this.analyticsTableTask = analyticsTableTask;
    }

    private DataMartTask dataMartTask;

    public void setDataMartTask( DataMartTask dataMartTask )
    {
        this.dataMartTask = dataMartTask;
    }
    
    @Autowired
    private Notifier notifier;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Set<String> periodTypes = new HashSet<String>();
    
    public void setPeriodTypes( Set<String> periodTypes )
    {
        this.periodTypes = periodTypes;
    }

    private String startDate;
    
    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    private String endDate;

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }
    
    private boolean analytics;

    public void setAnalytics( boolean analytics )
    {
        this.analytics = analytics;
    }

    private boolean dataMart;

    public void setDataMart( boolean dataMart )
    {
        this.dataMart = dataMart;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        TaskId taskId = new TaskId( TaskCategory.DATAMART, currentUserService.getCurrentUser() );
        
        notifier.clear( taskId );
        
        ScheduledTasks tasks = new ScheduledTasks();

        // ---------------------------------------------------------------------
        // Analytics
        // ---------------------------------------------------------------------

        if ( analytics )
        {        
            analyticsTableTask.setTaskId( taskId );
            
            tasks.addTask( resourceTableTask );
            tasks.addTask( analyticsTableTask );
        }        

        // ---------------------------------------------------------------------
        // Data mart
        // ---------------------------------------------------------------------

        if ( dataMart )
        {
            Date start = DateUtils.getMediumDate( startDate );
            Date end = DateUtils.getMediumDate( endDate );
            
            List<Period> periods = new ArrayList<Period>();
            
            for ( String type : periodTypes )
            {
                CalendarPeriodType periodType = (CalendarPeriodType) PeriodType.getPeriodTypeByName( type );
                
                periods.addAll( periodType.generatePeriods( start, end ) );
            }
    
            if ( periods.size() > 0 )
            {
                dataMartTask.setPeriods( periods );
                dataMartTask.setTaskId( taskId );
            
                tasks.addTask( dataMartTask );
            }
        }
        
        if ( !tasks.isEmpty() )
        {
            scheduler.executeTask( tasks );
        }
        
        return SUCCESS;
    }
}
