package org.hisp.dhis.coldchain.equipment;

import java.util.Collection;
import java.util.Map;

import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version EquipmentType_AttributeService.java Jun 14, 2012 2:30:47 PM	
 */
public interface EquipmentType_AttributeService
{
    String ID = EquipmentType_AttributeService.class.getName();
    
    void addEquipmentType_Attribute( EquipmentType_Attribute equipmentType_Attribute );
    
    void updateEquipmentType_Attribute( EquipmentType_Attribute equipmentType_Attribute );

    void deleteEquipmentType_Attribute( EquipmentType_Attribute equipmentType_Attribute );
    
    EquipmentType_Attribute getEquipmentTypeAttribute( EquipmentType equipmentType, EquipmentTypeAttribute equipmentTypeAttribute );

    Collection<EquipmentType_Attribute> getAllEquipmentTypeAttributes();

    Collection<EquipmentType_Attribute> getAllEquipmentTypeAttributesByEquipmentType( EquipmentType equipmentType );
    
    Collection<EquipmentTypeAttribute> getListEquipmentTypeAttribute( EquipmentType equipmentType );
    
    EquipmentType_Attribute getEquipmentTypeAttributeForDisplay( EquipmentType equipmentType, EquipmentTypeAttribute equipmentTypeAttribute, boolean display);
    
    Collection<EquipmentType_Attribute> getAllEquipmentTypeAttributeForDisplay( EquipmentType equipmentType, boolean display );
    
    Map<String, String> getOrgUnitAttributeDataValue( String orgUnitIdsByComma, String orgUnitAttribIdsByComma );
    
    Collection<OrganisationUnit> searchOrgUnitByAttributeValue( String orgUnitIdsByComma, Attribute attribute, String searchText );
    
    Map<Integer, String> getEquipmentCountByOrgUnitList( String orgUnitIdsByComma );
}
