package org.hisp.dhis.coldchain.equipmenttype.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.coldchain.equipment.EquipmentType;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeService;
import org.hisp.dhis.coldchain.equipment.EquipmentType_Attribute;
import org.hisp.dhis.coldchain.equipment.EquipmentType_AttributeService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version GetEquipmentTypeAttributeListForDisplayAction.javaOct 25, 2012 2:31:21 PM	
 */

public class GetEquipmentTypeAttributeListForDisplayAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
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
    // Input/output Getter/Setter
    // -------------------------------------------------------------------------
   
    private Integer id;
    
    public void setId( Integer id )
    {
        this.id = id;
    }
    
    public List<EquipmentType_Attribute> equipmentTypeAttributeList;
    
    public List<EquipmentType_Attribute> getEquipmentTypeAttributeList()
    {
        return equipmentTypeAttributeList;
    }
    
    private List<EquipmentTypeAttribute> equipmentTypeAttributes;

    public List<EquipmentTypeAttribute> getEquipmentTypeAttributes()
    {
        return equipmentTypeAttributes;
    }
    
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        
        EquipmentType equipmentType = equipmentTypeService.getEquipmentType( id );
        
        equipmentTypeAttributeList = new ArrayList<EquipmentType_Attribute>( equipmentType_AttributeService.getAllEquipmentTypeAttributeForDisplay( equipmentType, true ) );
        
        if( equipmentTypeAttributeList == null || equipmentTypeAttributeList.size() == 0  )
        {
            equipmentTypeAttributeList = new ArrayList<EquipmentType_Attribute>( equipmentType_AttributeService.getAllEquipmentTypeAttributesByEquipmentType( equipmentType ) );
            
            if( equipmentTypeAttributeList != null && equipmentTypeAttributeList.size() > 3 )
            {
                int count = 1;
                Iterator<EquipmentType_Attribute> iterator = equipmentTypeAttributeList.iterator();
                while( iterator.hasNext() )
                {
                    iterator.next();
                    
                    if( count > 3 )
                        iterator.remove();
                    
                    count++;
                }            
            }
            
        }
        
        equipmentTypeAttributes = new ArrayList<EquipmentTypeAttribute>();
        
        for( EquipmentType_Attribute equipmentType_Attribute : equipmentTypeAttributeList )
        {
            equipmentTypeAttributes.add( equipmentType_Attribute.getEquipmentTypeAttribute() );
        }
        
        return SUCCESS;
    }

}

