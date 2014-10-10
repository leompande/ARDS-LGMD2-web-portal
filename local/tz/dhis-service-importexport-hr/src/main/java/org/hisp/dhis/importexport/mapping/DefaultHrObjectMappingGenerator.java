package org.hisp.dhis.importexport.mapping;

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

import java.util.Collection;
import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.hisp.dhis.concept.Concept;
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
import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeGroup;
import org.hisp.dhis.hr.AttributeOptionGroup;
import org.hisp.dhis.hr.AttributeOptions;
import org.hisp.dhis.hr.DataType;
import org.hisp.dhis.hr.DataValues;
import org.hisp.dhis.hr.History;
import org.hisp.dhis.hr.HrDataSet;
import org.hisp.dhis.hr.InputType;
import org.hisp.dhis.hr.Person;
import org.hisp.dhis.hr.TargetIndicator;
import org.hisp.dhis.hr.Training;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.jdbc.batchhandler.AttributeOptionsBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.AttributeOptionGroupBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.AttributeGroupBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.ConceptBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataDictionaryBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.AttributeBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementCategoryBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementCategoryComboBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementCategoryOptionBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementGroupBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataElementGroupSetBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataSetBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.GroupSetBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.HrDataSetBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataValuesBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.HistoryBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.InputTypeBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataTypeBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.PersonBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.TargetIndicatorBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.TrainingBatchHandler;
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
 * @author John Francis Mukulu <john.f.mukulu@gmail.com>
 * @version $Id: DefaultObjectMappingGenerator.java 6425 2008-11-22 00:08:57Z larshelg $
 */
public class DefaultHrObjectMappingGenerator
    implements HrObjectMappingGenerator
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
    // HrDataElement
    // -------------------------------------------------------------------------

    public Map<Object, Integer> getHrDataElementMapping( boolean skipMapping )
    {
        BatchHandler<DataElement> batchHandler = batchHandlerFactory.createBatchHandler( DataElementBatchHandler.class );
        
        return getMapping( batchHandler, HrNameMappingUtil.getHrDataElementMap(), skipMapping );
    }
    
    // -------------------------------------------------------------------------
    // Attribute
    // -------------------------------------------------------------------------

    public Map<Object, Integer> getAttributeMapping( boolean skipMapping )
    {
        BatchHandler<Attribute> batchHandler = batchHandlerFactory.createBatchHandler( AttributeBatchHandler.class );
        
        return getMapping( batchHandler, HrNameMappingUtil.getAttributeMap(), skipMapping );
    }
    
    // -------------------------------------------------------------------------
    // Attribute Options
    // -------------------------------------------------------------------------
    public Map<Object, Integer> getAttributeOptionsMapping( boolean skipMapping )
    {
        BatchHandler<AttributeOptions> batchHandler = batchHandlerFactory.createBatchHandler( AttributeOptionsBatchHandler.class );
        
        return getMapping( batchHandler, HrNameMappingUtil.getAttributeOptionsMap(), skipMapping );
    }
    
    // -------------------------------------------------------------------------
    // Attribute Group
    // -------------------------------------------------------------------------
    public Map<Object, Integer> getAttributeGroupMapping( boolean skipMapping )
    {
    	BatchHandler<AttributeGroup> batchHandler = batchHandlerFactory.createBatchHandler( AttributeGroupBatchHandler.class );
        
        return getMapping( batchHandler, HrNameMappingUtil.getAttributeGroupMap(), skipMapping );
    }
    
    // -------------------------------------------------------------------------
    // Attribute Option Group
    // -------------------------------------------------------------------------
    public Map<Object, Integer> getAttributeOptionGroupMapping( boolean skipMapping )
    {
    	BatchHandler<AttributeOptionGroup> batchHandler = batchHandlerFactory.createBatchHandler( AttributeOptionGroupBatchHandler.class );
        
        return getMapping( batchHandler, HrNameMappingUtil.getAttributeOptionGroupMap(), skipMapping );
    }
    
    // -------------------------------------------------------------------------
    // HrDataSet
    // -------------------------------------------------------------------------
    public Map<Object, Integer> getHrDataSetMapping( boolean skipMapping )
    {
    	BatchHandler<HrDataSet> batchHandler = batchHandlerFactory.createBatchHandler( HrDataSetBatchHandler.class );
        
        return getMapping( batchHandler, HrNameMappingUtil.getHrDataSetMap(), skipMapping );
    }
    
    // -------------------------------------------------------------------------
    // DataValues
    // -------------------------------------------------------------------------
    public Map<Object, Integer> getDataValuesMapping( boolean skipMapping )
    {
    	BatchHandler<DataValues> batchHandler = batchHandlerFactory.createBatchHandler( DataValuesBatchHandler.class );
        
        return getMapping( batchHandler, HrNameMappingUtil.getDataValuesMap(), skipMapping );
    }
    
    // -------------------------------------------------------------------------
    // History
    // -------------------------------------------------------------------------
    public Map<Object, Integer> getHistoryMapping( boolean skipMapping )
    {
    	BatchHandler<History> batchHandler = batchHandlerFactory.createBatchHandler( HistoryBatchHandler.class );
        
        return getMapping( batchHandler, HrNameMappingUtil.getHistoryMap(), skipMapping );
    }
    
    // -------------------------------------------------------------------------
    // Training
    // -------------------------------------------------------------------------
    public Map<Object, Integer> getTrainingMapping( boolean skipMapping )
    {
    	BatchHandler<Training> batchHandler = batchHandlerFactory.createBatchHandler( TrainingBatchHandler.class );
        
        return getMapping( batchHandler, HrNameMappingUtil.getTrainingMap(), skipMapping );
    }
    
    // -------------------------------------------------------------------------
    // Person
    // -------------------------------------------------------------------------
    public Map<Object, Integer> getPersonMapping( boolean skipMapping )
    {
    	BatchHandler<Person> batchHandler = batchHandlerFactory.createBatchHandler( PersonBatchHandler.class );
        return getMapping( batchHandler, HrNameMappingUtil.getPersonMap(), skipMapping );
    }
    
    // -------------------------------------------------------------------------
    // TargetIndicator
    // -------------------------------------------------------------------------
    public Map<Object, Integer> getTargetIndicator( boolean skipMapping )
    {
    	BatchHandler<TargetIndicator> batchHandler = batchHandlerFactory.createBatchHandler( TargetIndicatorBatchHandler.class );
        
        return getMapping( batchHandler, HrNameMappingUtil.getTargetIndicatorMap(), skipMapping );
    }
    
    // -------------------------------------------------------------------------
    // InputType
    // -------------------------------------------------------------------------
    public Map<Object, Integer> getInputTypeMapping( boolean skipMapping )
    {
    	BatchHandler<InputType> batchHandler = batchHandlerFactory.createBatchHandler( InputTypeBatchHandler.class );
        
        return getMapping( batchHandler, HrNameMappingUtil.getInputTypeMap(), skipMapping );
    }
    
    // -------------------------------------------------------------------------
    // DataType
    // -------------------------------------------------------------------------
    public Map<Object, Integer> getDataTypeMapping( boolean skipMapping )
    {
    	BatchHandler<DataType> batchHandler = batchHandlerFactory.createBatchHandler( DataTypeBatchHandler.class );
        
        return getMapping( batchHandler, HrNameMappingUtil.getDatatypeMap(), skipMapping );
    }

    // -------------------------------------------------------------------------
    // HrOrganisationUnit
    // -------------------------------------------------------------------------

    public Map<Object, Integer> getHrOrganisationUnitMapping( boolean skipMapping )
    {
        BatchHandler<OrganisationUnit> batchHandler = batchHandlerFactory.createBatchHandler( OrganisationUnitBatchHandler.class );

        return getMapping( batchHandler, HrNameMappingUtil.getHrOrganisationUnitMap(), skipMapping );
    }

    // -------------------------------------------------------------------------
    // OrganisationUnitGroup
    // -------------------------------------------------------------------------

    public Map<Object, Integer> getHrOrganisationUnitGroupMapping( boolean skipMapping )
    {
        BatchHandler<OrganisationUnitGroup> batchHandler = batchHandlerFactory.createBatchHandler( OrganisationUnitGroupBatchHandler.class );
        
        return getMapping( batchHandler, HrNameMappingUtil.getHrOrganisationUnitGroupMap(), skipMapping );
    }

    // -------------------------------------------------------------------------
    // HrOrganisationUnitGroupSet
    // -------------------------------------------------------------------------

    public Map<Object, Integer> getHrOrganisationUnitGroupSetMapping( boolean skipMapping )
    {
        BatchHandler<OrganisationUnitGroupSet> batchHandler = batchHandlerFactory.createBatchHandler( GroupSetBatchHandler.class );
        
        return getMapping( batchHandler, HrNameMappingUtil.getHrGroupSetMap(), skipMapping );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Map<Object, Integer> getMapping( BatchHandler<?> batchHandler, Map<Object, String> nameMap, boolean skipMapping )
    {
        batchHandler.init();

        Map<Object, Integer> identifierMap = new LoggingHashMap<Object, Integer>();
        
        if ( nameMap != null )
        {
            for ( Map.Entry<Object, String> nameMapEntry : nameMap.entrySet() )
            {
                int identifier = skipMapping ? getKey( nameMapEntry.getKey() ) : batchHandler.getObjectIdentifier( nameMapEntry.getValue() );
                
                verifyIdentifier( identifier, skipMapping, nameMapEntry.getValue() );

                identifierMap.put( nameMapEntry.getKey(), identifier );
            }
            
            verifyMap( nameMap, identifierMap );
        }
        
        batchHandler.flush();
        
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
