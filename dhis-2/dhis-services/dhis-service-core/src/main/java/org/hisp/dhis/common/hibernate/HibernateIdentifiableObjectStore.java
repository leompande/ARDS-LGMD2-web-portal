package org.hisp.dhis.common.hibernate;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.AuditLogUtil;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.GenericNameableObjectStore;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.hibernate.exception.ReadAccessDeniedException;
import org.hisp.dhis.user.User;

/**
 * @author bobj
 */
public class HibernateIdentifiableObjectStore<T extends BaseIdentifiableObject>
    extends HibernateGenericStore<T> implements GenericNameableObjectStore<T>
{
    private static final Log log = LogFactory.getLog( HibernateIdentifiableObjectStore.class );
    
    @Override
    public int save( T object )
    {
        object.setAutoFields();
        return super.save( object );
    }

    @Override
    public void update( T object )
    {
        object.setAutoFields();
        super.update( object );
    }

    @Override
    public final T getByUid( String uid )
    {
        T object = getObject( Restrictions.eq( "uid", uid ) );

        if ( !isReadAllowed( object ) )
        {
            AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_READ_DENIED );
            throw new ReadAccessDeniedException( object.toString() );
        }

        return object;
    }

    @Override
    public final T getByUidNoAcl( String uid )
    {
        return getObject( Restrictions.eq( "uid", uid ) );
    }

    @Override
    public final void updateNoAcl( T object )
    {
        sessionFactory.getCurrentSession().update( object );
    }

    @Override
    @Deprecated
    public final T getByName( String name )
    {
        T object = getObject( Restrictions.eq( "name", name ) );

        if ( !isReadAllowed( object ) )
        {
            AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_READ_DENIED );
            throw new ReadAccessDeniedException( object.toString() );
        }

        return object;
    }

    @Override
    @Deprecated
    public final T getByShortName( String shortName )
    {
        T object = getObject( Restrictions.eq( "shortName", shortName ) );

        if ( !isReadAllowed( object ) )
        {
            AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_READ_DENIED );
            throw new ReadAccessDeniedException( object.toString() );
        }

        return object;
    }

    @Override
    public final T getByCode( String code )
    {
        T object = getObject( Restrictions.eq( "code", code ) );

        if ( !isReadAllowed( object ) )
        {
            AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_READ_DENIED );
            throw new ReadAccessDeniedException( object.toString() );
        }

        return object;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getAllEqName( String name )
    {
        Query query = sharingEnabled() ? getQueryAllEqNameAcl( name ) : getQueryAllEqName( name );

        return query.list();
    }

    private Query getQueryAllEqNameAcl( String name )
    {
        String hql = "select distinct c from " + clazz.getName() + " c"
            + " where name = :name and ( c.publicAccess like 'r%' or c.user IS NULL or c.user=:user"
            + " or exists "
            + "     (from c.userGroupAccesses uga join uga.userGroup ug join ug.members ugm where ugm = :user and uga.access like 'r%')"
            + " ) order by c.name";

        Query query = getQuery( hql );
        query.setEntity( "user", currentUserService.getCurrentUser() );
        query.setString( "name", name );

        return query;
    }

    private Query getQueryAllEqName( String name )
    {
        Query query = getQuery( "from " + clazz.getName() + " c where name = :name order by c.name" );
        query.setString( "name", name );

        return query;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getAllEqNameIgnoreCase( String name )
    {
        Query query = sharingEnabled() ? getQueryAllEqNameAclIgnoreCase( name ) : getQueryAllEqNameIgnoreCase( name );

        return query.list();
    }

    private Query getQueryAllEqNameAclIgnoreCase( String name )
    {
        String hql = "select distinct c from " + clazz.getName() + " c"
            + " where lower(name) = :name and ( c.publicAccess like 'r%' or c.user IS NULL or c.user=:user"
            + " or exists "
            + "     (from c.userGroupAccesses uga join uga.userGroup ug join ug.members ugm where ugm = :user and uga.access like 'r%')"
            + " ) order by c.name";

        Query query = getQuery( hql );
        query.setEntity( "user", currentUserService.getCurrentUser() );
        query.setString( "name", name.toLowerCase() );

        return query;
    }

    private Query getQueryAllEqNameIgnoreCase( String name )
    {
        Query query = getQuery( "from " + clazz.getName() + " c where lower(name) = :name order by c.name" );
        query.setString( "name", name.toLowerCase() );

        return query;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getAllEqShortName( String shortName )
    {
        Query query = sharingEnabled() ? getQueryAllEqShortNameAcl( shortName ) : getQueryAllEqShortName( shortName );

        return query.list();
    }

    private Query getQueryAllEqShortNameAcl( String shortName )
    {
        String hql = "select distinct c from " + clazz.getName() + " c"
            + " where shortName = :shortName and ( c.publicAccess like 'r%' or c.user IS NULL or c.user=:user"
            + " or exists "
            + "     (from c.userGroupAccesses uga join uga.userGroup ug join ug.members ugm where ugm = :user and uga.access like 'r%')"
            + " ) order by c.shortName";

        Query query = getQuery( hql );
        query.setEntity( "user", currentUserService.getCurrentUser() );
        query.setString( "shortName", shortName );

        return query;
    }

    private Query getQueryAllEqShortName( String shortName )
    {
        Query query = getQuery( "from " + clazz.getName() + " c where shortName = :shortName order by c.shortName" );
        query.setString( "shortName", shortName );

        return query;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getAllEqShortNameIgnoreCase( String shortName )
    {
        Query query = sharingEnabled() ? getQueryAllEqShortNameAclIgnoreCase( shortName ) : getQueryAllEqShortNameIgnoreCase( shortName );

        return query.list();
    }

    private Query getQueryAllEqShortNameAclIgnoreCase( String shortName )
    {
        String hql = "select distinct c from " + clazz.getName() + " c"
            + " where lower(shortName) = :shortName and ( c.publicAccess like 'r%' or c.user IS NULL or c.user=:user"
            + " or exists "
            + "     (from c.userGroupAccesses uga join uga.userGroup ug join ug.members ugm where ugm = :user and uga.access like 'r%')"
            + " ) order by c.shortName";

        Query query = getQuery( hql );
        query.setEntity( "user", currentUserService.getCurrentUser() );
        query.setString( "shortName", shortName.toLowerCase() );

        return query;
    }

    private Query getQueryAllEqShortNameIgnoreCase( String shortName )
    {
        Query query = getQuery( "from " + clazz.getName() + " c where lower(shortName) = :shortName order by c.shortName" );
        query.setString( "shortName", shortName.toLowerCase() );

        return query;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getAllLikeName( String name )
    {
        Query query = sharingEnabled() ? getQueryAllLikeNameAcl( name ) : getQueryAllLikeName( name );

        return query.list();
    }

    private Query getQueryAllLikeNameAcl( String name )
    {
        String hql = "select distinct c from " + clazz.getName() + " c"
            + " where lower(name) like :name and ( c.publicAccess like 'r%' or c.user IS NULL or c.user=:user"
            + " or exists "
            + "     (from c.userGroupAccesses uga join uga.userGroup ug join ug.members ugm where ugm = :user and uga.access like 'r%')"
            + " ) order by c.name";

        Query query = getQuery( hql );
        query.setEntity( "user", currentUserService.getCurrentUser() );
        query.setString( "name", "%" + name.toLowerCase() + "%" );

        return query;
    }

    private Query getQueryAllLikeName( String name )
    {
        Query query = getQuery( "from " + clazz.getName() + " c where lower(name) like :name order by c.name" );
        query.setString( "name", "%" + name.toLowerCase() + "%" );

        return query;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getAllLikeShortName( String shortName )
    {
        if ( NameableObject.class.isAssignableFrom( clazz ) )
        {
            Query query = sharingEnabled() ? getQueryAllLikeShortNameAcl( shortName ) : getQueryAllLikeShortName( shortName );
            return query.list();
        }

        // fallback to using name
        return getAllLikeName( shortName );
    }

    private Query getQueryAllLikeShortNameAcl( String shortName )
    {
        String hql = "select distinct c from " + clazz.getName() + " c"
            + " where lower(shortName) like :shortName and ( c.publicAccess like 'r%' or c.user IS NULL or c.user=:user"
            + " or exists "
            + "     (from c.userGroupAccesses uga join uga.userGroup ug join ug.members ugm where ugm = :user and uga.access like 'r%')"
            + " ) order by c.shortName";

        Query query = getQuery( hql );
        query.setEntity( "user", currentUserService.getCurrentUser() );
        query.setString( "shortName", "%" + shortName.toLowerCase() + "%" );

        return query;
    }

    private Query getQueryAllLikeShortName( String shortName )
    {
        Query query = getQuery( "from " + clazz.getName() + " c where lower(shortName) like :shortName order by c.shortName" );
        query.setString( "shortName", "%" + shortName.toLowerCase() + "%" );

        return query;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getAllOrderedName()
    {
        Query query = sharingEnabled() ? getQueryAllOrderedNameAcl() : getQueryAllOrderedName();

        return query.list();
    }

    private Query getQueryAllOrderedNameAcl()
    {
        String hql = "select distinct c from " + clazz.getName() + " c"
            + " where c.publicAccess like 'r%' or c.user IS NULL or c.user=:user"
            + " or exists "
            + "     (from c.userGroupAccesses uga join uga.userGroup ug join ug.members ugm where ugm = :user and uga.access like 'r%')"
            + " order by c.name";

        Query query = getQuery( hql );
        query.setEntity( "user", currentUserService.getCurrentUser() );

        return query;
    }

    private Query getQueryAllOrderedName()
    {
        return getQuery( "from " + clazz.getName() + " c order by c.name" );
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getAllOrderedName( int first, int max )
    {
        Query query = sharingEnabled() ? getQueryAllOrderedNameAcl() : getQueryAllOrderedName();

        query.setFirstResult( first );
        query.setMaxResults( max );

        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getAllOrderedLastUpdated( int first, int max )
    {
        Query query = sharingEnabled() ? getQueryAllOrderedLastUpdatedAcl() : getQueryAllOrderedLastUpdated();

        query.setFirstResult( first );
        query.setMaxResults( max );

        return query.list();
    }

    private Query getQueryAllOrderedLastUpdatedAcl()
    {
        String hql = "select distinct c from " + clazz.getName() + " c"
            + " where c.publicAccess like 'r%' or c.user IS NULL or c.user=:user"
            + " or exists "
            + "     (from c.userGroupAccesses uga join uga.userGroup ug join ug.members ugm where ugm = :user and uga.access like 'r%')"
            + " order by c.lastUpdated desc";

        Query query = getQuery( hql );
        query.setEntity( "user", currentUserService.getCurrentUser() );

        return query;
    }

    private Query getQueryAllOrderedLastUpdated()
    {
        return getQuery( "from " + clazz.getName() + " c order by lastUpdated desc" );
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getAllLikeNameOrderedName( String name, int first, int max )
    {
        Query query = sharingEnabled() ? getQueryAllLikeNameOrderedNameAcl( name ) : getQueryAllLikeNameOrderedName( name );

        query.setFirstResult( first );
        query.setMaxResults( max );

        return query.list();
    }

    private Query getQueryAllLikeNameOrderedNameAcl( String name )
    {
        String hql = "select distinct c from " + clazz.getName() + " c"
            + " where lower(c.name) like :name and ( c.publicAccess like 'r%' or c.user IS NULL or c.user=:user"
            + " or exists "
            + "     (from c.userGroupAccesses uga join uga.userGroup ug join ug.members ugm where ugm = :user and uga.access like 'r%')"
            + " ) order by c.name";

        Query query = getQuery( hql );
        query.setString( "name", "%" + name.toLowerCase() + "%" );
        query.setEntity( "user", currentUserService.getCurrentUser() );

        return query;
    }

    private Query getQueryAllLikeNameOrderedName( String name )
    {
        Query query = getQuery( "from " + clazz.getName() + " c where lower(name) like :name order by name" );
        query.setString( "name", "%" + name.toLowerCase() + "%" );

        return query;
    }

    @Override
    public int getCountLikeName( String name )
    {
        Query query = sharingEnabled() ? getQueryCountLikeNameAcl( name ) : getQueryCountLikeName( name );

        return ((Long) query.uniqueResult()).intValue();
    }

    private Query getQueryCountLikeNameAcl( String name )
    {
        String hql = "select count(distinct c) from " + clazz.getName() + " c"
            + " where lower(name) like :name and (c.publicAccess like 'r%' or c.user IS NULL or c.user=:user"
            + " or exists "
            + "     (from c.userGroupAccesses uga join uga.userGroup ug join ug.members ugm where ugm = :user and uga.access like 'r%')"
            + " )";

        Query query = getQuery( hql );
        query.setEntity( "user", currentUserService.getCurrentUser() );
        query.setString( "name", "%" + name.toLowerCase() + "%" );

        return query;
    }

    private Query getQueryCountLikeName( String name )
    {
        Query query = getQuery( "select count(distinct c) from " + clazz.getName() + " c where lower(name) like :name" );
        query.setString( "name", "%" + name.toLowerCase() + "%" );

        return query;
    }

    @Override
    public long getCountGeLastUpdated( Date lastUpdated )
    {
        Query query = sharingEnabled() ? getQueryCountGeLastUpdatedAcl( lastUpdated ) : getQueryCountGeLastUpdated( lastUpdated );

        return ((Long) query.uniqueResult()).intValue();
    }

    private Query getQueryCountGeLastUpdatedAcl( Date lastUpdated )
    {
        String hql = "select count(distinct c) from " + clazz.getName() + " c"
            + " where c.lastUpdated >= :lastUpdated and (c.publicAccess like 'r%' or c.user IS NULL or c.user=:user"
            + " or exists "
            + "     (from c.userGroupAccesses uga join uga.userGroup ug join ug.members ugm where ugm = :user and uga.access like 'r%')"
            + " )";

        Query query = getQuery( hql );
        query.setEntity( "user", currentUserService.getCurrentUser() );
        query.setTimestamp( "lastUpdated", lastUpdated );

        return query;
    }

    private Query getQueryCountGeLastUpdated( Date lastUpdated )
    {
        Query query = getQuery( "select count(distinct c) from " + clazz.getName() + " c where lastUpdated >= :lastUpdated" );
        query.setTimestamp( "lastUpdated", lastUpdated );

        return query;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getAllGeLastUpdated( Date lastUpdated )
    {
        Query query = sharingEnabled() ? getQueryAllGeLastUpdatedAcl( lastUpdated ) : getQueryAllGeLastUpdated( lastUpdated );

        return query.list();
    }

    private Query getQueryAllGeLastUpdatedAcl( Date lastUpdated )
    {
        String hql = "select distinct c from " + clazz.getName() + " c"
            + " where c.lastUpdated >= :lastUpdated and ( c.publicAccess like 'r%' or c.user IS NULL or c.user=:user"
            + " or exists "
            + "     (from c.userGroupAccesses uga join uga.userGroup ug join ug.members ugm where ugm = :user and uga.access like 'r%')"
            + " )";

        Query query = getQuery( hql );
        query.setEntity( "user", currentUserService.getCurrentUser() );
        query.setTimestamp( "lastUpdated", lastUpdated );

        return query;
    }

    private Query getQueryAllGeLastUpdated( Date lastUpdated )
    {
        Query query = getQuery( "from " + clazz.getName() + " c where c.lastUpdated >= :lastUpdated" );
        query.setTimestamp( "lastUpdated", lastUpdated );

        return query;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getAllGeCreated( Date created )
    {
        Query query = sharingEnabled() ? getQueryAllGeCreatedAcl( created ) : getQueryAllGeCreated( created );

        return query.list();
    }

    private Query getQueryAllGeCreatedAcl( Date created )
    {
        String hql = "select distinct c from " + clazz.getName() + " c"
            + " where c.created >= :created and ( c.publicAccess like 'r%' or c.user IS NULL or c.user=:user"
            + " or exists "
            + "     (from c.userGroupAccesses uga join uga.userGroup ug join ug.members ugm where ugm = :user and uga.access like 'r%')"
            + " ) order by c.name";

        Query query = getQuery( hql );
        query.setEntity( "user", currentUserService.getCurrentUser() );
        query.setTimestamp( "created", created );

        return query;
    }

    private Query getQueryAllGeCreated( Date created )
    {
        Query query = getQuery( "from " + clazz.getName() + " c where c.created >= :created" );
        query.setTimestamp( "created", created );

        return query;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getAllGeLastUpdatedOrderedName( Date lastUpdated )
    {
        Query query = sharingEnabled() ? getQueryAllGeLastUpdatedOrderedNameAcl( lastUpdated ) : getQueryAllGeLastUpdatedOrderedName( lastUpdated );

        return query.list();
    }

    private Query getQueryAllGeLastUpdatedOrderedNameAcl( Date lastUpdated )
    {
        String hql = "select distinct c from " + clazz.getName() + " c"
            + " where c.lastUpdated >= :lastUpdated and ( c.publicAccess like 'r%' or c.user IS NULL or c.user=:user"
            + " or exists "
            + "     (from c.userGroupAccesses uga join uga.userGroup ug join ug.members ugm where ugm = :user and uga.access like 'r%')"
            + " ) order by c.name";

        Query query = getQuery( hql );
        query.setEntity( "user", currentUserService.getCurrentUser() );
        query.setTimestamp( "lastUpdated", lastUpdated );

        return query;
    }

    private Query getQueryAllGeLastUpdatedOrderedName( Date lastUpdated )
    {
        Query query = getQuery( "from " + clazz.getName() + " c where c.lastUpdated >= :lastUpdated order by c.name" );
        query.setTimestamp( "lastUpdated", lastUpdated );

        return query;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getByUser( User user )
    {
        Query query = getQuery( "from " + clazz.getName() + " c where user = :user" );
        query.setEntity( "user", user );

        return query.list();
    }

    @Override
    public List<T> getByUid( Collection<String> uids )
    {
        List<T> list = new ArrayList<T>();

        if ( uids != null )
        {
            for ( String uid : uids )
            {
                T object = getByUid( uid );

                if ( object != null )
                {
                    list.add( object );
                }
            }
        }

        return list;
    }

    @Override
    public List<T> getByUidNoAcl( Collection<String> uids )
    {
        List<T> list = new ArrayList<T>();

        if ( uids != null )
        {
            for ( String uid : uids )
            {
                T object = getByUidNoAcl( uid );

                if ( object != null )
                {
                    list.add( object );
                }
            }
        }

        return list;
    }

    //----------------------------------------------------------------------------------------------------------------
    // No ACL (unfiltered methods)
    //----------------------------------------------------------------------------------------------------------------

    @Override
    public int getCountEqNameNoAcl( String name )
    {
        Query query = getQuery( "select count(distinct c) from " + clazz.getName() + " c where c.name = :name" );
        query.setParameter( "name", name );

        return ((Long) query.uniqueResult()).intValue();
    }

    @Override
    public int getCountEqShortNameNoAcl( String shortName )
    {
        Query query = getQuery( "select count(distinct c) from " + clazz.getName() + " c where c.shortName = :shortName" );
        query.setParameter( "shortName", shortName );

        return ((Long) query.uniqueResult()).intValue();
    }
}
