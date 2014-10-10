package org.hisp.dhis.config.action;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.config.ConfigurationService;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.system.database.DatabaseInfoProvider;
import org.springframework.beans.factory.annotation.Required;

import com.opensymphony.xwork2.Action;

public class AdvanceMySqlBackupResultAction implements Action
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
    
    private List<String> selectedTables = new ArrayList<String>();
    
    public List<String> getSelectedTables()
    {
        return selectedTables;
    }

    public void setSelectedTables( List<String> selectedTables )
    {
        this.selectedTables = selectedTables;
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
    
    private String statusMessage;
    
    public String getStatusMessage()
    {
        return statusMessage;
    }
    
    private SimpleDateFormat simpleDateFormat;
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    

    public String execute() throws Exception
    {        
        status = "INPUT";
        System.out.println(" Total No of Selected Tables for Backup is :" + selectedTables.size() );
        
        System.out.println( "Backup Start Time is : " + new Date() );
        
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
        //System.out.println(" MY-SQL Path is :" + mySqlPath );
        String backupCommand = "";
        
        String temTables = "";
        for( String table : selectedTables )
        {
            
            temTables += " " + table;
            //backupCommand += backupCommand + " " + table +" -r "+backupFilePath;
        }
        
        //System.out.println(" Tables are :" + temTables );
        
        try
        {
            if( password == null || password.trim().equals( "" ) )
            {
                backupCommand = mySqlPath + "mysqldump -u "+ userName +" "+ dbName + " "+ temTables +" -r "+backupFilePath;
                
                //backupCommand = mySqlPath + "mysqldump -u "+ userName +" "+ dbName +" -r "+backupFilePath;
            }
            else
            {
                backupCommand = mySqlPath + "mysqldump -u "+ userName +" -p"+ password +" "+ dbName + " "+ temTables +" -r "+backupFilePath;
                
                //backupCommand = mySqlPath + "mysqldump -u "+ userName +" -p"+ password +" "+ dbName +" -r "+backupFilePath;
            }
            //System.out.println(" Backup Command is :" + backupCommand );
            
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
        
        //System.out.println(" Backup Path is :" + backupFilePath );
        System.out.println( "Backup End Time is : " + new Date() );
        /*
        int i =1;
        for( String table : selectedTables )
        {
            System.out.println(" Table " + i + " is : " + table );
            i++;
        }
        */
        return SUCCESS;
    }
}
