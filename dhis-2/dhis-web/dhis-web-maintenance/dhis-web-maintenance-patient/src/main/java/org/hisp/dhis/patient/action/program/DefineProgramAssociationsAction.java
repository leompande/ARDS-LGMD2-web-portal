package org.hisp.dhis.patient.action.program;

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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;

import com.opensymphony.xwork2.Action;

/**
 * @author Kristian
 * @version $Id: DefineDataSetAssociationsAction.java 3648 2007-10-15 22:47:45Z
 *          larshelg $
 */
public class DefineProgramAssociationsAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    private int id;

    public void setId( int id )
    {
        this.id = id;
    }

    private Collection<Integer> orgunitGroupIds = new HashSet<Integer>();

    public void setOrgunitGroupIds( Collection<Integer> orgunitGroupIds )
    {
        this.orgunitGroupIds = orgunitGroupIds;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        Collection<OrganisationUnit> rootUnits = selectionTreeManager.getRootOrganisationUnits();

        Set<OrganisationUnit> unitsInTheTree = new HashSet<OrganisationUnit>();

        getUnitsInTheTree( rootUnits, unitsInTheTree );

        Program program = programService.getProgram( id );

        Set<OrganisationUnit> assignedUnits = program.getOrganisationUnits();

        assignedUnits.removeAll( convert( unitsInTheTree ) );

        Collection<OrganisationUnit> selectedOrganisationUnits = selectionTreeManager
            .getReloadedSelectedOrganisationUnits();

        assignedUnits.addAll( convert( selectedOrganisationUnits ) );

        program.setOrganisationUnits( assignedUnits );
        
        if ( orgunitGroupIds != null )
        {
            Set<OrganisationUnitGroup> orgunitGroups = new HashSet<OrganisationUnitGroup>();
            for ( Integer orgunitGroupId : orgunitGroupIds )
            {
                orgunitGroups.add( organisationUnitGroupService.getOrganisationUnitGroup( orgunitGroupId ) );
            }
            program.setOrganisationUnitGroups( orgunitGroups );
        }
        
        programService.updateProgram( program );

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supporting methods
    // -------------------------------------------------------------------------

    private Set<OrganisationUnit> convert( Collection<OrganisationUnit> organisationUnits )
    {
        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();

        units.addAll( organisationUnits );

        return units;
    }

    private void getUnitsInTheTree( Collection<OrganisationUnit> rootUnits, Set<OrganisationUnit> unitsInTheTree )
    {
        for ( OrganisationUnit root : rootUnits )
        {
            unitsInTheTree.add( root );
            getUnitsInTheTree( root.getChildren(), unitsInTheTree );
        }
    }
}
