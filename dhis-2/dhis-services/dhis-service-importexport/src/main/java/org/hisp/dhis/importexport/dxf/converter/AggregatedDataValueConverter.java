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

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;

import java.util.Collection;

import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.aggregation.AggregatedDataValue;
import org.hisp.dhis.aggregation.AggregatedDataValueService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class AggregatedDataValueConverter
    implements XMLConverter
{
    private static final Log log = LogFactory.getLog( AggregatedDataValueConverter.class );
    
    public static final String COLLECTION_NAME = "dataValues";
    public static final String ELEMENT_NAME = "dataValue";
    
    private static final String FIELD_DATAELEMENT = "dataElement";
    private static final String FIELD_PERIOD = "period";
    private static final String FIELD_SOURCE = "source";
    private static final String FIELD_VALUE = "value";
    private static final String FIELD_STOREDBY = "storedBy";
    private static final String FIELD_TIMESTAMP = "timeStamp";
    private static final String FIELD_COMMENT = "comment";
    private static final String FIELD_CATEGORY_OPTION_COMBO = "categoryOptionCombo";

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private AggregatedDataValueService aggregatedDataValueService;

    private DataSetService dataSetService;
    
    private PeriodService periodService;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public AggregatedDataValueConverter( AggregatedDataValueService aggregatedDataValueService,
        DataSetService dataSetService,
        PeriodService periodService )
    {
        this.aggregatedDataValueService = aggregatedDataValueService;
        this.dataSetService = dataSetService;
        this.periodService = periodService;
    }

    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, ExportParams params )
    {
        if ( params.isIncludeDataValues() )
        {
            Collection<DataElement> dataElements = dataSetService.getDistinctDataElements( params.getDataSets() );

            if ( dataElements.size() > 0 && params.getStartDate() != null && params.getEndDate() != null )
            {
                Collection<Period> periods = null;

                Collection<AggregatedDataValue> values = null;
                
                writer.openElement( COLLECTION_NAME );
                
                for ( final DataElement dataElement : dataElements )
                {
                    final PeriodType periodType = dataSetService.getPeriodType( dataElement, params.getDataSets() );
                    
                    periods = periodService.getIntersectingPeriodsByPeriodType( periodType, params.getStartDate(), params.getEndDate() );
                    
                    log.debug( "Using period type: " + periodType.getName() + " for data element: " + dataElement.getName() );
                    
                    values = aggregatedDataValueService.getAggregatedDataValues( dataElement.getId(), 
                        getIdentifiers( Period.class, periods ), params.getOrganisationUnits() );
                    
                    for ( AggregatedDataValue value : values )
                    {
                        writer.openElement( ELEMENT_NAME );
                        
                        writer.writeElement( FIELD_DATAELEMENT, String.valueOf( value.getDataElementId() ) );
                        writer.writeElement( FIELD_PERIOD, String.valueOf( value.getPeriodId() ) );
                        writer.writeElement( FIELD_SOURCE, String.valueOf( value.getOrganisationUnitId() ) );
                        writer.writeElement( FIELD_VALUE, String.valueOf( Math.round( value.getValue() ) ) );
                        writer.writeElement( FIELD_STOREDBY, "" );
                        writer.writeElement( FIELD_TIMESTAMP, "" );
                        writer.writeElement( FIELD_COMMENT, "" );
                        writer.writeElement( FIELD_CATEGORY_OPTION_COMBO, String.valueOf( value.getCategoryOptionComboId() ) );
                        
                        writer.closeElement();
                    }
                }
                
                writer.closeElement();
            }
        }
    }
    
    public void read( XMLReader reader, ImportParams params )
    {
        // this code should never be called
	throw new UnsupportedOperationException( "Read operation for AggregatedDataValue not supported" );
    }
}
