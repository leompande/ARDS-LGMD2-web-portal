package org.hisp.dhis.hrentry.action.dataentry;

import org.hisp.dhis.hr.Person;
import org.hisp.dhis.hr.PersonService;
import org.hisp.dhis.hr.Training;
import org.hisp.dhis.hr.TrainingService;
import java.util.Date;
import com.opensymphony.xwork2.Action;

/**
 * @author Ismail Koleleni
 * @version $Id$
 */

public class UpdateTrainingAction implements Action
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
      

   // -------------------------------------------------------------------------
   // Input/Output
   // -------------------------------------------------------------------------
   private int id;

   public void setId( int id )
   {
       this.id = id;
   }
   
   private String courseName;

   public void setCourseName( String courseName )
   {
       this.courseName = courseName;
   }
   
   private String courseLocation;

   public void setCourseLocation( String courseLocation )
   {
       this.courseLocation = courseLocation;
   }
   
   private String courseSponsor;

   public void setCourseSponsor( String courseSponsor )
   {
       this.courseSponsor = courseSponsor;
   }
   
   private Date startDate;

   public void setStartDate( Date startDate )
   {
       this.startDate = startDate;
   }
   
   private Date endDate;

   public void setEndDate( Date endDate )
   {
       this.endDate = endDate;
   }
   
   private Person person;
   
   public void setPerson ( Person person)
   {
	   this.person = person;
   }
   
   private int personId;

   public void setPersonId( int personId )
   {
       this.personId = personId;
   }
   

   // -------------------------------------------------------------------------
   // Action implementation
   // -------------------------------------------------------------------------

   public String execute()
       throws Exception
   {
	   person = personService.getPerson( personId );
	   Training training = trainingService.getTraining(id);       

       training.setName( courseName );
       training.setLocation( courseLocation );
       training.setSponsor( courseSponsor );
       training.setStartDate( startDate );
       training.setEndDate( endDate );
       training.setPerson( person );
       
       trainingService.updateTraining( training );
       
       
       return SUCCESS;
   }
}