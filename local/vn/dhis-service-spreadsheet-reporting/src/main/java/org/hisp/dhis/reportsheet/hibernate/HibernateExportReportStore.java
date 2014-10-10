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
import java.util.HashSet;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reportsheet.ExportItem;
import org.hisp.dhis.reportsheet.ExportReport;
import org.hisp.dhis.reportsheet.ExportReportCategory;
import org.hisp.dhis.reportsheet.ExportReportNormal;
import org.hisp.dhis.reportsheet.ExportReportOrganizationGroupListing;
import org.hisp.dhis.reportsheet.ExportReportPeriodColumnListing;
import org.hisp.dhis.reportsheet.ExportReportStore;
import org.hisp.dhis.reportsheet.PeriodColumn;
import org.hisp.dhis.reportsheet.status.DataEntryStatus;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */

@Transactional
public class HibernateExportReportStore
    implements ExportReportStore
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // -------------------------------------------------------------------------
    // Service of Report
    // -------------------------------------------------------------------------

    public int addExportReport( ExportReport report )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( report );
    }

    public void updateExportReport( ExportReport report )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( report );
    }

    public void deleteExportReport( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( getExportReport( id ) );
    }

    public ExportReport getExportReport( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (ExportReport) session.get( ExportReport.class, id );
    }

    public ExportReport getExportReport( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ExportReport.class );

        criteria.add( Restrictions.eq( "name", name ) );

        return (ExportReport) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ExportReport> getExportReportsByOrganisationUnit( OrganisationUnit organisationUnit )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ExportReport.class );

        criteria.createAlias( "organisationAssocitions", "o" );

        criteria.add( Restrictions.eq( "o.id", organisationUnit.getId() ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ExportReport> getAllExportReport()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ExportReport.class );

        return criteria.list();

    }

    @SuppressWarnings( "unchecked" )
    public Collection<String> getExportReportGroups()
    {
        String sql;

        if ( currentUserService.currentUserIsSuper() )
        {
            sql = "SELECT DISTINCT(reportgroup) FROM reportexcels ";
        }
        else
        {
            sql = "SELECT DISTINCT(reportgroup) FROM reportexcel_userroles, reportexcels "
                + " WHERE reportexcels.reportexcelid=reportexcel_userroles.reportexcelid "
                + " AND reportexcel_userroles.userroleid IN ( "
                + " SELECT userrole.userroleid FROM userrole, userrolemembers " + " WHERE userrolemembers.userid="
                + currentUserService.getCurrentUser().getId() + " AND userrole.userroleid=userrolemembers.userroleid)";
        }

        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery( sql );

        return sqlQuery.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ExportReport> getExportReportsByGroup( String group )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ExportReport.class );

        criteria.add( Restrictions.eq( "group", group ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ExportReport> getExportReportsByClazz( Class<?> clazz )
    {
        return sessionFactory.getCurrentSession().createCriteria( clazz ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ExportReport> getExportReportsByReportType( String reportType )
    {
        Class<?> clazz = null;

        if ( reportType.equals( ExportReport.TYPE.NORMAL ) )
        {
            clazz = ExportReportNormal.class;
        }
        else if ( reportType.equals( ExportReport.TYPE.CATEGORY ) )
        {
            clazz = ExportReportCategory.class;
        }
        else if ( reportType.equals( ExportReport.TYPE.ORGANIZATION_GROUP_LISTING ) )
        {
            clazz = ExportReportOrganizationGroupListing.class;
        }
        else
        {
            clazz = ExportReportPeriodColumnListing.class;
        }

        return getExportReportsByClazz( clazz );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<String> getAllExportReportTemplates()
    {
        Session session = sessionFactory.getCurrentSession();

        SQLQuery sqlQuery = session.createSQLQuery( "select DISTINCT(exceltemplate) from reportexcels" );

        return sqlQuery.list();
    }

    // -------------------------------------------------------------------------
    // Service of Report Item
    // -------------------------------------------------------------------------

    public void addExportItem( ExportItem reportItem )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( reportItem );
    }

    public void updateExportItem( ExportItem reportItem )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( reportItem );
    }

    public void deleteExportItem( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( this.getExportItem( id ) );
    }

    public ExportItem getExportItem( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (ExportItem) session.get( ExportItem.class, id );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ExportItem> getAllExportItem()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ExportItem.class );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ExportItem> getExportItem( int sheetNo, Integer reportId )
    {
        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session.createSQLQuery( "SELECT * from reportexcel_items where reportexcel_items.sheetno="
            + sheetNo + " and reportexcel_items.reportexcelid=" + reportId.intValue() );
        sqlQuery.addEntity( ExportItem.class );
        return sqlQuery.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Integer> getSheets( Integer reportId )
    {
        Session session = sessionFactory.getCurrentSession();
        SQLQuery sqlQuery = session
            .createSQLQuery( "select DISTINCT(sheetno) from reportexcel_items where reportexcel_items.reportexcelid="
                + reportId.intValue() + " order by sheetno" );

        return sqlQuery.list();
    }

    public void deleteMultiExportItem( Collection<Integer> ids )
    {
        String sql = "delete ExportItem d where d.id in (:ids)";

        Query query = sessionFactory.getCurrentSession().createQuery( sql );
        query.setParameterList( "ids", ids );

        query.executeUpdate();
    }

    // -------------------------------------------------------------------------
    // Data Entry Status
    // -------------------------------------------------------------------------

    public int countDataValueOfDataSet( DataSet dataSet, OrganisationUnit organisationUnit, Period period )
    {
        Session session = sessionFactory.getCurrentSession();

        Collection<Integer> deIds = new HashSet<Integer>();

        for ( DataElement element : dataSet.getDataElements() )
        {
            deIds.add( element.getId() );
        }

        String sql = "select count(*) from DataValue where sourceid=" + organisationUnit.getId();
        sql += " and periodid=" + period.getId();
        sql += " and dataelementid in (:deIds)";

        Query query = session.createQuery( sql );
        query.setParameterList( "deIds", deIds );

        Number nr = (Number) query.uniqueResult();

        return nr == null ? 0 : nr.intValue();
    }

    public void deleteDataEntryStatus( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( getDataEntryStatus( id ) );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataEntryStatus> getALLDataEntryStatus()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataEntryStatus.class );

        return criteria.list();
    }

    public DataEntryStatus getDataEntryStatus( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (DataEntryStatus) session.get( DataEntryStatus.class, id );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataEntryStatus> getDataEntryStatusDefault()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataEntryStatus.class );
        criteria.add( Restrictions.eq( "makeDefault", true ) );
        return criteria.list();
    }

    public int saveDataEntryStatus( DataEntryStatus arg0 )
    {
        Session session = sessionFactory.getCurrentSession();
        return (Integer) session.save( arg0 );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataEntryStatus> getDataEntryStatusByDataSets( Collection<DataSet> dataSets )
    {
        Collection<DataEntryStatus> result = new HashSet<DataEntryStatus>();

        Session session = sessionFactory.getCurrentSession();

        for ( DataSet dataSet : dataSets )
        {
            Criteria criteria = session.createCriteria( DataEntryStatus.class );
            criteria.add( Restrictions.eq( "dataSet", dataSet ) );
            result.addAll( criteria.list() );
        }

        return result;
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataEntryStatus> getDataEntryStatusDefaultByDataSets( Collection<DataSet> dataSets )
    {
        Collection<DataEntryStatus> result = new HashSet<DataEntryStatus>();

        Session session = sessionFactory.getCurrentSession();

        for ( DataSet dataSet : dataSets )
        {
            Criteria criteria = session.createCriteria( DataEntryStatus.class );
            criteria.add( Restrictions.eq( "dataSet", dataSet ) );
            criteria.add( Restrictions.eq( "makeDefault", true ) );
            result.addAll( criteria.list() );
        }

        return result;
    }

    public void updateDataEntryStatus( DataEntryStatus arg0 )
    {
        Session session = sessionFactory.getCurrentSession();
        session.update( arg0 );
    }

    public PeriodColumn getPeriodColumn( Integer id )
    {
        Session session = sessionFactory.getCurrentSession();
        return (PeriodColumn) session.get( PeriodColumn.class, id );
    }

    public void updatePeriodColumn( PeriodColumn periodColumn )
    {
        Session session = sessionFactory.getCurrentSession();
        session.update( periodColumn );
    }

    public void updateReportWithExcelTemplate( String curTemplateName, String newTemplateName )
    {
        Session session = sessionFactory.getCurrentSession();

        String hqlQuery = "update reportexcels set exceltemplate = :newName where exceltemplate = :curName";

        SQLQuery query = session.createSQLQuery( hqlQuery );

        query.setString( "newName", newTemplateName ).setString( "curName", curTemplateName );

        query.executeUpdate();
    }

    public ExportReport getExportReportByDataSet( DataSet dataSet )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ExportReport.class );

        criteria.createAlias( "dataSets", "d" );

        criteria.add( Restrictions.eq( "d.id", dataSet.getId() ) );

        return (ExportReport) criteria.uniqueResult();
    }
}
