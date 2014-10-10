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
import static org.hisp.dhis.system.util.TextUtils.subString;
import static org.hisp.dhis.system.util.TextUtils.trimEnd;
import static org.hisp.dhis.system.util.TextUtils.*;

import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id $
 */
public class TextUtilsTest
{
    private static final String STRING = "abcdefghij";
    
    @Test
    public void testHtmlLinks()
    {
        assertEquals( "<a href=\"http://dhis2.org\">http://dhis2.org</a>", htmlLinks( "http://dhis2.org" ) );
        assertEquals( "<a href=\"https://dhis2.org\">https://dhis2.org</a>", htmlLinks( "https://dhis2.org" ) );
        assertEquals( "<a href=\"http://www.dhis2.org\">www.dhis2.org</a>", htmlLinks( "www.dhis2.org" ) );        
        assertEquals( "Navigate to <a href=\"http://dhis2.org\">http://dhis2.org</a> or <a href=\"http://www.dhis2.com\">www.dhis2.com</a> to read more.", 
            htmlLinks( "Navigate to http://dhis2.org or www.dhis2.com to read more." ) );
    }
    
    @Test
    public void testSubString()
    {
        assertEquals( "abcdefghij", subString( STRING, 0, 10 ) );
        
        assertEquals( "cdef", subString( STRING, 2, 4 ) );

        assertEquals( "ghij", subString( STRING, 6, 4 ) );
        
        assertEquals( "ghij", subString( STRING, 6, 6 ) );

        assertEquals( "", subString( STRING, 11, 3 ) );
        
        assertEquals( "j", subString( STRING, 9, 1 ) );
        
        assertEquals( "", subString( STRING, 4, 0 ) );
    }
    
    @Test
    public void testTrim()
    {
        assertEquals( "abcdefgh", trimEnd( "abcdefghijkl", 4 ) );
    }
}
