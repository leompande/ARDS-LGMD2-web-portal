package org.hisp.dhis.dataentrystatus.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.amplecode.quick.StatementManager;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataentrystatus.DataEntryStatus;
import org.hisp.dhis.dataentrystatus.DataEntryStatusStore;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodStore;


public class HibernateDataEntryStatusStore implements DataEntryStatusStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private PeriodStore periodStore;

    public void setPeriodStore( PeriodStore periodStore )
    {
        this.periodStore = periodStore;
    }
    
    // -------------------------------------------------------------------------
    // Basic DataValue
    // -------------------------------------------------------------------------

    public void addDataEntryStatus( DataEntryStatus dataEntryStatus )
    {
        dataEntryStatus.setPeriod( periodStore.reloadForceAddPeriod( dataEntryStatus.getPeriod() ) );

        Session session = sessionFactory.getCurrentSession();

        session.save( dataEntryStatus );
    }
    
    public void updateDataEntryStatus( DataEntryStatus dataEntryStatus )
    {
        dataEntryStatus.setPeriod( periodStore.reloadForceAddPeriod( dataEntryStatus.getPeriod() ) );

        Session session = sessionFactory.getCurrentSession();

        session.update( dataEntryStatus );
    }

    public void deleteDataEntryStatus( DataEntryStatus dataEntryStatus )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( dataEntryStatus );
    }

    public int deleteDataEntryStatusBySource( OrganisationUnit organisationUnit )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "delete value where organisationunit = :organisationUnit" );
        query.setEntity( "organisationunit", organisationUnit );

        return query.executeUpdate();
    }

    public int deleteDataEntryStatusByDataSet( DataSet dataSet )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "delete DataValue where dataset = :dataSet" );
        query.setEntity( "dataset", dataSet );

        return query.executeUpdate();
    }
    
    
    public DataEntryStatus getDataEntryStatusValue( DataSet dataSet, OrganisationUnit organisationUnit, Period period, String includeZero )
    {
        Session session = sessionFactory.getCurrentSession();

        Period storedPeriod = periodStore.reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return null;
        }

        Criteria criteria = session.createCriteria( DataEntryStatus.class );
        criteria.add( Restrictions.eq( "dataset", dataSet ) );
        criteria.add( Restrictions.eq( "organisationunit", organisationUnit ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.add( Restrictions.eq( "includeZero", includeZero ) );
        
        return (DataEntryStatus) criteria.uniqueResult();
    }

    public String getValue( int dataSetId,  int organisationUnitId, int periodId ,String includeZero )
    {
        final String sql = "SELECT value " + "FROM dataentrystatus " + "WHERE datasetid='" + dataSetId + "' "
            + "AND organisationunitid='" + organisationUnitId + "' " + "AND periodid='" + periodId + "' " + "AND includezero ='" + includeZero + " '" ;
        
        return statementManager.getHolder().queryForString( sql );
    }
    
    
    // -------------------------------------------------------------------------
    // Collections of DataEntryStatus
    // -------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    public Collection<DataEntryStatus> getAllDataEntryStatusValues()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataEntryStatus.class );

        return criteria.list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<DataEntryStatus> getDataEntryStatusValues( OrganisationUnit organisationUnit, Period period )
    {
        Period storedPeriod = periodStore.reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return Collections.emptySet();
        }

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataEntryStatus.class );
        criteria.add( Restrictions.eq( "organisationunit", organisationUnit ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );

        return criteria.list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<DataEntryStatus> getDataEntryStatusValues( DataSet dataSet, OrganisationUnit organisationUnit )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataEntryStatus.class );
        criteria.add( Restrictions.eq( "dataset", dataSet ) );
        criteria.add( Restrictions.eq( "organisationunit", organisationUnit ) );
        
        return criteria.list();
    }
    
    
    @SuppressWarnings( "unchecked" )
    public Collection<DataEntryStatus> getDataEntryStatusValues( DataSet dataSet )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataEntryStatus.class );
        criteria.add( Restrictions.eq( "dataset", dataSet ) );

        return criteria.list();
    }
    
    
    @SuppressWarnings( "unchecked" )
    public Collection<DataEntryStatus> getDataEntryStatusValues( DataSet dataSet, Collection<OrganisationUnit> organisationUnits  )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataEntryStatus.class );
        criteria.add( Restrictions.eq( "dataset", dataSet ) );
        criteria.add( Restrictions.in( "organisationunit", organisationUnits ) );
        
        return criteria.list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<DataEntryStatus> getDataEntryStatusValues( Collection<DataSet> dataSets, OrganisationUnit organisationUnit, Period period )
    {
        Period storedPeriod = periodStore.reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return Collections.emptySet();
        }

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataEntryStatus.class );
        criteria.add( Restrictions.in( "dataset", dataSets ) );
        criteria.add( Restrictions.eq( "organisationunit", organisationUnit ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        
        return criteria.list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<DataEntryStatus> getDataEntryStatusValues( DataSet dataSet, Collection<OrganisationUnit> organisationUnits, Period period )
    {
        Period storedPeriod = periodStore.reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return new HashSet<DataEntryStatus>();
        }

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataEntryStatus.class );
        criteria.add( Restrictions.eq( "dataset", dataSet ) );
        criteria.add( Restrictions.in( "organisationunit", organisationUnits ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.addOrder( Order.asc( "organisationunit" ));
        return criteria.list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<DataEntryStatus> getDataEntryStatusValues( DataSet dataSet, Collection<OrganisationUnit> organisationUnits, Collection<Period> periods )
    {
        Collection<Period> storedPeriods = new ArrayList<Period>();

        for ( Period period : periods )
        {
            Period storedPeriod = periodStore.reloadPeriod( period );

            if ( storedPeriod != null )
            {
                storedPeriods.add( storedPeriod );
            }
        }

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataEntryStatus.class );
        criteria.add( Restrictions.eq( "dataset", dataSet ) );
        criteria.add( Restrictions.in( "organisationunit", organisationUnits ) );
        criteria.add( Restrictions.in( "period", storedPeriods ) );
        criteria.addOrder( Order.asc( "organisationunit" ));
        criteria.addOrder( Order.asc( "period" ));
        return criteria.list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<DataEntryStatus> getDataEntryStatusValues( Collection<DataSet> dataSets )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataValue.class );
        criteria.add( Restrictions.in( "dataset", dataSets ) );

        return criteria.list();
    }
}
