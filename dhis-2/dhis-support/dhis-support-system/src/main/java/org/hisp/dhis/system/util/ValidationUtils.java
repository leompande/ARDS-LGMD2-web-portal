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

import org.apache.commons.validator.routines.DateValidator;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.datavalue.DataValue;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hisp.dhis.dataelement.DataElement.*;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class ValidationUtils
{
    private static Pattern POINT_PATTERN = Pattern.compile( "\\[(.+),\\s?(.+)\\]" );
    private static Pattern DIGIT_PATTERN = Pattern.compile( ".*\\d.*" );
    private static Pattern UPPERCASE_PATTERN = Pattern.compile( ".*[A-Z].*" );

    private static int LONG_MAX = 180;
    private static int LONG_MIN = -180;
    private static int LAT_MAX = 90;
    private static int LAT_MIN = -90;

    /**
     * Validates whether an email string is valid.
     *
     * @param email the email string.
     * @return true if the email string is valid, false otherwise.
     */
    public static boolean emailIsValid( String email )
    {
        return EmailValidator.getInstance().isValid( email );
    }

    /**
     * Validates whether a date string is valid for the given Locale.
     *
     * @param date   the date string.
     * @param locale the Locale
     * @return true if the date string is valid, false otherwise.
     */
    public static boolean dateIsValid( String date, Locale locale )
    {
        return DateValidator.getInstance().isValid( date, locale );
    }

    /**
     * Validates whether a date string is valid for the default Locale.
     *
     * @param date the date string.
     * @return true if the date string is valid, false otherwise.
     */
    public static boolean dateIsValid( String date )
    {
        return dateIsValid( date, null );
    }

    /**
     * Validates whether an URL string is valid.
     *
     * @param url the URL string.
     * @return true if the URL string is valid, false otherwise.
     */
    public static boolean urlIsValid( String url )
    {
        return new UrlValidator().isValid( url );
    }

    /**
     * Validates whether a password is valid. A password must:
     * <p/>
     * <ul>
     * <li>Be between 8 and 80 characters long</li>
     * <li>Include at least one digit</li>
     * <li>Include at least one uppercase letter</li>
     * </ul>
     *
     * @param password the password.
     * @return true if the password is valid, false otherwise.
     */
    public static boolean passwordIsValid( String password )
    {
        if ( password == null || password.trim().length() < 8 || password.trim().length() > 80 )
        {
            return false;
        }

        return DIGIT_PATTERN.matcher( password ).matches() && UPPERCASE_PATTERN.matcher( password ).matches();
    }

    /**
     * Validates whether a coordinate is valid.
     *
     * @return true if the coordinate is valid, false otherwise.
     */
    public static boolean coordinateIsValid( String coordinate )
    {
        if ( coordinate == null || coordinate.trim().isEmpty() )
        {
            return false;
        }

        Matcher matcher = POINT_PATTERN.matcher( coordinate );

        if ( !matcher.find() )
        {
            return false;
        }

        double longitude = 0.0;
        double latitude = 0.0;

        try
        {
            longitude = Double.parseDouble( matcher.group( 1 ) );
            latitude = Double.parseDouble( matcher.group( 2 ) );
        }
        catch ( NumberFormatException ex )
        {
            return false;
        }

        return longitude >= LONG_MIN && longitude <= LONG_MAX && latitude >= LAT_MIN && latitude <= LAT_MAX;
    }

    /**
     * Returns the longitude from the given coordinate. Returns null if the
     * coordinate string is not valid. The coordinate is on the form
     * longitude / latitude.
     *
     * @param coordinate the coordinate string.
     * @return the longitude.
     */
    public static String getLongitude( String coordinate )
    {
        if ( coordinate == null )
        {
            return null;
        }

        Matcher matcher = POINT_PATTERN.matcher( coordinate );

        return matcher.find() ? matcher.group( 1 ) : null;
    }

    /**
     * Returns the latitude from the given coordinate. Returns null if the
     * coordinate string is not valid. The coordinate is on the form
     * longitude / latitude.
     *
     * @param coordinate the coordinate string.
     * @return the latitude.
     */
    public static String getLatitude( String coordinate )
    {
        if ( coordinate == null )
        {
            return null;
        }

        Matcher matcher = POINT_PATTERN.matcher( coordinate );

        return matcher.find() ? matcher.group( 2 ) : null;
    }


    /**
     * Returns the longitude and latitude from the given coordinate.
     *
     * @param coordinate the coordinate string.
     * @return Point2D of the coordinate.
     */
    public static Point2D getCoordinatePoint2D( String coordinate )
    {
        if ( coordinate == null )
        {
            return null;
        }

        Matcher matcher = POINT_PATTERN.matcher( coordinate );

        if( matcher.find() && matcher.groupCount() == 2 )
        {
            return new Point2D.Double( Double.parseDouble( matcher.group( 1 ) ), Double.parseDouble( matcher.group( 2 ) ) );
        }
        else return null;        
    }

    /**
     * Returns a coordinate string based on the given latitude and longitude.
     * The coordinate is on the form longitude / latitude.
     *
     * @param longitude the longitude string.
     * @param latitude  the latitude string.
     * @return a coordinate string.
     */
    public static String getCoordinate( String longitude, String latitude )
    {
        return "[" + longitude + "," + latitude + "]";
    }

    /**
     * Checks if the given data value is valid according to the value type of the
     * given data element. Considers the value to be valid if null or empty.
     * Returns a string if the valid is invalid, possible
     * values are:
     * <p/>
     * <ul>
     * <li>data_element_or_type_null_or_empty</li>
     * <li>value_length_greater_than_max_length</li>
     * <li>value_not_numeric</li>
     * <li>value_not_integer</li>
     * <li>value_not_positive_integer</li>
     * <li>value_not_negative_integer</li>
     * <li>value_is_zero_and_not_zero_significant</li>
     * <li>value_not_bool</li>
     * <li>value_not_true_only</li>
     * <li>value_not_valid_date</li>
     * </ul>
     *
     * @param value       the data value.
     * @param dataElement the data element.
     * @return null if the value is valid, a string if not.
     */
    public static String dataValueIsValid( String value, DataElement dataElement )
    {
        if ( value == null || value.trim().isEmpty() )
        {
            return null;
        }

        if ( dataElement == null || dataElement.getType() == null || dataElement.getType().isEmpty() )
        {
            return "data_element_or_type_null_or_empty";
        }

        List<String> types = Arrays.asList( VALUE_TYPE_STRING, VALUE_TYPE_INT, VALUE_TYPE_NUMBER, 
            VALUE_TYPE_POSITIVE_INT, VALUE_TYPE_NEGATIVE_INT, VALUE_TYPE_ZERO_OR_POSITIVE_INT );

        String type = dataElement.getDetailedNumberType();

        if ( types.contains( type ) && value.length() > 255 )
        {
            return "value_length_greater_than_max_length";
        }

        if ( VALUE_TYPE_NUMBER.equals( type ) && !MathUtils.isNumeric( value ) )
        {
            return "value_not_numeric";
        }

        if ( VALUE_TYPE_INT.equals( type ) && !MathUtils.isInteger( value ) )
        {
            return "value_not_integer";
        }

        if ( VALUE_TYPE_POSITIVE_INT.equals( type ) && !MathUtils.isPositiveInteger( value ) )
        {
            return "value_not_positive_integer";
        }

        if ( VALUE_TYPE_NEGATIVE_INT.equals( type ) && !MathUtils.isNegativeInteger( value ) )
        {
            return "value_not_negative_integer";
        }
        
        if ( VALUE_TYPE_ZERO_OR_POSITIVE_INT.equals( type ) && !MathUtils.isZeroOrPositiveInteger( value ) )
        {
            return "value_not_zero_or_positive_integer";
        }
        
        if ( VALUE_TYPE_BOOL.equals( type ) && !MathUtils.isBool( value ) )
        {
            return "value_not_bool";
        }

        if ( VALUE_TYPE_TRUE_ONLY.equals( type ) && !DataValue.TRUE.equals( value ) )
        {
            return "value_not_true_only";
        }
        
        if ( VALUE_TYPE_DATE.equals( type ) && !DateUtils.dateIsValid( value ) )
        {
            return "value_not_valid_date";
        }
        
        if ( VALUE_TYPE_INT.equals( dataElement.getType() ) && MathUtils.isZero( value ) &&
            !dataElement.isZeroIsSignificant() && !AGGREGATION_OPERATOR_AVERAGE.equals( dataElement.getAggregationOperator() ) )
        {
            return "value_is_zero_and_not_zero_significant";
        }

        return null;
    }
    
    /**
     * Checks if the given comment is valid. Returns null if valid and a string
     * if invalid, possible values are:
     * </p>
     * <ul>
     * <li>comment_too_long</li>
     * </ul>
     * 
     * @param comment the comment.
     * @return null if the comment is valid, a string if not.
     */
    public static String commentIsValid( String comment )
    {
        if ( comment == null || comment.trim().isEmpty() )
        {
            return null;
        }
        
        if ( comment.length() > 360 )
        {
            return "comment_too_long";
        }
        
        return null;
    }
}
