package org.hisp.dhis.coldchain.model;

import java.io.Serializable;

public class ModelTypeAttributeOption implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 9052641474847384010L;
    
    private int id;
    
    private String name;

    private ModelTypeAttribute modelTypeAttribute;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ModelTypeAttributeOption()
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

        if ( !(o instanceof ModelTypeAttributeOption) )
        {
            return false;
        }

        final ModelTypeAttributeOption other = (ModelTypeAttributeOption) o;

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

    public ModelTypeAttribute getModelTypeAttribute()
    {
        return modelTypeAttribute;
    }

    public void setModelTypeAttribute( ModelTypeAttribute modelTypeAttribute )
    {
        this.modelTypeAttribute = modelTypeAttribute;
    }

    
    
}
