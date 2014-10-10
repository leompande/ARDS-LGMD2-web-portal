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

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.common.DxfNamespaces;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The superclass of all PeriodTypes.
 *
 * @author Kristian Nordal
 */
@JacksonXmlRootElement( localName = "periodType", namespace = DxfNamespaces.DXF_2_0)
public abstract class PeriodType
    implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 2402122626196305083L;

    // -------------------------------------------------------------------------
    // Available PeriodTypes
    // -------------------------------------------------------------------------

    /**
     * All period types in descending order according to frequency.
     */
    public static final List<PeriodType> PERIOD_TYPES = new ArrayList<PeriodType>()
    {
        {
            add( new DailyPeriodType() );
            add( new WeeklyPeriodType() );
            add( new MonthlyPeriodType() );
            add( new BiMonthlyPeriodType() );
            add( new QuarterlyPeriodType() );
            add( new SixMonthlyPeriodType() );
            add( new YearlyPeriodType() );
            add( new FinancialAprilPeriodType() );
            add( new FinancialJulyPeriodType() );
            add( new FinancialOctoberPeriodType() );
        }
    };

    private static final Map<String, PeriodType> PERIOD_TYPE_MAP = new HashMap<String, PeriodType>()
    {
        {
            for ( PeriodType periodType : PERIOD_TYPES )
            {
                put( periodType.getName(), periodType );
            }
        }
    };

    /**
     * Returns an immutable list of all available PeriodTypes in their natural order.
     *
     * @return all available PeriodTypes in their natural order.
     */
    public static List<PeriodType> getAvailablePeriodTypes()
    {
        return new ArrayList<PeriodType>( PERIOD_TYPES );
    }

    /**
     * Returns a PeriodType with a given name.
     *
     * @param name the name of the PeriodType to return.
     * @return the PeriodType with the given name or null if no such PeriodType
     *         exists.
     */
    public static PeriodType getPeriodTypeByName( String name )
    {
        return PERIOD_TYPE_MAP.get( name );
    }

    public static PeriodType getByNameIgnoreCase( String name )
    {
        for ( PeriodType periodType : getAvailablePeriodTypes() )
        {
            if ( name != null && periodType.getName().toLowerCase().trim().equals( name.toLowerCase().trim() ) )
            {
                return periodType;
            }
        }

        return null;
    }

    /**
     * Get period type according to natural order order.
     *
     * @param index the index of the period type with base 1
     * @return period type according to index order or null if no match
     *         TODO: Consider manual ordering, since relying on natural order might create problems if new periods are introduced.
     */
    public static PeriodType getByIndex( int index )
    {
        index -= 1;

        if ( index < 0 || index > PERIOD_TYPES.size() - 1 )
        {
            return null;
        }

        return PERIOD_TYPES.get( index );
    }

    // -------------------------------------------------------------------------
    // Persistable
    // -------------------------------------------------------------------------

    private int id;

    public void setId( int id )
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    // -------------------------------------------------------------------------
    // PeriodType functionality
    // -------------------------------------------------------------------------

    /**
     * Returns a unique name for the PeriodType.
     *
     * @return a unique name for the PeriodType. E.g. "Monthly".
     */
    public abstract String getName();

    /**
     * Creates a valid Period based on the current date. E.g. if today is
     * January 5. 2007, a monthly PeriodType should return January 2007.
     *
     * @return a valid Period based on the current date.
     */
    public abstract Period createPeriod();

    /**
     * Creates a valid Period based on the given date. E.g. the given date is
     * February 10. 2007, a monthly PeriodType should return February 2007.
     *
     * @param date the date which is contained by the created period.
     * @return the valid Period based on the given date
     */
    public abstract Period createPeriod( Date date );

    public abstract Period createPeriod( Calendar cal );

    /**
     * Returns a comparable value for the frequency length of this PeriodType.
     * Shortest is 0.
     *
     * @return the frequency order.
     */
    public abstract int getFrequencyOrder();

    /**
     * Returns a new date rewinded from now.
     *
     * @return the Date.
     */    
    public abstract Date getRewindedDate( Date date, Integer rewindedPeriods );

    // -------------------------------------------------------------------------
    // Calendar support
    // -------------------------------------------------------------------------

    /**
     * Returns an instance of a Calendar without any time of day, with the
     * current date.
     *
     * @return an instance of a Calendar without any time of day.
     */
    public static final Calendar createCalendarInstance()
    {
        Calendar calendar = new GregorianCalendar();

        clearTimeOfDay( calendar );

        return calendar;
    }

    /**
     * Returns an instance of a Calendar without any time of day, with the given
     * date.
     *
     * @param date the date of the Calendar.
     * @return an instance of a Calendar without any time of day.
     */
    public static final Calendar createCalendarInstance( Date date )
    {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime( date );

        clearTimeOfDay( calendar );

        return calendar;
    }

    /**
     * Clears the time of day in a Calendar instance.
     *
     * @param calendar the Calendar to fix.
     */
    public static final void clearTimeOfDay( Calendar calendar )
    {
        calendar.set( Calendar.MILLISECOND, 0 );
        calendar.set( Calendar.SECOND, 0 );
        calendar.set( Calendar.MINUTE, 0 );
        calendar.set( Calendar.HOUR_OF_DAY, 0 );
    }

    /**
     * Parses a date from a String on the format YYYY-MM-DD.
     * 
     * @param dateString the String to parse.
     * @return a Date based on the given String.
     */
    public static Date getMediumDate( String dateString )
    {
        try
        {
            final SimpleDateFormat format = new SimpleDateFormat();
            format.applyPattern( "yyyy-MM-dd" );
            return dateString != null ? format.parse( dateString ) : null;
        }
        catch ( ParseException ex )
        {
            throw new RuntimeException( "Failed to parse medium date", ex );
        }
    }

    /**
     * Returns a PeriodType corresponding to the provided string The test is
     * quite rudimentary, testing for string format rather than invalid periods.
     * Currently only recognizes the basic subset of common period types.
     *
     * @param isoPeriod String formatted period (2011, 201101, 2011W34, 2011Q1
     *                  etc
     * @return the PeriodType or null if unrecognized
     */
    public static PeriodType getPeriodTypeFromIsoString( String isoPeriod )
    {
        if ( isoPeriod.matches( "\\b\\d{4}\\b" ) )
        {
            return new YearlyPeriodType();
        }
        if ( isoPeriod.matches( "\\b\\d{4}[-]?\\d{2}\\b" ) )
        {
            return new MonthlyPeriodType();
        }
        if ( isoPeriod.matches( "\\b\\d{4}W\\d[\\d]?\\b" ) )
        {
            return new WeeklyPeriodType();
        }
        if ( isoPeriod.matches( "\\b\\d{8}\\b" ) )
        {
            return new DailyPeriodType();
        }
        if ( isoPeriod.matches( "\\b\\d{4}Q\\d\\b" ) )
        {
            return new QuarterlyPeriodType();
        }
        if ( isoPeriod.matches( "\\b\\d{4}S\\d\\b" ) )
        {
            return new SixMonthlyPeriodType();
        }
        if ( isoPeriod.matches( "\\b\\d{6}B\\b" ) )
        {
            return new BiMonthlyPeriodType();
        }
        if ( isoPeriod.matches( "\\b\\d{4}April\\b" ) )
        {
            return new FinancialAprilPeriodType();
        }
        if ( isoPeriod.matches( "\\b\\d{4}July\\b" ) )
        {
            return new FinancialJulyPeriodType();
        }
        if ( isoPeriod.matches( "\\b\\d{4}Oct\\b" ) )
        {
            return new FinancialOctoberPeriodType();
        }

        return null;
    }
    
    /**
     * Returns a period type based on the given date string in ISO format. Returns
     * null if the date string cannot be parsed to a date.
     * 
     * @param isoPeriod the date string in ISO format.
     * @return a period.
     */
    public static Period getPeriodFromIsoString( String isoPeriod )
    {
        if ( isoPeriod != null )
        {
            PeriodType periodType = getPeriodTypeFromIsoString( isoPeriod );
            
            try
            {
                return periodType != null ? periodType.createPeriod( isoPeriod ) : null;
            }
            catch ( Exception ex )
            {
                // Do nothing and return null
            }
        }
        
        return null;
    }

    /**
     * Return the potential number of periods of the given period type which is
     * spanned by this period.
     *
     * @param type the period type.
     * @return the potential number of periods of the given period type spanned
     *         by this period.
     */
    public int getPeriodSpan( PeriodType type )
    {
        double no = (double) this.getFrequencyOrder() / type.getFrequencyOrder();

        return (int) Math.floor( no );
    }
    
    // -------------------------------------------------------------------------
    // ISO format methods
    // -------------------------------------------------------------------------

    /**
     * Returns an iso8601 formatted string representation of the period
     *
     * @param period
     * @return the period as string
     */
    public abstract String getIsoDate( Period period );

    /**
     * Generates a period based on the given iso8601 formatted string.
     *
     * @param isoDate the iso8601 string.
     * @return the period.
     */
    public abstract Period createPeriod( String isoDate );

    /**
     * Returns the iso8601 format as a string for this period type.
     * 
     * @return the iso8601 format.
     */
    public abstract String getIsoFormat();

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return getName().hashCode();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof PeriodType) )
        {
            return false;
        }

        final PeriodType other = (PeriodType) o;

        return getName().equals( other.getName() );
    }

    @Override
    public String toString()
    {
        return "[" + getName() + "]";
    }
}
