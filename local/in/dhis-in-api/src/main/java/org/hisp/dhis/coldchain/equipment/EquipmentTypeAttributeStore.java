package org.hisp.dhis.coldchain.equipment;

import java.util.Collection;

import org.hisp.dhis.common.GenericNameableObjectStore;

//public interface EquipmentTypeAttributeStore
public interface EquipmentTypeAttributeStore extends GenericNameableObjectStore<EquipmentTypeAttribute>
{
    String ID = EquipmentTypeAttributeStore.class.getName();
    /*
    int addEquipmentTypeAttribute( EquipmentTypeAttribute equipmentTypeAttribute );

    void updateEquipmentTypeAttribute( EquipmentTypeAttribute equipmentTypeAttribute );

    void deleteEquipmentTypeAttribute( EquipmentTypeAttribute equipmentTypeAttribute );
    */
    
    Collection<EquipmentTypeAttribute> getAllEquipmentTypeAttributes();
    
    EquipmentTypeAttribute getEquipmentTypeAttribute( int id );
    
    EquipmentTypeAttribute getEquipmentTypeAttributeByName( String name );
    
    EquipmentTypeAttribute getEquipmentTypeAttributeByDescription( String description );
    
    //Collection<EquipmentTypeAttribute> getAllEquipmentTypeAttributesForDisplay( EquipmentTypeAttribute equipmentTypeAttribute );
    
    
}
