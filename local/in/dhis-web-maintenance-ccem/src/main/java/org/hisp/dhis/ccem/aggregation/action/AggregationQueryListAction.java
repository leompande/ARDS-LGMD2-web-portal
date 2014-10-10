package org.hisp.dhis.ccem.aggregation.action;

import java.util.Collection;

import org.hisp.dhis.caseaggregation.CaseAggregationCondition;
import org.hisp.dhis.caseaggregation.CaseAggregationConditionService;

import com.opensymphony.xwork2.Action;

public class AggregationQueryListAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private CaseAggregationConditionService aggregationConditionService;

    public void setAggregationConditionService( CaseAggregationConditionService aggregationConditionService )
    {
        this.aggregationConditionService = aggregationConditionService;
    }
    
    // -------------------------------------------------------------------------
    // Input/ Output
    // -------------------------------------------------------------------------

    private Collection<CaseAggregationCondition> aggregationConditions;

    public Collection<CaseAggregationCondition> getAggregationConditions()
    {
        return aggregationConditions;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        aggregationConditions = aggregationConditionService.getAllCaseAggregationCondition();
        
        return SUCCESS;
    }

}
