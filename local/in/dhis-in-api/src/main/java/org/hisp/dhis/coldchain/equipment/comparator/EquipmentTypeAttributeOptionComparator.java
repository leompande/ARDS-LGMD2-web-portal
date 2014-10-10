package org.hisp.dhis.coldchain.equipment.comparator;

import java.util.Comparator;

import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeOption;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version EquipmentTypeAttributeOptionComparator.java Aug 1, 2012 3:50:35 PM	
 */
public class EquipmentTypeAttributeOptionComparator implements Comparator<EquipmentTypeAttributeOption>
{
    public int compare( EquipmentTypeAttributeOption equipmentTypeAttributeOption0, EquipmentTypeAttributeOption equipmentTypeAttributeOption1 )
    {
        return equipmentTypeAttributeOption0.getName().compareToIgnoreCase( equipmentTypeAttributeOption1.getName() );
    }
}

