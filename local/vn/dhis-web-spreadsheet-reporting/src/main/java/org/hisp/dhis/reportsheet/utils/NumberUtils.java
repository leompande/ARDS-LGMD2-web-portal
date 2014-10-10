package org.hisp.dhis.reportsheet.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

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
 */
public class NumberUtils
{
    // DecimalFormatNumber for VN as same as Locale.GERMAN's one.
    private static DecimalFormat df = null;

    // This pattern used for VN
    public static final String PATTERN_DECIMAL_FORMAT1 = "#,##0.######";

    // This pattern is default in DHIS2
    public static final String PATTERN_DECIMAL_FORMAT2 = "#0.######";

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------

    public static void resetDecimalFormatByLocale( Locale locale )
    {
        df = (DecimalFormat) NumberFormat.getInstance( locale );
    }

    public static void applyPatternDecimalFormat( String pattern )
    {
        df.applyPattern( pattern == null ? PATTERN_DECIMAL_FORMAT1 : pattern );
    }

    public static String getFormattedNumber( String input )
    {
        if ( df == null )
        {
            return input;
        }
        
        try
        {
            return df.format( Double.parseDouble( input ) );
        }
        catch ( NumberFormatException nfe )
        {
            return input;
        }
    }
}
