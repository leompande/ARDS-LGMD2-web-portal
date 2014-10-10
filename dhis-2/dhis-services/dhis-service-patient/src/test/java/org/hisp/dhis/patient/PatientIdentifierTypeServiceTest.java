/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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

package org.hisp.dhis.patient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.hisp.dhis.DhisSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chau Thu Tran
 * 
 * @version $ PatientIdentifierTypeServiceTest.java Nov 5, 2013 3:11:48 PM $
 */
public class PatientIdentifierTypeServiceTest
    extends DhisSpringTest
{
    @Autowired
    private PatientIdentifierTypeService identifierTypeService;

    private PatientIdentifierType identifierTypeA;

    private PatientIdentifierType identifierTypeB;

    private PatientIdentifierType identifierTypeC;

    @Override
    public void setUpTest()
    {
        identifierTypeA = createPatientIdentifierType( 'A' );
        identifierTypeB = createPatientIdentifierType( 'B' );
        identifierTypeC = createPatientIdentifierType( 'C' );
    }

    @Test
    public void testSavePatientIdentifierType()
    {
        int idA = identifierTypeService.savePatientIdentifierType( identifierTypeA );
        int idB = identifierTypeService.savePatientIdentifierType( identifierTypeB );

        assertNotNull( identifierTypeService.getPatientIdentifierType( idA ) );
        assertNotNull( identifierTypeService.getPatientIdentifierType( idB ) );
    }

    @Test
    public void testDeletePatientIdentifierType()
    {
        int idA = identifierTypeService.savePatientIdentifierType( identifierTypeA );
        int idB = identifierTypeService.savePatientIdentifierType( identifierTypeB );

        assertNotNull( identifierTypeService.getPatientIdentifierType( idA ) );
        assertNotNull( identifierTypeService.getPatientIdentifierType( idB ) );

        identifierTypeService.deletePatientIdentifierType( identifierTypeA );

        assertNull( identifierTypeService.getPatientIdentifierType( idA ) );
        assertNotNull( identifierTypeService.getPatientIdentifierType( idB ) );

        identifierTypeService.deletePatientIdentifierType( identifierTypeB );

        assertNull( identifierTypeService.getPatientIdentifierType( idA ) );
        assertNull( identifierTypeService.getPatientIdentifierType( idB ) );
    }

    @Test
    public void testUpdatePatientIdentifierType()
    {
        int idA = identifierTypeService.savePatientIdentifierType( identifierTypeA );

        assertNotNull( identifierTypeService.getPatientIdentifierType( idA ) );

        identifierTypeA.setName( "B" );
        identifierTypeService.updatePatientIdentifierType( identifierTypeA );

        assertEquals( "B", identifierTypeService.getPatientIdentifierType( idA ).getName() );
    }

    @Test
    public void testGetPatientIdentifierTypeById()
    {
        int idA = identifierTypeService.savePatientIdentifierType( identifierTypeA );
        int idB = identifierTypeService.savePatientIdentifierType( identifierTypeB );

        assertEquals( identifierTypeA, identifierTypeService.getPatientIdentifierType( idA ) );
        assertEquals( identifierTypeB, identifierTypeService.getPatientIdentifierType( idB ) );
    }

    @Test
    public void testGetPatientIdentifierTypeByName()
    {
        int idA = identifierTypeService.savePatientIdentifierType( identifierTypeA );

        assertNotNull( identifierTypeService.getPatientIdentifierType( idA ) );
        assertEquals( identifierTypeA.getName(), identifierTypeService.getPatientIdentifierType( "IdentifierTypeA" )
            .getName() );
    }

    @Test
    public void testGetAllPatientIdentifierTypes()
    {
        identifierTypeService.savePatientIdentifierType( identifierTypeA );
        identifierTypeService.savePatientIdentifierType( identifierTypeB );

        assertTrue( equals( identifierTypeService.getAllPatientIdentifierTypes(), identifierTypeA, identifierTypeB ) );
    }

    @Test
    public void testGetPatientIdentifierTypeByUid()
    {
        identifierTypeA.setUid( "A" );
        identifierTypeB.setUid( "B" );

        identifierTypeService.savePatientIdentifierType( identifierTypeA );
        identifierTypeService.savePatientIdentifierType( identifierTypeB );

        assertEquals( identifierTypeA, identifierTypeService.getPatientIdentifierTypeByUid( "A" ) );
        assertEquals( identifierTypeB, identifierTypeService.getPatientIdentifierTypeByUid( "B" ) );
    }

    @Test
    public void testGetPatientIdentifierTypesByMandatory()
    {
        identifierTypeA.setMandatory( true );
        identifierTypeB.setMandatory( true );

        identifierTypeService.savePatientIdentifierType( identifierTypeA );
        identifierTypeService.savePatientIdentifierType( identifierTypeB );
        identifierTypeService.savePatientIdentifierType( identifierTypeC );

        Collection<PatientIdentifierType> identifierTypes = identifierTypeService.getPatientIdentifierTypes( true );
        assertEquals( 2, identifierTypes.size() );
        assertTrue( equals( identifierTypes, identifierTypeA, identifierTypeB ) );

        identifierTypes = identifierTypeService.getPatientIdentifierTypes( false );
        assertEquals( 1, identifierTypes.size() );
        assertTrue( equals( identifierTypes, identifierTypeC ) );
    }

}
