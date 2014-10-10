package org.hisp.dhis.ccem.model.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeService;
import org.hisp.dhis.coldchain.model.comparator.ModelTypeComparator;

import com.opensymphony.xwork2.Action;

public class AddModelFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private ModelTypeService modelTypeService;
    
    public void setModelTypeService( ModelTypeService modelTypeService )
    {
        this.modelTypeService = modelTypeService;
    }

    // -------------------------------------------------------------------------
    // Input & Output Getters & Setters
    // -------------------------------------------------------------------------
    
    private List<ModelType> modelTypes;
    
    public List<ModelType> getModelTypes()
    {
        return modelTypes;
    }

    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        modelTypes = new ArrayList<ModelType>( modelTypeService.getAllModelTypes());
        
        Collections.sort( modelTypes, new ModelTypeComparator() );
        
        return SUCCESS;
    }
}

