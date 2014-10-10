package org.hisp.dhis.reportsheet.importitem.impl;

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

import static org.hisp.dhis.i18n.I18nUtils.i18n;

import java.util.Collection;

import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.reportsheet.importitem.ImportItem;
import org.hisp.dhis.reportsheet.importitem.ImportReport;
import org.hisp.dhis.reportsheet.importitem.ImportReportService;
import org.hisp.dhis.reportsheet.importitem.ImportReportStore;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chau Thu Tran
 * @version $Id$
 */

@Transactional
public class DefaultImportReportService
    implements ImportReportService
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private ImportReportStore importReportStore;

    public void setImportReportStore( ImportReportStore importReportStore )
    {
        this.importReportStore = importReportStore;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    // -------------------------------------------------------------------------
    // Import Report Services
    // -------------------------------------------------------------------------

    public int addImportReport( ImportReport importReport )
    {
        int id = importReportStore.addImportReport( importReport );

        return id;
    }

    public void deleteImportReport( int id )
    {
        i18nService.removeObject( importReportStore.getImportReport( id ) );

        importReportStore.deleteImportReport( id );
    }

    public Collection<ImportReport> getAllImportReport()
    {
        return i18n( i18nService, importReportStore.getAllImportReport() );
    }

    public ImportReport getImportReport( int id )
    {
        return i18n( i18nService, importReportStore.getImportReport( id ) );
    }

    public ImportReport getImportReport( String name )
    {
        return i18n( i18nService, importReportStore.getImportReport( name ) );
    }

    public void updateImportReport( ImportReport importReport )
    {
        importReportStore.updateImportReport( importReport );
    }

    public Collection<ImportReport> getImportReports( OrganisationUnit organisationUnit )
    {
        return i18n( i18nService, importReportStore.getImportReports( organisationUnit ) );
    }

    public Collection<ImportReport> getImportReportsByType( String type )
    {
        return i18n( i18nService, importReportStore.getImportReportsByType( type ) );
    }
    // -------------------------------------------------------------------------
    // Import Item Services
    // -------------------------------------------------------------------------

    public int addImportItem( ImportItem excelItem )
    {
        int id = importReportStore.addImportItem( excelItem );

        return id;
    }

    public void deleteImportItem( int id )
    {
        i18nService.removeObject( importReportStore.getImportItem( id ) );

        importReportStore.deleteImportItem( id );
    }

    public Collection<ImportItem> getAllImportItem()
    {
        return importReportStore.getAllImportItem();
    }

    public void updateImportItem( ImportItem excelItem )
    {
        importReportStore.updateImportItem( excelItem );
    }

    public ImportItem getImportItem( int id )
    {
        return importReportStore.getImportItem( id );
    }

    public ImportItem getImportItem( String name )
    {
        return importReportStore.getImportItem( name );
    }

    public Collection<Integer> getAllSheet()
    {
        return importReportStore.getSheets();
    }
}
