package org.hisp.dhis.config.action;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.hisp.dhis.config.ConfigurationService;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.system.database.DatabaseInfoProvider;
import org.springframework.beans.factory.annotation.Required;

import com.opensymphony.xwork2.Action;

public class TakeMySqlBackupAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ConfigurationService configurationService;

    private DatabaseInfoProvider provider;
 
    @Required
    public void setConfigurationService( ConfigurationService configurationService )
    {
        this.configurationService = configurationService;
    }

    @Required
    public void setProvider( DatabaseInfoProvider provider )
    {
        this.provider = provider;
    }
    
    // -------------------------------------------------------------------------
    // Input and Output Parameters
    // -------------------------------------------------------------------------


    private String statusMessage;
    
    public String getStatusMessage()
    {
        return statusMessage;
    }

    private String status;
    
    public String getStatus()
    {
        return status;
    }

    private String backupFilePath;

    public String getBackupFilePath()
    {
        return backupFilePath;
    }

    private SimpleDateFormat simpleDateFormat;

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {        
        status = "INPUT";
        
        String dbName = provider.getDatabaseInfo().getName();
        String userName = provider.getDatabaseInfo().getUser();
        String password = provider.getDatabaseInfo().getPassword();
        
        String mySqlPath = configurationService.getConfigurationByKey( Configuration_IN.KEY_MYSQLPATH ).getValue();
        
        Calendar curDateTime = Calendar.getInstance();
        Date curDate = new Date();                
        curDateTime.setTime( curDate );
        
        simpleDateFormat = new SimpleDateFormat( "ddMMMyyyy-HHmmssSSS" );
                
        String tempFolderName = simpleDateFormat.format( curDate );
        
        backupFilePath = configurationService.getConfigurationByKey( Configuration_IN.KEY_BACKUPDATAPATH ).getValue();
        backupFilePath += tempFolderName;
        
        File newdir = new File( backupFilePath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        
        backupFilePath += "/" + "dhis2.sql";
        System.out.println(" MY-SQL Path is :" + mySqlPath );
        String backupCommand = "";
        
        try
        {
            if( password == null || password.trim().equals( "" ) )
            {
                backupCommand = mySqlPath + "mysqldump -u "+ userName +" "+ dbName +" -r "+backupFilePath;
            }
            else
            {
                backupCommand = mySqlPath + "mysqldump -u "+ userName +" -p"+ password +" "+ dbName +" -r "+backupFilePath;
            }
            
            System.out.println(" Backup Command is :" + backupCommand );
            Runtime rt = Runtime.getRuntime();
            
            Process process = rt.exec( backupCommand );
            
            process.waitFor();
            
            if( process.exitValue() == 0 )
            {
                statusMessage = "Backup taken succussfully at : "+backupFilePath;
                
                status = "SUCCESS";
            }
            else
            {
                statusMessage = "Not able to take Backup, Please try again";
            }
        }
        catch ( Exception e )
        {
            System.out.println("Exception : "+e.getMessage());
            
            statusMessage = "Not able to take Backup, Please check MySQL configuration and SQL file path.";
        }
        System.out.println(" Backup Path is :" + backupFilePath );
        return SUCCESS;
        
        
    }
    
}
