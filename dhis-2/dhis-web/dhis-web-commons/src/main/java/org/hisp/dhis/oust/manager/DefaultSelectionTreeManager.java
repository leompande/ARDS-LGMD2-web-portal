package org.hisp.dhis.oust.manager;

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

import com.opensymphony.xwork2.ActionContext;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: DefaultSelectionTreeManager.java 5652 2008-09-06 13:24:34Z larshelg $
 */
public class DefaultSelectionTreeManager
    implements SelectionTreeManager
{
    private static final String SESSION_KEY_SELECTED_ORG_UNITS = "dhis-oust-selected-org-units";
    private static final String SESSION_KEY_ROOT_ORG_UNITS = "dhis-oust-root-org-units";

    private static final double PERCENTAGE_OF_MULTIPLE_RELOADING_ORG_UNITS = 0.2;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // -------------------------------------------------------------------------
    // SelectionTreeManager implementation
    // -------------------------------------------------------------------------

    public void setRootOrganisationUnits( Collection<OrganisationUnit> organisationUnits )
    {
        if ( organisationUnits == null )
        {
            throw new IllegalArgumentException( "Root OrganisationUnit cannot be null" );
        }

        saveToSession( SESSION_KEY_ROOT_ORG_UNITS, new HashSet<OrganisationUnit>( organisationUnits ) );

        clearSelectedOrganisationUnits();
    }

    public void setRootOrganisationUnitsParent( OrganisationUnit rootUnitsParent )
    {
        if ( rootUnitsParent == null )
        {
            throw new IllegalArgumentException( "Root OrganisationUnits parent cannot be null" );
        }

        OrganisationUnit reloadedRootUnitsParent = reloadOrganisationUnit( rootUnitsParent );

        saveToSession( SESSION_KEY_ROOT_ORG_UNITS, reloadedRootUnitsParent.getChildren() );

        clearSelectedOrganisationUnits();
    }

    public Collection<OrganisationUnit> getRootOrganisationUnits()
    {
        Collection<OrganisationUnit> rootUnits = getCollectionFromSession( SESSION_KEY_ROOT_ORG_UNITS );

        if ( rootUnits == null )
        {
            return organisationUnitService.getRootOrganisationUnits();
        }

        return reloadOrganisationUnits( rootUnits );
    }

    public OrganisationUnit getRootOrganisationUnitsParent()
    {
        Collection<OrganisationUnit> rootUnits = getCollectionFromSession( SESSION_KEY_ROOT_ORG_UNITS );

        if ( rootUnits == null || rootUnits.isEmpty() )
        {
            return null;
        }

        OrganisationUnit randomRootUnit = rootUnits.iterator().next();

        OrganisationUnit reloadedRootUnit = reloadOrganisationUnit( randomRootUnit );

        return reloadedRootUnit.getParent();
    }

    public void resetRootOrganisationUnits()
    {
        removeFromSession( SESSION_KEY_ROOT_ORG_UNITS );
    }

    public void setSelectedOrganisationUnits( Collection<OrganisationUnit> selectedUnits )
    {
        if ( selectedUnits == null )
        {
            throw new IllegalArgumentException( "Selected OrganisationUnits cannot be null" );
        }

        // ---------------------------------------------------------------------
        // Remove all selected units that are not in the trees
        // ---------------------------------------------------------------------

        Collection<OrganisationUnit> rootUnits = getRootOrganisationUnits();

        if ( rootUnits != null )
        {
            selectedUnits = getUnitsInTree( rootUnits, selectedUnits );

            saveToSession( SESSION_KEY_SELECTED_ORG_UNITS, selectedUnits );
        }
    }

    public Collection<OrganisationUnit> getSelectedOrganisationUnits()
    {
        Collection<OrganisationUnit> selectedUnits = getCollectionFromSession( SESSION_KEY_SELECTED_ORG_UNITS );

        if ( selectedUnits == null )
        {
            return new HashSet<OrganisationUnit>();
        }

        return selectedUnits;
    }

    public Collection<OrganisationUnit> getReloadedSelectedOrganisationUnits()
    {
        return reloadOrganisationUnits( getSelectedOrganisationUnits() );
    }

    public OrganisationUnit getReloadedSelectedOrganisationUnit()
    {
        return reloadOrganisationUnit( getSelectedOrganisationUnit() );
    }

    public void clearSelectedOrganisationUnits()
    {
        removeFromSession( SESSION_KEY_SELECTED_ORG_UNITS );
    }

    public OrganisationUnit getSelectedOrganisationUnit()
    {
        Collection<OrganisationUnit> selectedUnits = getSelectedOrganisationUnits();

        if ( selectedUnits.isEmpty() )
        {
            return null;
        }

        return selectedUnits.iterator().next();
    }

    public void setSelectedOrganisationUnit( OrganisationUnit selectedUnit )
    {
        if ( selectedUnit == null )
        {
            throw new IllegalArgumentException( "Selected OrganisationUnit cannot be null" );
        }

        Set<OrganisationUnit> set = new HashSet<OrganisationUnit>( 1 );
        set.add( selectedUnit );
        setSelectedOrganisationUnits( set );
    }

    public boolean setCurrentUserOrganisationUnitAsSelected()
    {
        User user = currentUserService.getCurrentUser();

        if ( user != null && user.getOrganisationUnit() != null )
        {
            setSelectedOrganisationUnit( user.getOrganisationUnit() );
            return true;
        }

        return false;
    }

    // -------------------------------------------------------------------------
    // Session methods
    // -------------------------------------------------------------------------

    protected Map<String, Object> getSession()
    {
        return ActionContext.getContext().getSession();
    }

    private final void saveToSession( String key, Object object )
    {
        getSession().put( key, object );
    }

    @SuppressWarnings("unchecked")
    private final Collection<OrganisationUnit> getCollectionFromSession( String key )
    {
        return (Collection<OrganisationUnit>) getSession().get( key );
    }

    private final void removeFromSession( String key )
    {
        getSession().remove( key );
    }

    // -------------------------------------------------------------------------
    // Reload methods
    // -------------------------------------------------------------------------

    private OrganisationUnit reloadOrganisationUnit( OrganisationUnit unit )
    {
        return unit == null ? null : organisationUnitService.getOrganisationUnit( unit.getId() );
    }

    private Collection<OrganisationUnit> reloadOrganisationUnits( Collection<OrganisationUnit> units )
    {
        Set<OrganisationUnit> reloadedUnits = new HashSet<OrganisationUnit>();

        int noTotal = organisationUnitService.getNumberOfOrganisationUnits();

        int noSelected = units.size();

        if ( (double) noSelected / noTotal > PERCENTAGE_OF_MULTIPLE_RELOADING_ORG_UNITS ) // Select all at once
        {
            Collection<OrganisationUnit> allOrgUnits = organisationUnitService.getAllOrganisationUnits();

            for ( OrganisationUnit unit : allOrgUnits )
            {
                if ( units.contains( unit ) )
                {
                    reloadedUnits.add( unit );
                }
            }
        }
        else // Select one by one
        {
            for ( OrganisationUnit unit : units )
            {
                OrganisationUnit reloadedUnit = reloadOrganisationUnit( unit );

                if ( reloadedUnit != null )
                {
                    reloadedUnits.add( reloadedUnit );
                }
            }
        }
        return reloadedUnits;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Collection<OrganisationUnit> getUnitsInTree( Collection<OrganisationUnit> rootUnits, Collection<OrganisationUnit> selectedUnits )
    {
        Collection<OrganisationUnit> unitsInTree = new ArrayList<OrganisationUnit>();

        for ( OrganisationUnit selectedUnit : selectedUnits )
        {
            if ( rootUnits.contains( selectedUnit ) )
            {
                unitsInTree.add( selectedUnit );
                continue;
            }

            OrganisationUnit parent = selectedUnit.getParent();

            while ( parent != null )
            {
                if ( rootUnits.contains( parent ) )
                {
                    unitsInTree.add( selectedUnit );
                }

                parent = parent.getParent();
            }
        }

        return unitsInTree;
    }
}
