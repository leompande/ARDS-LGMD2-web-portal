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
import static org.hisp.dhis.common.DimensionalObject.DIMENSION_SEP;
import static org.hisp.dhis.common.DimensionalObject.DATA_X_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.ORGUNIT_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.PERIOD_DIM_ID;
import static org.hisp.dhis.common.NameableObjectUtils.getList;
import static org.hisp.dhis.dataelement.DataElement.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.analytics.DataQueryGroups;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.analytics.DimensionItem;
import org.hisp.dhis.analytics.IllegalQueryException;
import org.hisp.dhis.analytics.QueryPlanner;
import org.hisp.dhis.common.BaseDimensionalObject;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.ListMap;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Cal;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.hisp.dhis.period.YearlyPeriodType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 */
public class QueryPlannerTest
    extends DhisSpringTest
{
    @Autowired
    private QueryPlanner queryPlanner;
    
    @Autowired
    private DataElementService dataElementService;
    
    @Autowired
    private DataSetService dataSetService;
    
    @Autowired
    private DataElementCategoryService categoryService;
    
    @Autowired
    private IndicatorService indicatorService;
    
    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    private Indicator inA;
    
    private DataElement deA;
    private DataElement deB;
    private DataElement deC;
    private DataElement deD;
    
    private DataSet dsA;
    private DataSet dsB;
    private DataSet dsC;
    private DataSet dsD;
        
    private DataElementCategoryOptionCombo coc;
    
    private OrganisationUnit ouA;
    private OrganisationUnit ouB;
    private OrganisationUnit ouC;
    private OrganisationUnit ouD;
    private OrganisationUnit ouE;

    //TODO test for indicators, periods in filter
    
    @Override
    public void setUpTest()
    {
        PeriodType pt = new MonthlyPeriodType();
        
        inA = createIndicator( 'A', null );
        
        indicatorService.addIndicator( inA );
        
        deA = createDataElement( 'A', VALUE_TYPE_INT, AGGREGATION_OPERATOR_SUM );
        deB = createDataElement( 'B', VALUE_TYPE_INT, AGGREGATION_OPERATOR_SUM );
        deC = createDataElement( 'C', VALUE_TYPE_INT, AGGREGATION_OPERATOR_AVERAGE );
        deD = createDataElement( 'D', VALUE_TYPE_INT, AGGREGATION_OPERATOR_AVERAGE );
        
        dataElementService.addDataElement( deA );
        dataElementService.addDataElement( deB );
        dataElementService.addDataElement( deC );
        dataElementService.addDataElement( deD );
        
        dsA = createDataSet( 'A', pt );
        dsB = createDataSet( 'B', pt );
        dsC = createDataSet( 'C', pt );
        dsD = createDataSet( 'D', pt );
        
        dataSetService.addDataSet( dsA );
        dataSetService.addDataSet( dsB );
        dataSetService.addDataSet( dsC );
        dataSetService.addDataSet( dsD );
        
        coc = categoryService.getDefaultDataElementCategoryOptionCombo();
        
        ouA = createOrganisationUnit( 'A' );
        ouB = createOrganisationUnit( 'B' );
        ouC = createOrganisationUnit( 'C' );
        ouD = createOrganisationUnit( 'D' );
        ouE = createOrganisationUnit( 'E' );
        
        organisationUnitService.addOrganisationUnit( ouA );
        organisationUnitService.addOrganisationUnit( ouB );
        organisationUnitService.addOrganisationUnit( ouC );
        organisationUnitService.addOrganisationUnit( ouD );
        organisationUnitService.addOrganisationUnit( ouE );
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void testGetHeaderDimensions()
    {
        List<DimensionalObject> expected = new ArrayList<DimensionalObject>();
        expected.add( new BaseDimensionalObject( DATA_X_DIM_ID ) );
        expected.add( new BaseDimensionalObject( ORGUNIT_DIM_ID ) );
        expected.add( new BaseDimensionalObject( PERIOD_DIM_ID ) );
        
        DataQueryParams params = new DataQueryParams();
        params.setDataElements( getList( deA, deB ) );
        params.setOrganisationUnits( getList( ouA, ouB ) );
        params.setPeriods( getList( createPeriod( "2000Q1" ), createPeriod( "2000Q2" ) ) );
        
        assertEquals( expected, params.getHeaderDimensions() );
        
        params = new DataQueryParams();
        params.setDataElements( getList( deA, deB ) );
        params.setIndicators( getList( inA ) );
        params.setOrganisationUnits( getList( ouA, ouB ) );
        params.setPeriods( getList( createPeriod( "2000Q1" ), createPeriod( "2000Q2" ) ) );

        assertEquals( expected, params.getHeaderDimensions() );
        
        expected = new ArrayList<DimensionalObject>();
        expected.add( new BaseDimensionalObject( ORGUNIT_DIM_ID ) );
        expected.add( new BaseDimensionalObject( PERIOD_DIM_ID ) );

        params = new DataQueryParams();
        params.setOrganisationUnits( getList( ouA, ouB ) );
        params.setPeriods( getList( createPeriod( "2000Q1" ), createPeriod( "2000Q2" ) ) );

        assertEquals( expected, params.getHeaderDimensions() );
    }
    
    @Test
    public void testSetGetCopy()
    {
        List<NameableObject> desA = getList( deA, deB );
        List<NameableObject> ousA = getList( ouA, ouB );
        List<NameableObject> ousB = getList( ouC, ouD );
        List<NameableObject> pesA = getList( createPeriod( "2000Q1" ), createPeriod( "2000Q2" ) );
        List<NameableObject> pesB = getList( createPeriod( "200001" ), createPeriod( "200002" ) );
        
        DataQueryParams paramsA = new DataQueryParams();
        paramsA.setDataElements( desA );
        paramsA.setOrganisationUnits( ousA );
        paramsA.setPeriods( pesA );
        
        DataQueryParams paramsB = paramsA.instance();
        paramsB.setOrganisationUnits( ousB );
        paramsB.setPeriods( pesB );
        
        assertEquals( desA, paramsA.getDataElements() );
        assertEquals( ousA, paramsA.getOrganisationUnits() );
        assertEquals( pesA, paramsA.getPeriods() );
        
        assertEquals( desA, paramsB.getDataElements() );
        assertEquals( ousB, paramsB.getOrganisationUnits() );
        assertEquals( pesB, paramsB.getPeriods() );
    }
    
    @Test
    public void testGetPermutationOperandValueMap()
    {
        DataQueryParams params = new DataQueryParams();
        params.setDataElements( getList( deA, deB ) );
        params.setOrganisationUnits( getList( ouA, ouB ) );
        params.setPeriods( getList( createPeriod( "2000Q1" ), createPeriod( "2000Q2" ) ) );
        params.enableCategoryOptionCombos();
        
        Map<String, Double> aggregatedDataMap = new HashMap<String, Double>();
        aggregatedDataMap.put( deA.getUid() + DIMENSION_SEP + ouA.getUid() + DIMENSION_SEP + "2000Q1" + DIMENSION_SEP + coc.getUid(), 1d );
        aggregatedDataMap.put( deA.getUid() + DIMENSION_SEP + ouA.getUid() + DIMENSION_SEP + "2000Q2" + DIMENSION_SEP + coc.getUid(), 2d );
        aggregatedDataMap.put( deA.getUid() + DIMENSION_SEP + ouB.getUid() + DIMENSION_SEP + "2000Q1" + DIMENSION_SEP + coc.getUid(), 3d );
        aggregatedDataMap.put( deA.getUid() + DIMENSION_SEP + ouB.getUid() + DIMENSION_SEP + "2000Q2" + DIMENSION_SEP + coc.getUid(), 4d );
        aggregatedDataMap.put( deB.getUid() + DIMENSION_SEP + ouA.getUid() + DIMENSION_SEP + "2000Q1" + DIMENSION_SEP + coc.getUid(), 5d );
        aggregatedDataMap.put( deB.getUid() + DIMENSION_SEP + ouA.getUid() + DIMENSION_SEP + "2000Q2" + DIMENSION_SEP + coc.getUid(), 6d );
        aggregatedDataMap.put( deB.getUid() + DIMENSION_SEP + ouB.getUid() + DIMENSION_SEP + "2000Q1" + DIMENSION_SEP + coc.getUid(), 7d );
        aggregatedDataMap.put( deB.getUid() + DIMENSION_SEP + ouB.getUid() + DIMENSION_SEP + "2000Q2" + DIMENSION_SEP + coc.getUid(), 8d );
        
        Map<String, Map<DataElementOperand, Double>> permutationMap = params.getPermutationOperandValueMap( aggregatedDataMap );
        
        assertNotNull( permutationMap );
        
        String ouAQ1Key = ouA.getUid() + DIMENSION_SEP + "2000Q1";
        String ouAQ2Key = ouA.getUid() + DIMENSION_SEP + "2000Q2";
        String ouBQ1Key = ouB.getUid() + DIMENSION_SEP + "2000Q1";
        String ouBQ2Key = ouB.getUid() + DIMENSION_SEP + "2000Q2";
        
        Map<DataElementOperand, Double> ouAQ1 = permutationMap.get( ouAQ1Key );
        Map<DataElementOperand, Double> ouAQ2 = permutationMap.get( ouAQ2Key );
        Map<DataElementOperand, Double> ouBQ1 = permutationMap.get( ouBQ1Key );
        Map<DataElementOperand, Double> ouBQ2 = permutationMap.get( ouBQ2Key );
        
        assertEquals( 2, ouAQ1.size() );
        assertEquals( 2, ouAQ2.size() );
        assertEquals( 2, ouBQ1.size() );
        assertEquals( 2, ouBQ2.size() );
        
        DataElementOperand deACoc = new DataElementOperand( deA.getUid(), coc.getUid() );
        DataElementOperand deBCoc = new DataElementOperand( deB.getUid(), coc.getUid() );
        
        Map<DataElementOperand, Double> ouAQ1Expected = new HashMap<DataElementOperand, Double>();
        ouAQ1Expected.put( deACoc, 1d );
        ouAQ1Expected.put( deBCoc, 5d );

        Map<DataElementOperand, Double> ouAQ2Expected = new HashMap<DataElementOperand, Double>();
        ouAQ2Expected.put( deACoc, 2d );
        ouAQ2Expected.put( deBCoc, 6d );

        Map<DataElementOperand, Double> ouBQ1Expected = new HashMap<DataElementOperand, Double>();
        ouBQ1Expected.put( deACoc, 3d );
        ouBQ1Expected.put( deBCoc, 7d );

        Map<DataElementOperand, Double> ouBQ2Expected = new HashMap<DataElementOperand, Double>();
        ouBQ2Expected.put( deACoc, 4d );
        ouBQ2Expected.put( deBCoc, 8d );
                
        assertEquals( ouAQ1Expected, ouAQ1 );
        assertEquals( ouAQ2Expected, ouAQ2 );
        assertEquals( ouBQ1Expected, ouBQ1 );
        assertEquals( ouBQ2Expected, ouBQ2 );
    }
    
    /**
     * Ignores data element dimension and generates 2 x 3 = 6 combinations based
     * on organisation unit and period dimensions.
     */
    @Test
    public void testGetDimensionOptionPermutations()
    {
        DataQueryParams params = new DataQueryParams();
        params.setDataElements( getList( deA, deB ) );
        params.setOrganisationUnits( getList( ouA, ouB, ouC ) );
        params.setPeriods( getList( createPeriod( "2000Q1" ), createPeriod( "2000Q2" ) ) );
        
        List<List<DimensionItem>> permutations = params.getDimensionItemPermutations();
        
        assertNotNull( permutations );
        assertEquals( 6, permutations.size() );
        
        for ( List<DimensionItem> permutation : permutations )
        {
            assertNotNull( permutation );
            assertEquals( 2, permutation.size() );
            assertEquals( ORGUNIT_DIM_ID, permutation.get( 0 ).getDimension() );
            assertEquals( PERIOD_DIM_ID, permutation.get( 1 ).getDimension() );
        }
    }

    /**
     * First, combines data elements and data sets into one data dimension and 
     * returns (2 + 3) * 3 * 2 = 30. Second, ignores any data dimension and
     * returns 3 * 2 = 6.
     */
    @Test
    public void testGetNumberOfDimensionOptionPermutations()
    {
        DataQueryParams params = new DataQueryParams();
        params.setDataElements( getList( deA, deB ) );
        params.setDataSets( getList( dsA, dsB, dsC ) );
        params.setOrganisationUnits( getList( ouA, ouB, ouC ) );
        params.setPeriods( getList( createPeriod( "2000Q1" ), createPeriod( "2000Q2" ) ) );
        
        assertEquals( 30, params.getNumberOfDimensionOptionPermutations() );

        params = new DataQueryParams();
        params.setOrganisationUnits( getList( ouA, ouB, ouC ) );
        params.setPeriods( getList( createPeriod( "2000Q1" ), createPeriod( "2000Q2" ) ) );

        assertEquals( 6, params.getNumberOfDimensionOptionPermutations() );
    }
    
    @Test
    public void testGetDataPeriodAggregationPeriodMap()
    {
        DataQueryParams params = new DataQueryParams();
        params.setDataElements( getList( deA, deB, deC, deD ) );
        params.setOrganisationUnits( getList( ouA, ouB, ouC, ouD, ouE ) );
        params.setPeriods( getList( createPeriod( "2000Q1" ), createPeriod( "2000Q2" ), createPeriod( "2000Q3" ), createPeriod( "2000Q4" ), createPeriod( "2001Q1" ), createPeriod( "2001Q2" ) ) );
        params.setPeriodType( QuarterlyPeriodType.NAME );
        params.setDataPeriodType( new YearlyPeriodType() );
        
        ListMap<NameableObject, NameableObject> map = params.getDataPeriodAggregationPeriodMap();
        
        assertEquals( 2, map.size() );
        
        assertTrue( map.keySet().contains( createPeriod( "2000" ) ) );
        assertTrue( map.keySet().contains( createPeriod( "2001" ) ) );
        
        assertEquals( 4, map.get( createPeriod( "2000" ) ).size() );
        assertEquals( 2, map.get( createPeriod( "2001" ) ).size() );
        
        assertTrue( map.get( createPeriod( "2000" ) ).contains( createPeriod( "2000Q1" ) ) );
        assertTrue( map.get( createPeriod( "2000" ) ).contains( createPeriod( "2000Q2" ) ) );
        assertTrue( map.get( createPeriod( "2000" ) ).contains( createPeriod( "2000Q3" ) ) );
        assertTrue( map.get( createPeriod( "2000" ) ).contains( createPeriod( "2000Q4" ) ) );

        assertTrue( map.get( createPeriod( "2001" ) ).contains( createPeriod( "2001Q1" ) ) );
        assertTrue( map.get( createPeriod( "2001" ) ).contains( createPeriod( "2001Q2" ) ) );
    }
    
    /**
     * Query spans 2 partitions. Splits in 2 queries for each partition, then
     * splits in 4 queries on data elements to satisfy optimal for a total 
     * of 8 queries, because query has 2 different aggregation types.
     */
    @Test
    public void planQueryA()
    {
        DataQueryParams params = new DataQueryParams();
        params.setDataElements( getList( deA, deB, deC, deD ) );
        params.setOrganisationUnits( getList( ouA, ouB, ouC, ouD, ouE ) );
        params.setPeriods( getList( createPeriod( "2000Q1" ), createPeriod( "2000Q2" ), createPeriod( "2000Q3" ), createPeriod( "2000Q4" ), createPeriod(  "2001Q1" ), createPeriod( "2001Q2" ) ) );
        
        DataQueryGroups queryGroups = queryPlanner.planQuery( params, 4, ANALYTICS_TABLE_NAME );
        
        assertEquals( 8, queryGroups.getAllQueries().size() );
        assertEquals( 2, queryGroups.getSequentialQueries().size() );
        assertEquals( 4, queryGroups.getLargestGroupSize() );
        
        for ( DataQueryParams query : queryGroups.getAllQueries() )
        {
            assertTrue( samePeriodType( query.getPeriods() ) );
            assertTrue( samePartition( query.getPeriods() ) );
            assertDimensionNameNotNull( query );
        }
    }
    
    /**
     * Query spans 3 period types. Splits in 3 queries for each period type, then
     * splits in 2 queries on organisation units to satisfy optimal for a total 
     * of 6 queries.
     */
    @Test
    public void planQueryB()
    {
        DataQueryParams params = new DataQueryParams();
        params.setDataElements( getList( deA ) );
        params.setOrganisationUnits( getList( ouA, ouB, ouC, ouD, ouE ) );
        params.setPeriods( getList( createPeriod( "2000Q1" ), createPeriod( "2000Q2" ), createPeriod( "2000" ), createPeriod( "200002" ), createPeriod( "200003" ), createPeriod( "200004" ) ) );
        
        DataQueryGroups queryGroups = queryPlanner.planQuery( params, 6, ANALYTICS_TABLE_NAME );
        
        assertEquals( 6, queryGroups.getAllQueries().size() );
        assertEquals( 1, queryGroups.getSequentialQueries().size() );
        assertEquals( 6, queryGroups.getLargestGroupSize() );
        
        for ( DataQueryParams query : queryGroups.getAllQueries() )
        {
            assertTrue( samePeriodType( query.getPeriods() ) );
            assertTrue( samePartition( query.getPeriods() ) );
            assertDimensionNameNotNull( query );
        }
    }
    
    /**
     * Query spans 3 organisation unit levels. Splits in 3 queries for each level,
     * then splits in 2 queries on organisation units to satisfy optimal for a total 
     * of 5 queries, as there are only 5 organisation units in total.
     */
    @Test
    public void planQueryC()
    {
        ouB.setParent( ouA );
        ouC.setParent( ouA );
        ouD.setParent( ouB );
        ouE.setParent( ouC );
        ouA.getChildren().add( ouB );
        ouA.getChildren().add( ouC );
        ouD.getChildren().add( ouB );
        ouC.getChildren().add( ouE );
        organisationUnitService.updateOrganisationUnit( ouA );
        organisationUnitService.updateOrganisationUnit( ouB );
        organisationUnitService.updateOrganisationUnit( ouC );
        organisationUnitService.updateOrganisationUnit( ouD );
        organisationUnitService.updateOrganisationUnit( ouE );
        
        DataQueryParams params = new DataQueryParams();
        params.setDataElements( getList( deA ) );
        params.setOrganisationUnits( getList( ouA, ouB, ouC, ouD, ouE ) );
        params.setPeriods( getList( createPeriod( "2000Q1" ), createPeriod( "2000Q2" ), createPeriod( "2000Q3" ) ) );
        
        DataQueryGroups queryGroups = queryPlanner.planQuery( params, 6, ANALYTICS_TABLE_NAME );
        
        assertEquals( 5, queryGroups.getAllQueries().size() );
        assertEquals( 1, queryGroups.getSequentialQueries().size() );
        assertEquals( 5, queryGroups.getLargestGroupSize() );
        
        for ( DataQueryParams query : queryGroups.getAllQueries() )
        {
            assertTrue( samePeriodType( query.getPeriods() ) );
            assertTrue( samePartition( query.getPeriods() ) );
            assertDimensionNameNotNull( query );
        }
    }
    
    /**
     * Splits on 3 data elements.
     */
    @Test
    public void planQueryD()
    {
        DataQueryParams params = new DataQueryParams();
        params.setDataElements( getList( deA, deB, deC ) );
        params.setOrganisationUnits( getList( ouA ) );
        params.setPeriods( getList( createPeriod( "200001" ), createPeriod( "200002" ), createPeriod( "200003" ), createPeriod( "200004" ),
            createPeriod( "200005" ), createPeriod( "200006" ), createPeriod( "200007" ), createPeriod( "200008" ), createPeriod( "200009" ) ) );
        
        DataQueryGroups queryGroups = queryPlanner.planQuery( params, 4, ANALYTICS_TABLE_NAME );
        
        assertEquals( 3, queryGroups.getAllQueries().size() );
        assertEquals( 2, queryGroups.getSequentialQueries().size() );
        assertEquals( 2, queryGroups.getLargestGroupSize() );
        
        for ( DataQueryParams query : queryGroups.getAllQueries() )
        {
            assertTrue( samePeriodType( query.getPeriods() ) );
            assertTrue( samePartition( query.getPeriods() ) );
            assertDimensionNameNotNull( query );
        }
    }
    
    /**
     * Splits on 3 data elements. No organisation units specified.
     */
    @Test
    public void planQueryE()
    {
        DataQueryParams params = new DataQueryParams();
        params.setDataElements( getList( deA, deB, deC ) );
        params.setPeriods( getList( createPeriod( "200001" ), createPeriod( "200002" ), createPeriod( "200003" ), createPeriod( "200004" ), 
            createPeriod( "200005" ), createPeriod( "200006" ), createPeriod( "200007" ), createPeriod( "200008" ), createPeriod( "200009" ) ) );

        DataQueryGroups queryGroups = queryPlanner.planQuery( params, 4, ANALYTICS_TABLE_NAME );

        assertEquals( 3, queryGroups.getAllQueries().size() );
        assertEquals( 2, queryGroups.getSequentialQueries().size() );
        assertEquals( 2, queryGroups.getLargestGroupSize() );

        for ( DataQueryParams query : queryGroups.getAllQueries() )
        {
            assertTrue( samePeriodType( query.getPeriods() ) );
            assertTrue( samePartition( query.getPeriods() ) );
            assertDimensionNameNotNull( query );
        }
    }

    /**
     * Splits on 3 queries on organisation units for an optimal of 4 queries. No 
     * data elements specified.
     */
    @Test
    public void planQueryF()
    {
        DataQueryParams params = new DataQueryParams();
        params.setOrganisationUnits( getList( ouA, ouB, ouC, ouD, ouE ) );
        params.setPeriods( getList( createPeriod( "200001" ), createPeriod( "200002" ), createPeriod( "200003" ), createPeriod( "200004" ), 
            createPeriod( "200005" ), createPeriod( "200006" ), createPeriod( "200007" ), createPeriod( "200008" ), createPeriod( "200009" ) ) );

        DataQueryGroups queryGroups = queryPlanner.planQuery( params, 4, ANALYTICS_TABLE_NAME );

        assertEquals( 3, queryGroups.getAllQueries().size() );
        assertEquals( 1, queryGroups.getSequentialQueries().size() );
        assertEquals( 3, queryGroups.getLargestGroupSize() );

        for ( DataQueryParams query : queryGroups.getAllQueries() )
        {
            assertTrue( samePeriodType( query.getPeriods() ) );
            assertTrue( samePartition( query.getPeriods() ) );
            assertDimensionNameNotNull( query );
        }
    }
    
    /**
     * Expected to fail because of no periods specified.
     */
    @Test( expected = IllegalQueryException.class )
    public void planQueryG()
    {
        DataQueryParams params = new DataQueryParams();
        params.setDataElements( getList( deA, deB, deC ) );
        params.setOrganisationUnits( getList( ouA, ouB, ouC, ouD, ouE ) );

        queryPlanner.planQuery( params, 4, ANALYTICS_TABLE_NAME );
    }

    /**
     * Query filters span 2 partitions. Splits in 4 queries on data elements to 
     * satisfy optimal for a total of 8 queries, because query has 2 different 
     * aggregation types.
     */
    @Test
    public void planQueryH()
    {
        DataQueryParams params = new DataQueryParams();
        params.setDataElements( getList( deA, deB, deC, deD ) );
        params.setOrganisationUnits( getList( ouA, ouB, ouC, ouD, ouE ) );
        params.setFilterPeriods( getList( createPeriod( "2000Q1" ), createPeriod( "2000Q2" ), createPeriod( "2000Q3" ), createPeriod( "2000Q4" ), createPeriod( "2001Q1" ), createPeriod( "2001Q2" ) ) );
        
        DataQueryGroups queryGroups = queryPlanner.planQuery( params, 4, ANALYTICS_TABLE_NAME );
        
        assertEquals( 8, queryGroups.getAllQueries().size() );
        assertEquals( 2, queryGroups.getSequentialQueries().size() );
        assertEquals( 4, queryGroups.getLargestGroupSize() );

        for ( DataQueryParams query : queryGroups.getAllQueries() )
        {
            assertDimensionNameNotNull( query );

            assertTrue( query.spansMultiplePartitions() );
        }
    }

    /**
     * Query spans 3 period types. Splits in 3 queries for each period type, then
     * splits in 4 queries on data elements units to satisfy optimal for a total 
     * of 12 queries, because query has 2 different  aggregation types.
     */
    @Test
    public void planQueryI()
    {
        DataQueryParams params = new DataQueryParams();
        params.setDataElements( getList( deA, deB, deC, deD ) );
        params.setOrganisationUnits( getList( ouA, ouB, ouC, ouD, ouE ) );
        params.setPeriods( getList( createPeriod( "2000Q1" ), createPeriod( "2000Q2" ), createPeriod( "2000" ), createPeriod( "200002" ), createPeriod( "200003" ), createPeriod( "200004" ) ) );
        
        DataQueryGroups queryGroups = queryPlanner.planQuery( params, 6, ANALYTICS_TABLE_NAME );
        
        assertEquals( 12, queryGroups.getAllQueries().size() );
        assertEquals( 2, queryGroups.getSequentialQueries().size() );
        assertEquals( 6, queryGroups.getLargestGroupSize() );

        for ( DataQueryParams query : queryGroups.getAllQueries() )
        {
            assertTrue( samePeriodType( query.getPeriods() ) );
            assertTrue( samePartition( query.getPeriods() ) );
            assertDimensionNameNotNull( query );
        }
    }

    /**
     * No periods specified, illegal query.
     */
    @Test( expected=IllegalQueryException.class )
    public void planQueryJ()
    {
        DataQueryParams params = new DataQueryParams();
        params.setDataElements( getList( deA, deB, deC, deD ) );
        params.setOrganisationUnits( getList( ouA, ouB, ouC, ouD, ouE ) );
        
        queryPlanner.planQuery( params, 4, ANALYTICS_TABLE_NAME );
    }

    /**
     * Query spans 2 partitions. Splits in 2 queries for each partition, then
     * splits in 2 queries on data sets to satisfy optimal for a total 
     * of 4 queries.
     */
    @Test
    public void planQueryK()
    {
        DataQueryParams params = new DataQueryParams();
        params.setDataSets( getList( dsA, dsB, dsC, dsD ) );
        params.setOrganisationUnits( getList( ouA, ouB, ouC, ouD, ouE ) );
        params.setPeriods( getList( createPeriod( "2000Q1" ), createPeriod( "2000Q2" ), createPeriod( "2000Q3" ), createPeriod( "2000Q4" ), createPeriod(  "2001Q1" ), createPeriod( "2001Q2" ) ) );
        
        List<DataQueryParams> queries = queryPlanner.planQuery( params, 4, ANALYTICS_TABLE_NAME ).getAllQueries();
        
        assertEquals( 4, queries.size() );
        
        for ( DataQueryParams query : queries )
        {
            assertTrue( samePeriodType( query.getPeriods() ) );
            assertTrue( samePartition( query.getPeriods() ) );
            assertDimensionNameNotNull( query );
        }
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private static boolean samePeriodType( List<NameableObject> isoPeriods )
    {
        Iterator<NameableObject> periods = new ArrayList<NameableObject>( isoPeriods ).iterator();
        
        PeriodType first = ((Period) periods.next()).getPeriodType();
        
        while ( periods.hasNext() )
        {
            PeriodType next = ((Period) periods.next()).getPeriodType();
            
            if ( !first.equals( next ) )
            {   
                return false;
            }
        }
        
        return true;
    }
    
    private static boolean samePartition( List<NameableObject> isoPeriods )
    {
        Iterator<NameableObject> periods = new ArrayList<NameableObject>( isoPeriods ).iterator();
        
        int year = new Cal().set( ((Period) periods.next()).getStartDate() ).getYear();
        
        while ( periods.hasNext() )
        {
            int next = new Cal().set( ((Period) periods.next()).getStartDate() ).getYear();
            
            if ( year != next )
            {   
                return false;
            }
        }
        
        return true;
    }
    
    private static void assertDimensionNameNotNull( DataQueryParams params )
    {
        for ( DimensionalObject dim : params.getDimensions() )
        {
            assertNotNull( dim.getDimensionName() );
        }
        
        for ( DimensionalObject filter : params.getFilters() )
        {
            assertNotNull( filter.getDimensionName() );
        }
    }
}
