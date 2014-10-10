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

package org.hisp.dhis.reportsheet.periodcolumn.action;

import org.hisp.dhis.reportsheet.ExportReportService;
import org.hisp.dhis.reportsheet.PeriodColumn;
import org.hisp.dhis.reportsheet.ExportReportPeriodColumnListing;
import org.hisp.dhis.reportsheet.action.ActionSupport;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class SavePeriodColumnAction
    extends ActionSupport
{

    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private ExportReportService exportReportService;

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer exportReportId;

    private String periodType;

    private Integer column;

    private String startdate;

    private String enddate;

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    public void setExportReportService( ExportReportService exportReportService )
    {
        this.exportReportService = exportReportService;
    }

    public Integer getExportReportId()
    {
        return exportReportId;
    }

    public void setExportReportId( Integer exportReportId )
    {
        this.exportReportId = exportReportId;
    }

    public void setPeriodType( String periodType )
    {
        this.periodType = periodType;
    }

    public void setColumn( Integer column )
    {
        this.column = column;
    }

    public void setStartdate( String startdate )
    {
        this.startdate = startdate;
    }

    public void setEnddate( String enddate )
    {
        this.enddate = enddate;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        ExportReportPeriodColumnListing exportReport = (ExportReportPeriodColumnListing) exportReportService
            .getExportReport( exportReportId );

        PeriodColumn periodColumn = new PeriodColumn();
        periodColumn.setColumn( column );
        periodColumn.setPeriodType( periodType );

        periodColumn.setStartdate( format.parseDate( startdate ) );

        periodColumn.setEnddate( format.parseDate( enddate ) );

        exportReport.addPeriodColumn( periodColumn );

        exportReportService.updateExportReport( exportReport );

        return SUCCESS;
    }

}
