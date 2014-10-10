package org.hisp.dhis.analytics.scheduling;

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

import static org.hisp.dhis.system.notification.NotificationLevel.ERROR;
import static org.hisp.dhis.system.notification.NotificationLevel.INFO;

import javax.annotation.Resource;

import org.hisp.dhis.analytics.AnalyticsTableService;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.scheduling.TaskId;
import org.hisp.dhis.system.notification.Notifier;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 */
public class AnalyticsTableTask
    implements Runnable
{
    @Resource(name="org.hisp.dhis.analytics.AnalyticsTableService")
    private AnalyticsTableService analyticsTableService;

    @Resource(name="org.hisp.dhis.analytics.CompletenessTableService")
    private AnalyticsTableService completenessTableService;
    
    @Resource(name="org.hisp.dhis.analytics.CompletenessTargetTableService")
    private AnalyticsTableService completenessTargetTableService;
    
    @Resource(name="org.hisp.dhis.analytics.EventAnalyticsTableService")
    private AnalyticsTableService eventAnalyticsTableService;
    
    @Autowired
    private Notifier notifier;
    
    @Autowired
    private MessageService messageService;

    private boolean last3Years = false;

    public void setLast3Years( boolean last3Years )
    {
        this.last3Years = last3Years;
    }
    
    private boolean skipResourceTables = false;

    public void setSkipResourceTables( boolean skipResourceTables )
    {
        this.skipResourceTables = skipResourceTables;
    }
    
    private boolean skipAggregate = false;
    
    public void setSkipAggregate( boolean skipAggregate )
    {
        this.skipAggregate = skipAggregate;
    }

    private boolean skipEvents = false;

    public void setSkipEvents( boolean skipEvents )
    {
        this.skipEvents = skipEvents;
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
        notifier.clear( taskId ).notify( taskId, "Updating resource tables" );

        try
        {
            if ( !skipResourceTables )
            {
                analyticsTableService.generateResourceTables();    
                notifier.notify( taskId, "Updating analytics tables" );
            }
            
            if ( !skipAggregate )
            {
                analyticsTableService.update( last3Years, taskId );
                notifier.notify( taskId, "Updating completeness tables" );
            
                completenessTableService.update( last3Years, taskId );    
                notifier.notify( taskId, "Updating compeleteness target table" );
            
                completenessTargetTableService.update( last3Years, taskId );            
                notifier.notify( taskId, "Updating event analytics tables" );
            }
            
            if ( !skipEvents )
            {
                eventAnalyticsTableService.update( last3Years, taskId );
                notifier.notify( taskId, INFO, "Analytics tables updated", true );
            }
        }
        catch ( RuntimeException ex )
        {
            notifier.notify( taskId, ERROR, "Process failed: " + ex.getMessage(), true );
            
            messageService.sendFeedback( "Analytics table process failed", "Analytics table process failed, please check the logs.", null );
            
            throw ex;
        }
    }
}
