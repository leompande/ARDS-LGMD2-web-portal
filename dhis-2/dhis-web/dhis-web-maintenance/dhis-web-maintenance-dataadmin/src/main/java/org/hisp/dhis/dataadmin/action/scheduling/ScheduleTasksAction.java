package org.hisp.dhis.dataadmin.action.scheduling;

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

import static org.hisp.dhis.scheduling.SchedulingManager.TASK_ANALYTICS_ALL;
import static org.hisp.dhis.scheduling.SchedulingManager.TASK_ANALYTICS_LAST_3_YEARS;
import static org.hisp.dhis.scheduling.SchedulingManager.TASK_DATAMART_FROM_6_TO_12_MONTS;
import static org.hisp.dhis.scheduling.SchedulingManager.TASK_DATAMART_LAST_12_MONTHS;
import static org.hisp.dhis.scheduling.SchedulingManager.TASK_DATAMART_LAST_6_MONTHS;
import static org.hisp.dhis.scheduling.SchedulingManager.TASK_RESOURCE_TABLE;
import static org.hisp.dhis.scheduling.SchedulingManager.TASK_MONITORING_LAST_DAY;
import static org.hisp.dhis.setting.SystemSettingManager.DEFAULT_ORGUNITGROUPSET_AGG_LEVEL;
import static org.hisp.dhis.setting.SystemSettingManager.DEFAULT_SCHEDULED_PERIOD_TYPES;
import static org.hisp.dhis.setting.SystemSettingManager.KEY_ORGUNITGROUPSET_AGG_LEVEL;
import static org.hisp.dhis.setting.SystemSettingManager.KEY_SCHEDULED_PERIOD_TYPES;
import static org.hisp.dhis.system.scheduling.Scheduler.CRON_DAILY_0AM_EXCEPT_SUNDAY;
import static org.hisp.dhis.system.scheduling.Scheduler.CRON_WEEKLY_SUNDAY_0AM;
import static org.hisp.dhis.system.scheduling.Scheduler.STATUS_RUNNING;
import static org.hisp.dhis.system.util.CollectionUtils.emptyIfNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.ListMap;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.scheduling.SchedulingManager;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.system.scheduling.Scheduler;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 */
public class ScheduleTasksAction
    implements Action
{
    private static final String STRATEGY_LAST_12_DAILY = "last12Daily";
    private static final String STRATEGY_LAST_6_DAILY_6_TO_12_WEEKLY = "last6Daily6To12Weekly";
    private static final String STRATEGY_ALL_DAILY = "allDaily";
    private static final String STRATEGY_LAST_3_YEARS_DAILY = "last3YearsDaily";
    
    private static final Log log = LogFactory.getLog( ScheduleTasksAction.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SystemSettingManager systemSettingManager;
    
    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }
    
    private SchedulingManager schedulingManager;

    public void setSchedulingManager( SchedulingManager schedulingManager )
    {
        this.schedulingManager = schedulingManager;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
    
    private boolean schedule;

    public void setSchedule( boolean schedule )
    {
        this.schedule = schedule;
    }

    private String resourceTableStrategy;

    public String getResourceTableStrategy()
    {
        return resourceTableStrategy;
    }

    public void setResourceTableStrategy( String resourceTableStrategy )
    {
        this.resourceTableStrategy = resourceTableStrategy;
    }

    private String analyticsStrategy;
    
    public String getAnalyticsStrategy()
    {
        return analyticsStrategy;
    }

    public void setAnalyticsStrategy( String analyticsStrategy )
    {
        this.analyticsStrategy = analyticsStrategy;
    }

    private Set<String> scheduledPeriodTypes = new HashSet<String>();

    public Set<String> getScheduledPeriodTypes()
    {
        return scheduledPeriodTypes;
    }

    public void setScheduledPeriodTypes( Set<String> scheduledPeriodTypes )
    {
        this.scheduledPeriodTypes = scheduledPeriodTypes;
    }
    
    private Integer orgUnitGroupSetAggLevel;
    
    public Integer getOrgUnitGroupSetAggLevel()
    {
        return orgUnitGroupSetAggLevel;
    }

    public void setOrgUnitGroupSetAggLevel( Integer orgUnitGroupSetAggLevel )
    {
        this.orgUnitGroupSetAggLevel = orgUnitGroupSetAggLevel;
    }

    private String dataMartStrategy;

    public String getDataMartStrategy()
    {
        return dataMartStrategy;
    }

    public void setDataMartStrategy( String dataMartStrategy )
    {
        this.dataMartStrategy = dataMartStrategy;
    }
    
    private String monitoringStrategy;

    public String getMonitoringStrategy()
    {
        return monitoringStrategy;
    }

    public void setMonitoringStrategy( String monitoringStrategy )
    {
        this.monitoringStrategy = monitoringStrategy;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private String status;

    public String getStatus()
    {
        return status;
    }

    private boolean running;

    public boolean isRunning()
    {
        return running;
    }
        
    private List<OrganisationUnitLevel> levels;

    public List<OrganisationUnitLevel> getLevels()
    {
        return levels;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public String execute()
    {        
        if ( schedule )
        {
            systemSettingManager.saveSystemSetting( KEY_SCHEDULED_PERIOD_TYPES, (HashSet<String>) scheduledPeriodTypes );
            systemSettingManager.saveSystemSetting( KEY_ORGUNITGROUPSET_AGG_LEVEL, orgUnitGroupSetAggLevel );
            
            if ( Scheduler.STATUS_RUNNING.equals( schedulingManager.getTaskStatus() ) )
            {
                schedulingManager.stopTasks();
            }
            else
            {
                ListMap<String, String> cronKeyMap = new ListMap<String, String>();
                
                // -------------------------------------------------------------
                // Resource tables
                // -------------------------------------------------------------

                if ( STRATEGY_ALL_DAILY.equals( resourceTableStrategy ) )
                {
                    cronKeyMap.putValue( CRON_DAILY_0AM_EXCEPT_SUNDAY, TASK_RESOURCE_TABLE );
                    cronKeyMap.putValue( CRON_WEEKLY_SUNDAY_0AM, TASK_RESOURCE_TABLE );
                }
                
                // -------------------------------------------------------------
                // Analytics
                // -------------------------------------------------------------

                if ( STRATEGY_ALL_DAILY.equals( analyticsStrategy ) )
                {
                    cronKeyMap.putValue( CRON_DAILY_0AM_EXCEPT_SUNDAY, TASK_ANALYTICS_ALL );
                    cronKeyMap.putValue( CRON_WEEKLY_SUNDAY_0AM, TASK_ANALYTICS_ALL );
                }
                else if ( STRATEGY_LAST_3_YEARS_DAILY.equals( analyticsStrategy ) )
                {
                    cronKeyMap.putValue( CRON_DAILY_0AM_EXCEPT_SUNDAY, TASK_ANALYTICS_LAST_3_YEARS );
                    cronKeyMap.putValue( CRON_WEEKLY_SUNDAY_0AM, TASK_ANALYTICS_LAST_3_YEARS );
                }
                
                // -------------------------------------------------------------
                // Data mart
                // -------------------------------------------------------------
                
                if ( STRATEGY_LAST_12_DAILY.equals( dataMartStrategy ) )
                {
                    cronKeyMap.putValue( CRON_DAILY_0AM_EXCEPT_SUNDAY, TASK_DATAMART_LAST_12_MONTHS );
                    cronKeyMap.putValue( CRON_WEEKLY_SUNDAY_0AM, TASK_DATAMART_LAST_12_MONTHS );
                }
                else if ( STRATEGY_LAST_6_DAILY_6_TO_12_WEEKLY.equals( dataMartStrategy ) )
                {
                    cronKeyMap.putValue( CRON_DAILY_0AM_EXCEPT_SUNDAY, TASK_DATAMART_LAST_6_MONTHS );
                    cronKeyMap.putValue( CRON_WEEKLY_SUNDAY_0AM, TASK_DATAMART_FROM_6_TO_12_MONTS );
                }

                // -------------------------------------------------------------
                // Monitoring
                // -------------------------------------------------------------
                
                if ( STRATEGY_ALL_DAILY.equals( monitoringStrategy ) )
                {
                    cronKeyMap.putValue( CRON_DAILY_0AM_EXCEPT_SUNDAY, TASK_MONITORING_LAST_DAY );
                    cronKeyMap.putValue( CRON_WEEKLY_SUNDAY_0AM, TASK_MONITORING_LAST_DAY );
                }
                
                schedulingManager.scheduleTasks( cronKeyMap );
            }
        }
        else
        {
            Collection<String> keys = emptyIfNull( schedulingManager.getCronKeyMap().get( CRON_DAILY_0AM_EXCEPT_SUNDAY ) );
            
            // -----------------------------------------------------------------
            // Resource tables
            // -----------------------------------------------------------------

            if ( keys.contains( TASK_RESOURCE_TABLE ) )
            {
                resourceTableStrategy = STRATEGY_ALL_DAILY;
            }
            
            // -----------------------------------------------------------------
            // Analytics
            // -----------------------------------------------------------------

            if ( keys.contains( TASK_ANALYTICS_ALL ) )
            {
                analyticsStrategy = STRATEGY_ALL_DAILY;
            }
            else if ( keys.contains( TASK_ANALYTICS_LAST_3_YEARS ) )
            {
                analyticsStrategy = STRATEGY_LAST_3_YEARS_DAILY;
            }
            
            // -----------------------------------------------------------------
            // Data mart
            // -----------------------------------------------------------------

            if ( keys.contains( TASK_DATAMART_LAST_12_MONTHS ) )
            {
                dataMartStrategy = STRATEGY_LAST_12_DAILY;
            }
            else if ( keys.contains( TASK_DATAMART_LAST_6_MONTHS ) )
            {
                dataMartStrategy = STRATEGY_LAST_6_DAILY_6_TO_12_WEEKLY;
            }

            // -------------------------------------------------------------
            // Monitoring
            // -------------------------------------------------------------
            
            if ( keys.contains( TASK_MONITORING_LAST_DAY ) )
            {
                monitoringStrategy = STRATEGY_ALL_DAILY;
            }
        }
        
        scheduledPeriodTypes = (Set<String>) systemSettingManager.getSystemSetting( KEY_SCHEDULED_PERIOD_TYPES, DEFAULT_SCHEDULED_PERIOD_TYPES );
        orgUnitGroupSetAggLevel = (Integer) systemSettingManager.getSystemSetting( KEY_ORGUNITGROUPSET_AGG_LEVEL, DEFAULT_ORGUNITGROUPSET_AGG_LEVEL );
        
        status = schedulingManager.getTaskStatus();        
        running = STATUS_RUNNING.equals( status );
        
        levels = organisationUnitService.getOrganisationUnitLevels();

        log.info( "Status: " + status );
        log.info( "Running: " + running );
        
        return SUCCESS;
    }
}
