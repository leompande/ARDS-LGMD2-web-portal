package org.hisp.dhis.reportsheet.degroup.action;

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

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.reportsheet.DataElementGroupOrder;
import org.hisp.dhis.reportsheet.DataElementGroupOrderService;
import org.hisp.dhis.reportsheet.ExportReport;
import org.hisp.dhis.reportsheet.ExportReportCategory;
import org.hisp.dhis.reportsheet.ExportReportService;
import org.hisp.dhis.reportsheet.action.ActionSupport;
import org.hisp.dhis.reportsheet.importitem.ImportReport;
import org.hisp.dhis.reportsheet.importitem.ImportReportService;

/**
 * @author Tran Thanh Tri
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class UpdateSortedDataElementGroupOrderAction
    extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private DataElementGroupOrderService dataElementGroupOrderService;

    public void setDataElementGroupOrderService( DataElementGroupOrderService dataElementGroupOrderService )
    {
        this.dataElementGroupOrderService = dataElementGroupOrderService;
    }

    private ExportReportService exportReportService;

    public void setExportReportService( ExportReportService exportReportService )
    {
        this.exportReportService = exportReportService;
    }

    private ImportReportService importReportService;

    public void setImportReportService( ImportReportService importReportService )
    {
        this.importReportService = importReportService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer reportId;

    public Integer getReportId()
    {
        return reportId;
    }

    public void setReportId( Integer reportId )
    {
        this.reportId = reportId;
    }

    private String clazzName;

    public void setClazzName( String clazzName )
    {
        this.clazzName = clazzName;
    }

    private List<String> dataElementGroupOrderId = new ArrayList<String>();

    public void setDataElementGroupOrderId( List<String> dataElementGroupOrderId )
    {
        this.dataElementGroupOrderId = dataElementGroupOrderId;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        List<DataElementGroupOrder> dataElementGroupOrders = new ArrayList<DataElementGroupOrder>();

        for ( String id : this.dataElementGroupOrderId )
        {
            DataElementGroupOrder daElementGroupOrder = dataElementGroupOrderService.getDataElementGroupOrder( Integer
                .parseInt( id ) );

            dataElementGroupOrders.add( daElementGroupOrder );
        }

        if ( clazzName.equals( ExportReport.class.getSimpleName() ) )
        {
            ExportReportCategory exportReportCategory = (ExportReportCategory) exportReportService
                .getExportReport( reportId );

            exportReportCategory.setDataElementOrders( dataElementGroupOrders );

            exportReportService.updateExportReport( exportReportCategory );
        }
        else
        {
            ImportReport importReport = (ImportReport) importReportService.getImportReport( reportId );

            importReport.setDataElementOrders( dataElementGroupOrders );

            importReportService.updateImportReport( importReport );
        }

        message = i18n.getString( "update_successful" );
        
        return SUCCESS;
    }

}
