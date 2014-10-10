package org.hisp.dhis.reports.action;

import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;

import com.opensymphony.xwork2.Action;

public class ValidateReportAction
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
    // Input & Output
    // -------------------------------------------------------------------------

    private String message;

    public String getMessage()
    {
        return message;
    }

    private Integer reportId;

    public void setReportId( Integer reportId )
    {
        this.reportId = reportId;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    public String execute()
        throws Exception
    {
        // ---------------------------------------------------------------------
        // Name Validation
        // ---------------------------------------------------------------------

        if ( name == null )
        {
            message = "Please Specify Name";

            return INPUT;
        }
        else
        {
            name = name.trim();

            if ( name.length() == 0 )
            {
                message = "Please Specify Name";

                return INPUT;
            }

            Report_in match = reportService.getReportByName( name );

            if ( match != null && (reportId == null || match.getId() != reportId) )
            {
                message = "Name Already Exists, Please Specify Another Name";

                return INPUT;
            }
        }

        return SUCCESS;
    }
}
