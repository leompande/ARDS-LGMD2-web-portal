package org.hisp.dhis.coldchain.model;

import java.util.Collection;

import org.hisp.dhis.coldchain.model.Model;
import org.hisp.dhis.coldchain.model.ModelAttributeValue;
import org.hisp.dhis.coldchain.model.ModelAttributeValueService;
import org.hisp.dhis.coldchain.model.ModelAttributeValueStore;
import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeOption;
import org.springframework.transaction.annotation.Transactional;

public class DefaultModelAttributeValueService implements ModelAttributeValueService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private ModelAttributeValueStore modelAttributeValueStore;
    
    public void setModelAttributeValueStore( ModelAttributeValueStore modelAttributeValueStore )
    {
        this.modelAttributeValueStore = modelAttributeValueStore;
    }
    
    // -------------------------------------------------------------------------
    // ModelAttributeValue
    // -------------------------------------------------------------------------
    @Transactional
    @Override
    public void addModelAttributeValue( ModelAttributeValue modelAttributeValue )
    {
        
        modelAttributeValueStore.addModelAttributeValue( modelAttributeValue );
    }
    
    @Transactional
    @Override
    public void deleteModelAttributeValue( ModelAttributeValue modelAttributeValue )
    {
        modelAttributeValueStore.deleteModelAttributeValue( modelAttributeValue );
    }
    
    @Transactional
    @Override
    public Collection<ModelAttributeValue> getAllModelAttributeValues()
    {
        return modelAttributeValueStore.getAllModelAttributeValues();
    }
    
    @Transactional
    @Override
    public Collection<ModelAttributeValue> getAllModelAttributeValuesByModel( Model model )
    {
        return modelAttributeValueStore.getAllModelAttributeValuesByModel( model );
    }
    
    @Transactional
    @Override
    public void updateModelAttributeValue( ModelAttributeValue modelAttributeValue )
    {
        modelAttributeValueStore.updateModelAttributeValue( modelAttributeValue );
    }
    
    @Transactional
    @Override
    public ModelAttributeValue modelAttributeValue( Model model ,ModelTypeAttribute modelTypeAttribute )
    {
        return modelAttributeValueStore.modelAttributeValue( model, modelTypeAttribute );
    }
    
    @Transactional
    @Override
    public ModelAttributeValue modelAttributeValue( Model model ,ModelTypeAttribute modelTypeAttribute, ModelTypeAttributeOption modelTypeAttributeOption )
    {
        return modelAttributeValueStore.modelAttributeValue( model, modelTypeAttribute, modelTypeAttributeOption );
    }

	@Override
	public Collection<ModelAttributeValue> getAllModelAttributeValuesByModelTypeAttribute(
			ModelTypeAttribute modelTypeAttribute) {
		
		return modelAttributeValueStore.getAllModelAttributeValuesByModelTypeAttribute( modelTypeAttribute );
	}
    
}
