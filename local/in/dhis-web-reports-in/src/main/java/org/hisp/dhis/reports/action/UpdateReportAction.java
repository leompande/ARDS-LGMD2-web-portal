package org.hisp.dhis.reports.action;

import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;

import com.opensymphony.xwork2.Action;

public class UpdateReportAction
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

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String reportmodel;

    public void setReportmodel( String reportmodel )
    {
        this.reportmodel = reportmodel;
    }

    private String excelname;

    public void setExcelname( String excelname )
    {
        this.excelname = excelname;
    }

    private String xmlname;

    public void setXmlname( String xmlname )
    {
        this.xmlname = xmlname;
    }

    private String reporttype;

    public void setReporttype( String reporttype )
    {
        this.reporttype = reporttype;
    }

    private String frequencySelect;

    public void setFrequencySelect( String frequencySelect )
    {
        this.frequencySelect = frequencySelect;
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

        PeriodType periodType = periodService.getPeriodTypeByName( frequencySelect );

        Report_in report = reportService.getReport( reportId );

        report.setName( name );
        report.setModel( reportmodel );
        report.setPeriodType( periodType );
        report.setReportType( reporttype );
        report.setExcelTemplateName( excelname );
        report.setXmlTemplateName( xmlname );

        reportService.updateReport( report );

        return SUCCESS;
    }
}
