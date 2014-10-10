package org.hisp.dhis.config.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.hisp.dhis.config.ConfigurationService;
import org.hisp.dhis.config.Configuration_IN;

import com.opensymphony.xwork2.Action;

public class ClearFolderAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private ConfigurationService configurationService;

    public void setConfigurationService( ConfigurationService configurationService )
    {
        this.configurationService = configurationService;
    }

    // -------------------------------------------------------------------------
    // Input and Output Parameters
    // -------------------------------------------------------------------------
    
    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    private String selectedButton;
    
    public void setSelectedButton( String selectedButton )
    {
        this.selectedButton = selectedButton;
    }

    private String statusMessage;
    
    public String getStatusMessage()
    {
        return statusMessage;
    }
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        String clearFolderPath;
        
        statusMessage = "";
        
        if( selectedButton.equalsIgnoreCase( "clearoutput" ) )
        {
            String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();
            
            clearFolderPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output";
            
            if( configurationService.clearXfolder( clearFolderPath ) )
            {
                statusMessage = "Successfully Cleared the Folder : OUTPUT";
            }
            else
            {
                statusMessage = "Problem while clearing OUTPUT folder, please see the log";
            }
        }
        else if( selectedButton.equalsIgnoreCase( "clearbounced" ) )
        {
            clearFolderPath = System.getenv( "DHIS2_HOME" ) + File.separator + "mi" + File.separator + "bounced";
            
            String backupStatus = configurationService.backupFolder( System.getenv( "DHIS2_HOME" ) + File.separator + "mi" );
            
            if( backupStatus.equalsIgnoreCase( "INPUT" ) )
            {
                statusMessage = "Problem while taking backup for mi folder, please see the log";
            }
            else
            {
                if( configurationService.clearXfolder( clearFolderPath ) )
                {
                    statusMessage = "Successfully Cleared the Folder : BOUNCED";
                }
                else
                {
                    statusMessage = "Problem while clearing BOUNCED folder, please see the log";
                }
            }
        }
        else if( selectedButton.equalsIgnoreCase( "clearpending" ) )
        {
            clearFolderPath = System.getenv( "DHIS2_HOME" ) + File.separator + "mi" + File.separator + "pending";
            
            String backupStatus = configurationService.backupFolder( System.getenv( "DHIS2_HOME" ) + File.separator + "mi" );
            
            if( backupStatus.equalsIgnoreCase( "INPUT" ) )
            {
                statusMessage = "Problem while taking backup for mi folder, please see the log";
            }
            else
            {
                if( configurationService.clearXfolder( clearFolderPath ) )
                {
                    statusMessage = "Successfully Cleared the Folder : PENDING";
                }
                else
                {
                    statusMessage = "Problem while clearing PENDING folder, please see the log";
                }
            }
        }
        else if( selectedButton.equalsIgnoreCase( "clearcompleted" ) )
        {
            clearFolderPath = System.getenv( "DHIS2_HOME" ) + File.separator + "mi" + File.separator + "completed";
            
            String backupStatus = configurationService.backupFolder( System.getenv( "DHIS2_HOME" ) + File.separator + "mi" );
            
            if( backupStatus.equalsIgnoreCase( "INPUT" ) )
            {
                statusMessage = "Problem while taking backup for mi folder, please see the log";
            }
            else
            {
                if( configurationService.clearXfolder( clearFolderPath ) )
                {
                    statusMessage = "Successfully Cleared the Folder : COMPLETED";
                }
                else
                {
                    statusMessage = "Problem while clearing COMPLETED folder, please see the log";
                }
            }
        }
        else if( selectedButton.equalsIgnoreCase( "clearmi" ) )
        {
            String backupStatus = configurationService.backupFolder( System.getenv( "DHIS2_HOME" ) + File.separator + "mi" );
            
            if( backupStatus.equalsIgnoreCase( "INPUT" ) )
            {
                statusMessage = "Problem while taking backup for mi folder, please see the log";
            }
            else
            {
                // Clearing Bounced Folder
                clearFolderPath = System.getenv( "DHIS2_HOME" ) + File.separator + "mi" + File.separator + "bounced";
                
                if( configurationService.clearXfolder( clearFolderPath ) )
                {
                    statusMessage += " Successfully Cleared the Folder : BOUNCED;";
                }
                else
                {
                    statusMessage += " Problem while clearing BOUNCED folder, please see the log";
                }

                // Clearing Pending Folder
                clearFolderPath = System.getenv( "DHIS2_HOME" ) + File.separator + "mi" + File.separator + "pending";
                
                if( configurationService.clearXfolder( clearFolderPath ) )
                {
                    statusMessage += " Successfully Cleared the Folder : PENDING";
                }
                else
                {
                    statusMessage += " Problem while clearing PENDING folder, please see the log";
                }
                
                // Clearing Completed Folder
                clearFolderPath = System.getenv( "DHIS2_HOME" ) + File.separator + "mi" + File.separator + "completed";
                
                if( configurationService.clearXfolder( clearFolderPath ) )
                {
                    statusMessage += " Successfully Cleared the Folder : COMPLETED";
                }
                else
                {
                    statusMessage += " Problem while clearing COMPLETED folder, please see the log";
                }
            }
        }
        else if( selectedButton.equalsIgnoreCase( "downloadmi" ) )
        {
            String backupStatus = configurationService.backupFolder( System.getenv( "DHIS2_HOME" ) + File.separator + "mi" );
            
            if( backupStatus.equalsIgnoreCase( "INPUT" ) )
            {
                statusMessage = "Problem while taking backup for mi folder, please see the log";
            }
            else
            {
                fileName = "mi.zip";
                
                inputStream = new BufferedInputStream( new FileInputStream( backupStatus ), 1024 );
                
                return "download";
            }
        }
        else if( selectedButton.equalsIgnoreCase( "downlaodra" ) )
        {
            String raFolderName = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER ).getValue();
            
            String backupStatus = configurationService.backupFolder( System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName );
            
            if( backupStatus.equalsIgnoreCase( "INPUT" ) )
            {
                statusMessage = "Problem while taking backup for reports folder, please see the log";
            }
            else
            {
                fileName = raFolderName+".zip";
                
                inputStream = new BufferedInputStream( new FileInputStream( backupStatus ), 1024 );
                
                return "download";
            }
        }

        return SUCCESS;
    }
    
}
