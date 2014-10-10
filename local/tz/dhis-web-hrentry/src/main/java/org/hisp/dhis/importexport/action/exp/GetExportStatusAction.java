package org.hisp.dhis.importexport.action.exp;

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

import static org.hisp.dhis.util.InternalProcessUtil.*;

import org.amplecode.cave.process.ProcessCoordinator;
import org.amplecode.cave.process.ProcessExecutor;
import org.amplecode.cave.process.state.MessageState;
import org.hisp.dhis.i18n.I18n;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class GetExportStatusAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProcessCoordinator processCoordinator;

    public void setProcessCoordinator( ProcessCoordinator processCoordinator )
    {
        this.processCoordinator = processCoordinator;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
    
    private String statusMessage = new String();

    public String getStatusMessage()
    {
        return statusMessage;
    }
    
    private boolean finished = false;

    public boolean getFinished()
    {
        return finished;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        if ( processIsRunning( PROCESS_KEY_EXPORT ) )
        {
            String id = getCurrentRunningProcess( PROCESS_KEY_EXPORT );

            ProcessExecutor executor = processCoordinator.getProcess( id );
            
            if ( executor != null && executor.getProcess() != null && executor.getState() != null )
            {
                MessageState state = (MessageState) executor.getState();
                
                statusMessage = i18n.getString( state.getMessage() );
                
                finished = state.isEnded();
            }            
        }
        else
        {
            statusMessage = i18n.getString( "no_process_running" );
            
            finished = false;
        }
        
        return SUCCESS;
    }        
}
