package org.hisp.dhis.reporttable;

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

import static org.hisp.dhis.common.DimensionalObject.DIMENSION_SEP;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.aggregation.AggregatedDataValue;
import org.hisp.dhis.aggregation.AggregatedIndicatorValue;
import org.hisp.dhis.analytics.AnalyticsService;
import org.hisp.dhis.analytics.data.MockAnalyticsService;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.completeness.DataSetCompletenessResult;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.jdbc.batchhandler.AggregatedDataValueBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.AggregatedIndicatorValueBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataSetCompletenessResultBatchHandler;
import org.hisp.dhis.mock.MockI18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 */
public class ReportTableGridTest
    extends DhisSpringTest
{
    private ReportTableService reportTableService;
    
    private BatchHandlerFactory batchHandlerFactory;
    
    private AnalyticsService analyticsService;
        
    private Map<String, Double> valueMap;
    
    private List<DataElement> dataElements;
    private List<DataElementCategoryOptionCombo> categoryOptionCombos;
    private List<Indicator> indicators;
    private List<DataSet> dataSets;
    private List<Period> periods;
    private List<OrganisationUnit> units;

    private PeriodType montlyPeriodType;

    private DataElement dataElementA;
    private DataElement dataElementB;

    private DataElementCategoryOption categoryOptionA;
    private DataElementCategoryOption categoryOptionB;
    
    private DataElementCategory categoryA;
    
    private DataElementCategoryCombo categoryComboA;
    
    private DataElementCategoryOptionCombo categoryOptionComboA;
    private DataElementCategoryOptionCombo categoryOptionComboB;  

    private IndicatorType indicatorType;
    
    private Indicator indicatorA;
    private Indicator indicatorB;
    
    private DataSet dataSetA;
    private DataSet dataSetB;
    
    private Period periodA;
    private Period periodB;
    
    private OrganisationUnit unitA;
    private OrganisationUnit unitB;
    
    private int dataElementIdA;
    private int dataElementIdB;
    
    private int categoryOptionComboIdA;
    
    private int indicatorIdA;
    private int indicatorIdB;
    
    private int dataSetIdA;
    private int dataSetIdB;
    
    private int periodIdA;
    private int periodIdB;
    
    private int unitIdA;
    private int unitIdB;
        
    private I18nFormat i18nFormat;
    
    private Date date = new Date();

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
        throws Exception
    {
        reportTableService = (ReportTableService) getBean( ReportTableService.ID );
        
        dataElementService = (DataElementService) getBean( DataElementService.ID );
        
        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );        
        
        indicatorService = (IndicatorService) getBean( IndicatorService.ID );
        dataSetService = (DataSetService) getBean( DataSetService.ID );
        periodService = (PeriodService) getBean( PeriodService.ID );
        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );
        organisationUnitGroupService = (OrganisationUnitGroupService) getBean( OrganisationUnitGroupService.ID );
        
        batchHandlerFactory = (BatchHandlerFactory) getBean( "batchHandlerFactory" );
        
        dataElements = new ArrayList<DataElement>();
        categoryOptionCombos = new ArrayList<DataElementCategoryOptionCombo>();
        indicators = new ArrayList<Indicator>();
        dataSets = new ArrayList<DataSet>();
        periods = new ArrayList<Period>();
        units = new ArrayList<OrganisationUnit>();
        
        montlyPeriodType = PeriodType.getPeriodTypeByName( MonthlyPeriodType.NAME );       

        // ---------------------------------------------------------------------
        // Mock injection
        // ---------------------------------------------------------------------

        valueMap = new HashMap<String, Double>();
        
        analyticsService = new MockAnalyticsService( valueMap );
        
        setDependency( reportTableService, "analyticsService", analyticsService );

        // ---------------------------------------------------------------------
        // Setup Dimensions
        // ---------------------------------------------------------------------

        categoryOptionA = new DataElementCategoryOption( "Male" );
        categoryOptionB = new DataElementCategoryOption( "Female" );
        
        categoryService.addDataElementCategoryOption( categoryOptionA );
        categoryService.addDataElementCategoryOption( categoryOptionB );

        categoryA = new DataElementCategory( "Gender" );
        categoryA.getCategoryOptions().add( categoryOptionA );
        categoryA.getCategoryOptions().add( categoryOptionB );

        categoryService.addDataElementCategory( categoryA );

        categoryComboA = new DataElementCategoryCombo( "Gender" );
        categoryComboA.getCategories().add( categoryA );        
        
        categoryService.addDataElementCategoryCombo( categoryComboA );
        
        categoryService.generateOptionCombos( categoryComboA );

        Iterator<DataElementCategoryOptionCombo> iter = categoryComboA.getOptionCombos().iterator();
        categoryOptionComboA = iter.next();
        categoryOptionComboB = iter.next();
        
        categoryOptionComboA.getCategoryOptions().iterator().next().setCategoryOptionCombos( getSet( categoryOptionComboA ) ); // Inverse association not set before transaction is committed
        categoryOptionComboB.getCategoryOptions().iterator().next().setCategoryOptionCombos( getSet( categoryOptionComboB ) );
        
        categoryOptionComboIdA = categoryOptionComboA.getId();

        categoryOptionCombos.add( categoryOptionComboA );        
        categoryOptionCombos.add( categoryOptionComboB );
        
        // ---------------------------------------------------------------------
        // Setup DataElements
        // ---------------------------------------------------------------------

        dataElementA = createDataElement( 'A' );
        dataElementB = createDataElement( 'B' );
        
        dataElementIdA = dataElementService.addDataElement( dataElementA );
        dataElementIdB = dataElementService.addDataElement( dataElementB );
                
        dataElements.add( dataElementA );
        dataElements.add( dataElementB );

        // ---------------------------------------------------------------------
        // Setup Indicators
        // ---------------------------------------------------------------------

        indicatorType = createIndicatorType( 'A' );
        
        indicatorService.addIndicatorType( indicatorType );
        
        indicatorA = createIndicator( 'A', indicatorType );
        indicatorB = createIndicator( 'B', indicatorType );
        
        indicatorIdA = indicatorService.addIndicator( indicatorA );
        indicatorIdB = indicatorService.addIndicator( indicatorB );
                
        indicators.add( indicatorA );
        indicators.add( indicatorB );

        // ---------------------------------------------------------------------
        // Setup DataSets
        // ---------------------------------------------------------------------

        dataSetA = createDataSet( 'A', montlyPeriodType );
        dataSetB = createDataSet( 'B', montlyPeriodType );
        
        dataSetIdA = dataSetService.addDataSet( dataSetA );
        dataSetIdB = dataSetService.addDataSet( dataSetB );
        
        dataSets.add( dataSetA );
        dataSets.add( dataSetB );

        // ---------------------------------------------------------------------
        // Setup Periods
        // ---------------------------------------------------------------------

        periodA = createPeriod( montlyPeriodType, getDate( 2008, 1, 1 ), getDate( 2008, 1, 31 ) );
        periodB = createPeriod( montlyPeriodType, getDate( 2008, 2, 1 ), getDate( 2008, 2, 28 ) );
        
        periodIdA = periodService.addPeriod( periodA );
        periodIdB = periodService.addPeriod( periodB );
                
        periods.add( periodA );
        periods.add( periodB );
        
        // ---------------------------------------------------------------------
        // Setup OrganisationUnits
        // ---------------------------------------------------------------------

        unitA = createOrganisationUnit( 'A' );
        unitB = createOrganisationUnit( 'B' );
        
        unitIdA = organisationUnitService.addOrganisationUnit( unitA );
        unitIdB = organisationUnitService.addOrganisationUnit( unitB );
        
        units.add( unitA );
        units.add( unitB );

        // ---------------------------------------------------------------------
        // Setup OrganisationUnitGroups
        // ---------------------------------------------------------------------

        i18nFormat = new MockI18nFormat();

        BatchHandler<AggregatedDataValue> dataValueBatchHandler = batchHandlerFactory.createBatchHandler( AggregatedDataValueBatchHandler.class ).init();
        
        dataValueBatchHandler.addObject( new AggregatedDataValue( dataElementIdA, categoryOptionComboIdA, periodIdA, 8, unitIdA, 8, 11 ) );
        dataValueBatchHandler.addObject( new AggregatedDataValue( dataElementIdA, categoryOptionComboIdA, periodIdA, 8, unitIdB, 8, 12 ) );
        dataValueBatchHandler.addObject( new AggregatedDataValue( dataElementIdA, categoryOptionComboIdA, periodIdB, 8, unitIdA, 8, 13 ) );
        dataValueBatchHandler.addObject( new AggregatedDataValue( dataElementIdA, categoryOptionComboIdA, periodIdB, 8, unitIdB, 8, 14 ) );
        dataValueBatchHandler.addObject( new AggregatedDataValue( dataElementIdB, categoryOptionComboIdA, periodIdA, 8, unitIdA, 8, 15 ) );
        dataValueBatchHandler.addObject( new AggregatedDataValue( dataElementIdB, categoryOptionComboIdA, periodIdA, 8, unitIdB, 8, 16 ) );
        dataValueBatchHandler.addObject( new AggregatedDataValue( dataElementIdB, categoryOptionComboIdA, periodIdB, 8, unitIdA, 8, 17 ) );
        dataValueBatchHandler.addObject( new AggregatedDataValue( dataElementIdB, categoryOptionComboIdA, periodIdB, 8, unitIdB, 8, 18 ) );  
        
        dataValueBatchHandler.flush();
        
        BatchHandler<AggregatedIndicatorValue> indicatorValueBatchHandler = batchHandlerFactory.createBatchHandler( AggregatedIndicatorValueBatchHandler.class ).init();
        
        indicatorValueBatchHandler.addObject( new AggregatedIndicatorValue( indicatorIdA, periodIdA, 8, unitIdA, 8, "", 1, 11, 0, 0 ) );
        indicatorValueBatchHandler.addObject( new AggregatedIndicatorValue( indicatorIdA, periodIdA, 8, unitIdB, 8, "", 1, 12, 0, 0 ) );
        indicatorValueBatchHandler.addObject( new AggregatedIndicatorValue( indicatorIdA, periodIdB, 8, unitIdA, 8, "", 1, 13, 0, 0 ) );
        indicatorValueBatchHandler.addObject( new AggregatedIndicatorValue( indicatorIdA, periodIdB, 8, unitIdB, 8, "", 1, 14, 0, 0 ) );
        indicatorValueBatchHandler.addObject( new AggregatedIndicatorValue( indicatorIdB, periodIdA, 8, unitIdA, 8, "", 1, 15, 0, 0 ) );
        indicatorValueBatchHandler.addObject( new AggregatedIndicatorValue( indicatorIdB, periodIdA, 8, unitIdB, 8, "", 1, 16, 0, 0 ) );
        indicatorValueBatchHandler.addObject( new AggregatedIndicatorValue( indicatorIdB, periodIdB, 8, unitIdA, 8, "", 1, 17, 0, 0 ) );
        indicatorValueBatchHandler.addObject( new AggregatedIndicatorValue( indicatorIdB, periodIdB, 8, unitIdB, 8, "", 1, 18, 0, 0 ) );
        
        indicatorValueBatchHandler.flush();
        
        BatchHandler<DataSetCompletenessResult> completenessBatchHandler = batchHandlerFactory.createBatchHandler( DataSetCompletenessResultBatchHandler.class ).init();
        
        completenessBatchHandler.addObject( new DataSetCompletenessResult( dataSetIdA, periodIdA, null, unitIdA, null, 100, 11, 11 ) );
        completenessBatchHandler.addObject( new DataSetCompletenessResult( dataSetIdA, periodIdA, null, unitIdB, null, 100, 12, 12 ) );
        completenessBatchHandler.addObject( new DataSetCompletenessResult( dataSetIdA, periodIdB, null, unitIdA, null, 100, 13, 13 ) );
        completenessBatchHandler.addObject( new DataSetCompletenessResult( dataSetIdA, periodIdB, null, unitIdB, null, 100, 14, 14 ) );
        completenessBatchHandler.addObject( new DataSetCompletenessResult( dataSetIdB, periodIdA, null, unitIdA, null, 100, 15, 15 ) );
        completenessBatchHandler.addObject( new DataSetCompletenessResult( dataSetIdB, periodIdA, null, unitIdB, null, 100, 16, 16 ) );
        completenessBatchHandler.addObject( new DataSetCompletenessResult( dataSetIdB, periodIdB, null, unitIdA, null, 100, 17, 17 ) );
        completenessBatchHandler.addObject( new DataSetCompletenessResult( dataSetIdB, periodIdB, null, unitIdB, null, 100, 18, 18 ) );
        
        completenessBatchHandler.flush();
    }
    
    private Set<DataElementCategoryOptionCombo> getSet( DataElementCategoryOptionCombo c )
    {
        return new HashSet<DataElementCategoryOptionCombo>( Arrays.asList( c ) );
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void testGetIndicatorReportTableA()
    {
        valueMap.put( indicatorA.getUid() + DIMENSION_SEP + periodA.getUid() + DIMENSION_SEP + unitA.getUid(), 11d );
        valueMap.put( indicatorA.getUid() + DIMENSION_SEP + periodA.getUid() + DIMENSION_SEP + unitB.getUid(), 12d );
        valueMap.put( indicatorA.getUid() + DIMENSION_SEP + periodB.getUid() + DIMENSION_SEP + unitA.getUid(), 13d );
        valueMap.put( indicatorA.getUid() + DIMENSION_SEP + periodB.getUid() + DIMENSION_SEP + unitB.getUid(), 14d );
        valueMap.put( indicatorB.getUid() + DIMENSION_SEP + periodA.getUid() + DIMENSION_SEP + unitA.getUid(), 15d );
        valueMap.put( indicatorB.getUid() + DIMENSION_SEP + periodA.getUid() + DIMENSION_SEP + unitB.getUid(), 16d );
        valueMap.put( indicatorB.getUid() + DIMENSION_SEP + periodB.getUid() + DIMENSION_SEP + unitA.getUid(), 17d );
        valueMap.put( indicatorB.getUid() + DIMENSION_SEP + periodB.getUid() + DIMENSION_SEP + unitB.getUid(), 18d );
                
        ReportTable reportTable = new ReportTable( "Prescriptions",
            new ArrayList<DataElement>(), indicators, new ArrayList<DataSet>(), periods, units, 
            true, true, false, null, null, "january_2000" );

        reportTableService.saveReportTable( reportTable );

        Grid grid = reportTableService.getReportTableGrid( reportTable.getUid(), i18nFormat, null, "0" );
        
        assertEquals( 11d, grid.getRow( 0 ).get( 7 ) );
        assertEquals( 13d, grid.getRow( 0 ).get( 8 ) );
        assertEquals( 15d, grid.getRow( 0 ).get( 9 ) );
        assertEquals( 17d, grid.getRow( 0 ).get( 10 ) );
        
        assertEquals( 12d, grid.getRow( 1 ).get( 7 ) );
        assertEquals( 14d, grid.getRow( 1 ).get( 8 ) );
        assertEquals( 16d, grid.getRow( 1 ).get( 9 ) );
        assertEquals( 18d, grid.getRow( 1 ).get( 10 ) );
    }
    
    @Test
    public void testGetIndicatorReportTableB()
    {
        valueMap.put( unitA.getUid() + DIMENSION_SEP + indicatorA.getUid() + DIMENSION_SEP + periodA.getUid(), 11d );
        valueMap.put( unitA.getUid() + DIMENSION_SEP + indicatorA.getUid() + DIMENSION_SEP + periodB.getUid(), 12d );
        valueMap.put( unitA.getUid() + DIMENSION_SEP + indicatorB.getUid() + DIMENSION_SEP + periodA.getUid(), 13d );
        valueMap.put( unitA.getUid() + DIMENSION_SEP + indicatorB.getUid() + DIMENSION_SEP + periodB.getUid(), 14d );
        valueMap.put( unitB.getUid() + DIMENSION_SEP + indicatorA.getUid() + DIMENSION_SEP + periodA.getUid(), 15d );
        valueMap.put( unitB.getUid() + DIMENSION_SEP + indicatorA.getUid() + DIMENSION_SEP + periodB.getUid(), 16d );
        valueMap.put( unitB.getUid() + DIMENSION_SEP + indicatorB.getUid() + DIMENSION_SEP + periodA.getUid(), 17d );
        valueMap.put( unitB.getUid() + DIMENSION_SEP + indicatorB.getUid() + DIMENSION_SEP + periodB.getUid(), 18d );
        
        ReportTable reportTable = new ReportTable( "Embezzlement",
            new ArrayList<DataElement>(), indicators, new ArrayList<DataSet>(), periods, units, 
            false, false, true, null, null, "january_2000" );

        reportTableService.saveReportTable( reportTable );

        Grid grid = reportTableService.getReportTableGrid( reportTable.getUid(), i18nFormat, date, "0" );

        assertEquals( 11d, grid.getRow( 0 ).get( 11 ) );
        assertEquals( 15d, grid.getRow( 0 ).get( 12 ) );
        
        assertEquals( 12d, grid.getRow( 1 ).get( 11 ) );
        assertEquals( 16d, grid.getRow( 1 ).get( 12 ) );
        
        assertEquals( 13d, grid.getRow( 2 ).get( 11 ) );
        assertEquals( 17d, grid.getRow( 2 ).get( 12 ) );
        
        assertEquals( 14d, grid.getRow( 3 ).get( 11 ) );
        assertEquals( 18d, grid.getRow( 3 ).get( 12 ) );
    }

    @Test
    public void testGetIndicatorReportTableC()
    {
        valueMap.put( indicatorA.getUid() + DIMENSION_SEP + unitA.getUid() + DIMENSION_SEP + periodA.getUid(), 11d );
        valueMap.put( indicatorA.getUid() + DIMENSION_SEP + unitA.getUid() + DIMENSION_SEP + periodB.getUid(), 12d );
        valueMap.put( indicatorA.getUid() + DIMENSION_SEP + unitB.getUid() + DIMENSION_SEP + periodA.getUid(), 13d );
        valueMap.put( indicatorA.getUid() + DIMENSION_SEP + unitB.getUid() + DIMENSION_SEP + periodB.getUid(), 14d );
        valueMap.put( indicatorB.getUid() + DIMENSION_SEP + unitA.getUid() + DIMENSION_SEP + periodA.getUid(), 15d );
        valueMap.put( indicatorB.getUid() + DIMENSION_SEP + unitA.getUid() + DIMENSION_SEP + periodB.getUid(), 16d );
        valueMap.put( indicatorB.getUid() + DIMENSION_SEP + unitB.getUid() + DIMENSION_SEP + periodA.getUid(), 17d );
        valueMap.put( indicatorB.getUid() + DIMENSION_SEP + unitB.getUid() + DIMENSION_SEP + periodB.getUid(), 18d );
                
        ReportTable reportTable = new ReportTable( "Embezzlement",
            new ArrayList<DataElement>(), indicators, new ArrayList<DataSet>(), periods, units, 
            true, false, true, null, null, "january_2000" );

        reportTableService.saveReportTable( reportTable );

        Grid grid = reportTableService.getReportTableGrid( reportTable.getUid(), i18nFormat, date, "0" );

        assertEquals( 11d, grid.getRow( 0 ).get( 7 ) );
        assertEquals( 13d, grid.getRow( 0 ).get( 8 ) );
        assertEquals( 15d, grid.getRow( 0 ).get( 9 ) );
        assertEquals( 17d, grid.getRow( 0 ).get( 10) );
        
        assertEquals( 12d, grid.getRow( 1 ).get( 7 ) );
        assertEquals( 14d, grid.getRow( 1 ).get( 8 ) );
        assertEquals( 16d, grid.getRow( 1 ).get( 9 ) );
        assertEquals( 18d, grid.getRow( 1 ).get( 10 ) );
    }
    
    @Test
    public void testGetDataElementReportTableA()
    {
        valueMap.put( dataElementA.getUid() + DIMENSION_SEP + periodA.getUid() + DIMENSION_SEP + unitA.getUid(), 11d );
        valueMap.put( dataElementA.getUid() + DIMENSION_SEP + periodA.getUid() + DIMENSION_SEP + unitB.getUid(), 12d );
        valueMap.put( dataElementA.getUid() + DIMENSION_SEP + periodB.getUid() + DIMENSION_SEP + unitA.getUid(), 13d );
        valueMap.put( dataElementA.getUid() + DIMENSION_SEP + periodB.getUid() + DIMENSION_SEP + unitB.getUid(), 14d );
        valueMap.put( dataElementB.getUid() + DIMENSION_SEP + periodA.getUid() + DIMENSION_SEP + unitA.getUid(), 15d );
        valueMap.put( dataElementB.getUid() + DIMENSION_SEP + periodA.getUid() + DIMENSION_SEP + unitB.getUid(), 16d );
        valueMap.put( dataElementB.getUid() + DIMENSION_SEP + periodB.getUid() + DIMENSION_SEP + unitA.getUid(), 17d );
        valueMap.put( dataElementB.getUid() + DIMENSION_SEP + periodB.getUid() + DIMENSION_SEP + unitB.getUid(), 18d );
                
        ReportTable reportTable = new ReportTable( "Prescriptions",
            dataElements, new ArrayList<Indicator>(), new ArrayList<DataSet>(), periods, units, 
            true, true, false, null, null, "january_2000" );

        reportTableService.saveReportTable( reportTable );

        Grid grid = reportTableService.getReportTableGrid( reportTable.getUid(), i18nFormat, date, "0" );

        assertEquals( 11d, grid.getRow( 0 ).get( 7 ) );
        assertEquals( 13d, grid.getRow( 0 ).get( 8 ) );
        assertEquals( 15d, grid.getRow( 0 ).get( 9 ) );
        assertEquals( 17d, grid.getRow( 0 ).get( 10 ) );
        
        assertEquals( 12d, grid.getRow( 1 ).get( 7 ) );
        assertEquals( 14d, grid.getRow( 1 ).get( 8 ) );
        assertEquals( 16d, grid.getRow( 1 ).get( 9 ) );
        assertEquals( 18d, grid.getRow( 1 ).get( 10 ) );
    }
    
    @Test
    public void testGetDataElementReportTableB()
    {
        valueMap.put( unitA.getUid() + DIMENSION_SEP + dataElementA.getUid() + DIMENSION_SEP + periodA.getUid(), 11d );
        valueMap.put( unitA.getUid() + DIMENSION_SEP + dataElementA.getUid() + DIMENSION_SEP + periodB.getUid(), 12d );
        valueMap.put( unitA.getUid() + DIMENSION_SEP + dataElementB.getUid() + DIMENSION_SEP + periodA.getUid(), 13d );
        valueMap.put( unitA.getUid() + DIMENSION_SEP + dataElementB.getUid() + DIMENSION_SEP + periodB.getUid(), 14d );
        valueMap.put( unitB.getUid() + DIMENSION_SEP + dataElementA.getUid() + DIMENSION_SEP + periodA.getUid(), 15d );
        valueMap.put( unitB.getUid() + DIMENSION_SEP + dataElementA.getUid() + DIMENSION_SEP + periodB.getUid(), 16d );
        valueMap.put( unitB.getUid() + DIMENSION_SEP + dataElementB.getUid() + DIMENSION_SEP + periodA.getUid(), 17d );
        valueMap.put( unitB.getUid() + DIMENSION_SEP + dataElementB.getUid() + DIMENSION_SEP + periodB.getUid(), 18d );
        
        ReportTable reportTable = new ReportTable( "Embezzlement",
            dataElements, new ArrayList<Indicator>(), new ArrayList<DataSet>(), periods, units, 
            false, false, true, null, null, "january_2000" );

        reportTableService.saveReportTable( reportTable );

        Grid grid = reportTableService.getReportTableGrid( reportTable.getUid(), i18nFormat, date, "0" );

        assertEquals( 11d, grid.getRow( 0 ).get( 11 ) );
        assertEquals( 15d, grid.getRow( 0 ).get( 12 ) );
        
        assertEquals( 12d, grid.getRow( 1 ).get( 11 ) );
        assertEquals( 16d, grid.getRow( 1 ).get( 12 ) );
        
        assertEquals( 13d, grid.getRow( 2 ).get( 11 ) );
        assertEquals( 17d, grid.getRow( 2 ).get( 12 ) );
        
        assertEquals( 14d, grid.getRow( 3 ).get( 11 ) );
        assertEquals( 18d, grid.getRow( 3 ).get( 12 ) );
    }

    @Test
    public void testGetDataElementReportTableC()
    {
        valueMap.put( dataElementA.getUid() + DIMENSION_SEP + unitA.getUid() + DIMENSION_SEP + periodA.getUid(), 11d );
        valueMap.put( dataElementA.getUid() + DIMENSION_SEP + unitA.getUid() + DIMENSION_SEP + periodB.getUid(), 12d );
        valueMap.put( dataElementA.getUid() + DIMENSION_SEP + unitB.getUid() + DIMENSION_SEP + periodA.getUid(), 13d );
        valueMap.put( dataElementA.getUid() + DIMENSION_SEP + unitB.getUid() + DIMENSION_SEP + periodB.getUid(), 14d );
        valueMap.put( dataElementB.getUid() + DIMENSION_SEP + unitA.getUid() + DIMENSION_SEP + periodA.getUid(), 15d );
        valueMap.put( dataElementB.getUid() + DIMENSION_SEP + unitA.getUid() + DIMENSION_SEP + periodB.getUid(), 16d );
        valueMap.put( dataElementB.getUid() + DIMENSION_SEP + unitB.getUid() + DIMENSION_SEP + periodA.getUid(), 17d );
        valueMap.put( dataElementB.getUid() + DIMENSION_SEP + unitB.getUid() + DIMENSION_SEP + periodB.getUid(), 18d );
                
        ReportTable reportTable = new ReportTable( "Embezzlement",
            dataElements, new ArrayList<Indicator>(), new ArrayList<DataSet>(), periods, units, 
            true, false, true, null, null, "january_2000" );

        reportTableService.saveReportTable( reportTable );

        Grid grid = reportTableService.getReportTableGrid( reportTable.getUid(), i18nFormat, date, "0" );

        assertEquals( 11d, grid.getRow( 0 ).get( 7 ) );
        assertEquals( 13d, grid.getRow( 0 ).get( 8 ) );
        assertEquals( 15d, grid.getRow( 0 ).get( 9 ) );
        assertEquals( 17d, grid.getRow( 0 ).get( 10) );
        
        assertEquals( 12d, grid.getRow( 1 ).get( 7 ) );
        assertEquals( 14d, grid.getRow( 1 ).get( 8 ) );
        assertEquals( 16d, grid.getRow( 1 ).get( 9 ) );
        assertEquals( 18d, grid.getRow( 1 ).get( 10 ) );
    }
    
    @Test
    public void testGetDataSetReportTableA()
    {
        valueMap.put( dataSetA.getUid() + DIMENSION_SEP + periodA.getUid() + DIMENSION_SEP + unitA.getUid(), 11d );
        valueMap.put( dataSetA.getUid() + DIMENSION_SEP + periodA.getUid() + DIMENSION_SEP + unitB.getUid(), 12d );
        valueMap.put( dataSetA.getUid() + DIMENSION_SEP + periodB.getUid() + DIMENSION_SEP + unitA.getUid(), 13d );
        valueMap.put( dataSetA.getUid() + DIMENSION_SEP + periodB.getUid() + DIMENSION_SEP + unitB.getUid(), 14d );
        valueMap.put( dataSetB.getUid() + DIMENSION_SEP + periodA.getUid() + DIMENSION_SEP + unitA.getUid(), 15d );
        valueMap.put( dataSetB.getUid() + DIMENSION_SEP + periodA.getUid() + DIMENSION_SEP + unitB.getUid(), 16d );
        valueMap.put( dataSetB.getUid() + DIMENSION_SEP + periodB.getUid() + DIMENSION_SEP + unitA.getUid(), 17d );
        valueMap.put( dataSetB.getUid() + DIMENSION_SEP + periodB.getUid() + DIMENSION_SEP + unitB.getUid(), 18d );
                
        ReportTable reportTable = new ReportTable( "Prescriptions",
            new ArrayList<DataElement>(), new ArrayList<Indicator>(), dataSets, periods, units, 
            true, true, false, null, null, "january_2000" );

        reportTableService.saveReportTable( reportTable );

        Grid grid = reportTableService.getReportTableGrid( reportTable.getUid(), i18nFormat, date, "0" );

        assertEquals( 11d, grid.getRow( 0 ).get( 7 ) );
        assertEquals( 13d, grid.getRow( 0 ).get( 8 ) );
        assertEquals( 15d, grid.getRow( 0 ).get( 9 ) );
        assertEquals( 17d, grid.getRow( 0 ).get( 10 ) );
        
        assertEquals( 12d, grid.getRow( 1 ).get( 7 ) );
        assertEquals( 14d, grid.getRow( 1 ).get( 8 ) );
        assertEquals( 16d, grid.getRow( 1 ).get( 9 ) );
        assertEquals( 18d, grid.getRow( 1 ).get( 10 ) );
    }
    
    @Test
    public void testGetDataSetReportTableB()
    {
        valueMap.put( unitA.getUid() + DIMENSION_SEP + dataSetA.getUid() + DIMENSION_SEP + periodA.getUid(), 11d );
        valueMap.put( unitA.getUid() + DIMENSION_SEP + dataSetA.getUid() + DIMENSION_SEP + periodB.getUid(), 12d );
        valueMap.put( unitA.getUid() + DIMENSION_SEP + dataSetB.getUid() + DIMENSION_SEP + periodA.getUid(), 13d );
        valueMap.put( unitA.getUid() + DIMENSION_SEP + dataSetB.getUid() + DIMENSION_SEP + periodB.getUid(), 14d );
        valueMap.put( unitB.getUid() + DIMENSION_SEP + dataSetA.getUid() + DIMENSION_SEP + periodA.getUid(), 15d );
        valueMap.put( unitB.getUid() + DIMENSION_SEP + dataSetA.getUid() + DIMENSION_SEP + periodB.getUid(), 16d );
        valueMap.put( unitB.getUid() + DIMENSION_SEP + dataSetB.getUid() + DIMENSION_SEP + periodA.getUid(), 17d );
        valueMap.put( unitB.getUid() + DIMENSION_SEP + dataSetB.getUid() + DIMENSION_SEP + periodB.getUid(), 18d );
        
        ReportTable reportTable = new ReportTable( "Embezzlement",
            new ArrayList<DataElement>(), new ArrayList<Indicator>(), dataSets, periods, units, 
            false, false, true, null, null, "january_2000" );

        reportTableService.saveReportTable( reportTable );

        Grid grid = reportTableService.getReportTableGrid( reportTable.getUid(), i18nFormat, date, "0" );

        assertEquals( 11d, grid.getRow( 0 ).get( 11 ) );
        assertEquals( 15d, grid.getRow( 0 ).get( 12 ) );
        
        assertEquals( 12d, grid.getRow( 1 ).get( 11 ) );
        assertEquals( 16d, grid.getRow( 1 ).get( 12 ) );
        
        assertEquals( 13d, grid.getRow( 2 ).get( 11 ) );
        assertEquals( 17d, grid.getRow( 2 ).get( 12 ) );
        
        assertEquals( 14d, grid.getRow( 3 ).get( 11 ) );
        assertEquals( 18d, grid.getRow( 3 ).get( 12 ) );
    }

    @Test
    public void testGetDataSetReportTableC()
    {
        valueMap.put( dataSetA.getUid() + DIMENSION_SEP + unitA.getUid() + DIMENSION_SEP + periodA.getUid(), 11d );
        valueMap.put( dataSetA.getUid() + DIMENSION_SEP + unitA.getUid() + DIMENSION_SEP + periodB.getUid(), 12d );
        valueMap.put( dataSetA.getUid() + DIMENSION_SEP + unitB.getUid() + DIMENSION_SEP + periodA.getUid(), 13d );
        valueMap.put( dataSetA.getUid() + DIMENSION_SEP + unitB.getUid() + DIMENSION_SEP + periodB.getUid(), 14d );
        valueMap.put( dataSetB.getUid() + DIMENSION_SEP + unitA.getUid() + DIMENSION_SEP + periodA.getUid(), 15d );
        valueMap.put( dataSetB.getUid() + DIMENSION_SEP + unitA.getUid() + DIMENSION_SEP + periodB.getUid(), 16d );
        valueMap.put( dataSetB.getUid() + DIMENSION_SEP + unitB.getUid() + DIMENSION_SEP + periodA.getUid(), 17d );
        valueMap.put( dataSetB.getUid() + DIMENSION_SEP + unitB.getUid() + DIMENSION_SEP + periodB.getUid(), 18d );
                
        ReportTable reportTable = new ReportTable( "Embezzlement",
            new ArrayList<DataElement>(), new ArrayList<Indicator>(), dataSets, periods, units, 
            true, false, true, null, null, "january_2000" );

        reportTableService.saveReportTable( reportTable );

        Grid grid = reportTableService.getReportTableGrid( reportTable.getUid(), i18nFormat, date, "0" );

        assertEquals( 11d, grid.getRow( 0 ).get( 7 ) );
        assertEquals( 13d, grid.getRow( 0 ).get( 8 ) );
        assertEquals( 15d, grid.getRow( 0 ).get( 9 ) );
        assertEquals( 17d, grid.getRow( 0 ).get( 10) );
        
        assertEquals( 12d, grid.getRow( 1 ).get( 7 ) );
        assertEquals( 14d, grid.getRow( 1 ).get( 8 ) );
        assertEquals( 16d, grid.getRow( 1 ).get( 9 ) );
        assertEquals( 18d, grid.getRow( 1 ).get( 10 ) );
    }

    @Test
    public void testGetMultiReportTableA()
    {
        valueMap.put( indicatorA.getUid() + DIMENSION_SEP + periodA.getUid() + DIMENSION_SEP + unitA.getUid(), 11d );
        valueMap.put( indicatorA.getUid() + DIMENSION_SEP + periodA.getUid() + DIMENSION_SEP + unitB.getUid(), 12d );
        valueMap.put( indicatorA.getUid() + DIMENSION_SEP + periodB.getUid() + DIMENSION_SEP + unitA.getUid(), 13d );
        valueMap.put( indicatorA.getUid() + DIMENSION_SEP + periodB.getUid() + DIMENSION_SEP + unitB.getUid(), 14d );
        valueMap.put( indicatorB.getUid() + DIMENSION_SEP + periodA.getUid() + DIMENSION_SEP + unitA.getUid(), 15d );
        valueMap.put( indicatorB.getUid() + DIMENSION_SEP + periodA.getUid() + DIMENSION_SEP + unitB.getUid(), 16d );
        valueMap.put( indicatorB.getUid() + DIMENSION_SEP + periodB.getUid() + DIMENSION_SEP + unitA.getUid(), 17d );
        valueMap.put( indicatorB.getUid() + DIMENSION_SEP + periodB.getUid() + DIMENSION_SEP + unitB.getUid(), 18d );

        valueMap.put( dataElementA.getUid() + DIMENSION_SEP + periodA.getUid() + DIMENSION_SEP + unitA.getUid(), 21d );
        valueMap.put( dataElementA.getUid() + DIMENSION_SEP + periodA.getUid() + DIMENSION_SEP + unitB.getUid(), 22d );
        valueMap.put( dataElementA.getUid() + DIMENSION_SEP + periodB.getUid() + DIMENSION_SEP + unitA.getUid(), 23d );
        valueMap.put( dataElementA.getUid() + DIMENSION_SEP + periodB.getUid() + DIMENSION_SEP + unitB.getUid(), 24d );
        valueMap.put( dataElementB.getUid() + DIMENSION_SEP + periodA.getUid() + DIMENSION_SEP + unitA.getUid(), 25d );
        valueMap.put( dataElementB.getUid() + DIMENSION_SEP + periodA.getUid() + DIMENSION_SEP + unitB.getUid(), 26d );
        valueMap.put( dataElementB.getUid() + DIMENSION_SEP + periodB.getUid() + DIMENSION_SEP + unitA.getUid(), 27d );
        valueMap.put( dataElementB.getUid() + DIMENSION_SEP + periodB.getUid() + DIMENSION_SEP + unitB.getUid(), 28d );

        valueMap.put( dataSetA.getUid() + DIMENSION_SEP + periodA.getUid() + DIMENSION_SEP + unitA.getUid(), 31d );
        valueMap.put( dataSetA.getUid() + DIMENSION_SEP + periodA.getUid() + DIMENSION_SEP + unitB.getUid(), 32d );
        valueMap.put( dataSetA.getUid() + DIMENSION_SEP + periodB.getUid() + DIMENSION_SEP + unitA.getUid(), 33d );
        valueMap.put( dataSetA.getUid() + DIMENSION_SEP + periodB.getUid() + DIMENSION_SEP + unitB.getUid(), 34d );
        valueMap.put( dataSetB.getUid() + DIMENSION_SEP + periodA.getUid() + DIMENSION_SEP + unitA.getUid(), 35d );
        valueMap.put( dataSetB.getUid() + DIMENSION_SEP + periodA.getUid() + DIMENSION_SEP + unitB.getUid(), 36d );
        valueMap.put( dataSetB.getUid() + DIMENSION_SEP + periodB.getUid() + DIMENSION_SEP + unitA.getUid(), 37d );
        valueMap.put( dataSetB.getUid() + DIMENSION_SEP + periodB.getUid() + DIMENSION_SEP + unitB.getUid(), 38d );
                
        ReportTable reportTable = new ReportTable( "Prescriptions",
            dataElements, indicators, dataSets, periods, units, 
            true, true, false, null, null, "january_2000" );

        reportTableService.saveReportTable( reportTable );

        Grid grid = reportTableService.getReportTableGrid( reportTable.getUid(), i18nFormat, date, "0" );
        
        assertEquals( 11d, grid.getRow( 0 ).get( 7 ) );
        assertEquals( 13d, grid.getRow( 0 ).get( 8 ) );
        assertEquals( 15d, grid.getRow( 0 ).get( 9 ) );
        assertEquals( 17d, grid.getRow( 0 ).get( 10 ) );
        assertEquals( 21d, grid.getRow( 0 ).get( 11 ) );
        assertEquals( 23d, grid.getRow( 0 ).get( 12 ) );
        assertEquals( 25d, grid.getRow( 0 ).get( 13 ) );
        assertEquals( 27d, grid.getRow( 0 ).get( 14 ) );
        assertEquals( 31d, grid.getRow( 0 ).get( 15 ) );
        assertEquals( 33d, grid.getRow( 0 ).get( 16 ) );
        assertEquals( 35d, grid.getRow( 0 ).get( 17 ) );
        assertEquals( 37d, grid.getRow( 0 ).get( 18 ) );
        
        assertEquals( 12d, grid.getRow( 1 ).get( 7 ) );
        assertEquals( 14d, grid.getRow( 1 ).get( 8 ) );
        assertEquals( 16d, grid.getRow( 1 ).get( 9 ) );
        assertEquals( 18d, grid.getRow( 1 ).get( 10 ) );
        assertEquals( 22d, grid.getRow( 1 ).get( 11 ) );
        assertEquals( 24d, grid.getRow( 1 ).get( 12 ) );
        assertEquals( 26d, grid.getRow( 1 ).get( 13 ) );
        assertEquals( 28d, grid.getRow( 1 ).get( 14 ) );
        assertEquals( 32d, grid.getRow( 1 ).get( 15 ) );
        assertEquals( 34d, grid.getRow( 1 ).get( 16 ) );
        assertEquals( 36d, grid.getRow( 1 ).get( 17 ) );
        assertEquals( 38d, grid.getRow( 1 ).get( 18 ) );        
    }
    
    @Test
    public void testGetMultiReportTableB()
    {
        valueMap.put( unitA.getUid() + DIMENSION_SEP + indicatorA.getUid() + DIMENSION_SEP + periodA.getUid(), 11d );
        valueMap.put( unitA.getUid() + DIMENSION_SEP + indicatorA.getUid() + DIMENSION_SEP + periodB.getUid(), 12d );
        valueMap.put( unitA.getUid() + DIMENSION_SEP + indicatorB.getUid() + DIMENSION_SEP + periodA.getUid(), 13d );
        valueMap.put( unitA.getUid() + DIMENSION_SEP + indicatorB.getUid() + DIMENSION_SEP + periodB.getUid(), 14d );
        valueMap.put( unitB.getUid() + DIMENSION_SEP + indicatorA.getUid() + DIMENSION_SEP + periodA.getUid(), 15d );
        valueMap.put( unitB.getUid() + DIMENSION_SEP + indicatorA.getUid() + DIMENSION_SEP + periodB.getUid(), 16d );
        valueMap.put( unitB.getUid() + DIMENSION_SEP + indicatorB.getUid() + DIMENSION_SEP + periodA.getUid(), 17d );
        valueMap.put( unitB.getUid() + DIMENSION_SEP + indicatorB.getUid() + DIMENSION_SEP + periodB.getUid(), 18d );

        valueMap.put( unitA.getUid() + DIMENSION_SEP + dataElementA.getUid() + DIMENSION_SEP + periodA.getUid(), 21d );
        valueMap.put( unitA.getUid() + DIMENSION_SEP + dataElementA.getUid() + DIMENSION_SEP + periodB.getUid(), 22d );
        valueMap.put( unitA.getUid() + DIMENSION_SEP + dataElementB.getUid() + DIMENSION_SEP + periodA.getUid(), 23d );
        valueMap.put( unitA.getUid() + DIMENSION_SEP + dataElementB.getUid() + DIMENSION_SEP + periodB.getUid(), 24d );
        valueMap.put( unitB.getUid() + DIMENSION_SEP + dataElementA.getUid() + DIMENSION_SEP + periodA.getUid(), 25d );
        valueMap.put( unitB.getUid() + DIMENSION_SEP + dataElementA.getUid() + DIMENSION_SEP + periodB.getUid(), 26d );
        valueMap.put( unitB.getUid() + DIMENSION_SEP + dataElementB.getUid() + DIMENSION_SEP + periodA.getUid(), 27d );
        valueMap.put( unitB.getUid() + DIMENSION_SEP + dataElementB.getUid() + DIMENSION_SEP + periodB.getUid(), 28d );

        valueMap.put( unitA.getUid() + DIMENSION_SEP + dataSetA.getUid() + DIMENSION_SEP + periodA.getUid(), 31d );
        valueMap.put( unitA.getUid() + DIMENSION_SEP + dataSetA.getUid() + DIMENSION_SEP + periodB.getUid(), 32d );
        valueMap.put( unitA.getUid() + DIMENSION_SEP + dataSetB.getUid() + DIMENSION_SEP + periodA.getUid(), 33d );
        valueMap.put( unitA.getUid() + DIMENSION_SEP + dataSetB.getUid() + DIMENSION_SEP + periodB.getUid(), 34d );
        valueMap.put( unitB.getUid() + DIMENSION_SEP + dataSetA.getUid() + DIMENSION_SEP + periodA.getUid(), 35d );
        valueMap.put( unitB.getUid() + DIMENSION_SEP + dataSetA.getUid() + DIMENSION_SEP + periodB.getUid(), 36d );
        valueMap.put( unitB.getUid() + DIMENSION_SEP + dataSetB.getUid() + DIMENSION_SEP + periodA.getUid(), 37d );
        valueMap.put( unitB.getUid() + DIMENSION_SEP + dataSetB.getUid() + DIMENSION_SEP + periodB.getUid(), 38d );
        
        ReportTable reportTable = new ReportTable( "Embezzlement",
            dataElements, indicators, dataSets, periods, units, 
            false, false, true, null, null, "january_2000" );

        reportTableService.saveReportTable( reportTable );

        Grid grid = reportTableService.getReportTableGrid( reportTable.getUid(), i18nFormat, date, "0" );

        assertEquals( 11d, grid.getRow( 0 ).get( 11 ) );
        assertEquals( 15d, grid.getRow( 0 ).get( 12 ) );
        
        assertEquals( 12d, grid.getRow( 1 ).get( 11 ) );
        assertEquals( 16d, grid.getRow( 1 ).get( 12 ) );
        
        assertEquals( 13d, grid.getRow( 2 ).get( 11 ) );
        assertEquals( 17d, grid.getRow( 2 ).get( 12 ) );
        
        assertEquals( 14d, grid.getRow( 3 ).get( 11 ) );
        assertEquals( 18d, grid.getRow( 3 ).get( 12 ) );
        
        assertEquals( 21d, grid.getRow( 4 ).get( 11 ) );
        assertEquals( 25d, grid.getRow( 4 ).get( 12 ) );
        
        assertEquals( 22d, grid.getRow( 5 ).get( 11 ) );
        assertEquals( 26d, grid.getRow( 5 ).get( 12 ) );
        
        assertEquals( 23d, grid.getRow( 6 ).get( 11 ) );
        assertEquals( 27d, grid.getRow( 6 ).get( 12 ) );
        
        assertEquals( 24d, grid.getRow( 7 ).get( 11 ) );
        assertEquals( 28d, grid.getRow( 7 ).get( 12 ) );
        
        assertEquals( 31d, grid.getRow( 8 ).get( 11 ) );
        assertEquals( 35d, grid.getRow( 8 ).get( 12 ) );
        
        assertEquals( 32d, grid.getRow( 9 ).get( 11 ) );
        assertEquals( 36d, grid.getRow( 9 ).get( 12 ) );
        
        assertEquals( 33d, grid.getRow( 10 ).get( 11 ) );
        assertEquals( 37d, grid.getRow( 10 ).get( 12 ) );
        
        assertEquals( 34d, grid.getRow( 11 ).get( 11 ) );
        assertEquals( 38d, grid.getRow( 11 ).get( 12 ) );
    }
    
    @Test
    public void testGetMultiReportTableC()
    {
        valueMap.put( indicatorA.getUid() + DIMENSION_SEP + unitA.getUid() + DIMENSION_SEP + periodA.getUid(), 11d );
        valueMap.put( indicatorA.getUid() + DIMENSION_SEP + unitA.getUid() + DIMENSION_SEP + periodB.getUid(), 12d );
        valueMap.put( indicatorA.getUid() + DIMENSION_SEP + unitB.getUid() + DIMENSION_SEP + periodA.getUid(), 13d );
        valueMap.put( indicatorA.getUid() + DIMENSION_SEP + unitB.getUid() + DIMENSION_SEP + periodB.getUid(), 14d );
        valueMap.put( indicatorB.getUid() + DIMENSION_SEP + unitA.getUid() + DIMENSION_SEP + periodA.getUid(), 15d );
        valueMap.put( indicatorB.getUid() + DIMENSION_SEP + unitA.getUid() + DIMENSION_SEP + periodB.getUid(), 16d );
        valueMap.put( indicatorB.getUid() + DIMENSION_SEP + unitB.getUid() + DIMENSION_SEP + periodA.getUid(), 17d );
        valueMap.put( indicatorB.getUid() + DIMENSION_SEP + unitB.getUid() + DIMENSION_SEP + periodB.getUid(), 18d );

        valueMap.put( dataElementA.getUid() + DIMENSION_SEP + unitA.getUid() + DIMENSION_SEP + periodA.getUid(), 21d );
        valueMap.put( dataElementA.getUid() + DIMENSION_SEP + unitA.getUid() + DIMENSION_SEP + periodB.getUid(), 22d );
        valueMap.put( dataElementA.getUid() + DIMENSION_SEP + unitB.getUid() + DIMENSION_SEP + periodA.getUid(), 23d );
        valueMap.put( dataElementA.getUid() + DIMENSION_SEP + unitB.getUid() + DIMENSION_SEP + periodB.getUid(), 24d );
        valueMap.put( dataElementB.getUid() + DIMENSION_SEP + unitA.getUid() + DIMENSION_SEP + periodA.getUid(), 25d );
        valueMap.put( dataElementB.getUid() + DIMENSION_SEP + unitA.getUid() + DIMENSION_SEP + periodB.getUid(), 26d );
        valueMap.put( dataElementB.getUid() + DIMENSION_SEP + unitB.getUid() + DIMENSION_SEP + periodA.getUid(), 27d );
        valueMap.put( dataElementB.getUid() + DIMENSION_SEP + unitB.getUid() + DIMENSION_SEP + periodB.getUid(), 28d );

        valueMap.put( dataSetA.getUid() + DIMENSION_SEP + unitA.getUid() + DIMENSION_SEP + periodA.getUid(), 31d );
        valueMap.put( dataSetA.getUid() + DIMENSION_SEP + unitA.getUid() + DIMENSION_SEP + periodB.getUid(), 32d );
        valueMap.put( dataSetA.getUid() + DIMENSION_SEP + unitB.getUid() + DIMENSION_SEP + periodA.getUid(), 33d );
        valueMap.put( dataSetA.getUid() + DIMENSION_SEP + unitB.getUid() + DIMENSION_SEP + periodB.getUid(), 34d );
        valueMap.put( dataSetB.getUid() + DIMENSION_SEP + unitA.getUid() + DIMENSION_SEP + periodA.getUid(), 35d );
        valueMap.put( dataSetB.getUid() + DIMENSION_SEP + unitA.getUid() + DIMENSION_SEP + periodB.getUid(), 36d );
        valueMap.put( dataSetB.getUid() + DIMENSION_SEP + unitB.getUid() + DIMENSION_SEP + periodA.getUid(), 37d );
        valueMap.put( dataSetB.getUid() + DIMENSION_SEP + unitB.getUid() + DIMENSION_SEP + periodB.getUid(), 38d );
                
        ReportTable reportTable = new ReportTable( "Embezzlement",
            dataElements, indicators, dataSets, periods, units, 
            true, false, true, null, null, "january_2000" );

        reportTableService.saveReportTable( reportTable );

        Grid grid = reportTableService.getReportTableGrid( reportTable.getUid(), i18nFormat, date, "0" );

        assertEquals( 11d, grid.getRow( 0 ).get( 7 ) );
        assertEquals( 13d, grid.getRow( 0 ).get( 8 ) );
        assertEquals( 15d, grid.getRow( 0 ).get( 9 ) );
        assertEquals( 17d, grid.getRow( 0 ).get( 10 ) );
        assertEquals( 21d, grid.getRow( 0 ).get( 11 ) );
        assertEquals( 23d, grid.getRow( 0 ).get( 12 ) );
        assertEquals( 25d, grid.getRow( 0 ).get( 13 ) );
        assertEquals( 27d, grid.getRow( 0 ).get( 14 ) );
        assertEquals( 31d, grid.getRow( 0 ).get( 15 ) );
        assertEquals( 33d, grid.getRow( 0 ).get( 16 ) );
        assertEquals( 35d, grid.getRow( 0 ).get( 17 ) );
        assertEquals( 37d, grid.getRow( 0 ).get( 18 ) );

        assertEquals( 12d, grid.getRow( 1 ).get( 7 ) );
        assertEquals( 14d, grid.getRow( 1 ).get( 8 ) );
        assertEquals( 16d, grid.getRow( 1 ).get( 9 ) );
        assertEquals( 18d, grid.getRow( 1 ).get( 10 ) );
        assertEquals( 22d, grid.getRow( 1 ).get( 11 ) );
        assertEquals( 24d, grid.getRow( 1 ).get( 12 ) );
        assertEquals( 26d, grid.getRow( 1 ).get( 13 ) );
        assertEquals( 28d, grid.getRow( 1 ).get( 14 ) );
        assertEquals( 32d, grid.getRow( 1 ).get( 15 ) );
        assertEquals( 34d, grid.getRow( 1 ).get( 16 ) );
        assertEquals( 36d, grid.getRow( 1 ).get( 17 ) );
        assertEquals( 38d, grid.getRow( 1 ).get( 18 ) );
    }

    @Test
    public void testGetIndicatorReportTableColumnsOnly()
    {
        putIndicatorData();
                
        ReportTable reportTable = new ReportTable( "Prescriptions",
            new ArrayList<DataElement>(), indicators, new ArrayList<DataSet>(), periods, units, 
            true, true, true, null, null, "january_2000" );

        reportTableService.saveReportTable( reportTable );

        Grid grid = reportTableService.getReportTableGrid( reportTable.getUid(), i18nFormat, date, "0" );

        assertEquals( 11d, grid.getRow( 0 ).get( 3 ) );
        assertEquals( 12d, grid.getRow( 0 ).get( 4 ) );
        assertEquals( 13d, grid.getRow( 0 ).get( 5 ) );
        assertEquals( 14d, grid.getRow( 0 ).get( 6 ) );
        assertEquals( 15d, grid.getRow( 0 ).get( 7 ) );
        assertEquals( 16d, grid.getRow( 0 ).get( 8 ) );
        assertEquals( 17d, grid.getRow( 0 ).get( 9 ) );
        assertEquals( 18d, grid.getRow( 0 ).get( 10 ) );
    }

    @Test
    public void testGetIndicatorReportTableRowsOnly()
    {
        putIndicatorData();
                
        ReportTable reportTable = new ReportTable( "Prescriptions",
            new ArrayList<DataElement>(), indicators, new ArrayList<DataSet>(), periods, units, 
            false, false, false, null, null, "january_2000" );

        reportTableService.saveReportTable( reportTable );

        Grid grid = reportTableService.getReportTableGrid( reportTable.getUid(), i18nFormat, date, "0" );

        assertEquals( 11d, grid.getRow( 0 ).get( 15 ) );
        assertEquals( 12d, grid.getRow( 1 ).get( 15 ) );
        assertEquals( 13d, grid.getRow( 2 ).get( 15 ) );
        assertEquals( 14d, grid.getRow( 3 ).get( 15 ) );
        assertEquals( 15d, grid.getRow( 4 ).get( 15 ) );
        assertEquals( 16d, grid.getRow( 5 ).get( 15 ) );
        assertEquals( 17d, grid.getRow( 6 ).get( 15 ) );
        assertEquals( 18d, grid.getRow( 7 ).get( 15 ) );
    }
    
    @Test
    public void testGetIndicatorReportTableTopLimit()
    {
        putIndicatorData();
                
        ReportTable reportTable = new ReportTable( "Embezzlement",
            new ArrayList<DataElement>(), indicators, new ArrayList<DataSet>(), periods, units, 
            false, false, true, null, null, "january_2000" );
        reportTable.setTopLimit( 2 );
        
        reportTableService.saveReportTable( reportTable );

        Grid grid = reportTableService.getReportTableGrid( reportTable.getUid(), i18nFormat, date, "0" );

        assertEquals( 2, grid.getHeight() );
    }

    @Test
    public void testGetIndicatorReportTableSortOrder()
    {
        valueMap.put( unitA.getUid() + DIMENSION_SEP + indicatorA.getUid() + DIMENSION_SEP + periodA.getUid(), 11d );
        valueMap.put( unitA.getUid() + DIMENSION_SEP + indicatorA.getUid() + DIMENSION_SEP + periodB.getUid(), 12d );
        valueMap.put( unitA.getUid() + DIMENSION_SEP + indicatorB.getUid() + DIMENSION_SEP + periodA.getUid(), 13d );
        valueMap.put( unitA.getUid() + DIMENSION_SEP + indicatorB.getUid() + DIMENSION_SEP + periodB.getUid(), 14d );
        valueMap.put( unitB.getUid() + DIMENSION_SEP + indicatorA.getUid() + DIMENSION_SEP + periodA.getUid(), 15d );
        valueMap.put( unitB.getUid() + DIMENSION_SEP + indicatorA.getUid() + DIMENSION_SEP + periodB.getUid(), 16d );
        valueMap.put( unitB.getUid() + DIMENSION_SEP + indicatorB.getUid() + DIMENSION_SEP + periodA.getUid(), 17d );
        valueMap.put( unitB.getUid() + DIMENSION_SEP + indicatorB.getUid() + DIMENSION_SEP + periodB.getUid(), 18d );
        
        ReportTable reportTable = new ReportTable( "Embezzlement",
            new ArrayList<DataElement>(), indicators, new ArrayList<DataSet>(), periods, units, 
            false, false, true, null, null, "january_2000" );
        reportTable.setSortOrder( ReportTable.DESC );
        
        reportTableService.saveReportTable( reportTable );

        Grid grid = reportTableService.getReportTableGrid( reportTable.getUid(), i18nFormat, date, "0" );

        assertEquals( 14d, grid.getRow( 0 ).get( 11 ) );
        assertEquals( 18d, grid.getRow( 0 ).get( 12 ) );

        assertEquals( 13d, grid.getRow( 1 ).get( 11 ) );
        assertEquals( 17d, grid.getRow( 1 ).get( 12 ) );

        assertEquals( 12d, grid.getRow( 2 ).get( 11 ) );
        assertEquals( 16d, grid.getRow( 2 ).get( 12 ) );
        
        assertEquals( 11d, grid.getRow( 3 ).get( 11 ) );
        assertEquals( 15d, grid.getRow( 3 ).get( 12 ) );
    }

    @Test
    public void testGetDataElementReportTableRegression()
    {
        valueMap.put( dataElementA.getUid() + DIMENSION_SEP + unitA.getUid() + DIMENSION_SEP + periodA.getUid(), 11d );
        valueMap.put( dataElementA.getUid() + DIMENSION_SEP + unitA.getUid() + DIMENSION_SEP + periodB.getUid(), 12d );
        valueMap.put( dataElementA.getUid() + DIMENSION_SEP + unitB.getUid() + DIMENSION_SEP + periodA.getUid(), 13d );
        valueMap.put( dataElementA.getUid() + DIMENSION_SEP + unitB.getUid() + DIMENSION_SEP + periodB.getUid(), 14d );
        valueMap.put( dataElementB.getUid() + DIMENSION_SEP + unitA.getUid() + DIMENSION_SEP + periodA.getUid(), 15d );
        valueMap.put( dataElementB.getUid() + DIMENSION_SEP + unitA.getUid() + DIMENSION_SEP + periodB.getUid(), 16d );
        valueMap.put( dataElementB.getUid() + DIMENSION_SEP + unitB.getUid() + DIMENSION_SEP + periodA.getUid(), 17d );
        valueMap.put( dataElementB.getUid() + DIMENSION_SEP + unitB.getUid() + DIMENSION_SEP + periodB.getUid(), 18d );
        
        ReportTable reportTable = new ReportTable( "Embezzlement",
            dataElements, new ArrayList<Indicator>(), new ArrayList<DataSet>(), periods, units, 
            true, false, true, null, null, "january_2000" );
        
        reportTable.setRegression( true );
        
        reportTableService.saveReportTable( reportTable );

        Grid grid = reportTableService.getReportTableGrid( reportTable.getUid(), i18nFormat, date, "0" );
        
        assertEquals( 11.0, grid.getRow( 0 ).get( 7 ) );
        assertEquals( 13.0, grid.getRow( 0 ).get( 8 ) );
        assertEquals( 15.0, grid.getRow( 0 ).get( 9 ) );
        assertEquals( 17.0, grid.getRow( 0 ).get( 10 ) );

        assertEquals( 11.0, grid.getRow( 0 ).get( 11 ) );
        assertEquals( 13.0, grid.getRow( 0 ).get( 12 ) );
        assertEquals( 15.0, grid.getRow( 0 ).get( 13 ) );
        assertEquals( 17.0, grid.getRow( 0 ).get( 14 ) );
        
        assertEquals( 12.0, grid.getRow( 1 ).get( 7 ) );
        assertEquals( 14.0, grid.getRow( 1 ).get( 8 ) );
        assertEquals( 16.0, grid.getRow( 1 ).get( 9 ) );
        assertEquals( 18.0, grid.getRow( 1 ).get( 10 ) );

        assertEquals( 12.0, grid.getRow( 1 ).get( 11 ) );
        assertEquals( 14.0, grid.getRow( 1 ).get( 12 ) );
        assertEquals( 16.0, grid.getRow( 1 ).get( 13 ) );
        assertEquals( 18.0, grid.getRow( 1 ).get( 14 ) );
    }
    
    private void putIndicatorData()
    {
        valueMap.put( indicatorA.getUid() + DIMENSION_SEP + periodA.getUid() + DIMENSION_SEP + unitA.getUid(), 11d );
        valueMap.put( indicatorA.getUid() + DIMENSION_SEP + periodA.getUid() + DIMENSION_SEP + unitB.getUid(), 12d );
        valueMap.put( indicatorA.getUid() + DIMENSION_SEP + periodB.getUid() + DIMENSION_SEP + unitA.getUid(), 13d );
        valueMap.put( indicatorA.getUid() + DIMENSION_SEP + periodB.getUid() + DIMENSION_SEP + unitB.getUid(), 14d );
        valueMap.put( indicatorB.getUid() + DIMENSION_SEP + periodA.getUid() + DIMENSION_SEP + unitA.getUid(), 15d );
        valueMap.put( indicatorB.getUid() + DIMENSION_SEP + periodA.getUid() + DIMENSION_SEP + unitB.getUid(), 16d );
        valueMap.put( indicatorB.getUid() + DIMENSION_SEP + periodB.getUid() + DIMENSION_SEP + unitA.getUid(), 17d );
        valueMap.put( indicatorB.getUid() + DIMENSION_SEP + periodB.getUid() + DIMENSION_SEP + unitB.getUid(), 18d );                
    }
}
