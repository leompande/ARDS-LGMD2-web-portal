package org.hisp.dhis.analytics;

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

import static org.hisp.dhis.common.NameableObjectUtils.getList;
import static org.hisp.dhis.dataelement.DataElement.AGGREGATION_OPERATOR_AVERAGE;
import static org.hisp.dhis.dataelement.DataElement.AGGREGATION_OPERATOR_SUM;
import static org.hisp.dhis.dataelement.DataElement.VALUE_TYPE_INT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.DhisConvenienceTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 */
public class DataQueryGroupsTest
    extends DhisConvenienceTest
{
    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------
    
    private DataElement deA;
    private DataElement deB;
    private DataElement deC;
    private DataElement deD;
    private DataElement deE;
    private DataElement deF;
    private DataElement deG;
    
    private OrganisationUnit ouA;
    private OrganisationUnit ouB;
    private OrganisationUnit ouC;
    private OrganisationUnit ouD;
    private OrganisationUnit ouE;

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------
    
    @Before
    public void before()
    {
        deA = createDataElement( 'A', VALUE_TYPE_INT, AGGREGATION_OPERATOR_SUM );
        deB = createDataElement( 'B', VALUE_TYPE_INT, AGGREGATION_OPERATOR_SUM );
        deC = createDataElement( 'C', VALUE_TYPE_INT, AGGREGATION_OPERATOR_SUM );
        deD = createDataElement( 'D', VALUE_TYPE_INT, AGGREGATION_OPERATOR_SUM );
        deE = createDataElement( 'E', VALUE_TYPE_INT, AGGREGATION_OPERATOR_SUM );
        deF = createDataElement( 'F', VALUE_TYPE_INT, AGGREGATION_OPERATOR_AVERAGE );
        deG = createDataElement( 'G', VALUE_TYPE_INT, AGGREGATION_OPERATOR_AVERAGE );
                
        ouA = createOrganisationUnit( 'A' );
        ouB = createOrganisationUnit( 'B' );
        ouC = createOrganisationUnit( 'C' );
        ouD = createOrganisationUnit( 'D' );
        ouE = createOrganisationUnit( 'E' );
    }
    
    @Test
    public void planQueryA()
    {
        DataQueryParams paramsA = new DataQueryParams();
        paramsA.setDataElements( getList( deA, deB ) );
        paramsA.setOrganisationUnits( getList( ouA, ouB, ouC, ouD, ouE ) );
        paramsA.setPeriods( getList( createPeriod( "2000Q1" ), createPeriod( "2000Q2" ), createPeriod( "2000Q3" ), createPeriod( "2000Q4" ), createPeriod(  "2001Q1" ), createPeriod( "2001Q2" ) ) );
        paramsA.setAggregationType( AggregationType.SUM );
        
        DataQueryParams paramsB = new DataQueryParams();
        paramsB.setDataElements( getList( deC, deD ) );
        paramsB.setOrganisationUnits( getList( ouA, ouB, ouC, ouD, ouE ) );
        paramsB.setPeriods( getList( createPeriod( "2000Q1" ), createPeriod( "2000Q2" ), createPeriod( "2000Q3" ), createPeriod( "2000Q4" ), createPeriod(  "2001Q1" ), createPeriod( "2001Q2" ) ) );
        paramsB.setAggregationType( AggregationType.SUM );
        
        DataQueryParams paramsC = new DataQueryParams();
        paramsC.setDataElements( getList( deE ) );
        paramsC.setOrganisationUnits( getList( ouA, ouB, ouC, ouD, ouE ) );
        paramsC.setPeriods( getList( createPeriod( "2000Q1" ), createPeriod( "2000Q2" ), createPeriod( "2000Q3" ), createPeriod( "2000Q4" ), createPeriod(  "2001Q1" ), createPeriod( "2001Q2" ) ) );
        paramsC.setAggregationType( AggregationType.SUM );
        
        DataQueryParams paramsD = new DataQueryParams();
        paramsD.setDataElements( getList( deF, deG ) );
        paramsD.setOrganisationUnits( getList( ouA, ouB, ouC, ouD, ouE ) );
        paramsD.setPeriods( getList( createPeriod( "2000Q1" ), createPeriod( "2000Q2" ), createPeriod( "2000Q3" ), createPeriod( "2000Q4" ), createPeriod(  "2001Q1" ), createPeriod( "2001Q2" ) ) );
        paramsD.setAggregationType( AggregationType.AVERAGE_INT );
        
        List<DataQueryParams> queries = new ArrayList<DataQueryParams>();
        queries.add( paramsA );
        queries.add( paramsB );
        queries.add( paramsC );
        queries.add( paramsD );        
        
        DataQueryGroups queryGroups = new DataQueryGroups( queries );
        
        assertEquals( 2, queryGroups.getSequentialQueries().size() );
        assertEquals( 4, queryGroups.getAllQueries().size() );
        assertEquals( 3, queryGroups.getLargestGroupSize() );
        assertTrue( queryGroups.isOptimal( 3 ) );
        assertTrue( queryGroups.isOptimal( 2 ) );
        assertFalse( queryGroups.isOptimal( 4 ) );
    }
}
