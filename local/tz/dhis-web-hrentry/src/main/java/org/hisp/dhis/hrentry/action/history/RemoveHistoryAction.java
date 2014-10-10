package org.hisp.dhis.hrentry.action.history;


import org.hisp.dhis.hr.History;
import org.hisp.dhis.hr.HistoryService;
import com.opensymphony.xwork2.Action;

/**
 * @author Ismail Koleleni
 * @version $Id$
 */

public class RemoveHistoryAction implements Action
{
	 // -------------------------------------------------------------------------
   // Dependencies
   // -------------------------------------------------------------------------

   private HistoryService historyService;

   public void setHistoryService( HistoryService historyService )
   {
       this.historyService = historyService;
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
	   History history = historyService.getHistory( id );
	   
	   historyService.deleteHistory( history );
       
       return SUCCESS;
   }
}