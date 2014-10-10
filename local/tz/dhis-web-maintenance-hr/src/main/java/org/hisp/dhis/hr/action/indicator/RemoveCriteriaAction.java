package org.hisp.dhis.hr.action.indicator;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.hr.AggregateAttribute;
import org.hisp.dhis.hr.AggregateAttributeService;
import org.hisp.dhis.hr.Criteria;
import org.hisp.dhis.hr.CriteriaService;

import com.opensymphony.xwork2.Action;

public class RemoveCriteriaAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	private CriteriaService criteriaService;
	
	public void setCriteriaService( CriteriaService criteriaService )
	{
		this.criteriaService = criteriaService;
	}
	
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
    
    private String message;
    
    public String getMessage()
    {
    	return message;
    }
    
    public Criteria criteria;
    
    public void setCriteria(Criteria criteria)
    {
    	this.criteria = criteria;
    }

    public String execute()
        throws Exception
    {
    	criteria = criteriaService.getCriteria(id);
    	Collection<Criteria> criteriasInAggregateAttribute = new ArrayList<Criteria>();
    	for( AggregateAttribute aggregateAttribute : aggregateAttributeService.getAllAggregateAttribute() )
    	{
    		criteriasInAggregateAttribute.addAll(aggregateAttribute.getCriterias());
    	}
    	if ( criteriasInAggregateAttribute.contains(criteria) )
    	{
    		message = new String("Criteria is used.");
    		return ERROR;
    	}
    	else
    	{
    		criteriaService.deleteCriteria(criteria);
    		message = new String("Criteria Deleted");
    		return SUCCESS;
    	}
    }
}

