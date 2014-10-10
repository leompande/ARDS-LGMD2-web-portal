package org.hisp.dhis.patient;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.validation.ValidationCriteriaService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 */
public class PatientStoreTest
    extends DhisSpringTest
{
    @Autowired
    private PatientStore patientStore;

    @Autowired
    private ProgramService programService;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private PatientAttributeService patientAttributeService;

    @Autowired
    private PatientIdentifierTypeService identifierTypeService;

    @Autowired
    private PatientAttributeValueService patientAttributeValueService;

    @Autowired
    private ValidationCriteriaService validationCriteriaService;

    @Autowired
    private PatientService patientService;

    private Patient patientA1;

    private Patient patientA2;

    private Patient patientA3;

    private Patient patientB1;

    private Patient patientB2;

    private PatientAttribute patientAttribute;

    private int attributeId;

    private Program programA;

    private Program programB;

    private OrganisationUnit organisationUnit;

    private Date date = new Date();

    @Override
    public void setUpTest()
    {
        organisationUnit = createOrganisationUnit( 'A' );
        organisationUnitService.addOrganisationUnit( organisationUnit );

        OrganisationUnit organisationUnitB = createOrganisationUnit( 'B' );
        organisationUnitService.addOrganisationUnit( organisationUnitB );

        PatientIdentifierType patientIdentifierType = createPatientIdentifierType( 'A' );
        identifierTypeService.savePatientIdentifierType( patientIdentifierType );

        patientAttribute = createPatientAttribute( 'A' );
        attributeId = patientAttributeService.savePatientAttribute( patientAttribute );

        patientA1 = createPatient( 'A', organisationUnit );
        patientA2 = createPatient( 'A', organisationUnitB );
        patientA3 = createPatient( 'A', organisationUnit, patientIdentifierType );
        patientB1 = createPatient( 'B', organisationUnit );
        patientB2 = createPatient( 'B', organisationUnit );

        programA = createProgram( 'A', new HashSet<ProgramStage>(), organisationUnit );
        programB = createProgram( 'B', new HashSet<ProgramStage>(), organisationUnit );
    }

    @Test
    public void testAddGet()
    {
        int idA = patientStore.save( patientA1 );
        int idB = patientStore.save( patientB1 );

        assertNotNull( patientStore.get( idA ) );
        assertNotNull( patientStore.get( idB ) );
    }

    @Test
    public void testAddGetbyOu()
    {
        int idA = patientStore.save( patientA1 );
        int idB = patientStore.save( patientB1 );

        assertEquals( patientA1.getName(), patientStore.get( idA ).getName() );
        assertEquals( patientB1.getName(), patientStore.get( idB ).getName() );
    }

    @Test
    public void testDelete()
    {
        int idA = patientStore.save( patientA1 );
        int idB = patientStore.save( patientB1 );

        assertNotNull( patientStore.get( idA ) );
        assertNotNull( patientStore.get( idB ) );

        patientStore.delete( patientA1 );

        assertNull( patientStore.get( idA ) );
        assertNotNull( patientStore.get( idB ) );

        patientStore.delete( patientB1 );

        assertNull( patientStore.get( idA ) );
        assertNull( patientStore.get( idB ) );
    }

    @Test
    public void testGetAll()
    {
        patientStore.save( patientA1 );
        patientStore.save( patientB1 );

        assertTrue( equals( patientStore.getAll(), patientA1, patientB1 ) );
    }

    @Test
    public void testGetByOrgUnitProgram()
    {
        programService.addProgram( programA );
        programService.addProgram( programB );

        patientStore.save( patientA1 );
        patientStore.save( patientB1 );
        patientStore.save( patientA2 );
        patientStore.save( patientB2 );

        programInstanceService.enrollPatient( patientA1, programA, date, date, organisationUnit, null );
        programInstanceService.enrollPatient( patientB1, programA, date, date, organisationUnit, null );
        programInstanceService.enrollPatient( patientA2, programA, date, date, organisationUnit, null );
        programInstanceService.enrollPatient( patientB2, programB, date, date, organisationUnit, null );

        Collection<Patient> patients = patientStore.getByOrgUnitProgram( organisationUnit, programA, 0, 100 );

        assertEquals( 2, patients.size() );
        assertTrue( patients.contains( patientA1 ) );
        assertTrue( patients.contains( patientB1 ) );

        patients = patientStore.getByOrgUnitProgram( organisationUnit, programB, 0, 100 );

        assertEquals( 1, patients.size() );
        assertTrue( patients.contains( patientB2 ) );
    }

    @Test
    public void testGetByProgram()
    {
        programService.addProgram( programA );
        programService.addProgram( programB );

        patientStore.save( patientA1 );
        patientStore.save( patientB1 );
        patientStore.save( patientA2 );
        patientStore.save( patientB2 );

        programInstanceService.enrollPatient( patientA1, programA, date, date, organisationUnit, null );
        programInstanceService.enrollPatient( patientA2, programA, date, date, organisationUnit, null );
        programInstanceService.enrollPatient( patientB1, programA, date, date, organisationUnit, null );
        programInstanceService.enrollPatient( patientB2, programB, date, date, organisationUnit, null );

        Collection<Patient> patients = patientStore.getByProgram( programA, 0, 100 );

        assertEquals( 3, patients.size() );
        assertTrue( patients.contains( patientA1 ) );
        assertTrue( patients.contains( patientA2 ) );
        assertTrue( patients.contains( patientB1 ) );

        patients = patientStore.getByOrgUnitProgram( organisationUnit, programB, 0, 100 );

        assertEquals( 1, patients.size() );
        assertTrue( patients.contains( patientB2 ) );
    }

    @Test
    public void testGetRepresentatives()
    {
        patientStore.save( patientB1 );

        patientA1.setRepresentative( patientB1 );
        patientA2.setRepresentative( patientB1 );
        patientStore.save( patientA1 );
        patientStore.save( patientA2 );

        assertEquals( 2, patientStore.getRepresentatives( patientB1 ).size() );
    }

    @Test
    public void testGetByPhoneNumber()
    {
        patientStore.save( patientA1 );
        patientStore.save( patientB1 );

        PatientAttribute attribute = createPatientAttribute( 'B' );
        attribute.setValueType( PatientAttribute.TYPE_PHONE_NUMBER );
        patientAttributeService.savePatientAttribute( attribute );

        PatientAttributeValue attributeValue = createPatientAttributeValue( 'A', patientA1, attribute );
        attributeValue.setValue( "123456789" );
        patientAttributeValueService.savePatientAttributeValue( attributeValue );

        patientA1.addAttributeValue( attributeValue );
        patientService.updatePatient( patientA1 );

        attributeValue = createPatientAttributeValue( 'A', patientB1, attribute );
        attributeValue.setValue( "123456789" );
        patientAttributeValueService.savePatientAttributeValue( attributeValue );

        patientB1.addAttributeValue( attributeValue );
        patientService.updatePatient( patientB1 );

        assertEquals( 2, patientStore.getByPhoneNumber( "123456789", null, null ).size() );
    }

    @Test
    public void testSearch()
    {
        int idA = programService.addProgram( programA );
        programService.addProgram( programB );

        patientStore.save( patientA1 );
        patientStore.save( patientA2 );
        patientStore.save( patientA3 );
        patientStore.save( patientB1 );
        patientStore.save( patientB2 );

        PatientAttributeValue attributeValue = createPatientAttributeValue( 'A', patientA3, patientAttribute );
        patientAttributeValueService.savePatientAttributeValue( attributeValue );

        programInstanceService.enrollPatient( patientA3, programA, date, date, organisationUnit, null );
        programInstanceService.enrollPatient( patientB1, programA, date, date, organisationUnit, null );

        List<String> searchKeys = new ArrayList<String>();
        searchKeys.add( Patient.PREFIX_PATIENT_ATTRIBUTE + Patient.SEARCH_SAPERATE + attributeId
            + Patient.SEARCH_SAPERATE + "a" + Patient.SEARCH_SAPERATE + organisationUnit.getId() );
        searchKeys.add( Patient.PREFIX_PROGRAM + Patient.SEARCH_SAPERATE + idA );

        Collection<OrganisationUnit> orgunits = new HashSet<OrganisationUnit>();
        orgunits.add( organisationUnit );

        Collection<Patient> patients = patientStore.search( searchKeys, orgunits, null, null, null,
            ProgramStageInstance.ACTIVE_STATUS, null, null );

        assertEquals( 1, patients.size() );
        assertTrue( patients.contains( patientA3 ) );
    }

    @Test
    public void testQuery()
    {
        patientStore.save( patientA1 );
        patientStore.save( patientA2 );
        patientStore.save( patientA3 );
        patientStore.save( patientB1 );
        patientStore.save( patientB2 );

        TrackedEntityQueryParams params = new TrackedEntityQueryParams();

        List<Patient> list = patientStore.query( params );

        assertEquals( 5, list.size() );
    }
}
