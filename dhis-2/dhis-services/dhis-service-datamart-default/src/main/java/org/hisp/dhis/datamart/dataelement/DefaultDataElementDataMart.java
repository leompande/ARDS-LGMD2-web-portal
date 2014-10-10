package org.hisp.dhis.datamart.dataelement;

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

import static org.hisp.dhis.datamart.crosstab.jdbc.CrossTabStore.AGGREGATEDDATA_CACHE_PREFIX;
import static org.hisp.dhis.datamart.crosstab.jdbc.CrossTabStore.AGGREGATEDORGUNITDATA_CACHE_PREFIX;
import static org.hisp.dhis.system.util.MathUtils.getRounded;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.amplecode.quick.StatementManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.aggregation.AggregatedDataValue;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.datamart.DataElementOperandList;
import org.hisp.dhis.datamart.aggregation.cache.AggregationCache;
import org.hisp.dhis.datamart.aggregation.dataelement.DataElementAggregator;
import org.hisp.dhis.datamart.DataMartEngine;
import org.hisp.dhis.jdbc.batchhandler.GenericBatchHandler;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitHierarchy;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.system.util.SystemUtils;
import org.springframework.scheduling.annotation.Async;

/**
 * @author Lars Helge Overland
 */
public class DefaultDataElementDataMart
    implements DataElementDataMart
{
    private static final Log log = LogFactory.getLog( DefaultDataElementDataMart.class );
    
    private static final int DECIMALS = 1;
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private BatchHandlerFactory batchHandlerFactory;

    public void setBatchHandlerFactory( BatchHandlerFactory batchHandlerFactory )
    {
        this.batchHandlerFactory = batchHandlerFactory;
    }
    
    private BatchHandlerFactory inMemoryBatchHandlerFactory;
        
    public void setInMemoryBatchHandlerFactory( BatchHandlerFactory inMemoryBatchHandlerFactory )
    {
        this.inMemoryBatchHandlerFactory = inMemoryBatchHandlerFactory;
    }

    private AggregationCache aggregationCache;

    public void setAggregationCache( AggregationCache aggregationCache )
    {
        this.aggregationCache = aggregationCache;
    }

    private DataElementAggregator sumIntAggregator;

    public void setSumIntAggregator( DataElementAggregator sumIntDataElementAggregator )
    {
        this.sumIntAggregator = sumIntDataElementAggregator;
    }

    private DataElementAggregator averageIntAggregator;

    public void setAverageIntAggregator( DataElementAggregator averageIntDataElementAggregator )
    {
        this.averageIntAggregator = averageIntDataElementAggregator;
    }

    private DataElementAggregator averageIntSingleValueAggregator;

    public void setAverageIntSingleValueAggregator( DataElementAggregator averageIntSingleValueAggregator )
    {
        this.averageIntSingleValueAggregator = averageIntSingleValueAggregator;
    }

    private DataElementAggregator sumBoolAggregator;

    public void setSumBoolAggregator( DataElementAggregator sumBooleanDataElementAggregator )
    {
        this.sumBoolAggregator = sumBooleanDataElementAggregator;
    }

    private DataElementAggregator averageBoolAggregator;

    public void setAverageBoolAggregator( DataElementAggregator averageBooleanDataElementAggregator )
    {
        this.averageBoolAggregator = averageBooleanDataElementAggregator;
    }
    
    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    // -------------------------------------------------------------------------
    // DataMart functionality
    // -------------------------------------------------------------------------
    
    @Async
    public Future<?> exportDataValues( Collection<DataElementOperand> operands, Collection<Period> periods, 
        Collection<OrganisationUnit> organisationUnits, Collection<OrganisationUnitGroup> organisationUnitGroups, 
        DataElementOperandList operandList, OrganisationUnitHierarchy hierarchy, 
        Map<String, Integer> dataElementUidIdMap, Map<String, Integer> categoryOptionComboUidIdMap,
        Class<? extends BatchHandler<AggregatedDataValue>> clazz, String key )
    {
        statementManager.initialise(); // Running in separate thread
        
        final BatchHandler<AggregatedDataValue> batchHandler = batchHandlerFactory.createBatchHandler( clazz ).init();
        
        final String tableName = organisationUnitGroups != null ? AGGREGATEDORGUNITDATA_CACHE_PREFIX : AGGREGATEDDATA_CACHE_PREFIX;
        
        final BatchHandler<Object> cacheHandler = inMemoryBatchHandlerFactory.createBatchHandler( GenericBatchHandler.class ).setTableName( tableName + key ).init();
        
        final Map<DataElementOperand, Double> valueMap = new HashMap<DataElementOperand, Double>();
        
        final AggregatedDataValue aggregatedValue = new AggregatedDataValue();
        
        organisationUnitGroups = organisationUnitGroups != null ? organisationUnitGroups : DataMartEngine.DUMMY_ORG_UNIT_GROUPS;
        
        for ( final Period period : periods )
        {
            final Collection<DataElementOperand> sumIntOperands = sumIntAggregator.filterOperands( operands, period.getPeriodType() );
            final Collection<DataElementOperand> averageIntOperands = averageIntAggregator.filterOperands( operands, period.getPeriodType() );
            final Collection<DataElementOperand> averageIntSingleValueOperands = averageIntSingleValueAggregator.filterOperands( operands, period.getPeriodType() );
            final Collection<DataElementOperand> sumBoolOperands = sumBoolAggregator.filterOperands( operands, period.getPeriodType() );
            final Collection<DataElementOperand> averageBoolOperands = averageBoolAggregator.filterOperands( operands, period.getPeriodType() );
            
            for ( OrganisationUnitGroup group : organisationUnitGroups )
            {
                for ( final OrganisationUnit unit : organisationUnits )
                {
                    operandList.init( period, unit, group );
                    
                    final int level = aggregationCache.getLevelOfOrganisationUnit( unit.getId() );
                    
                    final Collection<Integer> orgUnitChildren = hierarchy.getChildren( unit.getId(), group );
                    
                    valueMap.clear();                
                    valueMap.putAll( sumIntAggregator.getAggregatedValues( sumIntOperands, period, level, orgUnitChildren, key ) );
                    valueMap.putAll( averageIntAggregator.getAggregatedValues( averageIntOperands, period, level, orgUnitChildren, key ) );
                    valueMap.putAll( averageIntSingleValueAggregator.getAggregatedValues( averageIntSingleValueOperands, period, level, orgUnitChildren, key ) );
                    valueMap.putAll( sumBoolAggregator.getAggregatedValues( sumBoolOperands, period, level, orgUnitChildren, key ) );
                    valueMap.putAll( averageBoolAggregator.getAggregatedValues( averageBoolOperands, period, level, orgUnitChildren, key ) );
                    
                    if ( valueMap.size() > 0 )
                    {
                        for ( Entry<DataElementOperand, Double> entry : valueMap.entrySet() )
                        {
                            aggregatedValue.clear();
                            
                            final double value = getRounded( entry.getValue(), DECIMALS );
                            
                            aggregatedValue.setDataElementId( dataElementUidIdMap.get( entry.getKey().getDataElementId() ) );
                            aggregatedValue.setCategoryOptionComboId( categoryOptionComboUidIdMap.get( entry.getKey().getOptionComboId() ) );
                            aggregatedValue.setPeriodId( period.getId() );
                            aggregatedValue.setPeriodTypeId( period.getPeriodType().getId() );
                            aggregatedValue.setOrganisationUnitId( unit.getId() );
                            aggregatedValue.setOrganisationUnitGroupId( group != null ? group.getId() : 0 );
                            aggregatedValue.setLevel( level );
                            aggregatedValue.setValue( value );
                            
                            batchHandler.addObject( aggregatedValue );
                            
                            operandList.addValue( entry.getKey(), value );
                        }
                    }
                    
                    if ( operandList.hasValues() )
                    {
                        cacheHandler.addObject( operandList.getList() );
                    }
                }
            }
            
            log.debug( "Exported data values for period: " + period + ", " + SystemUtils.getMemoryString() );
        }
        
        batchHandler.flush();
        
        cacheHandler.flush();

        statementManager.destroy();
        
        aggregationCache.clearCache();
        
        log.info( "Data element export task done" );
        
        return null;
    }
}
