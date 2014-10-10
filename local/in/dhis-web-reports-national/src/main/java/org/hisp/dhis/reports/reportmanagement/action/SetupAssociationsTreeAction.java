package org.hisp.dhis.reports.reportmanagement.action;

import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;

import com.opensymphony.xwork2.Action;

public class SetupAssociationsTreeAction
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

    public int getReportId()
    {
        return reportId;
    }

    public void setReportId( int reportId )
    {
        this.reportId = reportId;
    }

    private Report_in report;

    public Report_in getReport()
    {
        return report;
    }

    // -------------------------------------------------------------------------
    // ActionSupport implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        report = reportService.getReport( reportId );

        selectionTreeManager.setSelectedOrganisationUnits( report.getSources() );

        return SUCCESS;
    }
}

