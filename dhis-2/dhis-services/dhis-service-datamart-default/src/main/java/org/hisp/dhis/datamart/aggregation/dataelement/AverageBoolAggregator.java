package org.hisp.dhis.datamart.aggregation.dataelement;

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
import static org.hisp.dhis.dataelement.DataElement.VALUE_TYPE_BOOL;
import static org.hisp.dhis.system.util.DateUtils.getDaysInclusive;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.datamart.CrossTabDataValue;
import org.hisp.dhis.datamart.aggregation.cache.AggregationCache;
import org.hisp.dhis.datamart.crosstab.CrossTabService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.springframework.util.CollectionUtils;

/**
 * @author Lars Helge Overland
 */
public class AverageBoolAggregator
    implements DataElementAggregator
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CrossTabService crossTabService;

    public void setCrossTabService( CrossTabService crossTabService )
    {
        this.crossTabService = crossTabService;
    }

    protected AggregationCache aggregationCache;
        
    public void setAggregationCache( AggregationCache aggregationCache )
    {
        this.aggregationCache = aggregationCache;
    }

    // -------------------------------------------------------------------------
    // DataElementAggregator implementation
    // -------------------------------------------------------------------------

    public Map<DataElementOperand, Double> getAggregatedValues( final Collection<DataElementOperand> operands, 
        final Period period, int unitLevel, final Collection<Integer> organisationUnits, String key )
    {
        if ( CollectionUtils.isEmpty( operands ) )
        {
            return EMPTY_MAP;
        }
        
        final Collection<CrossTabDataValue> crossTabValues = crossTabService.getCrossTabDataValues( operands, 
            aggregationCache.getIntersectingPeriods( period.getStartDate(), period.getEndDate() ), organisationUnits, key );
        
        final Map<DataElementOperand, double[]> entries = getAggregate( crossTabValues, period.getStartDate(), 
            period.getEndDate(), period.getStartDate(), period.getEndDate(), unitLevel ); // <Operand, [total value, total days]>

        final Map<DataElementOperand, Double> values = new HashMap<DataElementOperand, Double>(); // <Operand, average value>

        double average = 0.0;
        
        for ( final Entry<DataElementOperand, double[]> entry : entries.entrySet() )
        {
            if ( entry.getValue() != null && entry.getValue()[ 1 ] > 0 )
            {
                average = entry.getValue()[ 0 ] / entry.getValue()[ 1 ] * 100; // Percentage value
                
                values.put( entry.getKey(), average );
            }
        }
        
        return values;        
    }
    
    private Map<DataElementOperand, double[]> getAggregate( final Collection<CrossTabDataValue> crossTabValues, 
        final Date startDate, final Date endDate, final Date aggregationStartDate, final Date aggregationEndDate, int unitLevel )
    {
        final Map<DataElementOperand, double[]> totalSums = new HashMap<DataElementOperand, double[]>(); // <Operand, [total value, total days]>

        for ( final CrossTabDataValue crossTabValue : crossTabValues )
        {
            final Period period = aggregationCache.getPeriod( crossTabValue.getPeriodId() );
            
            final Date currentStartDate = period.getStartDate();
            final Date currentEndDate = period.getEndDate();
            
            final double duration = getDaysInclusive( currentStartDate, currentEndDate );

            final int dataValueLevel = aggregationCache.getLevelOfOrganisationUnit( crossTabValue.getSourceId() );
            
            if ( duration > 0 )
            {
                for ( final Entry<DataElementOperand, String> entry : crossTabValue.getValueMap().entrySet() ) // <Operand, value>
                {
                    if ( entry.getValue() != null && entry.getKey().aggregationLevelIsValid( unitLevel, dataValueLevel ) )
                    {
                        double value = 0.0;                        
                        double relevantDays = 0.0;
                        
                        if ( currentStartDate.compareTo( startDate ) >= 0 && currentEndDate.compareTo( endDate ) <= 0 ) // Value is within period
                        {
                            relevantDays = getDaysInclusive( currentStartDate, currentEndDate );
                        }
                        else if ( currentStartDate.compareTo( startDate ) <= 0 && currentEndDate.compareTo( endDate ) >= 0 ) // Value spans whole period
                        {
                            relevantDays = getDaysInclusive( startDate, endDate );
                        }
                        else if ( currentStartDate.compareTo( startDate ) <= 0 && currentEndDate.compareTo( startDate ) >= 0 // Value spans period start
                            && currentEndDate.compareTo( endDate ) <= 0 )
                        {
                            relevantDays = getDaysInclusive( startDate, currentEndDate );
                        }
                        else if ( currentStartDate.compareTo( startDate ) >= 0 && currentStartDate.compareTo( endDate ) <= 0 // Value spans period end
                            && currentEndDate.compareTo( endDate ) >= 0 )
                        {
                            relevantDays = getDaysInclusive( currentStartDate, endDate );
                        }

                        if ( entry.getValue().toLowerCase().equals( TRUE ) )
                        {
                            value = relevantDays;
                        }

                        final double[] totalSum = totalSums.get( entry.getKey() );
                        value += totalSum != null ? totalSum[0] : 0;
                        final double days = totalSum != null ? totalSum[1] + duration : duration;
                        
                        final double[] values = { value, days };
                        
                        totalSums.put( entry.getKey(), values );
                    }
                }
            }
        }
        
        return totalSums;
    }

    public Collection<DataElementOperand> filterOperands( final Collection<DataElementOperand> operands, final PeriodType periodType )
    {
        final Collection<DataElementOperand> filteredOperands = new HashSet<DataElementOperand>();
        
        for ( final DataElementOperand operand : operands )
        {
            if ( operand.getValueType().equals( VALUE_TYPE_BOOL ) && operand.getAggregationOperator().equals( AGGREGATION_OPERATOR_AVERAGE ) )
            {
                filteredOperands.add( operand );
            }
        }
        
        return filteredOperands;
    }
}
