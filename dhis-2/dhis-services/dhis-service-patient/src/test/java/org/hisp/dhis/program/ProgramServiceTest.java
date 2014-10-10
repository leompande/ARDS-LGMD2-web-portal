package org.hisp.dhis.program;

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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.validation.ValidationCriteria;
import org.hisp.dhis.validation.ValidationCriteriaService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class ProgramServiceTest
    extends DhisSpringTest
{
    @Autowired
    private ProgramService programService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private ValidationCriteriaService validationCriteriaService;

    private OrganisationUnit organisationUnitA;

    private OrganisationUnit organisationUnitB;

    private Program programA;

    private Program programB;

    private Program programC;

    @Override
    public void setUpTest()
    {
        organisationUnitA = createOrganisationUnit( 'A' );
        organisationUnitService.addOrganisationUnit( organisationUnitA );

        organisationUnitB = createOrganisationUnit( 'B' );
        organisationUnitService.addOrganisationUnit( organisationUnitB );

        programA = createProgram( 'A', new HashSet<ProgramStage>(), organisationUnitA );
        programA.setUid( "UID-A" );

        programB = createProgram( 'B', new HashSet<ProgramStage>(), organisationUnitA );
        programB.setUid( "UID-B" );

        programC = createProgram( 'C', new HashSet<ProgramStage>(), organisationUnitB );
        programC.setUid( "UID-C" );
    }

    @Test
    public void testAddProgram()
    {
        int idA = programService.addProgram( programA );
        int idB = programService.addProgram( programB );

        assertNotNull( programService.getProgram( idA ) );
        assertNotNull( programService.getProgram( idB ) );
    }

    @Test
    public void testUpdateProgram()
    {
        int idA = programService.addProgram( programA );

        assertNotNull( programService.getProgram( idA ) );

        programA.setName( "B" );
        programService.updateProgram( programA );

        assertEquals( "B", programService.getProgram( idA ).getName() );
    }

    @Test
    public void testDeleteProgram()
    {
        int idA = programService.addProgram( programA );
        int idB = programService.addProgram( programB );

        assertNotNull( programService.getProgram( idA ) );
        assertNotNull( programService.getProgram( idB ) );

        programService.deleteProgram( programA );

        assertNull( programService.getProgram( idA ) );
        assertNotNull( programService.getProgram( idB ) );

        programService.deleteProgram( programB );

        assertNull( programService.getProgram( idA ) );
        assertNull( programService.getProgram( idB ) );
    }

    @Test
    public void testGetProgramById()
    {
        int idA = programService.addProgram( programA );
        int idB = programService.addProgram( programB );

        assertEquals( programA, programService.getProgram( idA ) );
        assertEquals( programB, programService.getProgram( idB ) );
    }

    @Test
    public void testGetProgramByName()
    {
        int idA = programService.addProgram( programA );

        assertNotNull( programService.getProgram( idA ) );
        assertEquals( "ProgramA", programService.getProgramByName( "ProgramA" ).getName() );
    }

    @Test
    public void testGetAllPrograms()
    {
        programService.addProgram( programA );
        programService.addProgram( programB );

        assertTrue( equals( programService.getAllPrograms(), programA, programB ) );
    }

    @Test
    public void testGetProgramsByOu()
    {
        programService.addProgram( programA );
        programService.addProgram( programB );
        programService.addProgram( programC );

        Collection<Program> programs = programService.getPrograms( organisationUnitA );
        assertTrue( equals( programs, programA, programB ) );

        programs = programService.getPrograms( organisationUnitB );
        assertTrue( equals( programs, programC ) );
    }

    @Test
    public void testGetProgramsByCriteria()
    {
        programService.addProgram( programA );
        programService.addProgram( programB );

        ValidationCriteria validationCriteria = createValidationCriteria( 'A', "gender", 0, "F" );
        validationCriteriaService.saveValidationCriteria( validationCriteria );

        programA.getPatientValidationCriteria().add( validationCriteria );
        programService.updateProgram( programA );

        programB.getPatientValidationCriteria().add( validationCriteria );
        programService.updateProgram( programB );

        Collection<Program> programs = programService.getPrograms( validationCriteria );
        assertEquals( 2, programs.size() );
        assertTrue( programs.contains( programA ) );
        assertTrue( programs.contains( programB ) );
    }

    @Test
    public void testGetProgramsByType()
    {
        programService.addProgram( programA );
        programService.addProgram( programB );

        programC.setType( Program.SINGLE_EVENT_WITH_REGISTRATION );
        programService.addProgram( programC );

        Collection<Program> programs = programService.getPrograms( Program.MULTIPLE_EVENTS_WITH_REGISTRATION );
        assertTrue( equals( programs, programA, programB ) );

        programs = programService.getPrograms( Program.SINGLE_EVENT_WITH_REGISTRATION );
        assertTrue( equals( programs, programC ) );
    }

    @Test
    public void testGetProgramsByTypeOu()
    {
        programService.addProgram( programA );
        programService.addProgram( programB );
        programService.addProgram( programC );

        Collection<Program> programs = programService.getPrograms( Program.MULTIPLE_EVENTS_WITH_REGISTRATION,
            organisationUnitA );
        assertTrue( equals( programs, programA, programB ) );
    }

    @Test
    public void testGetProgramByUid()
    {
        programService.addProgram( programA );
        programService.addProgram( programB );

        assertEquals( programA, programService.getProgram( "UID-A" ) );
        assertEquals( programB, programService.getProgram( "UID-B" ) );
    }

    @Test
    public void testGetProgramsByDisplayOnAllOrgunit()
    {
        programA.setDisplayOnAllOrgunit( true );
        programB.setDisplayOnAllOrgunit( true );
        programC.setDisplayOnAllOrgunit( false );

        programService.addProgram( programA );
        programService.addProgram( programB );
        programService.addProgram( programC );

        Collection<Program> programs = programService.getProgramsByDisplayOnAllOrgunit( true, organisationUnitA );
        assertEquals( 2, programs.size() );
        assertTrue( programs.contains( programA ) );
        assertTrue( programs.contains( programB ) );

        programs = programService.getProgramsByDisplayOnAllOrgunit( false, organisationUnitB );
        assertEquals( 1, programs.size() );
        assertTrue( programs.contains( programC ) );
    }
}
