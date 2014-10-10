package org.hisp.dhis.period;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Lars Helge Overland
 */
public class BiMonthlyPeriodType
    extends CalendarPeriodType
{
    private static final String ISO_FORMAT = "yyyyMMB";

    /**
     * The name of the BiMonthlyPeriodType, which is "BiMonthly".
     */
    public static final String NAME = "BiMonthly";

    public static final int FREQUENCY_ORDER = 61;

    // -------------------------------------------------------------------------
    // PeriodType functionality
    // -------------------------------------------------------------------------

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public Period createPeriod()
    {
        return createPeriod( createCalendarInstance() );
    }

    @Override
    public Period createPeriod( Date date )
    {
        return createPeriod( createCalendarInstance( date ) );
    }

    @Override
    public Period createPeriod( Calendar cal )
    {
        cal.set( Calendar.MONTH, cal.get( Calendar.MONTH ) - cal.get( Calendar.MONTH ) % 2 );
        cal.set( Calendar.DAY_OF_MONTH, 1 );

        Date startDate = cal.getTime();

        cal.add( Calendar.MONTH, 1 );
        cal.set( Calendar.DAY_OF_MONTH, cal.getActualMaximum( Calendar.DAY_OF_MONTH ) );

        return new Period( this, startDate, cal.getTime() );
    }

    @Override
    public int getFrequencyOrder()
    {
        return FREQUENCY_ORDER;
    }

    // -------------------------------------------------------------------------
    // CalendarPeriodType functionality
    // -------------------------------------------------------------------------

    @Override
    public Period getNextPeriod( Period period )
    {
        Calendar cal = createCalendarInstance( period.getStartDate() );
        cal.add( Calendar.MONTH, 2 );
        return createPeriod( cal );
    }

    @Override
    public Period getPreviousPeriod( Period period )
    {
        Calendar cal = createCalendarInstance( period.getStartDate() );
        cal.add( Calendar.MONTH, -2 );
        return createPeriod( cal );
    }

    /**
     * Generates bimonthly Periods for the whole year in which the start date of
     * the given Period exists.
     */
    @Override
    public List<Period> generatePeriods( Date date )
    {
        Calendar cal = createCalendarInstance( date );
        cal.set( Calendar.DAY_OF_YEAR, 1 );

        int year = cal.get( Calendar.YEAR );
        ArrayList<Period> periods = new ArrayList<Period>();

        while ( cal.get( Calendar.YEAR ) == year )
        {
            periods.add( createPeriod( cal ) );
            cal.add( Calendar.MONTH, 2 );
        }

        return periods;
    }

    /**
     * Generates the last 6 bi-months where the last one is the bi-month
     * which the given date is inside.
     */
    @Override
    public List<Period> generateRollingPeriods( Date date )
    {
        Calendar cal = createCalendarInstance( date );
        cal.set( Calendar.DAY_OF_MONTH, 1 );
        cal.add( Calendar.MONTH, ( ( cal.get( Calendar.MONTH ) % 2 ) * -1 ) - 10 );        

        ArrayList<Period> periods = new ArrayList<Period>();
        
        for ( int i = 0; i < 6; i++ )
        {
            periods.add( createPeriod( cal ) );
            cal.add( Calendar.MONTH, 2 );
        }
        
        return periods;
    }

    @Override
    public String getIsoDate( Period period )
    {        
        return new SimpleDateFormat( "yyyyMM" ).format( period.getStartDate() ) + "B";
    }

    @Override
    public Period createPeriod( String isoDate )
    {
        try
        {
            Date date = new SimpleDateFormat( "yyyyMM" ).parse( isoDate.substring( 0, 6 ) );
            return createPeriod( date );
        }
        catch ( ParseException ex )
        {
            throw new RuntimeException( ex );
        }
    }

    @Override
    public String getIsoFormat()
    {
        return ISO_FORMAT;
    }
    
    @Override
    public Date getRewindedDate( Date date, Integer rewindedPeriods )
    {
        date = date != null ? date : new Date();        
        rewindedPeriods = rewindedPeriods != null ? rewindedPeriods : 1;

        Calendar cal = createCalendarInstance( date );        
        cal.add( Calendar.MONTH, (rewindedPeriods * -2) );

        return cal.getTime();
    }
}
