package org.hisp.dhis.ccem.model.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeGroup;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeGroupService;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeService;
import org.hisp.dhis.coldchain.model.ModelTypeService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version AddModelTypeAttributeGroupAction.javaOct 10, 2012 2:58:01 PM	
 */

public class AddModelTypeAttributeGroupAction implements Action
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
    
    private ModelTypeAttributeGroupService modelTypeAttributeGroupService;
    
    public void setModelTypeAttributeGroupService( ModelTypeAttributeGroupService modelTypeAttributeGroupService )
    {
        this.modelTypeAttributeGroupService = modelTypeAttributeGroupService;
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

    private String modelTypeId;
    
    public void setModelTypeId( String modelTypeId )
    {
        this.modelTypeId = modelTypeId;
    }
    
    public String getModelTypeId()
    {
        return modelTypeId;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        ModelTypeAttributeGroup modelTypeAttributeGroup = new ModelTypeAttributeGroup();
        
        modelTypeAttributeGroup.setName( name );
        modelTypeAttributeGroup.setDescription( description );
        
        ModelType modelType = modelTypeService.getModelType( Integer.parseInt( modelTypeId ) );
        modelTypeAttributeGroup.setModelType( modelType );
        
        modelTypeAttributeGroup.setSortOrder( 0 );
        
        List<ModelTypeAttribute> modelTypeAttributes = new ArrayList<ModelTypeAttribute>();
        
        //System.out.println( "- Size of selectedModelTypeAttributesValidator List is : " + selectedModelTypeAttributesValidator.size());
        
        for ( int i = 0; i < this.selectedModelTypeAttributesValidator.size(); i++ )
        {
            ModelTypeAttribute modelTypeAttribute = modelTypeAttributeService.getModelTypeAttribute( selectedModelTypeAttributesValidator.get( i ) );
            
            modelTypeAttributes.add( modelTypeAttribute );
            
        }
        //System.out.println( "- Size of modelTypeAttributes List is : " + modelTypeAttributes.size());
        
        modelTypeAttributeGroup.setModelTypeAttributes( modelTypeAttributes );
        
        modelTypeAttributeGroupService.addModelTypeAttributeGroup( modelTypeAttributeGroup );
        
        return SUCCESS;
    }
}


