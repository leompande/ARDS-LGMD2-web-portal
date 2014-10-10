package org.hisp.dhis.analytics.data;

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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.hisp.dhis.analytics.AggregationType;
import org.hisp.dhis.analytics.AnalyticsService;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.common.BaseAnalyticalObject;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.i18n.I18nFormat;

/**
 * Configurable mock implementation of AnalyticsService for testing purposes.
 */
public class MockAnalyticsService
    implements AnalyticsService
{
    private Map<String, Double> valueMap;

    public MockAnalyticsService( Map<String, Double> valueMap )
    {
        this.valueMap = valueMap;
    }

    @Override
    public Grid getAggregatedDataValues( DataQueryParams params )
    {
        throw new NotImplementedException();
    }

    @Override
    public Grid getAggregatedDataValues( DataQueryParams params, boolean tableLayout, List<String> columns, List<String> rows )
    {
        throw new NotImplementedException();
    }

    @Override
    public Map<String, Double> getAggregatedDataValueMapping( DataQueryParams params )
    {
        return valueMap;
    }

    @Override
    public Map<String, Double> getAggregatedDataValueMapping( BaseAnalyticalObject object, I18nFormat format )
    {
        return valueMap;
    }

    @Override
    public DataQueryParams getFromUrl( Set<String> dimensionParams, Set<String> filterParams, AggregationType aggregationType, 
        String measureCriteria, boolean skipMeta, boolean skipRounding, boolean hierarchyMeta, boolean ignoreLimit, boolean hideEmptyRows, boolean showHierarchy, I18nFormat format )
    {
        throw new NotImplementedException();
    }

    @Override
    public DataQueryParams getFromAnalyticalObject( BaseAnalyticalObject object, I18nFormat format )
    {
        throw new NotImplementedException();
    }

    @Override
    public List<DimensionalObject> getDimension( String dimension, List<String> items, Date relativePeriodDate,
        I18nFormat format )
    {
        throw new NotImplementedException();
    }
}
