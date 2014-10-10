package org.hisp.dhis.reports.csreview.action;

import java.util.ArrayList;
import java.util.List;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.ReportType;
import org.hisp.dhis.reports.Report_in;

import com.opensymphony.xwork2.Action;

public class GenerateCSReviewReportFormAction
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
    // Input & output
    // -------------------------------------------------------------------------

    private List<Report_in> reportList;

    public List<Report_in> getReportList()
    {
        return reportList;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        reportList = new ArrayList<Report_in>( reportService.getReportsByReportType( ReportType.RT_CSREVIEW ) );
        
        return SUCCESS;
    }
}
