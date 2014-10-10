package org.hisp.dhis.reportsheet.hibernate;

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

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hisp.dhis.reportsheet.CategoryOptionGroupOrder;
import org.hisp.dhis.reportsheet.CategoryOptionGroupOrderStore;
import org.hisp.dhis.reportsheet.ExportReport;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */

@Transactional
public class HibernateCategoryOptionGroupOrderStore
    implements CategoryOptionGroupOrderStore
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    // -------------------------------------------------------------------------
    // Data Element Group Order
    // -------------------------------------------------------------------------

    public CategoryOptionGroupOrder getCategoryOptionGroupOrder( Integer id )
    {
        Session session = sessionFactory.getCurrentSession();
        return (CategoryOptionGroupOrder) session.get( CategoryOptionGroupOrder.class, id );
    }

    public CategoryOptionGroupOrder getCategoryOptionGroupOrder( String name, String clazzName, Integer reportId )
    {
        Session session = sessionFactory.getCurrentSession();

        String sql = "SELECT * FROM reportexcel_categoryoptiongrouporders WHERE lower(name) = :name";

        if ( clazzName.equals( ExportReport.class.getSimpleName() ) )
        {
            sql += " AND reportexcelid = :reportId";
        }
        else
        {
            sql += " AND excelitemgroupid = :reportId";
        }

        SQLQuery query = session.createSQLQuery( sql );

        query.addEntity( CategoryOptionGroupOrder.class );
        query.setString( "name", name.toLowerCase() ).setInteger( "reportId", reportId );

        return (CategoryOptionGroupOrder) query.uniqueResult();
    }

    public void updateCategoryOptionGroupOrder( CategoryOptionGroupOrder categoryOptionGroupOrder )
    {
        Session session = sessionFactory.getCurrentSession();
        session.update( categoryOptionGroupOrder );
    }

    public void deleteCategoryOptionGroupOrder( Integer id )
    {
        Session session = sessionFactory.getCurrentSession();
        session.delete( this.getCategoryOptionGroupOrder( id ) );
    }
}
