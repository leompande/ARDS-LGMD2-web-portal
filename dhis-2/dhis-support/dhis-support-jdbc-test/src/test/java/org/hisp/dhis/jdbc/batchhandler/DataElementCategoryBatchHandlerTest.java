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
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataElementCategoryBatchHandlerTest
    extends DhisTest
{
    @Autowired
    private BatchHandlerFactory batchHandlerFactory;
    
    private BatchHandler<DataElementCategory> batchHandler;
    
    private DataElementCategory categoryA;
    private DataElementCategory categoryB;
    private DataElementCategory categoryC;
    
    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
    {
        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );
        
        batchHandler = batchHandlerFactory.createBatchHandler( DataElementCategoryBatchHandler.class );
        
        batchHandler.init();
        
        categoryA = new DataElementCategory( "CategoryA" );
        categoryB = new DataElementCategory( "CategoryB" );
        categoryC = new DataElementCategory( "CategoryC" );
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

    @Ignore
    @Test
    public void testAddObject()
    {
        batchHandler.addObject( categoryA );
        batchHandler.addObject( categoryB );
        batchHandler.addObject( categoryC );
        
        batchHandler.flush();
        
        Collection<DataElementCategory> categories = categoryService.getAllDataElementCategories();
        
        assertTrue( categories.contains( categoryA ) );
        assertTrue( categories.contains( categoryB ) );
        assertTrue( categories.contains( categoryC ) );        
    }

    @Ignore
    @Test
    public void testInsertObject()
    {
        int idA = batchHandler.insertObject( categoryA, true );
        int idB = batchHandler.insertObject( categoryB, true );
        int idC = batchHandler.insertObject( categoryC, true );
        
        assertNotNull( categoryService.getDataElementCategory( idA ) );
        assertNotNull( categoryService.getDataElementCategory( idB ) );
        assertNotNull( categoryService.getDataElementCategory( idC ) );
    }

    @Ignore
    @Test
    public void testUpdateObject()
    {
        int id = batchHandler.insertObject( categoryA, true );
        
        categoryA.setId( id );
        categoryA.setName( "updatedName" );
        
        batchHandler.updateObject( categoryA );
        
        assertEquals( "updatedName", categoryService.getDataElementCategory( id ).getName() );
    }

    @Test
    public void testGetObjectIdentifier()
    {
        int referenceId = categoryService.addDataElementCategory( categoryA );
        
        int retrievedId = batchHandler.getObjectIdentifier( "CategoryA" );
        
        assertEquals( referenceId, retrievedId );
    }

    @Test
    public void testObjectExists()
    {
        categoryService.addDataElementCategory( categoryA );
        
        assertTrue( batchHandler.objectExists( categoryA ) );
        
        assertFalse( batchHandler.objectExists( categoryB ) );
    }
}
