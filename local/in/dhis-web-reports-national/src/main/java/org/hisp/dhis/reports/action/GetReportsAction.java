package org.hisp.dhis.reports.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.reports.comparator.Report_inNameComparator;

import com.opensymphony.xwork2.Action;

public class GetReportsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

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

    private String periodType;

    public void setPeriodType( String periodType )
    {
        this.periodType = periodType;
    }

    private String ouId;

    public void setOuId( String ouId )
    {
        this.ouId = ouId;
    }

    private String reportType;
    
    public void setReportType( String reportType )
    {
        this.reportType = reportType;
    }

    private List<Report_in> reportList;

    public List<Report_in> getReportList()
    {
        return reportList;
    }

    private String ouName;

    public String getOuName()
    {
        return ouName;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        if ( ouId != null )
        {
            try
            {
                OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( ouId ) );

                ouName = orgUnit.getShortName();

                PeriodType periodTypeObj = periodService.getPeriodTypeByName( periodType );
                
                reportList = new ArrayList<Report_in>( reportService.getReportsByPeriodSourceAndReportType(
                    periodTypeObj, orgUnit, reportType ) );
                
                Collections.sort( reportList, new Report_inNameComparator() );
            }
            catch ( Exception e )
            {
                System.out.println( "Exception while getting Reports List : " + e.getMessage() );
            }
        }

        return SUCCESS;
    }
}
