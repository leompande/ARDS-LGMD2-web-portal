package org.hisp.dhis.ccem.equipment.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.coldchain.model.Model;
import org.hisp.dhis.coldchain.model.ModelService;
import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValue;
import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValueService;
import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentService;
import org.hisp.dhis.coldchain.equipment.EquipmentStatus;
import org.hisp.dhis.coldchain.equipment.EquipmentStatusService;
import org.hisp.dhis.coldchain.equipment.EquipmentType;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeOption;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeOptionService;
import org.hisp.dhis.coldchain.equipment.EquipmentType_Attribute;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

public class UpdateEquipmentAction implements Action
{

    public static final String PREFIX_ATTRIBUTE = "attr";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private EquipmentTypeAttributeOptionService equipmentTypeAttributeOptionService;
    
    private EquipmentService equipmentService;

    private EquipmentAttributeValueService equipmentAttributeValueService;
    
    private ModelService modelService;
    
    private CurrentUserService currentUserService;
    
    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    private EquipmentStatusService equipmentStatusService;
    
    public void setEquipmentStatusService( EquipmentStatusService equipmentStatusService )
    {
        this.equipmentStatusService = equipmentStatusService;
    }

    private I18nFormat format;
    
    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input/ Output
    // -------------------------------------------------------------------------
    
    private Integer equipmentID;
    
    private String message;
    
    private Integer model;
    
    public void setModel( Integer model )
    {
        this.model = model;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
    {

        //System.out.println("inside UpdateEquipmentAction : "+ equipmentID);
        
        Equipment equipment = equipmentService.getEquipment( equipmentID );
        
        EquipmentType equipmentType = equipment.getEquipmentType();
        
        Model selModel = null;
        
        if( model != null )
        {    
            selModel = modelService.getModel( model );
        }
        if( selModel != null )
        {
            equipment.setModel( selModel );
            
            equipmentService.updateEquipment( equipment );
        }
        
        // -----------------------------------------------------------------------------
        // Preparing EquipmentAttributeValue Details
        // -----------------------------------------------------------------------------
        HttpServletRequest request = ServletActionContext.getRequest();
        String value = null;
        
        List<EquipmentTypeAttribute> equipmentTypeAttributes = new ArrayList<EquipmentTypeAttribute>( );
        
        for( EquipmentType_Attribute equipmentType_Attribute : equipmentType.getEquipmentType_Attributes() )
        {
            equipmentTypeAttributes.add( equipmentType_Attribute.getEquipmentTypeAttribute() );
        }
        
        EquipmentAttributeValue equipmentAttributeValueDetails = null;
        for ( EquipmentTypeAttribute attribute : equipmentTypeAttributes )
        {
            value = request.getParameter( PREFIX_ATTRIBUTE + attribute.getId() );
            
            equipmentAttributeValueDetails = equipmentAttributeValueService.getEquipmentAttributeValue( equipment, attribute );
            
            if( equipmentAttributeValueDetails == null && value != null )
            {
                equipmentAttributeValueDetails = new EquipmentAttributeValue();
                equipmentAttributeValueDetails.setEquipment( equipment );
                equipmentAttributeValueDetails.setEquipmentTypeAttribute( attribute );

                if ( EquipmentTypeAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                {
                    EquipmentTypeAttributeOption option = equipmentTypeAttributeOptionService.getEquipmentTypeAttributeOption( NumberUtils.toInt( value, 0 ) );
                    if ( option != null )
                    {
                        equipmentAttributeValueDetails.setEquipmentTypeAttributeOption( option );
                        equipmentAttributeValueDetails.setValue( option.getName() );
                        
                        if ( EquipmentStatus.WORKING_STATUS.equalsIgnoreCase( attribute.getDescription() ) )
                        {
                            //System.out.println( "Option ID is  : " + option.getId() + "---Option Name is : "+option.getName() );
                            
                            if ( EquipmentStatus.STATUS_NOT_WORKING.equalsIgnoreCase( option.getName() ) )
                            {
                                equipment.setWorking( false );
                                equipmentService.updateEquipment( equipment );
                            }
                            else
                            {
                                equipment.setWorking( true );
                                equipmentService.updateEquipment( equipment );
                            }
                            
                            
                            String storedBy = currentUserService.getCurrentUsername();
                            
                            EquipmentStatus equipmentStatus = new EquipmentStatus();
                            
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");      
                            String currentDate = sdf.format(new Date());

                            equipmentStatus.setDescription( "Updated from edit equipmentAttributeValue screen" );
                            equipmentStatus.setEquipment( equipment );
                            equipmentStatus.setStatus( option.getName() );
                            
                            equipmentStatus.setReportingDate( format.parseDate( currentDate.trim() ) );
                            equipmentStatus.setUpdationDate( format.parseDate( currentDate.trim() ) );
                            equipmentStatus.setStoredBy( storedBy );
                            
                            equipmentStatusService.addEquipmentStatus( equipmentStatus );
                            
                        }
                        
                    }
                    
                    else
                    {
                        // Someone deleted this option ...
                    }
                }
                else if ( EquipmentTypeAttribute.TYPE_MODEL.equalsIgnoreCase( attribute.getValueType() ) )
                {
                    Model model = modelService.getModel( NumberUtils.toInt( value, 0 ) );
                    if ( model != null )
                    {
                        //equipmentAttributeValueDetails.setEquipmentTypeAttributeOption( option );
                        equipmentAttributeValueDetails.setValue( model.getName() );
                    }
                    else
                    {
                        // Someone deleted this model ...
                    }
                }
                else
                {
                    equipmentAttributeValueDetails.setValue( value.trim() );
                }
                
                equipmentAttributeValueService.addEquipmentAttributeValue( equipmentAttributeValueDetails );
            }
            else
            {
                if ( EquipmentTypeAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                {
                    EquipmentTypeAttributeOption option = equipmentTypeAttributeOptionService.getEquipmentTypeAttributeOption( NumberUtils.toInt( value, 0 ) );
                    
                    //System.out.println( " Option is  : " + option + "-- and value is --" + value.trim());
                    
                    if ( option != null )
                    {
                        equipmentAttributeValueDetails.setEquipmentTypeAttributeOption( option );
                        equipmentAttributeValueDetails.setValue( option.getName() );
                        
                        
                        if ( EquipmentStatus.WORKING_STATUS.equalsIgnoreCase( attribute.getDescription() ) )
                        {
                            //System.out.println( " Option ID is  : " + option.getId() + "---Option Name is : " + option.getName() );
                            
                            if ( EquipmentStatus.STATUS_NOT_WORKING.equalsIgnoreCase( option.getName() ) )
                            {
                                equipment.setWorking( false );
                                equipmentService.updateEquipment( equipment );
                            }
                            else
                            {
                                equipment.setWorking( true );
                                equipmentService.updateEquipment( equipment );
                            }
                            
                            String storedBy = currentUserService.getCurrentUsername();
                            
                            EquipmentStatus equipmentStatus = new EquipmentStatus();
                            
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");      
                            String currentDate = sdf.format(new Date());

                            equipmentStatus.setDescription( "Updated from edit equipmentAttributeValue screen" );
                            equipmentStatus.setEquipment( equipment );
                            equipmentStatus.setStatus( option.getName() );
                            
                            equipmentStatus.setReportingDate( format.parseDate( currentDate.trim() ) );
                            equipmentStatus.setUpdationDate( format.parseDate( currentDate.trim() ) );
                            equipmentStatus.setStoredBy( storedBy );
                            
                            equipmentStatusService.addEquipmentStatus( equipmentStatus );
                            
                        }
                        
                    }
                    else
                    {
                        // Someone deleted this option ...
                        equipmentAttributeValueDetails.setValue( value.trim() );
                    }
                }
                else if ( EquipmentTypeAttribute.TYPE_MODEL.equalsIgnoreCase( attribute.getValueType() ) )
                {
                    Model model = modelService.getModel( NumberUtils.toInt( value, 0 ) );
                    if ( model != null )
                    {
                        //equipmentAttributeValueDetails.setEquipmentTypeAttributeOption( option );
                        equipmentAttributeValueDetails.setValue( model.getName() );
                    }
                    else
                    {
                        // Someone deleted this model ...
                        equipmentAttributeValueDetails.setValue( value.trim() );
                    }
                }
                else
                {
                    equipmentAttributeValueDetails.setValue( value.trim() );
                }

                equipmentAttributeValueService.updateEquipmentAttributeValue( equipmentAttributeValueDetails );
            }
                
        }
         
        message = ""+ equipmentID;
        
        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Setters & Getters
    // -------------------------------------------------------------------------

    public String getMessage()
    {
        return message;
    }

    public void setEquipmentTypeAttributeOptionService(
        EquipmentTypeAttributeOptionService equipmentTypeAttributeOptionService )
    {
        this.equipmentTypeAttributeOptionService = equipmentTypeAttributeOptionService;
    }

    public void setEquipmentService( EquipmentService equipmentService )
    {
        this.equipmentService = equipmentService;
    }

    public void setEquipmentAttributeValueService( EquipmentAttributeValueService equipmentAttributeValueService )
    {
        this.equipmentAttributeValueService = equipmentAttributeValueService;
    }

    public void setEquipmentID( Integer equipmentID )
    {
        this.equipmentID = equipmentID;
    }

    public void setModelService( ModelService modelService )
    {
        this.modelService = modelService;
    }

}
