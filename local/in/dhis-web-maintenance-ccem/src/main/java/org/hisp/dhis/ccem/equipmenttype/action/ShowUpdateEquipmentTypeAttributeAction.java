package org.hisp.dhis.ccem.equipmenttype.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeService;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;

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

    private OptionService optionService;
    
    public void setOptionService(OptionService optionService) 
    {
		this.optionService = optionService;
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
    
    private List<OptionSet> optionSets;

	public List<OptionSet> getOptionSets() 
	{
		return optionSets;
	}
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        equipmentTypeAttribute = equipmentTypeAttributeService.getEquipmentTypeAttribute( id );
        
        optionSets = new ArrayList<OptionSet>( optionService.getAllOptionSets() );
        
        return SUCCESS;
    }
}
