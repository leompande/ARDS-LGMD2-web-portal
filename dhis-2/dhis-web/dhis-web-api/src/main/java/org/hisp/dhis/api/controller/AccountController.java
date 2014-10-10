package org.hisp.dhis.api.controller;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.security.PasswordManager;
import org.hisp.dhis.security.SecurityService;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.system.util.ValidationUtils;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping( value = "/account" )
public class AccountController
{
    private static final Log log = LogFactory.getLog( AccountController.class );

    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/verify";
    protected static final String PUB_KEY = "6LcM6tcSAAAAANwYsFp--0SYtcnze_WdYn8XwMMk";
    private static final String KEY = "6LcM6tcSAAAAAFnHo1f3lLstk3rZv3EVinNROfRq";
    private static final String TRUE = "true";
    private static final String SPLIT = "\n";
    private static final int MAX_LENGTH = 80;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private PasswordManager passwordManager;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private SystemSettingManager systemSettingManager;
    
    private ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping( value = "/recovery", method = RequestMethod.POST, produces = ContextUtils.CONTENT_TYPE_TEXT )
    public @ResponseBody String recoverAccount(
        @RequestParam String username,
        HttpServletRequest request,
        HttpServletResponse response )
    {
        String rootPath = ContextUtils.getContextPath( request );

        if ( !systemSettingManager.accountRecoveryEnabled() )
        {
            response.setStatus( HttpServletResponse.SC_CONFLICT );
            return "Account recovery is not enabled";
        }

        UserCredentials credentials = userService.getUserCredentialsByUsername( username );

        if ( credentials == null )
        {
            response.setStatus( HttpServletResponse.SC_CONFLICT );
            return "User does not exist: " + username;
        }
        
        boolean recover = securityService.sendRestoreMessage( credentials, rootPath );

        if ( !recover )
        {
            response.setStatus( HttpServletResponse.SC_CONFLICT );
            return "Account could not be recovered";
        }

        log.info( "Recovery message sent for user: " + username );

        response.setStatus( HttpServletResponse.SC_OK );
        return "Recovery message sent";
    }

    @RequestMapping( value = "/restore", method = RequestMethod.POST, produces = ContextUtils.CONTENT_TYPE_TEXT )
    public @ResponseBody String restoreAccount(
        @RequestParam String username,
        @RequestParam String token,
        @RequestParam String code,
        @RequestParam String password,
        HttpServletRequest request,
        HttpServletResponse response )
    {
        if ( !systemSettingManager.accountRecoveryEnabled() )
        {
            response.setStatus( HttpServletResponse.SC_CONFLICT );
            return "Account recovery is not enabled";
        }

        if ( password == null || !ValidationUtils.passwordIsValid( password ) )
        {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return "Password is not specified or invalid";
        }

        if ( password.trim().equals( username.trim() ) )
        {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return "Password cannot be equal to username";
        }

        UserCredentials credentials = userService.getUserCredentialsByUsername( username );

        if ( credentials == null )
        {
            response.setStatus( HttpServletResponse.SC_CONFLICT );
            return "User does not exist: " + username;
        }
        
        boolean restore = securityService.restore( credentials, token, code, password );

        if ( !restore )
        {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return "Account could not be restored";
        }

        log.info( "Account restored for user: " + username );

        response.setStatus( HttpServletResponse.SC_OK );
        return "Account restored";
    }

    @RequestMapping( method = RequestMethod.POST, produces = ContextUtils.CONTENT_TYPE_TEXT )
    public @ResponseBody String createAccount(
        @RequestParam String username,
        @RequestParam String firstName,
        @RequestParam String surname,
        @RequestParam String password,
        @RequestParam String email,
        @RequestParam String phoneNumber,
        @RequestParam String employer,
        @RequestParam( value = "recaptcha_challenge_field", required = false ) String recapChallenge,
        @RequestParam( value = "recaptcha_response_field", required = false ) String recapResponse,
        HttpServletRequest request,
        HttpServletResponse response )
    {
        boolean allowed = configurationService.getConfiguration().selfRegistrationAllowed();

        if ( !allowed )
        {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return "User self registration is not allowed";
        }

        // ---------------------------------------------------------------------
        // Trim input
        // ---------------------------------------------------------------------

        username = StringUtils.trimToNull( username );
        firstName = StringUtils.trimToNull( firstName );
        surname = StringUtils.trimToNull( surname );
        password = StringUtils.trimToNull( password );
        email = StringUtils.trimToNull( email );
        phoneNumber = StringUtils.trimToNull( phoneNumber );
        employer = StringUtils.trimToNull( employer );
        recapChallenge = StringUtils.trimToNull( recapChallenge );
        recapResponse = StringUtils.trimToNull( recapResponse );

        // ---------------------------------------------------------------------
        // Validate input, return 400 if invalid
        // ---------------------------------------------------------------------

        if ( username == null || username.trim().length() > MAX_LENGTH )
        {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return "User name is not specified or invalid";
        }

        UserCredentials credentials = userService.getUserCredentialsByUsername( username );

        if ( credentials != null )
        {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return "User name is already taken";
        }

        if ( firstName == null || firstName.trim().length() > MAX_LENGTH )
        {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return "First name is not specified or invalid";
        }

        if ( surname == null || surname.trim().length() > MAX_LENGTH )
        {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return "Last name is not specified or invalid";
        }

        if ( password == null || !ValidationUtils.passwordIsValid( password ) )
        {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return "Password is not specified or invalid";
        }

        if ( password.trim().equals( username.trim() ) )
        {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return "Password cannot be equal to username";
        }

        if ( email == null || !ValidationUtils.emailIsValid( email ) )
        {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return "Email is not specified or invalid";
        }

        if ( phoneNumber == null || phoneNumber.trim().length() > 30 )
        {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return "Phone number is not specified or invalid";
        }

        if ( employer == null || employer.trim().length() > MAX_LENGTH )
        {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return "Employer is not specified or invalid";
        }

        if ( !systemSettingManager.selfRegistrationNoRecaptcha() )
        {
            if ( recapChallenge == null )
            {
                response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
                return "Recaptcha challenge must be specified";
            }

            if ( recapResponse == null )
            {
                response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
                return "Recaptcha response must be specified";
            }

            // ---------------------------------------------------------------------
            // Check result from API, return 500 if not
            // ---------------------------------------------------------------------

            String[] results = checkRecaptcha( KEY, request.getRemoteAddr(), recapChallenge, recapResponse );

            if ( results == null || results.length == 0 )
            {
                response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
                return "Captcha could not be verified due to a server error";
            }

            // ---------------------------------------------------------------------
            // Check if verification was successful, return 400 if not
            // ---------------------------------------------------------------------

            if ( !TRUE.equalsIgnoreCase( results[0] ) )
            {
                log.info( "Recaptcha failed with code: " + (results.length > 0 ? results[1] : "") );

                response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
                return "The characters you entered did not match the word verification, try again";
            }
        }

        // ---------------------------------------------------------------------
        // Create and save user, return 201
        // ---------------------------------------------------------------------

        UserAuthorityGroup userRole = configurationService.getConfiguration().getSelfRegistrationRole();
        OrganisationUnit orgUnit = configurationService.getConfiguration().getSelfRegistrationOrgUnit();

        User user = new User();
        user.setFirstName( firstName );
        user.setSurname( surname );
        user.setEmail( email );
        user.setPhoneNumber( phoneNumber );
        user.setEmployer( employer );
        user.getOrganisationUnits().add( orgUnit );

        credentials = new UserCredentials();
        credentials.setUsername( username );
        credentials.setPassword( passwordManager.encodePassword( username, password ) );
        credentials.setSelfRegistered( true );
        credentials.setUser( user );
        credentials.getUserAuthorityGroups().add( userRole );

        user.setUserCredentials( credentials );

        userService.addUser( user );
        userService.addUserCredentials( credentials );

        authenticate( username, password, userRole, request );

        log.info( "Created user with username: " + username );

        response.setStatus( HttpServletResponse.SC_CREATED );
        return "Account created";
    }

    @RequestMapping( method = RequestMethod.PUT, produces = ContextUtils.CONTENT_TYPE_TEXT )
    public @ResponseBody String updatePassword(
        @RequestParam String oldPassword,
        @RequestParam String password,
        HttpServletRequest request,
        HttpServletResponse response ) throws IOException
    {
        String username = (String) request.getSession().getAttribute( "username" );
        UserCredentials credentials = userService.getUserCredentialsByUsername( username );

        Map<String, String> result = new HashMap<String, String>();
        result.put( "status", "OK" );

        if ( userService.credentialsNonExpired( credentials ) )
        {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            result.put( "status", "NON_EXPIRED" );
            result.put( "message", "Account is not expired, redirecting to login." );

            return objectMapper.writeValueAsString( result );
        }

        String oldPasswordEncoded = passwordManager.encodePassword( username, oldPassword );

        if ( !credentials.getPassword().equals( oldPasswordEncoded ) )
        {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            result.put( "status", "NON_MATCHING_PASSWORD" );
            result.put( "message", "Old password is wrong, please correct and try again." );

            return objectMapper.writeValueAsString( result );
        }

        if ( password == null || !ValidationUtils.passwordIsValid( password ) )
        {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            result.put( "status", "PASSWORD_INVALID" );
            result.put( "message", "Password is not specified or invalid" );

            return objectMapper.writeValueAsString( result );
        }

        if ( password.trim().equals( username.trim() ) )
        {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            result.put( "status", "PASSWORD_EQUAL_TO_USERNAME" );
            result.put( "message", "Password cannot be equal to username" );

            return objectMapper.writeValueAsString( result );
        }

        String passwordEncoded = passwordManager.encodePassword( username, password );

        credentials.setPassword( passwordEncoded );
        credentials.setPasswordLastUpdated( new Date() );
        userService.updateUserCredentials( credentials );

        authenticate( username, password, getAuthorities( credentials.getUserAuthorityGroups() ), request );

        result.put( "message", "Account was updated." );

        return objectMapper.writeValueAsString( result );
    }

    @RequestMapping( value = "/username", method = RequestMethod.GET, produces = ContextUtils.CONTENT_TYPE_JSON )
    public @ResponseBody String validateUserName( @RequestParam String username )
    {
        boolean valid = username != null && userService.getUserCredentialsByUsername( username ) == null;

        // Custom code required because of our hacked jQuery validation

        return valid ? "{ \"response\": \"success\", \"message\": \"\" }" :
            "{ \"response\": \"error\", \"message\": \"Username is already taken\" }";
    }

    // ---------------------------------------------------------------------
    // Supportive methods
    // ---------------------------------------------------------------------

    private String[] checkRecaptcha( String privateKey, String remoteIp, String challenge, String response )
    {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();

        params.add( "privatekey", privateKey );
        params.add( "remoteip", remoteIp );
        params.add( "challenge", challenge );
        params.add( "response", response );

        String result = restTemplate.postForObject( RECAPTCHA_VERIFY_URL, params, String.class );

        log.info( "Recaptcha result: " + result );

        return result != null ? result.split( SPLIT ) : null;
    }

    private void authenticate( String username, String rawPassword, Collection<GrantedAuthority> authorities, HttpServletRequest request )
    {
        UsernamePasswordAuthenticationToken token =
            new UsernamePasswordAuthenticationToken( username, rawPassword, authorities );

        Authentication auth = authenticationManager.authenticate( token );

        SecurityContextHolder.getContext().setAuthentication( auth );

        HttpSession session = request.getSession();

        session.setAttribute( "SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext() );
    }

    private void authenticate( String username, String rawPassword, UserAuthorityGroup userRole, HttpServletRequest request )
    {
        UsernamePasswordAuthenticationToken token =
            new UsernamePasswordAuthenticationToken( username, rawPassword, getAuthorities( userRole ) );

        Authentication auth = authenticationManager.authenticate( token );

        SecurityContextHolder.getContext().setAuthentication( auth );

        HttpSession session = request.getSession();

        session.setAttribute( "SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext() );
    }

    private Collection<GrantedAuthority> getAuthorities( Set<UserAuthorityGroup> userRoles )
    {
        Collection<GrantedAuthority> auths = new HashSet<GrantedAuthority>();

        for ( UserAuthorityGroup userRole : userRoles )
        {
            auths.addAll( getAuthorities( userRole ) );
        }

        return auths;
    }

    private Collection<GrantedAuthority> getAuthorities( UserAuthorityGroup userRole )
    {
        Collection<GrantedAuthority> auths = new HashSet<GrantedAuthority>();

        for ( String auth : userRole.getAuthorities() )
        {
            auths.add( new SimpleGrantedAuthority( auth ) );
        }

        return auths;
    }
}
