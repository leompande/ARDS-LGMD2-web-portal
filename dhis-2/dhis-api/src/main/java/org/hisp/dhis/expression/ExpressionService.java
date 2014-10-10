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

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.period.Period;

/**
 * Expressions are mathematical formulas and can contain references to various
 * elements.
 * 
 * - Data element operands on the form #{dataelementuid.categoryoptioncombouid}
 * - Data element totals on the form #{dataelementuid}
 * - Constants on the form C{constantuid}
 * - Days in aggregation period as the symbol D{}
 *
 * @author Margrethe Store
 * @author Lars Helge Overland
 * @version $Id: ExpressionService.java 6461 2008-11-24 11:32:37Z larshelg $
 */
public interface ExpressionService
{
    String ID = ExpressionService.class.getName();
    
    final String VALID = "valid";
    final String EXPRESSION_IS_EMPTY = "expression_is_empty";
    final String DATAELEMENT_DOES_NOT_EXIST = "data_element_does_not_exist";
    final String CATEGORYOPTIONCOMBO_DOES_NOT_EXIST = "category_option_combo_does_not_exist";
    final String CONSTANT_DOES_NOT_EXIST = "constant_does_not_exist";
    final String EXPRESSION_NOT_WELL_FORMED = "expression_not_well_formed";

    final String DAYS_DESCRIPTION = "[Number of days]";
    final String NULL_REPLACEMENT = "0";
    final String SPACE = " ";

    final String OPERAND_EXPRESSION = "#\\{(\\w+)\\.?(\\w*)\\}";
    final String OPERAND_UID_EXPRESSION = "(\\w+)\\.?(\\w*)";
    final String DATA_ELEMENT_TOTAL_EXPRESSION = "#\\{(\\w+)\\}";
    final String CONSTANT_EXPRESSION = "C\\{(\\w+)\\}";
    final String DAYS_EXPRESSION = "\\[days\\]";

    final Pattern OPERAND_PATTERN = Pattern.compile( OPERAND_EXPRESSION );
    final Pattern OPERAND_UID_PATTERN = Pattern.compile( OPERAND_UID_EXPRESSION );
    final Pattern DATA_ELEMENT_TOTAL_PATTERN = Pattern.compile( DATA_ELEMENT_TOTAL_EXPRESSION );
    final Pattern CONSTANT_PATTERN = Pattern.compile( CONSTANT_EXPRESSION );
    final Pattern DAYS_PATTERN = Pattern.compile( DAYS_EXPRESSION );

    final String DAYS_SYMBOL = "[days]";
    
    /**
     * Adds a new Expression to the database.
     *
     * @param expression The Expression to add.
     * @return The generated identifier for this Expression.
     */
    int addExpression( Expression expression );

    /**
     * Updates an Expression.
     *
     * @param expression The Expression to update.
     */
    void updateExpression( Expression expression );

    /**
     * Deletes an Expression from the database.
     *
     * @param id Identifier of the Expression to delete.
     */
    void deleteExpression( Expression expression );

    /**
     * Get the Expression with the given identifier.
     *
     * @param id The identifier.
     * @return an Expression with the given identifier.
     */
    Expression getExpression( int id );

    /**
     * Gets all Expressions.
     *
     * @return A collection with all Expressions.
     */
    Collection<Expression> getAllExpressions();
    
    Double getIndicatorValue( Indicator indicator, Period period, Map<DataElementOperand, Double> valueMap, 
        Map<String, Double> constantMap, Integer days );
    
    /**
     * Generates the calculated value for the given expression base on the values
     * supplied in the value map, constant map and days.
     * 
     * @param expression the expression which holds the formula for the calculation.
     * @param valueMap the mapping between data element operands and values to
     *        use in the calculation.
     * @param constantMap the mapping between the constant uid and value to use
     *        in the calculation.
     * @param days the number of days to use in the calculation.
     * @return the calculated value as a double.
     */
    Double getExpressionValue( Expression expression, Map<DataElementOperand, Double> valueMap, 
        Map<String, Double> constantMap, Integer days );
    
    /**
     * Generates the calculated value for the given expression base on the values
     * supplied in the value map, constant map and days.
     * 
     * @param expression the expression which holds the formula for the calculation.
     * @param valueMap the mapping between data element operands and values to
     *        use in the calculation.
     * @param constantMap the mapping between the constant uid and value to use
     *        in the calculation.
     * @param days the number of days to use in the calculation.
     * @param set of data element operands that have values but they are incomplete
     *        (for example due to aggregation from organisationUnit children where
     *        not all children had a value.)
     * @return the calculated value as a double.
     */
    Double getExpressionValue( Expression expression, Map<DataElementOperand, Double> valueMap, 
        Map<String, Double> constantMap, Integer days, Set<DataElementOperand> incompleteValues );
    
    /**
     * Returns the uids of the data element totals in the given expression.
     * 
     * @param expression the expression.
     * @return a set of data element uids.
     */
    Set<String> getDataElementTotalUids( String expression );
    
    /**
     * Returns all DataElements included in the given expression string.
     * 
     * @param expression the expression string.
     * @return a Set of DataElements included in the expression string.
     */
    Set<DataElement> getDataElementsInExpression( String expression );
    
    /**
     * Returns all CategoryOptionCombos in the given expression string.
     * 
     * @param expression the expression string.
     * @return a Set of CategoryOptionCombos included in the expression string.
     */
    Set<DataElementCategoryOptionCombo> getOptionCombosInExpression( String expression );
    
    /**
     * Returns all operands included in an expression string. The operand is on
     * the form <data element id>.<category option combo id>.
     * 
     * @param expression The expression string.
     * @return A Set of Operands.
     */
    Set<DataElementOperand> getOperandsInExpression( String expression );
    
    /**
     * Returns all data elements which are present in the numerator and denominator
     * of the given indicators.
     * 
     * @param indicators the collection of indicators.
     * @return a set of data elements.
     */
    Set<DataElement> getDataElementsInIndicators( Collection<Indicator> indicators );
    
    /**
     * Filters indicators from the given collection where the numerator and /
     * or the denominator are invalid.
     *  
     * @param indicators collection of Indicators.
     */
    void filterInvalidIndicators( Collection<Indicator> indicators );
    
    /**
     * Tests whether the expression is valid. Returns a positive value if the
     * expression is valid, or a negative value if not.
     * 
     * @param formula the expression formula.
     * @return VALID if the expression is valid.
     * 	       EXPRESSION_IS_EMPTY if the expression is empty.
     * 	       DATAELEMENT_DOES_NOT_EXIST if the data element does not exist.
     *         CATEGORYOPTIONCOMBO_DOES_NOT_EXIST if the category option combo does not exist.
     *         CONSTANT_DOES_NOT_EXIST if the constant does not exist.
     *         EXPRESSION_NOT_WELL_FORMED if the expression is not well-formed.
     */
    String expressionIsValid( String formula );

    /**
     * Tests whether the expression is valid. Returns a positive value if the
     * expression is valid, or a negative value if not.
     * 
     * @param formula the expression formula.
     * @return VALID if the expression is valid.
     *         EXPRESSION_IS_EMPTY if the expression is empty.
     *         DATAELEMENT_DOES_NOT_EXIST if the data element does not exist.
     *         CATEGORYOPTIONCOMBO_DOES_NOT_EXIST if the category option combo does not exist.
     *         CONSTANT_DOES_NOT_EXIST if the constant does not exist.
     *         EXPRESSION_NOT_WELL_FORMED if the expression is not well-formed.
     */
    String expressionIsValid( String formula, Set<String> dataElements, Set<String> categoryOptionCombos, Set<String> constants );
    
    /**
     * Creates an expression string containing DataElement names and the names of
     * the CategoryOptions in the CategoryOptionCombo from a string consisting
     * of identifiers.
     * 
     * @param expression The expression string.
     * @return An expression string containing DataElement names and the names of
     *         the CategoryOptions in the CategoryOptionCombo.
     * @throws IllegalArgumentException if data element id or category option combo
     * 		   id are not numeric or data element or category option combo do not exist.
     */
    String getExpressionDescription( String expression );

    /**
     * Substitutes potential constant and days in the numerator and denominator
     * on all indicators in the given collection.
     *  
     * Populates the explodedNumerator and explodedDenominator property on all
     * indicators in the given collection. This method uses
     * explodeExpression( String ) internally to generate the exploded expressions.
     * This method will perform better compared to calling explodeExpression( String )
     * multiple times outside a transactional context as the transactional
     * overhead is avoided.
     * 
     * @param indicators the collection of indicators.
     * @param days the number of days in aggregation period.
     */
    void explodeAndSubstituteExpressions( Collection<Indicator> indicators, Integer days );

    /**
     * Populates the explodedNumerator and explodedDenominator property on all
     * indicators in the given collection. This method uses
     * explodeExpression( String ) internally to generate the exploded expressions.
     * 
     * @param indicators the collection of indicators.
     */    
    void explodeExpressions( Collection<Indicator> indicators );
    
    /**
     * Replaces references to data element totals with references to all
     * category option combos in the category combo for that data element.
     * 
     * @param expression the expression to explode.
     * @return the exploded expression string.
     */
    String explodeExpression( String expression );
    
    /**
     * Substitutes potential constants and days in the given expression.
     * 
     * @param expression the expression to operate on.
     * @param days the number of days to substitute for potential days in the
     *        expression, 0 if null
     * @return the substituted expression.
     */
    String substituteExpression( String expression, Integer days );
    
    /**
     * Generates an expression where the Operand identifiers, consisting of 
     * data element id and category option combo id, are replaced
     * by the aggregated value for the relevant combination of data element,
     * period, and source.
     * 
     * @param formula The formula to parse.
     * @param valueMap The map containing data element identifiers and aggregated value.
     * @param days The number to be substituted with the days expression in the formula.
     */
    String generateExpression( String expression, Map<DataElementOperand, Double> valueMap, Map<String, Double> constantMap, Integer days, boolean nullIfNoValues );
    
    /**
     * Returns all Operands included in the formulas for the given collection of
     * Indicators. Requires that the explodedNumerator and explodedDenominator
     * properties have been populated.
     * 
     * @param indicators the collection of Indicators.
     */
    Set<DataElementOperand> getOperandsInIndicators( Collection<Indicator> indicators );
}
