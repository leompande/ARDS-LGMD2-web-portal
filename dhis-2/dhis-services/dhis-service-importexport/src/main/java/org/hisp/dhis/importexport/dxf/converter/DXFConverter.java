package org.hisp.dhis.importexport.dxf.converter;

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

import static org.apache.commons.lang.StringUtils.defaultIfEmpty;

import javax.xml.namespace.QName;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.cache.HibernateCacheManager;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.common.ProcessState;
import org.hisp.dhis.concept.Concept;
import org.hisp.dhis.concept.ConceptService;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.datadictionary.DataDictionary;
import org.hisp.dhis.datadictionary.DataDictionaryService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.GroupMemberAssociation;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.analysis.DefaultImportAnalyser;
import org.hisp.dhis.importexport.analysis.ImportAnalyser;
import org.hisp.dhis.importexport.invoker.ConverterInvoker;
import org.hisp.dhis.importexport.mapping.NameMappingUtil;
import org.hisp.dhis.importexport.mapping.ObjectMappingGenerator;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.jdbc.batchhandler.CategoryCategoryOptionAssociationBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.CategoryComboCategoryAssociationBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.ConceptBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.ConstantBatchHandler;
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
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.report.ReportService;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.reporttable.ReportTableService;
import org.hisp.dhis.validation.ValidationRuleService;

/**
 * DXFConverter class This does NOT implement XMLConverter, because we need to
 * pass ProcessState in read() method.
 * 
 * @author bobj
 * @version created 13-Feb-2010
 */
public class DXFConverter
{
    public static final String DXFROOT = "dxf";

    public static final String ATTRIBUTE_MINOR_VERSION = "minorVersion";

    public static final String ATTRIBUTE_EXPORTED = "exported";

    public static final String NAMESPACE_10 = "http://dhis2.org/schema/dxf/1.0";

    public static final String MINOR_VERSION_10 = "1.0";

    public static final String MINOR_VERSION_11 = "1.1";

    public static final String MINOR_VERSION_12 = "1.2";

    public static final String MINOR_VERSION_13 = "1.3";

    private final Log log = LogFactory.getLog( DXFConverter.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ImportObjectService importObjectService;

    public void setImportObjectService( ImportObjectService importObjectService )
    {
        this.importObjectService = importObjectService;
    }

    private ConceptService conceptService;

    public void setConceptService( ConceptService conceptService )
    {
        this.conceptService = conceptService;
    }

    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
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

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
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

    private HibernateCacheManager cacheManager;

    public void setCacheManager( HibernateCacheManager cacheManager )
    {
        this.cacheManager = cacheManager;
    }

    private ConverterInvoker converterInvoker;

    public void setConverterInvoker( ConverterInvoker converterInvoker )
    {
        this.converterInvoker = converterInvoker;
    }

    public void write( XMLWriter writer, ExportParams params, ProcessState state )
    {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    public void read( XMLReader reader, ImportParams params, ProcessState state )
    {
        ImportAnalyser importAnalyser = new DefaultImportAnalyser( expressionService );

        NameMappingUtil.clearMapping();

        if ( params.isPreview() )
        {
            importObjectService.deleteImportObjects();
            log.info( "Deleted previewed objects" );
        }

        if ( !reader.moveToStartElement( DXFROOT, DXFROOT ) )
        {
            throw new RuntimeException( "Couldn't find dxf root element" );
        }
        QName rootName = reader.getElementQName();

        params.setNamespace( defaultIfEmpty( rootName.getNamespaceURI(), NAMESPACE_10 ) );
        String version = reader.getAttributeValue( ATTRIBUTE_MINOR_VERSION );
        params.setMinorVersion( version != null ? version : MINOR_VERSION_10 );
        log.debug( "Importing dxf1 minor version " + version );

        while ( reader.next() )
        {
            if ( reader.isStartElement( ConceptConverter.COLLECTION_NAME ) )
            {
                log.debug( "Starting Concepts import" );

                state.setMessage( "importing_concepts" );

                BatchHandler<Concept> batchHandler = batchHandlerFactory.createBatchHandler( ConceptBatchHandler.class )
                    .init();

                XMLConverter converter = new ConceptConverter( batchHandler, importObjectService, conceptService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported Concepts" );
            }
            else if ( reader.isStartElement( ConstantConverter.COLLECTION_NAME ) )
            {
                log.debug( "Starting Constants import" ) ;

                state.setMessage( "importing_constants" );

                BatchHandler<Constant> batchHandler = batchHandlerFactory.createBatchHandler(
                    ConstantBatchHandler.class ).init();

                XMLConverter converter = new ConstantConverter( batchHandler, importObjectService, constantService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported Constants" );
            }
            else if ( reader.isStartElement( DataElementCategoryOptionConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting DataElementCategoryOptions import ");

                state.setMessage( "importing_data_element_category_options" );

                BatchHandler<DataElementCategoryOption> batchHandler = batchHandlerFactory.createBatchHandler(
                    DataElementCategoryOptionBatchHandler.class ).init();

                XMLConverter converter = new DataElementCategoryOptionConverter( batchHandler, importObjectService,
                    categoryService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported DataElementCategoryOptions" );
            }
            else if ( reader.isStartElement( DataElementCategoryConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting DataElementCategories import");

                state.setMessage( "importing_data_element_categories" );

                BatchHandler<DataElementCategory> batchHandler = batchHandlerFactory.createBatchHandler(
                    DataElementCategoryBatchHandler.class ).init();

                XMLConverter converter = new DataElementCategoryConverter( batchHandler, importObjectService,
                    categoryService, conceptService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported DataElementCategories" );
            }
            else if ( reader.isStartElement( DataElementCategoryComboConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting DataElementCategoryCombos import");

                state.setMessage( "importing_data_element_category_combos" );

                BatchHandler<DataElementCategoryCombo> batchHandler = batchHandlerFactory.createBatchHandler(
                    DataElementCategoryComboBatchHandler.class ).init();

                XMLConverter converter = new DataElementCategoryComboConverter( batchHandler, importObjectService,
                    categoryService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported DataElementCategoryCombos" );
            }
            else if ( reader.isStartElement( DataElementCategoryOptionComboConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting DataElementCategoryOptionCombos import");

                state.setMessage( "importing_data_element_category_option_combos" );

                XMLConverter converter = new DataElementCategoryOptionComboConverter( importObjectService,
                    objectMappingGenerator.getCategoryComboMapping( params.skipMapping() ), objectMappingGenerator
                        .getCategoryOptionMapping( params.skipMapping() ), categoryService );

                converterInvoker.invokeRead( converter, reader, params );

                log.info( "Imported DataElementCategoryOptionCombos" );
            }
            else if ( reader.isStartElement( CategoryCategoryOptionAssociationConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting CategoryCategoryOption associations import");

                state.setMessage( "importing_data_element_category_members" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler(
                    CategoryCategoryOptionAssociationBatchHandler.class ).init();

                XMLConverter converter = new CategoryCategoryOptionAssociationConverter( batchHandler,
                    importObjectService, objectMappingGenerator.getCategoryMapping( params.skipMapping() ),
                    objectMappingGenerator.getCategoryOptionMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported CategoryCategoryOption associations" );
            }
            else if ( reader.isStartElement( CategoryComboCategoryAssociationConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting CategoryComboCategory associations import");

                state.setMessage( "importing_data_element_category_combo_members" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler(
                    CategoryComboCategoryAssociationBatchHandler.class ).init();

                XMLConverter converter = new CategoryComboCategoryAssociationConverter( batchHandler,
                    importObjectService, objectMappingGenerator.getCategoryComboMapping( params.skipMapping() ),
                    objectMappingGenerator.getCategoryMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported CategoryComboCategory associations" );
            }
            else if ( reader.isStartElement( DataElementConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting DataElements import");

                state.setMessage( "importing_data_elements" );

                BatchHandler<DataElement> batchHandler = batchHandlerFactory.createBatchHandler(
                    DataElementBatchHandler.class ).init();

                XMLConverter converter = new DataElementConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getCategoryComboMapping( params.skipMapping() ), dataElementService,
                    importAnalyser );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported DataElements" );
            }
            else if ( reader.isStartElement( DataElementGroupConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting DataElementGroups import");

                state.setMessage( "importing_data_element_groups" );

                BatchHandler<DataElementGroup> batchHandler = batchHandlerFactory.createBatchHandler(
                    DataElementGroupBatchHandler.class ).init();

                XMLConverter converter = new DataElementGroupConverter( batchHandler, importObjectService,
                    dataElementService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported DataElementGroups" );
            }
            else if ( reader.isStartElement( DataElementGroupMemberConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting DataElementGroup members import");

                state.setMessage( "importing_data_element_group_members" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler(
                    DataElementGroupMemberBatchHandler.class ).init();

                XMLConverter converter = new DataElementGroupMemberConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getDataElementMapping( params.skipMapping() ), objectMappingGenerator
                        .getDataElementGroupMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported DataElementGroup members" );
            }
            else if ( reader.isStartElement( DataElementGroupSetConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting DataElementGroupSets import");

                state.setMessage( "importing_data_element_group_sets" );

                BatchHandler<DataElementGroupSet> batchHandler = batchHandlerFactory.createBatchHandler(
                    DataElementGroupSetBatchHandler.class ).init();

                XMLConverter converter = new DataElementGroupSetConverter( batchHandler, importObjectService,
                    dataElementService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported DataElementGroupSets" );
            }
            else if ( reader.isStartElement( DataElementGroupSetMemberConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting Imported DataElementGroupSet members import");

                state.setMessage( "importing_data_element_group_set_members" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler(
                    DataElementGroupSetMemberBatchHandler.class ).init();

                XMLConverter converter = new DataElementGroupSetMemberConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getDataElementGroupMapping( params.skipMapping() ), objectMappingGenerator
                        .getDataElementGroupSetMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported DataElementGroupSet members" );
            }
            else if ( reader.isStartElement( IndicatorTypeConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting IndicatorTypes import");

                state.setMessage( "importing_indicator_types" );

                BatchHandler<IndicatorType> batchHandler = batchHandlerFactory.createBatchHandler(
                    IndicatorTypeBatchHandler.class ).init();

                XMLConverter converter = new IndicatorTypeConverter( batchHandler, importObjectService,
                    indicatorService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported IndicatorTypes" );
            }
            else if ( reader.isStartElement( IndicatorConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting Indicators import");

                state.setMessage( "importing_indicators" );

                BatchHandler<Indicator> batchHandler = batchHandlerFactory.createBatchHandler(
                    IndicatorBatchHandler.class ).init();

                XMLConverter converter = new IndicatorConverter( batchHandler, importObjectService, indicatorService,
                    objectMappingGenerator.getIndicatorTypeMapping( params.skipMapping() ),
                    importAnalyser );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported Indicators" );
            }
            else if ( reader.isStartElement( IndicatorGroupConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting IndicatorGroups import");

                state.setMessage( "importing_indicator_groups" );

                BatchHandler<IndicatorGroup> batchHandler = batchHandlerFactory.createBatchHandler(
                    IndicatorGroupBatchHandler.class ).init();

                XMLConverter converter = new IndicatorGroupConverter( batchHandler, importObjectService,
                    indicatorService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported IndicatorGroups" );
            }
            else if ( reader.isStartElement( IndicatorGroupMemberConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting IndicatorGroup members import");

                state.setMessage( "importing_indicator_group_members" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler(
                    IndicatorGroupMemberBatchHandler.class ).init();

                XMLConverter converter = new IndicatorGroupMemberConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getIndicatorMapping( params.skipMapping() ), objectMappingGenerator
                        .getIndicatorGroupMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported IndicatorGroup members" );
            }
            else if ( reader.isStartElement( IndicatorGroupSetConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting IndicatorGroupSets import");

                state.setMessage( "importing_indicator_group_sets" );

                BatchHandler<IndicatorGroupSet> batchHandler = batchHandlerFactory.createBatchHandler(
                    IndicatorGroupSetBatchHandler.class ).init();

                XMLConverter converter = new IndicatorGroupSetConverter( batchHandler, importObjectService,
                    indicatorService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported IndicatorGroupSets" );
            }
            else if ( reader.isStartElement( IndicatorGroupSetMemberConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting IndicatorGroupSet import");

                state.setMessage( "importing_indicator_group_set_members" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler(
                    IndicatorGroupSetMemberBatchHandler.class ).init();

                XMLConverter converter = new IndicatorGroupSetMemberConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getIndicatorGroupMapping( params.skipMapping() ), objectMappingGenerator
                        .getIndicatorGroupSetMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported IndicatorGroupSet members" );
            }
            else if ( reader.isStartElement( DataDictionaryConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting DataDictionaries import");

                state.setMessage( "importing_data_dictionaries" );

                BatchHandler<DataDictionary> batchHandler = batchHandlerFactory.createBatchHandler(
                    DataDictionaryBatchHandler.class ).init();

                XMLConverter converter = new DataDictionaryConverter( batchHandler, importObjectService,
                    dataDictionaryService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported DataDictionaries" );
            }
            else if ( reader.isStartElement( DataDictionaryDataElementConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting DataDictionary DataElements import");

                state.setMessage( "importing_data_dictionary_data_elements" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler(
                    DataDictionaryDataElementBatchHandler.class ).init();

                XMLConverter converter = new DataDictionaryDataElementConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getDataDictionaryMapping( params.skipMapping() ), objectMappingGenerator
                        .getDataElementMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported DataDictionary DataElements" );
            }
            else if ( reader.isStartElement( DataDictionaryIndicatorConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting DataDictionary Indicators import");

                state.setMessage( "importing_data_dictionary_indicators" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler(
                    DataDictionaryIndicatorBatchHandler.class ).init();

                XMLConverter converter = new DataDictionaryIndicatorConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getDataDictionaryMapping( params.skipMapping() ), objectMappingGenerator
                        .getIndicatorMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported DataDictionary Indicators" );
            }
            else if ( reader.isStartElement( DataSetConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting DataSets import");

                state.setMessage( "importing_data_sets" );

                BatchHandler<DataSet> batchHandler = batchHandlerFactory.createBatchHandler( DataSetBatchHandler.class )
                    .init();

                XMLConverter converter = new DataSetConverter( batchHandler, importObjectService, dataSetService,
                    objectMappingGenerator.getPeriodTypeMapping() );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported DataSets" );
            }
            else if ( reader.isStartElement( DataSetMemberConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting DataSet members import");

                state.setMessage( "importing_data_set_members" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler(
                    DataSetMemberBatchHandler.class ).init();

                XMLConverter converter = new DataSetMemberConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getDataElementMapping( params.skipMapping() ), objectMappingGenerator
                        .getDataSetMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported DataSet members" );
            }
            else if ( reader.isStartElement( OrganisationUnitConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting OrganisationUnits import");

                state.setMessage( "importing_organisation_units" );

                BatchHandler<OrganisationUnit> batchHandler = batchHandlerFactory.createBatchHandler(
                    OrganisationUnitBatchHandler.class ).init();

                XMLConverter converter = new OrganisationUnitConverter( batchHandler, importObjectService,
                    organisationUnitService, importAnalyser );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported OrganisationUnits" );
            }
            else if ( reader.isStartElement( OrganisationUnitRelationshipConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting OrganisationUnit relationships import");

                state.setMessage( "importing_organisation_unit_relationships" );

                BatchHandler<OrganisationUnit> batchHandler = batchHandlerFactory.createBatchHandler(
                    OrganisationUnitBatchHandler.class ).init();

                XMLConverter converter = new OrganisationUnitRelationshipConverter( batchHandler, importObjectService,
                    organisationUnitService, objectMappingGenerator.getOrganisationUnitMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported OrganisationUnit relationships" );
            }
            else if ( reader.isStartElement( OrganisationUnitGroupConverter.COLLECTION_NAME ) )
            {
                log.info("Starting OrganisationUnitGroups import ");

                state.setMessage( "importing_organisation_unit_groups" );

                BatchHandler<OrganisationUnitGroup> batchHandler = batchHandlerFactory.createBatchHandler(
                    OrganisationUnitGroupBatchHandler.class ).init();

                XMLConverter converter = new OrganisationUnitGroupConverter( batchHandler, importObjectService,
                    organisationUnitGroupService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported OrganisationUnitGroups" );
            }
            else if ( reader.isStartElement( OrganisationUnitGroupMemberConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting OrganisationUnitGroup members import");

                state.setMessage( "importing_organisation_unit_group_members" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler(
                    OrganisationUnitGroupMemberBatchHandler.class ).init();

                XMLConverter converter = new OrganisationUnitGroupMemberConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getOrganisationUnitMapping( params.skipMapping() ), objectMappingGenerator
                        .getOrganisationUnitGroupMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported OrganisationUnitGroup members" );
            }
            else if ( reader.isStartElement( GroupSetConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting OrganisationUnitGroupSets import");

                state.setMessage( "importing_organisation_unit_group_sets" );

                BatchHandler<OrganisationUnitGroupSet> batchHandler = batchHandlerFactory.createBatchHandler(
                    GroupSetBatchHandler.class ).init();

                XMLConverter converter = new GroupSetConverter( batchHandler, importObjectService,
                    organisationUnitGroupService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported OrganisationUnitGroupSets" );
            }
            else if ( reader.isStartElement( GroupSetMemberConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting OrganisationUnitGroupSet members import");

                state.setMessage( "importing_organisation_unit_group_set_members" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler(
                    GroupSetMemberBatchHandler.class ).init();

                XMLConverter converter = new GroupSetMemberConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getOrganisationUnitGroupMapping( params.skipMapping() ),
                    objectMappingGenerator.getOrganisationUnitGroupSetMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported OrganisationUnitGroupSet members" );
            }
            else if ( reader.isStartElement( OrganisationUnitLevelConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting OrganisationUnitLevels import");

                state.setMessage( "importing_organisation_unit_levels" );

                XMLConverter converter = new OrganisationUnitLevelConverter( organisationUnitService,
                    importObjectService );

                converterInvoker.invokeRead( converter, reader, params );

                log.info( "Imported OrganisationUnitLevels" );
            }
            else if ( reader.isStartElement( DataSetSourceAssociationConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting DataSet Source associations import");

                state.setMessage( "importing_data_set_source_associations" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler(
                    DataSetSourceAssociationBatchHandler.class ).init();

                XMLConverter converter = new DataSetSourceAssociationConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getDataSetMapping( params.skipMapping() ), objectMappingGenerator
                        .getOrganisationUnitMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported DataSet Source associations" );
            }
            else if ( reader.isStartElement( ValidationRuleConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting ValidationRules import");

                state.setMessage( "importing_validation_rules" );

                XMLConverter converter = new ValidationRuleConverter( importObjectService, validationRuleService, expressionService );

                converterInvoker.invokeRead( converter, reader, params );

                log.info( "Imported ValidationRules" );
            }
            else if ( reader.isStartElement( PeriodConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting Periods import");

                state.setMessage( "importing_periods" );

                BatchHandler<Period> batchHandler = batchHandlerFactory.createBatchHandler( PeriodBatchHandler.class )
                    .init();

                XMLConverter converter = new PeriodConverter( batchHandler, importObjectService, periodService,
                    objectMappingGenerator.getPeriodTypeMapping() );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported Periods" );
            }
            else if ( reader.isStartElement( ReportConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting Reports import");

                state.setMessage( "importing_reports" );

                XMLConverter converter = new ReportConverter( reportService, importObjectService );

                converterInvoker.invokeRead( converter, reader, params );

                log.info( "Imported Reports" );
            }
            else if ( reader.isStartElement( ReportTableConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting ReportTables import");

                state.setMessage( "importing_report_tables" );

                BatchHandler<ReportTable> batchHandler = batchHandlerFactory.createBatchHandler(
                    ReportTableBatchHandler.class ).init();

                XMLConverter converter = new ReportTableConverter( reportTableService, importObjectService,
                    dataElementService, categoryService, indicatorService, dataSetService, periodService,
                    organisationUnitService, objectMappingGenerator.getDataElementMapping( params.skipMapping() ),
                    objectMappingGenerator.getCategoryComboMapping( params.skipMapping() ), objectMappingGenerator
                        .getIndicatorMapping( params.skipMapping() ), objectMappingGenerator.getDataSetMapping( params
                        .skipMapping() ), objectMappingGenerator.getPeriodMapping( params.skipMapping() ),
                    objectMappingGenerator.getOrganisationUnitMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported ReportTables" );
            }
            else if ( reader.isStartElement( ChartConverter.COLLECTION_NAME ) )
            {
                log.debug("Starting Charts import");

                state.setMessage( "importing_charts" );

                XMLConverter converter = new ChartConverter( chartService, importObjectService, indicatorService,
                    organisationUnitService, objectMappingGenerator.getIndicatorMapping( params
                        .skipMapping() ), objectMappingGenerator.getOrganisationUnitMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                log.info( "Imported Charts" );
            }
        }

        if ( params.isAnalysis() )
        {
            state.setOutput( importAnalyser.getImportAnalysis() );
        }

        NameMappingUtil.clearMapping();

        cacheManager.clearCache();
    }

}
