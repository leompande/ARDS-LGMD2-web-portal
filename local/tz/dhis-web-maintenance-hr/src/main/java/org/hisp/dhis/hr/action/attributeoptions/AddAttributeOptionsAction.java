package org.hisp.dhis.hr.action.attributeoptions;

import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeOptions;
import org.hisp.dhis.hr.AttributeOptionsService;
import org.hisp.dhis.hr.AttributeService;

import com.opensymphony.xwork2.Action;

public class AddAttributeOptionsAction 
implements Action
{
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private AttributeOptionsService attributeOptionsService;

    public void setAttributeOptionsService( AttributeOptionsService attributeOptionsService )
    {
        this.attributeOptionsService = attributeOptionsService;
    }

    private AttributeService attributeService;

    public void setAttributeService( AttributeService attributeService )
    {
        this.attributeService = attributeService;
    }
    
    // -------------------------------------------------------------------------
    // Input/Output
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
    
    private String nameField;

    public void setNameField( String nameField )
    {
        this.nameField = nameField;
    }
     
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
    	attributes = attributeService.getAttribute( attributeId );
    	
    	AttributeOptions attributeOptions = new AttributeOptions();

    	attributeOptions.setValue( nameField );
    	attributeOptions.setAttribute( attributes );
        
    	attributeOptionsService.saveAttributeOptions( attributeOptions );

        return SUCCESS;
    }

}
