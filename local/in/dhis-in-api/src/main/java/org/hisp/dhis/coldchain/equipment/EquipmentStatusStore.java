package org.hisp.dhis.coldchain.equipment;

import java.util.Collection;

import org.hisp.dhis.common.GenericStore;

public interface EquipmentStatusStore extends GenericStore<EquipmentStatus>
{
    String ID = EquipmentStatusStore.class.getName();
    
    //int addEquipmentStatus( EquipmentStatus equipmentStatus );

    //void updateEquipmentStatus( EquipmentStatus equipmentStatus );

    //void deleteEquipmentStatus( EquipmentStatus equipmentStatus );

    //Collection<EquipmentStatus> getAllEquipmentStatus();
    
    Collection<EquipmentStatus> getEquipmentStatusHistory( Equipment equipment );
    
    Collection<EquipmentStatus> getEquipmentStatusHistoryDescOrder( Equipment equipment );
}
