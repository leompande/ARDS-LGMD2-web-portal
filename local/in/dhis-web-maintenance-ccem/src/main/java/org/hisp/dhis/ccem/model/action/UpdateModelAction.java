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
import org.hisp.dhis.coldchain.model.ModelAttributeValueService;
import org.hisp.dhis.coldchain.model.ModelService;
import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeOption;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeOptionService;
import org.hisp.dhis.coldchain.model.ModelTypeService;

import com.opensymphony.xwork2.Action;

public class UpdateModelAction
implements Action
{
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
    
    private ModelAttributeValueService modelAttributeValueService;
    
    
    public void setModelAttributeValueService( ModelAttributeValueService modelAttributeValueService )
    {
        this.modelAttributeValueService = modelAttributeValueService;
    }
    
    // -------------------------------------------------------------------------
    // Input/output and Getter/Setter
    // -------------------------------------------------------------------------
    
    private int modelID;
    
    public void setModelID( int modelID )
    {
        this.modelID = modelID;
    }

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
    /*
    private int modelType;
    
    public void setModelType( int modelType )
    {
        this.modelType = modelType;
    }
    */

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Model model;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        
        //System.out.println( "model id is   :" + modelID );
        
        model = modelService.getModel( modelID );
        
        // ---------------------------------------------------------------------
        // Set name, description, and  modelType
        // ---------------------------------------------------------------------
        
        model.setName( name );
        model.setDescription( description );
        
        ModelType modelType = modelTypeService.getModelType( model.getModelType().getId() );
        
        model.setModelType( modelType );
        
        //System.out.println( " model Name  is   :" + model.getName() + "---- modelType id is " + modelType.getId() + " --model Type name is " + modelType.getName());

        // --------------------------------------------------------------------------------------------------------
        // Save model Attributes
        // -----------------------------------------------------------------------------------------------------
        
        HttpServletRequest request = ServletActionContext.getRequest();

        String value = null;
        
        ModelAttributeValue modelAttributeValue = null;
        
        Collection<ModelTypeAttribute> modelTypeAttributes = modelType.getModelTypeAttributes();
        
        List<ModelAttributeValue> valuesForSave = new ArrayList<ModelAttributeValue>();
        List<ModelAttributeValue> valuesForUpdate = new ArrayList<ModelAttributeValue>();
        Collection<ModelAttributeValue> valuesForDelete = null;
        
        
        if ( modelTypeAttributes != null && modelTypeAttributes.size() > 0 )
        {
            //model.getModelType().getModelTypeAttributes().clear();
            valuesForDelete = modelAttributeValueService.getAllModelAttributeValuesByModel( modelService.getModel( modelID )) ;
            
            for ( ModelTypeAttribute modelTypeAttribute : modelTypeAttributes )
            {
                value = request.getParameter( AddModelAction.PREFIX_ATTRIBUTE + modelTypeAttribute.getId() );
                
                if ( StringUtils.isNotBlank( value ) )
                {
                    modelAttributeValue = modelAttributeValueService.modelAttributeValue( model ,modelTypeAttribute );
                    
                    if ( !model.getModelType().getModelTypeAttributes().contains( modelTypeAttribute ) )
                    {
                        model.getModelType().getModelTypeAttributes().add( modelTypeAttribute );
                    }
                    
                    if ( modelAttributeValue == null )
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
                        valuesForSave.add( modelAttributeValue );
                    }
                    else
                    {
                        if ( ModelTypeAttribute.TYPE_COMBO.equalsIgnoreCase( modelTypeAttribute.getValueType() ) )
                        {
                            ModelTypeAttributeOption option = modelTypeAttributeOptionService.getModelTypeAttributeOption( NumberUtils.toInt( value ));
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
                        valuesForUpdate.add( modelAttributeValue );
                        valuesForDelete.remove( modelAttributeValue );
                    }
                }
            }
        }
        modelService.updateModelAndDataValue(  model, valuesForSave, valuesForUpdate , valuesForDelete);
        return SUCCESS;
    }


}