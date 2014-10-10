package org.hisp.dhis.common;

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

import static org.hisp.dhis.common.DimensionalObject.CATEGORYOPTIONCOMBO_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.DATAELEMENT_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.DATAELEMENT_OPERAND_ID;
import static org.hisp.dhis.common.DimensionalObject.DATASET_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.DATA_X_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.DIMENSION_SEP;
import static org.hisp.dhis.common.DimensionalObject.INDICATOR_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.ORGUNIT_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.PERIOD_DIM_ID;
import static org.hisp.dhis.organisationunit.OrganisationUnit.KEY_LEVEL;
import static org.hisp.dhis.organisationunit.OrganisationUnit.KEY_ORGUNIT_GROUP;
import static org.hisp.dhis.organisationunit.OrganisationUnit.KEY_USER_ORGUNIT;
import static org.hisp.dhis.organisationunit.OrganisationUnit.KEY_USER_ORGUNIT_CHILDREN;
import static org.hisp.dhis.organisationunit.OrganisationUnit.KEY_USER_ORGUNIT_GRANDCHILDREN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.common.adapter.JacksonPeriodDeserializer;
import org.hisp.dhis.common.adapter.JacksonPeriodSerializer;
import org.hisp.dhis.common.annotation.Scanned;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.DimensionalView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryDimension;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.ConfigurablePeriod;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.RelativePeriodEnum;
import org.hisp.dhis.period.RelativePeriods;
import org.hisp.dhis.period.comparator.AscendingPeriodComparator;
import org.hisp.dhis.user.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * This class contains associations to dimensional meta-data. Should typically
 * be sub-classed by analytical objects like tables, maps and charts.
 * 
 * Implementation note: Objects currently managing this class are AnalyticsService,
 * DefaultDimensionService and the getDimensionalObject and getDimensionalObjectList 
 * methods of this class.
 * 
 * @author Lars Helge Overland
 */
public abstract class BaseAnalyticalObject
    extends BaseIdentifiableObject
{    
    // -------------------------------------------------------------------------
    // Persisted properties
    // -------------------------------------------------------------------------

    @Scanned
    protected List<Indicator> indicators = new ArrayList<Indicator>();

    @Scanned
    protected List<DataElement> dataElements = new ArrayList<DataElement>();

    @Scanned
    protected List<DataElementOperand> dataElementOperands = new ArrayList<DataElementOperand>();
    
    @Scanned
    protected List<DataSet> dataSets = new ArrayList<DataSet>();

    @Scanned
    protected List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>();

    @Scanned
    protected List<Period> periods = new ArrayList<Period>();
    
    protected RelativePeriods relatives;

    @Scanned
    protected List<DataElementCategoryDimension> categoryDimensions = new ArrayList<DataElementCategoryDimension>();
    
    @Scanned
    protected List<DataElementGroup> dataElementGroups = new ArrayList<DataElementGroup>();

    @Scanned
    protected List<OrganisationUnitGroup> organisationUnitGroups = new ArrayList<OrganisationUnitGroup>();

    protected boolean userOrganisationUnit;

    protected boolean userOrganisationUnitChildren;
    
    protected boolean userOrganisationUnitGrandChildren;

    @Scanned
    protected List<Integer> organisationUnitLevels = new ArrayList<Integer>();

    @Scanned
    protected List<OrganisationUnitGroup> itemOrganisationUnitGroups = new ArrayList<OrganisationUnitGroup>();
    
    protected boolean rewindRelativePeriods;

    // -------------------------------------------------------------------------
    // Analytical properties
    // -------------------------------------------------------------------------

    protected transient List<DimensionalObject> columns = new ArrayList<DimensionalObject>();
    
    protected transient List<DimensionalObject> rows = new ArrayList<DimensionalObject>();
    
    protected transient List<DimensionalObject> filters = new ArrayList<DimensionalObject>();
    
    protected transient Map<String, String> parentGraphMap = new HashMap<String, String>();

    // -------------------------------------------------------------------------
    // Transient properties
    // -------------------------------------------------------------------------

    protected transient List<OrganisationUnit> transientOrganisationUnits = new ArrayList<OrganisationUnit>();
    
    protected transient List<DataElementCategoryOptionCombo> transientCategoryOptionCombos = new ArrayList<DataElementCategoryOptionCombo>();
        
    protected transient Date relativePeriodDate;

    protected transient OrganisationUnit relativeOrganisationUnit;
        
    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public abstract void init( User user, Date date, OrganisationUnit organisationUnit, 
        List<OrganisationUnit> organisationUnitsAtLevel, List<OrganisationUnit> organisationUnitsInGroups, I18nFormat format );
    
    public abstract void populateAnalyticalProperties();
    
    public boolean hasUserOrgUnit()
    {
        return userOrganisationUnit || userOrganisationUnitChildren || userOrganisationUnitGrandChildren;
    }
    
    public boolean hasRelativePeriods()
    {
        return relatives != null && !relatives.isEmpty();
    }
    
    public boolean hasOrganisationUnitLevels()
    {
        return organisationUnitLevels != null && !organisationUnitLevels.isEmpty();
    }
    
    public boolean hasItemOrganisationUnitGroups()
    {
        return itemOrganisationUnitGroups != null && !itemOrganisationUnitGroups.isEmpty();
    }
    
    protected void addTransientOrganisationUnits( Collection<OrganisationUnit> organisationUnits )
    {
        if ( organisationUnits != null )
        {
            this.transientOrganisationUnits.addAll( organisationUnits );
        }
    }
    
    protected void addTransientOrganisationUnit( OrganisationUnit organisationUnit )
    {
        if ( organisationUnit != null )
        {
            this.transientOrganisationUnits.add( organisationUnit );
        }
    }
    
    /**
     * Assembles a DimensionalObject based on the persisted properties of this
     * AnalyticalObject. Collapses indicators, data elements, data element 
     * operands and data sets into the dx dimension.
     * 
     * Collapses fixed and relative periods into the pe dimension. Collapses
     * fixed and user organisation units into the ou dimension.
     * 
     * @param dimension the dimension identifier.
     * @param date the date used for generating relative periods.
     * @param user the current user.
     * @param dynamicNames whether to use dynamic or static names.
     * @param format the I18nFormat.
     * @return a DimensionalObject.
     */
    protected DimensionalObject getDimensionalObject( String dimension, Date date, User user, boolean dynamicNames, 
        List<OrganisationUnit> organisationUnitsAtLevel, List<OrganisationUnit> organisationUnitsInGroups, I18nFormat format )
    {       
        List<NameableObject> items = new ArrayList<NameableObject>();
        
        DimensionType type = null;

        List<String> categoryDims = getCategoryDims();
        
        if ( DATA_X_DIM_ID.equals( dimension ) )
        {
            items.addAll( indicators );
            items.addAll( dataElements );
            items.addAll( dataElementOperands );
            items.addAll( dataSets );
            
            type = DimensionType.DATA_X;
        }
        else if ( PERIOD_DIM_ID.equals( dimension ) )
        {
            setPeriodNames( periods, dynamicNames, format );
            
            items.addAll( periods );
            
            if ( hasRelativePeriods() )
            {
                if ( rewindRelativePeriods )
                {
                    items.addAll( relatives.getRewindedRelativePeriods( 1, date, format, dynamicNames ) );
                }
                else
                {
                    items.addAll( relatives.getRelativePeriods( date, format, dynamicNames ) );
                }
            }
            
            type = DimensionType.PERIOD;
        }
        else if ( ORGUNIT_DIM_ID.equals( dimension ) )
        {
            items.addAll( organisationUnits );
            items.addAll( transientOrganisationUnits );
            
            if ( userOrganisationUnit && user != null && user.hasOrganisationUnit() )
            {
                items.add( user.getOrganisationUnit() );
            }
            
            if ( userOrganisationUnitChildren && user != null && user.hasOrganisationUnit() )
            {
                items.addAll( user.getOrganisationUnit().getSortedChildren() );
            }
            
            if ( userOrganisationUnitGrandChildren && user != null && user.hasOrganisationUnit() )
            {
                items.addAll( user.getOrganisationUnit().getSortedGrandChildren() );
            }
            
            if ( organisationUnitLevels != null && !organisationUnitLevels.isEmpty() && organisationUnitsAtLevel != null )
            {
                items.addAll( organisationUnitsAtLevel ); // Must be set externally
            }
            
            if ( itemOrganisationUnitGroups != null && !itemOrganisationUnitGroups.isEmpty() && organisationUnitsInGroups != null )
            {
                items.addAll( organisationUnitsInGroups ); // Must be set externally
            }
            
            type = DimensionType.ORGANISATIONUNIT;
        }
        else if ( CATEGORYOPTIONCOMBO_DIM_ID.equals( dimension ) )
        {
            items.addAll( transientCategoryOptionCombos );
            
            type = DimensionType.CATEGORY_OPTION_COMBO;
        }
        else if ( categoryDims.contains( dimension ) )
        {
            DataElementCategoryDimension categoryDimension = categoryDimensions.get( categoryDims.indexOf( dimension ) );
            
            items.addAll( categoryDimension.getItems() );
            
            type = DimensionType.CATEGORY;
        }
        else // Group set
        {
            ListMap<String, NameableObject> deGroupMap = new ListMap<String, NameableObject>();
            
            for ( DataElementGroup group : dataElementGroups )
            {
                if ( group.getGroupSet() != null )
                {
                    deGroupMap.putValue( group.getGroupSet().getDimension(), group );
                }
            }
            
            if ( deGroupMap.containsKey( dimension ) )
            {
                items.addAll( deGroupMap.get( dimension ) );
                
                type = DimensionType.DATAELEMENT_GROUPSET;
            }

            ListMap<String, NameableObject> ouGroupMap = new ListMap<String, NameableObject>();
            
            for ( OrganisationUnitGroup group : organisationUnitGroups )
            {
                if ( group.getGroupSet() != null )
                {
                    ouGroupMap.putValue( group.getGroupSet().getUid(), group );
                }
            }
            
            if ( ouGroupMap.containsKey( dimension ) )
            {
                items.addAll( ouGroupMap.get( dimension ) );
                
                type = DimensionType.ORGANISATIONUNIT_GROUPSET;
            }
        }
        
        IdentifiableObjectUtils.removeDuplicates( items );
        
        return new BaseDimensionalObject( dimension, type, items );
    }
    
    /**
     * Assembles a list of DimensionalObjects based on the concrete objects in
     * this BaseAnalyticalObject. Explodes the dx dimension into the in|de|dc|ds 
     * concrete objects and returns them as separate DimensionalObjects.
     * 
     * Merges fixed and relative periods into the pe dimension, where the
     * RelativePeriods object is represented by enums (e.g. LAST_MONTH). Merges 
     * fixed and user organisation units into the ou dimension, where user 
     * organisation units properties are represented by enums (e.g. USER_ORG_UNIT).
     * 
     * This method is useful when serializing the AnalyticalObject.
     * 
     * @param dimension the dimension identifier.
     * @return a list of DimensionalObjects.
     */
    protected List<DimensionalObject> getDimensionalObjectList( String dimension )
    {
        List<DimensionalObject> objects = new ArrayList<DimensionalObject>();
        
        List<String> categoryDims = getCategoryDims();
        
        if ( DATA_X_DIM_ID.equals( dimension ) )
        {
            if ( !indicators.isEmpty() )
            {
                objects.add( new BaseDimensionalObject( INDICATOR_DIM_ID, DimensionType.INDICATOR, indicators ) );
            }
            
            if ( !dataElements.isEmpty() )
            {
                objects.add( new BaseDimensionalObject( DATAELEMENT_DIM_ID, DimensionType.DATAELEMENT, dataElements ) );
            }
            
            if ( !dataElementOperands.isEmpty() )
            {
                objects.add( new BaseDimensionalObject( DATAELEMENT_OPERAND_ID, DimensionType.DATAELEMENT_OPERAND, dataElementOperands ) );
            }
            
            if ( !dataSets.isEmpty() )
            {
                objects.add( new BaseDimensionalObject( DATASET_DIM_ID, DimensionType.DATASET, dataSets ) );
            }
        }
        else if ( PERIOD_DIM_ID.equals( dimension ) && ( !periods.isEmpty() || hasRelativePeriods() ) )
        {
            List<Period> periodList = new ArrayList<Period>( periods );
            
            if ( hasRelativePeriods() )
            {
                List<RelativePeriodEnum> list = relatives.getRelativePeriodEnums();

                for ( RelativePeriodEnum periodEnum : list )
                {
                    periodList.add( new ConfigurablePeriod( periodEnum.toString() ) );
                }
            }
            
            Collections.sort( periodList, new AscendingPeriodComparator() );
            
            objects.add( new BaseDimensionalObject( dimension, DimensionType.PERIOD, periodList ) );
        }        
        else if ( ORGUNIT_DIM_ID.equals( dimension ) && ( !organisationUnits.isEmpty() || !transientOrganisationUnits.isEmpty() || hasUserOrgUnit() ) )
        {
            List<NameableObject> ouList = new ArrayList<NameableObject>();
            ouList.addAll( organisationUnits );
            ouList.addAll( transientOrganisationUnits );
            
            if ( userOrganisationUnit )
            {
                ouList.add( new BaseNameableObject( KEY_USER_ORGUNIT, KEY_USER_ORGUNIT, KEY_USER_ORGUNIT ) );
            }
            
            if ( userOrganisationUnitChildren )
            {
                ouList.add( new BaseNameableObject( KEY_USER_ORGUNIT_CHILDREN, KEY_USER_ORGUNIT_CHILDREN, KEY_USER_ORGUNIT_CHILDREN ) );
            }
            
            if ( userOrganisationUnitGrandChildren )
            {
                ouList.add( new BaseNameableObject( KEY_USER_ORGUNIT_GRANDCHILDREN, KEY_USER_ORGUNIT_GRANDCHILDREN, KEY_USER_ORGUNIT_GRANDCHILDREN ) );
            }
            
            if ( organisationUnitLevels != null && !organisationUnitLevels.isEmpty() )
            {
                for ( Integer level : organisationUnitLevels )
                {
                    String id = KEY_LEVEL + level;
                
                    ouList.add( new BaseNameableObject( id, id, id ) );
                }
            }
            
            if ( itemOrganisationUnitGroups != null && !itemOrganisationUnitGroups.isEmpty() )
            {
                for ( OrganisationUnitGroup group : itemOrganisationUnitGroups )
                {
                    String id = KEY_ORGUNIT_GROUP + group.getUid();
                    
                    ouList.add( new BaseNameableObject( id, id, id ) );
                }
            }
            
            objects.add( new BaseDimensionalObject( dimension, DimensionType.ORGANISATIONUNIT, ouList ) );
        }
        else if ( CATEGORYOPTIONCOMBO_DIM_ID.equals( dimension ) )
        {
            objects.add( new BaseDimensionalObject( dimension, DimensionType.CATEGORY_OPTION_COMBO, new ArrayList<BaseNameableObject>() ) );
        }
        else if ( categoryDims.contains( dimension ) )
        {
            DataElementCategoryDimension categoryDimension = categoryDimensions.get( categoryDims.indexOf( dimension ) );
            
            objects.add( new BaseDimensionalObject( dimension, DimensionType.CATEGORY, categoryDimension.getItems() ) );
        }
        else // Group set
        {
            ListMap<String, BaseNameableObject> deGroupMap = new ListMap<String, BaseNameableObject>();
            
            for ( DataElementGroup group : dataElementGroups )
            {
                if ( group.getGroupSet() != null )
                {
                    deGroupMap.putValue( group.getGroupSet().getDimension(), group );
                }
            }
            
            if ( deGroupMap.containsKey( dimension ) )
            {
                objects.add( new BaseDimensionalObject( dimension, DimensionType.DATAELEMENT_GROUPSET, deGroupMap.get( dimension ) ) );
            }
            
            ListMap<String, BaseNameableObject> ouGroupMap = new ListMap<String, BaseNameableObject>();
            
            for ( OrganisationUnitGroup group : organisationUnitGroups )
            {
                if ( group.getGroupSet() != null )
                {
                    ouGroupMap.putValue( group.getGroupSet().getUid(), group );
                }
            }
            
            if ( ouGroupMap.containsKey( dimension ) )
            {
                objects.add( new BaseDimensionalObject( dimension, DimensionType.ORGANISATIONUNIT_GROUPSET, ouGroupMap.get( dimension ) ) );
            }
        }
        
        return objects;
    }
    
    private List<String> getCategoryDims()
    {
        List<String> categoryDims = new ArrayList<String>();
        
        for ( DataElementCategoryDimension dim : categoryDimensions )
        {
            categoryDims.add( dim.getDimension().getDimension() );
        }
        
        return categoryDims;
    }

    private void setPeriodNames( List<Period> periods, boolean dynamicNames, I18nFormat format )
    {
        for ( Period period : periods )
        {
            RelativePeriods.setName( period, null, dynamicNames, format );
        }
    }
    
    /**
     * Splits the keys of the given map on the dimension identifier separator, 
     * sorts the identifiers, writes them out as a key and puts the key back into
     * the map.
     */
    public static void sortKeys( Map<String, Double> valueMap )
    {
        Map<String, Double> map = new HashMap<String, Double>();
        
        for ( String key : valueMap.keySet() )
        {
            if ( key != null )
            {
                String[] ids = key.split( DIMENSION_SEP );
                
                Collections.sort( Arrays.asList( ids ) );
                
                String sortedKey = StringUtils.join( ids, DIMENSION_SEP );
                
                map.put( sortedKey, valueMap.get( key ) );
            }
        }
        
        valueMap.clear();
        valueMap.putAll( map );
    }
    
    /**
     * Generates an identifier based on the given lists of NameableObjects. Uses
     * the UIDs for each NameableObject, sorts them and writes them out as a key.
     */
    public static String getIdentifier( List<NameableObject> column, List<NameableObject> row )
    {
        List<String> ids = new ArrayList<String>();
        
        List<NameableObject> dimensions = new ArrayList<NameableObject>();
        dimensions.addAll( column != null ? column : new ArrayList<NameableObject>() );
        dimensions.addAll( row != null ? row : new ArrayList<NameableObject>() );
        
        for ( NameableObject item : dimensions )
        {
            if ( item.getClass().isAssignableFrom( DataElementOperand.class ) )
            {
                ids.add( ((DataElementOperand) item).getDataElement().getUid() );
                ids.add( ((DataElementOperand) item).getCategoryOptionCombo().getUid() );
            }
            else
            {
                ids.add( item.getUid() );
            }
        }
        
        Collections.sort( ids );
        
        return StringUtils.join( ids, DIMENSION_SEP );
    }
    
    /**
     * Returns meta-data mapping for this analytical object. Includes a identifier
     * to name mapping for dynamic dimensions.
     */
    public Map<String, String> getMetaData()
    {
        Map<String, String> meta = new HashMap<String, String>();
        
        for ( DataElementGroup group : dataElementGroups )
        {
            meta.put( group.getGroupSet().getUid(), group.getGroupSet().getName() );
        }
        
        for ( OrganisationUnitGroup group : organisationUnitGroups )
        {
            meta.put( group.getGroupSet().getUid(), group.getGroupSet().getName() );
        }
        
        for ( DataElementCategoryDimension category : categoryDimensions )
        {
            meta.put( category.getDimension().getUid(), category.getDimension().getName() );
        }
        
        return meta;
    }
    
    /**
     * Clear or set to false all persistent properties for this object.
     */
    public void clear()
    {
        indicators.clear();
        dataElements.clear();
        dataElementOperands.clear();
        dataSets.clear();
        periods.clear();
        relatives = null;
        organisationUnits.clear();
        categoryDimensions.clear();
        dataElementGroups.clear();
        organisationUnitGroups.clear();
        userOrganisationUnit = false;
        userOrganisationUnitChildren = false;
        userOrganisationUnitGrandChildren = false;
        organisationUnitLevels.clear();
        itemOrganisationUnitGroups.clear();
        rewindRelativePeriods = false;
    }
    
    @Override
    public void mergeWith( IdentifiableObject other )
    {
        super.mergeWith( other );
        
        if ( other.getClass().isInstance( this ) )
        {
            BaseAnalyticalObject object = (BaseAnalyticalObject) other;
            
            this.clear();
            
            indicators.addAll( object.getIndicators() );
            dataElements.addAll( object.getDataElements() );
            dataElementOperands.addAll( object.getDataElementOperands() );
            dataSets.addAll( object.getDataSets() );
            periods.addAll( object.getPeriods() );
            relatives = object.getRelatives() == null ? relatives : object.getRelatives();
            organisationUnits.addAll( object.getOrganisationUnits() );
            categoryDimensions.addAll( object.getCategoryDimensions() );
            dataElementGroups.addAll( object.getDataElementGroups() );
            organisationUnitGroups.addAll( object.getOrganisationUnitGroups() );
            userOrganisationUnit = object.isUserOrganisationUnit();
            userOrganisationUnitChildren = object.isUserOrganisationUnitChildren();
            userOrganisationUnitGrandChildren = object.isUserOrganisationUnitGrandChildren();
            organisationUnitLevels.addAll( object.getOrganisationUnitLevels() );
            itemOrganisationUnitGroups = object.getItemOrganisationUnitGroups();
            rewindRelativePeriods = object.isRewindRelativePeriods();
        }
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @JsonProperty
    @JsonSerialize( contentAs = BaseNameableObject.class )
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlElementWrapper( localName = "indicators", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty( localName = "indicator", namespace = DxfNamespaces.DXF_2_0)
    public List<Indicator> getIndicators()
    {
        return indicators;
    }

    public void setIndicators( List<Indicator> indicators )
    {
        this.indicators = indicators;
    }

    @JsonProperty
    @JsonSerialize( contentAs = BaseNameableObject.class )
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlElementWrapper( localName = "dataElements", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty( localName = "dataElement", namespace = DxfNamespaces.DXF_2_0)
    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    public void setDataElements( List<DataElement> dataElements )
    {
        this.dataElements = dataElements;
    }

    @JsonProperty
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JsonView( DetailedView.class )
    @JacksonXmlElementWrapper( localName = "dataElementOperands", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty( localName = "dataElementOperand", namespace = DxfNamespaces.DXF_2_0)
    public List<DataElementOperand> getDataElementOperands()
    {
        return dataElementOperands;
    }

    public void setDataElementOperands( List<DataElementOperand> dataElementOperands )
    {
        this.dataElementOperands = dataElementOperands;
    }

    @JsonProperty
    @JsonSerialize( contentAs = BaseNameableObject.class )
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlElementWrapper( localName = "dataSets", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty( localName = "dataSet", namespace = DxfNamespaces.DXF_2_0)
    public List<DataSet> getDataSets()
    {
        return dataSets;
    }

    public void setDataSets( List<DataSet> dataSets )
    {
        this.dataSets = dataSets;
    }

    @JsonProperty
    @JsonSerialize( contentAs = BaseNameableObject.class )
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlElementWrapper( localName = "organisationUnits", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty( localName = "organisationUnit", namespace = DxfNamespaces.DXF_2_0)
    public List<OrganisationUnit> getOrganisationUnits()
    {
        return organisationUnits;
    }

    public void setOrganisationUnits( List<OrganisationUnit> organisationUnits )
    {
        this.organisationUnits = organisationUnits;
    }

    @JsonProperty
    @JsonSerialize( contentUsing = JacksonPeriodSerializer.class )
    @JsonDeserialize( contentUsing = JacksonPeriodDeserializer.class )
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlElementWrapper( localName = "periods", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty( localName = "period", namespace = DxfNamespaces.DXF_2_0)
    public List<Period> getPeriods()
    {
        return periods;
    }

    public void setPeriods( List<Period> periods )
    {
        this.periods = periods;
    }

    @JsonProperty( value = "relativePeriods" )
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public RelativePeriods getRelatives()
    {
        return relatives;
    }

    public void setRelatives( RelativePeriods relatives )
    {
        this.relatives = relatives;
    }

    public List<DataElementCategoryDimension> getCategoryDimensions()
    {
        return categoryDimensions;
    }

    public void setCategoryDimensions( List<DataElementCategoryDimension> categoryDimensions )
    {
        this.categoryDimensions = categoryDimensions;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlElementWrapper( localName = "dataElementGroups", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty( localName = "dataElementGroup", namespace = DxfNamespaces.DXF_2_0)
    public List<DataElementGroup> getDataElementGroups()
    {
        return dataElementGroups;
    }

    public void setDataElementGroups( List<DataElementGroup> dataElementGroups )
    {
        this.dataElementGroups = dataElementGroups;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlElementWrapper( localName = "organisationUnitGroups", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty( localName = "organisationUnitGroup", namespace = DxfNamespaces.DXF_2_0)
    public List<OrganisationUnitGroup> getOrganisationUnitGroups()
    {
        return organisationUnitGroups;
    }

    public void setOrganisationUnitGroups( List<OrganisationUnitGroup> organisationUnitGroups )
    {
        this.organisationUnitGroups = organisationUnitGroups;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public boolean isUserOrganisationUnit()
    {
        return userOrganisationUnit;
    }

    public void setUserOrganisationUnit( boolean userOrganisationUnit )
    {
        this.userOrganisationUnit = userOrganisationUnit;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public boolean isUserOrganisationUnitChildren()
    {
        return userOrganisationUnitChildren;
    }

    public void setUserOrganisationUnitChildren( boolean userOrganisationUnitChildren )
    {
        this.userOrganisationUnitChildren = userOrganisationUnitChildren;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public boolean isUserOrganisationUnitGrandChildren()
    {
        return userOrganisationUnitGrandChildren;
    }

    public void setUserOrganisationUnitGrandChildren( boolean userOrganisationUnitGrandChildren )
    {
        this.userOrganisationUnitGrandChildren = userOrganisationUnitGrandChildren;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlElementWrapper( localName = "organisationUnitLevels", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty( localName = "organisationUnitLevel", namespace = DxfNamespaces.DXF_2_0)
    public List<Integer> getOrganisationUnitLevels()
    {
        return organisationUnitLevels;
    }

    public void setOrganisationUnitLevels( List<Integer> organisationUnitLevels )
    {
        this.organisationUnitLevels = organisationUnitLevels;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlElementWrapper( localName = "itemOrganisationUnitGroups", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty( localName = "itemOrganisationUnitGroup", namespace = DxfNamespaces.DXF_2_0)
    public List<OrganisationUnitGroup> getItemOrganisationUnitGroups()
    {
        return itemOrganisationUnitGroups;
    }

    public void setItemOrganisationUnitGroups( List<OrganisationUnitGroup> itemOrganisationUnitGroups )
    {
        this.itemOrganisationUnitGroups = itemOrganisationUnitGroups;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public boolean isRewindRelativePeriods()
    {
        return rewindRelativePeriods;
    }

    public void setRewindRelativePeriods( boolean rewindRelativePeriods )
    {
        this.rewindRelativePeriods = rewindRelativePeriods;
    }

    // -------------------------------------------------------------------------
    // Transient properties
    // -------------------------------------------------------------------------

    @JsonIgnore
    public List<OrganisationUnit> getTransientOrganisationUnits()
    {
        return transientOrganisationUnits;
    }

    @JsonIgnore
    public Date getRelativePeriodDate()
    {
        return relativePeriodDate;
    }

    @JsonIgnore
    public OrganisationUnit getRelativeOrganisationUnit()
    {
        return relativeOrganisationUnit;
    }

    // -------------------------------------------------------------------------
    // Web domain properties
    // -------------------------------------------------------------------------

    @JsonProperty
    @JsonDeserialize( contentAs = BaseDimensionalObject.class )
    @JsonSerialize( contentAs = BaseDimensionalObject.class )
    @JsonView( {DimensionalView.class} )
    @JacksonXmlElementWrapper( localName = "columns", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty( localName = "column", namespace = DxfNamespaces.DXF_2_0)
    public List<DimensionalObject> getColumns()
    {
        return columns;
    }

    public void setColumns( List<DimensionalObject> columns )
    {
        this.columns = columns;
    }

    @JsonProperty
    @JsonDeserialize( contentAs = BaseDimensionalObject.class )
    @JsonSerialize( contentAs = BaseDimensionalObject.class )
    @JsonView( {DimensionalView.class} )
    @JacksonXmlElementWrapper( localName = "rows", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty( localName = "row", namespace = DxfNamespaces.DXF_2_0)
    public List<DimensionalObject> getRows()
    {
        return rows;
    }

    public void setRows( List<DimensionalObject> rows )
    {
        this.rows = rows;
    }

    @JsonProperty
    @JsonDeserialize( contentAs = BaseDimensionalObject.class )
    @JsonSerialize( contentAs = BaseDimensionalObject.class )
    @JsonView( {DimensionalView.class} )
    @JacksonXmlElementWrapper( localName = "filters", namespace = DxfNamespaces.DXF_2_0)
    @JacksonXmlProperty( localName = "filter", namespace = DxfNamespaces.DXF_2_0)
    public List<DimensionalObject> getFilters()
    {
        return filters;
    }

    public void setFilters( List<DimensionalObject> filters )
    {
        this.filters = filters;
    }

    @JsonProperty
    @JsonView( {DimensionalView.class} )
    public Map<String, String> getParentGraphMap()
    {
        return parentGraphMap;
    }

    public void setParentGraphMap( Map<String, String> parentGraphMap )
    {
        this.parentGraphMap = parentGraphMap;
    }
}
