package org.hisp.dhis.importexport.xml;

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

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.apache.commons.math.util.MathUtils;

/**
 * 
 * @author bobj
 */
public class Util
{
    /**
     * Compensating for Excel wonky storage for dates
     * 
     * @param xltimestr the number of days since 1/1/1900 as undertood by excel
     * @return
     */
    public static String date( String xltimestr )
    {
        try
        {
            // the number of days since 1/1/1900
            Integer xldays = new Integer( xltimestr );

            // the beginning of excel time is 1/1/1900
            Calendar cal = new GregorianCalendar();
            cal.set( 1900, 0, 1 );
            // cal.add( Calendar.DAY_OF_MONTH, 20 );
            cal.add( Calendar.DAY_OF_MONTH, xldays.intValue() - 2 );
            return cal.getTime().toString();
        }
        catch ( Exception ex )
        {
            return "";
        }
    }

    /**
     * Tokenizer to convert coordinates in GML to a sequence of
     * <coord>nnn,nnn</coord>
     * 
     * @param coordinates
     * @return
     */
    public static String gmlToCoords( String coordinates, String decimalPlacesAsString )
        throws ParseException
    {
        NumberFormat nf = NumberFormat.getInstance( Locale.ENGLISH );

        int decimals = Integer.parseInt( decimalPlacesAsString );
        
        StringBuilder sb = new StringBuilder();
        String[] coords = coordinates.split( "\\s" );

        for ( String coordAsString : coords )
        {
            String[] latlon = coordAsString.split( "," );
            double lat = nf.parse( latlon[0] ).doubleValue();
            double lon = nf.parse( latlon[1] ).doubleValue();
            sb.append( "<coord>" );
            sb.append( MathUtils.round( lat, decimals ) + "," + MathUtils.round( lon, decimals ) );
            sb.append( "</coord>" );
        }

        return sb.toString();
    }
}
