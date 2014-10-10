package org.hisp.dhis.dxf2.metadata;

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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class Options
{
    //--------------------------------------------------------------------------
    // Static helpers
    //--------------------------------------------------------------------------

    protected static String stringAsString( String str, String defaultValue )
    {
        if ( str == null )
        {
            str = defaultValue;
        }

        return str;
    }

    public static Date stringAsDate( String str )
    {
        if ( str == null )
        {
            return null;
        }

        String patterns[] = new String[] {
            "yyyy-MM-dd",
            "yyyy-MM",
            "yyyyMMdd",
            "yyyyMM",
            "yyyy"
        };

        for ( String pattern : patterns )
        {
            Date date = getDateByPattern( str, pattern );

            if ( date != null )
            {
                return date;
            }
        }

        return null;
    }

    protected static Date getDateByPattern( String str, String pattern )
    {
        if ( str != null )
        {
            try
            {
                return new SimpleDateFormat( pattern ).parse( str );
            } catch ( ParseException ignored )
            {
            }
        }

        return null;
    }

    protected static boolean stringAsBoolean( String str )
    {
        return stringAsBoolean( str, false );
    }

    protected static boolean stringAsBoolean( String str, boolean defaultValue )
    {
        if ( str != null )
        {
            if ( str.equalsIgnoreCase( "true" ) )
            {
                return true;
            }
            else if ( str.equalsIgnoreCase( "false" ) )
            {
                return false;
            }
        }

        return defaultValue;
    }


    protected static int stringAsInt( String str )
    {
        return stringAsInt( str, 0 );
    }

    protected static int stringAsInt( String str, int defaultValue )
    {
        if ( str != null )
        {
            try
            {
                return Integer.parseInt( str );
            } 
            catch ( NumberFormatException ignored )
            {
            }
        }

        return defaultValue;
    }

    protected static boolean isTrue( String str )
    {
        return stringAsBoolean( str );
    }

    //--------------------------------------------------------------------------
    // Internal State
    //--------------------------------------------------------------------------

    protected Map<String, String> options = new HashMap<String, String>();

    protected boolean assumeTrue;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    public Options( Map<String, String> options )
    {
        this.options = options;
        this.assumeTrue = options.get( "assumeTrue" ) == null || options.get( "assumeTrue" ).equalsIgnoreCase( "true" );
    }

    public Options()
    {
    }

    //--------------------------------------------------------------------------
    // Get options for classes/strings etc
    //--------------------------------------------------------------------------

    public boolean isEnabled( String type )
    {
        String enabled = options.get( type );

        return isTrue( enabled ) || enabled == null && assumeTrue;
    }

    public boolean isDisabled( String type )
    {
        return !isEnabled( type );
    }

    public Date getDate( String key )
    {
        return stringAsDate( options.get( key ) );
    }

    //--------------------------------------------------------------------------
    // Getters and Setters
    //--------------------------------------------------------------------------

    public Map<String, String> getOptions()
    {
        return options;
    }

    public void setOptions( Map<String, String> options )
    {
        this.options = options;
    }

    public boolean isAssumeTrue()
    {
        return assumeTrue;
    }

    public void setAssumeTrue( boolean assumeTrue )
    {
        this.assumeTrue = assumeTrue;
    }

    //--------------------------------------------------------------------------
    // Getters for standard options
    //--------------------------------------------------------------------------

    public Date getLastUpdated()
    {
        return getDate( "lastUpdated" );
    }
    
    //--------------------------------------------------------------------------
    // Adding options
    //--------------------------------------------------------------------------
    
    public void addOption(String option, String value)
    {
        options.put( option, value);
    }
    
    public void addOptions(Map<String,String> newOptions)
    {
        options.putAll( options );
    }
    
}
