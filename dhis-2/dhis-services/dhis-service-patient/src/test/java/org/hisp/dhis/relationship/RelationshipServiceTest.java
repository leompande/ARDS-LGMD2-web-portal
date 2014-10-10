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

package org.hisp.dhis.relationship;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chau Thu Tran
 * 
 * @version $ RelationshipServiceTest.java Nov 13, 2013 1:34:55 PM $
 */
public class RelationshipServiceTest
    extends DhisSpringTest
{
    @Autowired
    private RelationshipService relationshipService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private RelationshipTypeService relationshipTypeService;

    private RelationshipType relationshipType;

    private Patient patientA;

    private Patient patientB;

    private Patient patientC;

    private Patient patientD;

    private Relationship relationshipA;

    private Relationship relationshipB;

    private Relationship relationshipC;

    @Override
    public void setUpTest()
    {
        OrganisationUnit organisationUnit = createOrganisationUnit( 'A' );
        organisationUnitService.addOrganisationUnit( organisationUnit );

        patientA = createPatient( 'A', organisationUnit );
        patientService.savePatient( patientA );

        patientB = createPatient( 'B', organisationUnit );
        patientService.savePatient( patientB );

        patientC = createPatient( 'C', organisationUnit );
        patientService.savePatient( patientC );

        patientD = createPatient( 'D', organisationUnit );
        patientService.savePatient( patientD );

        relationshipType = createRelationshipType( 'A' );
        relationshipTypeService.saveRelationshipType( relationshipType );

        relationshipA = new Relationship( patientA, relationshipType, patientB );
        relationshipB = new Relationship( patientC, relationshipType, patientD );
        relationshipC = new Relationship( patientA, relationshipType, patientC );
    }

    @Test
    public void testSaveRelationship()
    {
        int idA = relationshipService.saveRelationship( relationshipA );
        int idB = relationshipService.saveRelationship( relationshipB );

        assertNotNull( relationshipService.getRelationship( idA ) );
        assertNotNull( relationshipService.getRelationship( idB ) );
    }

    @Test
    public void testDeleteRelationship()
    {
        int idA = relationshipService.saveRelationship( relationshipA );
        int idB = relationshipService.saveRelationship( relationshipB );

        assertNotNull( relationshipService.getRelationship( idA ) );
        assertNotNull( relationshipService.getRelationship( idB ) );

        relationshipService.deleteRelationship( relationshipA );

        assertNull( relationshipService.getRelationship( idA ) );
        assertNotNull( relationshipService.getRelationship( idB ) );

        relationshipService.deleteRelationship( relationshipB );

        assertNull( relationshipService.getRelationship( idA ) );
        assertNull( relationshipService.getRelationship( idB ) );
    }

    @Test
    public void testUpdateRelationship()
    {
        int idA = relationshipService.saveRelationship( relationshipA );

        assertNotNull( relationshipService.getRelationship( idA ) );

        relationshipA.setPatientA( patientC );
        relationshipService.updateRelationship( relationshipA );

        assertEquals( relationshipA, relationshipService.getRelationship( idA ) );
    }

    @Test
    public void testGetRelationshipById()
    {
        int idA = relationshipService.saveRelationship( relationshipA );
        int idB = relationshipService.saveRelationship( relationshipB );

        assertEquals( relationshipA, relationshipService.getRelationship( idA ) );
        assertEquals( relationshipB, relationshipService.getRelationship( idB ) );
    }

    @Test
    public void testGetRelationshipByTypePatient()
    {
        relationshipService.saveRelationship( relationshipA );
        relationshipService.saveRelationship( relationshipB );

        Relationship relationship = relationshipService.getRelationship( patientA, patientB, relationshipType );
        assertEquals( relationshipA, relationship );

        relationship = relationshipService.getRelationship( patientC, patientD, relationshipType );
        assertEquals( relationshipB, relationship );
    }

    @Test
    public void testGetRelationshipByTwoPatient()
    {
        relationshipService.saveRelationship( relationshipA );
        relationshipService.saveRelationship( relationshipB );

        Relationship relationship = relationshipService.getRelationship( patientA, patientB );
        assertEquals( relationshipA, relationship );

        relationship = relationshipService.getRelationship( patientC, patientD );
        assertEquals( relationshipB, relationship );
    }

    @Test
    public void testGetAllRelationships()
    {
        relationshipService.saveRelationship( relationshipA );
        relationshipService.saveRelationship( relationshipB );

        assertTrue( equals( relationshipService.getAllRelationships(), relationshipA, relationshipB ) );
    }

    @Test
    public void testGetRelationshipsForPatient()
    {
        relationshipService.saveRelationship( relationshipA );
        relationshipService.saveRelationship( relationshipC );

        Collection<Relationship> relationships = relationshipService.getRelationshipsForPatient( patientA );
        assertTrue( equals( relationships, relationshipA, relationshipC ) );
    }

    @Test
    public void testGetRelationships()
    {
        relationshipService.saveRelationship( relationshipA );
        relationshipService.saveRelationship( relationshipC );

        Collection<Relationship> relationships = relationshipService.getRelationships( patientA, relationshipType );
        assertTrue( equals( relationships, relationshipA, relationshipC ) );
    }

    @Test
    public void testGetRelationshipsByRelationshipType()
    {
        relationshipService.saveRelationship( relationshipA );
        relationshipService.saveRelationship( relationshipC );

        Collection<Relationship> relationships = relationshipService
            .getRelationshipsByRelationshipType( relationshipType );
        assertTrue( equals( relationships, relationshipA, relationshipC ) );
    }

}
