package org.hisp.dhis.organisationunit.hibernate;

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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.AuditLogUtil;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitHierarchy;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.system.objectmapper.OrganisationUnitRelationshipRowMapper;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.security.access.AccessDeniedException;

/**
 * @author Kristian Nordal
 */
public class HibernateOrganisationUnitStore
    extends HibernateIdentifiableObjectStore<OrganisationUnit>
    implements OrganisationUnitStore
{
    private static final Log log = LogFactory.getLog( HibernateOrganisationUnitStore.class );

    @Autowired
    private CurrentUserService currentUserService;

    // -------------------------------------------------------------------------
    // OrganisationUnit
    // -------------------------------------------------------------------------

    @Override
    public OrganisationUnit getByUuid( String uuid )
    {
        OrganisationUnit object = getObject( Restrictions.eq( "uuid", uuid ) );

        if ( !isReadAllowed( object ) )
        {
            AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_READ_DENIED );
            throw new AccessDeniedException( "You do not have read access to object with uuid " + uuid );
        }

        return object;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<OrganisationUnit> getAllOrganisationUnitsByStatus( boolean active )
    {
        Query query = getQuery( "from OrganisationUnit o where o.active is :active" );
        query.setParameter( "active", active );

        return query.list();
    }

    @Override
    public Collection<OrganisationUnit> getAllOrganisationUnitsByLastUpdated( Date lastUpdated )
    {
        return getAllGeLastUpdated( lastUpdated );
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<OrganisationUnit> getAllOrganisationUnitsByStatusLastUpdated( boolean active, Date lastUpdated )
    {
        return getCriteria().add( Restrictions.ge( "lastUpdated", lastUpdated ) ).add( Restrictions.eq( "active", active ) ).list();
    }

    @Override
    public OrganisationUnit getOrganisationUnitByNameIgnoreCase( String name )
    {
        return (OrganisationUnit) getCriteria( Restrictions.eq( "name", name ).ignoreCase() ).uniqueResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<OrganisationUnit> getRootOrganisationUnits()
    {
        return getQuery( "from OrganisationUnit o where o.parent is null" ).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<OrganisationUnit> getOrganisationUnitsWithoutGroups()
    {
        return getQuery( "from OrganisationUnit o where o.groups.size = 0" ).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<OrganisationUnit> getOrganisationUnitsByNameAndGroups( String query,
        Collection<OrganisationUnitGroup> groups, boolean limit )
    {
        boolean first = true;

        query = StringUtils.trimToNull( query );
        groups = CollectionUtils.isEmpty( groups ) ? null : groups;

        StringBuilder hql = new StringBuilder( "from OrganisationUnit o" );

        if ( query != null )
        {
            hql.append( " where ( lower(o.name) like :expression or o.code = :query or o.uid = :query )" );

            first = false;
        }

        if ( groups != null )
        {
            for ( int i = 0; i < groups.size(); i++ )
            {
                String clause = first ? " where" : " and";

                hql.append( clause ).append( " :g" ).append( i ).append( " in elements( o.groups )" );

                first = false;
            }
        }

        Query q = sessionFactory.getCurrentSession().createQuery( hql.toString() );

        if ( query != null )
        {
            q.setString( "expression", "%" + query.toLowerCase() + "%" );
            q.setString( "query", query );
        }

        if ( groups != null )
        {
            int i = 0;

            for ( OrganisationUnitGroup group : groups )
            {
                q.setEntity( "g" + i++, group );
            }
        }

        if ( limit )
        {
            q.setMaxResults( OrganisationUnitService.MAX_LIMIT );
        }

        return q.list();
    }

    public Map<String, Set<String>> getOrganisationUnitDataSetAssocationMap()
    {
        final String sql = "select ds.uid as ds_uid, ou.uid as ou_uid from datasetsource d " +
            "left join organisationunit ou on ou.organisationunitid=d.sourceid " +
            "left join dataset ds on ds.datasetid=d.datasetid";

        final Map<String, Set<String>> map = new HashMap<String, Set<String>>();

        jdbcTemplate.query( sql, new RowCallbackHandler()
        {
            public void processRow( ResultSet rs ) throws SQLException
            {
                String dataSetId = rs.getString( "ds_uid" );
                String organisationUnitId = rs.getString( "ou_uid" );

                Set<String> dataSets = map.get( organisationUnitId );

                if ( dataSets == null )
                {
                    dataSets = new HashSet<String>();
                    map.put( organisationUnitId, dataSets );
                }

                dataSets.add( dataSetId );
            }
        } );

        return map;
    }

    @Override
    public Map<String, Set<String>> getOrganisationUnitGroupDataSetAssocationMap()
    {
        final String sql = "select ds.uid as ds_uid, ou.uid as ou_uid from orgunitgroupdatasets ougds " +
            "left join orgunitgroupmembers ougm on ougds.orgunitgroupid=ougm.orgunitgroupid " +
            "left join organisationunit ou on ou.organisationunitid=ougm.organisationunitid " +
            "left join dataset ds on ds.datasetid=ougds.datasetid";

        final Map<String, Set<String>> map = new HashMap<String, Set<String>>();

        jdbcTemplate.query( sql, new RowCallbackHandler()
        {
            public void processRow( ResultSet rs ) throws SQLException
            {
                String dataSetId = rs.getString( "ds_uid" );
                String organisationUnitId = rs.getString( "ou_uid" );

                Set<String> dataSets = map.get( organisationUnitId );

                if ( dataSets == null )
                {
                    dataSets = new HashSet<String>();
                    map.put( organisationUnitId, dataSets );
                }

                dataSets.add( dataSetId );
            }
        } );

        return map;
    }

    public Set<Integer> getOrganisationUnitIdsWithoutData()
    {
        final String sql = "select organisationunitid from organisationunit ou where not exists (" +
            "select sourceid from datavalue where sourceid=ou.organisationunitid)";

        final Set<Integer> units = new HashSet<Integer>();

        jdbcTemplate.query( sql, new RowCallbackHandler()
        {
            public void processRow( ResultSet rs ) throws SQLException
            {
                units.add( rs.getInt( 1 ) );
            }
        } );

        return units;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<OrganisationUnit> getBetweenByStatus( boolean status, int first, int max )
    {
        Criteria criteria = getCriteria().add( Restrictions.eq( "active", status ) );
        criteria.setFirstResult( first );
        criteria.setMaxResults( max );

        return criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<OrganisationUnit> getBetweenByLastUpdated( Date lastUpdated, int first, int max )
    {
        Criteria criteria = getCriteria().add( Restrictions.ge( "lastUpdated", lastUpdated ) );
        criteria.setFirstResult( first );
        criteria.setMaxResults( max );

        return criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<OrganisationUnit> getBetweenByStatusLastUpdated( boolean status, Date lastUpdated, int first, int max )
    {
        Criteria criteria = getCriteria().add( Restrictions.ge( "lastUpdated", lastUpdated ) ).add( Restrictions.eq( "active", status ) );
        criteria.setFirstResult( first );
        criteria.setMaxResults( max );

        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public Collection<OrganisationUnit> getWithinCoordinateArea( double[] box )
    {
        return getQuery( "from OrganisationUnit o"
            + " where o.featureType='Point'"
            + " and o.coordinates is not null"
            + " and CAST( SUBSTRING(o.coordinates, 2, LOCATE(',', o.coordinates) - 2) AS big_decimal ) >= " + box[3]
            + " and CAST( SUBSTRING(o.coordinates, 2, LOCATE(',', o.coordinates) - 2) AS big_decimal ) <= " + box[1]
            + " and CAST( SUBSTRING(coordinates, LOCATE(',', o.coordinates) + 1, LOCATE(']', o.coordinates) - LOCATE(',', o.coordinates) - 1 ) AS big_decimal ) >= " + box[2]
            + " and CAST( SUBSTRING(coordinates, LOCATE(',', o.coordinates) + 1, LOCATE(']', o.coordinates) - LOCATE(',', o.coordinates) - 1 ) AS big_decimal ) <= " + box[0]
        ).list();
    }

    // -------------------------------------------------------------------------
    // OrganisationUnitHierarchy
    // -------------------------------------------------------------------------

    public OrganisationUnitHierarchy getOrganisationUnitHierarchy()
    {
        final String sql = "select organisationunitid, parentid from organisationunit";

        return new OrganisationUnitHierarchy( jdbcTemplate.query( sql, new OrganisationUnitRelationshipRowMapper() ) );
    }

    public void updateOrganisationUnitParent( int organisationUnitId, int parentId )
    {
        Timestamp now = new Timestamp( new Date().getTime() );

        final String sql = "update organisationunit " + "set parentid=" + parentId + ", lastupdated='"
            + now + "' " + "where organisationunitid=" + organisationUnitId;

        jdbcTemplate.execute( sql );
    }
}
