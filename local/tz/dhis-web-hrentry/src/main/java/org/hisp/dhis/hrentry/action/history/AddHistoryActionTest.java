package org.hisp.dhis.hrentry.action.history;

import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeOptionsService;
import org.hisp.dhis.hr.AttributeService;
import org.hisp.dhis.hr.History;
import org.hisp.dhis.hr.HistoryService;
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

public class AddHistoryActionTest implements Action
{
	 // -------------------------------------------------------------------------
   // Dependencies
   // -------------------------------------------------------------------------

   private HistoryService historyService;

   public void setHistoryService( HistoryService historyService )
   {
       this.historyService = historyService;
   }
   
   private AttributeService attributeService;
   
   public void setAttributeService ( AttributeService attributeService )
   {
	   this.attributeService = attributeService;
   }
   
   private AttributeOptionsService attributeOptionsService;
   
   public void setAttributeOptionsService ( AttributeOptionsService attributeOptionsService )
   {
	   this.attributeOptionsService = attributeOptionsService;
   }
   
   private PersonService personService;

   public void setPersonService( PersonService personService )
   {
       this.personService = personService;
   }
      

   // -------------------------------------------------------------------------
   // Input/Output
   // -------------------------------------------------------------------------
   
   private int historyType;
   
   public void setHistoryType( int historyType)
   {
	   this.historyType = historyType;
   }
   
   private Attribute attribute;
   
   public void setAttribute ( Attribute attribute )
   {
	   this.attribute = attribute;
   }
   
   private String history;
   
   public void setHistory( String history)
   {
	   this.history = history;
   }
   
   private String reason;
   
   public void setReason ( String reason)
   {
	   this.reason = reason;
   }
   
   private Date startDate;

   public void setStartDate( Date startDate )
   {
       this.startDate = startDate;
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
	   String historyIn;
	   person = personService.getPerson( personId );
	   attribute = attributeService.getAttribute(historyType);
	   History addHistory = new History();       
	   if ( attribute.getInputType().getName() == "combo" )
	   {
		   historyIn = attribute.getCaption();
	   }
	   else{
		   historyIn = "Haijapita!";
	   }
	   addHistory.setAttribute( attribute );
	   addHistory.setPerson( person );
	   addHistory.setStartDate( startDate );
	   addHistory.setReason( reason );
	   addHistory.setHistory( historyIn );
       
       historyService.saveHistory( addHistory );       
       
       return SUCCESS;
   }
}