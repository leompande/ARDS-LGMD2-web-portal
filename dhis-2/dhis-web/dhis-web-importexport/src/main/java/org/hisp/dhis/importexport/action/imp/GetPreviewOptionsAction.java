package org.hisp.dhis.importexport.action.imp;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import static org.hisp.dhis.importexport.ImportObjectStatus.NEW;
import static org.hisp.dhis.importexport.ImportObjectStatus.UPDATE;
import static org.hisp.dhis.importexport.ImportObjectStatus.valueOf;
import static org.hisp.dhis.util.SessionUtils.KEY_PREVIEW_STATUS;
import static org.hisp.dhis.util.SessionUtils.KEY_PREVIEW_TYPE;
import static org.hisp.dhis.util.SessionUtils.getSessionVar;
import static org.hisp.dhis.util.SessionUtils.setSessionVar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.datadictionary.DataDictionary;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.importexport.ImportDataValueService;
import org.hisp.dhis.importexport.ImportObject;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.action.util.ClassMapUtil;
import org.hisp.dhis.importexport.comparator.ImportObjectComparator;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.report.Report;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.validation.ValidationRule;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class GetPreviewOptionsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ImportObjectService importObjectService;

    public void setImportObjectService( ImportObjectService importObjectService )
    {
        this.importObjectService = importObjectService;
    }

    private ImportDataValueService importDataValueService;

    public void setImportDataValueService( ImportDataValueService importDataValueService )
    {
        this.importDataValueService = importDataValueService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String type;

    public String getType()
    {
        return type;
    }

    public void setType( String elementType )
    {
        this.type = elementType;
    }

    private String status;

    public String getStatus()
    {
        return status;
    }

    public void setStatus( String elementStatus )
    {
        this.status = elementStatus;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
    
    private Integer newConstants;

    public Integer getNewConstants()
    {
        return newConstants;
    }

    private Integer newDataElements;

    public Integer getNewDataElements()
    {
        return newDataElements;
    }

    private Integer newCalculatedDataElements;

    public Integer getNewCalculatedDataElements()
    {
        return newCalculatedDataElements;
    }

    private Integer newExtendedDataElements;

    public Integer getNewExtendedDataElements()
    {
        return newExtendedDataElements;
    }

    private Integer newDataElementGroups;

    public Integer getNewDataElementGroups()
    {
        return newDataElementGroups;
    }

    private Integer newDataElementGroupSets;

    public Integer getNewDataElementGroupSets()
    {
        return newDataElementGroupSets;
    }

    private Integer newIndicatorTypes;

    public Integer getNewIndicatorTypes()
    {
        return newIndicatorTypes;
    }

    private Integer newIndicators;

    public Integer getNewIndicators()
    {
        return newIndicators;
    }

    private Integer newIndicatorGroups;

    public Integer getNewIndicatorGroups()
    {
        return newIndicatorGroups;
    }

    private Integer newIndicatorGroupSets;

    public Integer getNewIndicatorGroupSets()
    {
        return newIndicatorGroupSets;
    }

    private Integer newDataDictionaries;

    public Integer getNewDataDictionaries()
    {
        return newDataDictionaries;
    }

    private Integer newDataSets;

    public Integer getNewDataSets()
    {
        return newDataSets;
    }

    private Integer newOrganisationUnits;

    public Integer getNewOrganisationUnits()
    {
        return newOrganisationUnits;
    }

    private Integer newOrganisationUnitGroups;

    public Integer getNewOrganisationUnitGroups()
    {
        return newOrganisationUnitGroups;
    }

    private Integer newOrganisationUnitGroupSets;

    public Integer getNewOrganisationUnitGroupSets()
    {
        return newOrganisationUnitGroupSets;
    }

    private Integer newOrganisationUnitLevels;

    public Integer getNewOrganisationUnitLevels()
    {
        return newOrganisationUnitLevels;
    }

    private Integer newValidationRules;

    public Integer getNewValidationRules()
    {
        return newValidationRules;
    }

    private Integer newReports;

    public Integer getNewReports()
    {
        return newReports;
    }

    private Integer newReportTables;

    public Integer getNewReportTables()
    {
        return newReportTables;
    }

    private Integer newCharts;

    public Integer getNewCharts()
    {
        return newCharts;
    }

    private Integer newOlapUrls;

    public Integer getNewOlapUrls()
    {
        return newOlapUrls;
    }

    private Integer newDataValues;

    public Integer getNewDataValues()
    {
        return newDataValues;
    }

    private Integer updateConstants;

    public Integer getUpdateConstants()
    {
        return updateConstants;
    }

    private Integer updateDataElements;

    public Integer getUpdateDataElements()
    {
        return updateDataElements;
    }

    private Integer updateCalculatedDataElements;

    public Integer getUpdateCalculatedDataElements()
    {
        return updateCalculatedDataElements;
    }

    private Integer updateExtendedDataElements;

    public Integer getUpdateExtendedDataElements()
    {
        return updateExtendedDataElements;
    }

    private Integer updateDataElementGroups;

    public Integer getUpdateDataElementGroups()
    {
        return updateDataElementGroups;
    }

    private Integer updateDataElementGroupSets;

    public Integer getUpdateDataElementGroupSets()
    {
        return updateDataElementGroupSets;
    }

    private Integer updateIndicatorTypes;

    public Integer getUpdateIndicatorTypes()
    {
        return updateIndicatorTypes;
    }

    private Integer updateIndicators;

    public Integer getUpdateIndicators()
    {
        return updateIndicators;
    }

    private Integer updateIndicatorGroups;

    public Integer getUpdateIndicatorGroups()
    {
        return updateIndicatorGroups;
    }

    private Integer updateIndicatorGroupSets;

    public Integer getUpdateIndicatorGroupSets()
    {
        return updateIndicatorGroupSets;
    }

    private Integer updateDataDictionaries;

    public Integer getUpdateDataDictionaries()
    {
        return updateDataDictionaries;
    }

    private Integer updateDataSets;

    public Integer getUpdateDataSets()
    {
        return updateDataSets;
    }

    private Integer updateOrganisationUnits;

    public Integer getUpdateOrganisationUnits()
    {
        return updateOrganisationUnits;
    }

    private Integer updateOrganisationUnitGroups;

    public Integer getUpdateOrganisationUnitGroups()
    {
        return updateOrganisationUnitGroups;
    }

    private Integer updateOrganisationUnitGroupSets;

    public Integer getUpdateOrganisationUnitGroupSets()
    {
        return updateOrganisationUnitGroupSets;
    }

    private Integer updateOrganisationUnitLevels;

    public Integer getUpdateOrganisationUnitLevels()
    {
        return updateOrganisationUnitLevels;
    }

    private Integer updateValidationRules;

    public Integer getUpdateValidationRules()
    {
        return updateValidationRules;
    }

    private Integer updateReports;

    public Integer getUpdateReports()
    {
        return updateReports;
    }

    private Integer updateReportTables;

    public Integer getUpdateReportTables()
    {
        return updateReportTables;
    }

    private Integer updateCharts;

    public Integer getUpdateCharts()
    {
        return updateCharts;
    }

    private Integer updateOlapUrls;

    public Integer getUpdateOlapUrls()
    {
        return updateOlapUrls;
    }

    private Integer updateDataValues;

    public Integer getUpdateDataValues()
    {
        return updateDataValues;
    }

    public List<ImportObject> importObjects;

    public List<ImportObject> getImportObjects()
    {
        return importObjects;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        newConstants = importObjectService.getImportObjects( NEW, Constant.class ).size();
        newDataElements = importObjectService.getImportObjects( NEW, DataElement.class ).size();
        newDataElementGroups = importObjectService.getImportObjects( NEW, DataElementGroup.class ).size();
        newDataElementGroupSets = importObjectService.getImportObjects( NEW, DataElementGroupSet.class ).size();
        newIndicatorTypes = importObjectService.getImportObjects( NEW, IndicatorType.class ).size();
        newIndicators = importObjectService.getImportObjects( NEW, Indicator.class ).size();
        newIndicatorGroups = importObjectService.getImportObjects( NEW, IndicatorGroup.class ).size();
        newIndicatorGroupSets = importObjectService.getImportObjects( NEW, IndicatorGroupSet.class ).size();
        newDataDictionaries = importObjectService.getImportObjects( NEW, DataDictionary.class ).size();
        newDataSets = importObjectService.getImportObjects( NEW, DataSet.class ).size();
        newOrganisationUnits = importObjectService.getImportObjects( NEW, OrganisationUnit.class ).size();
        newOrganisationUnitGroups = importObjectService.getImportObjects( NEW, OrganisationUnitGroup.class ).size();
        newOrganisationUnitGroupSets = importObjectService.getImportObjects( NEW, OrganisationUnitGroupSet.class ).size();
        newOrganisationUnitLevels = importObjectService.getImportObjects( NEW, OrganisationUnitLevel.class ).size();
        newValidationRules = importObjectService.getImportObjects( NEW, ValidationRule.class ).size();
        newReports = importObjectService.getImportObjects( NEW, Report.class ).size();
        newReportTables = importObjectService.getImportObjects( NEW, ReportTable.class ).size();
        newCharts = importObjectService.getImportObjects( NEW, Chart.class ).size();
        newDataValues = importDataValueService.getNumberOfImportDataValues( NEW );

        updateConstants = importObjectService.getImportObjects( UPDATE, Constant.class ).size();
        updateDataElements = importObjectService.getImportObjects( UPDATE, DataElement.class ).size();
        updateDataElementGroups = importObjectService.getImportObjects( UPDATE, DataElementGroup.class ).size();
        updateDataElementGroupSets = importObjectService.getImportObjects( UPDATE, DataElementGroupSet.class ).size();
        updateIndicatorTypes = importObjectService.getImportObjects( UPDATE, IndicatorType.class ).size();
        updateIndicators = importObjectService.getImportObjects( UPDATE, Indicator.class ).size();
        updateIndicatorGroups = importObjectService.getImportObjects( UPDATE, IndicatorGroup.class ).size();
        updateIndicatorGroupSets = importObjectService.getImportObjects( UPDATE, IndicatorGroupSet.class ).size();
        updateDataDictionaries = importObjectService.getImportObjects( UPDATE, DataDictionary.class ).size();
        updateDataSets = importObjectService.getImportObjects( UPDATE, DataSet.class ).size();
        updateOrganisationUnits = importObjectService.getImportObjects( UPDATE, OrganisationUnit.class ).size();
        updateOrganisationUnitGroups = importObjectService.getImportObjects( UPDATE, OrganisationUnitGroup.class ).size();
        updateOrganisationUnitGroupSets = importObjectService.getImportObjects( UPDATE, OrganisationUnitGroupSet.class ).size();
        updateOrganisationUnitLevels = importObjectService.getImportObjects( UPDATE, OrganisationUnitLevel.class ).size();
        updateValidationRules = importObjectService.getImportObjects( UPDATE, ValidationRule.class ).size();
        updateReports = importObjectService.getImportObjects( UPDATE, Report.class ).size();
        updateReportTables = importObjectService.getImportObjects( UPDATE, ReportTable.class ).size();
        updateCharts = importObjectService.getImportObjects( UPDATE, Chart.class ).size();
        updateDataValues = importDataValueService.getNumberOfImportDataValues( UPDATE );

        // -------------------------------------------------------------------------
        // Remember last type and status value
        // -------------------------------------------------------------------------

        type = type == null ? (String) getSessionVar( KEY_PREVIEW_TYPE ) : type;
        status = status == null ? (String) getSessionVar( KEY_PREVIEW_STATUS ) : status;

        setSessionVar( KEY_PREVIEW_TYPE, type );
        setSessionVar( KEY_PREVIEW_STATUS, status );

        // -------------------------------------------------------------------------
        // Provide relevant import objects
        // -------------------------------------------------------------------------

        if ( type != null && status != null )
        {
            importObjects = new ArrayList<ImportObject>( importObjectService.getImportObjects( valueOf( status ),
                ClassMapUtil.getClass( type ) ) );

            Collections.sort( importObjects, new ImportObjectComparator() );
        }

        return SUCCESS;
    }

}
