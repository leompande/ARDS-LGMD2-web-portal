package org.hisp.dhis.program;

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

import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.validation.ValidationCriteria;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.hisp.dhis.i18n.I18nUtils.i18n;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
@Transactional
public class DefaultProgramService
    implements ProgramService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramStore programStore;

    public void setProgramStore( ProgramStore programStore )
    {
        this.programStore = programStore;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    public int addProgram( Program program )
    {
        return programStore.save( program );
    }

    @Override
    public void updateProgram( Program program )
    {
        programStore.update( program );
    }

    @Override
    public void deleteProgram( Program program )
    {
        programStore.delete( program );
    }

    @Override
    public Collection<Program> getAllPrograms()
    {
        return i18n( i18nService, programStore.getAll() );
    }

    @Override
    public Program getProgram( int id )
    {
        return i18n( i18nService, programStore.get( id ) );
    }

    @Override
    public Program getProgramByName( String name )
    {
        return i18n( i18nService, programStore.getByName( name ) );
    }

    @Override
    public Collection<Program> getPrograms( OrganisationUnit organisationUnit )
    {
        Set<Program> programs = new HashSet<Program>();

        for ( Program program : getAllPrograms() )
        {
            Set<OrganisationUnitGroup> orgunitGroups = program.getOrganisationUnitGroups();
            for ( OrganisationUnitGroup orgunitGroup : orgunitGroups )
            {
                if ( orgunitGroup.getMembers().contains( organisationUnit ) )
                {
                    programs.add( program );
                }
            }
            if ( program.getOrganisationUnits().contains( organisationUnit ) )
            {
                programs.add( program );
            }
        }

        return i18n( i18nService, programs );
    }

    @Override
    public Collection<Program> getPrograms( ValidationCriteria validationCriteria )
    {
        Set<Program> programs = new HashSet<Program>();

        for ( Program program : getAllPrograms() )
        {
            if ( program.getPatientValidationCriteria().contains( validationCriteria ) )
            {
                programs.add( program );
            }
        }

        return i18n( i18nService, programs );
    }

    @Override
    public Collection<Program> getPrograms( int type )
    {
        return i18n( i18nService, programStore.getByType( type ) );
    }

    @Override
    public Collection<Program> getPrograms( int type, OrganisationUnit orgunit )
    {
        return i18n( i18nService, programStore.get( type, orgunit ) );
    }

    @Override
    public Collection<Program> getProgramsByCurrentUser()
    {
        return i18n( i18nService, programStore.getByCurrentUser() );
    }

    @Override
    public Collection<Program> getProgramsByCurrentUser( int type )
    {
        return i18n( i18nService, programStore.getByCurrentUser( type ) );
    }

    @Override
    public Program getProgram( String uid )
    {
        return i18n( i18nService, programStore.getByUid( uid ) );
    }

    @Override
    public Collection<Program> getProgramsByDisplayOnAllOrgunit( boolean displayOnAllOrgunit, OrganisationUnit orgunit )
    {
        return i18n( i18nService, programStore.getProgramsByDisplayOnAllOrgunit( displayOnAllOrgunit, orgunit ) );
    }

    @Override
    public Collection<Program> getProgramsByCurrentUser( OrganisationUnit organisationUnit )
    {
        Collection<Program> programs = new ArrayList<Program>( getProgramsByDisplayOnAllOrgunit( true, null ) );
        programs.addAll( getProgramsByDisplayOnAllOrgunit( false, organisationUnit ) );
        programs.retainAll( getProgramsByCurrentUser() );

        return programs;
    }

}
