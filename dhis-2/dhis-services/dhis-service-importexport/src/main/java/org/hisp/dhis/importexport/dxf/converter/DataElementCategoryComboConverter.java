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
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.importer.DataElementCategoryComboImporter;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataElementCategoryComboConverter
    extends DataElementCategoryComboImporter implements XMLConverter
{
    public static final String COLLECTION_NAME = "categoryCombos";
    public static final String ELEMENT_NAME = "categoryCombo";
    
    private static final String FIELD_ID = "id";
    private static final String FIELD_UID = "uid";
    private static final String FIELD_CODE = "code";
    private static final String FIELD_NAME = "name";

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public DataElementCategoryComboConverter( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }
    
    /**
     * Constructor for read operations.
     * 
     * @param batchHandler the batchHandler to use.
     * @param importObjectService the importObjectService to use.
     * @param categoryComboService the dataElementCategoryComboService to use.
     */
    public DataElementCategoryComboConverter( BatchHandler<DataElementCategoryCombo> batchHandler, 
        ImportObjectService importObjectService,
        DataElementCategoryService categoryService )
    {
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.categoryService = categoryService;
    }
    

    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<DataElementCategoryCombo> categoryCombos = categoryService.getDataElementCategoryCombos( params.getCategoryCombos() );
        
        if ( categoryCombos != null && categoryCombos.size() > 0)
        {
            writer.openElement( COLLECTION_NAME );
            
            for ( DataElementCategoryCombo categoryCombo : categoryCombos )
            {
                writer.openElement( ELEMENT_NAME );
                
                writer.writeElement( FIELD_ID, String.valueOf( categoryCombo.getId() ) );
                writer.writeElement( FIELD_UID,  categoryCombo.getUid() );
                writer.writeElement( FIELD_CODE, categoryCombo.getCode() );
                writer.writeElement( FIELD_NAME, categoryCombo.getName() );
                
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
            
            final DataElementCategoryCombo categoryCombo = new DataElementCategoryCombo();
            
            categoryCombo.setId( Integer.parseInt( values.get( FIELD_ID ) ) );
            
            if (params.minorVersionGreaterOrEqual( "1.3") )
            {
                categoryCombo.setUid( values.get( FIELD_UID) );
                categoryCombo.setCode( values.get( FIELD_CODE) );
            }
            
            categoryCombo.setName( values.get( FIELD_NAME ) );
            
            importObject( categoryCombo, params );
        }
    }
}
