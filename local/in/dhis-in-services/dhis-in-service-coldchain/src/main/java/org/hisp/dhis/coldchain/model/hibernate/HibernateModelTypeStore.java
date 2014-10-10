package org.hisp.dhis.coldchain.model.hibernate;

import java.util.Collection;

import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeStore;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;


//public class HibernateModelTypeStore implements ModelTypeStore
public class HibernateModelTypeStore  extends HibernateIdentifiableObjectStore<ModelType> implements ModelTypeStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    /*
    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }
    */
    // -------------------------------------------------------------------------
    // ModelType
    // -------------------------------------------------------------------------
    /*
    @Override
    public int addModelType( ModelType modelType )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( modelType );
    }

    @Override
    public void deleteModelType( ModelType modelType )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( modelType );        
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<ModelType> getAllModelTypes()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( ModelType.class ).list();
    }

    @Override
    public void updateModelType( ModelType modelType )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( modelType );        
    }
    
    @Override
    public ModelType getModelType( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (ModelType) session.get( ModelType.class, id );
    }
    
    @Override
    public ModelType getModelTypeByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ModelType.class );
        criteria.add( Restrictions.eq( "name", name ) );

        return (ModelType) criteria.uniqueResult();
    }
    */
    
    /*
    @Override
    public ModelType getModelTypeByAttribute( ModelType modelType, ModelTypeAttribute modelTypeAttribute)
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ModelType.class );
        criteria.add( Restrictions.eq( "name", modelTypeAttribute ) );

        return (ModelType) criteria.uniqueResult();
    }
    */
    
    // -------------------------------------------------------------------------
    // ModelType
    // -------------------------------------------------------------------------
   
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<ModelType> getAllModelTypes()
    {
        return sessionFactory.getCurrentSession().createCriteria( ModelType.class ).list();
    }
    
    @Override
    public ModelType getModelType( int id )
    {
        return (ModelType) sessionFactory.getCurrentSession().get( ModelType.class, id );
    }
    
    @Override
    public ModelType getModelTypeByName( String name )
    {
        return (ModelType) getCriteria( Restrictions.eq( "name", name ) ).uniqueResult();
    }
    
    /*
    @Override
    public ModelType getModelTypeByAttribute( ModelType modelType, ModelTypeAttribute modelTypeAttribute)
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ModelType.class );
        criteria.add( Restrictions.eq( "name", modelTypeAttribute ) );

        return (ModelType) criteria.uniqueResult();

    }
    */
    
   /*
    public ModelTypeAttribute getModelTypeAttributeForDisplay( ModelType modelType, ModelTypeAttribute modelTypeAttribute, boolean display )
    {
        
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ModelTypeAttribute.class );
        criteria.add( Restrictions.eq( "modelType", modelType ) );
        criteria.add( Restrictions.eq( "modelTypeAttribute", modelTypeAttribute ) );
        criteria.add( Restrictions.eq( "display", display ) );
        
        return (ModelTypeAttribute) criteria.uniqueResult();
        
        //return (EquipmentType_Attribute) getCriteria( Restrictions.eq( "equipmentType", equipmentType ), Restrictions.eq( "equipmentTypeAttribute", equipmentTypeAttribute ), Restrictions.eq( "display", display ) ).uniqueResult();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<ModelTypeAttribute> getAllModelTypeAttributeForDisplay( ModelType modelType, boolean display )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( ModelType.class );
        //criteria.setProjection( Projections.property( "modelType" ) );
        criteria.add( Restrictions.eq( "modelType", modelType ) );
        criteria.add( Restrictions.eq( "display", display ) );
        return criteria.list();
        
        //return getCriteria( Restrictions.eq( "equipmentType", equipmentType ), Restrictions.eq( "display", display ) ).list();
    }
    */
    
    
}
