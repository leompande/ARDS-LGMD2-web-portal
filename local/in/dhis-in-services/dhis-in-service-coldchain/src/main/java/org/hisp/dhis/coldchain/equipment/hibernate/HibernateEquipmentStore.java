package org.hisp.dhis.coldchain.equipment.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValue;
import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentStore;
import org.hisp.dhis.coldchain.equipment.EquipmentType;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class HibernateEquipmentStore 
    extends HibernateGenericStore<Equipment>
    implements EquipmentStore
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
    // Equipment
    // -------------------------------------------------------------------------

    /*
    public int addEquipment( Equipment equipment )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( equipment );
    }

    
    public void deleteEquipment( Equipment equipment )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( equipment );
    }

    
    public Collection<Equipment> getAllEquipment()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( Equipment.class ).list();
    }

    
    public void updateEquipment( Equipment equipment )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( equipment );
    }

    */
    
    @SuppressWarnings( "unchecked" )
    public Collection<Equipment> getEquipments( OrganisationUnit orgUnit )
    {
        /*
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( EquipmentTypeAttribute.class );
        criteria.add( Restrictions.eq( "organisationUnit", orgUnit ) );

        return criteria.list();
        */
        
        return getCriteria( Restrictions.eq( "organisationUnit", orgUnit ) ).list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<Equipment> getEquipments( OrganisationUnit orgUnit, EquipmentType equipmentType )
    {
        /*
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( EquipmentTypeAttribute.class );
        criteria.add( Restrictions.eq( "organisationUnit", orgUnit ) );
        criteria.add( Restrictions.eq( "equipmentType", equipmentType ) );

        return criteria.list();
        */
        
        Criteria crit = getCriteria();
        Conjunction con = Restrictions.conjunction();

        con.add( Restrictions.eq( "organisationUnit", orgUnit ) );
        con.add( Restrictions.eq( "equipmentType", equipmentType ) );

        crit.add( con );
        
       // Restrictions.in( "organisationUnit", values );
        return crit.list();
    }
    
   // public int getCountEquipment( OrganisationUnit orgUnit, EquipmentType equipmentType )
    
    public int getCountEquipment( List<OrganisationUnit> orgUnitList, EquipmentType equipmentType )
    {
        Number rs = 0;
        
        //System.out.println(" Size of orgUnitList is : " + orgUnitList.size() + " -- " + equipmentType.getId() + equipmentType.getName() );
       
        if ( orgUnitList != null && orgUnitList.size() != 0 )
        {
            rs = (Number) getCriteria( Restrictions.in( "organisationUnit", orgUnitList ) ).add( Restrictions.eq( "equipmentType", equipmentType ) ).setProjection(
                Projections.rowCount() ).uniqueResult();
        }
        
        /*
        Number rs = (Number) getCriteria( Restrictions.in( "organisationUnit", orgUnitList ) ).add( Restrictions.eq( "equipmentType", equipmentType ) ).setProjection(
            Projections.rowCount() ).uniqueResult();
        */
        
        //System.out.println(" RS is : " + rs );
        
        return rs != null ? rs.intValue() : 0;
    }

    @SuppressWarnings( "unchecked" )
    //public Collection<Equipment> getEquipments( OrganisationUnit orgUnit, EquipmentType equipmentType, int min, int max )
    public Collection<Equipment> getEquipments( List<OrganisationUnit> orgUnitList, EquipmentType equipmentType, int min, int max )
    {
        List<Equipment> equipmentList  = new ArrayList<Equipment>();
        
        if ( orgUnitList != null && orgUnitList.size() != 0 )
        {
            return getCriteria( Restrictions.in( "organisationUnit", orgUnitList ) ).add( Restrictions.eq( "equipmentType", equipmentType ) ).setFirstResult( min ).setMaxResults( max ).list();
        }
        
        else
        {
            return equipmentList;
        }
        
        //return getCriteria( Restrictions.in( "organisationUnit", orgUnitList ) ).add( Restrictions.eq( "equipmentType", equipmentType ) ).setFirstResult( min ).setMaxResults( max ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Equipment> getEquipmentList( OrganisationUnit organisationUnit, EquipmentType equipmentType )
    {
        List<Equipment> equipmentList  = new ArrayList<Equipment>();
        
        if ( organisationUnit != null )
        {
            return getCriteria( Restrictions.eq( "organisationUnit", organisationUnit ) ).add( Restrictions.eq( "equipmentType", equipmentType ) ).list();
        }
        
        else
        {
            return equipmentList;
        }
        
    }    
    
    
    //public int getCountEquipment( OrganisationUnit orgUnit, EquipmentType equipmentType, EquipmentTypeAttribute equipmentTypeAttribute, String searchText )
    public int getCountEquipment( String orgUnitIdsByComma, EquipmentType equipmentType, EquipmentTypeAttribute equipmentTypeAttribute, String searchText, String searchBy )
    {
        /*
        String hql = "SELECT COUNT( DISTINCT ei ) FROM Equipment AS ei  " +
                        " WHERE ei IN ( SELECT ed.equipment FROM EquipmentAttributeValue AS ed WHERE ed.equipmentTypeAttribute.id = "+ equipmentTypeAttribute.getId()+" AND ed.value LIKE '%" + searchText + "%' ) " +
                        " AND ei.organisationUnit.id IN (" + orgUnitIdsByComma  + " ) " +
                        " AND ei.equipmentType.id = " + equipmentType.getId();

       */ 
       
        String hql = "";
        
        if( searchBy.equalsIgnoreCase( EquipmentAttributeValue.PREFIX_MODEL_NAME ) )
        {
            hql = "SELECT COUNT( DISTINCT ei ) FROM Equipment AS ei, Model AS cat" +
            " WHERE ei.organisationUnit.id IN (" + orgUnitIdsByComma  + " ) " +
            " AND ei.equipmentType.id = " + equipmentType.getId() + 
            "AND cat.name like '%" + searchText + "%' AND cat.id = ei.model.id " ;
        }
        
        else if ( searchBy.equalsIgnoreCase( EquipmentAttributeValue.PREFIX_ORGANISATIONUNIT_NAME ))
        {
            hql = "SELECT COUNT( DISTINCT ei ) FROM Equipment AS ei, OrganisationUnit AS orgUnit" +
            " WHERE ei.organisationUnit.id IN (" + orgUnitIdsByComma  + " ) " +
            " AND ei.equipmentType.id = " + equipmentType.getId() + 
            "AND orgUnit.name like '%" + searchText + "%' AND orgUnit.id = ei.organisationUnit.id " ;
        }
        
        else
        {
            hql = "SELECT COUNT( DISTINCT ei ) FROM Equipment AS ei  " +
            " WHERE ei IN ( SELECT ed.equipment FROM EquipmentAttributeValue AS ed WHERE ed.equipmentTypeAttribute.id = "+ equipmentTypeAttribute.getId()+" AND ed.value LIKE '%" + searchText + "%' ) " +
            " AND ei.organisationUnit.id IN (" + orgUnitIdsByComma  + " ) " +
            " AND ei.equipmentType.id = " + equipmentType.getId();
        }
        
        
        Query query = getQuery( hql );

        Number rs = (Number) query.uniqueResult();

        return (rs != null) ? rs.intValue() : 0;
    }

    @SuppressWarnings( "unchecked" )
   // public Collection<Equipment> getEquipments( OrganisationUnit orgUnit, EquipmentType equipmentType, EquipmentTypeAttribute equipmentTypeAttribute, String searchText, int min, int max )
    public Collection<Equipment> getEquipments( String orgUnitIdsByComma, EquipmentType equipmentType, EquipmentTypeAttribute equipmentTypeAttribute, String searchText, String searchBy, int min, int max )
    {
        /*
        String hql = "SELECT DISTINCT ei FROM Equipment AS ei  " +
                        " WHERE ei IN ( SELECT ed.equipment FROM EquipmentAttributeValue AS ed WHERE ed.equipmentTypeAttribute.id = "+ equipmentTypeAttribute.getId()+" AND ed.value like '%" + searchText + "%' ) " +
                        " AND ei.organisationUnit.id IN (" + orgUnitIdsByComma  + " ) " +
                        " AND ei.equipmentType.id = " + equipmentType.getId();

        */
        
        String hql = "";
        
        //if( EquipmentAttributeValue.PREFIX_MODEL_NAME.equalsIgnoreCase( "modelname" ))
        if( searchBy.equalsIgnoreCase( EquipmentAttributeValue.PREFIX_MODEL_NAME ))
        {
            hql = "SELECT DISTINCT ei FROM Equipment AS ei, Model AS cat" +
            " WHERE ei.organisationUnit.id IN (" + orgUnitIdsByComma  + " ) " +
            " AND ei.equipmentType.id = " + equipmentType.getId() + 
            "AND cat.name like '%" + searchText + "%' AND cat.id = ei.model.id " ;
        }
        
        else if ( searchBy.equalsIgnoreCase( EquipmentAttributeValue.PREFIX_ORGANISATIONUNIT_NAME))
        {
            hql = "SELECT DISTINCT ei FROM Equipment AS ei, OrganisationUnit AS orgUnit" +
            " WHERE ei.organisationUnit.id IN (" + orgUnitIdsByComma  + " ) " +
            " AND ei.equipmentType.id = " + equipmentType.getId() + 
            "AND orgUnit.name like '%" + searchText + "%' AND orgUnit.id = ei.organisationUnit.id " ;
        }
            
        else
        {
            hql = "SELECT DISTINCT ei FROM Equipment AS ei  " +
            " WHERE ei IN ( SELECT ed.equipment FROM EquipmentAttributeValue AS ed WHERE ed.equipmentTypeAttribute.id = "+ equipmentTypeAttribute.getId()+" AND ed.value like '%" + searchText + "%' ) " +
            " AND ei.organisationUnit.id IN (" + orgUnitIdsByComma  + " ) " +
            " AND ei.equipmentType.id = " + equipmentType.getId();
        }
            
            
         /*
        String hql1 = "SELECT DISTINCT ei FROM Equipment AS ei, Model AS cat" +
        " WHERE ei.organisationUnit.id IN (" + orgUnitIdsByComma  + " ) " +
        " AND ei.equipmentType.id = " + equipmentType.getId() + 
        "AND cat.name like '%" + searchText + "%' AND cat.id = ei.model.id " ;
        */
        
        /*
        String hql1 = "SELECT DISTINCT ei FROM Equipment AS ei, organisationunit AS orgUnit" +
        " WHERE ei.organisationUnit.id IN (" + orgUnitIdsByComma  + " ) " +
        " AND ei.equipmentType.id = " + equipmentType.getId() + 
        "AND orgUnit.name like '%" + searchText + "%' AND orgUnit.id = ei.organisationUnit.id " ;
        */
        
        //select EI.*, org.name from equipment as EI ,organisationunit as org where org.name like '%district%' and EI.organisationunitid = org.organisationunitid;
        
        
        //select EI.*, CAT.name from equipment as EI ,model as CAT where CAT.name like '%VC%' and EI.modelid = CAT.modelid;
        
        Query query = getQuery( hql ).setFirstResult( min ).setMaxResults( max );

        return query.list();
        
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<OrganisationUnit> searchOrgUnitListByName( String searchText )
    {
        String hql = "SELECT orgUnit FROM OrganisationUnit AS orgUnit WHERE orgUnit.name like '%" + searchText + "%'";
        
        Query query = getQuery( hql );
        
        return query.list();

        /*
        Criteria criteria = getCriteria();
        
        criteria.add(Restrictions.like( "OrganisationUnit.name", "%" + searchText + "%"));
        
        return criteria.list();
        */
        
    }
    
    
    @SuppressWarnings( "unchecked" )
    public Collection<OrganisationUnit> searchOrgUnitListByCode( String searchText )
    {
        String hql = "SELECT orgUnit FROM OrganisationUnit AS orgUnit WHERE orgUnit.code like '%" + searchText + "%'";
        
        Query query = getQuery( hql );
        
        return query.list();

        /*
        Criteria criteria = getCriteria();
        
        criteria.add(Restrictions.like( "OrganisationUnit.name", "%" + searchText + "%"));
        
        return criteria.list();
        */
        
    }
    
    
    
    
    // for orgUnit list according to orGUnit Attribute values for paging purpose
    public int countOrgUnitByAttributeValue( Collection<Integer> orgunitIds, Attribute attribute, String searchText )
    {
        Criteria criteria = getCriteria( Restrictions.eq( "organisationUnitAttribute", true ));
        
        criteria.createAlias( "attributeValues", "attributeValue");
        criteria.add(Restrictions.eq( "attributeValue.attribute", attribute));
        criteria.add(Restrictions.like( "attributeValue.value", "%" + searchText + "%"));
        /*
        criteria.add(Restrictions.eq( "attributeValues.attribute", attribute));
        criteria.add(Restrictions.eq( "attributeValues.attribute", attribute));
        */
        criteria.add(Restrictions.in( "id",orgunitIds));
        
        Number rs = (Number) criteria.uniqueResult();

        return (rs != null) ? rs.intValue() : 0;
    }
    
    // for orgUnit list according to orGUnit Attribute values for paging purpose
    @SuppressWarnings( "unchecked" )
    public Collection<OrganisationUnit> searchOrgUnitByAttributeValue( Collection<Integer> orgunitIds, Attribute attribute, String searchText, Integer min, Integer max )
    {
        Criteria criteria = getCriteria( Restrictions.eq( "organisationUnitAttribute", true ));
        criteria.createAlias( "attributeValues", "attributeValue");
        criteria.add(Restrictions.eq( "attributeValue.attribute", attribute));
        criteria.add(Restrictions.like( "attributeValue.value", "%" + searchText + "%"));
        criteria.add(Restrictions.in( "id",orgunitIds));
        
        criteria.setFirstResult( min ).setMaxResults( max );

        return criteria.list();
    }
 
    
    
    
    
}
