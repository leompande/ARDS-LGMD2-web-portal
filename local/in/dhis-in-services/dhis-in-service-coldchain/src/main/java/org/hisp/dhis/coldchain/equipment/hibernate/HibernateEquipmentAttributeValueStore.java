package org.hisp.dhis.coldchain.equipment.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValue;
import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValueStore;
import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;

public class HibernateEquipmentAttributeValueStore implements EquipmentAttributeValueStore
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
    // EquipmentDetails
    // -------------------------------------------------------------------------
    
    @Override
    public void addEquipmentAttributeValue( EquipmentAttributeValue equipmentAttributeValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( equipmentAttributeValue );
    }

    @Override
    public void deleteEquipmentAttributeValue( EquipmentAttributeValue equipmentAttributeValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( equipmentAttributeValue );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<EquipmentAttributeValue> getAllEquipmentAttributeValues()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( EquipmentAttributeValue.class ).list();
    }

    @Override
    public void updateEquipmentAttributeValue( EquipmentAttributeValue equipmentAttributeValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( equipmentAttributeValue );
    }
    
    
    @SuppressWarnings( "unchecked" )
    public Collection<EquipmentAttributeValue> getEquipmentAttributeValues( Equipment equipment )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( EquipmentAttributeValue.class );
        
        criteria.add( Restrictions.eq( "equipment", equipment ) );
        
        return criteria.list();
    }

    public EquipmentAttributeValue getEquipmentAttributeValue( Equipment equipment, EquipmentTypeAttribute equipmentTypeAttribute )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( EquipmentAttributeValue.class );
        
        criteria.add( Restrictions.eq( "equipment", equipment ) );
        criteria.add( Restrictions.eq( "equipmentTypeAttribute", equipmentTypeAttribute ) );
        
        return (EquipmentAttributeValue) criteria.uniqueResult();
    }
    
}
