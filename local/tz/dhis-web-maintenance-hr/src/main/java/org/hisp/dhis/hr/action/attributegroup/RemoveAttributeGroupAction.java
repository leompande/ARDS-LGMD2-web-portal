package org.hisp.dhis.hr.action.attributegroup;

import org.hisp.dhis.hr.AttributeGroup;
import org.hisp.dhis.hr.AttributeGroupService;

import com.opensymphony.xwork2.Action;

public class RemoveAttributeGroupAction 
implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private AttributeGroupService attributeGroupService;

    public void setAttributeGroupService( AttributeGroupService attributeGroupService )
    {
        this.attributeGroupService = attributeGroupService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int id;

    public void setId( int id )
    {
        this.id = id;
    }

    public String execute()
        throws Exception
    {
        AttributeGroup attributeGroup = attributeGroupService.getAttributeGroup( id );

        attributeGroupService.deleteAttributeGroup( attributeGroup );

        return SUCCESS;
    }
}
