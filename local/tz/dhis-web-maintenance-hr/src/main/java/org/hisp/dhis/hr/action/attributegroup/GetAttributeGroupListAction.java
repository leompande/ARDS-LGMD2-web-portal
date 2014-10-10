package org.hisp.dhis.hr.action.attributegroup;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.hr.AttributeGroup;
import org.hisp.dhis.hr.AttributeGroupService;

import com.opensymphony.xwork2.Action;

public class GetAttributeGroupListAction 
implements Action{
	
	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private AttributeGroupService attributeGroupService;

    public void setAttributeGroupService( AttributeGroupService attributeGroupService )
    {
        this.attributeGroupService = attributeGroupService;
    }   
    
    

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
   
    
    private Collection<AttributeGroup> attributeGroups = new ArrayList<AttributeGroup>();

    public Collection<AttributeGroup> getAttributeGroup()
    {
        return attributeGroups;
    }
    

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    
    public String execute()
    throws Exception   {
    	
    	attributeGroups = attributeGroupService.getAllAttributeGroup();

        return SUCCESS;
    	
    }

}