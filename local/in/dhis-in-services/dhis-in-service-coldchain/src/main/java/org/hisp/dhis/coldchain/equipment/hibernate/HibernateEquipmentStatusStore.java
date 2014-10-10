package org.hisp.dhis.coldchain.equipment.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentStatus;
import org.hisp.dhis.coldchain.equipment.EquipmentStatusStore;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class HibernateEquipmentStatusStore 
    extends HibernateGenericStore<EquipmentStatus>
    implements EquipmentStatusStore
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
    // EquipmentWorkingStatus
    // -------------------------------------------------------------------------

    /*
    @Override
    public int addEquipmentStatus( EquipmentStatus equipmentStatus )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( equipmentStatus );
    }

    @Override
    public void deleteEquipmentStatus( EquipmentStatus equipmentStatus )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( equipmentStatus );
    }

    @Override
    public Collection<EquipmentStatus> getAllEquipmentStatus()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( EquipmentStatus.class ).list();
    }

    @Override
    public void updateEquipmentStatus( EquipmentStatus equipmentStatus )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( equipmentStatus );
    }
    */
    
    @SuppressWarnings( "unchecked" )
    public Collection<EquipmentStatus> getEquipmentStatusHistory( Equipment equipment )
    {
        return getCriteria( Restrictions.eq( "equipment", equipment ) ).list();
        
        //return getCriteria( Restrictions.eq( "equipment", equipment ), Restrictions. ).list();
        
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<EquipmentStatus> getEquipmentStatusHistoryDescOrder( Equipment equipment )
    {
        //return getCriteria( Restrictions.eq( "equipment", equipment ), ).list();
        
        Criteria criteria = getCriteria();
        
        criteria.add( Restrictions.eq( "equipment", equipment ) );
        criteria.addOrder(Order.desc( "reportingDate" ));
        criteria.addOrder(Order.desc( "updationDate" ));
        return criteria.list();
        
    }
}
