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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Lars Helge Overland
 */
public class Cal
{
    private Calendar calendar;
    
    public Cal()
    {
        calendar = new GregorianCalendar();
        calendar.clear();
    }

    /**
    * @param year the year starting at AD 1.
    * @param month the month starting at 1.
    * @param day the day of the month starting at 1.
    */
    public Cal( int year, int month, int day )
    {
        calendar = new GregorianCalendar();
        calendar.clear();
        set( year, month, day );
    }
    
    /**
     * @param date the date.
     */
    public Cal( Date date )
    {
        calendar = new GregorianCalendar();
        calendar.clear();
        calendar.setTime( date );
    }

    /**
     * Sets the time of the calendar to now.
     */
    public Cal now()
    {
        calendar.setTime( new Date() );
        return this;
    }
        
    /**
     * Adds the given amount of time to the given calendar field.
     * 
     * @param field the calendar field.
     * @param value the amount of time.
     */
    public Cal add( int field, int amount )
    {
        calendar.add( field, amount );
        return this;
    }

    /**
     * Subtracts the given amount of time to the given calendar field.
     * 
     * @param field the calendar field.
     * @param value the amount of time.
     */
    public Cal subtract( int field, int amount )
    {
        calendar.add( field, amount * -1 );
        return this;
    }

    /**
     * Returns the value of the given calendar field.
     * 
     * @param field the field.
     */
    public int get( int field )
    {
        return calendar.get( field );
    }
    
    /**
     * Returns the current year.
     * @return
     */
    public int getYear()
    {
        return calendar.get( Calendar.YEAR );
    }
    
    /**
     * Sets the current time.
     * 
     * @param year the year starting at AD 1.
     * @param month the month starting at 1.
     * @param day the day of the month starting at 1.
     */
    public Cal set( int year, int month, int day )
    {
        calendar.set( year, month - 1, day );
        return this;
    }

    /**
     * Sets the current month and day.
     * 
     * @param month the month starting at 1.
     * @param day the day of the month starting at 1.
     */
    public Cal set( int month, int day )
    {
        calendar.set( Calendar.MONTH, month - 1 );
        calendar.set( Calendar.DAY_OF_MONTH, day );
        return this;
    }    
    
    /**
     * Sets the current time.
     * 
     * @param date the date to base time on.
     */
    public Cal set( Date date )
    {
        calendar.setTime( date );
        return this;
    }
    
    /**
     * Returns the current date the cal.
     */
    public Date time()
    {
        return calendar.getTime();
    }
}
