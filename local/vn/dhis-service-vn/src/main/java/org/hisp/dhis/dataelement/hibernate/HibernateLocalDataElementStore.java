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

package org.hisp.dhis.dataelement.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataelement.LocalDataElementStore;
import org.hisp.dhis.dataset.DataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * @author Chau Thu Tran
 * 
 * @version $HibernateLocalDataElementStore.java Mar 23, 2012 4:08:56 PM$
 */
public class HibernateLocalDataElementStore
    extends HibernateIdentifiableObjectStore<DataElement>
    implements LocalDataElementStore
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataElementService dataElementService;

    @SuppressWarnings( "unchecked" )
    public Collection<DataElement> getByAttributeValue( Attribute attribute, String value )
    {
        return getCriteria().createAlias( "attributeValues", "attributeValue" ).add(
            Restrictions.eq( "attributeValue.attribute", attribute ) ).add(
            Restrictions.eq( "attributeValue.value", value ).ignoreCase() ).list();
    }

    @Override
    public int getDataElementCount( Integer dataElementId, Integer attributeId, String value )
    {
        Number rs = (Number) getCriteria().add( Restrictions.eq( "id", dataElementId ) ).createAlias(
            "attributeValues", "attributeValue" ).add( Restrictions.eq( "attributeValue.attribute.id", attributeId ) )
            .add( Restrictions.eq( "attributeValue.value", value ).ignoreCase() )
            .setProjection( Projections.rowCount() ).uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }

    @Override
    public Collection<DataElement> get( DataSet dataSet, String value )
    {
        List<DataElement> result = new ArrayList<DataElement>();
        try
        {
            String sql = "select de.dataelementid, de.name, de.formname from dataelement de "
                + "join datasetmembers dsm on de.dataelementid = dsm.dataelementid "
                + "join dataelementattributevalues deav on deav.dataelementid = dsm.dataelementid "
                + "join attributevalue av on av.attributevalueid = deav.attributevalueid "
                + "where dsm.datasetid = " + dataSet.getId() + " and av.value='" + value + "'";
            
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );

            while ( rowSet.next() )
            {
                result.add( dataElementService.getDataElement( rowSet.getInt( 1 ) ) );
            }

            return result;
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return new ArrayList<DataElement>();
        }
    }
    
    @Override
    public Map<String, List<Integer>> get(DataSet dataSet, List<String> values)
    {
        StringBuffer sql = new StringBuffer();

        int i = 0;
        for ( String value : values )
        {
            i++;

            if ( value != null && !value.trim().isEmpty() )
            {
                sql.append( "select av.value, de.dataelementid, de.name, de.formname from dataelement de " );
                sql.append( "join datasetmembers dsm on de.dataelementid = dsm.dataelementid " );
                sql.append( "join dataelementattributevalues deav on deav.dataelementid = dsm.dataelementid " );
                sql.append( "join attributevalue av on av.attributevalueid = deav.attributevalueid " );
                sql.append( "where dsm.datasetid = " + dataSet.getId() + " and av.value='" + value + "'" );
                sql.append( i == values.size() ? " ORDER BY name" : " UNION " );
            }
        }

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql.toString() );

        sql = null;
        String key = null;

        Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();

        while ( rowSet.next() )
        {
            key = rowSet.getString( 1 );

            if ( !map.containsKey( key ) )
            {
                List<Integer> ids = new ArrayList<Integer>();
                ids.add( rowSet.getInt( 2 ) );

                map.put( key, ids );
            }
            else
            {
                map.get( key ).add( rowSet.getInt( 2 ) );
            }
        }
        
        rowSet = null;
        key = null;
        
        return map;
    }
}
