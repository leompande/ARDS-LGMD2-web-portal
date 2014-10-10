package org.hisp.dhis.coldchain.model;

import static org.hisp.dhis.i18n.I18nUtils.getCountByName;
import static org.hisp.dhis.i18n.I18nUtils.getObjectsBetween;
import static org.hisp.dhis.i18n.I18nUtils.getObjectsBetweenByName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeService;
import org.hisp.dhis.coldchain.model.ModelTypeStore;
import org.hisp.dhis.i18n.I18nService;
import org.springframework.transaction.annotation.Transactional;
@Transactional
public class DefaultModelTypeService implements ModelTypeService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ModelTypeStore modelTypeStore;
    
    public void setModelTypeStore( ModelTypeStore modelTypeStore )
    {
        this.modelTypeStore = modelTypeStore;
    }
    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }
    
    // -------------------------------------------------------------------------
    // ModelType
    // -------------------------------------------------------------------------
    /*
    @Transactional
    @Override
    public int addModelType( ModelType modelType )
    {
        return modelTypeStore.addModelType( modelType );
    }
    
    @Transactional
    @Override
    public void deleteModelType( ModelType modelType )
    {
        modelTypeStore.deleteModelType( modelType );
    }

    @Transactional
    @Override
    public Collection<ModelType> getAllModelTypes()
    {
        return modelTypeStore.getAllModelTypes();
    }

    @Transactional
    @Override
    public void updateModelType( ModelType modelType )
    {
        modelTypeStore.updateModelType( modelType );
    }
    
    @Transactional
    @Override
    public ModelType getModelType( int id )
    {
        return modelTypeStore.getModelType( id );
    }
    
    @Transactional
    @Override
    public ModelType getModelTypeByName( String name )
    {
        return modelTypeStore.getModelTypeByName( name );
    }
    */
    
    
    // -------------------------------------------------------------------------
    // ModelType
    // -------------------------------------------------------------------------
    
    @Override
    public int addModelType( ModelType modelType )
    {
        return modelTypeStore.save( modelType );
    }
    
    @Override
    public void deleteModelType( ModelType modelType )
    {
        modelTypeStore.delete( modelType );
    }
    
    @Override
    public void updateModelType( ModelType modelType )
    {
        modelTypeStore.update( modelType );
    }
    
    @Override
    public Collection<ModelType> getAllModelTypes()
    {
        return modelTypeStore.getAllModelTypes();
    }
    
    @Override
    public ModelType getModelType( int id )
    {
        return modelTypeStore.getModelType( id );
    }
    
    @Override
    public ModelType getModelTypeByName( String name )
    {
        return modelTypeStore.getModelTypeByName( name );
    }
    
    //Methods
    public int getModelTypeCount()
    {
        return modelTypeStore.getCount();
    }
    
    public int getModelTypeCountByName( String name )
    {
        return getCountByName( i18nService, modelTypeStore, name );
    }

    public Collection<ModelType> getModelTypesBetween( int first, int max )
    {
        return getObjectsBetween( i18nService, modelTypeStore, first, max );
    }

    public Collection<ModelType> getModelTypesBetweenByName( String name, int first, int max )
    {
        return getObjectsBetweenByName( i18nService, modelTypeStore, name, first, max );
    }
    /*
    public ModelTypeAttribute getModelTypeAttributeForDisplay( ModelType modelType, ModelTypeAttribute modelTypeAttribute, boolean display )
    {
        return modelTypeStore.getModelTypeAttributeForDisplay( modelType, modelTypeAttribute, display );
    }
    
    public Collection<ModelTypeAttribute> getAllModelTypeAttributeForDisplay( ModelType modelType, boolean display )
    {
        return modelTypeStore.getAllModelTypeAttributeForDisplay( modelType, display );
    }
    */
    
    public Collection<ModelTypeAttribute> getAllModelTypeAttributeForDisplay( ModelType modelType )
    {
        List<ModelTypeAttribute> modelTypeAttributeList = new ArrayList<ModelTypeAttribute>();
       
        List<ModelTypeAttribute> tempModelypeAttributeList = new ArrayList<ModelTypeAttribute>( modelType.getModelTypeAttributes() );
        
        for ( ModelTypeAttribute cataogTypeAttribute : tempModelypeAttributeList )
        {
            if ( cataogTypeAttribute.isDisplay() )
            {
                modelTypeAttributeList.add( cataogTypeAttribute );
            }
        }

        return modelTypeAttributeList;
    }
    
}
