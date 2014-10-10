package org.hisp.dhis.dataentryform;

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
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * @author Lars Helge Overland
 */
public class DataEntryFormTest
{
    private String htmlCodeA = 
        "<p><table><tr valign=\"top\">" +
        "<td align=\"center\" width=\"8%\"><p><input title=\"\" view=\"@@deshortname@@\" value=\"\" name=\"entryfield\" id=\"value[1000].value:value[3].value\" style=\"width: 4em; text-align: center;\" /></p></td>" +
        "<td align=\"center\" width=\"4%\"><p><input title=\"\" view=\"@@deshortname@@\" value=\"\" name=\"entryfield\" id=\"value[2000].value:value[4].value\" style=\"width: 4em; text-align: center;\" /></p></td>" +
        "</tr></table></p>";

    private String htmlCodeB = 
        "<p><table><tr valign=\"top\">" +
        "<td align=\"center\" width=\"8%\"><p><input title=\"\" view=\"@@deshortname@@\" value=\"\" name=\"entryfield\" id=\"value[1100].value:value[13].value\" style=\"width: 4em; text-align: center;\" /></p></td>" +
        "<td align=\"center\" width=\"4%\"><p><input title=\"\" view=\"@@deshortname@@\" value=\"\" name=\"entryfield\" id=\"value[2100].value:value[14].value\" style=\"width: 4em; text-align: center;\" /></p></td>" +
        "</tr></table></p>";

    @Test
    public void testConvertDataEntryForm()
    {
        Map<Object, Integer> dataElementMap = new HashMap<Object, Integer>();
        Map<Object, Integer> categoryOptionComboMap = new HashMap<Object, Integer>();
        
        dataElementMap.put( 1000, 1100 );
        dataElementMap.put( 2000, 2100 );
        categoryOptionComboMap.put( 3, 13 );
        categoryOptionComboMap.put( 4, 14 );
        
        DataEntryForm form = new DataEntryForm( "DataEntryFormA", htmlCodeA );
        
        form.convertDataEntryForm( dataElementMap, categoryOptionComboMap );
        
        assertEquals( htmlCodeB, form.getHtmlCode() );
    }

    @Test(expected=RuntimeException.class)
    public void testConvertDataEntryFormInvalid()
    {
        Map<Object, Integer> dataElementMap = new HashMap<Object, Integer>();
        Map<Object, Integer> categoryOptionComboMap = new HashMap<Object, Integer>();

        DataEntryForm form = new DataEntryForm( "DataEntryFormA", htmlCodeA );
        
        form.convertDataEntryForm( dataElementMap, categoryOptionComboMap );
        
        fail();
    }
}
