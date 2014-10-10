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

import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.user.UserCredentials;

/**
 * @author Lars Helge Overland
 */
public interface SecurityService
{
    /**
     * Will invoke the initiateRestore method and dispatch email messages with
     * restore information to the user.
     *
     * @param credentials the credentials for the user to send restore message.
     * @param rootPath the root path of the request.
     * @return false if any of the arguments are null or if the user credentials
     *         identified by the user name does not exist, true otherwise.
     */
    boolean sendRestoreMessage( UserCredentials credentials, String rootPath );

    /**
     * Will populate the restoreToken and restoreCode property of the given
     * credentials with a hashed version of auto-generated values. Will set the
     * restoreExpiry property with a date time one hour from now. Changes will be
     * persisted.
     *
     * @param credentials the user credentials.
     * @return an array where index 0 is the clear-text token and index 1 the
     *         clear-text code.
     */
    String[] initRestore( UserCredentials credentials );

    /**
     * Tests whether the given token and code are valid for the given user name.
     * If true, it will update the user credentials identified by the given user
     * name with the new password. In order to succeed, the given token and code
     * must match the ones on the credentials, and the current date must be before
     * the expiry date time of the credentials.
     *
     * @param credentials the user credentials.
     * @param token the token.
     * @param code the code.
     * @param newPassword the proposed new password.
     * @return true or false.
     */
    boolean restore( UserCredentials credentials, String token, String code, String newPassword );

    /**
     * Tests whether the given token in combination with the given user name is
     * valid, i.e. whether the hashed version of the token matches the one on the
     * user credentials identified by the given user name.
     *
     * @param credentials the user credentials.
     * @param token the token.
     * @return false if any of the arguments are null or if the user credentials
     *         identified by the user name does not exist, true if the arguments
     *         are valid.
     */
    boolean verifyToken( UserCredentials credentials, String token );

    /**
     * Checks whether current user has read access to object.
     *
     * @param identifiableObject Object to check for read access.
     * @return true of false depending on outcome of read check
     */
    boolean canRead( IdentifiableObject identifiableObject );

    /**
     * Checks whether current user has create access to object.
     *
     * @param identifiableObject Object to check for write access.
     * @return true of false depending on outcome of write check
     */
    boolean canWrite( IdentifiableObject identifiableObject );

    /**
     * Checks whether current user can create public instances of the object.
     *
     * @param identifiableObject Object to check for write access.
     * @return true of false depending on outcome of write check
     */
    boolean canCreatePublic( IdentifiableObject identifiableObject );

    /**
     * Checks whether current user can create public instances of the object.
     *
     * @param type Type to check for write access.
     * @return true of false depending on outcome of write check
     */
    boolean canCreatePublic( String type );

    /**
     * Checks whether current user can create private instances of the object.
     *
     * @param identifiableObject Object to check for write access.
     * @return true of false depending on outcome of write check
     */
    boolean canCreatePrivate( IdentifiableObject identifiableObject );

    /**
     * Checks whether current user can create private instances of the object.
     *
     * @param type Type to check for write access.
     * @return true of false depending on outcome of write check
     */
    boolean canCreatePrivate( String type );

    /**
     * Checks whether current user has update access to object.
     *
     * @param identifiableObject Object to check for update access.
     * @return true of false depending on outcome of update check
     */
    boolean canUpdate( IdentifiableObject identifiableObject );

    /**
     * Checks whether current user has delete access to object.
     *
     * @param identifiableObject Object to check for delete access.
     * @return true of false depending on outcome of delete check
     */
    boolean canDelete( IdentifiableObject identifiableObject );

    /**
     * Checks whether current user has manage access to object.
     *
     * @param identifiableObject Object to check for manage access.
     * @return true of false depending on outcome of manage check
     */
    boolean canManage( IdentifiableObject identifiableObject );
}
