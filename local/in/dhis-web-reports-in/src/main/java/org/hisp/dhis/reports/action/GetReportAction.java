package org.hisp.dhis.reports.action;

import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;

import com.opensymphony.xwork2.Action;

public class GetReportAction
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
    // Getters & Setters
    // -------------------------------------------------------------------------

    private Report_in report;

    public Report_in getReport()
    {
        return report;
    }

    private Integer reportId;

    public void setReportId( Integer reportId )
    {
        this.reportId = reportId;
    }

    public Integer getReportId()
    {
        return reportId;
    }

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        report = reportService.getReport( reportId );

        return SUCCESS;
    }
}
