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

import org.hisp.dhis.DhisTest;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dxf2.events.person.Person;
import org.hisp.dhis.dxf2.events.person.PersonService;
import org.hisp.dhis.dxf2.importsummary.ImportStatus;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramStage;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class PersonServiceTest
    extends DhisTest
{
    @Autowired
    private PersonService personService;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private IdentifiableObjectManager manager;

    private Patient maleA;
    private Patient maleB;
    private Patient femaleA;
    private Patient femaleB;

    private OrganisationUnit organisationUnitA;
    private OrganisationUnit organisationUnitB;

    private Program programA;

    @Override
    protected void setUpTest() throws Exception
    {
        organisationUnitA = createOrganisationUnit( 'A' );
        organisationUnitB = createOrganisationUnit( 'B' );

        organisationUnitB.setParent( organisationUnitA );

        maleA = createPatient( 'A', organisationUnitA );
        maleB = createPatient( 'B', organisationUnitB );
        femaleA = createPatient( 'C', organisationUnitA );
        femaleB = createPatient( 'D', organisationUnitB );

        programA = createProgram( 'A', new HashSet<ProgramStage>(), organisationUnitA );
        manager.save( organisationUnitA );
        manager.save( organisationUnitB );
        manager.save( maleA );
        manager.save( maleB );
        manager.save( femaleA );
        manager.save( femaleB );
        manager.save( programA );

        programInstanceService.enrollPatient( maleA, programA, null, null, organisationUnitA, null );
        programInstanceService.enrollPatient( femaleA, programA, null, null, organisationUnitA, null );
    }

    @Override
    public boolean emptyDatabaseAfterTest()
    {
        return true;
    }

    @Test
    public void testGetPersons()
    {
        assertEquals( 4, personService.getPersons().getPersons().size() );
    }

    @Test
    public void testGetPersonByOrganisationUnit()
    {
        assertEquals( 2, personService.getPersons( organisationUnitA ).getPersons().size() );
        assertEquals( 2, personService.getPersons( organisationUnitB ).getPersons().size() );
    }

    @Test
    public void getPersonByPatients()
    {
        List<Patient> patients = Arrays.asList( maleA, femaleB );
        assertEquals( 2, personService.getPersons( patients ).getPersons().size() );
    }

    @Test
    public void getPersonByUid()
    {
        assertEquals( maleA.getUid(), personService.getPerson( maleA.getUid() ).getPerson() );
        assertEquals( femaleB.getUid(), personService.getPerson( femaleB.getUid() ).getPerson() );
        assertNotEquals( femaleA.getUid(), personService.getPerson( femaleB.getUid() ).getPerson() );
        assertNotEquals( maleA.getUid(), personService.getPerson( maleB.getUid() ).getPerson() );
    }

    @Test
    public void getPersonByPatient()
    {
        assertEquals( maleA.getUid(), personService.getPerson( maleA ).getPerson() );
        assertEquals( femaleB.getUid(), personService.getPerson( femaleB ).getPerson() );
        assertNotEquals( femaleA.getUid(), personService.getPerson( femaleB ).getPerson() );
        assertNotEquals( maleA.getUid(), personService.getPerson( maleB ).getPerson() );
    }

    @Test
    public void testGetPersonByProgram()
    {
        assertEquals( 2, personService.getPersons( programA ).getPersons().size() );
    }

    @Test
    @Ignore
    public void testUpdatePerson()
    {
        Person person = personService.getPerson( maleA.getUid() );
        // person.setName( "UPDATED_NAME" );

        ImportSummary importSummary = personService.updatePerson( person );
        assertEquals( ImportStatus.SUCCESS, importSummary.getStatus() );

        // assertEquals( "UPDATED_NAME", personService.getPerson( maleA.getUid() ).getName() );
    }

    @Test
    @Ignore
    public void testSavePerson()
    {
        Person person = new Person();
        // person.setName( "NAME" );
        person.setOrgUnit( organisationUnitA.getUid() );

        ImportSummary importSummary = personService.savePerson( person );
        assertEquals( ImportStatus.SUCCESS, importSummary.getStatus() );

        // assertEquals( "NAME", personService.getPerson( importSummary.getReference() ).getName() );
    }

    @Test
    public void testDeletePerson()
    {
        Person person = personService.getPerson( maleA.getUid() );
        personService.deletePerson( person );

        assertNull( personService.getPerson( maleA.getUid() ) );
        assertNotNull( personService.getPerson( maleB.getUid() ) );
    }
}
