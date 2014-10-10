package org.hisp.dhis.coldchain.model.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeOption;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeOptionStore;

public class HibernateModelTypeAttributeOptionStore implements ModelTypeAttributeOptionStore
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
    // ModelTypeAttributeOption
    // -------------------------------------------------------------------------
    @Override
    public int addModelTypeAttributeOption( ModelTypeAttributeOption modelTypeAttributeOption )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( modelTypeAttributeOption );
    }

    @Override
    public void deleteModelTypeAttributeOption( ModelTypeAttributeOption modelTypeAttributeOption )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( modelTypeAttributeOption );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<ModelTypeAttributeOption> getAllModelTypeAttributeOptions()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( ModelTypeAttributeOption.class ).list();
    }

    @Override
    public void updateModelTypeAttributeOption( ModelTypeAttributeOption modelTypeAttributeOption )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( modelTypeAttributeOption );
    }
    @Override
    public ModelTypeAttributeOption getModelTypeAttributeOption( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (ModelTypeAttributeOption) session.get( ModelTypeAttributeOption.class, id );
    }
    
    public int countByModelTypeAttributeoption( ModelTypeAttributeOption modelTypeAttributeOption )
    {
        
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ModelTypeAttributeOption.class );
        Number rs = (Number)criteria.add( Restrictions.eq( "modelTypeAttributeOption", modelTypeAttributeOption ) ).setProjection( Projections.rowCount() ).uniqueResult();
        return rs != null ? rs.intValue() : 0;
        
        /*
        Number rs = (Number) getCriteria( Restrictions.eq( "modelTypeAttributeOption", modelTypeAttributeOption ) ).setProjection(
            Projections.rowCount() ).uniqueResult();
        return rs != null ? rs.intValue() : 0;
        */
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<ModelTypeAttributeOption> getModelTypeAttributeOptions( ModelTypeAttribute modelTypeAttribute )
    {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria( ModelTypeAttributeOption.class );
        return criteria.add( Restrictions.eq( "modelTypeAttribute", modelTypeAttribute ) ).list();
        //return getCriteria( Restrictions.eq( "modelTypeAttribute", modelTypeAttribute ) ).list();
    }
    
    public ModelTypeAttributeOption getModelTypeAttributeOptionName( ModelTypeAttribute modelTypeAttribute, String name )
    {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria( ModelTypeAttributeOption.class );
        
        criteria.add( Restrictions.eq( "name", name ) );
       
        criteria.add( Restrictions.eq( "modelTypeAttribute", modelTypeAttribute ) );

        return (ModelTypeAttributeOption) criteria.uniqueResult();
        
        /*
        return (ModelTypeAttributeOption) getCriteria( Restrictions.eq( "name", name ),
            Restrictions.eq( "modelTypeAttribute", modelTypeAttribute ) ).uniqueResult();
       */
    }
    
}
