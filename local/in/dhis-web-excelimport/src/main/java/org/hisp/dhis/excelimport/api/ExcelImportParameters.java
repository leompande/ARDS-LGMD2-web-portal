package org.hisp.dhis.excelimport.api;

import java.io.Serializable;

public class ExcelImportParameters implements Serializable
{
    private String xmlCheckerFileName;
    
    private String xmlMapFileName;
    
    private String excelFileName;
    
    private String reportTypeName;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ExcelImportParameters()
    {
        
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public String getXmlCheckerFileName()
    {
        return xmlCheckerFileName;
    }

    public void setXmlCheckerFileName( String xmlCheckerFileName )
    {
        this.xmlCheckerFileName = xmlCheckerFileName;
    }

    public String getXmlMapFileName()
    {
        return xmlMapFileName;
    }

    public void setXmlMapFileName( String xmlMapFileName )
    {
        this.xmlMapFileName = xmlMapFileName;
    }

    public String getExcelFileName()
    {
        return excelFileName;
    }

    public void setExcelFileName( String excelFileName )
    {
        this.excelFileName = excelFileName;
    }

    public String getReportTypeName()
    {
        return reportTypeName;
    }

    public void setReportTypeName( String reportTypeName )
    {
        this.reportTypeName = reportTypeName;
    }


}
