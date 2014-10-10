package org.hisp.dhis.datamart.aggregation.cache;

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

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.ConversionUtils;

/**
 * @author Lars Helge Overland
 */
public class MemoryAggregationCache
    implements AggregationCache
{
    private static final String SEPARATOR = "-";
    
    // -------------------------------------------------------------------------
    // Cache
    // -------------------------------------------------------------------------

    private final ThreadLocal<Map<String, Collection<Integer>>> intersectingPeriodCache = new ThreadLocal<Map<String,Collection<Integer>>>();

    private final ThreadLocal<Map<String, Collection<Integer>>> periodBetweenDatesCache = new ThreadLocal<Map<String,Collection<Integer>>>();

    private final ThreadLocal<Map<String, Collection<Integer>>> periodBetweenDatesPeriodTypeCache = new ThreadLocal<Map<String,Collection<Integer>>>();

    private final ThreadLocal<Map<String, Period>> periodCache = new ThreadLocal<Map<String,Period>>();

    private final ThreadLocal<Map<String, Integer>> organisationUnitLevelCache = new ThreadLocal<Map<String, Integer>>();
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private PeriodService periodService;
    
    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    // -------------------------------------------------------------------------
    // AggregationCache implementation
    // -------------------------------------------------------------------------

    public Collection<Integer> getIntersectingPeriods( final Date startDate, final Date endDate )
    {
        final String key = startDate.toString() + SEPARATOR + endDate.toString();
        
        Map<String, Collection<Integer>> cache = intersectingPeriodCache.get();
        
        Collection<Integer> periods = null;
        
        if ( cache != null && ( periods = cache.get( key ) ) != null )
        {
            return periods;
        }
        
        periods = ConversionUtils.getIdentifiers( Period.class, periodService.getIntersectingPeriods( startDate, endDate ) );
        
        cache = ( cache == null ) ? new HashMap<String, Collection<Integer>>() : cache;
        
        cache.put( key, periods );
        
        intersectingPeriodCache.set( cache );
        
        return periods;
    }

    public Collection<Integer> getPeriodsBetweenDates( final Date startDate, final Date endDate )
    {
        final String key = startDate.toString() + SEPARATOR + endDate.toString();
        
        Map<String, Collection<Integer>> cache = periodBetweenDatesCache.get();
        
        Collection<Integer> periods = null;
        
        if ( cache != null && ( periods = cache.get( key ) ) != null )
        {
            return periods;
        }
        
        periods = ConversionUtils.getIdentifiers( Period.class, periodService.getPeriodsBetweenDates( startDate, endDate ) );
        
        cache = ( cache == null ) ? new HashMap<String, Collection<Integer>>() : cache;
        
        cache.put( key, periods );
        
        periodBetweenDatesCache.set( cache );
        
        return periods;
    }

    public Collection<Integer> getPeriodsBetweenDatesPeriodType( final PeriodType periodType, final Date startDate, final Date endDate )
    {
        final String key = periodType.getName() + SEPARATOR + startDate.toString() + SEPARATOR + endDate.toString();
        
        Map<String, Collection<Integer>> cache = periodBetweenDatesPeriodTypeCache.get();
        
        Collection<Integer> periods = null;
        
        if ( cache != null && ( periods = cache.get( key ) ) != null )
        {
            return periods;
        }
        
        periods = ConversionUtils.getIdentifiers( Period.class, periodService.getPeriodsBetweenDates( periodType, startDate, endDate ) );
        
        cache = ( cache == null ) ? new HashMap<String, Collection<Integer>>() : cache;
        
        cache.put( key, periods );
        
        periodBetweenDatesPeriodTypeCache.set( cache );
        
        return periods;
    }
    
    public Period getPeriod( final int id )
    {
        final String key = String.valueOf( id );
        
        Map<String, Period> cache = periodCache.get();
        
        Period period = null;
        
        if ( cache != null && ( period = cache.get( key ) ) != null )
        {
            return period;
        }
        
        period = periodService.getPeriod( id );
        
        cache = ( cache == null ) ? new HashMap<String, Period>() : cache;
        
        cache.put( key, period );
        
        periodCache.set( cache );
        
        return period;
    }

    public int getLevelOfOrganisationUnit( final int id )
    {
        final String key = String.valueOf( id );
        
        Map<String, Integer> cache = organisationUnitLevelCache.get();
        
        Integer level = null;
        
        if ( cache != null && ( level = cache.get( key ) ) != null )
        {
            return level;
        }
                
        level = organisationUnitService.getLevelOfOrganisationUnit( id );
        
        cache = ( cache == null ) ? new HashMap<String, Integer>() : cache;
        
        cache.put( key, level );
        
        organisationUnitLevelCache.set( cache );
        
        return level;
    }
    
    public void clearCache()
    {
        intersectingPeriodCache.remove();
        periodBetweenDatesCache.remove();
        periodBetweenDatesPeriodTypeCache.remove();
        periodCache.remove();
        organisationUnitLevelCache.remove();
    }
}
