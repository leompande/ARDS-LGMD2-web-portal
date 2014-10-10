package org.hisp.dhis.coldchain.model;

import java.util.Collection;

public interface ModelTypeAttributeOptionStore
{
    String ID = ModelTypeAttributeOptionStore.class.getName();
    
    int addModelTypeAttributeOption( ModelTypeAttributeOption modelTypeAttributeOption );

    void updateModelTypeAttributeOption( ModelTypeAttributeOption modelTypeAttributeOption );

    void deleteModelTypeAttributeOption( ModelTypeAttributeOption modelTypeAttributeOption );
    
    ModelTypeAttributeOption getModelTypeAttributeOption( int id );
    
    int countByModelTypeAttributeoption( ModelTypeAttributeOption modelTypeAttributeOption );
    
    Collection<ModelTypeAttributeOption> getModelTypeAttributeOptions( ModelTypeAttribute modelTypeAttribute );
    
    ModelTypeAttributeOption getModelTypeAttributeOptionName( ModelTypeAttribute modelTypeAttribute, String name );
    
    Collection<ModelTypeAttributeOption> getAllModelTypeAttributeOptions();

}
