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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.AuditLogUtil;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.system.filter.UserCredentialsCanUpdateFilter;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Chau Thu Tran
 */
@Transactional
public class DefaultUserService
    implements UserService
{
    private static final Log log = LogFactory.getLog( DefaultUserService.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private UserStore userStore;

    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }

    private UserCredentialsStore userCredentialsStore;

    public void setUserCredentialsStore( UserCredentialsStore userCredentialsStore )
    {
        this.userCredentialsStore = userCredentialsStore;
    }

    private UserAuthorityGroupStore userAuthorityGroupStore;

    public void setUserAuthorityGroupStore( UserAuthorityGroupStore userAuthorityGroupStore )
    {
        this.userAuthorityGroupStore = userAuthorityGroupStore;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private SystemSettingManager systemSettingManager;

    @Autowired
    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    // -------------------------------------------------------------------------
    // Implementing methods
    // -------------------------------------------------------------------------

    public boolean isSuperUser( UserCredentials userCredentials )
    {
        if ( userCredentials == null )
        {
            return false;
        }

        for ( UserAuthorityGroup group : userCredentials.getUserAuthorityGroups() )
        {
            if ( group.getAuthorities().contains( "ALL" ) )
            {
                return true;
            }
        }

        return false;
    }

    public boolean isLastSuperUser( UserCredentials userCredentials )
    {
        if ( !isSuperUser( userCredentials ) )
        {
            return false; // Cannot be last if not super user
        }

        Collection<UserCredentials> users = userCredentialsStore.getAllUserCredentials();

        for ( UserCredentials user : users )
        {
            if ( isSuperUser( user ) && !user.equals( userCredentials ) )
            {
                return false;
            }
        }

        return true;
    }

    public boolean isSuperRole( UserAuthorityGroup userAuthorityGroup )
    {
        if ( userAuthorityGroup == null )
        {
            return false;
        }

        return (userAuthorityGroup.getAuthorities().contains( "ALL" )) ? true : false;
    }

    public boolean isLastSuperRole( UserAuthorityGroup userAuthorityGroup )
    {
        Collection<UserAuthorityGroup> groups = userAuthorityGroupStore.getAll();

        for ( UserAuthorityGroup group : groups )
        {
            if ( isSuperRole( group ) && group.getId() != userAuthorityGroup.getId() )
            {
                return false;
            }
        }

        return true;
    }

    // -------------------------------------------------------------------------
    // User
    // -------------------------------------------------------------------------

    public int addUser( User user )
    {
        AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), user, AuditLogUtil.ACTION_CREATE );

        return userStore.save( user );
    }

    public void updateUser( User user )
    {
        userStore.update( user );

        AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), user, AuditLogUtil.ACTION_UPDATE );
    }

    public void deleteUser( User user )
    {
        AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), user, AuditLogUtil.ACTION_DELETE );

        userCredentialsStore.deleteUserCredentials( user.getUserCredentials() );

        userStore.delete( user );
    }

    public Collection<User> getAllUsers()
    {
        return userStore.getAll();
    }

    @Override
    public List<User> getAllUsersBetween( int first, int max )
    {
        return userStore.getAllOrderedName( first, max );
    }

    @Override
    public List<User> getAllUsersBetweenByName( String name, int first, int max )
    {
        return userStore.getAllLikeNameOrderedName( name, first, max );
    }

    @Override
    public Collection<User> getUsersByLastUpdated( Date lastUpdated )
    {
        return userStore.getAllGeLastUpdated( lastUpdated );
    }

    public User getUser( int userId )
    {
        return userStore.get( userId );
    }

    public User getUser( String uid )
    {
        return userStore.getByUid( uid );
    }

    public Collection<UserCredentials> getUsers( final Collection<Integer> identifiers, User user )
    {
        Collection<UserCredentials> userCredentials = getAllUserCredentials();

        FilterUtils.filter( userCredentials, new UserCredentialsCanUpdateFilter( user ) );

        return identifiers == null ? userCredentials : FilterUtils.filter( userCredentials,
            new Filter<UserCredentials>()
            {
                public boolean retain( UserCredentials object )
                {
                    return identifiers.contains( object.getId() );
                }
            } );
    }

    public List<User> getUsersByUid( List<String> uids )
    {
        return userStore.getByUid( uids );
    }

    public Collection<UserCredentials> getUsersByOrganisationUnitBetween( OrganisationUnit unit, int first, int max )
    {
        return userCredentialsStore.getUsersByOrganisationUnitBetween( unit, first, max );
    }

    public Collection<UserCredentials> getUsersByOrganisationUnitBetweenByName( OrganisationUnit unit, String userName,
        int first, int max )
    {
        return userCredentialsStore.getUsersByOrganisationUnitBetweenByName( unit, userName, first, max );
    }

    public int getUsersByOrganisationUnitCount( OrganisationUnit unit )
    {
        return userCredentialsStore.getUsersByOrganisationUnitCount( unit );
    }

    public int getUsersByOrganisationUnitCountByName( OrganisationUnit unit, String userName )
    {
        return userCredentialsStore.getUsersByOrganisationUnitCountByName( unit, userName );
    }

    public Collection<User> getUsersByPhoneNumber( String phoneNumber )
    {
        return userStore.getUsersByPhoneNumber( phoneNumber );
    }

    public Collection<User> getUsersByName( String name )
    {
        return userStore.getUsersByName( name );
    }

    public Collection<User> getUsersWithoutOrganisationUnit()
    {
        return userStore.getUsersWithoutOrganisationUnit();
    }

    public int getUsersWithoutOrganisationUnitCount()
    {
        return userCredentialsStore.getUsersWithoutOrganisationUnitCount();
    }

    public int getUsersWithoutOrganisationUnitCountByName( String userName )
    {
        return userCredentialsStore.getUsersWithoutOrganisationUnitCountByName( userName );
    }

    public User searchForUser( String query )
    {
        User user = userStore.getByUid( query );

        if ( user == null )
        {
            UserCredentials credentials = userCredentialsStore.getUserCredentialsByUsername( query );
            user = credentials != null ? credentials.getUser() : null;
        }

        return user;
    }

    public List<User> queryForUsers( String query )
    {
        List<User> users = new ArrayList<User>();

        User uidUser = userStore.getByUid( query );

        if ( uidUser != null )
        {
            users.add( uidUser );
        }

        users.addAll( userStore.getAllLikeNameOrderedName( query, 0, 1000 ) ); //TODO

        return users;
    }

    // -------------------------------------------------------------------------
    // UserAuthorityGroup
    // -------------------------------------------------------------------------

    public int addUserAuthorityGroup( UserAuthorityGroup userAuthorityGroup )
    {
        return userAuthorityGroupStore.save( userAuthorityGroup );
    }

    public void updateUserAuthorityGroup( UserAuthorityGroup userAuthorityGroup )
    {
        userAuthorityGroupStore.update( userAuthorityGroup );
    }

    public void deleteUserAuthorityGroup( UserAuthorityGroup userAuthorityGroup )
    {
        userAuthorityGroupStore.delete( userAuthorityGroup );
    }

    public Collection<UserAuthorityGroup> getAllUserAuthorityGroups()
    {
        return userAuthorityGroupStore.getAll();
    }

    public UserAuthorityGroup getUserAuthorityGroup( int id )
    {
        return userAuthorityGroupStore.get( id );
    }

    public UserAuthorityGroup getUserAuthorityGroup( String uid )
    {
        return userAuthorityGroupStore.getByUid( uid );
    }

    public UserAuthorityGroup getUserAuthorityGroupByName( String name )
    {
        return userAuthorityGroupStore.getByName( name );
    }

    public Collection<UserAuthorityGroup> getUserRolesBetween( int first, int max )
    {
        return userAuthorityGroupStore.getAllOrderedName( first, max );
    }

    public Collection<UserAuthorityGroup> getUserRolesBetweenByName( String name, int first, int max )
    {
        return userAuthorityGroupStore.getAllLikeNameOrderedName( name, first, max );
    }

    public int getUserRoleCount()
    {
        return userAuthorityGroupStore.getCount();
    }

    public int getUserRoleCountByName( String name )
    {
        return userAuthorityGroupStore.getCountLikeName( name );
    }

    public void assignDataSetToUserRole( DataSet dataSet )
    {
        User currentUser = currentUserService.getCurrentUser();

        if ( !currentUserService.currentUserIsSuper() && currentUser != null )
        {
            UserCredentials userCredentials = getUserCredentials( currentUser );

            for ( UserAuthorityGroup userAuthorityGroup : userCredentials.getUserAuthorityGroups() )
            {
                userAuthorityGroup.getDataSets().add( dataSet );

                updateUserAuthorityGroup( userAuthorityGroup );
            }
        }
    }

    // -------------------------------------------------------------------------
    // UserCredentials
    // -------------------------------------------------------------------------

    public User addUserCredentials( UserCredentials userCredentials )
    {
        return userCredentialsStore.addUserCredentials( userCredentials );
    }

    public void updateUserCredentials( UserCredentials userCredentials )
    {
        userCredentialsStore.updateUserCredentials( userCredentials );
    }

    public Collection<UserCredentials> getAllUserCredentials()
    {
        return userCredentialsStore.getAllUserCredentials();
    }

    public UserCredentials getUserCredentials( User user )
    {
        return userCredentialsStore.getUserCredentials( user );
    }

    public UserCredentials getUserCredentialsByUsername( String username )
    {
        return userCredentialsStore.getUserCredentialsByUsername( username );
    }

    public Collection<UserCredentials> getUsersBetween( int first, int max )
    {
        return userCredentialsStore.getUsersBetween( first, max );
    }

    public Collection<UserCredentials> getUsersBetweenByName( String username, int first, int max )
    {
        return userCredentialsStore.getUsersBetweenByName( username, first, max );
    }

    public int getUserCount()
    {
        return userCredentialsStore.getUserCount();
    }

    public int getUserCountByName( String userName )
    {
        return userCredentialsStore.getUserCountByName( userName );
    }

    public Collection<UserCredentials> getUsersWithoutOrganisationUnitBetween( int first, int max )
    {
        return userCredentialsStore.getUsersWithoutOrganisationUnitBetween( first, max );
    }

    public Collection<UserCredentials> getUsersWithoutOrganisationUnitBetweenByName( String username, int first, int max )
    {
        return userCredentialsStore.getUsersWithoutOrganisationUnitBetweenByName( username, first, max );
    }

    public Collection<UserCredentials> searchUsersByName( String name )
    {
        return userCredentialsStore.searchUsersByName( name );
    }

    public Collection<UserCredentials> searchUsersByName( String name, int first, int max )
    {
        return userCredentialsStore.searchUsersByName( name, first, max );
    }

    public void setLastLogin( String username )
    {
        UserCredentials credentials = getUserCredentialsByUsername( username );
        credentials.setLastLogin( new Date() );
        updateUserCredentials( credentials );
    }

    public Collection<UserCredentials> getSelfRegisteredUserCredentials( int first, int max )
    {
        return userCredentialsStore.getSelfRegisteredUserCredentials( first, max );
    }

    public int getSelfRegisteredUserCredentialsCount()
    {
        return userCredentialsStore.getSelfRegisteredUserCredentialsCount();
    }

    public Collection<UserCredentials> getInactiveUsers( int months )
    {
        Calendar cal = PeriodType.createCalendarInstance();
        cal.add( Calendar.MONTH, (months * -1) );

        return userCredentialsStore.getInactiveUsers( cal.getTime() );
    }

    public Collection<UserCredentials> getInactiveUsers( int months, int first, int max )
    {
        Calendar cal = PeriodType.createCalendarInstance();
        cal.add( Calendar.MONTH, (months * -1) );

        return userCredentialsStore.getInactiveUsers( cal.getTime(), first, max );
    }

    public int getInactiveUsersCount( int months )
    {
        Calendar cal = PeriodType.createCalendarInstance();
        cal.add( Calendar.MONTH, (months * -1) );

        return userCredentialsStore.getInactiveUsersCount( cal.getTime() );
    }

    public int getActiveUsersCount( int days )
    {
        Calendar cal = PeriodType.createCalendarInstance();
        cal.add( Calendar.DAY_OF_YEAR, (days * -1) );

        return userCredentialsStore.getActiveUsersCount( cal.getTime() );
    }

    public int getActiveUsersCount( Date since )
    {
        return userCredentialsStore.getActiveUsersCount( since );
    }

    // -------------------------------------------------------------------------
    // UserSettings
    // -------------------------------------------------------------------------

    public void addUserSetting( UserSetting userSetting )
    {
        userCredentialsStore.addUserSetting( userSetting );
    }

    public void addOrUpdateUserSetting( UserSetting userSetting )
    {
        UserSetting setting = getUserSetting( userSetting.getUser(), userSetting.getName() );

        if ( setting != null )
        {
            setting.mergeWith( userSetting );
            updateUserSetting( setting );
        }
        else
        {
            addUserSetting( userSetting );
        }
    }

    public void updateUserSetting( UserSetting userSetting )
    {
        userCredentialsStore.updateUserSetting( userSetting );
    }

    public void deleteUserSetting( UserSetting userSetting )
    {
        userCredentialsStore.deleteUserSetting( userSetting );
    }

    public Collection<UserSetting> getAllUserSettings( User user )
    {
        return userCredentialsStore.getAllUserSettings( user );
    }

    public Collection<UserSetting> getUserSettings( String name )
    {
        return userCredentialsStore.getUserSettings( name );
    }

    public UserSetting getUserSetting( User user, String name )
    {
        return userCredentialsStore.getUserSetting( user, name );
    }

    public Serializable getUserSettingValue( User user, String name, Serializable defaultValue )
    {
        UserSetting setting = getUserSetting( user, name );

        return setting != null && setting.getValue() != null ? setting.getValue() : defaultValue;
    }

    public Map<User, Serializable> getUserSettings( String name, Serializable defaultValue )
    {
        Map<User, Serializable> map = new HashMap<User, Serializable>();

        for ( UserSetting setting : userCredentialsStore.getUserSettings( name ) )
        {
            map.put( setting.getUser(), setting.getValue() != null ? setting.getValue() : defaultValue );
        }

        return map;
    }

    public Collection<User> getUsersByOrganisationUnits( Collection<OrganisationUnit> units )
    {
        return userStore.getUsersByOrganisationUnits( units );
    }

    public void removeUserSettings( User user )
    {
        userStore.removeUserSettings( user );
    }

    public Collection<String> getUsernames( String query, Integer max )
    {
        return userCredentialsStore.getUsernames( query, max );
    }

    @Override
    public int countDataSetUserAuthorityGroups( DataSet dataSet )
    {
        return userAuthorityGroupStore.countDataSetUserAuthorityGroups( dataSet );
    }

    @Override
    public boolean credentialsNonExpired( UserCredentials credentials )
    {
        int credentialsExpires = systemSettingManager.credentialsExpires();

        if ( credentialsExpires == 0 )
        {
            return true;
        }

        int months = DateUtils.monthsBetween( credentials.getPasswordLastUpdated(), new Date() );

        return months < credentialsExpires;
    }
}
