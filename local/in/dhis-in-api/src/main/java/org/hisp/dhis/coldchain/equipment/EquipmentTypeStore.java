package org.hisp.dhis.coldchain.equipment;

import java.util.Collection;

import org.hisp.dhis.common.GenericNameableObjectStore;

//public interface EquipmentTypeStore

public interface EquipmentTypeStore extends GenericNameableObjectStore<EquipmentType>
{
    String ID = EquipmentTypeStore.class.getName();
    /*
    int addEquipmentType( EquipmentType equipmentType );

    void updateEquipmentType( EquipmentType equipmentType );

    void deleteEquipmentType( EquipmentType equipmentType );
    */
    Collection<EquipmentType> getAllEquipmentTypes();
    
    EquipmentType getEquipmentTypeByName( String name );
    
    EquipmentType getEquipmentType( int id );
    
    //Collection<EquipmentTypeAttribute> getAllEquipmentTypeAttributesForDisplay( EquipmentType equipmentType );
}
