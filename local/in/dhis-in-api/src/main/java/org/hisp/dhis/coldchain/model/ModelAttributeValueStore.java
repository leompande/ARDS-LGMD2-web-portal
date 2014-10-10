package org.hisp.dhis.coldchain.model;

import java.util.Collection;

public interface ModelAttributeValueStore
{
    String ID = ModelAttributeValueStore.class.getName();
    
    void addModelAttributeValue( ModelAttributeValue modelAttributeValue );

    void updateModelAttributeValue( ModelAttributeValue modelAttributeValue );

    void deleteModelAttributeValue( ModelAttributeValue modelAttributeValue );

    Collection<ModelAttributeValue> getAllModelAttributeValues();
    
    Collection<ModelAttributeValue> getAllModelAttributeValuesByModel( Model model );
    
    Collection<ModelAttributeValue> getAllModelAttributeValuesByModelTypeAttribute( ModelTypeAttribute modelTypeAttribute );
    
    ModelAttributeValue modelAttributeValue( Model model ,ModelTypeAttribute modelTypeAttribute );
    
    ModelAttributeValue modelAttributeValue( Model model ,ModelTypeAttribute modelTypeAttribute, ModelTypeAttributeOption modelTypeAttributeOption );

}
