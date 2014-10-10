package org.hisp.dhis.dataanalyser.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.YearlyPeriodType;
import org.hisp.dhis.period.comparator.PeriodComparator;

import com.opensymphony.xwork2.Action;

public class GetFinacialYearAction implements Action
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

    private List<String> finacialYearList;
    
    public List<String> getFinacialYearList()
    {
        return finacialYearList;
    }

    private List<Period> periods;
    
    public List<Period> getPeriods()
    {
        return periods;
    }
   
    
    private List<Period> yearlyPeriods;

    public List<Period> getYearlyPeriods()
    {
        return yearlyPeriods;
    }
    
    private SimpleDateFormat simpleDateFormat;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------


    public String execute() throws Exception
    {  
       
        finacialYearList = new ArrayList<String>();
        
        yearlyPeriods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( new YearlyPeriodType() ) );
        Iterator<Period> periodIterator = yearlyPeriods.iterator();
        while( periodIterator.hasNext() )
        {
            Period p1 = periodIterator.next();
            
            if ( p1.getStartDate().compareTo( new Date() ) > 0 )
            {
                periodIterator.remove( );
            }
            
        }
        
        Collections.sort( yearlyPeriods, new PeriodComparator() );
        simpleDateFormat = new SimpleDateFormat( "yyyy" );
        for( Period p1 : yearlyPeriods )
        {
            int tempYear = Integer.parseInt( simpleDateFormat.format( p1.getStartDate() ) );
            
            //System.out.println( "Financial Year Start  : " + tempYear );
           // System.out.println( "Financial Year End  : " + (tempYear + 1) );
            finacialYearList.add( tempYear + "-" + (tempYear + 1)  );
        }
        
        for( String year : finacialYearList )
        {
            System.out.println( "Financial Year is  : " + year );
        }
        
        return SUCCESS;   
    }
   
}
