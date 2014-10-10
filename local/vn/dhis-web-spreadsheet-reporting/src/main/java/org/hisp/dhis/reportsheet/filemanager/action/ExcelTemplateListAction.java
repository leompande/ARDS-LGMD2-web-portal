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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.reportsheet.ExportReportService;
import org.hisp.dhis.reportsheet.ExportReport;
import org.hisp.dhis.reportsheet.ReportLocationManager;
import org.hisp.dhis.reportsheet.action.ActionSupport;
import org.hisp.dhis.reportsheet.state.SelectionManager;
import org.hisp.dhis.reportsheet.utils.ExcelFileFilter;
import org.hisp.dhis.reportsheet.utils.FileUtils;
import org.hisp.dhis.reportsheet.utils.StringUtils;
import org.hisp.dhis.system.comparator.FileNameComparator;

/**
 * @author Chau Thu Tran
 * @author Dang Duy Hieu
 * @version $Id
 * @since 2010-01-27
 */

public class ExcelTemplateListAction
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

    private ExportReportService exportReportService;

    public void setExportReportService( ExportReportService exportReportService )
    {
        this.exportReportService = exportReportService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String mode;

    private Integer id;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private ExportReport exportReport;

    private String newFileUploadedOrRenamed;

    private List<String> reportTypes = new ArrayList<String>();

    private Map<String, Boolean> mapTemplateFiles = new HashMap<String, Boolean>();

    // -------------------------------------------------------------------------
    // Getter && Setter
    // -------------------------------------------------------------------------

    public void setId( Integer id )
    {
        this.id = id;
    }

    public ExportReport getExportReport()
    {
        return exportReport;
    }

    public Map<String, Boolean> getMapTemplateFiles()
    {
        return mapTemplateFiles;
    }

    public String getNewFileUploadedOrRenamed()
    {
        return newFileUploadedOrRenamed;
    }

    public void setMode( String mode )
    {
        this.mode = mode;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    public List<String> getReportTypes()
    {
        this.reportTypes.add( ExportReport.TYPE.NORMAL );
        this.reportTypes.add( ExportReport.TYPE.ATTRIBUTE );
        this.reportTypes.add( ExportReport.TYPE.CATEGORY );
        this.reportTypes.add( ExportReport.TYPE.CATEGORY_VERTICAL );
        this.reportTypes.add( ExportReport.TYPE.PERIOD_COLUMN_LISTING );
        this.reportTypes.add( ExportReport.TYPE.ORGANIZATION_GROUP_LISTING );

        return reportTypes;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        if ( id != null )
        {
            selectionManager.setSelectedReportId( id );

            exportReport = exportReportService.getExportReport( id );
        }

        File templateDirectory = reportLocationManager.getExportReportTemplateDirectory();

        if ( templateDirectory == null || !templateDirectory.exists() )
        {
            return SUCCESS;
        }

        String newUploadOrRenamePath = null;

        // ---------------------------------------------------------------------
        // Get the path of newly uploaded file
        // ---------------------------------------------------------------------

        if ( !StringUtils.isNullOREmpty( mode ) )
        {
            if ( mode.equalsIgnoreCase( "edit" ) )
            {
                newUploadOrRenamePath = selectionManager.getRenameFilePath();
            }
            else
            {
                newUploadOrRenamePath = selectionManager.getUploadFilePath();
            }
        }

        if ( !StringUtils.isNullOREmpty( newUploadOrRenamePath ) )
        {
            newFileUploadedOrRenamed = new File( newUploadOrRenamePath ).getName();
        }

        // ---------------------------------------------------------------------
        // Get the list of files
        // ---------------------------------------------------------------------

        List<File> templateFiles = FileUtils.getListFile( templateDirectory, new ExcelFileFilter() );

        Collections.sort( templateFiles, new FileNameComparator() );

        Collection<String> exportReportTemplates = exportReportService.getAllExportReportTemplates();

        for ( File file : templateFiles )
        {
            mapTemplateFiles.put( file.getName(), exportReportTemplates.contains( file.getName() ) );
        }

        return SUCCESS;
    }
}
