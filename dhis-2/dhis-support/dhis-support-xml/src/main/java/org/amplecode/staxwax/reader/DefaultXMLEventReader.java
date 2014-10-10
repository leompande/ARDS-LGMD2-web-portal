package org.amplecode.staxwax.reader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.codehaus.stax2.XMLEventReader2;
import org.codehaus.stax2.XMLStreamReader2;

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
 * @version created 28-Dec-2009
 */
public class DefaultXMLEventReader
    implements XMLReader
{
    private XMLEventReader2 reader;

    private XMLEvent currentEvent;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
    
    public DefaultXMLEventReader( XMLEventReader2 reader )
    {
        this.reader = reader;
    }

    @Override
    public String getElementName()
    {
        String localName = null;

        if ( currentEvent.isStartElement() )
        {
            localName = currentEvent.asStartElement().getName().getLocalPart();
        }

        if ( currentEvent.isEndElement() )
        {
            localName = currentEvent.asEndElement().getName().getLocalPart();
        }

        return localName;
    }

    @Override
    public QName getElementQName()
    {
        QName qName = null;

        if ( currentEvent.isStartElement() )
        {
            qName = currentEvent.asStartElement().getName();
        }

        if ( currentEvent.isEndElement() )
        {
            qName = currentEvent.asEndElement().getName();
        }

        return qName;
    }

    @Override
    public String getElementValue()
    {
        try
        {
            if ( reader.peek().isCharacters() )
            {
                return this.getText();
            }
            else
            {
                return null;
            }
        }
        catch ( XMLStreamException ex )
        {
            throw new RuntimeException( "Failed to get element value", ex );
        }
    }

    @Override
    public void moveToStartElement( String name )
    {
        try
        {
            while ( reader.hasNext() )
            {
                currentEvent = reader.nextEvent();
                if ( currentEvent.isStartElement()
                    && currentEvent.asStartElement().getName().getLocalPart().equals( name ) )
                {
                    break;
                }
            }
        }
        catch ( XMLStreamException ex )
        {
            throw new RuntimeException( "Failed to move to start element", ex );
        }
    }

    @Override
    public void moveToStartElement( )
    {
        try
        {
            while ( reader.hasNext() )
            {
                currentEvent = reader.nextEvent();
                if ( currentEvent.isStartElement())
                {
                    break;
                }
            }
        }
        catch ( XMLStreamException ex )
        {
            throw new RuntimeException( "Failed to move to start element", ex );
        }
    }

    @Override
    public boolean moveToStartElement( String startElementName, String endElementName )
    {
        try
        {
            while ( reader.hasNext() )
            {
                currentEvent = reader.nextEvent();
                if ( currentEvent.isStartElement()
                    && currentEvent.asStartElement().getName().getLocalPart().equals( startElementName ) )
                {
                    return true;
                }
                if ( currentEvent.isEndElement()
                    && currentEvent.asEndElement().getName().getLocalPart().equals( endElementName ) )
                {
                    return false;
                }
            }
            return false;
        }
        catch ( XMLStreamException ex )
        {
            throw new RuntimeException( "Failed to move to start element", ex );
        }
    }

    @Override
    public boolean isStartElement( String name )
    {
        try
        {
            return currentEvent.asStartElement().getName().getLocalPart().equals( name );
        }
        catch ( ClassCastException ex )
        {
            // asStartElement() will throw ClassCastException if not a StartElement
            return false;
        }
    }

    @Override
    public boolean isEndElement( String name )
    {
        try
        {
            return currentEvent.asEndElement().getName().getLocalPart().equals( name );
        }
        catch ( ClassCastException ex )
        {
            return false;
        }
    }

    @Override
    public boolean next()
    {
        try
        {
            currentEvent = reader.nextEvent();
            return !currentEvent.isEndDocument();
        }
        catch ( XMLStreamException ex )
        {
            throw new RuntimeException( "Failed to move to next element", ex );
        }
    }

    @Override
    public boolean next( String endElementName )
    {
        try
        {
            currentEvent = reader.nextEvent();
            return !(currentEvent.isEndElement() && currentEvent.asEndElement().getName().getLocalPart().equals(
                endElementName ));
        }
        catch ( XMLStreamException ex )
        {
            throw new RuntimeException( "Failed to move to next element", ex );
        }
    }

    @Override
    public String getAttributeValue( String attributeName )
    {
        try
        {
            QName attributeQName = new QName( null, attributeName );
            Attribute attribute = currentEvent.asStartElement().getAttributeByName( attributeQName );
            return attribute != null ? attribute.getValue() : null;
        }
        catch ( ClassCastException ex )
        {
            return null;
        }
    }

    @Override
    public int getAttributeCount()
    {
        try
        {
            Iterator<?> attributeIter = currentEvent.asStartElement().getAttributes();
            int count = 0;
            while ( attributeIter.hasNext() )
            {
                ++count;
                attributeIter.next();
            }
            return count;
        }
        catch ( ClassCastException ex )
        {
            return 0;
        }
    }

    @Override
    public Map<String, String> readElements( String elementName )
    {
        try
        {
            final Map<String, String> elements = new HashMap<String, String>();
            StartElement startEvent = currentEvent.asStartElement();
            Iterator<?> attributeIter = startEvent.getAttributes();
            if ( attributeIter.hasNext() )
            {
                while ( attributeIter.hasNext() )
                {
                    Attribute a = (Attribute) attributeIter.next();
                    elements.put( a.getName().getLocalPart(), a.getValue() );
                }
                return elements;
            }
            else
            {
                while ( reader.hasNext() )
                {
                    currentEvent = reader.nextEvent();
                    if ( currentEvent.isEndElement()
                        && currentEvent.asEndElement().getName().getLocalPart().equals( elementName ) )
                    {
                        break;
                    }
                    if ( currentEvent.isStartElement() )
                    {
                        String name = currentEvent.asStartElement().getName().getLocalPart();
                        String value = this.getElementValue();
                        elements.put( name, value );
                    }
                }
                return elements;
            }
        }
        catch ( XMLStreamException ex )
        {
            throw new RuntimeException( "Failed to read elements", ex );
        }
        catch ( ClassCastException ex )
        {
            throw new RuntimeException( "Failed to read elements", ex );
        }
    }

    @Override
    public XMLStreamReader2 getXmlStreamReader()
    {
        throw new UnsupportedOperationException( "Can't get the stream reader back from event reader" );
    }

    @Override
    public XMLEventReader2 getXmlEventReader()
    {
        return reader;
    }

    @Override
    public void dryRun()
    {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    @Override
    public void closeReader()
    {
        try
        {
            reader.close();
        }
        catch ( XMLStreamException ex )
        {
            throw new RuntimeException( "Failed to close reader", ex );
        }
    }

    protected String getText()
        throws XMLStreamException
    {
        StringBuffer sb = new StringBuffer();
        while ( reader.peek().isCharacters() )
        {
            sb.append( reader.nextEvent().asCharacters().getData() );
        }
        return sb.length() == 0 ? null : sb.toString();
    }
}
