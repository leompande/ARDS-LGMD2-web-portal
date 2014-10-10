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

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.reportsheet.AttributeValueGroupOrder;
import org.hisp.dhis.reportsheet.AttributeValueGroupOrderStore;
import org.hisp.dhis.reportsheet.ExportReport;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */

@Transactional
public class HibernateAttributeValueGroupOrderStore
    implements AttributeValueGroupOrderStore
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

    public AttributeValueGroupOrder getAttributeValueGroupOrder( Integer id )
    {
        Session session = sessionFactory.getCurrentSession();
        return (AttributeValueGroupOrder) session.get( AttributeValueGroupOrder.class, id );
    }

    public AttributeValueGroupOrder getAttributeValueGroupOrderByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria( AttributeValueGroupOrder.class );
        criteria.add( Restrictions.eq( "name", name ) );

        return (AttributeValueGroupOrder) criteria.uniqueResult();
    }

    public AttributeValueGroupOrder getAttributeValueGroupOrder( String name, String clazzName, Integer reportId )
    {
        Session session = sessionFactory.getCurrentSession();

        String sql = "SELECT * FROM attributevaluegrouporder_reportexcels WHERE lower(name) = :name";

        if ( clazzName.equals( ExportReport.class.getSimpleName() ) )
        {
            sql += " AND reportexcelid = :reportId";
        }
        else
        {
            sql += " AND excelitemgroupid = :reportId";
        }

        SQLQuery query = session.createSQLQuery( sql );

        query.addEntity( AttributeValueGroupOrder.class );
        query.setString( "name", name.toLowerCase() ).setInteger( "reportId", reportId );

        return (AttributeValueGroupOrder) query.uniqueResult();
    }

    public void updateAttributeValueGroupOrder( AttributeValueGroupOrder attributeValueGroupOrder )
    {
        Session session = sessionFactory.getCurrentSession();
        session.update( attributeValueGroupOrder );
    }

    public void deleteAttributeValueGroupOrder( Integer id )
    {
        Session session = sessionFactory.getCurrentSession();
        session.delete( this.getAttributeValueGroupOrder( id ) );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<AttributeValueGroupOrder> getAllAttributeValueGroupOrder()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( AttributeValueGroupOrder.class ).list();
    }
}
