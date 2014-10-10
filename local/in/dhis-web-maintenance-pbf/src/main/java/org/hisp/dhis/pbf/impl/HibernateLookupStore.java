package org.hisp.dhis.pbf.impl;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.pbf.api.Lookup;
import org.hisp.dhis.pbf.api.LookupStore;

public class HibernateLookupStore extends HibernateIdentifiableObjectStore<Lookup> implements LookupStore
{

    public Collection<Lookup> getAllLookupsByType( String type )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Lookup.class );
        criteria.add( Restrictions.eq( "type", type ) );

        return criteria.list();
    }
}
