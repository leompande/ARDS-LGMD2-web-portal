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
import java.util.HashSet;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chau Thu Tran
 * 
 * @version $ PatientIdentifierServiceTest.java Nov 6, 2013 8:52:24 AM $
 */
public class PatientIdentifierServiceTest
    extends DhisSpringTest
{
    @Autowired
    private PatientIdentifierService identifierService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientIdentifierTypeService identifierTypeService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    private PatientIdentifier patientIdentifierA;

    private PatientIdentifier patientIdentifierB;

    private PatientIdentifier patientIdentifierC;

    private PatientIdentifier patientIdentifierD;

    private PatientIdentifierType identifierTypeA;

    private PatientIdentifierType identifierTypeB;

    private Patient patientA;
    
    private Patient patientB;
    
    @Override
    public void setUpTest()
    {
        OrganisationUnit organisationUnit = createOrganisationUnit( 'A' );
        organisationUnitService.addOrganisationUnit( organisationUnit );

        identifierTypeA = createPatientIdentifierType( 'A' );
        identifierTypeService.savePatientIdentifierType( identifierTypeA );

        identifierTypeB = createPatientIdentifierType( 'B' );
        identifierTypeService.savePatientIdentifierType( identifierTypeB );

        patientA = createPatient( 'A', organisationUnit );
        patientService.savePatient( patientA );

        patientB = createPatient( 'B', organisationUnit );
        patientService.savePatient( patientB );

        patientIdentifierA = new PatientIdentifier( identifierTypeA, patientA, "A" );
        patientIdentifierB = new PatientIdentifier( identifierTypeB, patientA, "B" );
        patientIdentifierC = new PatientIdentifier( identifierTypeA, patientB, "C" );
        patientIdentifierD = new PatientIdentifier( identifierTypeB, patientB, "A" );
    }

    @Test
    public void testSavePatientIdentifier()
    {
        int idA = identifierService.savePatientIdentifier( patientIdentifierA );
        int idB = identifierService.savePatientIdentifier( patientIdentifierB );

        assertNotNull( identifierService.getPatientIdentifier( idA ) );
        assertNotNull( identifierService.getPatientIdentifier( idB ) );
    }

    @Test
    public void testDeletePatientIdentifier()
    {
        int idA = identifierService.savePatientIdentifier( patientIdentifierA );
        int idB = identifierService.savePatientIdentifier( patientIdentifierB );

        assertNotNull( identifierService.getPatientIdentifier( idA ) );
        assertNotNull( identifierService.getPatientIdentifier( idB ) );

        identifierService.deletePatientIdentifier( patientIdentifierA );

        assertNull( identifierService.getPatientIdentifier( idA ) );
        assertNotNull( identifierService.getPatientIdentifier( idB ) );

        identifierService.deletePatientIdentifier( patientIdentifierB );

        assertNull( identifierService.getPatientIdentifier( idA ) );
        assertNull( identifierService.getPatientIdentifier( idB ) );
    }

    @Test
    public void testUpdatePatientIdentifier()
    {
        int idA = identifierService.savePatientIdentifier( patientIdentifierA );

        assertNotNull( identifierService.getPatientIdentifier( idA ) );

        patientIdentifierA.setName( "B" );
        identifierService.updatePatientIdentifier( patientIdentifierA );

        assertEquals( "B", identifierService.getPatientIdentifier( idA ).getName() );
    }

    @Test
    public void testGetPatientIdentifierById()
    {
        int idA = identifierService.savePatientIdentifier( patientIdentifierA );
        int idB = identifierService.savePatientIdentifier( patientIdentifierB );

        assertEquals( patientIdentifierA, identifierService.getPatientIdentifier( idA ) );
        assertEquals( patientIdentifierB, identifierService.getPatientIdentifier( idB ) );
    }

    @Test
    public void testGetAllPatientIdentifiers()
    {
        identifierService.savePatientIdentifier( patientIdentifierA );
        identifierService.savePatientIdentifier( patientIdentifierB );

        assertTrue( equals( identifierService.getAllPatientIdentifiers(), patientIdentifierA, patientIdentifierB ) );
    }

    @Test
    public void testGetPatientIdentifiersByType()
    {
        identifierService.savePatientIdentifier( patientIdentifierA );
        identifierService.savePatientIdentifier( patientIdentifierB );

        Collection<PatientIdentifier> identifiers = identifierService.getPatientIdentifiersByType( identifierTypeA );

        assertEquals( 1, identifiers.size() );
        assertTrue( identifiers.contains( patientIdentifierA ) );
    }

    @Test
    public void testGetPatientIdentifiersByIdentifier()
    {
        identifierService.savePatientIdentifier( patientIdentifierA );
        identifierService.savePatientIdentifier( patientIdentifierB );

        Collection<PatientIdentifier> identifiers = identifierService.getPatientIdentifiersByIdentifier( "A" );

        assertEquals( 1, identifiers.size() );
        assertTrue( identifiers.contains( patientIdentifierA ) );
    }

    @Test
    public void testGetPatientIdentifierByPatient()
    {
        identifierService.savePatientIdentifier( patientIdentifierA );
        identifierService.savePatientIdentifier( patientIdentifierB );

        PatientIdentifier identifiers = identifierService.getPatientIdentifier( "A", patientA );
        assertEquals( patientIdentifierA, identifiers );
    }

    @Test
    public void testGetPatientIdentifierByPatientIden()
    {
        identifierService.savePatientIdentifier( patientIdentifierA );
        identifierService.savePatientIdentifier( patientIdentifierD );

        PatientIdentifier identifiers = identifierService.getPatientIdentifier( identifierTypeA, patientA );
        assertEquals( patientIdentifierA, identifiers );
    }

    @Test
    public void testGetByType()
    {
        identifierService.savePatientIdentifier( patientIdentifierA );
        identifierService.savePatientIdentifier( patientIdentifierB );
        identifierService.savePatientIdentifier( patientIdentifierC );

        Collection<PatientIdentifier> identifiers = identifierService.getAll( identifierTypeA, "A" );
        assertEquals( 1, identifiers.size() );
        assertTrue( identifiers.contains( patientIdentifierA ) );
    }

    @Test
    public void testGetPatientsByIdentifier()
    {
        identifierService.savePatientIdentifier( patientIdentifierA );
        identifierService.savePatientIdentifier( patientIdentifierB );
        identifierService.savePatientIdentifier( patientIdentifierD );

        Collection<Patient> patients = identifierService.getPatientsByIdentifier( "a", null, null );
        assertEquals( 2, patients.size() );
        assertTrue( patients.contains( patientA ) );
        assertTrue( patients.contains( patientB ) );
    }

    @Test
    public void testCountGetPatientsByIdentifier()
    {
        identifierService.savePatientIdentifier( patientIdentifierA );
        identifierService.savePatientIdentifier( patientIdentifierB );
        identifierService.savePatientIdentifier( patientIdentifierD );

        int count = identifierService.countGetPatientsByIdentifier( "A" );
        assertEquals( 2, count );
    }

    @Test
    public void testGetPatientIdentifiers()
    {
        identifierService.savePatientIdentifier( patientIdentifierA );
        identifierService.savePatientIdentifier( patientIdentifierB );
        identifierService.savePatientIdentifier( patientIdentifierC );
        identifierService.savePatientIdentifier( patientIdentifierD );

        Collection<PatientIdentifierType> identifierTypes = new HashSet<PatientIdentifierType>();
        identifierTypes.add( identifierTypeA );
        identifierTypes.add( identifierTypeB );

        Collection<PatientIdentifier> identifiers = identifierService.getPatientIdentifiers( identifierTypes, patientA );
        assertEquals( 2, identifiers.size() );
        assertTrue( identifiers.contains( patientIdentifierA ) );
        assertTrue( identifiers.contains( patientIdentifierB ) );
    }

}
