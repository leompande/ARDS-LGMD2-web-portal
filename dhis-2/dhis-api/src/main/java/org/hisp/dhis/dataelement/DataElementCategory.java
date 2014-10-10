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

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.common.BaseDimensionalObject;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.common.annotation.Scanned;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.DimensionalView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.concept.Concept;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * A Category is a dimension of a data element. DataElements can have sets of
 * dimensions (known as CategoryCombos). An Example of a Category might be
 * "Sex". The Category could have two (or more) CategoryOptions such as "Male"
 * and "Female".
 *
 * @author Abyot Asalefew
 */
@JacksonXmlRootElement( localName = "category", namespace = DxfNamespaces.DXF_2_0)
public class DataElementCategory
    extends BaseDimensionalObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 6797241235048185629L;

    public static final String DEFAULT_NAME = "default";

    private Concept concept;
    
    private String dimensionType;

    @Scanned
    private List<DataElementCategoryOption> categoryOptions = new ArrayList<DataElementCategoryOption>();

    private boolean dataDimension;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DataElementCategory()
    {
    }

    public DataElementCategory( String name )
    {
        this.name = name;
    }

    public DataElementCategory( String name, List<DataElementCategoryOption> categoryOptions )
    {
        this.name = name;
        this.categoryOptions = categoryOptions;
    }

    public DataElementCategory( String name, Concept concept, List<DataElementCategoryOption> categoryOptions )
    {
        this.name = name;
        this.concept = concept;
        this.categoryOptions = categoryOptions;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public void addDataElementCategoryOption( DataElementCategoryOption dataElementCategoryOption )
    {
        categoryOptions.add( dataElementCategoryOption );
        dataElementCategoryOption.getCategories().add( this );
    }

    public void removeDataElementCategoryOption( DataElementCategoryOption dataElementCategoryOption )
    {
        categoryOptions.remove( dataElementCategoryOption );
        dataElementCategoryOption.getCategories().remove( this );
    }

    public void removeAllCategoryOptions()
    {
        for ( DataElementCategoryOption categoryOption : categoryOptions )
        {
            categoryOption.getCategories().remove( this );
        }

        categoryOptions.clear();
    }

    public DataElementCategoryOption getCategoryOption( DataElementCategoryOptionCombo categoryOptionCombo )
    {
        for ( DataElementCategoryOption categoryOption : categoryOptions )
        {
            if ( categoryOption.getCategoryOptionCombos().contains( categoryOptionCombo ) )
            {
                return categoryOption;
            }
        }

        return null;
    }
    
    public boolean isDefault()
    {
        return DEFAULT_NAME.equals( name );
    }

    // -------------------------------------------------------------------------
    // Dimensional object
    // -------------------------------------------------------------------------

    @Override
    @JsonProperty
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class, DimensionalView.class } )
    @JacksonXmlElementWrapper( localName = "items", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "item", namespace = DxfNamespaces.DXF_2_0 )
    public List<NameableObject> getItems()
    {
        return new ArrayList<NameableObject>( categoryOptions );
    }
    
    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    @Override
    public boolean isAutoGenerated()
    {
        return name != null && name.equals( DEFAULT_NAME );
    }

    // ------------------------------------------------------------------------
    // Getters and setters
    // ------------------------------------------------------------------------

    @Override
    public String getShortName()
    {
        if ( getName().length() <= 50 )
        {
            return getName();
        }
        else
        {
            return getName().substring( 0, 49 );
        }
    }

    @JsonProperty
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    @JsonInclude( JsonInclude.Include.NON_NULL )
    public Concept getConcept()
    {
        return concept;
    }

    public void setConcept( Concept concept )
    {
        this.concept = concept;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getDimensionType()
    {
        return dimensionType;
    }

    public void setDimensionType( String dimensionType )
    {
        this.dimensionType = dimensionType;
    }

    @JsonProperty
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlElementWrapper( localName = "categoryOptions", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "categoryOption", namespace = DxfNamespaces.DXF_2_0 )
    public List<DataElementCategoryOption> getCategoryOptions()
    {
        return categoryOptions;
    }

    public void setCategoryOptions( List<DataElementCategoryOption> categoryOptions )
    {
        this.categoryOptions = categoryOptions;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isDataDimension()
    {
        return dataDimension;
    }

    public void setDataDimension( boolean dataDimension )
    {
        this.dataDimension = dataDimension;
    }
    
    @Override
    public void mergeWith( IdentifiableObject other )
    {
        super.mergeWith( other );

        if ( other.getClass().isInstance( this ) )
        {
            DataElementCategory dataElementCategory = (DataElementCategory) other;

            concept = dataElementCategory.getConcept() == null ? concept : dataElementCategory.getConcept();

            removeAllCategoryOptions();

            for ( DataElementCategoryOption dataElementCategoryOption : dataElementCategory.getCategoryOptions() )
            {
                addDataElementCategoryOption( dataElementCategoryOption );
            }
        }
    }
}
