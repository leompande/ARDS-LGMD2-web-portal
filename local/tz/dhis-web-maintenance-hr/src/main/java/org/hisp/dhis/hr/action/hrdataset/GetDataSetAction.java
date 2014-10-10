package org.hisp.dhis.hr.action.hrdataset;

/*
 * Copyright (c) 2004-2012, University of Oslo, University Of Dar-es-salaam
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataentryform.DataEntryFormService;
import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeService;
import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.HrDataSetService;
import org.hisp.dhis.hr.Person;
import org.hisp.dhis.hr.PersonService;
import org.hisp.dhis.options.displayproperty.DisplayPropertyHandler;

import com.opensymphony.xwork2.Action;

/**
 * @author John Francis Mukulu <john.f.mukulu@gmail.com>
 *
 * GetDataSetAction.java  Nov 14, 2010 10:08:41 PM
 */
public class GetDataSetAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private HrDataSetService hrDataSetService;

    public void setHrDataSetService( HrDataSetService hrDataSetService )
    {
        this.hrDataSetService = hrDataSetService;
    }

    private DisplayPropertyHandler displayPropertyHandler;

    public void setDisplayPropertyHandler( DisplayPropertyHandler displayPropertyHandler )
    {
        this.displayPropertyHandler = displayPropertyHandler;
    }

    private Comparator<Attribute> attributeComparator;

    public void setAttributeComparator( Comparator<Attribute> attributeComparator )
    {
        this.attributeComparator = attributeComparator;
    }

    private DataEntryFormService dataEntryFormService;

    public void setDataEntryFormService( DataEntryFormService dataEntryFormService )
    {
        this.dataEntryFormService = dataEntryFormService;
    }
    
    private PersonService personService;
    
    public void setPersonService( PersonService personService )
    {
    	this.personService = personService;
    }
    
    private AttributeService attributeService;
    
    public void setAttributeService( AttributeService attributeService )
    {
    	this.attributeService = attributeService;
    }

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    private int hrDataSetId;

    public int getHrDataSetId()
    {
        return hrDataSetId;
    }

    public void setHrDataSetId( int hrDataSetId )
    {
        this.hrDataSetId = hrDataSetId;
    }

    private HrDataSet hrDataSet;

    public HrDataSet getHrDataSet()
    {
        return hrDataSet;
    }

    private Collection<Attribute> attributes;

    public Collection<Attribute> getAttributes()
    {
        return attributes;
    }
    
    private List<Person> persons;
    
    public List<Person> getpersons() {
    	return persons;
    }

    private DataEntryForm dataEntryForm;
    
    public DataEntryForm getDataEntryForm()
    {
        return dataEntryForm;
    }
    
    private DataElementCategoryCombo categoryCombo;

    public DataElementCategoryCombo getCategoryCombo()
    {
    	return categoryCombo;
    }

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        hrDataSet = hrDataSetService.getHrDataSet( hrDataSetId );
        
        attributes = new ArrayList<Attribute>( hrDataSet.getAttribute() );
        
        // Get List of all persons from database
        Collection<Person> allPersons = new ArrayList<Person>( personService.getAllPerson() );
        // Go through all persons and capture persons in the current HrDataset
        Iterator<Person> personIterator = allPersons.iterator();
        persons = new ArrayList<Person>();
        while( personIterator.hasNext() ) {
        	Person currentPerson = personIterator.next();
        	if( currentPerson.getDataset() == hrDataSet ) {
        		persons.add(currentPerson);
        	}
        }
        
        //Collections.sort( hrDataSetAttributes, attributeComparator );
        
        //displayPropertyHandler.handle( hrDataSetAttributes );
                	
        dataEntryForm = dataEntryFormService.getDataEntryForm(hrDataSetId);
                
        return SUCCESS;
    }
}
