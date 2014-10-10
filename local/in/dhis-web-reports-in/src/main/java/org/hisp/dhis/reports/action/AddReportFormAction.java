package org.hisp.dhis.reports.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportModel;
import org.hisp.dhis.reports.ReportType;

import com.opensymphony.xwork2.Action;

public class AddReportFormAction
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

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        periodTypes = new ArrayList<PeriodType>( periodService.getAllPeriodTypes() );

        reportTypes = ReportType.getReportTypes();

        reportModels = ReportModel.getReportModels();

        return SUCCESS;
    }

}
