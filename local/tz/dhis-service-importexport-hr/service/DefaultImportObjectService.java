package org.hisp.dhis.importexport.service;

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

import static org.hisp.dhis.expression.Expression.SEPARATOR;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hisp.dhis.cache.HibernateCacheManager;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.common.ImportableObject;
import org.hisp.dhis.datadictionary.DataDictionary;
import org.hisp.dhis.datadictionary.DataDictionaryService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.importexport.GroupMemberAssociation;
import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportDataValueService;
import org.hisp.dhis.importexport.ImportObject;
import org.hisp.dhis.importexport.ImportObjectManager;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportObjectStatus;
import org.hisp.dhis.importexport.ImportObjectStore;
import org.hisp.dhis.importexport.mapping.NameMappingUtil;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.report.Report;
import org.hisp.dhis.report.ReportService;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.reporttable.ReportTableService;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 * @version $Id: DefaultImportObjectService.java 5946 2008-10-16 15:46:43Z
 *          larshelg $
 */
public class DefaultImportObjectService<T>
    implements ImportObjectService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ImportObjectStore importObjectStore;

    public void setImportObjectStore( ImportObjectStore importObjectStore )
    {
        this.importObjectStore = importObjectStore;
    }

    private ImportDataValueService importDataValueService;

    public void setImportDataValueService( ImportDataValueService importDataValueService )
    {
        this.importDataValueService = importDataValueService;
    }

    private ImportObjectManager importObjectManager;

    public void setImportObjectManager( ImportObjectManager importObjectManager )
    {
        this.importObjectManager = importObjectManager;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private DataDictionaryService dataDictionaryService;

    public void setDataDictionaryService( DataDictionaryService dataDictionaryService )
    {
        this.dataDictionaryService = dataDictionaryService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    private ValidationRuleService validationRuleService;

    public void setValidationRuleService( ValidationRuleService validationRuleService )
    {
        this.validationRuleService = validationRuleService;
    }

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    private ReportTableService reportTableService;

    public void setReportTableService( ReportTableService reportTableService )
    {
        this.reportTableService = reportTableService;
    }

    private ChartService chartService;

    public void setChartService( ChartService chartService )
    {
        this.chartService = chartService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private HibernateCacheManager cacheManager;

    public void setCacheManager( HibernateCacheManager cacheManager )
    {
        this.cacheManager = cacheManager;
    }

    // -------------------------------------------------------------------------
    // ImportObjectService implementation
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // ImportObject operations
    // -------------------------------------------------------------------------

    @Transactional
    public int addImportObject( ImportObjectStatus status, GroupMemberType groupMemberType, ImportableObject object )
    {
        ImportObject importObject = new ImportObject( status, object.getClass().getName(), groupMemberType, object );

        return importObjectStore.addImportObject( importObject );
    }

    @Transactional
    public int addImportObject( ImportObjectStatus status, ImportableObject object, ImportableObject compareObject )
    {
        ImportObject importObject = new ImportObject( status, object.getClass().getName(), object, compareObject );

        return importObjectStore.addImportObject( importObject );
    }

    @Transactional
    public int addImportObject( ImportObjectStatus status, GroupMemberType groupMemberType, ImportableObject object,
        ImportableObject compareObject )
    {
        ImportObject importObject = new ImportObject( status, object.getClass().getName(), groupMemberType, object,
            compareObject );

        return importObjectStore.addImportObject( importObject );
    }

    @Transactional
    public ImportObject getImportObject( int id )
    {
        return importObjectStore.getImportObject( id );
    }

    @Transactional
    public Collection<ImportObject> getImportObjects( Class<?> clazz )
    {
        return importObjectStore.getImportObjects( clazz );
    }

    @Transactional
    public Collection<ImportObject> getImportObjects( ImportObjectStatus status, Class<?> clazz )
    {
        return importObjectStore.getImportObjects( status, clazz );
    }

    @Transactional
    public Collection<ImportObject> getImportObjects( GroupMemberType groupMemberType )
    {
        return importObjectStore.getImportObjects( groupMemberType );
    }

    @Transactional
    public void deleteImportObject( int importObjectId )
    {
        ImportObject importObject = importObjectStore.getImportObject( importObjectId );

        if ( importObject != null )
        {
            importObjectStore.deleteImportObject( importObject );
        }
    }

    @Transactional
    public void deleteImportObjects( Class<?> clazz )
    {
        importObjectStore.deleteImportObjects( clazz );
    }

    @Transactional
    public void deleteImportObjects()
    {
        importObjectStore.deleteImportObjects();
        importDataValueService.deleteImportDataValues();
    }

    // TODO Refactor: this code is not extensible and is error-prone in terms of
    // cascading deletion of associated objects

    @Transactional
    public void cascadeDeleteImportObject( int importObjectId )
    {
        ImportObject importObject = importObjectStore.getImportObject( importObjectId );

        if ( importObject != null )
        {
            if ( importObject.getClassName().equals( DataElement.class.getName() ) )
            {
                DataElement element = (DataElement) importObject.getObject();

                deleteMemberAssociations( GroupMemberType.DATAELEMENTGROUP, element.getId() );
                deleteMemberAssociations( GroupMemberType.DATASET, element.getId() );
                deleteMemberAssociations( GroupMemberType.DATADICTIONARY_DATAELEMENT, element.getId() );

                deleteIndicatorsContainingDataElement( element.getId() );

                importDataValueService.deleteImportDataValuesByDataElement( element.getId() );
            }
            else if ( importObject.getClassName().equals( DataElementGroup.class.getName() ) )
            {
                DataElementGroup group = (DataElementGroup) importObject.getObject();

                deleteGroupAssociations( GroupMemberType.DATAELEMENTGROUP, group.getId() );
                deleteMemberAssociations( GroupMemberType.DATAELEMENTGROUPSET, group.getId() );
            }
            else if ( importObject.getClassName().equals( DataElementGroupSet.class.getName() ) )
            {
                DataElementGroupSet groupSet = (DataElementGroupSet) importObject.getObject();

                deleteGroupAssociations( GroupMemberType.DATAELEMENTGROUPSET, groupSet.getId() );
            }
            else if ( importObject.getClassName().equals( IndicatorType.class.getName() ) )
            {
                IndicatorType type = (IndicatorType) importObject.getObject();

                deleteIndicatorsWithIndicatorType( type.getId() );
            }
            else if ( importObject.getClassName().equals( Indicator.class.getName() ) )
            {
                Indicator indicator = (Indicator) importObject.getObject();

                deleteMemberAssociations( GroupMemberType.INDICATORGROUP, indicator.getId() );
                deleteMemberAssociations( GroupMemberType.DATADICTIONARY_INDICATOR, indicator.getId() );
            }
            else if ( importObject.getClassName().equals( IndicatorGroup.class.getName() ) )
            {
                IndicatorGroup group = (IndicatorGroup) importObject.getObject();

                deleteGroupAssociations( GroupMemberType.INDICATORGROUP, group.getId() );
                deleteMemberAssociations( GroupMemberType.INDICATORGROUPSET, group.getId() );
            }
            else if ( importObject.getClassName().equals( IndicatorGroupSet.class.getName() ) )
            {
                IndicatorGroupSet groupSet = (IndicatorGroupSet) importObject.getObject();

                deleteGroupAssociations( GroupMemberType.INDICATORGROUPSET, groupSet.getId() );
            }
            else if ( importObject.getClassName().equals( DataDictionary.class.getName() ) )
            {
                DataDictionary dictionary = (DataDictionary) importObject.getObject();

                deleteGroupAssociations( GroupMemberType.DATADICTIONARY_DATAELEMENT, dictionary.getId() );
                deleteGroupAssociations( GroupMemberType.DATADICTIONARY_INDICATOR, dictionary.getId() );
            }
            else if ( importObject.getClassName().equals( DataSet.class.getName() ) )
            {
                DataSet dataSet = (DataSet) importObject.getObject();

                deleteGroupAssociations( GroupMemberType.DATASET, dataSet.getId() );
                deleteMemberAssociations( GroupMemberType.DATASET_SOURCE, dataSet.getId() );
                
                deleteCompleteDataSetRegistrationsByDataSet( dataSet.getId() );
            }
            else if ( importObject.getClassName().equals( OrganisationUnit.class.getName() ) )
            {
                OrganisationUnit unit = (OrganisationUnit) importObject.getObject();

                deleteMemberAssociations( GroupMemberType.ORGANISATIONUNITGROUP, unit.getId() );
                deleteGroupAssociations( GroupMemberType.ORGANISATIONUNITRELATIONSHIP, unit.getId() );
                deleteMemberAssociations( GroupMemberType.DATASET_SOURCE, unit.getId() );
                deleteMemberAssociations( GroupMemberType.ORGANISATIONUNITRELATIONSHIP, unit.getId() );

                deleteCompleteDataSetRegistrationsBySource( unit.getId() );

                importDataValueService.deleteImportDataValuesBySource( unit.getId() );
            }
            else if ( importObject.getClassName().equals( OrganisationUnitGroup.class.getName() ) )
            {
                OrganisationUnitGroup group = (OrganisationUnitGroup) importObject.getObject();

                deleteGroupAssociations( GroupMemberType.ORGANISATIONUNITGROUP, group.getId() );
                deleteMemberAssociations( GroupMemberType.ORGANISATIONUNITGROUPSET, group.getId() );
            }
            else if ( importObject.getClassName().equals( OrganisationUnitGroupSet.class.getName() ) )
            {
                OrganisationUnitGroupSet groupSet = (OrganisationUnitGroupSet) importObject.getObject();

                deleteGroupAssociations( GroupMemberType.ORGANISATIONUNITGROUPSET, groupSet.getId() );
            }
        }

        deleteImportObject( importObjectId );
    }

    @Transactional
    public void cascadeDeleteImportObjects( Class<?> clazz )
    {
        importObjectStore.deleteImportObjects( clazz );

        if ( clazz.equals( DataElement.class ) )
        {
            importObjectStore.deleteImportObjects( DataElementCategoryOptionCombo.class );
            importObjectStore.deleteImportObjects( DataElementCategoryCombo.class );
            importObjectStore.deleteImportObjects( DataElementCategory.class );
            importObjectStore.deleteImportObjects( DataElementCategoryOption.class );
            importObjectStore.deleteImportObjects( GroupMemberType.CATEGORY_CATEGORYOPTION );
            importObjectStore.deleteImportObjects( GroupMemberType.CATEGORYCOMBO_CATEGORY );

            importObjectStore.deleteImportObjects( GroupMemberType.DATAELEMENTGROUP );

            importObjectStore.deleteImportObjects( DataSet.class );
            importObjectStore.deleteImportObjects( GroupMemberType.DATASET );
            importObjectStore.deleteImportObjects( GroupMemberType.DATASET_SOURCE );
            importObjectStore.deleteImportObjects( CompleteDataSetRegistration.class );

            importObjectStore.deleteImportObjects( Indicator.class );
            importObjectStore.deleteImportObjects( GroupMemberType.INDICATORGROUP );
            importObjectStore.deleteImportObjects( GroupMemberType.DATADICTIONARY_INDICATOR );

            importObjectStore.deleteImportObjects( GroupMemberType.DATADICTIONARY_DATAELEMENT );

            importDataValueService.deleteImportDataValues();
        }
        else if ( clazz.equals( DataElementGroup.class ) )
        {
            importObjectStore.deleteImportObjects( GroupMemberType.DATAELEMENTGROUP );
            importObjectStore.deleteImportObjects( GroupMemberType.DATAELEMENTGROUPSET );
        }
        else if ( clazz.equals( DataElementGroupSet.class ) )
        {
            importObjectStore.deleteImportObjects( GroupMemberType.DATAELEMENTGROUPSET );
        }
        else if ( clazz.equals( IndicatorType.class ) )
        {
            importObjectStore.deleteImportObjects( Indicator.class );
            importObjectStore.deleteImportObjects( GroupMemberType.INDICATORGROUP );
            importObjectStore.deleteImportObjects( GroupMemberType.DATADICTIONARY_INDICATOR );
        }
        else if ( clazz.equals( Indicator.class ) )
        {
            importObjectStore.deleteImportObjects( GroupMemberType.INDICATORGROUP );
            importObjectStore.deleteImportObjects( GroupMemberType.DATADICTIONARY_INDICATOR );
        }
        else if ( clazz.equals( IndicatorGroup.class ) )
        {
            importObjectStore.deleteImportObjects( GroupMemberType.INDICATORGROUP );
            importObjectStore.deleteImportObjects( GroupMemberType.INDICATORGROUPSET );
        }
        else if ( clazz.equals( IndicatorGroupSet.class ) )
        {
            importObjectStore.deleteImportObjects( GroupMemberType.INDICATORGROUPSET );
        }
        else if ( clazz.equals( DataDictionary.class ) )
        {
            importObjectStore.deleteImportObjects( GroupMemberType.DATADICTIONARY_DATAELEMENT );
            importObjectStore.deleteImportObjects( GroupMemberType.DATADICTIONARY_INDICATOR );
        }
        else if ( clazz.equals( DataSet.class ) )
        {
            importObjectStore.deleteImportObjects( GroupMemberType.DATASET );
            importObjectStore.deleteImportObjects( GroupMemberType.DATASET_SOURCE );
            importObjectStore.deleteImportObjects( CompleteDataSetRegistration.class );
        }
        else if ( clazz.equals( OrganisationUnit.class ) )
        {
            importObjectStore.deleteImportObjects( GroupMemberType.ORGANISATIONUNITGROUP );
            importObjectStore.deleteImportObjects( GroupMemberType.ORGANISATIONUNITRELATIONSHIP );
            importObjectStore.deleteImportObjects( GroupMemberType.DATASET_SOURCE );

            importObjectStore.deleteImportObjects( CompleteDataSetRegistration.class );

            importDataValueService.deleteImportDataValues();
        }
        else if ( clazz.equals( OrganisationUnitGroup.class ) )
        {
            importObjectStore.deleteImportObjects( GroupMemberType.ORGANISATIONUNITGROUP );
        }
        else if ( clazz.equals( OrganisationUnitGroupSet.class ) )
        {
            importObjectStore.deleteImportObjects( GroupMemberType.ORGANISATIONUNITGROUPSET );
        }
    }

    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------

    @Transactional
    public void matchObject( int importObjectId, int existingObjectId )
    {
        ImportObject importObject = importObjectStore.getImportObject( importObjectId );

        Object object = importObject.getObject();

        // ---------------------------------------------------------------------
        // Updates the name of the import object to the name of the existing
        // object.
        // ---------------------------------------------------------------------

        if ( object.getClass().equals( DataElement.class ) )
        {
            DataElement element = (DataElement) object;

            element.setName( dataElementService.getDataElement( existingObjectId ).getName() );
        }
        else if ( object.getClass().equals( DataElementGroup.class ) )
        {
            DataElementGroup group = (DataElementGroup) object;

            group.setName( dataElementService.getDataElementGroup( existingObjectId ).getName() );
        }
        else if ( object.getClass().equals( DataElementGroupSet.class ) )
        {
            DataElementGroupSet groupSet = (DataElementGroupSet) object;

            groupSet.setName( dataElementService.getDataElementGroupSet( existingObjectId ).getName() );
        }
        else if ( object.getClass().equals( IndicatorType.class ) )
        {
            IndicatorType type = (IndicatorType) object;

            type.setName( indicatorService.getIndicatorType( existingObjectId ).getName() );
        }
        else if ( object.getClass().equals( Indicator.class ) )
        {
            Indicator indicator = (Indicator) object;

            indicator.setName( indicatorService.getIndicator( existingObjectId ).getName() );
        }
        else if ( object.getClass().equals( IndicatorGroup.class ) )
        {
            IndicatorGroup group = (IndicatorGroup) object;

            group.setName( indicatorService.getIndicatorGroup( existingObjectId ).getName() );
        }
        else if ( object.getClass().equals( IndicatorGroupSet.class ) )
        {
            IndicatorGroupSet groupSet = (IndicatorGroupSet) object;

            groupSet.setName( indicatorService.getIndicatorGroupSet( existingObjectId ).getName() );
        }
        else if ( object.getClass().equals( DataDictionary.class ) )
        {
            DataDictionary dictionary = (DataDictionary) object;

            dictionary.setName( dataDictionaryService.getDataDictionary( existingObjectId ).getName() );
        }
        else if ( object.getClass().equals( DataSet.class ) )
        {
            DataSet dataSet = (DataSet) object;

            dataSet.setName( dataSetService.getDataSet( existingObjectId ).getName() );
        }
        else if ( object.getClass().equals( OrganisationUnit.class ) )
        {
            OrganisationUnit unit = (OrganisationUnit) object;

            unit.setName( organisationUnitService.getOrganisationUnit( existingObjectId ).getName() );
        }
        else if ( object.getClass().equals( OrganisationUnitGroup.class ) )
        {
            OrganisationUnitGroup group = (OrganisationUnitGroup) object;

            group.setName( organisationUnitGroupService.getOrganisationUnitGroup( existingObjectId ).getName() );
        }
        else if ( object.getClass().equals( OrganisationUnitGroupSet.class ) )
        {
            OrganisationUnitGroupSet groupSet = (OrganisationUnitGroupSet) object;

            groupSet.setName( organisationUnitGroupService.getOrganisationUnitGroupSet( existingObjectId ).getName() );
        }
        else if ( object.getClass().equals( OrganisationUnitLevel.class ) )
        {
            OrganisationUnitLevel level = (OrganisationUnitLevel) object;

            level.setName( organisationUnitService.getOrganisationUnitLevel( existingObjectId ).getName() );
        }
        else if ( object.getClass().equals( ValidationRule.class ) )
        {
            ValidationRule validationRule = (ValidationRule) object;

            validationRule.setName( validationRuleService.getValidationRule( existingObjectId ).getName() );
        }
        else if ( object.getClass().equals( Report.class ) )
        {
            Report report = (Report) object;

            report.setName( reportService.getReport( existingObjectId ).getName() );
        }
        else if ( object.getClass().equals( ReportTable.class ) )
        {
            ReportTable reportTable = (ReportTable) object;

            reportTable.setName( reportTableService.getReportTable( existingObjectId ).getName() );
        }
        else if ( object.getClass().equals( Chart.class ) )
        {
            Chart chart = (Chart) object;

            chart.setName( chartService.getChart( existingObjectId ).getName() );
        }
        else if ( object.getClass().equals( DataValue.class ) )
        {
            DataValue dataValue = (DataValue) object;

            dataValue = updateDataValue( dataValue, dataValueService.getDataValue( dataValue.getSource(), dataValue
                .getDataElement(), dataValue.getPeriod(), dataValue.getOptionCombo() ) );
        }
        
        // ---------------------------------------------------------------------
        // Sets the status of the import object to match, these objects will
        // later be ignored on import all but is needed for matching of
        // associations.
        // ---------------------------------------------------------------------

        importObject.setStatus( ImportObjectStatus.MATCH );

        importObjectStore.updateImportObject( importObject );
    }

    // -------------------------------------------------------------------------
    // Import
    // -------------------------------------------------------------------------

    public void importAll()
    {
        importObjectManager.importCategoryOptions();
        importObjectManager.importCategories();
        importObjectManager.importCategoryCombos();
        importObjectManager.importCategoryOptionCombos();
        importObjectManager.importCategoryCategoryOptionAssociations();
        importObjectManager.importCategoryComboCategoryAssociations();
        importObjectManager.importDataElements();
        importObjectManager.importDataElementGroups();
        importObjectManager.importDataElementGroupMembers();
        importObjectManager.importDataElementGroupSets();
        importObjectManager.importDataElementGroupSetMembers();
        importObjectManager.importIndicatorTypes();
        importObjectManager.importIndicators();
        importObjectManager.importIndicatorGroups();
        importObjectManager.importIndicatorGroupMembers();
        importObjectManager.importIndicatorGroupSets();
        importObjectManager.importIndicatorGroupSetMembers();
        importObjectManager.importDataDictionaries();
        importObjectManager.importDataDictionaryDataElements();
        importObjectManager.importDataDictionaryIndicators();
        importObjectManager.importDataSets();
        importObjectManager.importDataSetMembers();
        importObjectManager.importOrganisationUnits();
        importObjectManager.importOrganisationUnitRelationships();
        importObjectManager.importOrganisationUnitGroups();
        importObjectManager.importOrganisationUnitGroupMembers();
        importObjectManager.importOrganisationUnitGroupSets();
        importObjectManager.importOrganisationUnitGroupSetMembers();
        importObjectManager.importOrganisationUnitLevels();
        importObjectManager.importDataSetSourceAssociations();
        importObjectManager.importValidationRules();
        importObjectManager.importPeriods();
        importObjectManager.importReports();
        importObjectManager.importReportTables();
        importObjectManager.importCharts();
        importObjectManager.importCompleteDataSetRegistrations();
        importObjectManager.importDataValues();

        NameMappingUtil.clearMapping();

        cacheManager.clearCache();
    }

    // -------------------------------------------------------------------------
    // Import - general supportive methods
    // -------------------------------------------------------------------------

    private boolean containsIdentifier( String formula, int identifier )
    {
        if ( formula != null )
        {
            Pattern pattern = Pattern.compile( "(\\[\\d+\\" + SEPARATOR + "\\d+\\])" );
            Matcher matcher = pattern.matcher( formula );

            while ( matcher.find() )
            {
                String match = matcher.group();

                match = match.replaceAll( "[\\[\\]]", "" );

                String matchId = match.substring( 0, match.indexOf( SEPARATOR ) );

                if ( matchId.equals( String.valueOf( identifier ) ) )
                {
                    return true;
                }
            }
        }

        return false;
    }

    // -------------------------------------------------------------------------
    // Update - supportive methods
    // -------------------------------------------------------------------------

    private DataValue updateDataValue( DataValue original, DataValue update )
    {
        original.setDataElement( update.getDataElement() );
        original.setPeriod( update.getPeriod() );
        original.setSource( update.getSource() );
        original.setValue( update.getValue() );
        original.setStoredBy( update.getStoredBy() );
        original.setTimestamp( update.getTimestamp() );
        original.setComment( update.getComment() );
        original.setOptionCombo( update.getOptionCombo() );

        return original;
    }

    // -------------------------------------------------------------------------
    // Cascade delete - supportive methods
    // -------------------------------------------------------------------------

    private void deleteMemberAssociations( GroupMemberType groupMemberType, int memberId )
    {
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( groupMemberType );

        for ( ImportObject importObject : importObjects )
        {
            GroupMemberAssociation association = (GroupMemberAssociation) importObject.getObject();

            if ( association.getMemberId() == memberId )
            {
                importObjectStore.deleteImportObject( importObject );
            }
        }
    }

    private void deleteGroupAssociations( GroupMemberType groupMemberType, int groupId )
    {
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( groupMemberType );

        for ( ImportObject importObject : importObjects )
        {
            GroupMemberAssociation association = (GroupMemberAssociation) importObject.getObject();

            if ( association.getGroupId() == groupId )
            {
                importObjectStore.deleteImportObject( importObject );
            }
        }
    }

    private void deleteIndicatorsContainingDataElement( int dataElementId )
    {
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( Indicator.class );

        for ( ImportObject importObject : importObjects )
        {
            Indicator indicator = (Indicator) importObject.getObject();

            if ( containsIdentifier( indicator.getNumerator(), dataElementId )
                || containsIdentifier( indicator.getDenominator(), dataElementId ) )
            {
                importObjectStore.deleteImportObject( importObject );

                deleteMemberAssociations( GroupMemberType.INDICATORGROUP, indicator.getId() );

                deleteMemberAssociations( GroupMemberType.DATADICTIONARY_INDICATOR, indicator.getId() );
            }
        }
    }

    private void deleteIndicatorsWithIndicatorType( int indicatorTypeId )
    {
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( Indicator.class );

        for ( ImportObject importObject : importObjects )
        {
            Indicator indicator = (Indicator) importObject.getObject();

            if ( indicator.getIndicatorType().getId() == indicatorTypeId )
            {
                importObjectStore.deleteImportObject( importObject );

                deleteMemberAssociations( GroupMemberType.INDICATORGROUP, indicator.getId() );

                deleteMemberAssociations( GroupMemberType.DATADICTIONARY_INDICATOR, indicator.getId() );
            }
        }
    }

    private void deleteCompleteDataSetRegistrationsByDataSet( int dataSetId )
    {
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( CompleteDataSetRegistration.class );

        for ( ImportObject importObject : importObjects )
        {
            CompleteDataSetRegistration registration = (CompleteDataSetRegistration) importObject.getObject();

            if ( registration.getDataSet().getId() == dataSetId )
            {
                importObjectStore.deleteImportObject( importObject );
            }
        }
    }

    private void deleteCompleteDataSetRegistrationsBySource( int sourceId )
    {
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( CompleteDataSetRegistration.class );

        for ( ImportObject importObject : importObjects )
        {
            CompleteDataSetRegistration registration = (CompleteDataSetRegistration) importObject.getObject();

            if ( registration.getSource().getId() == sourceId )
            {
                importObjectStore.deleteImportObject( importObject );
            }
        }
    }
}
