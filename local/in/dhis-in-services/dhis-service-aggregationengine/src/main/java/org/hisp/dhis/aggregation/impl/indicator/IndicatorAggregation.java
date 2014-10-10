package org.hisp.dhis.aggregation.impl.indicator;

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

import static org.hisp.dhis.system.util.DateUtils.daysBetween;
import static org.hisp.dhis.system.util.MathUtils.INVALID;
import static org.hisp.dhis.system.util.MathUtils.calculateExpression;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.aggregation.impl.cache.AggregationCache;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.system.util.DateUtils;

/**
 * @author Lars Helge Overland
 * @version $Id: IndicatorAggregation.java 5280 2008-05-28 10:10:29Z larshelg $
 */
public class IndicatorAggregation
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private AggregationCache aggregationCache;

    public void setAggregationCache( AggregationCache aggregationCache )
    {
        this.aggregationCache = aggregationCache;
    }    
    
    private DataElementService dataElementService;
    
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }
    
    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }

    // -------------------------------------------------------------------------
    // Indicator aggregation
    // -------------------------------------------------------------------------
    
    public Double getAggregatedIndicatorValue( Indicator indicator, Date startDate, Date endDate,
        OrganisationUnit organisationUnit )
    {
        int days = daysBetween( startDate, endDate );

        double denominatorValue = calculateExpression( generateExpression( indicator.getDenominator(),
            startDate, endDate, organisationUnit, days ) );
        
        if ( denominatorValue == INVALID || denominatorValue == 0.0 )
        {
            return null;
        }
        
        double numeratorValue = calculateExpression( generateExpression( indicator.getNumerator(), startDate,
            endDate, organisationUnit, days ) );
        
        if ( numeratorValue == INVALID )
        {
            return null;
        }
        
        int factor = indicator.getIndicatorType().getFactor();

        double annualizationFactor = DateUtils.getAnnualizationFactor( indicator, startDate, endDate );
        
        double aggregatedValue = ( numeratorValue / denominatorValue ) * factor * annualizationFactor;

        return aggregatedValue;
    }

    public double getAggregatedNumeratorValue( Indicator indicator, Date startDate, Date endDate,
        OrganisationUnit organisationUnit )
    {
        return calculateExpression( generateExpression( indicator.getNumerator(), startDate,
            endDate, organisationUnit, daysBetween( startDate, endDate ) ) );
    }

    public double getAggregatedDenominatorValue( Indicator indicator, Date startDate, Date endDate,
        OrganisationUnit organisationUnit )
    {
        return calculateExpression( generateExpression( indicator.getDenominator(),
            startDate, endDate, organisationUnit, daysBetween( startDate, endDate ) ) );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    
    private String generateExpression( String expression, Date startDate, Date endDate, OrganisationUnit organisationUnit, int days )
    {
        Map<String, Double> constantMap = constantService.getConstantMap();
        
        Set<DataElementOperand> operands = expressionService.getOperandsInExpression( expression );
        
        Map<DataElementOperand, Double> valueMap = new HashMap<DataElementOperand, Double>();
        
        for ( DataElementOperand operand : operands )
        {
            DataElement dataElement = dataElementService.getDataElement( operand.getDataElementId() );
            DataElementCategoryOptionCombo optionCombo = !operand.isTotal() ? categoryService.getDataElementCategoryOptionCombo( operand.getOptionComboId() ) : null;

            valueMap.put( operand, aggregationCache.getAggregatedDataValue( dataElement, optionCombo, startDate, endDate, organisationUnit ) );            
        }
        
        return expressionService.generateExpression( expression, valueMap, constantMap, null, false );
    }
}
