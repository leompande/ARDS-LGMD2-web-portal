package org.hisp.dhis.coldchain.equipment.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeOption;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeOptionStore;

public class HibernateEquipmentTypeAttributeOptionStore implements EquipmentTypeAttributeOptionStore
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
    // EquipmentTypeAttributeOption
    // -------------------------------------------------------------------------

    @Override
    public int addEquipmentTypeAttributeOption( EquipmentTypeAttributeOption equipmentTypeAttributeOption )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( equipmentTypeAttributeOption );
    }

    @Override
    public void deleteEquipmentTypeAttributeOption( EquipmentTypeAttributeOption equipmentTypeAttributeOption )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( equipmentTypeAttributeOption );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<EquipmentTypeAttributeOption> getAllEquipmentTypeAttributeOptions()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( EquipmentTypeAttributeOption.class ).list();
    }

    @Override
    public void updateEquipmentTypeAttributeOption( EquipmentTypeAttributeOption equipmentTypeAttributeOption )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( equipmentTypeAttributeOption );
    }
    
    public EquipmentTypeAttributeOption getEquipmentTypeAttributeOption( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (EquipmentTypeAttributeOption) session.get( EquipmentTypeAttributeOption.class, id );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<EquipmentTypeAttributeOption> get( EquipmentTypeAttribute equipmentTypeAttribute )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( EquipmentTypeAttributeOption.class );
        criteria.add( Restrictions.eq( "equipmentTypeAttribute", equipmentTypeAttribute ) );

        return criteria.list();
    }

    public EquipmentTypeAttributeOption get( EquipmentTypeAttribute equipmentTypeAttribute, String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( EquipmentTypeAttributeOption.class );
        criteria.add( Restrictions.eq( "equipmentTypeAttribute", equipmentTypeAttribute ) );
        criteria.add( Restrictions.eq( "name", name ) );

        return (EquipmentTypeAttributeOption) criteria.uniqueResult();
    }
}
