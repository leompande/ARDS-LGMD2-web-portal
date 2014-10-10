package org.hisp.dhis.datamart.crosstab;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.apache.commons.lang.RandomStringUtils;
import org.hisp.dhis.DhisTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datamart.CrossTabDataValue;
import org.hisp.dhis.datamart.crosstab.jdbc.CrossTabStore;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.jdbc.batchhandler.GenericBatchHandler;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.WeeklyPeriodType;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id: CrossTabServiceTest.java 6217 2008-11-06 18:53:04Z larshelg $
 */
public class CrossTabServiceTest
    extends DhisTest
{
    private CrossTabService crossTabService;
    
    private BatchHandlerFactory batchHandlerFactory;
    
    private Iterator<Period> generatedPeriods;

    private List<DataElementOperand> operands;
    private Collection<Integer> periodIds;
    private Collection<Integer> organisationUnitIds;
    
    private DataElementCategoryOptionCombo defaultOptionCombo;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
    {
        crossTabService = (CrossTabService) getBean( CrossTabService.ID );
        
        batchHandlerFactory = (BatchHandlerFactory) getBean( "inMemoryBatchHandlerFactory" );
        
        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );
        
        dataElementService = (DataElementService) getBean( DataElementService.ID );
        
        periodService = (PeriodService) getBean( PeriodService.ID );
        
        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );
        
        dataValueService = (DataValueService) getBean( DataValueService.ID );
        
        Calendar calendar = Calendar.getInstance();
        
        calendar.set( 2007, 0, 1 );
        
        WeeklyPeriodType periodType = (WeeklyPeriodType) periodService.getPeriodTypeByName( WeeklyPeriodType.NAME );
        
        Period period = createPeriod( periodType, calendar.getTime(), calendar.getTime() );

        generatedPeriods = periodType.generatePeriods( period ).iterator();
        
        setUpTestData();
    }
    
    @Override
    public boolean emptyDatabaseAfterTest()
    {
        return true;
    }

    private void setUpTestData()
    {
        DataElementCategoryOption categoryOptionA = new DataElementCategoryOption( "Male" );
        DataElementCategoryOption categoryOptionB = new DataElementCategoryOption( "Female" );
        
        categoryService.addDataElementCategoryOption( categoryOptionA );
        categoryService.addDataElementCategoryOption( categoryOptionB );
        
        DataElementCategory categoryA = new DataElementCategory( "Gender" );
        categoryA.getCategoryOptions().add( categoryOptionA );
        categoryA.getCategoryOptions().add( categoryOptionB );
        
        categoryService.addDataElementCategory( categoryA );
        
        DataElementCategoryCombo categoryComboA = new DataElementCategoryCombo( "Gender" );
        categoryComboA.getCategories().add( categoryA );        
        
        categoryService.addDataElementCategoryCombo( categoryComboA );
        
        categoryService.generateOptionCombos( categoryComboA );
        
        Collection<DataElementCategoryOptionCombo> categoryOptionCombos = categoryService.getAllDataElementCategoryOptionCombos();
                
        Character[] characters = { 'A', 'B', 'C', 'D', 'E' };
        
        periodIds = new HashSet<Integer>();
        organisationUnitIds = new HashSet<Integer>();
        
        Collection<DataElement> dataElements = new HashSet<DataElement>();
        Collection<Period> periods = new HashSet<Period>();
        Collection<OrganisationUnit> organisationUnits = new HashSet<OrganisationUnit>();
        
        for ( Character character : characters )
        {
            DataElement dataElement = createDataElement( character, categoryComboA );
            Period period = generatedPeriods.next();
            OrganisationUnit organisationUnit = createOrganisationUnit( character );
            
            dataElements.add( dataElement );
            periods.add( period );
            organisationUnits.add( organisationUnit );
            
            dataElementService.addDataElement( dataElement );
            periodIds.add( periodService.addPeriod( period ) );
            organisationUnitIds.add( organisationUnitService.addOrganisationUnit( organisationUnit ) );
        }
        
        defaultOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        
        operands = new ArrayList<DataElementOperand>( categoryService.getOperands( dataElements ) );
        
        for ( DataElement dataElement : dataElements )
        {
            for ( DataElementCategoryOptionCombo categoryOptionCombo : categoryOptionCombos )
            {
                for ( Period period : periods )
                {
                    for ( OrganisationUnit organisationUnit : organisationUnits )
                    {
                        dataValueService.addDataValue( createDataValue( dataElement, period, organisationUnit, "10", categoryOptionCombo, defaultOptionCombo ) );
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void testPopulateCrossTabValue()
        throws Exception
    {
        String key = crossTabService.createCrossTabTable( operands );
        crossTabService.populateCrossTabTable( operands, periodIds, organisationUnitIds, key ).get();
        
        Collection<CrossTabDataValue> values = crossTabService.getCrossTabDataValues( operands, periodIds, organisationUnitIds, key );
        
        assertNotNull( values );
        
        assertEquals( 25, values.size() );
        
        for ( CrossTabDataValue crossTabValue : values )
        {
            assertTrue( crossTabValue.getPeriodId() != 0 );
            assertTrue( crossTabValue.getSourceId() != 0 );
            
            assertNotNull( crossTabValue.getValueMap() );
            
            assertEquals( 10, crossTabValue.getValueMap().size() );
            
            for ( String value : crossTabValue.getValueMap().values() )
            {
                assertEquals( "10", value );
            }
        }
    }
    
    @Test
    public void testPopulateAggregatedDataCache()
    {
        String key = RandomStringUtils.randomAlphanumeric( 8 );

        Period period = new MonthlyPeriodType().createPeriod();
        period.setId( 1 );
        
        OrganisationUnit unit = createOrganisationUnit( 'A' );
        unit.setId( 1 );
        
        crossTabService.createAggregatedDataCache( operands, key );

        BatchHandler<Object> batchHandler = batchHandlerFactory.createBatchHandler( GenericBatchHandler.class ).
            setTableName( CrossTabStore.AGGREGATEDDATA_CACHE_PREFIX + key ).init();        

        List<Object> valueList = new ArrayList<Object>( operands.size() + 2 );
        valueList.add( 1 );
        valueList.add( 1 );
        
        for ( int i = 0; i < operands.size(); i++ )
        {
            valueList.add( 10.0 );
        }
        
        batchHandler.addObject( valueList );
        
        batchHandler.flush();
        
        Map<DataElementOperand, Double> valueMap = crossTabService.getAggregatedDataCacheValue( operands, period, unit, null, key );
        
        for ( DataElementOperand operand : valueMap.keySet() )
        {
            assertNotNull( valueMap.get( operand ) );
            assertEquals( 10.0, valueMap.get( operand ), DELTA );
        }
    }
}
