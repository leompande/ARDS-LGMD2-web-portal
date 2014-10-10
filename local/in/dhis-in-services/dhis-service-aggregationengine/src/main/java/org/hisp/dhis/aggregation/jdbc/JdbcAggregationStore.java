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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.hisp.dhis.aggregation.AggregationStore;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.period.Period;

import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

/**
 * @author Lars Helge Overland
 * @version $Id: JdbcAggregationStore.java 5942 2008-10-16 15:44:57Z larshelg $
 */
public class JdbcAggregationStore
    implements AggregationStore
{
    // ----------------------------------------------------------------------
    // Dependencies
    // ----------------------------------------------------------------------

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }
    
    // ----------------------------------------------------------------------
    // DataValue
    // ----------------------------------------------------------------------
    
    public Collection<DataValue> getDataValues( Collection<Integer> sourceIds, Integer dataElementId, Integer optionComboId, Collection<Integer> periodIds )
    {
        if ( sourceIds != null && sourceIds.size() > 0 && periodIds != null && periodIds.size() > 0 )
        {
            final StatementHolder holder = statementManager.getHolder();
            
            final String categoryOptionComboCriteria = optionComboId != null ? "AND categoryoptioncomboid = " + optionComboId + " " : "";
            
            try
            {
                String sql = 
                    "SELECT periodid, value " +
                    "FROM datavalue " +
                    "WHERE dataelementid = " + dataElementId + " " +
                    categoryOptionComboCriteria +
                    "AND periodid IN ( " + getCommaDelimitedString( periodIds ) + " ) " +
                    "AND sourceid IN ( " + getCommaDelimitedString( sourceIds ) + " )";                 
                
                final ResultSet resultSet = holder.getStatement().executeQuery( sql );
                
                return getDataValues( resultSet );             
            }
            catch ( Exception ex )
            {
                throw new RuntimeException( "Failed to get DataValues", ex );
            }
            finally
            {
                holder.close();            	
            }
        }
        
        return new ArrayList<DataValue>();
    }
    
    public Collection<DataValue> getDataValues( Integer sourceId, Integer dataElementId, Integer optionComboId, Collection<Integer> periodIds )
    {
        if ( periodIds != null && periodIds.size() > 0 )
        {
            final StatementHolder holder = statementManager.getHolder();

            final String categoryOptionComboCriteria = optionComboId != null ? "AND categoryoptioncomboid = " + optionComboId + " " : "";
            
            try
            {
                String sql = 
                    "SELECT periodid, value " +
                    "FROM datavalue " +
                    "WHERE dataelementid = " + dataElementId + " " +
                    categoryOptionComboCriteria +
                    "AND periodid IN ( " + getCommaDelimitedString( periodIds ) + " ) " +
                    "AND sourceid = " + sourceId;

                final ResultSet resultSet = holder.getStatement().executeQuery( sql );
                
                return getDataValues( resultSet );
            }
            catch ( SQLException ex )
            {
                throw new RuntimeException( "Failed to get DataValues", ex );
            }
            finally
            {
                holder.close();
            }
        }
        
        return new ArrayList<DataValue>();
    }

    // ----------------------------------------------------------------------
    // Supportive methods
    // ----------------------------------------------------------------------
    
    private Collection<DataValue> getDataValues( ResultSet resultSet )
    {
        try
        {
            final Collection<DataValue> list = new ArrayList<DataValue>();
            
            while ( resultSet.next() )
            {
                final Period period = new Period();
                
                period.setId( Integer.parseInt( resultSet.getString( 1 ) ) );
                
                final DataValue dataValue = new DataValue();
                
                dataValue.setPeriod( period );
                dataValue.setValue( resultSet.getString( 2 ) );
                
                list.add( dataValue );
            }
            
            return list;
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to transform resultset into collection", ex );
        }
    }
}
