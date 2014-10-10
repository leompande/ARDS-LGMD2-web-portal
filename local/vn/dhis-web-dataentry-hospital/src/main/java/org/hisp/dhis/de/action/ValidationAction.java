package org.hisp.dhis.de.action;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import static org.hisp.dhis.system.util.ListUtils.getCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

import com.opensymphony.xwork2.Action;

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

    private Integer dataSetId;

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private Integer organisationUnitId;

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<ValidationResult> results;

    public List<ValidationResult> getResults()
    {
        return results;
    }

    private Map<Integer, String> leftsideFormulaMap;

    public Map<Integer, String> getLeftsideFormulaMap()
    {
        return leftsideFormulaMap;
    }

    private Map<Integer, String> rightsideFormulaMap;

    public Map<Integer, String> getRightsideFormulaMap()
    {
        return rightsideFormulaMap;
    }

    private Collection<DeflatedDataValue> dataValues = new HashSet<DeflatedDataValue>();

    public Collection<DeflatedDataValue> getDataValues()
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

        Period selectedPeriod = PeriodType.createPeriodExternalId( periodId );

        if ( orgUnit != null && selectedPeriod != null )
        {
            Period period = periodService.getPeriod( selectedPeriod.getStartDate(), selectedPeriod.getEndDate(),
                selectedPeriod.getPeriodType() );

            DataSet dataSet = dataSetService.getDataSet( dataSetId );

            // ---------------------------------------------------------------------
            // Min-max and outlier analysis
            // ---------------------------------------------------------------------

            dataValues = minMaxOutlierAnalysisService.analyse( getCollection( orgUnit ), dataSet.getDataElements(), getCollection( period ), null );
            
            log.debug( "Number of outlier values: " + dataValues.size() );

            // ---------------------------------------------------------------------
            // Validation rule analysis
            // ---------------------------------------------------------------------

            results = new ArrayList<ValidationResult>( validationRuleService.validate( dataSet, period, orgUnit ) );

            log.debug( "Number of validation violations: " + results.size() );

            if ( results.size() > 0 )
            {
                leftsideFormulaMap = new HashMap<Integer, String>( results.size() );
                rightsideFormulaMap = new HashMap<Integer, String>( results.size() );

                for ( ValidationResult result : results )
                {
                    ValidationRule rule = result.getValidationRule();

                    leftsideFormulaMap.put( rule.getId(),
                        expressionService.getExpressionDescription( rule.getLeftSide().getExpression() ) );

                    rightsideFormulaMap.put( rule.getId(),
                        expressionService.getExpressionDescription( rule.getRightSide().getExpression() ) );
                }
            }
        }

        return dataValues.size() == 0 && results.size() == 0 ? SUCCESS : INPUT;
    }
}
