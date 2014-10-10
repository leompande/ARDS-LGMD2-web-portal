package org.hisp.dhis.analytics;

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

import org.hisp.dhis.common.BaseAnalyticalObject;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.i18n.I18nFormat;

/**
 * <p>This interface is responsible for retrieving aggregated data. Data will be
 * returned in a grid object or as a dimensional key-value mapping.</p>
 * 
 * <p>Most objects accept a DataQueryParams object which encapsulates the query 
 * parameters. The dimensions in the response will appear in the same order as
 * they are set on the DataQueryParams object. You can use various methods for
 * setting indicators, data elements, data sets, periods, organisation units,
 * categories, data element group sets and organisation unit group sets on the
 * the DataQueryParams object. Objects can be defined as dimensions or filters.</p>
 * 
 * <p>Example usage for setting multiple indicators and a period as dimensions
 * and an organisation unit as filter. In the grid response the first column
 * will contain indicator identifiers, the second column will contain period
 * identifiers and the third column will contain aggregated values. Note that
 * the organisation unit is excluded since it is defined as a filter:</p>
 * 
 * <pre>
 * <code>
 * DataQueryParams params = new DataQueryParams();
 * 
 * params.setIndicators( indicators );
 * params.setPeriod( period );
 * params.setFilterOrganisationUnit( organisationUnit );
 * 
 * Grid grid = analyticsService.getAggregatedDataValues( params );
 * </code>
 * </pre>
 * 
 * <p>Example usage for including category option combos in the response. Note that 
 * the index position of category option combos will follow the order of when the
 * enableCategoryOptionCombos method was called. In the map response, the keys
 * will represent the dimensions defined in the DataQueryParams object and will
 * contain dimension identifiers separated by the "-" character. The key will
 * be of type String and contain a data element identifier, a category option 
 * combo identifier and an organisation unit identifier in that order. The map 
 * values will be the aggregated values of type Double:</p>
 * 
 * <pre>
 * <code>
 * DataQueryParams params = new DataQueryParams();
 * 
 * params.setDataElement( dataElement );
 * params.enableCategoryOptionCombos();
 * params.setOrganisationUnits( organisationUnits );
 * params.setFilterPeriod( period );
 * 
 * Map<String, Double> map = analyticsService.getAggregatedDataValueMapping( params );
 * </code>
 * </pre>
 * 
 * @author Lars Helge Overland
 */
public interface AnalyticsService
{
    final String NAMES_META_KEY = "names";
    final String PAGER_META_KEY = "pager";
    final String OU_HIERARCHY_KEY = "ouHierarchy";
    final String OU_NAME_HIERARCHY_KEY = "ouNameHierarchy";
    
    /**
     * Generates aggregated values for the given query.
     * 
     * @param params the data query parameters.
     * @return aggregated data as a Grid object.
     */
    Grid getAggregatedDataValues( DataQueryParams params );
    
    /**
     * Generates an aggregated value grid for the given query. The grid will
     * represent a table with dimensions used as columns and rows as specified
     * in columns and rows dimension arguments.
     * 
     * @param params the data query parameters.
     * @param tableLayout whether to render the grid as a table with columns and rows,
     *        or as a normalized plain data source.
     * @param columns the identifiers of the dimensions to use as columns.
     * @param rows the identifiers of the dimensions to use as rows.
     * @return aggregated data as a Grid object.
     */
    Grid getAggregatedDataValues( DataQueryParams params, boolean tableLayout, List<String> columns, List<String> rows );

    /**
     * Generates a mapping where the key represents the dimensional item identifiers
     * concatenated by "-" and the value is the corresponding aggregated data value
     * based on the given DataQueryParams.
     * 
     * @param params the DataQueryParams.
     * @return a mapping of dimensional items and aggregated data values.
     */
    Map<String, Double> getAggregatedDataValueMapping( DataQueryParams params );

    /**
     * Generates a mapping where the key represents the dimensional item identifiers
     * concatenated by "-" and the value is the corresponding aggregated data value
     * based on the given BaseAnalyticalObject.
     * 
     * @param object the BaseAnalyticalObject.
     * @param format the I18nFormat, can be null.
     * @return a mapping of dimensional items and aggregated data values.
     */
    Map<String, Double> getAggregatedDataValueMapping( BaseAnalyticalObject object, I18nFormat format );

    /**
     * Creates a data query parameter object from the given URL.
     * 
     * @param dimensionParams the dimension URL parameters.
     * @param filterParams the filter URL parameters.
     * @param aggregationType the aggregation type.
     * @param measureCriteria the measure criteria.
     * @param skipMeta whether to skip the meta data part of the response.
     * @param skipRounding whether to skip rounding and provide full precision for values.
     * @param hierarchyMeta whether to include meta data about the org units 
     *        in the hierarchy.
     * @param ignoreLimit whether to ignore the max number of cells limit.
     * @param hideEmptyRows whether to hide rows without data values, applis to table layout.
     * @param showHierarchy whether to show the org unit hierarchy together with the name.
     * @param format the i18n format.
     * @return a data query parameter object created based on the given URL info.
     */
    DataQueryParams getFromUrl( Set<String> dimensionParams, Set<String> filterParams, AggregationType aggregationType, String measureCriteria, 
        boolean skipMeta, boolean skipRounding, boolean hierarchyMeta, boolean ignoreLimit, boolean hideEmptyRows, boolean showHierarchy, I18nFormat format );
    
    /**
     * Creates a data query parameter object from the given BaseAnalyticalObject.
     * 
     * @param object the BaseAnalyticalObject
     * @param format the i18n format.
     * @return a data query parameter object created based on the given BaseAnalyticalObject.
     */
    DataQueryParams getFromAnalyticalObject( BaseAnalyticalObject object, I18nFormat format );
    
    /**
     * Returns a list of persisted DimensionalObjects generated from the given 
     * dimension identifier and list of dimension options. The dx dimension
     * will be exploded into concrete in|de|ds object identifiers and returned
     * as separate DimensionalObjects. 
     * 
     * For the pe dimension items, relative periods represented by enums will be 
     * replaced by real ISO periods relative to the current date. For the ou 
     * dimension items, the user  organisation unit enums 
     * USER_ORG_UNIT|USER_ORG_UNIT_CHILDREN will be replaced by the persisted 
     * organisation units for the current user.
     * 
     * @param dimension the dimension identifier.
     * @param items the dimension items.
     * @param relativePeriodDate the date to use for generating relative periods, can be null.
     * @parma format the I18nFormat, can be null.
     * @return list of DimensionalObjects.
     */
    List<DimensionalObject> getDimension( String dimension, List<String> items, Date relativePeriodDate, I18nFormat format );
}
