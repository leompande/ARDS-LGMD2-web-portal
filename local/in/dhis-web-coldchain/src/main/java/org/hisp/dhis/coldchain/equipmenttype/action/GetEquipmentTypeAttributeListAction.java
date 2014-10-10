package org.hisp.dhis.coldchain.equipmenttype.action;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.coldchain.equipment.EquipmentType;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeService;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeService;
import org.hisp.dhis.coldchain.equipment.EquipmentType_Attribute;
import org.hisp.dhis.coldchain.equipment.EquipmentType_AttributeService;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.paging.ActionPagingSupport;

public class GetEquipmentTypeAttributeListAction extends ActionPagingSupport<EquipmentTypeAttribute>
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
    private EquipmentTypeAttributeService equipmentTypeAttributeService;

    public void setEquipmentTypeAttributeService( EquipmentTypeAttributeService equipmentTypeAttributeService )
    {
        this.equipmentTypeAttributeService = equipmentTypeAttributeService;
    }
    
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
    // Input & Output
    // -------------------------------------------------------------------------
    private List<EquipmentTypeAttribute> equipmentTypeAttributes;

    public List<EquipmentTypeAttribute> getEquipmentTypeAttributes()
    {
        return equipmentTypeAttributes;
    }

    private Integer id;
    
    public void setId( Integer id )
    {
        this.id = id;
    }
    
    private String key;
    
    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }
    
    public List<EquipmentType_Attribute> equipmentTypeAttributeList;
    
    public List<EquipmentType_Attribute> getEquipmentTypeAttributeList()
    {
        return equipmentTypeAttributeList;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
    throws Exception
    {
        
        if ( isNotBlank( key ) ) // Filter on key only if set
        {
            this.paging = createPaging( equipmentTypeAttributeService.getEquipmentTypeAttributeCountByName( key ) );
            
            equipmentTypeAttributes = new ArrayList<EquipmentTypeAttribute>( equipmentTypeAttributeService.getEquipmentTypeAttributesBetweenByName( key, paging.getStartPos(), paging.getPageSize() ));
            Collections.sort( equipmentTypeAttributes, new IdentifiableObjectNameComparator() );
        }
        
        else if ( id != null )
        {
            EquipmentType equipmentType = equipmentTypeService.getEquipmentType( id );
            /*
            equipmentTypeAttributes = new ArrayList<EquipmentTypeAttribute>();
            
            for( EquipmentType_Attribute equipmentType_Attribute : equipmentType.getEquipmentType_Attributes() )
            {
                equipmentTypeAttributes.add( equipmentType_Attribute.getEquipmentTypeAttribute() );
            }
            */
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
            /*
            equipmentTypeAttributes = new ArrayList<EquipmentTypeAttribute>();
            for( EquipmentType_Attribute equipmentType_Attribute : equipmentTypeAttributeList )
            {
                equipmentTypeAttributes.add( equipmentType_Attribute.getEquipmentTypeAttribute() );
            }
            */
        }
       
        else
        {
            this.paging = createPaging( equipmentTypeAttributeService.getEquipmentTypeAttributeCount() );
         
            equipmentTypeAttributes = new ArrayList<EquipmentTypeAttribute>( equipmentTypeAttributeService.getEquipmentTypeAttributesBetween( paging.getStartPos(), paging.getPageSize() ));
            Collections.sort( equipmentTypeAttributes, new IdentifiableObjectNameComparator() );
        }
        
        //Collections.sort( equipmentTypeAttributes, new IdentifiableObjectNameComparator() );
        
        //System.out.println(" Inside GetEquipmentTypeAttributeListAction");
        /*
        if( id != null )
        {
            EquipmentType equipmentType = equipmentTypeService.getEquipmentType( id );
            
            equipmentTypeAttributes = new ArrayList<EquipmentTypeAttribute>( equipmentType.getEquipmentTypeAttributes() );
        }
        else
        {
            equipmentTypeAttributes = new ArrayList<EquipmentTypeAttribute>( equipmentTypeAttributeService.getAllEquipmentTypeAttributes() );
        }
        Collections.sort( equipmentTypeAttributes, new EquipmentTypeAttributeComparator() );
        */
        
        /**
         * TODO - need to write comparator for sorting the list
         */
        
        return SUCCESS;
    }

}
