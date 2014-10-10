package org.hisp.dhis.linelisting;

/*
 * Copyright (c) 2004-2007, University of Oslo
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultLineListValidationRuleService
    implements LineListValidationRuleService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LineListValidationRuleStore lineListValidationRuleStore;

    public void setLineListValidationRuleStore( LineListValidationRuleStore lineListValidationRuleStore )
    {
        this.lineListValidationRuleStore = lineListValidationRuleStore;
    }

    // -------------------------------------------------------------------------
    // LineListValidationRule business logic
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    /**
     * Validates a collection of validation rules.
     * 
     * @param period the period to validate for.
     * @param source the source to validate for.
     * @param LineListValidationRules the rules to validate.
     * @returns a collection of rules that did not pass validation.
     */
    private Collection<ValidationResult> validate( final Collection<LineListValidationRule> LineListValidationRules )
    {
        final Collection<ValidationResult> validationResults = new HashSet<ValidationResult>();

        Double leftSide = null;
        Double rightSide = null;

        boolean violation = false;

        for ( final LineListValidationRule LineListValidationRule : LineListValidationRules )
        {
            /*
             * leftSide = expressionService.getExpressionValue(
             * LineListValidationRule.getLeftSide(), period, source ); rightSide
             * = expressionService.getExpressionValue(
             * LineListValidationRule.getRightSide(), period, source );
             * 
             * if ( leftSide != null && rightSide != null ) { violation =
             * !expressionIsTrue( leftSide,
             * LineListValidationRule.getMathematicalOperator(), rightSide );
             * 
             * if ( violation ) { validationResults.add( new ValidationResult(
             * period, source, LineListValidationRule, leftSide, rightSide ) );
             * } }
             */
        }

        return validationResults;
    }

    /**
     * Returns all validation rules which have data elements assigned to it
     * which are members of the given data set.
     * 
     * @param dataSet the data set.
     * @return all validation rules which have data elements assigned to it
     *         which are members of the given data set.
     */
    private Collection<LineListValidationRule> getRelevantLineListValidationRules( final LineListGroup group )
    {
        final Collection<LineListValidationRule> rules = lineListValidationRuleStore.getAllLineListValidationRules();

        return getRelevantLineListValidationRules( group, rules );
    }

    /**
     * Returns all validation rules which have data elements assigned to it
     * which are members of the given data set.
     * 
     * @param dataSet the data set.
     * @param LineListValidationRules the validation rules.
     * @return all validation rules which have data elements assigned to it
     *         which are members of the given data set.
     */
    private Collection<LineListValidationRule> getRelevantLineListValidationRules( final LineListGroup group,
        final Collection<LineListValidationRule> LineListValidationRules )
    {
        final Collection<LineListValidationRule> relevantLineListValidationRules = new HashSet<LineListValidationRule>();

        for ( final LineListValidationRule LineListValidationRule : LineListValidationRules )
        {
            for ( final LineListElement llElement : group.getLineListElements() )
            {
                /*
                 * if (
                 * LineListValidationRule.getLeftSide().getDataElementsInExpression
                 * ().contains( llElement ) ||
                 * LineListValidationRule.getRightSide
                 * ().getDataElementsInExpression().contains( llElement ) ) {
                 * relevantLineListValidationRules.add( LineListValidationRule
                 * ); }
                 */
            }
        }

        return relevantLineListValidationRules;
    }

    // -------------------------------------------------------------------------
    // LineListValidationRule CRUD operations
    // -------------------------------------------------------------------------
    public int addLineListValidationRule( LineListValidationRule LineListValidationRule )
    {
        return lineListValidationRuleStore.addLineListValidationRule( LineListValidationRule );
    }

    public void deleteLineListValidationRule( LineListValidationRule LineListValidationRule )
    {
        lineListValidationRuleStore.deleteLineListValidationRule( LineListValidationRule );
    }

    public Collection<LineListValidationRule> getAllLineListValidationRules()
    {
        return lineListValidationRuleStore.getAllLineListValidationRules();
    }

    public LineListValidationRule getLineListValidationRule( int id )
    {
        return lineListValidationRuleStore.getLineListValidationRule( id );
    }

    public Collection<LineListValidationRule> getLineListValidationRules( Collection<Integer> identifiers )
    {
        if ( identifiers == null )
        {
            return getAllLineListValidationRules();
        }

        Collection<LineListValidationRule> rules = new ArrayList<LineListValidationRule>();

        for ( Integer id : identifiers )
        {
            rules.add( getLineListValidationRule( id ) );
        }

        return rules;
    }

    public LineListValidationRule getLineListValidationRuleByName( String name )
    {
        return lineListValidationRuleStore.getLineListValidationRuleByName( name );
    }

    public void updateLineListValidationRule( LineListValidationRule LineListValidationRule )
    {
        lineListValidationRuleStore.updateLineListValidationRule( LineListValidationRule );
    }
}
