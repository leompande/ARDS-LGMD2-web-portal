package org.amplecode.staxwax.reader;

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

import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;

import javax.xml.stream.XMLStreamReader;

/**
 * This interface provides convenience methods for XML reading.
 * 
 * @author Lars Helge Overland
 * @version $Id: XMLReader.java 152 2009-10-30 23:01:04Z larshelg $
 */
public interface XMLReader
{
    /**
     * Reads the name of the current XML element.
     *
     * @return The name of the current XML element.
     */
    String getElementName();

    /**
     * Reads the QName of the current XML element.
     *
     * @return The QName of the current XML element.
     */
    QName getElementQName();

    /**
     * Reads the value of the current XML element.
     * 
     * @return The name of the current XML element.
     */
    String getElementValue();

    /**
     * Moves the cursor to the next XML start element in the document.
     *
     */
    void moveToStartElement( );

    /**
     * Moves the cursor to the first XML start element with the given name in the document.
     * 
     * @param name The name of the XML element to move to.
     */
    void moveToStartElement( String name );

    /**
     * Moves the cursor to the first XML start element with the given name in the document. Returns
     * true until the cursor points at an XML end element with the given name.
     * 
     * @param startElementName The name of the XML start element to move to.
     * @param endElementName The name of the XML end element to stop moving forward at.
     */
    boolean moveToStartElement( String startElementName, String endElementName );

    /**
     * Checks whether the current XML element is a start element with the given name.
     * 
     * @param name The name of the XML element.
     * @return True if the current XML element is a start element with the given name, otherwise false.
     */
    boolean isStartElement( String name );
    
    /**
     * Checks whether the current XML element is an end element with the given name.
     * 
     * @param name The name of the XML element.
     * @return True if the current XML element is an end element with the given name, otherwise false.
     */
    boolean isEndElement( String name );
    
    /**
     * Moves the cursor to the next XML event in the document. Returns true until the cursor points
     * at the XML document end.
     * 
     * @return False if the cursor points at the XML document end, true otherwise.
     */
    boolean next();
    
    /**
     * Moves the cursor to the next XML event in the document. Returns true until the cursor points
     * at an XML end element with the given name.
     * 
     * @param endElementName The name of the end element.
     * @return False if the cursor points at an XML end element with the given name, true otherwise.
     */
    boolean next( String endElementName );
    
    /**
     * Reads the value of the XML attribute with the given name.
     * 
     * @param attributeName The name of the attribute.
     * @return The value of the XML attribute with the given name.
     */
    String getAttributeValue( String attributeName );
    
    /**
     * Returns the count of attributes on this start element. Attributes are
     * zero-based.
     * 
     * @return the count of attributes.
     */
    int getAttributeCount();
    
    /**
     * Reads values for all properties for an element into a Map, where the key
     * is the property and the value is the value. This method is only suitable
     * for single-level nested elements.
     * 
     * @param elementName the name of the XML element.
     * @return A map with property-value pairs for the given XML element.
     */
    Map<String, String> readElements( String elementName );
    
    /**
     * Provides the underlying XMLStreamReader2.
     * 
     * @return the underlying XMLStreamReader2.
     */
    XMLStreamReader getXmlStreamReader();
    

    /**
     * Creates and XMLEventReader2 from the stream.
     *
     * @return the XMLEventReader2.
     */
    XMLEventReader getXmlEventReader();

    /**
     * Logs all events which occurs while parsing the document for debugging purposes.
     */
    void dryRun();
    
    /**
     * Closes the underlying reader.
     */
    void closeReader();
}
