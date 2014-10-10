package org.hisp.dhis.hr.action.attributeoptiongroup;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.hr.AttributeOptions;
import org.hisp.dhis.hr.AttributeOptionGroup;
import org.hisp.dhis.hr.AttributeOptionGroupService;
import org.hisp.dhis.hr.AttributeOptionsService;

import com.opensymphony.xwork2.Action;

public class GetAttributeOptionGroupAction implements Action
{
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private AttributeOptionGroupService attributeOptionGroupService;

    public void setAttributeOptionGroupService( AttributeOptionGroupService attributeOptionGroupService )
    {
        this.attributeOptionGroupService = attributeOptionGroupService;
    }
    
    private AttributeOptionsService attributeOptionsService;

    public void setAttributeOptionsService( AttributeOptionsService attributeOptionsService )
    {
        this.attributeOptionsService = attributeOptionsService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int id;

    public void setId( int id )
    {
        this.id = id;
    }

    private AttributeOptionGroup attributeOptionGroups;

    public AttributeOptionGroup getAttributeOptionGroup()
    {
        return attributeOptionGroups;
    } 
    
    private Collection<AttributeOptions> attributeOptions;

    public Collection<AttributeOptions> getAttributeOptions()
    {
        return attributeOptions;
    }
    
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        attributeOptionGroups = attributeOptionGroupService.getAttributeOptionGroup( id );
        
        attributeOptions = new ArrayList<AttributeOptions> (attributeOptionsService.getAllAttributeOptions() );

        return SUCCESS;
    }

}