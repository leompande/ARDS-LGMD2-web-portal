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

import java.util.Collection;
import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.importer.DataSetImporter;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.PeriodType;

/**
 * @author Lars Helge Overland
 * @version $Id: DataSetConverter.java 6455 2008-11-24 08:59:37Z larshelg $
 */
public class DataSetConverter
    extends DataSetImporter
    implements XMLConverter
{
    public static final String COLLECTION_NAME = "dataSets";
    public static final String ELEMENT_NAME = "dataSet";

    private static final String FIELD_ID = "id";
    private static final String FIELD_UID = "uid";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_SHORT_NAME = "shortName";
    private static final String FIELD_CODE = "code";
    private static final String FIELD_PERIOD_TYPE = "periodType";
    
    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private Map<String, Integer> periodTypeMapping;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public DataSetConverter( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    /**
     * Constructor for read operations.
     * 
     * @param batchHandler the batchHandler to use.
     * @param dataSetStore the dataSetStore to use.
     * @param importObjectService the importObjectService to use.
     */
    public DataSetConverter( BatchHandler<DataSet> batchHandler, ImportObjectService importObjectService,
        DataSetService dataSetService, Map<String, Integer> periodTypeMapping )
    {
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.dataSetService = dataSetService;
        this.periodTypeMapping = periodTypeMapping;
    }

    /**
     * Constructor for import operations.
     * 
     * @param batchHandler the batchHandler to use.
     * @param dataSetStore the dataSetStore to use.
     */
    public DataSetConverter( BatchHandler<DataSet> batchHandler, DataSetService dataSetService )
    {
        this.batchHandler = batchHandler;
        this.dataSetService = dataSetService;
    }

    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<DataSet> dataSets = dataSetService.getDataSets( params.getDataSets() );

        if ( dataSets != null && dataSets.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );

            for ( DataSet dataSet : dataSets )
            {
                writer.openElement( ELEMENT_NAME );

                writer.writeElement( FIELD_ID, String.valueOf( dataSet.getId() ) );
                writer.writeElement( FIELD_UID, dataSet.getUid() );
                writer.writeCData( FIELD_NAME, dataSet.getName() );
                writer.writeCData( FIELD_SHORT_NAME, dataSet.getShortName() );
                writer.writeElement( FIELD_CODE, dataSet.getCode() );
                writer.writeElement( FIELD_PERIOD_TYPE, dataSet.getPeriodType().getName() );

                writer.closeElement();
            }

            writer.closeElement();
        }
    }

    public void read( XMLReader reader, ImportParams params )
    {        
        while ( reader.moveToStartElement( ELEMENT_NAME, COLLECTION_NAME ) )
        {
            final DataSet dataSet = new DataSet();
            
            PeriodType periodType = new MonthlyPeriodType();
            dataSet.setPeriodType( periodType );

            final Map<String, String> values = reader.readElements( ELEMENT_NAME );
            
            dataSet.setId( Integer.parseInt( values.get( FIELD_ID ) ) );
            
            if ( params.minorVersionGreaterOrEqual( "1.3") )
            {
                dataSet.setUid( values.get( FIELD_UID) );
            }
            
            dataSet.setName( values.get( FIELD_NAME ) );
            dataSet.setShortName( values.get( FIELD_SHORT_NAME ) );
            
            if ( params.minorVersionGreaterOrEqual( "1.2") ) 
            {
              dataSet.setCode( values.get( FIELD_CODE ) );
            }
            
            dataSet.getPeriodType().setId( periodTypeMapping.get( values.get( FIELD_PERIOD_TYPE ) ) );
            
            importObject( dataSet, params );
        }
    }
}
