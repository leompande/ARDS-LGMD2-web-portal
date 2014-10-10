package org.hisp.dhis.importexport;

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

import java.io.InputStream;

import org.amplecode.cave.process.SerialToGroup;
import org.hisp.dhis.system.process.AbstractStatementInternalProcess;

/**
 * @author Lars Helge Overland
 * @version $Id: ImportInternalProcess.java 6443 2008-11-22 10:12:11Z larshelg $
 */
public class HrImportInternalProcess
    extends AbstractStatementInternalProcess implements SerialToGroup
{
    private static final String PROCESS_GROUP = "ImportProcessGroup";
    
    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    protected ImportParams params;

    public final void setImportParams( ImportParams params )
    {
        this.params = params;
    }
    
    protected InputStream inputStream;

    public final void setInputStream( InputStream inputStream )
    {
        this.inputStream = inputStream;
    }

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ImportService importService;

    public void setImportService( ImportService importService )
    {
        this.importService = importService;
    }
    
    // -------------------------------------------------------------------------
    // SerialToGroup implementation
    // -------------------------------------------------------------------------

    public String getGroup()
    {
        return PROCESS_GROUP;
    }
    
    // -------------------------------------------------------------------------
    // AbstractStatementInternalProcess implementation
    // -------------------------------------------------------------------------

    public void executeStatements()
        throws Exception
    {
        importService.importData( params, inputStream, getState() );
    }
}
