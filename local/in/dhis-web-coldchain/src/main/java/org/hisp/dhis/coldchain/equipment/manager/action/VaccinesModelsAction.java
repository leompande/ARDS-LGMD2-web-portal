
package org.hisp.dhis.coldchain.equipment.manager.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version VaccinesModelsAction.javaDec 19, 2012 11:35:46 AM	
 */

public class VaccinesModelsAction implements Action
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
    // Input/output
    // -------------------------------------------------------------------------

    
    private ModelType modelType;
    
    public ModelType getModelType()
    {
        return modelType;
    }


    private List<ModelType> modelTypes;
    
    public List<ModelType> getModelTypes()
    {
        return modelTypes;
    }
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        
        modelType = modelTypeService.getModelTypeByName( ModelType.PREFIX_MODEL_TYPE );
        
        //System.out.println( " model Id is -----" + modelType.getId() + "-- Model name is  " + modelType.getName() );
        
        return SUCCESS;
    }

}

