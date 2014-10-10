package org.hisp.dhis.ll.action.llelements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListOption;
import org.hisp.dhis.linelisting.LineListService;
import org.hisp.dhis.linelisting.comparator.LineListOptionNameComparator;
import org.hisp.dhis.options.displayproperty.DisplayPropertyHandler;

import com.opensymphony.xwork2.Action;

public class GetLineListElementOptionsAction
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
    // Input and Output
    // -------------------------------------------------------------------------

    private Integer lineListElementId;

    public void setLineListElementId( Integer lineListElementId )
    {
        this.lineListElementId = lineListElementId;
    }

    private List<LineListOption> lineListElementOptions = new ArrayList<LineListOption>();

    public List<LineListOption> getLineListElementOptions()
    {
        return lineListElementOptions;
    }

    private List<LineListOption> availableLineListOptions = new ArrayList<LineListOption>();

    public List<LineListOption> getAvailableLineListOptions()
    {
        return availableLineListOptions;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        // ---------------------------------------------------------------------
        // Get line list element options
        // ---------------------------------------------------------------------

        if ( lineListElementId != null )
        {
            LineListElement lineListElement = lineListService.getLineListElement( lineListElementId.intValue() );

            lineListElementOptions = new ArrayList<LineListOption>( lineListElement.getLineListElementOptions() );
            
            //Collections.sort( lineListElementOptions, new LineListOptionNameComparator() );

            //displayPropertyHandler.handle( lineListElementOptions );
        }

        // ---------------------------------------------------------------------
        // Get available line list options
        // ---------------------------------------------------------------------

        availableLineListOptions = new ArrayList<LineListOption>( lineListService.getAllLineListOptions() );

        availableLineListOptions.removeAll( lineListElementOptions );

        Collections.sort( availableLineListOptions, new LineListOptionNameComparator() );

       // displayPropertyHandler.handle( availableLineListOptions );

        return SUCCESS;
    }

}
