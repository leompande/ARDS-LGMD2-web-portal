package org.hisp.dhis.ccem.model.action;

import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version AddModelTypeAttributeGroupFormAction.javaOct 10, 2012 12:54:52 PM	
 */

public class AddModelTypeAttributeGroupFormAction implements Action
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
    // Input/Output
    // -------------------------------------------------------------------------
    
    private String modelTypeId;
    
    public void setModelTypeId( String modelTypeId )
    {
        this.modelTypeId = modelTypeId;
    }
    
    public String getModelTypeId()
    {
        return modelTypeId;
    }
    
    private ModelType modelType;

    public ModelType getModelType()
    {
        return modelType;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------


    public String execute() throws Exception
    {
        modelType = modelTypeService.getModelType( Integer.parseInt( modelTypeId ) );
        return SUCCESS;
    }
}

