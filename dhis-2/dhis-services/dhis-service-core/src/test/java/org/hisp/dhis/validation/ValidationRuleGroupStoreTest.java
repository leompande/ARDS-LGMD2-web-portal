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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.hisp.dhis.expression.Operator.equal_to;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.common.GenericIdentifiableObjectStore;
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
@SuppressWarnings( "unchecked" )
public class ValidationRuleGroupStoreTest
    extends DhisSpringTest
{
    private GenericIdentifiableObjectStore<ValidationRule> validationRuleStore;

    private GenericIdentifiableObjectStore<ValidationRuleGroup> validationRuleGroupStore;

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
        validationRuleStore = (GenericIdentifiableObjectStore<ValidationRule>) getBean( "org.hisp.dhis.validation.ValidationRuleStore" );

        validationRuleGroupStore = (GenericIdentifiableObjectStore<ValidationRuleGroup>) getBean( "org.hisp.dhis.validation.ValidationRuleGroupStore" );

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
    // ValidationRuleGroup
    // -------------------------------------------------------------------------

    @Test
    public void testAddValidationRuleGroup()
    {
        ValidationRule ruleA = createValidationRule( 'A', equal_to, null, null, periodType );
        ValidationRule ruleB = createValidationRule( 'B', equal_to, null, null, periodType );

        validationRuleStore.save( ruleA );
        validationRuleStore.save( ruleB );

        Set<ValidationRule> rules = new HashSet<ValidationRule>();

        rules.add( ruleA );
        rules.add( ruleB );

        ValidationRuleGroup groupA = createValidationRuleGroup( 'A' );
        ValidationRuleGroup groupB = createValidationRuleGroup( 'B' );

        groupA.setMembers( rules );
        groupB.setMembers( rules );

        int idA = validationRuleGroupStore.save( groupA );
        int idB = validationRuleGroupStore.save( groupB );

        assertEquals( groupA, validationRuleGroupStore.get( idA ) );
        assertEquals( groupB, validationRuleGroupStore.get( idB ) );
    }

    @Test
    public void testUpdateValidationRuleGroup()
    {
        ValidationRule ruleA = createValidationRule( 'A', equal_to, null, null, periodType );
        ValidationRule ruleB = createValidationRule( 'B', equal_to, null, null, periodType );

        validationRuleStore.save( ruleA );
        validationRuleStore.save( ruleB );

        Set<ValidationRule> rules = new HashSet<ValidationRule>();

        rules.add( ruleA );
        rules.add( ruleB );

        ValidationRuleGroup groupA = createValidationRuleGroup( 'A' );
        ValidationRuleGroup groupB = createValidationRuleGroup( 'B' );

        groupA.setMembers( rules );
        groupB.setMembers( rules );

        int idA = validationRuleGroupStore.save( groupA );
        int idB = validationRuleGroupStore.save( groupB );

        assertEquals( groupA, validationRuleGroupStore.get( idA ) );
        assertEquals( groupB, validationRuleGroupStore.get( idB ) );

        ruleA.setName( "UpdatedValidationRuleA" );
        ruleB.setName( "UpdatedValidationRuleB" );

        validationRuleGroupStore.update( groupA );
        validationRuleGroupStore.update( groupB );

        assertEquals( groupA, validationRuleGroupStore.get( idA ) );
        assertEquals( groupB, validationRuleGroupStore.get( idB ) );
    }

    @Test
    public void testDeleteValidationRuleGroup()
    {
        ValidationRule ruleA = createValidationRule( 'A', equal_to, null, null, periodType );
        ValidationRule ruleB = createValidationRule( 'B', equal_to, null, null, periodType );

        validationRuleStore.save( ruleA );
        validationRuleStore.save( ruleB );

        Set<ValidationRule> rules = new HashSet<ValidationRule>();

        rules.add( ruleA );
        rules.add( ruleB );

        ValidationRuleGroup groupA = createValidationRuleGroup( 'A' );
        ValidationRuleGroup groupB = createValidationRuleGroup( 'B' );

        groupA.setMembers( rules );
        groupB.setMembers( rules );

        int idA = validationRuleGroupStore.save( groupA );
        int idB = validationRuleGroupStore.save( groupB );

        assertNotNull( validationRuleGroupStore.get( idA ) );
        assertNotNull( validationRuleGroupStore.get( idB ) );

        validationRuleGroupStore.delete( groupA );

        assertNull( validationRuleGroupStore.get( idA ) );
        assertNotNull( validationRuleGroupStore.get( idB ) );

        validationRuleGroupStore.delete( groupB );

        assertNull( validationRuleGroupStore.get( idA ) );
        assertNull( validationRuleGroupStore.get( idB ) );
    }

    @Test
    public void testGetAllValidationRuleGroup()
    {
        ValidationRule ruleA = createValidationRule( 'A', equal_to, null, null, periodType );
        ValidationRule ruleB = createValidationRule( 'B', equal_to, null, null, periodType );

        validationRuleStore.save( ruleA );
        validationRuleStore.save( ruleB );

        Set<ValidationRule> rules = new HashSet<ValidationRule>();

        rules.add( ruleA );
        rules.add( ruleB );

        ValidationRuleGroup groupA = createValidationRuleGroup( 'A' );
        ValidationRuleGroup groupB = createValidationRuleGroup( 'B' );

        groupA.setMembers( rules );
        groupB.setMembers( rules );

        validationRuleGroupStore.save( groupA );
        validationRuleGroupStore.save( groupB );

        Collection<ValidationRuleGroup> groups = validationRuleGroupStore.getAll();

        assertEquals( 2, groups.size() );
        assertTrue( groups.contains( groupA ) );
        assertTrue( groups.contains( groupB ) );
    }

    @Test
    public void testGetValidationRuleGroupByName()
    {
        ValidationRule ruleA = createValidationRule( 'A', equal_to, null, null, periodType );
        ValidationRule ruleB = createValidationRule( 'B', equal_to, null, null, periodType );

        validationRuleStore.save( ruleA );
        validationRuleStore.save( ruleB );

        Set<ValidationRule> rules = new HashSet<ValidationRule>();

        rules.add( ruleA );
        rules.add( ruleB );

        ValidationRuleGroup groupA = createValidationRuleGroup( 'A' );
        ValidationRuleGroup groupB = createValidationRuleGroup( 'B' );

        groupA.setMembers( rules );
        groupB.setMembers( rules );

        validationRuleGroupStore.save( groupA );
        validationRuleGroupStore.save( groupB );

        ValidationRuleGroup groupByName = validationRuleGroupStore.getByName( groupA.getName() );

        assertEquals( groupA, groupByName );
    }
}
