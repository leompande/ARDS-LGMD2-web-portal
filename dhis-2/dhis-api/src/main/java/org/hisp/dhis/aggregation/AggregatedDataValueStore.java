package org.hisp.dhis.aggregation;

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

import org.hisp.dhis.completeness.DataSetCompletenessResult;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.Period;

/**
 * @author Lars Helge Overland
 */
public interface AggregatedDataValueStore
{
    String ID = AggregatedDataValueStore.class.getName();
    
    // ----------------------------------------------------------------------
    // AggregatedDataValue
    // ----------------------------------------------------------------------
    
    /**
     * Gets the total aggregated value from the datamart table for the given parameters.
     * 
     * @param dataElement The DataElement identifier.
     * @param period The Period identifier.
     * @param organisationUnit The OrganisationUnit identifier.
     * @return the aggregated value.
     */
    Double getAggregatedDataValue( int dataElement, int period, int organisationUnit );

    /**
     * Gets the aggregated value from the datamart table for the given parameters.
     * 
     * @param dataElement The DataElement.
     * @param categoryOptionCombo The DataElementCategoryOptionCombo.
     * @param period The Period.
     * @param organisationUnit The OrganisationUnit.
     * @return the aggregated value, or -1 if no value exists.
     */
    Double getAggregatedDataValue( int dataElement, int categoryOptionCombo, int period, int organisationUnit );
    
    /**
     * Gets the total aggregated value from the datamart table for the given parameters.
     * 
     * @param dataElement The DataElement.
     * @param categoryOption the DataElementCategoryOption.
     * @param period The Period.
     * @param organisationUnit The OrganisationUnit.
     * @return the aggregated value.
     */
    Double getAggregatedDataValue( DataElement dataElement, DataElementCategoryOption categoryOption, Period period, OrganisationUnit organisationUnit );
    
    /**
     * Gets the aggregated value from the datamart table for the given parameters.
     * 
     * @param dataElement The DataElement identifier.
     * @param categoryOptionCombo The DataElementCategoryOptionCombo identifier.
     * @param periods The collection of Periods.
     * @param organisationUnit The OrganisationUnit identifier.
     * @return the aggregated value.
     */
    Double getAggregatedDataValue( int dataElement, int categoryOptionCombo, Collection<Integer> periodIds, int organisationUnit );

    /**
     * Gets a collection of AggregatedDataValues.
     * 
     * @param periodIds the collection of Period identifiers.
     * @param organisationUnitIds the collection of OrganisationUnit identifiers.
     * @return a collection of AggregatedDataValues.
     */
    Collection<AggregatedDataValue> getAggregatedDataValues( Collection<Integer> periodIds, Collection<Integer> organisationUnitIds );

    /**
     * Gets a collection of AggregatedDataValues where the value is the sum of
     * all category option combos for the data element. 0 is set as 
     * categoryoptioncombo identifier value on the AggregatedDataValues.
     * 
     * @param periodIds the collection of Period identifiers.
     * @param organisationUnitIds the collection of OrganisationUnit identifiers.
     * @return a collection of AggregatedDataValues.
     */
    Collection<AggregatedDataValue> getAggregatedDataValueTotals( Collection<Integer> periodIds, Collection<Integer> organisationUnitIds );
    
    /**
     * Gets a collection of AggregatedDataValues.
     * 
     * @param dataElementId the DataElement identifier.
     * @param periodIds the collection of Period identifiers.
     * @param organisationUnitIds the collection of OrganisationUnit identifiers.
     * @return a collection of AggregatedDataValues.
     */
    Collection<AggregatedDataValue> getAggregatedDataValues( int dataElementId, Collection<Integer> periodIds, Collection<Integer> organisationUnitIds );

    /**
     * Gets a collection of AggregatedDataValues.
     * 
     * @param dataElementIds the collection of DataElement identifiers.
     * @param periodIds the collection of Period identifiers.
     * @param organisationUnitIds the collection of OrganisationUnit identifiers.
     * @return a collection of AggregatedDataValues.
     */
    Collection<AggregatedDataValue> getAggregatedDataValues( Collection<Integer> dataElementIds, Collection<Integer> periodIds, Collection<Integer> organisationUnitIds );

    /**
     * Gets a collection of AggregatedDataValues where the value is the sum of
     * all category option combos for the data element. 0 is set as 
     * categoryoptioncombo identifier value on the AggregatedDataValues.
     * 
     * @param dataElementIds the collection of DataElement identifiers.
     * @param periodIds the collection of Period identifiers.
     * @param organisationUnitIds the collection of OrganisationUnit identifiers.
     * @return a collection of AggregatedDataValues.
     */
    Collection<AggregatedDataValue> getAggregatedDataValueTotals( Collection<Integer> dataElementIds, Collection<Integer> periodIds, Collection<Integer> organisationUnitIds );
    
    /**
     * Deletes AggregatedDataValues registered for the given parameters.
     * 
     * @param dataElementIds a collection of DataElement identifiers.
     * @param periodIds a collection of Period identifiers.
     * @param organisationUnitIds a collection of OrganisationUnit identifiers.
     */
    void deleteAggregatedDataValues( Collection<Integer> dataElementIds, Collection<Integer> periodIds,
        Collection<Integer> organisationUnitIds );

    /**
     * Deletes all AggregatedDataValues.
     * 
     * @return the number of deleted AggregatedDataValues.
     */
    void deleteAggregatedDataValues();

    /**
     * Returns values for children of an orgunit at a particular level
     * @param orgunit the root organisationunit
     * @param level the level to retrieve values at
     * @param periods the period to retrieve values for
     * @return an iterator type object for retrieving the values
     */
    public StoreIterator<AggregatedDataValue> getAggregatedDataValuesAtLevel(OrganisationUnit orgunit, OrganisationUnitLevel level, Collection<Period> periods);

    /**
     * Returns count of agg data values for children of an orgunit at a particular level
     * @param orgunit the root organisationunit
     * @param level the level to retrieve values at
     * @param periods the periods to retrieve values for
     * @return an iterator type object for retrieving the values
     */
    public int countDataValuesAtLevel( OrganisationUnit orgunit, OrganisationUnitLevel level, Collection<Period> periods );

    // ----------------------------------------------------------------------
    // AggregatedIndicatorValue
    // ----------------------------------------------------------------------

    /**
     * Gets the aggregated value from the datamart table for the given parameters.
     * 
     * @param indicator The Indicator identifier.
     * @param period The Period identifier.
     * @param organisationUnit The OrganisationUnit identifier.
     * @return the aggregated value, or -1 if no value exists.
     */
    Double getAggregatedIndicatorValue( int indicator, int period, int organisationUnit );

    /**
     * Gets a collection of AggregatedIndicatorValues.
     * 
     * @param periodIds the Period identifiers.
     * @param organisationUnitIds the OrganisationUnit identifiers.
     * @return a collection of AggregatedIndicatorValues.
     */
    Collection<AggregatedIndicatorValue> getAggregatedIndicatorValues( Collection<Integer> periodIds, Collection<Integer> organisationUnitIds );
    
    /**
     * Gets a collection of AggregatedIndicatorValues.
     * 
     * @param indicatorIds the Indicator identifiers.
     * @param periodIds the Period identifiers.
     * @param organisationUnitIds the OrganisationUnit identifiers.
     * @return a collection of AggregatedIndicatorValues.
     */
    Collection<AggregatedIndicatorValue> getAggregatedIndicatorValues( Collection<Integer> indicatorIds,
        Collection<Integer> periodIds, Collection<Integer> organisationUnitIds );
    
    /**
     * Deletes AggregatedIndicatorValue registered for the given parameters.
     * 
     * @param indicatorIds a collection of Indicator identifiers.
     * @param periodIds a collection of Period identifiers.
     * @param organisationUnitIds a collection of OrganisationUnit identifiers.
     */
    void deleteAggregatedIndicatorValues( Collection<Integer> indicatorIds, Collection<Integer> periodIds,
        Collection<Integer> organisationUnitIds );

    /**
     * Deletes all AggregatedIndicatorValue.
     * 
     * @return the number of deleted AggregatedIndicatorValues.
     */
    void deleteAggregatedIndicatorValues();


    /**
     * Returns values for children of an orgunit at a particular level
     * @param orgunit the root organisationunit
     * @param level the level to retrieve values at
     * @param periods the period to retrieve values for
     * @return an iterator type object for retrieving the values
     */
    public StoreIterator<AggregatedIndicatorValue> getAggregatedIndicatorValuesAtLevel(OrganisationUnit orgunit, OrganisationUnitLevel level, Collection<Period> periods);

    /**
     * Returns count of agg indicator values for children of an orgunit at a particular level
     * @param orgunit the root organisationunit
     * @param level the level to retrieve values at
     * @param periods the periods to retrieve values for
     * @return an iterator type object for retrieving the values
     */
    public int countIndicatorValuesAtLevel( OrganisationUnit orgunit, OrganisationUnitLevel level, Collection<Period> periods );

    // ----------------------------------------------------------------------
    // AggregatedDataSetCompleteness
    // ----------------------------------------------------------------------

    /**
     * Gets a collection of DataSetCompletenessResult. Populates the data set id,
     * period id, organisation unit id and value properties.
     * 
     * @param dataSetIds a collection of DataSet identifiers.
     * @param periodIds a collection of Period identifiers.
     * @param organisationUnitIds a collection of OrganisationUnit identifiers.
     */
    Collection<DataSetCompletenessResult> getAggregatedDataSetCompleteness( Collection<Integer> dataSetIds, Collection<Integer> periodIds,
        Collection<Integer> organisationUnitIds );

    // ----------------------------------------------------------------------
    // Data mart
    // ----------------------------------------------------------------------

    /**
     * Drops all data mart tables.
     */
    void dropDataMart();
    
    /**
     * Creates all data mart tables.
     */
    void createDataMart();
}
