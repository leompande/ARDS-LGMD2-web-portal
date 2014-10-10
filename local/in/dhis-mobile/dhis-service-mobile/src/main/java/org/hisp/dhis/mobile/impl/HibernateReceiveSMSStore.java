package org.hisp.dhis.mobile.impl;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hisp.dhis.mobile.api.ReceiveSMS;
import org.hisp.dhis.mobile.api.ReceiveSMSStore;

public class HibernateReceiveSMSStore implements ReceiveSMSStore
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }
 
    // -------------------------------------------------------------------------
    // SendSMS
    // -------------------------------------------------------------------------

    public void addReceiveSMS( ReceiveSMS receiveSMS )
    {
        Session session = sessionFactory.getCurrentSession();
        
        session.save( receiveSMS );
    }
    
    public void updateReceiveSMS( ReceiveSMS receiveSMS )
    {
        Session session = sessionFactory.getCurrentSession();
        
        session.save( receiveSMS );
    }
    
    public void deleteReceiveSMS( ReceiveSMS receiveSMS )
    {
        Session session = sessionFactory.getCurrentSession();
        
        session.delete( receiveSMS );
    }
    
    public Collection<ReceiveSMS> getReceiveSMS( int start, int end )
    {
        Session session = sessionFactory.getCurrentSession();
        
        return session.createQuery( "from ReceiveSMS" ).list().subList( start, end );
    }
    
    public Collection<ReceiveSMS> getAllReceiveSMS()
    {
        Session session = sessionFactory.getCurrentSession();
        
        return session.createQuery( "from ReceiveSMS" ).list();
    }
    
    @SuppressWarnings( "unchecked" )
    public long getRowCount()
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( ReceiveSMS.class );
        criteria.setProjection( Projections.rowCount() );
        Long count = (Long) criteria.uniqueResult();
        return count != null ? count.longValue() : (long) 0;
    }

}
