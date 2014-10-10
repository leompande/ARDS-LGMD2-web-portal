package org.hisp.dhis.reports.action;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataentryform.DataEntryFormService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;

import com.opensymphony.xwork2.ActionSupport;

public class GenerateInputScreenAction extends ActionSupport
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private DataEntryFormService dataEntryFormService;
    
    public void setDataEntryFormService( DataEntryFormService dataEntryFormService )
    {
        this.dataEntryFormService = dataEntryFormService;
    }
        
    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    private String contentType;

    public String getContentType()
    {
        return contentType;
    }

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    private int bufferSize;

    public int getBufferSize()
    {
        return bufferSize;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {                
        DataSet dataSet = dataSetService.getDataSet( 26 );        
        
        DataEntryForm dataEntryForm = dataEntryFormService.getDataEntryFormByDataSet( dataSet );
        
        String htmlCode = dataEntryForm.getHtmlCode();

        fileName = "sample.xls";
                
        inputStream = new BufferedInputStream( new ByteArrayInputStream( htmlCode.getBytes() ) );
        
        return SUCCESS;
    }
    
}
