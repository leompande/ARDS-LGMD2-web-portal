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

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.system.filter.UserCredentialsCanUpdateFilter;
import org.hisp.dhis.system.util.FilterUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;
import org.hisp.dhis.user.comparator.UsernameComparator;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: GetUserListAction.java 2869 2007-02-20 14:26:09Z andegje $
 */
public class GetUserListAction
    extends ActionPagingSupport<User>
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private List<UserCredentials> userCredentialsList;

    public List<UserCredentials> getUserCredentialsList()
    {
        return userCredentialsList;
    }

    private String currentUserName;

    public String getCurrentUserName()
    {
        return currentUserName;
    }

    private String key;

    public void setKey( String key )
    {
        this.key = key;
    }

    public String getKey()
    {
        return key;
    }

    private Integer months;

    public Integer getMonths()
    {
        return months;
    }

    public void setMonths( Integer months )
    {
        this.months = months;
    }

    private Boolean selfRegistered;

    public Boolean getSelfRegistered()
    {
        return selfRegistered;
    }

    public void setSelfRegistered( Boolean selfRegistered )
    {
        this.selfRegistered = selfRegistered;
    }

    // -------------------------------------------------------------------------
    // Action implemantation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        if ( isNotBlank( key ) ) // Filter on key only if set
        {
            this.paging = createPaging( userService.getUserCountByName( key ) );

            userCredentialsList = new ArrayList<UserCredentials>( userService.searchUsersByName( key, paging.getStartPos(),
                paging.getPageSize() ) );

            Collections.sort( userCredentialsList, new UsernameComparator() );
        }
        else if ( months != null && months != 0 )
        {
            this.paging = createPaging( userService.getInactiveUsersCount( months ) );

            userCredentialsList = new ArrayList<UserCredentials>( userService.getInactiveUsers( months, paging
                .getStartPos(), paging.getPageSize() ) );
        }
        else if ( Boolean.TRUE.equals( selfRegistered ) )
        {
            this.paging = createPaging( userService.getSelfRegisteredUserCredentialsCount() );
            
            userCredentialsList = new ArrayList<UserCredentials>( userService.getSelfRegisteredUserCredentials( paging.
                getStartPos(), paging.getPageSize() ) );
        }
        else
        {
            this.paging = createPaging( userService.getUserCount() );

            userCredentialsList = new ArrayList<UserCredentials>( userService.getUsersBetween( paging.getStartPos(),
                paging.getPageSize() ) );

            Collections.sort( userCredentialsList, new UsernameComparator() );
        }

        FilterUtils.filter( userCredentialsList, new UserCredentialsCanUpdateFilter( currentUserService
            .getCurrentUser() ) );

        currentUserName = currentUserService.getCurrentUsername();

        return SUCCESS;
    }
}
