package org.hisp.dhis.coldchain.equipment;

import java.io.Serializable;

public class EquipmentTypeAttributeOption implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    
    private static final long serialVersionUID = -6551567526188061690L;
    
    private int id;
    
    private String name;

    private EquipmentTypeAttribute equipmentTypeAttribute;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public EquipmentTypeAttributeOption()
    {
        
    }
    
    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------
    
    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

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

        if ( !(o instanceof EquipmentTypeAttributeOption) )
        {
            return false;
        }

        final EquipmentTypeAttributeOption other = (EquipmentTypeAttributeOption) o;

        return name.equals( other.getName() );
    }

    @Override
    public String toString()
    {
        return "[" + name + "]";
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    
    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public EquipmentTypeAttribute getEquipmentTypeAttribute()
    {
        return equipmentTypeAttribute;
    }

    public void setEquipmentTypeAttribute( EquipmentTypeAttribute equipmentTypeAttribute )
    {
        this.equipmentTypeAttribute = equipmentTypeAttribute;
    }

}
