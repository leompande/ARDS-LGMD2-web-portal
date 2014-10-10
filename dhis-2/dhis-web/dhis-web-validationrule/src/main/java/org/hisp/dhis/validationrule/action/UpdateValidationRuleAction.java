package org.hisp.dhis.validationrule.action;

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

import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.expression.Operator;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleService;

import com.opensymphony.xwork2.Action;

/**
 * @author Margrethe Store
 * @author Lars Helge Overland
 * @version $Id: UpdateValidationRuleAction.java 3868 2007-11-08 15:11:12Z
 *          larshelg $
 */
public class UpdateValidationRuleAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ValidationRuleService validationRuleService;

    public void setValidationRuleService( ValidationRuleService validationRuleService )
    {
        this.validationRuleService = validationRuleService;
    }

    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String description;

    public void setDescription( String description )
    {
        this.description = description;
    }

    private String importance;

    public void setImportance( String importance )
    {
        this.importance = importance;
    }
    
    private String ruleType;

    public void setRuleType( String ruleType )
    {
        this.ruleType = ruleType;
    }

    private String operator;

    public void setOperator( String operator )
    {
        this.operator = operator;
    }

    private String leftSideExpression;

    public void setLeftSideExpression( String leftSideExpression )
    {
        this.leftSideExpression = leftSideExpression;
    }

    private String leftSideDescription;

    public void setLeftSideDescription( String leftSideDescription )
    {
        this.leftSideDescription = leftSideDescription;
    }

    private boolean leftSideNullIfBlank;
    
    public void setLeftSideNullIfBlank( boolean leftSideNullIfBlank )
    {
        this.leftSideNullIfBlank = leftSideNullIfBlank;
    }

    private String rightSideExpression;

    public void setRightSideExpression( String rightSideExpression )
    {
        this.rightSideExpression = rightSideExpression;
    }

    private String rightSideDescription;

    public void setRightSideDescription( String rightSideDescription )
    {
        this.rightSideDescription = rightSideDescription;
    }

    private boolean rightSideNullIfBlank;

    public void setRightSideNullIfBlank( boolean rightSideNullIfBlank )
    {
        this.rightSideNullIfBlank = rightSideNullIfBlank;
    }

    private Integer organisationUnitLevel;
    
    public void setOrganisationUnitLevel( Integer organisationUnitLevel )
    {
        this.organisationUnitLevel = organisationUnitLevel;
    }

    private String periodTypeName;

    public void setPeriodTypeName( String periodTypeName )
    {
        this.periodTypeName = periodTypeName;
    }

    private String sequentialSampleCount;

    public void setSequentialSampleCount( String sequentialSampleCount )
    {
        this.sequentialSampleCount = sequentialSampleCount;
    }

    private String annualSampleCount;

    public void setAnnualSampleCount( String annualSampleCount )
    {
        this.annualSampleCount = annualSampleCount;
    }

    private String highOutliers;

    public void setHighOutliers( String highOutliers )
    {
        this.highOutliers = highOutliers;
    }

    private String lowOutliers;

    public void setLowOutliers( String lowOutliers )
    {
        this.lowOutliers = lowOutliers;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        ValidationRule validationRule = validationRuleService.getValidationRule( id );

        validationRule.setName( name );
        validationRule.setDescription( description );
        validationRule.setImportance( importance );
        validationRule.setRuleType( ruleType );
        validationRule.setOperator( Operator.valueOf( operator ) );

        validationRule.getLeftSide().setExpression( leftSideExpression );
        validationRule.getLeftSide().setDescription( leftSideDescription );
        validationRule.getLeftSide().setNullIfBlank( leftSideNullIfBlank );
        validationRule.getLeftSide().setDataElementsInExpression( expressionService.getDataElementsInExpression( leftSideExpression ) );
        validationRule.getLeftSide().setOptionCombosInExpression( expressionService.getOptionCombosInExpression( leftSideExpression ) );

        validationRule.getRightSide().setExpression( rightSideExpression );
        validationRule.getRightSide().setDescription( rightSideDescription );
        validationRule.getRightSide().setNullIfBlank( rightSideNullIfBlank );
        validationRule.getRightSide().setDataElementsInExpression( expressionService.getDataElementsInExpression( rightSideExpression ) );
        validationRule.getRightSide().setOptionCombosInExpression( expressionService.getOptionCombosInExpression( rightSideExpression ) );
        validationRule.setOrganisationUnitLevel( organisationUnitLevel );
        
        PeriodType periodType = periodService.getPeriodTypeByName( periodTypeName );
        validationRule.setPeriodType( periodType == null ? null : periodService.getPeriodTypeByClass( periodType.getClass() ) );

        validationRule.setSequentialSampleCount( sequentialSampleCount != null && !sequentialSampleCount.isEmpty() ? Integer.parseInt( sequentialSampleCount ) : null );
        validationRule.setAnnualSampleCount( annualSampleCount != null && !annualSampleCount.isEmpty() ? Integer.parseInt( annualSampleCount ) : null );
        validationRule.setHighOutliers( highOutliers != null && !highOutliers.isEmpty() ? Integer.parseInt( highOutliers ) : null );
        validationRule.setLowOutliers( lowOutliers != null && !lowOutliers.isEmpty() ? Integer.parseInt( lowOutliers ) : null );

        validationRuleService.updateValidationRule( validationRule );

        return SUCCESS;
    }
}
