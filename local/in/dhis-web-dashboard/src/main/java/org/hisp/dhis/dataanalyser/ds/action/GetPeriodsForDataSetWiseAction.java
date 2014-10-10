package org.hisp.dhis.dataanalyser.ds.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.comparator.PeriodComparator;

import com.opensymphony.xwork2.Action;

public class GetPeriodsForDataSetWiseAction implements Action
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

    private String id;

    public void setId( String id )
    {
        this.id = id;
    }

    private List<Period> periods;

    public List<Period> getPeriods()
    {
        return periods;
    }

    private List<String> periodNameList;

    public List<String> getPeriodNameList()
    {
        return periodNameList;
    }

    private SimpleDateFormat simpleDateFormat1;

    private SimpleDateFormat simpleDateFormat2;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        periodNameList = new ArrayList<String>();
        System.out.println( "Period Type Id is : " + id );
        PeriodType periodType = periodService.getPeriodTypeByName( id );

        periods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( periodType ) );
        
        Iterator<Period> periodIterator = periods.iterator();
        while( periodIterator.hasNext() )
        {
            Period p1 = periodIterator.next();
            
            if ( p1.getStartDate().compareTo( new Date() ) > 0 )
            {
                periodIterator.remove( );
            }
            
        }
        Collections.sort( periods, new PeriodComparator() );

        if ( periodType.getName().equalsIgnoreCase( "monthly" ) )
        {
            simpleDateFormat1 = new SimpleDateFormat( "MMM-yyyy" );
            for ( Period p1 : periods )
            {
                periodNameList.add( simpleDateFormat1.format( p1.getStartDate() ) );
            }
        }
        else if ( periodType.getName().equalsIgnoreCase( "quarterly" ) )
        {
            simpleDateFormat1 = new SimpleDateFormat( "MMM" );
            simpleDateFormat2 = new SimpleDateFormat( "MMM yyyy" );

            for ( Period p1 : periods )
            {
                String tempPeriodName = simpleDateFormat1.format( p1.getStartDate() ) + " - "
                    + simpleDateFormat2.format( p1.getEndDate() );
                periodNameList.add( tempPeriodName );
            }
        }
        else if ( periodType.getName().equalsIgnoreCase( "yearly" ) )
        {
            simpleDateFormat1 = new SimpleDateFormat( "yyyy" );
           
            for ( Period p1 : periods )
            {
                //year = Integer.parseInt( simpleDateFormat1.format( p1.getStartDate() ) ) + 1;
                //periodNameList.add( simpleDateFormat1.format( p1.getStartDate() ) + "-" + year );
                periodNameList.add( simpleDateFormat1.format( p1.getStartDate() ) );
            }
        }
        else if ( periodType.getName().equalsIgnoreCase( "daily" ) )
        {
            simpleDateFormat1 = new SimpleDateFormat( "yyyy-MM-dd" );
            
            for ( Period p1 : periods )
            {
                periodNameList.add( simpleDateFormat1.format( p1.getStartDate() ) );
            }
        }
        else
        {
            simpleDateFormat1 = new SimpleDateFormat( "yyyy-MM-dd" );
            for ( Period p1 : periods )
            {
                String tempPeriodName = simpleDateFormat1.format( p1.getStartDate() ) + " - "
                    + simpleDateFormat1.format( p1.getEndDate() );
                periodNameList.add( tempPeriodName );
            }
        }
  
        return SUCCESS;
    }

}