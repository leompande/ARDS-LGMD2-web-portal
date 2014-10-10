package org.hisp.dhis.organisationunit;

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

import org.apache.commons.collections.CollectionUtils;
import org.hisp.dhis.common.IdentifiableObjectUtils;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.hierarchy.HierarchyViolationException;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitLevelComparator;
import org.hisp.dhis.system.util.ConversionUtils;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.hisp.dhis.system.util.GeoUtils;
import org.hisp.dhis.system.util.ValidationUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.version.VersionService;
import org.springframework.transaction.annotation.Transactional;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hisp.dhis.i18n.I18nUtils.i18n;

/**
 * @author Torgeir Lorange Ostby
 */
@Transactional
public class DefaultOrganisationUnitService
    implements OrganisationUnitService
{
    private static final String LEVEL_PREFIX = "Level ";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitStore organisationUnitStore;

    public void setOrganisationUnitStore( OrganisationUnitStore organisationUnitStore )
    {
        this.organisationUnitStore = organisationUnitStore;
    }

    private OrganisationUnitLevelStore organisationUnitLevelStore;

    public void setOrganisationUnitLevelStore( OrganisationUnitLevelStore organisationUnitLevelStore )
    {
        this.organisationUnitLevelStore = organisationUnitLevelStore;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private VersionService versionService;

    public void setVersionService( VersionService versionService )
    {
        this.versionService = versionService;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    // -------------------------------------------------------------------------
    // OrganisationUnit
    // -------------------------------------------------------------------------

    @Override
    public int addOrganisationUnit( OrganisationUnit organisationUnit )
    {
        int id = organisationUnitStore.save( organisationUnit );

        if ( organisationUnit.getParent() == null && currentUserService.getCurrentUser() != null )
        {
            // Adding a new root node, add this node to the current user

            currentUserService.getCurrentUser().getOrganisationUnits().add( organisationUnit );
        }

        versionService.updateVersion( VersionService.ORGANISATIONUNIT_VERSION );

        return id;
    }

    public void updateOrganisationUnit( OrganisationUnit organisationUnit )
    {
        organisationUnitStore.update( organisationUnit );

        versionService.updateVersion( VersionService.ORGANISATIONUNIT_VERSION );
    }

    public void updateOrganisationUnit( OrganisationUnit organisationUnit, boolean updateHierarchy )
    {
        updateOrganisationUnit( organisationUnit );
    }

    public void deleteOrganisationUnit( OrganisationUnit organisationUnit )
        throws HierarchyViolationException
    {
        organisationUnit = getOrganisationUnit( organisationUnit.getId() );

        if ( !organisationUnit.getChildren().isEmpty() )
        {
            throw new HierarchyViolationException( "Cannot delete an OrganisationUnit with children" );
        }

        OrganisationUnit parent = organisationUnit.getParent();

        if ( parent != null )
        {
            parent.getChildren().remove( organisationUnit );

            organisationUnitStore.update( parent );
        }

        organisationUnitStore.delete( organisationUnit );

        versionService.updateVersion( VersionService.ORGANISATIONUNIT_VERSION );
    }

    public OrganisationUnit getOrganisationUnit( int id )
    {
        return organisationUnitStore.get( id );
    }

    public Collection<OrganisationUnit> getAllOrganisationUnits()
    {
        return organisationUnitStore.getAll();
    }

    @Override
    public Collection<OrganisationUnit> getAllOrganisationUnitsByStatus( boolean status )
    {
        return organisationUnitStore.getAllOrganisationUnitsByStatus( status );
    }

    @Override
    public Collection<OrganisationUnit> getAllOrganisationUnitsByLastUpdated( Date lastUpdated )
    {
        return organisationUnitStore.getAllOrganisationUnitsByLastUpdated( lastUpdated );
    }

    @Override
    public Collection<OrganisationUnit> getAllOrganisationUnitsByStatusLastUpdated( boolean status, Date lastUpdated )
    {
        return organisationUnitStore.getAllOrganisationUnitsByStatusLastUpdated( status, lastUpdated );
    }

    public void searchOrganisationUnitByName( List<OrganisationUnit> orgUnits, String key )
    {
        Iterator<OrganisationUnit> iterator = orgUnits.iterator();

        while ( iterator.hasNext() )
        {
            if ( !iterator.next().getName().toLowerCase().contains( key.toLowerCase() ) )
            {
                iterator.remove();
            }
        }
    }

    public Collection<OrganisationUnit> getOrganisationUnits( final Collection<Integer> identifiers )
    {
        Collection<OrganisationUnit> objects = getAllOrganisationUnits();

        return identifiers == null ? objects : FilterUtils.filter( objects, new Filter<OrganisationUnit>()
        {
            public boolean retain( OrganisationUnit object )
            {
                return identifiers.contains( object.getId() );
            }
        } );
    }

    public List<OrganisationUnit> getOrganisationUnitsByUid( Collection<String> uids )
    {
        return organisationUnitStore.getByUid( uids );
    }

    public OrganisationUnit getOrganisationUnit( String uid )
    {
        return organisationUnitStore.getByUid( uid );
    }

    public OrganisationUnit getOrganisationUnitByUuid( String uuid )
    {
        return organisationUnitStore.getByUuid( uuid );
    }

    public List<OrganisationUnit> getOrganisationUnitByName( String name )
    {
        return organisationUnitStore.getAllEqName( name );
    }

    public OrganisationUnit getOrganisationUnitByCode( String code )
    {
        return organisationUnitStore.getByCode( code );
    }

    public Collection<OrganisationUnit> getOrganisationUnitByNameIgnoreCase( String name )
    {
        return organisationUnitStore.getAllEqNameIgnoreCase( name );
    }

    public Collection<OrganisationUnit> getRootOrganisationUnits()
    {
        return organisationUnitStore.getRootOrganisationUnits();
    }

    public int getLevelOfOrganisationUnit( int id )
    {
        return getOrganisationUnit( id ).getOrganisationUnitLevel();
    }

    public int getLevelOfOrganisationUnit( String uid )
    {
        return getOrganisationUnit( uid ).getOrganisationUnitLevel();
    }

    public Collection<OrganisationUnit> getLeafOrganisationUnits( int id )
    {
        Collection<OrganisationUnit> units = getOrganisationUnitWithChildren( id );

        return FilterUtils.filter( units, new Filter<OrganisationUnit>()
        {
            public boolean retain( OrganisationUnit object )
            {
                return object != null && object.getChildren().isEmpty();
            }
        } );
    }

    public Collection<OrganisationUnit> getOrganisationUnits( Collection<OrganisationUnitGroup> groups, Collection<OrganisationUnit> parents )
    {
        Set<OrganisationUnit> members = new HashSet<OrganisationUnit>();

        for ( OrganisationUnitGroup group : groups )
        {
            members.addAll( group.getMembers() );
        }

        if ( parents != null && !parents.isEmpty() )
        {
            Collection<OrganisationUnit> children = getOrganisationUnitsWithChildren( IdentifiableObjectUtils.getUids( parents ) );

            members.retainAll( children );
        }

        return members;
    }

    public Collection<OrganisationUnit> getOrganisationUnitsWithChildren( Collection<String> parentUids )
    {
        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();

        for ( String uid : parentUids )
        {
            units.addAll( getOrganisationUnitsWithChildren( uid ) );
        }

        return units;
    }

    public Collection<OrganisationUnit> getOrganisationUnitsWithChildren( String uid )
    {
        return getOrganisationUnitWithChildren( getOrganisationUnit( uid ).getId() );
    }

    public Collection<OrganisationUnit> getOrganisationUnitWithChildren( int id )
    {
        OrganisationUnit organisationUnit = getOrganisationUnit( id );

        if ( organisationUnit == null )
        {
            return Collections.emptySet();
        }

        List<OrganisationUnit> result = new ArrayList<OrganisationUnit>();

        int rootLevel = organisationUnit.getOrganisationUnitLevel();

        organisationUnit.setLevel( rootLevel );
        result.add( organisationUnit );

        addOrganisationUnitChildren( organisationUnit, result, rootLevel );

        return result;
    }

    /**
     * Support method for getOrganisationUnitWithChildren(). Adds all
     * OrganisationUnit children to a result collection.
     */
    private void addOrganisationUnitChildren( OrganisationUnit parent, List<OrganisationUnit> result, int level )
    {
        if ( parent.getChildren() != null && parent.getChildren().size() > 0 )
        {
            level++;
        }

        List<OrganisationUnit> childList = parent.getSortedChildren();

        for ( OrganisationUnit child : childList )
        {
            child.setLevel( level );
            result.add( child );

            addOrganisationUnitChildren( child, result, level );
        }
    }

    public List<OrganisationUnit> getOrganisationUnitBranch( int id )
    {
        OrganisationUnit organisationUnit = getOrganisationUnit( id );

        if ( organisationUnit == null )
        {
            return Collections.emptyList();
        }

        ArrayList<OrganisationUnit> result = new ArrayList<OrganisationUnit>();

        result.add( organisationUnit );

        OrganisationUnit parent = organisationUnit.getParent();

        while ( parent != null )
        {
            result.add( parent );

            parent = parent.getParent();
        }

        Collections.reverse( result ); // From root to target

        return result;
    }

    public Collection<OrganisationUnit> getOrganisationUnitsAtLevel( int level )
    {
        Collection<OrganisationUnit> roots = getRootOrganisationUnits();

        if ( level == 1 )
        {
            return roots;
        }

        return getOrganisationUnitsAtLevel( level, roots );
    }

    public Collection<OrganisationUnit> getOrganisationUnitsAtLevel( int level, OrganisationUnit parent )
    {
        Set<OrganisationUnit> parents = new HashSet<OrganisationUnit>();
        parents.add( parent );

        return getOrganisationUnitsAtLevel( level, parent != null ? parents : null );
    }

    public Collection<OrganisationUnit> getOrganisationUnitsAtLevel( int level, Collection<OrganisationUnit> parents )
    {
        Set<Integer> levels = new HashSet<Integer>();
        levels.add( level );

        return getOrganisationUnitsAtLevels( levels, parents );
    }

    public Collection<OrganisationUnit> getOrganisationUnitsAtLevels( Collection<Integer> levels, Collection<OrganisationUnit> parents )
    {
        if ( parents == null || parents.isEmpty() )
        {
            parents = new HashSet<OrganisationUnit>( getRootOrganisationUnits() );
        }

        Set<OrganisationUnit> result = new HashSet<OrganisationUnit>();

        for ( Integer level : levels )
        {
            if ( level < 1 )
            {
                throw new IllegalArgumentException( "Level must be greater than zero" );
            }

            for ( OrganisationUnit parent : parents )
            {
                int parentLevel = parent.getOrganisationUnitLevel();

                if ( level < parentLevel )
                {
                    throw new IllegalArgumentException(
                        "Level must be greater than or equal to level of parent organisation unit" );
                }

                if ( level == parentLevel )
                {
                    parent.setLevel( level );
                    result.add( parent );
                }
                else
                {
                    addOrganisationUnitChildrenAtLevel( parent, parentLevel + 1, level, result );
                }
            }
        }

        return result;
    }

    /**
     * Support method for getOrganisationUnitsAtLevel(). Adds all children at a
     * given targetLevel to a result collection. The parent's children are at
     * the current level.
     */
    private void addOrganisationUnitChildrenAtLevel( OrganisationUnit parent, int currentLevel, int targetLevel,
        Set<OrganisationUnit> result )
    {
        if ( currentLevel == targetLevel )
        {
            for ( OrganisationUnit child : parent.getChildren() )
            {
                child.setLevel( currentLevel );
                result.add( child );
            }
        }
        else
        {
            for ( OrganisationUnit child : parent.getChildren() )
            {
                addOrganisationUnitChildrenAtLevel( child, currentLevel + 1, targetLevel, result );
            }
        }
    }

    public int getNumberOfOrganisationalLevels()
    {
        int maxDepth = 0;
        int depth;

        for ( OrganisationUnit root : getRootOrganisationUnits() )
        {
            depth = getDepth( root, 1 );

            if ( depth > maxDepth )
            {
                maxDepth = depth;
            }
        }

        return maxDepth;
    }

    /**
     * Support method for getNumberOfOrganisationalLevels(). Finds the depth of
     * a given subtree. The parent is at the current level.
     */
    private int getDepth( OrganisationUnit parent, int currentLevel )
    {
        int maxDepth = currentLevel;
        int depth;

        for ( OrganisationUnit child : parent.getChildren() )
        {
            depth = getDepth( child, currentLevel + 1 );

            if ( depth > maxDepth )
            {
                maxDepth = depth;
            }
        }

        return maxDepth;
    }

    public Collection<OrganisationUnit> getOrganisationUnitsWithoutGroups()
    {
        return organisationUnitStore.getOrganisationUnitsWithoutGroups();
    }

    public Collection<OrganisationUnit> getOrganisationUnitsByNameAndGroups( String query,
        Collection<OrganisationUnitGroup> groups, boolean limit )
    {
        return organisationUnitStore.getOrganisationUnitsByNameAndGroups( query, groups, limit );
    }

    @SuppressWarnings("unchecked")
    public Collection<OrganisationUnit> getOrganisationUnitsByNameAndGroups( String name,
        Collection<OrganisationUnitGroup> groups, OrganisationUnit parent, boolean limit )
    {
        // Can only limit in query if parent is not set and we get all units

        boolean _limit = limit && parent == null;

        final Collection<OrganisationUnit> result = organisationUnitStore.getOrganisationUnitsByNameAndGroups( name,
            groups, _limit );

        if ( parent == null )
        {
            return result;
        }

        final Collection<OrganisationUnit> subTree = getOrganisationUnitWithChildren( parent.getId() );

        List<OrganisationUnit> intersection = new ArrayList<OrganisationUnit>( CollectionUtils.intersection( subTree,
            result ) );

        return limit && intersection.size() > MAX_LIMIT ? intersection.subList( 0, MAX_LIMIT )
            : intersection;
    }

    public OrganisationUnitDataSetAssociationSet getOrganisationUnitDataSetAssociationSet()
    {
        Map<String, Set<String>> associationSet = organisationUnitStore.getOrganisationUnitDataSetAssocationMap();
        associationSet.putAll( organisationUnitStore.getOrganisationUnitGroupDataSetAssocationMap() );

        filterUserDataSets( associationSet );
        filterChildOrganisationUnits( associationSet );

        OrganisationUnitDataSetAssociationSet set = new OrganisationUnitDataSetAssociationSet();

        for ( Map.Entry<String, Set<String>> entry : associationSet.entrySet() )
        {
            int index = set.getDataSetAssociationSets().indexOf( entry.getValue() );

            if ( index == -1 ) // Association set does not exist, add new
            {
                index = set.getDataSetAssociationSets().size();
                set.getDataSetAssociationSets().add( entry.getValue() );
            }

            set.getOrganisationUnitAssociationSetMap().put( entry.getKey(), index );
            set.getDistinctDataSets().addAll( entry.getValue() );
        }

        return set;
    }

    private void filterUserDataSets( Map<String, Set<String>> associationMap )
    {
        User currentUser = currentUserService.getCurrentUser();

        if ( currentUser != null && !currentUser.getUserCredentials().isSuper() )
        {
            Collection<String> userDataSets = ConversionUtils.getUids( DataSet.class, currentUser.getUserCredentials().getAllDataSets() );

            for ( Set<String> dataSets : associationMap.values() )
            {
                dataSets.retainAll( userDataSets );
            }
        }
    }

    private void filterChildOrganisationUnits( Map<String, Set<String>> associationMap )
    {
        User currentUser = currentUserService.getCurrentUser();

        if ( currentUser != null )
        {
            Collection<String> parentIds = ConversionUtils.getUids( OrganisationUnit.class,
                currentUser.getOrganisationUnits() );

            Collection<OrganisationUnit> organisationUnitsWithChildren = getOrganisationUnitsWithChildren( parentIds );
            Collection<String> children = ConversionUtils.getUids( OrganisationUnit.class, organisationUnitsWithChildren );

            associationMap.keySet().retainAll( children );
        }
    }

    public void filterOrganisationUnitsWithoutData( Collection<OrganisationUnit> organisationUnits )
    {
        final Set<Integer> unitsWithoutData = organisationUnitStore.getOrganisationUnitIdsWithoutData();

        FilterUtils.filter( organisationUnits, new Filter<OrganisationUnit>()
        {
            public boolean retain( OrganisationUnit unit )
            {
                return unit != null && (!unitsWithoutData.contains( unit.getId() ) || unit.hasChild());
            }
        } );
    }

    public Collection<OrganisationUnit> getOrganisationUnitsBetween( int first, int max )
    {
        return organisationUnitStore.getAllOrderedName( first, max );
    }

    public Collection<OrganisationUnit> getOrganisationUnitsBetweenByName( String name, int first, int max )
    {
        return organisationUnitStore.getAllLikeNameOrderedName( name, first, max );
    }

    @Override
    public Collection<OrganisationUnit> getOrganisationUnitsBetweenByStatus( boolean status, int first, int max )
    {
        return organisationUnitStore.getBetweenByStatus( status, first, max );
    }

    @Override
    public Collection<OrganisationUnit> getOrganisationUnitsBetweenByLastUpdated( Date lastUpdated, int first, int max )
    {
        return organisationUnitStore.getBetweenByLastUpdated( lastUpdated, first, max );
    }

    @Override
    public Collection<OrganisationUnit> getOrganisationUnitsBetweenByStatusLastUpdated( boolean status,
        Date lastUpdated, int first, int max )
    {
        return organisationUnitStore.getBetweenByStatusLastUpdated( status, lastUpdated, first, max );
    }

    // -------------------------------------------------------------------------
    // OrganisationUnitHierarchy
    // -------------------------------------------------------------------------

    public OrganisationUnitHierarchy getOrganisationUnitHierarchy()
    {
        return organisationUnitStore.getOrganisationUnitHierarchy();
    }

    public void updateOrganisationUnitParent( int organisationUnitId, int parentId )
    {
        organisationUnitStore.updateOrganisationUnitParent( organisationUnitId, parentId );
    }

    // -------------------------------------------------------------------------
    // OrganisationUnitLevel
    // -------------------------------------------------------------------------

    public int addOrganisationUnitLevel( OrganisationUnitLevel organisationUnitLevel )
    {
        return organisationUnitLevelStore.save( organisationUnitLevel );
    }

    public void updateOrganisationUnitLevel( OrganisationUnitLevel organisationUnitLevel )
    {
        organisationUnitLevelStore.update( organisationUnitLevel );
    }

    public void addOrUpdateOrganisationUnitLevel( OrganisationUnitLevel level )
    {
        OrganisationUnitLevel existing = getOrganisationUnitLevelByLevel( level.getLevel() );

        if ( existing == null )
        {
            addOrganisationUnitLevel( level );
        }
        else
        {
            existing.setName( level.getName() );

            updateOrganisationUnitLevel( existing );
        }
    }

    public void pruneOrganisationUnitLevels( Set<Integer> currentLevels )
    {
        for ( OrganisationUnitLevel level : getOrganisationUnitLevels() )
        {
            if ( !currentLevels.contains( level.getLevel() ) )
            {
                deleteOrganisationUnitLevel( level );
            }
        }
    }

    public OrganisationUnitLevel getOrganisationUnitLevel( int id )
    {
        return organisationUnitLevelStore.get( id );
    }

    public OrganisationUnitLevel getOrganisationUnitLevel( String uid )
    {
        return organisationUnitLevelStore.getByUid( uid );
    }

    public Collection<OrganisationUnitLevel> getOrganisationUnitLevels( final Collection<Integer> identifiers )
    {
        Collection<OrganisationUnitLevel> objects = getOrganisationUnitLevels();

        return identifiers == null ? objects : FilterUtils.filter( objects, new Filter<OrganisationUnitLevel>()
        {
            public boolean retain( OrganisationUnitLevel object )
            {
                return identifiers.contains( object.getId() );
            }
        } );
    }

    public void deleteOrganisationUnitLevel( OrganisationUnitLevel organisationUnitLevel )
    {
        organisationUnitLevelStore.delete( organisationUnitLevel );
    }

    public void deleteOrganisationUnitLevels()
    {
        organisationUnitLevelStore.deleteAll();
    }

    public List<OrganisationUnitLevel> getOrganisationUnitLevels()
    {
        List<OrganisationUnitLevel> organisationUnitLevels = new ArrayList<OrganisationUnitLevel>( i18n( i18nService,
            organisationUnitLevelStore.getAll() ) );

        Collections.sort( organisationUnitLevels, OrganisationUnitLevelComparator.INSTANCE );

        return organisationUnitLevels;
    }

    public OrganisationUnitLevel getOrganisationUnitLevelByLevel( int level )
    {
        return i18n( i18nService, organisationUnitLevelStore.getByLevel( level ) );
    }

    public List<OrganisationUnitLevel> getOrganisationUnitLevelByName( String name )
    {
        return new ArrayList<OrganisationUnitLevel>( i18n( i18nService, organisationUnitLevelStore.getAllEqName( name ) ) );
    }

    public List<OrganisationUnitLevel> getFilledOrganisationUnitLevels()
    {
        Map<Integer, OrganisationUnitLevel> levelMap = getOrganisationUnitLevelMap();

        List<OrganisationUnitLevel> levels = new ArrayList<OrganisationUnitLevel>();

        for ( int i = 0; i < getNumberOfOrganisationalLevels(); i++ )
        {
            int level = i + 1;

            levels.add( levelMap.get( level ) != null ? levelMap.get( level ) : new OrganisationUnitLevel( level,
                LEVEL_PREFIX + level ) );
        }

        return levels;
    }

    public Map<Integer, OrganisationUnitLevel> getOrganisationUnitLevelMap()
    {
        Map<Integer, OrganisationUnitLevel> levelMap = new HashMap<Integer, OrganisationUnitLevel>();

        Collection<OrganisationUnitLevel> levels = getOrganisationUnitLevels();

        for ( OrganisationUnitLevel level : levels )
        {
            levelMap.put( level.getLevel(), level );
        }

        return levelMap;
    }

    @Override
    public int getNumberOfOrganisationUnits()
    {
        return organisationUnitStore.getCount();
    }

    @Override
    public int getMaxOfOrganisationUnitLevels()
    {
        return organisationUnitLevelStore.getMaxLevels();
    }

    public Collection<OrganisationUnit> getWithinCoordinateArea( double longitude, double latitude, double distance )
    {
        Collection<OrganisationUnit> objects = organisationUnitStore.getWithinCoordinateArea( GeoUtils.getBoxShape( longitude, latitude, distance ) );

        // Go through the list and remove the ones located farther than the distance.
        if ( objects != null && objects.size() > 0 )
        {
            Iterator<OrganisationUnit> iter = objects.iterator();

            Point2D centerPoint = new Point2D.Double( longitude, latitude );

            while ( iter.hasNext() )
            {
                OrganisationUnit orgunit = iter.next();

                double distancebetween = GeoUtils.getDistanceBetweenTwoPoints( centerPoint,
                    ValidationUtils.getCoordinatePoint2D( orgunit.getCoordinates() ) );

                if ( distancebetween > distance )
                {
                    // Remove the orgUnits that is outside of the distance range 
                    // - due to the 'getWithinCoordinateArea' looking at square area instead of circle.
                    iter.remove();
                }
            }
        }

        return objects;
    }

    private Collection<OrganisationUnit> getOrganisationUnitByLocation( Collection<OrganisationUnit> orgUnits_source,
        double longitude, double latitude )
    {
        Collection<OrganisationUnit> orgUnits = new ArrayList<OrganisationUnit>();

        for ( OrganisationUnit ou : orgUnits_source )
        {
            String ou_featureType = ou.getFeatureType();
            String ou_coordinate = ou.getCoordinates();

            if ( ou_featureType != null
                && ou_coordinate != null
                && !ou_coordinate.isEmpty()
                && (ou_featureType.compareTo( OrganisationUnit.FEATURETYPE_POLYGON ) == 0 || ou_featureType
                    .compareTo( OrganisationUnit.FEATURETYPE_MULTIPOLYGON ) == 0) )
            {

                if ( GeoUtils.checkPointWithMultiPolygon( longitude, latitude, ou.getCoordinates(), ou_featureType ) )
                {
                    orgUnits.add( ou );
                    System.out.println( "Location Match OU: [" + ou.getLevel() + "], " + ou.getName() + ", "
                        + ou.getUid() );
                }

            }
        }

        return orgUnits;

    }

    private Collection<OrganisationUnit> getTopLevelOrgUnitWithPoint( double longitude, double latitude, int searchLvl,
        int stopLvl )
    {
        Collection<OrganisationUnit> orgUnits = new ArrayList<OrganisationUnit>();

        // Search down the level until it finds a orgunit with
        // polygon/multipolygon that contains the point

        for ( int i = searchLvl; i <= stopLvl; i++ )
        {
            Collection<OrganisationUnit> orgUnits_onLevel = getOrganisationUnitByLocation(
                getOrganisationUnitsAtLevel( i ), longitude, latitude );

            if ( orgUnits_onLevel.size() > 0 )
            {
                orgUnits = orgUnits_onLevel;
                break;
            }
        }

        return orgUnits;
    }

    public Collection<OrganisationUnit> getOrgUnitByLocation( double longitude, double latitude, String topOrgUnitUid,
        Integer targetLevel )
    {
        Collection<OrganisationUnit> orgUnits = new ArrayList<OrganisationUnit>();

        if ( GeoUtils.checkGeoJsonPointValid( longitude, latitude ) )
        {

            OrganisationUnit topOrgUnit = null;

            // 1. Get top org unit of search.
            if ( topOrgUnitUid != null && topOrgUnitUid != "" )
            {
                topOrgUnit = getOrganisationUnit( topOrgUnitUid );
            }
            else
            {
                // Get top orgunit that contains the coordinate - to get a top
                // search point.
                Collection<OrganisationUnit> orgUnits_TopLevel = getTopLevelOrgUnitWithPoint( longitude, latitude, 1,
                    getNumberOfOrganisationalLevels() - 1 );

                if ( orgUnits_TopLevel.size() == 1 )
                {
                    topOrgUnit = orgUnits_TopLevel.iterator().next();
                }
                else
                {
                    System.out.print( "Multiple Top Level Org Unit found with the coordinate: " );

                    for ( OrganisationUnit ou : orgUnits_TopLevel )
                    {
                        System.out.print( " [" + ou.getLevel() + "] " + ou.getName() + " " + ou.getUid() + " " );
                    }

                    System.out.println( "" );
                }

            }

            // 2. From the found top level orgunit, search children orgunits to
            // get the lowest level orgunit that contains the coordinate
            if ( topOrgUnit == null )
            {
                System.out.println( "Could not retrieve a top org Unit." );
            }
            else
            {
                Collection<OrganisationUnit> orgUnits_childrens = new ArrayList<OrganisationUnit>();

                if ( targetLevel != null )
                {
                    orgUnits_childrens = getOrganisationUnitsAtLevel( targetLevel, topOrgUnit );
                }
                else
                {
                    orgUnits_childrens = getOrganisationUnitWithChildren( topOrgUnit.getId() );
                }

                Collection<OrganisationUnit> orgUnits_found = getOrganisationUnitByLocation( orgUnits_childrens,
                    longitude, latitude );

                // 3. for all the org units with polygon containing the coordinate
                // , get polygon with highest level

                int bottomLvl = topOrgUnit.getLevel();

                for ( OrganisationUnit ou : orgUnits_found )
                {

                    if ( ou.getLevel() > bottomLvl )
                    {
                        bottomLvl = ou.getLevel();
                    }
                }

                for ( OrganisationUnit ou : orgUnits_found )
                {
                    if ( ou.getLevel() == bottomLvl )
                    {
                        System.out.println( "Bottom Lvl OU Found: [" + ou.getLevel() + "], " + ou.getName() + ", "
                            + ou.getUid() );

                        orgUnits.add( ou );
                    }
                }
            }

        }
        else
        {
            System.out.println( "Could not create a valid GeoJson Point from coordinate.  Longitude: " + longitude
                + ", latitude: " + latitude );
        }

        return orgUnits;
    }

    // -------------------------------------------------------------------------
    // Version
    // -------------------------------------------------------------------------

    @Override
    public void updateVersion()
    {
        versionService.updateVersion( VersionService.ORGANISATIONUNIT_VERSION );
    }
}
