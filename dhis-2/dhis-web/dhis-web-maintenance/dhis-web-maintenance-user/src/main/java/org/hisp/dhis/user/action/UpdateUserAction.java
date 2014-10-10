package org.hisp.dhis.user.action;

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

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.attribute.AttributeService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.security.PasswordManager;
import org.hisp.dhis.system.util.AttributeUtils;
import org.hisp.dhis.system.util.LocaleUtils;
import org.hisp.dhis.user.*;

import java.util.*;

/**
 * @author Torgeir Lorange Ostby
 */
public class UpdateUserAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    private PasswordManager passwordManager;

    public void setPasswordManager( PasswordManager passwordManager )
    {
        this.passwordManager = passwordManager;
    }

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private AttributeService attributeService;

    public void setAttributeService( AttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private String rawPassword;

    public void setRawPassword( String rawPassword )
    {
        this.rawPassword = rawPassword;
    }

    private String surname;

    public void setSurname( String surname )
    {
        this.surname = surname;
    }

    private String firstName;

    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }

    private String email;

    public void setEmail( String email )
    {
        this.email = email;
    }

    private String phoneNumber;

    public void setPhoneNumber( String phoneNumber )
    {
        this.phoneNumber = phoneNumber;
    }

    private String localeUi;
    
    public void setLocaleUi( String localeUi )
    {
        this.localeUi = localeUi;
    }

    private String localeDb;

    public void setLocaleDb( String localeDb )
    {
        this.localeDb = localeDb;
    }

    private Collection<String> selectedList = new ArrayList<String>();

    public void setSelectedList( Collection<String> selectedList )
    {
        this.selectedList = selectedList;
    }

    private List<String> jsonAttributeValues;

    public void setJsonAttributeValues( List<String> jsonAttributeValues )
    {
        this.jsonAttributeValues = jsonAttributeValues;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        UserCredentials currentUserCredentials = currentUserService.getCurrentUser() != null ? currentUserService
            .getCurrentUser().getUserCredentials() : null;

        // ---------------------------------------------------------------------
        // Prepare values
        // ---------------------------------------------------------------------

        if ( email != null && email.trim().length() == 0 )
        {
            email = null;
        }

        if ( rawPassword != null && rawPassword.trim().length() == 0 )
        {
            rawPassword = null;
        }

        // ---------------------------------------------------------------------
        // Update userCredentials and user
        // ---------------------------------------------------------------------

        Collection<OrganisationUnit> units = selectionTreeManager.getReloadedSelectedOrganisationUnits();

        User user = userService.getUser( id );
        user.setSurname( surname );
        user.setFirstName( firstName );
        user.setEmail( email );
        user.setPhoneNumber( phoneNumber );
        user.updateOrganisationUnits( new HashSet<OrganisationUnit>( units ) );

        UserCredentials userCredentials = userService.getUserCredentials( user );

        Set<UserAuthorityGroup> userAuthorityGroups = new HashSet<UserAuthorityGroup>();

        for ( String id : selectedList )
        {
            UserAuthorityGroup group = userService.getUserAuthorityGroup( Integer.parseInt( id ) );

            if ( currentUserCredentials != null && currentUserCredentials.canIssue( group ) )
            {
                userAuthorityGroups.add( group );
            }
        }

        userCredentials.setUserAuthorityGroups( userAuthorityGroups );

        if ( rawPassword != null )
        {
            userCredentials.setPassword( passwordManager.encodePassword( userCredentials.getUsername(), rawPassword ) );
        }

        if ( jsonAttributeValues != null )
        {
            AttributeUtils.updateAttributeValuesFromJson( user.getAttributeValues(), jsonAttributeValues,
                attributeService );
        }

        userService.updateUserCredentials( userCredentials );
        userService.updateUser( user );

        if ( currentUserService.getCurrentUser() == user )
        {
            selectionManager.setRootOrganisationUnits( units );
            selectionManager.setSelectedOrganisationUnits( units );

            selectionTreeManager.setRootOrganisationUnits( units );
            selectionTreeManager.setSelectedOrganisationUnits( units );
        }

        if ( units.size() > 0 )
        {
            selectionManager.setSelectedOrganisationUnits( units );
        }

        userService.addOrUpdateUserSetting( new UserSetting( user, UserSettingService.KEY_UI_LOCALE, LocaleUtils.getLocale( localeUi ) ) );
        userService.addOrUpdateUserSetting( new UserSetting( user, UserSettingService.KEY_DB_LOCALE, LocaleUtils.getLocale( localeDb ) ) );
        
        return SUCCESS;
    }
}
