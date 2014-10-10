package org.hisp.dhis.coldchain.equipment;

import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.BaseNameableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.option.OptionSet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

//public class EquipmentTypeAttribute implements Serializable

@JacksonXmlRootElement( localName = "equipmentTypeAttribute", namespace = DxfNamespaces.DXF_2_0 )
public class EquipmentTypeAttribute extends BaseNameableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -6551567526188061690L;
    
    public static final String TYPE_DATE = "DATE";

    public static final String TYPE_STRING = "TEXT";

    public static final String TYPE_INT = "NUMBER";

    public static final String TYPE_BOOL = "YES/NO";

    public static final String TYPE_COMBO = "COMBO";

    public static final String TYPE_MODEL = "MODEL";
    
    //private int id;
    
    //private String name;
    
    //private String description;

    private String valueType;
    
    private boolean mandatory;
    
    /*
     * True if this EquipmentTypeAttribute is for display in list
     */
    //private boolean display = false;
    
    private Integer noChars;

    private Set<EquipmentTypeAttributeOption> attributeOptions;
    
    private OptionSet optionSet;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public EquipmentTypeAttribute()
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

        if ( !(o instanceof EquipmentTypeAttribute) )
        {
            return false;
        }

        final EquipmentTypeAttribute other = (EquipmentTypeAttribute) o;

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
    
    /*
    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }
*/
    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getValueType()
    {
        return valueType;
    }

    public void setValueType( String valueType )
    {
        this.valueType = valueType;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isMandatory()
    {
        return mandatory;
    }

    public void setMandatory( boolean mandatory )
    {
        this.mandatory = mandatory;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Integer getNoChars()
    {
        return noChars;
    }

    public void setNoChars( Integer noChars )
    {
        this.noChars = noChars;
    }

    public Set<EquipmentTypeAttributeOption> getAttributeOptions()
    {
        return attributeOptions;
    }

    public void setAttributeOptions( Set<EquipmentTypeAttributeOption> attributeOptions )
    {
        this.attributeOptions = attributeOptions;
    }
    
    public void addAttributeOptions( EquipmentTypeAttributeOption option )
    {
        if ( attributeOptions == null )
            attributeOptions = new HashSet<EquipmentTypeAttributeOption>();
        attributeOptions.add( option );
    }
    
    public OptionSet getOptionSet() 
	{
		return optionSet;
	}

	public void setOptionSet(OptionSet optionSet) 
	{
		this.optionSet = optionSet;
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
    
    /*
    public void mergeWith(IdentifiableObject other )
    {
    	
    	super.mergeWith( other );
    	if ( other.getClass().isInstance( this ) )
        {
    		EquipmentTypeAttribute equipmentTypeAttribute= (EquipmentTypeAttribute) other;
    		
    		id = equipmentTypeAttribute.getId();
    		mandatory = equipmentTypeAttribute.isMandatory();
    		noChars = equipmentTypeAttribute.getNoChars();
    		valueType = equipmentTypeAttribute.getValueType() == null ? valueType : equipmentTypeAttribute.getValueType();
    		name =equipmentTypeAttribute.getName() == null ? name : equipmentTypeAttribute.getName();
    		description = equipmentTypeAttribute.getDescription() == null ? description : equipmentTypeAttribute.getDescription();
    		
    		attributeOptions.clear();
    		attributeOptions.addAll(equipmentTypeAttribute.getAttributeOptions());
    		
        }
   }
   */
}
