package org.hisp.dhis.hr.action.attributeoptiongroup;

import org.hisp.dhis.hr.AttributeOptionGroup;
import org.hisp.dhis.hr.AttributeOptionGroupService;
import org.hisp.dhis.i18n.I18n;

import com.opensymphony.xwork2.Action;

public class ValidateAttributeOptionGroupAction 
implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private AttributeOptionGroupService attributeOptionGroupService;

    public void setAttributeOptionGroupService( AttributeOptionGroupService attributeOptionGroupService )
    {
        this.attributeOptionGroupService = attributeOptionGroupService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer id;

    private String nameField;

    private String[] selectedAttributes;
    
    private String message;

    private I18n i18n;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    public void setSelectedAttributes( String[] selectedAttributes )
    {
        this.selectedAttributes = selectedAttributes;
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

            AttributeOptionGroup match = attributeOptionGroupService.getAttributeOptionGroupByName( nameField );
            
            if ( match != null && (id == null || match.getId() != id.intValue()) )
            {
                message = i18n.getString( "name_in_use" );

                return INPUT;
            }
        }

        if ( selectedAttributes == null || selectedAttributes.length == 0)
        {
            message = i18n.getString( "please_specify_attributes" );

            return INPUT;
        }
        // ---------------------------------------------------------------------
        // Validation success
        // ---------------------------------------------------------------------

        message = i18n.getString( "everything_is_ok" );

        return SUCCESS;

    }
}
