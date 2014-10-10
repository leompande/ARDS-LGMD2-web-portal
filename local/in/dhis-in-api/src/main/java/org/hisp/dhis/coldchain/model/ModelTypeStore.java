package org.hisp.dhis.coldchain.model;

import java.util.Collection;

import org.hisp.dhis.common.GenericNameableObjectStore;

//public interface ModelTypeStore
public interface ModelTypeStore extends GenericNameableObjectStore<ModelType>
{
    String ID = ModelTypeStore.class.getName();
    /*
    int addModelType( ModelType modelType );

    void updateModelType( ModelType modelType );

    void deleteModelType( ModelType modelType );
    */
    
    ModelType getModelType( int id );
    
    ModelType getModelTypeByName( String name );
    
    //ModelType getModelTypeByAttribute( ModelType modelType, ModelTypeAttribute modelTypeAttribute);

    Collection<ModelType> getAllModelTypes();
    
    /*
    //Methods For Display
    ModelTypeAttribute getModelTypeAttributeForDisplay( ModelType modelType, ModelTypeAttribute modelTypeAttribute, boolean display );
    
    Collection<ModelTypeAttribute> getAllModelTypeAttributeForDisplay( ModelType modelType, boolean display );
    */

}
