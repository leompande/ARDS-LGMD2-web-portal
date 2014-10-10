package org.hisp.dhis.importexport.action.exp;

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

import java.io.InputStream;

import org.hisp.dhis.common.ServiceProvider;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
//import org.hisp.dhis.importexport.ExportParams;
//import org.hisp.dhis.importexport.ExportService;
//import org.hisp.dhis.importexport.ImportDataValueService;
//import org.hisp.dhis.importexport.ImportObjectService;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author John Francis Mukulu <john.f.mukulu@gmail.com>
 * @version $Id$
 */
public class MetaDataExportAction
    extends ActionSupport
{
    private static final String FILENAME = "Export_meta.zip";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

//    private ServiceProvider<ExportService> serviceProvider;
//
//    public void setServiceProvider( ServiceProvider<ExportService> serviceProvider )
//    {
//        this.serviceProvider = serviceProvider;
//    }
//
//    private ImportObjectService importObjectService;
//
//    public void setImportObjectService( ImportObjectService importObjectService )
//    {
//        this.importObjectService = importObjectService;
//    }
//
//    private ImportDataValueService importDataValueService;
//
//    public void setImportDataValueService( ImportDataValueService importDataValueService )
//    {
//        this.importDataValueService = importDataValueService;
//    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String exportFormat;

    public String getExportFormat()
    {
        return exportFormat;
    }

    public void setExportFormat( String exportFormat )
    {
        this.exportFormat = exportFormat;
    }

    private boolean dataElements;

    public void setDataElements( boolean dataElements )
    {
        this.dataElements = dataElements;
    }

    private boolean dataElementGroups;

    public void setDataElementGroups( boolean dataElementGroups )
    {
        this.dataElementGroups = dataElementGroups;
    }

    private boolean dataElementGroupSets;

    public void setDataElementGroupSets( boolean dataElementGroupSets )
    {
        this.dataElementGroupSets = dataElementGroupSets;
    }

    private boolean dataDictionaries;

    public void setDataDictionaries( boolean dataDictionaries )
    {
        this.dataDictionaries = dataDictionaries;
    }

    private boolean dataSets;

    public void setDataSets( boolean dataSets )
    {
        this.dataSets = dataSets;
    }

    private boolean indicators;

    public void setIndicators( boolean indicators )
    {
        this.indicators = indicators;
    }

    private boolean indicatorGroups;

    public void setIndicatorGroups( boolean indicatorGroups )
    {
        this.indicatorGroups = indicatorGroups;
    }

    private boolean indicatorGroupSets;

    public void setIndicatorGroupSets( boolean indicatorGroupSets )
    {
        this.indicatorGroupSets = indicatorGroupSets;
    }

    private boolean organisationUnits;

    public void setOrganisationUnits( boolean organisationUnits )
    {
        this.organisationUnits = organisationUnits;
    }

    private boolean organisationUnitGroups;

    public void setOrganisationUnitGroups( boolean organisationUnitGroups )
    {
        this.organisationUnitGroups = organisationUnitGroups;
    }

    private boolean organisationUnitGroupSets;

    public void setOrganisationUnitGroupSets( boolean organisationUnitGroupSets )
    {
        this.organisationUnitGroupSets = organisationUnitGroupSets;
    }

    private boolean organisationUnitLevels;

    public void setOrganisationUnitLevels( boolean organisationUnitLevels )
    {
        this.organisationUnitLevels = organisationUnitLevels;
    }

    private boolean validationRules;

    public void setValidationRules( boolean validationRules )
    {
        this.validationRules = validationRules;
    }

    private boolean reports;

    public void setReports( boolean reports )
    {
        this.reports = reports;
    }

    private boolean reportTables;

    public void setReportTables( boolean reportTables )
    {
        this.reportTables = reportTables;
    }

    private boolean charts;

    public void setCharts( boolean charts )
    {
        this.charts = charts;
    }

    private boolean olapUrls;

    public void setOlapUrls( boolean olapUrls )
    {
        this.olapUrls = olapUrls;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
//        importDataValueService.deleteImportDataValues();
//        importObjectService.deleteImportObjects();
//
//        ExportParams params = new ExportParams();
//
//        if ( dataElements || dataElementGroups || indicators || dataSets || validationRules || reportTables || charts )
//        {
//            params.setCategories( null );
//            params.setCategoryCombos( null );
//            params.setCategoryOptions( null );
//            params.setCategoryOptionCombos( null );
//            params.setDataElements( null );
//            params.setCalculatedDataElements( null );
//        }
//
//        if ( dataElementGroups )
//        {
//            params.setDataElementGroups( null );
//        }
//
//        if ( dataElementGroupSets )
//        {
//            params.setDataElementGroupSets( null );
//        }
//
//        if ( indicators || indicatorGroups || reportTables || charts )
//        {
//            params.setIndicators( null );
//
//            params.setIndicatorTypes( null );
//        }
//
//        if ( indicatorGroups )
//        {
//            params.setIndicatorGroups( null );
//        }
//
//        if ( indicatorGroupSets )
//        {
//            params.setIndicatorGroupSets( null );
//        }
//
//        if ( dataDictionaries )
//        {
//            params.setDataDictionaries( null );
//        }
//
//        if ( dataSets || reportTables )
//        {
//            params.setDataSets( null );
//        }
//
//        if ( organisationUnits || organisationUnitGroups || reportTables || charts )
//        {
//            params.setOrganisationUnits( null );
//        }
//
//        if ( organisationUnitGroups || organisationUnitGroupSets )
//        {
//            params.setOrganisationUnitGroups( null );
//        }
//
//        if ( organisationUnitGroupSets )
//        {
//            params.setOrganisationUnitGroupSets( null );
//        }
//
//        if ( organisationUnitLevels )
//        {
//            params.setOrganisationUnitLevels( null );
//        }
//
//        if ( validationRules )
//        {
//            params.setValidationRules( null );
//        }
//
//        if ( reports )
//        {
//            params.setReports( null );
//        }
//
//        if ( reportTables )
//        {
//            params.setReportTables( null );
//            params.setPeriods( null ); // TODO Include only relevant periods
//        }
//
//        if ( charts )
//        {
//            params.setCharts( null );
//            params.setPeriods( null );
//        }
//
//        if ( olapUrls )
//        {
//            params.setOlapUrls( null );
//        }
//
//        params.setIncludeDataValues( false );
//
//        params.setI18n( i18n );
//        params.setFormat( format );
//
//        ExportService exportService = serviceProvider.provide( exportFormat );
//
//        inputStream = exportService.exportData( params );

        fileName = FILENAME;

        return SUCCESS;
    }
}
