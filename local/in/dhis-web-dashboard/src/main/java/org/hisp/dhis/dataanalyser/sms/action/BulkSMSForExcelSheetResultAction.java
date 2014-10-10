package org.hisp.dhis.dataanalyser.sms.action;

import java.io.File;
import java.util.UUID;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.WritableWorkbook;

import org.hisp.dhis.dataanalyser.util.BulkSMSHttpInterface;

import com.opensymphony.xwork2.Action;

public class BulkSMSForExcelSheetResultAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    private String resultMessage;
    
    public String getResultMessage()
    {
        return resultMessage;
    }
    
    private File output;

    public File getOutput()
    {
        return output;
    }

    private File upload;

    public File getUpload()
    {
        return upload;
    }

    public void setUpload( File upload )
    {
        this.upload = upload;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        BulkSMSHttpInterface bulkSMSHTTPInterface = new BulkSMSHttpInterface();

        int rowStart = 2;
        int colStart = 1;
        int rowEnd = 65530;
        
        try
        {
            String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + "temp" + File.separator + UUID.randomUUID().toString() + ".xls";
            Workbook excelImportFile = Workbook.getWorkbook( upload );
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
                System.out.println( cellContent1 + " : " + cellContent2 );
                bulkSMSHTTPInterface.sendMessage( cellContent2, cellContent1 );
            }

            resultMessage = bulkSMSHTTPInterface.checkBalance();
        }
        catch( Exception e )
        {
            resultMessage = "Not able to send SMS to the group bec of "+e.getMessage();
            e.printStackTrace();
        }

        return SUCCESS;
    }
}
