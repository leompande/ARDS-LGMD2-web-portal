package org.hisp.dhis.coldchain.equipment;

import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.common.BaseNameableObject;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataset.DataSet;

//public class EquipmentType implements Serializable
public class EquipmentType extends BaseNameableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -6551567526188061690L;
    
    private int id;
    
    private String name;
    
    private String description;
    
    private boolean tracking;
    
    private ModelType modelType;
    
   // private Set<EquipmentTypeAttribute> equipmentTypeAttributes;
    
    //private List<EquipmentTypeAttribute> equipmentTypeAttributes = new ArrayList<EquipmentTypeAttribute>();
    
    //private List<Boolean> display = new ArrayList<Boolean>();
    
    private Set<EquipmentType_Attribute> equipmentType_Attributes;
    
    private Set<DataSet> dataSets = new HashSet<DataSet>();
    
    private DataEntryForm dataEntryForm;
    
    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------

    public EquipmentType()
    {
        
    }
    public EquipmentType( String name, boolean tracking )
    {
        this.name = name;
        this.tracking = tracking;
    }
    
    public EquipmentType( String name, String description, boolean tracking, ModelType modelType )
    {
        this.name = name;
        this.description = description;
        this.tracking = tracking;
        this.modelType = modelType;
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

        if ( !(o instanceof EquipmentType) )
        {
            return false;
        }

        final EquipmentType other = (EquipmentType) o;

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
    public boolean isTracking()
    {
        return tracking;
    }
    public void setTracking( boolean tracking )
    {
        this.tracking = tracking;
    }
    public ModelType getModelType()
    {
        return modelType;
    }
    public void setModelType( ModelType modelType )
    {
        this.modelType = modelType;
    }
    /*
    public Set<EquipmentTypeAttribute> getEquipmentTypeAttributes()
    {
        return equipmentTypeAttributes;
    }
    public void setEquipmentTypeAttributes( Set<EquipmentTypeAttribute> equipmentTypeAttributes )
    {
        this.equipmentTypeAttributes = equipmentTypeAttributes;
    }
    */
    /*
    public List<EquipmentTypeAttribute> getEquipmentTypeAttributes()
    {
        return equipmentTypeAttributes;
    }
    public void setEquipmentTypeAttributes( List<EquipmentTypeAttribute> equipmentTypeAttributes )
    {
        this.equipmentTypeAttributes = equipmentTypeAttributes;
    }
    */
    public DataEntryForm getDataEntryForm()
    {
        return dataEntryForm;
    }
    public void setDataEntryForm( DataEntryForm dataEntryForm )
    {
        this.dataEntryForm = dataEntryForm;
    }
    
    public Set<DataSet> getDataSets()
    {
        return dataSets;
    }
    public void setDataSets( Set<DataSet> dataSets )
    {
        this.dataSets = dataSets;
    }
    /*
    public boolean isDisplay()
    {
        return display;
    }
    public void setDisplay( boolean display )
    {
        this.display = display;
    }
    */
    
    public Set<EquipmentType_Attribute> getEquipmentType_Attributes()
    {
        return equipmentType_Attributes;
    }
    public void setEquipmentType_Attributes( Set<EquipmentType_Attribute> equipmentType_Attributes )
    {
        this.equipmentType_Attributes = equipmentType_Attributes;
    }
    
}
