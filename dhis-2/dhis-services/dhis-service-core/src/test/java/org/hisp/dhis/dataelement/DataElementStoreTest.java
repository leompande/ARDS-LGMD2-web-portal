package org.hisp.dhis.dataelement;

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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.junit.Test;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: DataElementStoreTest.java 5742 2008-09-26 11:37:35Z larshelg $
 */
public class DataElementStoreTest
    extends DhisSpringTest
{
    private DataElementStore dataElementStore;
    
    private DataSetService dataSetService;
    
    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
        throws Exception
    {
        dataElementStore = (DataElementStore) getBean( DataElementStore.ID );
        
        dataElementService = (DataElementService) getBean( DataElementService.ID );
        
        dataSetService = (DataSetService) getBean( DataSetService.ID );
    }
    
    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void testAddDataElement()
    {
        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );
        DataElement dataElementC = createDataElement( 'C' );

        int idA = dataElementStore.save( dataElementA );
        int idB = dataElementStore.save( dataElementB );
        int idC = dataElementStore.save( dataElementC );

        dataElementA = dataElementStore.get( idA );
        assertNotNull( dataElementA );
        assertEquals( idA, dataElementA.getId() );
        assertEquals( "DataElementA", dataElementA.getName() );

        dataElementB = dataElementStore.get( idB );
        assertNotNull( dataElementB );
        assertEquals( idB, dataElementB.getId() );
        assertEquals( "DataElementB", dataElementB.getName() );

        dataElementC = dataElementStore.get( idC );
        assertNotNull( dataElementC );
        assertEquals( idC, dataElementC.getId() );
        assertEquals( "DataElementC", dataElementC.getName() );
    }

    @Test
    public void testUpdateDataElement()
    {
        DataElement dataElementA = createDataElement( 'A' );
        int idA = dataElementStore.save( dataElementA );
        dataElementA = dataElementStore.get( idA );
        assertEquals( DataElement.VALUE_TYPE_INT, dataElementA.getType() );

        dataElementA.setType( DataElement.VALUE_TYPE_BOOL );
        dataElementStore.update( dataElementA );
        dataElementA = dataElementStore.get( idA );
        assertNotNull( dataElementA.getType() );
        assertEquals( DataElement.VALUE_TYPE_BOOL, dataElementA.getType() );
    }

    @Test
    public void testDeleteAndGetDataElement()
        throws Exception
    {
        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );
        DataElement dataElementC = createDataElement( 'C' );
        DataElement dataElementD = createDataElement( 'D' );

        int idA = dataElementStore.save( dataElementA );
        int idB = dataElementStore.save( dataElementB );
        int idC = dataElementStore.save( dataElementC );
        int idD = dataElementStore.save( dataElementD );

        assertNotNull( dataElementStore.get( idA ) );
        assertNotNull( dataElementStore.get( idB ) );
        assertNotNull( dataElementStore.get( idC ) );
        assertNotNull( dataElementStore.get( idD ) );

        dataElementA = dataElementStore.get( idA );
        dataElementB = dataElementStore.get( idB );
        dataElementC = dataElementStore.get( idC );
        dataElementD = dataElementStore.get( idD );

        dataElementStore.delete( dataElementA );
        assertNull( dataElementStore.get( idA ) );
        assertNotNull( dataElementStore.get( idB ) );
        assertNotNull( dataElementStore.get( idC ) );
        assertNotNull( dataElementStore.get( idD ) );

        dataElementStore.delete( dataElementB );
        assertNull( dataElementStore.get( idA ) );
        assertNull( dataElementStore.get( idB ) );
        assertNotNull( dataElementStore.get( idC ) );
        assertNotNull( dataElementStore.get( idD ) );

        dataElementStore.delete( dataElementC );
        assertNull( dataElementStore.get( idA ) );
        assertNull( dataElementStore.get( idB ) );
        assertNull( dataElementStore.get( idC ) );
        assertNotNull( dataElementStore.get( idD ) );

        dataElementStore.delete( dataElementD );
        assertNull( dataElementStore.get( idA ) );
        assertNull( dataElementStore.get( idB ) );
        assertNull( dataElementStore.get( idC ) );
        assertNull( dataElementStore.get( idD ) );
    }

    @Test
    public void testGetDataElementByName()
    {
        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );
        int idA = dataElementStore.save( dataElementA );
        int idB = dataElementStore.save( dataElementB );

        dataElementA = dataElementStore.getByName( "DataElementA" );
        assertNotNull( dataElementA );
        assertEquals( idA, dataElementA.getId() );
        assertEquals( "DataElementA", dataElementA.getName() );

        dataElementB = dataElementStore.getByName( "DataElementB" );
        assertNotNull( dataElementB );
        assertEquals( idB, dataElementB.getId() );
        assertEquals( "DataElementB", dataElementB.getName() );

        DataElement dataElementC = dataElementStore.getByName( "DataElementC" );
        assertNull( dataElementC );
    }

    @Test
    public void testGetDataElementByShortName()
    {
        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );
        int idA = dataElementStore.save( dataElementA );
        int idB = dataElementStore.save( dataElementB );

        dataElementA = dataElementStore.getByShortName( "DataElementShortA" );
        assertNotNull( dataElementA );
        assertEquals( idA, dataElementA.getId() );
        assertEquals( "DataElementA", dataElementA.getName() );

        dataElementB = dataElementStore.getByShortName( "DataElementShortB" );
        assertNotNull( dataElementB );
        assertEquals( idB, dataElementB.getId() );
        assertEquals( "DataElementB", dataElementB.getName() );

        DataElement dataElementC = dataElementStore.getByShortName( "DataElementShortC" );
        assertNull( dataElementC );
    }
    
    @Test
    public void testGetAllDataElements()
    {
        assertEquals( 0, dataElementStore.getAll().size() );

        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );
        DataElement dataElementC = createDataElement( 'C' );
        DataElement dataElementD = createDataElement( 'D' );

        dataElementStore.save( dataElementA );
        dataElementStore.save( dataElementB );
        dataElementStore.save( dataElementC );
        dataElementStore.save( dataElementD );

        Collection<DataElement> dataElementsRef = new HashSet<DataElement>();
        dataElementsRef.add( dataElementA );
        dataElementsRef.add( dataElementB );
        dataElementsRef.add( dataElementC );
        dataElementsRef.add( dataElementD );

        Collection<DataElement> dataElements = dataElementStore.getAll();
        assertNotNull( dataElements );
        assertEquals( dataElementsRef.size(), dataElements.size() );
        assertTrue( dataElements.containsAll( dataElementsRef ) );
    }

    @Test
    public void testGetAggregateableDataElements()
    {
        assertEquals( 0, dataElementStore.getAggregateableDataElements().size() );

        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );
        DataElement dataElementC = createDataElement( 'C' );
        DataElement dataElementD = createDataElement( 'D' );
        
        dataElementA.setType( DataElement.VALUE_TYPE_INT );
        dataElementB.setType( DataElement.VALUE_TYPE_BOOL );
        dataElementC.setType( DataElement.VALUE_TYPE_STRING );
        dataElementD.setType( DataElement.VALUE_TYPE_INT );

        dataElementStore.save( dataElementA );
        dataElementStore.save( dataElementB );
        dataElementStore.save( dataElementC );
        dataElementStore.save( dataElementD );

        Collection<DataElement> dataElementsRef = new HashSet<DataElement>();
        dataElementsRef.add( dataElementA );
        dataElementsRef.add( dataElementB );
        dataElementsRef.add( dataElementD );

        Collection<DataElement> dataElements = dataElementStore.getAggregateableDataElements();
        assertNotNull( dataElements );
        assertEquals( dataElementsRef.size(), dataElements.size() );
        assertTrue( dataElements.containsAll( dataElementsRef ) );
    }

    @Test
    public void testGetAllActiveDataElements()
    {
        assertEquals( 0, dataElementStore.getAllActiveDataElements().size() );

        DataElement dataElementA = createDataElement( 'A' );
        dataElementA.setActive( true );
        DataElement dataElementB = createDataElement( 'B' );
        dataElementB.setActive( true );
        DataElement dataElementC = createDataElement( 'C' );
        dataElementC.setActive( true );
        DataElement dataElementD = createDataElement( 'D' );
        dataElementD.setActive( false );

        dataElementStore.save( dataElementA );
        dataElementStore.save( dataElementB );
        dataElementStore.save( dataElementC );
        dataElementStore.save( dataElementD );

        Collection<DataElement> dataElementsRef = new HashSet<DataElement>();
        dataElementsRef.add( dataElementA );
        dataElementsRef.add( dataElementB );
        dataElementsRef.add( dataElementC );

        assertEquals( dataElementsRef.size() + 1, dataElementStore.getAll().size() );

        Collection<DataElement> dataElements = dataElementStore.getAllActiveDataElements();
        assertNotNull( dataElements );
        assertEquals( dataElementsRef.size(), dataElements.size() );
        assertTrue( dataElements.containsAll( dataElementsRef ) );
    }

    @Test
    public void testGetDataElementsByAggregationOperator()
    {
        assertEquals( 0, dataElementStore.getDataElementsByAggregationOperator(
            DataElement.AGGREGATION_OPERATOR_AVERAGE ).size() );
        assertEquals( 0, dataElementStore.getDataElementsByAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM )
            .size() );

        DataElement dataElementA = createDataElement( 'A' );
        dataElementA.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_AVERAGE );
        DataElement dataElementB = createDataElement( 'B' );
        dataElementB.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );
        DataElement dataElementC = createDataElement( 'C' );
        dataElementC.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );
        DataElement dataElementD = createDataElement( 'D' );
        dataElementD.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );

        dataElementStore.save( dataElementA );
        dataElementStore.save( dataElementB );
        dataElementStore.save( dataElementC );
        dataElementStore.save( dataElementD );

        assertEquals( 1, dataElementStore.getDataElementsByAggregationOperator(
            DataElement.AGGREGATION_OPERATOR_AVERAGE ).size() );
        assertEquals( 3, dataElementStore.getDataElementsByAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM )
            .size() );
    }
    
    @Test
    public void testGetDataElementsByDomainType()
    {
        assertEquals( 0, dataElementStore.getDataElementsByDomainType( DataElement.DOMAIN_TYPE_AGGREGATE ).size() );
        assertEquals( 0, dataElementStore.getDataElementsByDomainType( DataElement.DOMAIN_TYPE_PATIENT ).size() );

        DataElement dataElementA = createDataElement( 'A' );
        dataElementA.setDomainType( DataElement.DOMAIN_TYPE_AGGREGATE );
        DataElement dataElementB = createDataElement( 'B' );
        dataElementB.setDomainType( DataElement.DOMAIN_TYPE_PATIENT );
        DataElement dataElementC = createDataElement( 'C' );
        dataElementC.setDomainType( DataElement.DOMAIN_TYPE_PATIENT );
        DataElement dataElementD = createDataElement( 'D' );
        dataElementD.setDomainType( DataElement.DOMAIN_TYPE_PATIENT );

        dataElementStore.save( dataElementA );
        dataElementStore.save( dataElementB );
        dataElementStore.save( dataElementC );
        dataElementStore.save( dataElementD );

        assertEquals( 1, dataElementStore.getDataElementsByDomainType( DataElement.DOMAIN_TYPE_AGGREGATE ).size() );
        assertEquals( 3, dataElementStore.getDataElementsByDomainType( DataElement.DOMAIN_TYPE_PATIENT ).size() );
    }

    @Test
    public void testGetDataElementsByType()
    {
        assertEquals( 0, dataElementStore.getDataElementsByType( DataElement.VALUE_TYPE_INT ).size() );
        assertEquals( 0, dataElementStore.getDataElementsByType( DataElement.VALUE_TYPE_BOOL ).size() );

        DataElement dataElementA = createDataElement( 'A' );
        dataElementA.setType( DataElement.VALUE_TYPE_INT );
        DataElement dataElementB = createDataElement( 'B' );
        dataElementB.setType( DataElement.VALUE_TYPE_BOOL );
        DataElement dataElementC = createDataElement( 'C' );
        dataElementC.setType( DataElement.VALUE_TYPE_BOOL );
        DataElement dataElementD = createDataElement( 'D' );
        dataElementD.setType( DataElement.VALUE_TYPE_BOOL );

        dataElementStore.save( dataElementA );
        dataElementStore.save( dataElementB );
        dataElementStore.save( dataElementC );
        dataElementStore.save( dataElementD );

        assertEquals( 1, dataElementStore.getDataElementsByType( DataElement.VALUE_TYPE_INT ).size() );
        assertEquals( 3, dataElementStore.getDataElementsByType( DataElement.VALUE_TYPE_BOOL ).size() );
    }

    @Test
    public void testGetDataElementAggregationLevels()
    {
        List<Integer> aggregationLevels = Arrays.asList( 3, 5 );
        
        DataElement dataElementA = createDataElement( 'A' );
        dataElementA.setAggregationLevels( aggregationLevels );
        
        int idA = dataElementStore.save( dataElementA );
        
        assertNotNull( dataElementStore.get( idA ).getAggregationLevels() );
        assertEquals( 2, dataElementStore.get( idA ).getAggregationLevels().size() );
        assertEquals( aggregationLevels, dataElementStore.get( idA ).getAggregationLevels() );
    }
        
    @Test
    public void testGetDataElementsByAggregationLevel()
    {
        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );
        DataElement dataElementC = createDataElement( 'C' );
        
        dataElementA.getAggregationLevels().addAll( Arrays.asList( 3, 5 ) );
        dataElementB.getAggregationLevels().addAll( Arrays.asList( 4, 5 ) );

        dataElementStore.save( dataElementA );
        dataElementStore.save( dataElementB );
        dataElementStore.save( dataElementC );
        
        Collection<DataElement> dataElements = dataElementStore.getDataElementsByAggregationLevel( 2 );
        
        assertEquals( 0, dataElements.size() );
        
        dataElements = dataElementStore.getDataElementsByAggregationLevel( 3 );
        
        assertEquals( 1, dataElements.size() );

        dataElements = dataElementStore.getDataElementsByAggregationLevel( 4 );
        
        assertEquals( 1, dataElements.size() );
        
        dataElements = dataElementStore.getDataElementsByAggregationLevel( 5 );
        
        assertEquals( 2, dataElements.size() );
        assertTrue( dataElements.contains( dataElementA ) );
        assertTrue( dataElements.contains( dataElementB ) );
    }
    
    @Test
    public void testGetDataElementsZeroIsSignificant()
    {
        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );
        DataElement dataElementC = createDataElement( 'C' );
        DataElement dataElementD = createDataElement( 'D' );

        dataElementA.setZeroIsSignificant( true );
        dataElementB.setZeroIsSignificant( true );
        
        dataElementStore.save( dataElementA );
        dataElementStore.save( dataElementB );
        dataElementStore.save( dataElementC );
        dataElementStore.save( dataElementD );
        
        Collection<DataElement> dataElements = dataElementStore.getDataElementsByZeroIsSignificant( true );
        
        assertTrue( equals( dataElements, dataElementA, dataElementB ) );
    }
    
    @Test
    public void testGetDataElements()
    {
        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );
        DataElement dataElementC = createDataElement( 'C' );
        DataElement dataElementD = createDataElement( 'D' );
        DataElement dataElementE = createDataElement( 'E' );
        DataElement dataElementF = createDataElement( 'F' );
        
        dataElementStore.save( dataElementA );
        dataElementStore.save( dataElementB );
        dataElementStore.save( dataElementC );
        dataElementStore.save( dataElementD );
        dataElementStore.save( dataElementE );
        dataElementStore.save( dataElementF );
        
        DataSet dataSetA = createDataSet( 'A', new MonthlyPeriodType() );
        DataSet dataSetB = createDataSet( 'B', new MonthlyPeriodType() );
        
        dataSetA.getDataElements().add( dataElementA );
        dataSetA.getDataElements().add( dataElementC );
        dataSetA.getDataElements().add( dataElementF );
        dataSetB.getDataElements().add( dataElementD );
        dataSetB.getDataElements().add( dataElementF );
        
        dataSetService.addDataSet( dataSetA );
        dataSetService.addDataSet( dataSetB );
        
        Collection<DataSet> dataSets = new HashSet<DataSet>();
        dataSets.add( dataSetA );
        dataSets.add( dataSetB );
        
        Collection<DataElement> dataElements = dataElementStore.getDataElementsByDataSets( dataSets );
        
        assertNotNull( dataElements );
        assertEquals( 4, dataElements.size() );
        assertTrue( dataElements.contains( dataElementA ) );
        assertTrue( dataElements.contains( dataElementC ) );
        assertTrue( dataElements.contains( dataElementD ) );
        assertTrue( dataElements.contains( dataElementF ) );
    }
}
