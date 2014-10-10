package org.hisp.dhis.organisationunit;

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

import org.hisp.dhis.DhisSpringTest;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class OrganisationUnitStoreTest
    extends DhisSpringTest
{
    private OrganisationUnitLevelStore organisationUnitLevelStore;

    @Override
    public void setUpTest()
    {
        organisationUnitLevelStore = (OrganisationUnitLevelStore) getBean( OrganisationUnitLevelStore.ID );
    }

    // -------------------------------------------------------------------------
    // OrganisationUnitLevel
    // -------------------------------------------------------------------------

    @Test
    public void testAddGetOrganisationUnitLevel()
    {
        OrganisationUnitLevel levelA = new OrganisationUnitLevel( 1, "National" );
        OrganisationUnitLevel levelB = new OrganisationUnitLevel( 2, "District" );

        int idA = organisationUnitLevelStore.save( levelA );
        int idB = organisationUnitLevelStore.save( levelB );

        assertEquals( levelA, organisationUnitLevelStore.get( idA ) );
        assertEquals( levelB, organisationUnitLevelStore.get( idB ) );
    }

    @Test
    public void testGetOrganisationUnitLevels()
    {
        OrganisationUnitLevel levelA = new OrganisationUnitLevel( 1, "National" );
        OrganisationUnitLevel levelB = new OrganisationUnitLevel( 2, "District" );

        organisationUnitLevelStore.save( levelA );
        organisationUnitLevelStore.save( levelB );

        Collection<OrganisationUnitLevel> actual = organisationUnitLevelStore.getAll();

        assertNotNull( actual );
        assertEquals( 2, actual.size() );
        assertTrue( actual.contains( levelA ) );
        assertTrue( actual.contains( levelB ) );
    }

    @Test
    public void testRemoveOrganisationUnitLevel()
    {
        OrganisationUnitLevel levelA = new OrganisationUnitLevel( 1, "National" );
        OrganisationUnitLevel levelB = new OrganisationUnitLevel( 2, "District" );

        int idA = organisationUnitLevelStore.save( levelA );
        int idB = organisationUnitLevelStore.save( levelB );

        assertNotNull( organisationUnitLevelStore.get( idA ) );
        assertNotNull( organisationUnitLevelStore.get( idB ) );

        organisationUnitLevelStore.delete( levelA );

        assertNull( organisationUnitLevelStore.get( idA ) );
        assertNotNull( organisationUnitLevelStore.get( idB ) );

        organisationUnitLevelStore.delete( levelB );

        assertNull( organisationUnitLevelStore.get( idA ) );
        assertNull( organisationUnitLevelStore.get( idB ) );
    }
}