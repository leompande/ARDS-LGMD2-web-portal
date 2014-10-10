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

import static org.junit.Assert.*;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 */
public class SecurityServiceTest
    extends DhisSpringTest
{
    private UserCredentials credentials;
    
    @Autowired
    private UserService userService; 
    
    @Autowired
    private PasswordManager passwordManager;
    
    @Autowired
    private SecurityService securityService;
    
    @Override
    public void setUpTest()
    {        
        credentials = new UserCredentials();
        credentials.setUsername( "johndoe" );
        credentials.setPassword( "" );
        
        User user = createUser( 'A' );
        user.setEmail( "valid@email.com" );
        user.setUserCredentials( credentials );
        credentials.setUser( user );
        userService.addUserCredentials( credentials );
    }
    
    @Test
    public void testRestore()
    {
        String[] result = securityService.initRestore( credentials );
        
        assertNotNull( result[0] );
        assertNotNull( result[1] );
        assertNotNull( credentials.getRestoreToken() );
        assertNotNull( credentials.getRestoreCode() );
        assertNotNull( credentials.getRestoreExpiry() );
        
        boolean verified = securityService.verifyToken( credentials, result[0] );
        
        assertTrue( verified );
        
        String password = "NewPassword1";
        
        boolean restored = securityService.restore( credentials, result[0], result[1], password );
        
        assertTrue( restored );
        
        String hashedPassword = passwordManager.encodePassword( credentials.getUsername(), password );
        
        assertEquals( hashedPassword, credentials.getPassword() );
    }
}
