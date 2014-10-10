package org.hisp.dhis.de.action;

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

import com.opensymphony.xwork2.Action;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataanalysis.DataAnalysisService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DeflatedDataValue;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.validation.ValidationResult;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.hisp.dhis.system.util.ListUtils.getCollection;

/**
 * @author Margrethe Store
 * @author Lars Helge Overland
 */
public class ValidationAction
    implements Action
{
    private static final Log log = LogFactory.getLog( ValidationAction.class );

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

    private DataAnalysisService minMaxOutlierAnalysisService;

    public void setMinMaxOutlierAnalysisService( DataAnalysisService minMaxOutlierAnalysisService )
    {
        this.minMaxOutlierAnalysisService = minMaxOutlierAnalysisService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String periodId;

    public void setPeriodId( String periodId )
    {
        this.periodId = periodId;
    }

    private String dataSetId;

    public void setDataSetId( String dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private String organisationUnitId;

    public void setOrganisationUnitId( String organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    private boolean multiOrganisationUnit;

    public void setMultiOrganisationUnit( boolean multiOrganisationUnit )
    {
        this.multiOrganisationUnit = multiOrganisationUnit;
    }

    public boolean isMultiOrganisationUnit()
    {
        return multiOrganisationUnit;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Map<OrganisationUnit, List<ValidationResult>> validationResults = new TreeMap<OrganisationUnit, List<ValidationResult>>();

    public Map<OrganisationUnit, List<ValidationResult>> getValidationResults()
    {
        return validationResults;
    }

    private Map<OrganisationUnit, Map<Integer, String>> leftSideFormulaMap = new HashMap<OrganisationUnit, Map<Integer, String>>();

    public Map<OrganisationUnit, Map<Integer, String>> getLeftSideFormulaMap()
    {
        return leftSideFormulaMap;
    }

    private Map<OrganisationUnit, Map<Integer, String>> rightSideFormulaMap = new HashMap<OrganisationUnit, Map<Integer, String>>();

    public Map<OrganisationUnit, Map<Integer, String>> getRightSideFormulaMap()
    {
        return rightSideFormulaMap;
    }

    private Map<OrganisationUnit, List<DeflatedDataValue>> dataValues = new TreeMap<OrganisationUnit, List<DeflatedDataValue>>();

    public Map<OrganisationUnit, List<DeflatedDataValue>> getDataValues()
    {
        return dataValues;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );

        DataSet dataSet = dataSetService.getDataSet( dataSetId );

        Period selectedPeriod = PeriodType.getPeriodFromIsoString( periodId );

        if ( selectedPeriod == null || orgUnit == null || ( multiOrganisationUnit && !orgUnit.hasChild() ) )
        {
            return SUCCESS;
        }

        Period period = periodService.getPeriod( selectedPeriod.getStartDate(), selectedPeriod.getEndDate(),
            selectedPeriod.getPeriodType() );

        List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>();

        if ( !multiOrganisationUnit )
        {
            organisationUnits.add( orgUnit );
        }
        else
        {
            organisationUnits.addAll( orgUnit.getChildren() );
        }

        Collections.sort( organisationUnits, IdentifiableObjectNameComparator.INSTANCE );

        for ( OrganisationUnit organisationUnit : organisationUnits )
        {
            List<DeflatedDataValue> values = outlierAnalysis( organisationUnit, dataSet, period );

            if ( !values.isEmpty() )
            {
                dataValues.put( organisationUnit, values );
            }

            List<ValidationResult> results = validationRuleAnalysis( organisationUnit, dataSet, period );

            if ( !results.isEmpty() )
            {
                validationResults.put( organisationUnit, results );
            }
        }

        return dataValues.size() == 0 && validationResults.size() == 0 ? SUCCESS : INPUT;
    }

    // -------------------------------------------------------------------------
    // Min-max and outlier analysis
    // -------------------------------------------------------------------------
    
    private List<DeflatedDataValue> outlierAnalysis( OrganisationUnit organisationUnit, DataSet dataSet, Period period )
    {
        List<DeflatedDataValue> deflatedDataValues = new ArrayList<DeflatedDataValue>( minMaxOutlierAnalysisService.analyse( getCollection( organisationUnit ),
            dataSet.getDataElements(), getCollection( period ), null ) );

        log.debug( "Number of outlier values: " + deflatedDataValues.size() );

        return deflatedDataValues;
    }

    // -------------------------------------------------------------------------
    // Validation rule analysis
    // -------------------------------------------------------------------------
    
    private List<ValidationResult> validationRuleAnalysis( OrganisationUnit organisationUnit, DataSet dataSet, Period period )
    {
        List<ValidationResult> validationResults = new ArrayList<ValidationResult>( validationRuleService.validate( dataSet, period, organisationUnit ) );

        log.debug( "Number of validation violations: " + validationResults.size() );

        if ( validationResults.size() > 0 )
        {
            Map<Integer, String> leftSideFormulas = new HashMap<Integer, String>( validationResults.size() );
            Map<Integer, String> rightSideFormulas = new HashMap<Integer, String>( validationResults.size() );

            for ( ValidationResult validationResult : validationResults )
            {
                ValidationRule rule = validationResult.getValidationRule();

                leftSideFormulas.put( rule.getId(), expressionService.getExpressionDescription( rule
                    .getLeftSide().getExpression() ) );

                rightSideFormulas.put( rule.getId(), expressionService.getExpressionDescription( rule
                    .getRightSide().getExpression() ) );
            }

            leftSideFormulaMap.put( organisationUnit, leftSideFormulas );
            rightSideFormulaMap.put( organisationUnit, rightSideFormulas );
        }

        return validationResults;
    }
}
