package org.hisp.dhis.importexport.dxf.converter;

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

import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.expression.Expression;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.expression.Operator;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.importer.ValidationRuleImporter;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleService;

/**
 * @author Lars Helge Overland
 * @version $Id: ValidationRuleConverter.java 6455 2008-11-24 08:59:37Z larshelg
 *          $
 */
public class ValidationRuleConverter
    extends ValidationRuleImporter
    implements XMLConverter
{
    public static final String COLLECTION_NAME = "validationRules";
    public static final String ELEMENT_NAME = "validationRule";

    private static final String FIELD_ID = "id";
    private static final String FIELD_UID = "uid";
    private static final String FIELD_CODE = "code";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_OPERATOR = "operator";
    private static final String FIELD_LEFTSIDE_EXPRESSION = "leftSideExpression";
    private static final String FIELD_LEFTSIDE_DESCRIPTION = "leftSideDescription";
    private static final String FIELD_RIGHTSIDE_EXPRESSION = "rightSideExpression";
    private static final String FIELD_RIGHTSIDE_DESCRIPTION = "rightSideDescription";

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public ValidationRuleConverter( ValidationRuleService validationRuleService )
    {
        this.validationRuleService = validationRuleService;
    }

    /**
     * Constructor for write operations.
     */
    public ValidationRuleConverter( ValidationRuleService validationRuleService, ExpressionService expressionService )
    {
        this.validationRuleService = validationRuleService;
        this.expressionService = expressionService;
    }

    /**
     * Constructor for read operations.
     * 
     * @param importObjectService the importObjectService to use.
     * @param validationRuleService the ValidationRuleService to use.
     * @param expressionService the expressionService to use.
     * @param dataElementMapping the data element mapping to use.
     */
    public ValidationRuleConverter( ImportObjectService importObjectService,
        ValidationRuleService validationRuleService, ExpressionService expressionService )
    {
        this.importObjectService = importObjectService;
        this.validationRuleService = validationRuleService;
        this.expressionService = expressionService;
    }

    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<ValidationRule> validationRules = validationRuleService.getValidationRules( params
            .getValidationRules() );

        if ( validationRules != null && validationRules.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );

            for ( ValidationRule rule : validationRules )
            {
                writer.openElement( ELEMENT_NAME );

                writer.writeElement( FIELD_ID, String.valueOf( rule.getId() ) );
                writer.writeElement( FIELD_UID, rule.getUid() );
                writer.writeElement( FIELD_CODE, rule.getCode() );

                writer.writeElement( FIELD_NAME, rule.getName() );
                writer.writeElement( FIELD_DESCRIPTION, rule.getDescription() );
                writer.writeElement( FIELD_TYPE, rule.getType() );
                writer.writeElement( FIELD_OPERATOR, rule.getOperator().toString() );
                writer.writeElement( FIELD_LEFTSIDE_EXPRESSION, rule.getLeftSide().getExpression() );
                writer.writeElement( FIELD_LEFTSIDE_DESCRIPTION, rule.getLeftSide().getDescription() );
                writer.writeElement( FIELD_RIGHTSIDE_EXPRESSION, rule.getRightSide().getExpression() );
                writer.writeElement( FIELD_RIGHTSIDE_DESCRIPTION, rule.getRightSide().getDescription() );

                writer.closeElement();
            }

            writer.closeElement();
        }
    }

    public void read( XMLReader reader, ImportParams params )
    {
        while ( reader.moveToStartElement( ELEMENT_NAME, COLLECTION_NAME ) )
        {
            final Map<String, String> values = reader.readElements( ELEMENT_NAME );

            final ValidationRule validationRule = new ValidationRule();

            if ( params.minorVersionGreaterOrEqual( "1.3") )
            {
                validationRule.setId( Integer.parseInt( values.get( FIELD_ID )));
                validationRule.setUid( values.get( FIELD_UID ) );
                validationRule.setCode( values.get( FIELD_CODE) );
            }

            final Expression leftSide = new Expression();
            final Expression rightSide = new Expression();

            validationRule.setLeftSide( leftSide );
            validationRule.setRightSide( rightSide );

            validationRule.setName( values.get( FIELD_NAME ) );
            validationRule.setDescription( values.get( FIELD_DESCRIPTION ) );
            validationRule.setType( values.get( FIELD_TYPE ) );
            validationRule.setOperator( Operator.valueOf( values.get( FIELD_OPERATOR ) ) );

            validationRule.getLeftSide().setExpression( values.get( FIELD_LEFTSIDE_EXPRESSION ) );
            validationRule.getLeftSide().setDescription( values.get( FIELD_LEFTSIDE_DESCRIPTION ) );
            
            validationRule.getLeftSide().setDataElementsInExpression(
                expressionService.getDataElementsInExpression( validationRule.getLeftSide().getExpression() ) );
            validationRule.getLeftSide().setOptionCombosInExpression(
                expressionService.getOptionCombosInExpression( validationRule.getLeftSide().getExpression() ) );

            validationRule.getRightSide().setExpression( values.get( FIELD_RIGHTSIDE_EXPRESSION ) );
            validationRule.getRightSide().setDescription( values.get( FIELD_RIGHTSIDE_DESCRIPTION ) );
            validationRule.getRightSide().setDataElementsInExpression(
                expressionService.getDataElementsInExpression( validationRule.getRightSide().getExpression() ) );
            validationRule.getRightSide().setOptionCombosInExpression(
                expressionService.getOptionCombosInExpression( validationRule.getRightSide().getExpression() ) );

            validationRule.setPeriodType( PeriodType.getPeriodTypeByName( MonthlyPeriodType.NAME ) );

            importObject( validationRule, params );
        }
    }
}
