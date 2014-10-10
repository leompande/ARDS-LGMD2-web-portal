package org.hisp.dhis.coldchain.model.dataentryform.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.coldchain.model.ModelDataEntryService;
import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeService;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataentryform.DataEntryForm;

import com.opensymphony.xwork2.Action;

public class ViewModelDataEntryFormAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
    private ModelTypeService modelTypeService;
    
    public void setModelTypeService( ModelTypeService modelTypeService )
    {
        this.modelTypeService = modelTypeService;
    }

    private ModelDataEntryService modelDataEntryService;
    
    public void setModelDataEntryService( ModelDataEntryService modelDataEntryService )
    {
        this.modelDataEntryService = modelDataEntryService;
    }
    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------



    private Integer modelTypeId;

    public void setModelTypeId( Integer modelTypeId )
    {
        this.modelTypeId = modelTypeId;
    }
    
    private DataEntryForm dataEntryForm;

    public DataEntryForm getDataEntryForm()
    {
        return dataEntryForm;
    }
    
    private ModelType modelType;
    
    public ModelType getModelType()
    {
        return modelType;
    }

    private String dataEntryValue;

    public String getDataEntryValue()
    {
        return dataEntryValue;
    }

    private List<ModelTypeAttribute> modelTypeAttributes = new ArrayList<ModelTypeAttribute>();
    
    public List<ModelTypeAttribute> getModelTypeAttributes()
    {
        return modelTypeAttributes;
    }
    
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------


    public String execute()
        throws Exception
    {
        modelType = modelTypeService.getModelType( modelTypeId );
        
        
        // ---------------------------------------------------------------------
        // Get dataEntryForm of selected modeltype
        // ---------------------------------------------------------------------
        
        dataEntryForm = modelType.getDataEntryForm();
        
        if ( dataEntryForm != null )
        {
            dataEntryValue = modelDataEntryService.prepareDataEntryFormForEdit( dataEntryForm.getHtmlCode() );
        }
        else
        {
            dataEntryValue = "";
        }

        // ---------------------------------------------------------------------
        // Get ModelType Attribute
        // ---------------------------------------------------------------------
        
        modelTypeAttributes = new ArrayList<ModelTypeAttribute> ( modelType.getModelTypeAttributes());
        
        Collections.sort( modelTypeAttributes, new IdentifiableObjectNameComparator() );
        
        return SUCCESS;
    }
}
