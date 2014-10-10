package org.hisp.dhis.dataentry.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentService;
import org.hisp.dhis.coldchain.equipment.EquipmentType;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GetEquipmentAndDataSetListAction implements Action
{
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
        
    private EquipmentTypeService equipmentTypeService;

    public void setEquipmentTypeService( EquipmentTypeService equipmentTypeService )
    {
        this.equipmentTypeService = equipmentTypeService;
    }
    
    private EquipmentService equipmentService;

    public void setEquipmentService( EquipmentService equipmentService )
    {
        this.equipmentService = equipmentService;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------
    
    private Integer orgUnitId;
    
    public void setOrgUnitId( Integer orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
    
    private Integer equipmentTypeId;
   
    public void setEquipmentTypeId( Integer equipmentTypeId )
    {
        this.equipmentTypeId = equipmentTypeId;
    }

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }
    
    private List<Equipment> equipments;

    public List<Equipment> getEquipments()
    {
        return equipments;
    }

    private EquipmentType equipmentType;
    
    public EquipmentType getEquipmentType()
    {
        return equipmentType;
    }
    
    private List<OrganisationUnit> orgUnitList;
    
    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }
    
    private List<DataSet> datasets;
    
    public List<DataSet> getDatasets()
    {
        return datasets;
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        datasets = new ArrayList<DataSet>();
        equipments = new ArrayList<Equipment>();
        
        equipmentType = equipmentTypeService.getEquipmentType(  equipmentTypeId );
        
        organisationUnit = organisationUnitService.getOrganisationUnit(  orgUnitId );
        
        if( organisationUnit != null && equipmentType != null)
        {
            // list of DataSets of Selected Equipment Type
            datasets = new ArrayList<DataSet>(  equipmentType.getDataSets() );
            
            // list of Equipments of Selected organisationUnit and EquipmentType
            equipments = new ArrayList<Equipment>( equipmentService.getEquipmentList( organisationUnit, equipmentType ) );
            
        }
        
        for( Equipment equipment : equipments)
        {
            equipment.getModel().getName();
        }
        
        
        return SUCCESS;
    }
}


