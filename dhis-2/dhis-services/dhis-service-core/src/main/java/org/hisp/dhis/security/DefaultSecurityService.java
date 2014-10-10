package org.hisp.dhis.security;

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
import org.hisp.dhis.common.CodeGenerator;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.SharingUtils;
import org.hisp.dhis.message.MessageSender;
import org.hisp.dhis.period.Cal;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.system.util.ValidationUtils;
import org.hisp.dhis.system.velocity.VelocityManager;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Lars Helge Overland
 */
public class DefaultSecurityService
    implements SecurityService
{
    private static final Log log = LogFactory.getLog( DefaultSecurityService.class );

    private static final String RESTORE_PATH = "/dhis-web-commons/security/restore.action";

    private static final int TOKEN_LENGTH = 50;
    private static final int CODE_LENGTH = 15;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PasswordManager passwordManager;

    public void setPasswordManager( PasswordManager passwordManager )
    {
        this.passwordManager = passwordManager;
    }

    private MessageSender emailMessageSender;

    public void setEmailMessageSender( MessageSender emailMessageSender )
    {
        this.emailMessageSender = emailMessageSender;
    }

    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    @Autowired
    private CurrentUserService currentUserService;

    // -------------------------------------------------------------------------
    // SecurityService implementation
    // -------------------------------------------------------------------------

    public boolean sendRestoreMessage( UserCredentials credentials, String rootPath )
    {
        if ( credentials == null || rootPath == null )
        {
            return false;
        }

        if ( credentials.getUser() == null || credentials.getUser().getEmail() == null )
        {
            log.info( "Could not send message as user does not exist or has no email: " + credentials );
            return false;
        }

        if ( !ValidationUtils.emailIsValid( credentials.getUser().getEmail() ) )
        {
            log.info( "Could not send message as email is invalid" );
            return false;
        }

        if ( !systemSettingManager.emailEnabled() )
        {
            log.info( "Could not send message as email is not configured" );
            return false;
        }

        if ( credentials.hasAnyAuthority( Arrays.asList( UserAuthorityGroup.CRITICAL_AUTHS ) ) )
        {
            log.info( "Not allowed to recover credentials with critical authorities" );
            return false;
        }

        String[] result = initRestore( credentials );

        Set<User> users = new HashSet<User>();
        users.add( credentials.getUser() );

        Map<String, String> vars = new HashMap<String, String>();
        vars.put( "rootPath", rootPath );
        vars.put( "restorePath", rootPath + RESTORE_PATH );
        vars.put( "token", result[0] );
        vars.put( "code", result[1] );
        vars.put( "username", credentials.getUsername() );

        String text1 = new VelocityManager().render( vars, "restore_message1" );
        String text2 = new VelocityManager().render( vars, "restore_message2" );

        emailMessageSender.sendMessage( "User account restore confirmation (message 1 of 2)", text1, null, users, true );
        emailMessageSender.sendMessage( "User account restore confirmation (message 2 of 2)", text2, null, users, true );

        return true;
    }

    public String[] initRestore( UserCredentials credentials )
    {
        String token = CodeGenerator.generateCode( TOKEN_LENGTH );
        String code = CodeGenerator.generateCode( CODE_LENGTH );

        String hashedToken = passwordManager.encodePassword( credentials.getUsername(), token );
        String hashedCode = passwordManager.encodePassword( credentials.getUsername(), code );

        Date expiry = new Cal().now().add( Calendar.HOUR_OF_DAY, 1 ).time();

        credentials.setRestoreToken( hashedToken );
        credentials.setRestoreCode( hashedCode );
        credentials.setRestoreExpiry( expiry );

        userService.updateUserCredentials( credentials );

        String[] result = { token, code };
        return result;
    }

    public boolean restore( UserCredentials credentials, String token, String code, String newPassword )
    {
        if ( credentials == null || token == null || code == null || newPassword == null )
        {
            return false;
        }

        String username = credentials.getUsername();
        
        token = passwordManager.encodePassword( username, token );
        code = passwordManager.encodePassword( username, code );

        Date date = new Cal().now().time();

        if ( !credentials.canRestore( token, code, date ) )
        {
            return false;
        }

        newPassword = passwordManager.encodePassword( username, newPassword );

        credentials.setPassword( newPassword );

        credentials.setRestoreCode( null );
        credentials.setRestoreToken( null );
        credentials.setRestoreExpiry( null );

        userService.updateUserCredentials( credentials );

        return true;
    }

    public boolean verifyToken( UserCredentials credentials, String token )
    {
        if ( credentials == null || token == null )
        {
            return false;
        }

        if ( credentials.getRestoreToken() == null )
        {
            log.info( "Could not verify token as user has no token: " + credentials );
            return false;
        }

        token = passwordManager.encodePassword( credentials.getUsername(), token );

        return credentials.getRestoreToken().equals( token );
    }

    @Override
    public boolean canCreatePublic( IdentifiableObject identifiableObject )
    {
        return !SharingUtils.isSupported( identifiableObject.getClass() ) || SharingUtils.canCreatePublic( currentUserService.getCurrentUser(), identifiableObject );
    }

    @Override
    public boolean canCreatePublic( String type )
    {
        return !SharingUtils.isSupported( type ) || SharingUtils.canCreatePublic( currentUserService.getCurrentUser(), type );
    }

    @Override
    public boolean canCreatePrivate( IdentifiableObject identifiableObject )
    {
        return !SharingUtils.isSupported( identifiableObject.getClass() ) || SharingUtils.canCreatePrivate( currentUserService.getCurrentUser(), identifiableObject );
    }

    @Override
    public boolean canCreatePrivate( String type )
    {
        return !SharingUtils.isSupported( type ) || SharingUtils.canCreatePrivate( currentUserService.getCurrentUser(), type );
    }

    @Override
    public boolean canRead( IdentifiableObject identifiableObject )
    {
        return !SharingUtils.isSupported( identifiableObject.getClass() ) || SharingUtils.canRead( currentUserService.getCurrentUser(), identifiableObject );
    }

    @Override
    public boolean canWrite( IdentifiableObject identifiableObject )
    {
        return !SharingUtils.isSupported( identifiableObject.getClass() ) || SharingUtils.canWrite( currentUserService.getCurrentUser(), identifiableObject );
    }

    @Override
    public boolean canUpdate( IdentifiableObject identifiableObject )
    {
        return !SharingUtils.isSupported( identifiableObject.getClass() ) || SharingUtils.canUpdate( currentUserService.getCurrentUser(), identifiableObject );
    }

    @Override
    public boolean canDelete( IdentifiableObject identifiableObject )
    {
        return !SharingUtils.isSupported( identifiableObject.getClass() ) || SharingUtils.canDelete( currentUserService.getCurrentUser(), identifiableObject );
    }

    @Override
    public boolean canManage( IdentifiableObject identifiableObject )
    {
        return !SharingUtils.isSupported( identifiableObject.getClass() ) || SharingUtils.canManage( currentUserService.getCurrentUser(), identifiableObject );
    }
}
