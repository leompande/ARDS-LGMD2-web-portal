package org.hisp.dhis.mapping;

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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.annotation.Scanned;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.indicator.Indicator;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Jan Henrik Overland
 */
@JacksonXmlRootElement( localName = "mapLegendSet", namespace = DxfNamespaces.DXF_2_0 )
public class MapLegendSet
    extends BaseIdentifiableObject
{
    private String symbolizer;

    @Scanned
    private Set<MapLegend> mapLegends = new HashSet<MapLegend>();

    public MapLegendSet()
    {
    }

    public MapLegendSet( String name, String type, String symbolizer, Set<MapLegend> mapLegends,
        Set<Indicator> indicators, Set<DataElement> dataElements )
    {
        this.name = name;
        this.symbolizer = symbolizer;
        this.mapLegends = mapLegends;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public void removeAllMapLegends()
    {
        mapLegends.clear();
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getSymbolizer()
    {
        return symbolizer;
    }

    public void setSymbolizer( String symbolizer )
    {
        this.symbolizer = symbolizer;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlElementWrapper( localName = "mapLegends", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "mapLegend", namespace = DxfNamespaces.DXF_2_0 )
    public Set<MapLegend> getMapLegends()
    {
        return mapLegends;
    }

    public void setMapLegends( Set<MapLegend> mapLegends )
    {
        this.mapLegends = mapLegends;
    }

    @Override
    public void mergeWith( IdentifiableObject other )
    {
        super.mergeWith( other );

        if ( other.getClass().isInstance( this ) )
        {
            MapLegendSet mapLegendSet = (MapLegendSet) other;

            symbolizer = mapLegendSet.getSymbolizer() == null ? symbolizer : mapLegendSet.getSymbolizer();

            removeAllMapLegends();
            mapLegends.addAll( mapLegendSet.getMapLegends() );
        }
    }
}