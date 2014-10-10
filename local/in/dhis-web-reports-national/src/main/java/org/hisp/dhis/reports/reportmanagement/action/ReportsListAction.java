package org.hisp.dhis.reports.reportmanagement.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import com.opensymphony.xwork2.Action;

public class ReportsListAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

        private ReportService reportService;
        
        public void setReportService(ReportService reportService) 
        {
                this.reportService = reportService;
        }
        
    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------
        
        private List<Report_in> reportList;
        
        public List<Report_in> getReportList() 
        {
                return reportList;
        }
    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        reportList = new ArrayList<Report_in>( reportService.getAllReports() );

        return SUCCESS;
    }

}

