package org.hisp.dhis.coldchain.model;

import java.util.Collection;

import org.hisp.dhis.common.GenericNameableObjectStore;

public interface ModelStore extends GenericNameableObjectStore<Model>
{

    String ID = ModelStore.class.getName();
    /*
    int addModel( Model model );

    void updateModel( Model model );

    void deleteModel( Model model );
    */
    Model getModel( int id );
    
    Model getModelByName( String name );
    
    Model getModelByDescription( String description );

    Collection<Model> getAllModels();
    
    Collection<Model> getModels( ModelType modelType );
    
    
    
    int getCountModel( ModelType modelType );
    
    Collection<Model> getModels( ModelType modelType, int min, int max );
    
    /*
    int getCountModel( ModelType modelType, ModelTypeAttribute modelTypeAttribute, String searchText );
    
    Collection<Model> getModels( ModelType modelType, ModelTypeAttribute modelTypeAttribute, String searchText, int min, int max );
    */
    int getCountModel( ModelType modelType, ModelTypeAttribute modelTypeAttribute, String searchText, String searchBy  );
    
    Collection<Model> getModels( ModelType modelType, ModelTypeAttribute modelTypeAttribute, String searchText, String searchBy, int min, int max );
}
