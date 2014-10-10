package org.hisp.dhis.reports.action;

import java.io.File;

import org.hisp.dhis.reports.ReportService;

import com.opensymphony.xwork2.Action;

public class ReportsHomePageAction
    implements Action
{

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    private String raFolderName;

    public String execute()
        throws Exception
    {
        raFolderName = reportService.getRAFolderName();

        //clearCache();

        return SUCCESS;
    }

    private void clearCache()
    {
        try
        {
            String cacheFolderPath = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator
                + raFolderName + File.separator + "output";
            try
            {
                String newpath = System.getenv( "DHIS2_HOME" );
                if ( newpath != null )
                {
                    cacheFolderPath = newpath + File.separator + raFolderName + File.separator + "output";
                }
            }
            catch ( NullPointerException npe )
            {
                System.out.println( "DHIS2 Home is not set" );
                // do nothing, but we might be using this somewhere without
                // DHIS2_HOME set, which will throw a NPE
            }

            File dir = new File( cacheFolderPath );
            String[] files = dir.list();
            for ( String file : files )
            {
                file = cacheFolderPath + File.separator + file;
                File tempFile = new File( file );
                tempFile.delete();
            }
            System.out.println( "Cache cleared successfully" );
        }
        catch ( Exception e )
        {
            System.out.println( e.getMessage() );
        }
    }
}
