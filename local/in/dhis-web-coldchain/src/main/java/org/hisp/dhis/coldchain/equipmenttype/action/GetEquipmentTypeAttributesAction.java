package org.hisp.dhis.coldchain.equipmenttype.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeService;
import org.hisp.dhis.coldchain.equipment.comparator.EquipmentTypeAttributeComparator;

import com.opensymphony.xwork2.Action;

public class GetEquipmentTypeAttributesAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
    
    private EquipmentTypeAttributeService equipmentTypeAttributeService;
    
    public void setEquipmentTypeAttributeService( EquipmentTypeAttributeService equipmentTypeAttributeService )
    {
        this.equipmentTypeAttributeService = equipmentTypeAttributeService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    private List<EquipmentTypeAttribute> equipmentTypeAttributes;
    
    public List<EquipmentTypeAttribute> getEquipmentTypeAttributes()
    {
        return equipmentTypeAttributes;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        equipmentTypeAttributes = new ArrayList<EquipmentTypeAttribute>( equipmentTypeAttributeService.getAllEquipmentTypeAttributes() );
        Collections.sort( equipmentTypeAttributes, new EquipmentTypeAttributeComparator() );
        
        /*
        for( EquipmentTypeAttribute equipmentTypeAttribute : equipmentTypeAttributes )
        {
            System.out.println( "ID---" + equipmentTypeAttribute.getId() );
            System.out.println( "Name---" + equipmentTypeAttribute.getName());
            System.out.println( "Discription---" + equipmentTypeAttribute.getDescription() );
            System.out.println( "ValueType---" + equipmentTypeAttribute.getValueType() );
        }
        */
        
        return SUCCESS;
    }

}

