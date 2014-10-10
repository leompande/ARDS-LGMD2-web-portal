package org.hisp.dhis.datamart;

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
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElementOperand;

/**
 * @author Lars Helge Overland
 */
public interface DataMartManager
{
    /**
     * Filters and returns the DataElementOperands with data from the given
     * collection of DataElementOperands.
     * 
     * @param operands the DataElementOperands.
     * @return the DataElementOperands with data.
     */
    Set<DataElementOperand> getOperandsWithData( Set<DataElementOperand> operands );

    Map<DataElementOperand, String> getDataValueMap( int periodId, int sourceId );
    
    void createDataValueIndex();
    
    void createIndicatorValueIndex();
    
    void dropDataValueIndex();
    
    void dropIndicatorValueIndex();
    
    /**
     * Deletes AggregatedDataValues registered for the given parameters.
     * 
     * @param periodIds a collection of Period identifiers.
     */
    void deleteAggregatedDataValues( Collection<Integer> periodIds );

    /**
     * Deletes AggregatedIndicatorValue registered for the given parameters.
     * 
     * @param periodIds a collection of Period identifiers.
     */
    void deleteAggregatedIndicatorValues( Collection<Integer> periodIds );
    
    void createOrgUnitDataValueIndex();
    
    void createOrgUnitIndicatorValueIndex();
    
    void dropOrgUnitDataValueIndex();
    
    void dropOrgUnitIndicatorValueIndex();
    
    void deleteAggregatedOrgUnitIndicatorValues( Collection<Integer> periodIds );

    void deleteAggregatedOrgUnitDataValues( Collection<Integer> periodIds );
    
    void createTempAggregatedTables();
    
    void dropTempAggregatedTables();
    
    void copyAggregatedDataValuesFromTemp();
    
    void copyAggregatedIndicatorValuesFromTemp();
    
    void copyAggregatedOrgUnitDataValuesFromTemp();
    
    void copyAggregatedOrgUnitIndicatorValuesFromTemp();
}
