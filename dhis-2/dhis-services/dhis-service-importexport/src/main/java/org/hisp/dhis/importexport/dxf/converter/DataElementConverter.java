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
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.analysis.ImportAnalyser;
import org.hisp.dhis.importexport.importer.DataElementImporter;
import org.hisp.dhis.system.util.DateUtils;

/**
 * @author Lars Helge Overland
 * @version $Id: DataElementConverter.java 6455 2008-11-24 08:59:37Z larshelg $
 */
public class DataElementConverter
    extends DataElementImporter implements XMLConverter
{
    public static final String COLLECTION_NAME = "dataElements";
    public static final String ELEMENT_NAME = "dataElement";

    private static final String FIELD_ID = "id";
    private static final String FIELD_UID = "uid";
    private static final String FIELD_CODE = "code";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_SHORT_NAME = "shortName";
    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_ACTIVE = "active";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_DOMAIN_TYPE = "domainType";
    private static final String FIELD_AGGREGATION_OPERATOR = "aggregationOperator";
    private static final String FIELD_CATEGORY_COMBO = "categoryCombo";
    private static final String FIELD_LAST_UPDATED = "lastUpdated";

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private Map<Object, Integer> categoryComboMapping;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public DataElementConverter( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    /**
     * Constructor for read operations.
     * 
     * @param batchHandler the batchHandler to use.
     * @param importObjectService the importObjectService to use.
     * @param categoryComboMapping the categoryComboMapping to use.
     * @param dataElementService the dataElementService to use.
     */
    public DataElementConverter( BatchHandler<DataElement> batchHandler, 
        ImportObjectService importObjectService,
        Map<Object, Integer> categoryComboMapping,
        DataElementService dataElementService,
        ImportAnalyser importAnalyser )
    {        
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.categoryComboMapping = categoryComboMapping;
        this.dataElementService = dataElementService;
        this.importAnalyser = importAnalyser;
    }

    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<DataElement> elements = dataElementService.getDataElements( params.getDataElements() );
        
        if ( elements != null && elements.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );
            
            for ( DataElement element : elements )
            {
                writer.openElement( ELEMENT_NAME );
                
                writer.writeElement( FIELD_ID, String.valueOf( element.getId() ) );
                writer.writeElement( FIELD_UID, element.getUid() );
                writer.writeElement( FIELD_NAME, element.getName() );
                writer.writeElement( FIELD_SHORT_NAME, element.getShortName() );
                writer.writeElement( FIELD_CODE, element.getCode() ); // historic positioning from v1.2
                writer.writeElement( FIELD_DESCRIPTION, element.getDescription() );
                writer.writeElement( FIELD_ACTIVE, String.valueOf( element.isActive() ) );
                writer.writeElement( FIELD_TYPE, element.getType() );
                writer.writeElement( FIELD_DOMAIN_TYPE, element.getDomainType() );
                writer.writeElement( FIELD_AGGREGATION_OPERATOR, element.getAggregationOperator() );
                writer.writeElement( FIELD_CATEGORY_COMBO, String.valueOf( element.getCategoryCombo().getId() ) );
                writer.writeElement( FIELD_LAST_UPDATED, DateUtils.getMediumDateString( element.getLastUpdated(), EMPTY ) );
                
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
            
            final DataElement element = new DataElement();
            
            final DataElementCategoryCombo categoryCombo = new DataElementCategoryCombo();
            element.setCategoryCombo( categoryCombo );
            
            element.setId( Integer.parseInt( values.get( FIELD_ID ) ) );
            
            if ( params.minorVersionGreaterOrEqual( "1.2" ))
            {
                element.setUid( values.get( FIELD_UID ) );
            }

            element.setName( values.get( FIELD_NAME ) );
            element.setShortName( values.get( FIELD_SHORT_NAME ) );
            
            if ( params.minorVersionGreaterOrEqual( "1.2" )) {
              element.setCode( values.get( FIELD_CODE ) );                
            }
            
            element.setDescription( values.get( FIELD_DESCRIPTION ) );
            element.setActive( Boolean.parseBoolean( values.get( FIELD_ACTIVE ) ) );
            element.setType( values.get( FIELD_TYPE ) );
            element.setDomainType( values.get( FIELD_DOMAIN_TYPE ) );
            element.setAggregationOperator( values.get( FIELD_AGGREGATION_OPERATOR ) );
            element.getCategoryCombo().setId( categoryComboMapping.get( Integer.parseInt( values.get( FIELD_CATEGORY_COMBO ) ) ) );
            element.setLastUpdated( DateUtils.getMediumDate( values.get( FIELD_LAST_UPDATED ) ) );            
            
            importObject( element, params );
        }
    }
}
