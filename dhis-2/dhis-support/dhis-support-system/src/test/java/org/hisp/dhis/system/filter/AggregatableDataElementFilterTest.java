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

import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.DhisConvenienceTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.system.util.FilterUtils;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class AggregatableDataElementFilterTest
    extends DhisConvenienceTest
{
    @Test
    public void filter()
    {
        DataElement elementA = createDataElement( 'A' );
        DataElement elementB = createDataElement( 'B' );
        DataElement elementC = createDataElement( 'C' );
        DataElement elementD = createDataElement( 'D' );
        DataElement elementE = createDataElement( 'E' );
        DataElement elementF = createDataElement( 'F' );
        
        elementA.setType( DataElement.VALUE_TYPE_BOOL );
        elementB.setType( DataElement.VALUE_TYPE_INT );
        elementC.setType( DataElement.VALUE_TYPE_STRING );
        elementD.setType( DataElement.VALUE_TYPE_BOOL );
        elementE.setType( DataElement.VALUE_TYPE_INT );
        elementF.setType( DataElement.VALUE_TYPE_STRING );        
        
        Set<DataElement> set = new HashSet<DataElement>();
        
        set.add( elementA );
        set.add( elementB );
        set.add( elementC );
        set.add( elementD );
        set.add( elementE );
        set.add( elementF );
        
        Set<DataElement> reference = new HashSet<DataElement>();
        
        reference.add( elementA );
        reference.add( elementB );
        reference.add( elementD );
        reference.add( elementE );
        
        FilterUtils.filter( set, new AggregatableDataElementFilter() );
        
        assertEquals( reference.size(), set.size() );
        assertEquals( reference, set );
    }
}
