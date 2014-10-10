package org.hisp.dhis.coldchain.model.action;

import org.hisp.dhis.coldchain.model.ModelTypeAttributeOption;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeOptionService;
import org.hisp.dhis.i18n.I18n;

import com.opensymphony.xwork2.Action;

public class RemoveModelTypeAttributeOptionAction
implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ModelTypeAttributeOptionService modelTypeAttributeOptionService;
    
    public void setModelTypeAttributeOptionService( ModelTypeAttributeOptionService modelTypeAttributeOptionService )
    {
        this.modelTypeAttributeOptionService = modelTypeAttributeOptionService;
    }
    
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int id;

    private String message;

    private I18n i18n;

    // -------------------------------------------------------------------------
    // Getter && Setter
    // -------------------------------------------------------------------------


    public void setId( int id )
    {
        this.id = id;
    }

    public String getMessage()
    {
        return message;
    }

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
        ModelTypeAttributeOption modelTypeAttributeOption = modelTypeAttributeOptionService.getModelTypeAttributeOption( id );
       
        if ( modelTypeAttributeOption != null )
        {
            /*
            int count = modelTypeAttributeOptionService.countByModelTypeAttributeoption( modelTypeAttributeOption );
            if ( count > 0 )
            {
                message = i18n.getString( "warning_delete_modelType_attribute_option" );
                return INPUT;
            }
            */
            //else
            //{
                modelTypeAttributeOptionService.deleteModelTypeAttributeOption( modelTypeAttributeOption );
                message = i18n.getString( "success_delete_ctalogType_attribute_option" );
                return SUCCESS;
            //}
        }
        else
        {
            message = i18n.getString( "error_delete_modelType_attribute_option" );
            return ERROR;
        }

    }

}

