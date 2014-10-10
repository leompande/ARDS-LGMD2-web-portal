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
package org.hisp.dhis.reportsheet.degroup.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.reportsheet.DataElementGroupOrder;
import org.hisp.dhis.reportsheet.ExportReport;
import org.hisp.dhis.reportsheet.ExportReportService;
import org.hisp.dhis.reportsheet.ExportReportCategory;
import org.hisp.dhis.reportsheet.importitem.ImportReport;
import org.hisp.dhis.reportsheet.importitem.ImportReportService;

import com.opensymphony.xwork2.Action;

/**
 * @author Tran Thanh Tri
 * @author Dang Duy Hieu
 * @version $Id$
 */

public class SaveDataElementGroupOrderAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
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
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    private String name;

    private String code;

    private String clazzName;

    private List<String> dataElementIds = new ArrayList<String>();

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    public void setName( String name )
    {
        this.name = name;
    }

    public void setDataElementIds( List<String> dataElementIds )
    {
        this.dataElementIds = dataElementIds;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }

    public void setCode( String code )
    {
        this.code = code;
    }

    public void setClazzName( String clazzName )
    {
        this.clazzName = clazzName;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        DataElementGroupOrder dataElementGroupOrder = new DataElementGroupOrder();
        dataElementGroupOrder.setName( name );
        dataElementGroupOrder.setCode( code );

        List<DataElement> dataElements = new ArrayList<DataElement>();

        for ( String id : dataElementIds )
        {
            DataElement dataElement = dataElementService.getDataElement( Integer.parseInt( id ) );

            dataElements.add( dataElement );
        }

        dataElementGroupOrder.setDataElements( dataElements );

        if ( clazzName.equals( ExportReport.class.getSimpleName() ) )
        {
            ExportReportCategory exportReportCategory = (ExportReportCategory) exportReportService.getExportReport( id );

            List<DataElementGroupOrder> dataElementGroupOrders = exportReportCategory.getDataElementOrders();

            dataElementGroupOrders.add( dataElementGroupOrder );

            exportReportCategory.setDataElementOrders( dataElementGroupOrders );

            exportReportService.updateExportReport( exportReportCategory );
        }
        else
        {
            ImportReport importReportCategory = (ImportReport) importReportService.getImportReport( id );

            List<DataElementGroupOrder> dataElementGroupOrders = importReportCategory.getDataElementOrders();

            dataElementGroupOrders.add( dataElementGroupOrder );

            importReportCategory.setDataElementOrders( dataElementGroupOrders );

            importReportService.updateImportReport( importReportCategory );
        }

        return SUCCESS;
    }
}
