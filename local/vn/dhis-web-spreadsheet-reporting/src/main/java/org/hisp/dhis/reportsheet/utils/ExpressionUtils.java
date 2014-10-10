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

import static org.hisp.dhis.expression.Expression.SEPARATOR;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reportsheet.ExportItem;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class ExpressionUtils
{
    static final String NULL_REPLACEMENT = "0";

    static final String EMPTY = "";

    /**
     * Converts an expression on the form<br>
     * [34] + [23], where the numbers are IDs of DataElements, to the form<br>
     * 200 + 450, where the numbers are the values of the DataValues registered
     * for the Period and source.
     * 
     * @param dataElementService
     * @param categoryService
     * 
     * @return The generated expression
     */
    public static String generateExpression( ExportItem reportItem, Period period, OrganisationUnit organisationUnit,
        DataElementService dataElementService, DataElementCategoryService categoryService,
        DataValueService dataValueService )
    {
        try
        {
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( reportItem.getExpression() );
            StringBuffer buffer = new StringBuffer();

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", EMPTY );

                String dataElementIdString = replaceString.substring( 0, replaceString.indexOf( SEPARATOR ) );
                String optionComboIdString = replaceString.substring( replaceString.indexOf( SEPARATOR ) + 1,
                    replaceString.length() );

                int dataElementId = Integer.parseInt( dataElementIdString );
                int optionComboId = Integer.parseInt( optionComboIdString );

                DataElement dataElement = dataElementService.getDataElement( dataElementId );

                DataElementCategoryOptionCombo optionCombo = categoryService
                    .getDataElementCategoryOptionCombo( optionComboId );

                {
                    replaceString = getValue( dataElement, optionCombo, organisationUnit, period, dataValueService );

                    matcher.appendReplacement( buffer, replaceString );
                }
            }

            // Finally
            matcher.appendTail( buffer );

            return buffer.toString();
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }

    public static String generateExpression( ExportItem reportItem, Date startDate, Date endDate,
        OrganisationUnit organisationUnit, DataElementService dataElementService,
        DataElementCategoryService categoryService, AggregationService aggregationService )
    {
        try
        {
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( reportItem.getExpression() );
            StringBuffer buffer = new StringBuffer();

            while ( matcher.find() )
            {
                String replaceString = matcher.group();
                
                replaceString = replaceString.replaceAll( "[\\[\\]]", EMPTY );

                String dataElementIdString = replaceString.substring( 0, replaceString.indexOf( SEPARATOR ) );
                String optionComboIdString = replaceString.substring( replaceString.indexOf( SEPARATOR ) + 1,
                    replaceString.length() );
                
                int dataElementId = Integer.parseInt( dataElementIdString );
                int optionComboId = Integer.parseInt( optionComboIdString );

                DataElement dataElement = dataElementService.getDataElement( dataElementId );
                DataElementCategoryOptionCombo optionCombo = categoryService
                    .getDataElementCategoryOptionCombo( optionComboId );

                {
                    replaceString = getValue( dataElement, optionCombo, organisationUnit, startDate, endDate,
                        aggregationService )
                        + EMPTY;

                    matcher.appendReplacement( buffer, replaceString );
                }
            }

            // Finally
            matcher.appendTail( buffer );

            return buffer.toString();
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }

    public static String generateIndicatorExpression( ExportItem reportItem, Date startDate, Date endDate,
        OrganisationUnit organisationUnit, IndicatorService indicatorService, AggregationService aggregationService )
    {
        try
        {
            Pattern pattern = Pattern.compile( "(\\[\\d+\\])" );

            Matcher matcher = pattern.matcher( reportItem.getExpression() );
            StringBuffer buffer = new StringBuffer();

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );

                String indicatorIdString = replaceString.trim();

                int indicatorId = Integer.parseInt( indicatorIdString );

                Indicator indicator = indicatorService.getIndicator( indicatorId );

                Double aggregatedValue = aggregationService.getAggregatedIndicatorValue( indicator, startDate, endDate,
                    organisationUnit );

                if ( aggregatedValue == null )
                {
                    replaceString = NULL_REPLACEMENT;
                }
                else
                {
                    replaceString = String.valueOf( aggregatedValue );
                }

                matcher.appendReplacement( buffer, replaceString );
            }

            matcher.appendTail( buffer );

            return buffer.toString();
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }

    // -------------------------------------------------------------------------
    // Supporting method(s)
    // -------------------------------------------------------------------------

    private static String getValue( DataElement dataElement, DataElementCategoryOptionCombo optionCombo,
        OrganisationUnit organisationUnit, Period period, DataValueService dataValueService )
    {
        if ( period == null )
        {
            return EMPTY;
        }

        DataValue dv = dataValueService.getDataValue( organisationUnit, dataElement, period, optionCombo );

        return (dv == null ? EMPTY : dv.getValue());
    }

    private static double getValue( DataElement dataElement, DataElementCategoryOptionCombo optionCombo,
        OrganisationUnit organisationUnit, Date startDate, Date endDate, AggregationService aggregationService )
    {
        Double aggregatedValue = aggregationService.getAggregatedDataValue( dataElement, optionCombo, startDate,
            endDate, organisationUnit );
        // aggregatedDataValueService.getAggregatedValue( dataElement,
        // optionCombo, startDate, endDate, );

        return (aggregatedValue == null) ? 0 : aggregatedValue;
    }
}
