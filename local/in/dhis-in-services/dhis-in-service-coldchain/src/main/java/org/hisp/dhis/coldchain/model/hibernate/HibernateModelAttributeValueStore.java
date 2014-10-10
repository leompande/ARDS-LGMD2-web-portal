package org.hisp.dhis.coldchain.model.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.coldchain.model.Model;
import org.hisp.dhis.coldchain.model.ModelAttributeValue;
import org.hisp.dhis.coldchain.model.ModelAttributeValueStore;
import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeOption;

public class HibernateModelAttributeValueStore implements ModelAttributeValueStore
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
    // ModelAttributeValue
    // -------------------------------------------------------------------------

    @Override
    public void addModelAttributeValue( ModelAttributeValue modelAttributeValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( modelAttributeValue );
    }

    @Override
    public void deleteModelAttributeValue( ModelAttributeValue modelAttributeValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( modelAttributeValue );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<ModelAttributeValue> getAllModelAttributeValues()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( ModelAttributeValue.class ).list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<ModelAttributeValue> getAllModelAttributeValuesByModel( Model model )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( ModelAttributeValue.class );
        
        criteria.add( Restrictions.eq( "model", model ) );
        return criteria.list();
    }
    
    @Override
    public void updateModelAttributeValue( ModelAttributeValue modelAttributeValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( modelAttributeValue );
    }
    
    @Override
    public ModelAttributeValue modelAttributeValue( Model model ,ModelTypeAttribute modelTypeAttribute )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ModelAttributeValue.class );
        criteria.add( Restrictions.eq( "model", model ) );
        criteria.add( Restrictions.eq( "modelTypeAttribute", modelTypeAttribute ) );

        return (ModelAttributeValue) criteria.uniqueResult();
    }
    
    @Override
    public ModelAttributeValue modelAttributeValue( Model model ,ModelTypeAttribute modelTypeAttribute, ModelTypeAttributeOption modelTypeAttributeOption )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ModelAttributeValue.class );
        criteria.add( Restrictions.eq( "model", model ) );
        criteria.add( Restrictions.eq( "modelTypeAttribute", modelTypeAttribute ) );
        criteria.add( Restrictions.eq( "modelTypeAttributeOption", modelTypeAttributeOption ) );

        return (ModelAttributeValue) criteria.uniqueResult();
    }

	@Override
	public Collection<ModelAttributeValue> getAllModelAttributeValuesByModelTypeAttribute(
			ModelTypeAttribute modelTypeAttribute) 
	{
		
		Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( ModelAttributeValue.class );
        
        criteria.add( Restrictions.eq( "modelTypeAttribute", modelTypeAttribute ) );
        return criteria.list();
	}
}
