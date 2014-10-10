package org.hisp.dhis.hrentry.action.dataentry;


import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.HrDataSetService;
import org.hisp.dhis.hr.Person;
import org.hisp.dhis.hr.PersonService;
import org.hisp.dhis.hr.Training;
import org.hisp.dhis.hr.TrainingService;
import com.opensymphony.xwork2.Action;

/**
 * @author Ismail Koleleni
 * @version $Id$
 */

public class GetTrainingAction 
implements Action
{
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private TrainingService trainingService;

    public void setTrainingService( TrainingService trainingService )
    {
        this.trainingService = trainingService;
    }
    
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
    // Input/Output
    // -------------------------------------------------------------------------

    private int id;

    public void setId( int id )
    {
        this.id = id;
    }
    
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

    private Training training;

    public Training getTraining()
    {
        return training;
    }
    
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
    	person = personService.getPerson( personId );
    	hrDataSet =  hrDataSetService.getHrDataSet( hrDataSetId );
    	training = trainingService.getTraining(id);
        return SUCCESS;
    }

}
