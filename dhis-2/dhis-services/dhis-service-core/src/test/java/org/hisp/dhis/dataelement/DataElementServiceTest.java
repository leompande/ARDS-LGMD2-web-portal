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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.junit.Test;

/**
 * @author Kristian Nordal
 */
public class DataElementServiceTest
    extends DhisSpringTest
{
    private DataElementService dataElementService;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
        throws Exception
    {
        dataElementService = (DataElementService) getBean( DataElementService.ID );
    }
    
    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void testAddDataElement()
        throws Exception
    {
        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );
        DataElement dataElementC = createDataElement( 'C' );

        int idA = dataElementService.addDataElement( dataElementA );
        int idB = dataElementService.addDataElement( dataElementB );
        int idC = dataElementService.addDataElement( dataElementC );

        assertNotNull( dataElementA.getUid() );
        assertNotNull( dataElementB.getUid() );
        assertNotNull( dataElementC.getUid() );
        
        assertNotNull( dataElementA.getLastUpdated() );
        assertNotNull( dataElementB.getLastUpdated() );
        assertNotNull( dataElementC.getLastUpdated() );
        
        dataElementA = dataElementService.getDataElement( idA );
        assertNotNull( dataElementA );
        assertEquals( idA, dataElementA.getId() );
        assertEquals( "DataElementA", dataElementA.getName() );

        dataElementB = dataElementService.getDataElement( idB );
        assertNotNull( dataElementB );
        assertEquals( idB, dataElementB.getId() );
        assertEquals( "DataElementB", dataElementB.getName() );

        dataElementC = dataElementService.getDataElement( idC );
        assertNotNull( dataElementC );
        assertEquals( idC, dataElementC.getId() );
        assertEquals( "DataElementC", dataElementC.getName() );
    }

    @Test
    public void testUpdateDataElement()
        throws Exception
    {
        DataElement dataElementA = createDataElement( 'A' );
        
        int idA = dataElementService.addDataElement( dataElementA );
        assertNotNull( dataElementA.getUid() );
        assertNotNull( dataElementA.getLastUpdated() );
        
        dataElementA = dataElementService.getDataElement( idA );
        assertEquals( DataElement.VALUE_TYPE_INT, dataElementA.getType() );

        dataElementA.setType( DataElement.VALUE_TYPE_BOOL );
        dataElementService.updateDataElement( dataElementA );
        dataElementA = dataElementService.getDataElement( idA );
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

        int idA = dataElementService.addDataElement( dataElementA );
        int idB = dataElementService.addDataElement( dataElementB );
        int idC = dataElementService.addDataElement( dataElementC );
        int idD = dataElementService.addDataElement( dataElementD );

        assertNotNull( dataElementService.getDataElement( idA ) );
        assertNotNull( dataElementService.getDataElement( idB ) );
        assertNotNull( dataElementService.getDataElement( idC ) );
        assertNotNull( dataElementService.getDataElement( idD ) );

        dataElementA = dataElementService.getDataElement( idA );
        dataElementB = dataElementService.getDataElement( idB );
        dataElementC = dataElementService.getDataElement( idC );
        dataElementD = dataElementService.getDataElement( idD );

        dataElementService.deleteDataElement( dataElementA );
        assertNull( dataElementService.getDataElement( idA ) );
        assertNotNull( dataElementService.getDataElement( idB ) );
        assertNotNull( dataElementService.getDataElement( idC ) );
        assertNotNull( dataElementService.getDataElement( idD ) );

        dataElementService.deleteDataElement( dataElementB );
        assertNull( dataElementService.getDataElement( idA ) );
        assertNull( dataElementService.getDataElement( idB ) );
        assertNotNull( dataElementService.getDataElement( idC ) );
        assertNotNull( dataElementService.getDataElement( idD ) );

        dataElementService.deleteDataElement( dataElementC );
        assertNull( dataElementService.getDataElement( idA ) );
        assertNull( dataElementService.getDataElement( idB ) );
        assertNull( dataElementService.getDataElement( idC ) );
        assertNotNull( dataElementService.getDataElement( idD ) );

        dataElementService.deleteDataElement( dataElementD );
        assertNull( dataElementService.getDataElement( idA ) );
        assertNull( dataElementService.getDataElement( idB ) );
        assertNull( dataElementService.getDataElement( idC ) );
        assertNull( dataElementService.getDataElement( idD ) );
    }

    @Test
    public void testGetDataElementByCode()
        throws Exception
    {
        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );
        DataElement dataElementC = createDataElement( 'C' );

        dataElementA.setCode( "codeA");
        dataElementB.setCode( "codeB");
        dataElementC.setCode( "codeC");

        int idA = dataElementService.addDataElement( dataElementA );
        int idB = dataElementService.addDataElement( dataElementB );
        dataElementService.addDataElement( dataElementC );

        dataElementA = dataElementService.getDataElementByCode( "codeA" );
        assertNotNull( dataElementA );
        assertEquals( idA, dataElementA.getId() );
        assertEquals( "DataElementA", dataElementA.getName() );

        dataElementB = dataElementService.getDataElementByCode( "codeB" );
        assertNotNull( dataElementB );
        assertEquals( idB, dataElementB.getId() );
        assertEquals( "DataElementB", dataElementB.getName() );
        
        DataElement dataElementE = dataElementService.getDataElementByCode( "codeE" );
        assertNull( dataElementE );
    }

    @Test
    public void testGetDataElementByName()
        throws Exception
    {
        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );
        int idA = dataElementService.addDataElement( dataElementA );
        int idB = dataElementService.addDataElement( dataElementB );

        dataElementA = dataElementService.getDataElementByName( "DataElementA" );
        assertNotNull( dataElementA );
        assertEquals( idA, dataElementA.getId() );
        assertEquals( "DataElementA", dataElementA.getName() );

        dataElementB = dataElementService.getDataElementByName( "DataElementB" );
        assertNotNull( dataElementB );
        assertEquals( idB, dataElementB.getId() );
        assertEquals( "DataElementB", dataElementB.getName() );

        DataElement dataElementC = dataElementService.getDataElementByName( "DataElementC" );
        assertNull( dataElementC );
    }

    @Test
    public void testGetDataElementByShortName()
        throws Exception
    {
        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );
        int idA = dataElementService.addDataElement( dataElementA );
        int idB = dataElementService.addDataElement( dataElementB );

        dataElementA = dataElementService.getDataElementByShortName( "DataElementShortA" );
        assertNotNull( dataElementA );
        assertEquals( idA, dataElementA.getId() );
        assertEquals( "DataElementA", dataElementA.getName() );

        dataElementB = dataElementService.getDataElementByShortName( "DataElementShortB" );
        assertNotNull( dataElementB );
        assertEquals( idB, dataElementB.getId() );
        assertEquals( "DataElementB", dataElementB.getName() );

        DataElement dataElementC = dataElementService.getDataElementByShortName( "DataElementShortC" );
        assertNull( dataElementC );
    }

    @Test
    public void testGetAllDataElements()
        throws Exception
    {
        assertEquals( 0, dataElementService.getAllDataElements().size() );

        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );
        DataElement dataElementC = createDataElement( 'C' );
        DataElement dataElementD = createDataElement( 'D' );

        dataElementService.addDataElement( dataElementA );
        dataElementService.addDataElement( dataElementB );
        dataElementService.addDataElement( dataElementC );
        dataElementService.addDataElement( dataElementD );

        Collection<DataElement> dataElementsRef = new HashSet<DataElement>();
        dataElementsRef.add( dataElementA );
        dataElementsRef.add( dataElementB );
        dataElementsRef.add( dataElementC );
        dataElementsRef.add( dataElementD );

        Collection<DataElement> dataElements = dataElementService.getAllDataElements();
        assertNotNull( dataElements );
        assertEquals( dataElementsRef.size(), dataElements.size() );
        assertTrue( dataElements.containsAll( dataElementsRef ) );
    }

    @Test
    public void testGetAggregateableDataElements()
    {
        assertEquals( 0, dataElementService.getAggregateableDataElements().size() );

        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );
        DataElement dataElementC = createDataElement( 'C' );
        DataElement dataElementD = createDataElement( 'D' );
        
        dataElementA.setType( DataElement.VALUE_TYPE_INT );
        dataElementB.setType( DataElement.VALUE_TYPE_BOOL );
        dataElementC.setType( DataElement.VALUE_TYPE_STRING );
        dataElementD.setType( DataElement.VALUE_TYPE_INT );

        dataElementService.addDataElement( dataElementA );
        dataElementService.addDataElement( dataElementB );
        dataElementService.addDataElement( dataElementC );
        dataElementService.addDataElement( dataElementD );

        Collection<DataElement> dataElementsRef = new HashSet<DataElement>();
        dataElementsRef.add( dataElementA );
        dataElementsRef.add( dataElementB );
        dataElementsRef.add( dataElementD );

        Collection<DataElement> dataElements = dataElementService.getAggregateableDataElements();
        assertNotNull( dataElements );
        assertEquals( dataElementsRef.size(), dataElements.size() );
        assertTrue( dataElements.containsAll( dataElementsRef ) );
    }

    @Test
    public void testGetAllActiveDataElements()
        throws Exception
    {
        assertEquals( 0, dataElementService.getAllActiveDataElements().size() );

        DataElement dataElementA = createDataElement( 'A' );
        dataElementA.setActive( true );
        DataElement dataElementB = createDataElement( 'B' );
        dataElementB.setActive( true );
        DataElement dataElementC = createDataElement( 'C' );
        dataElementC.setActive( true );
        DataElement dataElementD = createDataElement( 'D' );
        dataElementD.setActive( false );

        dataElementService.addDataElement( dataElementA );
        dataElementService.addDataElement( dataElementB );
        dataElementService.addDataElement( dataElementC );
        dataElementService.addDataElement( dataElementD );

        Collection<DataElement> dataElementsRef = new HashSet<DataElement>();
        dataElementsRef.add( dataElementA );
        dataElementsRef.add( dataElementB );
        dataElementsRef.add( dataElementC );

        assertEquals( dataElementsRef.size() + 1, dataElementService.getAllDataElements().size() );

        Collection<DataElement> dataElements = dataElementService.getAllActiveDataElements();
        assertNotNull( dataElements );
        assertEquals( dataElementsRef.size(), dataElements.size() );
        assertTrue( dataElements.containsAll( dataElementsRef ) );
    }

    @Test
    public void testGetDataElementsByAggregationOperator()
        throws Exception
    {
        assertEquals( 0, dataElementService.getDataElementsByAggregationOperator(
            DataElement.AGGREGATION_OPERATOR_AVERAGE ).size() );
        assertEquals( 0, dataElementService.getDataElementsByAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM )
            .size() );

        DataElement dataElementA = createDataElement( 'A' );
        dataElementA.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_AVERAGE );
        DataElement dataElementB = createDataElement( 'B' );
        dataElementB.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );
        DataElement dataElementC = createDataElement( 'C' );
        dataElementC.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );
        DataElement dataElementD = createDataElement( 'D' );
        dataElementD.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );

        dataElementService.addDataElement( dataElementA );
        dataElementService.addDataElement( dataElementB );
        dataElementService.addDataElement( dataElementC );
        dataElementService.addDataElement( dataElementD );

        assertEquals( 1, dataElementService.getDataElementsByAggregationOperator(
            DataElement.AGGREGATION_OPERATOR_AVERAGE ).size() );
        assertEquals( 3, dataElementService.getDataElementsByAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM )
            .size() );
    }
    
    @Test
    public void testGetDataElementsByDomainType()
    {
        assertEquals( 0, dataElementService.getDataElementsByType( DataElement.DOMAIN_TYPE_AGGREGATE ).size() );
        assertEquals( 0, dataElementService.getDataElementsByType( DataElement.DOMAIN_TYPE_PATIENT ).size() );

        DataElement dataElementA = createDataElement( 'A' );
        dataElementA.setType( DataElement.DOMAIN_TYPE_AGGREGATE );
        DataElement dataElementB = createDataElement( 'B' );
        dataElementB.setType( DataElement.DOMAIN_TYPE_PATIENT );
        DataElement dataElementC = createDataElement( 'C' );
        dataElementC.setType( DataElement.DOMAIN_TYPE_PATIENT );
        DataElement dataElementD = createDataElement( 'D' );
        dataElementD.setType( DataElement.DOMAIN_TYPE_PATIENT );

        dataElementService.addDataElement( dataElementA );
        dataElementService.addDataElement( dataElementB );
        dataElementService.addDataElement( dataElementC );
        dataElementService.addDataElement( dataElementD );

        assertEquals( 1, dataElementService.getDataElementsByType( DataElement.DOMAIN_TYPE_AGGREGATE ).size() );
        assertEquals( 3, dataElementService.getDataElementsByType( DataElement.DOMAIN_TYPE_PATIENT ).size() );
    }

    @Test
    public void testGetDataElementsByType()
    {
        assertEquals( 0, dataElementService.getDataElementsByType( DataElement.VALUE_TYPE_INT ).size() );
        assertEquals( 0, dataElementService.getDataElementsByType( DataElement.VALUE_TYPE_BOOL ).size() );

        DataElement dataElementA = createDataElement( 'A' );
        dataElementA.setType( DataElement.VALUE_TYPE_INT );
        DataElement dataElementB = createDataElement( 'B' );
        dataElementB.setType( DataElement.VALUE_TYPE_BOOL );
        DataElement dataElementC = createDataElement( 'C' );
        dataElementC.setType( DataElement.VALUE_TYPE_BOOL );
        DataElement dataElementD = createDataElement( 'D' );
        dataElementD.setType( DataElement.VALUE_TYPE_BOOL );

        dataElementService.addDataElement( dataElementA );
        dataElementService.addDataElement( dataElementB );
        dataElementService.addDataElement( dataElementC );
        dataElementService.addDataElement( dataElementD );

        assertEquals( 1, dataElementService.getDataElementsByType( DataElement.VALUE_TYPE_INT ).size() );
        assertEquals( 3, dataElementService.getDataElementsByType( DataElement.VALUE_TYPE_BOOL ).size() );
    }

    // -------------------------------------------------------------------------
    // DataElementGroup
    // -------------------------------------------------------------------------

    @Test
    public void testAddDataElementGroup()
        throws Exception
    {
        DataElementGroup dataElementGroupA = new DataElementGroup( "DataElementGroupA" );
        DataElementGroup dataElementGroupB = new DataElementGroup( "DataElementGroupB" );
        DataElementGroup dataElementGroupC = new DataElementGroup( "DataElementGroupC" );

        int idA = dataElementService.addDataElementGroup( dataElementGroupA );
        int idB = dataElementService.addDataElementGroup( dataElementGroupB );
        int idC = dataElementService.addDataElementGroup( dataElementGroupC );

        dataElementGroupA = dataElementService.getDataElementGroup( idA );
        assertNotNull( dataElementGroupA );
        assertEquals( idA, dataElementGroupA.getId() );
        assertEquals( "DataElementGroupA", dataElementGroupA.getName() );

        dataElementGroupB = dataElementService.getDataElementGroup( idB );
        assertNotNull( dataElementGroupB );
        assertEquals( idB, dataElementGroupB.getId() );
        assertEquals( "DataElementGroupB", dataElementGroupB.getName() );

        dataElementGroupC = dataElementService.getDataElementGroup( idC );
        assertNotNull( dataElementGroupC );
        assertEquals( idC, dataElementGroupC.getId() );
        assertEquals( "DataElementGroupC", dataElementGroupC.getName() );
    }

    @Test
    public void testUpdateDataElementGroup()
        throws Exception
    {
        DataElementGroup dataElementGroupA = new DataElementGroup( "DataElementGroupA" );
        DataElementGroup dataElementGroupB = new DataElementGroup( "DataElementGroupB" );
        DataElementGroup dataElementGroupC = new DataElementGroup( "DataElementGroupC" );

        int idA = dataElementService.addDataElementGroup( dataElementGroupA );
        int idB = dataElementService.addDataElementGroup( dataElementGroupB );
        int idC = dataElementService.addDataElementGroup( dataElementGroupC );

        dataElementGroupA = dataElementService.getDataElementGroup( idA );
        assertNotNull( dataElementGroupA );
        assertEquals( idA, dataElementGroupA.getId() );
        assertEquals( "DataElementGroupA", dataElementGroupA.getName() );

        dataElementGroupA.setName( "DataElementGroupAA" );
        dataElementService.updateDataElementGroup( dataElementGroupA );

        dataElementGroupA = dataElementService.getDataElementGroup( idA );
        assertNotNull( dataElementGroupA );
        assertEquals( idA, dataElementGroupA.getId() );
        assertEquals( "DataElementGroupAA", dataElementGroupA.getName() );

        dataElementGroupB = dataElementService.getDataElementGroup( idB );
        assertNotNull( dataElementGroupB );
        assertEquals( idB, dataElementGroupB.getId() );
        assertEquals( "DataElementGroupB", dataElementGroupB.getName() );

        dataElementGroupC = dataElementService.getDataElementGroup( idC );
        assertNotNull( dataElementGroupC );
        assertEquals( idC, dataElementGroupC.getId() );
        assertEquals( "DataElementGroupC", dataElementGroupC.getName() );
    }

    @Test
    public void testDeleteAndGetDataElementGroup()
        throws Exception
    {
        DataElementGroup dataElementGroupA = new DataElementGroup( "DataElementGroupA" );
        DataElementGroup dataElementGroupB = new DataElementGroup( "DataElementGroupB" );
        DataElementGroup dataElementGroupC = new DataElementGroup( "DataElementGroupC" );
        DataElementGroup dataElementGroupD = new DataElementGroup( "DataElementGroupD" );

        int idA = dataElementService.addDataElementGroup( dataElementGroupA );
        int idB = dataElementService.addDataElementGroup( dataElementGroupB );
        int idC = dataElementService.addDataElementGroup( dataElementGroupC );
        int idD = dataElementService.addDataElementGroup( dataElementGroupD );

        assertNotNull( dataElementService.getDataElementGroup( idA ) );
        assertNotNull( dataElementService.getDataElementGroup( idB ) );
        assertNotNull( dataElementService.getDataElementGroup( idC ) );
        assertNotNull( dataElementService.getDataElementGroup( idD ) );

        dataElementService.deleteDataElementGroup( dataElementGroupA );
        assertNull( dataElementService.getDataElementGroup( idA ) );
        assertNotNull( dataElementService.getDataElementGroup( idB ) );
        assertNotNull( dataElementService.getDataElementGroup( idC ) );
        assertNotNull( dataElementService.getDataElementGroup( idD ) );

        dataElementService.deleteDataElementGroup( dataElementGroupB );
        assertNull( dataElementService.getDataElementGroup( idA ) );
        assertNull( dataElementService.getDataElementGroup( idB ) );
        assertNotNull( dataElementService.getDataElementGroup( idC ) );
        assertNotNull( dataElementService.getDataElementGroup( idD ) );

        dataElementService.deleteDataElementGroup( dataElementGroupC );
        assertNull( dataElementService.getDataElementGroup( idA ) );
        assertNull( dataElementService.getDataElementGroup( idB ) );
        assertNull( dataElementService.getDataElementGroup( idC ) );
        assertNotNull( dataElementService.getDataElementGroup( idD ) );

        dataElementService.deleteDataElementGroup( dataElementGroupD );
        assertNull( dataElementService.getDataElementGroup( idA ) );
        assertNull( dataElementService.getDataElementGroup( idB ) );
        assertNull( dataElementService.getDataElementGroup( idC ) );
        assertNull( dataElementService.getDataElementGroup( idD ) );
    }

    @Test
    public void testGetDataElementGroupByName()
        throws Exception
    {
        DataElementGroup dataElementGroupA = new DataElementGroup( "DataElementGroupA" );
        DataElementGroup dataElementGroupB = new DataElementGroup( "DataElementGroupB" );
        int idA = dataElementService.addDataElementGroup( dataElementGroupA );
        int idB = dataElementService.addDataElementGroup( dataElementGroupB );

        assertNotNull( dataElementService.getDataElementGroup( idA ) );
        assertNotNull( dataElementService.getDataElementGroup( idB ) );

        dataElementGroupA = dataElementService.getDataElementGroupByName( "DataElementGroupA" );
        assertNotNull( dataElementGroupA );
        assertEquals( idA, dataElementGroupA.getId() );
        assertEquals( "DataElementGroupA", dataElementGroupA.getName() );

        dataElementGroupB = dataElementService.getDataElementGroupByName( "DataElementGroupB" );
        assertNotNull( dataElementGroupB );
        assertEquals( idB, dataElementGroupB.getId() );
        assertEquals( "DataElementGroupB", dataElementGroupB.getName() );

        DataElementGroup dataElementGroupC = dataElementService.getDataElementGroupByName( "DataElementGroupC" );
        assertNull( dataElementGroupC );
    }

    @Test
    public void testGetGroupsContainingDataElement() throws Exception
    {
        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );
        DataElement dataElementC = createDataElement( 'C' );
        DataElement dataElementD = createDataElement( 'D' );
        dataElementService.addDataElement( dataElementA );
        dataElementService.addDataElement( dataElementB );
        dataElementService.addDataElement( dataElementC );
        dataElementService.addDataElement( dataElementD );
        
        DataElementGroup dataElementGroupA = new DataElementGroup( "DataElementGroupA" );
        DataElementGroup dataElementGroupB = new DataElementGroup( "DataElementGroupB" );
        DataElementGroup dataElementGroupC = new DataElementGroup( "DataElementGroupC" );
        DataElementGroup dataElementGroupD = new DataElementGroup( "DataElementGroupD" );
        
        Set<DataElement> membersA = new HashSet<DataElement>();
        membersA.add( dataElementA );
        membersA.add( dataElementB );
        membersA.add( dataElementC );
        
        Set<DataElement> membersB = new HashSet<DataElement>();
        membersB.add( dataElementC );
        membersB.add( dataElementD );

        dataElementGroupA.setMembers( membersA );
        dataElementGroupB.setMembers( membersB );
        dataElementGroupC.setMembers( membersA );
        dataElementGroupD.setMembers( membersB );
        
        dataElementService.addDataElementGroup( dataElementGroupA );
        dataElementService.addDataElementGroup( dataElementGroupB );
        dataElementService.addDataElementGroup( dataElementGroupC );
        dataElementService.addDataElementGroup( dataElementGroupD );
        
        Collection<DataElementGroup> groupsA = dataElementService.getGroupsContainingDataElement( dataElementA );
        
        assertTrue( groupsA.size() == 2 );
        assertTrue( groupsA.contains( dataElementGroupA ) );
        assertTrue( groupsA.contains( dataElementGroupC ) );        

        Collection<DataElementGroup> groupsB = dataElementService.getGroupsContainingDataElement( dataElementC );
        
        assertTrue( groupsB.size() == 4 );
    }
}
