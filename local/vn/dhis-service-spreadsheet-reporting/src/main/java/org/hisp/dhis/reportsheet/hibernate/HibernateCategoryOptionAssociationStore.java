package org.hisp.dhis.reportsheet.hibernate;

/*
 * Copyright (c) 2004-2012, University of Oslo All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of the HISP project nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission. THIS SOFTWARE IS
 * PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.reportsheet.CategoryOptionAssociation;
import org.hisp.dhis.reportsheet.CategoryOptionAssociationStore;
import org.hisp.dhis.system.util.ConversionUtils;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class HibernateCategoryOptionAssociationStore
    implements CategoryOptionAssociationStore
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

    public void saveCategoryOptionAssociation( CategoryOptionAssociation registration )
    {
        sessionFactory.getCurrentSession().save( registration );
    }

    public void updateCategoryOptionAssociation( CategoryOptionAssociation registration )
    {
        sessionFactory.getCurrentSession().update( registration );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<CategoryOptionAssociation> getAllCategoryOptionAssociations()
    {
        return sessionFactory.getCurrentSession().createCriteria( CategoryOptionAssociation.class ).list();
    }

    public CategoryOptionAssociation getCategoryOptionAssociation( OrganisationUnit source,
        DataElementCategoryOption categoryOption )
    {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria( CategoryOptionAssociation.class );

        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "categoryOption", categoryOption ) );

        return (CategoryOptionAssociation) criteria.uniqueResult();
    }

    public void deleteCategoryOptionAssociation( CategoryOptionAssociation registration )
    {
        sessionFactory.getCurrentSession().delete( registration );
        sessionFactory.getCurrentSession().flush();
    }

    public void deleteCategoryOptionAssociations( OrganisationUnit source )
    {
        String hql = "delete from CategoryOptionAssociation c where c.source = :source";

        Query query = sessionFactory.getCurrentSession().createQuery( hql );

        query.setEntity( "source", source );

        query.executeUpdate();
    }

    public void deleteCategoryOptionAssociations( DataElementCategoryOption categoryOption )
    {
        String hql = "delete from CategoryOptionAssociation c where c.categoryOption = :categoryOption";

        Query query = sessionFactory.getCurrentSession().createQuery( hql );

        query.setEntity( "categoryOption", categoryOption );

        query.executeUpdate();
    }

    @Override
    public void deleteCategoryOptionAssociations( Collection<OrganisationUnit> sources,
        DataElementCategoryOption categoryOption )
    {
        String hql = "delete from CategoryOptionAssociation c where c.categoryOption = :categoryOption and c.source.id in (:ids)";

        Query query = sessionFactory.getCurrentSession().createQuery( hql );

        query.setEntity( "categoryOption", categoryOption );
        query.setParameterList( "ids", ConversionUtils.getIdentifiers( OrganisationUnit.class, sources ) );

        query.executeUpdate();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<CategoryOptionAssociation> getCategoryOptionAssociations( OrganisationUnit source )
    {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria( CategoryOptionAssociation.class );

        criteria.add( Restrictions.eq( "source", source ) );

        return criteria.list();
    }

    public Collection<DataElementCategoryOption> getCategoryOptions( OrganisationUnit source )
    {
        Set<DataElementCategoryOption> categoryOptions = new HashSet<DataElementCategoryOption>();

        for ( CategoryOptionAssociation association : this.getCategoryOptionAssociations( source ) )
        {
            categoryOptions.add( association.getCategoryOption() );
        }

        return categoryOptions;
    }

    @SuppressWarnings( "unchecked" )
    public Collection<CategoryOptionAssociation> getCategoryOptionAssociations( DataElementCategoryOption categoryOption )
    {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria( CategoryOptionAssociation.class );

        criteria.add( Restrictions.eq( "categoryOption", categoryOption ) );

        return criteria.list();
    }
}
