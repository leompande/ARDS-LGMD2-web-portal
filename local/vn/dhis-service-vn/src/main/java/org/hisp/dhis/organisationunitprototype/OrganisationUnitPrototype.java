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

import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.common.BaseNameableObject;
import org.hisp.dhis.common.DxfNamespaces;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
@JacksonXmlRootElement( localName = "orgUnitProtoype", namespace = DxfNamespaces.DXF_2_0 )
public class OrganisationUnitPrototype
    extends BaseNameableObject
{
    private Set<OrganisationUnitPrototypeGroup> groups = new HashSet<OrganisationUnitPrototypeGroup>();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public OrganisationUnitPrototype()
    {
    }

    public OrganisationUnitPrototype( String name )
    {
        this.name = name;
    }

    public OrganisationUnitPrototype( String name, String shortName )
    {
        this.name = name;
        this.shortName = shortName;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public void addOrganisationUnitPrototypeGroup( OrganisationUnitPrototypeGroup orgUnitPrototypeGroup )
    {
        groups.add( orgUnitPrototypeGroup );
        orgUnitPrototypeGroup.getMembers().add( this );
    }

    public void removeOrganisationUnitPrototypeGroup( OrganisationUnitPrototypeGroup orgUnitPrototypeGroup )
    {
        groups.remove( orgUnitPrototypeGroup );
        orgUnitPrototypeGroup.getMembers().remove( this );
    }

    public void removeAllOrganisationUnitPrototypeGroups()
    {
        for ( OrganisationUnitPrototypeGroup orgUnitPrototypeGroup : groups )
        {
            orgUnitPrototypeGroup.getMembers().remove( this );
        }

        groups.clear();
    }

    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof OrganisationUnitPrototype) )
        {
            return false;
        }

        final OrganisationUnitPrototype other = (OrganisationUnitPrototype) o;

        return name.equals( other.getName() );
    }

    @Override
    public String toString()
    {
        return "[" + name + "]";
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public Set<OrganisationUnitPrototypeGroup> getGroups()
    {
        return groups;
    }

    public void setGroups( Set<OrganisationUnitPrototypeGroup> groups )
    {
        this.groups = groups;
    }
}
