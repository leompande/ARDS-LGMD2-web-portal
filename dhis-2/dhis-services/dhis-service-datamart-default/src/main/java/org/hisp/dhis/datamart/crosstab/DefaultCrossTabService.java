package org.hisp.dhis.datamart.crosstab;

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

import static org.hisp.dhis.datavalue.DataValue.FALSE;
import static org.hisp.dhis.datavalue.DataValue.TRUE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.amplecode.quick.StatementManager;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.datamart.CrossTabDataValue;
import org.hisp.dhis.datamart.DataMartManager;
import org.hisp.dhis.datamart.crosstab.jdbc.CrossTabStore;
import org.hisp.dhis.jdbc.batchhandler.GenericBatchHandler;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;
import org.springframework.scheduling.annotation.Async;

/**
 * @author Lars Helge Overland
 */
public class DefaultCrossTabService
    implements CrossTabService
{
    private static final Log log = LogFactory.getLog( DefaultCrossTabService.class );

    private static final int MAX_LENGTH = 20;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private BatchHandlerFactory batchHandlerFactory;

    public void setBatchHandlerFactory( BatchHandlerFactory batchHandlerFactory )
    {
        this.batchHandlerFactory = batchHandlerFactory;
    }

    private CrossTabStore crossTabStore;

    public void setCrossTabStore( CrossTabStore crossTabTableManager )
    {
        this.crossTabStore = crossTabTableManager;
    }

    private DataMartManager dataMartManager;

    public void setDataMartManager( DataMartManager dataMartManager )
    {
        this.dataMartManager = dataMartManager;
    }

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }
    
    // -------------------------------------------------------------------------
    // CrossTabService implementation
    // -------------------------------------------------------------------------
    
    public String createCrossTabTable( List<DataElementOperand> operands )
    {
        final String key = RandomStringUtils.randomAlphanumeric( 8 );
        
        crossTabStore.dropCrossTabTable( key );    
        crossTabStore.createCrossTabTable( operands, key );

        return key;
    }
    
    @Async
    public Future<?> populateCrossTabTable( List<DataElementOperand> operands,
        Collection<Integer> periodIds, Collection<Integer> organisationUnitIds, String key )
    {
        statementManager.initialise();
        
        final BatchHandler<Object> batchHandler = batchHandlerFactory.createBatchHandler( GenericBatchHandler.class ).
            setTableName( CrossTabStore.CROSSTAB_TABLE_PREFIX + key ).init();

        int rows = 0;
        
        for ( final Integer periodId : periodIds )
        {
            for ( final Integer sourceId : organisationUnitIds )
            {
                final Map<DataElementOperand, String> map = dataMartManager.getDataValueMap( periodId, sourceId );

                final List<String> valueList = new ArrayList<String>( operands.size() + 2 );

                valueList.add( String.valueOf( periodId ) );
                valueList.add( String.valueOf( sourceId ) );

                boolean hasValues = false;

                for ( DataElementOperand operand : operands )
                {
                    String value = map.get( operand );

                    if ( value != null && value.length() > MAX_LENGTH )
                    {
                        log.warn( "Value ignored, too long: '" + value + "'" );                                
                        value = null;
                    }
                    
                    if ( value != null && !TRUE.equalsIgnoreCase( value ) && !FALSE.equalsIgnoreCase( value ) )
                    {
                        try
                        {
                            Double.parseDouble( value );
                        }
                        catch ( NumberFormatException ex )
                        {
                            log.warn( "Value ignored, not numeric: '" + value + "'" );
                            value = null;
                        }
                    }

                    if ( value != null )
                    {
                        hasValues = true;
                    }

                    valueList.add( value );
                }

                if ( hasValues )
                {
                    rows++;
                    
                    batchHandler.addObject( valueList );
                }
            }
        }
        
        batchHandler.flush();
        
        statementManager.destroy();
        
        log.info( "Crosstab table columns: " + ( operands.size() + 2 ) + ", rows: " + rows );
        
        return null;
    }

    public void dropCrossTabTable( String key )
    {
        crossTabStore.dropCrossTabTable( key );
    }

    public void createAggregatedDataCache( List<DataElementOperand> operands, String key )
    {
        crossTabStore.createAggregatedDataCache( operands, key );
    }
    
    public void dropAggregatedDataCache( String key )
    {
        crossTabStore.dropAggregatedDataCache( key );
    }
    
    public void createAggregatedOrgUnitDataCache( List<DataElementOperand> operands, String key )
    {
        crossTabStore.createAggregatedOrgUnitDataCache( operands, key );
    }
    
    public void dropAggregatedOrgUnitDataCache( String key )
    {
        crossTabStore.dropAggregatedOrgUnitDataCache( key );
    }
    
    public Collection<CrossTabDataValue> getCrossTabDataValues( Collection<DataElementOperand> operands,
        Collection<Integer> periodIds, Collection<Integer> sourceIds, String key )
    {
        return crossTabStore.getCrossTabDataValues( operands, periodIds, sourceIds, key );
    }

    public Map<DataElementOperand, Double> getAggregatedDataCacheValue( Collection<DataElementOperand> operands, 
        Period period, OrganisationUnit unit, OrganisationUnitGroup group, String key )
    {
        if ( group != null && group.getId() > 0 )
        {
            return crossTabStore.getAggregatedOrgUnitDataCacheValue( operands, period.getId(), unit.getId(), group.getId(), key );
        }
        
        return crossTabStore.getAggregatedDataCacheValue( operands, period.getId(), unit.getId(), key );
    }
}
