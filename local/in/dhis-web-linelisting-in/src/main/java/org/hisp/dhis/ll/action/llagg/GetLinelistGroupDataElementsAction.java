package org.hisp.dhis.ll.action.llagg;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListService;

import com.opensymphony.xwork2.Action;

public class GetLinelistGroupDataElementsAction implements Action
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
    // Input & output
    // -------------------------------------------------------------------------
    private Integer llgId;
    
    public void setLlgId( Integer llgId )
    {
        this.llgId = llgId;
    }
    
    private List<LineListElement> llElements;
    
    public List<LineListElement> getLlElements()
    {
        return llElements;
    }
   
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        llElements = new ArrayList<LineListElement>( lineListService.getLineListGroup( llgId ).getLineListElements() );
        
        System.out.println("Linelistelements size : "+llElements.size());
        return SUCCESS;
    }

}
