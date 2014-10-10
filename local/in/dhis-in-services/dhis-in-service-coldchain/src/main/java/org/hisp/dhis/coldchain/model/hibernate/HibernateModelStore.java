package org.hisp.dhis.coldchain.model.hibernate;

import java.util.Collection;

import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.coldchain.model.Model;
import org.hisp.dhis.coldchain.model.ModelStore;
import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;

public class HibernateModelStore extends HibernateIdentifiableObjectStore<Model> implements ModelStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    
    // -------------------------------------------------------------------------
    // Model
    // -------------------------------------------------------------------------
    
    
    @Override
    public Model getModel( int id )
    {
        return (Model) sessionFactory.getCurrentSession().get( Model.class, id );
    }
    
    @Override
    public Model getModelByName( String name )
    {
        return (Model) getCriteria( Restrictions.eq( "name", name ) ).uniqueResult();
    }
    
    @Override
    public Model getModelByDescription( String description )
    {
        return (Model) getCriteria( Restrictions.eq( "description", description ) ).uniqueResult();
    }
    
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<Model> getAllModels()
    {
        return sessionFactory.getCurrentSession().createCriteria( Model.class ).list();
    }
    
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<Model> getModels( ModelType modelType )
    {
        return getCriteria( Restrictions.eq( "modelType", modelType ) ).list();
    }
      
    public int getCountModel( ModelType modelType )
    {
        Number rs = (Number) getCriteria(  Restrictions.eq( "modelType", modelType ) ).setProjection( Projections.rowCount() ).uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }   
    
    @SuppressWarnings( "unchecked" )
    public Collection<Model> getModels( ModelType modelType, int min, int max )
    {
        return getCriteria( Restrictions.eq( "modelType", modelType ) ).setFirstResult( min ).setMaxResults( max ).list();
    }
    
    
    
    
    //public int getCountModel( ModelType modelType, ModelTypeAttribute modelTypeAttribute, String searchText )
    public int getCountModel( ModelType modelType, ModelTypeAttribute modelTypeAttribute, String searchText, String searchBy )
    {
        /*
        String hql = "SELECT COUNT( DISTINCT cat ) FROM Model AS cat  " +
                        " WHERE cat IN ( SELECT catdata.model FROM ModelAttributeValue catdata  WHERE catdata.modelTypeAttribute.id = "+ modelTypeAttribute.getId()+" AND catdata.value LIKE '%" + searchText + "%' ) " +
                        " AND cat.modelType.id = " + modelType.getId();

       */
        
        String hql = "";
        
        if( searchBy.equalsIgnoreCase( Model.PREFIX_MODEL_NAME ) )
        {
            hql = "SELECT COUNT( DISTINCT cat ) FROM Model AS cat  " +
                   " WHERE cat.modelType.id = " + modelType.getId() +
                   " AND cat.name LIKE '%" + searchText + "%' ";
        }
        
        else
        {
            hql = "SELECT COUNT( DISTINCT cat ) FROM Model AS cat  " +
                   " WHERE cat IN ( SELECT catdata.model FROM ModelAttributeValue catdata  WHERE catdata.modelTypeAttribute.id = "+ modelTypeAttribute.getId()+" AND catdata.value LIKE '%" + searchText + "%' ) " +
                   " AND cat.modelType.id = " + modelType.getId();
        }
        
        /*
        String hql = "SELECT COUNT( DISTINCT cat) FROM Model AS cat  " +
        " WHERE cat.name LIKE '%" + searchText + "%'  " +
        " AND cat.modelType.id = " + modelType.getId();
       */ 
        
        Query query = getQuery( hql );

        Number rs = (Number) query.uniqueResult();

        return (rs != null) ? rs.intValue() : 0;
    }
    
    @SuppressWarnings( "unchecked" )
    //public Collection<Model> getModels( ModelType modelType, ModelTypeAttribute modelTypeAttribute, String searchText, int min, int max )
    public Collection<Model> getModels( ModelType modelType, ModelTypeAttribute modelTypeAttribute, String searchText, String searchBy, int min, int max )
    {
        /*
        String hql = "SELECT DISTINCT cat FROM Model AS cat  " +
                        " WHERE cat IN ( SELECT catdata.model FROM ModelAttributeValue catdata WHERE catdata.modelTypeAttribute.id = "+ modelTypeAttribute.getId()+" AND catdata.value LIKE '%" + searchText + "%' ) " +
                        " AND cat.modelType.id = " + modelType.getId();

        */
        
        String hql = "";
        
        if( searchBy.equalsIgnoreCase( Model.PREFIX_MODEL_NAME ) )
        {
            hql = "SELECT DISTINCT cat FROM Model AS cat  " +
                   " WHERE cat.modelType.id = " + modelType.getId() +
                   " AND cat.name LIKE '%" + searchText + "%' ";
        }
        
        else
        {
            hql = "SELECT DISTINCT cat FROM Model AS cat  " +
                   " WHERE cat IN ( SELECT catdata.model FROM ModelAttributeValue catdata WHERE catdata.modelTypeAttribute.id = "+ modelTypeAttribute.getId()+" AND catdata.value LIKE '%" + searchText + "%' ) " +
                   " AND cat.modelType.id = " + modelType.getId();
        }
        
        /*
        postGress Hibernate query
        
        SELECT DISTINCT cat FROM Model AS cat  " +
        " WHERE cat IN ( SELECT catdata.model FROM ModelAttributeValue catdata WHERE catdata.modelTypeAttribute.id = "+ modelTypeAttribute.getId()+" AND lower(catdata.value) LIKE '%" + searchText + "%' ) " +
        " AND cat.modelType.id = " + modelType.getId();
        */
        
        /*
        String hql = "SELECT DISTINCT cat FROM Model AS cat  " +
                     " WHERE cat.name LIKE '%" + searchText + "%' ) " +
                     " AND cat.modelType.id = " + modelType.getId();
        
        */
        
        Query query = getQuery( hql ).setFirstResult( min ).setMaxResults( max );

        return query.list();
    }   
    
}
