package org.hisp.dhis.reportsheet.filemanager.action;

/*
 * Copyright (c) 2004-2011, University of Oslo
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

import java.io.File;

import org.hisp.dhis.reportsheet.ReportLocationManager;
import org.hisp.dhis.reportsheet.action.ActionSupport;
import org.hisp.dhis.reportsheet.state.SelectionManager;
import org.hisp.dhis.system.util.StreamUtils;

/**
 * @author Dang Duy Hieu
 * @version $Id
 * @since 2010-01-28
 */

public class UploadExcelFileAction
    extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private ReportLocationManager reportLocationManager;

    public void setReportLocationManager( ReportLocationManager reportLocationManager )
    {
        this.reportLocationManager = reportLocationManager;
    }

    private SelectionManager selectionManager;

    public void setSelectionManager( SelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String fileName;

    private File upload;

    private boolean isDraft;

    private boolean allowNewName = true;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private String mode;

    // -------------------------------------------------------------------------
    // Getter && Setter
    // -------------------------------------------------------------------------

    public void setUploadFileName( String fileName )
    {
        this.fileName = fileName;
    }

    public void setUpload( File upload )
    {
        this.upload = upload;
    }

    public String getMode()
    {
        return mode;
    }

    public void setDraft( boolean isDraft )
    {
        this.isDraft = isDraft;
    }

    public void setAllowNewName( boolean allowNewName )
    {
        this.allowNewName = allowNewName;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        mode = "upload";

        File output = null;
        File directory = null;

        if ( isDraft )
        {
            if ( allowNewName )
            {
                directory = reportLocationManager.getExportReportTemporaryDirectory();

                output = new File( directory, (Math.random() * 1000) + fileName );
            }
            else
            {
                directory = reportLocationManager.getExportReportTemporaryDirectory();

                output = new File( directory, fileName );
            }
        }
        else
        {
            directory = reportLocationManager.getExportReportTemplateDirectory();

            output = new File( directory, fileName );
        }

        selectionManager.setUploadFilePath( output.getAbsolutePath() );

        try
        {
            StreamUtils.write( upload, output );
        }
        catch ( Exception e )
        {
            message = "cannot_write_file_being_used";

            return SUCCESS;
        }

        if ( output.exists() )
        {
            message = "override_successful";
        }
        else
        {
            message = "upload_successful";
        }

        return SUCCESS;
    }
}
