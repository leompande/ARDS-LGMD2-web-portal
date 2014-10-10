package org.hisp.dhis.ccem.model.dataentryform.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeService;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataentryform.DataEntryFormService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version SaveModelDataEntryFormAction.java Jun 7, 2012 4:09:05 PM	
 */
public class SaveModelDataEntryFormAction implements Action
{
    Log logger = LogFactory.getLog( getClass() );

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
    // Getters & Setters
    // -------------------------------------------------------------------------

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String designTextarea;

    public void setDesignTextarea( String designTextarea )
    {
        this.designTextarea = designTextarea;
    }

    private Integer modelTypeId;
    
    public void setModelTypeId( Integer modelTypeId )
    {
        this.modelTypeId = modelTypeId;
    }


    private Integer modelDataEntryFormId;
    
    public void setModelDataEntryFormId( Integer modelDataEntryFormId )
    {
        this.modelDataEntryFormId = modelDataEntryFormId;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        ModelType modelType = modelTypeService.getModelType( modelTypeId );
        
        DataEntryForm modelDataEntryForm = null;

        // ---------------------------------------------------------------------
        // Get data-entry-form
        // ---------------------------------------------------------------------

        if ( modelDataEntryFormId == null )
        {
            modelDataEntryForm = modelType.getDataEntryForm();
            
        }
        else
        {
            modelDataEntryForm = dataEntryFormService.getDataEntryForm( modelDataEntryFormId );
        }

        // ---------------------------------------------------------------------
        // Save data-entry-form
        // ---------------------------------------------------------------------

        if ( modelDataEntryForm == null )
        {
            modelDataEntryForm = new DataEntryForm( name, dataEntryFormService.prepareDataEntryFormForSave( designTextarea ) );
            dataEntryFormService.addDataEntryForm( modelDataEntryForm );
        }
        else
        {
            modelDataEntryForm.setName( name );
            modelDataEntryForm.setHtmlCode( dataEntryFormService.prepareDataEntryFormForSave( designTextarea ) );
            dataEntryFormService.updateDataEntryForm( modelDataEntryForm );
        }            
        
        modelType.setDataEntryForm( modelDataEntryForm );
        modelTypeService.updateModelType( modelType );

        return SUCCESS;
    }
    
}

