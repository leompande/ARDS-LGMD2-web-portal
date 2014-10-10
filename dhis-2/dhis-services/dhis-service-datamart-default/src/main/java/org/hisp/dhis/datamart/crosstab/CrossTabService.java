package org.hisp.dhis.datamart.crosstab;

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
import java.util.concurrent.Future;

import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.datamart.CrossTabDataValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;

/**
 * @author Lars Helge Overland
 */
public interface CrossTabService
{
    String ID = CrossTabService.class.getName();

    String createCrossTabTable( List<DataElementOperand> operands );
    
    /**
     * Creates and populates the crosstab table. Operands without data will be
     * removed from the operands argument collection.
     * 
     * @param operands the list of DataElementOperands.
     * @param periodIds the collection of Period identifiers.
     * @param organisationUnitIds the collection of OrganisationUnit identifiers.
     * @return a List of random keys for each generated crosstab table. 
     */
    Future<?> populateCrossTabTable( List<DataElementOperand> operands, 
        Collection<Integer> periodIds, Collection<Integer> organisationUnitIds, String key );

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
     * element value from the cache table. If the group argument is not null it
     * will read from the aggregated org unit data cache, if null it will read from
     * the aggregated data cache.
     * 
     * @param operands the list of DataElementOperand to return map entries for.
     * @param period the Period.
     * @param unit the OrganisationUnit.
     * @param group the OrganisationUnitGroup.
     * @param key the key to use in the table name.
     * @return a map of DataElementOperands and aggregated values.
     */
    Map<DataElementOperand, Double> getAggregatedDataCacheValue( Collection<DataElementOperand> operands, 
        Period period, OrganisationUnit unit, OrganisationUnitGroup group, String key );
}
