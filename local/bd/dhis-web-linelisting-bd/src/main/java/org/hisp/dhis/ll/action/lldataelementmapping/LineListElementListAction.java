package org.hisp.dhis.ll.action.lldataelementmapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListService;
import org.hisp.dhis.linelisting.comparator.LineListElementNameComparator;
import org.hisp.dhis.options.displayproperty.DisplayPropertyHandler;

import com.opensymphony.xwork2.Action;

public class LineListElementListAction
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

    private DisplayPropertyHandler displayPropertyHandler;

    public void setDisplayPropertyHandler( DisplayPropertyHandler displayPropertyHandler )
    {
        this.displayPropertyHandler = displayPropertyHandler;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private LineListGroup lineListGroup;
    
    public void setLineListGroup( LineListGroup lineListGroup )
    {
        this.lineListGroup = lineListGroup;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<LineListElement> lineListElements;

    public List<LineListElement> getLineListElements()
    {
        return lineListElements;
    }

    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        lineListElements = new ArrayList<LineListElement>();
        
        lineListElements.addAll( lineListGroup.getLineListElements() );
        
        Collections.sort( lineListElements, new LineListElementNameComparator() );
        
        //displayPropertyHandler.handle( lineListElements );

        return SUCCESS;
    }

}
