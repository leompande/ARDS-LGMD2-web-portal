package org.hisp.dhis.coldchain.equipment;

import java.util.Collection;

public interface EquipmentTypeService
{
    String ID = EquipmentTypeService.class.getName();
    
    int addEquipmentType( EquipmentType equipmentType );

    void updateEquipmentType( EquipmentType equipmentType );

    void deleteEquipmentType( EquipmentType equipmentType );

    Collection<EquipmentType> getAllEquipmentTypes();
    
    EquipmentType getEquipmentTypeByName( String name );
    
    EquipmentType getEquipmentType( int id );
    
    //  methods
    
    int getEquipmentTypeCount();
    
    int getEquipmentTypeCountByName( String name );
    
    Collection<EquipmentType> getEquipmentTypesBetween( int first, int max );
    
    Collection<EquipmentType> getEquipmentTypesBetweenByName( String name, int first, int max );
    
    Collection<EquipmentTypeAttribute> getAllEquipmentTypeAttributesForDisplay( EquipmentType equipmentType );
    
    
}
