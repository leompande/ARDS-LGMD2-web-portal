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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.aggregation.AggregatedDataValueService;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.datadictionary.DataDictionary;
import org.hisp.dhis.datadictionary.DataDictionaryService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.importexport.GroupMemberAssociation;
import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportDataValue;
import org.hisp.dhis.importexport.ImportDataValueService;
import org.hisp.dhis.importexport.ImportObject;
import org.hisp.dhis.importexport.ImportObjectManager;
import org.hisp.dhis.importexport.ImportObjectStatus;
import org.hisp.dhis.importexport.ImportObjectStore;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.ImportStrategy;
import org.hisp.dhis.importexport.ImportType;
import org.hisp.dhis.importexport.Importer;
import org.hisp.dhis.importexport.importer.ChartImporter;
import org.hisp.dhis.importexport.importer.CompleteDataSetRegistrationImporter;
import org.hisp.dhis.importexport.importer.DataDictionaryImporter;
import org.hisp.dhis.importexport.importer.DataElementCategoryComboImporter;
import org.hisp.dhis.importexport.importer.DataElementCategoryImporter;
import org.hisp.dhis.importexport.importer.DataElementCategoryOptionImporter;
import org.hisp.dhis.importexport.importer.DataElementGroupImporter;
import org.hisp.dhis.importexport.importer.DataElementGroupSetImporter;
import org.hisp.dhis.importexport.importer.DataElementImporter;
import org.hisp.dhis.importexport.importer.DataSetImporter;
import org.hisp.dhis.importexport.importer.DataValueImporter;
import org.hisp.dhis.importexport.importer.GroupSetImporter;
import org.hisp.dhis.importexport.importer.IndicatorGroupImporter;
import org.hisp.dhis.importexport.importer.IndicatorGroupSetImporter;
import org.hisp.dhis.importexport.importer.IndicatorImporter;
import org.hisp.dhis.importexport.importer.IndicatorTypeImporter;
import org.hisp.dhis.importexport.importer.OrganisationUnitGroupImporter;
import org.hisp.dhis.importexport.importer.OrganisationUnitImporter;
import org.hisp.dhis.importexport.importer.OrganisationUnitLevelImporter;
import org.hisp.dhis.importexport.importer.PeriodImporter;
import org.hisp.dhis.importexport.importer.ReportImporter;
import org.hisp.dhis.importexport.importer.ReportTableImporter;
import org.hisp.dhis.importexport.importer.ValidationRuleImporter;
import org.hisp.dhis.importexport.mapping.GroupMemberAssociationVerifier;
import org.hisp.dhis.importexport.mapping.NameMappingUtil;
import org.hisp.dhis.importexport.mapping.ObjectMappingGenerator;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.jdbc.batchhandler.CategoryCategoryOptionAssociationBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.CategoryComboCategoryAssociationBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.CompleteDataSetRegistrationBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataDictionaryBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataDictionaryDataElementBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataDictionaryIndicatorBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementCategoryBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementCategoryComboBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementCategoryOptionBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementGroupBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementGroupMemberBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementGroupSetBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementGroupSetMemberBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataSetBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataSetMemberBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataSetSourceAssociationBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataValueBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.GroupSetBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.GroupSetMemberBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.IndicatorBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.IndicatorGroupBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.IndicatorGroupMemberBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.IndicatorGroupSetBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.IndicatorGroupSetMemberBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.IndicatorTypeBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.OrganisationUnitBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.OrganisationUnitGroupBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.OrganisationUnitGroupMemberBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.PeriodBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.ReportTableBatchHandler;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.report.Report;
import org.hisp.dhis.report.ReportService;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.reporttable.ReportTableService;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DefaultImportObjectManager
    implements ImportObjectManager
{
    private static final Log log = LogFactory.getLog( DefaultImportObjectManager.class );

    private final ImportParams params = new ImportParams( ImportType.IMPORT, ImportStrategy.NEW_AND_UPDATES, true );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private BatchHandlerFactory batchHandlerFactory;

    public void setBatchHandlerFactory( BatchHandlerFactory batchHandlerFactory )
    {
        this.batchHandlerFactory = batchHandlerFactory;
    }

    private ObjectMappingGenerator objectMappingGenerator;

    public void setObjectMappingGenerator( ObjectMappingGenerator objectMappingGenerator )
    {
        this.objectMappingGenerator = objectMappingGenerator;
    }

    private ImportObjectStore importObjectStore;

    public void setImportObjectStore( ImportObjectStore importObjectStore )
    {
        this.importObjectStore = importObjectStore;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataDictionaryService dataDictionaryService;

    public void setDataDictionaryService( DataDictionaryService dataDictionaryService )
    {
        this.dataDictionaryService = dataDictionaryService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
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

    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }

    private ImportDataValueService importDataValueService;

    public void setImportDataValueService( ImportDataValueService importDataValueService )
    {
        this.importDataValueService = importDataValueService;
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

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private AggregatedDataValueService aggregatedDataValueService;

    public void setAggregatedDataValueService( AggregatedDataValueService aggregatedDataValueService )
    {
        this.aggregatedDataValueService = aggregatedDataValueService;
    }

    // -------------------------------------------------------------------------
    // ImportObjectManager implementation
    // -------------------------------------------------------------------------

    @Transactional
    public void importCategoryOptions()
    {
        BatchHandler<DataElementCategoryOption> batchHandler = batchHandlerFactory.createBatchHandler(
            DataElementCategoryOptionBatchHandler.class ).init();

        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( DataElementCategoryOption.class );

        Importer<DataElementCategoryOption> importer = new DataElementCategoryOptionImporter( batchHandler,
            categoryService );

        for ( ImportObject importObject : importObjects )
        {
            importer.importObject( (DataElementCategoryOption) importObject.getObject(), params );
        }

        batchHandler.flush();

        importObjectStore.deleteImportObjects( DataElementCategoryOption.class );

        log.info( "Imported DataElementCategoryOptions" );
    }

    @Transactional
    public void importCategories()
    {
        BatchHandler<DataElementCategory> batchHandler = batchHandlerFactory.createBatchHandler(
            DataElementCategoryBatchHandler.class ).init();

        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( DataElementCategory.class );

        Importer<DataElementCategory> importer = new DataElementCategoryImporter( batchHandler, categoryService );

        for ( ImportObject importObject : importObjects )
        {
            importer.importObject( (DataElementCategory) importObject.getObject(), params );
        }

        batchHandler.flush();

        importObjectStore.deleteImportObjects( DataElementCategory.class );

        log.info( "Imported DataElementCategories" );
    }

    @Transactional
    public void importCategoryCombos()
    {
        BatchHandler<DataElementCategoryCombo> batchHandler = batchHandlerFactory.createBatchHandler(
            DataElementCategoryComboBatchHandler.class ).init();

        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( DataElementCategoryCombo.class );

        Importer<DataElementCategoryCombo> importer = new DataElementCategoryComboImporter( batchHandler,
            categoryService );

        for ( ImportObject importObject : importObjects )
        {
            importer.importObject( (DataElementCategoryCombo) importObject.getObject(), params );
        }

        batchHandler.flush();

        importObjectStore.deleteImportObjects( DataElementCategoryCombo.class );

        log.info( "Imported DataElementCategoryCombos" );
    }

    @Transactional
    public void importCategoryOptionCombos() // TODO reuse importer
    {
        Collection<ImportObject> importObjects = importObjectStore
            .getImportObjects( DataElementCategoryOptionCombo.class );

        Map<Object, Integer> categoryComboMapping = objectMappingGenerator.getCategoryComboMapping( false );
        Map<Object, Integer> categoryOptionMapping = objectMappingGenerator.getCategoryOptionMapping( false );

        for ( ImportObject importObject : importObjects )
        {
            DataElementCategoryOptionCombo object = (DataElementCategoryOptionCombo) importObject.getObject();

            int categoryOptionComboId = object.getId();

            if ( importObject.getStatus() == ImportObjectStatus.UPDATE )
            {
                DataElementCategoryOptionCombo compareObject = (DataElementCategoryOptionCombo) importObject
                    .getCompareObject();

                object.setId( compareObject.getId() );
            }

            int categoryComboId = categoryComboMapping.get( object.getCategoryCombo().getId() );

            object.setCategoryCombo( categoryService.getDataElementCategoryCombo( categoryComboId ) );

            List<DataElementCategoryOption> categoryOptions = new ArrayList<DataElementCategoryOption>();

            for ( DataElementCategoryOption categoryOption : object.getCategoryOptions() )
            {
                int categoryOptionId = categoryOptionMapping.get( categoryOption.getId() );

                categoryOptions.add( categoryService.getDataElementCategoryOption( categoryOptionId ) );
            }

            object.setCategoryOptions( categoryOptions );

            NameMappingUtil.addCategoryOptionComboMapping( categoryOptionComboId, object );

            if ( importObject.getStatus() == ImportObjectStatus.NEW )
            {
                categoryService.addDataElementCategoryOptionCombo( object );
            }
            else if ( importObject.getStatus() == ImportObjectStatus.UPDATE )
            {
                categoryService.updateDataElementCategoryOptionCombo( object );
            }
        }

        importObjectStore.deleteImportObjects( DataElementCategoryOptionCombo.class );

        log.info( "Imported DataElementCategoryOptionCombos" );
    }

    @Transactional
    public void importCategoryCategoryOptionAssociations()
    {
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory
            .createBatchHandler( CategoryCategoryOptionAssociationBatchHandler.class );

        importGroupMemberAssociation( batchHandler, GroupMemberType.CATEGORY_CATEGORYOPTION, objectMappingGenerator
            .getCategoryMapping( false ), objectMappingGenerator.getCategoryOptionMapping( false ) );

        log.info( "Imported CategoryCategoryOption associations" );
    }

    @Transactional
    public void importCategoryComboCategoryAssociations()
    {
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory
            .createBatchHandler( CategoryComboCategoryAssociationBatchHandler.class );

        importGroupMemberAssociation( batchHandler, GroupMemberType.CATEGORYCOMBO_CATEGORY, objectMappingGenerator
            .getCategoryComboMapping( false ), objectMappingGenerator.getCategoryMapping( false ) );

        log.info( "Imported CategoryComboCategory associations" );
    }

    @Transactional
    public void importDataElements()
    {
        BatchHandler<DataElement> batchHandler = batchHandlerFactory.createBatchHandler( DataElementBatchHandler.class )
            .init();

        Map<Object, Integer> categoryComboMapping = objectMappingGenerator.getCategoryComboMapping( false );

        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( DataElement.class );

        Importer<DataElement> importer = new DataElementImporter( batchHandler, dataElementService );

        for ( ImportObject importObject : importObjects )
        {
            DataElement object = (DataElement) importObject.getObject();
            object.getCategoryCombo().setId( categoryComboMapping.get( object.getCategoryCombo().getId() ) );
            importer.importObject( object, params );
        }

        batchHandler.flush();

        importObjectStore.deleteImportObjects( DataElement.class );

        log.info( "Imported DataElements" );
    }

    @Transactional
    public void importDataElementGroups()
    {
        BatchHandler<DataElementGroup> batchHandler = batchHandlerFactory.createBatchHandler(
            DataElementGroupBatchHandler.class ).init();

        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( DataElementGroup.class );

        Importer<DataElementGroup> importer = new DataElementGroupImporter( batchHandler, dataElementService );

        for ( ImportObject importObject : importObjects )
        {
            importer.importObject( (DataElementGroup) importObject.getObject(), params );
        }

        batchHandler.flush();

        importObjectStore.deleteImportObjects( DataElementGroup.class );

        log.info( "Imported DataElementGroups" );
    }

    @Transactional
    public void importDataElementGroupMembers()
    {
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory
            .createBatchHandler( DataElementGroupMemberBatchHandler.class );

        importGroupMemberAssociation( batchHandler, GroupMemberType.DATAELEMENTGROUP, objectMappingGenerator
            .getDataElementGroupMapping( false ), objectMappingGenerator.getDataElementMapping( false ) );

        log.info( "Imported DataElementGroup members" );
    }

    @Transactional
    public void importDataElementGroupSets()
    {
        BatchHandler<DataElementGroupSet> batchHandler = batchHandlerFactory.createBatchHandler(
            DataElementGroupSetBatchHandler.class ).init();

        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( DataElementGroupSet.class );

        Importer<DataElementGroupSet> importer = new DataElementGroupSetImporter( batchHandler, dataElementService );

        for ( ImportObject importObject : importObjects )
        {
            importer.importObject( (DataElementGroupSet) importObject.getObject(), params );
        }

        batchHandler.flush();

        importObjectStore.deleteImportObjects( DataElementGroupSet.class );

        log.info( "Imported DataElementGroupSets" );
    }

    @Transactional
    public void importDataElementGroupSetMembers()
    {
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory
            .createBatchHandler( DataElementGroupSetMemberBatchHandler.class );

        importGroupMemberAssociation( batchHandler, GroupMemberType.DATAELEMENTGROUPSET, objectMappingGenerator
            .getDataElementGroupSetMapping( false ), objectMappingGenerator.getDataElementGroupMapping( false ) );

        log.info( "Imported DataElementGroupSet members" );
    }

    @Transactional
    public void importIndicatorTypes()
    {
        BatchHandler<IndicatorType> batchHandler = batchHandlerFactory.createBatchHandler(
            IndicatorTypeBatchHandler.class ).init();

        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( IndicatorType.class );

        Importer<IndicatorType> importer = new IndicatorTypeImporter( batchHandler, indicatorService );

        for ( ImportObject importObject : importObjects )
        {
            importer.importObject( (IndicatorType) importObject.getObject(), params );
        }

        batchHandler.flush();

        importObjectStore.deleteImportObjects( IndicatorType.class );

        log.info( "Imported IndicatorTypes" );
    }

    @Transactional
    public void importIndicators()
    {
        BatchHandler<Indicator> batchHandler = batchHandlerFactory.createBatchHandler( IndicatorBatchHandler.class ).init();

        Map<Object, Integer> indicatorTypeMapping = objectMappingGenerator.getIndicatorTypeMapping( false );
        Map<Object, Integer> dataElementMapping = objectMappingGenerator.getDataElementMapping( false );
        Map<Object, Integer> categoryOptionComboMapping = objectMappingGenerator.getCategoryOptionComboMapping( false );

        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( Indicator.class );

        Importer<Indicator> importer = new IndicatorImporter( batchHandler, indicatorService );

        for ( ImportObject importObject : importObjects )
        {
            Indicator object = (Indicator) importObject.getObject();
            object.getIndicatorType().setId( indicatorTypeMapping.get( object.getIndicatorType().getId() ) );
            object.setNumerator( expressionService.convertExpression( object.getNumerator(), dataElementMapping,
                categoryOptionComboMapping ) );
            object.setDenominator( expressionService.convertExpression( object.getDenominator(), dataElementMapping,
                categoryOptionComboMapping ) );
            importer.importObject( object, params );
        }

        batchHandler.flush();

        importObjectStore.deleteImportObjects( Indicator.class );

        log.info( "Imported Indicators" );
    }

    @Transactional
    public void importIndicatorGroups()
    {
        BatchHandler<IndicatorGroup> batchHandler = batchHandlerFactory.createBatchHandler(
            IndicatorGroupBatchHandler.class ).init();

        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( IndicatorGroup.class );

        Importer<IndicatorGroup> importer = new IndicatorGroupImporter( batchHandler, indicatorService );

        for ( ImportObject importObject : importObjects )
        {
            importer.importObject( (IndicatorGroup) importObject.getObject(), params );
        }

        batchHandler.flush();

        importObjectStore.deleteImportObjects( IndicatorGroup.class );

        log.info( "Imported IndicatorGroups" );
    }

    @Transactional
    public void importIndicatorGroupMembers()
    {
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory
            .createBatchHandler( IndicatorGroupMemberBatchHandler.class );

        importGroupMemberAssociation( batchHandler, GroupMemberType.INDICATORGROUP, objectMappingGenerator
            .getIndicatorGroupMapping( false ), objectMappingGenerator.getIndicatorMapping( false ) );

        log.info( "Imported IndicatorGroup members" );
    }

    @Transactional
    public void importIndicatorGroupSets()
    {
        BatchHandler<IndicatorGroupSet> batchHandler = batchHandlerFactory.createBatchHandler(
            IndicatorGroupSetBatchHandler.class ).init();

        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( IndicatorGroupSet.class );

        Importer<IndicatorGroupSet> importer = new IndicatorGroupSetImporter( batchHandler, indicatorService );

        for ( ImportObject importObject : importObjects )
        {
            importer.importObject( (IndicatorGroupSet) importObject.getObject(), params );
        }

        batchHandler.flush();

        importObjectStore.deleteImportObjects( IndicatorGroupSet.class );

        log.info( "Imported IndicatorGroupSets" );
    }

    @Transactional
    public void importIndicatorGroupSetMembers()
    {
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory
            .createBatchHandler( IndicatorGroupSetMemberBatchHandler.class );

        importGroupMemberAssociation( batchHandler, GroupMemberType.INDICATORGROUPSET, objectMappingGenerator
            .getIndicatorGroupSetMapping( false ), objectMappingGenerator.getIndicatorGroupMapping( false ) );

        log.info( "Imported IndicatorGroupSet members" );
    }

    @Transactional
    public void importDataDictionaries()
    {
        BatchHandler<DataDictionary> batchHandler = batchHandlerFactory.createBatchHandler(
            DataDictionaryBatchHandler.class ).init();

        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( DataDictionary.class );

        Importer<DataDictionary> importer = new DataDictionaryImporter( batchHandler, dataDictionaryService );

        for ( ImportObject importObject : importObjects )
        {
            importer.importObject( (DataDictionary) importObject.getObject(), params );
        }

        batchHandler.flush();

        importObjectStore.deleteImportObjects( DataDictionary.class );

        log.info( "Imported DataDictionaries" );
    }

    @Transactional
    public void importDataDictionaryDataElements()
    {
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory
            .createBatchHandler( DataDictionaryDataElementBatchHandler.class );

        importGroupMemberAssociation( batchHandler, GroupMemberType.DATADICTIONARY_DATAELEMENT, objectMappingGenerator
            .getDataDictionaryMapping( false ), objectMappingGenerator.getDataElementMapping( false ) );

        log.info( "Imported DataDictionary DataElements" );
    }

    @Transactional
    public void importDataDictionaryIndicators()
    {
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory
            .createBatchHandler( DataDictionaryIndicatorBatchHandler.class );

        importGroupMemberAssociation( batchHandler, GroupMemberType.DATADICTIONARY_INDICATOR, objectMappingGenerator
            .getDataDictionaryMapping( false ), objectMappingGenerator.getIndicatorMapping( false ) );

        log.info( "Imported DataDictionary Indicators" );
    }

    @Transactional
    public void importDataSets()
    {
        BatchHandler<DataSet> batchHandler = batchHandlerFactory.createBatchHandler( DataSetBatchHandler.class ).init();

        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( DataSet.class );

        Importer<DataSet> importer = new DataSetImporter( batchHandler, dataSetService );

        for ( ImportObject importObject : importObjects )
        {
            importer.importObject( (DataSet) importObject.getObject(), params );
        }

        batchHandler.flush();

        importObjectStore.deleteImportObjects( DataSet.class );

        log.info( "Imported DataSets" );
    }

    @Transactional
    public void importDataSetMembers()
    {
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory
            .createBatchHandler( DataSetMemberBatchHandler.class );

        importGroupMemberAssociation( batchHandler, GroupMemberType.DATASET, objectMappingGenerator
            .getDataSetMapping( false ), objectMappingGenerator.getDataElementMapping( false ) );

        log.info( "Imported DataSet members" );
    }

    @Transactional
    public void importOrganisationUnits()
    {
        BatchHandler<OrganisationUnit> organisationUnitBatchHandler = batchHandlerFactory.createBatchHandler(
            OrganisationUnitBatchHandler.class ).init();

        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( OrganisationUnit.class );

        Importer<OrganisationUnit> importer = new OrganisationUnitImporter( organisationUnitBatchHandler, organisationUnitService );

        for ( ImportObject importObject : importObjects )
        {
            importer.importObject( (OrganisationUnit) importObject.getObject(), params );
        }

        organisationUnitBatchHandler.flush();

        importObjectStore.deleteImportObjects( OrganisationUnit.class );

        log.info( "Imported OrganisationUnits" );
    }

    @Transactional
    public void importOrganisationUnitRelationships()
    {
        Map<Object, Integer> organisationUnitMapping = objectMappingGenerator.getOrganisationUnitMapping( false );

        BatchHandler<OrganisationUnit> batchHandler = batchHandlerFactory.createBatchHandler(
            OrganisationUnitBatchHandler.class ).init();

        Collection<ImportObject> importObjects = importObjectStore
            .getImportObjects( GroupMemberType.ORGANISATIONUNITRELATIONSHIP );

        for ( ImportObject importObject : importObjects )
        {
            GroupMemberAssociation object = (GroupMemberAssociation) importObject.getObject();

            OrganisationUnit child = organisationUnitService.getOrganisationUnit( organisationUnitMapping.get( object
                .getMemberId() ) );
            OrganisationUnit parent = organisationUnitService.getOrganisationUnit( organisationUnitMapping.get( object
                .getGroupId() ) );
            child.setParent( parent );

            batchHandler.updateObject( child );
        }

        batchHandler.flush();

        importObjectStore.deleteImportObjects( GroupMemberType.ORGANISATIONUNITRELATIONSHIP );

        log.info( "Imported OrganisationUnit relationships" );
    }

    @Transactional
    public void importOrganisationUnitGroups()
    {
        BatchHandler<OrganisationUnitGroup> batchHandler = batchHandlerFactory.createBatchHandler(
            OrganisationUnitGroupBatchHandler.class ).init();

        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( OrganisationUnitGroup.class );

        Importer<OrganisationUnitGroup> importer = new OrganisationUnitGroupImporter( batchHandler,
            organisationUnitGroupService );

        for ( ImportObject importObject : importObjects )
        {
            importer.importObject( (OrganisationUnitGroup) importObject.getObject(), params );
        }

        batchHandler.flush();

        importObjectStore.deleteImportObjects( OrganisationUnitGroup.class );

        log.info( "Imported OrganisationUnitGroups" );
    }

    @Transactional
    public void importOrganisationUnitGroupMembers()
    {
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory
            .createBatchHandler( OrganisationUnitGroupMemberBatchHandler.class );

        importGroupMemberAssociation( batchHandler, GroupMemberType.ORGANISATIONUNITGROUP, objectMappingGenerator
            .getOrganisationUnitGroupMapping( false ), objectMappingGenerator.getOrganisationUnitMapping( false ) );

        log.info( "Imported OrganissationUnitGroup members" );
    }

    @Transactional
    public void importOrganisationUnitGroupSets()
    {
        BatchHandler<OrganisationUnitGroupSet> batchHandler = batchHandlerFactory.createBatchHandler(
            GroupSetBatchHandler.class ).init();

        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( OrganisationUnitGroupSet.class );

        Importer<OrganisationUnitGroupSet> importer = new GroupSetImporter( batchHandler, organisationUnitGroupService );

        for ( ImportObject importObject : importObjects )
        {
            importer.importObject( (OrganisationUnitGroupSet) importObject.getObject(), params );
        }

        batchHandler.flush();

        importObjectStore.deleteImportObjects( OrganisationUnitGroupSet.class );

        log.info( "Imported OrganisationUnitGroupSets" );
    }

    @Transactional
    public void importOrganisationUnitGroupSetMembers()
    {
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory
            .createBatchHandler( GroupSetMemberBatchHandler.class );

        importGroupMemberAssociation( batchHandler, GroupMemberType.ORGANISATIONUNITGROUPSET, objectMappingGenerator
            .getOrganisationUnitGroupSetMapping( false ), objectMappingGenerator
            .getOrganisationUnitGroupMapping( false ) );

        log.info( "Imported OrganisationUnitGroupSet members" );
    }

    @Transactional
    public void importOrganisationUnitLevels()
    {
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( OrganisationUnitLevel.class );

        Importer<OrganisationUnitLevel> importer = new OrganisationUnitLevelImporter( organisationUnitService );

        for ( ImportObject importObject : importObjects )
        {
            importer.importObject( (OrganisationUnitLevel) importObject.getObject(), params );
        }

        importObjectStore.deleteImportObjects( OrganisationUnitLevel.class );

        log.info( "Imported OrganisationUnitLevels" );
    }

    @Transactional
    public void importDataSetSourceAssociations()
    {
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory
            .createBatchHandler( DataSetSourceAssociationBatchHandler.class );

        importGroupMemberAssociation( batchHandler, GroupMemberType.DATASET_SOURCE, objectMappingGenerator
            .getDataSetMapping( false ), objectMappingGenerator.getOrganisationUnitMapping( false ) );

        log.info( "Imported DataSet Source associations" );
    }

    @Transactional
    public void importValidationRules()
    {
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( ValidationRule.class );

        Importer<ValidationRule> importer = new ValidationRuleImporter( validationRuleService, expressionService );

        for ( ImportObject importObject : importObjects )
        {
            importer.importObject( (ValidationRule) importObject.getObject(), params );
        }

        importObjectStore.deleteImportObjects( ValidationRule.class );

        log.info( "Imported ValidationRules" );
    }

    @Transactional
    public void importPeriods()
    {
        BatchHandler<Period> batchHandler = batchHandlerFactory.createBatchHandler( PeriodBatchHandler.class ).init();

        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( Period.class );

        Importer<Period> importer = new PeriodImporter( batchHandler, periodService );

        for ( ImportObject importObject : importObjects )
        {
            importer.importObject( (Period) importObject.getObject(), params );
        }

        batchHandler.flush();

        importObjectStore.deleteImportObjects( Period.class );

        log.info( "Imported Periods" );
    }

    @Transactional
    public void importReports()
    {
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( Report.class );

        Importer<Report> importer = new ReportImporter( reportService );

        for ( ImportObject importObject : importObjects )
        {
            importer.importObject( (Report) importObject.getObject(), params );
        }

        importObjectStore.deleteImportObjects( Report.class );

        log.info( "Imported Reports" );
    }

    @Transactional
    public void importReportTables()
    {
        BatchHandler<ReportTable> batchHandler = batchHandlerFactory.createBatchHandler( ReportTableBatchHandler.class )
            .init();

        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( ReportTable.class );

        Importer<ReportTable> importer = new ReportTableImporter( reportTableService );

        for ( ImportObject importObject : importObjects )
        {
            importer.importObject( (ReportTable) importObject.getObject(), params );
        }

        batchHandler.flush();

        importObjectStore.deleteImportObjects( ReportTable.class );

        log.info( "Imported ReportTables" );
    }

    @Transactional
    public void importCharts()
    {
        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( Chart.class );

        Importer<Chart> importer = new ChartImporter( chartService );

        for ( ImportObject importObject : importObjects )
        {
            importer.importObject( (Chart) importObject.getObject(), params );
        }

        importObjectStore.deleteImportObjects( Report.class );

        log.info( "Imported Reports" );
    }
    
    @Transactional
    public void importCompleteDataSetRegistrations()
    {
        BatchHandler<CompleteDataSetRegistration> batchHandler = batchHandlerFactory.createBatchHandler(
            CompleteDataSetRegistrationBatchHandler.class ).init();

        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( CompleteDataSetRegistration.class );

        Map<Object, Integer> dataSetMapping = objectMappingGenerator.getDataSetMapping( false );
        Map<Object, Integer> periodMapping = objectMappingGenerator.getPeriodMapping( false );
        Map<Object, Integer> sourceMapping = objectMappingGenerator.getOrganisationUnitMapping( false );

        Importer<CompleteDataSetRegistration> importer = new CompleteDataSetRegistrationImporter( batchHandler, params );

        for ( ImportObject importObject : importObjects )
        {
            CompleteDataSetRegistration registration = (CompleteDataSetRegistration) importObject.getObject();

            registration.getDataSet().setId( dataSetMapping.get( registration.getDataSet().getId() ) );
            registration.getPeriod().setId( periodMapping.get( registration.getPeriod().getId() ) );
            registration.getSource().setId( sourceMapping.get( registration.getSource().getId() ) );

            importer.importObject( registration, params );
        }

        batchHandler.flush();

        importObjectStore.deleteImportObjects( CompleteDataSetRegistration.class );

        log.info( "Imported CompleteDataSetRegistrations" );
    }

    @Transactional
    public void importDataValues()
    {
        Integer importedObjects = 0;
        Integer failedObjects = 0;

        BatchHandler<DataValue> batchHandler = batchHandlerFactory.createBatchHandler( DataValueBatchHandler.class )
            .init();

        Map<Object, Integer> dataElementMapping = objectMappingGenerator.getDataElementMapping( false );
        Map<Object, Integer> periodMapping = objectMappingGenerator.getPeriodMapping( false );
        Map<Object, Integer> sourceMapping = objectMappingGenerator.getOrganisationUnitMapping( false );
        Map<Object, Integer> categoryOptionComboMapping = objectMappingGenerator.getCategoryOptionComboMapping( false );

        Collection<ImportDataValue> importValues = importDataValueService.getImportDataValues( ImportObjectStatus.NEW );

        Importer<DataValue> importer = new DataValueImporter( batchHandler, aggregatedDataValueService, params );

        for ( ImportDataValue importValue : importValues )
        {
            DataValue value = importValue.getDataValue();
            try
            {
                value.getDataElement().setId( dataElementMapping.get( value.getDataElement().getId() ) );
                value.getPeriod().setId( periodMapping.get( value.getPeriod().getId() ) );
                value.getSource().setId( sourceMapping.get( value.getSource().getId() ) );
                value.getOptionCombo().setId( categoryOptionComboMapping.get( value.getOptionCombo().getId() ) );
                importer.importObject( value, params );
                importedObjects++;

            } catch ( Exception e )
            {
                importedObjects--;
                failedObjects++;
                log.error( "Object import failed" + e );
            }
        }

        batchHandler.flush();

        importDataValueService.deleteImportDataValues();

        log.info( importReport( importedObjects,failedObjects ) );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void importGroupMemberAssociation( BatchHandler<GroupMemberAssociation> batchHandler, GroupMemberType type,
        Map<Object, Integer> groupMapping, Map<Object, Integer> memberMapping )
    {
        GroupMemberAssociationVerifier.clear();

        batchHandler.init();

        Collection<ImportObject> importObjects = importObjectStore.getImportObjects( type );

        for ( ImportObject importObject : importObjects )
        {
            GroupMemberAssociation object = (GroupMemberAssociation) importObject.getObject();

            object.setGroupId( groupMapping.get( object.getGroupId() ) );
            object.setMemberId( memberMapping.get( object.getMemberId() ) );

            if ( GroupMemberAssociationVerifier.isUnique( object, type ) && !batchHandler.objectExists( object ) )
            {
                batchHandler.addObject( object );
            }
        }

        batchHandler.flush();

        importObjectStore.deleteImportObjects( type );
    }

    private String importReport(Integer importedObjects, Integer failedObjects)
    {
        Integer totalObjects = importedObjects + failedObjects;
        String importReportString = "";
        if (failedObjects > 0 )
        {
            importReportString = totalObjects.toString() + " values handled.\n" + importedObjects.toString() + " new values successfully imported.\n"
                + failedObjects.toString() + " were not imported due to errors.";
             return importReportString;
        }
        else
        {
            importReportString = importedObjects.toString() + " values were imported.";
                return importReportString;
        }

    }
}
