package org.hisp.dhis.coldchain.model;

import org.hisp.dhis.common.BaseNameableObject;
//public class Model implements Serializable
public class Model extends BaseNameableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -6551567526188061690L;
    
    public static final String DEFAULT_CCEMFOLDER = "images";
    public static String PREFIX_MODEL_NAME = "modelname";
    
    public static final String NAME_OWNERSHIP_GROUP_SET = "Ownership";
    public static final String NAME_FACILITY_TYPE = "Facility Type";
    //public static final String NAME_FACILITY_TYPE = "Type";
    
    
    private int id;
    
    private String name;
    
    private String description;
    
    private String modelImage;
    
    //private Blob image;

    private ModelType modelType;
    
   // private File image;
    
    //private byte[] image;
    

    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------
    public Model()
    {
        
    }

    public Model( String name, ModelType modelType )
    {
        this.name = name;
        this.modelType = modelType;
    }
    
    public Model( String name, String description, ModelType modelType )
    {
        this.name = name;
        this.description = description;
        this.modelType = modelType;
    }
    
    /*
    public Model( Blob image ) 
    {
        this.image = image;
    }
    
   
    public Model( byte[] image ) 
    {
        this.image = image;
    }
    */

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

        if ( !(o instanceof Model) )
        {
            return false;
        }

        final Model other = (Model) o;

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

    public ModelType getModelType()
    {
        return modelType;
    }

    public void setModelType( ModelType modelType )
    {
        this.modelType = modelType;
    }
    
    public String getModelImage()
    {
        return modelImage;
    }

    public void setModelImage( String modelImage )
    {
        this.modelImage = modelImage;
    }
    /*
    public File getImage()
    {
        return image;
    }

    public void setImage( File image )
    {
        this.image = image;
    }
    
    public void setBinaryStream( Blob image )
    {
        this.image = image;
    }
    
    public Blob getBinaryStream()
    {
        return image;
    }
    
    public Blob getImage()
    {
        return image;
    }

    public void setImage( Blob image )
    {
        this.image = image;
    }
    
    
    public byte[] getImage()
    {
        return image;
    }

    public void setImage( byte[] image )
    {
        this.image = image;
    }
    */

    
    
    
}
