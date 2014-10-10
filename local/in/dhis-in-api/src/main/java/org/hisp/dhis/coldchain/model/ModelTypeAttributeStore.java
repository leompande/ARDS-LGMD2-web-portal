package org.hisp.dhis.coldchain.model;

import java.util.Collection;

import org.hisp.dhis.common.GenericNameableObjectStore;

public interface ModelTypeAttributeStore extends GenericNameableObjectStore<ModelTypeAttribute>
{
    String ID = ModelTypeAttributeStore.class.getName();
    
    /*
    int addModelTypeAttribute( ModelTypeAttribute modelTypeAttribute );
    
    void updateModelTypeAttribute( ModelTypeAttribute modelTypeAttribute );

    void deleteModelTypeAttribute( ModelTypeAttribute modelTypeAttribute );
    */
    ModelTypeAttribute getModelTypeAttribute( int id );
    
    ModelTypeAttribute getModelTypeAttributeByName( String name );
    
    ModelTypeAttribute getModelTypeAttributeByDescription( String description );
    
    Collection<ModelTypeAttribute> getAllModelTypeAttributes();

}
