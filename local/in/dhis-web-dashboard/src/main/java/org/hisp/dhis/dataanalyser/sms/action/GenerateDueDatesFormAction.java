package org.hisp.dhis.dataanalyser.sms.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.hisp.dhis.organisationunit.OrganisationUnit;

import org.hisp.dhis.period.PeriodService;

import com.opensymphony.xwork2.Action;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.hisp.dhis.dataanalyser.util.BulkSMSHttpInterface;
import org.hisp.dhis.dataanalyser.util.DashBoardService;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageService;

public class GenerateDueDatesFormAction implements Action
{
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------    
    
    private PeriodService periodService;

    private int orgId;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    public SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }
    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
private Collection<Program> allPrograms;

    public Collection<Program> getAllPrograms()
    {
        return allPrograms;
    }
   private String result="";

    public String getResult()
    {
        return result;
    }

   
private String selectDates;

    public void setSelectDates( String selectDates )
    {
        this.selectDates = selectDates;
    }
    private String programName;

    public void setProgramName( String programName )
    {
        this.programName = programName;
    }
    
    private DashBoardService dashBoardService;

    public void setDashBoardService( DashBoardService dashBoardService )
    {
        this.dashBoardService = dashBoardService;
    }
    
    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }
    
    public String execute() throws Exception
    {
       
        Collection<Collection<String>> messgs=new ArrayList<Collection<String>>();
      System.out.println("date="+selectDates+"prg name="+programName);
        
      if (programName==null){
      programName="1";
      }  
      if (selectDates==null){
      selectDates="today";
      }
        allPrograms = programService.getAllPrograms();
        
        Collection<HashMap> dueDates = null;
       String str,phoneNumber,startDate,endDate="2011-03-29";
       Calendar cal=Calendar.getInstance();
      SimpleDateFormat dateFormat=new SimpleDateFormat( "yy-MM-dd"); 
       startDate=dateFormat.format( cal.getTime());
       
       if (selectDates.equalsIgnoreCase( "today") ){
       endDate=startDate;
       System.out.println("1start date="+startDate+"end="+endDate);
       }else if (selectDates.equalsIgnoreCase( "tomorrow") ){
       cal.add( Calendar.DAY_OF_MONTH, 1);
       endDate=dateFormat.format( cal.getTime());
      System.out.println("2start date="+startDate+"end="+endDate);
       }else if (selectDates.equalsIgnoreCase( "nweek") ){
       cal.add( Calendar.DAY_OF_MONTH, 7);
       endDate=dateFormat.format( cal.getTime());
     System.out.println("3start date="+startDate+"end="+endDate);  }
       else if (selectDates.equalsIgnoreCase( "nmonth") ){
       cal.add( Calendar.MONTH, 1);
       endDate=dateFormat.format( cal.getTime());
       System.out.println("4start date="+startDate+"end="+endDate);}
       
       System.out.println("start date="+startDate+"end="+endDate);
       
       
       OrganisationUnit next;
       Collection<OrganisationUnit> selectedOrganisationUnits = selectionTreeManager.getSelectedOrganisationUnits();
        Iterator<OrganisationUnit> iterator = selectedOrganisationUnits.iterator();
      
        while(iterator.hasNext()){
            next = iterator.next();
       orgId=next.getId();
            phoneNumber = next.getPhoneNumber();
      // System.out.println("orgid="+orgId);
          
            
       dueDates=dashBoardService.getDueDates( Integer.parseInt( programName), orgId, startDate,endDate);
      if (!dueDates.isEmpty()){
          Collection<String> dDatesForOrg = new ArrayList<String>();  
          dDatesForOrg.add( ""+orgId);
          dDatesForOrg.add( phoneNumber);
          Iterator<HashMap> iteratorDueD = dueDates.iterator();
          while(iteratorDueD.hasNext()){
                    HashMap dueD = iteratorDueD.next();
                    int programStageId=Integer.parseInt( dueD.get( "id").toString());
                    ProgramStage programStageTemp = programStageService.getProgramStage( programStageId);
                    String prgmStageNameTemp = programStageTemp.getName();
                    str=dueD.get( "name")+","+dueD.get( "date") +","+prgmStageNameTemp;
       
              dDatesForOrg.add( str );
       //System.out.println(str);
          }
                messgs.add( dDatesForOrg );                        
          
       }
     
       }
       
        sendSms(messgs);
        
        
       result="";
        return SUCCESS;
    }

    private void sendSms( Collection<Collection<String>> messgs ) throws IOException
    {
        String phoneNumber,orgId,message = "",res;
        int countNoOfMesg=0;
        BulkSMSHttpInterface bulkSmsHttpInterface =new BulkSMSHttpInterface();
        
        
        Iterator<Collection<String>> iterator = messgs.iterator();
   while(iterator.hasNext()){
            Collection<String> messgForAnOrgUnit = iterator.next();
            Iterator<String> iteratorMsgOrg = messgForAnOrgUnit.iterator();
            orgId=iteratorMsgOrg.next();
            phoneNumber=iteratorMsgOrg.next();
            System.out.println("messages for orgunit="+orgId+"with phone="+phoneNumber);
            countNoOfMesg=0;
            while(iteratorMsgOrg.hasNext()){
                String next = iteratorMsgOrg.next();
                if (message.length()+next.length()>=160 ){
                countNoOfMesg++;
                System.out.println("message"+countNoOfMesg+"="+message);
            res = bulkSmsHttpInterface.sendMessage( message, phoneNumber );
            System.out.println(res);
            
                message=next;
                }else{
                message+=";"+next;
                }
            }
            countNoOfMesg++;
            System.out.println("message"+countNoOfMesg+"="+message);
            res = bulkSmsHttpInterface.sendMessage( message, phoneNumber );
            System.out.println(res);
           
            System.out.println("--------------------------");
            message="";
   }
    
    
    }

    
}
