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

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A simple utility class for evaluating xpath expressions on xml streams
 * 
 * @author bobj
 * @version created 16-Feb-2010
 */
public class XPathFilter
{
    private static final Log log = LogFactory.getLog( XPathFilter.class );

    /**
     * Find at most one Node from stream
     * 
     * @param in
     * @param xpathExpr
     * @return
     */
    public static Node findNode( InputStream in, String xpathExpr )
    {
        Node result = null;

        try
        {
            XPathExpression expr = compileXPath( xpathExpr );

            Document doc = parseDocument( in );

            result = (Node) expr.evaluate( doc, XPathConstants.NODE );

        }
        catch ( Exception ex )
        {
            log.info( ex );
        }
        return result;
    }

    /**
     * Find set of nodes in stream
     * 
     * @param in
     * @param xpathExpr
     * @return
     */
    public static NodeList findNodes( InputStream in, String xpathExpr )
    {
        NodeList result = null;

        try
        {
            XPathExpression expr = compileXPath( xpathExpr );

            Document doc = parseDocument( in );

            result = (NodeList) expr.evaluate( doc, XPathConstants.NODESET );

        }
        catch ( Exception ex )
        {
            log.info( ex );
        }

        return result;
    }

    /**
     * Find text data in stream
     * 
     * @param in
     * @param xpathExpr
     * @return
     */
    public static String findText( InputStream in, String xpathExpr )
    {
        String result = null;

        try
        {
            XPathExpression expr = compileXPath( xpathExpr );

            Document doc = parseDocument( in );

            result = (String) expr.evaluate( doc, XPathConstants.STRING );
        }
        catch ( Exception ex )
        {
            log.info( ex );
        }
        return result;
    }

    private static synchronized XPathExpression compileXPath( String xpathString )
    {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        XPathExpression expr = null;
        try
        {
            expr = xpath.compile( xpathString );
        }
        catch ( XPathExpressionException ex )
        {
            log.info( "Failed to compile xpath: " + xpathString + " : " + ex.getCause() );
        }

        return expr;
    }

    private static synchronized Document parseDocument( InputStream in )
    {
        Document doc = null;

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();

        // keep life simple using xpath 1.0
        docBuilderFactory.setNamespaceAware( false );

        try
        {
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            doc = docBuilder.parse( in );
        }
        catch ( Exception ex )
        {
            log.info( "XPath: Failed to parse input stream" + ex.getCause() );
        }

        return doc;
    }
}
