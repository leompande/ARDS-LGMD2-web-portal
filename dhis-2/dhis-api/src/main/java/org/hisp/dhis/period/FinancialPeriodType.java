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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Lars Helge Overland
 */
public abstract class FinancialPeriodType
    extends CalendarPeriodType
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 2649990007010207631L;

    public static final int FREQUENCY_ORDER = 365;
    
    // -------------------------------------------------------------------------
    // Abstract methods
    // -------------------------------------------------------------------------

    protected abstract int getBaseMonth();
    
    // -------------------------------------------------------------------------
    // PeriodType functionality
    // -------------------------------------------------------------------------

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
        boolean past = cal.get( Calendar.MONTH ) >= getBaseMonth();
        
        cal.set( Calendar.YEAR, past ? cal.get( Calendar.YEAR ) : cal.get( Calendar.YEAR ) - 1 );
        cal.set( Calendar.MONTH, getBaseMonth() );
        cal.set( Calendar.DATE, 1 );

        Date startDate = cal.getTime();

        cal.add( Calendar.YEAR, 1 );
        cal.set( Calendar.DAY_OF_YEAR, cal.get( Calendar.DAY_OF_YEAR ) - 1  );

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
        cal.add( Calendar.YEAR, 1 );
        return createPeriod( cal );
    }

    @Override
    public Period getPreviousPeriod( Period period )
    {
        Calendar cal = createCalendarInstance( period.getStartDate() );
        cal.add( Calendar.YEAR, -1 );
        return createPeriod( cal );
    }

    /**
     * Generates financial yearly periods for the last 5, current and next 5 
     * financial years.
     */
    @Override
    public List<Period> generatePeriods( Date date )
    {
        Calendar cal = createCalendarInstance( date );
        
        boolean past = cal.get( Calendar.MONTH ) >= getBaseMonth();
        
        cal.add( Calendar.YEAR, past ? -5 : -6 );
        cal.set( Calendar.MONTH, getBaseMonth() );
        cal.set( Calendar.DATE, 1 );

        ArrayList<Period> periods = new ArrayList<Period>();

        for ( int i = 0; i < 11; ++i )
        {
            periods.add( createPeriod( cal ) );
            cal.add( Calendar.YEAR, 1 );
        }

        return periods;
    }

    /**
     * Generates the last 5 financial years where the last one is the financial
     * year which the given date is inside.
     */
    @Override
    public List<Period> generateRollingPeriods( Date date )
    {
        return generateLast5Years( date );
    }
    
    @Override
    public List<Period> generateLast5Years( Date date )
    {
        Calendar cal = createCalendarInstance( date );
        
        boolean past = cal.get( Calendar.MONTH ) >= getBaseMonth();
        
        cal.add( Calendar.YEAR, past ? -4 : -5 );
        cal.set( Calendar.MONTH, getBaseMonth() );
        cal.set( Calendar.DATE, 1 );

        ArrayList<Period> periods = new ArrayList<Period>();

        for ( int i = 0; i < 5; ++i )
        {
            periods.add( createPeriod( cal ) );
            cal.add( Calendar.YEAR, 1 );
        }

        return periods;
    }

    @Override
    public Period createPeriod( String isoDate )
    {
        int year = Integer.parseInt( isoDate.substring( 0, 4 ) );
        Calendar cal = createCalendarInstance();
        cal.set( Calendar.YEAR, year );
        cal.set( Calendar.MONTH, 11 );
        cal.set( Calendar.DAY_OF_MONTH, 31 );
        return createPeriod( cal );
    }
    
    @Override
    public Date getRewindedDate( Date date, Integer rewindedPeriods )
    {
        date = date != null ? date : new Date();        
        rewindedPeriods = rewindedPeriods != null ? rewindedPeriods : 1;

        Calendar cal = createCalendarInstance( date );        
        cal.add( Calendar.YEAR, (rewindedPeriods * -1) );

        return cal.getTime();
    }
}
