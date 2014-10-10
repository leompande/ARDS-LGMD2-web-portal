package org.hisp.dhis.reports.reportmanagement.action;

import org.hisp.dhis.reports.ReportService;
import com.opensymphony.xwork2.Action;

public class DelReportAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    // -------------------------------------------------------------------------
    // Input/Output Getters & setters
    // -------------------------------------------------------------------------

    private Integer reportId;

    public void setReportId( Integer reportId )
    {
        this.reportId = reportId;
    }

    // -------------------------------------------------------------------------
    // ActionSupport implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {

        reportService.deleteReport( reportService.getReport( reportId ) );

        return SUCCESS;
    }

}
