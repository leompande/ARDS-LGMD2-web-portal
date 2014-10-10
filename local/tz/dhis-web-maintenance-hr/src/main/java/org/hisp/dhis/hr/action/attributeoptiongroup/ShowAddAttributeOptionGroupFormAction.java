package org.hisp.dhis.hr.action.attributeoptiongroup;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeService;
import org.hisp.dhis.hr.AttributeOptions;
import org.hisp.dhis.hr.AttributeOptionsService;

import com.opensymphony.xwork2.Action;

public class ShowAddAttributeOptionGroupFormAction 
implements Action{
	
	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private AttributeOptionsService attributeOptionsService;

    public void setAttributeOptionsService( AttributeOptionsService attributeOptionsService )
    {
        this.attributeOptionsService = attributeOptionsService;
    }  
    
    

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
   
    
    private Collection<AttributeOptions> attributeOptions = new ArrayList<AttributeOptions>();

    public Collection<AttributeOptions> getAttributeOptions()
    {
        return attributeOptions;
    }  

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    
    public String execute()
    throws Exception   {
    	
    	attributeOptions = attributeOptionsService.getAllAttributeOptions();    	

        return SUCCESS;
    	
    }

}