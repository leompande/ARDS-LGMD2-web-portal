package org.hisp.dhis.aggregation.jdbc;

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

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.amplecode.quick.mapper.RowMapper;
import org.hisp.dhis.aggregation.AggregatedDataValue;
import org.hisp.dhis.aggregation.AggregatedDataValueStore;
import org.hisp.dhis.aggregation.AggregatedIndicatorValue;
import org.hisp.dhis.aggregation.StoreIterator;
import org.hisp.dhis.completeness.DataSetCompletenessResult;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.system.objectmapper.AggregatedDataSetCompletenessRowMapper;
import org.hisp.dhis.system.objectmapper.AggregatedDataValueRowMapper;
import org.hisp.dhis.system.objectmapper.AggregatedIndicatorValueRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Lars Helge Overland
 */
public class JdbcAggregatedDataValueStore
    implements AggregatedDataValueStore
{
    private int FETCH_SIZE = 1000; // Number of rows to fetch from db for large resultset

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
    
    private StatementManager statementManager; //TODO remove

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    // -------------------------------------------------------------------------
    // AggregatedDataValue
    // -------------------------------------------------------------------------
    
    public Double getAggregatedDataValue( int dataElement, int period, int organisationUnit )
    {
        final String sql = 
            "SELECT SUM(value) " +
            "FROM aggregateddatavalue " +
            "WHERE dataelementid = " + dataElement + " " +
            "AND periodid = " + period + " " +
            "AND organisationunitid = " + organisationUnit;
        
        return jdbcTemplate.queryForObject( sql, Double.class );
    }

    public Double getAggregatedDataValue( int dataElement, int categoryOptionCombo, int period, int organisationUnit )
    {
        final String sql =
            "SELECT value " +
            "FROM aggregateddatavalue " +
            "WHERE dataelementid = " + dataElement + " " +
            "AND categoryoptioncomboid = " + categoryOptionCombo + " " +
            "AND periodid = " + period + " " +
            "AND organisationunitid = " + organisationUnit;
        
        return jdbcTemplate.queryForObject( sql, Double.class );
    }

    public Double getAggregatedDataValue( int dataElement, int categoryOptionCombo, Collection<Integer> periodIds, int organisationUnit )
    {
        final String sql =
            "SELECT SUM(value) " +
            "FROM aggregateddatavalue " +
            "WHERE dataelementid = " + dataElement + " " +
            "AND categoryoptioncomboid = " + categoryOptionCombo + " " +
            "AND periodid IN ( " + getCommaDelimitedString( periodIds ) + " ) " +
            "AND organisationunitid = " + organisationUnit;
        
        return jdbcTemplate.queryForObject( sql, Double.class );
    }
    
    public Double getAggregatedDataValue( DataElement dataElement, DataElementCategoryOption categoryOption, Period period, OrganisationUnit organisationUnit )
    {
        String ids = getCommaDelimitedString( getIdentifiers( DataElementCategoryOptionCombo.class, categoryOption.getCategoryOptionCombos() ) );
        
        final String sql =
            "SELECT SUM(value) " +
            "FROM aggregateddatavalue " +
            "WHERE dataelementid = " + dataElement.getId() + " " +
            "AND categoryoptioncomboid IN (" + ids + ") " +
            "AND periodid = " + period.getId() + " " +
            "AND organisationunitid = " + organisationUnit.getId();

        return jdbcTemplate.queryForObject( sql, Double.class );
    }

    public Collection<AggregatedDataValue> getAggregatedDataValues( Collection<Integer> periodIds, Collection<Integer> organisationUnitIds )
    {
        final String sql = 
            "SELECT * " +
            "FROM aggregateddatavalue " +
            "WHERE periodid IN ( " + getCommaDelimitedString( periodIds ) + " ) " +
            "AND organisationunitid IN ( " + getCommaDelimitedString( organisationUnitIds ) + " )";
        
        return jdbcTemplate.query( sql, new AggregatedDataValueRowMapper() );
    }
    
    public Collection<AggregatedDataValue> getAggregatedDataValueTotals( Collection<Integer> periodIds, Collection<Integer> organisationUnitIds )
    {
        final String sql = 
            "SELECT dataelementid, 0 as categoryoptioncomboid, periodid, organisationunitid, SUM(value) as value " +
            "FROM aggregateddatavalue " +
            "WHERE periodid IN ( " + getCommaDelimitedString( periodIds ) + " ) " +
            "AND organisationunitid IN ( " + getCommaDelimitedString( organisationUnitIds ) + " ) " +
            "GROUP BY dataelementid, periodid, organisationunitid";
        
        return jdbcTemplate.query( sql, new AggregatedDataValueRowMapper() );
    }

    public Collection<AggregatedDataValue> getAggregatedDataValues( int dataElementId, 
        Collection<Integer> periodIds, Collection<Integer> organisationUnitIds )
    {
        final String sql = 
            "SELECT * " +
            "FROM aggregateddatavalue " +
            "WHERE dataelementid = " + dataElementId + " " +
            "AND periodid IN ( " + getCommaDelimitedString( periodIds ) + " ) " +
            "AND organisationunitid IN ( " + getCommaDelimitedString( organisationUnitIds ) + " )";
        
        return jdbcTemplate.query( sql, new AggregatedDataValueRowMapper() );
    }

    public Collection<AggregatedDataValue> getAggregatedDataValues( Collection<Integer> dataElementIds, Collection<Integer> periodIds, Collection<Integer> organisationUnitIds )
    {
        final String sql = 
            "SELECT * " +
            "FROM aggregateddatavalue " +
            "WHERE dataelementid IN ( " + getCommaDelimitedString( dataElementIds ) + " ) " +
            "AND periodid IN ( " + getCommaDelimitedString( periodIds ) + " ) " +
            "AND organisationunitid IN ( " + getCommaDelimitedString( organisationUnitIds ) + " )";
        
        return jdbcTemplate.query( sql, new AggregatedDataValueRowMapper() );        
    }

    public Collection<AggregatedDataValue> getAggregatedDataValueTotals( Collection<Integer> dataElementIds, Collection<Integer> periodIds, Collection<Integer> organisationUnitIds )
    {
        final String sql = 
            "SELECT dataelementid, 0 as categoryoptioncomboid, periodid, organisationunitid, SUM(value) as value " +
            "FROM aggregateddatavalue " +
            "WHERE dataelementid IN ( " + getCommaDelimitedString( dataElementIds ) + " ) " +
            "AND periodid IN ( " + getCommaDelimitedString( periodIds ) + " ) " +
            "AND organisationunitid IN ( " + getCommaDelimitedString( organisationUnitIds ) + " ) " +
            "GROUP BY dataelementid, periodid, organisationunitid";
        
        return jdbcTemplate.query( sql, new AggregatedDataValueRowMapper() );
    }

    public StoreIterator<AggregatedDataValue> getAggregatedDataValuesAtLevel( OrganisationUnit rootOrgunit, OrganisationUnitLevel level, Collection<Period> periods )
    {
        final StatementHolder holder = statementManager.getHolder( false );

        try
        {
            int rootlevel = rootOrgunit.getLevel();

            String periodids = getCommaDelimitedString( getIdentifiers(Period.class, periods));

            final String sql =
                "SELECT dataelementid, categoryoptioncomboid, periodid, adv.organisationunitid, value " +
                "FROM aggregateddatavalue AS adv " +
                "INNER JOIN _orgunitstructure AS ous on adv.organisationunitid=ous.organisationunitid " +
                "WHERE adv.level = " + level.getLevel() +
                " AND ous.idlevel" + rootlevel + "=" + rootOrgunit.getId() +
                " AND adv.periodid IN (" + periodids + ") ";

            Statement statement = holder.getStatement();

            statement.setFetchSize( FETCH_SIZE );

            final ResultSet resultSet = statement.executeQuery( sql );

            RowMapper<AggregatedDataValue> rm = new AggregatedDataValueRowMapper();
            
            return new JdbcStoreIterator<AggregatedDataValue>( resultSet, holder, rm );
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to get aggregated data values", ex );
        }
        finally
        {
            // Don't close holder or we lose resultset - iterator must close
        }
    }

    public int countDataValuesAtLevel( OrganisationUnit rootOrgunit, OrganisationUnitLevel level, Collection<Period> periods )
    {
        final String periodids = getCommaDelimitedString( getIdentifiers( Period.class, periods ) );

        final String sql =
            "SELECT count(*) " +
            "FROM aggregateddatavalue AS adv " +
            "INNER JOIN _orgunitstructure AS ous on adv.organisationunitid=ous.organisationunitid " +
            "WHERE adv.level = " + level.getLevel() +
            " AND ous.idlevel" + rootOrgunit.getLevel() + "=" + rootOrgunit.getId() +
            " AND adv.periodid IN (" + periodids + ") ";

        return jdbcTemplate.queryForObject( sql, Integer.class );
    }

    public void deleteAggregatedDataValues( Collection<Integer> dataElementIds, Collection<Integer> periodIds, Collection<Integer> organisationUnitIds )
    {
        final String sql =
            "DELETE FROM aggregateddatavalue " +
            "WHERE dataelementid IN ( " + getCommaDelimitedString( dataElementIds ) + " ) " +
            "AND periodid IN ( " + getCommaDelimitedString( periodIds ) + " ) " +
            "AND organisationunitid IN ( " + getCommaDelimitedString( organisationUnitIds ) + " )";
        
        jdbcTemplate.execute( sql );
    }

    public void deleteAggregatedDataValues()
    {
        final String sql = "DELETE FROM aggregateddatavalue";
        
        jdbcTemplate.execute( sql );
    }
    
    // -------------------------------------------------------------------------
    // AggregatedIndicatorValue
    // -------------------------------------------------------------------------

    public Double getAggregatedIndicatorValue( int indicator, int period, int organisationUnit )
    {
        final String sql =
            "SELECT value " +
            "FROM aggregatedindicatorvalue " +
            "WHERE indicatorid = " + indicator + " " +
            "AND periodid = " + period + " " +
            "AND organisationunitid = " + organisationUnit;
        
        return jdbcTemplate.queryForObject( sql, Double.class );
    }

    public Collection<AggregatedIndicatorValue> getAggregatedIndicatorValues( Collection<Integer> periodIds, Collection<Integer> organisationUnitIds )
    {
        final String sql =
            "SELECT * " +
            "FROM aggregatedindicatorvalue " +
            "WHERE periodid IN ( " + getCommaDelimitedString( periodIds ) + " ) " +
            "AND organisationunitid IN ( " + getCommaDelimitedString( organisationUnitIds ) + " )";
        
        return jdbcTemplate.query( sql, new AggregatedIndicatorValueRowMapper() );
    }

    public Collection<AggregatedIndicatorValue> getAggregatedIndicatorValues( Collection<Integer> indicatorIds, 
        Collection<Integer> periodIds, Collection<Integer> organisationUnitIds )
    {
        final String sql =
            "SELECT * " +
            "FROM aggregatedindicatorvalue " +
            "WHERE indicatorid IN ( " + getCommaDelimitedString( indicatorIds ) + " ) " +
            "AND periodid IN ( " + getCommaDelimitedString( periodIds ) + " ) " +
            "AND organisationunitid IN ( " + getCommaDelimitedString( organisationUnitIds ) + " )";
        
        return jdbcTemplate.query( sql, new AggregatedIndicatorValueRowMapper() );
    }

    public void deleteAggregatedIndicatorValues( Collection<Integer> indicatorIds, Collection<Integer> periodIds,
        Collection<Integer> organisationUnitIds )
    {
        final String sql =
            "DELETE FROM aggregatedindicatorvalue " +
            "WHERE indicatorid IN ( " + getCommaDelimitedString( indicatorIds ) + " ) " +
            "AND periodid IN ( " + getCommaDelimitedString( periodIds ) + " ) " +
            "AND organisationunitid IN ( " + getCommaDelimitedString( organisationUnitIds ) + " )";
        
        jdbcTemplate.execute( sql );
    }

    public void deleteAggregatedIndicatorValues()
    {
        final String sql = "DELETE FROM aggregatedindicatorvalue";
        
        jdbcTemplate.execute( sql );
    }

    @Override
    public StoreIterator<AggregatedIndicatorValue> getAggregatedIndicatorValuesAtLevel( OrganisationUnit rootOrgunit, OrganisationUnitLevel level, Collection<Period> periods )
    {
        final StatementHolder holder = statementManager.getHolder( false );

        try
        {
            int rootlevel = rootOrgunit.getLevel();

            String periodids = getCommaDelimitedString( getIdentifiers(Period.class, periods));

            final String sql =
                "SELECT aiv.* " +
                "FROM aggregatedindicatorvalue AS aiv " +
                "INNER JOIN _orgunitstructure AS ous on aiv.organisationunitid=ous.organisationunitid " +
                "WHERE aiv.level = " + level.getLevel() +
                " AND ous.idlevel" + rootlevel + "=" + rootOrgunit.getId() +
                " AND aiv.periodid IN (" + periodids + ") ";

            Statement statement = holder.getStatement();

            statement.setFetchSize( FETCH_SIZE );

            final ResultSet resultSet = statement.executeQuery( sql );

            RowMapper<AggregatedIndicatorValue> rm = new AggregatedIndicatorValueRowMapper();
            
            return new JdbcStoreIterator<AggregatedIndicatorValue>( resultSet, holder, rm );
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to get aggregated indicator values", ex );
        }
        finally
        {
            // don't close holder or we lose resultset - iterator must close
        }
    }

    @Override
    public int countIndicatorValuesAtLevel( OrganisationUnit rootOrgunit, OrganisationUnitLevel level, Collection<Period> periods )
    {
        int rootlevel = rootOrgunit.getLevel();

        String periodids = getCommaDelimitedString( getIdentifiers(Period.class, periods));
        
        final String sql =
            "SELECT count(*) as rowcount " +
            "FROM aggregatedindicatorvalue AS aiv " +
            "INNER JOIN _orgunitstructure AS ous on aiv.organisationunitid=ous.organisationunitid " +
            "WHERE aiv.level = " + level.getLevel() +
            " AND ous.idlevel" + rootlevel + "=" + rootOrgunit.getId() +
            " AND aiv.periodid IN (" + periodids + ") ";
        
        return jdbcTemplate.queryForObject( sql, Integer.class );
    }

    // -------------------------------------------------------------------------
    // DataSetCompleteness
    // -------------------------------------------------------------------------

    public Collection<DataSetCompletenessResult> getAggregatedDataSetCompleteness( Collection<Integer> dataSetIds, Collection<Integer> periodIds,
        Collection<Integer> organisationUnitIds )
    {
        final String sql =
            "SELECT datasetid, periodid, organisationunitid, value " +
            "FROM aggregateddatasetcompleteness " +
            "WHERE datasetid IN ( " + getCommaDelimitedString( dataSetIds ) + " ) " +
            "AND periodid IN ( " + getCommaDelimitedString( periodIds ) + " ) " +
            "AND organisationunitid IN ( " + getCommaDelimitedString( organisationUnitIds ) + " ) ";
        
        return jdbcTemplate.query( sql, new AggregatedDataSetCompletenessRowMapper() );
    }

    public void dropDataMart()
    {
        executeSilently( "drop table aggregateddatavalue" );
        executeSilently( "drop table aggregatedorgunitdatavalue" );
        executeSilently( "drop table aggregatedindicatorvalue" );
        executeSilently( "drop table aggregatedorgunitindicatorvalue" );
        executeSilently( "drop table aggregateddatasetcompleteness" );
        executeSilently( "drop table aggregatedorgunitdatasetcompleteness" );
    }
    
    public void createDataMart()
    {
        executeSilently( statementBuilder.getCreateAggregatedDataValueTable( false ) );
        executeSilently( statementBuilder.getCreateAggregatedOrgUnitDataValueTable( false ) );
        executeSilently( statementBuilder.getCreateAggregatedIndicatorTable( false ) );
        executeSilently( statementBuilder.getCreateAggregatedOrgUnitIndicatorTable( false ) );
        executeSilently( statementBuilder.getCreateDataSetCompletenessTable() );
        executeSilently( statementBuilder.getCreateOrgUnitDataSetCompletenessTable() );        
    }
    
    // -------------------------------------------------------------------------
    // Supportive
    // -------------------------------------------------------------------------

    private void executeSilently( final String sql )
    {
        try
        {
            jdbcTemplate.execute( sql );
        }
        catch ( Exception ex )
        {
            // Ignore
        }
    }
}
