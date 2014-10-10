package org.hisp.dhis.coldchain.model;

import java.io.Serializable;

public class ModelAttributeValue implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -6551567526188061690L;
    
    private Model model;
    
    private ModelTypeAttribute modelTypeAttribute;
    
    private String value;
    
    private ModelTypeAttributeOption modelTypeAttributeOption;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
    public ModelAttributeValue( )
    {
        
    }
    public ModelAttributeValue( Model model, ModelTypeAttribute modelTypeAttribute, String value )
    {
        this.model = model;
        this.modelTypeAttribute = modelTypeAttribute;
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

        if ( !(o instanceof ModelAttributeValue) )
        {
            return false;
        }

        final ModelAttributeValue other = (ModelAttributeValue) o;

        return modelTypeAttribute.equals( other.getModelTypeAttribute() ) && model.equals( other.getModel() );

    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + modelTypeAttribute.hashCode();
        result = result * prime + model.hashCode();

        return result;
    }
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    
    public Model getModel()
    {
        return model;
    }
    public void setModel( Model model )
    {
        this.model = model;
    }
    public ModelTypeAttribute getModelTypeAttribute()
    {
        return modelTypeAttribute;
    }
    public void setModelTypeAttribute( ModelTypeAttribute modelTypeAttribute )
    {
        this.modelTypeAttribute = modelTypeAttribute;
    }
    public String getValue()
    {
        return value;
    }
    public void setValue( String value )
    {
        this.value = value;
    }
    public ModelTypeAttributeOption getModelTypeAttributeOption()
    {
        return modelTypeAttributeOption;
    }
    public void setModelTypeAttributeOption( ModelTypeAttributeOption modelTypeAttributeOption )
    {
        this.modelTypeAttributeOption = modelTypeAttributeOption;
    }
    
}
