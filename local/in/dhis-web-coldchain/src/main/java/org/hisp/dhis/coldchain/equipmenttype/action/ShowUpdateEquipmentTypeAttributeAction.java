package org.hisp.dhis.coldchain.equipmenttype.action;

import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeService;

import com.opensymphony.xwork2.Action;

public class ShowUpdateEquipmentTypeAttributeAction implements Action
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
    // Input/Output
    // -------------------------------------------------------------------------
    private int id;

    public void setId( int id )
    {
        this.id = id;
    }

    private EquipmentTypeAttribute equipmentTypeAttribute;

    public EquipmentTypeAttribute getEquipmentTypeAttribute()
    {
        return equipmentTypeAttribute;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        equipmentTypeAttribute = equipmentTypeAttributeService.getEquipmentTypeAttribute( id );
        
        return SUCCESS;
    }
}
