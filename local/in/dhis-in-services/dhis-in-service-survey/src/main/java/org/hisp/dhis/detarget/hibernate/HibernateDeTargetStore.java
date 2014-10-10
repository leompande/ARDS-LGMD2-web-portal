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
package org.hisp.dhis.detarget.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.detarget.DeTarget;
import org.hisp.dhis.detarget.DeTargetMember;
import org.hisp.dhis.detarget.DeTargetStore;
import org.hisp.dhis.detargetdatavalue.DeTargetDataValue;
import org.hisp.dhis.detargetdatavalue.DeTargetDataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version HibernateDeTargetStore.java Jan 13, 2011 10:35:49 AM
 */
public class HibernateDeTargetStore implements DeTargetStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    
    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }
    
    private DeTargetDataValueService deTargetDataValueService;

    public void setDeTargetDataValueService( DeTargetDataValueService deTargetDataValueService )
    {
        this.deTargetDataValueService = deTargetDataValueService;
    }
    
    // -------------------------------------------------------------------------
    // DeTarget
    // -------------------------------------------------------------------------

    public int addDeTarget( DeTarget  deTarget )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( deTarget );
    }
    
    public void updateDeTarget( DeTarget deTarget )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( deTarget );
    }
    
    public int deleteDeTarget( DeTarget deTarget )
    {
        Session session = sessionFactory.getCurrentSession();

        List<DeTargetDataValue> deTargetDataValueList = new ArrayList<DeTargetDataValue>( deTargetDataValueService.getDeTargetDataValues( deTarget ) );
        
        if( deTargetDataValueList == null || deTargetDataValueList.isEmpty() )        
        {
            session.delete( deTarget );
        }
        else
        {            
            return -1;
        }
        
        return 0;
    }
   public DeTarget getDeTarget( int id )
   {
       Session session = sessionFactory.getCurrentSession();

       return (DeTarget) session.get( DeTarget.class, id );
   }

   public DeTarget getDeTargetByName( String name )
   {
       Session session = sessionFactory.getCurrentSession();

       Criteria criteria = session.createCriteria( DeTarget.class );
       criteria.add( Restrictions.eq( "name", name ) );

       return (DeTarget) criteria.uniqueResult();
   }
    
   public DeTarget getDeTargetByShortName( String shortName )
   {
       Session session = sessionFactory.getCurrentSession();

       Criteria criteria = session.createCriteria( DeTarget.class );
       criteria.add( Restrictions.eq( "shortName", shortName ) );

       return (DeTarget) criteria.uniqueResult();
       
   }
    
   @SuppressWarnings( "unchecked" )
   
   public Collection<DeTarget> getDeTargetsBySource( OrganisationUnit source )
   {
       Session session = sessionFactory.getCurrentSession();

       Criteria criteria = session.createCriteria( DeTarget.class );
       criteria.createAlias( "sources", "s" );
       criteria.add( Restrictions.eq( "s.id", source.getId() ) );

       return criteria.list();
   }
   @SuppressWarnings( "unchecked" )
   
   public Collection<DeTargetMember> getDeTargetsByDataElementAndCategoryOptionCombo( DataElement dataelement ,DataElementCategoryOptionCombo deoptioncombo )
   {
       Session session = sessionFactory.getCurrentSession();

       Criteria criteria = session.createCriteria( DeTargetMember.class );
       //criteria.createAlias( "dataelements", "i" );
       //criteria.createAlias( "decategoryOptionCombo", "j" );
       //criteria.add( Restrictions.eq( "i.id", dataelement.getId() ) );
       //criteria.add( Restrictions.eq( "j.id", deoptioncombo.getId() ) );
       criteria.add( Restrictions.eq( "dataelements", dataelement ) );
       criteria.add( Restrictions.eq( "decategoryOptionCombo", deoptioncombo ) );
       
       return criteria.list();
   }
   @SuppressWarnings( "unchecked" )
   public Collection<DeTarget> getAllDeTargets()
   {
       Session session = sessionFactory.getCurrentSession();

       return session.createCriteria( DeTarget.class ).list();
   }
   
   
   // -------------------------------------------------------------------------
   // DeTargetMember
   // -------------------------------------------------------------------------
   
   public void addDeTargetMember( DeTargetMember  deTargetMember )
   {
       Session session = sessionFactory.getCurrentSession();

       session.save( deTargetMember );
   }
   
   public void updateDeTargetMember( DeTargetMember deTargetMember )
   {
       Session session = sessionFactory.getCurrentSession();

       session.update( deTargetMember );
   }
/*
   public int deleteDeTargetMember( DeTarget deTarget ,DataElement dataelement ,DataElementCategoryOptionCombo deoptioncombo )
   {
       Session session = sessionFactory.getCurrentSession();

       List<DeTargetDataValue> deTargetDataValueList = new ArrayList<DeTargetDataValue>( deTargetDataValueService.getDeTargetDataValues(  deTarget , dataelement , deoptioncombo  ) );
       
       if( deTargetDataValueList == null || deTargetDataValueList.isEmpty() )        
       {
           session.delete( deTarget );
       }
       else
       {            
           return -1;
       }
       
       return 0;
   }
   */
   
   public int deleteDeTargetMember( DeTargetMember  deTargetMember )
   {
       Session session = sessionFactory.getCurrentSession();

       List<DeTargetDataValue> deTargetDataValueList = new ArrayList<DeTargetDataValue>( deTargetDataValueService.getDeTargetDataValues(  deTargetMember.getDetarget() , deTargetMember.getDataelements() , deTargetMember.getDecategoryOptionCombo()  ) );
       
       if( deTargetDataValueList == null || deTargetDataValueList.isEmpty() )        
       {
           session.delete( deTargetMember );
       }
       else
       {            
           return -1;
       }
       
       return 0;
   }
   
   
   
   @SuppressWarnings( "unchecked" )
   public  List<DeTargetMember> getDeTargetMembers( DeTarget deTarget )
   {
       Session session = sessionFactory.getCurrentSession();

       Criteria criteria = session.createCriteria( DeTargetMember.class );
       //criteria.createAlias( "detarget", "d" );
       //criteria.add( Restrictions.eq( "d.id", deTarget.getId() ) );
       criteria.add( Restrictions.eq( "detarget", deTarget ) );
       
       return criteria.list();
   }



}

    
    
    

