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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.junit.Test;

/**
 * @author Nguyen Hong Duc
 * @version $Id: UserStoreTest.java 5724 2008-09-18 14:37:01Z larshelg $
 */
public class UserStoreTest
    extends DhisSpringTest
{
    private UserStore userStore;
    
    private UserCredentialsStore userCredentialsStore;

    private OrganisationUnitService organisationUnitService;

    @Override
    public void setUpTest()
        throws Exception
    {
        userStore = (UserStore) getBean( UserStore.ID );
        
        userCredentialsStore = (UserCredentialsStore) getBean( UserCredentialsStore.ID );

        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );
    }

    @Test
    public void testBasicUser()
        throws Exception
    {
        OrganisationUnit unit1 = new OrganisationUnit( "name1", "shortName1", "organisationUnitCode1", new Date(),
            new Date(), true, "comment" );
        OrganisationUnit unit2 = new OrganisationUnit( "name2", "shortName2", "organisationUnitCode2", new Date(),
            new Date(), true, "comment" );
        
        Set<OrganisationUnit> units1 = new HashSet<OrganisationUnit>();       
        
        units1.add(unit1);
        units1.add(unit2);

        organisationUnitService.addOrganisationUnit( unit1 );
        organisationUnitService.addOrganisationUnit( unit2 );
        
        String userName = "User";
        User user = new User();
        user.setSurname( userName );
        user.setFirstName( userName );

        // Test addUser
        int id = userStore.save( user );
        assertEquals( userStore.get( id ).getSurname(), userName );
        assertEquals( userStore.get( id ).getFirstName(), userName );
        assertEquals( 1, userStore.getAll().size(), 1 );
        assertEquals( 1, userStore.getUsersWithoutOrganisationUnit().size() );

        // Test updateUser
        user.setSurname( "User1" );
        user.setOrganisationUnits( units1 );
        userStore.update( user );
        
        assertEquals( userStore.get( id ).getSurname(), "User1" );
        assertEquals( 0, userStore.getUsersWithoutOrganisationUnit().size() );

        // Test getUser
        assertEquals( userStore.get( user.getId() ).getSurname(), "User1" );
        assertEquals( userStore.get( user.getId() ).getFirstName(), userName );
        assertEquals( 2, userStore.get( user.getId() ).getOrganisationUnits().size() );
        assertEquals( userStore.get( user.getId() ).getId(), id );

        // Test getAllUsers
        User user2 = new User();
        Set<OrganisationUnit> units2 = new HashSet<OrganisationUnit>();        
        units2.add(unit2);
        
        user2.setSurname( "User2" );
        user2.setFirstName( "User2" );
        user2.setOrganisationUnits( units2 );
        userStore.save( user2 );

        assertEquals( userStore.getAll().size(), 2 );
        
        assertEquals( 0, userStore.getUsersWithoutOrganisationUnit().size() );

        // Test deleteUser
        User user3 = new User();
        user3.setSurname( "User3" );
        user3.setFirstName( "User3" );
        OrganisationUnit unit3 = new OrganisationUnit( "name3", "shortName3", "organisationUnitCode3", new Date(),
            new Date(), true, "comment" );        
        organisationUnitService.addOrganisationUnit( unit3 );
        Set<OrganisationUnit> units3 = new HashSet<OrganisationUnit>();        
        units3.add(unit3);
        
        user.setOrganisationUnits( units3 );
        userStore.save( user3 );

        assertEquals( userStore.getAll().size(), 3 );
        // delete User3
        assertEquals( userStore.get( user3.getId() ).getSurname(), "User3" );
        userStore.delete( user3 );
        assertEquals( userStore.getAll().size(), 2 );
    }

    @Test
    public void testBasicUserCredentials()
        throws Exception
    {
        // Test addUserCredentials
        String username = "user";
        String password = "password";
        String someone = "someone";
        String iloveyou = "iloveyou";

        User user = new User();
        user.setSurname( username );
        user.setFirstName( username );
        userStore.save( user );

        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setUser( user );
        userCredentials.setUsername( username );
        userCredentials.setPassword( password );

        userCredentialsStore.addUserCredentials( userCredentials );

        assertEquals( userCredentialsStore.getUserCredentials( user ).getUser().getId(), user.getId() );
        assertEquals( userCredentialsStore.getUserCredentials( user ).getUsername(), username );
        assertEquals( userCredentialsStore.getUserCredentials( user ).getPassword(), password );

        // Test updateUserCredentials
        userCredentials.setUser( user );
        userCredentials.setUsername( someone );
        userCredentials.setPassword( iloveyou );

        userCredentialsStore.updateUserCredentials( userCredentials );
        assertEquals( userCredentialsStore.getUserCredentials( user ).getUsername(), someone );
        assertEquals( userCredentialsStore.getUserCredentials( user ).getPassword(), iloveyou );

        // Test getUserCredentials
        assertEquals( userCredentialsStore.getUserCredentials( user ).getUsername(), someone );
        assertEquals( userCredentialsStore.getUserCredentials( user ).getPassword(), iloveyou );

        // Test getUserCredentialsByUsername
        assertEquals( userCredentialsStore.getUserCredentialsByUsername( someone ).getPassword(), userCredentials.getPassword() );
        assertEquals( userCredentialsStore.getUserCredentialsByUsername( someone ).getClass(), userCredentials.getClass() );

        // Test deleteUserCredentials
        // Before delete
        assertNotNull( userCredentialsStore.getUserCredentials( user ) );
        userCredentialsStore.deleteUserCredentials( userCredentialsStore.getUserCredentials( user ) );
        // After delete
        assertNull( userCredentialsStore.getUserCredentials( user ) );
    }

    @Test
    public void testBasicUserSettings()
        throws Exception
    {
        String name = "name";
        String value = "value";
        String value1 = "value1";

        // Test addUserSetting
        String userName = "User";
        User user = new User();
        user.setSurname( userName );
        user.setFirstName( userName );
        userStore.save( user );

        UserSetting userSetting = new UserSetting();
        userSetting.setUser( user );
        userSetting.setName( name );
        userSetting.setValue( value );

        userCredentialsStore.addUserSetting( userSetting );
        assertEquals( userCredentialsStore.getUserSetting( user, name ).getName(), userSetting.getName() );

        // Test updateUserSetting
        userSetting.setValue( value1 );
        userCredentialsStore.updateUserSetting( userSetting );
        assertEquals( value1, userCredentialsStore.getUserSetting( user, name ).getValue() );

        // Test getUserSetting
        assertEquals( userCredentialsStore.getUserSetting( userSetting.getUser(), name ).getName(), name );
        assertEquals( userCredentialsStore.getUserSetting( userSetting.getUser(), name ).getUser().getId(), user.getId() );
        assertEquals( userCredentialsStore.getUserSetting( userSetting.getUser(), name ).getValue(), value1 );

        // Test getAllUserSettings
        assertEquals( userCredentialsStore.getAllUserSettings( user ).size(), 1 );
        for ( int i = 1; i <= userCredentialsStore.getAllUserSettings( user ).size(); i++ )
        {
            assertEquals( userCredentialsStore.getUserSetting( user, name ).getValue(), "value" + i );
        }

        // Test deleteUserSetting
        assertEquals( userCredentialsStore.getAllUserSettings( user ).size(), 1 );
        userCredentialsStore.deleteUserSetting( userCredentialsStore.getUserSetting( user, name ) );
        assertEquals( userCredentialsStore.getAllUserSettings( user ).size(), 0 );
    }
}
