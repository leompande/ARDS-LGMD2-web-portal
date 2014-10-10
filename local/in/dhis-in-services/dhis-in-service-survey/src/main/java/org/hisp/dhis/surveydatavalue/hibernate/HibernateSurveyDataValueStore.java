package org.hisp.dhis.surveydatavalue.hibernate;

/*
 * Copyright (c) 2004-2009, University of Oslo
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
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.survey.Survey;
import org.hisp.dhis.surveydatavalue.SurveyDataValue;
import org.hisp.dhis.surveydatavalue.SurveyDataValueStore;

/**
 * @author Brajesh Murari
 * @version $Id$
 */

public class HibernateSurveyDataValueStore
    implements SurveyDataValueStore
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

    public void addSurveyDataValue( SurveyDataValue surveyDataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( surveyDataValue );
    }

    public void deleteSurveyDataValue( SurveyDataValue surveyDataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( surveyDataValue );
    }

    public int deleteSurveyDataValuesBySource( OrganisationUnit source )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "delete DataValue where source = :source" );
        query.setEntity( "source", source );

        return query.executeUpdate();
    }

    public int deleteSurveyDataValuesBySurvey( Survey survey )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "delete SurveyDataValue where survey = :survey" );
        query.setEntity( "survey", survey );

        return query.executeUpdate();
    }

    public int deleteSurveyDataValuesByIndicator( Indicator indicator )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "delete SurveyDataValue where indicator = :indicator" );
        query.setEntity( "indicator", indicator );

        return query.executeUpdate();
    }

    public int deleteSurveyDataValuesBySurveyIndicatorAndSource( Survey survey, Indicator indicator, OrganisationUnit source )
    {
    	Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "delete SurveyDataValue where survey = :survey,indicator = :indicator,source = :source" );
        query.setEntity( "survey", survey );
        query.setEntity( "indicator", indicator );
        query.setEntity( "source", source );

        return query.executeUpdate();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<SurveyDataValue> getAllSurveyDataValues()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( SurveyDataValue.class );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<SurveyDataValue> getSurveyDataValues( OrganisationUnit source )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( SurveyDataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<SurveyDataValue> getSurveyDataValues( Survey survey, Collection<OrganisationUnit> sources )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( SurveyDataValue.class );
        criteria.add( Restrictions.in( "source", sources ) );
        criteria.add( Restrictions.eq( "survey", survey ) );

        return criteria.list();
    }

    public SurveyDataValue getSurveyDataValue( OrganisationUnit source, Survey survey )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( SurveyDataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "survey", survey ) );

        return (SurveyDataValue) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<SurveyDataValue> getSurveyDataValues( OrganisationUnit source, Survey survey )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( SurveyDataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "survey", survey ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<SurveyDataValue> getSurveyDataValues( Collection<OrganisationUnit> sources, Survey survey )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( SurveyDataValue.class );
        criteria.add( Restrictions.in( "source", sources ) );
        criteria.add( Restrictions.eq( "survey", survey ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<SurveyDataValue> getSurveyDataValues( OrganisationUnit source, Collection<Survey> surveys )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( SurveyDataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.in( "surveys", surveys ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<SurveyDataValue> getSurveyDataValues( Collection<Survey> surveys,
        Collection<OrganisationUnit> sources, int firstResult, int maxResults )
    {

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataValue.class );
        criteria.add( Restrictions.in( "surveys", surveys ) );
        criteria.add( Restrictions.in( "source", sources ) );

        if ( maxResults != 0 )
        {
            criteria.addOrder( Order.asc( "surveys" ) );
            criteria.addOrder( Order.asc( "source" ) );

            criteria.setFirstResult( firstResult );
            criteria.setMaxResults( maxResults );
        }

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<SurveyDataValue> getSurveyDataValues( Survey survey )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( SurveyDataValue.class );
        criteria.add( Restrictions.eq( "survey", survey ) );

        return criteria.list();
    }

    public void updateSurveyDataValue( SurveyDataValue dataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( dataValue );
    }

    public SurveyDataValue getSurveyDataValue( OrganisationUnit source, Survey survey, Indicator indicator )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( SurveyDataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "survey", survey ) );
        criteria.add( Restrictions.eq( "indicator", indicator ) );

        return (SurveyDataValue) criteria.uniqueResult();
    }
}
