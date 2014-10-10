package org.hisp.dhis.coldchain.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.common.BaseNameableObject;
import org.hisp.dhis.dataentryform.DataEntryForm;

//public class ModelType implements Serializable
public class ModelType extends BaseNameableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -6551567526188061690L;
    
    public static String PREFIX_MODEL_TYPE = "Vaccines";
    
    private int id;
    
    private String name;
    
    private String description;
    
    private List<ModelTypeAttribute> modelTypeAttributes = new ArrayList<ModelTypeAttribute>();
	
    private DataEntryForm dataEntryForm;
    
    private String modelTypeImage;
    
    
    /**
     * The ModelTypeAttributeGroup associated with the ModelType.
     */
    private Set<ModelTypeAttributeGroup> modelTypeAttributeGroups = new HashSet<ModelTypeAttributeGroup>();
    

    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------
    public ModelType()
    {

    }
    public ModelType( String name )
    {
        this.name = name;
    }
    
    public ModelType( String name, String description )
    {
        this.name = name;
        this.description = description;
    }
    
    // -------------------------------------------------------------------------
    // hashCode and equals
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

        if ( !(o instanceof ModelType) )
        {
            return false;
        }

        final ModelType other = (ModelType) o;

        return name.equals( other.getName() );
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

    public DataEntryForm getDataEntryForm()
    {
        return dataEntryForm;
    }
    public void setDataEntryForm( DataEntryForm dataEntryForm )
    {
        this.dataEntryForm = dataEntryForm;
    }
    public List<ModelTypeAttribute> getModelTypeAttributes()
    {
        return modelTypeAttributes;
    }
    public void setModelTypeAttributes( List<ModelTypeAttribute> modelTypeAttributes )
    {
        this.modelTypeAttributes = modelTypeAttributes;
    }
    
    public String getModelTypeImage()
    {
        return modelTypeImage;
    }
    public void setModelTypeImage( String modelTypeImage )
    {
        this.modelTypeImage = modelTypeImage;
    }
    
    public Set<ModelTypeAttributeGroup> getModelTypeAttributeGroups()
    {
        return modelTypeAttributeGroups;
    }
    public void setModelTypeAttributeGroups( Set<ModelTypeAttributeGroup> modelTypeAttributeGroups )
    {
        this.modelTypeAttributeGroups = modelTypeAttributeGroups;
    }
    
    
}
