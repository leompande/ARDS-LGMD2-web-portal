package org.hisp.dhis.dxf2.events;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

import java.util.HashSet;

import org.hamcrest.CoreMatchers;
import org.hisp.dhis.DhisTest;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dxf2.events.enrollment.Enrollment;
import org.hisp.dhis.dxf2.events.enrollment.EnrollmentService;
import org.hisp.dhis.dxf2.events.event.DataValue;
import org.hisp.dhis.dxf2.events.event.Event;
import org.hisp.dhis.dxf2.events.event.EventService;
import org.hisp.dhis.dxf2.events.event.EventStatus;
import org.hisp.dhis.dxf2.events.person.Person;
import org.hisp.dhis.dxf2.events.person.PersonService;
import org.hisp.dhis.dxf2.importsummary.ImportStatus;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageDataElementService;
import org.hisp.dhis.user.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class RegistrationSingleEventServiceTest
    extends DhisTest
{
    @Autowired
    private EventService eventService;

    @Autowired
    private PersonService personService;

    @Autowired
    private ProgramStageDataElementService programStageDataElementService;

    @Autowired
    private EnrollmentService enrollmentService;

    private Patient maleA;
    private Patient maleB;
    private Patient femaleA;
    private Patient femaleB;

    private Person personMaleA;
    private Person personMaleB;
    private Person personFemaleA;
    private Person personFemaleB;

    private OrganisationUnit organisationUnitA;
    private OrganisationUnit organisationUnitB;
    private DataElement dataElementA;
    private Program programA;
    private ProgramStage programStageA;

    @Override
    protected void setUpTest() throws Exception
    {
        identifiableObjectManager = (IdentifiableObjectManager) getBean( IdentifiableObjectManager.ID );
        userService = (UserService) getBean( UserService.ID );
        
        organisationUnitA = createOrganisationUnit( 'A' );
        organisationUnitB = createOrganisationUnit( 'B' );
        identifiableObjectManager.save( organisationUnitA );
        identifiableObjectManager.save( organisationUnitB );

        maleA = createPatient( 'A',  organisationUnitA );
        maleB = createPatient( 'B',  organisationUnitB );
        femaleA = createPatient( 'C',  organisationUnitA );
        femaleB = createPatient( 'D', organisationUnitB );

        identifiableObjectManager.save( maleA );
        identifiableObjectManager.save( maleB );
        identifiableObjectManager.save( femaleA );
        identifiableObjectManager.save( femaleB );

        personMaleA = personService.getPerson( maleA );
        personMaleB = personService.getPerson( maleB );
        personFemaleA = personService.getPerson( femaleA );
        personFemaleB = personService.getPerson( femaleB );

        dataElementA = createDataElement( 'A' );
        dataElementA.setType( DataElement.VALUE_TYPE_INT );
        identifiableObjectManager.save( dataElementA );

        programStageA = createProgramStage( 'A', 0 );
        identifiableObjectManager.save( programStageA );

        programA = createProgram( 'A', new HashSet<ProgramStage>(), organisationUnitA );
        programA.setType( Program.SINGLE_EVENT_WITH_REGISTRATION );
        identifiableObjectManager.save( programA );

        ProgramStageDataElement programStageDataElement = new ProgramStageDataElement();
        programStageDataElement.setDataElement( dataElementA );
        programStageDataElement.setProgramStage( programStageA );
        programStageDataElementService.addProgramStageDataElement( programStageDataElement );

        programStageA.getProgramStageDataElements().add( programStageDataElement );
        programStageA.setProgram( programA );
        programA.getProgramStages().add( programStageA );

        identifiableObjectManager.update( programStageA );
        identifiableObjectManager.update( programA );

        createUserAndInjectSecurityContext( true );
    }

    @Override
    public boolean emptyDatabaseAfterTest()
    {
        return true;
    }

    @Test
    public void testSaveWithoutEnrollmentShouldFail()
    {
        Event event = createEvent( programA.getUid(), organisationUnitA.getUid(), personMaleA.getPerson() );
        ImportSummary importSummary = eventService.saveEvent( event );
        assertEquals( ImportStatus.ERROR, importSummary.getStatus() );
        assertThat( importSummary.getDescription(), CoreMatchers.containsString( "is not enrolled in program" ) );
    }

    @Test
    public void testSaveWithEnrollmentShouldNotFail()
    {
        Enrollment enrollment = createEnrollment( programA.getUid(), personMaleA.getPerson() );
        ImportSummary importSummary = enrollmentService.saveEnrollment( enrollment );
        assertEquals( ImportStatus.SUCCESS, importSummary.getStatus() );

        Event event = createEvent( programA.getUid(), organisationUnitA.getUid(), personMaleA.getPerson() );
        importSummary = eventService.saveEvent( event );
        assertEquals( ImportStatus.SUCCESS, importSummary.getStatus() );
    }

    @Test
    public void testSavingMultipleEventsShouldOnlyUpdate()
    {
        Enrollment enrollment = createEnrollment( programA.getUid(), personMaleA.getPerson() );
        ImportSummary importSummary = enrollmentService.saveEnrollment( enrollment );
        assertEquals( ImportStatus.SUCCESS, importSummary.getStatus() );

        Event event = createEvent( programA.getUid(), organisationUnitA.getUid(), personMaleA.getPerson() );
        importSummary = eventService.saveEvent( event );
        assertEquals( ImportStatus.SUCCESS, importSummary.getStatus() );

        assertEquals( 1, eventService.getEvents( programA, organisationUnitA ).getEvents().size() );

        event = createEvent( programA.getUid(), organisationUnitA.getUid(), personMaleA.getPerson() );
        importSummary = eventService.saveEvent( event );
        assertEquals( ImportStatus.SUCCESS, importSummary.getStatus() );

        assertEquals( 1, eventService.getEvents( programA, organisationUnitA ).getEvents().size() );

        event = createEvent( programA.getUid(), organisationUnitA.getUid(), personMaleA.getPerson() );
        importSummary = eventService.saveEvent( event );
        assertEquals( ImportStatus.SUCCESS, importSummary.getStatus() );

        assertEquals( 1, eventService.getEvents( programA, organisationUnitA ).getEvents().size() );
    }

    @Test
    public void testMultipleEnrollmentsWithEventShouldGiveDifferentUIDs()
    {
        Enrollment enrollment = createEnrollment( programA.getUid(), personMaleA.getPerson() );
        enrollmentService.saveEnrollment( enrollment );

        Event event = createEvent( programA.getUid(), organisationUnitA.getUid(), personMaleA.getPerson() );
        event.setStatus( EventStatus.COMPLETED );
        ImportSummary importSummary1 = eventService.saveEvent( event );
        assertEquals( ImportStatus.SUCCESS, importSummary1.getStatus() );
        enrollment = enrollmentService.getEnrollments( personMaleA ).getEnrollments().get( 0 );
        enrollmentService.completeEnrollment( enrollment );

        enrollment = createEnrollment( programA.getUid(), personMaleA.getPerson() );
        enrollmentService.saveEnrollment( enrollment );

        event = createEvent( programA.getUid(), organisationUnitA.getUid(), personMaleA.getPerson() );
        event.setStatus( EventStatus.COMPLETED );
        ImportSummary importSummary2 = eventService.saveEvent( event );
        assertEquals( ImportStatus.SUCCESS, importSummary2.getStatus() );
        enrollment = enrollmentService.getEnrollments( personMaleA ).getEnrollments().get( 0 );
        enrollmentService.completeEnrollment( enrollment );

        assertNotEquals( importSummary1.getReference(), importSummary2.getReference() );
    }

    private Enrollment createEnrollment( String program, String person )
    {
        Enrollment enrollment = new Enrollment();
        enrollment.setProgram( program );
        enrollment.setPerson( person );

        return enrollment;
    }

    private Event createEvent( String program, String orgUnit, String person )
    {
        Event event = new Event();
        event.setProgram( program );
        event.setOrgUnit( orgUnit );
        event.setPerson( person );
        event.setEventDate( "2013-01-01" );

        event.getDataValues().add( new DataValue( dataElementA.getUid(), "10" ) );

        return event;
    }
}
