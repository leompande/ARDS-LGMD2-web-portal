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

import static org.hisp.dhis.analytics.AnalyticsTableManager.ANALYTICS_TABLE_NAME;
import static org.hisp.dhis.analytics.AnalyticsTableManager.COMPLETENESS_TABLE_NAME;
import static org.hisp.dhis.analytics.AnalyticsTableManager.COMPLETENESS_TARGET_TABLE_NAME;
import static org.hisp.dhis.analytics.DataQueryParams.DISPLAY_NAME_CATEGORYOPTIONCOMBO;
import static org.hisp.dhis.analytics.DataQueryParams.DISPLAY_NAME_DATA_X;
import static org.hisp.dhis.analytics.DataQueryParams.DISPLAY_NAME_ORGUNIT;
import static org.hisp.dhis.analytics.DataQueryParams.DISPLAY_NAME_PERIOD;
import static org.hisp.dhis.analytics.DataQueryParams.FIXED_DIMS;
import static org.hisp.dhis.analytics.DataQueryParams.getDimensionFromParam;
import static org.hisp.dhis.analytics.DataQueryParams.getDimensionItemsFromParam;
import static org.hisp.dhis.common.DimensionalObject.CATEGORYOPTIONCOMBO_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.DATAELEMENT_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.DATASET_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.DATA_X_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.DIMENSION_SEP;
import static org.hisp.dhis.common.DimensionalObject.INDICATOR_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.ORGUNIT_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.PERIOD_DIM_ID;
import static org.hisp.dhis.common.DimensionalObjectUtils.toDimension;
import static org.hisp.dhis.common.IdentifiableObjectUtils.getUids;
import static org.hisp.dhis.common.NameableObjectUtils.asList;
import static org.hisp.dhis.common.NameableObjectUtils.asTypedList;
import static org.hisp.dhis.organisationunit.OrganisationUnit.KEY_LEVEL;
import static org.hisp.dhis.organisationunit.OrganisationUnit.KEY_ORGUNIT_GROUP;
import static org.hisp.dhis.organisationunit.OrganisationUnit.KEY_USER_ORGUNIT;
import static org.hisp.dhis.organisationunit.OrganisationUnit.KEY_USER_ORGUNIT_CHILDREN;
import static org.hisp.dhis.organisationunit.OrganisationUnit.KEY_USER_ORGUNIT_GRANDCHILDREN;
import static org.hisp.dhis.organisationunit.OrganisationUnit.getParentGraphMap;
import static org.hisp.dhis.organisationunit.OrganisationUnit.getParentNameGraphMap;
import static org.hisp.dhis.period.PeriodType.getPeriodTypeFromIsoString;
import static org.hisp.dhis.reporttable.ReportTable.IRT2D;
import static org.hisp.dhis.reporttable.ReportTable.addIfEmpty;
import static org.hisp.dhis.system.util.DateUtils.daysBetween;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.analytics.AggregationType;
import org.hisp.dhis.analytics.AnalyticsManager;
import org.hisp.dhis.analytics.AnalyticsService;
import org.hisp.dhis.analytics.DataQueryGroups;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.analytics.DimensionItem;
import org.hisp.dhis.analytics.IllegalQueryException;
import org.hisp.dhis.analytics.QueryPlanner;
import org.hisp.dhis.common.BaseAnalyticalObject;
import org.hisp.dhis.common.BaseDimensionalObject;
import org.hisp.dhis.common.CodeGenerator;
import org.hisp.dhis.common.CombinationGenerator;
import org.hisp.dhis.common.DimensionType;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.IdentifiableObjectUtils;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.common.NameableObjectUtils;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.DataElementOperandService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.RelativePeriodEnum;
import org.hisp.dhis.period.RelativePeriods;
import org.hisp.dhis.period.comparator.AscendingPeriodComparator;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.system.util.ConversionUtils;
import org.hisp.dhis.system.util.DebugUtils;
import org.hisp.dhis.system.util.ListUtils;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.system.util.SystemUtils;
import org.hisp.dhis.system.util.Timer;
import org.hisp.dhis.system.util.UniqueArrayList;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 */
public class DefaultAnalyticsService
    implements AnalyticsService
{
    private static final Log log = LogFactory.getLog( DefaultAnalyticsService.class );
    
    private static final String VALUE_HEADER_NAME = "Value";
    private static final int PERCENT = 100;
    private static final int MAX_QUERIES = 8;

    //TODO make sure data x dims are successive
    //TODO completeness on time
    
    @Autowired
    private AnalyticsManager analyticsManager;
    
    @Autowired
    private QueryPlanner queryPlanner;
    
    @Autowired
    private IndicatorService indicatorService;
    
    @Autowired
    private DataElementService dataElementService;
    
    @Autowired
    private DataElementCategoryService categoryService;
    
    @Autowired
    private DataSetService dataSetService;
    
    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    @Autowired
    private OrganisationUnitGroupService organisationUnitGroupService;
    
    @Autowired
    private ExpressionService expressionService;
    
    @Autowired
    private ConstantService constantService;

    @Autowired
    private DataElementOperandService operandService;

    @Autowired
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService; // Testing purposes
    }

    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------

    @Override
    public Grid getAggregatedDataValues( DataQueryParams params )
    {
        queryPlanner.validate( params );
        
        params.conform();
                
        // ---------------------------------------------------------------------
        // Headers
        // ---------------------------------------------------------------------

        Grid grid = new ListGrid();

        for ( DimensionalObject col : params.getHeaderDimensions() )
        {
            grid.addHeader( new GridHeader( col.getDimension(), col.getDisplayName(), String.class.getName(), false, true ) );
        }
        
        grid.addHeader( new GridHeader( DataQueryParams.VALUE_ID, VALUE_HEADER_NAME, Double.class.getName(), false, false ) );

        // ---------------------------------------------------------------------
        // Indicators
        // ---------------------------------------------------------------------

        if ( params.getIndicators() != null )
        {   
            int indicatorIndex = params.getIndicatorDimensionIndex();
            List<Indicator> indicators = asTypedList( params.getIndicators() );
            
            expressionService.explodeExpressions( indicators );
            
            DataQueryParams dataSourceParams = params.instance();
            dataSourceParams.removeDimension( DATAELEMENT_DIM_ID );
            dataSourceParams.removeDimension( DATASET_DIM_ID );
            
            dataSourceParams = replaceIndicatorsWithDataElements( dataSourceParams, indicatorIndex );

            Map<String, Double> aggregatedDataMap = getAggregatedDataValueMap( dataSourceParams );

            Map<String, Map<DataElementOperand, Double>> permutationOperandValueMap = dataSourceParams.getPermutationOperandValueMap( aggregatedDataMap );
            
            List<List<DimensionItem>> dimensionItemPermutations = dataSourceParams.getDimensionItemPermutations();

            Map<String, Double> constantMap = constantService.getConstantMap();

            Period filterPeriod = dataSourceParams.getFilterPeriod();
            
            for ( Indicator indicator : indicators )
            {
                for ( List<DimensionItem> options : dimensionItemPermutations )
                {
                    String permKey = DimensionItem.asItemKey( options );

                    Map<DataElementOperand, Double> valueMap = permutationOperandValueMap.get( permKey );
                    
                    if ( valueMap == null )
                    {
                        continue;
                    }
                    
                    Period period = filterPeriod != null ? filterPeriod : (Period) DimensionItem.getPeriodItem( options );

                    int days = daysBetween( period.getStartDate(), period.getEndDate() );
                    
                    Double value = expressionService.getIndicatorValue( indicator, period, valueMap, constantMap, days );

                    if ( value != null )
                    {
                        List<DimensionItem> row = new ArrayList<DimensionItem>( options );
                        
                        row.add( indicatorIndex, new DimensionItem( INDICATOR_DIM_ID, indicator ) );
                                                    
                        grid.addRow();
                        grid.addValues( DimensionItem.getItemIdentifiers( row ) );
                        grid.addValue( params.isSkipRounding() ? value : MathUtils.getRounded( value ) );
                    }
                }
            }
        }

        // ---------------------------------------------------------------------
        // Data elements
        // ---------------------------------------------------------------------

        if ( params.getDataElements() != null )
        {
            DataQueryParams dataSourceParams = params.instance();
            dataSourceParams.removeDimension( INDICATOR_DIM_ID );
            dataSourceParams.removeDimension( DATASET_DIM_ID );
            
            Map<String, Double> aggregatedDataMap = getAggregatedDataValueMap( dataSourceParams );
            
            for ( Map.Entry<String, Double> entry : aggregatedDataMap.entrySet() )
            {
                grid.addRow();
                grid.addValues( entry.getKey().split( DIMENSION_SEP ) );
                grid.addValue( params.isSkipRounding() ? entry.getValue() : MathUtils.getRounded( entry.getValue() ) );
            }
        }

        // ---------------------------------------------------------------------
        // Data sets / completeness
        // ---------------------------------------------------------------------

        if ( params.getDataSets() != null )
        {
            // -----------------------------------------------------------------
            // Get complete data set registrations
            // -----------------------------------------------------------------

            DataQueryParams dataSourceParams = params.instance();
            dataSourceParams.removeDimension( INDICATOR_DIM_ID );
            dataSourceParams.removeDimension( DATAELEMENT_DIM_ID );
            dataSourceParams.setAggregationType( AggregationType.COUNT );

            Map<String, Double> aggregatedDataMap = getAggregatedCompletenessValueMap( dataSourceParams );

            // -----------------------------------------------------------------
            // Get completeness targets
            // -----------------------------------------------------------------

            List<Integer> completenessDimIndexes = dataSourceParams.getCompletenessDimensionIndexes();
            List<Integer> completenessFilterIndexes = dataSourceParams.getCompletenessFilterIndexes();
            
            DataQueryParams targetParams = dataSourceParams.instance();

            targetParams.setDimensions( ListUtils.getAtIndexes( targetParams.getDimensions(), completenessDimIndexes ) );
            targetParams.setFilters( ListUtils.getAtIndexes( targetParams.getFilters(), completenessFilterIndexes ) );
            targetParams.setSkipPartitioning( true );

            Map<String, Double> targetMap = getAggregatedCompletenessTargetMap( targetParams );

            Integer periodIndex = dataSourceParams.getPeriodDimensionIndex();
            Integer dataSetIndex = dataSourceParams.getDataSetDimensionIndex();

            Map<String, PeriodType> dsPtMap = dataSourceParams.getDataSetPeriodTypeMap();

            PeriodType filterPeriodType = dataSourceParams.getFilterPeriodType();
            
            // -----------------------------------------------------------------
            // Join data maps, calculate completeness and add to grid
            // -----------------------------------------------------------------

            for ( Map.Entry<String, Double> entry : aggregatedDataMap.entrySet() )
            {
                List<String> dataRow = new ArrayList<String>( Arrays.asList( entry.getKey().split( DIMENSION_SEP ) ) );
                
                List<String> targetRow = ListUtils.getAtIndexes( dataRow, completenessDimIndexes );
                String targetKey = StringUtils.join( targetRow, DIMENSION_SEP );
                Double target = targetMap.get( targetKey );
                
                if ( target != null && entry.getValue() != null )
                {
                    PeriodType queryPt = filterPeriodType != null ? filterPeriodType : getPeriodTypeFromIsoString( dataRow.get( periodIndex ) );
                    PeriodType dataSetPt = dsPtMap.get( dataRow.get( dataSetIndex ) );
                    
                    target = target * queryPt.getPeriodSpan( dataSetPt );
                    
                    double value = entry.getValue() * PERCENT / target;
                    
                    grid.addRow();
                    grid.addValues( dataRow.toArray() );
                    grid.addValue( params.isSkipRounding() ? value : MathUtils.getRounded( value ) );
                }
            }
        }

        // ---------------------------------------------------------------------
        // Other dimensions
        // ---------------------------------------------------------------------

        if ( params.getIndicators() == null && params.getDataElements() == null && params.getDataSets() == null )
        {
            Map<String, Double> aggregatedDataMap = getAggregatedDataValueMap( params.instance() );
            
            for ( Map.Entry<String, Double> entry : aggregatedDataMap.entrySet() )
            {
                grid.addRow();
                grid.addValues( entry.getKey().split( DIMENSION_SEP ) );
                grid.addValue( params.isSkipRounding() ? entry.getValue() : MathUtils.getRounded( entry.getValue() ) );
            }
        }

        // ---------------------------------------------------------------------
        // Meta-data
        // ---------------------------------------------------------------------

        if ( !params.isSkipMeta() )
        {
            Integer cocIndex = params.getCocIndex();
            
            Map<Object, Object> metaData = new HashMap<Object, Object>();
            
            Map<String, String> uidNameMap = getUidNameMap( params );
            Map<String, String> cocNameMap = getCocNameMap( grid, cocIndex );            
            uidNameMap.putAll( cocNameMap );
            
            metaData.put( NAMES_META_KEY, uidNameMap );
            metaData.put( PERIOD_DIM_ID, getUids( params.getDimensionOrFilter( PERIOD_DIM_ID ) ) );
            metaData.put( ORGUNIT_DIM_ID, getUids( params.getDimensionOrFilter( ORGUNIT_DIM_ID ) ) );
            metaData.put( CATEGORYOPTIONCOMBO_DIM_ID, cocNameMap.keySet() );
            
            if ( params.isHierarchyMeta() )
            {
                metaData.put( OU_HIERARCHY_KEY, getParentGraphMap( asTypedList( params.getDimensionOrFilter( ORGUNIT_DIM_ID ), OrganisationUnit.class ) ) );
            }
            
            if ( params.isShowHierarchy() )
            {
                metaData.put( OU_NAME_HIERARCHY_KEY, getParentNameGraphMap( asTypedList( params.getDimensionOrFilter( ORGUNIT_DIM_ID ), OrganisationUnit.class ), true ) );
            }
            
            grid.setMetaData( metaData );
        }
        
        return grid;
    }
    
    @Override
    public Grid getAggregatedDataValues( DataQueryParams params, boolean tableLayout, List<String> columns, List<String> rows )
    {
        if ( !tableLayout )
        {
            return getAggregatedDataValues( params );
        }
        
        ListUtils.removeEmptys( columns );
        ListUtils.removeEmptys( rows );
        
        queryPlanner.validateTableLayout( params, columns, rows );
        
        ReportTable reportTable = new ReportTable();
        
        List<NameableObject[]> tableColumns = new ArrayList<NameableObject[]>();
        List<NameableObject[]> tableRows = new ArrayList<NameableObject[]>();

        if ( columns != null )
        {            
            for ( String dimension : columns )
            {
                reportTable.getColumnDimensions().add( dimension );
                
                tableColumns.add( params.getDimensionArrayCollapseDxExplodeCoc( dimension ) );
            }
        }
        
        if ( rows != null )
        {
            for ( String dimension : rows )
            {
                reportTable.getRowDimensions().add( dimension );
                
                tableRows.add( params.getDimensionArrayCollapseDxExplodeCoc( dimension ) );
            }
        }

        reportTable.setGridColumns( new CombinationGenerator<NameableObject>( tableColumns.toArray( IRT2D ) ).getCombinations() );
        reportTable.setGridRows( new CombinationGenerator<NameableObject>( tableRows.toArray( IRT2D ) ).getCombinations() );

        addIfEmpty( reportTable.getGridColumns() ); 
        addIfEmpty( reportTable.getGridRows() );
        
        reportTable.setTitle( IdentifiableObjectUtils.join( params.getFilterItems() ) );
        reportTable.setHideEmptyRows( params.isHideEmptyRows() );
        reportTable.setShowHierarchy( params.isShowHierarchy() );

        Grid grid = getAggregatedDataValues( params );
        
        Map<String, Double> valueMap = getAggregatedDataValueMapping( grid );

        return reportTable.getGrid( new ListGrid( grid.getMetaData() ), valueMap, false );
    }
    
    @Override
    public Map<String, Double> getAggregatedDataValueMapping( DataQueryParams params )
    {
        Grid grid = getAggregatedDataValues( params );
        
        return getAggregatedDataValueMapping( grid );
    }
    
    /**
     * Generates a mapping where the key represents the dimensional item identifiers
     * concatenated by "-" and the value is the corresponding aggregated data value
     * based on the given grid.
     * 
     * @param grid the grid.
     * @return a mapping between item identifiers and aggregated values.
     */
    private Map<String, Double> getAggregatedDataValueMapping( Grid grid )
    {
        Map<String, Double> map = new HashMap<String, Double>();
        
        int metaCols = grid.getWidth() - 1;
        int valueIndex = grid.getWidth() - 1;
        
        for ( List<Object> row : grid.getRows() )
        {
            StringBuilder key = new StringBuilder();
            
            for ( int index = 0; index < metaCols; index++ )
            {
                key.append( row.get( index ) ).append( DIMENSION_SEP );
            }

            key.deleteCharAt( key.length() - 1 );
            
            Double value = (Double) row.get( valueIndex );
            
            map.put( key.toString(), value );
        }
        
        return map;
    }

    @Override
    public Map<String, Double> getAggregatedDataValueMapping( BaseAnalyticalObject object, I18nFormat format )
    {
        DataQueryParams params = getFromAnalyticalObject( object, format );
        
        return getAggregatedDataValueMapping( params );
    }

    /**
     * Generates aggregated values for the given query. Creates a mapping between 
     * a dimension key and the aggregated value. The dimension key is a 
     * concatenation of the identifiers of the dimension items separated by "-".
     * 
     * @param params the data query parameters.
     * @return a mapping between a dimension key and the aggregated value.
     */
    private Map<String, Double> getAggregatedDataValueMap( DataQueryParams params )
    {
        return getAggregatedValueMap( params, ANALYTICS_TABLE_NAME );
    }

    /**
     * Generates aggregated values for the given query. Creates a mapping between 
     * a dimension key and the aggregated value. The dimension key is a 
     * concatenation of the identifiers of the dimension items separated by "-".
     * 
     * @param params the data query parameters.
     * @return a mapping between a dimension key and the aggregated value.
     */
    private Map<String, Double> getAggregatedCompletenessValueMap( DataQueryParams params )
    {
        return getAggregatedValueMap( params, COMPLETENESS_TABLE_NAME );
    }

    /**
     * 
     * @param params
     * @return
     */
    private Map<String, Double> getAggregatedCompletenessTargetMap( DataQueryParams params )
    {
        return getAggregatedValueMap( params, COMPLETENESS_TARGET_TABLE_NAME );
    }
    
    /**
     * Generates a mapping between a dimension key and the aggregated value. The
     * dimension key is a concatenation of the identifiers of the dimension items
     * separated by "-".
     */
    private Map<String, Double> getAggregatedValueMap( DataQueryParams params, String tableName )        
    {
        int optimalQueries = MathUtils.getWithin( SystemUtils.getCpuCores(), 1, MAX_QUERIES );
        
        Timer t = new Timer().start();
        
        DataQueryGroups queryGroups = queryPlanner.planQuery( params, optimalQueries, tableName );
        
        t.getSplitTime( "Planned query, got: " + queryGroups.getLargestGroupSize() + " for optimal: " + optimalQueries );

        Map<String, Double> map = new HashMap<String, Double>();
        
        for ( List<DataQueryParams> queries : queryGroups.getSequentialQueries() )
        {
            List<Future<Map<String, Double>>> futures = new ArrayList<Future<Map<String, Double>>>();
            
            for ( DataQueryParams query : queries )
            {
                futures.add( analyticsManager.getAggregatedDataValues( query ) );
            }
    
            for ( Future<Map<String, Double>> future : futures )
            {
                try
                {
                    Map<String, Double> taskValues = future.get();
                    
                    if ( taskValues != null )
                    {
                        map.putAll( taskValues );
                    }
                }
                catch ( Exception ex )
                {
                    log.error( DebugUtils.getStackTrace( ex ) );
                    log.error( DebugUtils.getStackTrace( ex.getCause() ) );
                    
                    throw new RuntimeException( "Error during execution of aggregation query task", ex );
                }
            }
            
            t.getSplitTime( "Got aggregated values for query group" );
        }
        
        t.getTime( "Got aggregated values" );
        
        return map;
    }
    
    @Override
    public DataQueryParams getFromUrl( Set<String> dimensionParams, Set<String> filterParams, AggregationType aggregationType, 
        String measureCriteria, boolean skipMeta, boolean skipRounding, boolean hierarchyMeta, boolean ignoreLimit, boolean hideEmptyRows, boolean showHierarchy, I18nFormat format )
    {
        DataQueryParams params = new DataQueryParams();

        params.setAggregationType( aggregationType );
        params.setIgnoreLimit( ignoreLimit );
        
        if ( dimensionParams != null && !dimensionParams.isEmpty() )
        {
            for ( String param : dimensionParams )
            {
                String dimension = getDimensionFromParam( param );
                List<String> options = getDimensionItemsFromParam( param );
                
                if ( dimension != null && options != null )
                {
                    params.getDimensions().addAll( getDimension( dimension, options, null, format ) );
                }
            }
        }

        if ( filterParams != null && !filterParams.isEmpty() )
        {
            for ( String param : filterParams )
            {
                String dimension = DataQueryParams.getDimensionFromParam( param );
                List<String> options = DataQueryParams.getDimensionItemsFromParam( param );
                
                if ( dimension != null && options != null )
                {
                    params.getFilters().addAll( getDimension( dimension, options, null, format ) );
                }
            }
        }
        
        if ( measureCriteria != null && !measureCriteria.isEmpty() )
        {
            params.setMeasureCriteria( DataQueryParams.getMeasureCriteriaFromParam( measureCriteria ) );
        }
        
        params.setSkipMeta( skipMeta );
        params.setSkipRounding( skipRounding );
        params.setHierarchyMeta( hierarchyMeta );
        params.setHideEmptyRows( hideEmptyRows );
        params.setShowHierarchy( showHierarchy );

        return params;
    }

    @Override
    public DataQueryParams getFromAnalyticalObject( BaseAnalyticalObject object, I18nFormat format )
    {
        DataQueryParams params = new DataQueryParams();
        
        if ( object != null )
        {
            Date date = object.getRelativePeriodDate();
            
            object.populateAnalyticalProperties();
            
            for ( DimensionalObject column : object.getColumns() )
            {
                params.getDimensions().addAll( getDimension( toDimension( column.getDimension() ), getUids( column.getItems() ), date, format ) );
            }
            
            for ( DimensionalObject row : object.getRows() )
            {
                params.getDimensions().addAll( getDimension( toDimension( row.getDimension() ), getUids( row.getItems() ), date, format ) );
            }
            
            for ( DimensionalObject filter : object.getFilters() )
            {
                params.getFilters().addAll( getDimension( toDimension( filter.getDimension() ), getUids( filter.getItems() ), date, format ) );
            }
        }
        
        return params;
    }
    
    public List<DimensionalObject> getDimension( String dimension, List<String> items, Date relativePeriodDate, I18nFormat format )
    {        
        if ( DATA_X_DIM_ID.equals( dimension ) )
        {
            List<DimensionalObject> dataDimensions = new ArrayList<DimensionalObject>();
            
            List<NameableObject> indicators = new ArrayList<NameableObject>();
            List<NameableObject> dataElements = new ArrayList<NameableObject>();
            List<NameableObject> dataSets = new ArrayList<NameableObject>();
            List<NameableObject> operandDataElements = new ArrayList<NameableObject>();
            
            options : for ( String uid : items )
            {
                Indicator in = indicatorService.getIndicator( uid );
                
                if ( in != null )
                {
                    indicators.add( in );
                    continue options;
                }
                
                DataElement de = dataElementService.getDataElement( uid );
                
                if ( de != null )
                {       
                    dataElements.add( de );
                    continue options;
                }
                
                DataSet ds = dataSetService.getDataSet( uid );
                
                if ( ds != null )
                {
                    dataSets.add( ds );
                    continue options;
                }
                
                DataElementOperand dc = operandService.getDataElementOperandByUid( uid );
                
                if ( dc != null )
                {
                    operandDataElements.add( dc.getDataElement() );
                    continue options;
                }
                
                throw new IllegalQueryException( "Data dimension option identifier does not reference any option: " + uid );                
            }
            
            if ( !indicators.isEmpty() )
            {
                dataDimensions.add( new BaseDimensionalObject( INDICATOR_DIM_ID, DimensionType.INDICATOR, indicators ) );
            }
            
            if ( !dataElements.isEmpty() )
            {
                dataDimensions.add( new BaseDimensionalObject( DATAELEMENT_DIM_ID, DimensionType.DATAELEMENT, dataElements ) );
            }
            
            if ( !dataSets.isEmpty() )
            {
                dataDimensions.add( new BaseDimensionalObject( DATASET_DIM_ID, DimensionType.DATASET, dataSets ) );
            }
            
            if ( !operandDataElements.isEmpty() )
            {
                dataDimensions.add( new BaseDimensionalObject( DATAELEMENT_DIM_ID, DimensionType.DATAELEMENT, operandDataElements ) );
                dataDimensions.add( new BaseDimensionalObject( CATEGORYOPTIONCOMBO_DIM_ID, DimensionType.CATEGORY_OPTION_COMBO, new ArrayList<NameableObject>() ) );
            }
            
            if ( indicators.isEmpty() && dataElements.isEmpty() && dataSets.isEmpty() && operandDataElements.isEmpty() )
            {
                throw new IllegalQueryException( "Dimension dx is present in query without any valid dimension options" );
            }
            
            return dataDimensions;
        }
        
        if ( CATEGORYOPTIONCOMBO_DIM_ID.equals( dimension ) )
        {
            DimensionalObject object = new BaseDimensionalObject( dimension, DimensionType.CATEGORY_OPTION_COMBO, null, DISPLAY_NAME_CATEGORYOPTIONCOMBO, new ArrayList<NameableObject>() );
            
            return Arrays.asList( object );
        }

        if ( PERIOD_DIM_ID.equals( dimension ) )
        {
            Set<Period> periods = new HashSet<Period>();
            
            for ( String isoPeriod : items )
            {
                if ( RelativePeriodEnum.contains( isoPeriod ) )
                {
                    RelativePeriodEnum relativePeriod = RelativePeriodEnum.valueOf( isoPeriod );
                    periods.addAll( RelativePeriods.getRelativePeriodsFromEnum( relativePeriod, relativePeriodDate, format, true ) );
                }
                else
                {
                    Period period = PeriodType.getPeriodFromIsoString( isoPeriod );
                
                    if ( period != null )
                    {
                        periods.add( period );
                    }
                }
            }
            
            if ( periods.isEmpty() )
            {
                throw new IllegalQueryException( "Dimension pe is present in query without any valid dimension options" );
            }
            
            for ( Period period : periods )
            {
                period.setName( format != null ? format.formatPeriod( period ) : null );
            }

            List<Period> periodList = new ArrayList<Period>( periods );
            Collections.sort( periodList, AscendingPeriodComparator.INSTANCE );
            
            DimensionalObject object = new BaseDimensionalObject( dimension, DimensionType.PERIOD, null, DISPLAY_NAME_PERIOD, asList( periodList ) );
            
            return Arrays.asList( object );
        }
        
        if ( ORGUNIT_DIM_ID.equals( dimension ) )
        {
            User user = currentUserService.getCurrentUser();
            
            List<NameableObject> ous = new UniqueArrayList<NameableObject>();
            List<Integer> levels = new UniqueArrayList<Integer>();
            List<OrganisationUnitGroup> groups = new UniqueArrayList<OrganisationUnitGroup>();
            
            for ( String ou : items )
            {
                if ( KEY_USER_ORGUNIT.equals( ou ) && user != null && user.getOrganisationUnit() != null )
                {
                    ous.add( user.getOrganisationUnit() );
                }
                else if ( KEY_USER_ORGUNIT_CHILDREN.equals( ou ) && user != null && user.getOrganisationUnit() != null )
                {
                    ous.addAll( user.getOrganisationUnit().getSortedChildren() );
                }
                else if ( KEY_USER_ORGUNIT_GRANDCHILDREN.equals( ou ) && user != null && user.getOrganisationUnit() != null )
                {
                    ous.addAll( user.getOrganisationUnit().getSortedGrandChildren() );
                }
                else if ( ou != null && ou.startsWith( KEY_LEVEL ) )
                {
                    int level = DataQueryParams.getLevelFromLevelParam( ou );
                    
                    if ( level > 0 )
                    {
                        levels.add( level );
                    }
                }
                else if ( ou != null && ou.startsWith( KEY_ORGUNIT_GROUP ) )
                {
                    String uid = DataQueryParams.getUidFromOrgUnitGroupParam( ou );
                    
                    OrganisationUnitGroup group = organisationUnitGroupService.getOrganisationUnitGroup( uid );
                    
                    if ( uid != null )
                    {
                        groups.add( group );
                    }
                }
                else if ( CodeGenerator.isValidCode( ou ) )
                {
                    OrganisationUnit unit = organisationUnitService.getOrganisationUnit( ou );
                    
                    if ( unit != null )
                    {
                        ous.add( unit );
                    }
                }
            }
            
            List<NameableObject> orgUnits = new UniqueArrayList<NameableObject>();
            List<OrganisationUnit> ousList = NameableObjectUtils.asTypedList( ous );
            
            if ( !levels.isEmpty() )
            {
                orgUnits.addAll( organisationUnitService.getOrganisationUnitsAtLevels( levels, ousList ) );
            }
            
            if ( !groups.isEmpty() )
            {
                orgUnits.addAll( organisationUnitService.getOrganisationUnits( groups, ousList ) );
            }
            
            if ( levels.isEmpty() && groups.isEmpty() )
            {
                orgUnits.addAll( ous );
            }            
            
            if ( orgUnits.isEmpty() )
            {
                throw new IllegalQueryException( "Dimension ou is present in query without any valid dimension options" );
            }
            
            DimensionalObject object = new BaseDimensionalObject( dimension, DimensionType.ORGANISATIONUNIT, null, DISPLAY_NAME_ORGUNIT, orgUnits );
            
            return Arrays.asList( object );
        }
        
        OrganisationUnitGroupSet ougs = organisationUnitGroupService.getOrganisationUnitGroupSet( dimension );
            
        if ( ougs != null )
        {
            List<NameableObject> ous = asList( organisationUnitGroupService.getOrganisationUnitGroupsByUid( items ) );
            
            DimensionalObject object = new BaseDimensionalObject( dimension, DimensionType.ORGANISATIONUNIT_GROUPSET, null, ougs.getDisplayName(), ous );
            
            return Arrays.asList( object );
        }
        
        DataElementGroupSet degs = dataElementService.getDataElementGroupSet( dimension );
        
        if ( degs != null )
        {
            List<NameableObject> des = asList( dataElementService.getDataElementGroupsByUid( items ) );
            
            DimensionalObject object = new BaseDimensionalObject( dimension, DimensionType.DATAELEMENT_GROUPSET, null, degs.getDisplayName(), des );
            
            return Arrays.asList( object );
        }
        
        DataElementCategory dec = categoryService.getDataElementCategory( dimension );
        
        if ( dec != null && dec.isDataDimension() )
        {
            List<NameableObject> decos = asList( categoryService.getDataElementCategoryOptionsByUid( items ) );
            
            DimensionalObject object = new BaseDimensionalObject( dimension, DimensionType.CATEGORY, null, dec.getDisplayName(), decos );
            
            return Arrays.asList( object );
        }
        
        throw new IllegalQueryException( "Dimension identifier does not reference any dimension: " + dimension );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    
    private DataQueryParams replaceIndicatorsWithDataElements( DataQueryParams params, int indicatorIndex )
    {
        List<Indicator> indicators = asTypedList( params.getIndicators() );        
        List<NameableObject> dataElements = asList( expressionService.getDataElementsInIndicators( indicators ) );
        
        params.getDimensions().set( indicatorIndex, new BaseDimensionalObject( DATAELEMENT_DIM_ID, DimensionType.DATAELEMENT, dataElements ) );
        params.enableCategoryOptionCombos();
        
        return params;
    }
    
    /**
     * Returns a mapping between the uid and the name of all dimension and filter
     * items for the given params.
     */
    private Map<String, String> getUidNameMap( DataQueryParams params )
    {
        Map<String, String> map = new HashMap<String, String>();
        map.putAll( getUidNameMap( params.getDimensions(), params.isHierarchyMeta() ) );
        map.putAll( getUidNameMap( params.getFilters(), params.isHierarchyMeta() ) );
        map.put( DATA_X_DIM_ID, DISPLAY_NAME_DATA_X );
        
        return map;
    }

    private Map<String, String> getUidNameMap( List<DimensionalObject> dimensions, boolean hierarchyMeta )
    {
        Map<String, String> map = new HashMap<String, String>();
        
        for ( DimensionalObject dimension : dimensions )
        {
            List<NameableObject> items = new ArrayList<NameableObject>( dimension.getItems() );

            boolean hierarchy = hierarchyMeta && DimensionType.ORGANISATIONUNIT.equals( dimension.getType() );
            
            // -----------------------------------------------------------------
            // If dimension is not fixed and has no options, insert all options
            // -----------------------------------------------------------------
            
            if ( !FIXED_DIMS.contains( dimension.getDimension() ) && items.isEmpty() )
            {
                if ( DimensionType.ORGANISATIONUNIT_GROUPSET.equals( dimension.getType() ) )
                {
                    items = asList( organisationUnitGroupService.getOrganisationUnitGroupSet( dimension.getDimension() ).getOrganisationUnitGroups() );
                }
                else if ( DimensionType.DATAELEMENT_GROUPSET.equals( dimension.getType() ) )
                {
                    items = asList( dataElementService.getDataElementGroupSet( dimension.getDimension() ).getMembers() );
                }
                else if ( DimensionType.CATEGORY.equals( dimension.getType() ) )
                {
                    items = asList( categoryService.getDataElementCategory( dimension.getDimension() ).getCategoryOptions() );
                }
            }

            // -----------------------------------------------------------------
            // Insert UID and name into map
            // -----------------------------------------------------------------
            
            for ( IdentifiableObject idObject : items )
            {
                map.put( idObject.getUid(), idObject.getDisplayName() );
                
                if ( hierarchy )
                {
                    OrganisationUnit unit = (OrganisationUnit) idObject;
                    
                    map.putAll( IdentifiableObjectUtils.getUidNameMap( unit.getAncestors() ) );
                }
            }
            
            if ( dimension.getDisplayName() != null )
            {
                map.put( dimension.getDimension(), dimension.getDisplayName() );
            }
        }
        
        return map;
    }
    
    /**
     * Returns a mapping between the category option combo identifiers and names
     * in the given grid. Returns an empty map if the grid or cocIndex parameters
     * are null.
     */
    private Map<String, String> getCocNameMap( Grid grid, Integer cocIndex )
    {
        Map<String, String> metaData = new HashMap<String, String>();
        
        if ( grid != null && cocIndex != null )
        {
            Set<String> uids = new HashSet<String>( ConversionUtils.<String>cast( grid.getColumn( cocIndex ) ) );
            
            Collection<DataElementCategoryOptionCombo> cocs = categoryService.getDataElementCategoryOptionCombosByUid( uids );
            
            for ( DataElementCategoryOptionCombo coc : cocs )
            {
                metaData.put( coc.getUid(), coc.getName() );
            }
        }
        
        return metaData;
    }
}
