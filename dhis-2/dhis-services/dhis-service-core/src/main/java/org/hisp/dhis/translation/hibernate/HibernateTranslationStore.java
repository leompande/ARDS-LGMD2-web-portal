package org.hisp.dhis.translation.hibernate;

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
import java.util.List;
import java.util.Locale;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.system.util.LocaleUtils;
import org.hisp.dhis.translation.Translation;
import org.hisp.dhis.translation.TranslationStore;

/**
 * @author Oyvind Brucker
 */
public class HibernateTranslationStore
    implements TranslationStore
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
    // Translation
    // -------------------------------------------------------------------------

    public void addTranslation( Translation translation )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( translation );
    }

    public void updateTranslation( Translation translation )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( translation );
    }

    @SuppressWarnings( "unchecked" )
    public Translation getTranslation( String className, int id, Locale locale, String property )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Translation.class );

        criteria.add( Restrictions.eq( "className", className ) );
        criteria.add( Restrictions.eq( "id", id ) );
        criteria.add( Restrictions.in( "locale", LocaleUtils.getLocaleFallbacks( locale ) ) );
        criteria.add( Restrictions.eq( "property", property ) );

        criteria.setCacheable( true );

        List<Translation> list = criteria.list();
        
        List<Translation> translations = LocaleUtils.getTranslationsHighestSpecifity( list );
        
        return !translations.isEmpty() ? translations.get( 0 ) : null;
    }

    @SuppressWarnings( "unchecked" )
    public Translation getTranslationNoFallback( String className, int id, Locale locale, String property )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Translation.class );

        criteria.add( Restrictions.eq( "className", className ) );
        criteria.add( Restrictions.eq( "id", id ) );
        criteria.add( Restrictions.eq( "locale", locale.toString() ) );
        criteria.add( Restrictions.eq( "property", property ) );

        criteria.setCacheable( true );

        List<Translation> translations = criteria.list();     
               
        return !translations.isEmpty() ? translations.get( 0 ) : null;
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<Translation> getTranslations( String className, int id, Locale locale )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Translation.class );

        criteria.add( Restrictions.eq( "className", className ) );
        criteria.add( Restrictions.eq( "id", id ) );
        criteria.add( Restrictions.in( "locale", LocaleUtils.getLocaleFallbacks( locale ) ) );

        criteria.setCacheable( true );

        List<Translation> translations = criteria.list();
        
        return LocaleUtils.getTranslationsHighestSpecifity( translations );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Translation> getTranslationsNoFallback( String className, int id, Locale locale )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Translation.class );

        criteria.add( Restrictions.eq( "className", className ) );
        criteria.add( Restrictions.eq( "id", id ) );
        criteria.add( Restrictions.eq( "locale", locale.toString() ) );

        criteria.setCacheable( true );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Translation> getTranslations( String className, Locale locale )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Translation.class );

        criteria.add( Restrictions.eq( "className", className ) );
        criteria.add( Restrictions.in( "locale", LocaleUtils.getLocaleFallbacks( locale ) ) );

        criteria.setCacheable( true );

        List<Translation> translations = criteria.list();
        
        return LocaleUtils.getTranslationsHighestSpecifity( translations );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Translation> getTranslations( Locale locale )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Translation.class );

        criteria.add( Restrictions.eq( "locale", locale.toString() ) );

        criteria.setCacheable( true );
        
        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Translation> getAllTranslations()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Translation.class );

        criteria.setCacheable( true );

        return criteria.list();
    }

    public void deleteTranslation( Translation translation )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( translation );
    }

    @SuppressWarnings( "unchecked" )
    public void deleteTranslations( String className, int id )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "from Translation t where t.className = :className and t.id = :id" );

        query.setString( "className", className );
        query.setInteger( "id", id );

        List<Object> objlist = query.list();

        for ( Object object : objlist )
        {
            session.delete( object );
        }
    }
}
