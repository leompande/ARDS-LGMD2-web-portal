package org.hisp.dhis.datavalue.hibernate;

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

import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueStore;
import org.hisp.dhis.datavalue.DeflatedDataValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodStore;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.objectmapper.DataValueRowMapper;
import org.hisp.dhis.system.objectmapper.DeflatedDataValueRowMapper;
import org.hisp.dhis.system.util.ConversionUtils;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.system.util.TextUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * @author Torgeir Lorange Ostby
 */
public class HibernateDataValueStore
    implements DataValueStore
{
    private static final Log log = LogFactory.getLog( HibernateDataValueStore.class );
    
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

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    // -------------------------------------------------------------------------
    // Basic DataValue
    // -------------------------------------------------------------------------

    public void addDataValue( DataValue dataValue )
    {
        dataValue.setPeriod( periodStore.reloadForceAddPeriod( dataValue.getPeriod() ) );

        Session session = sessionFactory.getCurrentSession();

        session.save( dataValue );
    }

    public void updateDataValue( DataValue dataValue )
    {
        dataValue.setPeriod( periodStore.reloadForceAddPeriod( dataValue.getPeriod() ) );

        Session session = sessionFactory.getCurrentSession();

        session.update( dataValue );
    }

    public void deleteDataValue( DataValue dataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( dataValue );
    }

    public int deleteDataValuesBySource( OrganisationUnit source )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "delete DataValue where source = :source" );
        query.setEntity( "source", source );

        return query.executeUpdate();
    }

    public int deleteDataValuesByDataElement( DataElement dataElement )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "delete DataValue where dataElement = :dataElement" );
        query.setEntity( "dataElement", dataElement );

        return query.executeUpdate();
    }

    public DataValue getDataValue( DataElement dataElement, Period period, OrganisationUnit source, 
        DataElementCategoryOptionCombo categoryOptionCombo, DataElementCategoryOptionCombo attributeOptionCombo )
    {
        Session session = sessionFactory.getCurrentSession();

        Period storedPeriod = periodStore.reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return null;
        }

        Criteria criteria = session.createCriteria( DataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.add( Restrictions.eq( "categoryOptionCombo", categoryOptionCombo ) );
        criteria.add( Restrictions.eq( "attributeOptionCombo", attributeOptionCombo ) );

        return (DataValue) criteria.uniqueResult();
    }
    
    public DataValue getDataValue( int dataElementId, int periodId, int sourceId, int categoryOptionComboId, int attributeOptionComboId )
    {
        final String sql =
            "SELECT * FROM datavalue " +
            "WHERE dataelementid = " + dataElementId + " " +
            "AND periodid = " + periodId + " " +
            "AND sourceid = " + sourceId + " " +
            "AND categoryoptioncomboid = " + categoryOptionComboId + " " +
            "AND attributeoptioncomboid = " + attributeOptionComboId;
        
        try
        {
            return jdbcTemplate.queryForObject( sql, new DataValueRowMapper() );
        }
        catch ( EmptyResultDataAccessException ex )
        {
            return null;
        }
    }
    
    // -------------------------------------------------------------------------
    // Collections of DataValues
    // -------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    public Collection<DataValue> getAllDataValues()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataValue.class );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataValue> getDataValues( OrganisationUnit source, Period period )
    {
        Period storedPeriod = periodStore.reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return Collections.emptySet();
        }

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataValue> getDataValues( OrganisationUnit source, DataElement dataElement )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataValue> getDataValues( Collection<OrganisationUnit> sources, DataElement dataElement )
    {
        Session session = sessionFactory.getCurrentSession();
        
        if ( sources == null || sources.isEmpty() )
        {
            return Collections.emptySet();
        }
        
        Criteria criteria = session.createCriteria( DataValue.class );
        criteria.add( Restrictions.in( "source", sources ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataValue> getDataValues( OrganisationUnit source, Period period, Collection<DataElement> dataElements )
    {
        Period storedPeriod = periodStore.reloadPeriod( period );

        if ( storedPeriod == null || dataElements == null || dataElements.isEmpty() )
        {
            return Collections.emptySet();
        }

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.add( Restrictions.in( "dataElement", dataElements ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataValue> getDataValues( OrganisationUnit source, Period period, 
        Collection<DataElement> dataElements, DataElementCategoryOptionCombo attributeOptionCombo )
    {
        Period storedPeriod = periodStore.reloadPeriod( period );

        if ( storedPeriod == null || dataElements == null || dataElements.isEmpty() )
        {
            return Collections.emptySet();
        }

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.add( Restrictions.in( "dataElement", dataElements ) );
        criteria.add( Restrictions.eq( "attributeOptionCombo", attributeOptionCombo ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataValue> getDataValues( OrganisationUnit source, Period period, Collection<DataElement> dataElements,
        Collection<DataElementCategoryOptionCombo> categoryOptionCombos )
    {
        Period storedPeriod = periodStore.reloadPeriod( period );

        if ( storedPeriod == null || dataElements == null || dataElements.isEmpty() || categoryOptionCombos == null || categoryOptionCombos.isEmpty() )
        {
            return Collections.emptySet();
        }

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.add( Restrictions.in( "dataElement", dataElements ) );
        criteria.add( Restrictions.in( "categoryOptionCombo", categoryOptionCombos ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataValue> getDataValues( DataElement dataElement, Period period,
        Collection<OrganisationUnit> sources )
    {
        Period storedPeriod = periodStore.reloadPeriod( period );

        if ( storedPeriod == null || sources == null || sources.isEmpty() )
        {
            return new HashSet<DataValue>();
        }

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataValue.class );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.add( Restrictions.in( "source", sources ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataValue> getDataValues( DataElement dataElement, Collection<Period> periods,
        Collection<OrganisationUnit> sources )
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

        if ( storedPeriods.isEmpty() || sources == null || sources.isEmpty() )
        {
            return new HashSet<DataValue>();
        }

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataValue.class );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.add( Restrictions.in( "period", storedPeriods ) );
        criteria.add( Restrictions.in( "source", sources ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataValue> getDataValues( DataElement dataElement, DataElementCategoryOptionCombo categoryOptionCombo,
        Collection<Period> periods, Collection<OrganisationUnit> sources )
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

        if ( storedPeriods.isEmpty() || sources == null || sources.isEmpty() )
        {
            return new HashSet<DataValue>();
        }

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataValue.class );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.add( Restrictions.eq( "categoryOptionCombo", categoryOptionCombo ) );
        criteria.add( Restrictions.in( "period", storedPeriods ) );
        criteria.add( Restrictions.in( "source", sources ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataValue> getDataValues( Collection<DataElementCategoryOptionCombo> categoryOptionCombos )
    {
        Session session = sessionFactory.getCurrentSession();

        if ( categoryOptionCombos == null || categoryOptionCombos.isEmpty() )
        {
            return new HashSet<DataValue>();
        }
        
        Criteria criteria = session.createCriteria( DataValue.class );
        criteria.add( Restrictions.in( "categoryOptionCombo", categoryOptionCombos ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataValue> getDataValues( DataElement dataElement )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataValue.class );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );

        return criteria.list();
    }

    @Override
    public DataValue getLatestDataValues( DataElement dataElement, PeriodType periodType,
        OrganisationUnit organisationUnit )
    {
        final String hsql = "SELECT v FROM DataValue v, Period p WHERE  v.dataElement =:dataElement "
            + " AND v.period=p AND p.periodType=:periodType AND v.source=:source ORDER BY p.endDate DESC";

        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( hsql );

        query.setParameter( "dataElement", dataElement );
        query.setParameter( "periodType", periodType );
        query.setParameter( "source", organisationUnit );
        
        query.setFirstResult( 0 );
        query.setMaxResults( 1 );

        return (DataValue) query.uniqueResult();
    }
        
    public int getDataValueCount( Date date )
    {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria( DataValue.class );
        
        criteria.add( Restrictions.ge( "timestamp", date ) );
        criteria.setProjection( Projections.rowCount() );

        Number rs = (Number) criteria.uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }
    
    public Map<DataElementOperand, Double> getDataValueMap( Collection<DataElement> dataElements, Period period, OrganisationUnit source )
    {
        Map<DataElementOperand, Double> map = new HashMap<DataElementOperand, Double>();
        
        if ( dataElements.isEmpty() )
        {
            return map;
        }
        
        final String sql = 
            "select de.uid, coc.uid, dv.value " +
            "from datavalue dv " +
            "join dataelement de on dv.dataelementid = de.dataelementid " +
            "join categoryoptioncombo coc on dv.categoryoptioncomboid = coc.categoryoptioncomboid " +
            "where dv.dataelementid in (" + TextUtils.getCommaDelimitedString( ConversionUtils.getIdentifiers( DataElement.class, dataElements ) ) + ") " +
            "and dv.periodid = " + period.getId() + " " +
            "and dv.sourceid = " + source.getId();
        
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );
        
        while ( rowSet.next() )
        {
            String dataElement = rowSet.getString( 1 );
            String categoryOptionCombo = rowSet.getString( 2 );
            Double value = MathUtils.parseDouble( rowSet.getString( 3 ) );
            
            if ( value != null )
            {
                map.put( new DataElementOperand( dataElement, categoryOptionCombo ), value );
            }
        }
        
        return map; 
    }

    public Map<DataElementOperand, Double> getDataValueMap( Collection<DataElement> dataElements, Date date, OrganisationUnit source,
    		Collection<PeriodType> periodTypes, Map<DataElementOperand, Date> lastUpdatedMap )
    {
        Map<DataElementOperand, Double> map = new HashMap<DataElementOperand, Double>();
        
        if ( dataElements.isEmpty() || periodTypes.isEmpty() )
        {
            return map;
        }
        
        final String sql = 
            "select de.uid, coc.uid, dv.value, dv.lastupdated, p.startdate, p.enddate " +
            "from datavalue dv " +
            "join dataelement de on dv.dataelementid = de.dataelementid " +
            "join categoryoptioncombo coc on dv.categoryoptioncomboid = coc.categoryoptioncomboid " +
            "join period p on p.periodid = dv.periodid " +
            "where dv.dataelementid in (" + TextUtils.getCommaDelimitedString( ConversionUtils.getIdentifiers( DataElement.class, dataElements ) ) + ") " +
            "and dv.sourceid = " + source.getId() + " " +
            "and p.startdate <= '" + DateUtils.getMediumDateString( date ) + "' " +
            "and p.enddate >= '" + DateUtils.getMediumDateString( date ) + "' " +
        	"and p.periodtypeid in (" + TextUtils.getCommaDelimitedString( ConversionUtils.getIdentifiers( PeriodType.class, periodTypes ) ) + ") ";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );
        
        Map<DataElementOperand, Long> checkForDuplicates = new HashMap<DataElementOperand, Long>();

        while ( rowSet.next() )
        {
            String dataElement = rowSet.getString( 1 );
            String categoryOptionCombo = rowSet.getString( 2 );
            Double value = MathUtils.parseDouble( rowSet.getString( 3 ) );
            Date lastUpdated = rowSet.getDate( 4 );
            Date periodStartDate = rowSet.getDate( 5 );
            Date periodEndDate = rowSet.getDate( 6 );
            long periodInterval = periodEndDate.getTime() - periodStartDate.getTime();

            log.trace( "row: " + dataElement + " = " + value + " [" + periodStartDate + " : " + periodEndDate + "]");

            if ( value != null )
            {
                DataElementOperand dataElementOperand = new DataElementOperand( dataElement, categoryOptionCombo );
                Long existingPeriodInterval = checkForDuplicates.get( dataElementOperand );
                
                if ( existingPeriodInterval != null && existingPeriodInterval < periodInterval )
                {
                    // Don't overwrite the previous value if for a shorter interval
                    continue; 
                }
                
                map.put( dataElementOperand, value );
                
                if ( lastUpdatedMap != null )
                {
                    lastUpdatedMap.put( dataElementOperand, lastUpdated );
                }
                
                checkForDuplicates.put( dataElementOperand, periodInterval );
            }
        }

        return map;
    }

    public Collection<DeflatedDataValue> getDeflatedDataValues( int dataElementId, int periodId, Collection<Integer> sourceIds )
    {
        final String sql =
            "SELECT * FROM datavalue " +
            "WHERE dataelementid = " + dataElementId + " " +
            "AND periodid = " + periodId + " " +
            "AND sourceid IN ( " + getCommaDelimitedString( sourceIds ) + " )";
        
        return jdbcTemplate.query( sql, new DeflatedDataValueRowMapper() );
    }
}
