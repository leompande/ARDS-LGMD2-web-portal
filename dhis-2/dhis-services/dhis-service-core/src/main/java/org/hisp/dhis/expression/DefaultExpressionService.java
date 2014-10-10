package org.hisp.dhis.expression;

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

import static org.hisp.dhis.expression.Expression.EXP_CLOSE;
import static org.hisp.dhis.expression.Expression.EXP_OPEN;
import static org.hisp.dhis.expression.Expression.PAR_CLOSE;
import static org.hisp.dhis.expression.Expression.PAR_OPEN;
import static org.hisp.dhis.expression.Expression.SEPARATOR;
import static org.hisp.dhis.system.util.MathUtils.calculateExpression;
import static org.hisp.dhis.system.util.MathUtils.isEqual;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.GenericStore;
import org.hisp.dhis.common.ListMap;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.MathUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * The expression is a string describing a formula containing data element ids
 * and category option combo ids. The formula can potentially contain references
 * to data element totals.
 * 
 * @author Margrethe Store
 * @author Lars Helge Overland
 */
public class DefaultExpressionService
    implements ExpressionService
{
    private static final Log log = LogFactory.getLog( DefaultExpressionService.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericStore<Expression> expressionStore;

    public void setExpressionStore( GenericStore<Expression> expressionStore )
    {
        this.expressionStore = expressionStore;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    // -------------------------------------------------------------------------
    // Expression CRUD operations
    // -------------------------------------------------------------------------

    @Transactional
    public int addExpression( Expression expression )
    {
        return expressionStore.save( expression );
    }

    @Transactional
    public void deleteExpression( Expression expression )
    {
        expressionStore.delete( expression );
    }

    @Transactional
    public Expression getExpression( int id )
    {
        return expressionStore.get( id );
    }

    @Transactional
    public void updateExpression( Expression expression )
    {
        expressionStore.update( expression );
    }

    @Transactional
    public Collection<Expression> getAllExpressions()
    {
        return expressionStore.getAll();
    }

    // -------------------------------------------------------------------------
    // Business logic
    // -------------------------------------------------------------------------
    
    public Double getIndicatorValue( Indicator indicator, Period period, Map<DataElementOperand, Double> valueMap, 
        Map<String, Double> constantMap, Integer days )
    {
        if ( indicator == null || indicator.getExplodedNumeratorFallback() == null || indicator.getExplodedDenominatorFallback() == null )
        {
            return null;
        }
        
        final String denominatorExpression = generateExpression( indicator.getExplodedDenominatorFallback(), valueMap, constantMap, days, false );
        
        if ( denominatorExpression == null )
        {
            return null;
        }
        
        final double denominatorValue = calculateExpression( denominatorExpression );
        
        if ( !isEqual( denominatorValue, 0d ) )
        {
            final String numeratorExpression = generateExpression( indicator.getExplodedNumeratorFallback(), valueMap, constantMap, days, false );
            
            if ( numeratorExpression == null )
            {
                return null;
            }
            
            final double numeratorValue = calculateExpression( numeratorExpression );
            
            final double annualizationFactor = period != null ? DateUtils.getAnnualizationFactor( indicator, period.getStartDate(), period.getEndDate() ) : 1d;
            final double factor = indicator.getIndicatorType().getFactor();
            final double aggregatedValue = ( numeratorValue / denominatorValue ) * factor * annualizationFactor;
            
            return aggregatedValue;
        }
        
        return null;
    }
    
    public Double getExpressionValue( Expression expression, Map<DataElementOperand, Double> valueMap, 
            Map<String, Double> constantMap, Integer days )
        {
            final String expressionString = generateExpression( expression.getExpression(), valueMap, constantMap, days, expression.isNullIfBlank() );

            return expressionString != null ? calculateExpression( expressionString ) : null;
        }

    public Double getExpressionValue( Expression expression, Map<DataElementOperand, Double> valueMap, 
            Map<String, Double> constantMap, Integer days, Set<DataElementOperand> incompleteValues )
        {
            final String expressionString = generateExpression( expression.getExpression(), valueMap, constantMap, days, expression.isNullIfBlank(), incompleteValues );

            return expressionString != null ? calculateExpression( expressionString ) : null;
        }

    @Transactional
    public Set<DataElement> getDataElementsInExpression( String expression )
    {
        Set<DataElement> dataElementsInExpression = null;

        if ( expression != null )
        {
            dataElementsInExpression = new HashSet<DataElement>();

            final Matcher matcher = OPERAND_PATTERN.matcher( expression );

            while ( matcher.find() )
            {
                final DataElement dataElement = dataElementService.getDataElement( matcher.group( 1 ) );

                if ( dataElement != null )
                {
                    dataElementsInExpression.add( dataElement );
                }
            }
        }

        return dataElementsInExpression;
    }
    
    public Set<String> getDataElementTotalUids( String expression )
    {
        Set<String> uids = new HashSet<String>();
        
        if ( expression != null )
        {
            final Matcher matcher = DATA_ELEMENT_TOTAL_PATTERN.matcher( expression );
            
            while ( matcher.find() )
            {
                uids.add( matcher.group( 1 ) );
            }
        }
        
        return uids;
    }
    
    @Transactional
    public Set<DataElementCategoryOptionCombo> getOptionCombosInExpression( String expression )
    {
        Set<DataElementCategoryOptionCombo> optionCombosInExpression = null;

        if ( expression != null )
        {
            optionCombosInExpression = new HashSet<DataElementCategoryOptionCombo>();

            final Matcher matcher = OPERAND_PATTERN.matcher( expression );

            while ( matcher.find() )
            {
                DataElementCategoryOptionCombo categoryOptionCombo = categoryService.
                    getDataElementCategoryOptionCombo( matcher.group( 2 ) );

                if ( categoryOptionCombo != null )
                {
                    optionCombosInExpression.add( categoryOptionCombo );
                }
            }
        }

        return optionCombosInExpression;
    }

    @Transactional
    public Set<DataElementOperand> getOperandsInExpression( String expression )
    {
        Set<DataElementOperand> operandsInExpression = null;

        if ( expression != null )
        {
            operandsInExpression = new HashSet<DataElementOperand>();

            final Matcher matcher = OPERAND_PATTERN.matcher( expression );

            while ( matcher.find() )
            {
                operandsInExpression.add( DataElementOperand.getOperand( matcher.group() ) );
            }
        }

        return operandsInExpression;
    }

    @Transactional
    public Set<DataElement> getDataElementsInIndicators( Collection<Indicator> indicators )
    {
        Set<DataElement> dataElements = new HashSet<DataElement>();
        
        for ( Indicator indicator : indicators )
        {
            Set<DataElement> numerator = getDataElementsInExpression( indicator.getNumerator() );
            Set<DataElement> denominator = getDataElementsInExpression( indicator.getDenominator() );
            
            if ( numerator != null )
            {
                dataElements.addAll( numerator );
            }
            
            if ( denominator != null )
            {
                dataElements.addAll( denominator );
            }
        }
        
        return dataElements;
    }

    @Transactional
    public void filterInvalidIndicators( Collection<Indicator> indicators )
    {
        if ( indicators != null )
        {
            Iterator<Indicator> iterator = indicators.iterator();
            
            while ( iterator.hasNext() )
            {
                Indicator indicator = iterator.next();
                
                if ( !expressionIsValid( indicator.getNumerator() ).equals( VALID ) ||
                    !expressionIsValid( indicator.getDenominator() ).equals( VALID ) )
                {
                    iterator.remove();
                    log.warn( "Indicator is invalid: " + indicator + ", " + indicator.getNumerator() + ", " + indicator.getDenominator() );
                }
            }
        }
    }

    @Transactional
    public String expressionIsValid( String formula )
    {
        return expressionIsValid( formula, null, null, null );
    }

    @Transactional
    public String expressionIsValid( String expression, Set<String> dataElements, Set<String> categoryOptionCombos, Set<String> constants )
    {
        if ( expression == null || expression.isEmpty() )
        {
            return EXPRESSION_IS_EMPTY;
        }

        // ---------------------------------------------------------------------
        // Operands
        // ---------------------------------------------------------------------
        
        StringBuffer sb = new StringBuffer();
        Matcher matcher = OPERAND_PATTERN.matcher( expression );

        while ( matcher.find() )
        {
            String de = matcher.group( 1 );
            String coc = matcher.group( 2 );
            
            if ( dataElements != null ? !dataElements.contains( de ) : dataElementService.getDataElement( de ) == null )
            {
                return DATAELEMENT_DOES_NOT_EXIST;
            }

            if ( !operandIsTotal( matcher ) && ( 
                categoryOptionCombos != null ? !categoryOptionCombos.contains( coc ) : categoryService.getDataElementCategoryOptionCombo( coc ) == null ) )
            {
                return CATEGORYOPTIONCOMBO_DOES_NOT_EXIST;
            }
                    
            matcher.appendReplacement( sb, "1.1" );
        }
        
        expression = appendTail( matcher, sb );

        // ---------------------------------------------------------------------
        // Constants
        // ---------------------------------------------------------------------
        
        matcher = CONSTANT_PATTERN.matcher( expression );
        sb = new StringBuffer();
        
        while ( matcher.find() )
        {
            String constant = matcher.group( 1 );
            
            if ( constants != null ? !constants.contains( constant ) : constantService.getConstant( constant ) == null )
            {
                return CONSTANT_DOES_NOT_EXIST;
            }
            
            matcher.appendReplacement( sb, "1.1" );
        }

        expression = appendTail( matcher, sb );

        expression = expression.replaceAll( DAYS_EXPRESSION, "1.1" );
        
        // ---------------------------------------------------------------------
        // Well-formed expression
        // ---------------------------------------------------------------------

        if ( MathUtils.expressionHasErrors( expression ) )
        {
            return EXPRESSION_NOT_WELL_FORMED;
        }

        return VALID;
    }

    @Transactional
    public String getExpressionDescription( String expression )
    {
        if ( expression == null || expression.isEmpty() )
        {
            return null;
        }

        // ---------------------------------------------------------------------
        // Operands
        // ---------------------------------------------------------------------
        
        StringBuffer sb = new StringBuffer();
        Matcher matcher = OPERAND_PATTERN.matcher( expression );
        
        while ( matcher.find() )
        {
            String de = matcher.group( 1 );
            String coc = matcher.group( 2 );
            
            DataElement dataElement = dataElementService.getDataElement( de );
            DataElementCategoryOptionCombo categoryOptionCombo = categoryService.getDataElementCategoryOptionCombo( coc );
            
            if ( dataElement == null )
            {
                throw new IllegalArgumentException( "Identifier does not reference a data element: " + de );
            }

            if ( !operandIsTotal( matcher ) && categoryOptionCombo == null )
            {
                throw new IllegalArgumentException( "Identifier does not reference a category option combo: " + coc );
            }
            
            matcher.appendReplacement( sb, DataElementOperand.getPrettyName( dataElement, categoryOptionCombo ) );
        }
        
        expression = appendTail( matcher, sb );
        
        // ---------------------------------------------------------------------
        // Constants
        // ---------------------------------------------------------------------

        sb = new StringBuffer();
        matcher = CONSTANT_PATTERN.matcher( expression );
        
        while ( matcher.find() )
        {
            String co = matcher.group( 1 );
            
            Constant constant = constantService.getConstant( co );
            
            if ( constant == null )
            {
                throw new IllegalArgumentException( "Identifier does not reference a constant: " + co );
            }
            
            matcher.appendReplacement( sb, constant.getDisplayName() );
        }

        expression = appendTail( matcher, sb );

        // ---------------------------------------------------------------------
        // Days
        // ---------------------------------------------------------------------

        sb = new StringBuffer();
        matcher = DAYS_PATTERN.matcher( expression );
        
        while ( matcher.find() )
        {
            matcher.appendReplacement( sb, DAYS_DESCRIPTION );
        }

        expression = appendTail( matcher, sb );

        return expression;
    }

    @Transactional
    public void explodeAndSubstituteExpressions( Collection<Indicator> indicators, Integer days )
    {
        if ( indicators != null && !indicators.isEmpty() )
        {
            for ( Indicator indicator : indicators )
            {
                indicator.setExplodedNumerator( substituteExpression( indicator.getNumerator(), days ) );
                indicator.setExplodedDenominator( substituteExpression( indicator.getDenominator(), days ) );
            }

            explodeExpressions( indicators );
        }
    }

    @Transactional
    public void explodeExpressions( Collection<Indicator> indicators )
    {
        if ( indicators != null && !indicators.isEmpty() )
        {
            Set<String> dataElementTotals = new HashSet<String>();
            
            for ( Indicator indicator : indicators )
            {
                dataElementTotals.addAll( getDataElementTotalUids( indicator.getNumerator() ) );
                dataElementTotals.addAll( getDataElementTotalUids( indicator.getDenominator() ) );
            }
            
            if ( !dataElementTotals.isEmpty() )
            {
                final ListMap<String, String> dataElementMap = dataElementService.getDataElementCategoryOptionComboMap( dataElementTotals );
                
                if ( !dataElementMap.isEmpty() )
                {
                    for ( Indicator indicator : indicators )
                    {
                        indicator.setExplodedNumerator( explodeExpression( indicator.getExplodedNumeratorFallback(), dataElementMap ) );
                        indicator.setExplodedDenominator( explodeExpression( indicator.getExplodedDenominatorFallback(), dataElementMap ) );
                    }
                }
            }
        }
    }
    
    private String explodeExpression( String expression, ListMap<String, String> dataElementOptionComboMap )
    {
        if ( expression == null || expression.isEmpty() )
        {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        Matcher matcher = OPERAND_PATTERN.matcher( expression );

        while ( matcher.find() )
        {
            if ( operandIsTotal( matcher ) )
            {
                final StringBuilder replace = new StringBuilder( PAR_OPEN );

                String de = matcher.group( 1 );
                
                List<String> cocs = dataElementOptionComboMap.get( de );
                
                for ( String coc : cocs )
                {
                    replace.append( EXP_OPEN ).append( matcher.group( 1 ) ).append( SEPARATOR ).append(
                        coc ).append( EXP_CLOSE ).append( "+" );
                }

                replace.deleteCharAt( replace.length() - 1 ).append( PAR_CLOSE );
                matcher.appendReplacement( sb, replace.toString() );
            }
        }

        return appendTail( matcher, sb );
    }

    @Transactional
    public String explodeExpression( String expression )
    {
        if ( expression == null || expression.isEmpty() )
        {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        Matcher matcher = OPERAND_PATTERN.matcher( expression );

        while ( matcher.find() )
        {
            if ( operandIsTotal( matcher ) )
            {
                final StringBuilder replace = new StringBuilder( PAR_OPEN );

                final DataElement dataElement = dataElementService.getDataElement( matcher.group( 1 ) );

                final DataElementCategoryCombo categoryCombo = dataElement.getCategoryCombo();

                for ( DataElementCategoryOptionCombo categoryOptionCombo : categoryCombo.getOptionCombos() )
                {
                    replace.append( EXP_OPEN ).append( dataElement.getUid() ).append( SEPARATOR ).append(
                        categoryOptionCombo.getUid() ).append( EXP_CLOSE ).append( "+" );
                }

                replace.deleteCharAt( replace.length() - 1 ).append( PAR_CLOSE );
                matcher.appendReplacement( sb, replace.toString() );
            }
        }

        return appendTail( matcher, sb );
    }

    @Transactional
    public String substituteExpression( String expression, Integer days )
    {
        if ( expression == null || expression.isEmpty() )
        {
            return null;
        }

        // ---------------------------------------------------------------------
        // Constants
        // ---------------------------------------------------------------------

        StringBuffer sb = new StringBuffer();        
        Matcher matcher = CONSTANT_PATTERN.matcher( expression );
        
        while ( matcher.find() )
        {
            String co = matcher.group( 1 );
            
            Constant constant = constantService.getConstant( co );
            
            String replacement = constant != null ? String.valueOf( constant.getValue() ) : NULL_REPLACEMENT; 
            
            matcher.appendReplacement( sb, replacement );
        }

        expression = appendTail( matcher, sb );
        
        // ---------------------------------------------------------------------
        // Days
        // ---------------------------------------------------------------------

        sb = new StringBuffer();
        matcher = DAYS_PATTERN.matcher( expression );
        
        while ( matcher.find() )
        {            
            String replacement = days != null ? String.valueOf( days ) : NULL_REPLACEMENT;
            
            matcher.appendReplacement( sb, replacement );
        }
        
        return appendTail( matcher, sb );
    }

    @Transactional
    public String generateExpression( String expression, Map<DataElementOperand, Double> valueMap, Map<String, Double> constantMap, Integer days, boolean nullIfNoValues )
    {
    	return generateExpression( expression, valueMap, constantMap, days, nullIfNoValues, null );
    }

    private String generateExpression( String expression, Map<DataElementOperand, Double> valueMap, Map<String, Double> constantMap, Integer days, boolean nullIfNoValues,
    		Set<DataElementOperand> incompleteValues )
    {
        if ( expression == null || expression.isEmpty() )
        {
            return null;
        }
        
        // ---------------------------------------------------------------------
        // Operands
        // ---------------------------------------------------------------------
        
        StringBuffer sb = new StringBuffer();
        Matcher matcher = OPERAND_PATTERN.matcher( expression );
        
        while ( matcher.find() )
        {
            DataElementOperand operand = DataElementOperand.getOperand( matcher.group() );

            final Double value = valueMap.get( operand );
            
            if ( nullIfNoValues && ( value == null || ( incompleteValues != null && incompleteValues.contains( operand ) ) ) )
            {
                return null;
            }

            String replacement = value != null ? String.valueOf( value ) : NULL_REPLACEMENT;
            
            matcher.appendReplacement( sb, replacement );
        }
        
        expression = appendTail( matcher, sb );
        
        // ---------------------------------------------------------------------
        // Constants
        // ---------------------------------------------------------------------
        
        sb = new StringBuffer();
        matcher = CONSTANT_PATTERN.matcher( expression );
        
        while ( matcher.find() )
        {
            final Double constant = constantMap.get( matcher.group( 1 ) );
            
            String replacement = constant != null ? String.valueOf( constant ) : NULL_REPLACEMENT;
            
            matcher.appendReplacement( sb, replacement );
        }
        
        expression = appendTail( matcher, sb );
        
        // ---------------------------------------------------------------------
        // Days
        // ---------------------------------------------------------------------
        
        sb = new StringBuffer();
        matcher = DAYS_PATTERN.matcher( expression );
        
        while ( matcher.find() )
        {
            String replacement = days != null ? String.valueOf( days ) : NULL_REPLACEMENT;
            
            matcher.appendReplacement( sb, replacement );
        }
        
        return appendTail( matcher, sb );
    }

    @Transactional
    public Set<DataElementOperand> getOperandsInIndicators( Collection<Indicator> indicators )
    {
        final Set<DataElementOperand> operands = new HashSet<DataElementOperand>();
        
        for ( Indicator indicator : indicators )
        {
            Set<DataElementOperand> temp = getOperandsInExpression( indicator.getExplodedNumerator() );
            operands.addAll( temp != null ? temp : new HashSet<DataElementOperand>() );
            
            temp = getOperandsInExpression( indicator.getExplodedDenominator() );            
            operands.addAll( temp != null ? temp : new HashSet<DataElementOperand>() );
        }
        
        return operands;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    
    private String appendTail( Matcher matcher, StringBuffer sb )
    {
        matcher.appendTail( sb );
        return sb.toString();
    }
    
    private boolean operandIsTotal( Matcher matcher )
    {
        return matcher != null && StringUtils.trimToEmpty( matcher.group( 2 ) ).isEmpty();
    }
}
