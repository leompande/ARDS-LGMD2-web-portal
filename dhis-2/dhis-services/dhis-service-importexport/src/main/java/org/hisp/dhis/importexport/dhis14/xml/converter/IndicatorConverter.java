package org.hisp.dhis.importexport.dhis14.xml.converter;

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

import static org.hisp.dhis.importexport.dhis14.util.Dhis14ExpressionConverter.convertExpressionFromDhis14;
import static org.hisp.dhis.importexport.dhis14.util.Dhis14ExpressionConverter.convertExpressionToDhis14;
import static org.hisp.dhis.importexport.dhis14.util.Dhis14TypeHandler.convertBooleanFromDhis14;
import static org.hisp.dhis.system.util.ConversionUtils.parseInt;

import java.util.Collection;
import java.util.Map;

import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.analysis.ImportAnalyser;
import org.hisp.dhis.importexport.dhis14.util.Dhis14DateUtil;
import org.hisp.dhis.importexport.dhis14.util.Dhis14ParsingUtils;
import org.hisp.dhis.importexport.importer.IndicatorImporter;
import org.hisp.dhis.importexport.mapping.NameMappingUtil;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.system.util.MimicingHashMap;
import org.hisp.dhis.system.util.TextUtils;

/**
 * @author Lars Helge Overland
 * @version $Id: IndicatorConverter.java 6455 2008-11-24 08:59:37Z larshelg $
 */
public class IndicatorConverter
    extends IndicatorImporter implements XMLConverter
{
    public static final String ELEMENT_NAME = "Indicator";
    
    private static final String FIELD_ID = "IndicatorID";
    private static final String FIELD_SORT_ORDER = "SortOrder";
    private static final String FIELD_NAME = "IndicatorName";
    private static final String FIELD_SHORT_NAME = "IndicatorShort";
    private static final String FIELD_DOS = "IndicatorDOS";
    private static final String FIELD_VALID_FROM = "ValidFrom";
    private static final String FIELD_VALID_TO = "ValidTo";
    private static final String FIELD_DESCRIPTION = "IndicatorDescription";
    private static final String FIELD_SELECTED = "Selected";
    private static final String FIELD_INDICATOR_TYPE = "IndicatorTypeID";
    private static final String FIELD_PERIOD_TYPE = "DataPeriodTypeID";
    private static final String FIELD_ANNUALISED = "Annualised";
    private static final String FIELD_NUMERATOR = "IndicatorNumerator";
    private static final String FIELD_NUMERATOR_AGG_LEVEL = "IndicatorNumeratorAggregateStartLevel";
    private static final String FIELD_NUMERATOR_TIME_LAG = "IndicatorNumeratorTimeLag";
    private static final String FIELD_NUMERATOR_DESCRIPTION = "IndicatorNumeratorDescription";
    private static final String FIELD_DENOMINATOR = "IndicatorDenominator";
    private static final String FIELD_DENOMINATOR_AGG_LEVEL = "IndicatorDenominatorAggregateStartLevel";
    private static final String FIELD_DENOMINATOR_TIME_LAG = "IndicatorDenominatorTimeLag";
    private static final String FIELD_DENOMINATOR_DESCRIPTION = "IndicatorDenominatorDescription";
    private static final String FIELD_LAST_USER = "LastUserID";
    private static final String FIELD_LAST_UPDATED = "LastUpdated";
    
    private static final int VALID_FROM = 34335;
    private static final int VALID_TO = 2958465;
    private static final int AGG_START_LEVEL = 5;

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private Map<Object, Integer> indicatorTypeMapping;
    private Map<Object, Integer> dataElementMapping;
    
    private DataElementCategoryOptionCombo categoryOptionCombo;
    
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
     * @param importObjectService
     * @param indicatorService
     */
    public IndicatorConverter( ImportObjectService importObjectService,
        IndicatorService indicatorService,
        ImportAnalyser importAnalyser,
        DataElementCategoryOptionCombo categoryOptionCombo )
    {
        this.importObjectService = importObjectService;
        this.indicatorTypeMapping = new MimicingHashMap<Object, Integer>();
        this.dataElementMapping = new MimicingHashMap<Object, Integer>();
        this.indicatorService = indicatorService;
        this.importAnalyser = importAnalyser;
        this.categoryOptionCombo = categoryOptionCombo;
    }

    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------
    
    public void write( XMLWriter writer, ExportParams params )
    {
        Map<Object, String> mapping = NameMappingUtil.getDataElementAggregationOperatorMap();
        
        Collection<Indicator> indicators = indicatorService.getIndicators( params.getIndicators() );
        
        if ( indicators != null && indicators.size() > 0 )
        {
            for ( Indicator object : indicators )
            {
                writer.openElement( ELEMENT_NAME );
                
                writer.writeElement( FIELD_ID, String.valueOf( object.getId() ) );
                writer.writeElement( FIELD_SORT_ORDER, object.getSortOrder() != null ? String.valueOf( object.getSortOrder() ) : EMPTY );
                writer.writeElement( FIELD_NAME, object.getName() );
                writer.writeElement( FIELD_SHORT_NAME, object.getShortName() );
                writer.writeElement( FIELD_DOS, TextUtils.subString( object.getShortName(), 0, 8 ) );
                writer.writeElement( FIELD_VALID_FROM, String.valueOf( VALID_FROM ) );
                writer.writeElement( FIELD_VALID_TO, String.valueOf( VALID_TO ) );
                writer.writeElement( FIELD_DESCRIPTION, object.getDescription() );
                writer.writeElement( FIELD_SELECTED, String.valueOf( 0 ) );
                writer.writeElement( FIELD_INDICATOR_TYPE, String.valueOf( object.getIndicatorType().getId() ) );
                writer.writeElement( FIELD_PERIOD_TYPE, String.valueOf( 1 ) );
                writer.writeElement( FIELD_ANNUALISED, object.isAnnualized() ? String.valueOf( 1 ) : String.valueOf( 0 ) );
                writer.writeElement( FIELD_NUMERATOR, convertExpressionToDhis14( object.getNumerator(), mapping ) );
                writer.writeElement( FIELD_NUMERATOR_AGG_LEVEL, String.valueOf( AGG_START_LEVEL ) );
                writer.writeElement( FIELD_NUMERATOR_TIME_LAG, String.valueOf( 0 ) );
                writer.writeElement( FIELD_NUMERATOR_DESCRIPTION, object.getNumeratorDescription() );
                writer.writeElement( FIELD_DENOMINATOR, convertExpressionToDhis14( object.getDenominator(), mapping ) );
                writer.writeElement( FIELD_DENOMINATOR_AGG_LEVEL, String.valueOf( AGG_START_LEVEL ) );
                writer.writeElement( FIELD_DENOMINATOR_TIME_LAG, String.valueOf( 0 ) );
                writer.writeElement( FIELD_DENOMINATOR_DESCRIPTION, object.getDenominatorDescription() );
                writer.writeElement( FIELD_LAST_USER, String.valueOf( 1 ) );
                writer.writeElement( FIELD_LAST_UPDATED, Dhis14DateUtil.getDateString( object.getLastUpdated() ) );
                
                writer.closeElement();
            }
        }
    }
    
    public void read( XMLReader reader, ImportParams params )
    {
        final Indicator indicator = new Indicator();

        final IndicatorType type = new IndicatorType();
        indicator.setIndicatorType( type );
        
        Map<String, String> values = reader.readElements( ELEMENT_NAME );
        
        indicator.setId( Integer.parseInt( values.get( FIELD_ID ) ) );
        indicator.setName( values.get( FIELD_NAME ) );
        indicator.setShortName( values.get( FIELD_SHORT_NAME ) );
        indicator.setDescription( Dhis14ParsingUtils.removeNewLine( values.get( FIELD_DESCRIPTION ) ) );
        indicator.setAnnualized( convertBooleanFromDhis14( values.get( FIELD_ANNUALISED ) ) );
        indicator.getIndicatorType().setId( indicatorTypeMapping.get( Integer.parseInt( values.get( FIELD_INDICATOR_TYPE ) ) ) );
        indicator.setNumeratorDescription( values.get( FIELD_NUMERATOR_DESCRIPTION ) );
        indicator.setDenominatorDescription( values.get( FIELD_DENOMINATOR_DESCRIPTION ) );

        indicator.setNumerator( convertExpressionFromDhis14( values.get( FIELD_NUMERATOR ), dataElementMapping, categoryOptionCombo.getId(), indicator.getName() ) );
        indicator.setDenominator( convertExpressionFromDhis14( values.get( FIELD_DENOMINATOR ), dataElementMapping, categoryOptionCombo.getId(), indicator.getName() ) );

        indicator.setSortOrder( parseInt( values.get( FIELD_SORT_ORDER ) ) );
        indicator.setLastUpdated( Dhis14DateUtil.getDate( values.get( FIELD_LAST_UPDATED ) ) );
        
        importObject( indicator, params );
    }
}
