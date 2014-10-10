package org.hisp.dhis.coldchain.equipment;

import java.util.Collection;

import org.hisp.dhis.common.GenericNameableObjectStore;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version EquipmentTypeAttributeGroupStore.javaMar 5, 2013 11:49:28 AM	
 */

public interface EquipmentTypeAttributeGroupStore extends GenericNameableObjectStore<EquipmentTypeAttributeGroup>
{
    String ID = EquipmentTypeAttributeGroupStore.class.getName();
    
    // -------------------------------------------------------------------------
    // EquipmentTypeAttributeGroup
    // -------------------------------------------------------------------------
    
    /*
    int addEquipmentTypeAttributeGroup( EquipmentTypeAttributeGroup EquipmentTypeAttributeGroup );

    void deleteEquipmentTypeAttributeGroup( EquipmentTypeAttributeGroup EquipmentTypeAttributeGroup );

    void updateEquipmentTypeAttributeGroup( EquipmentTypeAttributeGroup EquipmentTypeAttributeGroup );
    */
    
    
    
    EquipmentTypeAttributeGroup getEquipmentTypeAttributeGroupById( int id );

    EquipmentTypeAttributeGroup getEquipmentTypeAttributeGroupByName( String name );

    Collection<EquipmentTypeAttributeGroup> getAllEquipmentTypeAttributeGroups();
    
    Collection<EquipmentTypeAttributeGroup> getEquipmentTypeAttributeGroupsByEquipmentType( EquipmentType EquipmentType );
    
    
}
