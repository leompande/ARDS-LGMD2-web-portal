package org.hisp.dhis.coldchain.equipmenttype.action;

import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeOption;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeOptionService;
import org.hisp.dhis.i18n.I18n;

import com.opensymphony.xwork2.Action;

public class RemoveEquipmentTypeAttributeOptionAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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

    private String message;

    public String getMessage()
    {
        return message;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        
        EquipmentTypeAttributeOption attributeOption = equipmentTypeAttributeOptionService.getEquipmentTypeAttributeOption( id );

        if ( attributeOption != null )
        {
            //int count = equipmentAttributeValue.countByEquipmentTypeAttributeOption( attributeOption );
            //if ( count > 0 )
            //{
            //    message = i18n.getString( "warning_delete_patient_attribute_option" );
            //    return INPUT;
            //}
            //else
            {
                equipmentTypeAttributeOptionService.deleteEquipmentTypeAttributeOption( attributeOption );
                message = i18n.getString( "success_delete_equipmenttype_attribute_option" );
                return SUCCESS;
            }
        }
        else
        {
            message = i18n.getString( "error_delete_equipmenttype_attribute_option" );
            return ERROR;
        }
    }

}
