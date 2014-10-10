package org.hisp.dhis.datamart;

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

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import org.hisp.dhis.DhisTest;
import org.hisp.dhis.aggregation.AggregatedDataValueService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.expression.Expression;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataMartServiceMultiDimensionTest
    extends DhisTest
{
    private final String T = "true";
    private final String F = "false";
    
    private DataMartEngine dataMartEngine;

    private AggregatedDataValueService aggregatedDataValueService;

    private DataElementCategoryOption categoryOptionA;
    private DataElementCategoryOption categoryOptionB;
    
    private DataElementCategory categoryA;
    
    private DataElementCategoryCombo categoryComboA;
    
    private DataElementCategoryOptionCombo defaultOptionCombo;
    private DataElementCategoryOptionCombo categoryOptionComboA;
    private DataElementCategoryOptionCombo categoryOptionComboB;  
    
    private Collection<Integer> dataElementIds;
    private Collection<Integer> indicatorIds;
    private Collection<Integer> periodIds;
    private Collection<Integer> organisationUnitIds;
    
    private DataElement dataElementA;
    private DataElement dataElementB;
    
    private Period periodA;
    private Period periodB;
    private Period periodC;
    
    private OrganisationUnit unitA;
    private OrganisationUnit unitB;
    private OrganisationUnit unitC;
    
    @Override
    public void setUpTest()
    {   
        // ---------------------------------------------------------------------
        // Dependencies
        // ---------------------------------------------------------------------

        dataMartEngine = (DataMartEngine) getBean( DataMartEngine.ID );

        aggregatedDataValueService = (AggregatedDataValueService) getBean( AggregatedDataValueService.ID );
        
        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );

        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );
        
        dataElementService = (DataElementService) getBean( DataElementService.ID );
        
        indicatorService = (IndicatorService) getBean( IndicatorService.ID );

        dataSetService = (DataSetService) getBean( DataSetService.ID );
        
        periodService = (PeriodService) getBean( PeriodService.ID );
        
        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );
        
        dataValueService = (DataValueService) getBean( DataValueService.ID );

        // ---------------------------------------------------------------------
        // Setup identifier Collections
        // ---------------------------------------------------------------------

        dataElementIds = new HashSet<Integer>();
        indicatorIds = new HashSet<Integer>();
        periodIds = new HashSet<Integer>();
        organisationUnitIds = new HashSet<Integer>();
        
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

        Iterator<DataElementCategoryOptionCombo> categoryOptionCombos = categoryService.getAllDataElementCategoryOptionCombos().iterator();
        
        defaultOptionCombo = categoryOptionCombos.next();
        categoryOptionComboA = categoryOptionCombos.next();
        categoryOptionComboB = categoryOptionCombos.next();     
        
        // ---------------------------------------------------------------------
        // Setup DataElements
        // ---------------------------------------------------------------------

        dataElementA = createDataElement( 'A', categoryComboA );
        dataElementB = createDataElement( 'B', categoryComboA );

        dataElementIds.add( dataElementService.addDataElement( dataElementA ) );
        dataElementIds.add( dataElementService.addDataElement( dataElementB ) );

        // ---------------------------------------------------------------------
        // Setup DataSets (to get correct PeriodType for DataElements)
        // ---------------------------------------------------------------------

        DataSet dataSet = createDataSet( 'A', new MonthlyPeriodType() );
        dataSet.getDataElements().add( dataElementA );
        dataSet.getDataElements().add( dataElementB );
        dataSetService.addDataSet( dataSet );
        dataElementA.getDataSets().add( dataSet );
        dataElementB.getDataSets().add( dataSet );
        dataElementService.updateDataElement( dataElementA );
        dataElementService.updateDataElement( dataElementB );
        
        // ---------------------------------------------------------------------
        // Setup Periods
        // ---------------------------------------------------------------------

        PeriodType monthly = new MonthlyPeriodType();
        PeriodType quarterly = new QuarterlyPeriodType();
        
        Date jul01 = getDate( 2005, 7, 1 );
        Date jul31 = getDate( 2005, 7, 31 );
        Date aug01 = getDate( 2005, 8, 1 );
        Date aug31 = getDate( 2005, 8, 31 );
        
        periodA = createPeriod( monthly, jul01, jul31 );
        periodB = createPeriod( monthly, aug01, aug31 );
        periodC = createPeriod( quarterly, jul01, aug31 ); //TODO fix
        
        periodIds.add( periodService.addPeriod( periodA ) );
        periodIds.add( periodService.addPeriod( periodB ) );
        periodIds.add( periodService.addPeriod( periodC ) );
        
        // ---------------------------------------------------------------------
        // Setup OrganisationUnits
        // ---------------------------------------------------------------------

        // ---------------------------------------------------------------------
        //              A
        //      B               C
        // ---------------------------------------------------------------------

        unitA = createOrganisationUnit( 'A' );
        unitB = createOrganisationUnit( 'B', unitA );
        unitC = createOrganisationUnit( 'C', unitA );
        
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitA ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitB ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitC ) );
    }

    @Override
    public boolean emptyDatabaseAfterTest()
    {
        return true;
    }

    @Ignore //TODO fix
    @Test
    public void testSumIntDataElementDataMart()
    {
        dataElementA.setType( DataElement.VALUE_TYPE_INT );
        dataElementA.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );        
        dataElementService.updateDataElement( dataElementA );
        
        // ---------------------------------------------------------------------
        // Setup DataValues
        // ---------------------------------------------------------------------

        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitA, "40", categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitA, "20", categoryOptionComboB, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitB, "90", categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitB, "10", categoryOptionComboB, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitC, "25", categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitC, "20", categoryOptionComboB, defaultOptionCombo ) );

        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitA, "40", categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitA, "80", categoryOptionComboB, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitB, "70", categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitB, "30", categoryOptionComboB, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitC, "65", categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitC, "40", categoryOptionComboB, defaultOptionCombo ) );
        
        // ---------------------------------------------------------------------
        // Test
        // ---------------------------------------------------------------------

        dataMartEngine.export( dataElementIds, indicatorIds, periodIds, organisationUnitIds );
        
        assertEquals( 90.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodA, unitB ), DELTA );
        assertEquals( 70.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodB, unitB ), DELTA );
        assertEquals( 160.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodC, unitB ), DELTA );
        assertEquals( 10.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodA, unitB ), DELTA );
        assertEquals( 30.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodB, unitB ), DELTA );
        assertEquals( 40.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodC, unitB ), DELTA );

        assertEquals( 25.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodA, unitC ), DELTA );
        assertEquals( 65.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodB, unitC ), DELTA );
        assertEquals( 90.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodC, unitC ), DELTA );
        assertEquals( 20.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodA, unitC ), DELTA );
        assertEquals( 40.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodB, unitC ), DELTA );
        assertEquals( 60.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodC, unitC ), DELTA );

        assertEquals( 155.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodA, unitA ), DELTA );
        assertEquals( 175.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodB, unitA ), DELTA );
        assertEquals( 330.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodC, unitA ), DELTA );
        assertEquals( 50.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodA, unitA ), DELTA );
        assertEquals( 150.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodB, unitA ), DELTA );
        assertEquals( 200.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodC, unitA ), DELTA );
    }

    @Ignore
    @Test
    public void testAverageIntDataElementDataMart()
    {
        dataElementA.setType( DataElement.VALUE_TYPE_INT );
        dataElementA.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_AVERAGE );        
        dataElementService.updateDataElement( dataElementA );
        
        // ---------------------------------------------------------------------
        // Setup DataValues
        // ---------------------------------------------------------------------

        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitA, "40", categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitA, "20", categoryOptionComboB, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitB, "90", categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitB, "10", categoryOptionComboB, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitC, "25", categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitC, "20", categoryOptionComboB, defaultOptionCombo ) );

        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitA, "40", categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitA, "80", categoryOptionComboB, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitB, "70", categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitB, "30", categoryOptionComboB, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitC, "65", categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitC, "40", categoryOptionComboB, defaultOptionCombo ) );
        
        // ---------------------------------------------------------------------
        // Test
        // ---------------------------------------------------------------------

        dataMartEngine.export( dataElementIds, indicatorIds, periodIds, organisationUnitIds );

        assertEquals( 90.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodA, unitB ), DELTA );
        assertEquals( 70.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodB, unitB ), DELTA );
        assertEquals( 80.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodC, unitB ), DELTA );
        assertEquals( 10.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodA, unitB ), DELTA );
        assertEquals( 30.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodB, unitB ), DELTA );
        assertEquals( 20.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodC, unitB ), DELTA );

        assertEquals( 25.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodA, unitC ), DELTA );
        assertEquals( 65.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodB, unitC ), DELTA );
        assertEquals( 45.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodC, unitC ), DELTA );
        assertEquals( 20.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodA, unitC ), DELTA );
        assertEquals( 40.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodB, unitC ), DELTA );
        assertEquals( 30.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodC, unitC ), DELTA );

        assertEquals( 155.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodA, unitA ), DELTA );
        assertEquals( 175.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodB, unitA ), DELTA );
        assertEquals( 165.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodC, unitA ), DELTA );
        assertEquals( 50.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodA, unitA ), DELTA );
        assertEquals( 150.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodB, unitA ), DELTA );
        assertEquals( 100.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodC, unitA ), DELTA );
    }

    @Ignore
    @Test
    public void testSumBoolDataElement()
    {
        dataElementA.setType( DataElement.VALUE_TYPE_BOOL );
        dataElementA.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );        
        dataElementService.updateDataElement( dataElementA );
        
        // ---------------------------------------------------------------------
        // Setup DataValues
        // ---------------------------------------------------------------------

        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitA, T, categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitA, F, categoryOptionComboB, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitB, T, categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitB, F, categoryOptionComboB, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitC, F, categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitC, T, categoryOptionComboB, defaultOptionCombo ) );

        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitA, T, categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitA, T, categoryOptionComboB, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitB, F, categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitB, T, categoryOptionComboB, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitC, F, categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitC, F, categoryOptionComboB, defaultOptionCombo ) );
        
        // ---------------------------------------------------------------------
        // Test
        // ---------------------------------------------------------------------

        dataMartEngine.export( dataElementIds, indicatorIds, periodIds, organisationUnitIds );

        assertEquals( 1.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodA, unitB ), DELTA );
        assertEquals( 0.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodB, unitB ), DELTA );
        assertEquals( 1.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodC, unitB ), DELTA );
        assertEquals( 0.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodA, unitB ), DELTA );
        assertEquals( 1.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodB, unitB ), DELTA );
        assertEquals( 1.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodC, unitB ), DELTA );

        assertEquals( 0.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodA, unitC ), DELTA );
        assertEquals( 0.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodB, unitC ), DELTA );
        assertEquals( 0.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodC, unitC ), DELTA );
        assertEquals( 1.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodA, unitC ), DELTA );
        assertEquals( 0.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodB, unitC ), DELTA );
        assertEquals( 1.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodC, unitC ), DELTA );

        assertEquals( 2.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodA, unitA ), DELTA );
        assertEquals( 1.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodB, unitA ), DELTA );
        assertEquals( 3.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodC, unitA ), DELTA );
        assertEquals( 1.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodA, unitA ), DELTA );
        assertEquals( 2.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodB, unitA ), DELTA );
        assertEquals( 3.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodC, unitA ), DELTA );
    }

    @Ignore
    @Test
    public void testAverageBoolDataElement()
    {
        dataElementA.setType( DataElement.VALUE_TYPE_BOOL );
        dataElementA.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_AVERAGE );        
        dataElementService.updateDataElement( dataElementA );
        
        // ---------------------------------------------------------------------
        // Setup DataValues
        // ---------------------------------------------------------------------

        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitA, T, categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitA, F, categoryOptionComboB, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitB, T, categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitB, F, categoryOptionComboB, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitC, F, categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitC, T, categoryOptionComboB, defaultOptionCombo ) );

        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitA, T, categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitA, T, categoryOptionComboB, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitB, F, categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitB, T, categoryOptionComboB, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitC, F, categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitC, F, categoryOptionComboB, defaultOptionCombo ) );
        
        // ---------------------------------------------------------------------
        // Test
        // ---------------------------------------------------------------------

        dataMartEngine.export( dataElementIds, indicatorIds, periodIds, organisationUnitIds );

        assertEquals( 100.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodA, unitB ), DELTA );
        assertEquals( 0.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodB, unitB ), DELTA );
        assertEquals( 50.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodC, unitB ), DELTA );
        assertEquals( 0.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodA, unitB ), DELTA );
        assertEquals( 100.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodB, unitB ), DELTA );
        assertEquals( 50.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodC, unitB ), DELTA );

        assertEquals( 0.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodA, unitC ), DELTA );
        assertEquals( 0.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodB, unitC ), DELTA );
        assertEquals( 0.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodC, unitC ), DELTA );
        assertEquals( 100.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodA, unitC ), DELTA );
        assertEquals( 0.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodB, unitC ), DELTA );
        assertEquals( 50.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodC, unitC ), DELTA );

        assertEquals( 66.7, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodA, unitA ), DELTA );
        assertEquals( 33.3, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodB, unitA ), DELTA );
        assertEquals( 50.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboA, periodC, unitA ), DELTA );
        assertEquals( 33.3, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodA, unitA ), DELTA );
        assertEquals( 66.7, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodB, unitA ), DELTA );
        assertEquals( 50.0, aggregatedDataValueService.getAggregatedValue( dataElementA, categoryOptionComboB, periodC, unitA ), DELTA );
    }

    @Ignore
    @Test
    public void testIndicator()
    {
        dataElementA.setType( DataElement.VALUE_TYPE_INT );
        dataElementA.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );        
        dataElementService.updateDataElement( dataElementA );
        
        // ---------------------------------------------------------------------
        // Setup DataValues
        // ---------------------------------------------------------------------

        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitB, "9", categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitB, "3", categoryOptionComboB, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitC, "1", categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitC, "5", categoryOptionComboB, defaultOptionCombo ) );
        
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitB, "3", categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitB, "2", categoryOptionComboB, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitC, "7", categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitC, "9", categoryOptionComboB, defaultOptionCombo ) );
        
        // ---------------------------------------------------------------------
        // Setup Indicators
        // ---------------------------------------------------------------------

        IndicatorType indicatorType = createIndicatorType( 'A' ); // Factor = 100
        
        indicatorService.addIndicatorType( indicatorType );
        
        Indicator indicatorA = createIndicator( 'A', indicatorType );

        String suffixA = Expression.SEPARATOR + categoryOptionComboA.getId();
        String suffixB = Expression.SEPARATOR + categoryOptionComboB.getId();
        
        indicatorA.setNumerator( "[" + dataElementA.getId() + suffixA + "]*[" + dataElementA.getId() + suffixB + "]" );
        
        indicatorA.setDenominator( "1" );
        
        indicatorIds.add( indicatorService.addIndicator( indicatorA ) );

        // ---------------------------------------------------------------------
        // Test
        // ---------------------------------------------------------------------

        dataMartEngine.export( dataElementIds, indicatorIds, periodIds, organisationUnitIds );
        
        assertEquals( 2700.0, aggregatedDataValueService.getAggregatedValue( indicatorA, periodA, unitB ), DELTA );
        assertEquals( 600.0, aggregatedDataValueService.getAggregatedValue( indicatorA, periodB, unitB ), DELTA );
        assertEquals( 6000.0, aggregatedDataValueService.getAggregatedValue( indicatorA, periodC, unitB ), DELTA );
        
        assertEquals( 500.0, aggregatedDataValueService.getAggregatedValue( indicatorA, periodA, unitC ), DELTA );
        assertEquals( 6300.0, aggregatedDataValueService.getAggregatedValue( indicatorA, periodB, unitC ), DELTA );
        assertEquals( 11200.0, aggregatedDataValueService.getAggregatedValue( indicatorA, periodC, unitC ), DELTA );
        
        assertEquals( 8000.0, aggregatedDataValueService.getAggregatedValue( indicatorA, periodA, unitA ), DELTA );
        assertEquals( 11000.0, aggregatedDataValueService.getAggregatedValue( indicatorA, periodB, unitA ), DELTA );
        assertEquals( 38000.0, aggregatedDataValueService.getAggregatedValue( indicatorA, periodC, unitA ), DELTA );   
    }

    @Ignore
    @Test
    public void testIndicatorTotal()
    {
        dataElementA.setType( DataElement.VALUE_TYPE_INT );
        dataElementA.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );        
        dataElementService.updateDataElement( dataElementA );
        
        // ---------------------------------------------------------------------
        // Setup DataValues
        // ---------------------------------------------------------------------

        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitB, "9", categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitB, "3", categoryOptionComboB, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitC, "1", categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitC, "5", categoryOptionComboB, defaultOptionCombo ) );
        
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitB, "3", categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitB, "2", categoryOptionComboB, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitC, "7", categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitC, "9", categoryOptionComboB, defaultOptionCombo ) );
        
        // ---------------------------------------------------------------------
        // Setup Indicators
        // ---------------------------------------------------------------------

        IndicatorType indicatorType = createIndicatorType( 'A' ); // Factor = 100
        
        indicatorService.addIndicatorType( indicatorType );
        
        Indicator indicatorA = createIndicator( 'A', indicatorType );

        String suffixA = Expression.SEPARATOR + categoryOptionComboA.getId();
        String suffixB = Expression.SEPARATOR + categoryOptionComboB.getId();
        
        indicatorA.setNumerator( "[" + dataElementA.getId() + suffixA + "]+[" + dataElementA.getId() + suffixB + "]" );
        
        indicatorA.setDenominator( "100" );

        Indicator indicatorB = createIndicator( 'B', indicatorType );

        indicatorB.setNumerator( "[" + dataElementA.getId() + "]" );
        
        indicatorB.setDenominator( "100" );

        indicatorIds.add( indicatorService.addIndicator( indicatorA ) );
        indicatorIds.add( indicatorService.addIndicator( indicatorB ) );

        // ---------------------------------------------------------------------
        // Test
        // ---------------------------------------------------------------------

        dataMartEngine.export( dataElementIds, indicatorIds, periodIds, organisationUnitIds );

        assertEquals( 12.0, aggregatedDataValueService.getAggregatedValue( indicatorB, periodA, unitB ), DELTA );
        assertEquals( 5.0, aggregatedDataValueService.getAggregatedValue( indicatorB, periodB, unitB ), DELTA );
        assertEquals( 17.0, aggregatedDataValueService.getAggregatedValue( indicatorB, periodC, unitB ), DELTA );
        
        assertEquals( 6.0, aggregatedDataValueService.getAggregatedValue( indicatorB, periodA, unitC ), DELTA );
        assertEquals( 16.0, aggregatedDataValueService.getAggregatedValue( indicatorB, periodB, unitC ), DELTA );
        assertEquals( 22.0, aggregatedDataValueService.getAggregatedValue( indicatorB, periodC, unitC ), DELTA );
        
        assertEquals( 18.0, aggregatedDataValueService.getAggregatedValue( indicatorB, periodA, unitA ), DELTA );
        assertEquals( 21.0, aggregatedDataValueService.getAggregatedValue( indicatorB, periodB, unitA ), DELTA );
        assertEquals( 39.0, aggregatedDataValueService.getAggregatedValue( indicatorB, periodC, unitA ), DELTA );
        
        assertEquals( aggregatedDataValueService.getAggregatedValue( indicatorB, periodA, unitB ), aggregatedDataValueService.getAggregatedValue( indicatorA, periodA, unitB ) );
        assertEquals( aggregatedDataValueService.getAggregatedValue( indicatorB, periodB, unitB ), aggregatedDataValueService.getAggregatedValue( indicatorA, periodB, unitB ) );
        assertEquals( aggregatedDataValueService.getAggregatedValue( indicatorB, periodC, unitB ), aggregatedDataValueService.getAggregatedValue( indicatorA, periodC, unitB ) );
        
        assertEquals( aggregatedDataValueService.getAggregatedValue( indicatorB, periodA, unitC ), aggregatedDataValueService.getAggregatedValue( indicatorA, periodA, unitC ) );
        assertEquals( aggregatedDataValueService.getAggregatedValue( indicatorB, periodB, unitC ), aggregatedDataValueService.getAggregatedValue( indicatorA, periodB, unitC ) );
        assertEquals( aggregatedDataValueService.getAggregatedValue( indicatorB, periodC, unitC ), aggregatedDataValueService.getAggregatedValue( indicatorA, periodC, unitC ) );
        
        assertEquals( aggregatedDataValueService.getAggregatedValue( indicatorB, periodA, unitA ), aggregatedDataValueService.getAggregatedValue( indicatorA, periodA, unitA ) );
        assertEquals( aggregatedDataValueService.getAggregatedValue( indicatorB, periodB, unitA ), aggregatedDataValueService.getAggregatedValue( indicatorA, periodB, unitA ) );
        assertEquals( aggregatedDataValueService.getAggregatedValue( indicatorB, periodC, unitA ), aggregatedDataValueService.getAggregatedValue( indicatorA, periodC, unitA ) );
    }
    
    @Ignore
    @Test
    public void testAnnualizedIndicator()
    {
        dataElementA.setType( DataElement.VALUE_TYPE_INT );
        dataElementA.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );        
        dataElementService.updateDataElement( dataElementA );
        
        // ---------------------------------------------------------------------
        // Setup DataValues
        // ---------------------------------------------------------------------

        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitB, "9", categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitB, "3", categoryOptionComboB, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitC, "1", categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitC, "5", categoryOptionComboB, defaultOptionCombo ) );
        
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitB, "3", categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitB, "2", categoryOptionComboB, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitC, "7", categoryOptionComboA, defaultOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitC, "9", categoryOptionComboB, defaultOptionCombo ) );
        
        // ---------------------------------------------------------------------
        // Setup Indicators
        // ---------------------------------------------------------------------

        IndicatorType indicatorType = createIndicatorType( 'A' ); // Factor = 100
        
        indicatorService.addIndicatorType( indicatorType );
        
        Indicator indicatorA = createIndicator( 'A', indicatorType );

        indicatorA.setAnnualized( true );
        
        String suffixA = Expression.SEPARATOR + categoryOptionComboA.getId();
        String suffixB = Expression.SEPARATOR + categoryOptionComboB.getId();
        
        indicatorA.setNumerator( "[" + dataElementA.getId() + suffixA + "]*[" + dataElementA.getId() + suffixB + "]" );
        
        indicatorA.setDenominator( "1" );
        
        indicatorIds.add( indicatorService.addIndicator( indicatorA ) );

        // ---------------------------------------------------------------------
        // Test
        // ---------------------------------------------------------------------

        dataMartEngine.export( dataElementIds, indicatorIds, periodIds, organisationUnitIds );
        
        assertEquals( 31790.3, aggregatedDataValueService.getAggregatedValue( indicatorA, periodA, unitB ), DELTA );
        assertEquals( 7064.5, aggregatedDataValueService.getAggregatedValue( indicatorA, periodB, unitB ), DELTA );
        assertEquals( 35322.6, aggregatedDataValueService.getAggregatedValue( indicatorA, periodC, unitB ), DELTA );
        
        assertEquals( 5887.1, aggregatedDataValueService.getAggregatedValue( indicatorA, periodA, unitC ), DELTA );
        assertEquals( 74177.4, aggregatedDataValueService.getAggregatedValue( indicatorA, periodB, unitC ), DELTA );
        assertEquals( 65935.5, aggregatedDataValueService.getAggregatedValue( indicatorA, periodC, unitC ), DELTA );
        
        assertEquals( 94193.5, aggregatedDataValueService.getAggregatedValue( indicatorA, periodA, unitA ), DELTA );
        assertEquals( 129516.1, aggregatedDataValueService.getAggregatedValue( indicatorA, periodB, unitA ), DELTA );
        assertEquals( 223709.7, aggregatedDataValueService.getAggregatedValue( indicatorA, periodC, unitA ), DELTA );        
    }
}
