package org.hisp.dhis.hrentry.action.dataentry;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.HrDataSetService;
import org.hisp.dhis.hr.Person;
import org.hisp.dhis.hr.PersonService;
import org.hisp.dhis.hr.Training;

import com.opensymphony.xwork2.Action;

/**
* @author Ismail Yusuf Koleleni
 * @version $Id$
 */

public class PersonTraining
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

    private Collection<Training> trainings = new ArrayList<Training>();

    public Collection<Training> getTrainings()
    {
        return trainings;
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
        	
        	// -----------------------------------------------------------------
            // Load Training for Selected Person
            // -----------------------------------------------------------------
        	
        	trainings = person.getTraining(); 
            
        }

        return SUCCESS;
    }
}
