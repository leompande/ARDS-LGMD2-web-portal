package org.hisp.dhis.coldchain.equipment.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.coldchain.equipment.EquipmentType;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentType_Attribute;
import org.hisp.dhis.coldchain.equipment.EquipmentType_AttributeStore;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version HibernateEquipmentType_AttributeStore.java Jun 14, 2012 2:50:04 PM	
 */

//public class HibernateEquipmentType_AttributeStore extends HibernateGenericStore<EquipmentType_Attribute> implements EquipmentType_AttributeStore
public class HibernateEquipmentType_AttributeStore  implements EquipmentType_AttributeStore
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
    // EquipmentType_Attribute
    // -------------------------------------------------------------------------
    
    //add
    
    @Override
    public void addEquipmentType_Attribute( EquipmentType_Attribute equipmentType_Attribute )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( equipmentType_Attribute );
    }
    
    //update
    @Override
    public void updateEquipmentType_Attribute( EquipmentType_Attribute equipmentType_Attribute )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( equipmentType_Attribute );
    }
    
    //delete
    @Override
    public void deleteEquipmentType_Attribute( EquipmentType_Attribute equipmentType_Attribute )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( equipmentType_Attribute );
    }
    
    // get all
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<EquipmentType_Attribute> getAllEquipmentTypeAttributes()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( EquipmentType_Attribute.class ).list();
    }

    @Override
    public EquipmentType_Attribute getEquipmentTypeAttribute( EquipmentType equipmentType, EquipmentTypeAttribute equipmentTypeAttribute )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( EquipmentType_Attribute.class );
        criteria.add( Restrictions.eq( "equipmentType", equipmentType ) );
        criteria.add( Restrictions.eq( "equipmentTypeAttribute", equipmentTypeAttribute ) );

        return (EquipmentType_Attribute) criteria.uniqueResult();
        
        //return (EquipmentType_Attribute) getCriteria( Restrictions.eq( "equipmentType", equipmentType ),Restrictions.eq( "equipmentTypeAttribute", equipmentTypeAttribute ) ).uniqueResult();
    }
    
    @Override
    public EquipmentType_Attribute getEquipmentTypeAttributeForDisplay( EquipmentType equipmentType, EquipmentTypeAttribute equipmentTypeAttribute, boolean display )
    {
        
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( EquipmentType_Attribute.class );
        criteria.add( Restrictions.eq( "equipmentType", equipmentType ) );
        criteria.add( Restrictions.eq( "equipmentTypeAttribute", equipmentTypeAttribute ) );
        criteria.add( Restrictions.eq( "display", display ) );
        
        return (EquipmentType_Attribute) criteria.uniqueResult();
        
        //return (EquipmentType_Attribute) getCriteria( Restrictions.eq( "equipmentType", equipmentType ), Restrictions.eq( "equipmentTypeAttribute", equipmentTypeAttribute ), Restrictions.eq( "display", display ) ).uniqueResult();
    }
    
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<EquipmentType_Attribute> getAllEquipmentTypeAttributesByEquipmentType( EquipmentType equipmentType )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( EquipmentType_Attribute.class );
        criteria.add( Restrictions.eq( "equipmentType", equipmentType ) );
        return criteria.list();
        
        //return getCriteria( Restrictions.eq( "equipmentType", equipmentType ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<EquipmentType_Attribute> getAllEquipmentTypeAttributeForDisplay( EquipmentType equipmentType, boolean display )
    {
        Session session = sessionFactory.getCurrentSession();
        
        
        Criteria criteria = session.createCriteria( EquipmentType_Attribute.class );
        criteria.add( Restrictions.eq( "equipmentType", equipmentType ) );
        criteria.add( Restrictions.eq( "display", display ) );
        return criteria.list();
        
        //return getCriteria( Restrictions.eq( "equipmentType", equipmentType ), Restrictions.eq( "display", display ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<EquipmentTypeAttribute> getListEquipmentTypeAttribute( EquipmentType equipmentType )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( EquipmentType_Attribute.class );
        
        criteria.add( Restrictions.eq( "equipmentType", equipmentType ) );
        criteria.setProjection( Projections.property( "equipmentTypeAttribute" ) );
        return criteria.list();
        
        
        
        /*
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria( getClazz() );
        criteria.add( Restrictions.eq( "equipmentType", equipmentType ) );
        criteria.setProjection( Projections.property( "equipmentTypeAttribute" ) );
        return criteria.list();
        */
    }
    
    
 
    
    
    
    
}


