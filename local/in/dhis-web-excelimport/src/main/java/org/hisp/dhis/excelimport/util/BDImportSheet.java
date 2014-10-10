package org.hisp.dhis.excelimport.util;

import java.io.Serializable;

public class BDImportSheet implements Serializable
{
    private String xmlTemplateName;
    private String displayName;
    private String periodicity;
    private String checkTemplateName;
    private String checkRangeForHeader;
    private String checkRangeForData;


    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public BDImportSheet()
    {

    }

    public BDImportSheet(String xmlTemplateName, String displayName, String periodicity, String checkTemplateName, String checkRangeForHeader, String checkRangeForData)
    {
        this.xmlTemplateName = xmlTemplateName;
        this.displayName = displayName;
        this.periodicity = periodicity;
        this.checkTemplateName = checkTemplateName;
        this.checkRangeForHeader = checkRangeForHeader;
        this.checkRangeForData = checkRangeForData;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public String getXmlTemplateName()
    {
        return xmlTemplateName;
    }

    public void setXmlTemplateName( String xmlTemplateName )
    {
        this.xmlTemplateName = xmlTemplateName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( String displayName )
    {
        this.displayName = displayName;
    }

    public String getPeriodicity()
    {
        return periodicity;
    }

    public void setPeriodicity( String periodicity )
    {
        this.periodicity = periodicity;
    }

    public String getCheckTemplateName()
    {
        return checkTemplateName;
    }

    public void setCheckTemplateName(String checkTemplateName)
    {
        this.checkTemplateName = checkTemplateName;
    }

    public String getCheckRangeForHeader()
    {
        return checkRangeForHeader;
    }

    public void setCheckRangeForHeader(String checkRangeForHeader)
    {
        this.checkRangeForHeader = checkRangeForHeader;
    }

    public String getCheckRangeForData()
    {
        return checkRangeForData;
    }

    public void setCheckRangeForData(String checkRangeForData)
    {
        this.checkRangeForData = checkRangeForData;
    }

}
