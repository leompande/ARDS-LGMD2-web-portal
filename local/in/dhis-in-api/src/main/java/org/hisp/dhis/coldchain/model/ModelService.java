package org.hisp.dhis.coldchain.model;

import java.util.Collection;
import java.util.List;

public interface ModelService
{
    String ID = ModelService.class.getName();
    
    int addModel( Model model );

    void updateModel( Model model );

    void deleteModel( Model model );
    
    void deleteModelData( Model model );

    Collection<Model> getAllModels();
    
    Model getModel( int id );
    
    Model getModelByName( String name );
    
    Model getModelByDescription( String description );
    
    Collection<Model> getModels( ModelType modelType );
    
    int createModel( Model model, List<ModelAttributeValue> modelAttributeValues );
    
    void updateModelAndDataValue(  Model model, List<ModelAttributeValue> valuesForSave, List<ModelAttributeValue> valuesForUpdate, Collection<ModelAttributeValue> valuesForDelete );

    void deleteModelAndDataValue( Model model );
    
    
    //  methods
    
    int getModelCount();
    
    int getModelCountByName( String name );
    
    Collection<Model> getModelsBetween( int first, int max );
    
    Collection<Model> getModelsBetweenByName( String name, int first, int max );
    
    int getCountModel( ModelType modelType );
    
    Collection<Model> getModels( ModelType modelType, int min, int max );
    /*
    int getCountModel( ModelType modelType, ModelTypeAttribute modelTypeAttribute, String searchText );
    
    Collection<Model> getModels( ModelType modelType, ModelTypeAttribute modelTypeAttribute, String searchText, int min, int max );
    */
    
    int getCountModel( ModelType modelType, ModelTypeAttribute modelTypeAttribute, String searchText, String searchBy  );
    
    Collection<Model> getModels( ModelType modelType, ModelTypeAttribute modelTypeAttribute, String searchText, String searchBy, int min, int max );
}
