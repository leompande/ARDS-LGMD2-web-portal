package org.hisp.dhis.ccem.equipment.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.coldchain.model.Model;
import org.hisp.dhis.coldchain.model.ModelService;
import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.equipment.EquipmentType;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeOption;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeService;
import org.hisp.dhis.coldchain.equipment.EquipmentType_Attribute;
import org.hisp.dhis.coldchain.equipment.comparator.EquipmentTypeAttributeOptionComparator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

public class ShowAddEquipmentFormAction implements Action
{
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
    
    private ModelService modelService;
    
    public void setModelService( ModelService modelService )
    {
        this.modelService = modelService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private String orgUnitId;
    
    public void setOrgUnitId( String orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    private String equipmentTypeId;

    public void setEquipmentTypeId( String equipmentTypeId )
    {
        this.equipmentTypeId = equipmentTypeId;
    }

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    private EquipmentType equipmentType;

    public EquipmentType getEquipmentType()
    {
        return equipmentType;
    }

    private List<EquipmentTypeAttribute> equipmentTypeAttributes;
    
    public List<EquipmentTypeAttribute> getEquipmentTypeAttributes()
    {
        return equipmentTypeAttributes;
    }

    private List<Model> models;
    
    public List<Model> getModels()
    {
        return models;
    }
    
    private Map<Integer, List<EquipmentTypeAttributeOption>> equipmentTypeAttributeOptionsMap = new HashMap<Integer, List<EquipmentTypeAttributeOption>>();
    
    public Map<Integer, List<EquipmentTypeAttributeOption>> getEquipmentTypeAttributeOptionsMap()
    {
        return equipmentTypeAttributeOptionsMap;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        models = new ArrayList<Model>();
        
        organisationUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitId ) );
        
        equipmentType = equipmentTypeService.getEquipmentType( Integer.parseInt( equipmentTypeId ) );
        
        equipmentTypeAttributes = new ArrayList<EquipmentTypeAttribute>( );
        for( EquipmentType_Attribute equipmentType_Attribute : equipmentType.getEquipmentType_Attributes() )
        {
            equipmentTypeAttributes.add( equipmentType_Attribute.getEquipmentTypeAttribute() );
        }
        
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

            /*
            System.out.println( "Name :" + modelTypeAttribute.getName() );
            System.out.println( "valueType :" + modelTypeAttribute.getValueType() );
            System.out.println( "Is mandatory :" + modelTypeAttribute.isMandatory() );
            */
        }
        
        
        ModelType modelType = equipmentType.getModelType();
        
        if( modelType != null )
        {
            models = new ArrayList<Model>( modelService.getModels( modelType ) );
        }
        /*
        System.out.println( "Size of models is --- "  + models.size() );
        
        for( Model model : models)
        {
            System.out.println( "Model name is "  + model.getName() );
            System.out.println( "Model Id is "  + model.getId() );
        }
        */
        
        return SUCCESS;
    }

}
