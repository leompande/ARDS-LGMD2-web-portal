package org.hisp.dhis.dataanalyser.util;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;
import java.util.UUID;

import jxl.Sheet;
import jxl.Workbook;
import jxl.write.WritableWorkbook;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class SendingBulkSMSFromExcelJob extends QuartzJobBean
{
    // -------------------------------------------------------------------------
    // implementation
    // -------------------------------------------------------------------------

    protected void executeInternal( JobExecutionContext context ) throws JobExecutionException 
    {
        
        try
        {
            BulkSMSHttpInterface bulkSMSHTTPInterface = new BulkSMSHttpInterface();
            
            Properties properties = new Properties();
            properties.load( new FileReader( System.getenv( "DHIS2_HOME" ) + File.separator + "SMSServer.conf" ) );
            
            String excelFilePath = properties.getProperty( "excelfilepath" );

            int rowStart = 2;
            int colStart = 1;
            int rowEnd = 65530;

            String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + "temp" + File.separator + UUID.randomUUID().toString() + ".xls";
            Workbook excelImportFile = Workbook.getWorkbook( new File(excelFilePath) );
            WritableWorkbook writableExcelImportFile = Workbook.createWorkbook( new File(outputReportPath), excelImportFile );
            Sheet sheet = writableExcelImportFile.getSheet( 0 );

            for( int i = rowStart; i < rowEnd; i++ )
            {                
                String cellContent1 = sheet.getCell( colStart, i).getContents();
                String cellContent2 = sheet.getCell( colStart+1, i).getContents();
                if( cellContent1 == null || cellContent1.equalsIgnoreCase( "" ) || cellContent2 == null || cellContent2.equalsIgnoreCase( "" ) )
                {
                    continue;
                }
                
                bulkSMSHTTPInterface.sendMessage( cellContent2, cellContent1 );
            }

        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

    }
}
