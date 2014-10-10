package org.hisp.dhis.hrentry.action.dataentry;

import java.util.Collection;

import org.hisp.dhis.hr.DataValues;
import org.hisp.dhis.hr.DataValuesService;
import org.hisp.dhis.hr.Person;
import org.hisp.dhis.hr.PersonService;

import com.opensymphony.xwork2.Action;

public class RemovePersonAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PersonService personService;

    public void setPersonService( PersonService personService )
    {
        this.personService = personService;
    }
    
    private DataValuesService dataValuesService;
    
    public void setDataValuesService( DataValuesService dataValuesService )
    {
    	this.dataValuesService = dataValuesService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int id;

    public void setId( int id )
    {
        this.id = id;
    }

    public String execute()
        throws Exception
    {
        Person person = personService.getPerson( id );
        
        Collection<DataValues> dataValues = dataValuesService.getDataValuesByPerson(person);
        
        for( DataValues dataValue : dataValues )
        {
        	dataValuesService.deleteDataValues(dataValue);
        }
        personService.deletePerson( person );

        return SUCCESS;
    }
}

