package org.hisp.dhis.aggregation.impl;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.aggregation.impl.cache.AggregationCache;
import org.hisp.dhis.aggregation.impl.dataelement.AbstractDataElementAggregation;
import org.hisp.dhis.aggregation.impl.indicator.IndicatorAggregation;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import static org.hisp.dhis.system.util.DateUtils.*;
import static org.hisp.dhis.system.util.MathUtils.getAverage;
import static org.hisp.dhis.system.util.MathUtils.getSum;

import static org.hisp.dhis.dataelement.DataElement.*;

/**
 * @author Lars Helge Overland
 * @version $Id: DefaultAggregationService.java 5116 2008-05-08 10:51:21Z larshelg $
 */
public class DefaultAggregationService
    implements AggregationService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private AbstractDataElementAggregation sumIntDataElementAggregation;

    public void setSumIntDataElementAggregation( AbstractDataElementAggregation sumIntDataElementAggregation )
    {
        this.sumIntDataElementAggregation = sumIntDataElementAggregation;
    }

    private AbstractDataElementAggregation sumBoolDataElementAggregation;

    public void setSumBoolDataElementAggregation( AbstractDataElementAggregation sumBoolDataElementAggregation )
    {
        this.sumBoolDataElementAggregation = sumBoolDataElementAggregation;
    }

    private AbstractDataElementAggregation averageIntDataElementAggregation;

    public void setAverageIntDataElementAggregation( AbstractDataElementAggregation averageIntDataElementAggregation )
    {
        this.averageIntDataElementAggregation = averageIntDataElementAggregation;
    }

    private AbstractDataElementAggregation averageIntSingleValueAggregation;
    
    public void setAverageIntSingleValueAggregation( AbstractDataElementAggregation averageIntSingleValueAggregation )
    {
        this.averageIntSingleValueAggregation = averageIntSingleValueAggregation;
    }

    private AbstractDataElementAggregation averageBoolDataElementAggregation;

    public void setAverageBoolDataElementAggregation( AbstractDataElementAggregation averageBoolDataElementAggregation )
    {
        this.averageBoolDataElementAggregation = averageBoolDataElementAggregation;
    }
    
    private IndicatorAggregation indicatorAggregation;

    public void setIndicatorAggregation( IndicatorAggregation indicatorAggregation )
    {
        this.indicatorAggregation = indicatorAggregation;
    }
    
    private AggregationCache aggregationCache;

    public void setAggregationCache( AggregationCache aggregationCache )
    {
        this.aggregationCache = aggregationCache;
    }

    // -------------------------------------------------------------------------
    // DataElement
    // -------------------------------------------------------------------------

    public Double getAggregatedDataValue( DataElement dataElement, DataElementCategoryOptionCombo optionCombo, Date startDate, Date endDate,
        OrganisationUnit organisationUnit )
    {
        AbstractDataElementAggregation dataElementAggregation = 
            getInstance( dataElement.getType(), dataElement.getAggregationOperator(), startDate, endDate, dataElement );        

        return dataElementAggregation.getAggregatedValue( dataElement, optionCombo, startDate, endDate, organisationUnit );
    }

    public Double getAggregatedDataValue( DataElement dataElement, Date startDate, Date endDate, OrganisationUnit organisationUnit, DataElementCategoryOption categoryOption )
    {
        final List<Double> values = new ArrayList<Double>();
        
        for ( DataElementCategoryOptionCombo optionCombo : categoryOption.getCategoryOptionCombos() )
        {
            values.add( getAggregatedDataValue( dataElement, optionCombo, startDate, endDate, organisationUnit ) );
        }
        
        return dataElement.getAggregationOperator().equals( AGGREGATION_OPERATOR_SUM ) ? getSum( values ) : getAverage( values );
    }
    
    // -------------------------------------------------------------------------
    // Indicator
    // -------------------------------------------------------------------------

    public Double getAggregatedIndicatorValue( Indicator indicator, Date startDate, Date endDate,
        OrganisationUnit organisationUnit )
    {
        return indicatorAggregation.getAggregatedIndicatorValue( indicator, startDate, endDate, organisationUnit );
    }

    public Double getAggregatedNumeratorValue( Indicator indicator, Date startDate, Date endDate,
        OrganisationUnit organisationUnit )
    {
        return indicatorAggregation.getAggregatedNumeratorValue( indicator, startDate, endDate, organisationUnit );
    }

    public Double getAggregatedDenominatorValue( Indicator indicator, Date startDate, Date endDate,
        OrganisationUnit organisationUnit )
    {
        return indicatorAggregation.getAggregatedDenominatorValue( indicator, startDate, endDate, organisationUnit );
    }
    
    public void clearCache()
    {
        aggregationCache.clearCache();
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private AbstractDataElementAggregation getInstance( String valueType, String aggregationOperator, Date startDate, Date endDate, DataElement dataElement )
    {
        if ( valueType.equals( VALUE_TYPE_INT ) && aggregationOperator.equals( AGGREGATION_OPERATOR_SUM ) )
        {
            return sumIntDataElementAggregation;
        }
        else if ( valueType.equals( VALUE_TYPE_BOOL ) && aggregationOperator.equals( AGGREGATION_OPERATOR_SUM ) )
        {
            return sumBoolDataElementAggregation;
        }
        else if ( valueType.equals( VALUE_TYPE_INT ) && aggregationOperator.equals( AGGREGATION_OPERATOR_AVERAGE ) && dataElement.getFrequencyOrder() >= getDaysInclusive( startDate, endDate ) )
        {
            return averageIntSingleValueAggregation;
        }
        else if ( valueType.equals( VALUE_TYPE_INT ) && aggregationOperator.equals( AGGREGATION_OPERATOR_AVERAGE ) )
        {
            return averageIntDataElementAggregation;
        }
        else if ( valueType.equals( VALUE_TYPE_BOOL ) && aggregationOperator.equals( AGGREGATION_OPERATOR_AVERAGE ) )
        {
            return averageBoolDataElementAggregation;
        }
        else
        {
            throw new IllegalArgumentException( "Unsupported aggregation operator ("
                + aggregationOperator + ") or data element value type (" + valueType + ")" );
        }
    }
}
