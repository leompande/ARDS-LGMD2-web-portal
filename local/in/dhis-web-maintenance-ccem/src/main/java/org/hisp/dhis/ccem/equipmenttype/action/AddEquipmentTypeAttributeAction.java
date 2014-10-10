package org.hisp.dhis.ccem.equipmenttype.action;

import java.util.List;

import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeOption;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeOptionService;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeService;
import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;

import com.opensymphony.xwork2.Action;

public class AddEquipmentTypeAttributeAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private EquipmentTypeAttributeService equipmentTypeAttributeService;

    public void setEquipmentTypeAttributeService( EquipmentTypeAttributeService equipmentTypeAttributeService )
    {
        this.equipmentTypeAttributeService = equipmentTypeAttributeService;
    }

    private EquipmentTypeAttributeOptionService equipmentTypeAttributeOptionService;

    public void setEquipmentTypeAttributeOptionService( EquipmentTypeAttributeOptionService equipmentTypeAttributeOptionService )
    {
        this.equipmentTypeAttributeOptionService = equipmentTypeAttributeOptionService;
    }
    
    private OptionService optionService;
    
    public void setOptionService(OptionService optionService) 
    {
		this.optionService = optionService;
	}

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String description;

    public void setDescription( String description )
    {
        this.description = description;
    }

    private String valueType;

    public void setValueType( String valueType )
    {
        this.valueType = valueType;
    }

    private boolean mandatory;

    public void setMandatory( boolean mandatory )
    {
        this.mandatory = mandatory;
    }

    private List<String> attrOptions;

    public void setAttrOptions( List<String> attrOptions )
    {
        this.attrOptions = attrOptions;
    }

    private Integer noChars;

    public void setNoChars( Integer noChars )
    {
        this.noChars = noChars;
    }
    
    private boolean display;
    
    public void setDisplay( boolean display )
    {
        this.display = display;
    }

    private Integer optionSetId;
    
    public void setOptionSetId(Integer optionSetId) 
    {
	this.optionSetId = optionSetId;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        System.out.println("Inside AddEquipmentTypeAttributeAction");
        
        EquipmentTypeAttribute equipmentTypeAttribute = new EquipmentTypeAttribute();
        
        equipmentTypeAttribute.setName( name );
        equipmentTypeAttribute.setDescription( description );
        equipmentTypeAttribute.setMandatory( mandatory );
        equipmentTypeAttribute.setNoChars( noChars );
        equipmentTypeAttribute.setValueType( valueType );
        //equipmentTypeAttribute.setDisplay( display );
        
        //System.out.println( "optionSetId == " + optionSetId );
        
        if ( EquipmentTypeAttribute.TYPE_COMBO.equalsIgnoreCase( valueType ) )
        {
            if( optionSetId != -1 &&  optionSetId != null )
            {
                OptionSet optionSet = optionService.getOptionSet( optionSetId );
        	equipmentTypeAttribute.setOptionSet( optionSet );
            }
        }
        
        equipmentTypeAttributeService.addEquipmentTypeAttribute( equipmentTypeAttribute );
        
        /*
        if ( EquipmentTypeAttribute.TYPE_COMBO.equalsIgnoreCase( valueType ) )
        {
            EquipmentTypeAttributeOption opt = null;
            for ( String optionName : attrOptions )
            {
                opt = new EquipmentTypeAttributeOption();
                opt.setName( optionName );
                opt.setEquipmentTypeAttribute( equipmentTypeAttribute );
                equipmentTypeAttribute.addAttributeOptions( opt );
                equipmentTypeAttributeOptionService.addEquipmentTypeAttributeOption( opt );
            }
        }
        */
        
        return SUCCESS;
    }
}
