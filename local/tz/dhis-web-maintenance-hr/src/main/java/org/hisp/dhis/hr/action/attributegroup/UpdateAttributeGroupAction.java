package org.hisp.dhis.hr.action.attributegroup;


import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeGroup;
import org.hisp.dhis.hr.AttributeGroupService;
import org.hisp.dhis.hr.AttributeService;

import com.opensymphony.xwork2.Action;

public class UpdateAttributeGroupAction 
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

    private AttributeService attributeService;

    public void setAttributeService( AttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer id;

    private String nameField;

    private String[] selectedAttributes;

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public void setId( Integer id )
    {
        this.id = id;
    }

    public void setNameField( String nameField )
    {
        this.nameField = nameField;
    }

    public void setSelectedAttributes( String[] selectedAttributes )
    {
        this.selectedAttributes = selectedAttributes;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        AttributeGroup attributeGroup = attributeGroupService.getAttributeGroup( id );

        attributeGroup.setName( nameField );

        Set<Attribute> attributes = new HashSet<Attribute>();
        for ( String attributeId : selectedAttributes )
        {
            Attribute attribute = attributeService.getAttribute( Integer
                .parseInt( attributeId ) );
            attributes.add( attribute );
        }
        attributeGroup.setMembers( attributes );

        attributeGroupService.updateAttributeGroup( attributeGroup );

        return SUCCESS;
    }
}