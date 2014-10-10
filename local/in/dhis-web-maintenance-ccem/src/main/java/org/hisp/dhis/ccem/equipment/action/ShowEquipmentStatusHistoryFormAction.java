package org.hisp.dhis.ccem.equipment.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentService;
import org.hisp.dhis.coldchain.equipment.EquipmentStatus;
import org.hisp.dhis.coldchain.equipment.EquipmentStatusService;

import com.opensymphony.xwork2.Action;

public class ShowEquipmentStatusHistoryFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private EquipmentService equipmentService;

    public void setEquipmentService( EquipmentService equipmentService )
    {
        this.equipmentService = equipmentService;
    }

    private EquipmentStatusService equipmentStatusService;
    
    public void setEquipmentStatusService( EquipmentStatusService equipmentStatusService )
    {
        this.equipmentStatusService = equipmentStatusService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer equipmentId;
    
    public void setEquipmentId( Integer equipmentId )
    {
        this.equipmentId = equipmentId;
    }

    private List<EquipmentStatus> equipmentStatusHistory;
    
    public List<EquipmentStatus> getEquipmentStatusHistory()
    {
        return equipmentStatusHistory;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        //System.out.println("inside ShowEquipmentStatusHistoryFormAction ");
        Equipment equipment = equipmentService.getEquipment( equipmentId );
        
        equipmentStatusHistory = new ArrayList<EquipmentStatus>( equipmentStatusService.getEquipmentStatusHistory( equipment ) );
        
        return SUCCESS;
    }
}
