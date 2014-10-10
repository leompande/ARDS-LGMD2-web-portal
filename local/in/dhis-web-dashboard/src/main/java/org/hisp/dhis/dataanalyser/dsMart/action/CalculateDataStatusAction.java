package org.hisp.dhis.dataanalyser.dsMart.action;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

public class CalculateDataStatusAction
    implements Action
{

    Collection<Period> periods = new ArrayList<Period>();

    Collection<DataSet> dataSets = new ArrayList<DataSet>();

    //--------------------------------------------------------------------------
    // Dependencies
    //--------------------------------------------------------------------------

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    //--------------------------------------------------------------------------
    // Input/Output
    //--------------------------------------------------------------------------

    private Collection<Integer> selectedPeriods = new ArrayList<Integer>();

    public void setSelectedPeriods( Collection<Integer> selectedPeriods )
    {
        this.selectedPeriods = selectedPeriods;
    }

    private Collection<String> selectedDatasets = new ArrayList<String>();

    public void setSelectedDatasets( Collection<String> selectedDatasets )
    {
        this.selectedDatasets = selectedDatasets;
    }

    String deInfo;
    
    private DataSet selDataSet;

    //--------------------------------------------------------------------------
    // Action Implementation
    //--------------------------------------------------------------------------

    public String execute()
    {
        
        for ( Integer periodId : selectedPeriods )
        {
            periods.add( periodService.getPeriod( periodId.intValue() ) );
        }

        for ( String ds : selectedDatasets )
        {
            DataSet dSet = dataSetService.getDataSet( Integer.parseInt( ds ) );
            selDataSet = dSet;
            for ( DataElement de : dSet.getDataElements() )
                deInfo += "," + de.getId();
        }

        String currentUserName = currentUserService.getCurrentUsername();

        Collection<OrganisationUnit> selectedOrganisationUnits = selectionTreeManager.getSelectedOrganisationUnits();
        
        return SUCCESS;
    }

}
