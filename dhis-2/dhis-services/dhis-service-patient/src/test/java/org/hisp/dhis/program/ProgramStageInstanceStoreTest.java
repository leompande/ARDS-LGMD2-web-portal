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
import java.util.List;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientReminder;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.period.PeriodType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chau Thu Tran
 * 
 * @version $ ProgramStageInstanceStoreTest.java Nov 14, 2013 4:22:27 PM $
 */
public class ProgramStageInstanceStoreTest
    extends DhisSpringTest
{
    @Autowired
    private ProgramStageInstanceStore programStageInstanceStore;

    @Autowired
    private ProgramStageDataElementStore programStageDataElementStore;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private ProgramStageService programStageService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private ProgramInstanceService programInstanceService;

    private OrganisationUnit organisationUnitA;

    private OrganisationUnit organisationUnitB;

    private int orgunitAId;

    private int orgunitBId;

    private ProgramStage stageA;

    private ProgramStage stageB;

    private ProgramStage stageC;

    private ProgramStage stageD;

    private DataElement dataElementA;

    private DataElement dataElementB;

    private ProgramStageDataElement stageDataElementA;

    private ProgramStageDataElement stageDataElementB;

    private ProgramStageDataElement stageDataElementC;

    private ProgramStageDataElement stageDataElementD;

    private Date incidenDate;

    private Date enrollmentDate;

    private ProgramInstance programInstanceA;

    private ProgramInstance programInstanceB;

    private ProgramStageInstance programStageInstanceA;

    private ProgramStageInstance programStageInstanceB;

    private ProgramStageInstance programStageInstanceC;

    private ProgramStageInstance programStageInstanceD1;

    private ProgramStageInstance programStageInstanceD2;

    private Patient patientA;

    private Patient patientB;

    private Program programA;

    @Override
    public void setUpTest()
    {
        organisationUnitA = createOrganisationUnit( 'A' );
        orgunitAId = organisationUnitService.addOrganisationUnit( organisationUnitA );

        organisationUnitB = createOrganisationUnit( 'B' );
        orgunitBId = organisationUnitService.addOrganisationUnit( organisationUnitB );

        patientA = createPatient( 'A', organisationUnitA );
        patientService.savePatient( patientA );

        patientB = createPatient( 'B', organisationUnitB );
        patientService.savePatient( patientB );

        /**
         * Program A
         */
        programA = createProgram( 'A', new HashSet<ProgramStage>(), organisationUnitA );
        programService.addProgram( programA );

        stageA = new ProgramStage( "A", programA );

        PatientReminder patientReminderA = new PatientReminder( "A", 0, "Test program stage message template",
            PatientReminder.DUE_DATE_TO_COMPARE, PatientReminder.SEND_TO_PATIENT, null,
            PatientReminder.MESSAGE_TYPE_BOTH );

        PatientReminder patientReminderB = new PatientReminder( "B", 0, "Test program stage message template",
            PatientReminder.DUE_DATE_TO_COMPARE, PatientReminder.SEND_TO_PATIENT,
            PatientReminder.SEND_WHEN_TO_C0MPLETED_EVENT, PatientReminder.MESSAGE_TYPE_BOTH );

        Set<PatientReminder> patientReminders = new HashSet<PatientReminder>();
        patientReminders.add( patientReminderA );
        patientReminders.add( patientReminderB );
        stageA.setPatientReminders( patientReminders );

        programStageService.saveProgramStage( stageA );

        stageB = new ProgramStage( "B", programA );
        PatientReminder patientReminderC = new PatientReminder( "C", 0, "Test program stage message template",
            PatientReminder.DUE_DATE_TO_COMPARE, PatientReminder.SEND_TO_PATIENT,
            PatientReminder.SEND_WHEN_TO_C0MPLETED_EVENT, PatientReminder.MESSAGE_TYPE_BOTH );

        patientReminders = new HashSet<PatientReminder>();
        patientReminders.add( patientReminderC );
        stageB.setPatientReminders( patientReminders );
        programStageService.saveProgramStage( stageB );

        Set<ProgramStage> programStages = new HashSet<ProgramStage>();
        programStages.add( stageA );
        programStages.add( stageB );
        programA.setProgramStages( programStages );
        programService.updateProgram( programA );

        dataElementA = createDataElement( 'A' );
        dataElementB = createDataElement( 'B' );

        dataElementService.addDataElement( dataElementA );
        dataElementService.addDataElement( dataElementB );

        stageDataElementA = new ProgramStageDataElement( stageA, dataElementA, false, 1 );
        stageDataElementB = new ProgramStageDataElement( stageA, dataElementB, false, 2 );
        stageDataElementC = new ProgramStageDataElement( stageB, dataElementA, false, 1 );
        stageDataElementD = new ProgramStageDataElement( stageB, dataElementB, false, 2 );

        programStageDataElementStore.save( stageDataElementA );
        programStageDataElementStore.save( stageDataElementB );
        programStageDataElementStore.save( stageDataElementC );
        programStageDataElementStore.save( stageDataElementD );

        /**
         * Program B
         */

        Program programB = createProgram( 'B', new HashSet<ProgramStage>(), organisationUnitB );
        programService.addProgram( programB );

        stageC = new ProgramStage( "C", programB );
        programStageService.saveProgramStage( stageC );

        stageD = new ProgramStage( "D", programB );
        stageC.setIrregular( true );
        programStageService.saveProgramStage( stageD );

        programStages = new HashSet<ProgramStage>();
        programStages.add( stageC );
        programStages.add( stageD );
        programB.setProgramStages( programStages );
        programService.updateProgram( programB );

        /**
         * Program Instance and Program Stage Instance
         */

        Calendar calIncident = Calendar.getInstance();
        PeriodType.clearTimeOfDay( calIncident );
        calIncident.add( Calendar.DATE, -70 );
        incidenDate = calIncident.getTime();

        Calendar calEnrollment = Calendar.getInstance();
        PeriodType.clearTimeOfDay( calEnrollment );
        enrollmentDate = calEnrollment.getTime();

        programInstanceA = new ProgramInstance( enrollmentDate, incidenDate, patientA, programA );
        programInstanceA.setUid( "UID-PIA" );
        programInstanceService.addProgramInstance( programInstanceA );

        programInstanceB = new ProgramInstance( enrollmentDate, incidenDate, patientB, programB );
        programInstanceService.addProgramInstance( programInstanceB );

        programStageInstanceA = new ProgramStageInstance( programInstanceA, stageA );
        programStageInstanceA.setDueDate( enrollmentDate );
        programStageInstanceA.setUid( "UID-A" );

        programStageInstanceB = new ProgramStageInstance( programInstanceA, stageB );
        programStageInstanceB.setDueDate( enrollmentDate );
        programStageInstanceB.setUid( "UID-B" );

        programStageInstanceC = new ProgramStageInstance( programInstanceB, stageC );
        programStageInstanceC.setDueDate( enrollmentDate );
        programStageInstanceC.setUid( "UID-C" );

        programStageInstanceD1 = new ProgramStageInstance( programInstanceB, stageD );
        programStageInstanceD1.setDueDate( enrollmentDate );
        programStageInstanceD1.setUid( "UID-D1" );

        programStageInstanceD2 = new ProgramStageInstance( programInstanceB, stageD );
        programStageInstanceD2.setDueDate( enrollmentDate );
        programStageInstanceD2.setUid( "UID-D2" );
    }

    @Test
    public void testGetProgramStageInstanceByProgramInstanceStage()
    {
        programStageInstanceStore.save( programStageInstanceA );
        programStageInstanceStore.save( programStageInstanceB );

        ProgramStageInstance programStageInstance = programStageInstanceStore.get( programInstanceA, stageA );
        assertEquals( programStageInstanceA, programStageInstance );

        programStageInstance = programStageInstanceStore.get( programInstanceA, stageB );
        assertEquals( programStageInstanceB, programStageInstance );
    }

    @Test
    public void testGetProgramStageInstanceListByProgramInstanceStage()
    {
        programStageInstanceStore.save( programStageInstanceD1 );
        programStageInstanceStore.save( programStageInstanceD2 );

        Collection<ProgramStageInstance> stageInstances = programStageInstanceStore.getAll( programInstanceB, stageD );
        assertEquals( 2, stageInstances.size() );
        assertTrue( stageInstances.contains( programStageInstanceD1 ) );
        assertTrue( stageInstances.contains( programStageInstanceD2 ) );
    }

    @Test
    public void testGetProgramStageInstancesByInstanceListComplete()
    {
        programStageInstanceA.setCompleted( true );
        programStageInstanceB.setCompleted( false );
        programStageInstanceC.setCompleted( true );
        programStageInstanceD1.setCompleted( false );

        programStageInstanceStore.save( programStageInstanceA );
        programStageInstanceStore.save( programStageInstanceB );
        programStageInstanceStore.save( programStageInstanceC );
        programStageInstanceStore.save( programStageInstanceD1 );

        Collection<ProgramInstance> programInstances = new HashSet<ProgramInstance>();
        programInstances.add( programInstanceA );
        programInstances.add( programInstanceB );

        Collection<ProgramStageInstance> stageInstances = programStageInstanceStore.get( programInstances, true );
        assertEquals( 2, stageInstances.size() );
        assertTrue( stageInstances.contains( programStageInstanceA ) );
        assertTrue( stageInstances.contains( programStageInstanceC ) );

        stageInstances = programStageInstanceStore.get( programInstances, false );
        assertEquals( 2, stageInstances.size() );
        assertTrue( stageInstances.contains( programStageInstanceB ) );
        assertTrue( stageInstances.contains( programStageInstanceD1 ) );
    }

    @Test
    public void testGetProgramStageInstancesByPatientStatus()
    {
        programStageInstanceA.setCompleted( true );
        programStageInstanceB.setCompleted( false );
        programStageInstanceC.setCompleted( true );
        programStageInstanceD1.setCompleted( true );

        programStageInstanceStore.save( programStageInstanceA );
        programStageInstanceStore.save( programStageInstanceB );
        programStageInstanceStore.save( programStageInstanceC );
        programStageInstanceStore.save( programStageInstanceD1 );

        List<ProgramStageInstance> stageInstances = programStageInstanceStore.get( patientA, true );
        assertEquals( 1, stageInstances.size() );
        assertTrue( stageInstances.contains( programStageInstanceA ) );

        stageInstances = programStageInstanceStore.get( patientA, false );
        assertEquals( 1, stageInstances.size() );
        assertTrue( stageInstances.contains( programStageInstanceB ) );
    }

    @Test
    public void testGetProgramStageInstancesByOuPeriodProgram()
    {
        programStageInstanceA.setExecutionDate( enrollmentDate );
        programStageInstanceA.setOrganisationUnit( organisationUnitA );
        programStageInstanceB.setExecutionDate( enrollmentDate );
        programStageInstanceB.setOrganisationUnit( organisationUnitB );

        programStageInstanceStore.save( programStageInstanceA );
        programStageInstanceStore.save( programStageInstanceB );

        Collection<Integer> orgunitIds = new HashSet<Integer>();
        orgunitIds.add( orgunitAId );
        orgunitIds.add( orgunitBId );

        Collection<ProgramStageInstance> result = programStageInstanceStore.get( programA, orgunitIds, incidenDate,
            enrollmentDate, false );

        assertEquals( 2, result.size() );
        assertTrue( result.contains( programStageInstanceA ) );
        assertTrue( result.contains( programStageInstanceB ) );
    }

    @Test
    public void testGetOverDueEventCount()
    {
        Calendar cal = Calendar.getInstance();
        PeriodType.clearTimeOfDay( cal );
        cal.add( Calendar.DATE, -1 );
        Date date = cal.getTime();

        programStageInstanceA.setDueDate( date );
        programStageInstanceB.setDueDate( date );

        programStageInstanceStore.save( programStageInstanceA );
        programStageInstanceStore.save( programStageInstanceB );

        Collection<Integer> orgunitIds = new HashSet<Integer>();
        orgunitIds.add( orgunitAId );
        orgunitIds.add( orgunitBId );

        int count = programStageInstanceStore.getOverDueCount( stageA, orgunitIds, incidenDate, enrollmentDate );
        assertEquals( 1, count );
    }

    @Test
    public void testGetOrganisationUnitIds()
    {
        programStageInstanceA.setExecutionDate( enrollmentDate );
        programStageInstanceA.setOrganisationUnit( organisationUnitA );
        programStageInstanceB.setExecutionDate( enrollmentDate );
        programStageInstanceB.setOrganisationUnit( organisationUnitB );

        programStageInstanceStore.save( programStageInstanceA );
        programStageInstanceStore.save( programStageInstanceB );

        Collection<Integer> orgunitIds = programStageInstanceStore.getOrgunitIds( incidenDate, enrollmentDate );
        assertEquals( 2, orgunitIds.size() );
        assertTrue( orgunitIds.contains( orgunitAId ) );
        assertTrue( orgunitIds.contains( orgunitBId ) );
    }

}