package org.hisp.dhis.hr.action.attributeoptions;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeService;
import org.hisp.dhis.hr.AttributeOptions;
import org.hisp.dhis.hr.AttributeOptionsService;
import com.opensymphony.xwork2.Action;

public class GetAttributeOptionListAction 
implements Action{
	
	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private AttributeService attributeService;

    public void setAttributeService( AttributeService attributeService )
    {
        this.attributeService = attributeService;
    }   
    
    private AttributeOptionsService attributeOptionsService;

    public void setAttributeOptionsService( AttributeOptionsService attributeOptionsService )
    {
        this.attributeOptionsService = attributeOptionsService;
    } 

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
   
    private int attributeId;

    public void setAttributeId( int attributeId )
    {
        this.attributeId = attributeId;
    }
    
    private Attribute attributes;

    public Attribute getAttribute()
    {
        return attributes;
    }
    
    private Collection<AttributeOptions> attributesOptions = new ArrayList<AttributeOptions>();

    public Collection<AttributeOptions> getAttributeOptions()
    {
        return attributesOptions;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    
    public String execute()
    throws Exception   {
    	
    	attributes = attributeService.getAttribute( attributeId );
    	
    	attributesOptions = attributeOptionsService.getAttributeOptionsByAttribute( attributes );

        return SUCCESS;
    	
    }

}
