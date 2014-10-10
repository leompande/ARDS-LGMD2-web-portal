package org.hisp.dhis.coldchain.model;

import static org.hisp.dhis.i18n.I18nUtils.getCountByName;
import static org.hisp.dhis.i18n.I18nUtils.getObjectsBetween;
import static org.hisp.dhis.i18n.I18nUtils.getObjectsBetweenByName;

import java.util.Collection;

import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeGroup;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeGroupService;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeGroupStore;
import org.hisp.dhis.i18n.I18nService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version DefaultModelTypeAttributeGroupService.javaOct 9, 2012 3:54:39 PM	
 */
@Transactional
public class DefaultModelTypeAttributeGroupService implements ModelTypeAttributeGroupService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private ModelTypeAttributeGroupStore modelTypeAttributeGroupStore;

    public void setModelTypeAttributeGroupStore( ModelTypeAttributeGroupStore modelTypeAttributeGroupStore )
    {
        this.modelTypeAttributeGroupStore = modelTypeAttributeGroupStore;
    }
    
    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }
    
    // -------------------------------------------------------------------------
    // ModelTypeAttributeGroup
    // -------------------------------------------------------------------------
    /*
    public void addModelTypeAttributeGroup( ModelTypeAttributeGroup modelTypeAttributeGroup )
    {
        modelTypeAttributeGroupStore.addModelTypeAttributeGroup( modelTypeAttributeGroup );
    }
    
    public void deleteModelTypeAttributeGroup( ModelTypeAttributeGroup modelTypeAttributeGroup )
    {
        modelTypeAttributeGroupStore.deleteModelTypeAttributeGroup( modelTypeAttributeGroup );
    }
    
    public void updateModelTypeAttributeGroup( ModelTypeAttributeGroup modelTypeAttributeGroup )
    {
        modelTypeAttributeGroupStore.updateModelTypeAttributeGroup( modelTypeAttributeGroup );
    }
    */
    
    @Override
    public int addModelTypeAttributeGroup( ModelTypeAttributeGroup modelTypeAttributeGroup )
    {
        return modelTypeAttributeGroupStore.save( modelTypeAttributeGroup );
    }
    
    @Override
    public void deleteModelTypeAttributeGroup( ModelTypeAttributeGroup modelTypeAttributeGroup )
    {
        modelTypeAttributeGroupStore.delete( modelTypeAttributeGroup );
    }
    
    @Override
    public void updateModelTypeAttributeGroup( ModelTypeAttributeGroup modelTypeAttributeGroup )
    {
        modelTypeAttributeGroupStore.update( modelTypeAttributeGroup );
    }
    
    
    
    @Override    
    public ModelTypeAttributeGroup getModelTypeAttributeGroupById( int id )
    {
        return modelTypeAttributeGroupStore.getModelTypeAttributeGroupById( id );
    }
    @Override
    public ModelTypeAttributeGroup getModelTypeAttributeGroupByName( String name )
    {
        return modelTypeAttributeGroupStore.getModelTypeAttributeGroupByName( name );
    }
    @Override
    public Collection<ModelTypeAttributeGroup> getAllModelTypeAttributeGroups()
    {
        return modelTypeAttributeGroupStore.getAllModelTypeAttributeGroups();
    }
    @Override
    public Collection<ModelTypeAttributeGroup> getModelTypeAttributeGroupsByModelType( ModelType modelType )
    {
        return modelTypeAttributeGroupStore.getModelTypeAttributeGroupsByModelType( modelType );
    }
    
    
    //Methods
   
    public int getModelTypeAttributeGroupCount()
    {
        return modelTypeAttributeGroupStore.getCount();
    }
    
    public int getModelTypeAttributeGroupCountByName( String name )
    {
        return getCountByName( i18nService, modelTypeAttributeGroupStore, name );
    }

    public Collection<ModelTypeAttributeGroup> getModelTypeAttributeGroupsBetween( int first, int max )
    {
        return getObjectsBetween( i18nService, modelTypeAttributeGroupStore, first, max );
    }

    public Collection<ModelTypeAttributeGroup> getModelTypeAttributeGroupsBetweenByName( String name, int first, int max )
    {
        return getObjectsBetweenByName( i18nService, modelTypeAttributeGroupStore, name, first, max );
    }
   
    
    
}

