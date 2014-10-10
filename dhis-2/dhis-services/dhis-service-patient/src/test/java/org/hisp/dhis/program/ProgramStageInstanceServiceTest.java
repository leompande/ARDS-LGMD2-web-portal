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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.message.MessageConversation;
import org.hisp.dhis.mock.MockI18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientReminder;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.sms.config.BulkSmsGatewayConfig;
import org.hisp.dhis.sms.config.SmsConfiguration;
import org.hisp.dhis.sms.config.SmsConfigurationManager;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chau Thu Tran
 * 
 * @version $ ProgramStageInstanceServiceTest.java Nov 14, 2013 4:22:27 PM $
 */
public class ProgramStageInstanceServiceTest
    extends DhisSpringTest
{
    @Autowired
    private ProgramStageInstanceService programStageInstanceService;

    @Autowired
    private ProgramStageDataElementService programStageDataElementService;

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

    @Autowired
    private SmsConfigurationManager smsConfigurationManager;

    @Autowired
    private PatientAttributeService patientAttributeService;

    @Autowired
    private PatientAttributeValueService patientAttributeValueService;

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

    private MockI18nFormat mockFormat;

    @Override
    public void setUpTest()
    {
        mockFormat = new MockI18nFormat();

        organisationUnitA = createOrganisationUnit( 'A' );
        orgunitAId = organisationUnitService.addOrganisationUnit( organisationUnitA );

        organisationUnitB = createOrganisationUnit( 'B' );
        orgunitBId = organisationUnitService.addOrganisationUnit( organisationUnitB );

        patientA = createPatient( 'A', organisationUnitA );
        patientService.savePatient( patientA );

        patientB = createPatient( 'B', organisationUnitB );
        patientService.savePatient( patientB );

        PatientAttribute attribute = createPatientAttribute( 'A' );
        attribute.setValueType( PatientAttribute.TYPE_PHONE_NUMBER );
        patientAttributeService.savePatientAttribute( attribute );

        PatientAttributeValue attributeValue = createPatientAttributeValue( 'A', patientA, attribute );
        attributeValue.setValue( "123456789" );
        patientAttributeValueService.savePatientAttributeValue( attributeValue );

        patientA.getAttributeValues().add( attributeValue );
        patientService.updatePatient( patientA );

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

        programStageDataElementService.addProgramStageDataElement( stageDataElementA );
        programStageDataElementService.addProgramStageDataElement( stageDataElementB );
        programStageDataElementService.addProgramStageDataElement( stageDataElementC );
        programStageDataElementService.addProgramStageDataElement( stageDataElementD );

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
    public void testAddProgramStageInstance()
    {
        int idA = programStageInstanceService.addProgramStageInstance( programStageInstanceA );
        int idB = programStageInstanceService.addProgramStageInstance( programStageInstanceB );

        assertNotNull( programStageInstanceService.getProgramStageInstance( idA ) );
        assertNotNull( programStageInstanceService.getProgramStageInstance( idB ) );
    }

    @Test
    public void testDeleteProgramStageInstance()
    {
        int idA = programStageInstanceService.addProgramStageInstance( programStageInstanceA );
        int idB = programStageInstanceService.addProgramStageInstance( programStageInstanceB );

        assertNotNull( programStageInstanceService.getProgramStageInstance( idA ) );
        assertNotNull( programStageInstanceService.getProgramStageInstance( idB ) );

        programStageInstanceService.deleteProgramStageInstance( programStageInstanceA );

        assertNull( programStageInstanceService.getProgramStageInstance( idA ) );
        assertNotNull( programStageInstanceService.getProgramStageInstance( idB ) );

        programStageInstanceService.deleteProgramStageInstance( programStageInstanceB );

        assertNull( programStageInstanceService.getProgramStageInstance( idA ) );
        assertNull( programStageInstanceService.getProgramStageInstance( idB ) );
    }

    @Test
    public void testUpdateProgramStageInstance()
    {
        int idA = programStageInstanceService.addProgramStageInstance( programStageInstanceA );

        assertNotNull( programStageInstanceService.getProgramStageInstance( idA ) );

        programStageInstanceA.setName( "B" );
        programStageInstanceService.updateProgramStageInstance( programStageInstanceA );

        assertEquals( "B", programStageInstanceService.getProgramStageInstance( idA ).getName() );
    }

    @Test
    public void testGetProgramStageInstanceById()
    {
        int idA = programStageInstanceService.addProgramStageInstance( programStageInstanceA );
        int idB = programStageInstanceService.addProgramStageInstance( programStageInstanceB );

        assertEquals( programStageInstanceA, programStageInstanceService.getProgramStageInstance( idA ) );
        assertEquals( programStageInstanceB, programStageInstanceService.getProgramStageInstance( idB ) );
    }

    @Test
    public void testGetProgramStageInstanceByUid()
    {
        int idA = programStageInstanceService.addProgramStageInstance( programStageInstanceA );
        int idB = programStageInstanceService.addProgramStageInstance( programStageInstanceB );

        assertEquals( programStageInstanceA, programStageInstanceService.getProgramStageInstance( idA ) );
        assertEquals( programStageInstanceB, programStageInstanceService.getProgramStageInstance( idB ) );

        assertEquals( programStageInstanceA, programStageInstanceService.getProgramStageInstance( "UID-A" ) );
        assertEquals( programStageInstanceB, programStageInstanceService.getProgramStageInstance( "UID-B" ) );
    }

    @Test
    public void testGetProgramStageInstanceByProgramInstanceStage()
    {
        programStageInstanceService.addProgramStageInstance( programStageInstanceA );
        programStageInstanceService.addProgramStageInstance( programStageInstanceB );

        ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance(
            programInstanceA, stageA );
        assertEquals( programStageInstanceA, programStageInstance );

        programStageInstance = programStageInstanceService.getProgramStageInstance( programInstanceA, stageB );
        assertEquals( programStageInstanceB, programStageInstance );
    }

    @Test
    public void testGetProgramStageInstanceListByProgramInstanceStage()
    {
        programStageInstanceService.addProgramStageInstance( programStageInstanceD1 );
        programStageInstanceService.addProgramStageInstance( programStageInstanceD2 );

        Collection<ProgramStageInstance> stageInstances = programStageInstanceService.getProgramStageInstances(
            programInstanceB, stageD );
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

        programStageInstanceService.addProgramStageInstance( programStageInstanceA );
        programStageInstanceService.addProgramStageInstance( programStageInstanceB );
        programStageInstanceService.addProgramStageInstance( programStageInstanceC );
        programStageInstanceService.addProgramStageInstance( programStageInstanceD1 );

        Collection<ProgramInstance> programInstances = new HashSet<ProgramInstance>();
        programInstances.add( programInstanceA );
        programInstances.add( programInstanceB );

        Collection<ProgramStageInstance> stageInstances = programStageInstanceService.getProgramStageInstances(
            programInstances, true );
        assertEquals( 2, stageInstances.size() );
        assertTrue( stageInstances.contains( programStageInstanceA ) );
        assertTrue( stageInstances.contains( programStageInstanceC ) );

        stageInstances = programStageInstanceService.getProgramStageInstances( programInstances, false );
        assertEquals( 2, stageInstances.size() );
        assertTrue( stageInstances.contains( programStageInstanceB ) );
        assertTrue( stageInstances.contains( programStageInstanceD1 ) );
    }

    @Test
    public void testStatusProgramStageInstances()
    {
        programStageInstanceA.setCompleted( true );
        programStageInstanceC.setStatus( ProgramStageInstance.SKIPPED_STATUS );

        int idA = programStageInstanceService.addProgramStageInstance( programStageInstanceA );
        int idB = programStageInstanceService.addProgramStageInstance( programStageInstanceB );
        int idC = programStageInstanceService.addProgramStageInstance( programStageInstanceC );
        int idD = programStageInstanceService.addProgramStageInstance( programStageInstanceD1 );

        Collection<ProgramStageInstance> programStageInstances = new HashSet<ProgramStageInstance>();
        programStageInstances.add( programStageInstanceA );
        programStageInstances.add( programStageInstanceB );
        programStageInstances.add( programStageInstanceC );
        programStageInstances.add( programStageInstanceD1 );

        Map<Integer, Integer> expectedMap = new HashMap<Integer, Integer>();
        expectedMap.put( idA, ProgramStageInstance.COMPLETED_STATUS );
        expectedMap.put( idB, ProgramStageInstance.FUTURE_VISIT_STATUS );
        expectedMap.put( idC, ProgramStageInstance.SKIPPED_STATUS );
        expectedMap.put( idD, ProgramStageInstance.FUTURE_VISIT_STATUS );

        Map<Integer, Integer> actualMap = programStageInstanceService
            .statusProgramStageInstances( programStageInstances );
        assertEquals( expectedMap, actualMap );
    }

    @Test
    public void testGetProgramStageInstancesByPatientStatus()
    {
        programStageInstanceA.setCompleted( true );
        programStageInstanceB.setCompleted( false );
        programStageInstanceC.setCompleted( true );
        programStageInstanceD1.setCompleted( true );

        programStageInstanceService.addProgramStageInstance( programStageInstanceA );
        programStageInstanceService.addProgramStageInstance( programStageInstanceB );
        programStageInstanceService.addProgramStageInstance( programStageInstanceC );
        programStageInstanceService.addProgramStageInstance( programStageInstanceD1 );

        List<ProgramStageInstance> stageInstances = programStageInstanceService.getProgramStageInstances( patientA,
            true );
        assertEquals( 1, stageInstances.size() );
        assertTrue( stageInstances.contains( programStageInstanceA ) );

        stageInstances = programStageInstanceService.getProgramStageInstances( patientA, false );
        assertEquals( 1, stageInstances.size() );
        assertTrue( stageInstances.contains( programStageInstanceB ) );
    }

    @Test
    public void testUpdateProgramStageInstances()
    {
        int idA = programStageInstanceService.addProgramStageInstance( programStageInstanceA );
        int idB = programStageInstanceService.addProgramStageInstance( programStageInstanceB );

        OutboundSms outboundSms = new OutboundSms();
        Collection<Integer> programStageInstances = new HashSet<Integer>();
        programStageInstances.add( idA );
        programStageInstances.add( idB );

        programStageInstanceService.updateProgramStageInstances( programStageInstances, outboundSms );
        assertTrue( programStageInstanceService.getProgramStageInstance( idA ).getOutboundSms().contains( outboundSms ) );
        assertTrue( programStageInstanceService.getProgramStageInstance( idB ).getOutboundSms().contains( outboundSms ) );
    }

    @Test
    public void testGetProgramStageInstancesByOuPeriodProgram()
    {
        programStageInstanceA.setExecutionDate( enrollmentDate );
        programStageInstanceA.setOrganisationUnit( organisationUnitA );
        programStageInstanceB.setExecutionDate( enrollmentDate );
        programStageInstanceB.setOrganisationUnit( organisationUnitB );

        programStageInstanceService.addProgramStageInstance( programStageInstanceA );
        programStageInstanceService.addProgramStageInstance( programStageInstanceB );

        Collection<Integer> orgunitIds = new HashSet<Integer>();
        orgunitIds.add( orgunitAId );
        orgunitIds.add( orgunitBId );

        Collection<ProgramStageInstance> result = programStageInstanceService.getProgramStageInstances( programA,
            orgunitIds, incidenDate, enrollmentDate, false );

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

        programStageInstanceService.addProgramStageInstance( programStageInstanceA );
        programStageInstanceService.addProgramStageInstance( programStageInstanceB );

        Collection<Integer> orgunitIds = new HashSet<Integer>();
        orgunitIds.add( orgunitAId );
        orgunitIds.add( orgunitBId );

        int count = programStageInstanceService.getOverDueEventCount( stageA, orgunitIds, incidenDate, enrollmentDate );
        assertEquals( 1, count );
    }

    @Test
    public void testGetOrganisationUnitIds()
    {
        programStageInstanceA.setExecutionDate( enrollmentDate );
        programStageInstanceA.setOrganisationUnit( organisationUnitA );
        programStageInstanceB.setExecutionDate( enrollmentDate );
        programStageInstanceB.setOrganisationUnit( organisationUnitB );

        programStageInstanceService.addProgramStageInstance( programStageInstanceA );
        programStageInstanceService.addProgramStageInstance( programStageInstanceB );

        Collection<Integer> orgunitIds = programStageInstanceService.getOrganisationUnitIds( incidenDate,
            enrollmentDate );
        assertEquals( 2, orgunitIds.size() );
        assertTrue( orgunitIds.contains( orgunitAId ) );
        assertTrue( orgunitIds.contains( orgunitBId ) );
    }

    @Test
    public void testSendMessages()
    {
        createSMSConfiguration();

        programStageInstanceService.addProgramStageInstance( programStageInstanceA );

        Collection<OutboundSms> outboundSmsList = programStageInstanceService.sendMessages( programStageInstanceA,
            PatientReminder.SEND_WHEN_TO_C0MPLETED_EVENT, mockFormat );
        assertEquals( 1, outboundSmsList.size() );
        assertEquals( "Test program stage message template", outboundSmsList.iterator().next().getMessage() );
    }

    @Test
    public void testSendMessageConversations()
    {
        programStageInstanceService.addProgramStageInstance( programStageInstanceB );

        Collection<MessageConversation> messages = programStageInstanceService.sendMessageConversations(
            programStageInstanceA, PatientReminder.SEND_WHEN_TO_C0MPLETED_EVENT, mockFormat );
        assertEquals( 1, messages.size() );
        assertEquals( "Test program stage message template", messages.iterator().next().getMessages().get( 0 )
            .getText() );
    }

    @Test
    public void testCompleteProgramStageInstance()
    {
        this.createSMSConfiguration();
        SmsConfiguration smsConfiguration = new SmsConfiguration();
        smsConfiguration.setEnabled( true );

        int idA = programStageInstanceService.addProgramStageInstance( programStageInstanceA );

        programStageInstanceService.completeProgramStageInstance( programStageInstanceA, mockFormat );
        assertEquals( true, programStageInstanceService.getProgramStageInstance( idA ).isCompleted() );
    }

    @Test
    public void testSetExecutionDate()
    {
        int idA = programStageInstanceService.addProgramStageInstance( programStageInstanceA );

        programStageInstanceService.setExecutionDate( programStageInstanceA, enrollmentDate, organisationUnitA );

        ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( idA );
        assertEquals( enrollmentDate, programStageInstance.getExecutionDate() );
        assertEquals( organisationUnitA, programStageInstance.getOrganisationUnit() );
    }

    @Test
    public void testCreateProgramStageInstance()
    {
        programA.setType( Program.SINGLE_EVENT_WITHOUT_REGISTRATION );
        programService.updateProgram( programA );

        ProgramStageInstance programStageInstance = programStageInstanceService.createProgramStageInstance( patientA,
            programA, enrollmentDate, organisationUnitA );

        assertNotNull( programStageInstanceService.getProgramStageInstance( programStageInstance.getUid() ) );
    }

    private void createSMSConfiguration()
    {
        BulkSmsGatewayConfig bulkGatewayConfig = new BulkSmsGatewayConfig();
        bulkGatewayConfig.setName( "bulksms" );
        bulkGatewayConfig.setPassword( "bulk" );
        bulkGatewayConfig.setUsername( "bulk" );
        bulkGatewayConfig.setRegion( "uk" );
        bulkGatewayConfig.setDefault( true );

        SmsConfiguration smsConfig = new SmsConfiguration();
        smsConfig.setPollingInterval( 3000 );
        smsConfig.getGateways().add( bulkGatewayConfig );
        smsConfig.setEnabled( true );
        smsConfigurationManager.updateSmsConfiguration( smsConfig );

    }

}