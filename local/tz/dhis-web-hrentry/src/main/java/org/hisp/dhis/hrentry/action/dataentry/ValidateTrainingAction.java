package org.hisp.dhis.hrentry.action.dataentry;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.hr.Training;
import org.hisp.dhis.hr.TrainingService;

import com.opensymphony.xwork2.Action;


/**
 * @author Ismail Koleleni
 * @version $Id$
 */

public class ValidateTrainingAction 
	implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private TrainingService trainingService;

    public void setTrainingService( TrainingService trainingService )
    {
        this.trainingService = trainingService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------
    
    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private String nameField;

    public void setNameField( String nameField )
    {
        this.nameField = nameField;
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
            
            Training match = trainingService.getTrainingByName( nameField );
            
            if ( match != null && ( id == null || match.getId() != id) )
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
