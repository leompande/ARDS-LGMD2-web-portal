package org.hisp.dhis.excelimport.api;

import java.util.List;
import java.util.Map;

public class XMLMapParameters
{

    private Map<String,List<String>> xmlNodes;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public XMLMapParameters()
    {
        
    }
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public Map<String, List<String>> getXmlNodes()
    {
        return xmlNodes;
    }

    public void setXmlNodes( Map<String, List<String>> xmlNodes )
    {
        this.xmlNodes = xmlNodes;
    }
    
}
