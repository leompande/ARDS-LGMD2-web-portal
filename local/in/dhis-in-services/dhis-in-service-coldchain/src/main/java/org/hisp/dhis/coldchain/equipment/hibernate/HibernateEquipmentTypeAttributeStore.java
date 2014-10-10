package org.hisp.dhis.coldchain.equipment.hibernate;

import java.util.Collection;

import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeStore;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;

public class HibernateEquipmentTypeAttributeStore extends HibernateIdentifiableObjectStore<EquipmentTypeAttribute> implements EquipmentTypeAttributeStore
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
    // Dependencies
    // -------------------------------------------------------------------------
    /*
    @Override
    public int addEquipmentTypeAttribute( EquipmentTypeAttribute equipmentTypeAttribute )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( equipmentTypeAttribute );
    }

    @Override
    public void deleteEquipmentTypeAttribute( EquipmentTypeAttribute equipmentTypeAttribute )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( equipmentTypeAttribute );
    }

    @Override
    public Collection<EquipmentTypeAttribute> getAllEquipmentTypeAttributes()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( EquipmentTypeAttribute.class ).list();
    }

    @Override
    public void updateEquipmentTypeAttribute( EquipmentTypeAttribute equipmentTypeAttribute )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( equipmentTypeAttribute );
    }

    public EquipmentTypeAttribute getEquipmentTypeAttribute( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (EquipmentTypeAttribute) session.get( EquipmentTypeAttribute.class, id );
    }
    
    public EquipmentTypeAttribute getEquipmentTypeAttributeByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( EquipmentTypeAttribute.class );
        criteria.add( Restrictions.eq( "name", name ) );

        return (EquipmentTypeAttribute) criteria.uniqueResult();
    }
    */
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<EquipmentTypeAttribute> getAllEquipmentTypeAttributes()
    {
        return sessionFactory.getCurrentSession().createCriteria( EquipmentTypeAttribute.class ).list();        
    }

    public EquipmentTypeAttribute getEquipmentTypeAttribute( int id )
    {
        return (EquipmentTypeAttribute) sessionFactory.getCurrentSession().get( EquipmentTypeAttribute.class, id );
    }
    
    public EquipmentTypeAttribute getEquipmentTypeAttributeByName( String name )
    {
        return (EquipmentTypeAttribute) getCriteria( Restrictions.eq( "name", name ) ).uniqueResult();
    }

    public EquipmentTypeAttribute getEquipmentTypeAttributeByDescription( String description )
    {
        return (EquipmentTypeAttribute) getCriteria( Restrictions.eq( "description", description ) ).uniqueResult();
    }

    /*
    @SuppressWarnings( "unchecked" )
    public Collection<EquipmentTypeAttribute> getAllEquipmentTypeAttributesForDisplay( EquipmentTypeAttribute equipmentTypeAttribute )
    {
        //return sessionFactory.getCurrentSession().createCriteria( EquipmentTypeAttribute.class,equipmentTypeAttribute.isDisplay() ).list();
        
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria( EquipmentTypeAttribute.class );
        criteria.setProjection( Projections.property( "display" ) );
        return criteria.list();
    }
    */
    
}
