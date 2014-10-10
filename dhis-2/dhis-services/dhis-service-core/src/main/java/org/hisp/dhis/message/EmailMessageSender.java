package org.hisp.dhis.message;

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

import static org.hisp.dhis.user.UserSettingService.KEY_MESSAGE_EMAIL_NOTIFICATION;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.springframework.scheduling.annotation.Async;

/**
 * @author Lars Helge Overland
 */
public class EmailMessageSender
    implements MessageSender
{
    private static final Log log = LogFactory.getLog( EmailMessageSender.class );
    private static final String FROM_ADDRESS = "noreply@dhis2.org";
    private static final String FROM_NAME = "DHIS2 Message [No reply]";
    private static final String SUBJECT_PREFIX = "[DHIS2] ";
    private static final String LB = System.getProperty( "line.separator" );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SystemSettingManager systemSettingManager;
    
    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }
    
    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    // -------------------------------------------------------------------------
    // MessageSender implementation
    // -------------------------------------------------------------------------

    /**
     * Note this methods is invoked asynchronously.
     */
    @Async
    @Override
    public String sendMessage( String subject, String text, User sender, Set<User> users, boolean forceSend )
    {        
        String hostName = systemSettingManager.getEmailHostName();
        int port = systemSettingManager.getEmailPort();
        String username = systemSettingManager.getEmailUsername();
        String password = systemSettingManager.getEmailPassword();
        boolean tls = systemSettingManager.getEmailTls();

        if ( hostName == null )
        {
            return null;
        }

        text = sender == null ? text : ( text + LB + LB + 
            sender.getName() + LB + 
            sender.getOrganisationUnitsName() + LB +
            ( sender.getEmail() != null ? ( sender.getEmail() + LB ) : StringUtils.EMPTY ) +
            ( sender.getPhoneNumber() != null ? ( sender.getPhoneNumber() + LB ) : StringUtils.EMPTY ) );
        
        Map<User, Serializable> settings = userService.getUserSettings( KEY_MESSAGE_EMAIL_NOTIFICATION, false );

        try
        {
            Email email = getEmail( hostName, port, username, password, tls );
            email.setSubject( SUBJECT_PREFIX + subject );
            email.setMsg( text );
            
            boolean hasRecipients = false;
            
            for ( User user : users )
            {
                boolean emailNotification = settings.get( user ) != null && (Boolean) settings.get( user ) == true;
                
                boolean doSend = forceSend || emailNotification;
    
                if ( doSend && user.getEmail() != null && !user.getEmail().trim().isEmpty() )
                {
                    email.addBcc( user.getEmail() );
                    
                    log.info( "Sending email to user: " + user + " with email address: " + user.getEmail() );
                    
                    hasRecipients = true;
                }
            }

            if ( hasRecipients )
            {
                email.send();
                
                log.info( "Email sent using host: " + hostName + " with TLS: " + tls );
            }
        }
        catch ( EmailException ex )
        {
            log.warn( "Could not send email: " + ex.getMessage() );
        }
        
        return null;
    }

    private Email getEmail( String hostName, int port, String username, String password, boolean tls )
        throws EmailException
    {
        Email email = new SimpleEmail();
        email.setHostName( hostName );
        email.setFrom( FROM_ADDRESS, FROM_NAME );
        email.setSmtpPort( port );
        email.setTLS( true );
        
        if ( username != null && password != null )
        {
            email.setAuthenticator( new DefaultAuthenticator( username, password ) );
        }
        
        return email;
    }
}
