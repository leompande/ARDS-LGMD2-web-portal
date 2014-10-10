package org.hisp.dhis.jdbc.batchhandler;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.hisp.dhis.DhisTest;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataElementGroupSetBatchHandlerTest
    extends DhisTest
{
    @Autowired
    private BatchHandlerFactory batchHandlerFactory;
    
    private DataElementService dataElementService;
    
    private BatchHandler<DataElementGroupSet> batchHandler;
    
    private DataElementGroupSet groupSetA;
    private DataElementGroupSet groupSetB;
    private DataElementGroupSet groupSetC;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
    {
        dataElementService = (DataElementService) getBean( DataElementService.ID );
        
        batchHandler = batchHandlerFactory.createBatchHandler( DataElementGroupSetBatchHandler.class );

        batchHandler.init();
        
        groupSetA = createDataElementGroupSet( 'A' );
        groupSetB = createDataElementGroupSet( 'B' );
        groupSetC = createDataElementGroupSet( 'C' );
    }

    @Override
    public void tearDownTest()
    {
        batchHandler.flush();
    }
    
    @Override
    public boolean emptyDatabaseAfterTest()
    {
        return true;
    }
    
    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void testAddObject()
    {
        batchHandler.addObject( groupSetA );
        batchHandler.addObject( groupSetB );
        batchHandler.addObject( groupSetC );

        batchHandler.flush();
        
        Collection<DataElementGroupSet> groupsSets = dataElementService.getAllDataElementGroupSets();
        
        assertTrue( groupsSets.contains( groupSetA ) );
        assertTrue( groupsSets.contains( groupSetB ) );
        assertTrue( groupsSets.contains( groupSetC ) );
    }

    @Test
    public void testInsertObject()
    {
        int idA = batchHandler.insertObject( groupSetA, true );
        int idB = batchHandler.insertObject( groupSetB, true );
        int idC = batchHandler.insertObject( groupSetC, true );
        
        assertNotNull( dataElementService.getDataElementGroupSet( idA ) );
        assertNotNull( dataElementService.getDataElementGroupSet( idB ) );
        assertNotNull( dataElementService.getDataElementGroupSet( idC ) );
    }

    @Test
    public void testUpdateObject()
    {
        int id = batchHandler.insertObject( groupSetA, true );
        
        groupSetA.setId( id );
        groupSetA.setName( "UpdatedName" );
        
        batchHandler.updateObject( groupSetA );
        
        assertEquals( "UpdatedName", dataElementService.getDataElementGroupSet( id ).getName() );
    }

    @Test
    public void testGetObjectIdentifier()
    {
        int referenceId = dataElementService.addDataElementGroupSet( groupSetA );

        int retrievedId = batchHandler.getObjectIdentifier( "DataElementGroupSetA" );
        
        assertEquals( referenceId, retrievedId );
    }

    @Test
    public void testObjectExists()
    {
        dataElementService.addDataElementGroupSet( groupSetA );
        
        assertTrue( batchHandler.objectExists( groupSetA ) );
        
        assertFalse( batchHandler.objectExists( groupSetB ) );
    }
}
