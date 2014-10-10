package org.hisp.dhis.coldchain.equipment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValue;
import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValueService;
import org.hisp.dhis.coldchain.equipment.EquipmentDataValue;
import org.hisp.dhis.coldchain.equipment.EquipmentDataValueService;
import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentService;
import org.hisp.dhis.coldchain.equipment.EquipmentStore;
import org.hisp.dhis.coldchain.equipment.EquipmentStatus;
import org.hisp.dhis.coldchain.equipment.EquipmentStatusService;
import org.hisp.dhis.coldchain.equipment.EquipmentType;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultEquipmentService implements EquipmentService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private EquipmentStore equipmentStore;

    public void setEquipmentStore( EquipmentStore equipmentStore )
    {
        this.equipmentStore = equipmentStore;
    }

    private EquipmentAttributeValueService equipmentAttributeValueService;
    
    public void setEquipmentAttributeValueService( EquipmentAttributeValueService equipmentAttributeValueService )
    {
        this.equipmentAttributeValueService = equipmentAttributeValueService;
    }

    private EquipmentStatusService equipmentStatusService;
    
    public void setEquipmentStatusService( EquipmentStatusService equipmentStatusService )
    {
        this.equipmentStatusService = equipmentStatusService;
    }
    
    private EquipmentDataValueService equipmentDataValueService;
    
    public void setEquipmentDataValueService( EquipmentDataValueService equipmentDataValueService )
    {
        this.equipmentDataValueService = equipmentDataValueService;
    }
    
    // -------------------------------------------------------------------------
    // Equipment
    // -------------------------------------------------------------------------
    

    @Override
    public int addEquipment( Equipment equipment )
    {
        //return equipmentStore.addEquipment( equipment );
        return equipmentStore.save( equipment );
    }
    @Override
    public void deleteEquipment( Equipment equipment )
    {
        //equipmentStore.deleteEquipment( equipment );
        equipmentStore.delete( equipment );
    }
    
    public void deleteCompleteEquipment( Equipment equipment )
    {
        List<EquipmentAttributeValue> equipmentAttributeValueDetailsList = new ArrayList<EquipmentAttributeValue>( equipmentAttributeValueService.getEquipmentAttributeValues( equipment ) );
        
        Collection<EquipmentDataValue> equipmentDataValueForDelete = equipmentDataValueService.getAllEquipmentDataValuesByEquipment( equipment );

        for( EquipmentAttributeValue equipmentAttributeValueDetails : equipmentAttributeValueDetailsList )
        {
            equipmentAttributeValueService.deleteEquipmentAttributeValue( equipmentAttributeValueDetails );
        }
        
        for ( EquipmentDataValue equipmentDataValueDelete : equipmentDataValueForDelete )
        {
            equipmentDataValueService.deleteEquipmentDataValue( equipmentDataValueDelete );
        }
        
        List<EquipmentStatus> equipmentStatusHistory = new ArrayList<EquipmentStatus>( equipmentStatusService.getEquipmentStatusHistory( equipment ) );
        for( EquipmentStatus equipmentStatus : equipmentStatusHistory )
        {
            equipmentStatusService.deleteEquipmentStatus( equipmentStatus );
        }
        
        deleteEquipment( equipment );
    }
    
    @Override
    public Collection<Equipment> getAllEquipment()
    {
        //return equipmentStore.getAllEquipment();
        return equipmentStore.getAll();
    }
    @Override
    public void updateEquipment( Equipment equipment )
    {
        //equipmentStore.updateEquipment( equipment );
        equipmentStore.update( equipment );
    }
    
    public int createEquipment( Equipment equipment, List<EquipmentAttributeValue> equipmentAttributeValueDetails )
    {
        int equipmentId = addEquipment( equipment );
        
        for( EquipmentAttributeValue equipmentAttributeValue : equipmentAttributeValueDetails )
        {
            equipmentAttributeValueService.addEquipmentAttributeValue( equipmentAttributeValue );
        }
        
        return equipmentId;
    }

    public Equipment getEquipment( int id )
    {
        return equipmentStore.get( id );
    }
    
    public Collection<Equipment> getEquipments( OrganisationUnit orgUnit )
    {
        return equipmentStore.getEquipments( orgUnit );
    }

    public Collection<Equipment> getEquipments( OrganisationUnit orgUnit, EquipmentType equipmentType )
    {
        return equipmentStore.getEquipments( orgUnit, equipmentType );
    }

    //public Collection<Equipment> getEquipments( OrganisationUnit orgUnit, EquipmentType equipmentType, int min, int max )
    public Collection<Equipment> getEquipments( List<OrganisationUnit> orgUnitList, EquipmentType equipmentType, int min, int max )
    {
        return equipmentStore.getEquipments( orgUnitList, equipmentType, min, max );
    }

   
   //public int getCountEquipment( OrganisationUnit orgUnit, EquipmentType equipmentType )
    public int getCountEquipment( List<OrganisationUnit> orgUnitList, EquipmentType equipmentType )
    {
        return equipmentStore.getCountEquipment( orgUnitList, equipmentType );
    }

    //public int getCountEquipment( OrganisationUnit orgUnit, EquipmentType equipmentType, EquipmentTypeAttribute equipmentTypeAttribute, String searchText )
    public int getCountEquipment( String orgUnitIdsByComma, EquipmentType equipmentType, EquipmentTypeAttribute equipmentTypeAttribute, String searchText, String searchBy )
    {
        return equipmentStore.getCountEquipment( orgUnitIdsByComma,  equipmentType, equipmentTypeAttribute ,  searchText, searchBy );
    }

    //public Collection<Equipment> getEquipments( OrganisationUnit orgUnit, EquipmentType equipmentType, EquipmentTypeAttribute equipmentTypeAttribute, String searchText, int min, int max )
    public Collection<Equipment> getEquipments( String orgUnitIdsByComma, EquipmentType equipmentType, EquipmentTypeAttribute equipmentTypeAttribute, String searchText, String searchBy, int min, int max )
    {
        return equipmentStore.getEquipments( orgUnitIdsByComma, equipmentType, equipmentTypeAttribute, searchText, searchBy, min, max );
    }
    
    
    public Collection<Equipment> getEquipmentList( OrganisationUnit organisationUnit, EquipmentType equipmentType )
    {
        return equipmentStore.getEquipmentList( organisationUnit, equipmentType );
    }
    
   
    public Collection<OrganisationUnit> searchOrgUnitListByName( String searchText )
    {
        return equipmentStore.searchOrgUnitListByName( searchText );
    }
    
    public Collection<OrganisationUnit> searchOrgUnitListByCode( String searchText )
    {
        return equipmentStore.searchOrgUnitListByCode( searchText );
    }
    
    
    // for orgUnit list according to orGUnit Attribute values for paging purpose
    public int countOrgUnitByAttributeValue( Collection<Integer> orgunitIds, Attribute attribute, String searchText )
    {
        return equipmentStore.countOrgUnitByAttributeValue( orgunitIds, attribute, searchText );
    }
    
    // for orgUnit list according to orGUnit Attribute values for paging purpose
    public Collection<OrganisationUnit> searchOrgUnitByAttributeValue( Collection<Integer> orgunitIds, Attribute attribute, String searchText, Integer min, Integer max )
    {
        return equipmentStore.searchOrgUnitByAttributeValue( orgunitIds, attribute, searchText, min, max );
    }
    
}
