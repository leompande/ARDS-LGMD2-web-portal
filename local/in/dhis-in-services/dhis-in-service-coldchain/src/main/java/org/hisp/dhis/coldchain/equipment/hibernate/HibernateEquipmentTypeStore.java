package org.hisp.dhis.coldchain.equipment.hibernate;

import java.util.Collection;

import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.coldchain.equipment.EquipmentType;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeStore;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
//public class HibernateEquipmentTypeStore implements EquipmentTypeStore
public class HibernateEquipmentTypeStore extends HibernateIdentifiableObjectStore<EquipmentType> implements EquipmentTypeStore
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
    // EquipmentTypeStore
    // -------------------------------------------------------------------------
    /*
    @Override
    public int addEquipmentType( EquipmentType equipmentType )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( equipmentType );
    }

    @Override
    public void deleteEquipmentType( EquipmentType equipmentType )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( equipmentType );
    }

    @Override
    public Collection<EquipmentType> getAllEquipmentTypes()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( EquipmentType.class ).list();
    }

    @Override
    public void updateEquipmentType( EquipmentType equipmentType )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( equipmentType );
    }

    public EquipmentType getEquipmentTypeByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( EquipmentType.class );
        criteria.add( Restrictions.eq( "name", name ) );

        return (EquipmentType) criteria.uniqueResult();
    }

    public EquipmentType getEquipmentType( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (EquipmentType) session.get( EquipmentType.class, id );
    }
    */
    
    // -------------------------------------------------------------------------
    // EquipmentTypeStore
    // -------------------------------------------------------------------------
    
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<EquipmentType> getAllEquipmentTypes()
    {
        return sessionFactory.getCurrentSession().createCriteria( EquipmentType.class ).list();
    }
    @Override
    public EquipmentType getEquipmentTypeByName( String name )
    {
        return (EquipmentType) getCriteria( Restrictions.eq( "name", name ) ).uniqueResult();
    }
    @Override
    public EquipmentType getEquipmentType( int id )
    {
        return (EquipmentType) sessionFactory.getCurrentSession().get( EquipmentType.class, id );
    }
    /*
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<EquipmentTypeAttribute> getAllEquipmentTypeAttributesForDisplay( EquipmentType equipmentType )
    {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria( EquipmentTypeAttribute.class );
        criteria.setProjection( Projections.property( "display" ) );
        return criteria.list();
    }
    */
    
}
