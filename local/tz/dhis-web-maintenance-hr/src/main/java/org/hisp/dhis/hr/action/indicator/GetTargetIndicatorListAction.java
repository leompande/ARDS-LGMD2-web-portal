package org.hisp.dhis.hr.action.indicator;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.hr.TargetIndicator;
import org.hisp.dhis.hr.TargetIndicatorService;

import com.opensymphony.xwork2.Action;

public class GetTargetIndicatorListAction 
	implements Action{
	
	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	
	private TargetIndicatorService targetIndicatorService;
	
	public void setTargetIndicatorService( TargetIndicatorService  targetIndicatorService )
	{
		this.targetIndicatorService = targetIndicatorService;
	}
	

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
   
    private Collection<TargetIndicator> targetIndicators = new ArrayList<TargetIndicator>();
    
    public Collection<TargetIndicator> getTargetIndicators()
    {
    	return targetIndicators;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    
    public String execute()
    	throws Exception  
    {
    	
    	targetIndicators = targetIndicatorService.getAllTargetIndicator();

        return SUCCESS;
    	
    }

}
