package org.hisp.dhis.mobile.sms;

/*
 * Copyright (c) 2004-2007, University of Oslo
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
import java.util.Date;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.mobile.sms.api.SmsInbound;
import org.hisp.dhis.mobile.sms.api.SmsInboundStore;

/**
 *
 * @author Saptarshi
 */
public class HibernateSmsInboundStore implements SmsInboundStore
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
    // Implementation
    // -------------------------------------------------------------------------
    
    @Override
    public void saveSms( SmsInbound sms )
    {
        sessionFactory.getCurrentSession().save( sms );
    }

    @Override
    public Collection<SmsInbound> getSms( String originator, Integer process, Date startDate, Date endDate )
    {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria( SmsInbound.class );
        if ( originator != null && !originator.equals( "" ) )
        {
            crit.add( Restrictions.eq( "originator", originator ) );
        }
        if ( process != null )
        {
            crit.add( Restrictions.eq( "process", process ) );
        }
        if ( startDate != null && endDate != null )
        {
            crit.add( Restrictions.between( "receiveDate", startDate, endDate ) );
        }
        return crit.list();
    }

    @Override
    public void updateSms( SmsInbound sms )
    {
        sessionFactory.getCurrentSession().update( sms );
    }

    @Override
    public long getSmsCount()
    {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria( SmsInbound.class );
        criteria.setProjection( Projections.rowCount() );
        Long count = (Long) criteria.uniqueResult();
        return count != null ? count.longValue() : (long) 0;
    }
}
