package org.hisp.dhis.analytics.event.data;

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

import static org.hisp.dhis.common.DimensionalObject.ORGUNIT_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.PERIOD_DIM_ID;
import static org.hisp.dhis.common.IdentifiableObjectUtils.getUids;
import static org.hisp.dhis.system.util.DateUtils.getMediumDateString;
import static org.hisp.dhis.system.util.TextUtils.getQuotedCommaDelimitedString;
import static org.hisp.dhis.system.util.TextUtils.removeLast;
import static org.hisp.dhis.system.util.TextUtils.trimEnd;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.analytics.event.EventAnalyticsManager;
import org.hisp.dhis.analytics.event.EventQueryParams;
import org.hisp.dhis.analytics.event.EventQueryPlanner;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.common.QueryItem;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.system.util.TextUtils;
import org.hisp.dhis.system.util.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * TODO could use row_number() and filtering for paging, but not supported on MySQL.
 * 
 * @author Lars Helge Overland
 */
public class JdbcEventAnalyticsManager
    implements EventAnalyticsManager
{
    private static final Log log = LogFactory.getLog( JdbcEventAnalyticsManager.class );
    
    private static final int MAX_LIMIT = 10000;
    
    private static final String QUERY_ERR_MSG = "Query failed, likely because the requested analytics table does not exist";
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private StatementBuilder statementBuilder;

    // -------------------------------------------------------------------------
    // EventAnalyticsManager implementation
    // -------------------------------------------------------------------------

    public Grid getAggregatedEventData( EventQueryParams params, Grid grid )
    {
        String sql = "select count(psi) as value," + getSelectColumns( params ) + " ";

        // ---------------------------------------------------------------------
        // Criteria
        // ---------------------------------------------------------------------

        if ( params.spansMultiplePartitions() )
        {
            sql += getFromWhereMultiplePartitionsClause( params );
        }
        else
        {
            sql += getFromWhereClause( params, params.getPartitions().getSinglePartition() );
        }

        // ---------------------------------------------------------------------
        // Group by
        // ---------------------------------------------------------------------

        sql += "group by " + getSelectColumns( params ) + " ";

        // ---------------------------------------------------------------------
        // Sort order
        // ---------------------------------------------------------------------

        if ( params.hasSortOrder() )
        {
            sql += "order by value " + params.getSortOrder().toString().toLowerCase() + " ";
        }
        
        // ---------------------------------------------------------------------
        // Limit
        // ---------------------------------------------------------------------

        if ( params.hasLimit() )
        {
            sql += "limit " + params.getLimit();
        }
        else
        {
            sql += "limit " + MAX_LIMIT;
        }
        
        // ---------------------------------------------------------------------
        // Grid
        // ---------------------------------------------------------------------

        try
        {
            grid.addRows( getAggregatedEventData( params, sql ) );
        }
        catch ( BadSqlGrammarException ex )
        {
            log.info( QUERY_ERR_MSG, ex );
        }
        
        return grid;
    }
    
    private Grid getAggregatedEventData( EventQueryParams params, String sql )
    {
        Timer t = new Timer().start();
        
        Grid grid = new ListGrid();
        
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );

        t.getTime( "Analytics event aggregate SQL: " + sql );
        
        while ( rowSet.next() )
        {
            int value = rowSet.getInt( "value" );
            
            grid.addRow();
            
            for ( DimensionalObject dimension : params.getDimensions() )
            {
                String dimensionValue = rowSet.getString( dimension.getDimensionName() );
                grid.addValue( dimensionValue );
            }
            
            for ( QueryItem queryItem : params.getItems() )
            {
                String itemValue = rowSet.getString( queryItem.getItem().getUid() );                
                grid.addValue( itemValue );
            }
            
            grid.addValue( value );
        }

        return grid;
    }
    
    public Grid getEvents( EventQueryParams params, Grid grid )
    {
        String sql = "select psi,ps,executiondate,longitude,latitude,ouname,oucode," + getSelectColumns( params ) + " ";

        // ---------------------------------------------------------------------
        // Criteria
        // ---------------------------------------------------------------------

        sql += getFromWhereClause( params, params.getPartitions().getSinglePartition() );
        
        // ---------------------------------------------------------------------
        // Sorting
        // ---------------------------------------------------------------------

        if ( params.isSorting() )
        {
            sql += "order by ";
        
            for ( String item : params.getAsc() )
            {
                sql += statementBuilder.columnQuote( item ) + " asc,";
            }
            
            for  ( String item : params.getDesc() )
            {
                sql += statementBuilder.columnQuote( item ) + " desc,";
            }
            
            sql = removeLast( sql, 1 ) + " ";
        }
        
        // ---------------------------------------------------------------------
        // Paging
        // ---------------------------------------------------------------------

        if ( params.isPaging() )
        {
            sql += "limit " + params.getPageSizeWithDefault() + " offset " + params.getOffset();
        }

        // ---------------------------------------------------------------------
        // Grid
        // ---------------------------------------------------------------------

        int rowLength = grid.getHeaders().size();

        try
        {
            grid.addRows( getEvents( params, sql, rowLength ) );
        }
        catch ( BadSqlGrammarException ex )
        {
            log.info( QUERY_ERR_MSG, ex );
        }
        
        return grid;
    }

    private Grid getEvents( EventQueryParams params, String sql, int rowLength )
    {
        Timer t = new Timer().start();

        Grid grid = new ListGrid();
        
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );

        t.getTime( "Analytics event query SQL: " + sql );
        
        while ( rowSet.next() )
        {
            grid.addRow();
            
            for ( int i = 0; i < rowLength; i++ )
            {
                int index = i + 1;
                
                grid.addValue( rowSet.getString( index ) );
            }
        }
        
        return grid;
    }
    
    public int getEventCount( EventQueryParams params )
    {
        String sql = "select count(psi) ";
        
        sql += getFromWhereClause( params, params.getPartitions().getSinglePartition() );
        
        int count = 0;
        
        try
        {
            count = getEventCount( sql );          
        }
        catch ( BadSqlGrammarException ex )
        {
            log.info( QUERY_ERR_MSG, ex );
        }

        return count;
    }
    
    private int getEventCount( String sql )
    {
        Timer t = new Timer().start();
        
        int count = jdbcTemplate.queryForObject( sql, Integer.class );

        t.getTime( "Analytics event count SQL: " + sql );
        
        return count;
    }
    
    public List<String> getAnalyticsTables( Program program )
    {
        final String sql = 
            "select table_name from information_schema.tables " +
            "where table_name like '" + EventQueryPlanner.TABLE_PREFIX + "_%_" + program.getUid().toLowerCase() + "' " +
            "and table_type = 'BASE TABLE'";
        
        return jdbcTemplate.queryForList( sql, String.class );
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Returns the dynamic select columns. Dimensions come first and query items
     * second.
     */
    private String getSelectColumns( EventQueryParams params )
    {
        String sql = "";
        
        for ( DimensionalObject dimension : params.getDimensions() )
        {
            sql += statementBuilder.columnQuote( dimension.getDimensionName() ) + ",";
        }
        
        for ( QueryItem queryItem : params.getItems() )
        {
            IdentifiableObject item = queryItem.getItem();
            
            sql += statementBuilder.columnQuote( item.getUid() ) + ",";
        }
        
        return removeLast( sql, 1 );
    }

    private String getFromWhereMultiplePartitionsClause( EventQueryParams params )
    {
        String sql = "from (";
        
        for ( String partition : params.getPartitions().getPartitions() )
        {
            sql += "select psi, " + getSelectColumns( params );
            
            sql += " " + getFromWhereClause( params, partition );
            
            sql += "union all ";
        }

        sql = trimEnd( sql, "union all ".length() ) + ") as data ";
        
        return sql;
    }
    
    private String getFromWhereClause( EventQueryParams params, String partition )
    {
        String sql = "from " + partition + " ";
        
        if ( params.hasStartEndDate() )
        {        
            sql += "where executiondate >= '" + getMediumDateString( params.getStartDate() ) + "' ";
            sql += "and executiondate <= '" + getMediumDateString( params.getEndDate() ) + "' ";
        }
        else // Periods
        {
            sql += "where " + params.getPeriodType() + " in (" + getQuotedCommaDelimitedString( getUids( params.getDimensionOrFilter( PERIOD_DIM_ID ) ) ) + ") ";
        }
        
        if ( params.isOrganisationUnitMode( EventQueryParams.OU_MODE_SELECTED ) )
        {
            sql += "and ou in (" + getQuotedCommaDelimitedString( getUids( params.getDimensionOrFilter( ORGUNIT_DIM_ID ) ) ) + ") ";
        }
        else if ( params.isOrganisationUnitMode( EventQueryParams.OU_MODE_CHILDREN ) )
        {
            sql += "and ou in (" + getQuotedCommaDelimitedString( getUids( params.getOrganisationUnitChildren() ) ) + ") ";
        }
        else // Descendants
        {
            sql += "and (";
            
            for ( NameableObject object : params.getDimensionOrFilter( ORGUNIT_DIM_ID ) )
            {
                OrganisationUnit unit = (OrganisationUnit) object;
                sql += "uidlevel" + unit.getLevel() + " = '" + unit.getUid() + "' or ";
            }
            
            sql = TextUtils.removeLast( sql, 3 ) + ") ";
        }
        
        if ( params.getProgramStage() != null )
        {
            sql += "and ps = '" + params.getProgramStage().getUid() + "' ";
        }

        for ( QueryItem item : params.getItems() )
        {
            if ( item.hasFilter() )
            {
                sql += "and " + getColumn( item ) + " " + item.getSqlOperator() + " " + getSqlFilter( item ) + " ";
            }
        }
        
        for ( QueryItem filter : params.getItemFilters() )
        {
            if ( filter.hasFilter() )
            {
                sql += "and " + getColumn( filter ) + " " + filter.getSqlOperator() + " " + getSqlFilter( filter ) + " ";
            }
        }
        
        if ( params.isCoordinatesOnly() )
        {
            sql += "and (longitude is not null and latitude is not null) ";
        }

        return sql;
    }
    
    /**
     * Returns an encoded column name wrapped in lower directive if not numeric.
     */
    private String getColumn( QueryItem item )
    {
        String col = statementBuilder.columnQuote( item.getItem().getUid() );
        
        return item.isNumeric() ? col : "lower(" + col + ")"; 
    }
    
    /**
     * Returns the filter value for the given query item.
     */
    private String getSqlFilter( QueryItem item )
    {
        String encodedFilter = statementBuilder.encode( item.getFilter(), false );
        
        String sqlFilter = item.getSqlFilter( encodedFilter );
        
        return item.isNumeric() ? sqlFilter : sqlFilter.toLowerCase();
    }
}
