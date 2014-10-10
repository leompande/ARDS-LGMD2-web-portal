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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.CombinationGenerator;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.annotation.Scanned;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Abyot Aselefew
 */
@JacksonXmlRootElement(localName = "categoryCombo", namespace = DxfNamespaces.DXF_2_0)
public class DataElementCategoryCombo
    extends BaseIdentifiableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1549406078091077760L;

    public static final String DEFAULT_CATEGORY_COMBO_NAME = "default";
    
    public static final String DIMENSION_TYPE_DISAGGREGATION = "disaggregation";
    public static final String DIMENSION_TYPE_ATTTRIBUTE = "attribute";

    /**
     * A set with categories.
     */
    @Scanned
    private List<DataElementCategory> categories = new ArrayList<DataElementCategory>();

    /**
     * A set of category option combos. Use getSortedOptionCombos() to get a
     * sorted list of category option combos.
     */
    private Set<DataElementCategoryOptionCombo> optionCombos = new HashSet<DataElementCategoryOptionCombo>();

    private String dimensionType;

    private boolean skipTotal;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DataElementCategoryCombo()
    {
    }

    public DataElementCategoryCombo( String name )
    {
        this.name = name;
    }

    public DataElementCategoryCombo( String name, List<DataElementCategory> categories )
    {
        this.name = name;
        this.categories = categories;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public boolean isDefault()
    {
        return name.equals( DEFAULT_CATEGORY_COMBO_NAME );
    }

    public List<DataElementCategoryOption> getCategoryOptions()
    {
        final List<DataElementCategoryOption> categoryOptions = new ArrayList<DataElementCategoryOption>();

        for ( DataElementCategory category : categories )
        {
            categoryOptions.addAll( category.getCategoryOptions() );
        }

        return categoryOptions;
    }

    public boolean doTotal()
    {
        return optionCombos != null && optionCombos.size() > 1 && !skipTotal;
    }

    public boolean doSubTotals()
    {
        return categories != null && categories.size() > 1;
    }

    public DataElementCategoryOption[][] getCategoryOptionsAsArray()
    {
        DataElementCategoryOption[][] arrays = new DataElementCategoryOption[categories.size()][];

        int i = 0;

        for ( DataElementCategory category : categories )
        {
            arrays[i++] = new ArrayList<DataElementCategoryOption>(
                category.getCategoryOptions() ).toArray( new DataElementCategoryOption[0] );
        }

        return arrays;
    }

    public List<DataElementCategoryOptionCombo> generateOptionCombosList()
    {
        List<DataElementCategoryOptionCombo> list = new ArrayList<DataElementCategoryOptionCombo>();

        CombinationGenerator<DataElementCategoryOption> generator =
            new CombinationGenerator<DataElementCategoryOption>( getCategoryOptionsAsArray() );

        while ( generator.hasNext() )
        {
            DataElementCategoryOptionCombo optionCombo = new DataElementCategoryOptionCombo();
            optionCombo.setCategoryOptions( new HashSet<DataElementCategoryOption>( generator.getNext() ) );
            optionCombo.setCategoryCombo( this );
            list.add( optionCombo );
        }

        return list;
    }

    public List<DataElementCategoryOptionCombo> getSortedOptionCombos()
    {
        List<DataElementCategoryOptionCombo> list = new ArrayList<DataElementCategoryOptionCombo>();

        CombinationGenerator<DataElementCategoryOption> generator =
            new CombinationGenerator<DataElementCategoryOption>( getCategoryOptionsAsArray() );

        sortLoop: while ( generator.hasNext() )
        {
            List<DataElementCategoryOption> categoryOptions = generator.getNext();

            Set<DataElementCategoryOption> categoryOptionSet = new HashSet<DataElementCategoryOption>( categoryOptions );

            for ( DataElementCategoryOptionCombo optionCombo : optionCombos )
            {
                if ( optionCombo.getCategoryOptions() != null && optionCombo.getCategoryOptions().equals( categoryOptionSet ) )
                {
                    optionCombo.setName( getNameFromCategoryOptions( categoryOptions ) );
                    list.add( optionCombo );
                    continue sortLoop;
                }
            }
        }

        return list;
    }

    private String getNameFromCategoryOptions( List<DataElementCategoryOption> categoryOptions )
    {
        StringBuilder name = new StringBuilder();

        if ( categoryOptions != null && categoryOptions.size() > 0 )
        {
            Iterator<DataElementCategoryOption> iterator = categoryOptions.iterator();

            name.append( "(" ).append( iterator.next().getDisplayName() );

            while ( iterator.hasNext() )
            {
                name.append( ", " ).append( iterator.next().getDisplayName() );
            }

            name.append( ")" );
        }

        return name.toString();
    }

    //TODO update category option -> category option combo association
    public void generateOptionCombos()
    {
        this.optionCombos = new HashSet<DataElementCategoryOptionCombo>( generateOptionCombosList() );
    }

    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public boolean isAutoGenerated()
    {
        return name != null && name.equals( DEFAULT_CATEGORY_COMBO_NAME );
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public void addDataElementCategory( DataElementCategory dataElementCategory )
    {
        categories.add( dataElementCategory );
    }

    public void removeDataElementCategory( DataElementCategory dataElementCategory )
    {
        categories.remove( dataElementCategory );
    }

    public void removeAllDataElementCategories()
    {
        categories.clear();
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @JsonProperty
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JsonView({ DetailedView.class, ExportView.class })
    @JacksonXmlElementWrapper(localName = "categories", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "category", namespace = DxfNamespaces.DXF_2_0)
    public List<DataElementCategory> getCategories()
    {
        return categories;
    }

    public void setCategories( List<DataElementCategory> categories )
    {
        this.categories = categories;
    }

    @JsonProperty(value = "categoryOptionCombos")
    @JsonSerialize(contentAs = BaseIdentifiableObject.class)
    @JsonView({ DetailedView.class })
    @JacksonXmlElementWrapper(localName = "categoryOptionCombos", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "categoryOptionCombo", namespace = DxfNamespaces.DXF_2_0)
    public Set<DataElementCategoryOptionCombo> getOptionCombos()
    {
        return optionCombos;
    }

    public void setOptionCombos( Set<DataElementCategoryOptionCombo> optionCombos )
    {
        this.optionCombos = optionCombos;
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
    @JsonView({ DetailedView.class, ExportView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public boolean isSkipTotal()
    {
        return skipTotal;
    }

    public void setSkipTotal( boolean skipTotal )
    {
        this.skipTotal = skipTotal;
    }

    @Override
    public void mergeWith( IdentifiableObject other )
    {
        super.mergeWith( other );

        if ( other.getClass().isInstance( this ) )
        {
            DataElementCategoryCombo dataElementCategoryCombo = (DataElementCategoryCombo) other;

            removeAllDataElementCategories();

            for ( DataElementCategory dataElementCategory : dataElementCategoryCombo.getCategories() )
            {
                addDataElementCategory( dataElementCategory );
            }
        }
    }
}
