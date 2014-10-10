package org.amplecode.staxwax.transformer;

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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.amplecode.staxwax.framework.XMLPipe;
import org.junit.Test;

/**
 *
 * @author bobj
 * @version created 14-Dec-2009
 */
public class TransformerTaskTest extends TestCase
{

    private InputStream inputStreamB;

    private InputStream stylesheet;

    private Map<String, String> params;

    private TransformerTask tt;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------
    @Override
    public void setUp()
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        inputStreamB = classLoader.getResourceAsStream( "dataB.xml" );
        stylesheet = classLoader.getResourceAsStream( "transform.xsl" );

        params = new HashMap<String, String>();
        params.put( "name", "Bob" );

        Source sheet = new StreamSource( stylesheet );
        tt = new TransformerTask( sheet, params );
    }

    @Test
    public synchronized void testTransform() throws TransformerConfigurationException, TransformerException, XMLStreamException
    {
        tt.compile();
        Source source = new StreamSource( inputStreamB );

        // make a pipe to catch output of transform
        XMLPipe pipe = new XMLPipe();
        XMLEventWriter pipeinput = pipe.getInput();
        XMLEventReader pipeoutput = pipe.getOutput();

        StAXResult result = new StAXResult( pipeinput );

        tt.transform( source, result, null );

        // expect 25 events in the pipe
        assertEquals( 25, pipe.getEventCount() );

        // just drain the pipe
        while ( pipeoutput.hasNext() )
        {
            pipeoutput.nextEvent();
        }
    }
}
