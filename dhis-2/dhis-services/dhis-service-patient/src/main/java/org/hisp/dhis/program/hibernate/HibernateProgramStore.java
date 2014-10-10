package org.hisp.dhis.program.hibernate;

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

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStore;
import org.hisp.dhis.system.util.CollectionUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserService;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Chau Thu Tran
 */
public class HibernateProgramStore
    extends HibernateIdentifiableObjectStore<Program>
    implements ProgramStore
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    // -------------------------------------------------------------------------
    // Implemented methods
    // -------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Program> getByType( int type )
    {
        return getCriteria( Restrictions.eq( "type", type ) ).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Program> get( int type, OrganisationUnit organisationUnit )
    {
        Criteria criteria = getCriteria();
        criteria.createAlias( "organisationUnits", "orgunit" );
        criteria.add( Restrictions.eq( "type", type ) );
        criteria.add( Restrictions.eq( "orgunit.id", organisationUnit.getId() ) );
        return criteria.list();
    }

    @Override
    public Collection<Program> getByCurrentUser()
    {
        Collection<Program> programs = new HashSet<Program>();

        if ( currentUserService.getCurrentUser() != null && !currentUserService.currentUserIsSuper() )
        {
            Set<UserAuthorityGroup> userRoles = userService.getUserCredentials( currentUserService.getCurrentUser() )
                .getUserAuthorityGroups();

            for ( Program program : getAll() )
            {
                if ( userRoles.contains( UserAuthorityGroup.AUTHORITY_ALL )
                    || CollectionUtils.intersection( program.getUserRoles(), userRoles ).size() > 0 )
                {
                    programs.add( program );
                }
            }
        }
        else
        {
            programs = getAll();
        }
        
        return programs;
    }

    @Override
    public Collection<Program> getByCurrentUser( int type )
    {
        Collection<Program> programs = new HashSet<Program>();

        if ( currentUserService.getCurrentUser() != null && !currentUserService.currentUserIsSuper() )
        {
            Set<UserAuthorityGroup> userRoles = userService.getUserCredentials( currentUserService.getCurrentUser() )
                .getUserAuthorityGroups();

            for ( Program program : getByType( type ) )
            {
                if ( userRoles.contains( UserAuthorityGroup.AUTHORITY_ALL )
                    || CollectionUtils.intersection( program.getUserRoles(), userRoles ).size() > 0 )
                {
                    programs.add( program );
                }
            }
        }
        else
        {
            programs = getByType( type );
        }
        
        return programs;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Program> getProgramsByDisplayOnAllOrgunit( boolean displayOnAllOrgunit, OrganisationUnit orgunit )
    {
        Criteria criteria = getCriteria();
        criteria.add( Restrictions.eq( "displayOnAllOrgunit", displayOnAllOrgunit ) );

        if ( orgunit != null )
        {
            criteria.createAlias( "organisationUnits", "orgunit" );
            criteria.add( Restrictions.eq( "orgunit.id", orgunit.getId() ) );
        }

        return criteria.list();
    }
}
