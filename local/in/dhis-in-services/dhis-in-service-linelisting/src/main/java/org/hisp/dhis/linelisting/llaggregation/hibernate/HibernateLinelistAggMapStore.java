package org.hisp.dhis.linelisting.llaggregation.hibernate;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.linelisting.llaggregation.LinelistAggMapStore;
import org.hisp.dhis.linelisting.llaggregation.LinelistAggregationMapping;
import org.springframework.jdbc.core.JdbcTemplate;

public class HibernateLinelistAggMapStore implements LinelistAggMapStore
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
    // LinelistAggregationMapping
    // -------------------------------------------------------------------------

    public void addLineListAggregationMapping( LinelistAggregationMapping llAggregationMapping )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( llAggregationMapping );
    }

    public void deleteLinelistAggregationMapping( LinelistAggregationMapping llAggregationMapping )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( llAggregationMapping );
    }

    public void updateLinelistAggregationMapping( LinelistAggregationMapping llAggregationMapping )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( llAggregationMapping );
    }
    
    public LinelistAggregationMapping getLinelistAggregationMappingByOptionCombo( DataElement dataElement, DataElementCategoryOptionCombo optionCombo )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LinelistAggregationMapping.class );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.add( Restrictions.eq( "optionCombo", optionCombo ) );

        return (LinelistAggregationMapping) criteria.uniqueResult();
    }
    
    public int executeAggregationQuery( String query )
    {
        return  jdbcTemplate.queryForInt( query );
    }

}
