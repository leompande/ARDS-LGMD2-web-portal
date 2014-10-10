package org.hisp.dhis.coldchain.equipment.comparator;

import java.util.Comparator;

import org.hisp.dhis.coldchain.equipment.EquipmentType;

public class EquipmentTypeComparator implements Comparator<EquipmentType>
{
    public int compare( EquipmentType equipmentType0, EquipmentType equipmentType1 )
    {
        return equipmentType0.getName().compareToIgnoreCase( equipmentType1.getName() );
    }
}
