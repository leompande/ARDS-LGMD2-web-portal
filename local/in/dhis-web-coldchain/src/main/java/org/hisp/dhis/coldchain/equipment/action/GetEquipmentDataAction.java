package org.hisp.dhis.coldchain.equipment.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.coldchain.model.Model;
import org.hisp.dhis.coldchain.model.ModelAttributeValue;
import org.hisp.dhis.coldchain.model.ModelAttributeValueService;
import org.hisp.dhis.coldchain.model.ModelService;
import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeGroup;
import org.hisp.dhis.coldchain.model.comparator.ModelTypeAttributeComparator;
import org.hisp.dhis.coldchain.model.comparator.ModelTypeAttributeGroupOrderComparator;
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
    
    private ModelAttributeValueService modelAttributeValueService;
    
    public void setModelAttributeValueService( ModelAttributeValueService modelAttributeValueService )
    {
        this.modelAttributeValueService = modelAttributeValueService;
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
    /*
    private Map<Integer, List<EquipmentTypeAttributeOption>> equipmentTypeAttributeOptionsMap = new HashMap<Integer, List<EquipmentTypeAttributeOption>>();
    
    public Map<Integer, List<EquipmentTypeAttributeOption>> getEquipmentTypeAttributeOptionsMap()
    {
        return equipmentTypeAttributeOptionsMap;
    }
    */
    private Map<Integer, List<String>> equipmentTypeAttributeOptionsMap = new HashMap<Integer, List<String>>();
    
    public Map<Integer, List<String>> getEquipmentTypeAttributeOptionsMap()
    {
        return equipmentTypeAttributeOptionsMap;
    }
    
    
    
    public List<EquipmentType_Attribute> equipmentTypeAttributeList;
    
    public List<EquipmentType_Attribute> getEquipmentTypeAttributeList()
    {
        return equipmentTypeAttributeList;
    }
    
    private Model model;
    
    public Model getModel()
    {
        return model;
    }
    
    private List<ModelTypeAttribute> modelTypeAttributes = new ArrayList<ModelTypeAttribute>();
    
    public List<ModelTypeAttribute> getModelTypeAttributes()
    {
        return modelTypeAttributes;
    }
    
    private Map<Integer, String> modelTypeAttributeValueMap = new HashMap<Integer, String>();
    
    public Map<Integer, String> getModelTypeAttributeValueMap()
    {
        return modelTypeAttributeValueMap;
    }
    
    
    private List<ModelTypeAttributeGroup> modelTypeAttributeGroups;
  
    public List<ModelTypeAttributeGroup> getModelTypeAttributeGroups()
    {
        return modelTypeAttributeGroups;
    }
    
    private String equipmentModelName;
    
    public String getEquipmentModelName()
    {
        return equipmentModelName;
    }

    public void setEquipmentModelName( String equipmentModelName )
    {
        this.equipmentModelName = equipmentModelName;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        
        equipment = equipmentService.getEquipment( equipmentId );
        //System.out.println( equipment.getModel().getId() + "-----" + equipment.getModel().getName() );
        
        
        equipmentModelName = null;
        
        if( equipment.getModel() != null && equipment.getModel().getName() != "" )
        {
            equipmentModelName = equipment.getModel().getName();
        }
        
        //equipment.getOrganisationUnit().getName();
        
        //equipment.getEquipmentType().getModelType();
        
        //equipment.getEquipmentType().getName();
        
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
        
        //List<EquipmentAttributeValue> equipmentDetailsList = new ArrayList<EquipmentAttributeValue>( equipmentAttributeValueService.getEquipments( equipment ) );
       
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
        for( EquipmentAttributeValue equipmentDetails : equipmentDetailsList )
        {
            if ( EquipmentTypeAttribute.TYPE_COMBO.equalsIgnoreCase( equipmentDetails.getEquipmentTypeAttribute().getValueType() ) )
            {
                System.out.println(" --- EquipmentType Attribute Option Name ---  " + equipmentDetails.getEquipmentTypeAttributeOption().getName().toString() );
                equipmentValueMap.put( equipmentDetails.getEquipmentTypeAttribute().getId(), equipmentDetails.getEquipmentTypeAttributeOption().getName() );
            }
            else
            {
                equipmentValueMap.put( equipmentDetails.getEquipmentTypeAttribute().getId(), equipmentDetails.getValue() );
            }
        }
        */
        for( EquipmentTypeAttribute equipmentTypeAttribute : equipmentTypeAttributes )
        {       
            /*
            List<EquipmentTypeAttributeOption> equipmentTypeAttributeOptions = new ArrayList<EquipmentTypeAttributeOption>();
            if( EquipmentTypeAttribute.TYPE_COMBO.equalsIgnoreCase( equipmentTypeAttribute.getValueType() ) )
            {
                equipmentTypeAttributeOptions = new ArrayList<EquipmentTypeAttributeOption>( equipmentTypeAttribute.getAttributeOptions() );
                Collections.sort( equipmentTypeAttributeOptions, new EquipmentTypeAttributeOptionComparator() );
                equipmentTypeAttributeOptionsMap.put( equipmentTypeAttribute.getId(), equipmentTypeAttributeOptions );
            }
            */
            
            List<String> equipmentTypeAttributeOptions = new ArrayList<String>();
            
            if( EquipmentTypeAttribute.TYPE_COMBO.equalsIgnoreCase( equipmentTypeAttribute.getValueType() ) )
            {
               
                equipmentTypeAttributeOptions = new ArrayList<String>( equipmentTypeAttribute.getOptionSet().getOptions() );
                equipmentTypeAttributeOptionsMap.put( equipmentTypeAttribute.getId(), equipmentTypeAttributeOptions );
            }
            
        }
        
        ModelType tempModelType = equipment.getEquipmentType().getModelType();
        
        if( tempModelType != null )
        {
            models = new ArrayList<Model>( modelService.getModels( tempModelType ) );
        }
        
        // model Details of EquipmentAttributeValue
        
        
        model = equipment.getModel();
        
        if ( model != null )
        {
            ModelType modelType = model.getModelType();
            
            //modelTypeAttributes = modelType.getModelTypeAttributes();
            
            modelTypeAttributes = new ArrayList<ModelTypeAttribute> ( modelType.getModelTypeAttributes());
            Collections.sort( modelTypeAttributes, new ModelTypeAttributeComparator() );
            
            List<ModelAttributeValue> modelAttributeValues = new ArrayList<ModelAttributeValue>( modelAttributeValueService.getAllModelAttributeValuesByModel( model ) );
            
            if ( modelAttributeValues != null && modelAttributeValues.size() != 0 )
            {
                for( ModelAttributeValue modelAttributeValue : modelAttributeValues )
                {
                    if ( ModelTypeAttribute.TYPE_COMBO.equalsIgnoreCase( modelAttributeValue.getModelTypeAttribute().getValueType() ) )
                    {
                        modelTypeAttributeValueMap.put( modelAttributeValue.getModelTypeAttribute().getId(), modelAttributeValue.getValue() );
                    }
                    
                    
                    /*
                    else if ( ModelTypeAttribute.TYPE_BOOL.equalsIgnoreCase( modelAttributeValue.getModelTypeAttribute().getValueType() ) )
                    {
                        if ( modelAttributeValue.getValue().equalsIgnoreCase( "false" ) )
                        {
                            modelTypeAttributeValueMap.put( modelAttributeValue.getModelTypeAttribute().getId(), "No" );
                        }
                        else
                        {
                            modelTypeAttributeValueMap.put( modelAttributeValue.getModelTypeAttribute().getId(), "Yes" );
                        }
                        
                    }
                    */
                    else
                    {
                        modelTypeAttributeValueMap.put( modelAttributeValue.getModelTypeAttribute().getId(), modelAttributeValue.getValue() );
                    }
                }
            }

            modelTypeAttributeGroups = new ArrayList<ModelTypeAttributeGroup>( modelType.getModelTypeAttributeGroups() );
            
            Collections.sort( modelTypeAttributeGroups, new ModelTypeAttributeGroupOrderComparator() );
            
        }
        
        return SUCCESS;
    }
}
