package org.hisp.dhis.reportsheet.exportreport.action;

/*
 * Copyright (c) 2004-2012, University of Oslo
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
import org.hisp.dhis.reportsheet.ExportReportAttribute;
import org.hisp.dhis.reportsheet.ExportReportCategory;
import org.hisp.dhis.reportsheet.ExportReportNormal;
import org.hisp.dhis.reportsheet.ExportReportOrganizationGroupListing;
import org.hisp.dhis.reportsheet.ExportReportPeriodColumnListing;
import org.hisp.dhis.reportsheet.ExportReportService;
import org.hisp.dhis.reportsheet.ExportReportVerticalCategory;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Tran Thanh Tri
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class AddExportReportAction
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

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private String name;

    private String excel;

    private Integer periodRow;

    private Integer periodCol;

    private Integer organisationRow;

    private Integer organisationCol;

    private String exportReportType;

    private ExportReport exportReport;

    private String group;

    private Set<Integer> dataSetIds = new HashSet<Integer>();

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    public void setGroup( String group )
    {
        this.group = group;
    }

    public void setExportReportType( String exportReportType )
    {
        this.exportReportType = exportReportType;
    }

    public ExportReport getExportReport()
    {
        return exportReport;
    }

    public void setName( String name )
    {
        this.name = name;
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
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        if ( exportReportType.equalsIgnoreCase( ExportReport.TYPE.NORMAL ) )
        {
            exportReport = new ExportReportNormal();
        }

        if ( exportReportType.equalsIgnoreCase( ExportReport.TYPE.ATTRIBUTE ) )
        {
            exportReport = new ExportReportAttribute();
        }

        if ( exportReportType.equalsIgnoreCase( ExportReport.TYPE.CATEGORY ) )
        {
            exportReport = new ExportReportCategory();
        }

        if ( exportReportType.equalsIgnoreCase( ExportReport.TYPE.CATEGORY_VERTICAL ) )
        {
            exportReport = new ExportReportVerticalCategory();
        }

        if ( exportReportType.equalsIgnoreCase( ExportReport.TYPE.ORGANIZATION_GROUP_LISTING ) )
        {
            exportReport = new ExportReportOrganizationGroupListing();
        }

        if ( exportReportType.equalsIgnoreCase( ExportReport.TYPE.PERIOD_COLUMN_LISTING ) )
        {
            exportReport = new ExportReportPeriodColumnListing();
        }

        exportReport.setName( name );
        exportReport.setExcelTemplateFile( excel );
        exportReport.setGroup( group );
        exportReport.setCreatedBy( currentUserService.getCurrentUsername() );

        if ( periodCol != null && periodRow != null )
        {
            exportReport.setPeriodColumn( this.periodCol );
            exportReport.setPeriodRow( this.periodRow );
        }

        if ( organisationCol != null && organisationRow != null )
        {
            exportReport.setOrganisationColumn( this.organisationCol );
            exportReport.setOrganisationRow( this.organisationRow );
        }

        if ( dataSetIds != null && !dataSetIds.isEmpty() )
        {
            exportReport.setDataSets( new HashSet<DataSet>( dataSetService.getDataSets( dataSetIds ) ) );
        }

        exportReportService.addExportReport( exportReport );

        return SUCCESS;
    }
}
