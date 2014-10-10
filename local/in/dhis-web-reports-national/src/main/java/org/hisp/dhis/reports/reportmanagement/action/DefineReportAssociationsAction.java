package org.hisp.dhis.reports.reportmanagement.action;

import java.util.HashSet;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;

import com.opensymphony.xwork2.Action;

/**
 * @author Kristian
 * @version $Id: DefineReportAssociationsAction.java 3648 2007-10-15 22:47:45Z
 *          larshelg $
 */
public class DefineReportAssociationsAction
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

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    // -------------------------------------------------------------------------
    // Input/Output Getters & setters
    // -------------------------------------------------------------------------

    private int reportId;

    public void setReportId( int reportId )
    {
        this.reportId = reportId;
    }

    // -------------------------------------------------------------------------
    // ActionSupport implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        Report_in report = reportService.getReport( reportId );
       

        report.updateOrganisationUnits( new HashSet<OrganisationUnit>( selectionTreeManager.getReloadedSelectedOrganisationUnits() ) );
        
        reportService.updateReport( report );

        
        /*
        
        Collection<OrganisationUnit> rootUnits = selectionTreeManager.getRootOrganisationUnits();

        Set<OrganisationUnit> unitsInTheTree = new HashSet<OrganisationUnit>();

        getUnitsInTheTree( rootUnits, unitsInTheTree );

        Report_in report = reportService.getReport( reportId );

        Set<OrganisationUnit> assignedSources = report.getSources();

        assignedSources.removeAll( unitsInTheTree );

        Collection<OrganisationUnit> selectedOrganisationUnits = selectionTreeManager.getSelectedOrganisationUnits();

        assignedSources.addAll( selectedOrganisationUnits );

        report.setSources( assignedSources );

        reportService.updateReport( report );
        */
        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /*
    private void getUnitsInTheTree( Collection<OrganisationUnit> rootUnits, Set<OrganisationUnit> unitsInTheTree )
    {
        for ( OrganisationUnit root : rootUnits )
        {
            unitsInTheTree.add( root );
            getUnitsInTheTree( root.getChildren(), unitsInTheTree );
        }
    }
    */
}
