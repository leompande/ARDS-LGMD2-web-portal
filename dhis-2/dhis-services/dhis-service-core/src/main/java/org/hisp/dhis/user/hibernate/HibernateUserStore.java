package org.hisp.dhis.user.hibernate;

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


import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserStore;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * @author Nguyen Hong Duc
 */
public class HibernateUserStore
    extends HibernateIdentifiableObjectStore<User>
    implements UserStore
{
    // -------------------------------------------------------------------------
    // UserStore implementation
    // -------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public List<User> getAllOrderedName( int first, int max )
    {
        Criteria criteria = getCriteria();
        criteria.addOrder( Order.asc( "surname" ) ).addOrder( Order.asc( "firstName" ) );
        criteria.setFirstResult( first );
        criteria.setMaxResults( max );
        return criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<User> getAllLikeNameOrderedName( String name, int first, int max )
    {
        Criteria criteria = getCriteria();
        criteria.add( Restrictions.or( Restrictions.ilike( "surname", "%" + name + "%" ),
            Restrictions.ilike( "firstName", "%" + name + "%" ) ) );
        criteria.addOrder( Order.asc( "surname" ) ).addOrder( Order.asc( "firstName" ) );
        criteria.setFirstResult( first );
        criteria.setMaxResults( max );
        return criteria.list();
    }

    public List<User> getUsersWithoutOrganisationUnit()
    {
        List<User> users = getAll();

        Iterator<User> iterator = users.iterator();

        while ( iterator.hasNext() )
        {
            if ( iterator.next().getOrganisationUnits().size() > 0 )
            {
                iterator.remove();
            }
        }

        return users;
    }

    @SuppressWarnings("unchecked")
    public List<User> getUsersByPhoneNumber( String phoneNumber )
    {
        String hql = "from User u where u.phoneNumber = :phoneNumber";

        Query query = sessionFactory.getCurrentSession().createQuery( hql );
        query.setString( "phoneNumber", phoneNumber );

        return query.list();
    }

    @SuppressWarnings("unchecked")
    public List<User> getUsersByOrganisationUnits( Collection<OrganisationUnit> orgunits )
    {
        String hql = "select distinct u from User u join u.organisationUnits o where o.id in (:ids)";

        return sessionFactory.getCurrentSession().createQuery( hql ).setParameterList( "ids", orgunits ).list();
    }

    public void removeUserSettings( User user )
    {
        String hql = "delete from UserSetting us where us.user = :user";

        getQuery( hql ).setEntity( "user", user ).executeUpdate();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<User> getUsersByName( String name )
    {
        Criteria criteria = getCriteria();
        criteria.add( Restrictions.or( Restrictions.ilike( "surname", "%" + name + "%" ),
            Restrictions.ilike( "firstName", "%" + name + "%" ) ) );
        criteria.addOrder( Order.asc( "surname" ) ).addOrder( Order.asc( "firstName" ) );

        return criteria.list();
    }
}
