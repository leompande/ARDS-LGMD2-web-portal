package org.hisp.dhis.config.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.opensymphony.xwork2.Action;

public class StreamMySqlBackupAction
    implements Action
{

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

    private String backupFilePath;

    public void setBackupFilePath( String backupFilePath )
    {
        this.backupFilePath = backupFilePath;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        fileName = "dhis2.zip";

        byte data[] = new byte[1024];

        String zipFilePath = backupFilePath.substring( 0, backupFilePath.lastIndexOf( "/" ) );

        zipFilePath += "/dhis2.zip";

        try
        {
            FileOutputStream zipFOS = new FileOutputStream( zipFilePath );

            ZipOutputStream out = new ZipOutputStream( new BufferedOutputStream( zipFOS ) );

            FileInputStream fis = new FileInputStream( backupFilePath );

            BufferedInputStream bis = new BufferedInputStream( fis, 1024 );

            ZipEntry entry = new ZipEntry( "dhis2.sql" );

            out.putNextEntry( entry );

            int count;
            while ( (count = bis.read( data, 0, 1024 )) != -1 )
            {
                out.write( data, 0, count );
            }

            out.close();

            bis.close();

            inputStream = new BufferedInputStream( new FileInputStream( zipFilePath ), 1024 );
        }
        catch ( Exception e )
        {
            System.out.println( e.getMessage() );
        }

        return SUCCESS;
    }
}
