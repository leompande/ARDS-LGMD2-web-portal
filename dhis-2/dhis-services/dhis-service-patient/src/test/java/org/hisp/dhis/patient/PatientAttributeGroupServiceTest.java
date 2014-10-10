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
import java.util.List;

import org.hisp.dhis.DhisSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chau Thu Tran
 * 
 * @version $ PatientAttributeGroupServiceTest.java Nov 5, 2013 3:11:48 PM $
 */
public class PatientAttributeGroupServiceTest
    extends DhisSpringTest
{
    @Autowired
    private PatientAttributeGroupService attributeGroupService;

    @Autowired
    private PatientAttributeService attributeService;

    private PatientAttributeGroup attributeGroupA;

    private PatientAttributeGroup attributeGroupB;

    @Override
    public void setUpTest()
    {
        PatientAttribute attributeA = createPatientAttribute( 'A' );
        PatientAttribute attributeB = createPatientAttribute( 'B' );
        PatientAttribute attributeC = createPatientAttribute( 'C' );

        attributeService.savePatientAttribute( attributeA );
        attributeService.savePatientAttribute( attributeB );
        attributeService.savePatientAttribute( attributeC );

        List<PatientAttribute> attributesA = new ArrayList<PatientAttribute>();
        attributesA.add( attributeA );
        attributesA.add( attributeB );

        List<PatientAttribute> attributesB = new ArrayList<PatientAttribute>();
        attributesB.add( attributeC );

        attributeGroupA = createPatientAttributeGroup( 'A', attributesA );
        attributeGroupB = createPatientAttributeGroup( 'B', attributesB );
    }

    @Test
    public void testSavePatientAttributeGroup()
    {
        int idA = attributeGroupService.savePatientAttributeGroup( attributeGroupA );
        int idB = attributeGroupService.savePatientAttributeGroup( attributeGroupB );

        assertNotNull( attributeGroupService.getPatientAttributeGroup( idA ) );
        assertNotNull( attributeGroupService.getPatientAttributeGroup( idB ) );
    }

    @Test
    public void testDeletePatientAttributeGroup()
    {
        int idA = attributeGroupService.savePatientAttributeGroup( attributeGroupA );
        int idB = attributeGroupService.savePatientAttributeGroup( attributeGroupB );

        assertNotNull( attributeGroupService.getPatientAttributeGroup( idA ) );
        assertNotNull( attributeGroupService.getPatientAttributeGroup( idB ) );

        attributeGroupService.deletePatientAttributeGroup( attributeGroupA );

        assertNull( attributeGroupService.getPatientAttributeGroup( idA ) );
        assertNotNull( attributeGroupService.getPatientAttributeGroup( idB ) );

        attributeGroupService.deletePatientAttributeGroup( attributeGroupB );

        assertNull( attributeGroupService.getPatientAttributeGroup( idA ) );
        assertNull( attributeGroupService.getPatientAttributeGroup( idB ) );
    }

    @Test
    public void testUpdatePatientAttributeGroup()
    {
        int idA = attributeGroupService.savePatientAttributeGroup( attributeGroupA );

        assertNotNull( attributeGroupService.getPatientAttributeGroup( idA ) );

        attributeGroupA.setName( "B" );
        attributeGroupService.updatePatientAttributeGroup( attributeGroupA );

        assertEquals( "B", attributeGroupService.getPatientAttributeGroup( idA ).getName() );
    }

    @Test
    public void testGetPatientAttributeGroupById()
    {
        int idA = attributeGroupService.savePatientAttributeGroup( attributeGroupA );
        int idB = attributeGroupService.savePatientAttributeGroup( attributeGroupB );

        assertEquals( attributeGroupA, attributeGroupService.getPatientAttributeGroup( idA ) );
        assertEquals( attributeGroupB, attributeGroupService.getPatientAttributeGroup( idB ) );
    }

    @Test
    public void testGetPatientAttributeGroupByName()
    {
        int idA = attributeGroupService.savePatientAttributeGroup( attributeGroupA );

        assertNotNull( attributeGroupService.getPatientAttributeGroup( idA ) );
        assertEquals( attributeGroupA.getName(),
            attributeGroupService.getPatientAttributeGroupByName( "PatientAttributeGroupA" ).getName() );
    }

    @Test
    public void testGetAllPatientAttributeGroups()
    {
        attributeGroupService.savePatientAttributeGroup( attributeGroupA );
        attributeGroupService.savePatientAttributeGroup( attributeGroupB );

        assertTrue( equals( attributeGroupService.getAllPatientAttributeGroups(), attributeGroupA, attributeGroupB ) );
    }

}
