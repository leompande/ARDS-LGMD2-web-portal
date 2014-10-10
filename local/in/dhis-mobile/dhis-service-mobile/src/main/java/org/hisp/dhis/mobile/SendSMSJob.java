package org.hisp.dhis.mobile;

import java.util.Date;

import org.hisp.dhis.mobile.api.MobileImportService;

public class SendSMSJob
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SmsService smsService;
    
    public void setSmsService( SmsService smsService )
    {
        this.smsService = smsService;
    }
    
    private MobileImportService mobileImportService;
    
    public void setMobileImportService( MobileImportService mobileImportService )
    {
        this.mobileImportService = mobileImportService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    
    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------
    
    /*
     * This method will execute complete process which means retreive all 
     * pending messages and process (create xml files, delete sms) those messages, 
     * and import data from xml into DHIS and then send acknowledgement for max of
     * 30 sms at a time.
     */
    public void executeCompleteProcess()
    {
        System.out.println("SMS Complete Process Job Started at : "+new Date() );
        
        if( smsService == null )
        {
            System.out.println("SMS Service is null");
        }
        if( mobileImportService == null )
        {
            System.out.println("Mobile Import Service is null");
        }
        
        smsService.processPendingMessages();
        
        mobileImportService.importPendingFiles();

        smsService.sendDrafts();
        
        System.out.println("SMS Complete Process Job Ended at : "+new Date() );
    }

    /*
     * This method will process pending messages which means retreive all 
     * pending messages and create xml files in pending folder and delete sms.
     */
    public void executeProcessPendingMessage()
    {
        System.out.println("ProcessPendingMessage Job Started at : "+new Date() );
        
        if( smsService == null )
        {
            System.out.println("SMS Service is null");
        }
        
        smsService.processPendingMessages();
        
        System.out.println("ProcessPendingMessage Job Ended at : "+new Date() );
    }

    /*
     * This method will execute import process which means retreive all xml files
     * from pending folder and import data into DHIS.
     */
    public void executeImportProcess()
    {
        System.out.println("ImportProcess Job Started at : "+new Date() );
        
        if( mobileImportService == null )
        {
            System.out.println("Mobile Import Service is null");
        }
        
        mobileImportService.importPendingFiles();

        System.out.println("ImportProcess Job Ended at : "+new Date() );
    }

    /*
     * This method will send acknowledgement for max of 30 sms at a time.
     */
    public void executeSendSMSProcess()
    {
        System.out.println("SendSMSProcess Job Started at : "+new Date() );
        
        if( smsService == null )
        {
            System.out.println("SMS Service is null");
        }
        
        smsService.sendDrafts();
        
        System.out.println("SendSMSProcess Job Ended at : "+new Date() );
    }

    /*
     * This method will import data from xml files that are in pending folder into
     * DHIS and then send acknowledgement for max of 30 sms at a time.
     */
    public void executeImportAndSendSMSProcess()
    {
        System.out.println("ImportAndSendSMSProcess Job Started at : "+new Date() );
        
        if( smsService == null )
        {
            System.out.println("SMS Service is null");
        }
        if( mobileImportService == null )
        {
            System.out.println("Mobile Import Service is null");
        }
        
      //  mobileImportService.importPendingFiles();

      //  smsService.sendDrafts();
        
        System.out.println("ImportAndSendSMSProcess Job Ended at : "+new Date() );
    }
    
}
