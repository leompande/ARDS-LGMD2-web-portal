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
import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.InputType;
import org.hisp.dhis.hr.Person;
import org.hisp.dhis.hr.PersonService;
import org.hisp.dhis.importexport.HrExportParams;
import org.hisp.dhis.importexport.ImportHrObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLHrConverter;
import org.hisp.dhis.importexport.importer.PersonImporter;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.system.util.DateUtils;


/**
 * @author John Francis Mukulu <john.f.mukulu@gmail.com>
 * @version $Id$
 */
public class PersonConverter
    extends PersonImporter implements XMLHrConverter
{
    public static final String COLLECTION_NAME = "persons";
    public static final String ELEMENT_NAME = "person";
    
    private static final String FIELD_ID = "id";
    private static final String FIELD_FIRSTNAME = "firstName";
    private static final String FIELD_MIDDLENAME = "middleName";
    private static final String FIELD_LASTNAME = "lastName";
    private static final String FIELD_BIRTHDATE = "birthdate";
    private static final String FIELD_GENDER = "gender";
    private static final String FIELD_NATIONALITY ="nationality";
    private static final String FIELD_HRDATASET = "formId";
    private static final String FIELD_ORGANISATIONUNIT ="organisationUnitId";
    private static final String FIELD_INSTANCE = "instance";
    
    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private Map<Object, Integer> hrDataSetMapping;
    private Map<Object, Integer> organisationUnitMapping;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public PersonConverter( PersonService personService )
    {
    	this.personService = personService;
    }
    
    /**
     * Constructor for read operations.
     * 
     * @param batchHandler the batchHandler to use.
     * @param importObjectService the importObjectService to use.
     * @param categoryService the dataElementCategoryService to use.
     */
    public PersonConverter( BatchHandler<Person> batchHandler, 
        ImportHrObjectService importObjectService,
        Map<Object, Integer> hrDataSetMapping,
        Map<Object, Integer> organisationUnitMapping,
        PersonService personService )
    {
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.hrDataSetMapping = hrDataSetMapping;
        this.organisationUnitMapping = organisationUnitMapping;
        this.personService = personService;
    }

    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, HrExportParams params )
    {
        Collection<Person> persons = personService.getAllPerson();
        
        if ( persons != null && persons.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );
            
            for ( Person person : persons )
            {
                writer.openElement( ELEMENT_NAME );
                
                writer.writeElement( FIELD_ID, String.valueOf( person.getId() ) );
                writer.writeElement( FIELD_FIRSTNAME, person.getFirstName() );
                writer.writeElement( FIELD_MIDDLENAME, person.getMiddleName() );
                writer.writeElement( FIELD_LASTNAME, String.valueOf(person.getLastName()) );
                writer.writeElement(FIELD_BIRTHDATE, DateUtils.getMediumDateString( person.getBirthDate(), EMPTY ) );
                writer.writeElement(FIELD_GENDER, person.getGender() );
                writer.writeElement(FIELD_NATIONALITY, person.getNationality() );
                writer.writeElement(FIELD_HRDATASET, String.valueOf( person.getDataset().getId()) );
                writer.writeElement(FIELD_ORGANISATIONUNIT, String.valueOf( person.getOrganisationUnit().getId()) );
                writer.writeElement( FIELD_INSTANCE, person.getInstance() );
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
            
            final Person person = new Person();
            
            final HrDataSet hrDataSet = new HrDataSet();
            person.setDataset( hrDataSet );
            
            final OrganisationUnit organisationUnit = new OrganisationUnit();
            person.setOrganisationUnit( organisationUnit );
            
            person.setId( Integer.parseInt( values.get( FIELD_ID ) ) );
            person.setFirstName( values.get( FIELD_FIRSTNAME ) );
            person.setMiddleName( values.get( FIELD_MIDDLENAME ) );
            person.setLastName( values.get( FIELD_LASTNAME ) );
            person.setBirthDate( DateUtils.getMediumDate( values.get( FIELD_BIRTHDATE ) ) );
            person.setGender( values.get(FIELD_GENDER) );
            person.setNationality( values.get( FIELD_NATIONALITY ) );
            person.getDataset().setId( hrDataSetMapping.get( Integer.parseInt( values.get( FIELD_HRDATASET ) ) ) );
            person.getOrganisationUnit().setId( organisationUnitMapping.get( Integer.parseInt( values.get( FIELD_ORGANISATIONUNIT ) ) ) );
            person.setInstance( values.get( FIELD_INSTANCE ) );
            importObject( person, params );
        }
    }
}
