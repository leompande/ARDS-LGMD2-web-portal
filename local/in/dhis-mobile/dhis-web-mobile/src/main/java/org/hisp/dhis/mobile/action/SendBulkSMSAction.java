/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hisp.dhis.mobile.action;


import com.opensymphony.xwork2.Action;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataelement.DefaultDataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DefaultSectionService;
import org.hisp.dhis.mobile.BulkSMSHttpInterface;
import org.hisp.dhis.mobile.SmsService;
import org.hisp.dhis.mobile.api.SendSMS;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
//import org.hisp.dhis.organisationunit.comparator.OrganisationUnitGroupNameComparator;
import org.smslib.http.BulkSmsHTTPGateway;
import org.w3c.dom.NodeList;

/**
 * 
 * @author harsh
 */
public class SendBulkSMSAction
    implements Action
{
    
     DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }


    SmsService smsService;

    //private int succ = 0, modemError = 0;

    public void setSmsService( SmsService smsService )
    {
        this.smsService = smsService;
    }

    boolean smsServiceStatus;

    public boolean getSmsServiceStatus()
    {
        smsServiceStatus = smsService.getServiceStatus();
        return smsServiceStatus;
    }

    String statAction;

    public void setStatAction( String statAction )
    {
        if ( statAction.equalsIgnoreCase( "Start" ) )
        {
            this.result = smsService.startService();
        } else
        {
            this.result = smsService.stopService();
        }
    }

    private String[] strArr;

    private List<String> phonenos;

    BulkSMSHttpInterface conn;

    // OrganisationUnitGroup orgUnitGrp;
    OrganisationUnitGroupService orgUnitGrpService;

    public void setOrgUnitGrpService( OrganisationUnitGroupService orgUnitGrpService )
    {
        this.orgUnitGrpService = orgUnitGrpService;
    }

    private List<OrganisationUnitGroup> availableOrganisationUnitGroups;

    public List<OrganisationUnitGroup> getAvailableOrganisationUnitGroups()
    {
        return availableOrganisationUnitGroups;
    }

    
    String message;

    public void setMessage( String message )
    {
        this.message = message;
    }

    String tempString;

    public void setTempString( String tempString )
    {
        this.tempString = tempString;
    }

    String selectedIdString;

    public void setSelectedIdString( String selectedIdString )
    {
        this.selectedIdString = selectedIdString;
    }

    String result = "";

    public String getResult()
    {
        return result;
    }
    public List<String> availableDataElements;

    public List<String> getAvailableDataElements()
    {
        return availableDataElements;
    }
    
public int countavailableDataElements;

    public void setAvailableDataElements( List<String> availableDataElements )
    {
        this.availableDataElements = availableDataElements;
    }
public String isMessageDisabled;

    public void setIsMessageDisabled( String isMessageDisabled )
    {
        this.isMessageDisabled = isMessageDisabled;
    }
    public String selectedAvailableDataElements;

    public void setSelectedAvailableDataElements( String selectedAvailableDataElements )
    {
        this.selectedAvailableDataElements = selectedAvailableDataElements;
    }
    
    
    
    @Override
    public String execute()
        throws Exception
    {
        //selectedOrganisationUnitGroup=new ArrayList<OrganisationUnitGroup>();

        availableOrganisationUnitGroups = new ArrayList<OrganisationUnitGroup>( orgUnitGrpService.getAllOrganisationUnitGroups() );
        System.out.println( "size(bulksmsmaction)==" + availableOrganisationUnitGroups.size() );   
        //Collections.sort( availableOrganisationUnitGroups, new OrganisationUnitGroupNameComparator() );
        Iterator<OrganisationUnitGroup> it=orgUnitGrpService.getAllOrganisationUnitGroups().iterator();
        while(it.hasNext())
        {
            System.out.println(it.next());
        }
//dataElementService=new DefaultDataElementService();

//System.out.println("------------------------------------->"+dataElementService.getDataElementGroupCount());
  //    DataElementGroup dataElementGrp=dataElementService.getDataElementGroupByName( "Mobile Daily");  
      

          availableDataElements=new ArrayList<String>();
    //  availableDataElements.add(">>"+  dataElementGrp.getMembers().size());
     //   availableDataElements.add( "do you like this service?");
       // availableDataElements.add( "randomly answer this question as yes or no");
 
          File file=new File( System.getenv( "DHIS2_HOME" ) + File.separator+"mobile.xml");
 if (!file.exists()){
 result="make a file mobile.xml in the directory\""+System.getenv( "DHIS2_HOME" )+"\" with dataelements in it and try again";
 return SUCCESS;
 }
          System.out.println(  file.getAbsolutePath());
      DocumentBuilderFactory docBuiderFactory=DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder= docBuiderFactory.newDocumentBuilder();
        org.w3c.dom.Document doc= docBuilder.parse(file);
NodeList nodelist=doc.getElementsByTagName( "dataelement");
    for (int i=0;i<nodelist.getLength();i++){
        org.w3c.dom.Node node=nodelist.item( i);
        String id=node.getChildNodes().item( 3).getTextContent();
        String name=node.getChildNodes().item( 1).getTextContent();
        String periodId=node.getChildNodes().item( 5).getTextContent();
        String comboId=node.getChildNodes().item( 7).getTextContent();
        String messageType=node.getChildNodes().item( 9).getTextContent();
        
        availableDataElements.add( id +"#"+messageType+"#"+name);        
        System.out.println(id +name+periodId+comboId+messageType);
    }
          
        conn = new BulkSMSHttpInterface();
        phonenos = new ArrayList<String>();


        //System.out.println( tempString );
        if ( selectedIdString != null )
        {
            strArr = selectedIdString.split( "," );

            for ( int i = 0; i < strArr.length; i++ )
            {
                if ( !( strArr[i].equals( "" ) ) )
                {
                    System.out.println( "strArrid=" + strArr[i] );
                    int parseInt = Integer.parseInt( strArr[i] );

//13321
                    for ( int j = 0; j < availableOrganisationUnitGroups.size(); j++ )
                    {
                        if ( availableOrganisationUnitGroups.get( j ).getId() == parseInt )
                        {

                            System.out.println( "  -" + availableOrganisationUnitGroups.get( j ).getMembers().size() );
                            Iterator<OrganisationUnit> itr = availableOrganisationUnitGroups.get( j ).getMembers().iterator();
                            while ( itr.hasNext() )
                            {
                                OrganisationUnit temp = (OrganisationUnit) itr.next();
                                if ( temp.getPhoneNumber() != null )
                                {
                                    phonenos.add( temp.getPhoneNumber() );
                                }
                                 System.out.println( "id="+temp.getId()+" ----phone=" + temp.getPhoneNumber() );
                            }

                        }
                    }
                }
            }
// System.out.print( countavailableDataElements+" "+availableDataElements.get( 1)+" "+isMessageDisabled+"sel="+selectedAvailableDataElements+"<----" );
            if (isMessageDisabled.equals( "true") )
            {
            message=selectedAvailableDataElements;
          //  System.out.println(selectedAvailableDataElements);
                String[] split = message.split( "#");
                for (int i=0;i<split.length;i++){
            //    System.out.println(split[i]+","+i+split.length+message);
                }
                
                message=split[0]+"#"+"1"+"#"+"1"+"#"+split[1]+"#"+split[2];
              System.out.println(message);
                // System.out.print( "sdsd---------------------"+countavailableDataElements);
            }
            
            if (message.isEmpty()){
            result="No Message Selected/Written";
            return SUCCESS;
            }
            else if (phonenos.isEmpty()){
            result="No Phone Numbers Found";
            return SUCCESS;
            
            }
            
            // fill in the sendSMS fields...
            List<SendSMS> sendSMSList = new ArrayList<SendSMS>();
            SendSMS tempSendSMS;
            for ( int i = 0; i < phonenos.size(); i++ )
            {
                tempSendSMS = new SendSMS( phonenos.get( i ), message );
                sendSMSList.add( tempSendSMS );
            }

            /* for testing........
            
            
            List<SendSMS> sendSMSListForTesting=new ArrayList();
            sendSMSListForTesting.add( new SendSMS( "9654", message) );
            sendSMSListForTesting.add( new SendSMS( "9718", message) );
            for (int i=0;i<80;i++){  
            sendSMSListForTesting.add( new SendSMS( "9654", message) );
            sendSMSListForTesting.add( new SendSMS( "9718", message) );       
            }              
            result =smsService.sendMessages( sendSMSListForTesting ); 
             */


            /*
            
            // for third party=
            result= conn.sendMessages( message, phonenos);
            System.out.println(result);
            
            String temp=conn.checkBalance();
            result+="Balance="+temp;
            

             */


                System.out.println(message);
            // for gsm modem
                if (isMessageDisabled.equals( "true"))
                result=smsService.sendMessagesToPort(sendSMSList, 16000,0);
                else
                result = smsService.sendMessages( sendSMSList );

            /* for testing...
            if (message.startsWith( "group") )
            result=     sendGroupMessage();
            else if (message.startsWith( "one") )
            result=     sendGroupMessageOneByOne();
            else result="message not yet supported";
            System.out.println("result="+result);
            System.out.println(".............................>>success="+succ+"  error="+modemError);
             */
        }

        return SUCCESS;
        // throw new UnsupportedOperationException("Not supported yet.");
    }
    /* for testing......
    
    
    private String sendGroupMessage() throws FileNotFoundException, IOException
    {
    List<String> phoneno=new ArrayList<String>(); 
    FileReader fr=new FileReader( System.getenv( "DHIS2_HOME")+"test.prop");
    BufferedReader bfr=new BufferedReader( fr );
    
    while(bfr.ready()){
    phoneno.add( bfr.readLine());                
    }
    System.out.println("------group:"+phoneno.get( 0) );
    result=smsService.sendMessageToGroup( "testing", phoneno, message);
    
    return result;
    }
    
    private String sendGroupMessageOneByOne() throws FileNotFoundException, IOException, InterruptedException
    {
    List<String> phonenoList=new ArrayList<String>(); 
    FileReader fr=new FileReader( System.getenv( "DHIS2_HOME")+"test.prop");
    BufferedReader bfr=new BufferedReader( fr );
    String temp;
    while(bfr.ready()){
    
    phonenoList.add( bfr.readLine());                
    // System.out.println("------group:"+phoneno.get( 0) );
    }
    String strDelay;
    int delay;
    strDelay=message.substring( 3, 8);
    delay=Integer.parseInt( strDelay);
    System.out.println("repeat="+Integer.parseInt( message.substring( 8, 12)) );
    for (int j=0;j<Integer.parseInt( message.substring( 8, 12) );j++)
    for (int i=0;i<phonenoList.size();i++){ 
    if (smsService.getServiceStatus()){          
    temp=smsService.sendMessage( phonenoList.get( i ), message);
    if (temp.equals( "SUCCESS") )
    succ++;
    else if (temp.equals( "MODEMERROR") )
    {modemError++;
    i--;
    }
    result+=temp+" ";
    
    System.out.println("..............succeded="+succ+"   failed="+modemError);
    }else{ i--;
    
    System.out.println("servicestatus:false..............succeded="+succ+"   failed="+modemError);
    }
    Thread.currentThread().sleep(delay );
    }
    //result=smsService.sendMessageToGroup( "testing", phoneno, message);
    
    return result;
    }
     */
}
