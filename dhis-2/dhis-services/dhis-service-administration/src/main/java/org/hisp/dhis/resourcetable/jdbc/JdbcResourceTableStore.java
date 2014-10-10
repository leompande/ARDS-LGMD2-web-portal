package org.hisp.dhis.resourcetable.jdbc;

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

import java.util.List;

import org.amplecode.quick.Statement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.resourcetable.ResourceTableStore;
import org.hisp.dhis.resourcetable.statement.CreateCategoryTableStatement;
import org.hisp.dhis.resourcetable.statement.CreateDataElementGroupSetTableStatement;
import org.hisp.dhis.resourcetable.statement.CreateIndicatorGroupSetTableStatement;
import org.hisp.dhis.resourcetable.statement.CreateOrganisationUnitGroupSetTableStatement;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Lars Helge Overland
 */
public class JdbcResourceTableStore
    implements ResourceTableStore
{
    private static final Log log = LogFactory.getLog( JdbcResourceTableStore.class );
    
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
    // ResourceTableStore implementation
    // -------------------------------------------------------------------------

    public void batchUpdate( int columns, String tableName, List<Object[]> batchArgs )
    {
        if ( columns == 0 || tableName == null )
        {
            return;
        }
        
        StringBuilder builder = new StringBuilder( "insert into " + tableName + " values (" );
        
        for ( int i = 0; i < columns; i++ )
        {
            builder.append( "?," );
        }
        
        builder.deleteCharAt( builder.length() - 1 ).append( ")" );
        
        jdbcTemplate.batchUpdate( builder.toString(), batchArgs );
    }
    
    // -------------------------------------------------------------------------
    // OrganisationUnitStructure
    // -------------------------------------------------------------------------

    public void createOrganisationUnitStructure( int maxLevel )
    {
        try
        {
            jdbcTemplate.execute( "DROP TABLE IF EXISTS " + TABLE_NAME_ORGANISATION_UNIT_STRUCTURE );            
        }
        catch ( BadSqlGrammarException ex )
        {
            // Do nothing, table does not exist
        }

        String quote = statementBuilder.getColumnQuote();
        
        StringBuilder sql = new StringBuilder();
        
        sql.append( "CREATE TABLE " ).append( TABLE_NAME_ORGANISATION_UNIT_STRUCTURE ).
            append( " ( organisationunitid INTEGER NOT NULL PRIMARY KEY, level INTEGER" );
        
        for ( int k = 1 ; k <= maxLevel; k++ )
        {
            sql.append( ", " ).append( quote ).append( "idlevel" + k ).append( quote ).append (" INTEGER, " ).
                append( quote ).append( "uidlevel" + k ).append( quote ).append( " CHARACTER(11)" );
        }
        
        sql.append( ");" );
        
        log.info( "Create organisation unit structure table SQL: " + sql );
        
        jdbcTemplate.execute( sql.toString() );
    }
    
    // -------------------------------------------------------------------------
    // DataElementCategoryOptionComboName
    // -------------------------------------------------------------------------
    
    public void createDataElementCategoryOptionComboName()
    {
        try
        {
            jdbcTemplate.execute( "DROP TABLE IF EXISTS " + TABLE_NAME_CATEGORY_OPTION_COMBO_NAME );            
        }
        catch ( BadSqlGrammarException ex )
        {
            // Do nothing, table does not exist
        }
        
        final String sql = "CREATE TABLE " + TABLE_NAME_CATEGORY_OPTION_COMBO_NAME + 
            " ( categoryoptioncomboid INTEGER NOT NULL PRIMARY KEY, categoryoptioncomboname VARCHAR(250) )";
        
        log.info( "Create category option combo name table SQL: " + sql );
        
        jdbcTemplate.execute( sql );
    }
    
    // -------------------------------------------------------------------------
    // DataElementGroupSetTable
    // -------------------------------------------------------------------------

    public void createDataElementGroupSetStructure( List<DataElementGroupSet> groupSets )
    {
        try
        {
            jdbcTemplate.execute( "DROP TABLE IF EXISTS " + CreateDataElementGroupSetTableStatement.TABLE_NAME );
        }
        catch ( BadSqlGrammarException ex )
        {
            // Do nothing, table does not exist
        }
        
        Statement statement = new CreateDataElementGroupSetTableStatement( groupSets, statementBuilder.getColumnQuote() );
        
        jdbcTemplate.execute( statement.getStatement() );
    }

    // -------------------------------------------------------------------------
    // DataElementGroupSetTable
    // -------------------------------------------------------------------------

    public void createIndicatorGroupSetStructure( List<IndicatorGroupSet> groupSets )
    {
        try
        {
            jdbcTemplate.execute( "DROP TABLE IF EXISTS " + CreateIndicatorGroupSetTableStatement.TABLE_NAME );
        }
        catch ( BadSqlGrammarException ex )
        {
            // Do nothing, table does not exist
        }
        
        Statement statement = new CreateIndicatorGroupSetTableStatement( groupSets, statementBuilder.getColumnQuote() );
        
        jdbcTemplate.execute( statement.getStatement() );
    }
    
    // -------------------------------------------------------------------------
    // OrganisationUnitGroupSetTable
    // -------------------------------------------------------------------------

    public void createOrganisationUnitGroupSetStructure( List<OrganisationUnitGroupSet> groupSets )
    {
        try
        {
            jdbcTemplate.execute( "DROP TABLE IF EXISTS " + CreateOrganisationUnitGroupSetTableStatement.TABLE_NAME );
        }
        catch ( BadSqlGrammarException ex )
        {
            // Do nothing, table does not exist
        }
        
        Statement statement = new CreateOrganisationUnitGroupSetTableStatement( groupSets, statementBuilder.getColumnQuote() );
        
        jdbcTemplate.execute( statement.getStatement() );
    }
    
    // -------------------------------------------------------------------------
    // CategoryTable
    // -------------------------------------------------------------------------

    public void createCategoryStructure( List<DataElementCategory> categories )
    {
        try
        {
            jdbcTemplate.execute( "DROP TABLE IF EXISTS " + CreateCategoryTableStatement.TABLE_NAME );
        }
        catch ( BadSqlGrammarException ex )
        {
            // Do nothing, table does not exist
        }
        
        Statement statement = new CreateCategoryTableStatement( categories, statementBuilder.getColumnQuote() );
        
        jdbcTemplate.execute( statement.getStatement() );
    }

    // -------------------------------------------------------------------------
    // DataElementStructure
    // -------------------------------------------------------------------------

    public void createDataElementStructure()
    {
        try
        {
            jdbcTemplate.execute( "DROP TABLE IF EXISTS " + TABLE_NAME_DATA_ELEMENT_STRUCTURE );            
        }
        catch ( BadSqlGrammarException ex )
        {
            // Do nothing, table does not exist
        }
        
        final String sql = "CREATE TABLE " + TABLE_NAME_DATA_ELEMENT_STRUCTURE + 
            " ( dataelementid INTEGER NOT NULL PRIMARY KEY, dataelementname VARCHAR(250), periodtypeid INTEGER, periodtypename VARCHAR(250) )";
        
        log.info( "Create data element structure SQL: " + sql );
        
        jdbcTemplate.execute( sql );        
    }
    
    // -------------------------------------------------------------------------
    // PeriodTable
    // -------------------------------------------------------------------------

    public void createDatePeriodStructure()
    {
        try
        {
            jdbcTemplate.execute( "DROP TABLE IF EXISTS " + TABLE_NAME_DATE_PERIOD_STRUCTURE );            
        }
        catch ( BadSqlGrammarException ex )
        {
            // Do nothing, table does not exist
        }
        
        String quote = statementBuilder.getColumnQuote();
        
        String sql = "CREATE TABLE " + TABLE_NAME_DATE_PERIOD_STRUCTURE + " (dateperiod DATE NOT NULL PRIMARY KEY";
        
        for ( PeriodType periodType : PeriodType.PERIOD_TYPES )
        {
            sql += ", " + quote + periodType.getName().toLowerCase() + quote + " VARCHAR(10)";
        }
        
        sql += ")";
        
        log.info( "Create date period structure SQL: " + sql );
        
        jdbcTemplate.execute( sql );
    }

    public void createPeriodStructure()
    {
        try
        {
            jdbcTemplate.execute( "DROP TABLE IF EXISTS " + TABLE_NAME_PERIOD_STRUCTURE );            
        }
        catch ( BadSqlGrammarException ex )
        {
            // Do nothing, table does not exist
        }

        String quote = statementBuilder.getColumnQuote();
        
        String sql = "CREATE TABLE " + TABLE_NAME_PERIOD_STRUCTURE + " (periodid INTEGER NOT NULL PRIMARY KEY, iso VARCHAR(10) NOT NULL, daysno INTEGER NOT NULL";
        
        for ( PeriodType periodType : PeriodType.PERIOD_TYPES )
        {
            sql += ", " + quote + periodType.getName().toLowerCase() + quote + " VARCHAR(10)";
        }
        
        sql += ")";
        
        log.info( "Create period structure SQL: " + sql );
        
        jdbcTemplate.execute( sql );
    }

    // -------------------------------------------------------------------------
    // DataElementCategoryOptionComboTable
    // -------------------------------------------------------------------------

    public void createAndGenerateDataElementCategoryOptionCombo()
    {
        try
        {
            jdbcTemplate.execute( "DROP TABLE IF EXISTS " + TABLE_NAME_DATA_ELEMENT_CATEGORY_OPTION_COMBO );            
        }
        catch ( BadSqlGrammarException ex )
        {
            // Do nothing, table does not exist
        }
        
        final String create = "CREATE TABLE " + TABLE_NAME_DATA_ELEMENT_CATEGORY_OPTION_COMBO + 
            " (dataelementuid VARCHAR(11) NOT NULL, categoryoptioncombouid VARCHAR(11) NOT NULL)";
        
        jdbcTemplate.execute( create );
        
        log.info( "Create data element category option combo SQL: " + create );
        
        final String sql = 
            "insert into " + TABLE_NAME_DATA_ELEMENT_CATEGORY_OPTION_COMBO + " (dataelementuid, categoryoptioncombouid) " +
            "select de.uid as dataelementuid, coc.uid as categoryoptioncombouid " +
            "from dataelement de " +
            "join categorycombos_optioncombos cc on de.categorycomboid = cc.categorycomboid " +
            "join categoryoptioncombo coc on cc.categoryoptioncomboid = coc.categoryoptioncomboid";
        
        log.info( "Insert data element category option combo SQL: " + sql );
        
        jdbcTemplate.execute( sql );
        
        final String index = "CREATE INDEX dataelement_categoryoptioncombo ON " + 
            TABLE_NAME_DATA_ELEMENT_CATEGORY_OPTION_COMBO + " (dataelementuid, categoryoptioncombouid)";
        
        log.info( "Create data element category option combo index SQL: " + index );

        jdbcTemplate.execute( index );        
    }
}
