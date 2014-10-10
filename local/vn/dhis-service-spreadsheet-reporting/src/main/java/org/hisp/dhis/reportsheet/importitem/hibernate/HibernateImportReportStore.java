package org.hisp.dhis.reportsheet.importitem.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reportsheet.importitem.ImportItem;
import org.hisp.dhis.reportsheet.importitem.ImportReport;
import org.hisp.dhis.reportsheet.importitem.ImportReportStore;

/*
 * Copyright (c) 2004-2011, University of Oslo
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

/**
 * @author Chau Thu Tran
 * @version $Id$
 */

public class HibernateImportReportStore
    implements ImportReportStore
{
    // ----------------------------------------------------------------------
    // Dependencies
    // ----------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    // ----------------------------------------------------------------------
    // ImportReportStore implementation
    // ----------------------------------------------------------------------

    public int addImportItem( ImportItem excelItem )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( excelItem );
    }

    public void deleteImportItem( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( getImportItem( id ) );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ImportItem> getAllImportItem()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ImportItem.class );

        return criteria.list();
    }

    public void updateImportItem( ImportItem excelItem )
    {
        Session session = sessionFactory.getCurrentSession();

        session.saveOrUpdate( excelItem );
    }

    public ImportItem getImportItem( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ImportItem.class );

        criteria.add( Restrictions.eq( "id", id ) );

        return (ImportItem) criteria.uniqueResult();
    }

    @Override
    public ImportItem getImportItem( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ImportItem.class );

        criteria.add( Restrictions.eq( "name", name ) );

        return (ImportItem) criteria.uniqueResult();
    }

    public int addImportReport( ImportReport importReport )
    {
        PeriodType periodType = periodService.reloadPeriodType( importReport.getPeriodType() );

        importReport.setPeriodType( periodType );

        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( importReport );
    }

    public void deleteImportReport( int id )
    {

        Session session = sessionFactory.getCurrentSession();

        session.delete( getImportReport( id ) );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ImportReport> getAllImportReport()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ImportReport.class );

        return criteria.list();
    }

    public ImportReport getImportReport( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ImportReport.class );

        criteria.add( Restrictions.eq( "id", id ) );

        return (ImportReport) criteria.uniqueResult();
    }

    public ImportReport getImportReport( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ImportReport.class );

        criteria.add( Restrictions.eq( "name", name ) );

        return (ImportReport) criteria.uniqueResult();
    }

    public void updateImportReport( ImportReport importReport )
    {
        PeriodType periodType = periodService.reloadPeriodType( importReport.getPeriodType() );

        importReport.setPeriodType( periodType );

        Session session = sessionFactory.getCurrentSession();

        session.update( importReport );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ImportReport> getImportReports( OrganisationUnit organisationUnit )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ImportReport.class );

        criteria.createAlias( "organisationAssocitions", "o" );

        criteria.add( Restrictions.eq( "o.id", organisationUnit.getId() ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ImportReport> getImportReportsByType( String type )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ImportReport.class );

        criteria.add( Restrictions.eq( "type", type ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Integer> getSheets()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ImportItem.class );

        return criteria.setProjection( Projections.distinct( Projections.property( "sheetNo" ) ) ).list();
    }
}
