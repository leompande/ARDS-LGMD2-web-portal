package org.hisp.dhis.linelisting.hibernate;

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

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.linelisting.LineListValidationRule;
import org.hisp.dhis.linelisting.LineListValidationRuleStore;

/**
 * @author Margrethe Store
 * @version $Id: HibernateLineListValidationRuleStore.java 3676 2007-10-22 17:30:12Z larshelg $
 */
public class HibernateLineListValidationRuleStore
    implements LineListValidationRuleStore
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
    // LineListValidationRule
    // -------------------------------------------------------------------------

    public int addLineListValidationRule( LineListValidationRule LineListValidationRule )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( LineListValidationRule );
    }

    public void deleteLineListValidationRule( LineListValidationRule LineListValidationRule )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( LineListValidationRule );
    }

    public void updateLineListValidationRule( LineListValidationRule LineListValidationRule )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( LineListValidationRule );
    }
    
    public LineListValidationRule getLineListValidationRule( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (LineListValidationRule) session.get( LineListValidationRule.class, id );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LineListValidationRule> getAllLineListValidationRules()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( LineListValidationRule.class ).list();
    }

    public LineListValidationRule getLineListValidationRuleByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LineListValidationRule.class );
        criteria.add( Restrictions.eq( "name", name ) );

        return (LineListValidationRule) criteria.uniqueResult();
    }
}
