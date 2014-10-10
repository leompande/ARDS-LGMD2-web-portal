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

package org.hisp.dhis.program;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientReminder;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.period.PeriodType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Chau Thu Tran
 * 
 * @version $ ProgramInstanceStoreTest.java Nov 13, 2013 1:34:55 PM $
 */
public class ProgramInstanceStoreTest
    extends DhisSpringTest
{
    @Autowired
    private ProgramInstanceStore programInstanceStore;

    @Autowired
    private PatientService patientService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private ProgramStageService programStageService;

    private Date incidenDate;

    private Date enrollmentDate;

    private Program programA;

    private Program programB;

    private Program programC;

    private OrganisationUnit organisationUnitA;

    private OrganisationUnit organisationUnitB;

    private ProgramInstance programInstanceA;

    private ProgramInstance programInstanceB;

    private ProgramInstance programInstanceC;

    private ProgramInstance programInstanceD;

    private Patient patientA;

    private Collection<Integer> orgunitIds;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void setUpTest()
    {
        organisationUnitA = createOrganisationUnit( 'A' );
        int idA = organisationUnitService.addOrganisationUnit( organisationUnitA );

        organisationUnitB = createOrganisationUnit( 'B' );
        int idB = organisationUnitService.addOrganisationUnit( organisationUnitB );

        orgunitIds = new HashSet<Integer>();
        orgunitIds.add( idA );
        orgunitIds.add( idB );

        programA = createProgram( 'A', new HashSet<ProgramStage>(), organisationUnitA );

        PatientReminder patientReminderA = new PatientReminder( "A", 0, "Test program message template", PatientReminder.ENROLLEMENT_DATE_TO_COMPARE,
            PatientReminder.SEND_TO_PATIENT, null, PatientReminder.MESSAGE_TYPE_BOTH );

        PatientReminder patientReminderB = new PatientReminder( "B", 0, "Test program message template", PatientReminder.ENROLLEMENT_DATE_TO_COMPARE,
            PatientReminder.SEND_TO_PATIENT, PatientReminder.SEND_WHEN_TO_C0MPLETED_EVENT,
            PatientReminder.MESSAGE_TYPE_BOTH );

        Set<PatientReminder> patientReminders = new HashSet<PatientReminder>();
        patientReminders.add( patientReminderA );
        patientReminders.add( patientReminderB );
        programA.setPatientReminders( patientReminders );

        programService.addProgram( programA );

        ProgramStage stageA = new ProgramStage( "StageA", programA );
        programStageService.saveProgramStage( stageA );

        ProgramStage stageB = new ProgramStage( "StageB", programA );
        programStageService.saveProgramStage( stageB );

        Set<ProgramStage> programStages = new HashSet<ProgramStage>();
        programStages.add( stageA );
        programStages.add( stageB );
        programA.setProgramStages( programStages );
        programService.updateProgram( programA );

        programB = createProgram( 'B', new HashSet<ProgramStage>(), organisationUnitA );
        programService.addProgram( programB );

        programC = createProgram( 'C', new HashSet<ProgramStage>(), organisationUnitA );
        programService.addProgram( programC );

        patientA = createPatient( 'A', organisationUnitA );
        patientService.savePatient( patientA );

        Patient patientB = createPatient( 'B', organisationUnitB );
        patientService.savePatient( patientB );

        Calendar calIncident = Calendar.getInstance();
        PeriodType.clearTimeOfDay( calIncident );
        calIncident.add( Calendar.DATE, -70 );
        incidenDate = calIncident.getTime();

        Calendar calEnrollment = Calendar.getInstance();
        PeriodType.clearTimeOfDay( calEnrollment );
        enrollmentDate = calEnrollment.getTime();

        programInstanceA = new ProgramInstance( enrollmentDate, incidenDate, patientA, programA );
        programInstanceA.setUid( "UID-A" );

        programInstanceB = new ProgramInstance( enrollmentDate, incidenDate, patientA, programB );
        programInstanceB.setUid( "UID-B" );
        programInstanceB.setStatus( ProgramInstance.STATUS_CANCELLED );

        programInstanceC = new ProgramInstance( enrollmentDate, incidenDate, patientA, programC );
        programInstanceC.setUid( "UID-C" );
        programInstanceC.setStatus( ProgramInstance.STATUS_COMPLETED );

        programInstanceD = new ProgramInstance( enrollmentDate, incidenDate, patientB, programA );
        programInstanceD.setUid( "UID-D" );
    }

    @Test
    public void testGetProgramInstancesByStatus()
    {
        programInstanceStore.save( programInstanceA );
        programInstanceStore.save( programInstanceB );
        programInstanceStore.save( programInstanceC );
        programInstanceStore.save( programInstanceD );

        Collection<ProgramInstance> programInstances = programInstanceStore.getByStatus( ProgramInstance.STATUS_ACTIVE );
        assertEquals( 2, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceA ) );
        assertTrue( programInstances.contains( programInstanceD ) );

        programInstances = programInstanceStore.getByStatus( ProgramInstance.STATUS_CANCELLED );
        assertEquals( 1, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceB ) );

        programInstances = programInstanceStore.getByStatus( ProgramInstance.STATUS_COMPLETED );
        assertEquals( 1, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceC ) );
    }

    @Test
    public void testGetProgramInstancesByProgram()
    {
        programInstanceStore.save( programInstanceA );
        programInstanceStore.save( programInstanceB );
        programInstanceStore.save( programInstanceD );

        Collection<ProgramInstance> programInstances = programInstanceStore.get( programA );
        assertEquals( 2, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceA ) );
        assertTrue( programInstances.contains( programInstanceD ) );

        programInstances = programInstanceStore.get( programB );
        assertEquals( 1, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceB ) );
    }

    @Test
    public void testGetProgramInstancesByProgramList()
    {
        programInstanceStore.save( programInstanceA );
        programInstanceStore.save( programInstanceB );
        programInstanceStore.save( programInstanceC );
        programInstanceStore.save( programInstanceD );

        Collection<Program> programs = new HashSet<Program>();
        programs.add( programA );
        programs.add( programB );

        Collection<ProgramInstance> programInstances = programInstanceStore.get( programs );
        assertEquals( 3, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceA ) );
        assertTrue( programInstances.contains( programInstanceB ) );
        assertTrue( programInstances.contains( programInstanceD ) );
    }

    @Test
    public void testGetProgramInstancesByOuProgramList()
    {
        programInstanceStore.save( programInstanceA );
        programInstanceStore.save( programInstanceB );
        programInstanceStore.save( programInstanceC );
        programInstanceStore.save( programInstanceD );

        Collection<Program> programs = new HashSet<Program>();
        programs.add( programA );
        programs.add( programB );

        Collection<ProgramInstance> programInstances = programInstanceStore.get( programs, organisationUnitA );
        assertEquals( 2, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceA ) );
        assertTrue( programInstances.contains( programInstanceB ) );

        programInstances = programInstanceStore.get( programs, organisationUnitB );
        assertEquals( 1, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceD ) );
    }

    @Test
    public void testGetProgramInstancesByOuProgramListStatus()
    {
        programInstanceStore.save( programInstanceA );
        programInstanceStore.save( programInstanceB );
        programInstanceStore.save( programInstanceC );

        Collection<Program> programs = new HashSet<Program>();
        programs.add( programA );
        programs.add( programB );
        programs.add( programC );

        Collection<ProgramInstance> programInstances = programInstanceStore.get( programs, organisationUnitA,
            ProgramInstance.STATUS_ACTIVE );
        assertEquals( 1, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceA ) );

        programInstances = programInstanceStore.get( programs, organisationUnitA, ProgramInstance.STATUS_COMPLETED );
        assertEquals( 1, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceC ) );

    }

    @Test
    public void testGetProgramInstancesByProgramStatus()
    {
        programInstanceStore.save( programInstanceA );
        programInstanceStore.save( programInstanceB );
        programInstanceStore.save( programInstanceC );
        programInstanceStore.save( programInstanceD );

        Collection<ProgramInstance> programInstances = programInstanceStore.get( programA,
            ProgramInstance.STATUS_ACTIVE );
        assertEquals( 2, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceA ) );
        assertTrue( programInstances.contains( programInstanceD ) );

        programInstances = programInstanceStore.get( programB, ProgramInstance.STATUS_CANCELLED );
        assertEquals( 1, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceB ) );

        programInstances = programInstanceStore.get( programC, ProgramInstance.STATUS_COMPLETED );
        assertEquals( 1, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceC ) );
    }

    @Test
    public void testGetProgramInstancesByProgramListStatus()
    {
        programInstanceStore.save( programInstanceA );
        programInstanceStore.save( programInstanceB );
        programInstanceStore.save( programInstanceC );
        programInstanceStore.save( programInstanceD );

        Collection<Program> programs = new HashSet<Program>();
        programs.add( programA );
        programs.add( programB );

        Collection<ProgramInstance> programInstances = programInstanceStore.get( programs,
            ProgramInstance.STATUS_ACTIVE );
        assertEquals( 2, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceA ) );
        assertTrue( programInstances.contains( programInstanceD ) );
    }

    @Test
    public void testGetProgramInstancesByPatientStatus()
    {
        programInstanceStore.save( programInstanceA );
        programInstanceStore.save( programInstanceD );

        Collection<Program> programs = new HashSet<Program>();
        programs.add( programA );
        programs.add( programB );

        Collection<ProgramInstance> programInstances = programInstanceStore.get( patientA,
            ProgramInstance.STATUS_ACTIVE );
        assertEquals( 1, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceA ) );
    }

    @Test
    public void testGetProgramInstancesByPatientProgramStatus()
    {
        programInstanceStore.save( programInstanceA );
        programInstanceStore.save( programInstanceB );
        programInstanceStore.save( programInstanceC );
        programInstanceStore.save( programInstanceD );

        Collection<ProgramInstance> programInstances = programInstanceStore.get( patientA, programC,
            ProgramInstance.STATUS_COMPLETED );
        assertEquals( 1, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceC ) );

        programInstances = programInstanceStore.get( patientA, programA, ProgramInstance.STATUS_ACTIVE );
        assertEquals( 1, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceA ) );
    }

    @Test
    public void testGetProgramInstancesByOuProgram()
    {
        programInstanceStore.save( programInstanceA );
        programInstanceStore.save( programInstanceC );
        programInstanceStore.save( programInstanceD );

        Collection<ProgramInstance> programInstances = programInstanceStore.get( programA, organisationUnitA, 0, 10 );
        assertEquals( 1, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceA ) );
    }

    @Test
    public void testGetProgramInstancesbyProgramOuPeriod()
    {
        programInstanceStore.save( programInstanceA );
        programInstanceStore.save( programInstanceD );

        Collection<ProgramInstance> programInstances = programInstanceStore.get( programA, organisationUnitA,
            incidenDate, enrollmentDate );
        assertEquals( 1, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceA ) );
    }

    @Test
    public void testGetProgramInstancesByOuListProgramPeriod()
    {
        programInstanceStore.save( programInstanceA );
        programInstanceStore.save( programInstanceB );
        programInstanceStore.save( programInstanceD );

        Collection<ProgramInstance> programInstances = programInstanceStore.get( programA, orgunitIds, incidenDate,
            enrollmentDate, null, null );
        assertEquals( 2, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceA ) );
        assertTrue( programInstances.contains( programInstanceD ) );
    }

    @Test
    public void testCountProgramInstancesByOuListProgramPeriod()
    {
        programInstanceStore.save( programInstanceA );
        programInstanceStore.save( programInstanceB );
        programInstanceStore.save( programInstanceD );

        int count = programInstanceStore.count( programA, orgunitIds, incidenDate, enrollmentDate );
        assertEquals( 2, count );
    }

    @Test
    public void testGetProgramInstancesByOuListProgramStatusPeriod()
    {
        programInstanceStore.save( programInstanceA );
        programInstanceStore.save( programInstanceB );
        programInstanceStore.save( programInstanceD );

        Collection<ProgramInstance> programInstances = programInstanceStore.getByStatus( ProgramInstance.STATUS_ACTIVE,
            programA, orgunitIds, incidenDate, enrollmentDate );
        assertEquals( 2, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceA ) );
        assertTrue( programInstances.contains( programInstanceD ) );
    }

    @Test
    public void testCountProgramInstancesByStatus()
    {
        programInstanceStore.save( programInstanceA );
        programInstanceStore.save( programInstanceB );
        programInstanceStore.save( programInstanceD );

        int count = programInstanceStore.countByStatus( ProgramInstance.STATUS_ACTIVE, programA, orgunitIds,
            incidenDate, enrollmentDate );
        assertEquals( 2, count );
    }

}
