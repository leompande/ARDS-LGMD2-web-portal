package org.hisp.dhis.ccem.model.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version GetModelTypeAttributeListAction.java Jun 22, 2012 2:10:54 PM	
 */
public class GetModelTypeAttributeListAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
    
    private ModelTypeService modelTypeService;
    
    public void setModelTypeService( ModelTypeService modelTypeService )
    {
        this.modelTypeService = modelTypeService;
    }
    
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    
    private List<ModelTypeAttribute> modelTypeAttributes = new ArrayList<ModelTypeAttribute>();
    
    public List<ModelTypeAttribute> getModelTypeAttributes()
    {
        return modelTypeAttributes;
    }

    private Integer id;
    
    public void setId( Integer id )
    {
        this.id = id;
    }
    
    
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        ModelType modelType = modelTypeService.getModelType( id );
        
        modelTypeAttributes = modelType.getModelTypeAttributes();
        
        return SUCCESS;
    }
    
    
}
