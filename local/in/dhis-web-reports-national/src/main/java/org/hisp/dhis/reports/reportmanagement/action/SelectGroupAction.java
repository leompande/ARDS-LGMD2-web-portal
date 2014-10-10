package org.hisp.dhis.reports.reportmanagement.action;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;

import com.opensymphony.xwork2.Action;

public class SelectGroupAction
    implements Action

{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer group;

    public void setGroup( Integer group )
    {
        this.group = group;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Integer selectgroup;

    public Integer getSelectgroup()
    {
        return selectgroup;
    }

    // -------------------------------------------------------------------------
    // ActionSupport implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        Collection<OrganisationUnit> selectedUnits = selectionTreeManager.getSelectedOrganisationUnits();

        Collection<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>();
        organisationUnits = organisationUnitGroupService.getOrganisationUnitGroup( group.intValue() ).getMembers();

        selectedUnits.addAll( organisationUnits );

        selectionTreeManager.setSelectedOrganisationUnits( selectedUnits );

        selectgroup = group;

        return SUCCESS;
    }

}
