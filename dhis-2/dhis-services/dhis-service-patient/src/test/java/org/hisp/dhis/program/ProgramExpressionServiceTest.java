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

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataentryform.DataEntryFormService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chau Thu Tran
 * @version $Id$
 */
public class ProgramExpressionServiceTest
    extends DhisSpringTest
{
    @Autowired
    private ProgramExpressionService programExpressionService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private ProgramStageDataElementService programStageDataElementService;

    @Autowired
    private DataEntryFormService dataEntryFormService;

    @Autowired
    private ProgramStageService programStageService;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private ProgramStageInstanceService programStageInstanceService;

    @Autowired
    private PatientDataValueService patientDataValueService;

    private ProgramExpression programExpressionA;

    private ProgramExpression programExpressionB;

    private ProgramStageInstance stageInstance;

    private int stageAId;

    private int deAId;

    private int deBId;

    @Override
    public void setUpTest()
    {
        OrganisationUnit organisationUnit = createOrganisationUnit( 'A' );
        organisationUnitService.addOrganisationUnit( organisationUnit );

        Program program = createProgram( 'A', new HashSet<ProgramStage>(), organisationUnit );
        programService.addProgram( program );

        ProgramStage stageA = new ProgramStage( "StageA", program );
        stageAId = programStageService.saveProgramStage( stageA );

        ProgramStage stageB = new ProgramStage( "StageB", program );
        programStageService.saveProgramStage( stageB );

        Set<ProgramStage> programStages = new HashSet<ProgramStage>();
        programStages.add( stageA );
        programStages.add( stageB );
        program.setProgramStages( programStages );
        programService.updateProgram( program );

        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );

        deAId = dataElementService.addDataElement( dataElementA );
        deBId = dataElementService.addDataElement( dataElementB );

        Patient patient = createPatient( 'A', organisationUnit );
        patientService.savePatient( patient );

        ProgramInstance programInstance = programInstanceService.enrollPatient( patient, program, new Date(),
            new Date(), organisationUnit, null );
        stageInstance = programStageInstanceService.getProgramStageInstance( programInstance, stageA );

        PatientDataValue dataValueA = new PatientDataValue( stageInstance, dataElementA, "1" );
        PatientDataValue dataValueB = new PatientDataValue( stageInstance, dataElementB, "2" );

        patientDataValueService.savePatientDataValue( dataValueA );
        patientDataValueService.savePatientDataValue( dataValueB );

        programExpressionA = new ProgramExpression( "[" + ProgramExpression.OBJECT_PROGRAM_STAGE_DATAELEMENT
            + ProgramExpression.SEPARATOR_OBJECT + stageAId + "." + deAId + "]", "A" );
        programExpressionB = new ProgramExpression( "[" + ProgramExpression.OBJECT_PROGRAM_STAGE_DATAELEMENT
            + ProgramExpression.SEPARATOR_OBJECT + stageAId + "." + deBId + "]", "B" );
    }

    @Test
    public void tesAddProgramExpression()
    {
        int idA = programExpressionService.addProgramExpression( programExpressionA );
        int idB = programExpressionService.addProgramExpression( programExpressionB );

        assertNotNull( programExpressionService.getProgramExpression( idA ) );
        assertNotNull( programExpressionService.getProgramExpression( idB ) );
    }

    @Test
    public void tesUpdateProgramExpression()
    {
        int idA = programExpressionService.addProgramExpression( programExpressionA );

        assertNotNull( programExpressionService.getProgramExpression( idA ) );

        programExpressionA.setDescription( "B" );
        programExpressionService.updateProgramExpression( programExpressionA );

        assertEquals( "B", programExpressionService.getProgramExpression( idA ).getDescription() );
    }

    @Test
    public void testDeleteProgramExpression()
    {
        int idA = programExpressionService.addProgramExpression( programExpressionA );
        int idB = programExpressionService.addProgramExpression( programExpressionB );

        assertNotNull( programExpressionService.getProgramExpression( idA ) );
        assertNotNull( programExpressionService.getProgramExpression( idB ) );

        programExpressionService.deleteProgramExpression( programExpressionA );

        assertNull( programExpressionService.getProgramExpression( idA ) );
        assertNotNull( programExpressionService.getProgramExpression( idB ) );

        programExpressionService.deleteProgramExpression( programExpressionB );

        assertNull( programExpressionService.getProgramExpression( idA ) );
        assertNull( programExpressionService.getProgramExpression( idB ) );
    }

    @Test
    public void tesGetProgramExpression()
    {
        int idA = programExpressionService.addProgramExpression( programExpressionA );
        int idB = programExpressionService.addProgramExpression( programExpressionB );

        assertEquals( programExpressionA, programExpressionService.getProgramExpression( idA ) );
        assertEquals( programExpressionB, programExpressionService.getProgramExpression( idB ) );
    }

    @Test
    public void tesGetAllProgramExpressions()
    {
        programExpressionService.addProgramExpression( programExpressionA );
        programExpressionService.addProgramExpression( programExpressionB );

        assertTrue( equals( programExpressionService.getAllProgramExpressions(), programExpressionA, programExpressionB ) );
    }

    @Test
    public void tesGetProgramExpressionValue()
    {
        programExpressionService.addProgramExpression( programExpressionA );

        Map<String, String> dataValueMap = new HashMap<String, String>();
        dataValueMap.put( stageAId + "." + deAId, "1" );
        dataValueMap.put( stageAId + "." + deBId, "2" );

        String value = programExpressionService.getProgramExpressionValue( programExpressionA, stageInstance,
            dataValueMap );

        assertEquals( "1", value );
    }

    @Test
    public void tesGetExpressionDescription()
    {
        programExpressionService.addProgramExpression( programExpressionA );

        String actual = programExpressionService.getExpressionDescription( programExpressionA.getExpression() );
        String expected = "[" + ProgramExpression.OBJECT_PROGRAM_STAGE_DATAELEMENT + ProgramExpression.SEPARATOR_OBJECT
            + "StageA.DataElementA]";
        assertEquals( expected, actual );
    }
}
