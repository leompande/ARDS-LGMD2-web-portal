package org.hisp.dhis.coldchain.equipment;

import java.util.Collection;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version EquipmentTypeAttributeGroupService.javaMar 5, 2013 11:48:18 AM	
 */

public interface EquipmentTypeAttributeGroupService
{
    String ID = EquipmentTypeAttributeGroupService.class.getName();
    
    // -------------------------------------------------------------------------
    // ModelTypeAttributeGroup
    // -------------------------------------------------------------------------
    
    int addEquipmentTypeAttributeGroup( EquipmentTypeAttributeGroup equipmentTypeAttributeGroup );

    void deleteEquipmentTypeAttributeGroup( EquipmentTypeAttributeGroup equipmentTypeAttributeGroup );

    void updateEquipmentTypeAttributeGroup( EquipmentTypeAttributeGroup equipmentTypeAttributeGroup );
    
    
    EquipmentTypeAttributeGroup getEquipmentTypeAttributeGroupById( int id );

    EquipmentTypeAttributeGroup getEquipmentTypeAttributeGroupByName( String name );

    Collection<EquipmentTypeAttributeGroup> getAllEquipmentTypeAttributeGroups();
    
    Collection<EquipmentTypeAttributeGroup> getEquipmentTypeAttributeGroupsByEquipmentType( EquipmentType equipmentType );

    
    //  methods for paging 
    
    
    int getEquipmentTypeAttributeGroupCount();
    
    int getEquipmentTypeAttributeGroupCountByName( String name );
    
    Collection<EquipmentTypeAttributeGroup> getEquipmentTypeAttributeGroupsBetween( int first, int max );
    
    Collection<EquipmentTypeAttributeGroup> getEquipmentTypeAttributeGroupsBetweenByName( String name, int first, int max );
    
    
    
}

