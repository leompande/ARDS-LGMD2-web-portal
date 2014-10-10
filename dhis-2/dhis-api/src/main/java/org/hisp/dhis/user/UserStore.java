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

import java.util.Collection;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Nguyen Hong Duc
 */
public interface UserStore
    extends GenericIdentifiableObjectStore<User>
{
    String ID = UserStore.class.getName();

    /**
     * Returns a Collection of the Users which are not associated with any
     * OrganisationUnits.
     * 
     * @return a Collection of Users.
     */
    Collection<User> getUsersWithoutOrganisationUnit();

    /**
     * Returns a Collection of the Users which are associated with
     * OrganisationUnits.
     * 
     * @param orgunits a Collection of the organization units.
     * 
     * @return a Collection of Users.
     */
    Collection<User> getUsersByOrganisationUnits( Collection<OrganisationUnit> orgunits );

    /**
     * Returns a Collection of Users which are having given Phone number.
     * 
     * @param phoneNumber
     * @return a Collection of Users.
     */
    Collection<User> getUsersByPhoneNumber( String phoneNumber );

    /**
     * Removes all user settings associated with the given user.
     * 
     * @param user the user.
     */
    void removeUserSettings( User user );

    Collection<User> getUsersByName( String name );

}
