package org.hisp.dhis.ccem.model.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeService;
import org.hisp.dhis.coldchain.model.ModelTypeService;

import com.opensymphony.xwork2.Action;

public class AddModelTypeAction
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

    private ModelTypeAttributeService modelTypeAttributeService;

    public void setModelTypeAttributeService( ModelTypeAttributeService modelTypeAttributeService )
    {
        this.modelTypeAttributeService = modelTypeAttributeService;
    }
    
    // -------------------------------------------------------------------------
    // Input/Output
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

    private List<Integer> selectedModelTypeAttributesValidator = new ArrayList<Integer>();

    
    public void setSelectedModelTypeAttributesValidator( List<Integer> selectedModelTypeAttributesValidator )
    {
        this.selectedModelTypeAttributesValidator = selectedModelTypeAttributesValidator;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {

        ModelType modelType = new ModelType();
        
        modelType.setName( name );
        modelType.setDescription( description );
        //modelType.getModelTypeAttributes().add( arg0 )
        //modelTypeService.addModelType( modelType );
       
        List<ModelTypeAttribute> modelTypeAttributes = new ArrayList<ModelTypeAttribute>();
        
        for ( int i = 0; i < this.selectedModelTypeAttributesValidator.size(); i++ )
        {
            ModelTypeAttribute modelTypeAttribute = modelTypeAttributeService.getModelTypeAttribute( selectedModelTypeAttributesValidator.get( i ) );
            
            modelTypeAttributes.add( modelTypeAttribute );
            
        }
        modelType.setModelTypeAttributes( modelTypeAttributes );
        
        modelTypeService.addModelType( modelType );
        
        return SUCCESS;
    }
}
