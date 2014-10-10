package org.hisp.dhis.reports.action;

import java.io.File;

import com.opensymphony.xwork2.Action;

public class ReportsHomePageAction implements Action
{

    public String execute()
        throws Exception
    {
        clearCache();
        
        return SUCCESS;
    }
    
    private void clearCache()
    {
        try
        {
            String cacheFolderPath = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + "ra_national"+ File.separator + "output";
   
            File dir = new File( cacheFolderPath );
            String[] files = dir.list();        
            for ( String file : files )
            {
                file = cacheFolderPath + File.separator + file;
                File tempFile = new File(file);
                tempFile.delete();
            }
            System.out.println("Cache cleared successfully");
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }        
    }
}
