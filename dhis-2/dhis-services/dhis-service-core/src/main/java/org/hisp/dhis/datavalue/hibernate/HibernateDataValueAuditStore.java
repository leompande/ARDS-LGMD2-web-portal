package org.hisp.dhis.datavalue.hibernate;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueAudit;
import org.hisp.dhis.datavalue.DataValueAuditStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

/**
 * @author Quang Nguyen
 * @version Mar 30, 2010 10:42:16 PM
 */
public class HibernateDataValueAuditStore
    implements DataValueAuditStore
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
    // DataValueAuditStore implementation
    // -------------------------------------------------------------------------

    public void addDataValueAudit( DataValueAudit dataValueAudit )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( dataValueAudit );
    }

    public void deleteDataValueAudit( DataValueAudit dataValueAudit )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( dataValueAudit );
    }

    public int deleteDataValueAuditByDataValue( DataValue dataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "delete DataValueAudit where dataValue = :dataValue" );
        query.setEntity( "dataValue", dataValue );

        return query.executeUpdate();
    }

    public void deleteDataValueAuditBySource( OrganisationUnit source )
    {
        for ( DataValueAudit each : getAll() )
        {
            if ( each.getDataValue().getSource().equals( source ) )
            {
                deleteDataValueAudit( each );
            }
        }
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataValueAudit> getDataValueAuditByDataValue( DataValue dataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataValueAudit.class );
        criteria.add( Restrictions.eq( "dataValue", dataValue ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataValueAudit> getAll()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataValueAudit.class );

        return criteria.list();
    }

    public void deleteDataValueAuditByDataElement( DataElement dataElement )
    {
        for ( DataValueAudit each : getAll() )
        {
            if ( each.getDataValue().getDataElement().equals( dataElement ) )
            {
                deleteDataValueAudit( each );
            }
        }
    }

    public int deleteByPeriod( Period period )
    {
        /*
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "delete DataValueAudit where period = :period" );
        query.setEntity( "period", period );

        return query.executeUpdate();
        */
        return 0;
    }

    public int deleteByDataElementCategoryOptionCombo( DataElementCategoryOptionCombo optionCombo )
    {
        /*
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "delete DataValueAudit where optionCombo = :optionCombo" );
        query.setEntity( "optionCombo", optionCombo );

        return query.executeUpdate();
        */
        return 0;
    }
}
