package org.hisp.dhis.indicator;

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

import static org.hisp.dhis.i18n.I18nUtils.getCountByName;
import static org.hisp.dhis.i18n.I18nUtils.getObjectsBetween;
import static org.hisp.dhis.i18n.I18nUtils.getObjectsBetweenByName;
import static org.hisp.dhis.i18n.I18nUtils.getObjectsByName;
import static org.hisp.dhis.i18n.I18nUtils.i18n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.mapping.MapLegendSet;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
@Transactional
public class DefaultIndicatorService
    implements IndicatorService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private IndicatorStore indicatorStore;

    public void setIndicatorStore( IndicatorStore indicatorStore )
    {
        this.indicatorStore = indicatorStore;
    }

    private GenericIdentifiableObjectStore<IndicatorType> indicatorTypeStore;

    public void setIndicatorTypeStore( GenericIdentifiableObjectStore<IndicatorType> indicatorTypeStore )
    {
        this.indicatorTypeStore = indicatorTypeStore;
    }

    private GenericIdentifiableObjectStore<IndicatorGroup> indicatorGroupStore;

    public void setIndicatorGroupStore( GenericIdentifiableObjectStore<IndicatorGroup> indicatorGroupStore )
    {
        this.indicatorGroupStore = indicatorGroupStore;
    }

    private GenericIdentifiableObjectStore<IndicatorGroupSet> indicatorGroupSetStore;

    public void setIndicatorGroupSetStore( GenericIdentifiableObjectStore<IndicatorGroupSet> indicatorGroupSetStore )
    {
        this.indicatorGroupSetStore = indicatorGroupSetStore;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    // -------------------------------------------------------------------------
    // Indicator
    // -------------------------------------------------------------------------

    public int addIndicator( Indicator indicator )
    {
        return indicatorStore.save( indicator );
    }

    public void updateIndicator( Indicator indicator )
    {
        indicatorStore.update( indicator );
    }

    public void deleteIndicator( Indicator indicator )
    {
        indicatorStore.delete( indicator );
    }

    public Indicator getIndicator( int id )
    {
        return i18n( i18nService, indicatorStore.get( id ) );
    }

    public Indicator getIndicator( String uid )
    {
        return i18n( i18nService, indicatorStore.getByUid( uid ) );
    }

    public Collection<Indicator> getAllIndicators()
    {
        return i18n( i18nService, indicatorStore.getAll() );
    }

    public Collection<Indicator> getIndicators( final Collection<Integer> identifiers )
    {
        Collection<Indicator> indicators = getAllIndicators();

        return identifiers == null ? indicators : FilterUtils.filter( indicators, new Filter<Indicator>()
        {
            public boolean retain( Indicator object )
            {
                return identifiers.contains( object.getId() );
            }
        } );
    }

    public List<Indicator> getIndicatorsByUid( Collection<String> uids )
    {
        return indicatorStore.getByUid( uids );
    }

    public List<Indicator> getIndicatorByName( String name )
    {
        return new ArrayList<Indicator>( i18n( i18nService, indicatorStore.getAllEqName( name ) ) );
    }

    public List<Indicator> getIndicatorByShortName( String shortName )
    {
        return new ArrayList<Indicator>( i18n( i18nService, indicatorStore.getAllEqShortName( shortName ) ) );
    }

    public Indicator getIndicatorByCode( String code )
    {
        return i18n( i18nService, indicatorStore.getByCode( code ) );
    }

    public Collection<Indicator> getIndicatorsWithGroupSets()
    {
        return i18n( i18nService, indicatorStore.getIndicatorsWithGroupSets() );
    }

    public Collection<Indicator> getIndicatorsWithoutGroups()
    {
        return i18n( i18nService, indicatorStore.getIndicatorsWithoutGroups() );
    }

    public Collection<Indicator> getIndicatorsWithDataSets()
    {
        return i18n( i18nService, indicatorStore.getIndicatorsWithDataSets() );
    }

    public Collection<Indicator> getIndicatorsLikeName( String name )
    {
        return getObjectsByName( i18nService, indicatorStore, name );
    }

    public int getIndicatorCount()
    {
        return indicatorStore.getCount();
    }

    public int getIndicatorCountByName( String name )
    {
        return getCountByName( i18nService, indicatorStore, name );
    }

    public Collection<Indicator> getIndicatorsBetween( int first, int max )
    {
        return getObjectsBetween( i18nService, indicatorStore, first, max );
    }

    public Collection<Indicator> getIndicatorsBetweenByName( String name, int first, int max )
    {
        return getObjectsBetweenByName( i18nService, indicatorStore, name, first, max );
    }

    @Override
    public int countMapLegendSetIndicators( MapLegendSet mapLegendSet )
    {
        return indicatorStore.countMapLegendSetIndicators( mapLegendSet );
    }

    // -------------------------------------------------------------------------
    // IndicatorType
    // -------------------------------------------------------------------------

    public int addIndicatorType( IndicatorType indicatorType )
    {
        return indicatorTypeStore.save( indicatorType );
    }

    public void updateIndicatorType( IndicatorType indicatorType )
    {
        indicatorTypeStore.update( indicatorType );
    }

    public void deleteIndicatorType( IndicatorType indicatorType )
    {
        indicatorTypeStore.delete( indicatorType );
    }

    public IndicatorType getIndicatorType( int id )
    {
        return i18n( i18nService, indicatorTypeStore.get( id ) );
    }

    public IndicatorType getIndicatorType( String uid )
    {
        return i18n( i18nService, indicatorTypeStore.getByUid( uid ) );
    }

    public Collection<IndicatorType> getIndicatorTypes( final Collection<Integer> identifiers )
    {
        Collection<IndicatorType> types = getAllIndicatorTypes();

        return identifiers == null ? types : FilterUtils.filter( types, new Filter<IndicatorType>()
        {
            public boolean retain( IndicatorType object )
            {
                return identifiers.contains( object.getId() );
            }
        } );
    }

    public Collection<IndicatorType> getAllIndicatorTypes()
    {
        return i18n( i18nService, indicatorTypeStore.getAll() );
    }

    public IndicatorType getIndicatorTypeByName( String name )
    {
        return i18n( i18nService, indicatorTypeStore.getByName( name ) );
    }

    public int getIndicatorTypeCount()
    {
        return indicatorTypeStore.getCount();
    }

    public int getIndicatorTypeCountByName( String name )
    {
        return getCountByName( i18nService, indicatorTypeStore, name );
    }

    public Collection<IndicatorType> getIndicatorTypesBetween( int first, int max )
    {
        return getObjectsBetween( i18nService, indicatorTypeStore, first, max );
    }

    public Collection<IndicatorType> getIndicatorTypesBetweenByName( String name, int first, int max )
    {
        return getObjectsBetweenByName( i18nService, indicatorTypeStore, name, first, max );
    }

    // -------------------------------------------------------------------------
    // IndicatorGroup
    // -------------------------------------------------------------------------

    public int addIndicatorGroup( IndicatorGroup indicatorGroup )
    {
        return indicatorGroupStore.save( indicatorGroup );
    }

    public void updateIndicatorGroup( IndicatorGroup indicatorGroup )
    {
        indicatorGroupStore.update( indicatorGroup );
    }

    public void deleteIndicatorGroup( IndicatorGroup indicatorGroup )
    {
        indicatorGroupStore.delete( indicatorGroup );
    }

    public IndicatorGroup getIndicatorGroup( int id )
    {
        return i18n( i18nService, indicatorGroupStore.get( id ) );
    }

    public IndicatorGroup getIndicatorGroup( int id, boolean i18nIndicators )
    {
        IndicatorGroup group = getIndicatorGroup( id );

        if ( i18nIndicators )
        {
            i18n( i18nService, group.getMembers() );
        }

        return group;
    }

    public Collection<IndicatorGroup> getIndicatorGroups( final Collection<Integer> identifiers )
    {
        Collection<IndicatorGroup> groups = getAllIndicatorGroups();

        return identifiers == null ? groups : FilterUtils.filter( groups, new Filter<IndicatorGroup>()
        {
            public boolean retain( IndicatorGroup object )
            {
                return identifiers.contains( object.getId() );
            }
        } );
    }

    public IndicatorGroup getIndicatorGroup( String uid )
    {
        return i18n( i18nService, indicatorGroupStore.getByUid( uid ) );
    }

    public Collection<IndicatorGroup> getAllIndicatorGroups()
    {
        return i18n( i18nService, indicatorGroupStore.getAll() );
    }

    public List<IndicatorGroup> getIndicatorGroupByName( String name )
    {
        return new ArrayList<IndicatorGroup>( i18n( i18nService, indicatorGroupStore.getAllEqName( name ) ) );
    }

    public Collection<IndicatorGroup> getGroupsContainingIndicator( Indicator indicator )
    {
        Collection<IndicatorGroup> groups = getAllIndicatorGroups();

        Iterator<IndicatorGroup> iterator = groups.iterator();

        while ( iterator.hasNext() )
        {
            IndicatorGroup group = iterator.next();

            if ( !group.getMembers().contains( indicator ) )
            {
                iterator.remove();
            }
        }

        return groups;
    }

    public int getIndicatorGroupCount()
    {
        return indicatorGroupStore.getCount();
    }

    public int getIndicatorGroupCountByName( String name )
    {
        return getCountByName( i18nService, indicatorGroupStore, name );
    }

    public Collection<IndicatorGroup> getIndicatorGroupsBetween( int first, int max )
    {
        return getObjectsBetween( i18nService, indicatorGroupStore, first, max );
    }

    public Collection<IndicatorGroup> getIndicatorGroupsBetweenByName( String name, int first, int max )
    {
        return getObjectsBetweenByName( i18nService, indicatorGroupStore, name, first, max );
    }

    // -------------------------------------------------------------------------
    // IndicatorGroupSet
    // -------------------------------------------------------------------------

    public int addIndicatorGroupSet( IndicatorGroupSet groupSet )
    {
        return indicatorGroupSetStore.save( groupSet );
    }

    public void updateIndicatorGroupSet( IndicatorGroupSet groupSet )
    {
        indicatorGroupSetStore.update( groupSet );
    }

    public void deleteIndicatorGroupSet( IndicatorGroupSet groupSet )
    {
        indicatorGroupSetStore.delete( groupSet );
    }

    public IndicatorGroupSet getIndicatorGroupSet( int id )
    {
        return i18n( i18nService, indicatorGroupSetStore.get( id ) );
    }

    public IndicatorGroupSet getIndicatorGroupSet( int id, boolean i18nGroups )
    {
        IndicatorGroupSet groupSet = getIndicatorGroupSet( id );

        if ( i18nGroups )
        {
            i18n( i18nService, groupSet.getMembers() );
        }

        return groupSet;
    }

    public IndicatorGroupSet getIndicatorGroupSet( String uid )
    {
        return i18n( i18nService, indicatorGroupSetStore.getByUid( uid ) );
    }

    public List<IndicatorGroupSet> getIndicatorGroupSetByName( String name )
    {
        return new ArrayList<IndicatorGroupSet>( i18n( i18nService, indicatorGroupSetStore.getAllEqName( name ) ) );
    }

    @Override
    public Collection<IndicatorGroupSet> getCompulsoryIndicatorGroupSets()
    {
        Collection<IndicatorGroupSet> groupSets = new ArrayList<IndicatorGroupSet>();

        for ( IndicatorGroupSet groupSet : getAllIndicatorGroupSets() )
        {
            if ( groupSet.isCompulsory() )
            {
                groupSets.add( groupSet );
            }
        }

        return groupSets;
    }

    @Override
    public Collection<IndicatorGroupSet> getCompulsoryIndicatorGroupSetsWithMembers()
    {
        return FilterUtils.filter( getAllIndicatorGroupSets(), new Filter<IndicatorGroupSet>()
        {
            public boolean retain( IndicatorGroupSet object )
            {
                return object.isCompulsory() && object.hasIndicatorGroups();
            }
        } );
    }

    @Override
    public Collection<IndicatorGroupSet> getCompulsoryIndicatorGroupSetsNotAssignedTo( Indicator indicator )
    {
        Collection<IndicatorGroupSet> groupSets = new ArrayList<IndicatorGroupSet>();

        for ( IndicatorGroupSet groupSet : getCompulsoryIndicatorGroupSets() )
        {
            if ( !groupSet.isMemberOfIndicatorGroups( indicator ) && groupSet.hasIndicatorGroups() )
            {
                groupSets.add( groupSet );
            }
        }

        return groupSets;
    }

    public Collection<IndicatorGroupSet> getAllIndicatorGroupSets()
    {
        return i18n( i18nService, indicatorGroupSetStore.getAll() );
    }

    public Collection<IndicatorGroupSet> getIndicatorGroupSets( final Collection<Integer> identifiers )
    {
        Collection<IndicatorGroupSet> groupSets = getAllIndicatorGroupSets();

        return identifiers == null ? groupSets : FilterUtils.filter( groupSets, new Filter<IndicatorGroupSet>()
        {
            public boolean retain( IndicatorGroupSet object )
            {
                return identifiers.contains( object.getId() );
            }
        } );
    }

    public int getIndicatorGroupSetCount()
    {
        return indicatorGroupSetStore.getCount();
    }

    public int getIndicatorGroupSetCountByName( String name )
    {
        return getCountByName( i18nService, indicatorGroupSetStore, name );
    }

    public Collection<IndicatorGroupSet> getIndicatorGroupSetsBetween( int first, int max )
    {
        return getObjectsBetween( i18nService, indicatorGroupSetStore, first, max );
    }

    public Collection<IndicatorGroupSet> getIndicatorGroupSetsBetweenByName( String name, int first, int max )
    {
        return getObjectsBetweenByName( i18nService, indicatorGroupSetStore, name, first, max );
    }
}
