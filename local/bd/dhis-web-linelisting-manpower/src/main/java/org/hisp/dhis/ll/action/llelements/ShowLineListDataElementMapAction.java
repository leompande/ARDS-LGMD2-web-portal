package org.hisp.dhis.ll.action.llelements;

import java.util.List;

import org.hisp.dhis.linelisting.LineListDataElementMap;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListService;

import com.opensymphony.xwork2.Action;

public class ShowLineListDataElementMapAction
    implements Action
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

    private String lineListElementId;

    public void setLineListElementId( String lineListElementId )
    {
        this.lineListElementId = lineListElementId;
    }

    private List<LineListDataElementMap> lineListDataElementMap;

    public List<LineListDataElementMap> getLineListDataElementMap()
    {
        return lineListDataElementMap;
    }

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        LineListElement lineListElement = lineListService.getLineListElement( Integer.parseInt( lineListElementId ) );

        // lineListDataElementMap =
        // lineListService.getLinelistDataelementMappings( lineListElement, );

        return SUCCESS;
    }

}
