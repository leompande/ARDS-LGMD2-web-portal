package org.hisp.dhis.hrentry.action.history;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.HrDataSetService;
import org.hisp.dhis.hr.Person;
import org.hisp.dhis.hr.PersonService;
import org.hisp.dhis.hr.History;

import com.opensymphony.xwork2.Action;

/**
* @author Ismail Yusuf Koleleni
 * @version $Id$
 */

public class PersonHistory
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private PersonService personService;

    public void setPersonService( PersonService personService )
    {
        this.personService = personService;
    }
    
    private HrDataSetService hrDataSetService;

    public void setHrDataSetService( HrDataSetService hrDataSetService )
    {
        this.hrDataSetService = hrDataSetService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
    
    private int personId;

    public void setPersonId( int personId )
    {
        this.personId = personId;
    }
    
    private int hrDataSetId;

    public void setHrDataSetId( int hrDataSetId )
    {
        this.hrDataSetId = hrDataSetId;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Set<History> histories;

    public Set<History> getHistories()
    {
        return histories;
    }
    
    private Collection<Attribute> attributes;
    
    public Collection<Attribute> getAttributes()
    {
    	return attributes;
    }
    
    private HrDataSet hrDataSet;
    
    public HrDataSet getHrDataSet()
    {
    	return hrDataSet;    	
    }
    
    private Person person;
    
    public Person getPerson()
    {
    	return person;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        person = personService.getPerson( personId );
        
        if ( person != null )
        {
        	hrDataSet =  hrDataSetService.getHrDataSet( hrDataSetId );
        	attributes = hrDataSet.getAttribute();
        	if ( hrDataSet != null )
        	{
        		//Collection<Attribute> attributes = hrDataSetService.getAttributes( hrDataSet );
        	}
        	// -----------------------------------------------------------------
            // Load Histories for Selected Person
            // -----------------------------------------------------------------
        	
        	histories = person.getHistory(); 
            
        }

        return SUCCESS;
    }
}
