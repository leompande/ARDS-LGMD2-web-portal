package org.hisp.dhis.ll.action.aggmap;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListOption;
import org.hisp.dhis.linelisting.LineListService;

import com.opensymphony.xwork2.Action;

public class GetLineListElementsAction implements Action
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
    // Input/output
    // -------------------------------------------------------------------------

    private List<LineListOption> lineListOptions;
    
    public List<LineListOption> getLineListOptions()
    {
        return lineListOptions;
    }

    private LineListElement lineListElement;
    
    public LineListElement getLineListElement()
    {
        return lineListElement;
    }

    private Integer lineListGroupId;
    
    public void setLineListGroupId( Integer lineListGroupId )
    {
        this.lineListGroupId = lineListGroupId;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        LineListGroup lineListGroup = lineListService.getLineListGroup( lineListGroupId );
        
        lineListElement = lineListGroup.getLineListElements().iterator().next();
        
        lineListOptions = new ArrayList<LineListOption>( lineListElement.getLineListElementOptions() );
        
        return SUCCESS;
    }

}
