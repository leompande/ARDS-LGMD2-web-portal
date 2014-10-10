package org.hisp.dhis.config.action;

import org.hisp.dhis.config.ConfigurationService;
import org.hisp.dhis.config.Configuration_IN;

import com.opensymphony.xwork2.Action;

public class ConfigurationResultAction implements Action
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
    
    private String mysqlPath;

    public void setMysqlPath( String mysqlPath )
    {
        this.mysqlPath = mysqlPath;
    }

    private String backupDataPath;

    public void setBackupDataPath( String backupDataPath )
    {
        this.backupDataPath = backupDataPath;
    }

    private String reportFolder;
    
    public void setReportFolder( String reportFolder )
    {
        this.reportFolder = reportFolder;
    }

    private Configuration_IN mySqlPathConfig;
       
    private Configuration_IN backupPathConfig;
    
    private Configuration_IN reportFolderConfig;
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {

        /* MYSQL PATH CONFIG */
        mySqlPathConfig = configurationService.getConfigurationByKey( Configuration_IN.KEY_MYSQLPATH );        

        if(mysqlPath == null || mysqlPath.trim().equals( "" ))
        {
            mysqlPath = Configuration_IN.DEFAULT_MYSQLPATH;
        }
        
        if( mySqlPathConfig == null )
        {
            mySqlPathConfig = new Configuration_IN( Configuration_IN.KEY_MYSQLPATH, mysqlPath );
            
            configurationService.addConfiguration( mySqlPathConfig );                        
        }
        else
        {
            mySqlPathConfig.setValue( mysqlPath );
            
            configurationService.updateConfiguration( mySqlPathConfig );
        }
        
        /* MYSQL BACKUP PATH CONFIG */
        backupPathConfig = configurationService.getConfigurationByKey( Configuration_IN.KEY_BACKUPDATAPATH );
        
        if(backupDataPath == null || backupDataPath.trim().equals( "" ))
        {
            backupDataPath = Configuration_IN.DEFAULT_BACKUPDATAPATH;
        }
                
        if( backupPathConfig == null )
        {
            backupPathConfig = new Configuration_IN( Configuration_IN.KEY_BACKUPDATAPATH, backupDataPath );
            
            configurationService.addConfiguration( backupPathConfig );                        
        }
        else
        {
            backupPathConfig.setValue( backupDataPath );
            
            configurationService.updateConfiguration( backupPathConfig );
        }
        
        /* REPORT FOLDER PATH */
        reportFolderConfig = configurationService.getConfigurationByKey( Configuration_IN.KEY_REPORTFOLDER );
        
        if(reportFolder == null || reportFolder.trim().equals( "" ))
        {
            reportFolder = Configuration_IN.DEFAULT_REPORTFOLDER;
        }
        
        if( reportFolderConfig == null )
        {
            reportFolderConfig = new Configuration_IN( Configuration_IN.KEY_REPORTFOLDER, reportFolder );
            
            configurationService.addConfiguration( reportFolderConfig );                        
        }        
        else
        {
            reportFolderConfig.setValue( reportFolder );
            
            configurationService.updateConfiguration( reportFolderConfig );
        }
        
        return SUCCESS;
    }

}
