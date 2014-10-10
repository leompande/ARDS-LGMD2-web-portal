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
import static org.hisp.dhis.reportsheet.utils.FileUtils.rename;

import java.io.File;

import org.hisp.dhis.reportsheet.ReportLocationManager;
import org.hisp.dhis.reportsheet.action.ActionSupport;
import org.hisp.dhis.reportsheet.state.SelectionManager;

/**
 * @author Dang Duy Hieu
 * @version $Id
 * @since 2010-02-05
 */
public class RenameExcelTemplateAction
    extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private ReportLocationManager reportLocationManager;
    
    private SelectionManager selectionManager;

    public void setSelectionManager( SelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    // -------------------------------------------------------------------------
    // Getter && Setter
    // -------------------------------------------------------------------------

    private String newFileName;

    public void setNewFileName( String newFileName )
    {
        this.newFileName = newFileName;
    }

    private String curFileName;

    public void setCurFileName( String curFileName )
    {
        this.curFileName = curFileName;
    }

    private String renamingMode;

    public void setRenamingMode( String renamingMode )
    {
        this.renamingMode = renamingMode;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        message = "";

        File templateDirectory = reportLocationManager.getExportReportTemplateDirectory();

        File curFile = new File( templateDirectory + File.separator + curFileName );
        File newFile = new File( templateDirectory + File.separator + newFileName );

        selectionManager.setRenameFilePath( curFile.getAbsolutePath() );

        if ( rename( curFile, newFile ) )
        {
            message = i18n.getString( "rename_successful" );
        }
        else
        {
            message = i18n.getString( "rename_failed" );

            return ERROR;
        }

        if ( renamingMode.equals( "RNUS" ) )
        {
            return NONE;
        }

        return SUCCESS;
    }

    public void setReportLocationManager( ReportLocationManager reportLocationManager )
    {
        this.reportLocationManager = reportLocationManager;
    }
}
