package org.hisp.dhis.coldchain.equipment.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
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
import org.hisp.dhis.coldchain.equipment.EquipmentTypeService;
import org.hisp.dhis.coldchain.equipment.EquipmentType_Attribute;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version AddUpdateEquipmentIcePacksAction.javaDec 21, 2012 2:49:06 PM	
 */

public class AddUpdateEquipmentIcePacksAction implements Action
{

    public static final String PREFIX_ATTRIBUTE = "attr";
    
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
    
    private EquipmentTypeAttributeOptionService equipmentTypeAttributeOptionService;
    
    public void setEquipmentTypeAttributeOptionService( EquipmentTypeAttributeOptionService equipmentTypeAttributeOptionService )
    {
        this.equipmentTypeAttributeOptionService = equipmentTypeAttributeOptionService;
    }
    
    private EquipmentService equipmentService;
    
    public void setEquipmentService( EquipmentService equipmentService )
    {
        this.equipmentService = equipmentService;
    }

    private ModelService modelService;
    
    public void setModelService( ModelService modelService )
    {
        this.modelService = modelService;
    }
    
    private EquipmentAttributeValueService equipmentAttributeValueService;
    
    public void setEquipmentAttributeValueService( EquipmentAttributeValueService equipmentAttributeValueService )
    {
        this.equipmentAttributeValueService = equipmentAttributeValueService;
    }

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
    
    private String equipmentID;
    
    public void setEquipmentID( String equipmentID )
    {
        this.equipmentID = equipmentID;
    }

    /*
    private Integer equipmentID;
    
    public void setEquipmentID( Integer equipmentID )
    {
        this.equipmentID = equipmentID;
    }
    */
    private Integer equipmentTypeIcePacksId;
    
    public void setEquipmentTypeIcePacksId( Integer equipmentTypeIcePacksId )
    {
        this.equipmentTypeIcePacksId = equipmentTypeIcePacksId;
    }
    
    private Integer healthFacility;
    
    public void setHealthFacility( Integer healthFacility )
    {
        this.healthFacility = healthFacility;
    }
    
    private Integer model;
    
    public void setModel( Integer model )
    {
        this.model = model;
    }
    
    private String message;
    
    public String getMessage()
    {
        return message;
    }
    
    private EquipmentType equipmentType;
    
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
    {
        OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( healthFacility );
        
        Equipment equipment = null;
        
        if( equipmentID != null && !equipmentID.equalsIgnoreCase( "" ) )
        {
            equipment = equipmentService.getEquipment( Integer.parseInt( equipmentID ) );
        }
        
        
        //Equipment equipment = equipmentService.getEquipment( equipmentID );
        
        Model selModel = null;
        
        if( model != null )
        {    
            selModel = modelService.getModel( model );
        }
        
        HttpServletRequest request = ServletActionContext.getRequest();
        String value = null;
        
        
        if( equipment == null )
        {
            equipmentType = equipmentTypeService.getEquipmentType( equipmentTypeIcePacksId );
            
            // -----------------------------------------------------------------------------
            // Preparing Equipment
            // -----------------------------------------------------------------------------
            
            Equipment equipmentIcePacks = new Equipment();
            
            equipmentIcePacks.setEquipmentType( equipmentType );
            equipmentIcePacks.setOrganisationUnit( orgUnit );
            
            if( selModel != null )
            {
                equipmentIcePacks.setModel( selModel );
            }
            
            // -----------------------------------------------------------------------------
            // Preparing EquipmentAttributeValue Details
            // -----------------------------------------------------------------------------
            
            List<EquipmentTypeAttribute> equipmentTypeAttributes = new ArrayList<EquipmentTypeAttribute>( );
            for( EquipmentType_Attribute equipmentType_Attribute : equipmentType.getEquipmentType_Attributes() )
            {
                equipmentTypeAttributes.add( equipmentType_Attribute.getEquipmentTypeAttribute() );
            }
            
            List<EquipmentAttributeValue> equipmentDeatilsList = new ArrayList<EquipmentAttributeValue>();
            
            EquipmentAttributeValue equipmentAttributeValueDetails = null;
            for ( EquipmentTypeAttribute attribute : equipmentTypeAttributes )
            {
                
                value = request.getParameter( PREFIX_ATTRIBUTE + attribute.getId() );
                if ( StringUtils.isNotBlank( value ) )
                {
                    equipmentAttributeValueDetails = new EquipmentAttributeValue();
                    equipmentAttributeValueDetails.setEquipment( equipmentIcePacks );
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
                                System.out.println( "Option ID is  : " + option.getId() + "Option Name is : "+option.getName() );
                                
                                if ( EquipmentStatus.STATUS_NOT_WORKING.equalsIgnoreCase( option.getName() ) )
                                {
                                    equipmentIcePacks.setWorking( false );
                                }
                                else
                                {
                                    equipmentIcePacks.setWorking( true );
                                }
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
                    equipmentDeatilsList.add( equipmentAttributeValueDetails );
                }
            }
            
            // -----------------------------------------------------------------------------
            // Creating EquipmentAttributeValue Instance and saving equipmentAttributeValue data
            // -----------------------------------------------------------------------------
            Integer id = equipmentService.createEquipment( equipmentIcePacks, equipmentDeatilsList );

            message = id + "";
        }
        
        else
        {
            equipmentType = equipment.getEquipmentType();
            
            if( selModel != null )
            {
                equipment.setModel( selModel );
                
                equipmentService.updateEquipment( equipment );
            }
            
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
            
        }
        
        return SUCCESS;
    }

}