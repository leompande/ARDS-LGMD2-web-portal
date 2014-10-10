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
import org.hisp.dhis.datadictionary.DataDictionary;
import org.hisp.dhis.datadictionary.DataDictionaryService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataDictionaryBatchHandlerTest
    extends DhisTest
{
    @Autowired
    private BatchHandlerFactory batchHandlerFactory;
    
    private BatchHandler<DataDictionary> batchHandler;
    
    private DataDictionary dataDictionaryA;
    private DataDictionary dataDictionaryB;
    private DataDictionary dataDictionaryC;    

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
    {
        dataDictionaryService = (DataDictionaryService) getBean( DataDictionaryService.ID );
        
        batchHandler = batchHandlerFactory.createBatchHandler( DataDictionaryBatchHandler.class );

        batchHandler.init();
        
        dataDictionaryA = createDataDictionary( 'A' );
        dataDictionaryB = createDataDictionary( 'B' );
        dataDictionaryC = createDataDictionary( 'C' );
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
        batchHandler.addObject( dataDictionaryA );
        batchHandler.addObject( dataDictionaryB );
        batchHandler.addObject( dataDictionaryC );
        
        batchHandler.flush();
        
        Collection<DataDictionary> dataDictionaries = dataDictionaryService.getAllDataDictionaries();
        
        assertTrue( dataDictionaries.contains( dataDictionaryA ) );
        assertTrue( dataDictionaries.contains( dataDictionaryB ) );
        assertTrue( dataDictionaries.contains( dataDictionaryC ) );
    }

    @Test
    public void testInsertObject()
    {
        int idA = batchHandler.insertObject( dataDictionaryA, true );
        int idB = batchHandler.insertObject( dataDictionaryB, true );
        int idC = batchHandler.insertObject( dataDictionaryC, true );
        
        assertNotNull( dataDictionaryService.getDataDictionary( idA ) );
        assertNotNull( dataDictionaryService.getDataDictionary( idB ) );
        assertNotNull( dataDictionaryService.getDataDictionary( idC ) );
    }

    @Test
    public void testUpdateObject()
    {
        int id = batchHandler.insertObject( dataDictionaryA, true );
        
        dataDictionaryA.setId( id );
        dataDictionaryA.setName( "UpdatedName" );
        
        batchHandler.updateObject( dataDictionaryA );
        
        assertEquals( "UpdatedName", dataDictionaryService.getDataDictionary( id ).getName() );
    }

    @Test
    public void testGetObjectIdentifier()
    {
        int referenceId = dataDictionaryService.saveDataDictionary( dataDictionaryA );
        
        int retrievedId = batchHandler.getObjectIdentifier( "DataDictionaryA" );
        
        assertEquals( referenceId, retrievedId );
    }

    @Test
    public void testObjectExists()
    {
        dataDictionaryService.saveDataDictionary( dataDictionaryA );
        
        assertTrue( batchHandler.objectExists( dataDictionaryA ) );
        
        assertFalse( batchHandler.objectExists( dataDictionaryB ) );
    }
}
