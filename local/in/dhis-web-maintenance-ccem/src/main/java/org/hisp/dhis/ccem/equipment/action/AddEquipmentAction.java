package org.hisp.dhis.ccem.equipment.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.coldchain.model.Model;
import org.hisp.dhis.coldchain.model.ModelService;
import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValue;
import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentService;
import org.hisp.dhis.coldchain.equipment.EquipmentStatus;
import org.hisp.dhis.coldchain.equipment.EquipmentType;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeOption;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeOptionService;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeService;
import org.hisp.dhis.coldchain.equipment.EquipmentType_Attribute;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

public class AddEquipmentAction implements Action
{
    public static final String PREFIX_ATTRIBUTE = "attr";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;
    
    private EquipmentTypeService equipmentTypeService;
    
    private EquipmentTypeAttributeOptionService equipmentTypeAttributeOptionService;
    
    private EquipmentService equipmentService;
    
    private ModelService modelService;
    
    // -------------------------------------------------------------------------
    // Input/ Output
    // -------------------------------------------------------------------------
    
    private Integer ouId;
    
    private Integer itypeId;
    
    private String message;
    
    /*
    private Boolean workingStatus = false;
    
    public void setWorkingStatus( Boolean workingStatus )
    {
        this.workingStatus = workingStatus;
    }
    */
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

        OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouId );
        
        EquipmentType equipmentType = equipmentTypeService.getEquipmentType( itypeId );
        
        Model selModel = null;
        
        if( model != null )
        {    
            selModel = modelService.getModel( model );
        }
        
        // -----------------------------------------------------------------------------
        // Preparing Equipment
        // -----------------------------------------------------------------------------
        Equipment equipment = new Equipment();
        
        equipment.setEquipmentType( equipmentType );
        equipment.setOrganisationUnit( orgUnit );
        
        //equipment.setWorking( workingStatus );
        
        if( selModel != null )
        {
            equipment.setModel( selModel );
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
        
        List<EquipmentAttributeValue> equipmentAttributeValueDetailsList = new ArrayList<EquipmentAttributeValue>();
        
        EquipmentAttributeValue equipmentAttributeValueDetails = null;
        for ( EquipmentTypeAttribute attribute : equipmentTypeAttributes )
        {
            
            value = request.getParameter( PREFIX_ATTRIBUTE + attribute.getId() );
            if ( StringUtils.isNotBlank( value ) )
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
                            System.out.println( "Option ID is  : " + option.getId() + "Option Name is : "+option.getName() );
                            
                            if ( EquipmentStatus.STATUS_NOT_WORKING.equalsIgnoreCase( option.getName() ) )
                            {
                                equipment.setWorking( false );
                            }
                            else
                            {
                                equipment.setWorking( true );
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
                        //equipmentDetails.setEquipmentTypeAttributeOption( option );
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
                equipmentAttributeValueDetailsList.add( equipmentAttributeValueDetails );
            }
        }
        
        // -----------------------------------------------------------------------------
        // Creating EquipmentAttributeValue Instance and saving equipmentAttributeValue data
        // -----------------------------------------------------------------------------
        Integer id = equipmentService.createEquipment( equipment, equipmentAttributeValueDetailsList );

        message = id + "";
        
        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Setters and Getters
    // -------------------------------------------------------------------------

    public String getMessage()
    {
        return message;
    }

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    public void setEquipmentTypeService( EquipmentTypeService equipmentTypeService )
    {
        this.equipmentTypeService = equipmentTypeService;
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

    public void setModelService( ModelService modelService )
    {
        this.modelService = modelService;
    }

    public void setOuId( Integer ouId )
    {
        this.ouId = ouId;
    }

    public void setItypeId( Integer itypeId )
    {
        this.itypeId = itypeId;
    }
}
