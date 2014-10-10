package org.hisp.dhis.coldchain.equipment;

import static org.hisp.dhis.i18n.I18nUtils.getCountByName;
import static org.hisp.dhis.i18n.I18nUtils.getObjectsBetween;
import static org.hisp.dhis.i18n.I18nUtils.getObjectsBetweenByName;

import java.util.Collection;

import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeService;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeStore;
import org.hisp.dhis.i18n.I18nService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultEquipmentTypeAttributeService implements EquipmentTypeAttributeService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private EquipmentTypeAttributeStore equipmentTypeAttributeStore;

    public void setEquipmentTypeAttributeStore( EquipmentTypeAttributeStore equipmentTypeAttributeStore )
    {
        this.equipmentTypeAttributeStore = equipmentTypeAttributeStore;
    }
    
    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    // -------------------------------------------------------------------------
    // EquipmentTypeAttribute
    // -------------------------------------------------------------------------
    /*
    @Transactional
    @Override
    public int addEquipmentTypeAttribute( EquipmentTypeAttribute equipmentTypeAttribute )
    {
        return equipmentTypeAttributeStore.addEquipmentTypeAttribute( equipmentTypeAttribute );
    }
    
    @Transactional
    @Override
    public void deleteEquipmentTypeAttribute( EquipmentTypeAttribute equipmentTypeAttribute )
    {
        equipmentTypeAttributeStore.deleteEquipmentTypeAttribute( equipmentTypeAttribute );
    }
    
    @Transactional
    @Override
    public Collection<EquipmentTypeAttribute> getAllEquipmentTypeAttributes()
    {
        return equipmentTypeAttributeStore.getAllEquipmentTypeAttributes();
    }
    
    @Transactional
    @Override
    public void updateEquipmentTypeAttribute( EquipmentTypeAttribute equipmentTypeAttribute )
    {
        equipmentTypeAttributeStore.updateEquipmentTypeAttribute( equipmentTypeAttribute );
    }
    
    public EquipmentTypeAttribute getEquipmentTypeAttribute( int id )
    {
        return equipmentTypeAttributeStore.getEquipmentTypeAttribute( id );
    }
    
    public  EquipmentTypeAttribute getEquipmentTypeAttributeByName( String name )
    {
        return equipmentTypeAttributeStore.getEquipmentTypeAttributeByName( name );
    }
    */
    

    @Override
    public int addEquipmentTypeAttribute( EquipmentTypeAttribute equipmentTypeAttribute )
    {
        return equipmentTypeAttributeStore.save( equipmentTypeAttribute );
    }
    
    @Override
    public void deleteEquipmentTypeAttribute( EquipmentTypeAttribute equipmentTypeAttribute )
    {
        equipmentTypeAttributeStore.delete( equipmentTypeAttribute );
    }
    
    @Override
    public Collection<EquipmentTypeAttribute> getAllEquipmentTypeAttributes()
    {
        return equipmentTypeAttributeStore.getAllEquipmentTypeAttributes();
    }
    
    @Override
    public void updateEquipmentTypeAttribute( EquipmentTypeAttribute equipmentTypeAttribute )
    {
        equipmentTypeAttributeStore.update( equipmentTypeAttribute );
    }
    
    public EquipmentTypeAttribute getEquipmentTypeAttribute( int id )
    {
        return equipmentTypeAttributeStore.getEquipmentTypeAttribute( id );
    }
    
    public  EquipmentTypeAttribute getEquipmentTypeAttributeByName( String name )
    {
        return equipmentTypeAttributeStore.getEquipmentTypeAttributeByName( name );
    }

    public EquipmentTypeAttribute getEquipmentTypeAttributeByDescription( String description )
    {
        return equipmentTypeAttributeStore.getEquipmentTypeAttributeByDescription( description );
    }

    
    //Methods
    public int getEquipmentTypeAttributeCount()
    {
        return equipmentTypeAttributeStore.getCount();
    }
    
    public int getEquipmentTypeAttributeCountByName( String name )
    {
        return getCountByName( i18nService, equipmentTypeAttributeStore, name );
    }

    public Collection<EquipmentTypeAttribute> getEquipmentTypeAttributesBetween( int first, int max )
    {
        return getObjectsBetween( i18nService, equipmentTypeAttributeStore, first, max );
    }
    
    public Collection<EquipmentTypeAttribute> getEquipmentTypeAttributesBetweenByName( String name, int first, int max )
    {
        return getObjectsBetweenByName( i18nService, equipmentTypeAttributeStore, name, first, max );
    }
    /*
    public Collection<EquipmentTypeAttribute> getAllEquipmentTypeAttributesForDisplay( EquipmentTypeAttribute equipmentTypeAttribute )
    {
        return equipmentTypeAttributeStore.getAllEquipmentTypeAttributesForDisplay( equipmentTypeAttribute );
    }
    */
}
