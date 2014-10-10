package org.hisp.dhis.importexport.action.imp;

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

import static org.hisp.dhis.importexport.action.util.ImportExportInternalProcessUtil.*;

import org.hisp.dhis.external.configuration.NoConfigurationFoundException;
import org.hisp.dhis.importexport.IbatisConfiguration;
import org.hisp.dhis.importexport.IbatisConfigurationManager;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class SetImportTypeAction
    implements Action
{
    //TODO rename to SetImportFormatAction
    
    private static final String CONFIG_DHIS14 = "configDhis14";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private IbatisConfigurationManager configurationManager;

    public void setConfigurationManager( IbatisConfigurationManager configurationManager )
    {
        this.configurationManager = configurationManager;
    }
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String importFormat;

    public void setImportFormat( String importFormat )
    {
        this.importFormat = importFormat;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        // ---------------------------------------------------------------------
        // Verify configuration if dhis14 file format
        // ---------------------------------------------------------------------

        if ( importFormat != null && importFormat.equalsIgnoreCase( "DHIS14FILE" ) )
        {
            try
            {
                IbatisConfiguration config = configurationManager.getIbatisConfiguration();
                        
                if ( config == null || !configurationManager.fileIsValid( config.getDataFile() ) )
                {
                    return CONFIG_DHIS14;
                }
            }
            catch ( NoConfigurationFoundException ex )
            {
                return CONFIG_DHIS14;
            }
        }

        // ---------------------------------------------------------------------
        // Set current format
        // ---------------------------------------------------------------------

        setCurrentRunningProcessImportFormat( importFormat );
        
        return SUCCESS;
    }
}
