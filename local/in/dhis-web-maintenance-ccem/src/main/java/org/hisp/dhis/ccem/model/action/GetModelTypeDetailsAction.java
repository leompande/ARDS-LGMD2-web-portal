package org.hisp.dhis.ccem.model.action;

import java.util.Collection;

import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeService;

import com.opensymphony.xwork2.Action;

public class GetModelTypeDetailsAction
implements Action
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

    private int id;

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    private ModelType modelType;

    public ModelType getModelType()
    {
        return modelType;
    }

    private Collection<ModelTypeAttribute> modelTypeAttributes;
    
    public Collection<ModelTypeAttribute> getModelTypeAttributes()
    {
        return modelTypeAttributes;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        
        modelType = modelTypeService.getModelType( id );
        //modelType.getDataEntryForm().getName();
        modelTypeAttributes = modelType.getModelTypeAttributes();
        
        return SUCCESS;
    }
}

