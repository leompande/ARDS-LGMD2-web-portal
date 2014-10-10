package org.hisp.dhis.concept;

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

import java.util.Collection;

import org.junit.Test;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class ConceptServiceTest
    extends ConceptTest
{
    @Override
    public void setUpTest()
        throws Exception
    {
        setUpConceptTest();
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void assertEq( char uniqueCharacter, Concept concept )
    {
        assertEquals( "Concept" + uniqueCharacter, concept.getName() );
    }

    // -------------------------------------------------------------------------
    // Concept
    // -------------------------------------------------------------------------

    @Test
    public void testAddConcept()
    {
        Concept conceptA = createConcept( 'A' );
        Concept conceptB = createConcept( 'B' );

        int idA = conceptService.saveConcept( conceptA );
        int idB = conceptService.saveConcept( conceptB );

        conceptA = conceptService.getConcept( idA );
        conceptB = conceptService.getConcept( idB );

        assertEquals( idA, conceptA.getId() );
        assertEq( 'A', conceptA );

        assertEquals( idB, conceptB.getId() );
        assertEq( 'B', conceptB );
    }

    @Test
    public void testUpdateConcept()
    {
        Concept concept = createConcept( 'A' );

        int id = conceptService.saveConcept( concept );
        concept = conceptService.getConcept( id );

        assertEq( 'A', concept );

        concept.setName( "ConceptC" );

        conceptService.updateConcept( concept );
        concept = conceptService.getConcept( id );

        assertEquals( concept.getName(), "ConceptC" );
    }

    @Test
    public void testDeleteAndGetConcept()
    {
        Concept conceptA = createConcept( 'A' );
        Concept conceptB = createConcept( 'B' );
        Concept conceptC = createConcept( 'C' );

        int idA = conceptService.saveConcept( conceptA );
        int idB = conceptService.saveConcept( conceptB );
        int idC = conceptService.saveConcept( conceptC );
        int idD = defaultConcept.getId();

        assertNotNull( conceptService.getConcept( idA ) );
        assertNotNull( conceptService.getConcept( idB ) );
        assertNotNull( conceptService.getConcept( idC ) );
        assertNotNull( conceptService.getConcept( idD ) );

        categoryA.setConcept( conceptA );
        categoryB.setConcept( conceptB );
        categoryC.setConcept( conceptC );
        defaultCategory.setConcept( null );

        categoryService.updateDataElementCategory( categoryA );
        categoryService.updateDataElementCategory( categoryB );
        categoryService.updateDataElementCategory( categoryC );
        categoryService.updateDataElementCategory( defaultCategory );

        conceptService.deleteConcept( defaultConcept );

        assertNotNull( conceptService.getConcept( idA ) );
        assertNotNull( conceptService.getConcept( idB ) );
        assertNotNull( conceptService.getConcept( idC ) );
        assertNull( conceptService.getConcept( idD ) );

    }

    @Test
    public void testGetConceptByName()
        throws Exception
    {
        Concept conceptA = createConcept( 'A' );
        Concept conceptB = createConcept( 'B' );

        int idA = conceptService.saveConcept( conceptA );
        int idB = conceptService.saveConcept( conceptB );
        int idC = defaultConcept.getId();

        assertEquals( conceptService.getConceptByName( "ConceptA" ).getId(), idA );
        assertEquals( conceptService.getConceptByName( "ConceptB" ).getId(), idB );
        assertEquals( defaultCategory.getConcept().getId(), idC );
        assertNull( conceptService.getConceptByName( "ConceptC" ) );
    }

    @Test
    public void testGetAllConcepts()
    {
        Concept conceptA = createConcept( 'A' );
        Concept conceptB = createConcept( 'B' );
        Concept conceptC = createConcept( 'C' );
        Concept conceptD = createConcept( 'D' );

        conceptService.saveConcept( conceptA );
        conceptService.saveConcept( conceptB );
        conceptService.saveConcept( conceptC );

        Collection<Concept> concepts = conceptService.getAllConcepts();
        // Currently, there is another concept named 'default'
        assertEquals( 4, concepts.size() );
        assertTrue( concepts.contains( conceptA ) );
        assertTrue( concepts.contains( conceptB ) );
        assertTrue( concepts.contains( conceptC ) );
        assertTrue( !concepts.contains( conceptD ) );
    }

    @Test
    public void testGetAllConceptNames()
    {
        Concept conceptA = createConcept( 'A' );
        Concept conceptB = createConcept( 'B' );
        Concept conceptC = createConcept( 'C' );
        Concept conceptD = createConcept( 'D' );

        conceptService.saveConcept( conceptA );
        conceptService.saveConcept( conceptB );
        conceptService.saveConcept( conceptC );
        conceptService.saveConcept( conceptD );

        assertEq( 'A', conceptService.getConceptByName( "ConceptA" ) );
        assertEq( 'B', conceptService.getConceptByName( "ConceptB" ) );
        assertEq( 'C', conceptService.getConceptByName( "ConceptC" ) );
        assertEq( 'D', conceptService.getConceptByName( "ConceptD" ) );
    }
}
