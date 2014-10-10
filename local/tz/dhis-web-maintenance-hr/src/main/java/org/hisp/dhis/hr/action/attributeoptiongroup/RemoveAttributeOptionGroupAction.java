package org.hisp.dhis.hr.action.attributeoptiongroup;

import org.hisp.dhis.hr.AttributeOptionGroup;
import org.hisp.dhis.hr.AttributeOptionGroupService;

import com.opensymphony.xwork2.Action;

public class RemoveAttributeOptionGroupAction 
implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private AttributeOptionGroupService attributeOptionGroupService;

    public void setAttributeOptionGroupService( AttributeOptionGroupService attributeOptionGroupService )
    {
        this.attributeOptionGroupService = attributeOptionGroupService;
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
        AttributeOptionGroup attributeOptionGroup = attributeOptionGroupService.getAttributeOptionGroup( id );

        attributeOptionGroupService.deleteAttributeOptionGroup( attributeOptionGroup );

        return SUCCESS;
    }
}
