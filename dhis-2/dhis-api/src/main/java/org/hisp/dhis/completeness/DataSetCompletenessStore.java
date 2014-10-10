package org.hisp.dhis.completeness;

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

import org.hisp.dhis.dataset.DataSet;

import java.util.Collection;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public interface DataSetCompletenessStore
{
    String ID = DataSetCompletenessStore.class.getName();

    Integer getCompleteDataSetRegistrations( DataSet dataSet, Collection<Integer> periods, Collection<Integer> relevantSources );

    Integer getCompleteDataSetRegistrationsWithTimeliness( DataSet dataSet, Collection<Integer> periods, Collection<Integer> relevantSources );

    Integer getCompulsoryDataElementRegistrations( DataSet dataSet, Collection<Integer> children, Collection<Integer> periods );

    Integer getCompulsoryDataElementRegistrations( DataSet dataSet, Collection<Integer> children, Collection<Integer> periods, int completenessOffset );

    Collection<DataSet> getDataSetsWithRegistrations( Collection<DataSet> dataSets );

    /**
     * Gets the percentage value for the datasetcompleteness with the given parameters.
     *
     * @param dataSetId the DataSet identifier.
     * @param periodId  the Period identifier.
     * @param sourceId  the Source identifier.
     * @return the percentage value for the datasetcompleteness result with the given parameters.
     */
    Double getPercentage( int dataSetId, int periodId, int sourceId );

    /**
     * Deletes the datasetcompleteness entries with the given parameters.
     *
     * @param dataSetIds the DataSet identifiers.
     * @param periodIds  the Period identifiers.
     * @param sourceIds  the Source identifiers.
     */
    void deleteDataSetCompleteness( Collection<Integer> dataSetIds, Collection<Integer> periodIds, Collection<Integer> sourceIds );

    /**
     * Deletes all datasetcompleteness entries.
     */
    void deleteDataSetCompleteness();

    void createIndex();

    void dropIndex();
}
