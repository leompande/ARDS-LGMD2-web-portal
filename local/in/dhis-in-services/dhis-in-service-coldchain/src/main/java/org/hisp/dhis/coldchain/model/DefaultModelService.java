package org.hisp.dhis.coldchain.model;

import static org.hisp.dhis.i18n.I18nUtils.getCountByName;
import static org.hisp.dhis.i18n.I18nUtils.getObjectsBetween;
import static org.hisp.dhis.i18n.I18nUtils.getObjectsBetweenByName;

import java.util.Collection;
import java.util.List;

import org.hisp.dhis.coldchain.model.Model;
import org.hisp.dhis.coldchain.model.ModelAttributeValue;
import org.hisp.dhis.coldchain.model.ModelAttributeValueService;
import org.hisp.dhis.coldchain.model.ModelService;
import org.hisp.dhis.coldchain.model.ModelStore;
import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.i18n.I18nService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultModelService
    implements ModelService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ModelStore modelStore;

    public void setModelStore( ModelStore modelStore )
    {
        this.modelStore = modelStore;
    }

    private ModelAttributeValueService modelAttributeValueService;

    public void setModelAttributeValueService( ModelAttributeValueService modelAttributeValueService )
    {
        this.modelAttributeValueService = modelAttributeValueService;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    // -------------------------------------------------------------------------
    // Model
    // -------------------------------------------------------------------------
    /*
     * public int addModel( Model model ) { return
     * modelStore.addModel( model ); }
     * 
     * public void deleteModel( Model model ) {
     * modelStore.deleteModel( model ); }
     * 
     * public void updateModel( Model model ) {
     * modelStore.updateModel( model ); }
     */

    @Override
    public int addModel( Model model )
    {
        return modelStore.save( model );
    }

    @Override
    public void deleteModel( Model model )
    {
        modelStore.delete( model );
    }

    @Override
    public void updateModel( Model model )
    {
        modelStore.update( model );
    }

    @Override
    public void deleteModelData( Model model )
    {
        modelStore.delete( model );
    }

    public Collection<Model> getAllModels()
    {
        return modelStore.getAllModels();
    }

    @Override
    public Model getModel( int id )
    {
        return modelStore.getModel( id );
    }

    @Override
    public Model getModelByName( String name )
    {
        return modelStore.getModelByName( name );
    }

    @Override
    public Model getModelByDescription( String description )
    {
        return modelStore.getModelByDescription( description );
    }
    
    @Override
    public int createModel( Model model, List<ModelAttributeValue> modelAttributeValues )
    {
        int modelId = addModel( model );

        for ( ModelAttributeValue modelAttributeValue : modelAttributeValues )
        {
            modelAttributeValueService.addModelAttributeValue( modelAttributeValue );
        }

        return modelId;
    }

    @Override
    public void updateModelAndDataValue( Model model, List<ModelAttributeValue> valuesForSave,
        List<ModelAttributeValue> valuesForUpdate, Collection<ModelAttributeValue> valuesForDelete )
    {
        modelStore.update( model );
        // modelStore.updateModel( model );

        for ( ModelAttributeValue modelAttributeValueAdd : valuesForSave )
        {
            modelAttributeValueService.addModelAttributeValue( modelAttributeValueAdd );
        }

        for ( ModelAttributeValue modelAttributeValueUpdate : valuesForUpdate )
        {
            modelAttributeValueService.updateModelAttributeValue( modelAttributeValueUpdate );
        }

        for ( ModelAttributeValue modelAttributeValueDelete : valuesForDelete )
        {
            modelAttributeValueService.deleteModelAttributeValue( modelAttributeValueDelete );
        }
    }

    @Override
    public void deleteModelAndDataValue( Model model )
    {
        Collection<ModelAttributeValue> valuesForDelete = modelAttributeValueService
            .getAllModelAttributeValuesByModel( model );
        for ( ModelAttributeValue modelAttributeValueDelete : valuesForDelete )
        {
            modelAttributeValueService.deleteModelAttributeValue( modelAttributeValueDelete );
        }

        // modelStore.deleteModel( model );
    }

    public Collection<Model> getModels( ModelType modelType )
    {
        return modelStore.getModels( modelType );
    }

    // Methods
    public int getModelCount()
    {
        return modelStore.getCount();
    }

    public int getModelCountByName( String name )
    {
        return getCountByName( i18nService, modelStore, name );
    }

    public Collection<Model> getModelsBetween( int first, int max )
    {
        return getObjectsBetween( i18nService, modelStore, first, max );
    }

    public Collection<Model> getModelsBetweenByName( String name, int first, int max )
    {
        return getObjectsBetweenByName( i18nService, modelStore, name, first, max );
    }

    @Override
    public int getCountModel( ModelType modelType )
    {
        return modelStore.getCountModel( modelType );
    }

    public Collection<Model> getModels( ModelType modelType, int min, int max )
    {
        return modelStore.getModels( modelType, min, max );
    }

    /*
    public int getCountModel( ModelType modelType, ModelTypeAttribute modelTypeAttribute, String searchText )
    {
        return modelStore.getCountModel( modelType, modelTypeAttribute, searchText );
    }

    public Collection<Model> getModels( ModelType modelType, ModelTypeAttribute modelTypeAttribute,
        String searchText, int min, int max )
    {
        return modelStore.getModels( modelType, modelTypeAttribute, searchText, min, max );
    }
    */
    
    public int getCountModel( ModelType modelType, ModelTypeAttribute modelTypeAttribute, String searchText, String searchBy )
    {
        return modelStore.getCountModel( modelType, modelTypeAttribute, searchText, searchBy );
    }

    public Collection<Model> getModels( ModelType modelType, ModelTypeAttribute modelTypeAttribute, String searchText, String searchBy, int min, int max )
    {
        return modelStore.getModels( modelType, modelTypeAttribute, searchText, searchBy, min, max );
    }

}
