package org.hisp.dhis.program.hibernate;

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

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageDataElementStore;

import java.util.Collection;

/**
 * @author Viet Nguyen
 * @version $Id$
 */
public class HibernateProgramStageDataElementStore
    implements ProgramStageDataElementStore
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
    // Basic ProgramStageDataElement
    // -------------------------------------------------------------------------

    public void save( ProgramStageDataElement programStageDataElement )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( programStageDataElement );
    }

    public void update( ProgramStageDataElement programStageDataElement )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( programStageDataElement );
    }

    public void delete( ProgramStageDataElement programStageDataElement )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( programStageDataElement );
    }

    @SuppressWarnings("unchecked")
    public Collection<ProgramStageDataElement> getAll()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ProgramStageDataElement.class );

        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public Collection<ProgramStageDataElement> get( ProgramStage programStage )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ProgramStageDataElement.class );

        return criteria.add( Restrictions.eq( "programStage", programStage ) ).list();
    }

    @SuppressWarnings("unchecked")
    public Collection<ProgramStageDataElement> get( ProgramStage programStage, boolean compulsory )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ProgramStageDataElement.class );
        criteria.add( Restrictions.eq( "programStage", programStage ) );
        criteria.add( Restrictions.eq( "compulsory", compulsory ) );

        return criteria.list();
    }

    public ProgramStageDataElement get( ProgramStage programStage, DataElement dataElement )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ProgramStageDataElement.class );
        criteria.add( Restrictions.eq( "programStage", programStage ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );

        return (ProgramStageDataElement) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public Collection<DataElement> getListDataElement( ProgramStage programStage )
    {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria( ProgramStageDataElement.class );
        criteria.add( Restrictions.eq( "programStage", programStage ) );
        criteria.setProjection( Projections.property( "dataElement" ) );
        return criteria.list();
    }
}
