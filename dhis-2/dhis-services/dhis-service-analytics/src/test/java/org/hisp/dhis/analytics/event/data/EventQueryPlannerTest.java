package org.hisp.dhis.analytics.event.data;

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

import java.util.Arrays;
import java.util.List;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.analytics.event.EventQueryParams;
import org.hisp.dhis.analytics.event.EventQueryPlanner;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Cal;
import org.hisp.dhis.program.Program;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 */
public class EventQueryPlannerTest
    extends DhisSpringTest
{
    private Program prA;
    private OrganisationUnit ouA;
    private OrganisationUnit ouB;
    
    @Autowired
    private EventQueryPlanner queryPlanner;
    
    @Override
    public void setUpTest()
    {
        prA = new Program();
        prA.setUid( "programuidA" );
        
        ouA = createOrganisationUnit( 'A' );
        ouB = createOrganisationUnit( 'B' );
        
        ouA.setLevel( 1 );
        ouB.setLevel( 2 );        
    }
    
    @Test
    public void testPlanQueryA()
    {        
        EventQueryParams params = new EventQueryParams();
        params.setProgram( prA );
        params.setStartDate( new Cal( 2010, 6, 1 ).time() );
        params.setEndDate( new Cal( 2012, 3, 20 ).time() );
        params.setOrganisationUnits( Arrays.asList( ouA ) );
        
        List<EventQueryParams> queries = queryPlanner.planQuery( params, null );
        
        assertEquals( 3, queries.size() );
        
        assertEquals( new Cal( 2010, 6, 1 ).time(), queries.get( 0 ).getStartDate() );
        assertEquals( new Cal( 2010, 12, 31 ).time(), queries.get( 0 ).getEndDate() );
        assertEquals( new Cal( 2011, 1, 1 ).time(), queries.get( 1 ).getStartDate() );
        assertEquals( new Cal( 2011, 12, 31 ).time(), queries.get( 1 ).getEndDate() );
        assertEquals( new Cal( 2012, 1, 1 ).time(), queries.get( 2 ).getStartDate() );
        assertEquals( new Cal( 2012, 3, 20 ).time(), queries.get( 2 ).getEndDate() );
        
        assertEquals( "analytics_event_2010_programuidA", queries.get( 0 ).getPartitions().getSinglePartition() );
        assertEquals( "analytics_event_2011_programuidA", queries.get( 1 ).getPartitions().getSinglePartition() );
        assertEquals( "analytics_event_2012_programuidA", queries.get( 2 ).getPartitions().getSinglePartition() );
    }

    @Test
    public void testPlanQueryB()
    {        
        EventQueryParams params = new EventQueryParams();
        params.setProgram( prA );
        params.setStartDate( new Cal( 2010, 3, 1 ).time() );
        params.setEndDate( new Cal( 2010, 9, 20 ).time() );
        params.setOrganisationUnits( Arrays.asList( ouA ) );
        
        List<EventQueryParams> queries = queryPlanner.planQuery( params, null );

        assertEquals( 1, queries.size() );
        
        assertEquals( new Cal( 2010, 3, 1 ).time(), queries.get( 0 ).getStartDate() );
        assertEquals( new Cal( 2010, 9, 20 ).time(), queries.get( 0 ).getEndDate() );

        assertEquals( "analytics_event_2010_programuidA", queries.get( 0 ).getPartitions().getSinglePartition() );
    }    

    @Test
    public void testPlanQueryC()
    {        
        EventQueryParams params = new EventQueryParams();
        params.setProgram( prA );
        params.setStartDate( new Cal( 2010, 6, 1 ).time() );
        params.setEndDate( new Cal( 2012, 3, 20 ).time() );
        params.setOrganisationUnits( Arrays.asList( ouA, ouB ) );
        
        List<EventQueryParams> queries = queryPlanner.planQuery( params, null );
        
        assertEquals( 6, queries.size() );
    }
}
