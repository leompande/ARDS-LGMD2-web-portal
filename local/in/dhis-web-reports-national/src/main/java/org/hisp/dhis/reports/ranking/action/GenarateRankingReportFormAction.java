package org.hisp.dhis.reports.ranking.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

public class GenarateRankingReportFormAction implements Action
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
    // Input & Output
    // -------------------------------------------------------------------------
        
    private List<Period> monthlyPeriods;
    
    public List<Period> getMonthlyPeriods()
    {
        return monthlyPeriods;
    }
    
    private SimpleDateFormat simpleDateFormat;
    
    public SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
    }
        
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {        
        PeriodType monthlyPeriodType = new MonthlyPeriodType();
        
        simpleDateFormat = new SimpleDateFormat("MMM-yyyy");
        
        monthlyPeriods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( monthlyPeriodType ) );
       
        return SUCCESS;
    }

}
