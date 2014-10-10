package org.hisp.dhis.ccem.model.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.coldchain.model.ModelDataEntryService;
import org.hisp.dhis.coldchain.model.ModelAttributeValue;
import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeOption;
import org.hisp.dhis.coldchain.model.ModelTypeService;
import org.hisp.dhis.coldchain.model.comparator.ModelTypeAttributeOptionComparator;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.i18n.I18n;

import com.opensymphony.xwork2.Action;

public class ShowAddModelFormAction
implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
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
    // Input/Output
    // -------------------------------------------------------------------------
    
    private int modelTypeId;
    
    public void setModelTypeId( int modelTypeId )
    {
        this.modelTypeId = modelTypeId;
    }
    
    private Boolean isCustom;
    
    public Boolean getIsCustom()
    {
        return isCustom;
    }
    
/*
    private Collection<ModelTypeAttribute> modelTypeAttributes;
    
    public Collection<ModelTypeAttribute> getModelTypeAttributes()
    {
        return modelTypeAttributes;
    }
*/    
    
    

    private List<ModelTypeAttribute> modelTypeAttributes = new ArrayList<ModelTypeAttribute>();
    
    public List<ModelTypeAttribute> getModelTypeAttributes()
    {
        return modelTypeAttributes;
    }
    
    private ModelType modelType;
    
    public ModelType getModelType()
    {
        return modelType;
    }
    
    private String customDataEntryFormCode;
    
    public String getCustomDataEntryFormCode()
    {
        return customDataEntryFormCode;
    }

    private I18n i18n;
    
    public I18n getI18n()
    {
        return i18n;
    }

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }
    /*
    private List<ModelTypeAttributeOption> modelTypeAttributesOptions = new ArrayList<ModelTypeAttributeOption>();
    
    public List<ModelTypeAttributeOption> getModelTypeAttributesOptions()
    {
        return modelTypeAttributesOptions;
    }
    */
    
    private Map<Integer, List<ModelTypeAttributeOption>> modelTypeAttributesOptionsMap = new HashMap<Integer, List<ModelTypeAttributeOption>>();
    
    public Map<Integer, List<ModelTypeAttributeOption>> getModelTypeAttributesOptionsMap()
    {
        return modelTypeAttributesOptionsMap;
    }
    
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        System.out.println("entering into  ShowAddModelFormAction action");
        modelType = modelTypeService.getModelType( modelTypeId );
        isCustom = false;
        String disabled = ""; 
        if ( modelType != null )
        {
            // ---------------------------------------------------------------------
            // Get data-entry-form
            // ---------------------------------------------------------------------

            DataEntryForm dataEntryForm = modelType.getDataEntryForm();

            System.out.println("dataentryform object retrieved");
            
            if ( dataEntryForm != null )
            {
                isCustom = true;
                System.out.println("dataentryform object is not null");
                Collection<ModelAttributeValue> modelAttributeValues = new ArrayList<ModelAttributeValue>();
                
                customDataEntryFormCode = modelDataEntryService.prepareDataEntryFormForModel( dataEntryForm.getHtmlCode(), modelAttributeValues, disabled, i18n, modelType );
                //customDataEntryFormCode = "custom dataentry form";
            }
            
            modelTypeAttributes = new ArrayList<ModelTypeAttribute> ( modelType.getModelTypeAttributes() );
            
            for( ModelTypeAttribute modelTypeAttribute : modelTypeAttributes )
            {
                List<ModelTypeAttributeOption> modelTypeAttributesOptions = new ArrayList<ModelTypeAttributeOption>();
                if( ModelTypeAttribute.TYPE_COMBO.equalsIgnoreCase( modelTypeAttribute.getValueType() ) )
                {
                    System.out.println(" inside ModelTypeAttribute.TYPE_COMBO ");
                    modelTypeAttributesOptions = new ArrayList<ModelTypeAttributeOption>( modelTypeAttribute.getAttributeOptions() );
                    Collections.sort( modelTypeAttributesOptions, new ModelTypeAttributeOptionComparator() );
                    modelTypeAttributesOptionsMap.put( modelTypeAttribute.getId(), modelTypeAttributesOptions );
                }

                /*
                System.out.println( "Name :" + modelTypeAttribute.getName() );
                System.out.println( "valueType :" + modelTypeAttribute.getValueType() );
                System.out.println( "Is mandatory :" + modelTypeAttribute.isMandatory() );
                */
            }
            
            
            //Collections.sort( modelTypeAttributes, new ModelTypeAttributeComparator() );
            
            /*
            System.out.println( "Name of ModelType is ======  :" + modelType.getName() );
            System.out.println( "Size of modelTypeAttributes  :" + modelTypeAttributes.size() );
            for( ModelTypeAttribute modelTypeAttribute : modelTypeAttributes )
            {
                System.out.println( "Name :" + modelTypeAttribute.getName() );
                System.out.println( "valueType :" + modelTypeAttribute.getValueType() );
                System.out.println( "Is mandatory :" + modelTypeAttribute.isMandatory() );
            }
            */
        }
        
        System.out.println("going out from  ShowAddModelFormAction action");
        return SUCCESS;
    }


}
