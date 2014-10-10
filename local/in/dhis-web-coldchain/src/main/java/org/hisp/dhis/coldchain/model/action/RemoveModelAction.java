package org.hisp.dhis.coldchain.model.action;

import org.hisp.dhis.coldchain.model.Model;
import org.hisp.dhis.coldchain.model.ModelService;
import org.hisp.dhis.common.DeleteNotAllowedException;
import org.hisp.dhis.i18n.I18n;

import com.opensymphony.xwork2.Action;

public class RemoveModelAction
implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private ModelService modelService;
    
    public void setModelService( ModelService modelService )
    {
        this.modelService = modelService;
    }
    /*
    private ModelAttributeValueService modelAttributeValueService;
    
    
    public void setModelAttributeValueService( ModelAttributeValueService modelAttributeValueService )
    {
        this.modelAttributeValueService = modelAttributeValueService;
    }
    */

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int id;

    public void setId( int id )
    {
        this.id = id;
    }
    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        try
        {
            Model model = modelService.getModel( id );
            
            //modelService.deleteModelData( model );
            
            //modelService.deleteModelData( modelService.getModel( id ) );
            
            //Collection<ModelAttributeValue> valuesForDelete = modelAttributeValueService.getAllModelAttributeValuesByModel( modelService.getModel( id )) ;
            
            modelService.deleteModelAndDataValue( model );
           
            modelService.deleteModel( model );
            
        }
        catch ( DeleteNotAllowedException ex )
        {
            if ( ex.getErrorCode().equals( DeleteNotAllowedException.ERROR_ASSOCIATED_BY_OTHER_OBJECTS ) )
            {
                message = i18n.getString( "object_not_deleted_associated_by_objects" ) + " " + ex.getMessage();
            }
            
            return INPUT;
        }
        
        catch ( Exception ex )
        {
            message = i18n.getString( "object_not_deleted_associated_by_objects" );
           
            return ERROR;
        }
        
        
        return SUCCESS;
    }
}

