package org.hisp.dhis.system.util;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.hisp.dhis.common.Weighted;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 */
public class WeightedPaginatedListTest
{
    private Weighted one = new One();
    private Weighted two = new Two();
    private Weighted three = new Three();

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void testNextPageA()
    {
        WeightedPaginatedList<Weighted> list = new WeightedPaginatedList<Weighted>( 
            Arrays.asList( one, one, three, three, three, one, one, one ), 3 );
        
        List<Weighted> page = list.nextPage();
        
        assertNotNull( page );
        assertEquals( 3, page.size() );
        assertTrue( page.contains( one ) );
        assertTrue( page.contains( three ) );
        
        page = list.nextPage();
        
        assertNotNull( page );
        assertEquals( 2, page.size() );
        assertTrue( page.contains( three ) );

        page = list.nextPage();
        
        assertNotNull( page );
        assertEquals( 3, page.size() );
        assertTrue( page.contains( one ) );
    }

    @Test
    public void testNextPageB()
    {
        WeightedPaginatedList<Weighted> list = new WeightedPaginatedList<Weighted>( 
            Arrays.asList( one, two, three, two, three, one, one, two, three, one ), 4 );
        
        List<Weighted> page = list.nextPage();
        
        assertNotNull( page );
        assertEquals( 3, page.size() );
        assertTrue( page.contains( one ) );
        assertTrue( page.contains( two ) );
        assertTrue( page.contains( three ) );

        page = list.nextPage();
        
        assertNotNull( page );
        assertEquals( 2, page.size() );
        assertTrue( page.contains( two ) );
        assertTrue( page.contains( three ) );
        
        page = list.nextPage();
        
        assertNotNull( page );
        assertEquals( 4, page.size() );
        assertTrue( page.contains( one ) );
        assertTrue( page.contains( two ) );
        assertTrue( page.contains( three ) );
        
        page = list.nextPage();
        
        assertNotNull( page );
        assertEquals( 1, page.size() );
        assertTrue( page.contains( one ) );
    }
    
    @Test
    public void testGetPages()
    {
        WeightedPaginatedList<Weighted> list = new WeightedPaginatedList<Weighted>( 
            Arrays.asList( three, three, one, one, one, one, two, two ), 3 );
        
        List<List<Weighted>> pages = list.getPages();
        
        List<Weighted> page = pages.get( 0 );

        assertNotNull( page );
        assertEquals( 2, page.size() );
        assertTrue( page.contains( three ) );
        
        page = pages.get( 1 );

        assertNotNull( page );
        assertEquals( 5, page.size() );
        assertTrue( page.contains( one ) );
        assertTrue( page.contains( two ) );
        
        page = pages.get( 2 );

        assertNotNull( page );
        assertEquals( 1, page.size() );
        assertTrue( page.contains( two ) );
    }

    // -------------------------------------------------------------------------
    // Test support classes
    // -------------------------------------------------------------------------

    class One implements Weighted
    {
        public int getWeight()
        {
            return 1;
        }
    }

    class Two implements Weighted
    {
        public int getWeight()
        {
            return 2;
        }
    }
    
    class Three implements Weighted
    {
        public int getWeight()
        {
            return 3;
        }
    }
}
