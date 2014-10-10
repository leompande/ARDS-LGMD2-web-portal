package org.hisp.dhis.ccem.equipmenttype.action;

import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.coldchain.equipment.EquipmentType;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeService;
import org.hisp.dhis.coldchain.equipment.EquipmentType_Attribute;
import org.hisp.dhis.coldchain.equipment.EquipmentType_AttributeService;
import org.hisp.dhis.common.DeleteNotAllowedException;
import org.hisp.dhis.i18n.I18n;
import org.springframework.dao.DataIntegrityViolationException;

import com.opensymphony.xwork2.Action;

public class RemoveEquipmentTypeAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
    private EquipmentTypeService equipmentTypeService;

    public void setEquipmentTypeService( EquipmentTypeService equipmentTypeService )
    {
        this.equipmentTypeService = equipmentTypeService;
    }
    
    private EquipmentType_AttributeService equipmentType_AttributeService;
    
    public void setEquipmentType_AttributeService( EquipmentType_AttributeService equipmentType_AttributeService )
    {
        this.equipmentType_AttributeService = equipmentType_AttributeService;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    private int id;

    public void setId( int id )
    {
        this.id = id;
    }
    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        try
        {
            EquipmentType equipmentType = equipmentTypeService.getEquipmentType( id );
            
            if( equipmentType != null)
            {
                Set<EquipmentType_Attribute> equipmentType_Attributes = new HashSet<EquipmentType_Attribute>( equipmentType.getEquipmentType_Attributes());
                
                for ( EquipmentType_Attribute equipmentType_AttributeForDelete : equipmentType_Attributes )
                {
                    equipmentType_AttributeService.deleteEquipmentType_Attribute( equipmentType_AttributeForDelete );
                }
            }
            
            /*
            if( equipmentType != null)
            {
                equipmentType.getEquipmentType_Attributes().clear();
            }
            */
            
            equipmentTypeService.deleteEquipmentType( equipmentType );
        }
        
        
        catch ( DataIntegrityViolationException ex )
        {
            message = i18n.getString( "object_not_deleted_associated_by_objects" );

            return ERROR;
        }
        
        catch ( DeleteNotAllowedException ex )
        {
            if ( ex.getErrorCode().equals( DeleteNotAllowedException.ERROR_ASSOCIATED_BY_OTHER_OBJECTS ) )
            {
                message = i18n.getString( "object_not_deleted_associated_by_objects" ) + " " + ex.getMessage();
            }
            
            return ERROR;
        }
        
        return SUCCESS;
    }
}
