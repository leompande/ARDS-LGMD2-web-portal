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
import java.util.Map;

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
 * @version $ PatientAttributeValueServiceTest.java Nov 11, 2013 9:45:10 AM $
 */
public class PatientAttributeValueServiceTest
    extends DhisSpringTest
{

    @Autowired
    private PatientAttributeValueService attributeValueService;

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

    private int patientAId;

    private int patientBId;

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

        patientAId = patientService.savePatient( patientA );
        patientBId = patientService.savePatient( patientB );
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

        attributeValueService.savePatientAttributeValue( attributeValueA );
        attributeValueService.savePatientAttributeValue( attributeValueB );

        assertNotNull( attributeValueService.getPatientAttributeValue( patientA, attributeA ) );
        assertNotNull( attributeValueService.getPatientAttributeValue( patientA, attributeA ) );
    }

    @Test
    public void testUpdatePatientAttributeValue()
    {
        attributeValueService.savePatientAttributeValue( attributeValueA );

        assertNotNull( attributeValueService.getPatientAttributeValue( patientA, attributeA ) );

        attributeValueA.setValue( "B" );
        attributeValueService.updatePatientAttributeValue( attributeValueA );

        assertEquals( "B", attributeValueService.getPatientAttributeValue( patientA, attributeA ).getValue() );
    }

    @Test
    public void testDeletePatientAttributeValue()
    {
        attributeValueService.savePatientAttributeValue( attributeValueA );
        attributeValueService.savePatientAttributeValue( attributeValueB );

        assertNotNull( attributeValueService.getPatientAttributeValue( patientA, attributeA ) );
        assertNotNull( attributeValueService.getPatientAttributeValue( patientA, attributeB ) );

        attributeValueService.deletePatientAttributeValue( attributeValueA );

        assertNull( attributeValueService.getPatientAttributeValue( patientA, attributeA ) );
        assertNotNull( attributeValueService.getPatientAttributeValue( patientA, attributeB ) );

        attributeValueService.deletePatientAttributeValue( attributeValueB );

        assertNull( attributeValueService.getPatientAttributeValue( patientA, attributeA ) );
        assertNull( attributeValueService.getPatientAttributeValue( patientA, attributeB ) );
    }

    @Test
    public void testDeletePatientAttributeValueByPatient()
    {
        attributeValueService.savePatientAttributeValue( attributeValueA );
        attributeValueService.savePatientAttributeValue( attributeValueB );
        attributeValueService.savePatientAttributeValue( attributeValueC );

        assertNotNull( attributeValueService.getPatientAttributeValue( patientA, attributeA ) );
        assertNotNull( attributeValueService.getPatientAttributeValue( patientA, attributeB ) );
        assertNotNull( attributeValueService.getPatientAttributeValue( patientB, attributeA ) );

        attributeValueService.deletePatientAttributeValue( patientA );

        assertNull( attributeValueService.getPatientAttributeValue( patientA, attributeA ) );
        assertNull( attributeValueService.getPatientAttributeValue( patientA, attributeB ) );
        assertNotNull( attributeValueService.getPatientAttributeValue( patientB, attributeA ) );

        attributeValueService.deletePatientAttributeValue( patientB );
        assertNull( attributeValueService.getPatientAttributeValue( patientA, attributeA ) );
        assertNull( attributeValueService.getPatientAttributeValue( patientA, attributeB ) );
        assertNull( attributeValueService.getPatientAttributeValue( patientB, attributeA ) );
    }

    @Test
    public void testDeletePatientAttributeValueByAttribute()
    {
        attributeValueService.savePatientAttributeValue( attributeValueA );
        attributeValueService.savePatientAttributeValue( attributeValueB );
        attributeValueService.savePatientAttributeValue( attributeValueC );

        assertNotNull( attributeValueService.getPatientAttributeValue( patientA, attributeA ) );
        assertNotNull( attributeValueService.getPatientAttributeValue( patientA, attributeB ) );
        assertNotNull( attributeValueService.getPatientAttributeValue( patientB, attributeA ) );

        attributeValueService.deletePatientAttributeValue( attributeA );

        assertNull( attributeValueService.getPatientAttributeValue( patientA, attributeA ) );
        assertNull( attributeValueService.getPatientAttributeValue( patientB, attributeA ) );
        assertNotNull( attributeValueService.getPatientAttributeValue( patientA, attributeB ) );

        attributeValueService.deletePatientAttributeValue( attributeB );
        assertNull( attributeValueService.getPatientAttributeValue( patientA, attributeA ) );
        assertNull( attributeValueService.getPatientAttributeValue( patientA, attributeB ) );
        assertNull( attributeValueService.getPatientAttributeValue( patientB, attributeA ) );

    }

    @Test
    public void testGetPatientAttributeValue()
    {
        attributeValueService.savePatientAttributeValue( attributeValueA );
        attributeValueService.savePatientAttributeValue( attributeValueC );

        assertEquals( attributeValueA, attributeValueService.getPatientAttributeValue( patientA, attributeA ) );
        assertEquals( attributeValueC, attributeValueService.getPatientAttributeValue( patientB, attributeA ) );
    }

    @Test
    public void testGetPatientAttributeValuesByPatient()
    {
        attributeValueService.savePatientAttributeValue( attributeValueA );
        attributeValueService.savePatientAttributeValue( attributeValueB );
        attributeValueService.savePatientAttributeValue( attributeValueC );

        Collection<PatientAttributeValue> attributeValues = attributeValueService.getPatientAttributeValues( patientA );

        assertEquals( 2, attributeValues.size() );
        assertTrue( equals( attributeValues, attributeValueA, attributeValueB ) );

        attributeValues = attributeValueService.getPatientAttributeValues( patientB );

        assertEquals( 1, attributeValues.size() );
        assertTrue( equals( attributeValues, attributeValueC ) );
    }

    @Test
    public void testGetPatientAttributeValuesbyAttribute()
    {
        attributeValueService.savePatientAttributeValue( attributeValueA );
        attributeValueService.savePatientAttributeValue( attributeValueB );
        attributeValueService.savePatientAttributeValue( attributeValueC );

        Collection<PatientAttributeValue> attributeValues = attributeValueService
            .getPatientAttributeValues( attributeA );
        assertEquals( 2, attributeValues.size() );
        assertTrue( attributeValues.contains( attributeValueA ) );
        assertTrue( attributeValues.contains( attributeValueC ) );

        attributeValues = attributeValueService.getPatientAttributeValues( attributeB );
        assertEquals( 1, attributeValues.size() );
        assertTrue( attributeValues.contains( attributeValueB ) );
    }

    @Test
    public void testGetPatientAttributeValuesbyPatientList()
    {
        attributeValueService.savePatientAttributeValue( attributeValueA );
        attributeValueService.savePatientAttributeValue( attributeValueB );
        attributeValueService.savePatientAttributeValue( attributeValueC );

        Collection<Patient> patients = new HashSet<Patient>();
        patients.add( patientA );
        patients.add( patientB );

        Collection<PatientAttributeValue> attributeValues = attributeValueService.getPatientAttributeValues( patients );
        assertEquals( 3, attributeValues.size() );
        assertTrue( equals( attributeValues, attributeValueA, attributeValueB, attributeValueC ) );
    }

    @Test
    public void testGetAllPatientAttributeValues()
    {
        attributeValueService.savePatientAttributeValue( attributeValueA );
        attributeValueService.savePatientAttributeValue( attributeValueB );
        attributeValueService.savePatientAttributeValue( attributeValueC );

        assertTrue( equals( attributeValueService.getAllPatientAttributeValues(), attributeValueA, attributeValueB,
            attributeValueC ) );
    }

    @Test
    public void testGetPatientAttributeValueMapForPatients()
    {
        attributeValueService.savePatientAttributeValue( attributeValueA );
        attributeValueService.savePatientAttributeValue( attributeValueB );
        attributeValueService.savePatientAttributeValue( attributeValueC );

        Collection<Patient> patients = new HashSet<Patient>();
        patients.add( patientA );
        patients.add( patientB );

        Map<Integer, Collection<PatientAttributeValue>> attributeValueMap = attributeValueService
            .getPatientAttributeValueMapForPatients( patients );

        assertEquals( 2, attributeValueMap.keySet().size() );
        assertTrue( equals( attributeValueMap.get( patientAId ), attributeValueA, attributeValueB ) );
        assertTrue( equals( attributeValueMap.get( patientBId ), attributeValueC ) );
    }

    @Test
    public void testGetPatientAttributeValueMapForPatientsAttributes()
    {
        attributeValueService.savePatientAttributeValue( attributeValueA );
        attributeValueService.savePatientAttributeValue( attributeValueB );
        attributeValueService.savePatientAttributeValue( attributeValueC );

        Collection<Patient> patients = new HashSet<Patient>();
        patients.add( patientA );
        patients.add( patientB );

        Map<Integer, PatientAttributeValue> attributeValueMap = attributeValueService
            .getPatientAttributeValueMapForPatients( patients, attributeA );

        assertEquals( 2, attributeValueMap.keySet().size() );
        assertEquals( attributeValueA, attributeValueMap.get( patientAId ) );
        assertEquals( attributeValueC, attributeValueMap.get( patientBId ) );
    }

    @Test
    public void testSearchPatientAttributeValue()
    {
        attributeValueService.savePatientAttributeValue( attributeValueA );
        attributeValueService.savePatientAttributeValue( attributeValueB );
        attributeValueService.savePatientAttributeValue( attributeValueC );

        Collection<PatientAttributeValue> attributeValues = attributeValueService.searchPatientAttributeValue(
            attributeA, "A" );
        assertTrue( equals( attributeValues, attributeValueA ) );
    }

    @Test
    public void testCopyPatientAttributeValues()
    {
        attributeValueService.savePatientAttributeValue( attributeValueB );
        attributeValueService.savePatientAttributeValue( attributeValueC );

        attributeValueService.copyPatientAttributeValues( patientA, patientB );

        PatientAttributeValue attributeValue = attributeValueService.getPatientAttributeValue( patientB, attributeB );
        assertEquals( "B", attributeValue.getValue() );

        attributeValue = attributeValueService.getPatientAttributeValue( patientB, attributeA );
        assertNull( attributeValue );
    }

    @Test
    public void testGetPatients()
    {
        attributeValueService.savePatientAttributeValue( attributeValueA );
        attributeValueService.savePatientAttributeValue( attributeValueB );
        attributeValueService.savePatientAttributeValue( attributeValueC );

        Collection<Patient> patients = attributeValueService.getPatient( attributeA, "A" );
        assertEquals( 1, patients.size() );
        assertTrue( patients.contains( patientA ) );
    }

    @Test
    public void testCountByPatientAttributeoption()
    {
        attributeValueService.savePatientAttributeValue( attributeValueA );
        attributeValueService.savePatientAttributeValue( attributeValueD );
        attributeValueService.savePatientAttributeValue( attributeValueE );

        int count = attributeValueService.countByPatientAttributeOption( attributeOpionA );
        assertEquals( 1, count );
    }

}
