package org.hisp.dhis.ll.action.aggmap;

import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.linelisting.LineListDataElementMap;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListOption;
import org.hisp.dhis.linelisting.LineListService;

import com.opensymphony.xwork2.Action;

public class SaveSanctionedPostMapAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LineListService lineListService;
    
    public void setLineListService( LineListService lineListService )
    {
        this.lineListService = lineListService;
    }

    private DataElementService dataElementService;
    
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService dataElementCategoryService;
    
    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer lineListElementId;

    public void setLineListElementId( Integer lineListElementId )
    {
        this.lineListElementId = lineListElementId;
    }

    private Integer lineListOptionId;
    
    public void setLineListOptionId( Integer lineListOptionId )
    {
        this.lineListOptionId = lineListOptionId;
    }

    private Integer dataElementId;

    public void setDataElementId( Integer dataElementId )
    {
        this.dataElementId = dataElementId;
    }

    private Integer deCOCId;

    public void setDeCOCId( Integer deCOCId )
    {
        this.deCOCId = deCOCId;
    }

    private String statusMsg;

    public String getStatusMsg()
    {
        return statusMsg;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        try
        {
            LineListElement lineListElement = lineListService.getLineListElement( lineListElementId );
            
            LineListOption lineListOption = lineListService.getLineListOption( lineListOptionId );
            
            DataElement dataElement = dataElementService.getDataElement( dataElementId );
            
            DataElementCategoryOptionCombo deCOC = dataElementCategoryService.getDataElementCategoryOptionCombo( deCOCId );
            
            List<LineListDataElementMap> lineListDataElementMaps = lineListService.getLinelistDataelementMappings( lineListElement, lineListOption );
            
            for( LineListDataElementMap lineListDataElementMap : lineListDataElementMaps )
            {
                lineListService.deleteLinelistDataelementMapping( lineListDataElementMap );
            }
            
            LineListDataElementMap lineListDataElementMap = new LineListDataElementMap( lineListElement, lineListOption, dataElement, deCOC );
                
            lineListService.addLinelistDataelementMapping( lineListDataElementMap );
            
            statusMsg = "Mapping has been saved Successfully";
        }
        catch( Exception e )
        {
            statusMsg = "ERROR: "+e.getMessage();
        }
        
        return SUCCESS;
    }
}
