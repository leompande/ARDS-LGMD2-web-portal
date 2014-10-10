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

import java.util.Collection;
import java.util.List;

import org.hisp.dhis.organisationunitprototype.OrganisationUnitPrototype;
import org.hisp.dhis.organisationunitprototype.OrganisationUnitPrototypeGroup;
import org.hisp.dhis.organisationunitprototype.OrganisationUnitPrototypeService;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public interface OrganisationUnitPrototypeService
{
    String ID = OrganisationUnitPrototypeService.class.getName();

    // -------------------------------------------------------------------------
    // OrganisationUnitPrototype
    // -------------------------------------------------------------------------

    int addOrganisationUnitPrototype( OrganisationUnitPrototype indicator );

    void updateOrganisationUnitPrototype( OrganisationUnitPrototype indicator );

    void deleteOrganisationUnitPrototype( OrganisationUnitPrototype indicator );

    OrganisationUnitPrototype getOrganisationUnitPrototype( int id );

    OrganisationUnitPrototype getOrganisationUnitPrototype( String uid );

    Collection<OrganisationUnitPrototype> getAllOrganisationUnitPrototypes();

    Collection<OrganisationUnitPrototype> getOrganisationUnitPrototypes( Collection<Integer> identifiers );

    List<OrganisationUnitPrototype> getOrganisationUnitPrototypesByUid( Collection<String> uids );

    OrganisationUnitPrototype getOrganisationUnitPrototypeByName( String name );

    OrganisationUnitPrototype getOrganisationUnitPrototypeByCode( String code );

    Collection<OrganisationUnitPrototype> getOrganisationUnitPrototypesWithoutGroups();

    int getOrganisationUnitPrototypeCountByName( String name );

    Collection<OrganisationUnitPrototype> getOrganisationUnitPrototypesLikeName( String name );

    Collection<OrganisationUnitPrototype> getOrganisationUnitPrototypesBetweenByName( String name, int first, int max );

    int getOrganisationUnitPrototypeCount();

    Collection<OrganisationUnitPrototype> getOrganisationUnitPrototypesBetween( int first, int max );

    // -------------------------------------------------------------------------
    // OrganisationUnitPrototypeGroup
    // -------------------------------------------------------------------------

    int addOrganisationUnitPrototypeGroup( OrganisationUnitPrototypeGroup indicatorGroup );

    void updateOrganisationUnitPrototypeGroup( OrganisationUnitPrototypeGroup indicatorGroup );

    void deleteOrganisationUnitPrototypeGroup( OrganisationUnitPrototypeGroup indicatorGroup );

    OrganisationUnitPrototypeGroup getOrganisationUnitPrototypeGroup( int id );

    OrganisationUnitPrototypeGroup getOrganisationUnitPrototypeGroup( int id, boolean i18nOrganisationUnitPrototypes );

    Collection<OrganisationUnitPrototypeGroup> getOrganisationUnitPrototypeGroups( Collection<Integer> identifiers );

    OrganisationUnitPrototypeGroup getOrganisationUnitPrototypeGroup( String uid );

    Collection<OrganisationUnitPrototypeGroup> getAllOrganisationUnitPrototypeGroups();

    OrganisationUnitPrototypeGroup getOrganisationUnitPrototypeGroupByName( String name );

    Collection<OrganisationUnitPrototypeGroup> getGroupsContainingOrganisationUnitPrototype(
        OrganisationUnitPrototype indicator );

    Collection<OrganisationUnitPrototypeGroup> getOrganisationUnitPrototypeGroupsBetween( int first, int max );

    Collection<OrganisationUnitPrototypeGroup> getOrganisationUnitPrototypeGroupsBetweenByName( String name, int first,
        int max );

    int getOrganisationUnitPrototypeGroupCount();

    int getOrganisationUnitPrototypeGroupCountByName( String name );
}
