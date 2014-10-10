package org.hisp.dhis.importexport.dxf.converter;

/*
 * Copyright (c) 2004-2012, University of Oslo, University Of Dar es salaam
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
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

import java.util.Collection;
import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeService;
import org.hisp.dhis.hr.DataType;
import org.hisp.dhis.hr.InputType;
import org.hisp.dhis.importexport.HrExportParams;
import org.hisp.dhis.importexport.ImportHrObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLHrConverter;
import org.hisp.dhis.importexport.analysis.ImportAnalyser;
import org.hisp.dhis.importexport.importer.AttributeImporter;


/**
 * @author John Francis Mukulu <john.f.mukulu@gmail.com>
 * @version $Id$
 */
public class AttributeConverter
    extends AttributeImporter implements XMLHrConverter
{
    public static final String COLLECTION_NAME = "fields";
    public static final String ELEMENT_NAME = "field";
    
    private static final String FIELD_ID = "fieldId";
    private static final String FIELD_NAME = "fieldName";
    private static final String FIELD_INPUTTYPE = "inputTypeId";
    private static final String FIELD_DATATYPE = "dataTypeId";
    private static final String FIELD_CAPTION = "caption";
    private static final String FIELD_COMPULSORY = "compulsory";
    private static final String FIELD_UNIQUEID = "uniqueid";
    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_HISTORY = "fieldHistory";
    
    
    
    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private Map<Object, Integer> dataTypeMapping;
    private Map<Object, Integer> inputTypeMapping;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public AttributeConverter( AttributeService attributeService )
    {
    	this.attributeService = attributeService;
    }
    
    /**
     * Constructor for read operations.
     * 
     * @param batchHandler the batchHandler to use.
     * @param importObjectService the importObjectService to use.
     * @param attributeService the dataElementCategoryService to use.
     */
    public AttributeConverter( BatchHandler<Attribute> batchHandler, 
        ImportHrObjectService importObjectService,
        Map<Object, Integer> dataTypeMapping,
        Map<Object, Integer> inputTypeMapping,
        AttributeService attributeService,
        ImportAnalyser importAnalyser )
    {
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.dataTypeMapping = dataTypeMapping;
        this.inputTypeMapping = inputTypeMapping;
        this.attributeService = attributeService;
        this.importAnalyser = importAnalyser;
    }

    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, HrExportParams params )
    {
        Collection<Attribute> attributes = attributeService.getAllAttribute();
        
        if ( attributes != null && attributes.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );
            
            for ( Attribute attribute : attributes )
            {
                writer.openElement( ELEMENT_NAME );
                
                writer.writeElement( FIELD_ID, String.valueOf( attribute.getId() ) );
                writer.writeElement( FIELD_NAME, attribute.getName() );
                writer.writeElement(FIELD_INPUTTYPE, String.valueOf(attribute.getInputType().getId()));
                writer.writeElement(FIELD_DATATYPE, String.valueOf( attribute.getDataType().getId()) );
                writer.writeElement( FIELD_CAPTION, attribute.getCaption() );
                writer.writeElement( FIELD_COMPULSORY, String.valueOf(attribute.getCompulsory()) );
                writer.writeElement( FIELD_UNIQUEID, String.valueOf(attribute.getIsUnique()) );
                writer.writeElement( FIELD_DESCRIPTION, attribute.getDescription() );
                writer.writeElement( FIELD_HISTORY, String.valueOf(attribute.getHistory()) );
                
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
            
            final Attribute attribute = new Attribute();
            
            final InputType inputType = new InputType();
            attribute.setInputType( inputType );
            
            final DataType dataType = new DataType();
            attribute.setDataType( dataType );
            
            attribute.setId( Integer.parseInt( values.get( FIELD_ID ) ) );
            attribute.setName( values.get( FIELD_NAME ) );
            attribute.setCaption( values.get(FIELD_CAPTION));
            attribute.setDescription( values.get( FIELD_DESCRIPTION ) );
            attribute.setHistory( Boolean.getBoolean( values.get( FIELD_HISTORY ) ) );
            attribute.getInputType().setId( inputTypeMapping.get( Integer.parseInt( values.get( FIELD_INPUTTYPE ) ) ) );
            attribute.getDataType().setId( dataTypeMapping.get( Integer.parseInt( values.get( FIELD_DATATYPE ) ) ) );
            
            importObject( attribute, params );
        }
    }
}
