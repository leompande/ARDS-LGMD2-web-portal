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

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.expression.Operator;
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
 * 
 * @version $ ProgramValidationServiceTest.java Nov 5, 2013 3:11:48 PM $
 */
public class ProgramValidationServiceTest
    extends DhisSpringTest
{
    @Autowired
    private ProgramValidationService programValidationService;

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
    private PatientDataValueService patientDataValueService;

    private Program program;

    private ProgramStage stageA;

    private ProgramStage stageB;

    private ProgramStageDataElement stageDataElementA;

    private ProgramStageDataElement stageDataElementC;

    private ProgramStageInstance stageInstanceA;

    private ProgramStageInstance stageInstanceB;

    private ProgramValidation validationA;

    private ProgramValidation validationB;

    @Override
    public void setUpTest()
    {
        OrganisationUnit organisationUnit = createOrganisationUnit( 'A' );
        organisationUnitService.addOrganisationUnit( organisationUnit );

        program = createProgram( 'A', new HashSet<ProgramStage>(), organisationUnit );
        programService.addProgram( program );

        stageA = new ProgramStage( "A", program );
        int psIdA = programStageService.saveProgramStage( stageA );

        stageB = new ProgramStage( "B", program );
        int psIdB = programStageService.saveProgramStage( stageB );

        Set<ProgramStage> programStages = new HashSet<ProgramStage>();
        programStages.add( stageA );
        programStages.add( stageB );
        program.setProgramStages( programStages );
        programService.updateProgram( program );

        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );

        int deIdA = dataElementService.addDataElement( dataElementA );
        int deIdB = dataElementService.addDataElement( dataElementB );

        stageDataElementA = new ProgramStageDataElement( stageA, dataElementA, false, 1 );
        ProgramStageDataElement stageDataElementB = new ProgramStageDataElement( stageA, dataElementB, false, 2 );
        stageDataElementC = new ProgramStageDataElement( stageB, dataElementA, false, 1 );
        ProgramStageDataElement stageDataElementD = new ProgramStageDataElement( stageB, dataElementB, false, 2 );

        programStageDataElementService.addProgramStageDataElement( stageDataElementA );
        programStageDataElementService.addProgramStageDataElement( stageDataElementB );
        programStageDataElementService.addProgramStageDataElement( stageDataElementC );
        programStageDataElementService.addProgramStageDataElement( stageDataElementD );

        Patient patient = createPatient( 'A', organisationUnit );
        patientService.savePatient( patient );

        ProgramInstance programInstance = programInstanceService.enrollPatient( patient, program, new Date(),
            new Date(), organisationUnit, null );

        stageInstanceA = programStageInstanceService.getProgramStageInstance( programInstance, stageA );
        stageInstanceB = programStageInstanceService.getProgramStageInstance( programInstance, stageB );

        Set<ProgramStageInstance> programStageInstances = new HashSet<ProgramStageInstance>();
        programStageInstances.add( stageInstanceA );
        programStageInstances.add( stageInstanceB );
        programInstance.setProgramStageInstances( programStageInstances );

        PatientDataValue dataValueA = new PatientDataValue( stageInstanceA, dataElementA, "1" );
        PatientDataValue dataValueB = new PatientDataValue( stageInstanceA, dataElementB, "1" );
        PatientDataValue dataValueC = new PatientDataValue( stageInstanceB, dataElementA, "2" );
        PatientDataValue dataValueD = new PatientDataValue( stageInstanceB, dataElementB, "3" );

        patientDataValueService.savePatientDataValue( dataValueA );
        patientDataValueService.savePatientDataValue( dataValueB );
        patientDataValueService.savePatientDataValue( dataValueC );
        patientDataValueService.savePatientDataValue( dataValueD );

        ProgramExpression programExpressionA = new ProgramExpression( "["
            + ProgramExpression.OBJECT_PROGRAM_STAGE_DATAELEMENT + ProgramExpression.SEPARATOR_OBJECT + psIdA + "."
            + deIdA + "]", "A" );
        ProgramExpression programExpressionB = new ProgramExpression( "["
            + ProgramExpression.OBJECT_PROGRAM_STAGE_DATAELEMENT + ProgramExpression.SEPARATOR_OBJECT + psIdA + "."
            + deIdB + "]", "B" );

        ProgramExpression programExpressionC = new ProgramExpression( "["
            + ProgramExpression.OBJECT_PROGRAM_STAGE_DATAELEMENT + ProgramExpression.SEPARATOR_OBJECT + psIdB + "."
            + deIdA + "]", "C" );
        ProgramExpression programExpressionD = new ProgramExpression( "["
            + ProgramExpression.OBJECT_PROGRAM_STAGE_DATAELEMENT + ProgramExpression.SEPARATOR_OBJECT + psIdB + "."
            + deIdB + "]", "D" );

        validationA = new ProgramValidation( "A", programExpressionA, programExpressionB, program );
        validationA.setOperator( Operator.valueOf( "equal_to" ) );

        validationB = new ProgramValidation( "B", programExpressionC, programExpressionD, program );
        validationB.setOperator( Operator.valueOf( "greater_than" ) );
    }

    @Test
    public void testAddProgramValidation()
    {
        int idA = programValidationService.addProgramValidation( validationA );
        int idB = programValidationService.addProgramValidation( validationB );

        assertNotNull( programValidationService.getProgramValidation( idA ) );
        assertNotNull( programValidationService.getProgramValidation( idB ) );
    }

    @Test
    public void testDeleteProgramValidation()
    {
        int idA = programValidationService.addProgramValidation( validationA );
        int idB = programValidationService.addProgramValidation( validationB );

        assertNotNull( programValidationService.getProgramValidation( idA ) );
        assertNotNull( programValidationService.getProgramValidation( idB ) );

        programValidationService.deleteProgramValidation( validationA );

        assertNull( programValidationService.getProgramValidation( idA ) );
        assertNotNull( programValidationService.getProgramValidation( idB ) );

        programValidationService.deleteProgramValidation( validationB );

        assertNull( programValidationService.getProgramValidation( idA ) );
        assertNull( programValidationService.getProgramValidation( idB ) );
    }

    @Test
    public void testUpdateProgramValidation()
    {
        int idA = programValidationService.addProgramValidation( validationA );

        assertNotNull( programValidationService.getProgramValidation( idA ) );

        validationA.setName( "B" );
        programValidationService.updateProgramValidation( validationA );

        assertEquals( "B", programValidationService.getProgramValidation( idA ).getName() );
    }

    @Test
    public void testGetPatientvalidationById()
    {
        int idA = programValidationService.addProgramValidation( validationA );
        int idB = programValidationService.addProgramValidation( validationB );

        assertEquals( validationA, programValidationService.getProgramValidation( idA ) );
        assertEquals( validationB, programValidationService.getProgramValidation( idB ) );
    }

    @Test
    public void testGetAllProgramValidations()
    {
        programValidationService.addProgramValidation( validationA );
        programValidationService.addProgramValidation( validationB );

        assertTrue( equals( programValidationService.getAllProgramValidation(), validationA, validationB ) );
    }

    @Test
    public void testGetProgramValidationByProgram()
    {
        programValidationService.addProgramValidation( validationA );
        programValidationService.addProgramValidation( validationB );

        assertTrue( equals( programValidationService.getProgramValidation( program ), validationA, validationB ) );
    }

    @Test
    public void testGetProgramValidationByStage()
    {
        programValidationService.addProgramValidation( validationA );
        programValidationService.addProgramValidation( validationB );

        assertTrue( equals( programValidationService.getProgramValidation( stageA ), validationA ) );
        assertTrue( equals( programValidationService.getProgramValidation( stageB ), validationB ) );
    }

    @Test
    public void testGetProgramValidationByStageDe()
    {
        programValidationService.addProgramValidation( validationA );

        Collection<ProgramValidation> result = programValidationService.getProgramValidation( stageDataElementA );
        assertEquals( 1, result.size() );
        assertTrue( result.contains( validationA ) );
    }

    @Test
    public void testValidate()
    {
        programValidationService.addProgramValidation( validationA );
        programValidationService.addProgramValidation( validationB );

        Collection<ProgramValidation> validationList = new HashSet<ProgramValidation>();
        validationList.add( validationA );
        validationList.add( validationB );

        Collection<ProgramValidationResult> result = programValidationService.validate( validationList, stageInstanceA );
        assertEquals( 1, result.size() );
        assertEquals( result.iterator().next().getProgramValidation(), validationB );
    }
}
