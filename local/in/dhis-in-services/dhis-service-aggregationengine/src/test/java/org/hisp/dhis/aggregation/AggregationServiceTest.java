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

import static org.hisp.dhis.expression.Expression.SEPARATOR;
import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.hisp.dhis.DhisTest;
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
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id: AggregationServiceTest.java 5942 2008-10-16 15:44:57Z larshelg $
 */
public class AggregationServiceTest
    extends DhisTest
{
    private final String T = "true";
    private final String F = "false";
    
    private AggregationService aggregationService;

    private DataElementCategoryOption categoryOptionA;
    private DataElementCategoryOption categoryOptionB;
    
    private DataElementCategory category;

    private DataElementCategoryCombo categoryCombo;
    
    private DataElementCategoryOptionCombo categoryOptionComboA;
    private DataElementCategoryOptionCombo categoryOptionComboB;    
    
    private DataElement dataElementA;
    private DataElement dataElementB;
    private DataElement dataElementC;
        
    private IndicatorType indicatorType;
    
    private Indicator indicatorA;
    private Indicator indicatorB;
    
    private DataSet dataSet;
    
    private Period periodA;
    private Period periodB;
    private Period periodC;
    
    private OrganisationUnit unitA;
    private OrganisationUnit unitB;
    private OrganisationUnit unitC;
    private OrganisationUnit unitD;
    private OrganisationUnit unitE;
    private OrganisationUnit unitF;
    private OrganisationUnit unitG;
    private OrganisationUnit unitH;
    private OrganisationUnit unitI;    

    private Date mar01 = getDate( 2005, 3, 1 );
    private Date mar31 = getDate( 2005, 3, 31 );
    private Date apr01 = getDate( 2005, 4, 1 );
    private Date apr30 = getDate( 2005, 4, 30 );
    private Date may01 = getDate( 2005, 5, 1 );
    private Date may31 = getDate( 2005, 5, 31 );
    
    @Override
    public void setUpTest()
    {
        aggregationService = (AggregationService) getBean( AggregationService.ID );
        
        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );
        
        dataElementService = (DataElementService) getBean( DataElementService.ID );
        
        indicatorService = (IndicatorService) getBean( IndicatorService.ID );

        dataSetService = (DataSetService) getBean( DataSetService.ID );
        
        periodService = (PeriodService) getBean( PeriodService.ID );

        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );

        dataValueService = (DataValueService) getBean( DataValueService.ID );

        expressionService = (ExpressionService) getBean( ExpressionService.ID );

        // ---------------------------------------------------------------------
        // Setup Dimensions
        // ---------------------------------------------------------------------

        categoryOptionA = new DataElementCategoryOption( "Male" );
        categoryOptionB = new DataElementCategoryOption( "Female" );
        
        categoryService.addDataElementCategoryOption( categoryOptionA );
        categoryService.addDataElementCategoryOption( categoryOptionB );

        category = new DataElementCategory( "Gender" );
        category.getCategoryOptions().add( categoryOptionA );
        category.getCategoryOptions().add( categoryOptionB );

        categoryService.addDataElementCategory( category );

        categoryCombo = new DataElementCategoryCombo( "Gender" );
        categoryCombo.getCategories().add( category );        
        
        categoryService.addDataElementCategoryCombo( categoryCombo );
        
        categoryOptionComboA = createCategoryOptionCombo( categoryCombo, categoryOptionA );
        categoryOptionComboB = createCategoryOptionCombo( categoryCombo, categoryOptionB );
        
        categoryService.addDataElementCategoryOptionCombo( categoryOptionComboA );
        categoryService.addDataElementCategoryOptionCombo( categoryOptionComboB );

        // ---------------------------------------------------------------------
        // Setup DataElements
        // ---------------------------------------------------------------------

        dataElementA = createDataElement( 'A', DataElement.VALUE_TYPE_INT, DataElement.AGGREGATION_OPERATOR_SUM, categoryCombo );
        dataElementB = createDataElement( 'B', DataElement.VALUE_TYPE_BOOL, DataElement.AGGREGATION_OPERATOR_SUM, categoryCombo );
        dataElementC = createDataElement( 'C', DataElement.VALUE_TYPE_INT, DataElement.AGGREGATION_OPERATOR_SUM, categoryCombo );

        dataElementService.addDataElement( dataElementA );
        dataElementService.addDataElement( dataElementB );
        dataElementService.addDataElement( dataElementC );

        // ---------------------------------------------------------------------
        // Setup Indicators
        // ---------------------------------------------------------------------

        indicatorType = createIndicatorType( 'A' );
        indicatorType.setFactor( 100 );
        
        indicatorService.addIndicatorType( indicatorType );
        
        indicatorA = createIndicator( 'A', indicatorType );
        indicatorA.setNumerator( "#{" + dataElementA.getUid() + SEPARATOR + categoryOptionComboA.getUid() + "}+150" );
        indicatorA.setDenominator( "#{" + dataElementB.getUid() + SEPARATOR + categoryOptionComboA.getUid() + "}" );
        
        indicatorB = createIndicator( 'B', indicatorType );
        indicatorB.setNumerator( "#{" + dataElementC.getUid() + "}" );
        indicatorB.setDenominator( "1" );
        
        indicatorService.addIndicator( indicatorA );
        indicatorService.addIndicator( indicatorB );
        
        // ---------------------------------------------------------------------
        // Setup DataSets (to get correct PeriodType for DataElements)
        // ---------------------------------------------------------------------

        dataSet = createDataSet( 'A', new MonthlyPeriodType() );
        dataSet.getDataElements().add( dataElementA );
        dataSet.getDataElements().add( dataElementB );
        dataSetService.addDataSet( dataSet );
        dataElementA.getDataSets().add( dataSet );
        dataElementB.getDataSets().add( dataSet );
        dataElementC.getDataSets().add( dataSet );
        dataElementService.updateDataElement( dataElementA );
        dataElementService.updateDataElement( dataElementB );
        dataElementService.updateDataElement( dataElementC );
        
        // ---------------------------------------------------------------------
        // Setup Periods
        // ---------------------------------------------------------------------
        
        PeriodType monthly = new MonthlyPeriodType();
        
        periodA = createPeriod( monthly, mar01, mar31 );
        periodB = createPeriod( monthly, apr01, apr30 );
        periodC = createPeriod( monthly, may01, may31 );
        
        periodService.addPeriod( periodA );
        periodService.addPeriod( periodB );
        periodService.addPeriod( periodC );
        
        // ---------------------------------------------------------------------
        // Setup OrganisationUnits
        // ---------------------------------------------------------------------

        unitA = createOrganisationUnit( 'A' );
        unitB = createOrganisationUnit( 'B', unitA );
        unitC = createOrganisationUnit( 'C', unitA );
        unitD = createOrganisationUnit( 'D', unitB );
        unitE = createOrganisationUnit( 'E', unitB );
        unitF = createOrganisationUnit( 'F', unitB );
        unitG = createOrganisationUnit( 'G', unitF);
        unitH = createOrganisationUnit( 'H', unitF );
        unitI = createOrganisationUnit( 'I' );

        organisationUnitService.addOrganisationUnit( unitA );
        organisationUnitService.addOrganisationUnit( unitB );
        organisationUnitService.addOrganisationUnit( unitC );
        organisationUnitService.addOrganisationUnit( unitD );
        organisationUnitService.addOrganisationUnit( unitE );
        organisationUnitService.addOrganisationUnit( unitF );
        organisationUnitService.addOrganisationUnit( unitG );
        organisationUnitService.addOrganisationUnit( unitH );
        organisationUnitService.addOrganisationUnit( unitI );
        
        // ---------------------------------------------------------------------
        // Setup DataValues
        // ---------------------------------------------------------------------

        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitC, "90", categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitD, "10", categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitE, "35", categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitF, "25", categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitG, "20", categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitH, "60", categoryOptionComboA ) );
        
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitC, "70", categoryOptionComboA ) );        
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitD, "40", categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitE, "65", categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitF, "55", categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitG, "20", categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitH, "15", categoryOptionComboA ) );
        
        dataValueService.addDataValue( createDataValue( dataElementA, periodC, unitC, "95", categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodC, unitD, "40", categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodC, unitE, "45", categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodC, unitF, "30", categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodC, unitG, "50", categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodC, unitH, "70", categoryOptionComboA ) );
        
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, unitC, T, categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, unitD, T, categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, unitE, F, categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, unitF, T, categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, unitG, F, categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, unitH, T, categoryOptionComboA ) );
        
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, unitC, T, categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, unitD, F, categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, unitE, T, categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, unitF, T, categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, unitG, F, categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, unitH, T, categoryOptionComboA ) );
        
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, unitC, F, categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, unitD, T, categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, unitE, F, categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, unitF, T, categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, unitG, T, categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, unitH, T, categoryOptionComboA ) );
        
        dataValueService.addDataValue( createDataValue( dataElementC, periodA, unitB, "30", categoryOptionComboA ) );
        dataValueService.addDataValue( createDataValue( dataElementC, periodA, unitB, "20", categoryOptionComboB ) );
                
        aggregationService.clearCache();
    }

    @Override
    public boolean emptyDatabaseAfterTest()
    {
        return true;
    }

    @Test
    public void indicator()
    {
        assertEquals( 10000.0, aggregationService.getAggregatedIndicatorValue( indicatorA, mar01, mar31, unitB ), DELTA );
        
        assertEquals( 30.0, aggregationService.getAggregatedDataValue( dataElementC, categoryOptionComboA, mar01, mar31, unitB ), DELTA );
        assertEquals( 20.0, aggregationService.getAggregatedDataValue( dataElementC, categoryOptionComboB, mar01, mar31, unitB ), DELTA );
        assertEquals( 50.0, aggregationService.getAggregatedDataValue( dataElementC, null, mar01, mar31, unitB ), DELTA );
        
        assertEquals( 5000.0, aggregationService.getAggregatedIndicatorValue( indicatorB, mar01, mar31, unitB ), DELTA );
    }
    
    @Test
    public void sumIntDataElement()
    {
        assertEquals( 90.0, aggregationService.getAggregatedDataValue( dataElementA, categoryOptionComboA, mar01, mar31, unitC ), DELTA );
        assertEquals( 105.0, aggregationService.getAggregatedDataValue( dataElementA, categoryOptionComboA, mar01, mar31, unitF ), DELTA );
        assertEquals( 150.0, aggregationService.getAggregatedDataValue( dataElementA, categoryOptionComboA, mar01, mar31, unitB ), DELTA );

        assertEquals( 255.0, aggregationService.getAggregatedDataValue( dataElementA, categoryOptionComboA, mar01, may31, unitC ), DELTA );
        assertEquals( 345.0, aggregationService.getAggregatedDataValue( dataElementA, categoryOptionComboA, mar01, may31, unitF ), DELTA );
        assertEquals( 580.0, aggregationService.getAggregatedDataValue( dataElementA, categoryOptionComboA, mar01, may31, unitB ), DELTA );
    }
    
    @Test
    public void sumBoolDataElement()
    {
        assertEquals( 1.0, aggregationService.getAggregatedDataValue( dataElementB, categoryOptionComboA, mar01, mar31, unitC ), DELTA );
        assertEquals( 2.0, aggregationService.getAggregatedDataValue( dataElementB, categoryOptionComboA, mar01, mar31, unitF ), DELTA );
        assertEquals( 3.0, aggregationService.getAggregatedDataValue( dataElementB, categoryOptionComboA, mar01, mar31, unitB ), DELTA );

        assertEquals( 2.0, aggregationService.getAggregatedDataValue( dataElementB, categoryOptionComboA, mar01, may31, unitC ), DELTA );
        assertEquals( 7.0, aggregationService.getAggregatedDataValue( dataElementB, categoryOptionComboA, mar01, may31, unitF ), DELTA );
        assertEquals( 10.0, aggregationService.getAggregatedDataValue( dataElementB, categoryOptionComboA, mar01, may31, unitB ), DELTA );
    }
    
    @Test
    public void averageIntDataElement()
    {
        dataElementA.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_AVERAGE );
        dataElementService.updateDataElement( dataElementA );
        
        assertEquals( 90.0, aggregationService.getAggregatedDataValue( dataElementA, categoryOptionComboA, mar01, mar31, unitC ), DELTA );
        assertEquals( 105.0, aggregationService.getAggregatedDataValue( dataElementA, categoryOptionComboA, mar01, mar31, unitF ), DELTA );
        assertEquals( 150.0, aggregationService.getAggregatedDataValue( dataElementA, categoryOptionComboA, mar01, mar31, unitB ), DELTA );

        assertEquals( 85.2, aggregationService.getAggregatedDataValue( dataElementA, categoryOptionComboA, mar01, may31, unitC ), 0.3 );
        assertEquals( 115.3, aggregationService.getAggregatedDataValue( dataElementA, categoryOptionComboA, mar01, may31, unitF ), 0.3 );
        assertEquals( 193.3, aggregationService.getAggregatedDataValue( dataElementA, categoryOptionComboA, mar01, may31, unitB ), 0.6 );
    }
    
    @Test
    public void averageBoolDataElement()
    {
        dataElementB.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_AVERAGE );
        dataElementService.updateDataElement( dataElementB );
        
        assertEquals( 1.0, aggregationService.getAggregatedDataValue( dataElementB, categoryOptionComboA, mar01, mar31, unitC ), DELTA );
        assertEquals( 0.67, aggregationService.getAggregatedDataValue( dataElementB, categoryOptionComboA, mar01, mar31, unitF ), DELTA );
        assertEquals( 0.6, aggregationService.getAggregatedDataValue( dataElementB, categoryOptionComboA, mar01, mar31, unitB ), DELTA );
        
        assertEquals( 0.66, aggregationService.getAggregatedDataValue( dataElementB, categoryOptionComboA, mar01, may31, unitC ), DELTA );
        assertEquals( 0.78, aggregationService.getAggregatedDataValue( dataElementB, categoryOptionComboA, mar01, may31, unitF ), DELTA );
        assertEquals( 0.67, aggregationService.getAggregatedDataValue( dataElementB, categoryOptionComboA, mar01, may31, unitB ), DELTA );
    }
}
