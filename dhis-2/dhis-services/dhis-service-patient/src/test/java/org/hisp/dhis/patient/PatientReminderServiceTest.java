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
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.mock.MockI18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chau Thu Tran
 * 
 * @version $ PatientAttributeGroupServiceTest.java Nov 5, 2013 3:11:48 PM $
 */
public class PatientReminderServiceTest
    extends DhisSpringTest
{
    @Autowired
    private PatientReminderService patientReminderService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private ProgramStageService programStageService;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private ProgramStageInstanceService programStageInstanceService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private UserService userService;

    @Autowired
    private PatientAttributeService patientAttributeService;

    @Autowired
    private PatientAttributeValueService patientAttributeValueService;

    private MockI18nFormat mockFormat;

    private ProgramInstance programInstance;

    private PatientReminder patientReminderA;

    private PatientReminder patientReminderB;

    private PatientReminder patientReminderC;

    private Patient patient;

    private ProgramStage stageA;

    private User user;

    @Override
    public void setUpTest()
    {
        mockFormat = new MockI18nFormat();

        OrganisationUnit organisationUnit = createOrganisationUnit( 'A' );
        organisationUnitService.addOrganisationUnit( organisationUnit );

        Set<OrganisationUnit> orgUnits = new HashSet<OrganisationUnit>();
        orgUnits.add( organisationUnit );

        user = new User();
        user.setSurname( "A" );
        user.setFirstName( "B" );
        user.setPhoneNumber( "111-222-333" );
        user.updateOrganisationUnits( orgUnits );
        userService.addUser( user );

        patient = createPatient( 'A', organisationUnit );
        patientService.savePatient( patient );

        Program program = createProgram( 'A', new HashSet<ProgramStage>(), organisationUnit );
        patientReminderA = new PatientReminder( "A", 0, "Test program message template", PatientReminder.ENROLLEMENT_DATE_TO_COMPARE,
            PatientReminder.SEND_TO_PATIENT, PatientReminder.SEND_WHEN_TO_C0MPLETED_EVENT,
            PatientReminder.MESSAGE_TYPE_DIRECT_SMS );
        Set<PatientReminder> patientReminders = new HashSet<PatientReminder>();
        patientReminders.add( patientReminderA );
        program.setPatientReminders( patientReminders );
        programService.addProgram( program );

        stageA = new ProgramStage( "A", program );
        patientReminderB = new PatientReminder( "B", 0, "Test event template" , PatientReminder.DUE_DATE_TO_COMPARE,
            PatientReminder.SEND_TO_PATIENT, PatientReminder.SEND_WHEN_TO_C0MPLETED_EVENT,
            PatientReminder.MESSAGE_TYPE_DIRECT_SMS );
        patientReminders = new HashSet<PatientReminder>();
        patientReminders.add( patientReminderB );
        stageA.setPatientReminders( patientReminders );
        programStageService.saveProgramStage( stageA );

        ProgramStage stageB = new ProgramStage( "B", program );
        patientReminderC = new PatientReminder( "C", 0, "Test event template", PatientReminder.DUE_DATE_TO_COMPARE,
            PatientReminder.SEND_TO_ALL_USERS_IN_ORGUGNIT_REGISTERED, PatientReminder.SEND_WHEN_TO_C0MPLETED_EVENT,
            PatientReminder.MESSAGE_TYPE_DIRECT_SMS );
        patientReminders = new HashSet<PatientReminder>();
        patientReminders.add( patientReminderB );
        stageB.setPatientReminders( patientReminders );

        Set<ProgramStage> programStages = new HashSet<ProgramStage>();
        programStages.add( stageA );
        programStages.add( stageB );
        program.setProgramStages( programStages );
        programStageService.saveProgramStage( stageB );

        programService.updateProgram( program );

        programInstance = programInstanceService.enrollPatient( patient, program, new Date(), new Date(),
            organisationUnit, null );
    }

    @Test
    public void testGetMessageFromTemplateByProgram()
    {
        String message = patientReminderService.getMessageFromTemplate( patientReminderA, programInstance, mockFormat );
        assertEquals( "Test program message template", message );
    }

    @Test
    public void testGetMessageFromTemplateByProgramStage()
    {
        ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance(
            programInstance, stageA );
        String message = patientReminderService.getMessageFromTemplate( patientReminderA, programStageInstance,
            mockFormat );
        assertEquals( "Test program message template", message );
    }

    @Test
    public void testGetPhonenumbers()
    {
        PatientAttribute attribute = createPatientAttribute( 'A' );
        attribute.setValueType( PatientAttribute.TYPE_PHONE_NUMBER );
        patientAttributeService.savePatientAttribute( attribute );

        PatientAttributeValue attributeValue = createPatientAttributeValue( 'A', patient, attribute );
        attributeValue.setValue( "123456789" );
        patientAttributeValueService.savePatientAttributeValue( attributeValue );
        
        patient.getAttributeValues().add( attributeValue );
        patientService.updatePatient( patient );

        Set<String> phoneNumbers = patientReminderService.getPhonenumbers( patientReminderA, patient );
        assertEquals( 1, phoneNumbers.size() );
        assertTrue( phoneNumbers.contains( "123456789" ) );
    }

    @Test
    public void testGetUsers()
    {
        Set<User> users = patientReminderService.getUsers( patientReminderC, patient );
        assertEquals( 1, users.size() );
        assertTrue( users.contains( user ) );
    }
}
