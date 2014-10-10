package org.hisp.dhis.system.util;

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

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class CsvUtils
{
    public static final String SEPARATOR = ",";
    public static final byte[] SEPARATOR_B = SEPARATOR.getBytes();
    public static final byte[] NEWLINE = "\n".getBytes();

    public static final String CSV_EXTENSION = ".csv";    
    private static final String ENCLOSURE = "\"";      
    private static final String EMPTY = "";
    
    /**
     * Encodes the given value to a CSV acceptable value. Returns the empty string
     * if argument is null.
     * 
     * @param value the value.
     * @return the CSV encoded value.
     */
    public static String csvEncode( int value )
    {
        return csvEncode( String.valueOf( value ) );
    }

    /**
     * Encodes the given value to a CSV acceptable value. Returns the empty string
     * if argument is null.
     * 
     * @param value the value.
     * @return the CSV encoded value.
     */
    public static String csvEncode( String value )
    {
        if ( value == null )
        {
            value = EMPTY;
        }
        else
        {            
            value = value.replaceAll( ENCLOSURE, ENCLOSURE + ENCLOSURE );
            value = ENCLOSURE + value + ENCLOSURE;
        }
                    
        return value;
    }

    /**
     * Encodes the given value to a CSV acceptable value. Returns the empty string
     * if argument is null.
     * 
     * @param value the value.
     * @return the CSV encoded value.
     */
    public static String csvEncode( Object value )
    {
        return value != null ? csvEncode( String.valueOf( value ) ) : EMPTY;
    }
    
    /**
     * Appends a separator to the value and returns the value as a byte array.
     * 
     * @param value the value.
     * @return a byte araray.
     */
    public static byte[] getCsvValue( int value )
    {
        return getCsvEndValue( value + SEPARATOR );
    }
    
    /**
     * Appends a separator to the value and returns the value as a byte array.
     * 
     * @param value the value.
     * @return a byte araray.
     */
    public static byte[] getCsvValue( String value )
    {
        return getCsvEndValue( value + SEPARATOR );
    }
    
    public static byte[] getCsvEndValue( int value )
    {
        return getCsvEndValue( String.valueOf( value ) );
    }
    
    public static byte[] getCsvEndValue( String value )
    {
        if ( value == null )
        {
            return EMPTY.getBytes();
        }
        
        return ( value ).getBytes();
    }
}
