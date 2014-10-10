package org.hisp.dhis.datadictionary;

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

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
@SuppressWarnings( "unchecked" )
public class DataDictionaryStoreTest
    extends DhisSpringTest
{
    private GenericIdentifiableObjectStore<DataDictionary> dataDictionaryStore;

    private DataElement dataElementA;
    private DataElement dataElementB;
    
    private IndicatorType indicatorType;
    
    private Indicator indicatorA;
    private Indicator indicatorB;
    
    private DataDictionary dataDictionaryA;
    private DataDictionary dataDictionaryB;
        
    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
        throws Exception
    {
        dataDictionaryStore = (GenericIdentifiableObjectStore<DataDictionary>) getBean( "org.hisp.dhis.datadictionary.DataDictionaryStore" );
        
        dataElementService = (DataElementService) getBean( DataElementService.ID );
        
        indicatorService = (IndicatorService) getBean( IndicatorService.ID );
        
        dataDictionaryA = createDataDictionary( 'A' );
        dataDictionaryB = createDataDictionary( 'B' );
        
        dataElementA = createDataElement( 'A' );
        dataElementB = createDataElement( 'B' );
        
        indicatorType = createIndicatorType( 'A' );
        
        indicatorService.addIndicatorType( indicatorType );
        
        indicatorA = createIndicator( 'A', indicatorType );
        indicatorB = createIndicator( 'B', indicatorType );
    }

    // -------------------------------------------------------------------------
    // DataDictionary tests
    // -------------------------------------------------------------------------
    
    @Test
    public void testSaveGetDataDictionary()
    {
        dataElementService.addDataElement( dataElementA );
        dataElementService.addDataElement( dataElementB );
        
        indicatorService.addIndicator( indicatorA );
        indicatorService.addIndicator( indicatorB );
                
        dataDictionaryA.getDataElements().add( dataElementA );
        dataDictionaryA.getDataElements().add( dataElementB );
        
        dataDictionaryA.getIndicators().add( indicatorA );
        dataDictionaryA.getIndicators().add( indicatorB );
                
        int id = dataDictionaryStore.save( dataDictionaryA );
        
        dataDictionaryA = dataDictionaryStore.get( id );
        
        assertEquals( dataDictionaryA.getName(), "DataDictionaryA" );
        assertEquals( dataDictionaryA.getDescription(), "DescriptionA" );
        assertEquals( dataDictionaryA.getRegion(), "RegionA" );
        assertEquals( dataDictionaryA.getDataElements().size(), 2 );
        
        assertTrue( dataDictionaryA.getDataElements().contains( dataElementA ) );
        assertTrue( dataDictionaryA.getDataElements().contains( dataElementB ) );
        
        assertTrue( dataDictionaryA.getIndicators().contains( indicatorA ) );
        assertTrue( dataDictionaryA.getIndicators().contains( indicatorB ) );
                
        dataDictionaryA.setName( "DataDictionaryB" );
        dataDictionaryA.setDescription( "DescriptionB" );
        dataDictionaryA.setRegion( "RegionB" );
        
        dataDictionaryStore.save( dataDictionaryA );
        
        dataDictionaryA = dataDictionaryStore.get( id );
        
        assertEquals( dataDictionaryA.getName(), "DataDictionaryB" );
        assertEquals( dataDictionaryA.getDescription(), "DescriptionB" );
        assertEquals( dataDictionaryA.getRegion(), "RegionB" );
    }

    @Test
    public void testGetDataDictionaryByName()
    {
        dataDictionaryStore.save( dataDictionaryA );
        dataDictionaryStore.save( dataDictionaryB );
        
        dataDictionaryA = dataDictionaryStore.getByName( "DataDictionaryA" );
        
        assertNotNull( dataDictionaryA );
        assertEquals( dataDictionaryA.getName(), "DataDictionaryA" );
        assertEquals( dataDictionaryA.getDescription(), "DescriptionA" );
    }

    @Test
    public void testDeleteDataDictionary()
    {
        int idA = dataDictionaryStore.save( dataDictionaryA );
        int idB = dataDictionaryStore.save( dataDictionaryB );
        
        assertNotNull( dataDictionaryStore.get( idA ) );
        assertNotNull( dataDictionaryStore.get( idB ) );
        
        dataDictionaryStore.delete( dataDictionaryA );

        assertNull( dataDictionaryStore.get( idA ) );
        assertNotNull( dataDictionaryStore.get( idB ) );

        dataDictionaryStore.delete( dataDictionaryB );

        assertNull( dataDictionaryStore.get( idA ) );
        assertNull( dataDictionaryStore.get( idB ) );
    }

    @Test
    public void testGetAllDataDictionaries()
    {
        dataDictionaryStore.save( dataDictionaryA );
        dataDictionaryStore.save( dataDictionaryB );
        
        Collection<DataDictionary> dictionaries = dataDictionaryStore.getAll();
        
        assertTrue( dictionaries.size() == 2 );
        assertTrue( dictionaries.contains( dataDictionaryA ) );
        assertTrue( dictionaries.contains( dataDictionaryB ) );        
    }
}
