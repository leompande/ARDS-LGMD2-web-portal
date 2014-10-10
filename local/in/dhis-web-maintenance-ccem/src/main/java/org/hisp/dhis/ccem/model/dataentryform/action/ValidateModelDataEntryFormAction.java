package org.hisp.dhis.ccem.model.dataentryform.action;

import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataentryform.DataEntryFormService;
import org.hisp.dhis.i18n.I18n;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version ValidateModelDataEntryFormAction.java Jun 7, 2012 2:42:06 PM	
 */
public class ValidateModelDataEntryFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private DataEntryFormService dataEntryFormService;

    public void setDataEntryFormService( DataEntryFormService dataEntryFormService )
    {
        this.dataEntryFormService = dataEntryFormService;
    }

    // -------------------------------------------------------------------------
    // I18n
    // -------------------------------------------------------------------------

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private Integer modelDataEntryFormId;
    
    public void setModelDataEntryFormId( Integer modelDataEntryFormId )
    {
        this.modelDataEntryFormId = modelDataEntryFormId;
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
        throws Exception
    {
        DataEntryForm match = dataEntryFormService.getDataEntryFormByName( name );

        if ( match != null && ( modelDataEntryFormId == null || match.getId() != modelDataEntryFormId.intValue()) )
        {
            message = i18n.getString( "duplicate_names" );

            return ERROR;
        }

        return SUCCESS;
    }
}

