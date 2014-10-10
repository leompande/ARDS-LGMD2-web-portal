package org.hisp.dhis.coldchain.equipment;

import java.io.Serializable;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version EquipmentType_Attribute.java Jun 14, 2012 1:27:37 PM	
 */
public class EquipmentType_Attribute implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -5670110591005778814L;
    
    
    /**
     * Part of composite key
     */
    private EquipmentType equipmentType;

    /**
     * Part of composite key
     */
    private EquipmentTypeAttribute equipmentTypeAttribute;
    
    private boolean display = false;
    
    private Integer sortOrder;
    
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    //Default Constructors
    
    public EquipmentType_Attribute()
    {
        
    }
    
    
    public EquipmentType_Attribute( EquipmentType equipmentType, EquipmentTypeAttribute equipmentTypeAttribute, boolean display )
    {
        this.equipmentType = equipmentType;
        this.equipmentTypeAttribute = equipmentTypeAttribute;
        this.display = display;
    }

    public EquipmentType_Attribute( EquipmentType equipmentType, EquipmentTypeAttribute equipmentTypeAttribute, boolean display, Integer sortOrder)
    {
        this.equipmentType = equipmentType;
        this.equipmentTypeAttribute = equipmentTypeAttribute;
        this.display = display;
        this.sortOrder = sortOrder;
    }

    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return equipmentType.hashCode() + equipmentTypeAttribute.hashCode();
    }

    @Override
    public boolean equals( Object object )
    {

        if ( object == null )
        {
            return false;
        }

        if ( getClass() != object.getClass() )
        {
            return false;
        }

        final EquipmentType_Attribute other = ( EquipmentType_Attribute ) object;

        return equipmentTypeAttribute.getId() == other.getEquipmentTypeAttribute().getId()
            && equipmentType.getId() == other.getEquipmentType().getId();
    }

    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public EquipmentType getEquipmentType()
    {
        return equipmentType;
    }

    public void setEquipmentType( EquipmentType equipmentType )
    {
        this.equipmentType = equipmentType;
    }

    public EquipmentTypeAttribute getEquipmentTypeAttribute()
    {
        return equipmentTypeAttribute;
    }

    public void setEquipmentTypeAttribute( EquipmentTypeAttribute equipmentTypeAttribute )
    {
        this.equipmentTypeAttribute = equipmentTypeAttribute;
    }

    public boolean isDisplay()
    {
        return display;
    }

    public void setDisplay( boolean display )
    {
        this.display = display;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder( Integer sortOrder )
    {
        this.sortOrder = sortOrder;
    }

}
