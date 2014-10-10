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
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chau Thu Tran
 * 
 * @version $ ProgramIndicatorStoreTest.java Nov 13, 2013 1:34:55 PM $
 */
public class ProgramIndicatorStoreTest
    extends DhisSpringTest
{
    @Autowired
    private ProgramIndicatorStore programIndicatorStore;

    @Autowired
    private PatientService patientService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private ProgramStageService programStageService;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private PatientDataValueService patientDataValueService;

    private Program programA;

    private Program programB;

    private ProgramIndicator indicatorDate;

    private ProgramIndicator indicatorInt;

    private ProgramIndicator indicatorC;

    @Override
    public void setUpTest()
    {
        OrganisationUnit organisationUnit = createOrganisationUnit( 'A' );
        organisationUnitService.addOrganisationUnit( organisationUnit );

        programA = createProgram( 'A', new HashSet<ProgramStage>(), organisationUnit );
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

        programB = createProgram( 'B', new HashSet<ProgramStage>(), organisationUnit );
        programService.addProgram( programB );

        indicatorDate = new ProgramIndicator( "IndicatorA", "IndicatorDesA", ProgramIndicator.VALUE_TYPE_INT, "( "
            + ProgramIndicator.INCIDENT_DATE + " - " + ProgramIndicator.ENROLLEMENT_DATE + " )  / 7" );
        indicatorDate.setUid( "UID-DATE" );
        indicatorDate.setShortName( "DATE" );
        indicatorDate.setProgram( programA );

        indicatorInt = new ProgramIndicator( "IndicatorB", "IndicatorDesB", ProgramIndicator.VALUE_TYPE_DATE, "70" );
        indicatorInt.setRootDate( ProgramIndicator.INCIDENT_DATE );
        indicatorInt.setUid( "UID-INT" );
        indicatorInt.setShortName( "INT" );
        indicatorInt.setProgram( programA );

        indicatorC = new ProgramIndicator( "IndicatorC", "IndicatorDesB", ProgramIndicator.VALUE_TYPE_INT, "0" );
        indicatorC.setUid( "UID-C" );
        indicatorC.setShortName( "C" );
        indicatorC.setProgram( programB );
    }

    @Test
    public void testGetProgramIndicatorsByProgram()
    {
        programIndicatorStore.save( indicatorDate );
        programIndicatorStore.save( indicatorInt );
        programIndicatorStore.save( indicatorC );

        Collection<ProgramIndicator> indicators = programIndicatorStore.getByProgram( programA );
        assertEquals( 2, indicators.size() );
        assertTrue( indicators.contains( indicatorDate ) );
        assertTrue( indicators.contains( indicatorInt ) );

        indicators = programIndicatorStore.getByProgram( programB );
        assertEquals( 1, indicators.size() );
        assertTrue( indicators.contains( indicatorC ) );

    }
}
