package org.hisp.dhis.coldchain.equipment.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValue;
import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValueService;
import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentService;
import org.hisp.dhis.coldchain.equipment.EquipmentType;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeService;
import org.hisp.dhis.coldchain.equipment.EquipmentType_Attribute;
import org.hisp.dhis.coldchain.equipment.EquipmentType_AttributeService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version LoadEquipmentIcePacksDataAction.javaDec 21, 2012 12:10:22 PM	
 */

public class LoadEquipmentIcePacksDataAction  implements Action
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

    
    private EquipmentType_AttributeService equipmentType_AttributeService;
    
    public void setEquipmentType_AttributeService( EquipmentType_AttributeService equipmentType_AttributeService )
    {
        this.equipmentType_AttributeService = equipmentType_AttributeService;
    }
    
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int equipmentTypeId;
    
    public void setEquipmentTypeId( int equipmentTypeId )
    {
        this.equipmentTypeId = equipmentTypeId;
    }
    
    private int orgUnitId;
    

    public void setOrgUnitId( int orgUnitId )
    {
        this.orgUnitId = orgUnitId;
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
    
    private List<Equipment> equipmentList;

    public List<Equipment> getEquipmentList()
    {
        return equipmentList;
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
        
        organisationUnit = organisationUnitService.getOrganisationUnit(  orgUnitId );
        
        equipmentType = equipmentTypeService.getEquipmentType(  equipmentTypeId  );
        
        equipmentList = new ArrayList<Equipment>( equipmentService.getEquipments( organisationUnit, equipmentType ) );
        
        /*
        
        if( equipmentList == null || equipmentList.size() == 0 )
        {
            
        }
        else
        {
            equipment = equipmentList.get( 0 );
            
        }
        
        for( Equipment equipment : equipmentList )
        {
            
        }
        */
        
        equipmentTypeAttributeList = new ArrayList<EquipmentType_Attribute>();
        
        equipmentTypeAttributes = new ArrayList<EquipmentTypeAttribute>();
        
        if( equipmentList != null && equipmentList.size() > 0 )
        {
            equipment = equipmentList.get( 0 );
            
            equipmentTypeAttributeList = new ArrayList<EquipmentType_Attribute>( equipmentType_AttributeService.getAllEquipmentTypeAttributesByEquipmentType( equipment.getEquipmentType() ) );
            
            for( EquipmentType_Attribute equipmentType_Attribute : equipment.getEquipmentType().getEquipmentType_Attributes() )
            {
                equipmentTypeAttributes.add( equipmentType_Attribute.getEquipmentTypeAttribute() );
            }
        
        }
        
        else
        {
            equipmentTypeAttributeList = new ArrayList<EquipmentType_Attribute>( equipmentType_AttributeService.getAllEquipmentTypeAttributesByEquipmentType( equipmentType ) );
        
            for( EquipmentType_Attribute equipmentType_Attribute : equipmentTypeAttributeList )
            {
                equipmentTypeAttributes.add( equipmentType_Attribute.getEquipmentTypeAttribute() );
            }
        
        }
        
        //equipmentTypeAttributeList = new ArrayList<EquipmentType_Attribute>( equipmentType_AttributeService.getAllEquipmentTypeAttributesByEquipmentType( equipment.getEquipmentType() ) );
        
        equipmentValueMap = new HashMap<Integer, String>();
        
        for( EquipmentType_Attribute equipmentTypeAttribute1 : equipmentTypeAttributeList )
        {
            EquipmentAttributeValue equipmentAttributeValueDetails = equipmentAttributeValueService.getEquipmentAttributeValue( equipment, equipmentTypeAttribute1.getEquipmentTypeAttribute() );
           
            if( equipmentAttributeValueDetails != null && equipmentAttributeValueDetails.getValue() != null )
            {
                equipmentValueMap.put( equipmentTypeAttribute1.getEquipmentTypeAttribute().getId(), equipmentAttributeValueDetails.getValue() );
            }
        }
        
        
        return SUCCESS;
    }
}

