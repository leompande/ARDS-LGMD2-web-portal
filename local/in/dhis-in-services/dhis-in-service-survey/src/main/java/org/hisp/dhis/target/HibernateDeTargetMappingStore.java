package org.hisp.dhis.target;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;

public class HibernateDeTargetMappingStore
    implements DeTargetMappingStore
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
    // DeTargetMapping
    // -------------------------------------------------------------------------

    public void addDeTargetMapping( DeTargetMapping deTargetMapping )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( deTargetMapping );
    }

    public void deleteDeTargetMapping( DeTargetMapping deTargetMapping )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( deTargetMapping );
    }

    public void updateDeTargetMapping( DeTargetMapping deTargetMapping )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( deTargetMapping );
    }

    public DeTargetMapping getDeTargetMapping( int deid )
    {
        Session session = sessionFactory.getCurrentSession();

        return (DeTargetMapping) session.get( DeTargetMapping.class, deid );
    }

    public DeTargetMapping getDeTargetMapping( DataElement dataElement, DataElementCategoryOptionCombo optionCombo )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DeTargetMapping.class );
        criteria.add( Restrictions.eq( "dataelement", dataElement ) );
        criteria.add( Restrictions.eq( "dataelementoptioncombo", optionCombo ) );

        return (DeTargetMapping) criteria.uniqueResult();
    }

}
