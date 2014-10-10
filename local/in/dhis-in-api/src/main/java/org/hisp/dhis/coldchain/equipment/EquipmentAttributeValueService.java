package org.hisp.dhis.coldchain.equipment;

import java.util.Collection;
import java.util.List;

public interface EquipmentAttributeValueService
{
    String ID = EquipmentAttributeValueService.class.getName();
    
    void addEquipmentAttributeValue( EquipmentAttributeValue equipmentAttributeValue );

    void updateEquipmentAttributeValue( EquipmentAttributeValue equipmentAttributeValue );

    void deleteEquipmentAttributeValue( EquipmentAttributeValue equipmentAttributeValue );

    Collection<EquipmentAttributeValue> getAllEquipmentAttributeValues();

    Collection<EquipmentAttributeValue> getEquipmentAttributeValues( Equipment equipment );
    
    EquipmentAttributeValue getEquipmentAttributeValue( Equipment equipment, EquipmentTypeAttribute equipmentTypeAttribute );
    
    //Map<String, String> inventryTypeAttributeAndValue( Equipment equipment, List<EquipmentTypeAttribute> equipmentTypeAttributeList );
    
    String inventryTypeAttributeAndValue( Equipment equipment, List<EquipmentTypeAttribute> equipmentTypeAttributeList );
}
