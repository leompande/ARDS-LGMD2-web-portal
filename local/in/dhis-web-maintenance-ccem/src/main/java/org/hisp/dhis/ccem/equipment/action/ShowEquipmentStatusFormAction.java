package org.hisp.dhis.ccem.equipment.action;

import com.opensymphony.xwork2.Action;

public class ShowEquipmentStatusFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    private Integer equipmentId;
    
    public void setEquipmentId( Integer equipmentId )
    {
        this.equipmentId = equipmentId;
    }

    public Integer getEquipmentId()
    {
        return equipmentId;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        return SUCCESS;
    }
}
