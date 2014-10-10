package org.hisp.dhis.jchart.data;

/*
 * Copyright (c) 2004-2011, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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

import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author Tran Thanh Tri
 */

public class JChartData
{
    private String title;

    private String subtitle;

    private List<JChartSeriesData> series = new ArrayList<JChartSeriesData>();

    private List<String> categories = new ArrayList<String>();

    private String legend;

    public String jsonCategories()
    {
        StringBuffer result = new StringBuffer();

        for ( int i = 0; i < categories.size(); i++ )
        {
            result.append( "\"" + StringEscapeUtils.escapeJavaScript( categories.get( i ) ) + "\"" );
            if ( i < categories.size() - 1 )
            {
                result.append( "," );
            }
        }

        return result.toString();
    }

    public String jsonSeries()
    {
        StringBuffer result = new StringBuffer();

        for ( int i = 0; i < series.size(); i++ )
        {
            JChartSeriesData jChartSeriesData = series.get( i );

            result.append( "{" );
            result.append( "\"type\":\"" + jChartSeriesData.getType() + "\"" );
            result.append( ",\"color\":\"" + jChartSeriesData.getColor() + "\"" );
            result.append( ",\"name\":\"" + StringEscapeUtils.escapeJavaScript( jChartSeriesData.getName() ) + "\"" );
            result.append( ",\"data\":[" );

            for ( int j = 0; j < jChartSeriesData.getValues().size(); j++ )
            {
                if ( jChartSeriesData.isPie() )
                {
                    result.append( "[" );
                    result.append( "\"" + StringEscapeUtils.escapeJavaScript( categories.get( j ) ) + "\","
                        + jChartSeriesData.getValues().get( j ) );
                    result.append( "]" );
                    if ( j < jChartSeriesData.getValues().size() - 1 )
                    {
                        result.append( "," );
                    }
                }
                else
                {

                    result.append( jChartSeriesData.getValues().get( j ) );

                    if ( j < jChartSeriesData.getValues().size() - 1 )
                    {
                        result.append( "," );
                    }
                }
            }
            result.append( "]" );

            result.append( "}" );

            if ( i < series.size() - 1 )
            {
                result.append( "," );
            }

        }

        return result.toString();
    }

    public String getLegend()
    {
        return legend;
    }

    public void setLegend( String legend )
    {
        this.legend = legend;
    }

    public void addCategory( String category )
    {
        this.categories.add( category );
    }

    public List<String> getCategories()
    {
        return categories;
    }

    public void setCategories( List<String> categories )
    {
        this.categories = categories;
    }

    public void addSeries( JChartSeriesData series_ )
    {
        this.series.add( series_ );
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public String getSubtitle()
    {
        return subtitle;
    }

    public void setSubtitle( String subtitle )
    {
        this.subtitle = subtitle;
    }

    public List<JChartSeriesData> getSeries()
    {
        return series;
    }

    public void setSeries( List<JChartSeriesData> series )
    {
        this.series = series;
    }

}
