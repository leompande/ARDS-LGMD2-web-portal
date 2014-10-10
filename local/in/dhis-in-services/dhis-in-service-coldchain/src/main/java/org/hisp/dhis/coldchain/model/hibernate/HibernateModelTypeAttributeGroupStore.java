package org.hisp.dhis.coldchain.model.hibernate;

import java.util.Collection;

import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeGroup;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeGroupStore;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version HibernateModelTypeAttributeGroupStore.javaOct 9, 2012 3:56:11 PM	
 */

public class HibernateModelTypeAttributeGroupStore extends HibernateIdentifiableObjectStore<ModelTypeAttributeGroup> implements ModelTypeAttributeGroupStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // ModelTypeAttributeGroup
    // -------------------------------------------------------------------------
    
    /*
    public void addModelTypeAttributeGroup( ModelTypeAttributeGroup modelTypeAttributeGroup )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( modelTypeAttributeGroup );
    }

    public void deleteModelTypeAttributeGroup( ModelTypeAttributeGroup modelTypeAttributeGroup )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( modelTypeAttributeGroup );
    }
    
    public void updateModelTypeAttributeGroup( ModelTypeAttributeGroup modelTypeAttributeGroup )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( modelTypeAttributeGroup );
    }
    
   */
    @Override
    
    public ModelTypeAttributeGroup getModelTypeAttributeGroupById( int id )
    {
        return (ModelTypeAttributeGroup) sessionFactory.getCurrentSession().get( ModelTypeAttributeGroup.class, id );
        
    }
    
    @Override
    
    public ModelTypeAttributeGroup getModelTypeAttributeGroupByName( String name )
    {
        return ( ModelTypeAttributeGroup ) getCriteria( Restrictions.eq( "name", name ) ).uniqueResult();
    }
    
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<ModelTypeAttributeGroup> getAllModelTypeAttributeGroups()
    {
        return sessionFactory.getCurrentSession().createCriteria( ModelTypeAttributeGroup.class ).list();
    }
   
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<ModelTypeAttributeGroup> getModelTypeAttributeGroupsByModelType( ModelType modelType )
    {
        return getCriteria( Restrictions.eq( "modelType", modelType ) ).list();
    }
}

