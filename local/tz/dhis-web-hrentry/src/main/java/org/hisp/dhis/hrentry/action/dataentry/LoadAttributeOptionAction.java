package org.hisp.dhis.hrentry.action.dataentry;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeOptions;
import org.hisp.dhis.hr.AttributeService;

import com.opensymphony.xwork2.Action;

public class LoadAttributeOptionAction 
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
    // Input
    // -------------------------------------------------------------------------

    private int attributeId;

    public void setAttributeId( int attributeId )
    {
        this.attributeId = attributeId;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Collection<AttributeOptions> attributesOptions = new ArrayList<AttributeOptions>();

    public Collection<AttributeOptions> getAttributesOptions()
    {
        return attributesOptions;
    }


    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
    	Attribute attribute = attributeService.getAttribute( attributeId );
        
        if ( attribute != null )
        {
        	// -----------------------------------------------------------------
            // Load AttributeOptions for selected Attribute
            // -----------------------------------------------------------------

        	attributesOptions =  attribute.getAttributeOptions();
        }

        return SUCCESS;
    }
}
