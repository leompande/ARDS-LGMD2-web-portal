package org.hisp.dhis.ccem.equipment.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValue;
import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValueService;
import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentService;
import org.hisp.dhis.coldchain.equipment.EquipmentStatus;
import org.hisp.dhis.coldchain.equipment.EquipmentStatusService;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentType_Attribute;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

public class UpdateEquipmentStatusAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private EquipmentStatusService equipmentStatusService;
    
    private EquipmentService equipmentService;
    
    private CurrentUserService currentUserService;

    private I18nFormat format;
    
    private EquipmentAttributeValueService equipmentAttributeValueService;
    
    public void setEquipmentAttributeValueService( EquipmentAttributeValueService equipmentAttributeValueService )
    {
        this.equipmentAttributeValueService = equipmentAttributeValueService;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    
    private Integer equipmentId;
    
    private String reportingDate;
    
    private String dateOfUpdation;
    
    private String status;
    
    private String description;
    
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        Equipment equipment = equipmentService.getEquipment( equipmentId );
        
        
       // EquipmentType equipmentType = equipment.getEquipmentType();
        
        List<EquipmentTypeAttribute> equipmentTypeAttributes = new ArrayList<EquipmentTypeAttribute>( );
        
        for( EquipmentType_Attribute equipmentType_Attribute : equipment.getEquipmentType().getEquipmentType_Attributes() )
        {
            equipmentTypeAttributes.add( equipmentType_Attribute.getEquipmentTypeAttribute() );
        }
        
        EquipmentAttributeValue equipmentValue = new EquipmentAttributeValue();
        
        for ( EquipmentTypeAttribute equipmentTypeAttribute : equipmentTypeAttributes )
        {
            if( EquipmentTypeAttribute.TYPE_COMBO.equalsIgnoreCase( equipmentTypeAttribute.getValueType() ) )
            {
                if ( EquipmentStatus.WORKING_STATUS.equalsIgnoreCase( equipmentTypeAttribute.getDescription() ) )
                {
                    System.out.println( "Inside Working Status" );
                    equipmentValue = equipmentAttributeValueService.getEquipmentAttributeValue( equipment, equipmentTypeAttribute );
                    
                    if( equipmentValue == null )
                    {
                        equipmentValue = new EquipmentAttributeValue();
                        if( status.equalsIgnoreCase( "WORKING" ))
                        {
                            equipmentValue.setValue( EquipmentStatus.STATUS_WORKING_WELL.trim() );
                        }
                        else
                        {
                            equipmentValue.setValue( EquipmentStatus.STATUS_NOT_WORKING.trim() );
                        }
                        equipmentAttributeValueService.addEquipmentAttributeValue( equipmentValue );
                    }
                    else
                    {
                        if( status.equalsIgnoreCase( "WORKING" ))
                        {
                            equipmentValue.setValue( EquipmentStatus.STATUS_WORKING_WELL.trim() );
                        }
                        else
                        {
                            equipmentValue.setValue( EquipmentStatus.STATUS_NOT_WORKING.trim() );
                        }
                        equipmentAttributeValueService.updateEquipmentAttributeValue( equipmentValue );
                    }
                    
                }
            }
        }
       
        if( status.equalsIgnoreCase( "WORKING" ))
        {
            equipment.setWorking( true );
            equipmentService.updateEquipment( equipment );
        }
        else
        {
            equipment.setWorking( false );
            equipmentService.updateEquipment( equipment );
        }
        
        String storedBy = currentUserService.getCurrentUsername();
        
        EquipmentStatus equipmentStatus = new EquipmentStatus();
        
        equipmentStatus.setDescription( description );
        equipmentStatus.setEquipment( equipment );
        equipmentStatus.setStatus( status );
        equipmentStatus.setReportingDate( format.parseDate( reportingDate.trim() ) );
        equipmentStatus.setUpdationDate( format.parseDate( dateOfUpdation.trim() ) );
        equipmentStatus.setStoredBy( storedBy );
        
        equipmentStatusService.addEquipmentStatus( equipmentStatus );
        
        
        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Setters & Getters
    // -------------------------------------------------------------------------

    public void setEquipmentStatusService( EquipmentStatusService equipmentStatusService )
    {
        this.equipmentStatusService = equipmentStatusService;
    }


    public void setEquipmentService( EquipmentService equipmentService )
    {
        this.equipmentService = equipmentService;
    }


    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }


    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }


    public void setEquipmentId( Integer equipmentId )
    {
        this.equipmentId = equipmentId;
    }


    public void setReportingDate( String reportingDate )
    {
        this.reportingDate = reportingDate;
    }


    public void setDateOfUpdation( String dateOfUpdation )
    {
        this.dateOfUpdation = dateOfUpdation;
    }


    public void setStatus( String status )
    {
        this.status = status;
    }


    public void setDescription( String description )
    {
        this.description = description;
    }
        
}
