package org.hisp.dhis.coldchain.equipmenttype.action;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.coldchain.equipment.EquipmentType;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeService;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.paging.ActionPagingSupport;

public class GetEquipmentTypeListAction extends ActionPagingSupport<EquipmentType>
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
    private EquipmentTypeService equipmentTypeService;

    public void setEquipmentTypeService( EquipmentTypeService equipmentTypeService )
    {
        this.equipmentTypeService = equipmentTypeService;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    private List<EquipmentType> equipmentTypes;

    public List<EquipmentType> getEquipmentTypes()
    {
        return equipmentTypes;
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

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        
        if ( isNotBlank( key ) ) // Filter on key only if set
        {
            this.paging = createPaging( equipmentTypeService.getEquipmentTypeCountByName( key ) );
            
            equipmentTypes = new ArrayList<EquipmentType>( equipmentTypeService.getEquipmentTypesBetweenByName( key, paging.getStartPos(), paging.getPageSize()) );
        }
        else
        {
            this.paging = createPaging( equipmentTypeService.getEquipmentTypeCount() );
            
            equipmentTypes = new ArrayList<EquipmentType>( equipmentTypeService.getEquipmentTypesBetween(paging.getStartPos(), paging.getPageSize()) );
        }
        
        Collections.sort( equipmentTypes, new IdentifiableObjectNameComparator() );
        
        /*
        equipmentTypes = new ArrayList<EquipmentType>( equipmentTypeService.getAllEquipmentTypes() );
        
        Collections.sort( equipmentTypes, new EquipmentTypeComparator() );
        */
        /**
         * TODO - need to write comparator for sorting the list
         */
        
        return SUCCESS;
    }

}
