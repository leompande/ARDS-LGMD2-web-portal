package org.hisp.dhis.hr.action.indicator;

import java.util.Collection;

import org.hisp.dhis.hr.AggregateAttribute;
import org.hisp.dhis.hr.AggregateAttributeService;
import org.hisp.dhis.hr.Criteria;
import org.hisp.dhis.hr.CriteriaService;

import com.opensymphony.xwork2.Action;

public class GetCriteriaListAction 
implements Action{
	
	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	private CriteriaService criteriaService;
	
	public void setCriteriaService( CriteriaService criteriaService )
	{
		this.criteriaService = criteriaService;
	}

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
    
   
    private Collection<Criteria> criterias;
    
    public Collection<Criteria> getCriterias()
    {
    	return criterias;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    
    public String execute()
    throws Exception   {
    	
    	criterias = criteriaService.getAllCriteria();

        return SUCCESS;
    	
    }

}
