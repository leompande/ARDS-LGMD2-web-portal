package org.hisp.dhis.coldchain.model;

import java.util.Collection;

import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeOption;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeOptionService;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeOptionStore;
import org.springframework.transaction.annotation.Transactional;

public class DefaultModelTypeAttributeOptionService implements ModelTypeAttributeOptionService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ModelTypeAttributeOptionStore modelTypeAttributeOptionStore;

    public void setModelTypeAttributeOptionStore( ModelTypeAttributeOptionStore modelTypeAttributeOptionStore )
    {
        this.modelTypeAttributeOptionStore = modelTypeAttributeOptionStore;
    }

    // -------------------------------------------------------------------------
    // ModelTypeAttributeOption
    // -------------------------------------------------------------------------
    @Transactional
    @Override
    public int addModelTypeAttributeOption( ModelTypeAttributeOption modelTypeAttributeOption )
    {
        return modelTypeAttributeOptionStore.addModelTypeAttributeOption( modelTypeAttributeOption );
    }
    
    @Transactional
    @Override
    public void deleteModelTypeAttributeOption( ModelTypeAttributeOption modelTypeAttributeOption )
    {
        modelTypeAttributeOptionStore.deleteModelTypeAttributeOption( modelTypeAttributeOption );
    }
    
    @Transactional
    @Override
    public Collection<ModelTypeAttributeOption> getAllModelTypeAttributeOptions()
    {
        return modelTypeAttributeOptionStore.getAllModelTypeAttributeOptions();
    }
    
    @Transactional
    @Override
    public void updateModelTypeAttributeOption( ModelTypeAttributeOption modelTypeAttributeOption )
    {
        modelTypeAttributeOptionStore.updateModelTypeAttributeOption( modelTypeAttributeOption );
    }
   
    @Transactional
    @Override
    public ModelTypeAttributeOption getModelTypeAttributeOption( int id )
    {
        return modelTypeAttributeOptionStore.getModelTypeAttributeOption( id );
    }
    
    @Transactional
    @Override
    public int countByModelTypeAttributeoption( ModelTypeAttributeOption modelTypeAttributeOption )
    {
        return modelTypeAttributeOptionStore.countByModelTypeAttributeoption( modelTypeAttributeOption );
    }
    
    @Transactional
    @Override
    public Collection<ModelTypeAttributeOption> getModelTypeAttributeOptions( ModelTypeAttribute modelTypeAttribute )
    {
        return modelTypeAttributeOptionStore.getModelTypeAttributeOptions( modelTypeAttribute );
    }
    
    @Transactional
    @Override
    public ModelTypeAttributeOption getModelTypeAttributeOptionName( ModelTypeAttribute modelTypeAttribute, String name )
    {
        return modelTypeAttributeOptionStore.getModelTypeAttributeOptionName( modelTypeAttribute, name );
    }
}
