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
import org.hisp.dhis.cache.HibernateCacheManager;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 * @version $Id: IndicatorBatchHandlerTest.java 4949 2008-04-21 07:59:54Z larshelg $
 */
public class IndicatorBatchHandlerTest
    extends DhisTest
{
    @Autowired
    private BatchHandlerFactory batchHandlerFactory;
    
    @Autowired
    private HibernateCacheManager cacheManager;

    private BatchHandler<Indicator> batchHandler;
    
    private Indicator indicatorA;
    private Indicator indicatorB;
    private Indicator indicatorC;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
    {
        indicatorService = (IndicatorService) getBean( IndicatorService.ID );
        
        batchHandler = batchHandlerFactory.createBatchHandler( IndicatorBatchHandler.class );

        batchHandler.init();
        
        IndicatorType indicatorType = createIndicatorType( 'A' );
        
        indicatorService.addIndicatorType( indicatorType );
        
        indicatorA = createIndicator( 'A', indicatorType );
        indicatorB = createIndicator( 'B', indicatorType );
        indicatorC = createIndicator( 'C', indicatorType );
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
        batchHandler.addObject( indicatorA );
        batchHandler.addObject( indicatorB );
        batchHandler.addObject( indicatorC );
        
        batchHandler.flush();
        
        cacheManager.clearCache();
        
        Collection<Indicator> indicators = indicatorService.getAllIndicators();
        
        assertTrue( indicators.contains( indicatorA  ) );
        assertTrue( indicators.contains( indicatorB  ) );
        assertTrue( indicators.contains( indicatorC  ) );
    }

    @Test
    public void testInsertObject()
    {
        int idA = batchHandler.insertObject( indicatorA, true );
        int idB = batchHandler.insertObject( indicatorB, true );
        int idC = batchHandler.insertObject( indicatorC, true );

        cacheManager.clearCache();
        
        assertNotNull( indicatorService.getIndicator( idA ) );
        assertNotNull( indicatorService.getIndicator( idB ) );
        assertNotNull( indicatorService.getIndicator( idC ) );
    }

    @Test
    public void testUpdateObject()
    {
        int id = batchHandler.insertObject( indicatorA, true );
        
        indicatorA.setId( id );
        indicatorA.setName( "UpdatedName" );
        
        batchHandler.updateObject( indicatorA );

        cacheManager.clearCache();
        
        assertEquals( "UpdatedName", indicatorService.getIndicator( id ).getName() );
    }

    @Test
    public void testGetObjectIdentifier()
    {
        int referenceId = indicatorService.addIndicator( indicatorA );
        
        int retrievedId = batchHandler.getObjectIdentifier( "IndicatorA" );
        
        assertEquals( referenceId, retrievedId );
    }

    @Test
    public void testObjectExists()
    {
        indicatorService.addIndicator( indicatorA );
        
        assertTrue( batchHandler.objectExists( indicatorA ) );
        
        assertFalse( batchHandler.objectExists( indicatorB ) );
    }
}
