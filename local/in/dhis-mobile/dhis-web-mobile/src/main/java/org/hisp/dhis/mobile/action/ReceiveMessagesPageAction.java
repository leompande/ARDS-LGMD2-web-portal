package org.hisp.dhis.mobile.action;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.mobile.SmsService;
import org.hisp.dhis.mobile.XMLFilter;
import org.hisp.dhis.mobile.api.MobileImportService;


import com.opensymphony.xwork2.Action;

public class ReceiveMessagesPageAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    SmsService smsService;

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
    // Parameters
    // -------------------------------------------------------------------------
    
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
        } 
        else
        {
            this.result = smsService.stopService();
        }
    }
    
    String result = "";

    public String getResult()
    {
        return result;
    }
        
    String processPendingSMS;
    
    public void setProcessPendingSMS( String processPendingSMS )
    {
        this.result = smsService.processPendingMessages();
    }
    
    String importAction;

    public void setImportAction( String importAction )
    {
        mobileImportService.importPendingFiles();
        
        this.result = getImportStatus();
    }

    private Integer pending = 0;

    public Integer getPending()
    {
        return pending;
    }

    private Integer bounced = 0;
    
    public Integer getBounced()
    {
        return bounced;
    }
    
    private Integer completed = 0;
    
    public Integer getCompleted()
    {
        return completed;
    }
    
    private Map<String, String> pendingMsgList;
    
    public Map<String, String> getPendingMsgList()
    {
        return pendingMsgList;
    }
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        pendingMsgList = new HashMap<String, String>( );
        
        if( smsService.getServiceStatus() )
        {
            pendingMsgList = smsService.readAllPendingMessages();
            
            getImportStatus();
        }
        
        return SUCCESS;
    }
    
    public String getImportStatus()
    {
        FilenameFilter filter = new XMLFilter();
        File pendingFolder = new File( System.getenv( "DHIS2_HOME" ) + File.separator + "mi" + File.separator + "pending" );

        pending = pendingFolder.list( filter ).length;
        File bouncedFolder = new File( System.getenv( "DHIS2_HOME" ) + File.separator + "mi" + File.separator + "bounced" );
        bounced = bouncedFolder.list( filter ).length;
        File completedFolder = new File( System.getenv( "DHIS2_HOME" ) + File.separator + "mi" + File.separator + "completed" );
        completed = completedFolder.list( filter ).length;
        
        return "ImportStatus| Pending : "+ pending +" Bounced : "+ bounced +" Completed : "+completed;
    }

}
