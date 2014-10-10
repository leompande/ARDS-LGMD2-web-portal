package org.hisp.dhis.coldchain.equipment;

import static org.hisp.dhis.i18n.I18nUtils.getCountByName;
import static org.hisp.dhis.i18n.I18nUtils.getObjectsBetween;
import static org.hisp.dhis.i18n.I18nUtils.getObjectsBetweenByName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.coldchain.equipment.EquipmentType;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeService;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeStore;
import org.hisp.dhis.coldchain.equipment.EquipmentType_Attribute;
import org.hisp.dhis.i18n.I18nService;
import org.springframework.transaction.annotation.Transactional;
@Transactional
public class DefaultEquipmentTypeService implements EquipmentTypeService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private EquipmentTypeStore equipmentTypeStore;

    public void setEquipmentTypeStore( EquipmentTypeStore equipmentTypeStore )
    {
        this.equipmentTypeStore = equipmentTypeStore;
    }
    
    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }
    
    // -------------------------------------------------------------------------
    // EquipmentType
    // -------------------------------------------------------------------------
    /*
    @Transactional
    @Override
    public int addEquipmentType( EquipmentType equipmentType )
    {
        return equipmentTypeStore.addEquipmentType( equipmentType );
    }

    @Transactional
    @Override
    public void deleteEquipmentType( EquipmentType equipmentType )
    {
        equipmentTypeStore.deleteEquipmentType( equipmentType );
    }

    @Transactional
    @Override
    public Collection<EquipmentType> getAllEquipmentTypes()
    {
        return equipmentTypeStore.getAllEquipmentTypes();
    }

    @Transactional
    @Override
    public void updateEquipmentType( EquipmentType equipmentType )
    {
        equipmentTypeStore.updateEquipmentType( equipmentType );
    }
    
    public EquipmentType getEquipmentTypeByName( String name )
    {
        return equipmentTypeStore.getEquipmentTypeByName( name );
    }
    
    public EquipmentType getEquipmentType( int id )
    {
        return equipmentTypeStore.getEquipmentType( id );
    }
    */
    
 
    @Override
    public int addEquipmentType( EquipmentType equipmentType )
    {
        return equipmentTypeStore.save( equipmentType );
    }
   
    @Override
    public void deleteEquipmentType( EquipmentType equipmentType )
    {
        equipmentTypeStore.delete( equipmentType );
    }
    
    @Override
    public void updateEquipmentType( EquipmentType equipmentType )
    {
        equipmentTypeStore.update( equipmentType );
    }
    
    @Override
    public Collection<EquipmentType> getAllEquipmentTypes()
    {
        return equipmentTypeStore.getAllEquipmentTypes();
    }
    
    @Override
    public EquipmentType getEquipmentTypeByName( String name )
    {
        return equipmentTypeStore.getEquipmentTypeByName( name );
    }
    
    @Override
    public EquipmentType getEquipmentType( int id )
    {
        return equipmentTypeStore.getEquipmentType( id );
    }
    
    //Methods
    public int getEquipmentTypeCount()
    {
        return equipmentTypeStore.getCount();
    }
    
    public int getEquipmentTypeCountByName( String name )
    {
        return getCountByName( i18nService, equipmentTypeStore, name );
    }
    
    public Collection<EquipmentType> getEquipmentTypesBetween( int first, int max )
    {
        return getObjectsBetween( i18nService, equipmentTypeStore, first, max );
    }
    
    public Collection<EquipmentType> getEquipmentTypesBetweenByName( String name, int first, int max )
    {
        return getObjectsBetweenByName( i18nService, equipmentTypeStore, name, first, max );
    } 
    /*
    public Collection<EquipmentTypeAttribute> getAllEquipmentTypeAttributesForDisplay( EquipmentType equipmentType )
    {
        return equipmentTypeStore.getAllEquipmentTypeAttributesForDisplay( equipmentType );
    }
    */
    public Collection<EquipmentTypeAttribute> getAllEquipmentTypeAttributesForDisplay( EquipmentType equipmentType )
    {
        List<EquipmentTypeAttribute> equipmentTypeAttributeList = new ArrayList<EquipmentTypeAttribute>();
       
        List<EquipmentType_Attribute> equipmentType_AttributeList = new ArrayList<EquipmentType_Attribute>( equipmentType.getEquipmentType_Attributes() );
        for ( EquipmentType_Attribute equipmentType_Attribute : equipmentType_AttributeList )
        {
            
            if ( equipmentType_Attribute.isDisplay() )
            {
                equipmentTypeAttributeList.add( equipmentType_Attribute.getEquipmentTypeAttribute() );
            }
        }

        return equipmentTypeAttributeList;
    }
    
    
    
}
