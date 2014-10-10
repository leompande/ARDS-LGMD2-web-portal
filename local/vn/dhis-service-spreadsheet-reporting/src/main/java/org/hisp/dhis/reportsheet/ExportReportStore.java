package org.hisp.dhis.reportsheet;

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

import java.util.Collection;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reportsheet.status.DataEntryStatus;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public interface ExportReportStore
{
    String ID = ExportReportStore.class.getName();

    // -------------------------------------------------------------------------
    // Service of Report
    // -------------------------------------------------------------------------

    public int addExportReport( ExportReport exportReport );

    public void updateExportReport( ExportReport exportReport );

    public void deleteExportReport( int id );

    public ExportReport getExportReport( int id );

    public ExportReport getExportReport( String name );

    public ExportReport getExportReportByDataSet( DataSet dataSet );

    public Collection<ExportReport> getExportReportsByOrganisationUnit( OrganisationUnit organisationUnit );

    public Collection<ExportReport> getAllExportReport();

    public Collection<ExportReport> getExportReportsByGroup( String group );

    public Collection<ExportReport> getExportReportsByClazz( Class<?> clazz );

    public Collection<ExportReport> getExportReportsByReportType( String reportType );

    public Collection<String> getExportReportGroups();

    public Collection<String> getAllExportReportTemplates();

    // -------------------------------------------------------------------------
    // Service of Report Item
    // -------------------------------------------------------------------------

    public void addExportItem( ExportItem exportItem );

    public void updateExportItem( ExportItem exportItem );

    public void deleteExportItem( int id );

    public ExportItem getExportItem( int id );

    public Collection<ExportItem> getAllExportItem();

    public Collection<ExportItem> getExportItem( int sheetNo, Integer exportReportId );

    public Collection<Integer> getSheets( Integer exportReportId );

    public void deleteMultiExportItem( Collection<Integer> ids );

    public void updateReportWithExcelTemplate( String curTemplateName, String newTemplateName );

    // -------------------------------------------------------------------------
    // Data Entry Status
    // -------------------------------------------------------------------------

    public int saveDataEntryStatus( DataEntryStatus dataStatus );

    public void updateDataEntryStatus( DataEntryStatus dataStatus );

    public DataEntryStatus getDataEntryStatus( int id );

    public void deleteDataEntryStatus( int id );

    public Collection<DataEntryStatus> getALLDataEntryStatus();

    public Collection<DataEntryStatus> getDataEntryStatusDefault();

    Collection<DataEntryStatus> getDataEntryStatusByDataSets( Collection<DataSet> dataSets );

    Collection<DataEntryStatus> getDataEntryStatusDefaultByDataSets( Collection<DataSet> dataSets );

    public int countDataValueOfDataSet( DataSet dataSet, OrganisationUnit organisationUnit, Period period );

    // -------------------------------------------------------------------------
    // Period Column
    // -------------------------------------------------------------------------

    public PeriodColumn getPeriodColumn( Integer id );

    public void updatePeriodColumn( PeriodColumn periodColumn );

}
