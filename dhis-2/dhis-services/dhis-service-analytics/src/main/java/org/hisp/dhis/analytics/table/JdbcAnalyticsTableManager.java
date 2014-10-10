package org.hisp.dhis.analytics.table;

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

import static org.hisp.dhis.system.util.TextUtils.getQuotedCommaDelimitedString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

import org.hisp.dhis.analytics.AnalyticsTable;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.MathUtils;
import org.springframework.scheduling.annotation.Async;

/**
 * This class manages the analytics table. The analytics table is a denormalized
 * table designed for analysis which contains raw data values. It has columns for
 * each organisation unit group set and organisation unit level. Also, columns
 * for dataelementid, periodid, organisationunitid, categoryoptioncomboid, value.
 * 
 * The analytics table is horizontally partitioned. The partition key is the start 
 * date of the  period of the data record. The table is partitioned according to 
 * time span with one partition per calendar quarter.
 * 
 * The data records in this table are not aggregated. Typically, queries will
 * aggregate in organisation unit hierarchy dimension, in the period/time dimension,
 * and the category dimensions, as well as organisation unit group set dimensions.
 * 
 * @author Lars Helge Overland
 */
public class JdbcAnalyticsTableManager
    extends AbstractJdbcTableManager
{
    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------
    
    public boolean validState()
    {
        return jdbcTemplate.queryForRowSet( "select dataelementid from datavalue limit 1" ).next();
    }
    
    public String getTableName()
    {
        return "analytics";
    }
    
    public void createTable( AnalyticsTable table )
    {
        final String tableName = table.getTempTableName();
        
        final String dbl = statementBuilder.getDoubleColumnType();
        
        final String sqlDrop = "drop table " + tableName;
        
        executeSilently( sqlDrop );
        
        String sqlCreate = "create table " + tableName + " (";
        
        for ( String[] col : getDimensionColumns( table ) )
        {
            sqlCreate += col[0] + " " + col[1] + ",";
        }
        
        sqlCreate += "daysxvalue " + dbl + ", daysno integer not null, value " + dbl + ") ";
        
        sqlCreate += statementBuilder.getTableOptions( false );
        
        log.info( "Create SQL: " + sqlCreate );
        
        executeSilently( sqlCreate );
    }
    
    @Async
    public Future<?> populateTableAsync( ConcurrentLinkedQueue<AnalyticsTable> tables )
    {
        final String dbl = statementBuilder.getDoubleColumnType();
        
        taskLoop : while ( true )
        {
            AnalyticsTable table = tables.poll();
                
            if ( table == null )
            {
                break taskLoop;
            }
            
            String intClause = 
                "dv.value " + statementBuilder.getRegexpMatch() + " '" + MathUtils.NUMERIC_LENIENT_REGEXP + "' " +
                "and ( dv.value != '0' or de.aggregationtype = 'average' or de.zeroissignificant = true ) ";
            
            populateTable( table, "cast(dv.value as " + dbl + ")", "int", intClause );
            
            populateTable( table, "1" , DataElement.VALUE_TYPE_BOOL, "dv.value = 'true'" );
    
            populateTable( table, "0" , DataElement.VALUE_TYPE_BOOL, "dv.value = 'false'" );
            
            populateTable( table, "1" , DataElement.VALUE_TYPE_TRUE_ONLY, "dv.value = 'true'" );
        }
    
        return null;
    }
    
    private void populateTable( AnalyticsTable table, String valueExpression, String valueType, String clause )
    {
        final String start = DateUtils.getMediumDateString( table.getPeriod().getStartDate() );
        final String end = DateUtils.getMediumDateString( table.getPeriod().getEndDate() );
        
        String sql = "insert into " + table.getTempTableName() + " (";
        
        for ( String[] col : getDimensionColumns( table ) )
        {
            sql += col[0] + ",";
        }
        
        sql += "daysxvalue, daysno, value) select ";
        
        for ( String[] col : getDimensionColumns( table ) )
        {
            sql += col[2] + ",";
        }
        
        sql += 
            valueExpression + " * ps.daysno as daysxvalue, " +
            "ps.daysno as daysno, " +
            valueExpression + " as value " +
            "from datavalue dv " +
            "left join _dataelementgroupsetstructure degs on dv.dataelementid=degs.dataelementid " +
            "left join _organisationunitgroupsetstructure ougs on dv.sourceid=ougs.organisationunitid " +
            "left join _categorystructure dcs on dv.categoryoptioncomboid=dcs.categoryoptioncomboid " +
            "left join _categorystructure acs on dv.attributeoptioncomboid=acs.categoryoptioncomboid " +
            "left join _orgunitstructure ous on dv.sourceid=ous.organisationunitid " +
            "left join _periodstructure ps on dv.periodid=ps.periodid " +
            "left join dataelement de on dv.dataelementid=de.dataelementid " +
            "left join categoryoptioncombo co on dv.categoryoptioncomboid=co.categoryoptioncomboid " +
            "left join period pe on dv.periodid=pe.periodid " +
            "where de.valuetype = '" + valueType + "' " +
            "and de.domaintype = 'aggregate' " +
            "and pe.startdate >= '" + start + "' " +
            "and pe.startdate <= '" + end + "' " +
            "and dv.value is not null " + 
            "and " + clause;

        log.info( "Populate SQL: "+ sql );
        
        jdbcTemplate.execute( sql );
    }

    public List<String[]> getDimensionColumns( AnalyticsTable table )
    {
        List<String[]> columns = new ArrayList<String[]>();

        Collection<DataElementGroupSet> dataElementGroupSets =
            dataElementService.getDataDimensionDataElementGroupSets();
        
        Collection<OrganisationUnitGroupSet> orgUnitGroupSets = 
            organisationUnitGroupService.getDataDimensionOrganisationUnitGroupSets();

        Collection<DataElementCategory> disaggregationCategories =
            categoryService.getDisaggregationDataDimensionCategories();
        
        Collection<DataElementCategory> attributeCategories =
            categoryService.getAttributeDataDimensionCategories();

        Collection<OrganisationUnitLevel> levels =
            organisationUnitService.getOrganisationUnitLevels();
        
        for ( DataElementGroupSet groupSet : dataElementGroupSets )
        {
            String[] col = { quote( groupSet.getUid() ), "character(11)", "degs." + quote( groupSet.getUid() ) };
            columns.add( col );
        }
        
        for ( OrganisationUnitGroupSet groupSet : orgUnitGroupSets )
        {
            String[] col = { quote( groupSet.getUid() ), "character(11)", "ougs." + quote( groupSet.getUid() ) };
            columns.add( col );
        }
        
        for ( DataElementCategory category : disaggregationCategories )
        {
            String[] col = { quote( category.getUid() ), "character(11)", "dcs." + quote( category.getUid() ) };
            columns.add( col );
        }
        
        for ( DataElementCategory category : attributeCategories )
        {
            String[] col = { quote( category.getUid() ), "character(11)", "acs." + quote( category.getUid() ) };
            columns.add( col );
        }
        
        for ( OrganisationUnitLevel level : levels )
        {
            String column = quote( PREFIX_ORGUNITLEVEL + level.getLevel() );
            String[] col = { column, "character(11)", "ous." + column };
            columns.add( col );
        }
        
        List<PeriodType> periodTypes = PeriodType.getAvailablePeriodTypes();
        
        for ( PeriodType periodType : periodTypes )
        {
            String column = quote( periodType.getName().toLowerCase() );
            String[] col = { column, "character varying(10)", "ps." + column };
            columns.add( col );
        }
        
        String[] de = { quote( "de" ), "character(11) not null", "de.uid" };
        String[] co = { quote( "co" ), "character(11) not null", "co.uid" };
        String[] level = { quote( "level" ), "integer", "ous.level" };
        
        columns.addAll( Arrays.asList( de, co, level ) );
        
        return columns;
    }
    
    public Date getEarliestData()
    {
        final String sql = "select min(pe.startdate) from datavalue dv " +
            "join period pe on dv.periodid=pe.periodid " +
            "where pe.startdate is not null";
        
        return jdbcTemplate.queryForObject( sql, Date.class );
    }

    public Date getLatestData()
    {
        final String sql = "select max(pe.enddate) from datavalue dv " +
            "join period pe on dv.periodid=pe.periodid " + 
            "where pe.enddate is not null ";
        
        return jdbcTemplate.queryForObject( sql, Date.class );
    }
    
    @Async
    public Future<?> applyAggregationLevels( ConcurrentLinkedQueue<AnalyticsTable> tables, Collection<String> dataElements, int aggregationLevel )
    {
        taskLoop : while ( true )
        {
            AnalyticsTable table = tables.poll();
                
            if ( table == null )
            {
                break taskLoop;
            }
            
            StringBuilder sql = new StringBuilder( "update " + table.getTempTableName() + " set " );
            
            for ( int i = 0; i < aggregationLevel; i++ )
            {
                int level = i + 1;
                
                String column = quote( DataQueryParams.LEVEL_PREFIX + level );
                
                sql.append( column + " = null," );
            }
            
            sql.deleteCharAt( sql.length() - ",".length() );
            
            sql.append( " where level > " + aggregationLevel );
            sql.append( " and de in (" + getQuotedCommaDelimitedString( dataElements ) + ")" );
            
            log.info( "Aggregation level SQL: " + sql.toString() );
            
            jdbcTemplate.execute( sql.toString() );
        }

        return null;
    }
}
