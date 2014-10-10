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
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataentryform.DataEntryFormService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chau Thu Tran
 * 
 * @version $ PatientRegistrationFormServiceTest.java Nov 8, 2013 10:07:30 AM $
 */
public class PatientRegistrationFormServiceTest
    extends DhisSpringTest
{
    @Autowired
    private PatientRegistrationFormService patientRegistrationFormService;

    @Autowired
    private DataEntryFormService dataEntryFormService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    private PatientRegistrationForm patientRegistrationFormA;

    private PatientRegistrationForm patientRegistrationFormB;

    private PatientRegistrationForm patientRegistrationFormC;

    private Program programA;

    private Program programB;

    private DataEntryForm dataEntryForm;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
        throws Exception
    {
        OrganisationUnit organisationUnit = createOrganisationUnit( 'A' );
        organisationUnitService.addOrganisationUnit( organisationUnit );

        programA = createProgram( 'A', new HashSet<ProgramStage>(), organisationUnit );
        programService.addProgram( programA );

        programB = createProgram( 'B', new HashSet<ProgramStage>(), organisationUnit );
        programService.addProgram( programB );

        DataEntryForm dataEntryFormA = new DataEntryForm( "DataEntryForm-A" );
        dataEntryFormService.addDataEntryForm( dataEntryFormA );

        DataEntryForm dataEntryFormB = new DataEntryForm( "DataEntryForm-B" );
        dataEntryFormService.addDataEntryForm( dataEntryFormB );

        DataEntryForm dataEntryFormC = new DataEntryForm( "DataEntryForm-C" );
        dataEntryFormService.addDataEntryForm( dataEntryFormC );

        patientRegistrationFormA = new PatientRegistrationForm( programA, dataEntryForm );
        patientRegistrationFormB = new PatientRegistrationForm( programB, dataEntryForm );
        patientRegistrationFormC = new PatientRegistrationForm( dataEntryForm );
    }

    @Test
    public void testSavePatientRegistrationForm()
    {
        int idA = patientRegistrationFormService.savePatientRegistrationForm( patientRegistrationFormA );
        int idB = patientRegistrationFormService.savePatientRegistrationForm( patientRegistrationFormB );

        assertNotNull( patientRegistrationFormService.getPatientRegistrationForm( idA ) );
        assertNotNull( patientRegistrationFormService.getPatientRegistrationForm( idB ) );
    }

    @Test
    public void testDeletePatientRegistrationForm()
    {
        int idA = patientRegistrationFormService.savePatientRegistrationForm( patientRegistrationFormA );
        int idB = patientRegistrationFormService.savePatientRegistrationForm( patientRegistrationFormB );

        assertNotNull( patientRegistrationFormService.getPatientRegistrationForm( idA ) );
        assertNotNull( patientRegistrationFormService.getPatientRegistrationForm( idB ) );

        patientRegistrationFormService.deletePatientRegistrationForm( patientRegistrationFormA );

        assertNull( patientRegistrationFormService.getPatientRegistrationForm( idA ) );
        assertNotNull( patientRegistrationFormService.getPatientRegistrationForm( idB ) );

        patientRegistrationFormService.deletePatientRegistrationForm( patientRegistrationFormB );

        assertNull( patientRegistrationFormService.getPatientRegistrationForm( idA ) );
        assertNull( patientRegistrationFormService.getPatientRegistrationForm( idB ) );
    }

    @Test
    public void testUpdatePatientRegistrationForm()
    {
        int idA = patientRegistrationFormService.savePatientRegistrationForm( patientRegistrationFormA );

        assertNotNull( patientRegistrationFormService.getPatientRegistrationForm( idA ) );

        patientRegistrationFormA.setName( "B" );
        patientRegistrationFormService.updatePatientRegistrationForm( patientRegistrationFormA );

        assertEquals( "B", patientRegistrationFormService.getPatientRegistrationForm( idA ).getName() );
    }

    @Test
    public void testGetPatientRegistrationFormById()
    {
        int idA = patientRegistrationFormService.savePatientRegistrationForm( patientRegistrationFormA );
        int idB = patientRegistrationFormService.savePatientRegistrationForm( patientRegistrationFormB );

        assertEquals( patientRegistrationFormA, patientRegistrationFormService.getPatientRegistrationForm( idA ) );
        assertEquals( patientRegistrationFormB, patientRegistrationFormService.getPatientRegistrationForm( idB ) );
    }

    @Test
    public void testGetAllPatientRegistrationForms()
    {
        patientRegistrationFormService.savePatientRegistrationForm( patientRegistrationFormA );
        patientRegistrationFormService.savePatientRegistrationForm( patientRegistrationFormB );

        Collection<PatientRegistrationForm> forms = patientRegistrationFormService.getAllPatientRegistrationForms();
        assertEquals( 2, forms.size() );
        assertTrue( forms.contains( patientRegistrationFormA ) );
        assertTrue( forms.contains( patientRegistrationFormB ) );
    }

    @Test
    public void testGetPatientRegistrationFormByProgram()
    {
        patientRegistrationFormService.savePatientRegistrationForm( patientRegistrationFormA );
        patientRegistrationFormService.savePatientRegistrationForm( patientRegistrationFormB );
        patientRegistrationFormService.savePatientRegistrationForm( patientRegistrationFormC );

        PatientRegistrationForm form = patientRegistrationFormService.getPatientRegistrationForm( programA );
        assertEquals( patientRegistrationFormA, form );
    }

    @Test
    public void testGetCommonPatientRegistrationForm()
    {
        patientRegistrationFormService.savePatientRegistrationForm( patientRegistrationFormA );
        patientRegistrationFormService.savePatientRegistrationForm( patientRegistrationFormB );
        patientRegistrationFormService.savePatientRegistrationForm( patientRegistrationFormC );

        PatientRegistrationForm form = patientRegistrationFormService.getCommonPatientRegistrationForm();
        assertEquals( patientRegistrationFormC, form );
    }

}
