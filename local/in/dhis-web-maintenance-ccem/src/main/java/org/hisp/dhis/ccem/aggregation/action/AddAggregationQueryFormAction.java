package org.hisp.dhis.ccem.aggregation.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.lookup.Lookup;
import org.hisp.dhis.lookup.LookupService;

import com.opensymphony.xwork2.Action;

public class AddAggregationQueryFormAction implements Action 
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	private LookupService lookupService;

	public void setLookupService(LookupService lookupService) 
	{
		this.lookupService = lookupService;
	}
	
    private DataElementService dataElementService;
    
    public void setDataElementService(DataElementService dataElementService) 
    {
		this.dataElementService = dataElementService;
	}

    // -------------------------------------------------------------------------
    // Input/ Output
    // -------------------------------------------------------------------------
	List<Lookup> lookups;
	
    public List<Lookup> getLookups() 
    {
		return lookups;
	}
	
    List<DataElement> dataElementList;
    
	public List<DataElement> getDataElementList() 
	{
		return dataElementList;
	}
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
	public String execute()
        throws Exception
    {
		lookups = new ArrayList<Lookup>( lookupService.getAllLookupsByType( Lookup.CCEI_AGG_TYPE ) );
		
		dataElementList = new ArrayList<DataElement>( dataElementService.getAllActiveDataElements() );
		
    	return SUCCESS;
    }
}
