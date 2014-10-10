package org.hisp.dhis.reportsheet.importitem;

import java.util.Collection;

import org.hisp.dhis.organisationunit.OrganisationUnit;

public interface ImportReportService
{
    String ID = ImportReportService.class.getName();

    // -------------------------------------------------------------------------
    // Import Report services
    // -------------------------------------------------------------------------

    public int addImportReport( ImportReport importReport );

    public void updateImportReport( ImportReport importReport );

    public void deleteImportReport( int id );

    public Collection<ImportReport> getAllImportReport();

    public ImportReport getImportReport( int id );
    
    public ImportReport getImportReport( String name );

    public Collection<ImportReport> getImportReports( OrganisationUnit organisationUnit );

    public Collection<ImportReport> getImportReportsByType( String type );
    
    // -------------------------------------------------------------------------
    // Import item services
    // -------------------------------------------------------------------------

    public int addImportItem( ImportItem importItem );

    public void updateImportItem( ImportItem importItem );

    public void deleteImportItem( int id );

    public Collection<ImportItem> getAllImportItem();

    public ImportItem getImportItem( int id );
    
    public ImportItem getImportItem( String name );
    
    public Collection<Integer> getAllSheet();
}
