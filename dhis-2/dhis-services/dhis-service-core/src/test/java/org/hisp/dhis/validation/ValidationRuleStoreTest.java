package org.hisp.dhis.validation;

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

import static org.hisp.dhis.expression.Operator.equal_to;
import static org.hisp.dhis.expression.Operator.greater_than;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.expression.Expression;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.period.PeriodType;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 */
public class ValidationRuleStoreTest
    extends DhisSpringTest
{
    private ValidationRuleStore validationRuleStore;

    private ExpressionService expressionService;

    private DataElement dataElementA;

    private DataElement dataElementB;

    private DataElement dataElementC;

    private DataElement dataElementD;

    private Set<DataElement> dataElements;

    private Set<DataElementCategoryOptionCombo> optionCombos;

    private Expression expressionA;

    private Expression expressionB;

    private PeriodType periodType;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
        throws Exception
    {
        validationRuleStore = (ValidationRuleStore) getBean( ValidationRuleStore.ID );

        dataElementService = (DataElementService) getBean( DataElementService.ID );

        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );

        expressionService = (ExpressionService) getBean( ExpressionService.ID );

        dataElementA = createDataElement( 'A' );
        dataElementB = createDataElement( 'B' );
        dataElementC = createDataElement( 'C' );
        dataElementD = createDataElement( 'D' );

        dataElementService.addDataElement( dataElementA );
        dataElementService.addDataElement( dataElementB );
        dataElementService.addDataElement( dataElementC );
        dataElementService.addDataElement( dataElementD );

        dataElements = new HashSet<DataElement>();

        dataElements.add( dataElementA );
        dataElements.add( dataElementB );
        dataElements.add( dataElementC );
        dataElements.add( dataElementD );

        DataElementCategoryCombo categoryCombo = categoryService
            .getDataElementCategoryComboByName( DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );
        DataElementCategoryOptionCombo categoryOptionCombo = categoryCombo.getOptionCombos().iterator().next();

        optionCombos = new HashSet<DataElementCategoryOptionCombo>();
        optionCombos.add( categoryOptionCombo );

        expressionA = new Expression( "expressionA", "descriptionA", dataElements, optionCombos );
        expressionB = new Expression( "expressionB", "descriptionB", dataElements, optionCombos );

        expressionService.addExpression( expressionB );
        expressionService.addExpression( expressionA );

        periodType = PeriodType.getAvailablePeriodTypes().iterator().next();
    }

    // -------------------------------------------------------------------------
    // ValidationRule
    // -------------------------------------------------------------------------

    @Test
    public void testSaveValidationRule()
    {
        ValidationRule validationRule = createValidationRule( 'A', equal_to, expressionA, expressionB, periodType );

        int id = validationRuleStore.save( validationRule );

        validationRule = validationRuleStore.get( id );

        assertEquals( validationRule.getName(), "ValidationRuleA" );
        assertEquals( validationRule.getDescription(), "DescriptionA" );
        assertEquals( validationRule.getType(), ValidationRule.TYPE_ABSOLUTE );
        assertEquals( validationRule.getOperator(), equal_to );
        assertNotNull( validationRule.getLeftSide().getExpression() );
        assertNotNull( validationRule.getRightSide().getExpression() );
        assertEquals( validationRule.getPeriodType(), periodType );
    }

    @Test
    public void testUpdateValidationRule()
    {
        ValidationRule validationRule = createValidationRule( 'A', equal_to, expressionA, expressionB, periodType );

        int id = validationRuleStore.save( validationRule );

        validationRule = validationRuleStore.get( id );

        assertEquals( validationRule.getName(), "ValidationRuleA" );
        assertEquals( validationRule.getDescription(), "DescriptionA" );
        assertEquals( validationRule.getType(), ValidationRule.TYPE_ABSOLUTE );
        assertEquals( validationRule.getOperator(), equal_to );

        validationRule.setName( "ValidationRuleB" );
        validationRule.setDescription( "DescriptionB" );
        validationRule.setType( ValidationRule.TYPE_STATISTICAL );
        validationRule.setOperator( greater_than );

        validationRuleStore.update( validationRule );

        validationRule = validationRuleStore.get( id );

        assertEquals( validationRule.getName(), "ValidationRuleB" );
        assertEquals( validationRule.getDescription(), "DescriptionB" );
        assertEquals( validationRule.getType(), ValidationRule.TYPE_STATISTICAL );
        assertEquals( validationRule.getOperator(), greater_than );
    }

    @Test
    public void testDeleteValidationRule()
    {
        ValidationRule validationRuleA = createValidationRule( 'A', equal_to, expressionA, expressionB, periodType );
        ValidationRule validationRuleB = createValidationRule( 'B', equal_to, expressionA, expressionB, periodType );

        int idA = validationRuleStore.save( validationRuleA );
        int idB = validationRuleStore.save( validationRuleB );

        assertNotNull( validationRuleStore.get( idA ) );
        assertNotNull( validationRuleStore.get( idB ) );

        validationRuleA.clearExpressions();

        validationRuleStore.delete( validationRuleA );

        assertNull( validationRuleStore.get( idA ) );
        assertNotNull( validationRuleStore.get( idB ) );

        validationRuleB.clearExpressions();

        validationRuleStore.delete( validationRuleB );

        assertNull( validationRuleStore.get( idA ) );
        assertNull( validationRuleStore.get( idB ) );
    }

    @Test
    public void testGetAllValidationRules()
    {
        ValidationRule validationRuleA = createValidationRule( 'A', equal_to, expressionA, expressionB, periodType );
        ValidationRule validationRuleB = createValidationRule( 'B', equal_to, expressionA, expressionB, periodType );

        validationRuleStore.save( validationRuleA );
        validationRuleStore.save( validationRuleB );

        Collection<ValidationRule> rules = validationRuleStore.getAll();

        assertTrue( rules.size() == 2 );
        assertTrue( rules.contains( validationRuleA ) );
        assertTrue( rules.contains( validationRuleB ) );
    }

    @Test
    public void testGetValidationRuleByName()
    {
        ValidationRule validationRuleA = createValidationRule( 'A', equal_to, expressionA, expressionB, periodType );
        ValidationRule validationRuleB = createValidationRule( 'B', equal_to, expressionA, expressionB, periodType );

        int id = validationRuleStore.save( validationRuleA );
        validationRuleStore.save( validationRuleB );

        ValidationRule rule = validationRuleStore.getByName( "ValidationRuleA" );

        assertEquals( rule.getId(), id );
        assertEquals( rule.getName(), "ValidationRuleA" );
    }

    @Test
    public void testGetValidationRuleCount()
    {
        Set<DataElement> dataElementsA = new HashSet<DataElement>();
        dataElementsA.add( dataElementA );
        dataElementsA.add( dataElementB );

        Set<DataElement> dataElementsB = new HashSet<DataElement>();
        dataElementsB.add( dataElementC );
        dataElementsB.add( dataElementD );

        Set<DataElement> dataElementsC = new HashSet<DataElement>();

        Set<DataElement> dataElementsD = new HashSet<DataElement>();
        dataElementsD.addAll( dataElementsA );
        dataElementsD.addAll( dataElementsB );

        Expression expression1 = new Expression( "Expression1", "Expression1", dataElementsA, optionCombos );
        Expression expression2 = new Expression( "Expression2", "Expression2", dataElementsB, optionCombos );
        Expression expression3 = new Expression( "Expression3", "Expression3", dataElementsC, optionCombos );

        expressionService.addExpression( expression1 );
        expressionService.addExpression( expression2 );
        expressionService.addExpression( expression3 );

        ValidationRule ruleA = createValidationRule( 'A', equal_to, expression1, expression3, periodType );
        ValidationRule ruleB = createValidationRule( 'B', equal_to, expression2, expression3, periodType );
        ValidationRule ruleC = createValidationRule( 'C', equal_to, expression3, expression3, periodType );

        validationRuleStore.save( ruleA );
        validationRuleStore.save( ruleB );
        validationRuleStore.save( ruleC );

        assertNotNull( validationRuleStore.getCount() );
        assertEquals( 3, validationRuleStore.getCount() );
    }
}
