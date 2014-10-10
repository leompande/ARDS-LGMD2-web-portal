package org.hisp.dhis.dataanalyser.dsMart.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;

import com.opensymphony.xwork2.Action;

public class ValidateDataEntryStatusAction
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

    // -------------------------------------------------------------------------
    // I18n
    // -------------------------------------------------------------------------

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Collection<Integer> selectedPeriods = new ArrayList<Integer>();

    public void setSelectedPeriods( Collection<Integer> selectedPeriods )
    {
        this.selectedPeriods = selectedPeriods;
    }

    private Collection<Integer> selectedDataSets = new ArrayList<Integer>();

    public void setSelectedDataSets( Collection<Integer> selectedDataSets )
    {
        this.selectedDataSets = selectedDataSets;
    }
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String message;

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        if ( selectedPeriods == null || selectedPeriods.size() == 0 )
        {
            message = i18n.getString( "period_not_selected" );

            return INPUT;
        }

        if ( selectedDataSets == null || selectedDataSets.size() == 0 )
        {
            message = i18n.getString( "dataset_not_selected" );

            return INPUT;
        }
        
        Collection<OrganisationUnit> selectedUnits = new HashSet<OrganisationUnit>();
        selectedUnits = selectionTreeManager.getSelectedOrganisationUnits();

        if ( selectedUnits == null || selectedUnits.size() == 0 )
        {
            message = i18n.getString( "organisation_not_selected" );

            return INPUT;
        }

        return SUCCESS;
    }
}
