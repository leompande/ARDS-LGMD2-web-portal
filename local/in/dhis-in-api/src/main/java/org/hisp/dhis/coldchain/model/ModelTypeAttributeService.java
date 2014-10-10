package org.hisp.dhis.coldchain.model;

import java.util.Collection;

public interface ModelTypeAttributeService
{
    String ID = ModelTypeAttributeService.class.getName();
    
    int addModelTypeAttribute( ModelTypeAttribute modelTypeAttribute );

    void updateModelTypeAttribute( ModelTypeAttribute modelTypeAttribute );

    void deleteModelTypeAttribute( ModelTypeAttribute modelTypeAttribute );
    
    ModelTypeAttribute getModelTypeAttribute( int id );
    
    ModelTypeAttribute getModelTypeAttributeByName( String name );
    
    ModelTypeAttribute getModelTypeAttributeByDescription( String description );
    
    //ModelType getModelTypeByAttribute( ModelType modelType, ModelTypeAttribute modelTypeAttribute);

    Collection<ModelTypeAttribute> getAllModelTypeAttributes();
    
    //  methods
    
    int getModelTypeAttributeCount();
    
    int getModelTypeAttributeCountByName( String name );
    
    Collection<ModelTypeAttribute> getModelTypeAttributesBetween( int first, int max );
    
    Collection<ModelTypeAttribute> getModelTypeAttributesBetweenByName( String name, int first, int max );
    
}
