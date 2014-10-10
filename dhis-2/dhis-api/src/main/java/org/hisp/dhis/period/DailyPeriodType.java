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
 * PeriodType for daily Periods. A valid daily Period has equal startDate and
 * endDate.
 * 
 * @author Torgeir Lorange Ostby
 * @version $Id: DailyPeriodType.java 2971 2007-03-03 18:54:56Z torgeilo $
 */
public class DailyPeriodType
    extends CalendarPeriodType
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 5371766471215556241L;

    public static final String ISO_FORMAT = "yyyyMMdd";

    /**
     * The name of the DailyPeriodType, which is "Daily".
     */
    public static final String NAME = "Daily";

    public static final int FREQUENCY_ORDER = 1;

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
        Date date = cal.getTime();

        return new Period( this, date, date );
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
        cal.add( Calendar.DAY_OF_YEAR, 1 );

        Date date = cal.getTime();

        return new Period( this, date, date );
    }

    @Override
    public Period getPreviousPeriod( Period period )
    {
        Calendar cal = createCalendarInstance( period.getStartDate() );
        cal.add( Calendar.DAY_OF_YEAR, -1 );

        Date date = cal.getTime();

        return new Period( this, date, date );
    }
    
    /**
     * Generates daily Periods for the whole year in which the given Period's
     * startDate exists.
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
            periods.add( createPeriod( cal.getTime() ) );
            cal.add( Calendar.DAY_OF_YEAR, 1 );
        }

        return periods;
    }

    /**
     * Generates the last 365 days where the last one is the day of the given 
     * date.
     */
    @Override
    public List<Period> generateRollingPeriods( Date date )
    {
        Calendar cal = createCalendarInstance( date );
        cal.set( Calendar.DAY_OF_MONTH, -364 );        

        ArrayList<Period> periods = new ArrayList<Period>();
        
        for ( int i = 0; i < 365; i++ )
        {
            periods.add( createPeriod( cal ) );
            cal.add( Calendar.MONTH, 1 );
        }
        
        return periods;        
    }

    @Override
    public String getIsoDate( Period period )
    {
        return new SimpleDateFormat( ISO_FORMAT ).format( period.getStartDate() );
    }

    @Override
    public Period createPeriod( String isoDate )
    {
        try
        {
            Date date = new SimpleDateFormat( ISO_FORMAT ).parse( isoDate );
            return createPeriod( date );
        }
        catch ( ParseException e )
        {
            throw new RuntimeException( e );
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
        cal.add( Calendar.DAY_OF_YEAR, (rewindedPeriods * -1) );

        return cal.getTime();
    }
}
