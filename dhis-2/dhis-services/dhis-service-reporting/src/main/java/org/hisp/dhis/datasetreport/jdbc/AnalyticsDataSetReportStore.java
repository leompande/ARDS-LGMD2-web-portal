package org.hisp.dhis.datasetreport.jdbc;

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

import static org.hisp.dhis.system.util.ListUtils.getList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hisp.dhis.analytics.AnalyticsService;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.datasetreport.DataSetReportStore;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.system.filter.AggregatableDataElementFilter;
import org.hisp.dhis.system.util.FilterUtils;

/**
 * @author Lars Helge Overland
 */
public class AnalyticsDataSetReportStore
    implements DataSetReportStore
{
    private AnalyticsService analyticsService;
    
    public void setAnalyticsService( AnalyticsService analyticsService )
    {
        this.analyticsService = analyticsService;
    }
    
    // -------------------------------------------------------------------------
    // DataSetReportStore implementation
    // -------------------------------------------------------------------------

    @Override
    public Map<String, Double> getAggregatedValues( DataSet dataSet, Period period, OrganisationUnit unit, 
        Map<String, String> dimensions, boolean rawData )
    {
        List<DataElement> dataElements = new ArrayList<DataElement>( dataSet.getDataElements() );
        
        FilterUtils.filter( dataElements, new AggregatableDataElementFilter() );
        
        if ( dataElements.isEmpty() )
        {
            return new HashMap<String, Double>();
        }
        
        DataQueryParams params = new DataQueryParams();
        
        params.setDataElements( dataElements );
        params.setPeriod( period );
        params.setOrganisationUnit( unit );
        params.enableCategoryOptionCombos();
        
        if ( dimensions != null )
        {
            for ( String dim : dimensions.keySet() )
            {
                params.setFilters( analyticsService.getDimension( dim, getList( dimensions.get( dim ) ), null, null ) );
            }
        }
        
        Map<String, Double> map = analyticsService.getAggregatedDataValueMapping( params );
        
        Map<String, Double> dataMap = new HashMap<String, Double>();
        
        for ( Entry<String, Double> entry : map.entrySet() )
        {
            String[] split = entry.getKey().split( SEPARATOR );            
            dataMap.put( split[0] + SEPARATOR + split[3], entry.getValue() );
        }
        
        return dataMap;
    }

    @Override
    public Map<String, Double> getAggregatedSubTotals( DataSet dataSet, Period period, OrganisationUnit unit, Map<String, String> dimensions )
    {
        Map<String, Double> dataMap = new HashMap<String, Double>();
        
        for ( Section section : dataSet.getSections() )
        {
            List<DataElement> dataElements = new ArrayList<DataElement>( section.getDataElements() );
            List<DataElementCategory> categories = section.getCategoryCombo().getCategories();

            FilterUtils.filter( dataElements, new AggregatableDataElementFilter() );

            if ( dataElements.isEmpty() || categories.isEmpty() )
            {
                continue;
            }
            
            for ( DataElementCategory category : categories )
            {
                if ( category.isDefault() )
                {
                    continue;
                }
                
                DataQueryParams params = new DataQueryParams();
                
                params.setDataElements( dataElements );
                params.setPeriod( period );
                params.setOrganisationUnit( unit );
                params.setCategory( category );            

                if ( dimensions != null )
                {
                    for ( String dim : dimensions.keySet() )
                    {
                        params.setFilters( analyticsService.getDimension( dim, getList( dimensions.get( dim ) ), null, null ) );
                    }
                }
                
                Map<String, Double> map = analyticsService.getAggregatedDataValueMapping( params );
                
                for ( Entry<String, Double> entry : map.entrySet() )
                {
                    String[] split = entry.getKey().split( SEPARATOR );            
                    dataMap.put( split[0] + SEPARATOR + split[3], entry.getValue() );
                }
            }
        }
        
        return dataMap;
    }

    @Override
    public Map<String, Double> getAggregatedTotals( DataSet dataSet, Period period, OrganisationUnit unit, Map<String, String> dimensions )
    {
        List<DataElement> dataElements = new ArrayList<DataElement>( dataSet.getDataElements() );

        FilterUtils.filter( dataElements, new AggregatableDataElementFilter() );

        if ( dataElements.isEmpty() )
        {
            return new HashMap<String, Double>();
        }
        
        DataQueryParams params = new DataQueryParams();
        
        params.setDataElements( dataElements );
        params.setPeriod( period );
        params.setOrganisationUnit( unit );

        if ( dimensions != null )
        {
            for ( String dim : dimensions.keySet() )
            {
                params.setFilters( analyticsService.getDimension( dim, getList( dimensions.get( dim ) ), null, null ) );
            }
        }
        
        Map<String, Double> map = analyticsService.getAggregatedDataValueMapping( params );

        Map<String, Double> dataMap = new HashMap<String, Double>();
        
        for ( Entry<String, Double> entry : map.entrySet() )
        {
            String[] split = entry.getKey().split( SEPARATOR );            
            dataMap.put( split[0], entry.getValue() );
        }
        
        return dataMap;
    }

    @Override
    public Map<String, Double> getAggregatedIndicatorValues( DataSet dataSet, Period period, OrganisationUnit unit, Map<String, String> dimensions )
    {
        List<Indicator> indicators = new ArrayList<Indicator>( dataSet.getIndicators() );
        
        if ( indicators.isEmpty() )
        {
            return new HashMap<String, Double>();
        }        

        DataQueryParams params = new DataQueryParams();
        
        params.setIndicators( indicators );
        params.setPeriod( period );
        params.setOrganisationUnit( unit );

        if ( dimensions != null )
        {
            for ( String dim : dimensions.keySet() )
            {
                params.setFilters( analyticsService.getDimension( dim, getList( dimensions.get( dim ) ), null, null ) );
            }
        }
        
        Map<String, Double> map = analyticsService.getAggregatedDataValueMapping( params );

        Map<String, Double> dataMap = new HashMap<String, Double>();
        
        for ( Entry<String, Double> entry : map.entrySet() )
        {
            String[] split = entry.getKey().split( SEPARATOR );            
            dataMap.put( split[0], entry.getValue() );
        }
        
        return dataMap;
    }
}
