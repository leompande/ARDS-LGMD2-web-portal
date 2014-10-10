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
package org.hisp.dhis.linelisting.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.linelisting.Employee;
import org.hisp.dhis.linelisting.EmployeeStore;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version HiberateEmployeeStore.java Oct 15, 2010 1:58:34 PM
 */
public class HiberateEmployeeStore implements EmployeeStore
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
    // Employee
    // -------------------------------------------------------------------------
    
    public void addEmployee( Employee employee )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( employee );
    }

    public void deleteEmployee( Employee employee )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( employee );
    }

    public void updateEmployee( Employee employee )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( employee );
    }

    public Employee getEmployeeByPDSCode( String pdsCode )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Employee.class );
        criteria.add( Restrictions.eq( "pdsCode", pdsCode ) );

        return (Employee) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Employee> getAllEmployee()
    {
        Session session = sessionFactory.getCurrentSession();
        
        return session.createCriteria( Employee.class ).list();
    }
    
    public Collection<Employee> getEmployeeByisTransferred( Boolean isTransferred )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Employee.class );
        criteria.add( Restrictions.eq( "isTransferred", isTransferred ) );

        return criteria.list();        
    }
    
    public int getEmployeeCount()
    {
        Session session = sessionFactory.getCurrentSession();
        
        Query query = session.createQuery( "select count(*) from Employee" );
        
        Number rs = (Number) query.uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }
    
    @SuppressWarnings("unchecked")
    public Collection<Employee> getEmployeesBetween( int first, int max )
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery( "from Employee" ).setFirstResult( first ).setMaxResults( max ).list();
    }

}
