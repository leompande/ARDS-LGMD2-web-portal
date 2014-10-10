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

import static org.hisp.dhis.setting.SystemSettingManager.DEFAULT_SCHEDULED_PERIOD_TYPES;
import static org.hisp.dhis.setting.SystemSettingManager.KEY_SCHEDULED_PERIOD_TYPES;
import static org.hisp.dhis.system.notification.NotificationLevel.ERROR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.completeness.DataSetCompletenessEngine;
import org.hisp.dhis.datamart.DataMartEngine;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.RelativePeriods;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.system.notification.Notifier;
import org.hisp.dhis.system.util.ConversionUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 */
public class DataMartTask
    implements Runnable
{
    private static final Log log = LogFactory.getLog( DataMartTask.class );
    
    @Autowired
    private DataMartEngine dataMartEngine;

    @Autowired
    private DataSetCompletenessEngine completenessEngine;

    @Autowired
    private PeriodService periodService;

    @Autowired
    private SystemSettingManager systemSettingManager;
    
    @Autowired
    private Notifier notifier;

    @Autowired
    private MessageService messageService;

    private List<Period> periods;
    
    public void setPeriods( List<Period> periods )
    {
        this.periods = periods;
    }
    
    private boolean last6Months;

    public void setLast6Months( boolean last6Months )
    {
        this.last6Months = last6Months;
    }

    private boolean last6To12Months;

    public void setLast6To12Months( boolean last6To12Months )
    {
        this.last6To12Months = last6To12Months;
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
    @SuppressWarnings("unchecked")  
    public void run()
    {
        Set<String> periodTypes = (Set<String>) systemSettingManager.getSystemSetting( KEY_SCHEDULED_PERIOD_TYPES, DEFAULT_SCHEDULED_PERIOD_TYPES );
        
        List<Period> periods = getPeriods( periodTypes );
        
        log.info( "Using periods: " + periods );
        
        Collection<Integer> periodIds = ConversionUtils.getIdentifiers( Period.class, periodService.reloadPeriods( periods ) );
        
        try
        {
            dataMartEngine.export( periodIds, taskId );
            completenessEngine.exportDataSetCompleteness( periodIds, taskId );
        }
        catch ( RuntimeException ex )
        {
            notifier.notify( taskId, ERROR, "Process failed: " + ex.getMessage(), true );
            
            messageService.sendFeedback( "Data mart process failed", "Data mart process failed, please check the logs.", null );

            throw ex;
        }
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    public List<Period> getPeriods( Set<String> periodTypes )
    {
        if ( periods != null && periods.size() > 0 )
        {
            return periods;
        }
        
        List<Period> relatives =  new ArrayList<Period>();
        
        if  ( last6Months )
        {
            relatives.addAll( new RelativePeriods().getLast6Months( periodTypes ) );
        }
        
        if ( last6To12Months )
        {
            relatives.addAll( new RelativePeriods().getLast6To12Months( periodTypes ) );
        }
        
        return relatives;
    }
}
