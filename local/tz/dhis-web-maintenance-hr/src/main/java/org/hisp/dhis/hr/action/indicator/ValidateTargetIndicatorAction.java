package org.hisp.dhis.hr.action.indicator;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.hr.TargetIndicator;
import org.hisp.dhis.hr.TargetIndicatorService;

import com.opensymphony.xwork2.Action;

public class ValidateTargetIndicatorAction 
	implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private TargetIndicatorService targetIndicatorService;

    public void setTargetIndicatorService( TargetIndicatorService targetIndicatorService )
    {
        this.targetIndicatorService = targetIndicatorService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------
    
    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private String targetIndicatorName;

    public void setTargetIndicatorName( String targetIndicatorName )
    {
        this.targetIndicatorName = targetIndicatorName;
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

        if ( targetIndicatorName == null )
        {
            message = i18n.getString( "please_specify_a_name" );

            return INPUT;
        }

        else
        {
            targetIndicatorName = targetIndicatorName.trim();

            if ( targetIndicatorName.length() == 0 )
            {
                message = i18n.getString( "please_specify_a_name" );

                return INPUT;
            }
            
            TargetIndicator match = targetIndicatorService.getTargetIndicatorByName( targetIndicatorName );
            
            if ( match != null && ( id == null || match.getId() != id) )
            {
                message = i18n.getString( "name_in_use" );

                return INPUT;
            }
        }           
        
        // ---------------------------------------------------------------------
        // Validation success
        // ---------------------------------------------------------------------

        message = i18n.getString( "every_thing_is_ok" );

        return SUCCESS;

    }
}
