package org.hisp.dhis.coldchain.equipment;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.coldchain.equipment.EquipmentDataValue;
import org.hisp.dhis.coldchain.equipment.EquipmentDataValueService;
import org.hisp.dhis.coldchain.equipment.EquipmentDataValueStore;
import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultEquipmentDataValueService implements EquipmentDataValueService
{
    @SuppressWarnings( "unused" )
    private static final Log log = LogFactory.getLog( DefaultEquipmentDataValueService.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private EquipmentDataValueStore equipmentDataValueStore;

    public void setEquipmentDataValueStore( EquipmentDataValueStore equipmentDataValueStore )
    {
        this.equipmentDataValueStore = equipmentDataValueStore;
    }
    
    // -------------------------------------------------------------------------
    // EquipmentDataValue
    // -------------------------------------------------------------------------

    public void addEquipmentDataValue( EquipmentDataValue equipmentDataValue )
    {
        equipmentDataValueStore.addEquipmentDataValue( equipmentDataValue );
    }
    
    public void updateEquipmentDataValue( EquipmentDataValue equipmentDataValue )
    {
        equipmentDataValueStore.updateEquipmentDataValue( equipmentDataValue );
    }
    
    public void deleteEquipmentDataValue( EquipmentDataValue equipmentDataValue )
    {
        equipmentDataValueStore.deleteEquipmentDataValue( equipmentDataValue );
    }
    
    public Collection<EquipmentDataValue> getEquipmentDataValues( Equipment equipment, Period period, Collection<DataElement> dataElements )
    {
        return equipmentDataValueStore.getEquipmentDataValues( equipment, period, dataElements );
    }
    
    public EquipmentDataValue getEquipmentDataValue( Equipment equipment, Period period, DataElement dataElement )
    {
        return equipmentDataValueStore.getEquipmentDataValue( equipment, period, dataElement );
    }
    
    public Collection<EquipmentDataValue> getAllEquipmentDataValuesByEquipment( Equipment equipment )
    {
        return equipmentDataValueStore.getAllEquipmentDataValuesByEquipment( equipment );
    }
}
