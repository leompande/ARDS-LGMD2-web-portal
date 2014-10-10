package org.hisp.dhis.reportsheet.exportreport.action;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.reportsheet.ExportReportService;
import org.hisp.dhis.reportsheet.ExportReport;
import org.hisp.dhis.reportsheet.ReportLocationManager;
import org.hisp.dhis.reportsheet.comparator.ExportReportNameComparator;
import org.hisp.dhis.reportsheet.utils.ExcelFileFilter;
import org.hisp.dhis.reportsheet.utils.FileUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class ListExportReportAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private ExportReportService exportReportService;

    public void setExportReportService( ExportReportService exportReportService )
    {
        this.exportReportService = exportReportService;
    }

    private ReportLocationManager reportLocationManager;

    public void setReportLocationManager( ReportLocationManager reportLocationManager )
    {
        this.reportLocationManager = reportLocationManager;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private boolean valid;

    public void setValid( boolean valid )
    {
        this.valid = valid;
    }

    private List<ExportReport> exportReports;

    public List<ExportReport> getExportReports()
    {
        return exportReports;
    }

    private Map<String, Boolean> templateMap = new HashMap<String, Boolean>();

    public Map<String, Boolean> getTemplateMap()
    {
        return templateMap;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        valid = true;

        exportReports = new ArrayList<ExportReport>( exportReportService.getAllExportReport() );

        if ( valid )
        {
            File templateDirectory = reportLocationManager.getExportReportTemplateDirectory();

            if ( templateDirectory == null || !templateDirectory.exists() )
            {
                return SUCCESS;
            }

            List<File> templateFiles = FileUtils.getListFile( templateDirectory, new ExcelFileFilter() );

            if ( templateFiles == null || templateFiles.isEmpty() )
            {
                return SUCCESS;
            }

            for ( ExportReport exportReport : exportReports )
            {
                templateMap.put( exportReport.getExcelTemplateFile(), templateFiles.contains( new File(
                    templateDirectory, exportReport.getExcelTemplateFile() ) ) );
            }
        }

        Collections.sort( exportReports, new ExportReportNameComparator() );

        return SUCCESS;
    }

}
