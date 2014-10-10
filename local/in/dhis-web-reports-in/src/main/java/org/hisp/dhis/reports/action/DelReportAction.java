package org.hisp.dhis.reports.action;

import org.hisp.dhis.reports.ReportService;

import com.opensymphony.xwork2.Action;

public class DelReportAction implements Action 
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
    // Getters & setters
    // -------------------------------------------------------------------------

	private Integer reportId;
	
	public void setReportId(Integer reportId) 
	{
		this.reportId = reportId;
	}
	
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------
	 public String execute() throws Exception
	 {
     
		 reportService.deleteReport( reportService.getReport( reportId ) );

		 return SUCCESS;
	 }

}
