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
 * @version EquipmentModelsAction.javaOct 22, 2012 3:54:13 PM	
 */

public class EquipmentModelsAction implements Action
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

    private List<ModelType> modelTypes;
    
    public List<ModelType> getModelTypes()
    {
        return modelTypes;
    }
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        
        modelTypes = new ArrayList<ModelType>( modelTypeService.getAllModelTypes() );
        
        Iterator<ModelType> modelTypesIterator = modelTypes.iterator();
        while( modelTypesIterator.hasNext() )
        {
            ModelType modelType = modelTypesIterator.next();
            
            if ( modelType.getDescription().equalsIgnoreCase( ModelType.PREFIX_MODEL_TYPE ) )
            {
                modelTypesIterator.remove( );
            }
            
        }
        
        return SUCCESS;
    }

}



