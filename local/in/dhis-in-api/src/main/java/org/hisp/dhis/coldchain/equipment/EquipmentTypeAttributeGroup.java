package org.hisp.dhis.coldchain.equipment;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.common.BaseNameableObject;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version EquipmentTypeAttributeGroup.javaMar 5, 2013 11:47:03 AM	
 */

public class EquipmentTypeAttributeGroup extends BaseNameableObject
{ 
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -6551567526188061690L;
    
    private int id;
    
    private String name;
    
    private String description;
    
    private EquipmentType equipmentType;
    
    private List<EquipmentType_Attribute> equipmentType_Attributes = new ArrayList<EquipmentType_Attribute>();
    
    private Integer sortOrder;

    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public EquipmentTypeAttributeGroup()
    {
        
    }
    
    public EquipmentTypeAttributeGroup( String name )
    {
        this.name = name;
    }
    
    public EquipmentTypeAttributeGroup( String name, String description )
    {
        this.name = name;
        this.description = description;
    }
    
    public EquipmentTypeAttributeGroup( String name, String description , EquipmentType equipmentType )
    {
        this.name = name;
        this.description = description;
        this.equipmentType = equipmentType;
    }
    
    public EquipmentTypeAttributeGroup( String name, String description , EquipmentType equipmentType, Integer sortOrder )
    {
        this.name = name;
        this.description = description;
        this.equipmentType = equipmentType;
        this.sortOrder = sortOrder;
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

        if ( !(o instanceof EquipmentTypeAttributeGroup) )
        {
            return false;
        }

        final EquipmentTypeAttributeGroup other = (EquipmentTypeAttributeGroup) o;

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

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public EquipmentType getEquipmentType()
    {
        return equipmentType;
    }

    public void setEquipmentType( EquipmentType equipmentType )
    {
        this.equipmentType = equipmentType;
    }

    public List<EquipmentType_Attribute> getEquipmentType_Attributes()
    {
        return equipmentType_Attributes;
    }

    public void setEquipmentType_Attributes( List<EquipmentType_Attribute> equipmentType_Attributes )
    {
        this.equipmentType_Attributes = equipmentType_Attributes;
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
