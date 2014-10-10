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

package org.hisp.dhis.patientdatavalue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chau Thu Tran
 * 
 * @version $ PatientDataValueServiceTest.java Nov 5, 2013 3:11:48 PM $
 */
public class PatientDataValueServiceTest
    extends DhisSpringTest
{
    @Autowired
    private PatientDataValueService patientDataValueService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private ProgramStageService programStageService;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private ProgramStageInstanceService programStageInstanceService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private DataElementService dataElementService;

    private ProgramInstance programInstance;

    private ProgramStageInstance stageInstanceA;

    private ProgramStageInstance stageInstanceB;

    private DataElement dataElementA;

    private DataElement dataElementB;

    private PatientDataValue dataValueA;

    private PatientDataValue dataValueB;

    private PatientDataValue dataValueC;

    private PatientDataValue dataValueD;

    private Date yesterday;

    private Date tomorrow;

    private Patient patient;

    @Override
    public void setUpTest()
    {
        OrganisationUnit organisationUnit = createOrganisationUnit( 'A' );
        organisationUnitService.addOrganisationUnit( organisationUnit );

        dataElementA = createDataElement( 'A' );
        dataElementB = createDataElement( 'B' );

        dataElementService.addDataElement( dataElementA );
        dataElementService.addDataElement( dataElementB );

        patient = createPatient( 'A', organisationUnit );
        patientService.savePatient( patient );

        Program program = createProgram( 'A', new HashSet<ProgramStage>(), organisationUnit );
        programService.addProgram( program );

        ProgramStage stageA = new ProgramStage( "A", program );
        programStageService.saveProgramStage( stageA );

        ProgramStage stageB = new ProgramStage( "B", program );
        programStageService.saveProgramStage( stageB );

        Set<ProgramStage> programStages = new HashSet<ProgramStage>();
        programStages.add( stageA );
        programStages.add( stageB );
        program.setProgramStages( programStages );
        programService.updateProgram( program );

        Calendar calYesterday = Calendar.getInstance();
        calYesterday.add( Calendar.DATE, -1 );
        yesterday = calYesterday.getTime();
        Calendar calTomorrow = Calendar.getInstance();
        calTomorrow.add( Calendar.DATE, 1 );
        tomorrow = calTomorrow.getTime();

        programInstance = programInstanceService.enrollPatient( patient, program, yesterday, yesterday,
            organisationUnit, null );

        stageInstanceA = programStageInstanceService.getProgramStageInstance( programInstance, stageA );
        stageInstanceB = programStageInstanceService.getProgramStageInstance( programInstance, stageB );

        dataValueA = new PatientDataValue( stageInstanceA, dataElementA, "A" );
        dataValueB = new PatientDataValue( stageInstanceA, dataElementB, "B" );
        dataValueC = new PatientDataValue( stageInstanceB, dataElementA, "C" );
        dataValueD = new PatientDataValue( stageInstanceB, dataElementB, "D" );
    }

    @Test
    public void testSavePatientDataValue()
    {
        patientDataValueService.savePatientDataValue( dataValueA );
        patientDataValueService.savePatientDataValue( dataValueB );

        assertNotNull( patientDataValueService.getPatientDataValue( stageInstanceA, dataElementA ) );
        assertNotNull( patientDataValueService.getPatientDataValue( stageInstanceA, dataElementB ) );
    }

    @Test
    public void testDeletePatientDataValue()
    {
        patientDataValueService.savePatientDataValue( dataValueA );
        patientDataValueService.savePatientDataValue( dataValueB );

        assertNotNull( patientDataValueService.getPatientDataValue( stageInstanceA, dataElementA ) );
        assertNotNull( patientDataValueService.getPatientDataValue( stageInstanceA, dataElementB ) );

        patientDataValueService.deletePatientDataValue( dataValueA );

        assertNull( patientDataValueService.getPatientDataValue( stageInstanceA, dataElementA ) );
        assertNotNull( patientDataValueService.getPatientDataValue( stageInstanceA, dataElementB ) );

        patientDataValueService.deletePatientDataValue( dataValueB );

        assertNull( patientDataValueService.getPatientDataValue( stageInstanceA, dataElementA ) );
        assertNull( patientDataValueService.getPatientDataValue( stageInstanceA, dataElementB ) );
    }

    @Test
    public void testUpdatePatientDataValue()
    {
        patientDataValueService.savePatientDataValue( dataValueA );

        assertNotNull( patientDataValueService.getPatientDataValue( stageInstanceA, dataElementA ) );

        dataValueA.setValue( "B" );
        patientDataValueService.updatePatientDataValue( dataValueA );

        assertEquals( "B", patientDataValueService.getPatientDataValue( stageInstanceA, dataElementA ).getValue() );
    }

    @Test
    public void testDeletePatientDataValueByStageInstance()
    {
        patientDataValueService.savePatientDataValue( dataValueA );
        patientDataValueService.savePatientDataValue( dataValueB );
        patientDataValueService.savePatientDataValue( dataValueC );
        patientDataValueService.savePatientDataValue( dataValueD );

        assertNotNull( patientDataValueService.getPatientDataValue( stageInstanceA, dataElementA ) );
        assertNotNull( patientDataValueService.getPatientDataValue( stageInstanceA, dataElementB ) );
        assertNotNull( patientDataValueService.getPatientDataValue( stageInstanceB, dataElementA ) );
        assertNotNull( patientDataValueService.getPatientDataValue( stageInstanceB, dataElementB ) );

        patientDataValueService.deletePatientDataValue( stageInstanceA );
        assertNull( patientDataValueService.getPatientDataValue( stageInstanceA, dataElementA ) );
        assertNull( patientDataValueService.getPatientDataValue( stageInstanceA, dataElementB ) );
        assertNotNull( patientDataValueService.getPatientDataValue( stageInstanceB, dataElementA ) );
        assertNotNull( patientDataValueService.getPatientDataValue( stageInstanceB, dataElementB ) );
    }

    @Test
    public void testGetPatientDataValuesByStageInstance()
    {
        patientDataValueService.savePatientDataValue( dataValueA );
        patientDataValueService.savePatientDataValue( dataValueB );
        patientDataValueService.savePatientDataValue( dataValueC );
        patientDataValueService.savePatientDataValue( dataValueD );

        Collection<PatientDataValue> dataValues = patientDataValueService.getPatientDataValues( stageInstanceA );
        assertEquals( 2, dataValues.size() );
        assertTrue( dataValues.contains( dataValueA ) );
        assertTrue( dataValues.contains( dataValueB ) );

        dataValues = patientDataValueService.getPatientDataValues( stageInstanceB );
        assertEquals( 2, dataValues.size() );
        assertTrue( dataValues.contains( dataValueC ) );
        assertTrue( dataValues.contains( dataValueD ) );
    }

    @Test
    public void testGetPatientDataValuesByStageElement()
    {
        patientDataValueService.savePatientDataValue( dataValueA );
        patientDataValueService.savePatientDataValue( dataValueB );
        patientDataValueService.savePatientDataValue( dataValueC );
        patientDataValueService.savePatientDataValue( dataValueD );

        Collection<DataElement> dataElements = new HashSet<DataElement>();
        dataElements.add( dataElementA );
        dataElements.add( dataElementB );

        Collection<PatientDataValue> dataValues = patientDataValueService.getPatientDataValues( stageInstanceA,
            dataElements );
        assertEquals( 2, dataValues.size() );
        assertTrue( dataValues.contains( dataValueA ) );
        assertTrue( dataValues.contains( dataValueB ) );
    }

    @Test
    public void testGetPatientDataValues()
    {
        patientDataValueService.savePatientDataValue( dataValueA );
        patientDataValueService.savePatientDataValue( dataValueB );
        patientDataValueService.savePatientDataValue( dataValueC );
        patientDataValueService.savePatientDataValue( dataValueD );

        Collection<ProgramStageInstance> programStageInstances = new HashSet<ProgramStageInstance>();
        programStageInstances.add( stageInstanceA );
        programStageInstances.add( stageInstanceB );

        Collection<PatientDataValue> dataValues = patientDataValueService.getPatientDataValues( programStageInstances );
        assertEquals( 4, dataValues.size() );
        assertTrue( dataValues.contains( dataValueA ) );
        assertTrue( dataValues.contains( dataValueB ) );
        assertTrue( dataValues.contains( dataValueC ) );
        assertTrue( dataValues.contains( dataValueD ) );
    }

    @Test
    public void testGetPatientDataValuesByDataElement()
    {
        patientDataValueService.savePatientDataValue( dataValueA );
        patientDataValueService.savePatientDataValue( dataValueB );
        patientDataValueService.savePatientDataValue( dataValueC );
        patientDataValueService.savePatientDataValue( dataValueD );

        Collection<PatientDataValue> dataValues = patientDataValueService.getPatientDataValues( dataElementA );
        assertEquals( 2, dataValues.size() );
        assertTrue( dataValues.contains( dataValueA ) );
        assertTrue( dataValues.contains( dataValueC ) );

        dataValues = patientDataValueService.getPatientDataValues( dataElementB );
        assertEquals( 2, dataValues.size() );
        assertTrue( dataValues.contains( dataValueB ) );
        assertTrue( dataValues.contains( dataValueD ) );
    }

    @Test
    public void testGetPatientDataValuesByPatientDataElement()
    {
        patientDataValueService.savePatientDataValue( dataValueA );
        patientDataValueService.savePatientDataValue( dataValueB );
        patientDataValueService.savePatientDataValue( dataValueC );
        patientDataValueService.savePatientDataValue( dataValueD );

        Collection<DataElement> dataElements = new HashSet<DataElement>();
        dataElements.add( dataElementA );
        dataElements.add( dataElementB );

        Collection<PatientDataValue> dataValues = patientDataValueService.getPatientDataValues( patient, dataElements,
            yesterday, tomorrow );

        dataValues = patientDataValueService.getPatientDataValues( dataElementB );
        assertEquals( 2, dataValues.size() );
        assertTrue( dataValues.contains( dataValueB ) );
        assertTrue( dataValues.contains( dataValueD ) );
    }

    @Test
    public void testGetPatientDataValue()
    {
        patientDataValueService.savePatientDataValue( dataValueA );
        patientDataValueService.savePatientDataValue( dataValueB );

        PatientDataValue dataValue = patientDataValueService.getPatientDataValue( stageInstanceA, dataElementA );
        assertEquals( dataValueA, dataValue );

        dataValue = patientDataValueService.getPatientDataValue( stageInstanceA, dataElementB );
        assertEquals( dataValueB, dataValue );
    }

    @Test
    public void testGetAllPatientDataValues()
    {
        patientDataValueService.savePatientDataValue( dataValueA );
        patientDataValueService.savePatientDataValue( dataValueA );

        assertTrue( equals( patientDataValueService.getAllPatientDataValues(), dataValueA, dataValueA ) );
    }

}
