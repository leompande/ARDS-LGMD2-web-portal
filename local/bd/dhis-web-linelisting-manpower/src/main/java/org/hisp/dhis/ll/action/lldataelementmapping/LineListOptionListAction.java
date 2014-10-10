package org.hisp.dhis.ll.action.lldataelementmapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListOption;
import org.hisp.dhis.linelisting.LineListService;
import org.hisp.dhis.linelisting.comparator.LineListOptionNameComparator;

import com.opensymphony.xwork2.Action;

public class LineListOptionListAction
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
    // Comparator
    // -------------------------------------------------------------------------

    private Comparator<LineListOption> lineListOptionNameComparator;

    public void setLineListOptionComparator( Comparator<LineListOption> lineListOptionNameComparator )
    {
        this.lineListOptionNameComparator = lineListOptionNameComparator;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private LineListElement lineListElement;
    
    public void setLineListElement( LineListElement lineListElement )
    {
        this.lineListElement = lineListElement;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<LineListOption> lineListOptions;

    public List<LineListOption> getLineListOptions()
    {
        return lineListOptions;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    public String execute()
    {
        lineListOptions = new ArrayList<LineListOption>(  );
        
        lineListOptions.addAll( lineListElement.getLineListElementOptions() );

        Collections.sort( lineListOptions, new LineListOptionNameComparator() );

        return SUCCESS;
    }

}
