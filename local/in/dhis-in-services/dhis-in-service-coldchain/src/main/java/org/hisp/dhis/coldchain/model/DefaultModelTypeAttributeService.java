package org.hisp.dhis.coldchain.model;

import static org.hisp.dhis.i18n.I18nUtils.getCountByName;
import static org.hisp.dhis.i18n.I18nUtils.getObjectsBetween;
import static org.hisp.dhis.i18n.I18nUtils.getObjectsBetweenByName;

import java.util.Collection;

import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeService;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeStore;
import org.hisp.dhis.i18n.I18nService;
import org.springframework.transaction.annotation.Transactional;
@Transactional
public class DefaultModelTypeAttributeService implements ModelTypeAttributeService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ModelTypeAttributeStore modelTypeAttributeStore;

    public void setModelTypeAttributeStore( ModelTypeAttributeStore modelTypeAttributeStore )
    {
        this.modelTypeAttributeStore = modelTypeAttributeStore;
    }
    
    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }
    
    // -------------------------------------------------------------------------
    // ModelTypeAttribute
    // -------------------------------------------------------------------------
    /*
    @Override
    public int addModelTypeAttribute( ModelTypeAttribute modelTypeAttribute )
    {
        return modelTypeAttributeStore.addModelTypeAttribute( modelTypeAttribute );
    }
    @Override
    public void deleteModelTypeAttribute( ModelTypeAttribute modelTypeAttribute )
    {
        modelTypeAttributeStore.deleteModelTypeAttribute( modelTypeAttribute );
    }
    @Override
    public Collection<ModelTypeAttribute> getAllModelTypeAttributes()
    {
        return modelTypeAttributeStore.getAllModelTypeAttributes();
    }
    
    @Transactional
    @Override
    public void updateModelTypeAttribute( ModelTypeAttribute modelTypeAttribute )
    {
        modelTypeAttributeStore.updateModelTypeAttribute( modelTypeAttribute );
    }
    @Override
    public ModelTypeAttribute getModelTypeAttribute( int id )
    {
        return modelTypeAttributeStore.getModelTypeAttribute( id );
    }
    @Override
    public ModelTypeAttribute getModelTypeAttributeByName( String name )
    {
        return modelTypeAttributeStore.getModelTypeAttributeByName( name );
        
    }
    */
    // -------------------------------------------------------------------------
    // ModelTypeAttribute
    // -------------------------------------------------------------------------
    @Override
    public int addModelTypeAttribute( ModelTypeAttribute modelTypeAttribute )
    {
        return modelTypeAttributeStore.save( modelTypeAttribute );
    }
    @Override
    public void deleteModelTypeAttribute( ModelTypeAttribute modelTypeAttribute )
    {
        modelTypeAttributeStore.delete( modelTypeAttribute );       
    }
    @Override
    public void updateModelTypeAttribute( ModelTypeAttribute modelTypeAttribute )
    {
        modelTypeAttributeStore.update( modelTypeAttribute );
    }
    
    @Override
    public Collection<ModelTypeAttribute> getAllModelTypeAttributes()
    {
        return modelTypeAttributeStore.getAllModelTypeAttributes();
    }
    @Override
    public ModelTypeAttribute getModelTypeAttribute( int id )
    {
        return modelTypeAttributeStore.getModelTypeAttribute( id );
    }
    @Override
    public ModelTypeAttribute getModelTypeAttributeByName( String name )
    {
        return modelTypeAttributeStore.getModelTypeAttributeByName( name );
        
    }
    
    public ModelTypeAttribute getModelTypeAttributeByDescription( String description )
    {
        return modelTypeAttributeStore.getModelTypeAttributeByDescription( description );
    }

    //Methods
    public int getModelTypeAttributeCount()
    {
        return modelTypeAttributeStore.getCount();
    }
    
    public int getModelTypeAttributeCountByName( String name )
    {
        return getCountByName( i18nService, modelTypeAttributeStore, name );
    }

    public Collection<ModelTypeAttribute> getModelTypeAttributesBetween( int first, int max )
    {
        return getObjectsBetween( i18nService, modelTypeAttributeStore, first, max );
    }

    public Collection<ModelTypeAttribute> getModelTypeAttributesBetweenByName( String name, int first, int max )
    {
        return getObjectsBetweenByName( i18nService, modelTypeAttributeStore, name, first, max );
    }
}
