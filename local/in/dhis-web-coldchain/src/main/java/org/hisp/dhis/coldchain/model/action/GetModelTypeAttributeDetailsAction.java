package org.hisp.dhis.coldchain.model.action;

import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeService;

import com.opensymphony.xwork2.Action;

public class GetModelTypeAttributeDetailsAction
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

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        modelTypeAttribute = modelTypeAttributeService.getModelTypeAttribute( id );
        return SUCCESS;
    }
}
