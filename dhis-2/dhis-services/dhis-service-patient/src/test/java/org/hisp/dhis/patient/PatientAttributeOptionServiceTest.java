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

package org.hisp.dhis.patient;

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
 * @version $ PatientAttributeOptionServiceTest.java Nov 5, 2013 4:25:29 PM $
 */
public class PatientAttributeOptionServiceTest
    extends DhisSpringTest
{
    @Autowired
    private PatientAttributeOptionService attributeOptionService;

    @Autowired
    private PatientAttributeService attributeService;

    private PatientAttributeOption attributeOptionA;

    private PatientAttributeOption attributeOptionB;

    private PatientAttributeOption attributeOptionC;

    private PatientAttribute attributeA;

    private PatientAttribute attributeB;

    @Override
    public void setUpTest()
    {
        attributeA = createPatientAttribute( 'A' );
        attributeB = createPatientAttribute( 'B' );

        attributeService.savePatientAttribute( attributeA );
        attributeService.savePatientAttribute( attributeB );

        attributeOptionA = createPatientAttributeOption( 'A', attributeA );
        attributeOptionB = createPatientAttributeOption( 'B', attributeA );
        attributeOptionC = createPatientAttributeOption( 'C', attributeB );
    }

    @Test
    public void testAddPatientAttributeOption()
    {
        int idA = attributeOptionService.addPatientAttributeOption( attributeOptionA );
        int idB = attributeOptionService.addPatientAttributeOption( attributeOptionB );

        assertNotNull( attributeOptionService.get( idA ) );
        assertNotNull( attributeOptionService.get( idB ) );
    }

    @Test
    public void testDeletePatientAttributeGroup()
    {
        int idA = attributeOptionService.addPatientAttributeOption( attributeOptionA );
        int idB = attributeOptionService.addPatientAttributeOption( attributeOptionB );

        assertNotNull( attributeOptionService.get( idA ) );
        assertNotNull( attributeOptionService.get( idB ) );

        attributeOptionService.deletePatientAttributeOption( attributeOptionA );

        assertNull( attributeOptionService.get( idA ) );
        assertNotNull( attributeOptionService.get( idB ) );

        attributeOptionService.deletePatientAttributeOption( attributeOptionB );

        assertNull( attributeOptionService.get( idA ) );
        assertNull( attributeOptionService.get( idB ) );
    }

    @Test
    public void testUpdatePatientAttributeOption()
    {
        int idA = attributeOptionService.addPatientAttributeOption( attributeOptionA );

        assertNotNull( attributeOptionService.get( idA ) );

        attributeOptionA.setName( "B" );
        attributeOptionService.updatePatientAttributeOption( attributeOptionA );

        assertEquals( "B", attributeOptionService.get( idA ).getName() );
    }

    @Test
    public void testGetPatientAttributeGroupById()
    {
        int idA = attributeOptionService.addPatientAttributeOption( attributeOptionA );
        int idB = attributeOptionService.addPatientAttributeOption( attributeOptionB );

        assertEquals( attributeOptionA, attributeOptionService.get( idA ) );
        assertEquals( attributeOptionB, attributeOptionService.get( idB ) );
    }

    @Test
    public void testGetPatientAttributeGroupByName()
    {
        int idA = attributeOptionService.addPatientAttributeOption( attributeOptionA );

        assertNotNull( attributeOptionService.get( idA ) );

        assertEquals( attributeOptionA, attributeOptionService.get( attributeA, "AttributeOptionA" ) );
    }

    @Test
    public void testGetPatientAttributeOptionByAttribute()
    {
        attributeOptionService.addPatientAttributeOption( attributeOptionA );
        attributeOptionService.addPatientAttributeOption( attributeOptionB );
        attributeOptionService.addPatientAttributeOption( attributeOptionC );

        assertTrue( equals( attributeOptionService.get( attributeA ), attributeOptionA, attributeOptionB ) );
        assertTrue( equals( attributeOptionService.get( attributeB ), attributeOptionC ) );
    }

}