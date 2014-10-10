package org.hisp.dhis.coldchain.equipment;

import java.util.Collection;
import java.util.List;

import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.common.GenericStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;

public interface EquipmentStore extends GenericStore<Equipment>
{
    String ID = EquipmentStore.class.getName();
    
    //int addEquipment( Equipment equipment );

    //void updateEquipment( Equipment equipment );

    //void deleteEquipment( Equipment equipment );

    //Collection<Equipment> getAllEquipment();
    
    Collection<Equipment> getEquipments( OrganisationUnit orgUnit );
    
    Collection<Equipment> getEquipments( OrganisationUnit orgUnit, EquipmentType equipmentType );
    
    //int getCountEquipment( OrganisationUnit orgUnit, EquipmentType equipmentType );
    
    //Collection<Equipment> getEquipments( OrganisationUnit orgUnit, EquipmentType equipmentType, int min, int max );

   // int getCountEquipment( OrganisationUnit orgUnit, EquipmentType equipmentType, EquipmentTypeAttribute equipmentTypeAttribute, String searchText );

    //Collection<Equipment> getEquipments( OrganisationUnit orgUnit, EquipmentType equipmentType, EquipmentTypeAttribute equipmentTypeAttribute, String searchText, int min, int max );
    
    
    int getCountEquipment( List<OrganisationUnit> orgUnitList, EquipmentType equipmentType );
    
    Collection<Equipment> getEquipments( List<OrganisationUnit> orgUnitList, EquipmentType equipmentType, int min, int max );
    
    int getCountEquipment( String orgUnitIdsByComma, EquipmentType equipmentType, EquipmentTypeAttribute equipmentTypeAttribute, String searchText, String searchBy );

    Collection<Equipment> getEquipments( String orgUnitIdsByComma, EquipmentType equipmentType, EquipmentTypeAttribute equipmentTypeAttribute, String searchText, String searchBy, int min, int max );
    
    Collection<OrganisationUnit> searchOrgUnitListByName( String searchText );
    
    Collection<OrganisationUnit> searchOrgUnitListByCode( String searchText );
    
    
    // for orgUnit list according to orGUnit Attribute values for paging purpose
    int countOrgUnitByAttributeValue( Collection<Integer> orgunitIds, Attribute attribute, String searchText );
    Collection<OrganisationUnit> searchOrgUnitByAttributeValue( Collection<Integer> orgunitIds, Attribute attribute, String searchText, Integer min, Integer max );
    
    
    Collection<Equipment> getEquipmentList( OrganisationUnit organisationUnit, EquipmentType equipmentType );
    
}
