package org.hisp.dhis.coldchain.equipment;

import java.util.Collection;

public interface EquipmentAttributeValueStore
{
    String ID = EquipmentAttributeValueStore.class.getName();
    
    void addEquipmentAttributeValue( EquipmentAttributeValue equipmentAttributeValue );

    void updateEquipmentAttributeValue( EquipmentAttributeValue equipmentAttributeValue );

    void deleteEquipmentAttributeValue( EquipmentAttributeValue equipmentAttributeValue );
    
    Collection<EquipmentAttributeValue> getAllEquipmentAttributeValues();

    Collection<EquipmentAttributeValue> getEquipmentAttributeValues( Equipment equipment );
    
    EquipmentAttributeValue getEquipmentAttributeValue( Equipment equipment, EquipmentTypeAttribute equipmentTypeAttribute );
    
}
