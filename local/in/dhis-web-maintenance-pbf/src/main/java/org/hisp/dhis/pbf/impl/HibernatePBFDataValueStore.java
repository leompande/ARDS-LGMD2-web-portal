package org.hisp.dhis.pbf.impl;

import java.util.Collection;
import java.util.Collections;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.pbf.api.PBFDataValue;
import org.hisp.dhis.pbf.api.PBFDataValueStore;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodStore;

public class HibernatePBFDataValueStore implements PBFDataValueStore
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
    // PBFDataValue
    // -------------------------------------------------------------------------

	@Override
	public void addPBFDataValue( PBFDataValue pbfDataValue ) 
	{
		pbfDataValue.setPeriod( periodStore.reloadForceAddPeriod( pbfDataValue.getPeriod() ) );

        Session session = sessionFactory.getCurrentSession();

        session.save( pbfDataValue );
	}

	@Override
	public void updatePBFDataValue( PBFDataValue pbfDataValue ) 
	{
		pbfDataValue.setPeriod( periodStore.reloadForceAddPeriod( pbfDataValue.getPeriod() ) );

        Session session = sessionFactory.getCurrentSession();

        session.update( pbfDataValue );
	}

	@Override
	public void deletePBFDataValue( PBFDataValue pbfDataValue ) 
	{
        Session session = sessionFactory.getCurrentSession();

        session.delete( pbfDataValue );
	}


	@Override
	public PBFDataValue getPBFDataValue( OrganisationUnit organisationUnit, DataSet dataSet, Period period, DataElement dataElement ) 
	{
		Period storedPeriod = periodStore.reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return null;
        }

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( PBFDataValue.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );

        return ( PBFDataValue ) criteria.uniqueResult();
	}

	@Override
	public Collection<PBFDataValue> getPBFDataValues( OrganisationUnit organisationUnit, DataSet dataSet, Period period ) 
	{
		Period storedPeriod = periodStore.reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return Collections.emptySet();
        }

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( PBFDataValue.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );

        return criteria.list();
	}

}
