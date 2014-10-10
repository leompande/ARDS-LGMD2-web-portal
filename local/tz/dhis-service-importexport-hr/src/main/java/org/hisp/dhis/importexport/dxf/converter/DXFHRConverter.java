package org.hisp.dhis.importexport.dxf.converter;

/*
 * Copyright (c) 2004-2005, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the <ORGANIZATION> nor the names of its contributors may
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

import static org.apache.commons.lang.StringUtils.defaultIfEmpty;

import javax.xml.namespace.QName;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.cache.HibernateCacheManager;
import org.hisp.dhis.common.ProcessState;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeGroup;
import org.hisp.dhis.hr.AttributeGroupService;
import org.hisp.dhis.hr.AttributeOptionGroup;
import org.hisp.dhis.hr.AttributeOptionGroupService;
import org.hisp.dhis.hr.AttributeOptions;
import org.hisp.dhis.hr.AttributeOptionsService;
import org.hisp.dhis.hr.AttributeService;
import org.hisp.dhis.hr.DataType;
import org.hisp.dhis.hr.DataTypeService;
import org.hisp.dhis.hr.DataValues;
import org.hisp.dhis.hr.DataValuesService;
import org.hisp.dhis.hr.History;
import org.hisp.dhis.hr.HistoryService;
import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.HrDataSetService;
import org.hisp.dhis.hr.InputType;
import org.hisp.dhis.hr.InputTypeService;
import org.hisp.dhis.hr.Person;
import org.hisp.dhis.hr.PersonService;
import org.hisp.dhis.hr.Training;
import org.hisp.dhis.hr.TrainingService;
import org.hisp.dhis.importexport.HrExportParams;
import org.hisp.dhis.importexport.GroupMemberAssociation;
import org.hisp.dhis.importexport.ImportHrObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLHrConverter;
import org.hisp.dhis.importexport.analysis.DefaultImportAnalyser;
import org.hisp.dhis.importexport.analysis.ImportAnalyser;
import org.hisp.dhis.importexport.invoker.HrConverterInvoker;
import org.hisp.dhis.importexport.mapping.HrNameMappingUtil;
import org.hisp.dhis.importexport.mapping.HrObjectMappingGenerator;
import org.hisp.dhis.jdbc.batchhandler.AttributeAssociationBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.AttributeBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.AttributeGroupBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.AttributeOptionGroupBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.AttributeOptionsAssociationBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.AttributeOptionsBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataTypeBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataValuesBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.HistoryBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.HrDataSetBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.HrDataSetMemberBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.InputTypeBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.PersonBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.TrainingBatchHandler;

/**
 * DXFConverter class This does NOT implement XMLConverter, because we need to
 * pass ProcessState in read() method.
 * 
 * @author bobj
 * @version created 13-Feb-2010
 */
public class DXFHRConverter
{
    public static final String DXFROOT = "dxf";

    public static final String ATTRIBUTE_MINOR_VERSION = "minorVersion";

    public static final String ATTRIBUTE_EXPORTED = "exported";

    public static final String NAMESPACE_10 = "http://dhis2.org/schema/dxf/1.0";

    public static final String MINOR_VERSION_10 = "1.0";

    public static final String MINOR_VERSION_11 = "1.1";

    public static final String MINOR_VERSION_12 = "1.2";

    private final Log log = LogFactory.getLog( DXFHRConverter.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private ImportHrObjectService importObjectService;

    public void setImportObjectService( ImportHrObjectService importObjectService )
    {
        this.importObjectService = importObjectService;
    }
    
    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }

    private HrDataSetService hrDataSetService;
    
    public void setHrDataSetService( HrDataSetService hrDataSetService )
    {
    	this.hrDataSetService = hrDataSetService;
    }
    
    private AttributeService attributeService;
    
    public void setAttributeService( AttributeService attributeService )
    {
    	this.attributeService = attributeService;
    }
    
    private AttributeOptionsService attributeOptionsService;
    
    public void setAttributeOptionsService( AttributeOptionsService attributeOptionsService )
    {
    	this.attributeOptionsService = attributeOptionsService;
    }
    
    private AttributeOptionGroupService attributeOptionGroupService;
    
    public void setAttributeOptionGroupService( AttributeOptionGroupService attributeOptionGroupService )
    {
    	this.attributeOptionGroupService = attributeOptionGroupService;
    }
    
    private PersonService personService;
    
    public void setPersonService( PersonService personService )
    {
    	this.personService = personService;
    }
    
    private HistoryService historyService;
    
    public void setHistoryService( HistoryService historyService )
    {
    	this.historyService = historyService;
    }
    
    private TrainingService trainingService;
    
    public void setTrainingService( TrainingService trainingService )
    {
    	this.trainingService = trainingService;
    }
    
    private DataValuesService dataValuesService;
    
    public void setDataValuesService( DataValuesService dataValuesService )
    {
    	this.dataValuesService =  dataValuesService;
    }
    
    private AttributeGroupService attributeGroupService;
    
    public void setAttributeGroupService( AttributeGroupService attributeGroupService )
    {
    	this.attributeGroupService = attributeGroupService;
    }
    
    private DataTypeService dataTypeService;
    
    public void setDataTypeService( DataTypeService dataTypeService )
    {
    	this.dataTypeService = dataTypeService;
    }
    
    private InputTypeService inputTypeService;
    
    public void setInputTypeService( InputTypeService inputTypeService )
    {
    	this.inputTypeService = inputTypeService;
    }
    
    private BatchHandlerFactory batchHandlerFactory;

    public void setBatchHandlerFactory( BatchHandlerFactory batchHandlerFactory )
    {
        this.batchHandlerFactory = batchHandlerFactory;
    }

    private HrObjectMappingGenerator objectMappingGenerator;

    public void setObjectMappingGenerator( HrObjectMappingGenerator objectMappingGenerator )
    {
        this.objectMappingGenerator = objectMappingGenerator;
    }

    private HibernateCacheManager cacheManager;

    public void setCacheManager( HibernateCacheManager cacheManager )
    {
        this.cacheManager = cacheManager;
    }

    private HrConverterInvoker converterInvoker;

    public void setConverterInvoker( HrConverterInvoker converterInvoker )
    {
        this.converterInvoker = converterInvoker;
    }

    public void write( XMLWriter writer, HrExportParams params, ProcessState state )
    {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    public void read( XMLReader reader, ImportParams params, ProcessState state )
    {
        ImportAnalyser importAnalyser = new DefaultImportAnalyser( expressionService );

        HrNameMappingUtil.clearMapping();

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
            if ( reader.isStartElement( DataTypeConverter.COLLECTION_NAME ) )
            {
                state.setMessage( "importing_data_types" );

                BatchHandler<DataType> batchHandler = batchHandlerFactory.createBatchHandler(
                    DataTypeBatchHandler.class ).init();

                XMLHrConverter converter = new DataTypeConverter( batchHandler, importObjectService,
                    dataTypeService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported DataTypes" );
            }
            else if ( reader.isStartElement( InputTypeConverter.COLLECTION_NAME ) )
            {
                state.setMessage( "importing_input_types" );

                BatchHandler<InputType> batchHandler = batchHandlerFactory.createBatchHandler(
                    InputTypeBatchHandler.class ).init();

                XMLHrConverter converter = new InputTypeConverter( batchHandler, importObjectService,
                    inputTypeService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported InputTypes" );
            }
            else if ( reader.isStartElement( AttributeConverter.COLLECTION_NAME ) )
            {
                state.setMessage( "importing_attributes" );

                BatchHandler<Attribute> batchHandler = batchHandlerFactory.createBatchHandler(
                    AttributeBatchHandler.class ).init();

                XMLHrConverter converter = new AttributeConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getDataTypeMapping( params.skipMapping() ), 
                    objectMappingGenerator.getInputTypeMapping( params.skipMapping() ), attributeService,
                    importAnalyser );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported Attributes" );
            }
            else if ( reader.isStartElement( AttributeOptionsConverter.COLLECTION_NAME ) )
            {
                state.setMessage( "importing_attribute_options" );

                BatchHandler<AttributeOptions> batchHandler = batchHandlerFactory.createBatchHandler(
                    AttributeOptionsBatchHandler.class ).init();

                XMLHrConverter converter = new AttributeOptionsConverter( batchHandler, importObjectService,
                		objectMappingGenerator.getAttributeMapping( params.skipMapping() ), attributeOptionsService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported AttributeOptions" );
            }
            else if ( reader.isStartElement( AttributeGroupConverter.COLLECTION_NAME ) )
            {
                state.setMessage( "importing_attribute_group" );

                BatchHandler<AttributeGroup> batchHandler = batchHandlerFactory.createBatchHandler(
                    AttributeGroupBatchHandler.class ).init();

                XMLHrConverter converter = new AttributeGroupConverter( batchHandler, importObjectService,
                    attributeGroupService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported AttributeGroups" );
            }
            else if ( reader.isStartElement( AttributeAssociationConverter.COLLECTION_NAME ) )
            {
                state.setMessage( "importing_attribute_group_members" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler(
                    AttributeAssociationBatchHandler.class ).init();

                XMLHrConverter converter = new AttributeAssociationConverter( batchHandler,
                    importObjectService, objectMappingGenerator.getAttributeGroupMapping( params.skipMapping() ),
                    objectMappingGenerator.getAttributeMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported Attribute associations" );
            }
            else if ( reader.isStartElement( AttributeOptionGroupConverter.COLLECTION_NAME ) )
            {
                state.setMessage( "importing_attribute_option_group" );

                BatchHandler<AttributeOptionGroup> batchHandler = batchHandlerFactory.createBatchHandler(
                    AttributeOptionGroupBatchHandler.class ).init();

                XMLHrConverter converter = new AttributeOptionGroupConverter( batchHandler, importObjectService,
                    attributeOptionGroupService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported AttributeOptionGroups" );
            }
            else if ( reader.isStartElement( HrDataSetConverter.COLLECTION_NAME ) )
            {
                state.setMessage( "importing_hr_datasets" );

                BatchHandler<HrDataSet> batchHandler = batchHandlerFactory.createBatchHandler( HrDataSetBatchHandler.class )
                    .init();

                XMLHrConverter converter = new HrDataSetConverter( batchHandler, importObjectService, hrDataSetService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported HrDataSets" );
            }
            else if ( reader.isStartElement( HrDataSetMemberConverter.COLLECTION_NAME ) )
            {
                state.setMessage( "importing_hr_dataset_members" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler(
                    HrDataSetMemberBatchHandler.class ).init();

                XMLHrConverter converter = new HrDataSetMemberConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getAttributeMapping( params.skipMapping() ), objectMappingGenerator
                        .getHrDataSetMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported HrDataSet members" );
            }
            else if ( reader.isStartElement( PersonConverter.COLLECTION_NAME ) )
            {
                state.setMessage( "importing_persons" );

                BatchHandler<Person> batchHandler = batchHandlerFactory.createBatchHandler(
                    PersonBatchHandler.class ).init();

                XMLHrConverter converter = new PersonConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getHrDataSetMapping( params.skipMapping() ), 
                    objectMappingGenerator.getHrOrganisationUnitMapping(params.skipMapping()) , personService);

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported Persons" );
            }
            else if ( reader.isStartElement( HistoryConverter.COLLECTION_NAME ) )
            {
                state.setMessage( "importing_historys" );

                BatchHandler<History> batchHandler = batchHandlerFactory.createBatchHandler(
                    HistoryBatchHandler.class ).init();

                XMLHrConverter converter = new HistoryConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getPersonMapping( params.skipMapping() ), 
                    objectMappingGenerator.getAttributeMapping( params.skipMapping() ), historyService);

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported Histories" );
            }
            else if ( reader.isStartElement( TrainingConverter.COLLECTION_NAME ) )
            {
                state.setMessage( "importing_trainings" );

                BatchHandler<Training> batchHandler = batchHandlerFactory.createBatchHandler(
                    TrainingBatchHandler.class ).init();

                XMLHrConverter converter = new TrainingConverter( batchHandler, importObjectService,
                    objectMappingGenerator.getPersonMapping( params.skipMapping() ), trainingService);

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported Trainings" );
            }
            else if ( reader.isStartElement( DataValuesConverter.COLLECTION_NAME ) )
            {
                state.setMessage( "importing_hr_data_values" );

                BatchHandler<DataValues> batchHandler = batchHandlerFactory.createBatchHandler(
                    DataValuesBatchHandler.class ).init();

                XMLHrConverter converter = new DataValuesConverter( batchHandler,  importObjectService,
                		objectMappingGenerator.getPersonMapping( params.skipMapping() ), objectMappingGenerator.getAttributeMapping( params
                        .skipMapping() ), dataValuesService );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported DataValues" );
            }
            /*
            else if ( reader.isStartElement( AttributeOptionsAssociationConverter.COLLECTION_NAME ) )
            {
                state.setMessage( "importing_attribute_group_members" );

                BatchHandler<GroupMemberAssociation> batchHandler = batchHandlerFactory.createBatchHandler(
                    AttributeOptionsAssociationBatchHandler.class ).init();

                XMLConverter converter = new AttributeOptionsAssociationConverter( batchHandler,
                    importObjectService, objectMappingGenerator.getAttributeOptionGroupMapping( params.skipMapping() ),
                    objectMappingGenerator.getAttributeOptionsMapping( params.skipMapping() ) );

                converterInvoker.invokeRead( converter, reader, params );

                batchHandler.flush();

                log.info( "Imported AttributeOptions associations" );
            }
            */
        }

        if ( params.isAnalysis() )
        {
            state.setOutput( importAnalyser.getImportAnalysis() );
        }

        HrNameMappingUtil.clearMapping();

        cacheManager.clearCache();
    }
}
