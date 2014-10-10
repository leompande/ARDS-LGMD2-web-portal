package org.hisp.dhis.api.controller.user;

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

import org.apache.commons.collections.CollectionUtils;
import org.hisp.dhis.api.controller.exception.FilterTooShortException;
import org.hisp.dhis.api.controller.exception.NotAuthenticatedException;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.api.utils.ContextUtils.CacheStrategy;
import org.hisp.dhis.api.utils.FormUtils;
import org.hisp.dhis.api.webdomain.FormDataSet;
import org.hisp.dhis.api.webdomain.FormOrganisationUnit;
import org.hisp.dhis.api.webdomain.FormProgram;
import org.hisp.dhis.api.webdomain.Forms;
import org.hisp.dhis.api.webdomain.user.Dashboard;
import org.hisp.dhis.api.webdomain.user.Inbox;
import org.hisp.dhis.api.webdomain.user.Recipients;
import org.hisp.dhis.api.webdomain.user.UserAccount;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.interpretation.Interpretation;
import org.hisp.dhis.interpretation.InterpretationService;
import org.hisp.dhis.message.MessageConversation;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.system.util.TextUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserGroupService;
import org.hisp.dhis.user.UserService;
import org.hisp.dhis.user.UserSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hisp.dhis.user.UserSettingService.*;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = { CurrentUserController.RESOURCE_PATH, "/me" }, method = RequestMethod.GET )
public class CurrentUserController
{
    public static final String RESOURCE_PATH = "/currentUser";

    private static final int MAX_OBJECTS = 50;

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private InterpretationService interpretationService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private UserSettingService userSettingService;
    
    @Autowired
    private ContextUtils contextUtils;

    @Autowired
    private I18nService i18nService;

    @RequestMapping( produces = { "application/json", "text/*" } )
    public void getCurrentUser( HttpServletResponse response ) throws Exception
    {
        User currentUser = currentUserService.getCurrentUser();

        if ( currentUser == null )
        {
            throw new NotAuthenticatedException();
        }

        response.setContentType( MediaType.APPLICATION_JSON_VALUE );
        JacksonUtils.toJsonWithView( response.getOutputStream(), currentUser, DetailedView.class );
    }

    @RequestMapping( value = "/inbox", produces = { "application/json", "text/*" } )
    public void getInbox( HttpServletResponse response ) throws Exception
    {
        User currentUser = currentUserService.getCurrentUser();

        if ( currentUser == null )
        {
            throw new NotAuthenticatedException();
        }

        Inbox inbox = new Inbox();
        inbox.setMessageConversations( new ArrayList<MessageConversation>( messageService.getMessageConversations( 0, MAX_OBJECTS ) ) );
        inbox.setInterpretations( new ArrayList<Interpretation>( interpretationService.getInterpretations( 0, MAX_OBJECTS ) ) );

        response.setContentType( MediaType.APPLICATION_JSON_VALUE );
        JacksonUtils.toJson( response.getOutputStream(), inbox );
    }

    @RequestMapping( value = "/dashboard", produces = { "application/json", "text/*" } )
    public void getDashboard( HttpServletResponse response ) throws Exception
    {
        User currentUser = currentUserService.getCurrentUser();

        if ( currentUser == null )
        {
            throw new NotAuthenticatedException();
        }

        Dashboard dashboard = new Dashboard();
        dashboard.setUnreadMessageConversation( messageService.getUnreadMessageConversationCount() );
        dashboard.setUnreadInterpretations( interpretationService.getNewInterpretationCount() );

        response.setContentType( MediaType.APPLICATION_JSON_VALUE );
        JacksonUtils.toJson( response.getOutputStream(), dashboard );
    }

    @RequestMapping( value = { "/profile", "/user-account" }, produces = { "application/json", "text/*" } )
    public void getUserAccount( HttpServletResponse response ) throws Exception
    {
        User currentUser = currentUserService.getCurrentUser();

        if ( currentUser == null )
        {
            throw new NotAuthenticatedException();
        }

        UserAccount userAccount = new UserAccount();

        // user account
        userAccount.setFirstName( currentUser.getFirstName() );
        userAccount.setSurname( currentUser.getSurname() );
        userAccount.setEmail( currentUser.getEmail() );
        userAccount.setPhoneNumber( currentUser.getPhoneNumber() );

        // profile
        userAccount.setIntroduction( currentUser.getIntroduction() );
        userAccount.setJobTitle( currentUser.getJobTitle() );
        userAccount.setGender( currentUser.getGender() );

        if ( currentUser.getBirthday() != null )
        {
            userAccount.setBirthday( simpleDateFormat.format( currentUser.getBirthday() ) );
        }

        userAccount.setNationality( currentUser.getNationality() );
        userAccount.setEmployer( currentUser.getEmployer() );
        userAccount.setEducation( currentUser.getEducation() );
        userAccount.setInterests( currentUser.getInterests() );
        userAccount.setLanguages( currentUser.getLanguages() );
        
        userAccount.getSettings().put( KEY_UI_LOCALE, TextUtils.toString( userSettingService.getUserSetting( KEY_UI_LOCALE ) ) );
        userAccount.getSettings().put( KEY_DB_LOCALE, TextUtils.toString( userSettingService.getUserSetting( KEY_DB_LOCALE ) ) );
        userAccount.getSettings().put( KEY_MESSAGE_EMAIL_NOTIFICATION, TextUtils.toString( userSettingService.getUserSetting( KEY_MESSAGE_EMAIL_NOTIFICATION ) ) );
        userAccount.getSettings().put( KEY_MESSAGE_SMS_NOTIFICATION, TextUtils.toString( userSettingService.getUserSetting( KEY_MESSAGE_SMS_NOTIFICATION ) ) );

        response.setContentType( MediaType.APPLICATION_JSON_VALUE );
        JacksonUtils.toJson( response.getOutputStream(), userAccount );
    }

    @RequestMapping( value = { "/profile", "/user-account" }, method = RequestMethod.POST, consumes = "application/json" )
    public void postUserAccountJson( HttpServletResponse response, HttpServletRequest request ) throws Exception
    {
        UserAccount userAccount = JacksonUtils.fromJson( request.getInputStream(), UserAccount.class );
        User currentUser = currentUserService.getCurrentUser();

        if ( currentUser == null )
        {
            throw new NotAuthenticatedException();
        }

        // basic user account
        currentUser.setFirstName( userAccount.getFirstName() );
        currentUser.setSurname( userAccount.getSurname() );
        currentUser.setEmail( userAccount.getEmail() );
        currentUser.setPhoneNumber( userAccount.getPhoneNumber() );

        // profile
        currentUser.setIntroduction( userAccount.getIntroduction() );
        currentUser.setJobTitle( userAccount.getJobTitle() );
        currentUser.setGender( userAccount.getGender() );

        if ( userAccount.getBirthday() != null && !userAccount.getBirthday().isEmpty() )
        {
            currentUser.setBirthday( simpleDateFormat.parse( userAccount.getBirthday() ) );
        }

        currentUser.setNationality( userAccount.getNationality() );
        currentUser.setEmployer( userAccount.getEmployer() );
        currentUser.setEducation( userAccount.getEducation() );
        currentUser.setInterests( userAccount.getInterests() );
        currentUser.setLanguages( userAccount.getLanguages() );

        userService.updateUser( currentUser );
    }

    @RequestMapping( value = "/authorization/{auth}", produces = { "application/json", "text/*" } )
    public void hasAuthorization( @PathVariable String auth, HttpServletResponse response ) throws IOException
    {
        User currentUser = currentUserService.getCurrentUser();

        boolean hasAuth = currentUser != null && currentUser.getUserCredentials().isAuthorized( auth );
        
        response.setContentType( MediaType.APPLICATION_JSON_VALUE );
        JacksonUtils.toJson( response.getOutputStream(), hasAuth );
    }
    
    @RequestMapping( value = "/recipients", produces = { "application/json", "text/*" } )
    public void recipientsJson( HttpServletResponse response,
        @RequestParam( value = "filter" ) String filter ) throws IOException, NotAuthenticatedException, FilterTooShortException
    {
        User currentUser = currentUserService.getCurrentUser();

        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_JSON, CacheStrategy.CACHE_1_HOUR );

        if ( currentUser == null )
        {
            throw new NotAuthenticatedException();
        }

        if ( 3 > filter.length() )
        {
            throw new FilterTooShortException();
        }

        Recipients recipients = new Recipients();
        recipients.setOrganisationUnits( new HashSet<OrganisationUnit>( organisationUnitService.getOrganisationUnitsBetweenByName( filter, 0, MAX_OBJECTS ) ) );

        recipients.setUsers( new HashSet<User>( userService.getAllUsersBetweenByName( filter, 0, MAX_OBJECTS ) ) );
        recipients.setUserGroups( new HashSet<UserGroup>( userGroupService.getUserGroupsBetweenByName( filter, 0, MAX_OBJECTS ) ) );

        response.setContentType( MediaType.APPLICATION_JSON_VALUE );
        JacksonUtils.toJson( response.getOutputStream(), recipients );
    }

    @RequestMapping( value = { "/assignedOrganisationUnits", "/organisationUnits" }, produces = { "application/json", "text/*" } )
    public void getAssignedOrganisationUnits( HttpServletResponse response, @RequestParam Map<String, String> parameters ) throws IOException, NotAuthenticatedException
    {
        User currentUser = currentUserService.getCurrentUser();

        if ( currentUser == null )
        {
            throw new NotAuthenticatedException();
        }

        Set<OrganisationUnit> userOrganisationUnits = new HashSet<OrganisationUnit>();
        userOrganisationUnits.add( currentUser.getOrganisationUnit() );

        if ( parameters.containsKey( "includeChildren" ) && Boolean.parseBoolean( parameters.get( "includeChildren" ) ) )
        {
            List<OrganisationUnit> children = new ArrayList<OrganisationUnit>();

            for ( OrganisationUnit organisationUnit : userOrganisationUnits )
            {
                children.addAll( organisationUnit.getChildren() );
            }

            userOrganisationUnits.addAll( children );
        }
        else if ( parameters.containsKey( "includeDescendants" ) && Boolean.parseBoolean( parameters.get( "includeDescendants" ) ) )
        {
            List<OrganisationUnit> children = new ArrayList<OrganisationUnit>();

            for ( OrganisationUnit organisationUnit : userOrganisationUnits )
            {
                children.addAll( organisationUnitService.getOrganisationUnitsWithChildren( organisationUnit.getUid() ) );
            }

            userOrganisationUnits.addAll( children );
        }

        String viewName = parameters.get( "viewClass" );

        if ( viewName == null )
        {
            viewName = "detailed";
        }

        Class<?> viewClass = JacksonUtils.getViewClass( viewName );

        response.setContentType( MediaType.APPLICATION_JSON_VALUE );
        JacksonUtils.toJsonWithView( response.getOutputStream(), userOrganisationUnits, viewClass );
    }

    @RequestMapping( value = { "/assignedPrograms", "/programs" }, produces = { "application/json", "text/*" } )
    public void getPrograms( HttpServletResponse response, @RequestParam Map<String, String> parameters,
        @RequestParam( required = false ) Integer type )
        throws IOException, NotAuthenticatedException
    {
        User currentUser = currentUserService.getCurrentUser();

        if ( currentUser == null )
        {
            throw new NotAuthenticatedException();
        }

        Set<OrganisationUnit> userOrganisationUnits = new HashSet<OrganisationUnit>();
        Set<OrganisationUnit> organisationUnits = new HashSet<OrganisationUnit>();
        Set<Program> programs = new HashSet<Program>();
        Map<String, List<Program>> programAssociations = new HashMap<String, List<Program>>();
        List<Program> userPrograms;

        if ( type == null )
        {
            userPrograms = new ArrayList<Program>( programService.getProgramsByCurrentUser() );
        }
        else
        {
            userPrograms = new ArrayList<Program>( programService.getProgramsByCurrentUser( type ) );
        }

        if ( currentUserService.currentUserIsSuper() && currentUser.getOrganisationUnits().isEmpty() )
        {
            userOrganisationUnits.addAll( organisationUnitService.getRootOrganisationUnits() );
        }
        else
        {
            userOrganisationUnits.addAll( currentUser.getOrganisationUnits() );
        }

        if ( parameters.containsKey( "includeDescendants" ) && Boolean.parseBoolean( parameters.get( "includeDescendants" ) ) )
        {
            List<OrganisationUnit> children = new ArrayList<OrganisationUnit>();

            for ( OrganisationUnit organisationUnit : userOrganisationUnits )
            {
                children.addAll( organisationUnitService.getOrganisationUnitsWithChildren( organisationUnit.getUid() ) );
            }

            userOrganisationUnits.addAll( children );
        }
        else
        {
            List<OrganisationUnit> children = new ArrayList<OrganisationUnit>();

            for ( OrganisationUnit organisationUnit : userOrganisationUnits )
            {
                children.addAll( organisationUnit.getChildren() );
            }

            userOrganisationUnits.addAll( children );
        }

        for ( OrganisationUnit organisationUnit : userOrganisationUnits )
        {
            List<Program> ouPrograms = new ArrayList<Program>( programService.getPrograms( organisationUnit ) );

            if ( !ouPrograms.isEmpty() )
            {
                for ( Program program : ouPrograms )
                {
                    if ( userPrograms.contains( program ) )
                    {
                        organisationUnits.add( organisationUnit );
                        programs.add( program );

                        if ( programAssociations.get( organisationUnit.getUid() ) == null )
                        {
                            programAssociations.put( organisationUnit.getUid(), new ArrayList<Program>() );
                        }

                        programAssociations.get( organisationUnit.getUid() ).add( program );
                    }
                }
            }
        }

        i18nService.internationalise( programs );

        Forms forms = new Forms();

        for ( OrganisationUnit organisationUnit : organisationUnits )
        {
            FormOrganisationUnit formOrganisationUnit = new FormOrganisationUnit();
            formOrganisationUnit.setId( organisationUnit.getUid() );
            formOrganisationUnit.setLabel( organisationUnit.getDisplayName() );
            formOrganisationUnit.setLevel( organisationUnit.getOrganisationUnitLevel() );

            if ( organisationUnit.getParent() != null )
            {
                formOrganisationUnit.setParent( organisationUnit.getParent().getUid() );
            }

            for ( Program program : programAssociations.get( organisationUnit.getUid() ) )
            {
                FormProgram formProgram = new FormProgram();
                formProgram.setId( program.getUid() );
                formProgram.setLabel( program.getDisplayName() );

                formOrganisationUnit.getPrograms().add( formProgram );
            }

            forms.getOrganisationUnits().put( formOrganisationUnit.getId(), formOrganisationUnit );
        }

        for ( Program program : programs )
        {
            forms.getForms().put( program.getUid(), FormUtils.fromProgram( program ) );
        }

        response.setContentType( MediaType.APPLICATION_JSON_VALUE );
        JacksonUtils.toJson( response.getOutputStream(), forms );
    }

    @SuppressWarnings( "unchecked" )
    @RequestMapping( value = { "/assignedDataSets", "/dataSets" }, produces = { "application/json", "text/*" } )
    public void getDataSets( HttpServletResponse response, @RequestParam Map<String, String> parameters ) throws IOException, NotAuthenticatedException
    {
        User currentUser = currentUserService.getCurrentUser();

        if ( currentUser == null )
        {
            throw new NotAuthenticatedException();
        }

        Forms forms = new Forms();

        Set<OrganisationUnit> organisationUnits = new HashSet<OrganisationUnit>();
        Set<DataSet> userDataSets;
        Set<OrganisationUnit> userOrganisationUnits = new HashSet<OrganisationUnit>( currentUser.getOrganisationUnits() );

        if ( currentUser.getUserCredentials().getAllAuthorities().contains( "ALL" ) )
        {
            userDataSets = new HashSet<DataSet>( dataSetService.getAllDataSets() );

            if ( userOrganisationUnits.isEmpty() )
            {
                userOrganisationUnits = new HashSet<OrganisationUnit>( organisationUnitService.getRootOrganisationUnits() );
            }
        }
        else
        {
            userDataSets = currentUser.getUserCredentials().getAllDataSets();
        }

        if ( parameters.containsKey( "includeDescendants" ) && Boolean.parseBoolean( parameters.get( "includeDescendants" ) ) )
        {
            List<OrganisationUnit> children = new ArrayList<OrganisationUnit>();

            for ( OrganisationUnit organisationUnit : userOrganisationUnits )
            {
                children.addAll( organisationUnitService.getOrganisationUnitsWithChildren( organisationUnit.getUid() ) );
            }

            userOrganisationUnits.addAll( children );
        }
        else
        {
            List<OrganisationUnit> children = new ArrayList<OrganisationUnit>();

            for ( OrganisationUnit organisationUnit : userOrganisationUnits )
            {
                children.addAll( organisationUnit.getChildren() );
            }

            userOrganisationUnits.addAll( children );
        }

        for ( OrganisationUnit ou : userOrganisationUnits )
        {
            Set<DataSet> dataSets = new HashSet<DataSet>( CollectionUtils.intersection( ou.getDataSets(), userDataSets ) );

            if ( dataSets.size() > 0 )
            {
                organisationUnits.add( ou );
            }
        }

        i18nService.internationalise( organisationUnits );

        for ( OrganisationUnit organisationUnit : organisationUnits )
        {
            FormOrganisationUnit formOrganisationUnit = new FormOrganisationUnit();
            formOrganisationUnit.setId( organisationUnit.getUid() );
            formOrganisationUnit.setLabel( organisationUnit.getDisplayName() );
            formOrganisationUnit.setLevel( organisationUnit.getOrganisationUnitLevel() );

            if ( organisationUnit.getParent() != null )
            {
                formOrganisationUnit.setParent( organisationUnit.getParent().getUid() );
            }

            Set<DataSet> dataSets = new HashSet<DataSet>( CollectionUtils.intersection( organisationUnit.getDataSets(), userDataSets ) );
            i18nService.internationalise( dataSets );

            for ( DataSet dataSet : dataSets )
            {
                i18nService.internationalise( dataSet.getDataElements() );
                i18nService.internationalise( dataSet.getSections() );

                String uid = dataSet.getUid();

                FormDataSet formDataSet = new FormDataSet();
                formDataSet.setId( uid );
                formDataSet.setLabel( dataSet.getDisplayName() );

                forms.getForms().put( uid, FormUtils.fromDataSet( dataSet ) );
                formOrganisationUnit.getDataSets().add( formDataSet );
            }

            forms.getOrganisationUnits().put( formOrganisationUnit.getId(), formOrganisationUnit );
        }

        response.setContentType( MediaType.APPLICATION_JSON_VALUE );
        JacksonUtils.toJson( response.getOutputStream(), forms );
    }
}
