package org.hisp.dhis.importexport.mapping;

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

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.hisp.dhis.concept.Concept;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.datadictionary.DataDictionary;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.jdbc.batchhandler.ConceptBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.ConstantBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataDictionaryBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementCategoryBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementCategoryComboBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementCategoryOptionBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementGroupBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementGroupSetBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataSetBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.GroupSetBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.IndicatorBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.IndicatorGroupBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.IndicatorGroupSetBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.IndicatorTypeBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.OrganisationUnitBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.OrganisationUnitGroupBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.PeriodBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.ReportTableBatchHandler;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodStore;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.system.util.LoggingHashMap;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 * @version $Id: DefaultObjectMappingGenerator.java 6425 2008-11-22 00:08:57Z
 *          larshelg $
 */
public class DefaultObjectMappingGenerator
    implements ObjectMappingGenerator
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private BatchHandlerFactory batchHandlerFactory;

    public void setBatchHandlerFactory( BatchHandlerFactory batchHandlerFactory )
    {
        this.batchHandlerFactory = batchHandlerFactory;
    }

    private PeriodStore periodStore;

    public void setPeriodStore( PeriodStore periodStore )
    {
        this.periodStore = periodStore;
    }

    // -------------------------------------------------------------------------
    // Constant
    // -------------------------------------------------------------------------

    public Map<Object, Integer> getConstantMapping( boolean skipMapping )
    {
        BatchHandler<Constant> batchHandler = batchHandlerFactory.createBatchHandler( ConstantBatchHandler.class );

        return getMapping( batchHandler, NameMappingUtil.getConstantMap(), skipMapping );
    }

    // -------------------------------------------------------------------------
    // Concept
    // -------------------------------------------------------------------------

    public Map<Object, Integer> getConceptMapping( boolean skipMapping )
    {
        BatchHandler<Concept> batchHandler = batchHandlerFactory.createBatchHandler( ConceptBatchHandler.class );

        return getMapping( batchHandler, NameMappingUtil.getConceptMap(), skipMapping );
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    // -------------------------------------------------------------------------
    // DataElementCategory
    // -------------------------------------------------------------------------

    public Map<Object, Integer> getCategoryMapping( boolean skipMapping )
    {
        BatchHandler<DataElementCategory> batchHandler = batchHandlerFactory
            .createBatchHandler( DataElementCategoryBatchHandler.class );

        return getMapping( batchHandler, NameMappingUtil.getCategoryMap(), skipMapping );
    }

    // -------------------------------------------------------------------------
    // DataElementCategoryCombo
    // -------------------------------------------------------------------------

    public Map<Object, Integer> getCategoryComboMapping( boolean skipMapping )
    {
        BatchHandler<DataElementCategoryCombo> batchHandler = batchHandlerFactory
            .createBatchHandler( DataElementCategoryComboBatchHandler.class );

        return getMapping( batchHandler, NameMappingUtil.getCategoryComboMap(), skipMapping );
    }

    // -------------------------------------------------------------------------
    // DataElementCategoryOption
    // -------------------------------------------------------------------------

    public Map<Object, Integer> getCategoryOptionMapping( boolean skipMapping )
    {
        BatchHandler<DataElementCategoryOption> batchHandler = batchHandlerFactory
            .createBatchHandler( DataElementCategoryOptionBatchHandler.class );

        return getMapping( batchHandler, NameMappingUtil.getCategoryOptionMap(), skipMapping );
    }

    // -------------------------------------------------------------------------
    // DataElementCategoryOptionCombo
    // -------------------------------------------------------------------------

    public Map<Object, Integer> getCategoryOptionComboMapping( boolean skipMapping )
    {
        return getMapping( NameMappingUtil.getCategoryOptionComboMap(), skipMapping );
    }

    // -------------------------------------------------------------------------
    // DataElement
    // -------------------------------------------------------------------------

    public Map<Object, Integer> getDataElementMapping( boolean skipMapping )
    {
        BatchHandler<DataElement> batchHandler = batchHandlerFactory.createBatchHandler( DataElementBatchHandler.class );

        return getMapping( batchHandler, NameMappingUtil.getDataElementMap(), skipMapping );
    }

    // -------------------------------------------------------------------------
    // DataElementGroup
    // -------------------------------------------------------------------------

    public Map<Object, Integer> getDataElementGroupMapping( boolean skipMapping )
    {
        BatchHandler<DataElementGroup> batchHandler = batchHandlerFactory
            .createBatchHandler( DataElementGroupBatchHandler.class );

        return getMapping( batchHandler, NameMappingUtil.getDataElementGroupMap(), skipMapping );
    }

    // -------------------------------------------------------------------------
    // DataElementGroupSet
    // -------------------------------------------------------------------------

    public Map<Object, Integer> getDataElementGroupSetMapping( boolean skipMapping )
    {
        BatchHandler<DataElementGroupSet> batchHandler = batchHandlerFactory
            .createBatchHandler( DataElementGroupSetBatchHandler.class );

        return getMapping( batchHandler, NameMappingUtil.getDataElementGroupSetMap(), skipMapping );
    }

    // -------------------------------------------------------------------------
    // Indicator
    // -------------------------------------------------------------------------

    public Map<Object, Integer> getIndicatorMapping( boolean skipMapping )
    {
        BatchHandler<Indicator> batchHandler = batchHandlerFactory.createBatchHandler( IndicatorBatchHandler.class );

        return getMapping( batchHandler, NameMappingUtil.getIndicatorMap(), skipMapping );
    }

    // -------------------------------------------------------------------------
    // IndicatorGroup
    // -------------------------------------------------------------------------

    public Map<Object, Integer> getIndicatorGroupMapping( boolean skipMapping )
    {
        BatchHandler<IndicatorGroup> batchHandler = batchHandlerFactory
            .createBatchHandler( IndicatorGroupBatchHandler.class );

        return getMapping( batchHandler, NameMappingUtil.getIndicatorGroupMap(), skipMapping );
    }

    // -------------------------------------------------------------------------
    // IndicatorGroupSet
    // -------------------------------------------------------------------------

    public Map<Object, Integer> getIndicatorGroupSetMapping( boolean skipMapping )
    {
        BatchHandler<IndicatorGroupSet> batchHandler = batchHandlerFactory
            .createBatchHandler( IndicatorGroupSetBatchHandler.class );

        return getMapping( batchHandler, NameMappingUtil.getIndicatorGroupSetMap(), skipMapping );
    }

    // -------------------------------------------------------------------------
    // IndicatorType
    // -------------------------------------------------------------------------

    public Map<Object, Integer> getIndicatorTypeMapping( boolean skipMapping )
    {
        BatchHandler<IndicatorType> batchHandler = batchHandlerFactory
            .createBatchHandler( IndicatorTypeBatchHandler.class );

        return getMapping( batchHandler, NameMappingUtil.getIndicatorTypeMap(), skipMapping );
    }

    // -------------------------------------------------------------------------
    // DataDictionary
    // -------------------------------------------------------------------------

    public Map<Object, Integer> getDataDictionaryMapping( boolean skipMapping )
    {
        BatchHandler<DataDictionary> batchHandler = batchHandlerFactory
            .createBatchHandler( DataDictionaryBatchHandler.class );

        return getMapping( batchHandler, NameMappingUtil.getDataDictionaryMap(), skipMapping );
    }

    // -------------------------------------------------------------------------
    // DataSet
    // -------------------------------------------------------------------------

    public Map<Object, Integer> getDataSetMapping( boolean skipMapping )
    {
        BatchHandler<DataSet> batchHandler = batchHandlerFactory.createBatchHandler( DataSetBatchHandler.class );

        return getMapping( batchHandler, NameMappingUtil.getDataSetMap(), skipMapping );
    }

    // -------------------------------------------------------------------------
    // OrganisationUnit
    // -------------------------------------------------------------------------

    public Map<Object, Integer> getOrganisationUnitMapping( boolean skipMapping )
    {
        BatchHandler<OrganisationUnit> batchHandler = batchHandlerFactory
            .createBatchHandler( OrganisationUnitBatchHandler.class );

        return getMapping( batchHandler, NameMappingUtil.getOrganisationUnitMap(), skipMapping );
    }

    // -------------------------------------------------------------------------
    // OrganisationUnitGroup
    // -------------------------------------------------------------------------

    public Map<Object, Integer> getOrganisationUnitGroupMapping( boolean skipMapping )
    {
        BatchHandler<OrganisationUnitGroup> batchHandler = batchHandlerFactory
            .createBatchHandler( OrganisationUnitGroupBatchHandler.class );

        return getMapping( batchHandler, NameMappingUtil.getOrganisationUnitGroupMap(), skipMapping );
    }

    // -------------------------------------------------------------------------
    // OrganisationUnitGroupSet
    // -------------------------------------------------------------------------

    public Map<Object, Integer> getOrganisationUnitGroupSetMapping( boolean skipMapping )
    {
        BatchHandler<OrganisationUnitGroupSet> batchHandler = batchHandlerFactory
            .createBatchHandler( GroupSetBatchHandler.class );

        return getMapping( batchHandler, NameMappingUtil.getGroupSetMap(), skipMapping );
    }

    // -------------------------------------------------------------------------
    // ReportTable
    // -------------------------------------------------------------------------

    public Map<Object, Integer> getReportTableMapping( boolean skipMapping )
    {
        BatchHandler<ReportTable> batchHandler = batchHandlerFactory.createBatchHandler( ReportTableBatchHandler.class );

        return getMapping( batchHandler, NameMappingUtil.getReportTableMap(), skipMapping );
    }

    // -------------------------------------------------------------------------
    // Period
    // -------------------------------------------------------------------------

    public Map<Object, Integer> getPeriodMapping( boolean skipMapping ) // Original
    // identifier,
    // new
    // identifier
    {
        BatchHandler<Period> batchHandler = batchHandlerFactory.createBatchHandler( PeriodBatchHandler.class );

        batchHandler.init();

        Map<Object, Integer> periodMap = new LoggingHashMap<Object, Integer>();

        Map<Object, Period> mapping = NameMappingUtil.getPeriodMap();

        if ( mapping != null )
        {
            for ( Map.Entry<Object, Period> map : mapping.entrySet() )
            {
                int identifier = skipMapping ? getKey( map.getKey() ) : batchHandler.getObjectIdentifier( map
                    .getValue() );

                verifyIdentifier( identifier, skipMapping, map.getValue().toString() );

                periodMap.put( map.getKey(), identifier );
            }

            verifyMap( mapping, periodMap );
        }

        batchHandler.flush();

        return periodMap;
    }

    public Map<Period, Integer> getPeriodObjectMapping( boolean skipMapping ) // Original
    // object,
    // new
    // identifier
    {
        BatchHandler<Period> batchHandler = batchHandlerFactory.createBatchHandler( PeriodBatchHandler.class );

        batchHandler.init();

        Map<Period, Integer> periodMap = new LoggingHashMap<Period, Integer>();

        Collection<Period> periods = NameMappingUtil.getPeriodMap().values();

        if ( periods != null )
        {
            for ( Period period : periods )
            {
                int identifier = skipMapping ? period.getId() : batchHandler.getObjectIdentifier( period );

                verifyIdentifier( identifier, skipMapping, period.toString() );

                periodMap.put( period, identifier );
            }

            if ( periodMap.size() != periods.size() )
            {
                throw new RuntimeException( "The period mapping contains duplicates" );
            }
        }

        batchHandler.flush();

        return periodMap;
    }

    // -------------------------------------------------------------------------
    // PeriodType
    // -------------------------------------------------------------------------

    @Transactional
    public Map<String, Integer> getPeriodTypeMapping()
    {
        Map<String, Integer> periodTypeMap = new LoggingHashMap<String, Integer>();

        Collection<PeriodType> periodTypes = periodStore.getAllPeriodTypes();

        for ( PeriodType type : periodTypes )
        {
            periodTypeMap.put( type.getName(), type.getId() );
        }

        return periodTypeMap;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Map<Object, Integer> getMapping( BatchHandler<?> batchHandler, Map<Object, String> nameMap,
        boolean skipMapping )
    {
        batchHandler.init();

        Map<Object, Integer> identifierMap = new LoggingHashMap<Object, Integer>();

        if ( nameMap != null )
        {
            for ( Map.Entry<Object, String> nameMapEntry : nameMap.entrySet() )
            {
                int identifier = skipMapping ? getKey( nameMapEntry.getKey() ) : batchHandler
                    .getObjectIdentifier( nameMapEntry.getValue() );

                verifyIdentifier( identifier, skipMapping, nameMapEntry.getValue() );

                identifierMap.put( nameMapEntry.getKey(), identifier );
            }

            verifyMap( nameMap, identifierMap );
        }

        batchHandler.flush();

        return identifierMap;
    }

    private Map<Object, Integer> getMapping( Map<Object, DataElementCategoryOptionCombo> categoryOptionComboMap,
        boolean skipMapping )
    {
        Map<Object, Integer> identifierMap = new LoggingHashMap<Object, Integer>();

        if ( categoryOptionComboMap != null )
        {
            for ( Map.Entry<Object, DataElementCategoryOptionCombo> map : categoryOptionComboMap.entrySet() )
            {
                int identifier = 0;

                if ( skipMapping )
                {
                    identifier = getKey( map.getKey() );
                }
                else
                {
                    DataElementCategoryOptionCombo temp = map.getValue();

                    DataElementCategoryOptionCombo categoryOptionCombo = categoryService
                        .getDataElementCategoryOptionCombo( temp );

                    if ( categoryOptionCombo == null )
                    {
                        throw new RuntimeException( "DataElementCategoryOptionCombo does not exist: " + temp );
                    }

                    identifier = categoryOptionCombo.getId();
                }

                verifyIdentifier( identifier, skipMapping, "[DataElementCategoryOptionCombo]" );

                identifierMap.put( map.getKey(), identifier );
            }

            verifyMap( categoryOptionComboMap, identifierMap );
        }

        return identifierMap;
    }

    private void verifyIdentifier( Integer identifier, boolean skipMapping, String name )
    {
        if ( identifier == 0 && !skipMapping )
        {
            throw new RuntimeException( "The object named '" + name + "' does not exist" );
        }
    }

    private void verifyMap( Map<?, ?> nameMap, Map<?, ?> identifierMap )
    {
        if ( nameMap.size() != identifierMap.size() )
        {
            throw new RuntimeException( "The name mapping contains duplicate names" );
        }
    }

    /**
     * Return the Integer value of the Object argument. If the Object is not of
     * type Integer, -1 is returned.
     */
    private int getKey( Object key )
    {
        int value = -1;

        try
        {
            value = Integer.parseInt( String.valueOf( key ) );
        }
        catch ( NumberFormatException ex )
        {
            // Object is not of type Integer
        }

        return value;
    }

}
