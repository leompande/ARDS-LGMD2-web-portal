package org.hisp.dhis.analytics.table;

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

import static org.hisp.dhis.DhisConvenienceTest.createPeriod;
import static org.hisp.dhis.analytics.AnalyticsTableManager.ANALYTICS_TABLE_NAME;
import static org.hisp.dhis.common.NameableObjectUtils.getList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.analytics.Partitions;
import org.hisp.dhis.common.ListMap;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.period.Cal;
import org.hisp.dhis.period.Period;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 */
public class PartitionUtilsTest
{
    private static final String TBL = ANALYTICS_TABLE_NAME;
    
    @Test
    public void testGetPartitions()
    {
        assertEquals( new Partitions().add( TBL + "_2000" ), PartitionUtils.getPartitions( createPeriod( "200001" ), TBL, null, null ) );
        assertEquals( new Partitions().add( TBL + "_2001" ), PartitionUtils.getPartitions( createPeriod( "200110" ), TBL, null, null ) );
        assertEquals( new Partitions().add( TBL + "_2002" ), PartitionUtils.getPartitions( createPeriod( "2002Q2" ), TBL, null, null ) );
        assertEquals( new Partitions().add( TBL + "_2003" ), PartitionUtils.getPartitions( createPeriod( "2003S2" ), TBL, null, null ) );
        
        assertEquals( new Partitions().add( TBL + "_2000" ).add( TBL + "_2001" ), PartitionUtils.getPartitions( createPeriod( "2000July" ), TBL, null, null ) );
        assertEquals( new Partitions().add( TBL + "_2001" ).add( TBL + "_2002" ), PartitionUtils.getPartitions( createPeriod( "2001April" ), TBL, null, null ) );
    }

    @Test
    public void getGetPartitionsMultiplePeriods()
    {
        List<NameableObject> periods = new ArrayList<NameableObject>();
        periods.add( createPeriod( "200011" ) );
        periods.add( createPeriod( "200105" ) );
        periods.add( createPeriod( "200108" ) );
        
        assertEquals( new Partitions().add( TBL + "_2000" ).add( TBL + "_2001" ), PartitionUtils.getPartitions( periods, TBL, null ) );
    }

    @Test
    public void getGetPartitionsLongPeriods()
    {
        Period period = new Period();
        period.setStartDate( new Cal( 2008, 3, 1 ).time() );
        period.setEndDate( new Cal( 2011, 7, 1 ).time() );
        
        Partitions expected = new Partitions().add( TBL + "_2008" ).add( TBL + "_2009" ).add( TBL + "_2010" ).add( TBL + "_2011" );
        
        assertEquals( expected, PartitionUtils.getPartitions( period, TBL, null, null ) );
        
        period = new Period();
        period.setStartDate( new Cal( 2009, 8, 1 ).time() );
        period.setEndDate( new Cal( 2010, 2, 1 ).time() );
        
        expected = new Partitions().add( TBL + "_2009" ).add( TBL + "_2010" );
        
        assertEquals( expected, PartitionUtils.getPartitions( period, TBL, null, null ) );
    }

    @Test
    public void getGetPartitionsLongPeriodsPrune()
    {
        Period period = new Period();
        period.setStartDate( new Cal( 2008, 3, 1 ).time() );
        period.setEndDate( new Cal( 2011, 7, 1 ).time() );
        
        Partitions expected = new Partitions().add( TBL + "_2008" ).add( TBL + "_2009" ).add( TBL + "_2010" ).add( TBL + "_2011" );
        
        assertEquals( expected, PartitionUtils.getPartitions( period, TBL, null, null ) );
        
        List<String> validPartitions = new ArrayList<String>();
        validPartitions.add( TBL + "_2008" );
        validPartitions.add( TBL + "_2010" );
        
        expected = new Partitions().add( TBL + "_2008" ).add( TBL + "_2010" );
        
        assertEquals( expected, PartitionUtils.getPartitions( period, TBL, null, validPartitions ) );
    }
    
    @Test
    public void testGetTablePeriodMapA()
    {        
        ListMap<Partitions, NameableObject> map = PartitionUtils.getPartitionPeriodMap( getList( 
            createPeriod( "2000S1" ), createPeriod( "2000S2" ), createPeriod( "2001S1" ), createPeriod( "2001S2" ), createPeriod( "2002S1" ) ), TBL, null );
        
        assertEquals( 3, map.size() );
        
        assertTrue( map.keySet().contains( new Partitions().add( TBL + "_2000" ) ) );
        assertTrue( map.keySet().contains( new Partitions().add( TBL + "_2001" ) ) );
        assertTrue( map.keySet().contains( new Partitions().add( TBL + "_2002" ) ) );
        
        assertEquals( 2, map.get( new Partitions().add( TBL + "_2000" ) ).size() );
        assertEquals( 2, map.get( new Partitions().add( TBL + "_2001" ) ).size() );
        assertEquals( 1, map.get( new Partitions().add( TBL + "_2002" ) ).size() );
    }
    
    @Test
    public void testGetTablePeriodMapB()
    {        
        ListMap<Partitions, NameableObject> map = PartitionUtils.getPartitionPeriodMap( getList( 
            createPeriod( "2000April" ), createPeriod( "2000" ), createPeriod( "2001" ), createPeriod( "2001Oct" ), createPeriod( "2002Oct" ) ), TBL, null );

        assertEquals( 5, map.size() );
        
        assertTrue( map.keySet().contains( new Partitions().add( TBL + "_2000" ) ) );
        assertTrue( map.keySet().contains( new Partitions().add( TBL + "_2001" ) ) );
        assertTrue( map.keySet().contains( new Partitions().add( TBL + "_2000" ).add( TBL + "_2001" ) ) );
        assertTrue( map.keySet().contains( new Partitions().add( TBL + "_2001" ).add( TBL + "_2002" ) ) );
        assertTrue( map.keySet().contains( new Partitions().add( TBL + "_2002" ).add( TBL + "_2003" ) ) );
    }
}
