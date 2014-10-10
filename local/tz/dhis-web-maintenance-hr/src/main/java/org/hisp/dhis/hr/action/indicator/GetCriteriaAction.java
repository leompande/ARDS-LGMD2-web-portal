package org.hisp.dhis.hr.action.indicator;


import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.hr.AggregateAttribute;
import org.hisp.dhis.hr.AggregateAttributeService;
import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeOptions;
import org.hisp.dhis.hr.AttributeOptionsService;
import org.hisp.dhis.hr.Criteria;
import org.hisp.dhis.hr.CriteriaService;

import com.opensymphony.xwork2.Action;

public class GetCriteriaAction 
implements Action
{
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	
	private CriteriaService criteriaService;
	
	public void setCriteriaService(CriteriaService criteriaService)
	{
		this.criteriaService = criteriaService;
	}

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int id;

    public void setId( int id )
    {
        this.id = id;
    }
    
    private Attribute attribute;
    
    public Attribute getAttribute()
    {
    	return attribute;
    }
    
    private Criteria criteria;
    
    public Criteria getCriteria()
    {
    	return criteria;
    }
    
    
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
    	criteria = criteriaService.getCriteria(id);
    	attribute = criteria.getAttribute();
    	
        return SUCCESS;
    }

}
