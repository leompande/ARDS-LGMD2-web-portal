package org.hisp.dhis.ccem.equipment.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.coldchain.model.Model;
import org.hisp.dhis.coldchain.model.ModelService;
import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValue;
import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValueService;
import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentService;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeOption;
import org.hisp.dhis.coldchain.equipment.EquipmentType_Attribute;
import org.hisp.dhis.coldchain.equipment.EquipmentType_AttributeService;
import org.hisp.dhis.coldchain.equipment.comparator.EquipmentTypeAttributeOptionComparator;

import com.opensymphony.xwork2.Action;

public class GetEquipmentDataAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private EquipmentService equipmentService;

    public void setEquipmentService( EquipmentService equipmentService )
    {
        this.equipmentService = equipmentService;
    }
    
    private EquipmentAttributeValueService equipmentAttributeValueService;
    
    public void setEquipmentAttributeValueService( EquipmentAttributeValueService equipmentAttributeValueService )
    {
        this.equipmentAttributeValueService = equipmentAttributeValueService;
    }

    
    private ModelService modelService;
    
    public void setModelService( ModelService modelService )
    {
        this.modelService = modelService;
    }
    
    private EquipmentType_AttributeService equipmentType_AttributeService;
    
    public void setEquipmentType_AttributeService( EquipmentType_AttributeService equipmentType_AttributeService )
    {
        this.equipmentType_AttributeService = equipmentType_AttributeService;
    }
    
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer equipmentId;
    
    public void setEquipmentId( Integer equipmentId )
    {
        this.equipmentId = equipmentId;
    }

    private Equipment equipment;

    public Equipment getEquipment()
    {
        return equipment;
    }

    private List<EquipmentTypeAttribute> equipmentTypeAttributes;
    
    public List<EquipmentTypeAttribute> getEquipmentTypeAttributes()
    {
        return equipmentTypeAttributes;
    }

    private Map<Integer, String> equipmentValueMap;
    
    public Map<Integer, String> getEquipmentValueMap()
    {
        return equipmentValueMap;
    }

    private List<Model> models;
    
    public List<Model> getModels()
    {
        return models;
    }
    
    private int equipmentModelId;
    
    public int getEquipmentModelId()
    {
        return equipmentModelId;
    }

    private Map<Integer, List<EquipmentTypeAttributeOption>> equipmentTypeAttributeOptionsMap = new HashMap<Integer, List<EquipmentTypeAttributeOption>>();
    
    public Map<Integer, List<EquipmentTypeAttributeOption>> getEquipmentTypeAttributeOptionsMap()
    {
        return equipmentTypeAttributeOptionsMap;
    }
    
    public List<EquipmentType_Attribute> equipmentTypeAttributeList;
    
    public List<EquipmentType_Attribute> getEquipmentTypeAttributeList()
    {
        return equipmentTypeAttributeList;
    }
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        equipment = equipmentService.getEquipment( equipmentId );
        //System.out.println( equipment.getModel().getId() + "-----" + equipment.getModel().getName() );
        
        if ( equipment.getModel() != null )
        {
            equipmentModelId = equipment.getModel().getId();
        }
        else
        {
            equipmentModelId = 0;
        }
        
        equipmentTypeAttributes = new ArrayList<EquipmentTypeAttribute>( );
        for( EquipmentType_Attribute equipmentType_Attribute : equipment.getEquipmentType().getEquipmentType_Attributes() )
        {
            equipmentTypeAttributes.add( equipmentType_Attribute.getEquipmentTypeAttribute() );
        }
        
        equipmentValueMap = new HashMap<Integer, String>();
        
        //List<EquipmentAttributeValue> equipmentAttributeValueDetailsList = new ArrayList<EquipmentAttributeValue>( equipmentService.getEquipments( equipment ) );
       
        equipmentTypeAttributeList = new ArrayList<EquipmentType_Attribute>( equipmentType_AttributeService.getAllEquipmentTypeAttributesByEquipmentType( equipment.getEquipmentType() ) );
        
        for( EquipmentType_Attribute equipmentTypeAttribute1 : equipmentTypeAttributeList )
        {
            EquipmentAttributeValue equipmentAttributeValueDetails = equipmentAttributeValueService.getEquipmentAttributeValue( equipment, equipmentTypeAttribute1.getEquipmentTypeAttribute() );
            if( equipmentAttributeValueDetails != null && equipmentAttributeValueDetails.getValue() != null )
            {
                equipmentValueMap.put( equipmentTypeAttribute1.getEquipmentTypeAttribute().getId(), equipmentAttributeValueDetails.getValue() );
                //equipmentDetailsMap.put( equipment.getId()+":"+equipmentTypeAttribute1.getEquipmentTypeAttribute().getId(), equipmentDetails.getValue() );
            }
        }
   
        /*
        for( EquipmentAttributeValue equipmentAttributeValueDetails : equipmentAttributeValueDetailsList )
        {
            if ( EquipmentTypeAttribute.TYPE_COMBO.equalsIgnoreCase( equipmentDetails.getEquipmentTypeAttribute().getValueType() ) )
            {
                System.out.println(" --- EquipmentType Attribute Option Name ---  " + equipmentDetails.getEquipmentTypeAttributeOption().getName().toString() );
                equipmentValueMap.put( equipmentDetails.getEquipmentTypeAttribute().getId(), equipmentDetails.getEquipmentTypeAttributeOption().getName() );
            }
            else
            {
                equipmentValueMap.put( equipmentAttributeValueDetails.getEquipmentTypeAttribute().getId(), equipmentAttributeValueDetails.getValue() );
            }
        }
        */
        for( EquipmentTypeAttribute equipmentTypeAttribute : equipmentTypeAttributes )
        {
            List<EquipmentTypeAttributeOption> equipmentTypeAttributeOptions = new ArrayList<EquipmentTypeAttributeOption>();
            if( EquipmentTypeAttribute.TYPE_COMBO.equalsIgnoreCase( equipmentTypeAttribute.getValueType() ) )
            {
                //System.out.println(" inside equipmentTypeAttribute.TYPE_COMBO ");
                equipmentTypeAttributeOptions = new ArrayList<EquipmentTypeAttributeOption>( equipmentTypeAttribute.getAttributeOptions() );
                Collections.sort( equipmentTypeAttributeOptions, new EquipmentTypeAttributeOptionComparator() );
                equipmentTypeAttributeOptionsMap.put( equipmentTypeAttribute.getId(), equipmentTypeAttributeOptions );
            }

        }
        
        
        
        ModelType modelType = equipment.getEquipmentType().getModelType();
        
        if( modelType != null )
        {
            models = new ArrayList<Model>( modelService.getModels( modelType ) );
        }

        return SUCCESS;
    }
}
