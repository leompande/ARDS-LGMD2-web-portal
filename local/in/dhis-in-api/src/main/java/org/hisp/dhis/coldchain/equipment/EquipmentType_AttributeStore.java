package org.hisp.dhis.coldchain.equipment;

import java.util.Collection;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version EquipmentType_AttributeStore.java Jun 14, 2012 2:43:57 PM	
 */
//public interface EquipmentType_AttributeStore extends GenericStore<EquipmentType_Attribute>
public interface EquipmentType_AttributeStore
{
    String ID = EquipmentType_AttributeStore.class.getName();
    
    void addEquipmentType_Attribute( EquipmentType_Attribute equipmentType_Attribute );
    
    void updateEquipmentType_Attribute( EquipmentType_Attribute equipmentType_Attribute );

    void deleteEquipmentType_Attribute( EquipmentType_Attribute equipmentType_Attribute );
    
    Collection<EquipmentType_Attribute> getAllEquipmentTypeAttributes();
    
    EquipmentType_Attribute getEquipmentTypeAttribute( EquipmentType equipmentType, EquipmentTypeAttribute equipmentTypeAttribute );
    
    EquipmentType_Attribute getEquipmentTypeAttributeForDisplay( EquipmentType equipmentType, EquipmentTypeAttribute equipmentTypeAttribute, boolean display);
    
    Collection<EquipmentType_Attribute> getAllEquipmentTypeAttributeForDisplay( EquipmentType equipmentType, boolean display );

    Collection<EquipmentType_Attribute> getAllEquipmentTypeAttributesByEquipmentType( EquipmentType equipmentType );
    
    Collection<EquipmentTypeAttribute> getListEquipmentTypeAttribute( EquipmentType equipmentType );    
    
}
