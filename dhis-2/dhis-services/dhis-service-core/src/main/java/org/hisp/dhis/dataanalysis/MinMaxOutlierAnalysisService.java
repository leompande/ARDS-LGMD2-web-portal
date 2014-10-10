package org.hisp.dhis.dataanalysis;

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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.datavalue.DeflatedDataValue;
import org.hisp.dhis.jdbc.batchhandler.MinMaxDataElementBatchHandler;
import org.hisp.dhis.minmax.MinMaxDataElement;
import org.hisp.dhis.minmax.MinMaxDataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.system.filter.DataElementTypeFilter;
import org.hisp.dhis.system.util.ConversionUtils;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.hisp.dhis.system.util.MathUtils;

import static org.hisp.dhis.dataelement.DataElement.*;

/**
 * @author Lars Helge Overland
 */
public class MinMaxOutlierAnalysisService
    implements MinMaxDataAnalysisService
{
    private static final Log log = LogFactory.getLog( MinMaxOutlierAnalysisService.class );
    
    private static final Filter<DataElement> DATALEMENT_INT_FILTER = new DataElementTypeFilter( DataElement.VALUE_TYPE_INT );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private DataAnalysisStore dataAnalysisStore;

    public void setDataAnalysisStore( DataAnalysisStore dataAnalysisStore )
    {
        this.dataAnalysisStore = dataAnalysisStore;
    }
    
    private MinMaxDataElementService minMaxDataElementService;

    public void setMinMaxDataElementService( MinMaxDataElementService minMaxDataElementService )
    {
        this.minMaxDataElementService = minMaxDataElementService;
    }
    
    private BatchHandlerFactory batchHandlerFactory;

    public void setBatchHandlerFactory( BatchHandlerFactory batchHandlerFactory )
    {
        this.batchHandlerFactory = batchHandlerFactory;
    }

    // -------------------------------------------------------------------------
    // DataAnalysisService implementation
    // -------------------------------------------------------------------------

    public Collection<DeflatedDataValue> analyse( Collection<OrganisationUnit> organisationUnits,
        Collection<DataElement> dataElements, Collection<Period> periods, Double stdDevFactor )
    {
        Set<DataElement> elements = new HashSet<DataElement>( dataElements );
        
        FilterUtils.filter( elements, DATALEMENT_INT_FILTER );
        
        Set<DataElementCategoryOptionCombo> categoryOptionCombos = new HashSet<DataElementCategoryOptionCombo>();
        
        for ( DataElement dataElement : elements )
        {
            categoryOptionCombos.addAll( dataElement.getCategoryCombo().getOptionCombos() );
        }

        log.info( "Starting min-max analysis, no of data elements: " + elements.size() + ", no of org units: " + organisationUnits.size() );
        
        return dataAnalysisStore.getMinMaxViolations( elements, categoryOptionCombos, periods, organisationUnits, MAX_OUTLIERS );
    }
    
    public void generateMinMaxValues( Collection<OrganisationUnit> organisationUnits,
        Collection<DataElement> dataElements, Double stdDevFactor )
    {
        log.info( "Starting min-max value generation, no of data elements: " + dataElements.size() + ", no of org units: " + organisationUnits.size() );

        Set<Integer> orgUnitIds = new HashSet<Integer>( ConversionUtils.getIdentifiers( OrganisationUnit.class, organisationUnits ) ); 

        minMaxDataElementService.removeMinMaxDataElements( dataElements, organisationUnits );

        log.debug( "Deleted existing min-max values" );

        BatchHandler<MinMaxDataElement> batchHandler = batchHandlerFactory.createBatchHandler( MinMaxDataElementBatchHandler.class ).init();
        
        for ( DataElement dataElement : dataElements )
        {
            if ( VALUE_TYPE_INT.equals( dataElement.getType() ) )
            {
                Collection<DataElementCategoryOptionCombo> categoryOptionCombos = dataElement.getCategoryCombo().getOptionCombos();

                for ( DataElementCategoryOptionCombo categoryOptionCombo : categoryOptionCombos )
                {
                    Map<Integer, Double> standardDeviations = dataAnalysisStore.getStandardDeviation( dataElement, categoryOptionCombo, orgUnitIds );
                    
                    Map<Integer, Double> averages = dataAnalysisStore.getAverage( dataElement, categoryOptionCombo, standardDeviations.keySet() );
                    
                    for ( Integer unit : averages.keySet() )
                    {
                        Double stdDev = standardDeviations.get( unit );
                        Double avg = averages.get( unit );
                        
                        if ( stdDev != null && avg != null )
                        {
                            int min = (int) MathUtils.getLowBound( stdDev, stdDevFactor, avg );
                            int max = (int) MathUtils.getHighBound( stdDev, stdDevFactor, avg );
                            
                            if ( VALUE_TYPE_POSITIVE_INT.equals( dataElement.getNumberType() ) || VALUE_TYPE_ZERO_OR_POSITIVE_INT.equals( dataElement.getNumberType() ) )
                            {
                                min = Math.max( 0, min ); // Cannot be < 0
                            }
                            
                            if ( VALUE_TYPE_NEGATIVE_INT.equals( dataElement.getNumberType() ) )
                            {
                                max = Math.min( 0, max ); // Cannot be > 0
                            }
                            
                            OrganisationUnit source = new OrganisationUnit();
                            source.setId( unit );
                            
                            batchHandler.addObject( new MinMaxDataElement( source, dataElement, categoryOptionCombo, min, max, true ) );
                        }
                    }                        
                }
            }
        }
        
        log.info( "Min-max value generation done" );
        
        batchHandler.flush();
    }
}
