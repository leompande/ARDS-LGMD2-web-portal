package org.hisp.dhis.ccem.model.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.coldchain.model.Model;
import org.hisp.dhis.coldchain.model.ModelAttributeValue;
import org.hisp.dhis.coldchain.model.ModelAttributeValueService;
import org.hisp.dhis.coldchain.model.ModelService;
import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.comparator.ModelTypeAttributeComparator;

import com.opensymphony.xwork2.Action;

public class GetModelDetailsAction
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
    
    private ModelAttributeValueService modelAttributeValueService;
    
    
    public void setModelAttributeValueService( ModelAttributeValueService modelAttributeValueService )
    {
        this.modelAttributeValueService = modelAttributeValueService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int id;

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    
    private Model model;
    
    public Model getModel()
    {
        return model;
    }

    private ModelType modelType;

    public ModelType getModelType()
    {
        return modelType;
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
    
    private Map<Integer, String> modelTypeAttributeValueMap = new HashMap<Integer, String>();
    
    public Map<Integer, String> getModelTypeAttributeValueMap()
    {
        return modelTypeAttributeValueMap;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        
        model = modelService.getModel( id );
        
        if ( model != null )
        {
            modelType = model.getModelType();
            
            //modelTypeAttributes = modelType.getModelTypeAttributes();
            
            modelTypeAttributes = new ArrayList<ModelTypeAttribute> ( modelType.getModelTypeAttributes());
            Collections.sort( modelTypeAttributes, new ModelTypeAttributeComparator() );
            
            List<ModelAttributeValue> modelAttributeValues = new ArrayList<ModelAttributeValue>( modelAttributeValueService.getAllModelAttributeValuesByModel( modelService.getModel( id )) );
            
            for( ModelAttributeValue modelAttributeValue : modelAttributeValues )
            {
                if ( ModelTypeAttribute.TYPE_COMBO.equalsIgnoreCase( modelAttributeValue.getModelTypeAttribute().getValueType() ) )
                {
                    modelTypeAttributeValueMap.put( modelAttributeValue.getModelTypeAttribute().getId(), modelAttributeValue.getModelTypeAttributeOption().getName() );
                }
                
                else
                {
                    modelTypeAttributeValueMap.put( modelAttributeValue.getModelTypeAttribute().getId(), modelAttributeValue.getValue() );
                }
            }
        }

        return SUCCESS;
    }
}
