package org.hisp.dhis.ccem.model.action;

import org.hisp.dhis.coldchain.model.ModelTypeAttributeGroup;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeGroupService;
import org.hisp.dhis.i18n.I18n;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version ValidateModelTypeAttributeGroupAction.javaOct 10, 2012 12:55:25 PM	
 */

public class ValidateModelTypeAttributeGroupAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
    
    private ModelTypeAttributeGroupService modelTypeAttributeGroupService;
    
    public void setModelTypeAttributeGroupService( ModelTypeAttributeGroupService modelTypeAttributeGroupService )
    {
        this.modelTypeAttributeGroupService = modelTypeAttributeGroupService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        name = name.trim();
        ModelTypeAttributeGroup match = modelTypeAttributeGroupService.getModelTypeAttributeGroupByName( name );
        
        if ( match != null && (id == null || match.getId() != id.intValue()) )
        {
            message = i18n.getString( "duplicate_names" );

            return ERROR;
        }
        // ---------------------------------------------------------------------
        // Validation success
        // ---------------------------------------------------------------------

        message = i18n.getString( "everything_is_ok" );

        return SUCCESS;
    }
}


