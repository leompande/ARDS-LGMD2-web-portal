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

import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.reportsheet.ExportReport;
import org.hisp.dhis.reportsheet.ExportReportService;

import com.opensymphony.xwork2.Action;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class UpdateExportReportAction
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

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer id;

    private String name;

    private String excel;

    private Integer periodRow;

    private Integer periodCol;

    private Integer organisationRow;

    private Integer organisationCol;

    private ExportReport exportReport;

    private String group;

    private Set<Integer> dataSetIds = new HashSet<Integer>();

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    public void setName( String name )
    {
        this.name = name;
    }

    public void setGroup( String group )
    {
        this.group = group;
    }

    public ExportReport getExportReport()
    {
        return exportReport;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public void setExcel( String excel )
    {
        this.excel = excel;
    }

    public void setPeriodRow( Integer periodRow )
    {
        this.periodRow = periodRow;
    }

    public void setPeriodCol( Integer periodCol )
    {
        this.periodCol = periodCol;
    }

    public void setOrganisationRow( Integer organisationRow )
    {
        this.organisationRow = organisationRow;
    }

    public void setOrganisationCol( Integer organisationCol )
    {
        this.organisationCol = organisationCol;
    }

    public void setDataSetIds( Set<Integer> dataSetIds )
    {
        this.dataSetIds = dataSetIds;
    }

    // -------------------------------------------------------------------------
    // Action implement
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        exportReport = exportReportService.getExportReport( id );

        exportReport.setExcelTemplateFile( excel );
        exportReport.setGroup( group );
        exportReport.setName( name );

        if ( periodCol == null || periodRow == null )
        {
            exportReport.setPeriodColumn( null );
            exportReport.setPeriodRow( null );
        }
        else
        {
            exportReport.setPeriodColumn( periodCol );
            exportReport.setPeriodRow( periodRow );
        }
        if ( organisationCol == null || organisationRow == null )
        {
            exportReport.setOrganisationColumn( null );
            exportReport.setOrganisationRow( null );
        }
        else
        {
            exportReport.setOrganisationColumn( organisationCol );
            exportReport.setOrganisationRow( organisationRow );
        }

        if ( dataSetIds != null && !dataSetIds.isEmpty() )
        {
            exportReport.updateDataSetMembers( new HashSet<DataSet>( dataSetService.getDataSets( dataSetIds ) ) );
        }

        exportReportService.updateExportReport( exportReport );

        return SUCCESS;
    }
}
