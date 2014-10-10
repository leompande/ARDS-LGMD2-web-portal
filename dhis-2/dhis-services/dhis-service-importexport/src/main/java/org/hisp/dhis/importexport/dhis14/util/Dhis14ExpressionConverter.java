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

import static org.hisp.dhis.expression.Expression.SEPARATOR;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.DataElement;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class Dhis14ExpressionConverter
{
    private static final Log log = LogFactory.getLog( Dhis14ExpressionConverter.class );

    private static final String NULL_REPLACEMENT = "0";
    
    /**
     * Converts an indicator formula from the DHIS 1.4 format to the DHIS 2 format.
     * 
     * @param expression the DHIS 1.4 expression.
     * @return a DHIS 2 expression.
     * @throws IllegalArgumentException if a data element identifier in formula 
     *         is not of type int.
     */
    public static String convertExpressionFromDhis14( String expression, Map<Object, Integer> dataElementMapping, int categoryOptionComboId, String name )
    {
        StringBuffer convertedFormula = new StringBuffer();
        
        if ( expression != null )
        {
            // -----------------------------------------------------------------
            // Replace DHIS 1.4 types like "SUM" from formula  
            // -----------------------------------------------------------------

            expression = expression.replaceAll( "[A-Z,a-z]", "" );
            
            // -----------------------------------------------------------------
            // Extract DataElement ids on the form "[123]" from the formula
            // -----------------------------------------------------------------

            Matcher matcher = getMatcher( "\\(\\[\\d+\\]\\)", expression );
            
            while ( matcher.find() )
            {
                String replaceString = matcher.group();
                
                // -------------------------------------------------------------
                // Remove brackets to get identifier
                // -------------------------------------------------------------
    
                String id = replaceString.replaceAll( "[\\(\\[\\]\\)]", "" );
                
                // -------------------------------------------------------------
                // Parse identifier to int
                // -------------------------------------------------------------
    
                int dataElementId = -1;
                
                try
                {
                    dataElementId = Integer.parseInt( id );
                }
                catch ( NumberFormatException ex )
                {
                    throw new IllegalArgumentException( "Illegal identifier in formula: " + replaceString, ex );
                }
    
                // -------------------------------------------------------------
                // Get identifier from the DataElement in the database
                // -------------------------------------------------------------

                Integer convertedDataElementId = dataElementMapping.get( dataElementId );
                
                if ( convertedDataElementId == null )
                {
                    log.warn( "'" + name + "' contains a non-existing data element identifier: " + dataElementId );
                    
                    replaceString = NULL_REPLACEMENT;
                }
                else
                {
                    replaceString = "[" + convertedDataElementId + SEPARATOR + categoryOptionComboId + "]";
                }
                
                matcher.appendReplacement( convertedFormula, replaceString );
            }
            
            matcher.appendTail( convertedFormula );
        }
        
        return convertedFormula.toString();
    }
    
    /**
     * Converts an indicator formula from the DHIS 2 format to the DHIS 1.4 format.
     * 
     * @param expression the DHIS 2 expression.
     * @return a DHIS 1.4 expression.
     */
    public static String convertExpressionToDhis14( String expression, Map<Object, String> dataElementAggregationOperatorMap )
    {
        StringBuffer convertedFormula = new StringBuffer();
        
        if ( expression != null )
        {
            Matcher matcher = getMatcher( "(\\[\\d+\\" + SEPARATOR + "\\d+\\])", expression );

            while ( matcher.find() )
            {
                String match = matcher.group();
                
                match = match.replaceAll( "[\\[\\]]", "" );

                Integer dataElementId = Integer.parseInt( match.substring( 0, match.indexOf( SEPARATOR ) ) );

                String aggregationOperator = dataElementAggregationOperatorMap.get( dataElementId );
                
                if ( aggregationOperator == null )
                {
                    throw new IllegalArgumentException( "Data element with id: " + dataElementId + " does not exist / have an aggregation operator" );
                }
                
                char type = aggregationOperator.equals( DataElement.AGGREGATION_OPERATOR_AVERAGE ) ? 'S' : 'R';
                
                match = "Sum([" + type + dataElementId + "])";
                
                matcher.appendReplacement( convertedFormula, match );
            }

            matcher.appendTail( convertedFormula );
        }
        
        return convertedFormula.toString();
    }

    /**
     * Returns the identifier of the first data element found in the formula.
     * 
     * @param formula a DHIS 1.4 indicator formula.
     * @return the identifier of the first data element found in the formula.
     * @throws IllegalArgumentException if data element identifier in formula 
     *         is not of type int.
     */
    public static Integer getFirstDataElementId( String formula )
    {
        if ( formula != null )
        {
            formula = formula.replaceAll( "[A-Z,a-z]", "" );
    
            Matcher matcher = getMatcher( "\\[\\d+\\]", formula );
            
            if ( matcher.find() )
            {
                String identifier = matcher.group().replaceAll( "[\\[\\]]", "" );
                
                try
                {
                    return Integer.parseInt( identifier );
                }
                catch ( NumberFormatException ex )
                {
                    throw new IllegalArgumentException( "Illegal identifier in formula: " + identifier, ex );
                }           
            }
        }
        
        return null;
    }
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private static Matcher getMatcher( String regex, String expression )
    {
        Pattern pattern = Pattern.compile( regex );

        return pattern.matcher( expression );
    }
}
