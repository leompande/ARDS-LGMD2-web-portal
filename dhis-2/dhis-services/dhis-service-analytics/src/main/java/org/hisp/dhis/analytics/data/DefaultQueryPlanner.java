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

import static org.hisp.dhis.analytics.AggregationType.AVERAGE_BOOL;
import static org.hisp.dhis.analytics.AggregationType.AVERAGE_INT;
import static org.hisp.dhis.analytics.AggregationType.AVERAGE_INT_DISAGGREGATION;
import static org.hisp.dhis.analytics.AggregationType.SUM;
import static org.hisp.dhis.analytics.DataQueryParams.LEVEL_PREFIX;
import static org.hisp.dhis.analytics.DataQueryParams.MAX_DIM_OPT_PERM;
import static org.hisp.dhis.common.DimensionalObject.CATEGORYOPTIONCOMBO_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.DATAELEMENT_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.DATASET_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.INDICATOR_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.ORGUNIT_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.PERIOD_DIM_ID;
import static org.hisp.dhis.dataelement.DataElement.AGGREGATION_OPERATOR_AVERAGE;
import static org.hisp.dhis.dataelement.DataElement.AGGREGATION_OPERATOR_SUM;
import static org.hisp.dhis.dataelement.DataElement.VALUE_TYPE_BOOL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.analytics.AggregationType;
import org.hisp.dhis.analytics.DataQueryGroups;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.analytics.IllegalQueryException;
import org.hisp.dhis.analytics.Partitions;
import org.hisp.dhis.analytics.QueryPlanner;
import org.hisp.dhis.analytics.table.PartitionUtils;
import org.hisp.dhis.common.BaseDimensionalObject;
import org.hisp.dhis.common.DimensionType;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.ListMap;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.system.util.PaginatedList;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 */
public class DefaultQueryPlanner
    implements QueryPlanner
{
    private static final Log log = LogFactory.getLog( DefaultQueryPlanner.class );
    
    //TODO shortcut group by methods when only 1 option?
    
    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    // -------------------------------------------------------------------------
    // DefaultQueryPlanner implementation
    // -------------------------------------------------------------------------

    public void validate( DataQueryParams params )
        throws IllegalQueryException
    {
        String violation = null;
        
        if ( params == null )
        {
            throw new IllegalQueryException( "Params cannot be null" );
        }
        
        if ( params.getDimensions().isEmpty() )
        {
            violation = "At least one dimension must be specified";
        }
        
        if ( !params.getDimensionsAsFilters().isEmpty() )
        {
            violation = "Dimensions cannot be specified as dimension and filter simultaneously: " + params.getDimensionsAsFilters();
        }
        
        if ( !params.hasPeriods() && !params.isSkipPartitioning() )
        {
            violation = "At least one period must be specified as dimension or filter";
        }
        
        if ( params.getFilters().contains( new BaseDimensionalObject( INDICATOR_DIM_ID ) ) )
        {
            violation = "Indicators cannot be specified as filter";
        }

        if ( params.getFilters().contains( new BaseDimensionalObject( DATASET_DIM_ID ) ) )
        {
            violation = "Data sets cannot be specified as filter";
        }
        
        if ( params.getFilters().contains( new BaseDimensionalObject( CATEGORYOPTIONCOMBO_DIM_ID ) ) )
        {
            violation = "Category option combos cannot be specified as filter";
        }
        
        if ( !params.isIgnoreLimit() && params.getNumberOfDimensionOptionPermutations() > MAX_DIM_OPT_PERM )
        {
            violation = "Table exceeds max number of cells: " + MAX_DIM_OPT_PERM + " (" + params.getNumberOfDimensionOptionPermutations() + ")";
        }
        
        if ( !params.getDuplicateDimensions().isEmpty() )
        {
            violation = "Dimensions cannot be specified more than once: " + params.getDuplicateDimensions();
        }
        
        if ( params.hasDimensionOrFilter( DATASET_DIM_ID ) && !params.getDataElementGroupSets().isEmpty() )
        {
            violation = "Data sets and data element group sets cannot be specified simultaneously";
        }
        
        if ( violation != null )
        {
            log.warn( "Validation failed: " + violation );
            
            throw new IllegalQueryException( violation );
        }
    }
    
    public void validateTableLayout( DataQueryParams params, List<String> columns, List<String> rows )
    {
        String violation = null;
        
        if ( ( columns == null || columns.isEmpty() ) && ( rows == null || rows.isEmpty() ) )
        {
            violation = "Cannot generate table layout when columns and rows are empty";
        }
        
        if ( columns != null )
        {
            for ( String column : columns )
            {
                if ( !params.hasDimensionCollapseDx( column ) )
                {
                    violation = "Column must be present as dimension in query: " + column;
                }
            }
        }
        
        if ( rows != null )
        {
            for ( String row : rows )
            {
                if ( !params.hasDimensionCollapseDx( row ) )
                {
                    violation = "Row must be present as dimension in query: " + row;
                }
            }
        }
        
        if ( violation != null )
        {
            log.warn( "Validation failed: " + violation );
            
            throw new IllegalQueryException( violation );
        }
    }
    
    public DataQueryGroups planQuery( DataQueryParams params, int optimalQueries, String tableName )
    {
        validate( params );

        // ---------------------------------------------------------------------
        // Group queries by partition, period type and organisation unit level
        // ---------------------------------------------------------------------
        
        params = params.instance();
        
        List<DataQueryParams> queries = new ArrayList<DataQueryParams>();
        
        List<DataQueryParams> groupedByPartition = groupByPartition( params, tableName );
        
        for ( DataQueryParams byPartition : groupedByPartition )
        {
            List<DataQueryParams> groupedByOrgUnitLevel = groupByOrgUnitLevel( byPartition );

            for ( DataQueryParams byOrgUnitLevel : groupedByOrgUnitLevel )
            {
                List<DataQueryParams> groupedByPeriodType = groupByPeriodType( byOrgUnitLevel );

                for ( DataQueryParams byPeriodType : groupedByPeriodType )
                {
                    List<DataQueryParams> groupedByAggregationType = groupByAggregationType( byPeriodType );

                    for ( DataQueryParams byAggregationType : groupedByAggregationType )
                    {
                        if ( AVERAGE_INT_DISAGGREGATION.equals( byAggregationType.getAggregationType() ) )
                        {
                            List<DataQueryParams> groupedByDataPeriodType = groupByDataPeriodType( byAggregationType );
                            
                            for ( DataQueryParams byDataPeriodType : groupedByDataPeriodType )
                            {
                                byDataPeriodType.setPartitions( byPartition.getPartitions() );
                                byDataPeriodType.setPeriodType( byPeriodType.getPeriodType() );
                                byDataPeriodType.setAggregationType( byAggregationType.getAggregationType() );
                                
                                queries.add( byDataPeriodType );
                            }
                        }
                        else
                        {
                            byAggregationType.setPartitions( byPartition.getPartitions() );
                            byAggregationType.setPeriodType( byPeriodType.getPeriodType() );
                            
                            queries.add( byAggregationType );
                        }
                    }
                }
            }
        }

        DataQueryGroups queryGroups = new DataQueryGroups( queries );
        
        if ( queryGroups.isOptimal( optimalQueries ) )
        {
            return queryGroups;
        }

        // ---------------------------------------------------------------------
        // Group by data element
        // ---------------------------------------------------------------------
        
        queryGroups = splitByDimension( queryGroups, DATAELEMENT_DIM_ID, optimalQueries );

        if ( queryGroups.isOptimal( optimalQueries ) )
        {
            return queryGroups;
        }

        // ---------------------------------------------------------------------
        // Group by data set
        // ---------------------------------------------------------------------
        
        queryGroups = splitByDimension( queryGroups, DATASET_DIM_ID, optimalQueries );

        if ( queryGroups.isOptimal( optimalQueries ) )
        {
            return queryGroups;
        }

        // ---------------------------------------------------------------------
        // Group by organisation unit
        // ---------------------------------------------------------------------
        
        queryGroups = splitByDimension( queryGroups, ORGUNIT_DIM_ID, optimalQueries );

        return queryGroups;
    }
        
    public boolean canQueryFromDataMart( DataQueryParams params )
    {
        return true;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    
    /**
     * Splits the given list of queries in sub queries on the given dimension.
     */
    private DataQueryGroups splitByDimension( DataQueryGroups queryGroups, String dimension, int optimalQueries )
    {
        int optimalForSubQuery = MathUtils.divideToFloor( optimalQueries, queryGroups.getLargestGroupSize() );
        
        List<DataQueryParams> subQueries = new ArrayList<DataQueryParams>();
        
        for ( DataQueryParams query : queryGroups.getAllQueries() )
        {
            DimensionalObject dim = query.getDimension( dimension );

            List<NameableObject> values = null;

            if ( dim == null || ( values = dim.getItems() ) == null || values.isEmpty() )
            {
                subQueries.add( query.instance() );
                continue;
            }

            List<List<NameableObject>> valuePages = new PaginatedList<NameableObject>( values ).setNumberOfPages( optimalForSubQuery ).getPages();
            
            for ( List<NameableObject> valuePage : valuePages )
            {
                DataQueryParams subQuery = query.instance();
                subQuery.setDimensionOptions( dim.getDimension(), dim.getType(), dim.getDimensionName(), valuePage );
                subQueries.add( subQuery );
            }
        }

        if ( subQueries.size() > queryGroups.getAllQueries().size() )
        {
            log.debug( "Split on " + dimension + ": " + ( subQueries.size() / queryGroups.getAllQueries().size() ) );
        }
        
        return new DataQueryGroups( subQueries );
    }

    // -------------------------------------------------------------------------
    // Supportive - group by methods
    // -------------------------------------------------------------------------
    
    /**
     * Groups the given query into sub queries based on its periods and which 
     * partition it should be executed against. Sets the partition table name on
     * each query. Queries are grouped based on periods if appearing as a 
     * dimension.
     */
    private List<DataQueryParams> groupByPartition( DataQueryParams params, String tableName )
    {
        List<DataQueryParams> queries = new ArrayList<DataQueryParams>();

        if ( params.isSkipPartitioning() )
        {
            params.setPartitions( new Partitions().add( tableName ) );
            queries.add( params );
        }
        else if ( params.getPeriods() != null && !params.getPeriods().isEmpty() )
        {
            ListMap<Partitions, NameableObject> partitionPeriodMap = PartitionUtils.getPartitionPeriodMap( params.getPeriods(), tableName, null );
            
            for ( Partitions partitions : partitionPeriodMap.keySet() )
            {
                DataQueryParams query = params.instance();
                query.setPeriods( partitionPeriodMap.get( partitions ) );
                query.setPartitions( partitions );
                queries.add( query );
            }
        }
        else if ( params.getFilterPeriods() != null && !params.getFilterPeriods().isEmpty() )
        {
            DataQueryParams query = params.instance();
            query.setPartitions( PartitionUtils.getPartitions( params.getFilterPeriods(), tableName, null ) );
            queries.add( query );
        }
        else
        {
            throw new IllegalQueryException( "Query does not contain any period dimension items" );
        }
        
        if ( queries.size() > 1 )
        {
            log.debug( "Split on partition: " + queries.size() );
        }
        
        return queries;
    }
    
    /**
     * If periods appear as dimensions in the given query; groups the query into 
     * sub queries based on the period type of the periods. Sets the period type 
     * name on each query. If periods appear as filters; replaces the period filter
     * with one filter for each period type. Sets the dimension names and filter
     * names respectively.
     */
    public List<DataQueryParams> groupByPeriodType( DataQueryParams params )
    {
        List<DataQueryParams> queries = new ArrayList<DataQueryParams>();

        if ( params.isSkipPartitioning() )
        {
            queries.add( params );
        }
        else if ( params.getPeriods() != null && !params.getPeriods().isEmpty() )
        {
            ListMap<String, NameableObject> periodTypePeriodMap = PartitionUtils.getPeriodTypePeriodMap( params.getPeriods() );
    
            for ( String periodType : periodTypePeriodMap.keySet() )
            {
                DataQueryParams query = params.instance();
                query.setDimensionOptions( PERIOD_DIM_ID, DimensionType.PERIOD, periodType.toLowerCase(), periodTypePeriodMap.get( periodType ) );
                query.setPeriodType( periodType );
                queries.add( query );
            }
        }
        else if ( params.getFilterPeriods() != null && !params.getFilterPeriods().isEmpty() )
        {
            DimensionalObject filter = params.getFilter( PERIOD_DIM_ID );
            
            ListMap<String, NameableObject> periodTypePeriodMap = PartitionUtils.getPeriodTypePeriodMap( filter.getItems() );
            
            params.removeFilter( PERIOD_DIM_ID ).setPeriodType( periodTypePeriodMap.keySet().iterator().next() ); // Using first period type
            
            for ( String periodType : periodTypePeriodMap.keySet() )
            {
                params.getFilters().add( new BaseDimensionalObject( filter.getDimension(), filter.getType(), periodType.toLowerCase(), periodTypePeriodMap.get( periodType ) ) );
            }
            
            queries.add( params );
        }
        else
        {
            queries.add( params.instance() );
            return queries;
        }

        if ( queries.size() > 1 )
        {
            log.debug( "Split on period type: " + queries.size() );
        }
        
        return queries;        
    }
    
    public List<DataQueryParams> groupByOrgUnitLevel( DataQueryParams params )
    {
        List<DataQueryParams> queries = new ArrayList<DataQueryParams>();

        if ( params.getOrganisationUnits() != null && !params.getOrganisationUnits().isEmpty() )
        {
            ListMap<Integer, NameableObject> levelOrgUnitMap = getLevelOrgUnitMap( params.getOrganisationUnits() );
            
            for ( Integer level : levelOrgUnitMap.keySet() )
            {
                DataQueryParams query = params.instance();
                query.setDimensionOptions( ORGUNIT_DIM_ID, DimensionType.ORGANISATIONUNIT, LEVEL_PREFIX + level, levelOrgUnitMap.get( level ) );
                queries.add( query );
            }
        }
        else if ( params.getFilterOrganisationUnits() != null && !params.getFilterOrganisationUnits().isEmpty() )
        {
            DimensionalObject filter = params.getFilter( ORGUNIT_DIM_ID );
            
            ListMap<Integer, NameableObject> levelOrgUnitMap = getLevelOrgUnitMap( params.getFilterOrganisationUnits() );
            
            params.removeFilter( ORGUNIT_DIM_ID );
            
            for ( Integer level : levelOrgUnitMap.keySet() )
            {
                params.getFilters().add( new BaseDimensionalObject( filter.getDimension(), filter.getType(), LEVEL_PREFIX + level, levelOrgUnitMap.get( level ) ) );
            }
            
            queries.add( params );
        }
        else
        {
            queries.add( params.instance() );
            return queries;
        }

        if ( queries.size() > 1 )
        {
            log.info( "Split on org unit level: " + queries.size() );
        }
        
        return queries;    
    }
    
    /**
     * Groups the given query in sub queries based on the aggregation type of its
     * data elements. The aggregation type can be sum, average aggregation or
     * average disaggregation. Sum means that the data elements have sum aggregation
     * operator. Average aggregation means that the data elements have the average
     * aggregation operator and that the period type of the data elements have 
     * higher or equal frequency than the aggregation period type. Average disaggregation
     * means that the data elements have the average aggregation operator and
     * that the period type of the data elements have lower frequency than the
     * aggregation period type. Average bool means that the data elements have the
     * average aggregation operator and the bool value type.
     * 
     * If no data elements are present, the aggregation type will be determined
     * based on the first data element in the first data element group in the 
     * first data element group set in the query.
     * 
     * If the aggregation type is already set/overridden in the request, the
     * query will be returned unchanged. If there are no data elements or data
     * element group sets specified the aggregation type will fall back to sum.
     */
    private List<DataQueryParams> groupByAggregationType( DataQueryParams params )
    {
        List<DataQueryParams> queries = new ArrayList<DataQueryParams>();
        
        if ( params.getAggregationType() != null )
        {
            queries.add( params.instance() );
            return queries;
        }
        
        if ( params.getDataElements() != null && !params.getDataElements().isEmpty() )
        {
            PeriodType periodType = PeriodType.getPeriodTypeByName( params.getPeriodType() );
            
            ListMap<AggregationType, NameableObject> aggregationTypeDataElementMap = getAggregationTypeDataElementMap( params.getDataElements(), periodType );
            
            for ( AggregationType aggregationType : aggregationTypeDataElementMap.keySet() )
            {
                DataQueryParams query = params.instance();
                query.setDataElements( aggregationTypeDataElementMap.get( aggregationType ) );
                query.setAggregationType( aggregationType );
                queries.add( query );
            }
        }
        else if ( params.getDataElementGroupSets() != null && !params.getDataElementGroupSets().isEmpty() )
        {
            DimensionalObject degs = params.getDataElementGroupSets().get( 0 );
            DataElementGroup deg = (DataElementGroup) ( degs.hasItems() ? degs.getItems().get( 0 ) : null );
            
            DataQueryParams query = params.instance();
            
            if ( deg != null && !deg.getMembers().isEmpty() )
            {
                PeriodType periodType = PeriodType.getPeriodTypeByName( params.getPeriodType() );                
                query.setAggregationType( getAggregationType( deg.getValueType(), deg.getAggregationOperator(), periodType, deg.getPeriodType() ) );
            }
            else
            {
                query.setAggregationType( SUM );
            }
            
            queries.add( query );
        }
        else
        {
            DataQueryParams query = params.instance();
            query.setAggregationType( SUM );
            queries.add( query );
        }

        if ( queries.size() > 1 )
        {
            log.debug( "Split on aggregation type: " + queries.size() );
        }
        
        return queries;
    }
    
    /**
     * Groups the given query in sub queries based on the period type of its
     * data elements. Sets the data period type on each query.
     */
    private List<DataQueryParams> groupByDataPeriodType( DataQueryParams params )
    {
        List<DataQueryParams> queries = new ArrayList<DataQueryParams>();

        if ( params.getDataElements() == null || params.getDataElements().isEmpty() )
        {
            queries.add( params.instance() );
            return queries;
        }
        
        ListMap<PeriodType, NameableObject> periodTypeDataElementMap = getPeriodTypeDataElementMap( params.getDataElements() );
        
        for ( PeriodType periodType : periodTypeDataElementMap.keySet() )
        {
            DataQueryParams query = params.instance();
            query.setDataElements( periodTypeDataElementMap.get( periodType ) );
            query.setDataPeriodType( periodType );
            queries.add( query );
        }

        if ( queries.size() > 1 )
        {
            log.debug( "Split on data period type: " + queries.size() );
        }
        
        return queries;
    }

    // -------------------------------------------------------------------------
    // Supportive - get mapping methods
    // -------------------------------------------------------------------------
    
    /**
     * Creates a mapping between level and organisation unit for the given organisation
     * units.
     */
    private ListMap<Integer, NameableObject> getLevelOrgUnitMap( Collection<NameableObject> orgUnits )
    {
        ListMap<Integer, NameableObject> map = new ListMap<Integer, NameableObject>();
        
        for ( NameableObject orgUnit : orgUnits )
        {
            OrganisationUnit ou = (OrganisationUnit) orgUnit;
            
            int level = ou.getLevel() != 0 ? ou.getLevel() : organisationUnitService.getLevelOfOrganisationUnit( ou.getUid() );
            
            map.putValue( level, orgUnit );
        }
        
        return map;
    }
        
    /**
     * Creates a mapping between the aggregation type and data element for the
     * given data elements and period type.
     */
    private ListMap<AggregationType, NameableObject> getAggregationTypeDataElementMap( Collection<NameableObject> dataElements, PeriodType aggregationPeriodType )
    {
        ListMap<AggregationType, NameableObject> map = new ListMap<AggregationType, NameableObject>();
        
        for ( NameableObject element : dataElements )
        {
            DataElement de = (DataElement) element;

            AggregationType aggregationType = getAggregationType( de.getType(), de.getAggregationOperator(), aggregationPeriodType, de.getPeriodType() );
            
            map.putValue( aggregationType, de );
        }
        
        return map;
    }

    /**
     * Puts the given element into the map according to the value type, aggregation
     * operator, aggregation period type and data period type.
     */
    private AggregationType getAggregationType( String valueType, String aggregationOperator, 
        PeriodType aggregationPeriodType, PeriodType dataPeriodType )
    {
        AggregationType aggregationType = null;
        
        if ( AGGREGATION_OPERATOR_SUM.equals( aggregationOperator ) )
        {
            aggregationType = SUM;
        }
        else if ( AGGREGATION_OPERATOR_AVERAGE.equals( aggregationOperator ) )
        {
            if ( VALUE_TYPE_BOOL.equals( valueType ) )
            {
                aggregationType = AVERAGE_BOOL;
            }
            else
            {
                if ( dataPeriodType == null || aggregationPeriodType == null || aggregationPeriodType.getFrequencyOrder() >= dataPeriodType.getFrequencyOrder() )
                {
                    aggregationType = AVERAGE_INT;
                }
                else
                {
                    aggregationType = AVERAGE_INT_DISAGGREGATION;
                }
            }
        }
        
        return aggregationType;
    }

    /**
     * Creates a mapping between the period type and the data element for the
     * given data elements.
     */
    private ListMap<PeriodType, NameableObject> getPeriodTypeDataElementMap( Collection<NameableObject> dataElements )
    {
        ListMap<PeriodType, NameableObject> map = new ListMap<PeriodType, NameableObject>();
        
        for ( NameableObject element : dataElements )
        {
            DataElement dataElement = (DataElement) element;
            
            map.putValue( dataElement.getPeriodType(), element );
        }
        
        return map;
    }
}
