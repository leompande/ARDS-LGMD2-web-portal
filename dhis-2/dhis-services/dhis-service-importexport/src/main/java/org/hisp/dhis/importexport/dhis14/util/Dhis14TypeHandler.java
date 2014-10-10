package org.hisp.dhis.importexport.dhis14.util;

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

import static org.hisp.dhis.dataelement.DataElement.AGGREGATION_OPERATOR_AVERAGE;
import static org.hisp.dhis.dataelement.DataElement.AGGREGATION_OPERATOR_COUNT;
import static org.hisp.dhis.dataelement.DataElement.AGGREGATION_OPERATOR_SUM;
import static org.hisp.dhis.dataelement.DataElement.VALUE_TYPE_BOOL;
import static org.hisp.dhis.dataelement.DataElement.VALUE_TYPE_INT;
import static org.hisp.dhis.dataelement.DataElement.VALUE_TYPE_STRING;

import org.hisp.dhis.datavalue.DataValue;

/**
 * @author Lars Helge Overland
 * @version $Id: TypeHandler.java 6216 2008-11-06 18:06:42Z eivindwa $
 */
public class Dhis14TypeHandler
{
    private static final String DHIS14_TRUE = "1";
    private static final String DHIS14_FALSE = "0";
    
    private static final String DHIS14_AGGREGATION_OPERATOR_SUM = "Sum";
    private static final String DHIS14_AGGREGATION_OPERATOR_AVERAGE = "Average";
    private static final String DHIS14_AGGREGATION_OPERATOR_COUNT = "Count";
    
    private static final String DHIS14_TYPE_INT = "3";
    private static final String DHIS14_TYPE_STRING = "4";
    private static final String DHIS14_TYPE_BOOL = "5";
    
    // -------------------------------------------------------------------------
    // Yes/No
    // -------------------------------------------------------------------------
    
    public static String convertYesNoFromDhis14( Integer value )
    {
        return value != null && value.intValue() == 1 ? DataValue.TRUE : DataValue.FALSE;
    }
    
    // -------------------------------------------------------------------------
    // Boolean
    // -------------------------------------------------------------------------
    
    public static String convertBooleanToDhis14( boolean value )
    {
        return value ? DHIS14_TRUE : DHIS14_FALSE;
    }
    
    public static String convertBooleanToDhis14( String value )
    {
        return value.equals("true") ? "1" : "0";
    }
    
    public static boolean convertBooleanFromDhis14( String value )
    {
        return value.equals( DHIS14_TRUE );
    }
    

    // -------------------------------------------------------------------------
    // Aggregation operator
    // -------------------------------------------------------------------------
    
    public static String convertAggregationOperatorToDhis14( String value )
    {
        if ( value == null || value.equals( AGGREGATION_OPERATOR_SUM ) )
        {
            return DHIS14_AGGREGATION_OPERATOR_SUM;
        }
        if ( value.equals( AGGREGATION_OPERATOR_AVERAGE ) )
        {
            return DHIS14_AGGREGATION_OPERATOR_AVERAGE;
        }
        if ( value.equals( AGGREGATION_OPERATOR_COUNT ) )
        {
            return DHIS14_AGGREGATION_OPERATOR_COUNT;
        }
        
        return DHIS14_AGGREGATION_OPERATOR_SUM;
    }
    
    public static String convertAggregationOperatorFromDhis14( String value )
    {
        if ( value == null || value.equals( DHIS14_AGGREGATION_OPERATOR_SUM ) )
        {
            return AGGREGATION_OPERATOR_SUM;
        }
        if ( value.equals( DHIS14_AGGREGATION_OPERATOR_AVERAGE ) )
        {
            return AGGREGATION_OPERATOR_AVERAGE;            
        }
        if ( value.equals( DHIS14_AGGREGATION_OPERATOR_COUNT ) )
        {
            return AGGREGATION_OPERATOR_COUNT;
        }
        
        return AGGREGATION_OPERATOR_SUM;
    }

    // -------------------------------------------------------------------------
    // Type
    // -------------------------------------------------------------------------
    
    public static String convertTypeToDhis14( String value )
    {
        if ( value == null || value.equals( VALUE_TYPE_INT ) )
        {
            return DHIS14_TYPE_INT;
        }
        if ( value.equals( VALUE_TYPE_STRING ) )
        {
            return DHIS14_TYPE_STRING;
        }
        if ( value.equals( VALUE_TYPE_BOOL ) )
        {
            return DHIS14_TYPE_BOOL;
        }
        
        return DHIS14_TYPE_INT;
    }
    
    public static String convertTypeFromDhis14( String value )
    {
        if ( value == null || value.equals( DHIS14_TYPE_INT ) )
        {
            return VALUE_TYPE_INT;
        }
        if ( value.equals( DHIS14_TYPE_STRING ) )
        {
            return VALUE_TYPE_STRING;
        }
        if ( value.equals( DHIS14_TYPE_BOOL ) )
        {
            return VALUE_TYPE_BOOL;
        }
        
        return VALUE_TYPE_INT;
    }
}
