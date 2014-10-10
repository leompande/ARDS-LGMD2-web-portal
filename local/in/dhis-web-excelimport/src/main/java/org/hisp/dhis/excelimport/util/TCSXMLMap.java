package org.hisp.dhis.excelimport.util;

import java.io.Serializable;

public class TCSXMLMap implements Serializable
{
    private String tcsDataElement;
    private String dhisDataElement;
    private String orgunitCode;
    private String tscPeriod;
    private String dataValue;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public TCSXMLMap()
    {
        
    }

    public TCSXMLMap( String tcsDataElement, String dhisDataElement, String orgunitCode, String tscPeriod, String dataValue )
    {
        this.tcsDataElement = tcsDataElement;
        this.dhisDataElement = dhisDataElement;
        this.orgunitCode = orgunitCode;
        this.tscPeriod = tscPeriod;
        this.dataValue = dataValue;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public String getTcsDataElement()
    {
        return tcsDataElement;
    }

    public void setTcsDataElement( String tcsDataElement )
    {
        this.tcsDataElement = tcsDataElement;
    }

    public String getDhisDataElement()
    {
        return dhisDataElement;
    }

    public void setDhisDataElement( String dhisDataElement )
    {
        this.dhisDataElement = dhisDataElement;
    }

    public String getOrgunitCode()
    {
        return orgunitCode;
    }

    public void setOrgunitCode( String orgunitCode )
    {
        this.orgunitCode = orgunitCode;
    }

    public String getTscPeriod()
    {
        return tscPeriod;
    }

    public void setTscPeriod( String tscPeriod )
    {
        this.tscPeriod = tscPeriod;
    }

    public String getDataValue()
    {
        return dataValue;
    }

    public void setDataValue( String dataValue )
    {
        this.dataValue = dataValue;
    }
    
}
