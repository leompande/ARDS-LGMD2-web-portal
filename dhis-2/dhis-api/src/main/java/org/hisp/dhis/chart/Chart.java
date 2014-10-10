package org.hisp.dhis.chart;

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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.common.BaseAnalyticalObject;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.IdentifiableObjectUtils;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.DimensionalView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.user.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lars Helge Overland
 */
@JacksonXmlRootElement(localName = "chart", namespace = DxfNamespaces.DXF_2_0)
public class Chart
    extends BaseAnalyticalObject
{
    private static final long serialVersionUID = 2570074075484545534L;

    public static final String SIZE_NORMAL = "normal";
    public static final String SIZE_WIDE = "wide";
    public static final String SIZE_TALL = "tall";

    public static final String TYPE_COLUMN = "column";
    public static final String TYPE_STACKED_COLUMN = "stackedcolumn";
    public static final String TYPE_BAR = "bar";
    public static final String TYPE_STACKED_BAR = "stackedbar";
    public static final String TYPE_LINE = "line";
    public static final String TYPE_AREA = "area";
    public static final String TYPE_PIE = "pie";
    public static final String TYPE_RADAR = "radar"; // Spider web

    private String domainAxisLabel;

    private String rangeAxisLabel;

    private String type;

    private String series;

    private String category;

    private List<String> filterDimensions = new ArrayList<String>();

    private boolean hideLegend;

    private boolean regression;

    private boolean hideTitle;

    private boolean hideSubtitle;

    private String title;

    private Double targetLineValue;

    private String targetLineLabel;

    private Double baseLineValue;

    private String baseLineLabel;

    private boolean showData;

    // -------------------------------------------------------------------------
    // Transient properties
    // -------------------------------------------------------------------------

    private transient I18nFormat format;

    private transient List<Period> relativePeriods = new ArrayList<Period>();

    private transient User user;

    private transient List<OrganisationUnit> organisationUnitsAtLevel = new ArrayList<OrganisationUnit>();

    private transient List<OrganisationUnit> organisationUnitsInGroups = new ArrayList<OrganisationUnit>();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Chart()
    {
    }

    public Chart( String name )
    {
        this.name = name;
    }

    // -------------------------------------------------------------------------
    // Init
    // -------------------------------------------------------------------------

    @Override
    public void init( User user, Date date, OrganisationUnit organisationUnit,
        List<OrganisationUnit> organisationUnitsAtLevel, List<OrganisationUnit> organisationUnitsInGroups, I18nFormat format )
    {
        this.user = user;
        this.relativePeriodDate = date;
        this.relativeOrganisationUnit = organisationUnit;
        this.organisationUnitsAtLevel = organisationUnitsAtLevel;
        this.organisationUnitsInGroups = organisationUnitsInGroups;
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public List<NameableObject> series()
    {
        DimensionalObject object = getDimensionalObject( series, relativePeriodDate, user, true, organisationUnitsAtLevel, organisationUnitsInGroups, format );

        return object != null ? object.getItems() : null;
    }

    public List<NameableObject> category()
    {
        DimensionalObject object = getDimensionalObject( category, relativePeriodDate, user, true, organisationUnitsAtLevel, organisationUnitsInGroups, format );

        return object != null ? object.getItems() : null;
    }

    public List<NameableObject> filters()
    {
        List<NameableObject> filterItems = new ArrayList<NameableObject>();

        for ( String filter : filterDimensions )
        {
            DimensionalObject object = getDimensionalObject( filter, relativePeriodDate, user, true, organisationUnitsAtLevel, organisationUnitsInGroups, format );

            if ( object != null )
            {
                filterItems.addAll( object.getItems() );
            }
        }

        return filterItems;
    }

    public String generateTitle()
    {
        return IdentifiableObjectUtils.join( filters() );
    }

    @Override
    public void populateAnalyticalProperties()
    {
        columns.addAll( getDimensionalObjectList( series ) );
        rows.addAll( getDimensionalObjectList( category ) );

        for ( String filter : filterDimensions )
        {
            filters.addAll( getDimensionalObjectList( filter ) );
        }
    }

    public List<OrganisationUnit> getAllOrganisationUnits()
    {
        if ( transientOrganisationUnits != null && !transientOrganisationUnits.isEmpty() )
        {
            return transientOrganisationUnits;
        }
        else
        {
            return organisationUnits;
        }
    }

    public OrganisationUnit getFirstOrganisationUnit()
    {
        List<OrganisationUnit> units = getAllOrganisationUnits();
        return units != null && !units.isEmpty() ? units.iterator().next() : null;
    }

    public List<Period> getAllPeriods()
    {
        List<Period> list = new ArrayList<Period>();

        list.addAll( relativePeriods );

        for ( Period period : periods )
        {
            if ( !list.contains( period ) )
            {
                list.add( period );
            }
        }

        return list;
    }

    /**
     * Sets all dimensions for this chart.
     *
     * @param series   the series dimension.
     * @param category the category dimension.
     * @param filter   the filter dimension.
     */
    public void setDimensions( String series, String category, String filter )
    {
        this.series = series;
        this.category = category;
        this.filterDimensions.clear();
        this.filterDimensions.add( filter );
    }

    public boolean isType( String type )
    {
        return this.type != null && this.type.equalsIgnoreCase( type );
    }

    public boolean isTargetLine()
    {
        return targetLineValue != null;
    }

    public boolean isBaseLine()
    {
        return baseLineValue != null;
    }

    public int getWidth()
    {
        return 700;
    }

    public int getHeight()
    {
        return 500;
    }

    // -------------------------------------------------------------------------
    // Getters and setters properties
    // -------------------------------------------------------------------------

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class, DimensionalView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getDomainAxisLabel()
    {
        return domainAxisLabel;
    }

    public void setDomainAxisLabel( String domainAxisLabel )
    {
        this.domainAxisLabel = domainAxisLabel;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class, DimensionalView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getRangeAxisLabel()
    {
        return rangeAxisLabel;
    }

    public void setRangeAxisLabel( String rangeAxisLabel )
    {
        this.rangeAxisLabel = rangeAxisLabel;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class, DimensionalView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getSeries()
    {
        return series;
    }

    public void setSeries( String series )
    {
        this.series = series;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getCategory()
    {
        return category;
    }

    public void setCategory( String category )
    {
        this.category = category;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class })
    @JacksonXmlElementWrapper(localName = "filterDimensions", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty(localName = "filterDimension", namespace = DxfNamespaces.DXF_2_0)
    public List<String> getFilterDimensions()
    {
        return filterDimensions;
    }

    public void setFilterDimensions( List<String> filterDimensions )
    {
        this.filterDimensions = filterDimensions;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class, DimensionalView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public boolean isHideLegend()
    {
        return hideLegend;
    }

    public void setHideLegend( boolean hideLegend )
    {
        this.hideLegend = hideLegend;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class, DimensionalView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public boolean isRegression()
    {
        return regression;
    }

    public void setRegression( boolean regression )
    {
        this.regression = regression;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class, DimensionalView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Double getTargetLineValue()
    {
        return targetLineValue;
    }

    public void setTargetLineValue( Double targetLineValue )
    {
        this.targetLineValue = targetLineValue;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class, DimensionalView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getTargetLineLabel()
    {
        return targetLineLabel;
    }

    public void setTargetLineLabel( String targetLineLabel )
    {
        this.targetLineLabel = targetLineLabel;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class, DimensionalView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public Double getBaseLineValue()
    {
        return baseLineValue;
    }

    public void setBaseLineValue( Double baseLineValue )
    {
        this.baseLineValue = baseLineValue;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class, DimensionalView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getBaseLineLabel()
    {
        return baseLineLabel;
    }

    public void setBaseLineLabel( String baseLineLabel )
    {
        this.baseLineLabel = baseLineLabel;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class, DimensionalView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public boolean isHideTitle()
    {
        return hideTitle;
    }

    public void setHideTitle( boolean hideTitle )
    {
        this.hideTitle = hideTitle;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class, DimensionalView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public boolean isHideSubtitle()
    {
        return hideSubtitle;
    }

    public void setHideSubtitle( Boolean hideSubtitle )
    {
        this.hideSubtitle = hideSubtitle;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class, DimensionalView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public String getTitle()
    {
        return this.title;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class, DimensionalView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public boolean isShowData()
    {
        return showData;
    }

    public void setShowData( boolean showData )
    {
        this.showData = showData;
    }

    @JsonProperty
    @JsonView({ DetailedView.class, ExportView.class, DimensionalView.class })
    @JacksonXmlProperty(namespace = DxfNamespaces.DXF_2_0)
    public boolean isRewindRelativePeriods()
    {
        return rewindRelativePeriods;
    }

    public void setRewindRelativePeriods( boolean rewindRelativePeriods )
    {
        this.rewindRelativePeriods = rewindRelativePeriods;
    }

    // -------------------------------------------------------------------------
    // Getters and setters for transient properties
    // -------------------------------------------------------------------------

    @JsonIgnore
    public I18nFormat getFormat()
    {
        return format;
    }

    @JsonIgnore
    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    @JsonIgnore
    public List<Period> getRelativePeriods()
    {
        return relativePeriods;
    }

    @JsonIgnore
    public void setRelativePeriods( List<Period> relativePeriods )
    {
        this.relativePeriods = relativePeriods;
    }

    // -------------------------------------------------------------------------
    // Merge with
    // -------------------------------------------------------------------------

    @Override
    public void mergeWith( IdentifiableObject other )
    {
        super.mergeWith( other );

        if ( other.getClass().isInstance( this ) )
        {
            Chart chart = (Chart) other;

            domainAxisLabel = chart.getDomainAxisLabel() == null ? domainAxisLabel : chart.getDomainAxisLabel();
            rangeAxisLabel = chart.getRangeAxisLabel() == null ? rangeAxisLabel : chart.getRangeAxisLabel();
            type = chart.getType() == null ? type : chart.getType();
            series = chart.getSeries() == null ? series : chart.getSeries();
            category = chart.getCategory() == null ? category : chart.getCategory();
            hideLegend = chart.isHideLegend();
            regression = chart.isRegression();
            hideTitle = chart.isHideTitle();
            hideSubtitle = chart.isHideSubtitle();
            title = chart.getTitle() == null ? title : chart.getTitle();
            targetLineValue = chart.getTargetLineValue() == null ? targetLineValue : chart.getTargetLineValue();
            targetLineLabel = chart.getTargetLineLabel() == null ? targetLineLabel : chart.getTargetLineLabel();
            baseLineValue = chart.getBaseLineValue() == null ? baseLineValue : chart.getBaseLineValue();
            baseLineLabel = chart.getBaseLineLabel() == null ? baseLineLabel : chart.getBaseLineLabel();
            showData = chart.isShowData();
            rewindRelativePeriods = chart.isRewindRelativePeriods();

            filterDimensions.clear();
            filterDimensions.addAll( chart.getFilterDimensions() );
        }
    }
}
