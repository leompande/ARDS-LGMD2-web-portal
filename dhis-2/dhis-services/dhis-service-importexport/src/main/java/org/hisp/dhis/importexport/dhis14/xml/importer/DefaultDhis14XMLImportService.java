package org.hisp.dhis.importexport.dhis14.xml.importer;

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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.amplecode.staxwax.factory.XMLFactory;
import org.amplecode.staxwax.reader.XMLReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hisp.dhis.cache.HibernateCacheManager;
import org.hisp.dhis.common.ProcessState;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.importexport.CSVConverter;
import org.hisp.dhis.importexport.ImportDataValue;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.ImportService;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.analysis.DefaultImportAnalyser;
import org.hisp.dhis.importexport.analysis.ImportAnalyser;
import org.hisp.dhis.importexport.dhis14.xml.converter.DataElementCategoryComboConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.DataElementCategoryConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.DataElementCategoryOptionComboConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.DataElementCategoryOptionConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.DataElementConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.DataValueConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.IndicatorConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.IndicatorTypeConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.OrganisationUnitConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.OrganisationUnitHierarchyConverter;
import org.hisp.dhis.importexport.dhis14.xml.converter.PeriodConverter;
import org.hisp.dhis.importexport.invoker.ConverterInvoker;
import org.hisp.dhis.importexport.mapping.NameMappingUtil;
import org.hisp.dhis.importexport.mapping.ObjectMappingGenerator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.jdbc.batchhandler.ImportDataValueBatchHandler;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.system.process.OutputHolderState;
import org.hisp.dhis.system.util.StreamUtils;


/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DefaultDhis14XMLImportService
    implements ImportService
{
    private static final Log log = LogFactory.getLog( DefaultDhis14XMLImportService.class );

    private static final String DATA_FILE_NAME = "routinedata.txt";

    private static final String META_DATA_FILE_SUFFIX = ".xml";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private ImportObjectService importObjectService;

    public void setImportObjectService( ImportObjectService importObjectService )
    {
        this.importObjectService = importObjectService;
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

    private ConverterInvoker converterInvoker;

    public void setConverterInvoker( ConverterInvoker converterInvoker )
    {
        this.converterInvoker = converterInvoker;
    }

    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
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

    public DefaultDhis14XMLImportService()
    {
        super();
    }

    // -------------------------------------------------------------------------
    // ImportService implementation
    // -------------------------------------------------------------------------

    public void importData( ImportParams params, InputStream inputStream )
    {
        importData( params, inputStream, new OutputHolderState() );
    }

    public void importData( ImportParams params, InputStream inputStream, ProcessState state )
    {
        DataElementCategoryOptionCombo defaultCategoryOptionCombo = categoryService
            .getDefaultDataElementCategoryOptionCombo();

        NameMappingUtil.clearMapping();

        importAnalyser = new DefaultImportAnalyser( expressionService );

        if ( !(params.isPreview() || params.isAnalysis()) )
        {
            throw new RuntimeException( "Only preview mode allowed for DHIS 1.4 XML import" );
        }

        importObjectService.deleteImportObjects();

        ZipInputStream zipIn = new ZipInputStream( inputStream );

        ZipEntry zipEntry = StreamUtils.getNextZipEntry( zipIn );

        while ( zipEntry != null )
        {
            log.info( "Reading file: " + zipEntry.getName() );

            if ( zipEntry.getName().toLowerCase().trim().endsWith( META_DATA_FILE_SUFFIX ) )
            {
                // -------------------------------------------------------------
                // Meta-data
                // -------------------------------------------------------------

                state.setMessage( "importing_meta_data" );
                log.info( "Importing meta data" );

                XMLReader reader = XMLFactory.getXMLReader( zipIn );

                XMLConverter categoryOptionConverter = new DataElementCategoryOptionConverter( importObjectService,
                    categoryService );
                XMLConverter categoryConverter = new DataElementCategoryConverter( importObjectService, categoryService );
                XMLConverter categoryComboConverter = new DataElementCategoryComboConverter( importObjectService,
                    categoryService );
                XMLConverter categoryOptionComboConverter = new DataElementCategoryOptionComboConverter(
                    importObjectService, categoryService );
                XMLConverter dataElementConverter = new DataElementConverter( importObjectService, dataElementService,
                    categoryService, importAnalyser );
                XMLConverter indicatorTypeConverter = new IndicatorTypeConverter( importObjectService, indicatorService );
                XMLConverter indicatorConverter = new IndicatorConverter( importObjectService, indicatorService,
                    importAnalyser, defaultCategoryOptionCombo );
                XMLConverter organisationUnitConverter = new OrganisationUnitConverter( importObjectService,
                    organisationUnitService, importAnalyser );
                XMLConverter hierarchyConverter = new OrganisationUnitHierarchyConverter( importObjectService,
                    organisationUnitService );
                XMLConverter periodConverter = new PeriodConverter( importObjectService, periodService,
                    objectMappingGenerator.getPeriodTypeMapping() );

                converterInvoker.invokeRead( categoryOptionConverter, reader, params );
                converterInvoker.invokeRead( categoryConverter, reader, params );
                converterInvoker.invokeRead( categoryComboConverter, reader, params );
                converterInvoker.invokeRead( categoryOptionComboConverter, reader, params );

                while ( reader.next() )
                {
                    if ( reader.isStartElement( DataElementConverter.ELEMENT_NAME ) )
                    {
                        converterInvoker.invokeRead( dataElementConverter, reader, params );
                    }
                    else if ( reader.isStartElement( IndicatorTypeConverter.ELEMENT_NAME ) )
                    {
                        converterInvoker.invokeRead( indicatorTypeConverter, reader, params );
                    }
                    else if ( reader.isStartElement( IndicatorConverter.ELEMENT_NAME ) )
                    {
                        converterInvoker.invokeRead( indicatorConverter, reader, params );
                    }
                    else if ( reader.isStartElement( OrganisationUnitConverter.ELEMENT_NAME ) )
                    {
                        converterInvoker.invokeRead( organisationUnitConverter, reader, params );
                    }
                    else if ( reader.isStartElement( OrganisationUnitHierarchyConverter.ELEMENT_NAME ) )
                    {
                        converterInvoker.invokeRead( hierarchyConverter, reader, params );
                    }
                    else if ( reader.isStartElement( PeriodConverter.ELEMENT_NAME ) )
                    {
                        converterInvoker.invokeRead( periodConverter, reader, params );
                    }
                }

                reader.closeReader();
            }
            else if ( zipEntry.getName().toLowerCase().trim().equals( DATA_FILE_NAME ) )
            {
                // -------------------------------------------------------------
                // Data
                // -------------------------------------------------------------

                state.setMessage( "importing_data_values" );
                log.info( "Importing DataValues" );

                BufferedReader streamReader = new BufferedReader( new InputStreamReader( zipIn ) );

                BatchHandler<ImportDataValue> importDataValueBatchHandler = batchHandlerFactory.createBatchHandler(
                    ImportDataValueBatchHandler.class ).init();

                CSVConverter dataValueConverter = new DataValueConverter( importDataValueBatchHandler, categoryService,
                    importObjectService, importAnalyser, params );

                dataValueConverter.read( streamReader, params );

                importDataValueBatchHandler.flush();
            }

            zipEntry = StreamUtils.getNextZipEntry( zipIn ); // Move to next
            // entry in archive
        }

        if ( params.isAnalysis() )
        {
            state.setOutput( importAnalyser.getImportAnalysis() );
        }

        StreamUtils.closeInputStream( zipIn );

        NameMappingUtil.clearMapping();

        cacheManager.clearCache();
    }
}
