package org.hisp.dhis.hr.action.hrdataset;

import java.util.Collection;
import java.util.HashSet;

import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeService;
import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.HrDataSetService;

import com.opensymphony.xwork2.Action;

public class UpdateHrDataSetAction implements Action
{

	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
	private AttributeService attributeService;
	
	public void setAttributeService(AttributeService attributeService) {
		this.attributeService = attributeService;
	}
    private HrDataSetService hrDataSetService;
    
    public void setHrDataSetService( HrDataSetService hrDataSetService ) {
    	this.hrDataSetService = hrDataSetService;
    }
    
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
    private int id;

    public void setId( int id )
    {
        this.id = id;
    }

    private String nameField;

    public void setNameField( String nameField )
    {
        this.nameField = nameField;
    }

    private String description;

    public void setDescription( String description )
    {
        this.description = description;
    }

    private Collection<String> selectedAttributes = new HashSet<String> ();

    public void setSelectedAttributes( Collection<String> selectedAttributes )
    {
        this.selectedAttributes = selectedAttributes;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    	throws Exception
    {
        HrDataSet hrDataSet = hrDataSetService.getHrDataSet( id );
    	
        hrDataSet.setName( nameField );
    	
    	hrDataSet.setDescription( description.trim() );

        // Add attributes only if there were any selected.
    	if( selectedAttributes.size() > 0 ) {
    	
	        Collection<Attribute> attributes = new HashSet<Attribute>();
	        for ( String attributeId : selectedAttributes )
	        {
	            Attribute attribute = attributeService.getAttribute( Integer.parseInt( attributeId ) );
	            attributes.add( attribute );
	        }
	        
	        hrDataSet.setAttribute(attributes);
    	}
        hrDataSetService.updateHrDataSet(hrDataSet);
        return SUCCESS;
    }
}