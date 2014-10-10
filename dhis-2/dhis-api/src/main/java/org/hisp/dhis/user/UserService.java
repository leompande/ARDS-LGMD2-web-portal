package org.hisp.dhis.user;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

/**
 * @author Chau Thu Tran
 * @version $Id$
 */
public interface UserService
{
    String ID = UserService.class.getName();

    boolean isSuperUser( UserCredentials userCredentials );

    boolean isLastSuperUser( UserCredentials userCredentials );

    boolean isSuperRole( UserAuthorityGroup userAuthorityGroup );

    boolean isLastSuperRole( UserAuthorityGroup userAuthorityGroup );

    // -------------------------------------------------------------------------
    // User
    // -------------------------------------------------------------------------

    /**
     * Adds a User.
     *
     * @param user the User to add.
     * @return the generated identifier.
     */
    int addUser( User user );

    /**
     * Updates a User.
     *
     * @param user the User to update.
     */
    void updateUser( User user );

    /**
     * Retrieves the User with the given identifier.
     *
     * @param id the identifier of the User to retrieve.
     * @return the User.
     */
    User getUser( int id );

    /**
     * Retrieves the User with the given unique identifier.
     *
     * @param id the identifier of the User to retrieve.
     * @return the User.
     */
    User getUser( String uid );

    /**
     * Returns a Collection of all Users.
     *
     * @return a Collection of Users.
     */
    Collection<User> getAllUsers();

    List<User> getAllUsersBetween( int first, int max );
    
    List<User> getAllUsersBetweenByName( String name, int first, int max );

    Collection<User> getUsersByLastUpdated( Date lastUpdated );

    /**
     * Returns a Collection of the Users which are not associated with any
     * OrganisationUnits.
     *
     * @return a Collection of Users.
     */
    Collection<User> getUsersWithoutOrganisationUnit();

    /**
     * Returns a Collection of the Users which are associated with OrganisationUnits.
     *
     * @param units a Collection of the organization units.
     *
     * @return a Collection of Users.
     */
    Collection<User> getUsersByOrganisationUnits( Collection<OrganisationUnit> units );

    /**
     * Returns a Collection of Users which are having given Phone number.
     *
     * @param phoneNumber
     * @return a Collection of Users.
     */
    Collection<User> getUsersByPhoneNumber( String phoneNumber );

    /**
     * Deletes a User.
     *
     * @param user the User to delete.
     */
    void deleteUser( User user );

    int getUserCount();

    int getUserCountByName( String name );

    int getUsersWithoutOrganisationUnitCount();

    int getUsersWithoutOrganisationUnitCountByName( String name );

    int getUsersByOrganisationUnitCount( OrganisationUnit orgUnit );

    int getUsersByOrganisationUnitCountByName( OrganisationUnit orgUnit, String name );
    
    List<User> getUsersByUid( List<String> uids );
    
    User searchForUser( String query );
    
    List<User> queryForUsers( String query );

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
     * Updates the last login date of UserCredentials with the given username
     * with the current date.
     *
     * @param username the username of the UserCredentials.
     */
    void setLastLogin( String username );

    /**
     * Get the UserCredentials with the corresponding identifiers.
     *
     * @param identifiers the collection of identifiers.
     * @return a collection of users.
     */
    Collection<UserCredentials> getUsers( Collection<Integer> identifiers, User user );

    Collection<UserCredentials> searchUsersByName( String key );

    Collection<UserCredentials> searchUsersByName( String name, int first, int max );

    Collection<UserCredentials> getUsersBetween( int first, int max );

    Collection<UserCredentials> getUsersBetweenByName( String name, int first, int max );

    Collection<UserCredentials> getUsersWithoutOrganisationUnitBetween( int first, int max );

    Collection<UserCredentials> getUsersWithoutOrganisationUnitBetweenByName( String name, int first, int max );

    Collection<UserCredentials> getUsersByOrganisationUnitBetween( OrganisationUnit orgUnit, int first, int max );

    Collection<UserCredentials> getUsersByOrganisationUnitBetweenByName( OrganisationUnit orgUnit, String name, int first, int max );

    Collection<UserCredentials> getSelfRegisteredUserCredentials( int first, int max );
    
    int getSelfRegisteredUserCredentialsCount();
    
    Collection<UserCredentials> getInactiveUsers( int months );

    Collection<UserCredentials> getInactiveUsers( int months, int first, int max );

    int getInactiveUsersCount( int months );

    int getActiveUsersCount( int days );
    
    int getActiveUsersCount( Date since );

    // -------------------------------------------------------------------------
    // UserAuthorityGroup
    // -------------------------------------------------------------------------

    /**
     * Adds a UserAuthorityGroup.
     *
     * @param userAuthorityGroup the UserAuthorityGroup.
     * @return the generated identifier.
     */
    int addUserAuthorityGroup( UserAuthorityGroup userAuthorityGroup );

    /**
     * Updates a UserAuthorityGroup.
     *
     * @param userAuthorityGroup the UserAuthorityGroup.
     */
    void updateUserAuthorityGroup( UserAuthorityGroup userAuthorityGroup );

    /**
     * Retrieves the UserAuthorityGroup with the given identifier.
     *
     * @param id the identifier of the UserAuthorityGroup to retrieve.
     * @return the UserAuthorityGroup.
     */
    UserAuthorityGroup getUserAuthorityGroup( int id );

    /**
     * Retrieves the UserAuthorityGroup with the given identifier.
     *
     * @param id the identifier of the UserAuthorityGroup to retrieve.
     * @return the UserAuthorityGroup.
     */
    UserAuthorityGroup getUserAuthorityGroup( String uid );

    /**
     * Retrieves the UserAuthorityGroup with the given name.
     *
     * @param name the name of the UserAuthorityGroup to retrieve.
     * @return the UserAuthorityGroup.
     */
    UserAuthorityGroup getUserAuthorityGroupByName( String name );

    /**
     * Deletes a UserAuthorityGroup.
     *
     * @param userAuthorityGroup the UserAuthorityGroup to delete.
     */
    void deleteUserAuthorityGroup( UserAuthorityGroup userAuthorityGroup );

    /**
     * Retrieves all UserAuthorityGroups.
     *
     * @return a Collectio of UserAuthorityGroups.
     */
    Collection<UserAuthorityGroup> getAllUserAuthorityGroups();

    /**
     * Retrieves all UserAuthorityGroups.
     *
     * @return a Collectio of UserAuthorityGroups.
     */
    Collection<UserAuthorityGroup> getUserRolesBetween( int first, int max );

    /**
     * Retrieves all UserAuthorityGroups.
     *
     * @return a Collectio of UserAuthorityGroups.
     */
    Collection<UserAuthorityGroup> getUserRolesBetweenByName( String name, int first, int max );

    void assignDataSetToUserRole( DataSet dataSet );

    int getUserRoleCount();

    int getUserRoleCountByName( String name );

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
     * If a matching UserSetting exists, based on its user and name, it will be
     * updated, if not, the given UserSetting will be added.
     * 
     * @param userSetting the UserSetting.
     */
    void addOrUpdateUserSetting( UserSetting userSetting );
    
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
     * Retrieves a user setting value for the given user and setting name. Returns
     * the given default value if the setting does not exist or the setting value
     * is null.
     * 
     * @param user the user.
     * @param name the setting name.
     * @param defaultValue the default value.
     * @return a setting value.
     */
    Serializable getUserSettingValue( User user, String name, Serializable defaultValue );
    
    /**
     * Retrieves all UserSettings for the given User.
     *
     * @param user the User.
     * @return a Collection of UserSettings.
     */
    Collection<UserSetting> getAllUserSettings( User user );
    
    Collection<UserSetting> getUserSettings( String name );

    /**
     * Deletes a UserSetting.
     *
     * @param userSetting the UserSetting to delete.
     */
    void deleteUserSetting( UserSetting userSetting );

    /**
     * Returns a Map with an entry for all UserSettings with the given name where
     * the key is the user and the value is the value of the user setting.
     *
     * @param name         the name of the UserSetting.
     * @param defaultValue the value to return if the UserSetting value is null.
     * @return a Map.
     */
    Map<User, Serializable> getUserSettings( String name, Serializable defaultValue );

    /**
     * Removes all user settings associated with the given user.
     * 
     * @param user the user.
     */
    void removeUserSettings( User user );
    
    Collection<User> getUsersByName( String name );  
    
    Collection<String> getUsernames( String query, Integer max );

    int countDataSetUserAuthorityGroups( DataSet dataSet );

    boolean credentialsNonExpired( UserCredentials credentials );
}
