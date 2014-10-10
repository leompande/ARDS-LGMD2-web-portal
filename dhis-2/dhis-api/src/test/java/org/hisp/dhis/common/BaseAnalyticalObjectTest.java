package org.hisp.dhis.common;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataset.DataSet;
import org.junit.Test;
import static org.junit.Assert.*;

/**
* @author Lars Helge Overland
*/
public class BaseAnalyticalObjectTest
{
    @Test
    public void testSortKeys()
    {
        Map<String, Double> valueMap = new HashMap<String, Double>();
        
        valueMap.put( "b1-a1-c1", 1d );
        valueMap.put( "a2-c2-b2", 2d );
        valueMap.put( "c3-b3-a3", 3d );
        valueMap.put( "a4-b4-c4", 4d );
        
        BaseAnalyticalObject.sortKeys( valueMap );
        
        assertEquals( 4, valueMap.size() );
        assertTrue( valueMap.containsKey( "a1-b1-c1" ) );
        assertTrue( valueMap.containsKey( "a2-b2-c2" ) );
        assertTrue( valueMap.containsKey( "a3-b3-c3" ) );
        assertTrue( valueMap.containsKey( "a4-b4-c4" ) );
        
        assertEquals( 1d, valueMap.get( "a1-b1-c1" ), 0.01 );
        assertEquals( 2d, valueMap.get( "a2-b2-c2" ), 0.01 );
        assertEquals( 3d, valueMap.get( "a3-b3-c3" ), 0.01 );
        assertEquals( 4d, valueMap.get( "a4-b4-c4" ), 0.01 );
        
        valueMap = new HashMap<String, Double>();
        
        valueMap.put( "b1", 1d );
        valueMap.put( "b2", 2d );

        BaseAnalyticalObject.sortKeys( valueMap );

        assertEquals( 2, valueMap.size() );
        assertTrue( valueMap.containsKey( "b1" ) );
        assertTrue( valueMap.containsKey( "b2" ) );
        
        assertEquals( 1d, valueMap.get( "b1" ), 0.01 );
        assertEquals( 2d, valueMap.get( "b2" ), 0.01 );

        valueMap = new HashMap<String, Double>();
        
        valueMap.put( null, 1d );
        
        BaseAnalyticalObject.sortKeys( valueMap );

        assertEquals( 0, valueMap.size() );
    }
    
    @Test
    public void testGetIdentifier()
    {
        DataElementGroup oA = new DataElementGroup();
        DataElementGroup oB = new DataElementGroup();
        DataElementGroup oC = new DataElementGroup();
        
        oA.setUid( "a1" );
        oB.setUid( "b1" );
        oC.setUid( "c1" );
        
        List<NameableObject> column = new ArrayList<NameableObject>();
        column.add( oC );
        column.add( oA );
        
        List<NameableObject> row = new ArrayList<NameableObject>();
        row.add( oB );
        
        assertEquals( "a1-b1-c1", BaseAnalyticalObject.getIdentifier( column, row ) );
        assertEquals( "b1", BaseAnalyticalObject.getIdentifier( new ArrayList<NameableObject>(), row ) );
        assertEquals( "b1", BaseAnalyticalObject.getIdentifier( null, row ) );
    }
    
    @Test
    public void testEquals()
    {
        DataElement deA = new DataElement();
        deA.setUid( "A" );
        deA.setCode( "A" );
        deA.setName( "A" );

        DataElement deB = new DataElement();
        deB.setUid( "B" );
        deB.setCode( "B" );
        deB.setName( "B" );

        DataElement deC = new DataElement();
        deC.setUid( "A" );
        deC.setCode( "A" );
        deC.setName( "A" );
        
        DataSet dsA = new DataSet();
        dsA.setUid( "A" );
        dsA.setCode( "A" );
        dsA.setName( "A" );

        DataSet dsD = new DataSet();
        dsD.setUid( "D" );
        dsD.setCode( "D" );
        dsD.setName( "D" );
        
        assertTrue( deA.equals( deC ) );
        
        assertFalse( deA.equals( deB ) );
        assertFalse( deA.equals( dsA ) );
        assertFalse( deA.equals( dsD ) );
        assertFalse( dsA.equals( dsD ) );        
    }
}
