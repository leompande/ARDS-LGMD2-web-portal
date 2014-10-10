package org.hisp.dhis.importexport.action.object;

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

import static org.hisp.dhis.importexport.action.util.ImportExportInternalProcessUtil.getCurrentRunningProcessImportFormat;

import org.hisp.dhis.external.configuration.NoConfigurationFoundException;
import org.hisp.dhis.importexport.IbatisConfigurationManager;
import org.hisp.dhis.importexport.ImportObjectService;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class MatchObjectAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer importObjectId;

    public void setImportObjectId( Integer importObjectId )
    {
        this.importObjectId = importObjectId;
    }

    private Integer existingObjectId;

    public void setExistingObjectId( Integer existingObjectId )
    {
        this.existingObjectId = existingObjectId;
    }

    private String importFormat;

    public String getImportFormat()
    {
        return importFormat;
    }

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ImportObjectService importObjectService;

    public void setImportObjectService( ImportObjectService importObjectService )
    {
        this.importObjectService = importObjectService;
    }

    private IbatisConfigurationManager configurationManager;

    public void setConfigurationManager( IbatisConfigurationManager configurationManager )
    {
        this.configurationManager = configurationManager;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        if ( importObjectId != null && existingObjectId != null )
        {
            importObjectService.matchObject( importObjectId, existingObjectId );
        }

        // ---------------------------------------------------------------------
        // Verify import configuration
        // ---------------------------------------------------------------------
        
        importFormat = getCurrentRunningProcessImportFormat();

        if ( importFormat != null && importFormat.equals( "DHIS14FILE" ) )
        {
            try
            {
                configurationManager.getIbatisConfiguration();
            }
            catch ( NoConfigurationFoundException ex )
            {
                return "dhis14";
            }
        }
        
        return SUCCESS;
    }
}
