package org.hisp.dhis.coldchain.equipment;

import java.util.Collection;

public interface EquipmentTypeAttributeOptionService
{
    String ID = EquipmentTypeAttributeOptionService.class.getName();
    
    int addEquipmentTypeAttributeOption( EquipmentTypeAttributeOption equipmentTypeAttributeOption );

    void updateEquipmentTypeAttributeOption( EquipmentTypeAttributeOption equipmentTypeAttributeOption );

    void deleteEquipmentTypeAttributeOption( EquipmentTypeAttributeOption equipmentTypeAttributeOption );

    EquipmentTypeAttributeOption getEquipmentTypeAttributeOption( int id );
    
    Collection<EquipmentTypeAttributeOption> getAllEquipmentTypeAttributeOptions();
    
    Collection<EquipmentTypeAttributeOption> get( EquipmentTypeAttribute equipmentTypeAttribute);
    
    EquipmentTypeAttributeOption get( EquipmentTypeAttribute equipmentTypeAttribute, String name );
}
