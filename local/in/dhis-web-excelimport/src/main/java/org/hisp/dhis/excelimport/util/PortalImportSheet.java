package org.hisp.dhis.excelimport.util;

import java.io.Serializable;

public class PortalImportSheet implements Serializable
{
    private String xmlTemplateName;
    private String displayName;
    private String periodicity;
    private String proforma;
    private String checkerTemplateName;
    private String checkerRangeForHeader;
    private String checkerRangeForData;
    private String datasetId;
    private String orgunitGroupId;
    private String facilityStart;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public PortalImportSheet()
    {

    }

    public PortalImportSheet( String xmlTemplateName, String displayName, String periodicity, String proforma, String checkerTemplateName, String checkerRangeForHeader, String checkerRangeForData, String datasetId, String orgunitGroupId, String facilityStart )
    {
        this.xmlTemplateName = xmlTemplateName;
        this.displayName = displayName;
        this.periodicity = periodicity;
        this.proforma = proforma;
        this.checkerTemplateName = checkerTemplateName;
        this.checkerRangeForHeader = checkerRangeForHeader;
        this.checkerRangeForData = checkerRangeForData;
        this.datasetId = datasetId;
        this.orgunitGroupId = orgunitGroupId;
        this.facilityStart = facilityStart;
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

    public String getProforma()
    {
        return proforma;
    }

    public void setProforma( String proforma )
    {
        this.proforma = proforma;
    }

    public String getCheckerTemplateName()
    {
        return checkerTemplateName;
    }

    public void setCheckerTemplateName( String checkerTemplateName )
    {
        this.checkerTemplateName = checkerTemplateName;
    }

    public String getCheckerRangeForHeader()
    {
        return checkerRangeForHeader;
    }

    public void setCheckerRangeForHeader( String checkerRangeForHeader )
    {
        this.checkerRangeForHeader = checkerRangeForHeader;
    }

    public String getCheckerRangeForData()
    {
        return checkerRangeForData;
    }

    public void setCheckerRangeForData( String checkerRangeForData )
    {
        this.checkerRangeForData = checkerRangeForData;
    }

    public String getDatasetId()
    {
        return datasetId;
    }

    public void setDatasetId( String datasetId )
    {
        this.datasetId = datasetId;
    }

    public String getOrgunitGroupId()
    {
        return orgunitGroupId;
    }

    public void setOrgunitGroupId( String orgunitGroupId )
    {
        this.orgunitGroupId = orgunitGroupId;
    }

    public String getFacilityStart()
    {
        return facilityStart;
    }

    public void setFacilityStart( String facilityStart )
    {
        this.facilityStart = facilityStart;
    }
    
}
