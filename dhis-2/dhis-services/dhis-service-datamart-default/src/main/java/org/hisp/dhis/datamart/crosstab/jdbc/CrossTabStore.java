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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.datamart.CrossTabDataValue;

/**
 * @author Lars Helge Overland
 * @version $Id: CrossTabStore.java 6063 2008-10-28 16:44:12Z larshelg $
 */
public interface CrossTabStore
{
    final String ID = CrossTabStore.class.getName();
    final String CROSSTAB_TABLE_PREFIX = "crosstab_table_";
    final String AGGREGATEDDATA_CACHE_PREFIX = "aggregateddata_cache_";
    final String AGGREGATEDORGUNITDATA_CACHE_PREFIX = "aggregatedorgunitdata_cache_";
    
    /**
     * Creates a crosstab table where the first column is the period identifier,
     * the second column is the source identifer, and each subsequent column
     * corresponds to an operand.
     * 
     * @param operands the DataElementOperands.
     */
    void createCrossTabTable( List<DataElementOperand> operands, String key );

    /**
     * Drops the crosstab table.
     */
    void dropCrossTabTable( String key );

    /**
     * Creates a table which functions as a cache for aggregated data element values
     * with columns for period identifier, organisation unit identifier followed by
     * one column for each DataElementOperand in the given list.
     *  
     * @param operands the list of DataElementOperands.
     * @param key the key to use in table name.
     */
    void createAggregatedDataCache( List<DataElementOperand> operands, String key );

    /**
     * Drops the aggregated data cache table.
     * 
     * @param key the key used in the table name.
     */
    void dropAggregatedDataCache( String key );

    /**
     * Creates a table which functions as a cache for aggregated org unit data 
     * element values with columns for period identifier, organisation unit 
     * identifier, organisation unit group identifier followed by one column for 
     * each DataElementOperand in the given list.
     *  
     * @param operands the list of DataElementOperands.
     * @param key the key to use in table name.
     */
    void createAggregatedOrgUnitDataCache( List<DataElementOperand> operands, String key );

    /**
     * Drops the aggregated org unit data cache table.
     * 
     * @param key the key used in the table name.
     */
    void dropAggregatedOrgUnitDataCache( String key );
    
    /**
     * Gets all CrossTabDataValues for the given collection of period ids and source ids.
     * 
     * @param dataElementIds the data element identifiers.
     * @param periodIds the period identifiers.
     * @param sourceIds the source identifiers.
     * @return collection of CrossTabDataValues.
     */
    Collection<CrossTabDataValue> getCrossTabDataValues( Collection<DataElementOperand> operands, Collection<Integer> periodIds, 
        Collection<Integer> sourceIds, String key );

    /**
     * Gets a map of DataElementOperands and corresponding Double aggregated data
     * element value from the cache table.
     * 
     * @param operands the list of DataElementOperand to return map entries for.
     * @param periodId the period identifier.
     * @param sourceId the organisation unit identifier.
     * @param key the key to use in the table name.
     * @return a map of DataElementOperands and aggregated values.
     */
    Map<DataElementOperand, Double> getAggregatedDataCacheValue( Collection<DataElementOperand> operands, 
        int periodId, int sourceId, String key );

    /**
     * Gets a map of DataElementOperands and corresponding Double aggregated data
     * element value from the cache table.
     * 
     * @param operands the list of DataElementOperand to return map entries for.
     * @param periodId the period identifier.
     * @param sourceId the organisation unit identifier.
     * @param organisationUnitGroupId the organisation unit group identifier.
     * @param key the key to use in the table name.
     * @return a map of DataElementOperands and aggregated values.
     */
    Map<DataElementOperand, Double> getAggregatedOrgUnitDataCacheValue( Collection<DataElementOperand> operands, 
        int periodId, int sourceId, int organisationUnitGroupId, String key );
}
