package org.hisp.dhis.ll.action.llagg;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListService;

import com.opensymphony.xwork2.Action;

public class LinelistingAggQueryBuilderFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private DataElementService dataElementService;
    
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private LineListService lineListService;

    public void setLineListService( LineListService lineListService )
    {
        this.lineListService = lineListService;
    }

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    private List<DataElementGroup> dataElementGroups;
    
    public List<DataElementGroup> getDataElementGroups()
    {
        return dataElementGroups;
    }

    private List<LineListGroup> lineListGroups;

    public List<LineListGroup> getLineListGroups()
    {
        return lineListGroups;
    }
    
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        dataElementGroups = new ArrayList<DataElementGroup>( dataElementService.getAllDataElementGroups() );
        
        lineListGroups = new ArrayList<LineListGroup>( lineListService.getAllLineListGroups() );
        
        return SUCCESS;
    }

}
