package org.hisp.dhis.reports.reportmanagement.action;

import java.util.Collection;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;

import com.opensymphony.xwork2.Action;

public class UnselectLevelAction
    implements Action
{
    private static final int FIRST_LEVEL = 1;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer level;

    public void setLevel( Integer level )
    {
        this.level = level;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Integer selectLevel;

    public Integer getSelectLevel()
    {
        return selectLevel;
    }

    // -------------------------------------------------------------------------
    // ActionSupport implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        Collection<OrganisationUnit> rootUnits = selectionTreeManager.getRootOrganisationUnits();

        Collection<OrganisationUnit> selectedUnits = selectionTreeManager.getSelectedOrganisationUnits();

        for ( OrganisationUnit rootUnit : rootUnits )
        {
            unselectLevel( rootUnit, FIRST_LEVEL, selectedUnits );
        }

        selectionTreeManager.setSelectedOrganisationUnits( selectedUnits );

        selectLevel = level;

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void unselectLevel( OrganisationUnit orgUnit, int currentLevel, Collection<OrganisationUnit> selectedUnits )
    {
        if ( currentLevel == level )
        {
            selectedUnits.remove( orgUnit );
        }
        else
        {
            for ( OrganisationUnit child : orgUnit.getChildren() )
            {
                unselectLevel( child, currentLevel + 1, selectedUnits );
            }
        }
    }
}
