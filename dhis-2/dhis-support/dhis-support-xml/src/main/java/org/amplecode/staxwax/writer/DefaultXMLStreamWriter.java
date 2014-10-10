package org.amplecode.staxwax.writer;

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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * @author Lars Helge Overland
 * @version $Id: DefaultXMLStreamWriter.java 153 2009-11-02 14:18:49Z larshelg $
 */
public class DefaultXMLStreamWriter
    implements XMLWriter 
{
    private XMLStreamWriter writer;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
    
    public DefaultXMLStreamWriter( XMLStreamWriter writer )
    {
        this.writer = writer;
    }

    // -------------------------------------------------------------------------
    // XMLWriter implementation
    // -------------------------------------------------------------------------

    public void openDocument()
    {
        openDocument( "UTF-8", "1.0" );
    }

    public void openDocument( String encoding, String version )
    {
        try
        {
            writer.writeStartDocument( encoding, version );
        }
        catch ( XMLStreamException ex )
        {
            throw new RuntimeException( "Failed to open document", ex );
        }        
    }

    public void openElement( String name )
    {
        try
        {
            writer.writeStartElement( verifyNotNull( name ) );
        }
        catch ( XMLStreamException ex )
        {
            throw new RuntimeException( "Failed to open element: " + name , ex );
        }
    }
    
    public void openElement( String name, String... attributeNameValuePairs )
    {
        try
        {
            writer.writeStartElement( verifyNotNull( name ) );
            
            if ( attributeNameValuePairs.length % 2 == 0 )
            {
                for ( int i = 0; i < attributeNameValuePairs.length; i += 2 )
                {
                    if ( attributeNameValuePairs[ i + 1 ] != null )
                    {
                        writer.writeAttribute( verifyNotNull( attributeNameValuePairs[ i ] ), attributeNameValuePairs[ i + 1 ] );
                    }
                }
            }
        }
        catch ( XMLStreamException ex )
        {
            throw new RuntimeException( "Failed to open element: " + name, ex );
        }
    }
    
    public void writeAttribute( String name, String value )
    {
        try
        {
            if ( value != null )
            {
                writer.writeAttribute( verifyNotNull( name ), value ); 
            }
        }
        catch ( XMLStreamException ex )
        {
            throw new RuntimeException( "Failed to write attribute: " + name, ex );
        }
    }
    
    public void writeElement( String name, String value )
    {
        try
        {
            writer.writeStartElement( verifyNotNull( name ) );
                        
            writer.writeCharacters( replaceNull( value ) );
            
            writer.writeEndElement();
        }
        catch ( XMLStreamException ex )
        {
            throw new RuntimeException( "Failed to write element: " + name + ", value: " + value, ex );
        }
    }
    
    public void writeElement( String name, String value, String... attributeNameValuePairs )
    {
        try
        {
            writer.writeStartElement( verifyNotNull( name ) );
            
            if ( attributeNameValuePairs.length % 2 == 0 )
            {
                for ( int i = 0; i < attributeNameValuePairs.length; i += 2 )
                {
                    if ( attributeNameValuePairs[ i + 1 ] != null )
                    {
                        writer.writeAttribute( verifyNotNull( attributeNameValuePairs[ i ] ), attributeNameValuePairs[ i + 1 ] );
                    }
                }
            }
            
            writer.writeCharacters( replaceNull( value ) );
            
            writer.writeEndElement();
        }
        catch ( XMLStreamException ex )
        {
            throw new RuntimeException( "Failed to write element: " + name + ", value: " + value, ex );
        }
    }
    
    public void writeCharacters( String characters )
    {
        try
        {
            writer.writeCharacters( replaceNull( characters ) );
        }
        catch ( XMLStreamException ex )
        {
            throw new RuntimeException( "Failed to write characters: " + characters, ex );
        }
    }
    
    public void writeCData( String cData )
    {
        try
        {
            writer.writeCData( replaceNull( cData ) );
        }
        catch ( XMLStreamException ex )
        {
            throw new RuntimeException( "Failed to write CData: " + cData, ex );
        }
    }
    
    public void writeCData( String name, String cData )
    {
        try
        {
            writer.writeStartElement( verifyNotNull( name ) );
            
            writer.writeCData( replaceNull( cData ) );
            
            writer.writeEndElement();
        }
        catch ( XMLStreamException ex )
        {
            throw new RuntimeException( "Failed to write CData: " + cData, ex );
        }
    }

    public XMLStreamWriter getXmlStreamWriter()
    {
        return writer;
    }
    
    public void closeElement()
    {
        try
        {
            writer.writeEndElement();
        }
        catch ( XMLStreamException ex )
        {
            throw new RuntimeException( "Failed to close element", ex );
        }
    }
    
    public void closeDocument()
    {
        try
        {
            writer.writeEndDocument();
            
            writer.flush();
            
            writer.close();
        }
        catch ( XMLStreamException ex )
        {
            throw new RuntimeException( "Failed to close document", ex );
        }
    }
    
    public void closeWriter()
    {
        try
        {
            writer.flush();
        }
        catch ( XMLStreamException ex )
        {
            // Move on to close it
        }
        
        try
        {
            writer.close();
        }
        catch ( XMLStreamException ex )
        {
            throw new RuntimeException( "Failed to close writer", ex );
        }       
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    
    private String replaceNull( String string )
    {
        return string != null ? string : "";
    }
    
    private String verifyNotNull( String string )
    {
        if ( string == null )
        {
            throw new RuntimeException( "XML element or attribute can not be null" );
        }
        
        return string;
    }
}
