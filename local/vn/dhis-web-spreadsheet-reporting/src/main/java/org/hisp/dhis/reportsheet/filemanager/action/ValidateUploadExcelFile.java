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

import static org.apache.commons.io.FilenameUtils.getExtension;

import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.reportsheet.ReportLocationManager;
import org.hisp.dhis.system.util.StreamUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Tran Thanh Tri
 * @author Dang Duy Hieu
 * @version $Id
 */
public class ValidateUploadExcelFile
    implements Action
{
    // -------------------------------------------
    // Dependencies
    // -------------------------------------------

    private ReportLocationManager reportLocationManager;

    public void setReportLocationManager( ReportLocationManager reportLocationManager )
    {
        this.reportLocationManager = reportLocationManager;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private File upload;// The actual file

    private String uploadContentType; // The content type of the file

    private String uploadFileName; // The uploaded file name

    private String message;

    private boolean isDraft;

    private I18n i18n;

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    public void setUpload( File upload )
    {
        this.upload = upload;
    }

    public void setUploadContentType( String uploadContentType )
    {
        this.uploadContentType = uploadContentType;
    }

    public String getMessage()
    {
        return message;
    }

    public void setDraft( boolean isDraft )
    {
        this.isDraft = isDraft;
    }

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    public void setUploadFileName( String uploadFileName )
    {
        this.uploadFileName = uploadFileName;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        if ( upload == null || !upload.exists() )
        {
            message = i18n.getString( "upload_file_null" );

            return ERROR;
        }

        if ( !reportLocationManager.isFileTypeSupported( getExtension( uploadFileName ), uploadContentType ) )
        {
            message = i18n.getString( "file_type_not_supported" );

            return ERROR;
        }

        if ( isFormatBroken( uploadFileName, upload ) )
        {
            message = i18n.getString( "file_format_structure_broken" );

            return ERROR;
        }

        // Use for importing file

        if ( isDraft )
        {
            File tempImportDirectory = reportLocationManager.getExportReportTemporaryDirectory();

            if ( !tempImportDirectory.canRead() || !tempImportDirectory.canWrite() )
            {
                message = i18n.getString( "access_denied_to_folder" );

                return ERROR;
            }

            return SUCCESS;
        }

        // Use for excel template file management

        File templateDirectoryConfig = reportLocationManager.getExportReportTemplateDirectory();

        if ( !templateDirectoryConfig.canRead() || !templateDirectoryConfig.canWrite() )
        {
            message = i18n.getString( "access_denied_to_folder" );

            return ERROR;
        }

        File output = new File( templateDirectoryConfig, uploadFileName );

        if ( output != null && output.exists() )
        {
            message = i18n.getString( "override_confirm" );

            return INPUT;
        }

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive method
    // -------------------------------------------------------------------------

    private boolean isFormatBroken( String fileName, File file )
    {
        if ( getExtension( fileName ).equals( "xlsx" ) )
        {
            try
            {
                File output = new File( file.getParent(), (Math.random() * 1000) + fileName );

                StreamUtils.write( file, output );

                new XSSFWorkbook( new FileInputStream( output ) );

                return false;
            }
            catch ( Exception e )
            {
                return true;
            }
        }

        return false;
    }

}
