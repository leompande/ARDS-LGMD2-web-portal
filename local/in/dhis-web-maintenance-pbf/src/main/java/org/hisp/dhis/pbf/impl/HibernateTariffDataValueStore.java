package org.hisp.dhis.pbf.impl;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.pbf.api.TariffDataValue;
import org.hisp.dhis.pbf.api.TariffDataValueStore;
import org.hisp.dhis.period.Period;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class HibernateTariffDataValueStore implements TariffDataValueStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    // -------------------------------------------------------------------------
    // TariffDataValue
    // -------------------------------------------------------------------------
    
    @Override
    public void addTariffDataValue( TariffDataValue tariffDataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( tariffDataValue );
    }

    @Override
    public void updateTariffDataValue( TariffDataValue tariffDataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( tariffDataValue );
    }

    @Override
    public void deleteTariffDataValue( TariffDataValue tariffDataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( tariffDataValue );
    }

    @Override
    public TariffDataValue getTariffDataValue( OrganisationUnit organisationUnit, DataElement dataElement, DataSet dataSet, Date startDate, Date endDate )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( TariffDataValue.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );        
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );
        criteria.add( Restrictions.eq( "startDate", startDate ) );
        criteria.add( Restrictions.eq( "endDate", endDate ) );

        return (TariffDataValue) criteria.uniqueResult();
    }

    @Override
    public Collection<TariffDataValue> getAllTariffDataValues()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( TariffDataValue.class );

        return criteria.list();
    }

    @Override
    public Collection<TariffDataValue> getTariffDataValues( OrganisationUnit organisationUnit, DataSet dataSet )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( TariffDataValue.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "organisationUnitGroup", dataSet ) );

        return criteria.list();
    }

    @Override
    public Collection<TariffDataValue> getTariffDataValues( OrganisationUnit organisationUnit, DataElement dataElement )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( TariffDataValue.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.addOrder(Order.asc("dataSet"));

        return criteria.list();
    }

    public Map<Integer, Double> getTariffDataValues( OrganisationUnit organisationUnit, DataSet dataSet, Period period )
    {
        Map<Integer, Double> tariffDataValueMap = new HashMap<Integer, Double>();
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String curPeriod = simpleDateFormat.format( period.getEndDate() );
        
        try
        {
            String query = "SELECT dataelementid, value FROM tariffdatavalue " +
                            " WHERE " +
                                " organisationunitid = " + organisationUnit.getId() + " AND " +
                                " datasetid = " + dataSet.getId() + " AND " +
                                " startdate <= '" + curPeriod + "' AND "+ 
                                " enddate >= '" + curPeriod +"'";
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer dataElementId = rs.getInt( 1 );
                Double value = rs.getDouble( 2 );
                tariffDataValueMap.put( dataElementId, value );
            }
        }
        catch( Exception e )
        {
            System.out.println("In getTariffDataValues Exception :"+ e.getMessage() );
        }
        
        return tariffDataValueMap;
    }
}
