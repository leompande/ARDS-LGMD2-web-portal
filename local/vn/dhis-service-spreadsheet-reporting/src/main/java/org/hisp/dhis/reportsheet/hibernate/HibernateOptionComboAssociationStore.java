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
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.reportsheet.OptionComboAssociation;
import org.hisp.dhis.reportsheet.OptionComboAssociationStore;
import org.hisp.dhis.system.util.ConversionUtils;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class HibernateOptionComboAssociationStore
    implements OptionComboAssociationStore
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

    public void saveOptionComboAssociation( OptionComboAssociation registration )
    {
        sessionFactory.getCurrentSession().save( registration );
    }

    public void updateOptionComboAssociation( OptionComboAssociation registration )
    {
        sessionFactory.getCurrentSession().update( registration );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<OptionComboAssociation> getAllOptionComboAssociations()
    {
        return sessionFactory.getCurrentSession().createCriteria( OptionComboAssociation.class ).list();
    }

    public OptionComboAssociation getOptionComboAssociation( OrganisationUnit source,
        DataElementCategoryOptionCombo optionCombo )
    {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria( OptionComboAssociation.class );

        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "optionCombo", optionCombo ) );

        return (OptionComboAssociation) criteria.uniqueResult();
    }

    public void deleteOptionComboAssociation( OptionComboAssociation registration )
    {
        sessionFactory.getCurrentSession().delete( registration );
        sessionFactory.getCurrentSession().flush();
    }

    public void deleteOptionComboAssociations( OrganisationUnit source )
    {
        String hql = "delete from OptionComboAssociation c where c.source = :source";

        Query query = sessionFactory.getCurrentSession().createQuery( hql );

        query.setEntity( "source", source );

        query.executeUpdate();
    }

    public void deleteOptionComboAssociations( DataElementCategoryOptionCombo optionCombo )
    {
        String hql = "delete from OptionComboAssociation c where c.optionCombo = :optionCombo";

        Query query = sessionFactory.getCurrentSession().createQuery( hql );

        query.setEntity( "optionCombo", optionCombo );

        query.executeUpdate();
    }

    @Override
    public void deleteOptionComboAssociations( Collection<OrganisationUnit> sources,
        DataElementCategoryOptionCombo optionCombo )
    {
        String hql = "delete from OptionComboAssociation c where c.optionCombo = :optionCombo and c.source.id in (:ids)";

        Query query = sessionFactory.getCurrentSession().createQuery( hql );

        query.setEntity( "optionCombo", optionCombo );
        query.setParameterList( "ids", ConversionUtils.getIdentifiers( OrganisationUnit.class, sources ) );

        query.executeUpdate();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<OptionComboAssociation> getOptionComboAssociations( OrganisationUnit source )
    {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria( OptionComboAssociation.class );

        criteria.add( Restrictions.eq( "source", source ) );

        return criteria.list();
    }

    public Collection<DataElementCategoryOptionCombo> getOptionCombos( OrganisationUnit source )
    {
        Set<DataElementCategoryOptionCombo> optionCombos = new HashSet<DataElementCategoryOptionCombo>();

        for ( OptionComboAssociation association : this.getOptionComboAssociations( source ) )
        {
            optionCombos.add( association.getOptionCombo() );
        }

        return optionCombos;
    }

    @SuppressWarnings( "unchecked" )
    public Collection<OptionComboAssociation> getOptionComboAssociations( DataElementCategoryOptionCombo optionCombo )
    {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria( OptionComboAssociation.class );

        criteria.add( Restrictions.eq( "optionCombo", optionCombo ) );

        return criteria.list();
    }
}
