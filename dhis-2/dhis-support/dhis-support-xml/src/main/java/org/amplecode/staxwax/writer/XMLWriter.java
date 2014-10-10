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

import javax.xml.stream.XMLStreamWriter;

/**
 * This interface provides convenience methods for XML writing.
 * 
 * @author Lars Helge Overland
 * @version $Id: XMLWriter.java 145 2009-06-29 14:48:33Z larshelg $
 */
public interface XMLWriter
{
    /**
     * Writes the XML declaration to output.
     * 
     * @param encoding the encoding the XML file.
     * @param version the version of the XML file.
     */
    void openDocument();
    
    /**
     * Writes the XML declaration to output.
     * 
     * @param encoding the encoding the XML file.
     * @param version the version of the XML file.
     */
    void openDocument( String encoding, String version );
    
    /**
     * Writes an XML start tag to output.
     * 
     * @param name the name of the XML element.
     */
    void openElement( String name );
    
    /**
     * Writes an XML start tag to output.
     * 
     * @param name the name of the XML element.
     * @param attributeNameValuePairs the attributes of the XML element given in key-value-pairs.
     */
    void openElement( String name, String... attributeNameValuePairs );
    
    /**
     * Writes an XML attribute to output.
     * 
     * @param name the attribute name.
     * @param value the attribute value.
     */
    void writeAttribute( String name, String value );
    
    /**
     * Writes an XML start tag, value, and end tag to output.
     * 
     * @param name the name of the XML element.
     * @param value the value of the XML element.
     */
    void writeElement( String name, String value );
    
    /**
     * Writes an XML start tag with attributes, value, and end tag to ouput.
     * 
     * @param name the name of the XML element.
     * @param value the value of the XML element.
     * @param attributeNameValuePairs the attributes of the XML element given in key-value-pairs.
     */
    void writeElement( String name, String value, String... attributeNameValuePairs );
    
    /**
     * Writes characters to output.
     * 
     * @param characters the characters to write.
     */
    void writeCharacters( String characters );
    
    /**
     * Writes non-parsed character data to output.
     * 
     * @param cData character data to write.
     */
    void writeCData( String cData );

    /**
     * Writes non-parsed character data to output.
     * 
     * @param name the name of the XML element.
     * @param cData character data to write.
     */
    void writeCData( String name, String cData );
    
    /**
     * Provides the underlying XmlStreamWriter.
     * 
     * @return the underlying XmlStreamWriter.
     */
    XMLStreamWriter getXmlStreamWriter();
    
    /**
     * Writes an XML end tag to output.
     * 
     * @param name the name of the XML element.
     */
    void closeElement();
    
    /**
     * Writes XML end tags to start tags which have not been terminated to output.
     * 
     * @param encoding the encoding of the XML file.
     */
    void closeDocument();
    
    /**
     * Closes the underlying writer.
     */
    void closeWriter();
}
