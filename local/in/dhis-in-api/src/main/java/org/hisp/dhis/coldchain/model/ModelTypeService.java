package org.hisp.dhis.coldchain.model;

import java.util.Collection;

public interface ModelTypeService
{
    String ID = ModelTypeService.class.getName();
    
    int addModelType( ModelType modelType );

    void updateModelType( ModelType modelType );

    void deleteModelType( ModelType modelType );
    
    ModelType getModelType( int id );
    
    ModelType getModelTypeByName( String name );

    Collection<ModelType> getAllModelTypes();
    
    //  methods
    
    int getModelTypeCount();
    
    int getModelTypeCountByName( String name );
    
    Collection<ModelType> getModelTypesBetween( int first, int max );
    
    Collection<ModelType> getModelTypesBetweenByName( String name, int first, int max );
    
    /*
    //Methods For Display
    ModelTypeAttribute getModelTypeAttributeForDisplay( ModelType modelType, ModelTypeAttribute modelTypeAttribute, boolean display );
    
    Collection<ModelTypeAttribute> getAllModelTypeAttributeForDisplay( ModelType modelType, boolean display );
    */
    Collection<ModelTypeAttribute> getAllModelTypeAttributeForDisplay( ModelType modelType );
    
}
