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

import org.hisp.dhis.organisationunit.OrganisationUnit;

import java.util.Collection;
import java.util.Date;

/**
 * @author Lars Helge Overland
 */
public interface UserCredentialsStore
{
    String ID = UserCredentialsStore.class.getName();

    // -------------------------------------------------------------------------
    // UserCredentials
    // -------------------------------------------------------------------------

    /**
     * Adds a UserCredentials.
     *
     * @param userCredentials the UserCredentials to add.
     * @return the User which the UserCredentials is associated with.
     */
    User addUserCredentials( UserCredentials userCredentials );

    /**
     * Updates a UserCredentials.
     *
     * @param userCredentials the UserCredentials to update.
     */
    void updateUserCredentials( UserCredentials userCredentials );

    /**
     * Retrieves the UserCredentials of the given User.
     *
     * @param user the User.
     * @return the UserCredentials.
     */
    UserCredentials getUserCredentials( User user );

    /**
     * Retrieves the UserCredentials associated with the User with the given
     * name.
     *
     * @param username the name of the User.
     * @return the UserCredentials.
     */
    UserCredentials getUserCredentialsByUsername( String username );

    /**
     * Retrieves all UserCredentials.
     *
     * @return a Collection of UserCredentials.
     */
    Collection<UserCredentials> getAllUserCredentials();

    /**
     * Deletes a UserCredentials.
     *
     * @param userCredentials the UserCredentials.
     */
    void deleteUserCredentials( UserCredentials userCredentials );

    Collection<UserCredentials> searchUsersByName( String key );

    Collection<UserCredentials> searchUsersByName( String key, int first, int max );

    Collection<UserCredentials> getUsersBetween( int first, int max );

    Collection<UserCredentials> getUsersBetweenByName( String name, int first, int max );

    Collection<UserCredentials> getUsersWithoutOrganisationUnitBetween( int first, int max );

    Collection<UserCredentials> getUsersWithoutOrganisationUnitBetweenByName( String name, int first, int max );

    Collection<UserCredentials> getUsersByOrganisationUnitBetween( OrganisationUnit orgUnit, int first, int max );

    Collection<UserCredentials> getUsersByOrganisationUnitBetweenByName( OrganisationUnit orgUnit, String name,
        int first, int max );

    Collection<UserCredentials> getSelfRegisteredUserCredentials( int first, int max );

    int getSelfRegisteredUserCredentialsCount();

    Collection<UserCredentials> getInactiveUsers( Date date );

    Collection<UserCredentials> getInactiveUsers( Date date, int first, int max );

    int getInactiveUsersCount( Date date );

    int getActiveUsersCount( Date date );

    int getUserCount();

    int getUserCountByName( String name );

    int getUsersWithoutOrganisationUnitCount();

    int getUsersWithoutOrganisationUnitCountByName( String name );

    int getUsersByOrganisationUnitCount( OrganisationUnit orgUnit );

    int getUsersByOrganisationUnitCountByName( OrganisationUnit orgUnit, String name );

    // -------------------------------------------------------------------------
    // UserSettings
    // -------------------------------------------------------------------------

    /**
     * Adds a UserSetting.
     *
     * @param userSetting the UserSetting to add.
     */
    void addUserSetting( UserSetting userSetting );

    /**
     * Updates a UserSetting.
     *
     * @param userSetting the UserSetting to update.
     */
    void updateUserSetting( UserSetting userSetting );

    /**
     * Retrieves the UserSetting associated with the given User for the given
     * UserSetting name.
     *
     * @param user the User.
     * @param name the name of the UserSetting.
     * @return the UserSetting.
     */
    UserSetting getUserSetting( User user, String name );

    /**
     * Retrieves all UserSettings for the given User.
     *
     * @param user the User.
     * @return a Collection of UserSettings.
     */
    Collection<UserSetting> getAllUserSettings( User user );

    /**
     * Deletes a UserSetting.
     *
     * @param userSetting the UserSetting to delete.
     */
    void deleteUserSetting( UserSetting userSetting );

    /**
     * Returns all UserSettings with the given name.
     *
     * @param name the name.
     * @return a Collection of UserSettings.
     */
    Collection<UserSetting> getUserSettings( String name );

    Collection<String> getUsernames( String key, Integer max );

}
