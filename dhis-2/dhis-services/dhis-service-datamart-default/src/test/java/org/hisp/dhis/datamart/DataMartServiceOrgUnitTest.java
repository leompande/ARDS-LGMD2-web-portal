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

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.hisp.dhis.DhisTest;
import org.hisp.dhis.aggregation.AggregatedOrgUnitDataValueService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
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
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.junit.Test;

/**
 * Note: Test using the default org unit level for org unit data mart which is 2. 
 * 
 * @author Lars Helge Overland
 * @version $Id: DataMartServiceTest.java 5519 2008-08-05 09:00:31Z larshelg $
 */
public class DataMartServiceOrgUnitTest
    extends DhisTest
{    
    private final String T = "true";
    private final String F = "false";
    
    private DataMartEngine dataMartEngine;
    
    private DataElementCategoryCombo categoryCombo;
    
    private DataElementCategoryOptionCombo categoryOptionCombo;

    private Collection<Integer> dataElementIds;
    private Collection<Integer> indicatorIds;
    private Collection<Integer> periodIds;
    private Collection<Integer> organisationUnitIds;
    private Collection<Integer> organisationUnitGroupIds;
    
    private DataElement dataElementA;
    private DataElement dataElementB;
    
    private DataSet dataSet;
    
    private Period periodA;
    private Period periodB;
    private Period periodC;
    private Period periodD;
    
    private OrganisationUnit unitA;
    private OrganisationUnit unitB;
    private OrganisationUnit unitC;
    private OrganisationUnit unitD;
    private OrganisationUnit unitE;
    private OrganisationUnit unitF;
    private OrganisationUnit unitG;
    private OrganisationUnit unitH;
    private OrganisationUnit unitI;
    private OrganisationUnit unitJ;
    private OrganisationUnit unitK;
    private OrganisationUnit unitL;
    
    private OrganisationUnitGroup groupA;
    private OrganisationUnitGroup groupB;
    
    @Override
    public void setUpTest()
    {
        dataMartEngine = (DataMartEngine) getBean( DataMartEngine.ID );
        
        aggregatedOrgUnitDataValueService = (AggregatedOrgUnitDataValueService) getBean( AggregatedOrgUnitDataValueService.ID );
        
        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );
        
        dataElementService = (DataElementService) getBean( DataElementService.ID );
        
        indicatorService = (IndicatorService) getBean( IndicatorService.ID );

        dataSetService = (DataSetService) getBean( DataSetService.ID );
        
        periodService = (PeriodService) getBean( PeriodService.ID );

        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );
        
        organisationUnitGroupService = (OrganisationUnitGroupService) getBean( OrganisationUnitGroupService.ID );

        dataValueService = (DataValueService) getBean( DataValueService.ID );

        expressionService = (ExpressionService) getBean( ExpressionService.ID );
        
        categoryCombo = categoryService.getDataElementCategoryComboByName( DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );
        
        categoryOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        
        // ---------------------------------------------------------------------
        // Setup identifier Collections
        // ---------------------------------------------------------------------

        dataElementIds = new HashSet<Integer>();
        indicatorIds = new HashSet<Integer>();
        periodIds = new HashSet<Integer>();
        organisationUnitIds = new HashSet<Integer>();
        organisationUnitGroupIds = new HashSet<Integer>();
        
        // ---------------------------------------------------------------------
        // Setup DataElements
        // ---------------------------------------------------------------------

        dataElementA = createDataElement( 'A', DataElement.VALUE_TYPE_INT, DataElement.AGGREGATION_OPERATOR_SUM, categoryCombo );
        dataElementB = createDataElement( 'B', DataElement.VALUE_TYPE_BOOL, DataElement.AGGREGATION_OPERATOR_SUM, categoryCombo );

        dataElementIds.add( dataElementService.addDataElement( dataElementA ) );
        dataElementIds.add( dataElementService.addDataElement( dataElementB ) );

        // ---------------------------------------------------------------------
        // Setup DataSets (to get correct PeriodType for DataElements)
        // ---------------------------------------------------------------------

        dataSet = createDataSet( 'A', new MonthlyPeriodType() );
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
        
        Date mar01 = getDate( 2005, 3, 1 );
        Date mar31 = getDate( 2005, 3, 31 );
        Date apr01 = getDate( 2005, 4, 1 );
        Date apr30 = getDate( 2005, 4, 30 );
        Date may01 = getDate( 2005, 5, 1 );
        Date may31 = getDate( 2005, 5, 31 );
        
        periodA = createPeriod( monthly, mar01, mar31 );
        periodB = createPeriod( monthly, apr01, apr30 );
        periodC = createPeriod( monthly, may01, may31 );
        periodD = createPeriod( quarterly, mar01, may31 );
        
        periodIds.add( periodService.addPeriod( periodA ) );
        periodIds.add( periodService.addPeriod( periodB ) );
        periodIds.add( periodService.addPeriod( periodC ) );
        periodIds.add( periodService.addPeriod( periodD ) );
        
        // ---------------------------------------------------------------------
        // Setup OrganisationUnits
        // ---------------------------------------------------------------------

        unitA = createOrganisationUnit( 'A' );
        unitB = createOrganisationUnit( 'B', unitA );
        unitC = createOrganisationUnit( 'C', unitA );
        unitD = createOrganisationUnit( 'D', unitB );
        unitE = createOrganisationUnit( 'E', unitB );
        unitF = createOrganisationUnit( 'F', unitB );
        unitG = createOrganisationUnit( 'G', unitD);
        unitH = createOrganisationUnit( 'H', unitD );
        unitI = createOrganisationUnit( 'I', unitE );
        unitJ = createOrganisationUnit( 'J', unitE );
        unitK = createOrganisationUnit( 'K', unitF );
        unitL = createOrganisationUnit( 'L', unitF );

        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitA ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitB ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitC ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitD ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitE ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitF ) );
        organisationUnitService.addOrganisationUnit( unitG );
        organisationUnitService.addOrganisationUnit( unitH );
        organisationUnitService.addOrganisationUnit( unitI );
        organisationUnitService.addOrganisationUnit( unitJ );
        organisationUnitService.addOrganisationUnit( unitK );
        organisationUnitService.addOrganisationUnit( unitL );

        // ---------------------------------------------------------------------
        // Setup OrganisationUnitGroups
        // ---------------------------------------------------------------------

        groupA = createOrganisationUnitGroup( 'A' );
        groupB = createOrganisationUnitGroup( 'B' );
        
        groupA.getMembers().add( unitG );
        groupA.getMembers().add( unitI );
        groupA.getMembers().add( unitK );
        
        groupB.getMembers().add( unitH );
        groupB.getMembers().add( unitJ );
        groupB.getMembers().add( unitL );
        
        organisationUnitGroupIds.add( organisationUnitGroupService.addOrganisationUnitGroup( groupA ) );
        organisationUnitGroupIds.add( organisationUnitGroupService.addOrganisationUnitGroup( groupB ) );
        
        // ---------------------------------------------------------------------
        // Setup DataValues
        // ---------------------------------------------------------------------

        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitG, "90", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitH, "10", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitI, "35", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitJ, "25", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitK, "20", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitL, "60", categoryOptionCombo ) );
        
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitG, "70", categoryOptionCombo ) );        
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitH, "40", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitI, "65", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitJ, "55", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitK, "20", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitL, "15", categoryOptionCombo ) );
        
        dataValueService.addDataValue( createDataValue( dataElementA, periodC, unitG, "95", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodC, unitH, "40", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodC, unitI, "45", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodC, unitJ, "30", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodC, unitK, "50", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodC, unitL, "70", categoryOptionCombo ) );
        
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, unitG, T, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, unitH, T, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, unitI, F, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, unitJ, T, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, unitK, F, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, unitL, T, categoryOptionCombo ) );
        
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, unitG, T, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, unitH, F, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, unitI, T, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, unitJ, T, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, unitK, F, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, unitL, T, categoryOptionCombo ) );
        
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, unitG, F, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, unitH, T, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, unitI, F, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, unitJ, T, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, unitK, T, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, unitL, T, categoryOptionCombo ) );
    }

    @Override
    public boolean emptyDatabaseAfterTest()
    {
        return true;
    }

    @Test
    public void testSumIntDataElementDataMart()
    {
        dataElementA.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );
        
        dataElementService.updateDataElement( dataElementA );
        
        dataMartEngine.export( dataElementIds, indicatorIds, periodIds, organisationUnitIds, organisationUnitGroupIds, null );
        
        assertEquals( 145.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodA, unitA, groupA ), DELTA );
        assertEquals( 145.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodA, unitB, groupA ), DELTA );
        assertEquals( 90.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodA, unitD, groupA ), DELTA );
        assertEquals( 35.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodA, unitE, groupA ), DELTA );
        assertEquals( 20.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodA, unitF, groupA ), DELTA );
        
        assertEquals( 95.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodA, unitA, groupB ), DELTA );
        assertEquals( 95.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodA, unitB, groupB ), DELTA );
        assertEquals( 10.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodA, unitD, groupB ), DELTA );
        assertEquals( 25.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodA, unitE, groupB ), DELTA );
        assertEquals( 60.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodA, unitF, groupB ), DELTA );
        
        assertEquals( 490.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodD, unitA, groupA ), DELTA );
        assertEquals( 490.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodD, unitB, groupA ), DELTA );
        assertEquals( 255.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodD, unitD, groupA ), DELTA );
        assertEquals( 145.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodD, unitE, groupA ), DELTA );
        assertEquals( 90.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodD, unitF, groupA ), DELTA );
        
        assertEquals( 345.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodD, unitA, groupB ), DELTA );
        assertEquals( 345.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodD, unitB, groupB ), DELTA );
        assertEquals( 90.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodD, unitD, groupB ), DELTA );
        assertEquals( 110.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodD, unitE, groupB ), DELTA );
        assertEquals( 145.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodD, unitF, groupB ), DELTA );
    }

    @Test
    public void testAverageIntDataElementDataMart()
    {
        dataElementA.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_AVERAGE );
        
        dataElementService.updateDataElement( dataElementA );

        dataMartEngine.export( dataElementIds, indicatorIds, periodIds, organisationUnitIds, organisationUnitGroupIds, null );
        
        assertEquals( 145.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodA, unitA, groupA ), DELTA );
        assertEquals( 145.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodA, unitB, groupA ), DELTA );
        assertEquals( 90.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodA, unitD, groupA ), DELTA );
        assertEquals( 35.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodA, unitE, groupA ), DELTA );
        assertEquals( 20.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodA, unitF, groupA ), DELTA );
        
        assertEquals( 95.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodA, unitA, groupB ), DELTA );
        assertEquals( 95.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodA, unitB, groupB ), DELTA );
        assertEquals( 10.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodA, unitD, groupB ), DELTA );
        assertEquals( 25.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodA, unitE, groupB ), DELTA );
        assertEquals( 60.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodA, unitF, groupB ), DELTA );
        
        assertEquals( 163.6, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodD, unitA, groupA ), 1.0 );
        assertEquals( 163.6, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodD, unitB, groupA ), 1.0 );
        assertEquals( 85.1, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodD, unitD, groupA ), 1.0 );
        assertEquals( 48.3, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodD, unitE, groupA ), 1.0 );
        assertEquals( 30.2, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodD, unitF, groupA ), 1.0 );
        
        assertEquals( 115.3, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodD, unitA, groupB ), 1.0 );
        assertEquals( 115.3, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodD, unitB, groupB ), 1.0 );
        assertEquals( 30.1, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodD, unitD, groupB ), 1.0 );
        assertEquals( 36.6, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodD, unitE, groupB ), 1.0 );
        assertEquals( 48.6, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementA, categoryOptionCombo, periodD, unitF, groupB ), 1.0 );
    }
    
    @Test
    public void testSumBoolDataElementDataMart()
    {
        dataElementB.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );

        dataElementService.updateDataElement( dataElementB );

        dataMartEngine.export( dataElementIds, indicatorIds, periodIds, organisationUnitIds, organisationUnitGroupIds, null );
        
        assertEquals( 1.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodA, unitA, groupA ), DELTA );
        assertEquals( 1.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodA, unitB, groupA ), DELTA );
        assertEquals( 1.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodA, unitD, groupA ), DELTA );
        assertEquals( 0.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodA, unitE, groupA ), DELTA );
        assertEquals( 0.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodA, unitF, groupA ), DELTA );
        
        assertEquals( 3.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodA, unitA, groupB ), DELTA );
        assertEquals( 3.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodA, unitB, groupB ), DELTA );
        assertEquals( 1.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodA, unitD, groupB ), DELTA );
        assertEquals( 1.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodA, unitE, groupB ), DELTA );
        assertEquals( 1.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodA, unitF, groupB ), DELTA );
        
        assertEquals( 4.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodD, unitA, groupA ), DELTA );
        assertEquals( 4.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodD, unitB, groupA ), DELTA );
        assertEquals( 2.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodD, unitD, groupA ), DELTA );
        assertEquals( 1.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodD, unitE, groupA ), DELTA );
        assertEquals( 1.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodD, unitF, groupA ), DELTA );
        
        assertEquals( 8.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodD, unitA, groupB ), DELTA );
        assertEquals( 8.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodD, unitB, groupB ), DELTA );
        assertEquals( 2.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodD, unitD, groupB ), DELTA );
        assertEquals( 3.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodD, unitE, groupB ), DELTA );
        assertEquals( 3.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodD, unitF, groupB ), DELTA );
    }

    @Test
    public void testAverageBoolDataElementDataMart()
    {
        dataElementB.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_AVERAGE );
        
        dataElementService.updateDataElement( dataElementB );
        
        dataMartEngine.export( dataElementIds, indicatorIds, periodIds, organisationUnitIds, organisationUnitGroupIds, null );
        
        assertEquals( 33.3, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodA, unitA, groupA ), DELTA );
        assertEquals( 33.3, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodA, unitB, groupA ), DELTA );
        assertEquals( 100.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodA, unitD, groupA ), DELTA );
        assertEquals( 0.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodA, unitE, groupA ), DELTA );
        assertEquals( 0.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodA, unitF, groupA ), DELTA );
        
        assertEquals( 100.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodA, unitA, groupB ), DELTA );
        assertEquals( 100.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodA, unitB, groupB ), DELTA );
        assertEquals( 100.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodA, unitD, groupB ), DELTA );
        assertEquals( 100.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodA, unitE, groupB ), DELTA );
        assertEquals( 100.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodA, unitF, groupB ), DELTA );
        
        assertEquals( 44.3, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodD, unitA, groupA ), 1.0 );
        assertEquals( 44.3, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodD, unitB, groupA ), 1.0 );
        assertEquals( 65.9, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodD, unitD, groupA ), 1.0 );
        assertEquals( 33.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodD, unitE, groupA ), 1.2 );
        assertEquals( 34.1, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodD, unitF, groupA ), 1.0 );
        
        assertEquals( 89.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodD, unitA, groupB ), 1.0 );
        assertEquals( 89.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodD, unitB, groupB ), 1.0 );
        assertEquals( 67.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodD, unitD, groupB ), 1.1 );
        assertEquals( 100.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodD, unitE, groupB ), 1.0 );
        assertEquals( 100.0, aggregatedOrgUnitDataValueService.getAggregatedValue( dataElementB, categoryOptionCombo, periodD, unitF, groupB ), 1.0 );
    }

    @Test
    public void testIndicatorDataMart()
    {
        // ---------------------------------------------------------------------
        // Setup DataElements
        // ---------------------------------------------------------------------

        DataElement dataElementC = createDataElement( 'C', DataElement.VALUE_TYPE_INT, DataElement.AGGREGATION_OPERATOR_SUM, categoryCombo );
        DataElement dataElementD = createDataElement( 'D', DataElement.VALUE_TYPE_INT, DataElement.AGGREGATION_OPERATOR_SUM, categoryCombo );
        DataElement dataElementE = createDataElement( 'E', DataElement.VALUE_TYPE_INT, DataElement.AGGREGATION_OPERATOR_AVERAGE, categoryCombo );
        DataElement dataElementF = createDataElement( 'F', DataElement.VALUE_TYPE_INT, DataElement.AGGREGATION_OPERATOR_AVERAGE, categoryCombo );
        
        dataElementService.addDataElement( dataElementC );
        dataElementService.addDataElement( dataElementD );
        dataElementService.addDataElement( dataElementE );
        dataElementService.addDataElement( dataElementF );

        dataSet.getDataElements().add( dataElementC );
        dataSet.getDataElements().add( dataElementD );
        dataSet.getDataElements().add( dataElementE );
        dataSet.getDataElements().add( dataElementF );
        dataSetService.updateDataSet( dataSet );
        dataElementC.getDataSets().add( dataSet );
        dataElementD.getDataSets().add( dataSet );
        dataElementE.getDataSets().add( dataSet );
        dataElementF.getDataSets().add( dataSet );
        dataElementService.updateDataElement( dataElementC );
        dataElementService.updateDataElement( dataElementD );
        dataElementService.updateDataElement( dataElementE );
        dataElementService.updateDataElement( dataElementF );

        // ---------------------------------------------------------------------
        // Setup DataValues
        // ---------------------------------------------------------------------

        dataValueService.addDataValue( createDataValue( dataElementC, periodA, unitG, "10", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementC, periodA, unitH, "75", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementC, periodB, unitG, "10", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementC, periodB, unitH, "75", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementC, periodC, unitG, "10", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementC, periodC, unitH, "70", categoryOptionCombo ) );

        dataValueService.addDataValue( createDataValue( dataElementD, periodA, unitG, "90", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementD, periodA, unitH, "25", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementD, periodB, unitG, "55", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementD, periodB, unitH, "85", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementD, periodC, unitG, "15", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementD, periodC, unitH, "25", categoryOptionCombo ) );

        dataValueService.addDataValue( createDataValue( dataElementE, periodA, unitG, "5500", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementE, periodA, unitH, "4500", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementE, periodB, unitG, "7000", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementE, periodB, unitH, "2000", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementE, periodC, unitG, "5000", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementE, periodC, unitH, "3500", categoryOptionCombo ) );

        dataValueService.addDataValue( createDataValue( dataElementF, periodA, unitG, "3500", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementF, periodA, unitH, "4000", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementF, periodB, unitG, "1000", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementF, periodB, unitH, "3000", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementF, periodC, unitG, "6000", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementF, periodC, unitH, "1500", categoryOptionCombo ) );

        // ---------------------------------------------------------------------
        // Setup Indicators
        // ---------------------------------------------------------------------

        IndicatorType indicatorType = createIndicatorType( 'A' ); // Factor = 100
        
        indicatorService.addIndicatorType( indicatorType );
        
        Indicator indicatorA = createIndicator( 'A', indicatorType );
        
        String suffix = "." + categoryOptionCombo.getUid();
        
        indicatorA.setNumerator( "#{" + dataElementC.getUid() + suffix + "}*#{" + dataElementD.getUid() + suffix + "}" );
        
        indicatorA.setDenominator( "#{" + dataElementE.getUid() + suffix + "}+#{" + dataElementF.getUid() + suffix + "}" );
        
        indicatorIds.add( indicatorService.addIndicator( indicatorA ) );

        dataMartEngine.export( dataElementIds, indicatorIds, periodIds, organisationUnitIds, organisationUnitGroupIds, null );
        
        // ---------------------------------------------------------------------
        // Assert
        // ---------------------------------------------------------------------
                
        assertEquals( 10.0, aggregatedOrgUnitDataValueService.getAggregatedIndicatorValue( indicatorA, periodA, unitD, groupA ), DELTA );
        assertEquals( 22.1, aggregatedOrgUnitDataValueService.getAggregatedIndicatorValue( indicatorA, periodA, unitD, groupB ), DELTA );        
        assertEquals( 51.3, aggregatedOrgUnitDataValueService.getAggregatedIndicatorValue( indicatorA, periodD, unitD, groupA ), DELTA );        
        assertEquals( 480.6, aggregatedOrgUnitDataValueService.getAggregatedIndicatorValue( indicatorA, periodD, unitD, groupB ), 3.0 );
    }
}
