package org.hisp.dhis.importexport.importer;

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

import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.Importer;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import java.util.List;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class OrganisationUnitLevelImporter
    extends AbstractImporter<OrganisationUnitLevel> implements Importer<OrganisationUnitLevel>
{
    protected OrganisationUnitService organisationUnitService;

    public OrganisationUnitLevelImporter()
    {
    }

    public OrganisationUnitLevelImporter( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    @Override
    public void importObject( OrganisationUnitLevel object, ImportParams params )
    {
        read( object, GroupMemberType.NONE, params );
    }

    @Override
    protected void importUnique( OrganisationUnitLevel object )
    {
        organisationUnitService.addOrganisationUnitLevel( object );
    }

    @Override
    protected void importMatching( OrganisationUnitLevel object, OrganisationUnitLevel match )
    {
        match.setLevel( object.getLevel() );
        match.setName( object.getName() );

        organisationUnitService.updateOrganisationUnitLevel( match );
    }

    @Override
    protected OrganisationUnitLevel getMatching( OrganisationUnitLevel object )
    {
        OrganisationUnitLevel match = organisationUnitService.getOrganisationUnitLevelByLevel( object.getLevel() );

        if ( match == null )
        {
            List<OrganisationUnitLevel> organisationUnitLevelByName = organisationUnitService.getOrganisationUnitLevelByName( object.getName() );
            match = organisationUnitLevelByName.isEmpty() ? null : organisationUnitLevelByName.get( 0 );
        }

        return match;
    }

    @Override
    protected boolean isIdentical( OrganisationUnitLevel object, OrganisationUnitLevel existing )
    {
        if ( object.getLevel() != existing.getLevel() )
        {
            return false;
        }
        if ( !object.getName().equals( existing.getName() ) )
        {
            return false;
        }

        return true;
    }
}
