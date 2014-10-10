package org.amplecode.staxwax.framework;


import java.io.InputStream;
import junit.framework.TestCase;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
/**
 *
 * @author bobj
 * @version created 17-Feb-2010
 */
public class XPathFilterTest extends TestCase
{

    private InputStream inputStreamB;
    private InputStream inputStreamC;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        inputStreamB = classLoader.getResourceAsStream( "dataB.xml" );
        inputStreamC = classLoader.getResourceAsStream( "dataC.xml" );
    }

    @Override
    public void tearDown()
        throws Exception
    {
        inputStreamB.close();
        inputStreamC.close();
    }

    public synchronized void testFindText()
    {
        String result;
        result = XPathFilter.findText( inputStreamB,
            "/dataElements/dataElement[@code='code2']/description" );
        assertEquals( "description2", result );
    }

    public synchronized void testFindNode()
    {
        Node result;
        result = XPathFilter.findNode( inputStreamB,
            "/dataElements/dataElement[@code='code2']/description" );
        assertEquals( "description2", result.getTextContent() );
    }

    public synchronized void testFindNodes()
    {
        NodeList result;
        result = XPathFilter.findNodes( inputStreamB,
            "/dataElements/dataElement[(@code='code2') or (@code='code3')]/description" );
        assertEquals( 2, result.getLength() );
    }

    public synchronized void testFindNumber()
    {
        String result;
        result = XPathFilter.findText( inputStreamC,
            "/root/dataElements/@id" );
        assertEquals( "42", result );
    }
}
