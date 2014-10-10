package org.hisp.dhis.hr.action.indicator;

import org.hisp.dhis.hr.AggregateAttribute;
import org.hisp.dhis.hr.AggregateAttributeService;
import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeService;

import com.opensymphony.xwork2.Action;

public class RemoveAggregateIndicatorAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	
	private AggregateAttributeService aggregateAttributeService;
	
	public void setAggregateAttributeService( AggregateAttributeService aggregateAttributeService )
	{
		this.aggregateAttributeService = aggregateAttributeService;
	}

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int id;

    public void setId( int id )
    {
        this.id = id;
    }

    public String execute()
        throws Exception
    {
        AggregateAttribute aggregateAttribute = aggregateAttributeService.getAggregateAttribute(id);
        
        aggregateAttributeService.deleteAggregateAttribute(aggregateAttribute);
        
        return SUCCESS;
    }
}

