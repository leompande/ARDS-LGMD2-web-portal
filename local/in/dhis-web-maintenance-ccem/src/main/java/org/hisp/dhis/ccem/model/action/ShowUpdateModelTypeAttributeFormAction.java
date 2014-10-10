package org.hisp.dhis.ccem.model.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeService;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;

import com.opensymphony.xwork2.Action;

public class ShowUpdateModelTypeAttributeFormAction
implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private ModelTypeAttributeService modelTypeAttributeService;

    public void setModelTypeAttributeService( ModelTypeAttributeService modelTypeAttributeService )
    {
        this.modelTypeAttributeService = modelTypeAttributeService;
    }

    private OptionService optionService;
    
    public void setOptionService(OptionService optionService) 
    {
        this.optionService = optionService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int id;

    public void setId( int id )
    {
        this.id = id;
    }

    private ModelTypeAttribute modelTypeAttribute;

    public ModelTypeAttribute getModelTypeAttribute()
    {
        return modelTypeAttribute;
    }

    private List<OptionSet> optionSets;

    public List<OptionSet> getOptionSets() 
    {
	return optionSets;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        modelTypeAttribute = modelTypeAttributeService.getModelTypeAttribute( id );
        
        optionSets = new ArrayList<OptionSet>( optionService.getAllOptionSets() );
        
        Collections.sort( optionSets, new IdentifiableObjectNameComparator() );
        
        return SUCCESS;
    }
}
