package org.hisp.dhis.hibernate;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hisp.dhis.common.AccessStringHelper;
import org.hisp.dhis.common.AuditLogUtil;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.GenericStore;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.SharingUtils;
import org.hisp.dhis.dashboard.Dashboard;
import org.hisp.dhis.hibernate.exception.CreateAccessDeniedException;
import org.hisp.dhis.hibernate.exception.DeleteAccessDeniedException;
import org.hisp.dhis.hibernate.exception.ReadAccessDeniedException;
import org.hisp.dhis.hibernate.exception.UpdateAccessDeniedException;
import org.hisp.dhis.interpretation.Interpretation;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserGroupAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;

/**
 * @author Lars Helge Overland
 */
public class HibernateGenericStore<T>
    implements GenericStore<T>
{
    private static final Log log = LogFactory.getLog( HibernateGenericStore.class );

    protected SessionFactory sessionFactory;

    @Required
    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    protected JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    protected CurrentUserService currentUserService;

    protected Class<T> clazz;

    /**
     * Could be overridden programmatically.
     */
    public Class<T> getClazz()
    {
        return clazz;
    }

    /**
     * Could be injected through container.
     */
    @Required
    public void setClazz( Class<T> clazz )
    {
        this.clazz = clazz;
    }

    protected boolean cacheable = false;

    /**
     * Could be overridden programmatically.
     */
    protected boolean isCacheable()
    {
        return cacheable;
    }

    /**
     * Could be injected through container.
     */
    public void setCacheable( boolean cacheable )
    {
        this.cacheable = cacheable;
    }

    // -------------------------------------------------------------------------
    // Convenience methods
    // -------------------------------------------------------------------------

    /**
     * Returns the current session.
     *
     * @return the current session.
     */
    protected final Session getSession()
    {
        return sessionFactory.getCurrentSession();
    }

    /**
     * Creates a Query.
     *
     * @param hql the hql query.
     * @return a Query instance.
     */
    protected final Query getQuery( String hql )
    {
        return sessionFactory.getCurrentSession().createQuery( hql ).setCacheable( cacheable );
    }

    /**
     * Creates a SqlQuery.
     *
     * @param sql the sql query.
     * @return a SqlQuery instance.
     */
    protected final SQLQuery getSqlQuery( String sql )
    {
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery( sql );
        query.setCacheable( cacheable );
        return query;
    }

    /**
     * Creates a Criteria for the implementation Class type.
     *
     * @return a Criteria instance.
     */
    protected final Criteria getCriteria()
    {
        return getClazzCriteria().setCacheable( cacheable );
    }

    protected Criteria getClazzCriteria()
    {
        return sessionFactory.getCurrentSession().createCriteria( getClazz() );
    }

    /**
     * Creates a Criteria for the implementation Class type restricted by the
     * given Criterions.
     *
     * @param expressions the Criterions for the Criteria.
     * @return a Criteria instance.
     */
    protected final Criteria getCriteria( Criterion... expressions )
    {
        Criteria criteria = getCriteria();

        for ( Criterion expression : expressions )
        {
            criteria.add( expression );
        }

        return criteria;
    }

    /**
     * Retrieves an object based on the given Criterions.
     *
     * @param expressions the Criterions for the Criteria.
     * @return an object of the implementation Class type.
     */
    @SuppressWarnings("unchecked")
    protected final T getObject( Criterion... expressions )
    {
        return (T) getCriteria( expressions ).uniqueResult();
    }

    /**
     * Retrieves a List based on the given Criterions.
     *
     * @param expressions the Criterions for the Criteria.
     * @return a List with objects of the implementation Class type.
     */
    @SuppressWarnings("unchecked")
    protected final List<T> getList( Criterion... expressions )
    {
        return getCriteria( expressions ).list();
    }

    // -------------------------------------------------------------------------
    // GenericIdentifiableObjectStore implementation
    // -------------------------------------------------------------------------

    @Override
    public int save( T object )
    {
        if ( !Interpretation.class.isAssignableFrom( clazz ) && currentUserService.getCurrentUser() != null && SharingUtils.isSupported( clazz ) )
        {
            BaseIdentifiableObject identifiableObject = (BaseIdentifiableObject) object;

            // TODO we might want to allow setting sharing props on save, but for now we null them out
            identifiableObject.setPublicAccess( null );
            identifiableObject.setUserGroupAccesses( new HashSet<UserGroupAccess>() );

            if ( identifiableObject.getUser() == null )
            {
                identifiableObject.setUser( currentUserService.getCurrentUser() );
            }

            if ( SharingUtils.canCreatePublic( currentUserService.getCurrentUser(), identifiableObject ) )
            {
                if ( SharingUtils.defaultPublic( clazz ) )
                {
                    String build = AccessStringHelper.newInstance()
                        .enable( AccessStringHelper.Permission.READ )
                        .enable( AccessStringHelper.Permission.WRITE )
                        .build();

                    identifiableObject.setPublicAccess( build );
                }
                else
                {
                    String build = AccessStringHelper.newInstance().build();
                    identifiableObject.setPublicAccess( build );
                }
            }
            else if ( SharingUtils.canCreatePrivate( currentUserService.getCurrentUser(), identifiableObject ) )
            {
                identifiableObject.setPublicAccess( AccessStringHelper.newInstance().build() );
            }
            else
            {
                AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_CREATE_DENIED );
                throw new CreateAccessDeniedException( object.toString() );
            }
        }

        AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_CREATE );
        return (Integer) sessionFactory.getCurrentSession().save( object );
    }

    @Override
    public void update( T object )
    {
        if ( !Interpretation.class.isAssignableFrom( clazz ) && !isUpdateAllowed( object ) )
        {
            AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_UPDATE_DENIED );
            throw new UpdateAccessDeniedException( object.toString() );
        }

        AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_UPDATE );
        sessionFactory.getCurrentSession().update( object );
    }

    @Override
    @SuppressWarnings("unchecked")
    public final T get( int id )
    {
        T object = (T) sessionFactory.getCurrentSession().get( getClazz(), id );

        if ( !isReadAllowed( object ) )
        {
            AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_READ_DENIED );
            throw new ReadAccessDeniedException( object.toString() );
        }

        return object;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final T load( int id )
    {
        T object = (T) sessionFactory.getCurrentSession().load( getClazz(), id );

        if ( !isReadAllowed( object ) )
        {
            AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_READ_DENIED );
            throw new ReadAccessDeniedException( object.toString() );
        }

        return object;
    }

    @Override
    public final void delete( T object )
    {
        if ( !isDeleteAllowed( object ) )
        {
            AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_DELETE_DENIED );
            throw new DeleteAccessDeniedException( object.toString() );
        }

        AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_DELETE );
        sessionFactory.getCurrentSession().delete( object );
    }

    @Override
    @SuppressWarnings("unchecked")
    public final List<T> getAll()
    {
        Query query = sharingEnabled() ? getQueryAllAcl() : getQueryAll();

        return query.list();
    }

    private Query getQueryAllAcl()
    {
        String hql = "select distinct c from " + clazz.getName() + " c"
            + " where c.publicAccess like 'r%' or c.user IS NULL or c.user=:user"
            + " or exists "
            + "     (from c.userGroupAccesses uga join uga.userGroup ug join ug.members ugm where ugm = :user and uga.access like 'r%')";

        Query query = getQuery( hql );
        query.setEntity( "user", currentUserService.getCurrentUser() );

        return query;
    }

    private Query getQueryAll()
    {
        return getQuery( "from " + clazz.getName() + " c" );
    }

    @Override
    public int getCount()
    {
        Query query = sharingEnabled() ? getQueryCountAcl() : getQueryCount();

        return ((Long) query.uniqueResult()).intValue();
    }

    private Query getQueryCountAcl()
    {
        String hql = "select count(distinct c) from " + clazz.getName() + " c"
            + " where c.publicAccess like 'r%' or c.user IS NULL or c.user=:user"
            + " or exists "
            + "     (from c.userGroupAccesses uga join uga.userGroup ug join ug.members ugm where ugm = :user and uga.access like 'r%')";

        Query query = getQuery( hql );
        query.setEntity( "user", currentUserService.getCurrentUser() );

        return query;
    }

    private Query getQueryCount()
    {
        return getQuery( "select count(distinct c) from " + clazz.getName() + " c" );
    }

    //----------------------------------------------------------------------------------------------------------------
    // Helpers
    //----------------------------------------------------------------------------------------------------------------

    protected boolean forceAcl()
    {
        return Dashboard.class.isAssignableFrom( clazz );
    }

    protected boolean sharingEnabled()
    {
        return forceAcl() || (SharingUtils.isSupported( clazz ) && !(currentUserService.getCurrentUser() == null ||
            CollectionUtils.containsAny( currentUserService.getCurrentUser().getUserCredentials().getAllAuthorities(), SharingUtils.SHARING_OVERRIDE_AUTHORITIES )));
    }

    protected boolean isReadAllowed( T object )
    {
        if ( IdentifiableObject.class.isInstance( object ) )
        {
            IdentifiableObject idObject = (IdentifiableObject) object;

            if ( sharingEnabled() )
            {
                return SharingUtils.canRead( currentUserService.getCurrentUser(), idObject );
            }
        }

        return true;
    }

    protected boolean isWriteAllowed( T object )
    {
        if ( IdentifiableObject.class.isInstance( object ) )
        {
            IdentifiableObject idObject = (IdentifiableObject) object;

            if ( sharingEnabled() )
            {
                return SharingUtils.canWrite( currentUserService.getCurrentUser(), idObject );
            }
        }

        return true;
    }

    protected boolean isUpdateAllowed( T object )
    {
        if ( IdentifiableObject.class.isInstance( object ) )
        {
            IdentifiableObject idObject = (IdentifiableObject) object;

            if ( SharingUtils.isSupported( clazz ) )
            {
                return SharingUtils.canUpdate( currentUserService.getCurrentUser(), idObject );
            }
        }

        return true;
    }

    protected boolean isDeleteAllowed( T object )
    {
        if ( IdentifiableObject.class.isInstance( object ) )
        {
            IdentifiableObject idObject = (IdentifiableObject) object;

            if ( SharingUtils.isSupported( clazz ) )
            {
                return SharingUtils.canDelete( currentUserService.getCurrentUser(), idObject );
            }
        }

        return true;
    }
}
