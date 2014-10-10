package org.hisp.dhis.coldchain.equipment;

import java.util.Collection;

public interface EquipmentStatusService
{
    String ID = EquipmentStatusService.class.getName();
    
    int addEquipmentStatus( EquipmentStatus equipmentStatus );

    void updateEquipmentStatus( EquipmentStatus equipmentStatus );

    void deleteEquipmentStatus( EquipmentStatus equipmentStatus );

    Collection<EquipmentStatus> getAllEquipmentStatus();
    
    Collection<EquipmentStatus> getEquipmentStatusHistory( Equipment equipment );
    
    Collection<EquipmentStatus> getEquipmentStatusHistoryDescOrder( Equipment equipment );
    
}
