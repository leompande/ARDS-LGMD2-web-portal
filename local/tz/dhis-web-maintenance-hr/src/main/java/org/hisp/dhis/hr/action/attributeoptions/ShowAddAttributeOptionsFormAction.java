package org.hisp.dhis.hr.action.attributeoptions;

import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeService;

import com.opensymphony.xwork2.Action;

public class ShowAddAttributeOptionsFormAction 
implements Action
{
	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
   
    private AttributeService attributeService;

    public void setAttributeService( AttributeService attributeService )
    {
        this.attributeService = attributeService;
    }
  
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------
   
    private Attribute attributes;

    public Attribute getAttribute()
    {
        return attributes;
    }
    
    private int attributeId;
    
    public void setAttributeId( int attributeId ) {
    	this.attributeId = attributeId;
    }
    
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        
    	attributes = attributeService.getAttribute( attributeId );

        return SUCCESS;
    }
}
