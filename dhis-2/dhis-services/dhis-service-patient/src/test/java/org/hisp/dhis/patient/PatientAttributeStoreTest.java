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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.DhisSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chau Thu Tran
 * 
 * @version $ PatientAttributeStoreTest.java Nov 5, 2013 04:21:02 PM $
 */
public class PatientAttributeStoreTest
    extends DhisSpringTest
{
    @Autowired
    private PatientAttributeService attributeService;

    @Autowired
    private PatientAttributeGroupService attributeGroupService;

    private PatientAttribute attributeA;

    private PatientAttribute attributeB;

    private PatientAttribute attributeC;

    private PatientAttributeGroup attributeGroup;

    @Override
    public void setUpTest()
    {
        attributeA = createPatientAttribute( 'A' );
        attributeB = createPatientAttribute( 'B' );
        attributeC = createPatientAttribute( 'C', PatientAttribute.TYPE_INT );

        List<PatientAttribute> attributesA = new ArrayList<PatientAttribute>();
        attributesA.add( attributeA );
        attributesA.add( attributeB );
        attributeGroup = createPatientAttributeGroup( 'A', attributesA );
    }

    @Test
    public void testSavePatientAttribute()
    {
        int idA = attributeService.savePatientAttribute( attributeA );
        int idB = attributeService.savePatientAttribute( attributeB );

        assertNotNull( attributeService.getPatientAttribute( idA ) );
        assertNotNull( attributeService.getPatientAttribute( idB ) );
    }

    @Test
    public void testDeletePatientAttribute()
    {
        int idA = attributeService.savePatientAttribute( attributeA );
        int idB = attributeService.savePatientAttribute( attributeB );

        assertNotNull( attributeService.getPatientAttribute( idA ) );
        assertNotNull( attributeService.getPatientAttribute( idB ) );

        attributeService.deletePatientAttribute( attributeA );

        assertNull( attributeService.getPatientAttribute( idA ) );
        assertNotNull( attributeService.getPatientAttribute( idB ) );

        attributeService.deletePatientAttribute( attributeB );

        assertNull( attributeService.getPatientAttribute( idA ) );
        assertNull( attributeService.getPatientAttribute( idB ) );
    }

    @Test
    public void testUpdatePatientAttribute()
    {
        int idA = attributeService.savePatientAttribute( attributeA );

        assertNotNull( attributeService.getPatientAttribute( idA ) );

        attributeA.setName( "B" );
        attributeService.updatePatientAttribute( attributeA );

        assertEquals( "B", attributeService.getPatientAttribute( idA ).getName() );
    }

    @Test
    public void testGetPatientAttributeById()
    {
        int idA = attributeService.savePatientAttribute( attributeA );
        int idB = attributeService.savePatientAttribute( attributeB );

        assertEquals( attributeA, attributeService.getPatientAttribute( idA ) );
        assertEquals( attributeB, attributeService.getPatientAttribute( idB ) );
    }

    @Test
    public void testGetPatientAttributeByUid()
    {
        attributeA.setUid( "uid" );
        attributeService.savePatientAttribute( attributeA );

        assertEquals( attributeA, attributeService.getPatientAttribute( "uid" ) );
    }

    @Test
    public void testGetPatientAttributeByName()
    {
        int idA = attributeService.savePatientAttribute( attributeA );

        assertNotNull( attributeService.getPatientAttribute( idA ) );
        assertEquals( attributeA.getName(), attributeService.getPatientAttributeByName( "AttributeA" ).getName() );
    }

    @Test
    public void testGetAllPatientAttributes()
    {
        attributeService.savePatientAttribute( attributeA );
        attributeService.savePatientAttribute( attributeB );

        assertTrue( equals( attributeService.getAllPatientAttributes(), attributeA, attributeB ) );
    }

    @Test
    public void testGetPatientAttributesByValueType()
    {
        attributeService.savePatientAttribute( attributeA );
        attributeService.savePatientAttribute( attributeB );
        attributeService.savePatientAttribute( attributeC );

        Collection<PatientAttribute> attributes = attributeService
            .getPatientAttributesByValueType( PatientAttribute.TYPE_STRING );
        assertEquals( 2, attributes.size() );
        assertTrue( attributes.contains( attributeA ) );
        assertTrue( attributes.contains( attributeB ) );

        attributes = attributeService.getPatientAttributesByValueType( PatientAttribute.TYPE_INT );
        assertEquals( 1, attributes.size() );
        assertTrue( attributes.contains( attributeC ) );

    }

    @Test
    public void testGetPatientAttributesByMandatory()
    {
        attributeA.setMandatory( true );
        attributeB.setMandatory( true );
        attributeC.setMandatory( false );

        attributeService.savePatientAttribute( attributeA );
        attributeService.savePatientAttribute( attributeB );
        attributeService.savePatientAttribute( attributeC );

        Collection<PatientAttribute> attributes = attributeService.getPatientAttributesByMandatory( true );
        assertEquals( 2, attributes.size() );
        assertTrue( attributes.contains( attributeA ) );
        assertTrue( attributes.contains( attributeB ) );
    }

    @Test
    public void testGetPatientAttributeByGroupBy()
    {
        attributeA.setGroupBy( true );
        attributeB.setGroupBy( false );
        attributeC.setGroupBy( false );

        attributeService.savePatientAttribute( attributeA );
        attributeService.savePatientAttribute( attributeB );
        attributeService.savePatientAttribute( attributeC );

        assertEquals( attributeA, attributeService.getPatientAttributeByGroupBy() );
    }

    @Test
    public void testGetPatientAttributesWithoutGroup()
    {
        attributeService.savePatientAttribute( attributeA );
        attributeService.savePatientAttribute( attributeB );
        attributeService.savePatientAttribute( attributeC );

        attributeGroupService.savePatientAttributeGroup( attributeGroup );

        Collection<PatientAttribute> attributes = attributeService.getOptionalPatientAttributesWithoutGroup();
        assertEquals( 1, attributes.size() );
        assertTrue( attributes.contains( attributeC ) );
    }

    @Test
    public void testGetPatientAttributesByDisplayOnVisitSchedule()
    {
        attributeA.setDisplayOnVisitSchedule( true );
        attributeB.setDisplayOnVisitSchedule( true );
        attributeC.setDisplayOnVisitSchedule( false );

        attributeService.savePatientAttribute( attributeA );
        attributeService.savePatientAttribute( attributeB );
        attributeService.savePatientAttribute( attributeC );

        Collection<PatientAttribute> attributes = attributeService.getPatientAttributesByDisplayOnVisitSchedule( true );
        assertEquals( 2, attributes.size() );
        assertTrue( attributes.contains( attributeA ) );
        assertTrue( attributes.contains( attributeB ) );

        attributes = attributeService.getPatientAttributesByDisplayOnVisitSchedule( false );
        assertEquals( 1, attributes.size() );
        assertTrue( attributes.contains( attributeC ) );
    }

}