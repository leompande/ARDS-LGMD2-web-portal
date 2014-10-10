package org.hisp.dhis.coldchain.model;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.common.BaseNameableObject;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version ModelTypeAttributeGroup.javaOct 9, 2012 2:12:38 PM	
 */
//public class ModelTypeAttributeGroup implements Serializable
public class ModelTypeAttributeGroup extends BaseNameableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -6551567526188061690L;
    
    private int id;
    
    private String name;
    
    private String description;
    
    private ModelType modelType;
    
    private List<ModelTypeAttribute> modelTypeAttributes = new ArrayList<ModelTypeAttribute>();
    
    private Integer sortOrder;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ModelTypeAttributeGroup()
    {
        
    }
    
    public ModelTypeAttributeGroup( String name )
    {
        this.name = name;
    }
    
    public ModelTypeAttributeGroup( String name, String description )
    {
        this.name = name;
        this.description = description;
    }
    
    public ModelTypeAttributeGroup( String name, String description , ModelType modelType )
    {
        this.name = name;
        this.description = description;
        this.modelType = modelType;
    }
    
    public ModelTypeAttributeGroup( String name, String description , ModelType modelType, Integer sortOrder )
    {
        this.name = name;
        this.description = description;
        this.modelType = modelType;
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

        if ( !(o instanceof ModelTypeAttributeGroup) )
        {
            return false;
        }

        final ModelTypeAttributeGroup other = (ModelTypeAttributeGroup) o;

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

    public ModelType getModelType()
    {
        return modelType;
    }

    public void setModelType( ModelType modelType )
    {
        this.modelType = modelType;
    }

    public List<ModelTypeAttribute> getModelTypeAttributes()
    {
        return modelTypeAttributes;
    }

    public void setModelTypeAttributes( List<ModelTypeAttribute> modelTypeAttributes )
    {
        this.modelTypeAttributes = modelTypeAttributes;
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
