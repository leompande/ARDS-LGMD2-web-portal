package org.hisp.dhis.analytics.data;

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

import static org.hisp.dhis.analytics.AggregationType.AVERAGE_BOOL;
import static org.hisp.dhis.analytics.AggregationType.AVERAGE_INT;
import static org.hisp.dhis.analytics.AggregationType.AVERAGE_INT_DISAGGREGATION;
import static org.hisp.dhis.analytics.AggregationType.COUNT;
import static org.hisp.dhis.common.DimensionalObject.DIMENSION_SEP;
import static org.hisp.dhis.analytics.AggregationType.STDDEV;
import static org.hisp.dhis.analytics.AggregationType.VARIANCE;
import static org.hisp.dhis.analytics.DataQueryParams.VALUE_ID;
import static org.hisp.dhis.analytics.MeasureFilter.EQ;
import static org.hisp.dhis.analytics.MeasureFilter.GE;
import static org.hisp.dhis.analytics.MeasureFilter.GT;
import static org.hisp.dhis.analytics.MeasureFilter.LE;
import static org.hisp.dhis.analytics.MeasureFilter.LT;
import static org.hisp.dhis.common.IdentifiableObjectUtils.getUids;
import static org.hisp.dhis.system.util.TextUtils.getQuotedCommaDelimitedString;
import static org.hisp.dhis.system.util.TextUtils.trimEnd;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.analytics.AnalyticsManager;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.analytics.MeasureFilter;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.ListMap;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.system.util.SqlHelper;
import org.hisp.dhis.system.util.TextUtils;
import org.hisp.dhis.system.util.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.util.Assert;

/**
 * This class is responsible for producing aggregated data values. It reads data
 * from the analytics table. Organisation units provided as arguments must be on
 * the same level in the hierarchy.
 * 
 * @author Lars Helge Overland
 */
public class JdbcAnalyticsManager
    implements AnalyticsManager
{
    //TODO optimize when all options in dimensions are selected
    
    private static final Log log = LogFactory.getLog( JdbcAnalyticsManager.class );
        
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private StatementBuilder statementBuilder;
    
    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------
    
    @Async
    public Future<Map<String, Double>> getAggregatedDataValues( DataQueryParams params )
    {
        ListMap<NameableObject, NameableObject> dataPeriodAggregationPeriodMap = params.getDataPeriodAggregationPeriodMap();
        
        params.replaceAggregationPeriodsWithDataPeriods( dataPeriodAggregationPeriodMap );
        
        String sql = getSelectClause( params );
        
        if ( params.spansMultiplePartitions() )
        {
            sql += getFromWhereClauseMultiplePartitionFilters( params );
        }
        else
        {
            sql += getFromWhereClause( params, params.getPartitions().getSinglePartition() );
        }
        
        sql += getGroupByClause( params );
    
        log.debug( sql );

        Map<String, Double> map = null;
        
        try
        {
            map = getKeyValueMap( params, sql );
        }
        catch ( BadSqlGrammarException ex )
        {
            log.info( "Query failed, likely because the requested analytics table does not exist", ex );
            
            return new AsyncResult<Map<String, Double>>( new HashMap<String, Double>() );
        }
        
        replaceDataPeriodsWithAggregationPeriods( map, params, dataPeriodAggregationPeriodMap );
        
        return new AsyncResult<Map<String, Double>>( map );   
    }
    
    public void replaceDataPeriodsWithAggregationPeriods( Map<String, Double> dataValueMap, DataQueryParams params, ListMap<NameableObject, NameableObject> dataPeriodAggregationPeriodMap )
    {
        if ( params.isAggregationType( AVERAGE_INT_DISAGGREGATION ) )
        {
            int periodIndex = params.getPeriodDimensionIndex();
            
            if ( periodIndex == -1 )
            {
                return; // Period is filter, nothing to replace
            }
            
            Set<String> keys = new HashSet<String>( dataValueMap.keySet() );
            
            for ( String key : keys )
            {
                String[] keyArray = key.split( DIMENSION_SEP );
                
                Assert.notNull( keyArray[periodIndex], keyArray.toString() );
                
                List<NameableObject> periods = dataPeriodAggregationPeriodMap.get( PeriodType.getPeriodFromIsoString( keyArray[periodIndex] ) );
                
                Assert.notNull( periods, dataPeriodAggregationPeriodMap.toString() );
                
                Double value = dataValueMap.get( key );
                
                for ( NameableObject period : periods )
                {
                    String[] keyCopy = keyArray.clone();
                    keyCopy[periodIndex] = ((Period) period).getIsoDate();
                    dataValueMap.put( TextUtils.toString( keyCopy, DIMENSION_SEP ), value );
                }
                
                dataValueMap.remove( key );
            }
        }
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Generates the select clause of the query SQL.
     */
    private String getSelectClause( DataQueryParams params )
    {
        String sql = "select " + getCommaDelimitedQuotedColumns( params.getQueryDimensions() ) + ", ";
        
        if ( params.isAggregationType( AVERAGE_INT ) )
        {
            int days = PeriodType.getPeriodTypeByName( params.getPeriodType() ).getFrequencyOrder();
            
            sql += "sum(daysxvalue) / " + days;
        }
        else if ( params.isAggregationType( AVERAGE_BOOL ) )
        {
            sql += "sum(daysxvalue) / sum(daysno) * 100";
        }
        else if ( params.isAggregationType( COUNT ) )
        {
            sql += "count(value)";
        }
        else if ( params.isAggregationType( STDDEV ) )
        {
            sql += "stddev(value)";
        }
        else if ( params.isAggregationType( VARIANCE ) )
        {
            sql += "variance(value)";
        }
        else // SUM, AVERAGE_DISAGGREGATION and undefined //TODO
        {
            sql += "sum(value)";
        }
        
        sql += " as value ";
        
        return sql;        
    }
    
    /**
     * Generates the from clause of the SQL query. This method should be used for
     * queries where the period filter spans multiple partitions.
     */
    private String getFromWhereClauseMultiplePartitionFilters( DataQueryParams params )
    {
        String sql = "from (";
        
        for ( String partition : params.getPartitions().getPartitions() )
        {
            sql += "select " + getCommaDelimitedQuotedColumns( params.getQueryDimensions() ) + ", ";
            
            if ( params.isAggregationType( AVERAGE_INT ) )
            {
                sql += "daysxvalue";
            }
            else if ( params.isAggregationType( AVERAGE_BOOL ) )
            {
                sql += "daysxvalue, daysno";
            }
            else
            {
                sql += "value";
            }
            
            sql += " " + getFromWhereClause( params, partition );
            
            sql += "union all ";
        }
        
        sql = trimEnd( sql, "union all ".length() ) + ") as data ";
        
        return sql;
    }
    
    /**
     * Generates the from clause of the query SQL.
     */
    private String getFromWhereClause( DataQueryParams params, String partition )
    {
        SqlHelper sqlHelper = new SqlHelper();

        String sql = "from " + partition + " ";
        
        for ( DimensionalObject dim : params.getQueryDimensions() )
        {
            if ( !dim.isAllItems() )
            {
                String col = statementBuilder.columnQuote( dim.getDimensionName() );
                
                sql += sqlHelper.whereAnd() + " " + col + " in (" + getQuotedCommaDelimitedString( getUids( dim.getItems() ) ) + ") ";
            }
        }

        ListMap<String, DimensionalObject> filterMap = params.getDimensionFilterMap();
        
        for ( String dimension : filterMap.keySet() )
        {
            List<DimensionalObject> filters = filterMap.get( dimension );
            
            if ( DataQueryParams.anyDimensionHasItems( filters ) )
            {
                sql += sqlHelper.whereAnd() + " (";
                
                for ( DimensionalObject filter : filters )
                {
                    if ( filter.hasItems() )
                    {
                        String col = statementBuilder.columnQuote( filter.getDimensionName() );
                        
                        sql += col + " in (" + getQuotedCommaDelimitedString( getUids( filter.getItems() ) ) + ") or ";
                    }
                }
            }
            
            sql = trimEnd( sql, " or ".length() ) + ") ";
        }
        
        return sql;
    }
    
    /**
     * Generates the group by clause of the query SQL.
     */
    private String getGroupByClause( DataQueryParams params )
    {
        String sql = "group by " + getCommaDelimitedQuotedColumns( params.getQueryDimensions() );
        
        return sql;
    }

    /**
     * Retrieves data from the database based on the given query and SQL and puts
     * into a value key and value mapping.
     */
    private Map<String, Double> getKeyValueMap( DataQueryParams params, String sql )
        throws BadSqlGrammarException
    {
        Map<String, Double> map = new HashMap<String, Double>();
        
        Timer t = new Timer().start();
        
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );
        
        t.getTime( "Analytics SQL: " + sql );
        
        while ( rowSet.next() )
        {
            Double value = rowSet.getDouble( VALUE_ID );

            if ( !measureCriteriaSatisfied( params, value ) )
            {
                continue;
            }
            
            StringBuilder key = new StringBuilder();
            
            for ( DimensionalObject dim : params.getQueryDimensions() )
            {
                key.append( rowSet.getString( dim.getDimensionName() ) ).append( DIMENSION_SEP );
            }
            
            key.deleteCharAt( key.length() - 1 );
            
            map.put( key.toString(), value );
        }
        
        return map;
    }
    
    /**
     * Checks if the measure criteria specified for the given query are satisfied
     * for the given value.
     */
    private boolean measureCriteriaSatisfied( DataQueryParams params, Double value )
    {
        if ( value == null )
        {
            return false;
        }
        
        for ( MeasureFilter filter : params.getMeasureCriteria().keySet() )
        {
            Double criterion = params.getMeasureCriteria().get( filter );
            
            if ( EQ.equals( filter ) && !MathUtils.isEqual( value, criterion ) )
            {
                return false;
            }
            
            if ( GT.equals( filter ) && Double.compare( value, criterion ) <= 0 )
            {
                return false;
            }
            
            if ( GE.equals( filter ) && Double.compare( value, criterion ) < 0 )
            {
                return false;
            }
            
            if ( LT.equals( filter ) && Double.compare( value, criterion ) >= 0 )
            {
                return false;
            }
            
            if ( LE.equals( filter ) && Double.compare( value, criterion ) > 0 )
            {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Generates a comma-delimited string based on the dimension names of the
     * given dimensions where each dimension name is quoted.
     */
    private String getCommaDelimitedQuotedColumns( Collection<DimensionalObject> dimensions )
    {        
        final StringBuilder builder = new StringBuilder();
        
        if ( dimensions != null && !dimensions.isEmpty() )
        {
            for ( DimensionalObject dimension : dimensions )
            {
                builder.append( statementBuilder.columnQuote( dimension.getDimensionName() ) ).append( "," );
            }
            
            return builder.substring( 0, builder.length() - 1 );
        }
        
        return builder.toString();
    }
}
