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

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;

public class DefaultAggregatedOrgUnitDataValueService
    implements AggregatedOrgUnitDataValueService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private AggregatedOrgUnitDataValueStore aggregatedDataValueStore;

    public void setAggregatedDataValueStore( AggregatedOrgUnitDataValueStore aggregatedDataValueStore )
    {
        this.aggregatedDataValueStore = aggregatedDataValueStore;
    }

    // -------------------------------------------------------------------------
    // AggregatedOrgUnitDataValueService implementation
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // AggregatedDataValue
    // -------------------------------------------------------------------------

    public Double getAggregatedValue( DataElement dataElement, DataElementCategoryOptionCombo optionCombo, 
        Period period, OrganisationUnit organisationUnit, OrganisationUnitGroup group )
    {
        return aggregatedDataValueStore.getAggregatedDataValue( dataElement.getId(), optionCombo.getId(), period.getId(), organisationUnit.getId(), group.getId() );
    }
    public Collection<AggregatedDataValue> getAggregatedDataValueTotals( Collection<Integer> dataElementIds, 
        Collection<Integer> periodIds, int organisationUnitId, Collection<Integer> organisationUnitGroupIds )
    {
        return aggregatedDataValueStore.getAggregatedDataValueTotals( dataElementIds, periodIds, organisationUnitId, organisationUnitGroupIds );
    }
    
    public void deleteAggregatedDataValues( Collection<Integer> dataElementIds, Collection<Integer> periodIds, Collection<Integer> organisationUnitIds )
    {
        aggregatedDataValueStore.deleteAggregatedDataValues( dataElementIds, periodIds, organisationUnitIds );
    }
    
    public void deleteAggregatedDataValues()
    {
        aggregatedDataValueStore.deleteAggregatedDataValues();
    }
    
    // -------------------------------------------------------------------------
    // AggregatedIndicatorValue
    // -------------------------------------------------------------------------

    public Double getAggregatedIndicatorValue( Indicator indicator, Period period, OrganisationUnit organisationUnit, OrganisationUnitGroup group )
    {
        return aggregatedDataValueStore.getAggregatedIndicatorValue( indicator.getId(), period.getId(), organisationUnit.getId(), group.getId() );
    }
    
    public Collection<AggregatedIndicatorValue> getAggregatedIndicatorValues( Collection<Integer> indicatorIds, 
        Collection<Integer> periodIds, int organisationUnitId, Collection<Integer> organisationUnitGroupIds )
    {
        return aggregatedDataValueStore.getAggregatedIndicatorValues( indicatorIds, periodIds, organisationUnitId, organisationUnitGroupIds );
    }
    
    public void deleteAggregatedIndicatorValues( Collection<Integer> indicatorIds, Collection<Integer> periodIds, Collection<Integer> organisationUnitIds )
    {
        aggregatedDataValueStore.deleteAggregatedIndicatorValues( indicatorIds, periodIds, organisationUnitIds );
    }
        
    public void deleteAggregatedIndicatorValues()
    {
        aggregatedDataValueStore.deleteAggregatedIndicatorValues();
    }
}
