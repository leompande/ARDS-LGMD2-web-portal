package org.hisp.dhis.ll.action.aggmap;

import java.util.List;

import org.hisp.dhis.linelisting.LineListDataElementMap;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListOption;
import org.hisp.dhis.linelisting.LineListService;

import com.opensymphony.xwork2.Action;

public class GetSelDataElementAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LineListService lineListService;
    
    public void setLineListService( LineListService lineListService )
    {
        this.lineListService = lineListService;
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
    
    private String id;
    
    public String getId()
    {
        return id;
    }
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        LineListElement lineListElement = lineListService.getLineListElement( lineListElementId );
        
        LineListOption lineListOption = lineListService.getLineListOption( lineListOptionId );
        
        List<LineListDataElementMap> lineListDataElementMaps = lineListService.getLinelistDataelementMappings( lineListElement, lineListOption );
        
        if( lineListDataElementMaps != null && lineListDataElementMaps.size() !=0 )
        {
            LineListDataElementMap lineListDataElementMap = lineListDataElementMaps.get( 0 );
            
            id = lineListDataElementMap.getDataElement().getId() + ":" + lineListDataElementMap.getDataElementOptionCombo().getId();
        }
        else
        {
            id = "NA";
        }
        
        return SUCCESS;
    }
}
