package org.hisp.dhis.coldchain.equipment.action;

import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentService;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;

import com.opensymphony.xwork2.Action;

public class ShowEquipmentStatusFormAction implements Action
{
    public static final String WORKING_STATUS_OPTION_SET = "Working Status";//43.0
    
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
    
    private EquipmentService equipmentService;

    public void setEquipmentService( EquipmentService equipmentService )
    {
        this.equipmentService = equipmentService;
    }
    
    private ConstantService constantService;
    
    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    
    private OptionService optionService;
    
    public void setOptionService( OptionService optionService )
    {
        this.optionService = optionService;
    }
    
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
    
    private Equipment equipment;
    
    public Equipment getEquipment()
    {
        return equipment;
    }

    private OptionSet optionSet;
    
    public OptionSet getOptionSet()
    {
        return optionSet;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        if ( equipmentId != null )
        {
            equipment = equipmentService.getEquipment( equipmentId );
        }
        
        Constant optionSetConstant = constantService.getConstantByName( WORKING_STATUS_OPTION_SET );
        
        if( optionSetConstant != null )
        {
            optionSet = new OptionSet();
            optionSet = optionService.getOptionSet( (int) optionSetConstant.getValue() );
        }
        else
        {
            System.out.println( "constant for working status option set is not defined");
        }
        
        //equipment.getOrganisationUnit().getName();
        //equipment.getModel().getName();
        
        return SUCCESS;
    }
}
