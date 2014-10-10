package org.hisp.dhis.coldchain.model.action;

import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeService;
import org.hisp.dhis.i18n.I18n;

import com.opensymphony.xwork2.Action;

public class ValidateModelTypeAction
implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private ModelTypeService modelTypeService;
    
    public void setModelTypeService( ModelTypeService modelTypeService )
    {
        this.modelTypeService = modelTypeService;
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
        
        ModelType match = modelTypeService.getModelTypeByName( name );
        
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

