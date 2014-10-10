package org.hisp.dhis.system.filter;

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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.DhisConvenienceTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.system.util.FilterUtils;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataElementWithAggregationFilterTest
    extends DhisConvenienceTest
{
    @Test
    public void filter()
    {
        DataElement elementA = createDataElement( 'A' );
        DataElement elementB = createDataElement( 'B' );
        DataElement elementC = createDataElement( 'C' );
        DataElement elementD = createDataElement( 'D' );
        
        DataSet dataSetA = createDataSet( 'A', new MonthlyPeriodType() );
        dataSetA.setSkipAggregation( false );
        dataSetA.addDataElement( elementA );
        dataSetA.addDataElement( elementC );
        
        DataSet dataSetB = createDataSet( 'A', new MonthlyPeriodType() );
        dataSetB.setSkipAggregation( true );
        dataSetB.addDataElement( elementB );
        dataSetB.addDataElement( elementD );
        
        List<DataElement> list = new ArrayList<DataElement>();
        list.add( elementA );
        list.add( elementB );
        list.add( elementC );
        list.add( elementD );
        
        FilterUtils.filter( list, new DataElementWithAggregationFilter() );
        
        assertEquals( 2, list.size() );
        assertTrue( list.contains( elementA ) );
        assertTrue( list.contains( elementC ) );
    }
}
