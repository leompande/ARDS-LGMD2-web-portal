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
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.analysis.ImportAnalyser;
import org.hisp.dhis.importexport.importer.IndicatorImporter;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.system.util.DateUtils;

/**
 * @author Lars Helge Overland
 * @version $Id: IndicatorConverter.java 6455 2008-11-24 08:59:37Z larshelg $
 */
public class IndicatorConverter
    extends IndicatorImporter implements XMLConverter
{
    public static final String COLLECTION_NAME = "indicators";
    public static final String ELEMENT_NAME = "indicator";
    
    private static final String FIELD_ID = "id";
    private static final String FIELD_UID = "uid";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_SHORT_NAME = "shortName";
    private static final String FIELD_CODE = "code";
    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_ANNUALIZED = "annualized";
    private static final String FIELD_INDICATOR_TYPE = "indicatorType";
    private static final String FIELD_NUMERATOR = "numerator";
    private static final String FIELD_NUMERATOR_DESCRIPTION = "numeratorDescription";
    private static final String FIELD_DENOMINATOR = "denominator";
    private static final String FIELD_DENOMINATOR_DESCRIPTION = "denominatorDescription";
    private static final String FIELD_LAST_UPDATED = "lastUpdated";
    
    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------
    
    private Map<Object, Integer> indicatorTypeMapping;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public IndicatorConverter( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }
    
    /**
     * Constructor for read operations.
     * 
     * @param batchHandler the batchHandler to use.
     * @param importObjectService the importObjectService to use.
     * @param indicatorService the indicatorService to use.
     * @param categoryOptionComboMapping the categoryOptionComboMapping to use.
     */
    public IndicatorConverter( BatchHandler<Indicator> batchHandler, 
        ImportObjectService importObjectService, 
        IndicatorService indicatorService,
        Map<Object, Integer> indicatorTypeMapping, 
        ImportAnalyser importAnalyser )
    {
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.indicatorService = indicatorService;
        this.indicatorTypeMapping = indicatorTypeMapping;
        this.importAnalyser = importAnalyser;
    }    

    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<Indicator> indicators = indicatorService.getIndicators( params.getIndicators() );
        
        if ( indicators != null && indicators.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );
            
            for ( Indicator indicator : indicators )
            {
                writer.openElement( ELEMENT_NAME );
                
                writer.writeElement( FIELD_ID, String.valueOf( indicator.getId() ) );
                writer.writeElement( FIELD_UID,  indicator.getUid() );
                writer.writeElement( FIELD_NAME, indicator.getName() );
                writer.writeElement( FIELD_SHORT_NAME, indicator.getShortName() );
                writer.writeElement( FIELD_CODE, indicator.getCode() );
                writer.writeElement( FIELD_DESCRIPTION, indicator.getDescription() );
                writer.writeElement( FIELD_ANNUALIZED, String.valueOf( indicator.isAnnualized() ) );
                writer.writeElement( FIELD_INDICATOR_TYPE, String.valueOf( indicator.getIndicatorType().getId() ) );
                writer.writeElement( FIELD_NUMERATOR, indicator.getNumerator() );
                writer.writeElement( FIELD_NUMERATOR_DESCRIPTION, indicator.getNumeratorDescription() );
                writer.writeElement( FIELD_DENOMINATOR, indicator.getDenominator() );
                writer.writeElement( FIELD_DENOMINATOR_DESCRIPTION, indicator.getDenominatorDescription() );
                writer.writeElement( FIELD_LAST_UPDATED, DateUtils.getMediumDateString( indicator.getLastUpdated() ), EMPTY );
                            
                writer.closeElement();
            }
            
            writer.closeElement();
        }
    }
    
    public void read( XMLReader reader, ImportParams params )
    {
        while ( reader.moveToStartElement( ELEMENT_NAME, COLLECTION_NAME ) )
        {
            final Map<String, String> values = reader.readElements( ELEMENT_NAME );
            
            final Indicator indicator = new Indicator();
            
            final IndicatorType type = new IndicatorType();
            indicator.setIndicatorType( type );
            indicator.setId( Integer.parseInt( values.get( FIELD_ID ) ) );

            if ( params.minorVersionGreaterOrEqual( "1.3" ) )
            {
                indicator.setUid( values.get( FIELD_UID) );
            }

            indicator.setName( values.get( FIELD_NAME ) );
            indicator.setShortName( values.get( FIELD_SHORT_NAME ) );

            if ( params.minorVersionGreaterOrEqual(  "1.2" ) ) 
            {
                indicator.setCode( values.get( FIELD_CODE ) );
            }
            
            indicator.setDescription( values.get( FIELD_DESCRIPTION ) );
            indicator.setAnnualized( Boolean.parseBoolean( values.get( FIELD_ANNUALIZED ) ) );
            indicator.getIndicatorType().setId( indicatorTypeMapping.get( Integer.parseInt( values.get( FIELD_INDICATOR_TYPE ) ) ) );
            indicator.setNumerator( values.get( FIELD_NUMERATOR ) );
            indicator.setNumeratorDescription( values.get( FIELD_NUMERATOR_DESCRIPTION ) );
            indicator.setDenominator( values.get( FIELD_DENOMINATOR ) );
            indicator.setDenominatorDescription( values.get( FIELD_DENOMINATOR_DESCRIPTION ) );
            indicator.setLastUpdated( DateUtils.getMediumDate( values.get( FIELD_LAST_UPDATED ) ) );            
            
            importObject( indicator, params );
        }
    }
}