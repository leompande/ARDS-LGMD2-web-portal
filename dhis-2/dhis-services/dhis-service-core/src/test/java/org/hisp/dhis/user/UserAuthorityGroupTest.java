package org.hisp.dhis.user;

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

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.junit.Test;

public class UserAuthorityGroupTest
    extends DhisSpringTest
{
    private GenericIdentifiableObjectStore<UserAuthorityGroup> userAuthorityGroupStore;

    @Override
    @SuppressWarnings("unchecked")
    public void setUpTest() throws Exception
    {
        userAuthorityGroupStore = (GenericIdentifiableObjectStore<UserAuthorityGroup>) getBean( "org.hisp.dhis.user.UserAuthorityGroupStore" );
    }
    
    @Test
    public void testBasicUserAuthorityGroup()
        throws Exception
    {
        String name = "UserAuthorityGroup";
        String name1 = "UserAuthorityGroup1";
        String name2 = "UserAuthorityGroup2";

        // Test addUserAuthorityGroup
        UserAuthorityGroup userAuthorityGroup = new UserAuthorityGroup();
        userAuthorityGroup.setName( name );
        userAuthorityGroupStore.save( userAuthorityGroup );
        assertEquals( userAuthorityGroupStore.get( userAuthorityGroup.getId() ).getName(), name );

        // Test updateUserAuthorityGroup
        userAuthorityGroup.setName( name1 );
        userAuthorityGroupStore.update( userAuthorityGroup );
        assertEquals( userAuthorityGroup.getName(), name1 );

        // Test getUserAuthorityGroup
        assertEquals( userAuthorityGroupStore.get( userAuthorityGroup.getId() ).getName(), name1 );
        assertEquals( userAuthorityGroupStore.get( userAuthorityGroup.getId() ).getClass(), userAuthorityGroup
            .getClass() );

        // Test getAllUserAuthorityGroups
        UserAuthorityGroup userAuthorityGroup2 = new UserAuthorityGroup();
        userAuthorityGroup2.setName( name2 );
        userAuthorityGroupStore.save( userAuthorityGroup2 );

        // Test deleteUserAuthorityGroup
        assertEquals( userAuthorityGroupStore.getAll().size(), 2 );
        userAuthorityGroupStore.delete( userAuthorityGroup2 );
        assertEquals( userAuthorityGroupStore.getAll().size(), 1 );
    }
}
