package org.hisp.dhis.security.listener;

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

import org.hisp.dhis.user.UserService;
import org.hisp.dhis.useraudit.UserAuditService;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationFailureCredentialsExpiredEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.util.Assert;

/**
 * @author Lars Helge Overland
 */
public class AuthenticationListener
    implements ApplicationListener<ApplicationEvent>
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private UserAuditService userAuditService;

    public void setUserAuditService( UserAuditService userAuditService )
    {
        this.userAuditService = userAuditService;
    }

    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    // -------------------------------------------------------------------------
    // ApplicationListener implementation
    // -------------------------------------------------------------------------

    public void onApplicationEvent( ApplicationEvent applicationEvent )
    {
        Assert.notNull( applicationEvent );

        if ( applicationEvent instanceof AuthenticationSuccessEvent )
        {
            AuthenticationSuccessEvent event = (AuthenticationSuccessEvent) applicationEvent;

            String username = ((UserDetails) event.getAuthentication().getPrincipal()).getUsername();

            WebAuthenticationDetails details = (WebAuthenticationDetails) event.getAuthentication().getDetails();

            String ip = details != null ? details.getRemoteAddress() : "";

            userAuditService.registerLoginSuccess( username, ip );

            userService.setLastLogin( username );
        }
        else if ( applicationEvent instanceof AuthenticationFailureCredentialsExpiredEvent )
        {
            AuthenticationFailureCredentialsExpiredEvent event = (AuthenticationFailureCredentialsExpiredEvent) applicationEvent;

            WebAuthenticationDetails details = (WebAuthenticationDetails) event.getAuthentication().getDetails();

            userAuditService.registerLoginExpired( (String) event.getAuthentication().getPrincipal(), details.getRemoteAddress() );
        }
        else if ( applicationEvent instanceof AbstractAuthenticationFailureEvent )
        {
            AbstractAuthenticationFailureEvent event = (AbstractAuthenticationFailureEvent) applicationEvent;

            WebAuthenticationDetails details = (WebAuthenticationDetails) event.getAuthentication().getDetails();

            userAuditService.registerLoginFailure( (String) event.getAuthentication().getPrincipal(), details.getRemoteAddress() );
        }
    }
}
