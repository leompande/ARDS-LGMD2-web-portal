package org.hisp.dhis.coldchain.equipment;

import java.util.Collection;

import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeOption;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeOptionService;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeOptionStore;
import org.springframework.transaction.annotation.Transactional;

public class DefaultEquipmentTypeAttributeOptionService implements EquipmentTypeAttributeOptionService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private EquipmentTypeAttributeOptionStore equipmentTypeAttributeOptionStore;

    public void setEquipmentTypeAttributeOptionStore( EquipmentTypeAttributeOptionStore equipmentTypeAttributeOptionStore )
    {
        this.equipmentTypeAttributeOptionStore = equipmentTypeAttributeOptionStore;
    }

    // -------------------------------------------------------------------------
    // EquipmentTypeAttributeOption
    // -------------------------------------------------------------------------
    @Transactional
    @Override
    public int addEquipmentTypeAttributeOption( EquipmentTypeAttributeOption equipmentTypeAttributeOption )
    {
        return equipmentTypeAttributeOptionStore.addEquipmentTypeAttributeOption( equipmentTypeAttributeOption );
    }
    @Transactional
    @Override
    public void deleteEquipmentTypeAttributeOption( EquipmentTypeAttributeOption equipmentTypeAttributeOption )
    {
        equipmentTypeAttributeOptionStore.deleteEquipmentTypeAttributeOption( equipmentTypeAttributeOption );
    }
    @Transactional
    @Override
    public Collection<EquipmentTypeAttributeOption> getAllEquipmentTypeAttributeOptions()
    {
        return equipmentTypeAttributeOptionStore.getAllEquipmentTypeAttributeOptions();
    }
    @Transactional
    @Override
    public void updateEquipmentTypeAttributeOption( EquipmentTypeAttributeOption equipmentTypeAttributeOption )
    {
        equipmentTypeAttributeOptionStore.updateEquipmentTypeAttributeOption( equipmentTypeAttributeOption );
    }
    
    public Collection<EquipmentTypeAttributeOption> get( EquipmentTypeAttribute equipmentTypeAttribute)
    {
        return equipmentTypeAttributeOptionStore.get( equipmentTypeAttribute );
    }
    
    public EquipmentTypeAttributeOption get( EquipmentTypeAttribute equipmentTypeAttribute, String name )
    {
        return equipmentTypeAttributeOptionStore.get( equipmentTypeAttribute, name );
    }
    
    public EquipmentTypeAttributeOption getEquipmentTypeAttributeOption( int id )
    {
        return equipmentTypeAttributeOptionStore.getEquipmentTypeAttributeOption( id );
    }
}
