/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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
package org.hisp.dhis.reportsheet.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class DateUtils
{
    @SuppressWarnings( "deprecation" )
    public static Date getFirstDayOfMonth( Date date )
    {
        Calendar result = Calendar.getInstance();

        Calendar calendar = Calendar.getInstance();
        calendar.set( Calendar.YEAR, date.getYear() + 1900 );
        calendar.set( Calendar.MONTH, date.getMonth() );
        calendar.set( Calendar.DATE, date.getDate() );

        result.set( Calendar.DATE, calendar.getActualMinimum( Calendar.DATE ) );
        result.set( Calendar.MONTH, calendar.get( Calendar.MONTH ) );
        result.set( Calendar.YEAR, calendar.get( Calendar.YEAR ) );

        return result.getTime();
    }

    public static Date getFirstDayOfYear( int year )
    {
        Calendar calendar = Calendar.getInstance();

        calendar.set( year, Calendar.JANUARY, 1 );

        return calendar.getTime();
    }

    public static Date getLastDayOfYear( int year )
    {
        return getEndDate( Calendar.DECEMBER, year );
    }

    public static Date getStartDate( int month, int year )
    {
        Calendar calendar = Calendar.getInstance();

        calendar.set( Calendar.MONTH, month );

        calendar.set( Calendar.YEAR, year );

        calendar.set( Calendar.DATE, calendar.getActualMinimum( Calendar.DATE ) );

        return calendar.getTime();
    }

    public static Date getEndDate( int month, int year )
    {
        Calendar calendar = Calendar.getInstance();

        calendar.set( Calendar.MONTH, month );

        calendar.set( Calendar.YEAR, year );

        calendar.set( Calendar.DATE, calendar.getActualMaximum( Calendar.DATE ) );

        return calendar.getTime();
    }

    public static int getCurrentMonth()
    {
        Calendar calendar = Calendar.getInstance();

        return calendar.get( Calendar.MONTH );
    }

    public static int getCurrentYear()
    {
        Calendar calendar = Calendar.getInstance();

        return calendar.get( Calendar.YEAR );

    }

    @SuppressWarnings( "static-access" )
    public static Date getTimeRoll( Date current, int field, int numberOfRoll )
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( current );

        if ( field == Calendar.DATE )
        {
            if ( calendar.get( Calendar.DATE ) == calendar.getActualMinimum( Calendar.DATE ) )
            {
                if ( calendar.get( Calendar.MONTH ) == calendar.JANUARY )
                {
                    if ( numberOfRoll < 0 )
                    {
                        calendar.roll( Calendar.MONTH, -1 );
                        calendar.roll( Calendar.YEAR, -1 );
                        calendar.roll( field, numberOfRoll );
                    }
                    else
                    {
                        calendar.roll( field, numberOfRoll );
                    }
                }
                else
                {
                    if ( numberOfRoll < 0 )
                    {
                        calendar.roll( Calendar.MONTH, -1 );
                        calendar.roll( field, numberOfRoll );
                    }
                    else
                    {
                        calendar.roll( field, numberOfRoll );
                    }
                }

            }
            else if ( calendar.get( Calendar.DATE ) == calendar.getActualMaximum( Calendar.DATE ) )
            {
                if ( calendar.get( Calendar.MONTH ) == calendar.DECEMBER )
                {
                    if ( numberOfRoll > 0 )
                    {
                        calendar.roll( Calendar.YEAR, +1 );
                        calendar.roll( Calendar.MONTH, +1 );
                        calendar.roll( field, numberOfRoll );
                    }
                    else
                    {
                        calendar.roll( field, numberOfRoll );
                    }
                }
                else
                {
                    if ( numberOfRoll > 0 )
                    {
                        calendar.roll( Calendar.MONTH, +1 );
                        calendar.roll( field, numberOfRoll );
                    }
                    else
                    {
                        calendar.roll( field, numberOfRoll );
                    }
                }
            }
            else
            {

                calendar.roll( field, numberOfRoll );
            }
        }
        else if ( field == Calendar.MONTH )
        {
            if ( (numberOfRoll + calendar.get( Calendar.MONTH ) + 1) <= 0 )
            {
                calendar.roll( Calendar.YEAR, -1 );
            }

            if ( calendar.get( Calendar.MONTH ) == Calendar.JANUARY )
            {
                if ( numberOfRoll < 0 )
                {
                    calendar.roll( field, numberOfRoll );
                    calendar.roll( Calendar.YEAR, -1 );
                }
                else
                {
                    calendar.roll( field, numberOfRoll );
                }
            }
            else if ( calendar.get( Calendar.MONTH ) == Calendar.DECEMBER )
            {
                if ( numberOfRoll > 0 )
                {
                    calendar.roll( field, numberOfRoll );
                    calendar.roll( Calendar.YEAR, 1 );
                }
                else
                {
                    calendar.roll( field, numberOfRoll );
                }
            }
            else
            {
                calendar.roll( field, numberOfRoll );
            }

        }

        return calendar.getTime();
    }

    @SuppressWarnings( "deprecation" )
    public static Date getMonthBefore( Date currentDate, int numberMonthBefore )
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set( Calendar.YEAR, currentDate.getYear() + 1900 );
        calendar.set( Calendar.MONTH, currentDate.getMonth() + 1 - numberMonthBefore );
        calendar.set( Calendar.DATE, calendar.getActualMinimum( Calendar.DATE ) );
        return calendar.getTime();
    }

    @SuppressWarnings( "deprecation" )
    public static Date getStartQuaterly( Date startDate )
    {
        Calendar result = Calendar.getInstance();

        Calendar calendar = Calendar.getInstance();
        calendar.set( Calendar.YEAR, startDate.getYear() + 1900 );
        calendar.set( Calendar.MONTH, startDate.getMonth() );
        calendar.set( Calendar.DATE, startDate.getDate() );

        if ( calendar.get( Calendar.MONTH ) <= Calendar.MARCH )
        {
            result.set( Calendar.DATE, calendar.getActualMinimum( Calendar.DATE ) );
            result.set( Calendar.MONTH, Calendar.JANUARY );
            result.set( Calendar.YEAR, calendar.get( Calendar.YEAR ) );
        }
        else if ( calendar.get( Calendar.MONTH ) <= Calendar.JUNE )
        {
            result.set( Calendar.DATE, calendar.getActualMinimum( Calendar.DATE ) );
            result.set( Calendar.MONTH, Calendar.APRIL );
            result.set( Calendar.YEAR, calendar.get( Calendar.YEAR ) );
        }
        else if ( calendar.get( Calendar.MONTH ) <= Calendar.SEPTEMBER )
        {
            result.set( Calendar.DATE, calendar.getActualMinimum( Calendar.DATE ) );
            result.set( Calendar.MONTH, Calendar.JULY );
            result.set( Calendar.YEAR, calendar.get( Calendar.YEAR ) );
        }
        else
        {
            result.set( Calendar.DATE, calendar.getActualMinimum( Calendar.DATE ) );
            result.set( Calendar.MONTH, Calendar.OCTOBER );
            result.set( Calendar.YEAR, calendar.get( Calendar.YEAR ) );
        }

        return result.getTime();
    }

    @SuppressWarnings( "deprecation" )
    public static Date getEndQuaterly( Date startDate )
    {
        Calendar result = Calendar.getInstance();

        Calendar calendar = Calendar.getInstance();
        calendar.set( Calendar.YEAR, startDate.getYear() + 1900 );
        calendar.set( Calendar.MONTH, startDate.getMonth() );
        calendar.set( Calendar.DATE, startDate.getDate() );

        if ( calendar.get( Calendar.MONTH ) <= Calendar.MARCH )
        {
            result.set( Calendar.MONTH, Calendar.MARCH );
            result.set( Calendar.DATE, result.getActualMaximum( Calendar.DATE ) );
            result.set( Calendar.YEAR, calendar.get( Calendar.YEAR ) );

        }
        else if ( calendar.get( Calendar.MONTH ) <= Calendar.JUNE )
        {
            result.set( Calendar.MONTH, Calendar.JUNE );
            result.set( Calendar.DATE, result.getActualMaximum( Calendar.DATE ) );
            result.set( Calendar.YEAR, calendar.get( Calendar.YEAR ) );

        }
        else if ( calendar.get( Calendar.MONTH ) <= Calendar.SEPTEMBER )
        {
            result.set( Calendar.MONTH, Calendar.SEPTEMBER );
            result.set( Calendar.DATE, result.getActualMaximum( Calendar.DATE ) );
            result.set( Calendar.YEAR, calendar.get( Calendar.YEAR ) );

        }
        else
        {
            result.set( Calendar.MONTH, Calendar.DECEMBER );
            result.set( Calendar.DATE, calendar.getMaximum( Calendar.DATE ) );
            result.set( Calendar.YEAR, calendar.get( Calendar.YEAR ) );
        }

        return result.getTime();
    }

    @SuppressWarnings( "deprecation" )
    public static Date getStartSixMonthly( Date startDate )
    {
        Calendar result = Calendar.getInstance();

        Calendar calendar = Calendar.getInstance();
        calendar.set( Calendar.YEAR, startDate.getYear() + 1900 );
        calendar.set( Calendar.MONTH, startDate.getMonth() );
        calendar.set( Calendar.DATE, startDate.getDate() );

        if ( calendar.get( Calendar.MONTH ) <= Calendar.JUNE )
        {
            result.set( Calendar.DATE, calendar.getActualMinimum( Calendar.DATE ) );
            result.set( Calendar.MONTH, Calendar.JANUARY );
            result.set( Calendar.YEAR, calendar.get( Calendar.YEAR ) );

        }
        else
        {
            result.set( Calendar.DATE, calendar.getActualMinimum( Calendar.DATE ) );
            result.set( Calendar.MONTH, Calendar.JULY );
            result.set( Calendar.YEAR, calendar.get( Calendar.YEAR ) );
        }
        return result.getTime();
    }

    @SuppressWarnings( "deprecation" )
    public static Date getEndSixMonthly( Date startDate )
    {
        Calendar result = Calendar.getInstance();

        Calendar calendar = Calendar.getInstance();
        calendar.set( Calendar.YEAR, startDate.getYear() + 1900 );
        calendar.set( Calendar.MONTH, startDate.getMonth() );
        calendar.set( Calendar.DATE, startDate.getDate() );

        if ( calendar.get( Calendar.MONTH ) <= Calendar.JUNE )
        {
            result.set( Calendar.MONTH, Calendar.JUNE );
            result.set( Calendar.DATE, result.getActualMaximum( Calendar.DATE ) );
            result.set( Calendar.YEAR, calendar.get( Calendar.YEAR ) );
        }
        else
        {
            result.set( Calendar.MONTH, Calendar.DECEMBER );
            result.set( Calendar.DATE, result.getActualMaximum( Calendar.DATE ) );
            result.set( Calendar.YEAR, calendar.get( Calendar.YEAR ) );
        }
        return result.getTime();
    }

}
