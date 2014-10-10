package org.hisp.dhis.coldchain.equipment;

import java.util.Collection;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.period.Period;

public interface EquipmentDataValueStore
{
    String ID = EquipmentDataValueStore.class.getName();
    
    // -------------------------------------------------------------------------
    // EquipmentDataValue
    // -------------------------------------------------------------------------

    void addEquipmentDataValue( EquipmentDataValue equipmentDataValue );
    
    void updateEquipmentDataValue( EquipmentDataValue equipmentDataValue );
    
    void deleteEquipmentDataValue( EquipmentDataValue equipmentDataValue );
    
    Collection<EquipmentDataValue> getEquipmentDataValues( Equipment equipment, Period period, Collection<DataElement> dataElements );
    
    EquipmentDataValue getEquipmentDataValue( Equipment equipment, Period period, DataElement dataElement );
    
    Collection<EquipmentDataValue> getAllEquipmentDataValuesByEquipment( Equipment equipment );
}
