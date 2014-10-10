package org.hisp.dhis.hr.action.person;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.hr.Person;
import org.hisp.dhis.hr.PersonService;

import com.opensymphony.xwork2.Action;

public class GetHrPersonListAction 
implements Action
{
	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	
	private PersonService personService;
	
	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------
	private Collection<Person> persons = new ArrayList<Person>();
	private Collection<Person> getPersons() {
		return persons;
	}

    
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
    	persons = personService.getAllPerson();
    	
        return SUCCESS;
    }
}
