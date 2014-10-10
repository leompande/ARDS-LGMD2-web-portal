package org.hisp.dhis.hr.action.indicator;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.hr.AggregateAttribute;
import org.hisp.dhis.hr.AggregateAttributeService;
import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeService;

import com.opensymphony.xwork2.Action;

public class GetAggregateIndicatorListAction 
	implements Action{
	
	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private AggregateAttributeService aggregateAttributeService;

    public void setAggregateAttributeService( AggregateAttributeService aggregateAttributeService )
    {
        this.aggregateAttributeService = aggregateAttributeService;
    }   
    
    

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
   
    
    private Collection<AggregateAttribute> aggregateAttributes = new ArrayList<AggregateAttribute>();

    public Collection<AggregateAttribute> getAggregateAttributes()
    {
        return aggregateAttributes;
    }
    

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    
    public String execute()
    throws Exception   {
    	
    	aggregateAttributes = aggregateAttributeService.getAllAggregateAttribute();

        return SUCCESS;
    	
    }

}
