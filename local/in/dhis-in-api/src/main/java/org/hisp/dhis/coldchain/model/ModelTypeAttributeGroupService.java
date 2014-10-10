package org.hisp.dhis.coldchain.model;

import java.util.Collection;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version ModelTypeAttributeGroupService.javaOct 9, 2012 3:25:45 PM	
 */

public interface ModelTypeAttributeGroupService
{
    String ID = ModelTypeAttributeGroupService.class.getName();
    
    // -------------------------------------------------------------------------
    // ModelTypeAttributeGroup
    // -------------------------------------------------------------------------
    
    int addModelTypeAttributeGroup( ModelTypeAttributeGroup modelTypeAttributeGroup );

    void deleteModelTypeAttributeGroup( ModelTypeAttributeGroup modelTypeAttributeGroup );

    void updateModelTypeAttributeGroup( ModelTypeAttributeGroup modelTypeAttributeGroup );
    
    
    ModelTypeAttributeGroup getModelTypeAttributeGroupById( int id );

    ModelTypeAttributeGroup getModelTypeAttributeGroupByName( String name );

    Collection<ModelTypeAttributeGroup> getAllModelTypeAttributeGroups();
    
    Collection<ModelTypeAttributeGroup> getModelTypeAttributeGroupsByModelType( ModelType modelType );

    //  methods for paging 
    
    
    int getModelTypeAttributeGroupCount();
    
    int getModelTypeAttributeGroupCountByName( String name );
    
    Collection<ModelTypeAttributeGroup> getModelTypeAttributeGroupsBetween( int first, int max );
    
    Collection<ModelTypeAttributeGroup> getModelTypeAttributeGroupsBetweenByName( String name, int first, int max );
    

}
