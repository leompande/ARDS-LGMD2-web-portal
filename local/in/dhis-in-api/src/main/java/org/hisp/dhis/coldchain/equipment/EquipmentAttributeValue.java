package org.hisp.dhis.coldchain.equipment;

import java.io.Serializable;

public class EquipmentAttributeValue implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -6551567526188061690L;
    
    public static String PREFIX_MODEL_NAME = "modelname";
    
    public static String PREFIX_ORGANISATIONUNIT_NAME = "orgunitname";
    
    public static String PREFIX_ORGANISATIONUNIT_CODE = "code";
    
    public static final String HEALTHFACILITY = "Health Facility";
    
    private Equipment equipment;
    
    private EquipmentTypeAttribute equipmentTypeAttribute;
    
    private String value;
    
    private EquipmentTypeAttributeOption equipmentTypeAttributeOption;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public EquipmentAttributeValue()
    {
        
    }
    public EquipmentAttributeValue( Equipment equipment, EquipmentTypeAttribute equipmentTypeAttribute, String value )
    {
        this.equipment = equipment;
        this.equipmentTypeAttribute = equipmentTypeAttribute;
        this.value = value;
    }
    
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------
    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof EquipmentAttributeValue) )
        {
            return false;
        }

        final EquipmentAttributeValue other = (EquipmentAttributeValue) o;

        return equipment.equals( other.getEquipment() ) && equipmentTypeAttribute.equals( other.getEquipmentTypeAttribute() );

    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + equipment.hashCode();
        result = result * prime + equipmentTypeAttribute.hashCode();

        return result;
    }
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public Equipment getEquipment()
    {
        return equipment;
    }
    public void setEquipment( Equipment equipment )
    {
        this.equipment = equipment;
    }
    public EquipmentTypeAttribute getEquipmentTypeAttribute()
    {
        return equipmentTypeAttribute;
    }
    public void setEquipmentTypeAttribute( EquipmentTypeAttribute equipmentTypeAttribute )
    {
        this.equipmentTypeAttribute = equipmentTypeAttribute;
    }
    public String getValue()
    {
        return value;
    }
    public void setValue( String value )
    {
        this.value = value;
    }
    public EquipmentTypeAttributeOption getEquipmentTypeAttributeOption()
    {
        return equipmentTypeAttributeOption;
    }
    public void setEquipmentTypeAttributeOption( EquipmentTypeAttributeOption equipmentTypeAttributeOption )
    {
        this.equipmentTypeAttributeOption = equipmentTypeAttributeOption;
    }
}
