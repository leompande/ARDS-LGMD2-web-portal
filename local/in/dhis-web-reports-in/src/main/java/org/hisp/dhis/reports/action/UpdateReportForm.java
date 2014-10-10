package org.hisp.dhis.reports.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportModel;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.ReportType;
import org.hisp.dhis.reports.Report_in;

import com.opensymphony.xwork2.Action;

public class UpdateReportForm
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    private List<PeriodType> periodTypes;

    public List<PeriodType> getPeriodTypes()
    {
        return periodTypes;
    }

    private List<String> reportTypes;

    public List<String> getReportTypes()
    {
        return reportTypes;
    }

    private List<String> reportModels;

    public List<String> getReportModels()
    {
        return reportModels;
    }

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

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        report = reportService.getReport( reportId );

        periodTypes = new ArrayList<PeriodType>( periodService.getAllPeriodTypes() );

        reportTypes = ReportType.getReportTypes();

        reportModels = ReportModel.getReportModels();

        return SUCCESS;
    }

}
