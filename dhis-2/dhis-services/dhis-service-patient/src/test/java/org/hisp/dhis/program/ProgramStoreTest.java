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

import java.util.Collection;
import java.util.HashSet;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.validation.ValidationCriteriaService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chau Thu Tran
 * 
 * @version $ ProgramStoreTest.java Nov 14, 2013 4:22:27 PM $
 */
public class ProgramStoreTest
    extends DhisSpringTest
{
    @Autowired
    private ProgramStore programStore;

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
    public void testGetProgramsByType()
    {
        programStore.save( programA );
        programStore.save( programB );

        programC.setType( Program.SINGLE_EVENT_WITH_REGISTRATION );
        programStore.save( programC );

        Collection<Program> programs = programStore.getByType( Program.MULTIPLE_EVENTS_WITH_REGISTRATION );
        assertTrue( equals( programs, programA, programB ) );

        programs = programStore.getByType( Program.SINGLE_EVENT_WITH_REGISTRATION );
        assertTrue( equals( programs, programC ) );
    }

    @Test
    public void testGetProgramsByTypeOu()
    {
        programStore.save( programA );
        programStore.save( programB );
        programStore.save( programC );

        Collection<Program> programs = programStore.get( Program.MULTIPLE_EVENTS_WITH_REGISTRATION, organisationUnitA );
        assertTrue( equals( programs, programA, programB ) );
    }

    @Test
    public void testGetProgramsByDisplayOnAllOrgunit()
    {
        programA.setDisplayOnAllOrgunit( true );
        programB.setDisplayOnAllOrgunit( true );
        programC.setDisplayOnAllOrgunit( false );

        programStore.save( programA );
        programStore.save( programB );
        programStore.save( programC );

        Collection<Program> programs = programStore.getProgramsByDisplayOnAllOrgunit( true, organisationUnitA );
        assertEquals( 2, programs.size() );
        assertTrue( programs.contains( programA ) );
        assertTrue( programs.contains( programB ) );

        programs = programStore.getProgramsByDisplayOnAllOrgunit( false, organisationUnitB );
        assertEquals( 1, programs.size() );
        assertTrue( programs.contains( programC ) );
    }
}