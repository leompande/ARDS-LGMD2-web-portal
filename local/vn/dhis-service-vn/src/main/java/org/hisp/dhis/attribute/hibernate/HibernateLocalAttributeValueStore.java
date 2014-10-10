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

package org.hisp.dhis.attribute.hibernate;

import java.util.Collection;
import java.util.HashSet;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.attribute.LocalAttributeValueStore;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * @author Chau Thu Tran
 * 
 * @version $HibernateLocalAttributeValueStore.java Mar 24, 2012 8:30:37 AM$
 */
public class HibernateLocalAttributeValueStore
    extends HibernateGenericStore<AttributeValue>
    implements LocalAttributeValueStore
{
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<AttributeValue> getByAttribute( Attribute attribute )
    {
        return getCriteria().add( Restrictions.eq( "attribute", attribute ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<String> getDistinctValuesByAttribute( Attribute attribute )
    {
        return getCriteria().add( Restrictions.eq( "attribute", attribute ) ).add( Restrictions.ne( "value", "" ) )
            .setProjection( Projections.distinct( Projections.property( "value" ) ) ).addOrder( Order.asc( "value" ) )
            .list();
    }

    public boolean hasAttributesByDataSet( DataSet dataSet )
    {
        String sql = "select count(*) from datasetmembers dsm "
            + "inner join dataelementattributevalues deav on deav.dataelementid = dsm.dataelementid "
            + "inner join attributevalue av on av.attributevalueid = deav.attributevalueid "
            + "where dsm.datasetid = " + dataSet.getId();

        return (jdbcTemplate.queryForInt( sql ) > 0) ? true : false;
    }

    public Collection<String> getByDataSet( DataSet dataSet )
    {
        Collection<String> result = new HashSet<String>();

        try
        {
            String sql = "select distinct(av.value) from datasetmembers dsm "
                + "inner join dataelementattributevalues deav on deav.dataelementid = dsm.dataelementid "
                + "inner join attributevalue av on av.attributevalueid = deav.attributevalueid "
                + "inner join attribute a on av.attributeid = a.attributeid "
                + "where dsm.datasetid = " + dataSet.getId() + " "
                + "and a.name <> 'attribute_column_index'";

            SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );

            while ( rowSet.next() )
            {
                result.add( rowSet.getString( 1 ) );
            }    
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        
        return result;
    }
}
