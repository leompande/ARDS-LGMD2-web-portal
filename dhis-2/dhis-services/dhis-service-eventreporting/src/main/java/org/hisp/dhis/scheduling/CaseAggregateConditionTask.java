package org.hisp.dhis.scheduling;

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

import static org.hisp.dhis.setting.SystemSettingManager.DEFAULT_SCHEDULE_AGGREGATE_QUERY_BUILDER_TASK_STRATEGY;
import static org.hisp.dhis.setting.SystemSettingManager.KEY_SCHEDULE_AGGREGATE_QUERY_BUILDER_TASK_STRATEGY;
import static org.hisp.dhis.system.notification.NotificationLevel.INFO;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.caseaggregation.CaseAggregateSchedule;
import org.hisp.dhis.caseaggregation.CaseAggregationConditionService;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.system.notification.Notifier;
import org.hisp.dhis.system.util.Clock;
import org.hisp.dhis.system.util.SystemUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * @author Chau Thu Tran
 * 
 * @version CaseAggregateConditionTask.java 9:52:10 AM Oct 10, 2012 $
 */
public class CaseAggregateConditionTask
    implements Runnable
{
    private CaseAggregationConditionService aggregationConditionService;

    public void setAggregationConditionService( CaseAggregationConditionService aggregationConditionService )
    {
        this.aggregationConditionService = aggregationConditionService;
    }

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Notifier notifier;

    public void setNotifier( Notifier notifier )
    {
        this.notifier = notifier;
    }

    private TaskId taskId;

    public void setTaskId( TaskId taskId )
    {
        this.taskId = taskId;
    }

    // -------------------------------------------------------------------------
    // Runnable implementation
    // -------------------------------------------------------------------------

    @Override
    public void run()
    {
        final int cpuCores = SystemUtils.getCpuCores();
        Clock clock = new Clock().startClock().logTime(
            "Aggregate process started, number of CPU cores: " + cpuCores + ", " + SystemUtils.getMemoryString() );
        notifier.clear( taskId ).notify( taskId, "Aggregate process started" );

        String taskStrategy = (String) systemSettingManager.getSystemSetting(
            KEY_SCHEDULE_AGGREGATE_QUERY_BUILDER_TASK_STRATEGY, DEFAULT_SCHEDULE_AGGREGATE_QUERY_BUILDER_TASK_STRATEGY );

        // Get datasets which are used in case-aggregate-query-builder formula
        
        String datasetSQL = "select dm.datasetid as datasetid, pt.name as periodtypename, ds.name as datasetname";
        datasetSQL += "      from caseaggregationcondition cagg inner join datasetmembers dm ";
        datasetSQL += "            on cagg.aggregationdataelementid=dm.dataelementid inner join dataset ds ";
        datasetSQL += "            on ds.datasetid = dm.datasetid inner join periodtype pt ";
        datasetSQL += "            on pt.periodtypeid=ds.periodtypeid ";

        SqlRowSet rsDataset = jdbcTemplate.queryForRowSet( datasetSQL );
        List<CaseAggregateSchedule> caseAggregateSchedule = new ArrayList<CaseAggregateSchedule>();
        while ( rsDataset.next() )
        {
            CaseAggregateSchedule dataSet = new CaseAggregateSchedule( rsDataset.getInt( "datasetid" ),
                rsDataset.getString( "datasetname" ), rsDataset.getString( "periodtypename" ) );
            caseAggregateSchedule.add( dataSet );
        }

        aggregationConditionService.aggregate( caseAggregateSchedule, taskStrategy );

        clock.logTime( "Improrted aggregate data completed " );

        notifier.notify( taskId, INFO, "Improrted aggregate data completed", true );
    }    
}
