package org.hisp.dhis.importexport.dhis14.file.importer;

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

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.cache.HibernateCacheManager;
import org.hisp.dhis.common.ProcessState;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.importexport.GroupMemberAssociation;
import org.hisp.dhis.importexport.ImportDataValue;
import org.hisp.dhis.importexport.ImportException;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.ImportService;
import org.hisp.dhis.importexport.analysis.DefaultImportAnalyser;
import org.hisp.dhis.importexport.analysis.ImportAnalyser;
import org.hisp.dhis.importexport.dhis14.file.query.QueryManager;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.DataElementGroupMemberRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.DataElementGroupRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.DataElementRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.DataSetMemberRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.DataSetOrganisationUnitAssociationRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.DataSetRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.GroupSetMemberRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.GroupSetRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.IndicatorGroupMemberRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.IndicatorGroupRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.IndicatorRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.IndicatorTypeRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.OnChangePeriodRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.OrganisationUnitGroupMemberRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.OrganisationUnitGroupRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.OrganisationUnitRelationshipRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.OrganisationUnitRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.PeriodRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.RoutineDataValueRowHandler;
import org.hisp.dhis.importexport.dhis14.file.rowhandler.SemiPermanentDataValueRowHandler;
import org.hisp.dhis.importexport.dhis14.util.Dhis14PeriodUtil;
import org.hisp.dhis.importexport.mapping.NameMappingUtil;
import org.hisp.dhis.importexport.mapping.ObjectMappingGenerator;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.jdbc.batchhandler.DataElementBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementGroupBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementGroupMemberBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataSetBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataSetMemberBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataSetSourceAssociationBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataValueBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.GroupSetBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.GroupSetMemberBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.ImportDataValueBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.IndicatorBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.IndicatorGroupBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.IndicatorGroupMemberBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.IndicatorTypeBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.OrganisationUnitBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.OrganisationUnitGroupBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.OrganisationUnitGroupMemberBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.PeriodBatchHandler;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;

import com.ibatis.sqlmap.client.event.RowHandler;

/**
 * @author Lars Helge Overland
 * @version $Id: DefaultDhis14FileImportService.java 6425 2008-11-22 00:08:57Z larshelg $
 */
public class DefaultDhis14FileImportService
    implements ImportService
{
    private final Log log = LogFactory.getLog( DefaultDhis14FileImportService.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private QueryManager queryManager;

    public void setQueryManager( QueryManager queryManager )
    {
        this.queryManager = queryManager;
    }

    private ObjectMappingGenerator objectMappingGenerator;

    public void setObjectMappingGenerator( ObjectMappingGenerator objectMappingGenerator )
    {
        this.objectMappingGenerator = objectMappingGenerator;
    }
    
    private BatchHandlerFactory batchHandlerFactory;

    public void setBatchHandlerFactory( BatchHandlerFactory batchHandlerFactory )
    {
        this.batchHandlerFactory = batchHandlerFactory;
    }
    
    private ImportObjectService importObjectService;

    public void setImportObjectService( ImportObjectService importObjectService )
    {
        this.importObjectService = importObjectService;
    }
    
    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
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

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
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
    
    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
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

    private ImportAnalyser importAnalyser;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public DefaultDhis14FileImportService()
    {
        super();
    }
    
    // -------------------------------------------------------------------------
    // ImportService implementation
    // -------------------------------------------------------------------------

    @Override
    public void importData( ImportParams params, InputStream inputStream ) throws ImportException
    {
        importData( params, inputStream, null );
    }

    @Override
    public void importData( ImportParams params, InputStream inputStream, ProcessState state )
        throws ImportException
    {
        NameMappingUtil.clearMapping();

        importAnalyser = new DefaultImportAnalyser( expressionService );

        if ( !verifyImportFile( params, state ) )
        {
            return;
        }

        if ( params.isPreview() )
        {
            importObjectService.deleteImportObjects();
        }

        importDataElements( params, state );
        importIndicatorTypes( params, state );
        importIndicators( params, state );
        importDataElementGroups( params, state );
        importDataElementGroupMembers( params, state );
        importIndicatorGroups( params, state );
        importIndicatorGroupMembers( params, state );

        importDataSets( params, state );
        importDataSetMembers( params, state );

        importOrganisationUnits( params, state );
        importOrganisationUnitGroups( params, state );
        importOrganisationUnitGroupMembers( params, state );
        importGroupSets( params, state );
        importGroupSetMembers( params, state );
        importOrganisationUnitRelationships( params, state );

        importDataSetOrganisationUnitAssociations( params, state );

        if ( params.isDataValues() && !params.isAnalysis() )
        {
            importPeriods( params, state );
            importRoutineDataValues( params, state );

            importOnChangePeriods( params, state );
            importSemiPermanentDataValues( params, state );
        }

        if ( params.isAnalysis() )
        {
            state.setOutput( importAnalyser.getImportAnalysis() );
        }

        Dhis14PeriodUtil.clear();

        NameMappingUtil.clearMapping();

        cacheManager.clearCache();
    }

    // -------------------------------------------------------------------------
    // DataElement and Indicator
    // -------------------------------------------------------------------------

    private void importDataElements( ImportParams params, ProcessState state )
    {        
        state.setMessage( "importing_data_elements" );
        
        BatchHandler<DataElement> batchHandler = batchHandlerFactory.createBatchHandler( DataElementBatchHandler.class ).init();
        
        DataElementCategoryCombo categoryCombo = categoryService.
            getDataElementCategoryComboByName( DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );
        
        RowHandler rowHandler = new DataElementRowHandler( batchHandler,
            importObjectService,
            dataElementService, 
            params,
            categoryCombo,
            importAnalyser );

        queryManager.queryWithRowhandler( "getDataElements", rowHandler );

        batchHandler.flush();
        
        log.info( "Imported DataElements" );
    }
    
    private void importIndicatorTypes( ImportParams params, ProcessState state )
    {
        state.setMessage( "importing_indicator_types" );
        
        BatchHandler<IndicatorType> batchHandler = batchHandlerFactory.createBatchHandler( IndicatorTypeBatchHandler.class ).init();
        
        RowHandler rowHandler = new IndicatorTypeRowHandler( batchHandler,
            importObjectService,
            indicatorService,
            params );
        
        queryManager.queryWithRowhandler( "getIndicatorTypes", rowHandler );
        
        batchHandler.flush();
        
        log.info( "Imported IndicatorTypes" );
    }
    
    private void importIndicators( ImportParams params, ProcessState state )
    {
        state.setMessage( "importing_indicators" );
        
        BatchHandler<Indicator> indicatorBatchHandler = batchHandlerFactory.createBatchHandler( IndicatorBatchHandler.class ).init();
        BatchHandler<DataElement> dataElementBatchHandler = batchHandlerFactory.createBatchHandler( DataElementBatchHandler.class ).init();
        BatchHandler<IndicatorType> indicatorTypeBatchHandler = batchHandlerFactory.createBatchHandler( IndicatorTypeBatchHandler.class ).init();
        
        RowHandler rowHandler = new IndicatorRowHandler( indicatorBatchHandler,
            importObjectService,
            indicatorService,
            objectMappingGenerator.getIndicatorTypeMapping( params.skipMapping() ), 
            objectMappingGenerator.getDataElementMapping( params.skipMapping() ),
            categoryService.getDefaultDataElementCategoryOptionCombo(),
            params,
            importAnalyser );
        
        queryManager.queryWithRowhandler( "getIndicators", rowHandler );
        
        indicatorBatchHandler.flush();
        dataElementBatchHandler.flush();
        indicatorTypeBatchHandler.flush();
        
        log.info( "Imported Indicators" );
    }
    
    private void importDataElementGroups( ImportParams params, ProcessState state )
    {
        state.setMessage( "importing_data_element_groups" );
        
        BatchHandler<DataElementGroup> batchHandler = batchHandlerFactory.createBatchHandler( DataElementGroupBatchHandler.class ).init();
        
        RowHandler rowHandler = new DataElementGroupRowHandler( batchHandler,
            importObjectService,
            dataElementService, 
            params );
        
        queryManager.queryWithRowhandler( "getDataElementGroups", rowHandler );
        
        batchHandler.flush();        
        
        log.info( "Imported DataElementGroups" );
    }
    
    private void importIndicatorGroups( ImportParams params, ProcessState state )
    {
        state.setMessage( "importing_indicator_groups" );
        
        BatchHandler<IndicatorGroup> batchHandler = batchHandlerFactory.createBatchHandler( IndicatorGroupBatchHandler.class ).init();
        
        RowHandler rowHandler = new IndicatorGroupRowHandler( batchHandler,
            importObjectService,
            indicatorService, 
            params );
        
        queryManager.queryWithRowhandler( "getIndicatorGroups", rowHandler );
        
        batchHandler.flush();
        
        log.info( "Imported IndicatorGroups" );
    }
    
    private void importDataElementGroupMembers( ImportParams params, ProcessState state )
    {
        state.setMessage( "importing_data_element_group_members" );
        
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler( DataElementGroupMemberBatchHandler.class ).init();
        
        RowHandler rowHandler = new DataElementGroupMemberRowHandler( batchHandler,
            importObjectService,
            objectMappingGenerator.getDataElementMapping( params.skipMapping() ),
            objectMappingGenerator.getDataElementGroupMapping( params.skipMapping() ),
            params );
        
        queryManager.queryWithRowhandler( "getDataElementGroupMembers", rowHandler );
        
        batchHandler.flush();
        
        log.info( "Imported DataElementGroup members" );
    }

    private void importIndicatorGroupMembers( ImportParams params, ProcessState state )
    {
        state.setMessage( "importing_indicator_group_members" );
        
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler( IndicatorGroupMemberBatchHandler.class ).init();
        
        RowHandler rowHandler = new IndicatorGroupMemberRowHandler( batchHandler,
            importObjectService,
            objectMappingGenerator.getIndicatorMapping( params.skipMapping() ),
            objectMappingGenerator.getIndicatorGroupMapping( params.skipMapping() ),
            params );
        
        queryManager.queryWithRowhandler( "getIndicatorGroupMembers", rowHandler );
        
        batchHandler.flush();
        
        log.info( "Imported IndicatorGroup members" );
    }
    
    // -------------------------------------------------------------------------
    // DataSet
    // -------------------------------------------------------------------------

    private void importDataSets( ImportParams params, ProcessState state )
    {
        state.setMessage( "importing_data_sets" );
        
        BatchHandler<DataSet> batchHandler = batchHandlerFactory.createBatchHandler( DataSetBatchHandler.class ).init();
        
        RowHandler rowHandler = new DataSetRowHandler( batchHandler,
            importObjectService,
            dataSetService,
            objectMappingGenerator.getPeriodTypeMapping(),
            params,
            importAnalyser );
        
        queryManager.queryWithRowhandler( "getDataSets", rowHandler );
        
        batchHandler.flush();
        
        log.info( "Imported DataSets" );
    }
    
    private void importDataSetMembers( ImportParams params, ProcessState state )
    {
        state.setMessage( "importing_data_set_members" );
        
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler( DataSetMemberBatchHandler.class ).init();
        
        RowHandler rowHandler = new DataSetMemberRowHandler( batchHandler,
            importObjectService,
            objectMappingGenerator.getDataElementMapping( params.skipMapping() ), 
            objectMappingGenerator.getDataSetMapping( params.skipMapping() ),
            params );
        
        queryManager.queryWithRowhandler( "getDataSetMembers", rowHandler );
        
        batchHandler.flush();
        
        log.info( "Imported DataSet members" );
    }

    // -------------------------------------------------------------------------
    // OrganisatonUnit
    // -------------------------------------------------------------------------

    private void importOrganisationUnits( ImportParams params, ProcessState state )
    {
        state.setMessage( "importing_organisation_units" );
        
        BatchHandler<OrganisationUnit> organisationUnitBatchHandler = batchHandlerFactory.createBatchHandler( OrganisationUnitBatchHandler.class ).init();
        
        RowHandler rowHandler = new OrganisationUnitRowHandler( organisationUnitBatchHandler, 
            importObjectService,
            organisationUnitService,
            params,
            importAnalyser );
        
        queryManager.queryWithRowhandler( "getOrganisationUnits", rowHandler );
        
        organisationUnitBatchHandler.flush();
        
        log.info( "Imported OrganisationUnits" );       
    }
    
    private void importOrganisationUnitGroups( ImportParams params, ProcessState state )
    {
        state.setMessage( "importing_organisation_unit_groups" );
        
        BatchHandler<OrganisationUnitGroup> batchHandler = batchHandlerFactory.createBatchHandler( OrganisationUnitGroupBatchHandler.class ).init();
        
        RowHandler rowHandler = new OrganisationUnitGroupRowHandler( batchHandler,
            importObjectService,
            organisationUnitGroupService,
            params );
        
        queryManager.queryWithRowhandler( "getOrganisationUnitGroups", rowHandler );
        
        batchHandler.flush();
        
        log.info( "Imported OrganisationUnitGroups" );
    }
    
    private void importOrganisationUnitGroupMembers( ImportParams params, ProcessState state )
    {
        state.setMessage( "importing_organisation_unit_group_members" );
        
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler( OrganisationUnitGroupMemberBatchHandler.class ).init();
        
        RowHandler rowHandler = new OrganisationUnitGroupMemberRowHandler( batchHandler,
            importObjectService,
            objectMappingGenerator.getOrganisationUnitMapping( params.skipMapping() ),
            objectMappingGenerator.getOrganisationUnitGroupMapping( params.skipMapping() ),
            params );
        
        queryManager.queryWithRowhandler( "getOrganisationUnitGroupMembers", rowHandler );
        
        batchHandler.flush();
        
        log.info( "Imported OrganisationUnitGroup members" );
    }
    
    private void importGroupSets( ImportParams params, ProcessState state )
    {
        state.setMessage( "importing_organisation_unit_group_sets" );
        
        BatchHandler<OrganisationUnitGroupSet> batchHandler = batchHandlerFactory.createBatchHandler( GroupSetBatchHandler.class ).init();
        
        RowHandler rowHandler = new GroupSetRowHandler( batchHandler, 
            importObjectService,
            organisationUnitGroupService,
            params );
        
        queryManager.queryWithRowhandler( "getOrganisationUnitGroupSets", rowHandler );
        
        batchHandler.flush();
        
        log.info( "Imported OrganisationUnitGroupSets" );  
    }
    
    private void importGroupSetMembers( ImportParams params, ProcessState state )
    {
        state.setMessage( "importing_organisation_unit_group_set_members" );
        
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler( GroupSetMemberBatchHandler.class ).init();
        
        RowHandler rowHandler = new GroupSetMemberRowHandler( batchHandler,
            importObjectService,
            objectMappingGenerator.getOrganisationUnitGroupMapping( params.skipMapping() ),
            objectMappingGenerator.getOrganisationUnitGroupSetMapping( params.skipMapping() ),
            params );
        
        queryManager.queryWithRowhandler( "getOrganisationUnitGroupSetMembers", rowHandler );
        
        batchHandler.flush();
                
        log.info( "Imported OrganisationUnitGroupSet members" );
    }

    private void importOrganisationUnitRelationships( ImportParams params, ProcessState state )
    {
        state.setMessage( "importing_organisation_unit_relationships" );
        
        BatchHandler<OrganisationUnit> batchHandler = batchHandlerFactory.createBatchHandler( OrganisationUnitBatchHandler.class ).init();
        
        RowHandler rowHandler = new OrganisationUnitRelationshipRowHandler( batchHandler,
            importObjectService,
            organisationUnitService,
            objectMappingGenerator.getOrganisationUnitMapping( params.skipMapping() ),
            params );
        
        queryManager.queryWithRowhandler( "getOrganisationUnitRelationships", rowHandler );
        
        batchHandler.flush();
        
        log.info( "Imported OrganisationUnitRelationships" );
    }
    
    // -------------------------------------------------------------------------
    // DataSet - OrganisationUnit Associations
    // -------------------------------------------------------------------------

    private void importDataSetOrganisationUnitAssociations( ImportParams params, ProcessState state )
    {
        state.setMessage( "importing_data_set_organisation_unit_associations" );
        
        BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler( DataSetSourceAssociationBatchHandler.class ).init();
        
        RowHandler rowHandler = new DataSetOrganisationUnitAssociationRowHandler( batchHandler,
            importObjectService,
            objectMappingGenerator.getDataSetMapping( params.skipMapping() ), 
            objectMappingGenerator.getOrganisationUnitMapping( params.skipMapping() ),
            params );
        
        queryManager.queryWithRowhandler( "getDataSetOrganisationUnitAssociations", rowHandler );
        
        batchHandler.flush();
        
        log.info( "Imported DataSet OrganisationUnit Associations" );
    }

    // -------------------------------------------------------------------------
    // Period
    // -------------------------------------------------------------------------

    private void importPeriods( ImportParams params, ProcessState state )
    {   
        state.setMessage( "importing_periods" );
        
        BatchHandler<Period> batchHandler = batchHandlerFactory.createBatchHandler( PeriodBatchHandler.class ).init();
        
        RowHandler rowHandler = new PeriodRowHandler( batchHandler, 
            importObjectService,
            periodService,
            objectMappingGenerator.getPeriodTypeMapping(),
            params,
            getPeriodWithDataIdentifiers() );
        
        queryManager.queryWithRowhandler( "getPeriods", rowHandler );
        
        batchHandler.flush();
        
        log.info( "Imported Periods" );
    }
    
    // -------------------------------------------------------------------------
    // RoutineDataValue
    // -------------------------------------------------------------------------

    private void importRoutineDataValues( ImportParams params, ProcessState state )
    {
        state.setMessage( "importing_routine_data_values" );

        DataElementCategoryOptionCombo categoryOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        
        BatchHandler<DataValue> batchHandler = batchHandlerFactory.createBatchHandler( DataValueBatchHandler.class ).init();        
        BatchHandler<ImportDataValue> importDataValueBatchHandler = batchHandlerFactory.createBatchHandler( ImportDataValueBatchHandler.class ).init();
                
        RowHandler rowHandler = new RoutineDataValueRowHandler( batchHandler,
            importDataValueBatchHandler,
            dataValueService,
            objectMappingGenerator.getDataElementMapping( params.skipMapping() ),
            objectMappingGenerator.getPeriodMapping( params.skipMapping() ),
            objectMappingGenerator.getOrganisationUnitMapping( params.skipMapping() ),
            categoryOptionCombo,
            params );
        
        if ( params.getLastUpdated() == null )
        {
            queryManager.queryWithRowhandler( "getRoutineDataValues", rowHandler );
        }
        else
        {            
            queryManager.queryWithRowhandler( "getRoutineDataValuesLastUpdated", rowHandler, params.getLastUpdated() );
        }
        
        batchHandler.flush();
        
        importDataValueBatchHandler.flush();
        
        log.info( "Imported RoutineDataValues" );
    }

    // -------------------------------------------------------------------------
    // OnChangePeriod
    // -------------------------------------------------------------------------

    private void importOnChangePeriods( ImportParams params, ProcessState state )
    {
        state.setMessage( "importing_on_change_periods" );
        
        BatchHandler<Period> batchHandler = batchHandlerFactory.createBatchHandler( PeriodBatchHandler.class ).init();
        
        RowHandler rowHandler = new OnChangePeriodRowHandler( batchHandler,
            importObjectService,
            periodService,
            objectMappingGenerator.getPeriodTypeMapping(),
            params );
        
        queryManager.queryWithRowhandler( "getOnChangePeriods", rowHandler );
        
        batchHandler.flush();
        
        log.info( "Imported OnChangePeriods" );
    }

    // -------------------------------------------------------------------------
    // SemiPermanentDataValue
    // -------------------------------------------------------------------------

    private void importSemiPermanentDataValues( ImportParams params, ProcessState state )
    {
        state.setMessage( "importing_semi_permanent_data_values" );

        DataElementCategoryOptionCombo categoryOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        
        BatchHandler<DataValue> batchHandler = batchHandlerFactory.createBatchHandler( DataValueBatchHandler.class ).init();
        BatchHandler<ImportDataValue> importDataValueBatchHandler = batchHandlerFactory.createBatchHandler( ImportDataValueBatchHandler.class ).init();
        
        RowHandler rowHandler = new SemiPermanentDataValueRowHandler( batchHandler,
            importDataValueBatchHandler,
            dataValueService,
            objectMappingGenerator.getDataElementMapping( params.skipMapping() ),
            objectMappingGenerator.getPeriodObjectMapping( params.skipMapping() ),
            objectMappingGenerator.getOrganisationUnitMapping( params.skipMapping() ),
            categoryOptionCombo,
            params );
        
        if ( params.getLastUpdated() == null )
        {
            queryManager.queryWithRowhandler( "getSemiPermanentDataValues", rowHandler );
        }
        else
        {
            queryManager.queryWithRowhandler( "getSemiPermanentDataValuesLastUpdated", rowHandler, params.getLastUpdated() );
        }
        
        batchHandler.flush();
        
        importDataValueBatchHandler.flush();
        
        log.info( "Imported SemiPermanentDataValues" );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Returns a list of distinct period identifiers from the RoutineDataValue table,
     * ie. periods which have registered data. Could be used to avoid importing 
     * periods without data.
     */
    private Set<Integer> getPeriodWithDataIdentifiers()
    {
        Set<Integer> identifiers = new HashSet<Integer>();

        List<?> list = queryManager.queryForList( "getDistinctPeriodIdentifiers", null );
        
        for ( Object id : list )
        {
            identifiers.add( (Integer) id );
        }
        
        return identifiers;
    }
        
    /**
     * Verifies that the import file is valid by checking for routine and semi
     * permanent data values out of range.
     */
    private boolean verifyImportFile( ImportParams params, ProcessState state )
    {
        if ( params.isDataValues() )
        {
            Integer count = (Integer) queryManager.queryForObject( "getRoutineDataValuesOutOfRange", null );
            
            if ( count != null && count > 0 )
            {
                state.setMessage( "routine_data_contains_values_out_of_range" );
                log.error( "Table RoutineData contains values larger than 2^31 which is out of range"  );
                
                return false;
            }
            
            count = (Integer) queryManager.queryForObject( "getRoutineDataArchiveValuesOutOfRange", null );
            
            if ( count != null && count > 0 )
            {
                state.setMessage( "routine_data_archive_contains_values_out_of_range" );
                log.error( "Table RoutineDataArchive contains values larger than 2^31 which is out of range"  );
                
                return false;
            }
            
            count = (Integer) queryManager.queryForObject( "getSemiPermanentDataValuesOutOfRange", null );
            
            if ( count != null && count > 0 )
            {
                state.setMessage( "semi_permanent_data_contains_values_out_of_range" );
                log.error( "Table SemiPermanentData contains values larger than 2^31 which is out of range"  );
                
                return false;
            }
        }
        
        log.info( "Verified import file" );
        
        return true;
    }
}
