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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chau Thu Tran
 * 
 * @version $ ProgramStageSectionServiceTest.java Nov 14, 2013 4:22:27 PM $
 */
public class ProgramStageSectionServiceTest
    extends DhisSpringTest
{
    @Autowired
    private ProgramStageSectionService programStageSectionService;

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
    private ProgramStageDataElementService programStageDataElementService;

    private OrganisationUnit organisationUnit;

    private ProgramStage stageA;

    private ProgramStage stageB;

    private ProgramStageSection sectionA;

    private ProgramStageSection sectionB;

    @Override
    public void setUpTest()
    {
        organisationUnit = createOrganisationUnit( 'A' );
        organisationUnitService.addOrganisationUnit( organisationUnit );

        Program program = createProgram( 'A', new HashSet<ProgramStage>(), organisationUnit );
        programService.addProgram( program );

        stageA = new ProgramStage( "A", program );
        programStageService.saveProgramStage( stageA );

        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );

        dataElementService.addDataElement( dataElementA );
        dataElementService.addDataElement( dataElementB );

        ProgramStageDataElement stageDeA = new ProgramStageDataElement( stageA, dataElementA, false, 1 );
        ProgramStageDataElement stageDeB = new ProgramStageDataElement( stageA, dataElementB, false, 2 );

        programStageDataElementService.addProgramStageDataElement( stageDeA );
        programStageDataElementService.addProgramStageDataElement( stageDeB );

        List<ProgramStageDataElement> psDataElements = new ArrayList<ProgramStageDataElement>();
        psDataElements.add( stageDeA );
        psDataElements.add( stageDeB );

        stageB = new ProgramStage( "B", program );
        programStageService.saveProgramStage( stageB );

        Set<ProgramStage> programStages = new HashSet<ProgramStage>();
        programStages.add( stageA );
        programStages.add( stageB );
        program.setProgramStages( programStages );
        programService.updateProgram( program );

        sectionA = new ProgramStageSection();
        sectionA.setName( "A" );
        sectionA.setProgramStageDataElements( psDataElements );
        sectionA.setSortOrder( 1 );

        sectionB = new ProgramStageSection();
        sectionB.setName( "B" );
        sectionB.setSortOrder( 2 );

        Set<ProgramStageSection> sections = new HashSet<ProgramStageSection>();
        sections.add( sectionA );
        sections.add( sectionB );
        stageA.setProgramStageSections( sections );
    }

    @Test
    public void testSaveProgramStageSection()
    {
        int idA = programStageSectionService.saveProgramStageSection( sectionA );
        int idB = programStageSectionService.saveProgramStageSection( sectionB );

        assertNotNull( programStageSectionService.getProgramStageSection( idA ) );
        assertNotNull( programStageSectionService.getProgramStageSection( idB ) );
    }

    @Test
    public void testDeleteProgramStageSection()
    {
        int idA = programStageSectionService.saveProgramStageSection( sectionA );
        int idB = programStageSectionService.saveProgramStageSection( sectionB );

        assertNotNull( programStageSectionService.getProgramStageSection( idA ) );
        assertNotNull( programStageSectionService.getProgramStageSection( idB ) );

        programStageSectionService.deleteProgramStageSection( sectionA );

        assertNull( programStageSectionService.getProgramStageSection( idA ) );
        assertNotNull( programStageSectionService.getProgramStageSection( idB ) );

        programStageSectionService.deleteProgramStageSection( sectionB );

        assertNull( programStageSectionService.getProgramStageSection( idA ) );
        assertNull( programStageSectionService.getProgramStageSection( idB ) );
    }

    @Test
    public void testUpdateProgramStageSection()
    {
        int idA = programStageSectionService.saveProgramStageSection( sectionA );

        assertNotNull( programStageSectionService.getProgramStageSection( idA ) );

        sectionA.setName( "B" );
        programStageSectionService.updateProgramStageSection( sectionA );

        assertEquals( "B", programStageSectionService.getProgramStageSection( idA ).getName() );
    }

    @Test
    public void testGetProgramStageSectionById()
    {
        int idA = programStageSectionService.saveProgramStageSection( sectionA );
        int idB = programStageSectionService.saveProgramStageSection( sectionB );

        assertEquals( sectionA, programStageSectionService.getProgramStageSection( idA ) );
        assertEquals( sectionB, programStageSectionService.getProgramStageSection( idB ) );
    }

    @Test
    public void testGetProgramStageSectionListByName()
    {
        programStageService.updateProgramStage( stageA );

        List<ProgramStageSection> sections = programStageSectionService.getProgramStageSectionByName( "A" );
        assertEquals( 1, sections.size() );
        assertTrue( sections.contains( sectionA ) );
    }

    @Test
    public void testGetProgramStageSectionByNameStage()
    {
        programStageService.updateProgramStage( stageA );
        assertNotNull( programStageSectionService.getProgramStageSectionByName( "A", stageA ) );
    }

    @Test
    public void testGetAllProgramStageSections()
    {
        programStageService.updateProgramStage( stageA );
        Set<ProgramStageSection> sections = stageA.getProgramStageSections();

        assertTrue( equals( sections, sectionA, sectionB ) );
    }

}