package org.hisp.dhis.dataanalysis;

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

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.datavalue.DeflatedDataValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

/**
 * @author Lars Helge Overland
 */
public interface DataAnalysisStore
{
    final String ID = DataAnalysisStore.class.getName();
    
    /**
     * Calculates the standard deviation of the DataValues registered for the given
     * data element, category option combo and organisation unit.
     * 
     * @param dataElement the DataElement.
     * @param categoryOptionCombo the DataElementCategoryOptionCombo.
     * @param organisationUnit the OrganisationUnit.
     * @return a mapping between organisation unit identifier and its standard deviation.
     */
    Map<Integer, Double> getStandardDeviation( DataElement dataElement, DataElementCategoryOptionCombo categoryOptionCombo, Set<Integer> organisationUnits );
    
    /**
     * Calculates the average of the DataValues registered for the given
     * data element, category option combo and organisation unit.
     * 
     * @param dataElement the DataElement.
     * @param categoryOptionCombo the DataElementCategoryOptionCombo.
     * @param organisationUnit the OrganisationUnit.
     * @return a mapping between organisation unit identifier and its average data value.
     */
    Map<Integer, Double> getAverage( DataElement dataElement, DataElementCategoryOptionCombo categoryOptionCombo, Set<Integer> organisationUnits );
    
    /**
     * Generates a collection of data value violations of min-max predefined values.
     * 
     * @param dataElements the data elements.
     * @param categoryOptionCombos the category option combos.
     * @param periods the periods.
     * @param organisationUnits the organisation units.
     * @param limit the max limit of violations to return.
     * @return a collection of data value violations.
     */
    Collection<DeflatedDataValue> getMinMaxViolations( Collection<DataElement> dataElements, Collection<DataElementCategoryOptionCombo> categoryOptionCombos,
        Collection<Period> periods, Collection<OrganisationUnit> organisationUnits, int limit );
    
    /**
     * Returns a collection of DeflatedDataValues for the given input.
     * 
     * @param dataElement the DataElement.
     * @param categoryOptionCombo the DataElementCategoryOptionCombo.
     * @param periods the collection of Periods.
     * @param organisationUnit the OrganisationUnit.
     * @param lowerBound the lower bound for the registered MinMaxDataElement.
     * @param upperBound the upper bound for the registered MinMaxDataElement.
     * @return
     */
    Collection<DeflatedDataValue> getDeflatedDataValues( DataElement dataElement, DataElementCategoryOptionCombo categoryOptionCombo,
        Collection<Period> periods, Map<Integer, Integer> lowerBoundMap, Map<Integer, Integer> upperBoundMap );
    
    /**
     * Returns a collection of DeflatedDataValues which are marked for followup.
     * 
     * @return a collection of DeflatedDataValues.
     */
    Collection<DeflatedDataValue> getDataValuesMarkedForFollowup();
}
