package org.hisp.dhis.coldchain.equipment.comparator;

import java.util.Comparator;

import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;

public class EquipmentTypeAttributeComparator
    implements Comparator<EquipmentTypeAttribute>
{
    public int compare( EquipmentTypeAttribute equipmentTypeAttribute0, EquipmentTypeAttribute equipmentTypeAttribute1 )
    {
        return equipmentTypeAttribute0.getName().compareToIgnoreCase( equipmentTypeAttribute1.getName() );
    }
}
