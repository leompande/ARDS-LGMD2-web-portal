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

import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.util.Collection;

import org.hisp.dhis.aggregation.AggregatedDataValue;
import org.hisp.dhis.aggregation.AggregatedIndicatorValue;
import org.hisp.dhis.aggregation.AggregatedOrgUnitDataValueStore;
import org.hisp.dhis.system.objectmapper.AggregatedOrgUnitDataValueRowMapper;
import org.hisp.dhis.system.objectmapper.AggregatedOrgUnitIndicatorValueRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcAggregatedOrgUnitDataValueStore
    implements AggregatedOrgUnitDataValueStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private JdbcTemplate jdbcTemplate;
    
    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    // -------------------------------------------------------------------------
    // AggregatedOrgUnitDataValueStore implementation
    // -------------------------------------------------------------------------

    public Double getAggregatedDataValue( int dataElement, int categoryOptionCombo, int period, int organisationUnit, int organisationUnitGroup )
    {
        final String sql =
            "SELECT value " +
            "FROM aggregatedorgunitdatavalue " +
            "WHERE dataelementid = " + dataElement + " " +
            "AND categoryoptioncomboid = " + categoryOptionCombo + " " +
            "AND periodid = " + period + " " +
            "AND organisationunitid = " + organisationUnit + " " +
            "AND organisationunitgroupid = " + organisationUnitGroup;
        
        return jdbcTemplate.queryForObject( sql, Double.class );
    }

    public Collection<AggregatedDataValue> getAggregatedDataValueTotals( Collection<Integer> dataElementIds, 
        Collection<Integer> periodIds, int organisationUnitId, Collection<Integer> organisationUnitGroupIds )
    {
        final String sql = 
            "SELECT dataelementid, 0 as categoryoptioncomboid, periodid, organisationunitid, organisationunitgroupid, SUM(value) as value " +
            "FROM aggregatedorgunitdatavalue " +
            "WHERE dataelementid IN ( " + getCommaDelimitedString( dataElementIds ) + " ) " +
            "AND periodid IN ( " + getCommaDelimitedString( periodIds ) + " ) " +
            "AND organisationunitid = " + organisationUnitId + " " +
            "AND organisationunitgroupid IN ( " + getCommaDelimitedString( organisationUnitGroupIds ) + " ) " +
            "GROUP BY dataelementid, periodid, organisationunitid, organisationunitgroupid";
        
        return jdbcTemplate.query( sql, new AggregatedOrgUnitDataValueRowMapper() );
    }

    public void deleteAggregatedDataValues( Collection<Integer> dataElementIds, Collection<Integer> periodIds, Collection<Integer> organisationUnitIds )
    {
        final String sql =
            "DELETE FROM aggregatedorgunitdatavalue " +
            "WHERE dataelementid IN ( " + getCommaDelimitedString( dataElementIds ) + " ) " +
            "AND periodid IN ( " + getCommaDelimitedString( periodIds ) + " ) " +
            "AND organisationunitid IN ( " + getCommaDelimitedString( organisationUnitIds ) + " )";
        
        jdbcTemplate.execute( sql );
    }

    public void deleteAggregatedDataValues()
    {
        final String sql = "DELETE FROM aggregatedorgunitdatavalue";
        
        jdbcTemplate.execute( sql );
    }

    // -------------------------------------------------------------------------
    // AggregatedIndicatorValue
    // -------------------------------------------------------------------------

    public Double getAggregatedIndicatorValue( int indicator, int period, int organisationUnit, int organisationUnitGroup )
    {
        final String sql =
            "SELECT value " +
            "FROM aggregatedorgunitindicatorvalue " +
            "WHERE indicatorid = " + indicator + " " +
            "AND periodid = " + period + " " +
            "AND organisationunitid = " + organisationUnit + " " +
            "AND organisationunitgroupid = " + organisationUnitGroup;
        
        return jdbcTemplate.queryForObject( sql, Double.class );
    }

    public Collection<AggregatedIndicatorValue> getAggregatedIndicatorValues( Collection<Integer> indicatorIds, 
        Collection<Integer> periodIds, int organisationUnitId, Collection<Integer> organisationUnitGroupIds )
    {
        final String sql =
            "SELECT * " +
            "FROM aggregatedorgunitindicatorvalue " +
            "WHERE indicatorid IN ( " + getCommaDelimitedString( indicatorIds ) + " ) " +
            "AND periodid IN ( " + getCommaDelimitedString( periodIds ) + " ) " +
            "AND organisationunitid = " + organisationUnitId + " " +
            "AND organisationunitgroupid IN ( " + getCommaDelimitedString( organisationUnitGroupIds ) + " )";
        
        return jdbcTemplate.query( sql, new AggregatedOrgUnitIndicatorValueRowMapper() );
    }

    public void deleteAggregatedIndicatorValues( Collection<Integer> indicatorIds, Collection<Integer> periodIds,
        Collection<Integer> organisationUnitIds )
    {
        final String sql =
            "DELETE FROM aggregatedorgunitindicatorvalue " +
            "WHERE indicatorid IN ( " + getCommaDelimitedString( indicatorIds ) + " ) " +
            "AND periodid IN ( " + getCommaDelimitedString( periodIds ) + " ) " +
            "AND organisationunitid IN ( " + getCommaDelimitedString( organisationUnitIds ) + " )";
        
        jdbcTemplate.execute( sql );
    }

    public void deleteAggregatedIndicatorValues()
    {
        final String sql = "DELETE FROM aggregatedorgunitindicatorvalue ";
        
        jdbcTemplate.execute( sql );
    }
}
