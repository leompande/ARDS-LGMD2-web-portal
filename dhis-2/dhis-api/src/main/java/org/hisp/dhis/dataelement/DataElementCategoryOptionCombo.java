package org.hisp.dhis.dataelement;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.BaseNameableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.annotation.Scanned;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Abyot Aselefew
 */
@JacksonXmlRootElement(localName = "categoryOptionCombo", namespace = DxfNamespaces.DXF_2_0)
public class DataElementCategoryOptionCombo
    extends BaseNameableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 7759083342982353468L;

    public static final String DEFAULT_NAME = "default";

    public static final String DEFAULT_TOSTRING = "(default)";

    /**
     * The category combo.
     */
    private DataElementCategoryCombo categoryCombo;

    /**
     * The category options.
     */
    @Scanned
    private Set<DataElementCategoryOption> categoryOptions = new HashSet<DataElementCategoryOption>();

    // -------------------------------------------------------------------------
    // Transient properties
    // -------------------------------------------------------------------------

    private transient String name;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DataElementCategoryOptionCombo()
    {
    }

    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;

        int result = 1;

        result = prime * result + ((categoryCombo == null) ? 0 : categoryCombo.hashCode());
        result = prime * result + ((categoryOptions == null) ? 0 : categoryOptions.hashCode());

        return result;
    }

    @Override
    public boolean equals( Object object )
    {
        if ( this == object )
        {
            return true;
        }

        if ( object == null )
        {
            return false;
        }

        if ( !(object instanceof DataElementCategoryOptionCombo) )
        {
            return false;
        }

        final DataElementCategoryOptionCombo other = (DataElementCategoryOptionCombo) object;

        if ( categoryCombo == null )
        {
            if ( other.categoryCombo != null )
            {
                return false;
            }
        }
        else if ( !categoryCombo.equals( other.categoryCombo ) )
        {
            return false;
        }

        if ( categoryOptions == null )
        {
            if ( other.categoryOptions != null )
            {
                return false;
            }
        }
        else if ( !categoryOptions.equals( other.categoryOptions ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder( "[" + categoryCombo + ", [" );

        Iterator<DataElementCategoryOption> iterator = categoryOptions.iterator();

        while ( iterator.hasNext() )
        {
            DataElementCategoryOption dataElementCategoryOption = iterator.next();

            if ( dataElementCategoryOption != null )
            {
                builder.append( dataElementCategoryOption.toString() );
            }

            if ( iterator.hasNext() )
            {
                builder.append( ", " );
            }
        }

        return builder.append( "]]" ).toString();
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public void addDataElementCategoryOption( DataElementCategoryOption dataElementCategoryOption )
    {
        categoryOptions.add( dataElementCategoryOption );
        dataElementCategoryOption.getCategoryOptionCombos().add( this );
    }

    public void removeDataElementCategoryOption( DataElementCategoryOption dataElementCategoryOption )
    {
        categoryOptions.remove( dataElementCategoryOption );
        dataElementCategoryOption.getCategoryOptionCombos().remove( this );
    }

    public void removeAllCategoryOptions()
    {
        categoryOptions.clear();
    }

    /**
     * Tests whether two objects compare on a name basis. The default equals
     * method becomes unusable in the case of detached objects in conjunction
     * with persistence frameworks that put proxys on associated objects and
     * collections, since it tests the class type which will differ between the
     * proxy and the raw type.
     *
     * @param object the object to test for equality.
     * @return true if objects are equal, false otherwise.
     */
    public boolean equalsOnName( DataElementCategoryOptionCombo object )
    {
        if ( this == object )
        {
            return true;
        }

        if ( object == null || object.getCategoryCombo() == null || object.getCategoryOptions() == null )
        {
            return false;
        }

        if ( !categoryCombo.getName().equals( object.getCategoryCombo().getName() ) )
        {
            return false;
        }

        if ( categoryOptions.size() != object.getCategoryOptions().size() )
        {
            return false;
        }

        final Set<String> names1 = new HashSet<String>();
        final Set<String> names2 = new HashSet<String>();

        for ( DataElementCategoryOption option : categoryOptions )
        {
            names1.add( option.getName() );
        }

        for ( DataElementCategoryOption option : object.getCategoryOptions() )
        {
            names2.add( option.getName() );
        }

        return names1.equals( names2 );
    }

    /**
     * Tests if this object equals to an object in the given Collection on a
     * name basis.
     *
     * @param categoryOptionCombos the Collection.
     * @return true if the Collection contains this object, false otherwise.
     */
    public DataElementCategoryOptionCombo get( Collection<DataElementCategoryOptionCombo> categoryOptionCombos )
    {
        for ( DataElementCategoryOptionCombo combo : categoryOptionCombos )
        {
            if ( combo.equalsOnName( this ) )
            {
                return combo;
            }
        }

        return null;
    }

    public boolean isDefault()
    {
        return categoryCombo != null && categoryCombo.getName().equals( DEFAULT_NAME );
    }

    /**
     * Creates a mapping between the category option combo identifier and name
     * for the given collection of elements.
     */
    public static Map<Integer, String> getCategoryOptionComboMap( Collection<DataElementCategoryOptionCombo> categoryOptionCombos )
    {
        Map<Integer, String> map = new HashMap<Integer, String>();

        for ( DataElementCategoryOptionCombo coc : categoryOptionCombos )
        {
            map.put( coc.getId(), coc.getName() );
        }

        return map;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @Override
    public boolean haveUniqueNames()
    {
        return false;
    }

    @Override
    public boolean isAutoGenerated()
    {
        return name != null && name.equals( DEFAULT_NAME );
    }

    @Override
    public String getName()
    {
        if ( name != null )
        {
            return name;
        }

        StringBuilder name = new StringBuilder();

        if ( categoryOptions != null && categoryOptions.size() > 0 )
        {
            name.append( "(" );

            Iterator<DataElementCategoryOption> iterator = categoryOptions.iterator();

            if ( iterator.hasNext() )
            {
                name.append( iterator.next().getDisplayName() );
            }

            while ( iterator.hasNext() )
            {
                DataElementCategoryOption categoryOption = iterator.next();

                if ( categoryOption != null )
                {
                    name.append( ", " ).append( categoryOption.getDisplayName() );
                }
            }

            name.append( ")" );
        }

        return name.toString();
    }

    @Override
    public void setName( String name )
    {
        this.name = name;
    }

    @Override
    @JsonIgnore
    public String getShortName()
    {
        return getName();
    }

    @Override
    public void setShortName( String shortName )
    {
        // throw new UnsupportedOperationException( "Cannot set shortName on DataElementCategoryOptionCombo: " + shortName );
    }

    @JsonProperty
    @JsonSerialize(as = BaseIdentifiableObject.class)
    @JsonView({ DetailedView.class, ExportView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public DataElementCategoryCombo getCategoryCombo()
    {
        return categoryCombo;
    }

    public void setCategoryCombo( DataElementCategoryCombo categoryCombo )
    {
        this.categoryCombo = categoryCombo;
    }

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JsonView({ DetailedView.class, ExportView.class })
    @JacksonXmlElementWrapper(localName = "categoryOptions", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "categoryOption", namespace = DxfNamespaces.DXF_2_0)
    public Set<DataElementCategoryOption> getCategoryOptions()
    {
        return categoryOptions;
    }

    public void setCategoryOptions( Set<DataElementCategoryOption> categoryOptions )
    {
        this.categoryOptions = categoryOptions;
    }

    @Override
    public void mergeWith( IdentifiableObject other )
    {
        super.mergeWith( other );

        if ( other.getClass().isInstance( this ) )
        {
            DataElementCategoryOptionCombo dataElementCategoryOptionCombo = (DataElementCategoryOptionCombo) other;

            categoryCombo = dataElementCategoryOptionCombo.getCategoryCombo() == null ? categoryCombo : dataElementCategoryOptionCombo.getCategoryCombo();

            removeAllCategoryOptions();

            for ( DataElementCategoryOption dataElementCategoryOption : dataElementCategoryOptionCombo.getCategoryOptions() )
            {
                addDataElementCategoryOption( dataElementCategoryOption );
            }
        }
    }
}
