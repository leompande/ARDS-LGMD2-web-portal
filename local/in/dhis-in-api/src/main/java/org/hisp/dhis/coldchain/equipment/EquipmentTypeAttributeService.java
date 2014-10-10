package org.hisp.dhis.coldchain.equipment;

import java.util.Collection;

public interface EquipmentTypeAttributeService
{
    String ID = EquipmentTypeAttributeService.class.getName();
    
    int addEquipmentTypeAttribute( EquipmentTypeAttribute equipmentTypeAttribute );

    void updateEquipmentTypeAttribute( EquipmentTypeAttribute equipmentTypeAttribute );

    void deleteEquipmentTypeAttribute( EquipmentTypeAttribute equipmentTypeAttribute );

    Collection<EquipmentTypeAttribute> getAllEquipmentTypeAttributes();
    
    EquipmentTypeAttribute getEquipmentTypeAttribute( int id );
    
    EquipmentTypeAttribute getEquipmentTypeAttributeByName( String name );
    
    EquipmentTypeAttribute getEquipmentTypeAttributeByDescription( String description );
    
    //  methods
    
    int getEquipmentTypeAttributeCount();
    
    int getEquipmentTypeAttributeCountByName( String name );
    
    Collection<EquipmentTypeAttribute> getEquipmentTypeAttributesBetween( int first, int max );
    
    Collection<EquipmentTypeAttribute> getEquipmentTypeAttributesBetweenByName( String name, int first, int max );
    
    
    //Collection<EquipmentTypeAttribute> getAllEquipmentTypeAttributesForDisplay( EquipmentTypeAttribute equipmentTypeAttribute );
    

    
}
