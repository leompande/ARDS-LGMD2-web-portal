package org.hisp.dhis.reports.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.PeriodStore;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportStore;
import org.hisp.dhis.reports.Report_in;

public class HibernateReportStore
    implements ReportStore
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    private PeriodStore periodStore;

    public void setPeriodStore( PeriodStore periodStore )
    {
        this.periodStore = periodStore;
    }

    // -------------------------------------------------------------------------
    // Report_in
    // -------------------------------------------------------------------------

    public int addReport( Report_in report )
    {
        PeriodType periodType = periodStore.getPeriodType( report.getPeriodType().getClass() );

        report.setPeriodType( periodType );

        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( report );
    }

    public void deleteReport( Report_in report )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( report );
    }

    public void updateReport( Report_in report )
    {
        PeriodType periodType = periodStore.getPeriodType( report.getPeriodType().getClass() );

        report.setPeriodType( periodType );

        Session session = sessionFactory.getCurrentSession();

        session.update( report );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Report_in> getAllReports()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( Report_in.class ).list();
    }

    public Report_in getReport( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Report_in) session.get( Report_in.class, id );
    }

    public Report_in getReportByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Report_in.class );
        criteria.add( Restrictions.eq( "name", name ) );

        return (Report_in) criteria.uniqueResult();

    }

    @SuppressWarnings( "unchecked" )
    public Collection<Report_in> getReportBySource( OrganisationUnit source )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Report_in.class );
        criteria.createAlias( "sources", "s" );
        criteria.add( Restrictions.eq( "s.id", source.getId() ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Report_in> getReportsByPeriodAndReportType( PeriodType periodType, String reportType )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Report_in.class );

        PeriodType selPeriodType = periodStore.getPeriodType( periodType.getClass() );
        criteria.add( Restrictions.eq( "periodType", selPeriodType ) );
        
        criteria.add( Restrictions.eq( "reportType", reportType ) );

        return criteria.list();

    }

    @SuppressWarnings( "unchecked" )
    public Collection<Report_in> getReportsByPeriodType( PeriodType periodType )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Report_in.class );

        PeriodType selPeriodType = periodStore.getPeriodType( periodType.getClass() );
        criteria.add( Restrictions.eq( "periodType", selPeriodType ) );

        return criteria.list();

    }

    @SuppressWarnings( "unchecked" )
    public Collection<Report_in> getReportsByReportType( String reportType )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Report_in.class );
        criteria.add( Restrictions.eq( "reportType", reportType ) );

        return criteria.list();

    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<Report_in> getReportsByPeriodSourceAndReportType( PeriodType periodType, OrganisationUnit source, String reportType )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Report_in.class );
        
        PeriodType selPeriodType = periodStore.getPeriodType( periodType.getClass() );
        criteria.add( Restrictions.eq( "periodType", selPeriodType ) );
        
        criteria.createAlias( "sources", "s" );
        criteria.add( Restrictions.eq( "s.id", source.getId() ) );

        criteria.add( Restrictions.eq( "reportType", reportType ) );

        return criteria.list();
    }
}
