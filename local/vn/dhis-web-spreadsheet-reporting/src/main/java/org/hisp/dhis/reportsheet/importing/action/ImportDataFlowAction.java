package org.hisp.dhis.reportsheet.importing.action;

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

import org.hisp.dhis.reportsheet.importitem.ImportReport;
import org.hisp.dhis.reportsheet.importitem.ImportReportService;
import org.hisp.dhis.reportsheet.period.generic.PeriodGenericManager;
import org.hisp.dhis.reportsheet.state.SelectionManager;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version $Id$
 */
public class ImportDataFlowAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private ImportReportService importReportService;

    @Autowired
    private PeriodGenericManager periodGenericManager;

    @Autowired
    private SelectionManager selectionManager;

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer importReportId;

    private Integer periodId;

    private Integer sheetId;

    private Integer orgunitGroupId;

    public String[] importItemIds;

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    public Integer getImportReportId()
    {
        return importReportId;
    }

    public void setImportReportId( Integer importReportId )
    {
        this.importReportId = importReportId;
    }

    public void setPeriodId( Integer periodId )
    {
        this.periodId = periodId;
    }

    public Integer getSheetId()
    {
        return sheetId;
    }

    public void setSheetId( Integer sheetId )
    {
        this.sheetId = sheetId;
    }

    public Integer getOrgunitGroupId()
    {
        return orgunitGroupId;
    }

    public void setOrgunitGroupId( Integer orgunitGroupId )
    {
        this.orgunitGroupId = orgunitGroupId;
    }

    public void setImportItemIds( String[] importItemIds )
    {
        this.importItemIds = importItemIds;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        periodGenericManager.setSelectedPeriodIndex( periodId );

        selectionManager.setListObject( importItemIds );

        ImportReport importReport = importReportService.getImportReport( importReportId );

        return importReport.getType();
    }

}
