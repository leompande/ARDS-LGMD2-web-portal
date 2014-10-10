package org.hisp.dhis.reportsheet.utils;


/*
 * Copyright (c) 2004-2011, University of Oslo
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

/**
 * @author Dang Duy Hieu
 * @version $Id$
 * @since 2009-11-14
 */
public class StringUtils
{
    private static final String DOT = ".";

    private static final String MIDDLE_LINE = "-";

    public static final String NUMBER_OF_ZERO = "0";

    private static final String SEPARATE = "/";

    /* ---------------------------------------------------------------------- */
    /*                                                                        */
    /* ---------------------------------------------------------------------- */

    public static String refiningNumberDecimalFormat( String input )
    {
        try
        {
            String s1 = "";
            String s2 = "";

            if ( Double.parseDouble( input ) >= 0.0d )
            {
                if ( input.contains( DOT ) )
                {
                    s1 = input.split( "\\." )[0];
                    s2 = input.split( "\\." )[1];

                    if ( (new Double( s1 ) == 0.0d) && (new Double( s2 ) != 0.0d) )
                    {
                        input = (NUMBER_OF_ZERO + DOT).concat( splitZeroAtEndOfNumberic( s2 ) );
                    }
                    else if ( (new Double( s1 ) != 0.0d) && (new Double( s2 ) == 0.0d) )
                    {
                        input = String.valueOf( new Long( s1 ) );
                    }
                    else
                    {
                        input = String.valueOf( new Long( s1 ) ).concat( DOT + splitZeroAtEndOfNumberic( s2 ) );
                    }
                }

                if ( new Double( input ) == 0.0d )
                {
                    input = MIDDLE_LINE;
                }
            }

            return input;
        }
        catch ( NumberFormatException nfe )
        {
            return input;
        }
    }

    /* ---------------------------------------------------------------------- */
    /*                                                                        */
    /* ---------------------------------------------------------------------- */

    public static String splitZeroAtEndOfNumberic( String number )
    {
        int counterZero = 0;
        char[] ch = number.toCharArray();

        for ( int i = (ch.length - 1); i >= 0; i-- )
        {
            if ( ch[i] == '0' )
            {
                counterZero++;
            }
            else
            {
                number = number.substring( 0, ch.length - counterZero );
                break;
            }
        }

        return number;
    }

    /* ---------------------------------------------------------------------- */
    /*                                                                        */
    /* ---------------------------------------------------------------------- */

    @SuppressWarnings( "unused" )
    private static final String replacedSeparateCharacter( String path )
    {
        path = path.replace( "\\", SEPARATE );

        return path;
    }

    public static boolean isNullOREmpty( String input )
    {
        return (((input == "") || (input == null)) ? true : false);
    }
}
