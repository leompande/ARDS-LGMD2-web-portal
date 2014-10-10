package org.hisp.dhis.ccem.model.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.coldchain.model.Model;
import org.hisp.dhis.coldchain.model.ModelAttributeValue;
import org.hisp.dhis.coldchain.model.ModelService;
import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeOption;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeOptionService;
import org.hisp.dhis.coldchain.model.ModelTypeService;

import com.opensymphony.xwork2.Action;

public class AddModelAction
implements Action
{
    public static final String PREFIX_ATTRIBUTE = "attr";

    //public static final String PREFIX_IDENTIFIER = "iden";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private ModelService modelService;
    
    public void setModelService( ModelService modelService )
    {
        this.modelService = modelService;
    }
    
    private ModelTypeService modelTypeService;
    
    public void setModelTypeService( ModelTypeService modelTypeService )
    {
        this.modelTypeService = modelTypeService;
    }
    
    private ModelTypeAttributeOptionService modelTypeAttributeOptionService;
    
    public void setModelTypeAttributeOptionService( ModelTypeAttributeOptionService modelTypeAttributeOptionService )
    {
        this.modelTypeAttributeOptionService = modelTypeAttributeOptionService;
    }
    /*
    private ModelAttributeValueService modelAttributeValueService;
    
    public void setModelAttributeValueService( ModelAttributeValueService modelAttributeValueService )
    {
        this.modelAttributeValueService = modelAttributeValueService;
    }
    */
    
    // -------------------------------------------------------------------------
    // Input/output Getter/Setter
    // -------------------------------------------------------------------------



    private String name;
    
    public void setName( String name )
    {
        this.name = name;
    }
    
    private String description;
    
    public void setDescription( String description )
    {
        this.description = description;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }
    
    private int modelType;
    
    public void setModelType( int modelType )
    {
        this.modelType = modelType;
    }

    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        // ---------------------------------------------------------------------
        // Set Name, Description, ModelType
        // ---------------------------------------------------------------------

        Model model = new Model();
        model.setName( name );
        model.setDescription( description );
        
        ModelType tempmodelType = modelTypeService.getModelType( modelType );
        model.setModelType( tempmodelType );
        
        //modelService.addModel( model );
        

        // -----------------------------------------------------------------------------
        // Prepare model type Attributes
        // -----------------------------------------------------------------------------
        
        HttpServletRequest request = ServletActionContext.getRequest();
        
        Collection<ModelTypeAttribute> modelTypeAttributes = tempmodelType.getModelTypeAttributes();
        
        List<ModelAttributeValue> modelAttributeValues = new ArrayList<ModelAttributeValue>();
        
        String value = null;
        
        ModelAttributeValue modelAttributeValue = null;
        
        if ( modelTypeAttributes != null && modelTypeAttributes.size() > 0 )
        {
            for ( ModelTypeAttribute modelTypeAttribute : modelTypeAttributes )
            {
                value = request.getParameter( PREFIX_ATTRIBUTE + modelTypeAttribute.getId() );
                if ( StringUtils.isNotBlank( value ) )
                {
                    modelAttributeValue = new ModelAttributeValue();
                    modelAttributeValue.setModel( model );
                    modelAttributeValue.setModelTypeAttribute( modelTypeAttribute );
                    
                    if ( ModelTypeAttribute.TYPE_COMBO.equalsIgnoreCase( modelTypeAttribute.getValueType() ) )
                    {
                        ModelTypeAttributeOption option = modelTypeAttributeOptionService.getModelTypeAttributeOption( NumberUtils.toInt( value ) );
                        
                        if ( option != null )
                        {
                            modelAttributeValue.setModelTypeAttributeOption( option );
                            modelAttributeValue.setValue( option.getName() );
                        }
                        else
                        {
                            
                        }
                    }
                    else
                    {
                        modelAttributeValue.setValue( value.trim() );
                    }
                    
                    modelAttributeValues.add( modelAttributeValue );
                    
                    //modelAttributeValueService.addModelAttributeValue( modelAttributeValue );
                }
            }
        }
        /*
        System.out.println( "Size of model Data Values  :" + modelAttributeValues.size() );
        for( ModelAttributeValue  tempmodelAttributeValue  : modelAttributeValues )
        {
            System.out.println( "Name :" + tempmodelAttributeValue.getModel().getName() );
            System.out.println( "Model Type Attribute Name :" + tempmodelAttributeValue.getModelTypeAttribute().getName() );
            System.out.println( "Value :" + tempmodelAttributeValue.getValue() );
        }
        */
        // -------------------------------------------------------------------------
        // Save model
        // -------------------------------------------------------------------------

            
        Integer id = modelService.createModel(  model, modelAttributeValues );

            message = id + "";

            return SUCCESS;
    }

}

