package org.hisp.dhis.coldchain.equipment.hibernate;

import java.util.Collection;
import java.util.Collections;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.coldchain.equipment.EquipmentDataValue;
import org.hisp.dhis.coldchain.equipment.EquipmentDataValueStore;
import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodStore;

public class HibernateEquipmentDataValueStore implements EquipmentDataValueStore
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    private PeriodStore periodStore;

    public void setPeriodStore( PeriodStore periodStore )
    {
        this.periodStore = periodStore;
    }

    // -------------------------------------------------------------------------
    // EquipmentDataValue
    // -------------------------------------------------------------------------

    public void addEquipmentDataValue( EquipmentDataValue equipmentDataValue )
    {
        equipmentDataValue.setPeriod( periodStore.reloadForceAddPeriod( equipmentDataValue.getPeriod() ) );

        Session session = sessionFactory.getCurrentSession();

        session.save( equipmentDataValue );
    }

    public void updateEquipmentDataValue( EquipmentDataValue equipmentDataValue )
    {
        equipmentDataValue.setPeriod( periodStore.reloadForceAddPeriod( equipmentDataValue.getPeriod() ) );

        Session session = sessionFactory.getCurrentSession();

        session.update( equipmentDataValue );
    }

    public void deleteEquipmentDataValue( EquipmentDataValue equipmentDataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( equipmentDataValue );
    }
    @SuppressWarnings( "unchecked" )
    public Collection<EquipmentDataValue> getEquipmentDataValues( Equipment equipment, Period period, Collection<DataElement> dataElements )
    {
        Period storedPeriod = periodStore.reloadPeriod( period );

        if ( storedPeriod == null || dataElements == null || dataElements.isEmpty() )
        {
            return Collections.emptySet();
        }

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( EquipmentDataValue.class );
        criteria.add( Restrictions.eq( "equipment", equipment ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.add( Restrictions.in( "dataElement", dataElements ) );

        return criteria.list();
    }
    
    public EquipmentDataValue getEquipmentDataValue( Equipment equipment, Period period, DataElement dataElement )
    {
        Period storedPeriod = periodStore.reloadPeriod( period );

        if ( storedPeriod == null || dataElement == null )
        {
            return null;
        }

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( EquipmentDataValue.class );
        criteria.add( Restrictions.eq( "equipment", equipment ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );

        return (EquipmentDataValue) criteria.uniqueResult();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<EquipmentDataValue> getAllEquipmentDataValuesByEquipment( Equipment equipment )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( EquipmentDataValue.class );
        criteria.add( Restrictions.eq( "equipment", equipment ) );
        return criteria.list();
    }
    
}
