package org.hisp.dhis.organisationunitprototype;

/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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

import static org.hisp.dhis.i18n.I18nUtils.getCountByName;
import static org.hisp.dhis.i18n.I18nUtils.getObjectsBetween;
import static org.hisp.dhis.i18n.I18nUtils.getObjectsBetweenByName;
import static org.hisp.dhis.i18n.I18nUtils.getObjectsByName;
import static org.hisp.dhis.i18n.I18nUtils.i18n;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
@Transactional
public class DefaultOrganisationUnitPrototypeService
    implements OrganisationUnitPrototypeService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitPrototypeStore organisationUnitPrototypeStore;

    public void setOrganisationUnitPrototypeStore( OrganisationUnitPrototypeStore organisationUnitPrototypeStore )
    {
        this.organisationUnitPrototypeStore = organisationUnitPrototypeStore;
    }

    private GenericIdentifiableObjectStore<OrganisationUnitPrototypeGroup> organisationUnitPrototypeGroupStore;

    public void setOrganisationUnitPrototypeGroupStore(
        GenericIdentifiableObjectStore<OrganisationUnitPrototypeGroup> organisationUnitPrototypeGroupStore )
    {
        this.organisationUnitPrototypeGroupStore = organisationUnitPrototypeGroupStore;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    // -------------------------------------------------------------------------
    // OrganisationUnitPrototype
    // -------------------------------------------------------------------------

    public int addOrganisationUnitPrototype( OrganisationUnitPrototype orgUnitPrototype )
    {
        return organisationUnitPrototypeStore.save( orgUnitPrototype );
    }

    public void updateOrganisationUnitPrototype( OrganisationUnitPrototype orgUnitPrototype )
    {
        organisationUnitPrototypeStore.update( orgUnitPrototype );
    }

    public void deleteOrganisationUnitPrototype( OrganisationUnitPrototype orgUnitPrototype )
    {
        organisationUnitPrototypeStore.delete( orgUnitPrototype );
    }

    public OrganisationUnitPrototype getOrganisationUnitPrototype( int id )
    {
        return i18n( i18nService, organisationUnitPrototypeStore.get( id ) );
    }

    public OrganisationUnitPrototype getOrganisationUnitPrototype( String uid )
    {
        return i18n( i18nService, organisationUnitPrototypeStore.getByUid( uid ) );
    }

    public Collection<OrganisationUnitPrototype> getAllOrganisationUnitPrototypes()
    {
        return i18n( i18nService, organisationUnitPrototypeStore.getAll() );
    }

    public Collection<OrganisationUnitPrototype> getOrganisationUnitPrototypes( final Collection<Integer> identifiers )
    {
        Collection<OrganisationUnitPrototype> orgUnitPrototypes = getAllOrganisationUnitPrototypes();

        return identifiers == null ? orgUnitPrototypes : FilterUtils.filter( orgUnitPrototypes,
            new Filter<OrganisationUnitPrototype>()
            {
                public boolean retain( OrganisationUnitPrototype object )
                {
                    return identifiers.contains( object.getId() );
                }
            } );
    }

    public List<OrganisationUnitPrototype> getOrganisationUnitPrototypesByUid( Collection<String> uids )
    {
        return organisationUnitPrototypeStore.getByUid( uids );
    }

    public OrganisationUnitPrototype getOrganisationUnitPrototypeByName( String name )
    {
        return i18n( i18nService, organisationUnitPrototypeStore.getByName( name ) );
    }

    public OrganisationUnitPrototype getOrganisationUnitPrototypeByCode( String code )
    {
        return i18n( i18nService, organisationUnitPrototypeStore.getByCode( code ) );
    }

    public OrganisationUnitPrototype getOrganisationUnitPrototypeByShortName( String shortName )
    {
        return i18n( i18nService, organisationUnitPrototypeStore.getByShortName( shortName ) );
    }

    public Collection<OrganisationUnitPrototype> getOrganisationUnitPrototypesWithoutGroups()
    {
        return i18n( i18nService, organisationUnitPrototypeStore.getOrganisationUnitPrototypesWithoutGroups() );
    }

    public Collection<OrganisationUnitPrototype> getOrganisationUnitPrototypesLikeName( String name )
    {
        return getObjectsByName( i18nService, organisationUnitPrototypeStore, name );
    }

    public int getOrganisationUnitPrototypeCount()
    {
        return organisationUnitPrototypeStore.getCount();
    }

    public int getOrganisationUnitPrototypeCountByName( String name )
    {
        return getCountByName( i18nService, organisationUnitPrototypeStore, name );
    }

    public Collection<OrganisationUnitPrototype> getOrganisationUnitPrototypesBetween( int first, int max )
    {
        return getObjectsBetween( i18nService, organisationUnitPrototypeStore, first, max );
    }

    public Collection<OrganisationUnitPrototype> getOrganisationUnitPrototypesBetweenByName( String name, int first,
        int max )
    {
        return getObjectsBetweenByName( i18nService, organisationUnitPrototypeStore, name, first, max );
    }

    // -------------------------------------------------------------------------
    // OrganisationUnitPrototypeGroup
    // -------------------------------------------------------------------------

    public int addOrganisationUnitPrototypeGroup( OrganisationUnitPrototypeGroup orgUnitPrototypeGroup )
    {
        return organisationUnitPrototypeGroupStore.save( orgUnitPrototypeGroup );
    }

    public void updateOrganisationUnitPrototypeGroup( OrganisationUnitPrototypeGroup orgUnitPrototypeGroup )
    {
        organisationUnitPrototypeGroupStore.update( orgUnitPrototypeGroup );
    }

    public void deleteOrganisationUnitPrototypeGroup( OrganisationUnitPrototypeGroup orgUnitPrototypeGroup )
    {
        organisationUnitPrototypeGroupStore.delete( orgUnitPrototypeGroup );
    }

    public OrganisationUnitPrototypeGroup getOrganisationUnitPrototypeGroup( int id )
    {
        return i18n( i18nService, organisationUnitPrototypeGroupStore.get( id ) );
    }

    public OrganisationUnitPrototypeGroup getOrganisationUnitPrototypeGroup( int id,
        boolean i18nOrganisationUnitPrototypes )
    {
        OrganisationUnitPrototypeGroup group = getOrganisationUnitPrototypeGroup( id );

        if ( i18nOrganisationUnitPrototypes )
        {
            i18n( i18nService, group.getMembers() );
        }

        return group;
    }

    public Collection<OrganisationUnitPrototypeGroup> getOrganisationUnitPrototypeGroups(
        final Collection<Integer> identifiers )
    {
        Collection<OrganisationUnitPrototypeGroup> groups = getAllOrganisationUnitPrototypeGroups();

        return identifiers == null ? groups : FilterUtils.filter( groups, new Filter<OrganisationUnitPrototypeGroup>()
        {
            public boolean retain( OrganisationUnitPrototypeGroup object )
            {
                return identifiers.contains( object.getId() );
            }
        } );
    }

    public OrganisationUnitPrototypeGroup getOrganisationUnitPrototypeGroup( String uid )
    {
        return i18n( i18nService, organisationUnitPrototypeGroupStore.getByUid( uid ) );
    }

    public Collection<OrganisationUnitPrototypeGroup> getAllOrganisationUnitPrototypeGroups()
    {
        return i18n( i18nService, organisationUnitPrototypeGroupStore.getAll() );
    }

    public OrganisationUnitPrototypeGroup getOrganisationUnitPrototypeGroupByName( String name )
    {
        return i18n( i18nService, organisationUnitPrototypeGroupStore.getByName( name ) );
    }

    public Collection<OrganisationUnitPrototypeGroup> getGroupsContainingOrganisationUnitPrototype(
        OrganisationUnitPrototype orgUnitPrototype )
    {
        Collection<OrganisationUnitPrototypeGroup> groups = getAllOrganisationUnitPrototypeGroups();

        Iterator<OrganisationUnitPrototypeGroup> iterator = groups.iterator();

        while ( iterator.hasNext() )
        {
            OrganisationUnitPrototypeGroup group = iterator.next();

            if ( !group.getMembers().contains( orgUnitPrototype ) )
            {
                iterator.remove();
            }
        }

        return groups;
    }

    public int getOrganisationUnitPrototypeGroupCount()
    {
        return organisationUnitPrototypeGroupStore.getCount();
    }

    public int getOrganisationUnitPrototypeGroupCountByName( String name )
    {
        return getCountByName( i18nService, organisationUnitPrototypeGroupStore, name );
    }

    public Collection<OrganisationUnitPrototypeGroup> getOrganisationUnitPrototypeGroupsBetween( int first, int max )
    {
        return getObjectsBetween( i18nService, organisationUnitPrototypeGroupStore, first, max );
    }

    public Collection<OrganisationUnitPrototypeGroup> getOrganisationUnitPrototypeGroupsBetweenByName( String name,
        int first, int max )
    {
        return getObjectsBetweenByName( i18nService, organisationUnitPrototypeGroupStore, name, first, max );
    }
}
