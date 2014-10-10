package org.hisp.dhis.datamart.indicator;

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

import static org.hisp.dhis.setting.SystemSettingManager.KEY_OMIT_INDICATORS_ZERO_NUMERATOR_DATAMART;
import static org.hisp.dhis.system.util.DateUtils.daysBetween;
import static org.hisp.dhis.system.util.MathUtils.calculateExpression;
import static org.hisp.dhis.system.util.MathUtils.getRounded;
import static org.hisp.dhis.system.util.MathUtils.isEqual;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.amplecode.quick.StatementManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.aggregation.AggregatedIndicatorValue;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.datamart.aggregation.cache.AggregationCache;
import org.hisp.dhis.datamart.crosstab.CrossTabService;
import org.hisp.dhis.datamart.DataMartEngine;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.DateUtils;
import org.springframework.scheduling.annotation.Async;

/**
 * @author Lars Helge Overland
 */
public class DefaultIndicatorDataMart
    implements IndicatorDataMart
{
    private static final Log log = LogFactory.getLog( DefaultIndicatorDataMart.class );
    
    private static final int DECIMALS = 1;

    private static final String TRUE = "true";
    private static final String FALSE = "false";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }
    
    private AggregationCache aggregationCache;

    public void setAggregationCache( AggregationCache aggregationCache )
    {
        this.aggregationCache = aggregationCache;
    }

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    private CrossTabService crossTabService;

    public void setCrossTabService( CrossTabService crossTabService )
    {
        this.crossTabService = crossTabService;
    }
    
    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }

    private BatchHandlerFactory batchHandlerFactory;

    public void setBatchHandlerFactory( BatchHandlerFactory batchHandlerFactory )
    {
        this.batchHandlerFactory = batchHandlerFactory;
    }

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    // -------------------------------------------------------------------------
    // IndicatorDataMart implementation
    // -------------------------------------------------------------------------

    @Async
    public Future<?> exportIndicatorValues( Collection<Indicator> indicators, Collection<Period> periods, 
        Collection<OrganisationUnit> organisationUnits, Collection<OrganisationUnitGroup> organisationUnitGroups,
        Collection<DataElementOperand> operands, Class<? extends BatchHandler<AggregatedIndicatorValue>> clazz, String key )
    {
        statementManager.initialise(); // Running in separate thread
        
        final BatchHandler<AggregatedIndicatorValue> batchHandler = batchHandlerFactory.createBatchHandler( clazz ).init();

        final boolean omitZeroNumerator = (Boolean) systemSettingManager.getSystemSetting( KEY_OMIT_INDICATORS_ZERO_NUMERATOR_DATAMART, false );
        
        final AggregatedIndicatorValue indicatorValue = new AggregatedIndicatorValue();
        
        final Map<String, Double> constantMap = constantService.getConstantMap();

        organisationUnitGroups = organisationUnitGroups != null ? organisationUnitGroups : DataMartEngine.DUMMY_ORG_UNIT_GROUPS;
        
        for ( final Period period : periods )
        {
            int days = daysBetween( period.getStartDate(), period.getEndDate() );
            
            final PeriodType periodType = period.getPeriodType();
            
            for ( OrganisationUnitGroup group : organisationUnitGroups )
            {
                for ( final OrganisationUnit unit : organisationUnits )
                {
                    final int level = aggregationCache.getLevelOfOrganisationUnit( unit.getId() );
                    
                    final Map<DataElementOperand, Double> valueMap = crossTabService.getAggregatedDataCacheValue( operands, period, unit, group, key );
                    
                    if ( valueMap.size() > 0 )
                    {                
                        for ( final Indicator indicator : indicators )
                        {
                            final double denominatorValue = calculateExpression( expressionService.generateExpression( indicator.getExplodedDenominator(), valueMap, constantMap, days, false ) );
    
                            if ( !isEqual( denominatorValue, 0d ) )
                            {
                                final double numeratorValue = calculateExpression( expressionService.generateExpression( indicator.getExplodedNumerator(), valueMap, constantMap, days, false ) );
                             
                                if ( !( omitZeroNumerator && isEqual( numeratorValue, 0d ) ) )
                                {
                                    final double annualizationFactor = DateUtils.getAnnualizationFactor( indicator, period.getStartDate(), period.getEndDate() );                            
                                    final double factor = indicator.getIndicatorType().getFactor();                            
                                    final double aggregatedValue = ( numeratorValue / denominatorValue ) * factor * annualizationFactor;                            
                                    final double annualizedFactor = factor * annualizationFactor;
            
                                    indicatorValue.clear();
                                    
                                    indicatorValue.setIndicatorId( indicator.getId() );
                                    indicatorValue.setPeriodId( period.getId() );
                                    indicatorValue.setPeriodTypeId( periodType.getId() );
                                    indicatorValue.setOrganisationUnitId( unit.getId() );
                                    indicatorValue.setOrganisationUnitGroupId( group != null ? group.getId() : 0 );
                                    indicatorValue.setLevel( level );
                                    indicatorValue.setAnnualized( getAnnualizationString( indicator.isAnnualized() ) );
                                    indicatorValue.setFactor( annualizedFactor);
                                    indicatorValue.setValue( getRounded( aggregatedValue, DECIMALS ) );
                                    indicatorValue.setNumeratorValue( getRounded( numeratorValue, DECIMALS ) );
                                    indicatorValue.setDenominatorValue( getRounded( denominatorValue, DECIMALS ) );
                                    
                                    batchHandler.addObject( indicatorValue );
                                }
                            }
                        }
                    }
                }
            }
            
            log.debug( "Exported indicator values for period: " + period );
        }
        
        batchHandler.flush();
        
        statementManager.destroy();
        
        aggregationCache.clearCache();
        
        log.info( "Indicator export task done" );
        
        return null;
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
        
    public static String getAnnualizationString( final Boolean annualized )
    {
        return ( annualized == null || !annualized ) ? FALSE : TRUE;
    }
}
