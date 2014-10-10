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
import org.hisp.dhis.hr.DataValues;
import org.hisp.dhis.hr.AttributeService;
import org.hisp.dhis.hr.DataValuesService;
import org.hisp.dhis.hr.Person;
import org.hisp.dhis.importexport.HrExportParams;
import org.hisp.dhis.importexport.ImportHrObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLHrConverter;
import org.hisp.dhis.importexport.importer.DataValuesImporter;
import org.hisp.dhis.system.util.DateUtils;


/**
 * @author John Francis Mukulu <john.f.mukulu@gmail.com>
 * @version $Id$
 */
public class DataValuesConverter
    extends DataValuesImporter implements XMLHrConverter
{
    public static final String COLLECTION_NAME = "records";
    public static final String ELEMENT_NAME = "record";
    
    private static final String FIELD_DATAVALUESID = "id";
    private static final String FIELD_VALUE = "value";
    private static final String FIELD_STOREDBY = "username";
    private static final String FIELD_TIMESTAMP = "inputdate";
    private static final String FIELD_PERSONID = "personId";
    private static final String FIELD_ATTRIBUTEID = "fieldid";
    
    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private Map<Object, Integer> personMapping;
    private Map<Object, Integer> attributeMapping;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public DataValuesConverter( DataValuesService dataValuesService )
    {
    	this.dataValuesService = dataValuesService;
    }
    
    /**
     * Constructor for read operations.
     * 
     * @param batchHandler the batchHandler to use.
     * @param importObjectService the importObjectService to use.
     * @param categoryService the dataElementCategoryService to use.
     */
    public DataValuesConverter( BatchHandler<DataValues> batchHandler, 
        ImportHrObjectService importObjectService,
        Map<Object, Integer> personMapping,
        Map<Object, Integer> attributeMapping,
        DataValuesService dataValuesService )
    {
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.personMapping = personMapping;
        this.attributeMapping = attributeMapping;
        this.dataValuesService = dataValuesService;
    }
    
    private AttributeService attributeService;
    
    public void setAttributeService(AttributeService attributeService )
    {
    	this.attributeService = attributeService;
    }
    

    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, HrExportParams params )
    {
        Collection<DataValues> dataValues = dataValuesService.getAllDataValues();
        
        if ( dataValues != null && dataValues.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );
            
            for ( DataValues dataValue : dataValues )
            {
                writer.openElement( ELEMENT_NAME );
                
                writer.writeElement( FIELD_DATAVALUESID, String.valueOf( dataValue.getId() ) );
                writer.writeElement( FIELD_VALUE, dataValue.getValue() );
                writer.writeElement( FIELD_STOREDBY, dataValue.getStoredBy() );
                writer.writeElement( FIELD_TIMESTAMP, DateUtils.getMediumDateString( dataValue.getTimestamp(), EMPTY ) );
                writer.writeElement(FIELD_PERSONID, String.valueOf( dataValue.getPerson().getId()) );
                writer.writeElement(FIELD_ATTRIBUTEID, String.valueOf(dataValue.getAttribute().getId()));
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
            
            final DataValues dataValues = new DataValues();
            
            final Person person = new Person();
            dataValues.setPerson(person);
            
            final Attribute attribute = new Attribute();
            dataValues.setAttribute( attribute );
            
            dataValues.setId( Integer.parseInt( values.get( FIELD_DATAVALUESID ) ) );
            // See if the attribute has options, if it does get attribute option name 
            // from export file by id, the get id of the attribute option with same name
            
            dataValues.setValue( values.get( FIELD_VALUE ) );
            dataValues.setStoredBy( values.get( FIELD_STOREDBY ) );
            dataValues.setTimestamp( DateUtils.getMediumDate( values.get( FIELD_TIMESTAMP ) ) );
            dataValues.getPerson().setId( personMapping.get( Integer.parseInt( values.get( FIELD_PERSONID ) ) ) );
            dataValues.getAttribute().setId( attributeMapping.get( Integer.parseInt( values.get( FIELD_ATTRIBUTEID ) ) ) );
            
            // Get Attribute type of data value
            /*
            Attribute equivalenceAttribute = new Attribute();
            equivalenceAttribute.setId(attributeMapping.get(Integer.parseInt( values.get(FIELD_ATTRIBUTEID))));
            
            if( attributeService.getAttribute( attributeMapping.get(Integer.parseInt(values.get(FIELD_ATTRIBUTEID))) ).getInputType().getName() == "combo" )
            {
            	dataValues.setValue( String.valueOf(  attributeOptionsMapping.get( Integer.parseInt( values.get( FIELD_VALUE ) ) ) )  );
            }
            else {
            	dataValues.setValue( values.get( FIELD_VALUE ) );
            }
            */
            
            importObject( dataValues, params );
            
            
        }
    }
}
