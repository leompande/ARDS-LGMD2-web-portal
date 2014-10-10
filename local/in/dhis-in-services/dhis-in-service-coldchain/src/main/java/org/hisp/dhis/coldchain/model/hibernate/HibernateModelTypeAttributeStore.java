package org.hisp.dhis.coldchain.model.hibernate;

import java.util.Collection;

import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeStore;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;

//public class HibernateModelTypeAttributeStore implements ModelTypeAttributeStore
public class HibernateModelTypeAttributeStore extends HibernateIdentifiableObjectStore<ModelTypeAttribute> implements ModelTypeAttributeStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    /*
    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )o
    {
        this.sessionFactory = sessionFactory;
    }
    */
    // -------------------------------------------------------------------------
    // ModelTypeAttribute
    // -------------------------------------------------------------------------

    /*
    @Override
    public void addModelTypeAttribute( ModelTypeAttribute modelTypeAttribute )
    {
        
        sessionFactory.getCurrentSession().save( modelTypeAttribute );
       
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( modelTypeAttribute );
        
    }

    @Override
    public void deleteModelTypeAttribute( ModelTypeAttribute modelTypeAttribute )
    {
        
        sessionFactory.getCurrentSession().delete( modelTypeAttribute );
       
        Session session = sessionFactory.getCurrentSession();

        session.delete( modelTypeAttribute );
        
    }
    
    @Override
    public void updateModelTypeAttribute( ModelTypeAttribute modelTypeAttribute )
    {
        sessionFactory.getCurrentSession().update( modelTypeAttribute );
        
        Session session = sessionFactory.getCurrentSession();

        session.update( modelTypeAttribute );
             
    }
    */
    @Override
    public ModelTypeAttribute getModelTypeAttribute( int id )
    {
        return (ModelTypeAttribute) sessionFactory.getCurrentSession().get( ModelTypeAttribute.class, id );
        
        /*
        Session session = sessionFactory.getCurrentSession();

        return (ModelTypeAttribute) session.get( ModelTypeAttribute.class, id );
        */
    }

    @Override
    public ModelTypeAttribute getModelTypeAttributeByName( String name )
    {
        return (ModelTypeAttribute) getCriteria( Restrictions.eq( "name", name ) ).uniqueResult();
        
        /*
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ModelTypeAttribute.class );
        criteria.add( Restrictions.eq( "name", name ) );
        return (ModelTypeAttribute) criteria.uniqueResult();
        */

    }
    
    @Override
    public ModelTypeAttribute getModelTypeAttributeByDescription( String description )
    {
        return (ModelTypeAttribute) getCriteria( Restrictions.eq( "description", description ) ).uniqueResult();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<ModelTypeAttribute> getAllModelTypeAttributes()
    {
        return sessionFactory.getCurrentSession().createCriteria( ModelTypeAttribute.class ).list();
        /*
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( ModelTypeAttribute.class ).list();
        */
    }    
}
