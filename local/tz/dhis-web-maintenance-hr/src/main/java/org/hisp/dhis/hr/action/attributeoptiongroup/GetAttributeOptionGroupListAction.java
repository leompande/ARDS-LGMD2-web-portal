package org.hisp.dhis.hr.action.attributeoptiongroup;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.hr.AttributeOptionGroup;
import org.hisp.dhis.hr.AttributeOptionGroupService;

import com.opensymphony.xwork2.Action;

public class GetAttributeOptionGroupListAction 
implements Action{
	
	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private AttributeOptionGroupService attributeOptionGroupService;

    public void setAttributeOptionGroupService( AttributeOptionGroupService attributeOptionGroupService )
    {
        this.attributeOptionGroupService = attributeOptionGroupService;
    }   
    
    

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
   
    
    private Collection<AttributeOptionGroup> attributeOptionGroups = new ArrayList<AttributeOptionGroup>();

    public Collection<AttributeOptionGroup> getAttributeOptionGroup()
    {
        return attributeOptionGroups;
    }
    

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    
    public String execute()
    throws Exception   {
    	
    	attributeOptionGroups = attributeOptionGroupService.getAllAttributeOptionGroup();

        return SUCCESS;
    	
    }

}