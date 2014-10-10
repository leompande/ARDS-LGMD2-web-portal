package org.hisp.dhis.coldchain.equipmenttype.action;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeOption;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeOptionService;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeService;

import com.opensymphony.xwork2.Action;

public class UpdateEquipmentTypeAttributeAction implements Action
{
    public static final String PREFIX_ATTRIBUTE_OPTION = "attrOption";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private EquipmentTypeAttributeService equipmentTypeAttributeService;

    public void setEquipmentTypeAttributeService( EquipmentTypeAttributeService equipmentTypeAttributeService )
    {
        this.equipmentTypeAttributeService = equipmentTypeAttributeService;
    }

    private EquipmentTypeAttributeOptionService equipmentTypeAttributeOptionService;

    public void setEquipmentTypeAttributeOptionService(
        EquipmentTypeAttributeOptionService equipmentTypeAttributeOptionService )
    {
        this.equipmentTypeAttributeOptionService = equipmentTypeAttributeOptionService;
    }
    
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------
    private int id;

    public void setId( int id )
    {
        this.id = id;
    }

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
    
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        EquipmentTypeAttribute equipmentTypeAttribute = equipmentTypeAttributeService.getEquipmentTypeAttribute( id );
        
        equipmentTypeAttribute.setName( name );
        equipmentTypeAttribute.setDescription( description );
        equipmentTypeAttribute.setMandatory( mandatory );
        equipmentTypeAttribute.setNoChars( noChars );
        equipmentTypeAttribute.setValueType( valueType );
        //equipmentTypeAttribute.setDisplay( display );

        HttpServletRequest request = ServletActionContext.getRequest();
        Collection<EquipmentTypeAttributeOption> attributeOptions = equipmentTypeAttributeOptionService.get( equipmentTypeAttribute );
        
        if ( attributeOptions != null && attributeOptions.size() > 0 )
        {
            String value = null;
            for ( EquipmentTypeAttributeOption option : attributeOptions )
            {
                value = request.getParameter( PREFIX_ATTRIBUTE_OPTION + option.getId() );
                if ( StringUtils.isNotBlank( value ) )
                {
                    option.setName( value.trim() );
                    equipmentTypeAttributeOptionService.updateEquipmentTypeAttributeOption( option );
                   // equipmentTypeAttributeOptionService.updateEquipmentTypeAttributeValues( option );
                }
            }
        }
        
        if ( attrOptions != null )
        {
            EquipmentTypeAttributeOption opt = null;
            for ( String optionName : attrOptions )
            {
                opt = equipmentTypeAttributeOptionService.get( equipmentTypeAttribute, optionName );
                if ( opt == null )
                {
                    opt = new EquipmentTypeAttributeOption();
                    opt.setName( optionName );
                    opt.setEquipmentTypeAttribute( equipmentTypeAttribute );
                    equipmentTypeAttribute.addAttributeOptions( opt );
                    equipmentTypeAttributeOptionService.addEquipmentTypeAttributeOption( opt );
                }
            }
        }
        
        equipmentTypeAttributeService.updateEquipmentTypeAttribute( equipmentTypeAttribute );
        
        return SUCCESS;
    }
}
