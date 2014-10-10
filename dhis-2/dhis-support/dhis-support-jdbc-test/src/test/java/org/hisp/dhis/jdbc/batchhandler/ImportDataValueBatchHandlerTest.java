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

import static org.junit.Assert.*;

import java.util.Collection;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.hisp.dhis.DhisTest;
import org.hisp.dhis.importexport.ImportDataValue;
import org.hisp.dhis.importexport.ImportDataValueService;
import org.hisp.dhis.importexport.ImportObjectStatus;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 * @version $Id: GroupSetBatchHandlerTest.java 4949 2008-04-21 07:59:54Z larshelg $
 */
public class ImportDataValueBatchHandlerTest
    extends DhisTest
{
    @Autowired
    private BatchHandlerFactory batchHandlerFactory;
    
    private ImportDataValueService importDataValueService;

    private BatchHandler<ImportDataValue> batchHandler;
    
    private ImportObjectStatus status;
    
    private ImportDataValue valueA;
    private ImportDataValue valueB;
    private ImportDataValue valueC;
    
    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
    {
        importDataValueService = (ImportDataValueService) getBean( ImportDataValueService.ID );

        batchHandler = batchHandlerFactory.createBatchHandler( ImportDataValueBatchHandler.class );

        batchHandler.init();
        
        status = ImportObjectStatus.NEW;
        
        valueA = createImportDataValue( 1, 1, 1, 1, status );
        valueB = createImportDataValue( 2, 2, 2, 2, status );
        valueC = createImportDataValue( 3, 3, 3, 3, status );
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
        batchHandler.addObject( valueA );
        batchHandler.addObject( valueB );
        batchHandler.addObject( valueC );
        
        batchHandler.flush();
        
        Collection<ImportDataValue> values = importDataValueService.getImportDataValues( status );
        
        assertTrue( values.contains( valueA ) );
        assertTrue( values.contains( valueB ) );
        assertTrue( values.contains( valueC ) );
    }
    
    @Test
    public void testInsertObject()
    {
        batchHandler.insertObject( valueA, false );
        batchHandler.insertObject( valueB, false );
        batchHandler.insertObject( valueC, false );
        
        Collection<ImportDataValue> values = importDataValueService.getImportDataValues( status );
        
        assertTrue( values.contains( valueA ) );
        assertTrue( values.contains( valueB ) );
        assertTrue( values.contains( valueC ) );        
    }
    
    @Test
    public void testUpdateObject()
    {
        batchHandler.insertObject( valueA, false );
        
        valueA.setValue( String.valueOf( 20 ) );
        
        batchHandler.updateObject( valueA );
        
        assertEquals( String.valueOf( 20 ), importDataValueService.getImportDataValues( status ).iterator().next().getValue() );
    }

    @Test
    public void testObjectExists()
    {
        importDataValueService.addImportDataValue( valueA );
        
        assertTrue( batchHandler.objectExists( valueA ) );
        
        assertFalse( batchHandler.objectExists( valueB ) );
    }    
}
