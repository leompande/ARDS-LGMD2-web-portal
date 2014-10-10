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

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Transactional
public class DefaultUserGroupService
    implements UserGroupService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericIdentifiableObjectStore<UserGroup> userGroupStore;

    public void setUserGroupStore( GenericIdentifiableObjectStore<UserGroup> userGroupStore )
    {
        this.userGroupStore = userGroupStore;
    }

    // -------------------------------------------------------------------------
    // UserGroup
    // -------------------------------------------------------------------------

    @Override
    public void addUserGroup( UserGroup userGroup )
    {
        userGroupStore.save( userGroup );
    }

    @Override
    public void deleteUserGroup( UserGroup userGroup )
    {
        userGroupStore.delete( userGroup );
    }

    @Override
    public void updateUserGroup( UserGroup userGroup )
    {
        userGroupStore.update( userGroup );
    }

    @Override
    public Collection<UserGroup> getAllUserGroups()
    {
        return userGroupStore.getAll();
    }

    @Override
    public UserGroup getUserGroup( int userGroupId )
    {
        return userGroupStore.get( userGroupId );
    }

    @Override
    public UserGroup getUserGroup( String uid )
    {
        return userGroupStore.getByUid( uid );
    }

    @Override
    public List<UserGroup> getUserGroupByName( String name )
    {
        return userGroupStore.getAllEqName( name );
    }

    @Override
    public int getUserGroupCount()
    {
        return userGroupStore.getCount();
    }

    @Override
    public int getUserGroupCountByName( String name )
    {
        return userGroupStore.getCountLikeName( name );
    }

    @Override
    public List<UserGroup> getUserGroupsBetween( int first, int max )
    {
        return userGroupStore.getAllOrderedName( first, max );
    }

    @Override
    public List<UserGroup> getUserGroupsBetweenByName( String name, int first, int max )
    {
        return userGroupStore.getAllLikeNameOrderedName( name, first, max );
    }
}
