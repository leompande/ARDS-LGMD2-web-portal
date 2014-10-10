package org.hisp.dhis.mobile;

import java.util.Date;

import org.hisp.dhis.mobile.api.MobileImportService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;


public class SMSImportJob extends QuartzJobBean
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private MobileImportService mobileImportService;
    
    public void setMobileImportService( MobileImportService mobileImportService )
    {
        this.mobileImportService = mobileImportService;
    }
    
    private SmsService smsService;
    
    public void setSmsService( SmsService smsService )
    {
        this.smsService = smsService;
    }

    // -------------------------------------------------------------------------
    // implementation
    // -------------------------------------------------------------------------
    
    protected void executeInternal( JobExecutionContext context ) throws JobExecutionException 
    {
        System.out.println("SMSImport Job Started at : "+new Date() );
        
        if( smsService == null && mobileImportService == null )
        {
            System.out.println("SMS Service And MobileImportService is null");
        }
        else if( smsService == null )
        {
            System.out.println("SMS Service is null");
        }
        else if( mobileImportService == null )
        {
            System.out.println("MobileImportService is null");
        }
        
        smsService.processPendingMessages();
        
        mobileImportService.importPendingFiles();
        
        System.out.println("SMSImport Job Ended at : "+new Date() );
    }
    
}

