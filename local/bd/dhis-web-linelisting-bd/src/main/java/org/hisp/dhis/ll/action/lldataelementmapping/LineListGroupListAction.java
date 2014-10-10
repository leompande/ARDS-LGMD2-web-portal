package org.hisp.dhis.ll.action.lldataelementmapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListService;
import org.hisp.dhis.linelisting.comparator.LineListGroupNameComparator;

import com.opensymphony.xwork2.Action;

public class LineListGroupListAction
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

    private Comparator<LineListGroup> lineListGroupNameComparator;

    public void setLineListGroupComparator( Comparator<LineListGroup> lineListGroupNameComparator )
    {
        this.lineListGroupNameComparator = lineListGroupNameComparator;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<LineListGroup> lineListGroups;

    public List<LineListGroup> getLineListGroups()
    {
        return lineListGroups;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    public String execute()
    {
        lineListGroups = new ArrayList<LineListGroup>( lineListService.getAllLineListGroups() );

        Collections.sort( lineListGroups, new LineListGroupNameComparator() );

        return SUCCESS;
    }

}
