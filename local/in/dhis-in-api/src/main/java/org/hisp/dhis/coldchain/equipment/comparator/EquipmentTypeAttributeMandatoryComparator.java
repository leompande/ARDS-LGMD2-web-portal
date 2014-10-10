package org.hisp.dhis.coldchain.equipment.comparator;

import java.util.Comparator;

import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;

public class EquipmentTypeAttributeMandatoryComparator implements Comparator<EquipmentTypeAttribute>
{
    public int compare( EquipmentTypeAttribute equipmentTypeAttribute0, EquipmentTypeAttribute equipmentTypeAttribute1 )
    {
        if( ( equipmentTypeAttribute0.isMandatory() ) && ( equipmentTypeAttribute1.isMandatory() ) )
        {
            return equipmentTypeAttribute0.getName().compareToIgnoreCase( equipmentTypeAttribute1.getName() );
           //Boolean boolean1 = equipmentTypeAttribute0.isMandatory();
           //return boolean1.compareTo( equipmentTypeAttribute1.isMandatory() );
        }
        else
        {
            return equipmentTypeAttribute0.getName().compareToIgnoreCase( equipmentTypeAttribute1.getName() );
        }
        /*
        if( equipmentTypeAttribute0.isMandatory() )
            return 1;
        else
            return 0;
        */
    }
}
