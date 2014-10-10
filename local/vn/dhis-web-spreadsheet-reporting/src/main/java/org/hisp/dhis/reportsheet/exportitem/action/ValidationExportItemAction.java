package org.hisp.dhis.reportsheet.exportitem.action;

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

import org.hisp.dhis.reportsheet.ExportReportService;
import org.hisp.dhis.reportsheet.ExportReport;
import org.hisp.dhis.reportsheet.ExportItem;
import org.hisp.dhis.reportsheet.action.ActionSupport;

/**
 * @author Tran Thanh Tri
 * @version $Id 2010-08-27
 */
public class ValidationExportItemAction
    extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private ExportReportService exportReportService;

    public void setExportReportService( ExportReportService exportReportService )
    {
        this.exportReportService = exportReportService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private Integer exportReportId;

    public void setExportReportId( Integer exportReportId )
    {
        this.exportReportId = exportReportId;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private Integer row;

    public void setRow( Integer row )
    {
        this.row = row;
    };

    private Integer column;

    public void setColumn( Integer column )
    {
        this.column = column;
    };

    private Integer sheetNo;

    public void setSheetNo( Integer sheetNo )
    {
        this.sheetNo = sheetNo;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        if ( sheetNo == null )
        {
            message = i18n.getString( "please_enter_sheet_no" );

            return ERROR;
        }

        ExportReport exportReport = exportReportService.getExportReport( exportReportId );

        ExportItem match = exportReport.getExportItemByName( name, sheetNo );

        if ( match != null && (id == null || match.getId() != id) )
        {
            message = i18n.getString( "name_ready_exist_in_sheet" );

            return ERROR;
        }

        if ( row == null || column == null )
        {
            message = i18n.getString( "please_enter_row_and_column_first" );

            return ERROR;
        }

        match = exportReport.getExportItemBySheetRowColumn( sheetNo, row, column );

        if ( match != null && (id == null || match.getId() != id) )
        {
            message = i18n.getString( "cell_exist" );

            return ERROR;
        }

        return SUCCESS;

    }
}
