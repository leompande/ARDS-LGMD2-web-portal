package org.hisp.dhis.hrentry.action.dataentry;


import org.hisp.dhis.hr.Training;
import org.hisp.dhis.hr.TrainingService;
import com.opensymphony.xwork2.Action;

/**
 * @author Ismail Koleleni
 * @version $Id$
 */

public class RemoveTrainingAction implements Action
{
	 // -------------------------------------------------------------------------
   // Dependencies
   // -------------------------------------------------------------------------

   private TrainingService trainingService;

   public void setTrainingService( TrainingService trainingService )
   {
       this.trainingService = trainingService;
   }
      

   // -------------------------------------------------------------------------
   // Input/Output
   // -------------------------------------------------------------------------

   private int id;

   public void setId( int id )
   {
       this.id = id;
   }
   

   // -------------------------------------------------------------------------
   // Action implementation
   // -------------------------------------------------------------------------

   public String execute()
       throws Exception
   {
	   Training training = trainingService.getTraining( id );
	   
	   trainingService.deleteTraining( training );
       
       return SUCCESS;
   }
}