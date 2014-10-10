package org.hisp.dhis.ccem.equipmenttype.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeService;
import org.hisp.dhis.coldchain.equipment.EquipmentType;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeService;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeService;
import org.hisp.dhis.coldchain.equipment.EquipmentType_Attribute;

import com.opensymphony.xwork2.Action;

public class ShowUpdateEquipmentTypeAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private EquipmentTypeService equipmentTypeService;

    public void setEquipmentTypeService( EquipmentTypeService equipmentTypeService )
    {
        this.equipmentTypeService = equipmentTypeService;
    }
    private EquipmentTypeAttributeService equipmentTypeAttributeService;
    
    public void setEquipmentTypeAttributeService( EquipmentTypeAttributeService equipmentTypeAttributeService )
    {
        this.equipmentTypeAttributeService = equipmentTypeAttributeService;
    }
    
    private ModelTypeService modelTypeService;

    public void setModelTypeService( ModelTypeService modelTypeService )
    {
        this.modelTypeService = modelTypeService;
    }
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------
    private String id;

    public void setId( String id )
    {
        this.id = id;
    }
    
    private EquipmentType equipmentType;
    
    public EquipmentType getEquipmentType()
    {
        return equipmentType;
    }

    private List<EquipmentTypeAttribute> availEquipmentTypeAttributes;

    public List<EquipmentTypeAttribute> getAvailEquipmentTypeAttributes()
    {
        return availEquipmentTypeAttributes;
    }

    private List<EquipmentType_Attribute> selEquipmentTypeAttributes;
    
    public List<EquipmentType_Attribute> getSelEquipmentTypeAttributes()
    {
        return selEquipmentTypeAttributes;
    }
    /*
    private List<EquipmentTypeAttribute> selEquipmentTypeAttributes;

    public List<EquipmentTypeAttribute> getSelEquipmentTypeAttributes()
    {
        return selEquipmentTypeAttributes;
    }
    */
    private List<ModelType> modelTypes;

    public List<ModelType> getModelTypes()
    {
        return modelTypes;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        equipmentType = equipmentTypeService.getEquipmentType( Integer.parseInt( id ) );
        
        modelTypes =  new ArrayList<ModelType>( modelTypeService.getAllModelTypes() );
        
        availEquipmentTypeAttributes = new ArrayList<EquipmentTypeAttribute>( equipmentTypeAttributeService.getAllEquipmentTypeAttributes() );
        
        selEquipmentTypeAttributes = new ArrayList<EquipmentType_Attribute>( equipmentType.getEquipmentType_Attributes() );
        /*
        for( EquipmentType_Attribute equipmentType_Attribute : selEquipmentTypeAttributes )
        {
            System.out.println( "ID---" + equipmentType_Attribute.getEquipmentTypeAttribute().getId() );
            System.out.println( "Name---" + equipmentType_Attribute.getEquipmentTypeAttribute().getName());
            System.out.println( "ValueType---" + equipmentType_Attribute.getEquipmentTypeAttribute().getValueType() );
            System.out.println( "Sort Order---" + equipmentType_Attribute.getSortOrder() );
            System.out.println( "Display---" + equipmentType_Attribute.isDisplay());
        }
        */
        
        /*
        selEquipmentTypeAttributes = new ArrayList<EquipmentTypeAttribute>( equipmentType.getEquipmentTypeAttributes() );
        
        availEquipmentTypeAttributes.removeAll( selEquipmentTypeAttributes );
        */
        return SUCCESS;        
    }
}
