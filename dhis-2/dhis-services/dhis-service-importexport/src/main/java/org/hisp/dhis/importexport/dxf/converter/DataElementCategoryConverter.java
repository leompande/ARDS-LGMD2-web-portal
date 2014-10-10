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

import static org.hisp.dhis.importexport.dxf.converter.DXFConverter.MINOR_VERSION_12;

import java.util.Collection;
import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.concept.Concept;
import org.hisp.dhis.concept.ConceptService;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.importer.DataElementCategoryImporter;
import org.hisp.dhis.importexport.mapping.NameMappingUtil;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataElementCategoryConverter
    extends DataElementCategoryImporter
    implements XMLConverter
{
    public static final String COLLECTION_NAME = "categories";
    public static final String ELEMENT_NAME = "category";

    private static final String FIELD_ID = "id";
    private static final String FIELD_UID = "uid";
    private static final String FIELD_CODE = "code";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_CONCEPT_ID = "conceptid";
    
    private static final String BLANK = "";

    private ConceptService conceptService;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public DataElementCategoryConverter( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    /**
     * Constructor for read operations.
     * 
     * @param batchHandler the batchHandler to use.
     * @param importObjectService the importObjectService to use.
     * @param categoryService the dataElementCategoryService to use.
     * @param conceptService the ConceptService to use.
     */
    public DataElementCategoryConverter( BatchHandler<DataElementCategory> batchHandler,
        ImportObjectService importObjectService, DataElementCategoryService categoryService,
        ConceptService conceptService )
    {
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.categoryService = categoryService;
        this.conceptService = conceptService;
    }

    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<DataElementCategory> categories = categoryService.getDataElementCategories( params.getCategories() );

        if ( categories != null && categories.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );

            for ( DataElementCategory category : categories )
            {
                writer.openElement( ELEMENT_NAME );

                writer.writeElement( FIELD_ID, String.valueOf( category.getId() ) );
                writer.writeElement( FIELD_UID, category.getUid() );
                writer.writeElement( FIELD_CODE, category.getCode() );
                writer.writeElement( FIELD_NAME, category.getName() );
                writer.writeElement( FIELD_CONCEPT_ID, String.valueOf( category.getConcept() == null ? BLANK : category
                    .getConcept().getId() ) );

                writer.closeElement();
            }

            writer.closeElement();
        }
    }

    public void read( XMLReader reader, ImportParams params )
    {
        Map<Object, String> conceptMap = NameMappingUtil.getConceptMap();

        Concept defaultConcept = conceptService.getConceptByName( Concept.DEFAULT_CONCEPT_NAME );

        while ( reader.moveToStartElement( ELEMENT_NAME, COLLECTION_NAME ) )
        {
            final Map<String, String> values = reader.readElements( ELEMENT_NAME );

            final DataElementCategory category = new DataElementCategory();

            category.setId( Integer.parseInt( values.get( FIELD_ID ) ) );
            
            if (params.minorVersionGreaterOrEqual( "1.3") ) {
                category.setUid( values.get( FIELD_UID) );
                category.setCode( values.get( FIELD_CODE) );
            }

            category.setName( values.get( FIELD_NAME ) );

            if ( params.minorVersionGreaterOrEqual( MINOR_VERSION_12 ) && values.get( FIELD_CONCEPT_ID ) != null )
            {
                log.debug( "reading category dxf version >1.2" );
                int conceptid = Integer.parseInt( values.get( FIELD_CONCEPT_ID ) );
                String conceptName = conceptMap.get( conceptid );
                category.setConcept( conceptService.getConceptByName( conceptName ) );
            }
            else
            {
                log.debug( "reading category dxf version 1.0" );
                category.setConcept( defaultConcept );
            }

            importObject( category, params );
        }
    }
}
