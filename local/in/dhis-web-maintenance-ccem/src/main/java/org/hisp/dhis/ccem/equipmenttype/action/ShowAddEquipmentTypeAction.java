package org.hisp.dhis.ccem.equipmenttype.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeService;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeService;

import com.opensymphony.xwork2.Action;

public class ShowAddEquipmentTypeAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
    private ModelTypeService modelTypeService;

    public void setModelTypeService( ModelTypeService modelTypeService )
    {
        this.modelTypeService = modelTypeService;
    }
    
    private EquipmentTypeAttributeService equipmentTypeAttributeService;
    
    public void setEquipmentTypeAttributeService( EquipmentTypeAttributeService equipmentTypeAttributeService )
    {
        this.equipmentTypeAttributeService = equipmentTypeAttributeService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    private List<ModelType> modelTypes;

    public List<ModelType> getModelTypes()
    {
        return modelTypes;
    }
    
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
        modelTypes =  new ArrayList<ModelType>( modelTypeService.getAllModelTypes() );
        
        equipmentTypeAttributes = new ArrayList<EquipmentTypeAttribute>( equipmentTypeAttributeService.getAllEquipmentTypeAttributes() );
        
        return SUCCESS;
    }

}
