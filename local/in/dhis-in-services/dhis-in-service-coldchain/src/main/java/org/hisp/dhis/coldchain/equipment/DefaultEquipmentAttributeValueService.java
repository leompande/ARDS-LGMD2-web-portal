package org.hisp.dhis.coldchain.equipment;

import java.util.Collection;
import java.util.List;

import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValue;
import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValueService;
import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValueStore;
import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.springframework.transaction.annotation.Transactional;
@Transactional
public class DefaultEquipmentAttributeValueService implements EquipmentAttributeValueService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private EquipmentAttributeValueStore equipmentAttributeValueStore;
    
    public void setEquipmentAttributeValueStore( EquipmentAttributeValueStore equipmentAttributeValueStore )
    {
        this.equipmentAttributeValueStore = equipmentAttributeValueStore;
    }

    // -------------------------------------------------------------------------
    // EquipmentDetails
    // -------------------------------------------------------------------------
    
    @Override
    public void addEquipmentAttributeValue( EquipmentAttributeValue equipmentAttributeValue )
    {
        equipmentAttributeValueStore.addEquipmentAttributeValue( equipmentAttributeValue );
    }
    @Override
    public void deleteEquipmentAttributeValue( EquipmentAttributeValue equipmentAttributeValue )
    {
        equipmentAttributeValueStore.deleteEquipmentAttributeValue( equipmentAttributeValue );
    }
    @Override
    public Collection<EquipmentAttributeValue> getAllEquipmentAttributeValues()
    {
        return equipmentAttributeValueStore.getAllEquipmentAttributeValues();
    }
    @Override
    public void updateEquipmentAttributeValue( EquipmentAttributeValue equipmentAttributeValue )
    {
        equipmentAttributeValueStore.updateEquipmentAttributeValue( equipmentAttributeValue );
    }
    
    public Collection<EquipmentAttributeValue> getEquipmentAttributeValues( Equipment equipment)
    {
        return equipmentAttributeValueStore.getEquipmentAttributeValues( equipment );
    }

    public EquipmentAttributeValue getEquipmentAttributeValue( Equipment equipment, EquipmentTypeAttribute equipmentTypeAttribute )
    {
        return equipmentAttributeValueStore.getEquipmentAttributeValue( equipment, equipmentTypeAttribute );
    }
 /*   
    public Map<String, String> inventryTypeAttributeAndValue( Equipment equipment, List<EquipmentTypeAttribute> equipmentTypeAttributeList )
    {
        String equipmentTypeAttributeName = "";
        
        String equipmentTypeAttributeValue = "";
        
        String equipmentTypeAttributeNameValue = "";
        
        Map<String, String> inventryTypeAttributeAndValueMap = new HashMap<String, String>();
        
        for( EquipmentTypeAttribute equipmentTypeAttribute : equipmentTypeAttributeList )
        {
            EquipmentAttributeValue equipmentDetail = getEquipment( equipment, equipmentTypeAttribute );
            if( equipmentDetail != null && equipmentDetail.getValue() != null )
            {
                
                if ( EquipmentTypeAttribute.TYPE_COMBO.equalsIgnoreCase( equipmentDetail.getEquipmentTypeAttribute().getValueType() ) )
                {
                    inventryTypeAttributeAndValueMap.put( equipmentTypeAttribute.getName(), equipmentDetail.getEquipmentTypeAttributeOption().getName() );
                    
                    equipmentTypeAttributeName += "--" + equipmentTypeAttribute.getName();
                    equipmentTypeAttributeValue += "--" + equipmentDetail.getEquipmentTypeAttributeOption().getName();
                }
                else
                {
                    inventryTypeAttributeAndValueMap.put( equipmentTypeAttribute.getName(), equipmentDetail.getValue() );
                    
                    equipmentTypeAttributeName += "--" + equipmentTypeAttribute.getName();
                    equipmentTypeAttributeValue += "--" + equipmentDetail.getValue();
                }
                
                equipmentTypeAttributeNameValue = equipmentTypeAttributeName + "#@" + equipmentTypeAttributeValue;
            }
        }
        
        
        
        return inventryTypeAttributeAndValueMap;
    }
 */
    public String inventryTypeAttributeAndValue( Equipment equipment, List<EquipmentTypeAttribute> equipmentTypeAttributeList )
    {
        String equipmentTypeAttributeName = "";
        
        String equipmentTypeAttributeValue = "";
        
        String equipmentTypeAttributeNameValue = "";
        
        //Map<String, String> inventryTypeAttributeAndValueMap = new HashMap<String, String>();
        
        for( EquipmentTypeAttribute equipmentTypeAttribute : equipmentTypeAttributeList )
        {
            EquipmentAttributeValue equipmentAttributeValueDetail = getEquipmentAttributeValue( equipment, equipmentTypeAttribute );
            if( equipmentAttributeValueDetail != null && equipmentAttributeValueDetail.getValue() != null )
            {
                
                if ( EquipmentTypeAttribute.TYPE_COMBO.equalsIgnoreCase( equipmentAttributeValueDetail.getEquipmentTypeAttribute().getValueType() ) )
                {
                    //inventryTypeAttributeAndValueMap.put( equipmentTypeAttribute.getName(), equipmentDetail.getEquipmentTypeAttributeOption().getName() );
                    
                    equipmentTypeAttributeName += "--" + equipmentTypeAttribute.getName();
                    equipmentTypeAttributeValue += "--" + equipmentAttributeValueDetail.getEquipmentTypeAttributeOption().getName();
                }
                else
                {
                    //inventryTypeAttributeAndValueMap.put( equipmentTypeAttribute.getName(), equipmentDetail.getValue() );
                    
                    equipmentTypeAttributeName += "--" + equipmentTypeAttribute.getName();
                    equipmentTypeAttributeValue += "--" + equipmentAttributeValueDetail.getValue();
                }
                
                equipmentTypeAttributeNameValue = equipmentTypeAttributeName.substring( 2 ) + "#@#" + equipmentTypeAttributeValue.substring( 2 );
            }
        }
        
        //System.out.println( equipmentTypeAttributeName + "#@#" + equipmentTypeAttributeValue );
        
        return equipmentTypeAttributeNameValue;
    }    
    
}
