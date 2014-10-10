package org.hisp.dhis.coldchain.model.dataentryform.action;

import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeService;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataentryform.DataEntryFormService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version DeleteModelDataEntryFormAction.java Jun 8, 2012 4:33:05 PM	
 */
public class DeleteModelDataEntryFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataEntryFormService dataEntryFormService;

    public void setDataEntryFormService( DataEntryFormService dataEntryFormService )
    {
        this.dataEntryFormService = dataEntryFormService;
    }

   private ModelTypeService modelTypeService;
    
    public void setModelTypeService( ModelTypeService modelTypeService )
    {
        this.modelTypeService = modelTypeService;
    }

    // -------------------------------------------------------------------------
    // Getters & setters
    // -------------------------------------------------------------------------

    private Integer dataEntryFormId;
    
    public void setDataEntryFormId( Integer dataEntryFormId )
    {
        this.dataEntryFormId = dataEntryFormId;
    }


    private String message;

    public String getMessage()
    {
        return message;
    }

    private Integer modelTypeId;

    public void setModelTypeId( Integer modelTypeId )
    {
        this.modelTypeId = modelTypeId;
    }


    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        DataEntryForm dataEntryForm = dataEntryFormService.getDataEntryForm( dataEntryFormId );

        ModelType modelType = modelTypeService.getModelType( modelTypeId );
        
        DataEntryForm modelTypeDataEntryForm = modelType.getDataEntryForm();
        
        if ( modelTypeDataEntryForm != null && modelTypeDataEntryForm.equals( dataEntryForm ) )
        {
            modelType.setDataEntryForm( null );

            modelTypeService.updateModelType( modelType );
        }

        dataEntryFormService.deleteDataEntryForm( dataEntryForm );

        return SUCCESS;
    }
}

