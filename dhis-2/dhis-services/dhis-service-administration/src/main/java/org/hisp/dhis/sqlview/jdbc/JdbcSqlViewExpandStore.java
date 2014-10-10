package org.hisp.dhis.sqlview.jdbc;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.sqlview.SqlView;
import org.hisp.dhis.sqlview.SqlViewExpandStore;
import org.hisp.dhis.system.util.SqlHelper;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.Map;

/**
 * @author Dang Duy Hieu
 * @version $Id JdbcSqlViewExpandStore.java July 06, 2010$
 */
public class JdbcSqlViewExpandStore
    implements SqlViewExpandStore
{
    private static final Log log = LogFactory.getLog( JdbcSqlViewExpandStore.class );

    private static final String PREFIX_CREATEVIEW_QUERY = "CREATE VIEW ";
    private static final String PREFIX_SELECT_QUERY = "SELECT * FROM ";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    private StatementBuilder statementBuilder;

    public void setStatementBuilder( StatementBuilder statementBuilder )
    {
        this.statementBuilder = statementBuilder;
    }

    // -------------------------------------------------------------------------
    // Implementing methods
    // -------------------------------------------------------------------------

    @Override
    public boolean viewTableExists( String viewTableName )
    {
        try
        {
            jdbcTemplate.queryForRowSet( "select * from " + statementBuilder.columnQuote( viewTableName ) + " limit 1" );

            return true;
        }
        catch ( BadSqlGrammarException ex )
        {
            return false; // View does not exist
        }
    }

    @Override
    public String createViewTable( SqlView sqlViewInstance )
    {
        String viewName = sqlViewInstance.getViewName();

        dropViewTable( viewName );

        final String sql = PREFIX_CREATEVIEW_QUERY + statementBuilder.columnQuote( viewName ) + " AS " + sqlViewInstance.getSqlQuery();

        log.debug( "Create view SQL: " + sql );

        try
        {
            jdbcTemplate.execute( sql );
        }
        catch ( BadSqlGrammarException ex )
        {
            return ex.getCause().getMessage();
        }

        return null;
    }

    @Override
    public void setUpDataSqlViewTable( Grid grid, String viewTableName, Map<String, String> criteria )
    {
        String sql = PREFIX_SELECT_QUERY + statementBuilder.columnQuote( viewTableName );

        if ( criteria != null && !criteria.isEmpty() )
        {
            SqlHelper helper = new SqlHelper();

            for ( String filter : criteria.keySet() )
            {
                sql += " " + helper.whereAnd() + " " + statementBuilder.columnQuote( filter ) + "='" + criteria.get( filter ) + "'";
            }
        }

        log.info( "Get view SQL: " + sql );

        SqlRowSet rs = jdbcTemplate.queryForRowSet( sql );

        grid.addHeaders( rs );
        grid.addRows( rs );
    }

    @Override
    public String testSqlGrammar( String sql )
    {
        String viewNameCheck = SqlView.PREFIX_VIEWNAME + System.currentTimeMillis();

        sql = PREFIX_CREATEVIEW_QUERY + viewNameCheck + " AS " + sql;

        log.debug( "Test view SQL: " + sql );

        try
        {
            jdbcTemplate.execute( sql );

            dropViewTable( viewNameCheck );
        }
        catch ( BadSqlGrammarException ex )
        {
            return ex.getCause().getMessage();
        }

        return "";
    }

    @Override
    public void dropViewTable( String viewName )
    {
        try
        {
            final String sql = "DROP VIEW IF EXISTS " + statementBuilder.columnQuote( viewName );
            
            log.info( "Drop view SQL: " + sql );
            
            jdbcTemplate.update( sql );
        }
        catch ( BadSqlGrammarException ex )
        {
            log.warn( "Could not drop view: " + viewName, ex );
        }
    }
}