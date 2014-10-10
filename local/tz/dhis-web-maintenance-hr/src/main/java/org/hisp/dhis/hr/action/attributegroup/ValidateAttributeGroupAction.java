package org.hisp.dhis.hr.action.attributegroup;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.hr.AttributeGroup;
import org.hisp.dhis.hr.AttributeGroupService;

import com.opensymphony.xwork2.Action;

public class ValidateAttributeGroupAction 
implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private AttributeGroupService attributeGroupService;

    public void setAttributeGroupService( AttributeGroupService attributeGroupService )
    {
        this.attributeGroupService = attributeGroupService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer id;

    private String nameField;
    
    private String message;

    private I18n i18n;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }    

    public void setId( Integer id )
    {
        this.id = id;
    }

    public void setNameField( String nameField )
    {
        this.nameField = nameField;
    }

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        if ( nameField == null )
        {
            message = i18n.getString( "please_specify_a_name" );

            return INPUT;
        }

        else
        {
            nameField = nameField.trim();

            if ( nameField.length() == 0 )
            {
                message = i18n.getString( "please_specify_a_name" );

                return INPUT;
            }

            AttributeGroup match = attributeGroupService.getAttributeGroupByName( nameField );
            
            if ( match != null && (id == null || match.getId() != id.intValue()) )
            {
                message = i18n.getString( "name_in_use" );

                return INPUT;
            }
        }
        
        // ---------------------------------------------------------------------
        // Validation success
        // ---------------------------------------------------------------------

        message = i18n.getString( "everything_is_ok" );

        return SUCCESS;

    }
}
