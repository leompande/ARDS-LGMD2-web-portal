/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.detargetdatavalue.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.detarget.DeTarget;
import org.hisp.dhis.detargetdatavalue.DeTargetDataValue;
import org.hisp.dhis.detargetdatavalue.DeTargetDataValueStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;


/**
 * @author Mithilesh Kumar Thakur
 *
 * @version HibernateDeTargetDataValueStore.java Jan 13, 2011 10:37:20 AM
 */
public class HibernateDeTargetDataValueStore implements DeTargetDataValueStore
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
    // HibernateSurveyDataValueStore's methods
    // -------------------------------------------------------------------------

    public void addDeTargetDataValue( DeTargetDataValue deTargetDataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( deTargetDataValue );
    }
        
    public void updateDeTargetDataValue( DeTargetDataValue deTargetDataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( deTargetDataValue );
    }
    
    public void deleteDeTargetDataValue( DeTargetDataValue deTargetDataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( deTargetDataValue );
    }
    
    public int deleteDeTargetDataValuesBySource( OrganisationUnit source )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "delete DataValue where source = :source" );
        query.setEntity( "source", source );

        return query.executeUpdate();
    }
    
    public int deleteDeTargetDataValuesByDeTarget( DeTarget deTarget )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "delete DeTargetDataValue where deTarget = :deTarget" );
        query.setEntity( "survey", deTarget );

        return query.executeUpdate();
    }
    
    public int deleteDeTargetDataValuesByDataElementAndCategoryOptionCombo( DataElement dataelement ,DataElementCategoryOptionCombo deoptioncombo )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "delete DeTargetDataValue where dataelement = :dataelement ,deoptioncombo =:deoptioncombo" );
        query.setEntity( "dataelement", dataelement );
        query.setEntity( "dataelement", dataelement );

        return query.executeUpdate();
    }
    
    public int deleteDeTargetDataValuesByDeTargetDataElementCategoryOptionComboAndSource( DeTarget deTarget, DataElement dataelement ,DataElementCategoryOptionCombo deoptioncombo, OrganisationUnit source )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "delete DeTargetDataValue where deTarget = :deTarget,dataelement = :dataelement, deoptioncombo = :deoptioncombo ,source = :source" );
        query.setEntity( "deTarget", deTarget );
        query.setEntity( "dataelement", dataelement );
        query.setEntity( "deoptioncombo", deoptioncombo );
        query.setEntity( "source", source );

        return query.executeUpdate();
    }
    /*  
    public DeTargetDataValue getDeTargetDataValue( Source source, DeTarget deTarget )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DeTargetDataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "deTarget", deTarget ) );

        return (DeTargetDataValue) criteria.uniqueResult();
    }
    */
    @SuppressWarnings( "unchecked" )
    public Collection<DeTargetDataValue> getAllDeTargetDataValues()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DeTargetDataValue.class );

        return criteria.list();
    }
    @SuppressWarnings( "unchecked" )
    public Collection<DeTargetDataValue> getDeTargetDataValues( OrganisationUnit source )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DeTargetDataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );

        return criteria.list();
    }
    @SuppressWarnings( "unchecked" )
    public Collection<DeTargetDataValue> getDeTargetDataValues( OrganisationUnit source, DeTarget deTarget )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DeTargetDataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "deTarget", deTarget ) );

        return criteria.list();
    }
    @SuppressWarnings( "unchecked" )
    public  Collection<DeTargetDataValue> getDeTargetDataValues( Collection<OrganisationUnit> sources, DeTarget deTarget )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DeTargetDataValue.class );
        criteria.add( Restrictions.in( "sources", sources ) );
        criteria.add( Restrictions.eq( "deTarget", deTarget ) );

        return criteria.list();
    }
    @SuppressWarnings( "unchecked" )
    public Collection<DeTargetDataValue> getDeTargetDataValues( OrganisationUnit source, Collection<DeTarget> deTargets )
    {
       Session session = sessionFactory.getCurrentSession();

       Criteria criteria = session.createCriteria( DeTargetDataValue.class );
       criteria.add( Restrictions.eq( "source", source ) );
       criteria.add( Restrictions.in( "deTargets", deTargets ) );

       return criteria.list();
    }
    @SuppressWarnings( "unchecked" )
    public Collection<DeTargetDataValue> getDeTargetDataValues( DeTarget deTarget, Collection<OrganisationUnit> sources )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DeTargetDataValue.class );
        criteria.add( Restrictions.in( "source", sources ) );
        criteria.add( Restrictions.eq( "deTarget", deTarget ) );

        return criteria.list();
    }
    @SuppressWarnings( "unchecked" )
    public Collection<DeTargetDataValue> getDeTargetDataValues( Collection<DeTarget> deTargets,  Collection<OrganisationUnit> sources, int firstResult, int maxResults )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataValue.class );
        criteria.add( Restrictions.in( "deTargets", deTargets ) );
        criteria.add( Restrictions.in( "source", sources ) );

        if ( maxResults != 0 )
        {
            criteria.addOrder( Order.asc( "deTargets" ) );
            criteria.addOrder( Order.asc( "source" ) );

            criteria.setFirstResult( firstResult );
            criteria.setMaxResults( maxResults );
        }

        return criteria.list();
    }
    /*
    @SuppressWarnings( "unchecked" )
    public Collection<DeTargetDataValue> getDeTargetMemberDataValues( DeTargetMember deTargetMember ,DataElement dataelement ,DataElementCategoryOptionCombo decategoryOptionCombo )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DeTargetDataValue.class );
        criteria.add( Restrictions.eq( "deTargetMember", deTargetMember ) );
        criteria.add( Restrictions.eq( "dataelement", dataelement ) );
        criteria.add( Restrictions.eq( "decategoryOptionCombo", decategoryOptionCombo ) );

        return criteria.list();
    }
*/    
    @SuppressWarnings( "unchecked" )
     public Collection<DeTargetDataValue> getDeTargetDataValues( DeTarget deTarget, OrganisationUnit source, Period period )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DeTargetDataValue.class );
        criteria.add( Restrictions.eq( "deTarget", deTarget ) );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "period", period ) );

        return criteria.list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<DeTargetDataValue> getDeTargetDataValues( DeTarget deTarget ,DataElement dataelement ,DataElementCategoryOptionCombo decategoryOptionCombo )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DeTargetDataValue.class );
        criteria.add( Restrictions.eq( "deTarget", deTarget ) );
        criteria.add( Restrictions.eq( "dataelement", dataelement ) );
        criteria.add( Restrictions.eq( "decategoryOptionCombo", decategoryOptionCombo ) );

        return criteria.list();
    }
    
    
    @SuppressWarnings( "unchecked" )
    public Collection<DeTargetDataValue> getDeTargetDataValues( DeTarget deTarget )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DeTargetDataValue.class );
        
        criteria.add( Restrictions.eq( "deTarget", deTarget ) );

        return criteria.list();
    }
   
    @SuppressWarnings( "unchecked" )
    public Collection<DeTargetDataValue> getDeTargetDataValues( OrganisationUnit source, DeTarget deTarget, DataElement dataelement ,DataElementCategoryOptionCombo deoptioncombo )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DeTargetDataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "deTarget", deTarget ) );
        criteria.add( Restrictions.eq( "dataelement", dataelement ) );
        criteria.add( Restrictions.eq( "decategoryOptionCombo", deoptioncombo ) );

        return  criteria.list();
    }
    
    public DeTargetDataValue getDeTargetDataValue( OrganisationUnit source, DeTarget deTarget ,Period period, DataElement dataelement ,DataElementCategoryOptionCombo deoptioncombo )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DeTargetDataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "deTarget", deTarget ) );
        criteria.add( Restrictions.eq( "period", period ) );
        criteria.add( Restrictions.eq( "dataelement", dataelement ) );
        criteria.add( Restrictions.eq( "decategoryOptionCombo", deoptioncombo ) );

        return (DeTargetDataValue) criteria.uniqueResult();
    }
    
    
    
}

    
    

