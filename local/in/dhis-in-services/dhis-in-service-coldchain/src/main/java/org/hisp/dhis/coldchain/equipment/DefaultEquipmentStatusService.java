package org.hisp.dhis.coldchain.equipment;

import java.util.Collection;

import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentStatus;
import org.hisp.dhis.coldchain.equipment.EquipmentStatusService;
import org.hisp.dhis.coldchain.equipment.EquipmentStatusStore;
import org.springframework.transaction.annotation.Transactional;
@Transactional
public class DefaultEquipmentStatusService implements EquipmentStatusService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private EquipmentStatusStore equipmentStatusStore;

    public void setEquipmentStatusStore( EquipmentStatusStore equipmentStatusStore )
    {
        this.equipmentStatusStore = equipmentStatusStore;
    }
    
    // -------------------------------------------------------------------------
    // EquipmentWorkingStatus
    // -------------------------------------------------------------------------
    
    @Override
    public int addEquipmentStatus( EquipmentStatus equipmentStatus )
    {
        return equipmentStatusStore.save( equipmentStatus );
    }
    @Override
    public void deleteEquipmentStatus( EquipmentStatus equipmentStatus )
    {
        equipmentStatusStore.delete( equipmentStatus );
    }
    @Override
    public Collection<EquipmentStatus> getAllEquipmentStatus()
    {
        return equipmentStatusStore.getAll();
    }
    @Override
    public void updateEquipmentStatus( EquipmentStatus equipmentStatus )
    {
        equipmentStatusStore.update( equipmentStatus );
    }
    
    public Collection<EquipmentStatus> getEquipmentStatusHistory( Equipment equipment )
    {
        return equipmentStatusStore.getEquipmentStatusHistory( equipment );
    }
    
    public Collection<EquipmentStatus> getEquipmentStatusHistoryDescOrder( Equipment equipment )
    {
        return equipmentStatusStore.getEquipmentStatusHistoryDescOrder( equipment );
    }

}
