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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.DateUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chau Thu Tran
 * 
 * @version $ ProgramIndicatorServiceTest.java Nov 13, 2013 1:34:55 PM $
 */
public class ProgramIndicatorServiceTest
    extends DhisSpringTest
{
    @Autowired
    private ProgramIndicatorService programIndicatorService;

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

    private Date incidenDate;

    private Date enrollmentDate;

    private Program programA;

    private Program programB;

    private ProgramInstance programInstance;

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

        Patient patient = createPatient( 'A', organisationUnit );
        patientService.savePatient( patient );

        Calendar calIncident = Calendar.getInstance();
        PeriodType.clearTimeOfDay( calIncident );
        calIncident.add( Calendar.DATE, -70 );
        incidenDate = calIncident.getTime();

        Calendar calEnrollment = Calendar.getInstance();
        PeriodType.clearTimeOfDay( calEnrollment );
        enrollmentDate = calEnrollment.getTime();

        programInstance = programInstanceService.enrollPatient( patient, programA, enrollmentDate, incidenDate,
            organisationUnit, null );

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
    public void testAddProgramIndicator()
    {
        int idA = programIndicatorService.addProgramIndicator( indicatorDate );
        int idB = programIndicatorService.addProgramIndicator( indicatorInt );

        assertNotNull( programIndicatorService.getProgramIndicator( idA ) );
        assertNotNull( programIndicatorService.getProgramIndicator( idB ) );
    }

    @Test
    public void testDeleteProgramIndicator()
    {
        int idA = programIndicatorService.addProgramIndicator( indicatorDate );
        int idB = programIndicatorService.addProgramIndicator( indicatorInt );

        assertNotNull( programIndicatorService.getProgramIndicator( idA ) );
        assertNotNull( programIndicatorService.getProgramIndicator( idB ) );

        programIndicatorService.deleteProgramIndicator( indicatorDate );

        assertNull( programIndicatorService.getProgramIndicator( idA ) );
        assertNotNull( programIndicatorService.getProgramIndicator( idB ) );

        programIndicatorService.deleteProgramIndicator( indicatorInt );

        assertNull( programIndicatorService.getProgramIndicator( idA ) );
        assertNull( programIndicatorService.getProgramIndicator( idB ) );
    }

    @Test
    public void testUpdateProgramIndicator()
    {
        int idA = programIndicatorService.addProgramIndicator( indicatorDate );

        assertNotNull( programIndicatorService.getProgramIndicator( idA ) );

        indicatorDate.setName( "B" );
        programIndicatorService.updateProgramIndicator( indicatorDate );

        assertEquals( "B", programIndicatorService.getProgramIndicator( idA ).getName() );
    }

    @Test
    public void testGetProgramIndicatorById()
    {
        int idA = programIndicatorService.addProgramIndicator( indicatorDate );
        int idB = programIndicatorService.addProgramIndicator( indicatorInt );

        assertEquals( indicatorDate, programIndicatorService.getProgramIndicator( idA ) );
        assertEquals( indicatorInt, programIndicatorService.getProgramIndicator( idB ) );
    }

    @Test
    public void testGetProgramIndicatorByName()
    {
        programIndicatorService.addProgramIndicator( indicatorDate );
        programIndicatorService.addProgramIndicator( indicatorInt );

        assertEquals( "IndicatorA", programIndicatorService.getProgramIndicator( "IndicatorA" ).getName() );
        assertEquals( "IndicatorB", programIndicatorService.getProgramIndicator( "IndicatorB" ).getName() );
    }

    @Test
    public void testGetAllProgramIndicators()
    {
        programIndicatorService.addProgramIndicator( indicatorDate );
        programIndicatorService.addProgramIndicator( indicatorInt );

        assertTrue( equals( programIndicatorService.getAllProgramIndicators(), indicatorDate, indicatorInt ) );
    }

    @Test
    public void testGetProgramIndicatorByShortName()
    {
        programIndicatorService.addProgramIndicator( indicatorDate );
        programIndicatorService.addProgramIndicator( indicatorInt );

        assertEquals( "INT", programIndicatorService.getProgramIndicatorByShortName( "INT" ).getShortName() );
        assertEquals( "DATE", programIndicatorService.getProgramIndicatorByShortName( "DATE" ).getShortName() );
    }

    @Test
    public void testGetProgramIndicatorByUid()
    {
        programIndicatorService.addProgramIndicator( indicatorDate );
        programIndicatorService.addProgramIndicator( indicatorInt );

        assertEquals( "UID-INT", programIndicatorService.getProgramIndicatorByUid( "UID-INT" ).getUid() );
        assertEquals( "UID-DATE", programIndicatorService.getProgramIndicatorByUid( "UID-DATE" ).getUid() );
    }

    @Test
    public void testGetProgramIndicatorsByProgram()
    {
        programIndicatorService.addProgramIndicator( indicatorDate );
        programIndicatorService.addProgramIndicator( indicatorInt );
        programIndicatorService.addProgramIndicator( indicatorC );

        Collection<ProgramIndicator> indicators = programIndicatorService.getProgramIndicators( programA );
        assertEquals( 2, indicators.size() );
        assertTrue( indicators.contains( indicatorDate ) );
        assertTrue( indicators.contains( indicatorInt ) );

        indicators = programIndicatorService.getProgramIndicators( programB );
        assertEquals( 1, indicators.size() );
        assertTrue( indicators.contains( indicatorC ) );

    }

    @Test
    public void testGetProgramIndicatorValue()
    {
        programIndicatorService.addProgramIndicator( indicatorDate );
        programIndicatorService.addProgramIndicator( indicatorInt );

        String valueINT = programIndicatorService.getProgramIndicatorValue( programInstance, indicatorDate );
        assertEquals( "10.0", valueINT );

        String valueLDATE = programIndicatorService.getProgramIndicatorValue( programInstance, indicatorInt );
        assertEquals( DateUtils.getMediumDateString( enrollmentDate ), valueLDATE );
    }

    @Test
    public void testGetProgramIndicatorValues()
    {
        programIndicatorService.addProgramIndicator( indicatorDate );
        programIndicatorService.addProgramIndicator( indicatorInt );

        Map<String, String> indicatorMap = programIndicatorService.getProgramIndicatorValues( programInstance );
        assertEquals( 2, indicatorMap.keySet().size() );
        assertEquals( "10.0", indicatorMap.get( "IndicatorA" ) );
        assertEquals( DateUtils.getMediumDateString( enrollmentDate ), indicatorMap.get( "IndicatorB" ) );

    }
}
