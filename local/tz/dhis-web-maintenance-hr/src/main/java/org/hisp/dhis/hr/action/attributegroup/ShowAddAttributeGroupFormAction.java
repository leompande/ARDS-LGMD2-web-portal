package org.hisp.dhis.hr.action.attributegroup;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeService;

import com.opensymphony.xwork2.Action;

public class ShowAddAttributeGroupFormAction 
implements Action{
	
	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private AttributeService attributeService;

    public void setAttributeService( AttributeService attributeService )
    {
        this.attributeService = attributeService;
    }   
    
    

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
   
    
    private Collection<Attribute> attributes = new ArrayList<Attribute>();

    public Collection<Attribute> getAttribute()
    {
        return attributes;
    }
    

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    
    public String execute()
    throws Exception   {
    	
    	attributes = attributeService.getAllAttribute();

        return SUCCESS;
    	
    }

}