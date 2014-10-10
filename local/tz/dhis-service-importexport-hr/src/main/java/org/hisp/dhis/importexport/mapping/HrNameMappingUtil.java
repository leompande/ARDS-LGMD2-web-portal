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

import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.system.util.LoggingHashMap;

/**
 * @author John Francis Mukulu <john.f.mukulu@gmail.com>
 * @version $Id: HrNameMappingUtil.java 5727 2008-09-18 17:48:54Z larshelg $
 */
public class HrNameMappingUtil
{
	private static ThreadLocal<Map<Object, String>> attributeMap = new ThreadLocal<Map<Object, String>>();
	private static ThreadLocal<Map<Object, String>> attributeOptionsMap = new ThreadLocal<Map<Object, String>>();
	private static ThreadLocal<Map<Object, String>> hrDataSetMap = new ThreadLocal<Map<Object,String>>();
	private static ThreadLocal<Map<Object, String>> dataValuesMap = new ThreadLocal<Map<Object,String>>();
	private static ThreadLocal<Map<Object, String>> trainingMap = new ThreadLocal<Map<Object, String>>();
	private static ThreadLocal<Map<Object, String>> historyMap = new ThreadLocal<Map<Object,String>>();
	private static ThreadLocal<Map<Object, String>> inputTypeMap = new ThreadLocal<Map<Object,String>>();
	private static ThreadLocal<Map<Object, String>> dataTypeMap = new ThreadLocal<Map<Object, String>>();
	private static ThreadLocal<Map<Object, String>> personMap = new ThreadLocal<Map<Object,String>>();
	private static ThreadLocal<Map<Object, String>> attributeGroupMap = new ThreadLocal<Map<Object, String>>();
	private static ThreadLocal<Map<Object, String>> attributeOptionGroupMap = new ThreadLocal<Map<Object, String>>();
	private static ThreadLocal<Map<Object, String>> targetIndicatorMap = new ThreadLocal<Map<Object, String>>();
	
    private static ThreadLocal<Map<Object, String>> hrOrganisationUnitMap = new ThreadLocal<Map<Object, String>>();
    private static ThreadLocal<Map<Object, String>> hrOrganisationUnitGroupMap = new ThreadLocal<Map<Object, String>>();
    private static ThreadLocal<Map<Object, String>> hrOrganisationUnitGroupSetMap = new ThreadLocal<Map<Object, String>>();
    private static ThreadLocal<Map<Object, String>> hrDataElementMap = new ThreadLocal<Map<Object, String>>();


    // -------------------------------------------------------------------------
    // Control
    // -------------------------------------------------------------------------
    
    public static void clearMapping()
    {
    	attributeMap.remove();
    	attributeOptionsMap.remove();
    	hrDataSetMap.remove();
    	dataValuesMap.remove();
    	trainingMap.remove();
    	historyMap.remove();
    	inputTypeMap.remove();
    	dataTypeMap.remove();
    	personMap.remove();
    	targetIndicatorMap.remove();

        hrDataElementMap.remove();
        hrOrganisationUnitMap.remove();
        hrOrganisationUnitGroupMap.remove();
        hrOrganisationUnitGroupSetMap.remove();
    }

    // -------------------------------------------------------------------------
    // Attribute
    // -------------------------------------------------------------------------
    /**
     * Adds a map entry with Attribute identifier as key and name as value
     */
    public static void addAttributeMapping( Object attributeId, String attributeName)
    {
    	put( attributeMap, attributeId, attributeName );
    }
    /**
     * Returns a map with all Attribute identifier and name entries.
     */
    public static Map<Object, String> getAttributeMap()
    {
        return attributeMap.get() != null ? new HashMap<Object, String>( attributeMap.get() ) : new HashMap<Object, String>();
    }
    
    // ---------------------------------------------------------------------
    // Attribute Options
    // ---------------------------------------------------------------------
    /**
     * Adds a map entry with Attribute Options identifier as key and name as value
     */
    public static void addAttributeOptionsMapping(Object attributeOptionsId, String attributeOptionsValue)
    {
    	put( attributeOptionsMap, attributeOptionsId, attributeOptionsValue);
    }
    /**
     * Returns a map with all Attribute Options identifier and name entries.
     */
    public static Map<Object, String> getAttributeOptionsMap()
    {
        return attributeOptionsMap.get() != null ? new HashMap<Object, String>( attributeOptionsMap.get() ) : new HashMap<Object, String>();
    }
    
    // ---------------------------------------------------------------------
    // AttributeGroup
    // ---------------------------------------------------------------------
    /**
     * Adds a map entry with AttributeGroup identifier as key and name as value
     */
    public static void addAttributeGroupMapping(Object attributeGroupId, String name)
    {
    	put( attributeGroupMap, attributeGroupId, name);
    }
    /**
     * Returns a map with all AttributeGroup identifier and name entries.
     */
    public static Map<Object, String> getAttributeGroupMap()
    {
        return attributeGroupMap.get() != null ? new HashMap<Object, String>( attributeGroupMap.get() ) : new HashMap<Object, String>();
    }
    
    // ---------------------------------------------------------------------
    // AttributeOptionGroup
    // ---------------------------------------------------------------------
    /**
     * Adds a map entry with AttributeOptionGroup identifier as key and name as value
     */
    public static void addAttributeOptionGroupMapping(Object attributeOptionGroupId, String name)
    {
    	put( attributeOptionGroupMap, attributeOptionGroupId, name);
    }
    /**
     * Returns a map with all AttributeOptionGroup identifier and name entries.
     */
    public static Map<Object, String> getAttributeOptionGroupMap()
    {
        return attributeOptionGroupMap.get() != null ? new HashMap<Object, String>( attributeOptionGroupMap.get() ) : new HashMap<Object, String>();
    }
    
    // ---------------------------------------------------------------------
    // HrDataSets
    // ---------------------------------------------------------------------
    /**
     * Adds a map entry with HrDataSets identifier as key and name as value
     */
    public static void addHrDataSetMapping(Object hrDataSetId, String hrDataSetName)
    {
    	put( hrDataSetMap, hrDataSetId, hrDataSetName);
    }
    /**
     * Returns a map with all HrDataSet identifier and name entries.
     */
    public static Map<Object, String> getHrDataSetMap()
    {
        return hrDataSetMap.get() != null ? new HashMap<Object, String>( hrDataSetMap.get() ) : new HashMap<Object, String>();
    }
    
    // ---------------------------------------------------------------------
    // DataValues
    // --------------------------------------------------------------------
    /**
     * Adds a map entry with DataValue identifier as key and name as value
     */
    public static void addDataValuesMapping(Object dataValuesId,String uniqueDataValuesValue)
    {
    	put( dataValuesMap, dataValuesId, uniqueDataValuesValue);
    }
    /**
     * Returns a map with all DataValue identifier and name entries.
     */
    public static Map<Object, String> getDataValuesMap()
    {
        return dataValuesMap.get() != null ? new HashMap<Object, String>( dataValuesMap.get() ) : new HashMap<Object, String>();
    }
    
    // ---------------------------------------------------------------------
    // History
    // ---------------------------------------------------------------------
    /**
     * Adds a map entry with History identifier as key and name as value
     */
    public static void addHistoryMapping(Object historyId, String historyHistory)
    {
    	put( historyMap, historyId, historyHistory);
    }
    /**
     * Returns a map with all History identifier and name entries.
     */
    public static Map<Object, String> getHistoryMap()
    {
        return historyMap.get() != null ? new HashMap<Object, String>( historyMap.get() ) : new HashMap<Object, String>();
    }
    
    // ---------------------------------------------------------------------
    // Training
    // ---------------------------------------------------------------------
    /**
     * Adds a map entry with Training identifier as key and name as value
     */
    public static void addTrainingMapping(Object trainingId, String uniqueTraining)
    {
    	put( trainingMap, trainingId, uniqueTraining);
    }
    /**
     * Returns a map with all Training identifier and name entries.
     */
    public static Map<Object, String> getTrainingMap()
    {
        return trainingMap.get() != null ? new HashMap<Object, String>( trainingMap.get() ) : new HashMap<Object, String>();
    }

    // ---------------------------------------------------------------------
    // Person
    // ---------------------------------------------------------------------
    /**
     * Adds a map entry with Person identifier as key and name as value
     */
    public static void addPersonMapping(Object personId, String instance)
    {
    	put( personMap, personId, instance);
    }
    /**
     * Returns a map with all Person identifier and name entries.
     */
    public static Map<Object, String> getPersonMap()
    {
        return personMap.get() != null ? new HashMap<Object, String>( personMap.get() ) : new HashMap<Object, String>();
    }
    
    // ---------------------------------------------------------------------
    // Input Type
    // ---------------------------------------------------------------------
    /**
     * Adds a map entry with Input Type identifier as key and name as value
     */
    public static void addInputTypemapping(Object inputTypeId, String inputTypeName)
    {
    	put( inputTypeMap, inputTypeId, inputTypeName);
    }
    /**
     * Returns a map with all Input Type identifier and name entries.
     */
    public static Map<Object, String> getInputTypeMap()
    {
        return inputTypeMap.get() != null ? new HashMap<Object, String>( inputTypeMap.get() ) : new HashMap<Object, String>();
    }
    
    // ---------------------------------------------------------------------
    // Data Type
    // ---------------------------------------------------------------------
    /**
     * Adds a map entry with Data Type identifier as key and name as value
     */
    public static void addDataTypeMapping(Object dataTypeId, String dataTypeName)
    {
    	put( dataTypeMap, dataTypeId, dataTypeName);
    }
    /**
     * Returns a map with all DataType identifier and name entries.
     */
    public static Map<Object, String> getDatatypeMap()
    {
        return dataTypeMap.get() != null ? new HashMap<Object, String>( dataTypeMap.get() ) : new HashMap<Object, String>();
    }
    
    
    // ---------------------------------------------------------------------
    // TargetIndicator
    // ---------------------------------------------------------------------
    /**
     * Adds a map entry with TargetIndicator identifier as key and name as value
     */
    public static void addTargetIndicatorMapping(Object targetIndicatorId, String name)
    {
    	put( targetIndicatorMap, targetIndicatorId, name);
    }
    /**
     * Returns a map with all TargetIndicator identifier and name entries.
     */
    public static Map<Object, String> getTargetIndicatorMap()
    {
        return targetIndicatorMap.get() != null ? new HashMap<Object, String>( targetIndicatorMap.get() ) : new HashMap<Object, String>();
    }

    // -------------------------------------------------------------------------
    // HrDataElement
    // -------------------------------------------------------------------------

    /**
     * Adds a map entry with DataElement identifier as key and name as value.
     */
    public static void addHrDataElementMapping( Object dataElementId, String dataElementName )
    {
        put( hrDataElementMap, dataElementId, dataElementName );
    }
    
    /**
     * Returns a map with all DataElement identifier and name entries.
     */
    public static Map<Object, String> getHrDataElementMap()
    {
        return hrDataElementMap.get() != null ? new HashMap<Object, String>( hrDataElementMap.get() ) : new HashMap<Object, String>();
    }
    
    // -------------------------------------------------------------------------
    // HrOrganisationUnit
    // -------------------------------------------------------------------------

    /**
     * Adds a map entry with OrganisationUnit identifier as key and name as value.
     */
    public static void addHrOrganisationUnitMapping( Object organisationUnitId, String organisationUnitName )
    {
        put( hrOrganisationUnitMap, organisationUnitId, organisationUnitName );
    }
    
    /**
     * Returns a map with all OrganisationUnit identifier and name entries.
     */
    public static Map<Object, String> getHrOrganisationUnitMap()
    {
        return hrOrganisationUnitMap.get() != null ? new HashMap<Object, String>( hrOrganisationUnitMap.get() ) : new HashMap<Object, String>();
    }

    // -------------------------------------------------------------------------
    // HrOrganisationUnitGroup
    // -------------------------------------------------------------------------

    /**
     * Adds a map entry with OrganisationUnitGroup identifier as key and name as value.
     */
    public static void addHrOrganisationUnitGroupMapping( Object groupId, String groupName )
    {
        put( hrOrganisationUnitGroupMap, groupId, groupName );
    }
    
    /**
     * Returns a map with all OrganisationUnitGroup identifier and name entries.
     */
    public static Map<Object, String> getHrOrganisationUnitGroupMap()
    {
        return hrOrganisationUnitGroupMap.get() != null ? new HashMap<Object, String>( hrOrganisationUnitGroupMap.get() ) : new HashMap<Object, String>();
    }
    
    // -------------------------------------------------------------------------
    // HrOrganisationUnitGroupSet
    // -------------------------------------------------------------------------
    
    /**
     * Adds a map entry with OrganisationUnitGroupSet identifier as key and name as value.
     */
    public static void addHrGroupSetMapping( Object groupSetId, String groupSetName )
    {
        put( hrOrganisationUnitGroupSetMap, groupSetId, groupSetName );
    }
        
    /**
     * Returns a map with all OrganisationUnitGroupSet identifier and name entries.
     */
    public static Map<Object, String> getHrGroupSetMap()
    {
        return hrOrganisationUnitGroupSetMap.get() != null ? new HashMap<Object, String>( hrOrganisationUnitGroupSetMap.get() ) : new HashMap<Object, String>();
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private static void put( ThreadLocal<Map<Object, String>> threadLocal, Object key, String value )
    {
        Map<Object, String> map = threadLocal.get();
        
        if ( map == null )
        {
            map = new HashMap<Object, String>();
        }
        
        if ( !map.containsKey( key ) )
        {
            map.put( key, value );
        
            threadLocal.set( map );
        }
    }
}
