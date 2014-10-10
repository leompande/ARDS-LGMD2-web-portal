package org.hisp.dhis.datamart.crosstab.jdbc;

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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.datamart.CrossTabDataValue;

/**
 * @author Lars Helge Overland
 */
public class JDBCCrossTabStore
    implements CrossTabStore
{    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StatementManager statementManager;
    
    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }
    
    // -------------------------------------------------------------------------
    // CrossTabStore implementation
    // -------------------------------------------------------------------------

    public void createCrossTabTable( List<DataElementOperand> operands, String key )
    {
        final StringBuffer sql = new StringBuffer( "CREATE TABLE " + CROSSTAB_TABLE_PREFIX + key + " ( " );
        
        sql.append( "periodid INTEGER NOT NULL, " );
        sql.append( "sourceid INTEGER NOT NULL, " );
        
        for ( DataElementOperand operand : operands )
        {
            sql.append( operand.getColumnName() ).append( " VARCHAR(20), " );
        }
        
        sql.append( "PRIMARY KEY ( periodid, sourceid ) );" );
        
        statementManager.getHolder().executeUpdate( sql.toString() );
    }

    public void dropCrossTabTable( String key )
    {
        statementManager.getHolder().executeUpdate( "DROP TABLE IF EXISTS " + CROSSTAB_TABLE_PREFIX + key );
    }

    public void createAggregatedDataCache( List<DataElementOperand> operands, String key )
    {
        final StringBuffer sql = new StringBuffer( "CREATE TABLE " + AGGREGATEDDATA_CACHE_PREFIX + key + " ( " );
        
        sql.append( "periodid INTEGER NOT NULL, " );
        sql.append( "sourceid INTEGER NOT NULL, " );
        
        for ( DataElementOperand operand : operands )
        {
            sql.append( operand.getColumnName() ).append( " DOUBLE, " );
        }
        
        sql.append( "PRIMARY KEY ( periodid, sourceid ) );" );
        
        statementManager.getHolder().executeUpdate( sql.toString() );
    }
    
    public void dropAggregatedDataCache( String key )
    {
        statementManager.getHolder().executeUpdate( "DROP TABLE IF EXISTS " + AGGREGATEDDATA_CACHE_PREFIX + key );
    }

    public void createAggregatedOrgUnitDataCache( List<DataElementOperand> operands, String key )
    {
        final StringBuffer sql = new StringBuffer( "CREATE TABLE " + AGGREGATEDORGUNITDATA_CACHE_PREFIX + key + " ( " );
        
        sql.append( "periodid INTEGER NOT NULL, " );
        sql.append( "sourceid INTEGER NOT NULL, " );
        sql.append( "organisationunitgroupid INTEGER NOT NULL, " );
        
        for ( DataElementOperand operand : operands )
        {
            sql.append( operand.getColumnName() ).append( " DOUBLE, " );
        }
        
        sql.append( "PRIMARY KEY ( periodid, sourceid, organisationunitgroupid ) );" );
        
        statementManager.getHolder().executeUpdate( sql.toString() );
    }
    
    public void dropAggregatedOrgUnitDataCache( String key )
    {
        statementManager.getHolder().executeUpdate( "DROP TABLE IF EXISTS " + AGGREGATEDORGUNITDATA_CACHE_PREFIX + key );
    }
    
    // -------------------------------------------------------------------------
    // CrossTabDataValue
    // -------------------------------------------------------------------------

    public Collection<CrossTabDataValue> getCrossTabDataValues( Collection<DataElementOperand> operands, 
        Collection<Integer> periodIds, Collection<Integer> sourceIds, String key )
    {
        final StatementHolder holder = statementManager.getHolder();
        
        if ( operands.isEmpty() || periodIds.isEmpty() || sourceIds.isEmpty() )
        {
            return new ArrayList<CrossTabDataValue>();
        }
        
        final String sql = "SELECT periodid, sourceid, " + getCommadelimitedString( operands ) + 
            " FROM " + CROSSTAB_TABLE_PREFIX + key + " AS c WHERE c.periodid IN (" + 
            getCommaDelimitedString( periodIds ) + ") AND c.sourceid IN (" + getCommaDelimitedString( sourceIds ) + ")";
        
        try
        {            
            final ResultSet resultSet = holder.getStatement().executeQuery( sql );
            
            return getCrossTabDataValues( resultSet, operands );
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to get CrossTabDataValues", ex );
        }
        finally
        {
            holder.close();
        }
    }
        
    public Map<DataElementOperand, Double> getAggregatedDataCacheValue( Collection<DataElementOperand> operands, 
        int periodId, int sourceId, String key )
    {
        final StatementHolder holder = statementManager.getHolder();
        
        // TODO use prepared statement?
        
        final String sql = "SELECT * FROM " + AGGREGATEDDATA_CACHE_PREFIX + key + 
            " AS a WHERE a.periodid = " + periodId + " AND a.sourceid = " + sourceId;
        
        try
        {
            final ResultSet resultSet = holder.getStatement().executeQuery( sql );
            
            return getOperandValueMap( resultSet, operands );
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to get Map", ex );
        }
        finally
        {
            holder.close();
        }
    }

    public Map<DataElementOperand, Double> getAggregatedOrgUnitDataCacheValue( Collection<DataElementOperand> operands, 
        int periodId, int sourceId, int organisationUnitGroupId, String key )
    {
        final StatementHolder holder = statementManager.getHolder();
        
        // TODO use prepared statement?
        
        final String sql = "SELECT * FROM " + AGGREGATEDORGUNITDATA_CACHE_PREFIX + key + 
            " AS a WHERE a.periodid = " + periodId + " AND a.sourceid = " + sourceId + " AND a.organisationunitgroupid = " + organisationUnitGroupId;
        
        try
        {
            final ResultSet resultSet = holder.getStatement().executeQuery( sql );
            
            return getOperandValueMap( resultSet, operands );
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to get Map", ex );
        }
        finally
        {
            holder.close();
        }
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Collection<CrossTabDataValue> getCrossTabDataValues( ResultSet resultSet, Collection<DataElementOperand> operands )
        throws SQLException
    {
        final List<CrossTabDataValue> values = new ArrayList<CrossTabDataValue>();
        
        while ( resultSet.next() )
        {
            final CrossTabDataValue value = new CrossTabDataValue();
            
            value.setPeriodId( resultSet.getInt( 1 ) );
            value.setSourceId( resultSet.getInt( 2 ) );
            
            for ( DataElementOperand operand : operands )
            {
                final String columnValue = resultSet.getString( operand.getColumnName() );
                
                if ( columnValue != null )
                {
                    value.getValueMap().put( operand, columnValue );
                }
            }
            
            values.add( value );
        }
        
        return values;
    }
    
    private Map<DataElementOperand, Double> getOperandValueMap( ResultSet resultSet, Collection<DataElementOperand> operands )
        throws SQLException
    {
        final Map<DataElementOperand, Double> valueMap = new HashMap<DataElementOperand, Double>( operands.size() );
        
        if ( resultSet.next() )
        { 
            for ( DataElementOperand operand : operands )
            {       
                final Double columnValue = resultSet.getDouble( operand.getColumnName() );
                
                if ( columnValue != null )
                {
                    valueMap.put( operand, columnValue );
                }
            }
        }
        
        return valueMap;
    }
    
    private String getCommadelimitedString( Collection<DataElementOperand> operands )
    {
        final StringBuilder builder = new StringBuilder();
        
        for ( DataElementOperand operand : operands )
        {
            builder.append( operand.getColumnName() ).append( "," );
        }
        
        if ( builder.length() > 0 )
        {
            builder.deleteCharAt( builder.length() - 1);
        }
        
        return builder.toString();
    }
}
