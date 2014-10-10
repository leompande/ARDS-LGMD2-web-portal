package org.hisp.dhis.coldchain.model;

import java.util.Collection;

import org.hisp.dhis.common.GenericNameableObjectStore;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version ModelTypeAttributeGroupStore.javaOct 9, 2012 3:26:28 PM	
 */

public interface ModelTypeAttributeGroupStore extends GenericNameableObjectStore<ModelTypeAttributeGroup>
{
    String ID = ModelTypeAttributeGroupStore.class.getName();
    
    // -------------------------------------------------------------------------
    // ModelTypeAttributeGroup
    // -------------------------------------------------------------------------
    
    /*
    int addModelTypeAttributeGroup( ModelTypeAttributeGroup modelTypeAttributeGroup );

    void deleteModelTypeAttributeGroup( ModelTypeAttributeGroup modelTypeAttributeGroup );

    void updateModelTypeAttributeGroup( ModelTypeAttributeGroup modelTypeAttributeGroup );
    */
    
    
    
    ModelTypeAttributeGroup getModelTypeAttributeGroupById( int id );

    ModelTypeAttributeGroup getModelTypeAttributeGroupByName( String name );

    Collection<ModelTypeAttributeGroup> getAllModelTypeAttributeGroups();
    
    Collection<ModelTypeAttributeGroup> getModelTypeAttributeGroupsByModelType( ModelType modelType );
    
}
