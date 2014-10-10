package org.hisp.dhis.ccem.model.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;

import com.opensymphony.xwork2.Action;

public class AddColdChainModelTypeAttributeForm  implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private OptionService optionService;
    
    public void setOptionService(OptionService optionService) 
    {
	this.optionService = optionService;
    }
    
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------
    
    private List<OptionSet> optionSets;

    public List<OptionSet> getOptionSets() 
    {
        return optionSets;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
	
    {
	optionSets = new ArrayList<OptionSet>( optionService.getAllOptionSets() );
	
	Collections.sort( optionSets, new IdentifiableObjectNameComparator() );
	
        return SUCCESS;
    }
    
}
