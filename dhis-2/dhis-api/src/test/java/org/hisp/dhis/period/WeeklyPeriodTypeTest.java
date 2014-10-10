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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Bob Jolliffe
 * @version $Id$
 */
public class WeeklyPeriodTypeTest
{
    @Test
    @Ignore
    public void testGeneratePeriods()
    {
        Calendar testCal = Calendar.getInstance();
        WeeklyPeriodType wpt = new WeeklyPeriodType();
        
        for ( int year = 1990; year < 2020; year++ )
        {
            for ( int day = -7; day < 7; day++ )
            {
                testCal.set( year, 0, 1 ); // 1st day of year
                testCal.add( Calendar.DATE, day );
                Period p1 = wpt.createPeriod( testCal.getTime() );
                List<Period> generatedPeriods = wpt.generatePeriods( p1 );
                assertTrue( "Period " + p1 + " in generated set", generatedPeriods.contains( p1 ) );
            }
        }
    }

    @Test
    public void testCreatePeriod()
    {
        Calendar testCal = Calendar.getInstance();
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();

        // arbitrary instance - should increase coverage
        testCal.set( 2009, 4, 27 ); // Wednesday
        WeeklyPeriodType wpt = new WeeklyPeriodType();

        Period p = wpt.createPeriod( testCal.getTime() );
        startCal.setTime( p.getStartDate() );
        endCal.setTime( p.getEndDate() );

        assertFalse( "start date after given date", startCal.after( testCal ) );
        assertFalse( "end date before given date", endCal.before( testCal ) );

        assertTrue( startCal.get( Calendar.DAY_OF_WEEK ) == Calendar.MONDAY );
        assertTrue( endCal.get( Calendar.DAY_OF_WEEK ) == Calendar.SUNDAY );

    }

    @Test
    public void isoDates()
    {
        WeeklyPeriodType weekly = new WeeklyPeriodType();
        Calendar cal = Calendar.getInstance();

        cal.clear();

        cal.set( 2008, 11, 29 );
        Period period = weekly.createPeriod( "2009W1" );
        assertEquals( cal.getTime(), period.getStartDate() );

        cal.set( 2011, 0, 3 );
        period = weekly.createPeriod( "2011W1" );
        assertEquals( cal.getTime(), period.getStartDate() );

        period = weekly.createPeriod( "2011W11" );
        cal.set( 2011, 2, 14 );
        assertEquals( cal.getTime(), period.getStartDate() );
    }

    @Test
    public void getIsoDate()
    {
        Calendar cal = Calendar.getInstance();
        cal.set( 2011, 0, 3 ); // Wednesday
        WeeklyPeriodType wpt = new WeeklyPeriodType();
        Period p = wpt.createPeriod( cal.getTime() );
        assertEquals( "2011W1", p.getIsoDate());

        cal.set( 2012, 11, 31 ); // Monday
        p = wpt.createPeriod( cal.getTime() );
        assertEquals( "2013W1", p.getIsoDate());
    }
    
    @Test
    public void testGetPeriodsBetween()
    {
        PeriodType periodType = new WeeklyPeriodType();

        assertEquals( 1, periodType.createPeriod().getPeriodSpan( periodType ) );
        assertEquals( 4, new MonthlyPeriodType().createPeriod().getPeriodSpan( periodType ) );
        assertEquals( 8, new BiMonthlyPeriodType().createPeriod().getPeriodSpan( periodType ) );
        assertEquals( 13, new QuarterlyPeriodType().createPeriod().getPeriodSpan( periodType ) );
        assertEquals( 26, new SixMonthlyPeriodType().createPeriod().getPeriodSpan( periodType ) );
        assertEquals( 52, new YearlyPeriodType().createPeriod().getPeriodSpan( periodType ) );
    }
}
