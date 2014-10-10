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

import org.hisp.dhis.DhisSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chau Thu Tran
 * 
 * @version $ RelationshipTypeServiceTest.java Nov 5, 2013 3:11:48 PM $
 */
public class RelationshipTypeServiceTest
    extends DhisSpringTest
{
    @Autowired
    private RelationshipTypeService relationshipTypeService;

    private RelationshipType relationshipTypeA;

    private RelationshipType relationshipTypeB;

    @Override
    public void setUpTest()
    {
        relationshipTypeA = createRelationshipType( 'A' );
        relationshipTypeB = createRelationshipType( 'B' );
    }

    @Test
    public void testSaveRelationshipType()
    {
        int idA = relationshipTypeService.saveRelationshipType( relationshipTypeA );
        int idB = relationshipTypeService.saveRelationshipType( relationshipTypeB );

        assertNotNull( relationshipTypeService.getRelationshipType( idA ) );
        assertNotNull( relationshipTypeService.getRelationshipType( idB ) );
    }

    @Test
    public void testDeleteRelationshipType()
    {
        int idA = relationshipTypeService.saveRelationshipType( relationshipTypeA );
        int idB = relationshipTypeService.saveRelationshipType( relationshipTypeB );

        assertNotNull( relationshipTypeService.getRelationshipType( idA ) );
        assertNotNull( relationshipTypeService.getRelationshipType( idB ) );

        relationshipTypeService.deleteRelationshipType( relationshipTypeA );

        assertNull( relationshipTypeService.getRelationshipType( idA ) );
        assertNotNull( relationshipTypeService.getRelationshipType( idB ) );

        relationshipTypeService.deleteRelationshipType( relationshipTypeB );

        assertNull( relationshipTypeService.getRelationshipType( idA ) );
        assertNull( relationshipTypeService.getRelationshipType( idB ) );
    }

    @Test
    public void testUpdateRelationshipType()
    {
        int idA = relationshipTypeService.saveRelationshipType( relationshipTypeA );

        assertNotNull( relationshipTypeService.getRelationshipType( idA ) );

        relationshipTypeA.setName( "B" );
        relationshipTypeService.updateRelationshipType( relationshipTypeA );

        assertEquals( "B", relationshipTypeService.getRelationshipType( idA ).getName() );
    }

    @Test
    public void testGetRelationshipTypeById()
    {
        int idA = relationshipTypeService.saveRelationshipType( relationshipTypeA );
        int idB = relationshipTypeService.saveRelationshipType( relationshipTypeB );

        assertEquals( relationshipTypeA, relationshipTypeService.getRelationshipType( idA ) );
        assertEquals( relationshipTypeB, relationshipTypeService.getRelationshipType( idB ) );
    }

    @Test
    public void testGetRelationshipTypeByDescription()
    {
        relationshipTypeService.saveRelationshipType( relationshipTypeA );
        assertEquals( relationshipTypeA, relationshipTypeService.getRelationshipType( "aIsToB", "bIsToA" ) );
    }

    @Test
    public void testGetAllRelationshipTypes()
    {
        relationshipTypeService.saveRelationshipType( relationshipTypeA );
        relationshipTypeService.saveRelationshipType( relationshipTypeB );

        assertTrue( equals( relationshipTypeService.getAllRelationshipTypes(), relationshipTypeA, relationshipTypeB ) );
    }

}
