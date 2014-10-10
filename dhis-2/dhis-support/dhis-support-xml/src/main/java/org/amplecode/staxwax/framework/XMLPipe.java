package org.amplecode.staxwax.framework;

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

import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.codehaus.stax2.XMLEventReader2;

/**
 * An XML pipe is useful when you want to decouple reader/writer operations, for
 * example using different threads for each.
 * 
 * The output of the pipe looks like an XMLEventReader and can be used as a
 * Source for a transformation.
 * 
 * The input of the pipe looks like an XMLEventWriter and can be used as a
 * Result of a transformation.
 * 
 * Only minimal required methods of XMLReader and XMLWriter are implemented.
 * 
 * @author bobj
 * @version created 08-Dec-2009
 */
public class XMLPipe
{
    protected XMLEventReader2 output;

    protected XMLEventWriter input;

    public XMLEventWriter getInput()
    {
        return input;
    }

    public XMLEventReader2 getOutput()
    {
        return output;
    }

    public int getEventCount()
    {
        return eventQ.size();
    }

    /**
     * Storage for XMLEvents in pipeline
     */
    protected LinkedBlockingQueue<XMLEvent> eventQ;

    public XMLPipe()
    {
        eventQ = new LinkedBlockingQueue<XMLEvent>();
        output = new PipeReader();
        input = new PipeWriter();
    }

    private class PipeReader
        implements XMLEventReader2
    {
        // ---------------------------------------------------------------------
        // XMLEventReader methods
        // ---------------------------------------------------------------------

        @Override
        public XMLEvent nextEvent()
            throws XMLStreamException
        {
            XMLEvent result;
            try
            {
                // non-blocking poll()
                // return eventQ.poll();
                // do beware - this will block if q is empty
                result = eventQ.take();
                return result;

            }
            catch ( InterruptedException ex )
            {
                throw new XMLStreamException( "XMLpipe read interrupted", ex );
            }
        }

        @Override
        public boolean hasNext()
        {
            return (eventQ.size() != 0);
        }

        @Override
        public XMLEvent peek()
            throws XMLStreamException
        {
            return eventQ.peek();
        }

        @Override
        public String getElementText()
            throws XMLStreamException
        {
            // get the text
            String result = nextEvent().asCharacters().getData();
            // pop (and test caste) the end element
            nextEvent().asEndElement();

            return result;
        }

        @Override
        public XMLEvent nextTag()
            throws XMLStreamException
        {
            throw new UnsupportedOperationException( "Unused functionality.  Not implemented" );
        }

        @Override
        public Object getProperty( String name )
            throws IllegalArgumentException
        {
            throw new UnsupportedOperationException( "Unused functionality.  Not implemented" );
        }

        @Override
        public void close()
            throws XMLStreamException
        {
            // not a real stream ... no handle to close
            return;
        }

        @Override
        public Object next()
        {
            throw new UnsupportedOperationException( "Unused functionality.  Not implemented" );
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException( "Unused functionality.  Not implemented" );
        }

        @Override
        public boolean hasNextEvent()
            throws XMLStreamException
        {
            throw new UnsupportedOperationException( "Unused functionality.  Not implemented" );
        }

        @Override
        public boolean isPropertySupported( String string )
        {
            throw new UnsupportedOperationException( "Unused functionality.  Not implemented" );
        }

        @Override
        public boolean setProperty( String string, Object o )
        {
            throw new UnsupportedOperationException( "Unused functionality.  Not implemented" );
        }
    }

    private class PipeWriter
        implements XMLEventWriter
    {
        // ---------------------------------------------------------------------
        // XMLEventWriter methods
        // ---------------------------------------------------------------------

        @Override
        public void flush()
            throws XMLStreamException
        {
            // nothing cached to flush?
            return;
        }

        @Override
        public void add( XMLEvent event )
            throws XMLStreamException
        {
            eventQ.add( event );
        }

        @Override
        public void add( XMLEventReader reader )
            throws XMLStreamException
        {
            while ( reader.hasNext() )
            {
                eventQ.add( reader.nextEvent() );
            }
        }

        @Override
        public String getPrefix( String uri )
            throws XMLStreamException
        {
            throw new UnsupportedOperationException( "Unused functionality.  Not implemented" );
        }

        @Override
        public void setPrefix( String prefix, String uri )
            throws XMLStreamException
        {
            throw new UnsupportedOperationException( "Unused functionality.  Not implemented" );
        }

        @Override
        public void setDefaultNamespace( String uri )
            throws XMLStreamException
        {
            throw new UnsupportedOperationException( "Unused functionality.  Not implemented" );
        }

        @Override
        public void setNamespaceContext( NamespaceContext context )
            throws XMLStreamException
        {
            throw new UnsupportedOperationException( "Unused functionality.  Not implemented" );
        }

        @Override
        public NamespaceContext getNamespaceContext()
        {
            throw new UnsupportedOperationException( "Unused functionality.  Not implemented" );
        }

        @Override
        public void close()
            throws XMLStreamException
        {
            // not a real stream ... no handle to close
            return;
        }
    }
}
