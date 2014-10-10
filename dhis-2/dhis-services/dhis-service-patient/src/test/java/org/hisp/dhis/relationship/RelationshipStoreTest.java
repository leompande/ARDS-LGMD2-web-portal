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
 * @version $ RelationshipStoreTest.java Nov 13, 2013 1:34:55 PM $
 */
public class RelationshipStoreTest
    extends DhisSpringTest
{
    @Autowired
    private RelationshipStore relationshipStore;

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
    public void testGetRelationshipByTypePatient()
    {
        relationshipStore.save( relationshipA );
        relationshipStore.save( relationshipB );

        Relationship relationship = relationshipStore.get( patientA, patientB, relationshipType );
        assertEquals( relationshipA, relationship );

        relationship = relationshipStore.get( patientC, patientD, relationshipType );
        assertEquals( relationshipB, relationship );
    }

    @Test
    public void testGetRelationshipByTwoPatient()
    {
        relationshipStore.save( relationshipA );
        relationshipStore.save( relationshipB );

        Relationship relationship = relationshipStore.get( patientA, patientB );
        assertEquals( relationshipA, relationship );

        relationship = relationshipStore.get( patientC, patientD );
        assertEquals( relationshipB, relationship );
    }

    @Test
    public void testGetRelationshipsForPatient()
    {
        relationshipStore.save( relationshipA );
        relationshipStore.save( relationshipC );

        Collection<Relationship> relationships = relationshipStore.getForPatient( patientA );
        assertTrue( equals( relationships, relationshipA, relationshipC ) );
    }

    @Test
    public void testGetRelationships()
    {
        relationshipStore.save( relationshipA );
        relationshipStore.save( relationshipC );

        Collection<Relationship> relationships = relationshipStore.get( patientA, relationshipType );
        assertTrue( equals( relationships, relationshipA, relationshipC ) );
    }

    @Test
    public void testGetRelationshipsByRelationshipType()
    {
        relationshipStore.save( relationshipA );
        relationshipStore.save( relationshipC );

        Collection<Relationship> relationships = relationshipStore.getByRelationshipType( relationshipType );
        assertTrue( equals( relationships, relationshipA, relationshipC ) );
    }

}
