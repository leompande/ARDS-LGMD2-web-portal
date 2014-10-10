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

package org.hisp.dhis.patientattributevalue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeOption;
import org.hisp.dhis.patient.PatientAttributeOptionService;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chau Thu Tran
 * 
 * @version $ PatientattributeValueStoreTest.java Nov 11, 2013 9:45:10 AM $
 */
public class PatientAttributeValueStoreTest
    extends DhisSpringTest
{

    @Autowired
    private PatientAttributeValueStore attributeValueStore;

    @Autowired
    private PatientService patientService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private PatientAttributeService attributeService;

    @Autowired
    private PatientAttributeOptionService attributeOptionService;

    private PatientAttribute attributeA;

    private PatientAttribute attributeB;

    private PatientAttribute attributeC;

    private PatientAttributeOption attributeOpionA;

    private PatientAttributeOption attributeOpionB;

    private Patient patientA;

    private Patient patientB;

    private Patient patientC;

    private Patient patientD;

    private PatientAttributeValue attributeValueA;

    private PatientAttributeValue attributeValueB;

    private PatientAttributeValue attributeValueC;

    private PatientAttributeValue attributeValueD;

    private PatientAttributeValue attributeValueE;

    @Override
    public void setUpTest()
    {
        OrganisationUnit organisationUnit = createOrganisationUnit( 'A' );
        organisationUnitService.addOrganisationUnit( organisationUnit );

        patientA = createPatient( 'A', organisationUnit );
        patientB = createPatient( 'B', organisationUnit );
        patientC = createPatient( 'C', organisationUnit );
        patientD = createPatient( 'D', organisationUnit );

        patientService.savePatient( patientA );
        patientService.savePatient( patientB );
        patientService.savePatient( patientC );
        patientService.savePatient( patientD );

        attributeA = createPatientAttribute( 'A' );
        attributeB = createPatientAttribute( 'B' );
        attributeC = createPatientAttribute( 'C' );

        attributeService.savePatientAttribute( attributeA );
        attributeService.savePatientAttribute( attributeB );
        attributeService.savePatientAttribute( attributeC );

        attributeOpionA = createPatientAttributeOption( 'A', attributeC );
        attributeOpionB = createPatientAttributeOption( 'B', attributeC );

        attributeOptionService.addPatientAttributeOption( attributeOpionA );
        attributeOptionService.addPatientAttributeOption( attributeOpionB );

        attributeValueA = new PatientAttributeValue( attributeA, patientA, "A" );
        attributeValueB = new PatientAttributeValue( attributeB, patientA, "B" );
        attributeValueC = new PatientAttributeValue( attributeA, patientB, "C" );
        attributeValueD = new PatientAttributeValue( attributeC, patientC, "AttributeOptionA" );
        attributeValueD.setPatientAttributeOption( attributeOpionA );
        attributeValueE = new PatientAttributeValue( attributeC, patientD, "AttributeOptionB" );
        attributeValueE.setPatientAttributeOption( attributeOpionB );
    }

    @Test
    public void testSavePatientAttributeValue()
    {
        attributeValueStore.saveVoid( attributeValueA );
        attributeValueStore.saveVoid( attributeValueB );

        assertNotNull( attributeValueStore.get( patientA, attributeA ) );
        assertNotNull( attributeValueStore.get( patientA, attributeA ) );
    }

    @Test
    public void testDeletePatientAttributeValueByPatient()
    {
        attributeValueStore.saveVoid( attributeValueA );
        attributeValueStore.saveVoid( attributeValueB );
        attributeValueStore.saveVoid( attributeValueC );

        assertNotNull( attributeValueStore.get( patientA, attributeA ) );
        assertNotNull( attributeValueStore.get( patientA, attributeB ) );
        assertNotNull( attributeValueStore.get( patientB, attributeA ) );

        attributeValueStore.deleteByPatient( patientA );

        assertNull( attributeValueStore.get( patientA, attributeA ) );
        assertNull( attributeValueStore.get( patientA, attributeB ) );
        assertNotNull( attributeValueStore.get( patientB, attributeA ) );

        attributeValueStore.deleteByPatient( patientB );
        assertNull( attributeValueStore.get( patientA, attributeA ) );
        assertNull( attributeValueStore.get( patientA, attributeB ) );
        assertNull( attributeValueStore.get( patientB, attributeA ) );
    }

    @Test
    public void testDeleteByAttribute()
    {
        attributeValueStore.saveVoid( attributeValueA );
        attributeValueStore.saveVoid( attributeValueB );
        attributeValueStore.saveVoid( attributeValueC );

        assertNotNull( attributeValueStore.get( patientA, attributeA ) );
        assertNotNull( attributeValueStore.get( patientA, attributeB ) );
        assertNotNull( attributeValueStore.get( patientB, attributeA ) );

        attributeValueStore.deleteByAttribute( attributeA );

        assertNull( attributeValueStore.get( patientA, attributeA ) );
        assertNull( attributeValueStore.get( patientB, attributeA ) );
        assertNotNull( attributeValueStore.get( patientA, attributeB ) );

        attributeValueStore.deleteByAttribute( attributeB );
        assertNull( attributeValueStore.get( patientA, attributeA ) );
        assertNull( attributeValueStore.get( patientA, attributeB ) );
        assertNull( attributeValueStore.get( patientB, attributeA ) );

    }

    @Test
    public void testGetPatientAttributeValue()
    {
        attributeValueStore.saveVoid( attributeValueA );
        attributeValueStore.saveVoid( attributeValueC );

        assertEquals( attributeValueA, attributeValueStore.get( patientA, attributeA ) );
        assertEquals( attributeValueC, attributeValueStore.get( patientB, attributeA ) );
    }

    @Test
    public void testGetByPatient()
    {
        attributeValueStore.saveVoid( attributeValueA );
        attributeValueStore.saveVoid( attributeValueB );
        attributeValueStore.saveVoid( attributeValueC );

        Collection<PatientAttributeValue> attributeValues = attributeValueStore.get( patientA );

        assertEquals( 2, attributeValues.size() );
        assertTrue( equals( attributeValues, attributeValueA, attributeValueB ) );

        attributeValues = attributeValueStore.get( patientB );

        assertEquals( 1, attributeValues.size() );
        assertTrue( equals( attributeValues, attributeValueC ) );
    }

    @Test
    public void testGetPatientAttributeValuesbyAttribute()
    {
        attributeValueStore.saveVoid( attributeValueA );
        attributeValueStore.saveVoid( attributeValueB );
        attributeValueStore.saveVoid( attributeValueC );

        Collection<PatientAttributeValue> attributeValues = attributeValueStore.get( attributeA );
        assertEquals( 2, attributeValues.size() );
        assertTrue( attributeValues.contains( attributeValueA ) );
        assertTrue( attributeValues.contains( attributeValueC ) );

        attributeValues = attributeValueStore.get( attributeB );
        assertEquals( 1, attributeValues.size() );
        assertTrue( attributeValues.contains( attributeValueB ) );
    }

    @Test
    public void testGetPatientAttributeValuesbyPatientList()
    {
        attributeValueStore.saveVoid( attributeValueA );
        attributeValueStore.saveVoid( attributeValueB );
        attributeValueStore.saveVoid( attributeValueC );

        Collection<Patient> patients = new HashSet<Patient>();
        patients.add( patientA );
        patients.add( patientB );

        Collection<PatientAttributeValue> attributeValues = attributeValueStore.get( patients );
        assertEquals( 3, attributeValues.size() );
        assertTrue( equals( attributeValues, attributeValueA, attributeValueB, attributeValueC ) );
    }

    @Test
    public void testSearchPatientAttributeValue()
    {
        attributeValueStore.saveVoid( attributeValueA );
        attributeValueStore.saveVoid( attributeValueB );
        attributeValueStore.saveVoid( attributeValueC );

        Collection<PatientAttributeValue> attributeValues = attributeValueStore.searchByValue( attributeA, "A" );
        assertTrue( equals( attributeValues, attributeValueA ) );
    }

    @Test
    public void testGetPatients()
    {
        attributeValueStore.saveVoid( attributeValueA );
        attributeValueStore.saveVoid( attributeValueB );
        attributeValueStore.saveVoid( attributeValueC );

        Collection<Patient> patients = attributeValueStore.getPatient( attributeA, "A" );
        assertEquals( 1, patients.size() );
        assertTrue( patients.contains( patientA ) );
    }

    @Test
    public void testCountByPatientAttributeoption()
    {
        attributeValueStore.saveVoid( attributeValueA );
        attributeValueStore.saveVoid( attributeValueD );
        attributeValueStore.saveVoid( attributeValueE );

        int count = attributeValueStore.countByPatientAttributeOption( attributeOpionA );
        assertEquals( 1, count );
    }

}
