package org.hisp.dhis.system.process;

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

import org.amplecode.cave.process.Process;
import org.amplecode.quick.StatementManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.system.util.DebugUtils;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public abstract class AbstractStatementInternalProcess
    implements Process<OutputHolderState> 
{
    public static final String PROCESS_STARTED = "process_started";
    public static final String PROCESS_COMPLETED = "process_completed";
    public static final String PROCESS_FAILED = "process_failed";
        
    private static final Log log = LogFactory.getLog( AbstractStatementInternalProcess.class );
    
    private OutputHolderState state;

    protected OutputHolderState getState()
    {
        return state;
    }

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }
    
    private StatementManager inMemoryStatementManager;

    public void setInMemoryStatementManager( StatementManager inMemoryStatementManager )
    {
        this.inMemoryStatementManager = inMemoryStatementManager;
    }

    // -------------------------------------------------------------------------
    // InternalProcess implementation
    // -------------------------------------------------------------------------
    
    public final Class<OutputHolderState> getStateClass()
    {
        return OutputHolderState.class;
    }
    
    /**
     * Delegates process execution to {@link #executeStatements()}.
     */
    public final void execute( OutputHolderState state )
    {
        this.state = state;

        statementManager.initialise();
        
        if ( inMemoryStatementManager != null )
        {
            inMemoryStatementManager.initialise();
        }
        
        getState().setMessage( PROCESS_STARTED );
        
        log.info( "Internal process started" );
        
        try
        {
            executeStatements();
            
            getState().setMessage( PROCESS_COMPLETED );
            
            log.info( "Internal process completed successfully" );
        }
        catch ( Exception ex )
        {
            getState().setMessage( PROCESS_FAILED );
            
            log.error( "Internal process failed", ex );
            log.debug( DebugUtils.getStackTrace( ex ) );
            
            ex.printStackTrace();
        }
        finally
        {   
            statementManager.destroy();
            
            if ( inMemoryStatementManager != null )
            {
                inMemoryStatementManager.destroy();
            }
        }
    }

    /**
     * The process method which must be implemented by subclasses.
     */
    protected abstract void executeStatements()
        throws Exception;
}
