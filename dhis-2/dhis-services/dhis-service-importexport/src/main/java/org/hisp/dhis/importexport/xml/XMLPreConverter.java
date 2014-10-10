package org.hisp.dhis.importexport.xml;

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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.amplecode.staxwax.factory.XMLFactory;
import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.transformer.TransformerTask;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.importexport.ImportException;

/**
 * GenericXMLConvertor transforms imported foreign XML to dxf.
 * 
 * @author bobj
 */
public class XMLPreConverter
{
    private final Log log = LogFactory.getLog( XMLPreConverter.class );

    public static final int BUFFER_SIZE = 2000;

    public static final String TRANSFORMERS_CONFIG = "transform/transforms.xml";

    // -------------------------------------------------------------------------
    // Named XSLT parameters available to xslt stylesheets
    // -------------------------------------------------------------------------

    // Current timestamp
    public static final String TIMESTAMP = "timestamp";

    // url base where dxf metadata snapshots are found
    public static final String METADATA_URL_BASE = "metadata_url_base";

    // current dhis2 user
    public static final String DHIS_USER = "username";

    // url of zip file containing stream (may be null)
    public static final String ZIP_URL = "zip_url";

    public static final String defaultMetadataBase = "metadata/";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private XSLTLocator xsltLocator;

    public void setXsltLocator( XSLTLocator xsltLocator )
    {
        this.xsltLocator = xsltLocator;
    }

    private URIResolver dhisResolver;

    public void setDhisResolver( URIResolver dhisResolver )
    {
        this.dhisResolver = dhisResolver;
    }

    public QName getDocumentRoot( BufferedInputStream xmlDataStream )
        throws ImportException
    {
        QName rootName = null;

        try
        {
            // buffer enough space to read root elemen
            xmlDataStream.mark( BUFFER_SIZE );

            XMLReader reader = XMLFactory.getXMLReader( xmlDataStream );

            reader.moveToStartElement();
            rootName = reader.getElementQName();

            xmlDataStream.reset();
        }
        catch ( Exception ex )
        {
            throw new ImportException( "Couldn't locate document root element", ex );
        }

        return rootName;
    }

    /**
     * Performs transform on stream
     * 
     * @param source the input
     * @param result the result
     * @param xsltTag identifier used to look up xslt stylesheet
     * @param zipFile optional zipfile when importing from zip
     * @param userName the dhis username
     * @throws ImportException
     */
    public void transform( Source source, Result result, String xsltTag, File zipFile, String userName )
        throws ImportException
    {
        InputStream sheetStream = xsltLocator.getTransformerByTag( xsltTag );
        Source sheet = new StreamSource( sheetStream );

        log.debug( "Populating xslt parameters" );
        Map<String, String> xsltParams = new HashMap<String, String>();

        if ( userName != null )
        {
            xsltParams.put( DHIS_USER, userName );
        }
        if ( zipFile != null )
        {
            xsltParams.put( ZIP_URL, zipFile.getAbsolutePath() );
        }
        
        xsltParams.put( METADATA_URL_BASE, defaultMetadataBase );
        Date now = new Date();
        DateFormat dfm = new SimpleDateFormat( "yyyy-MM-dd'T'hh-mm" ); // iso8601 timestamp
        xsltParams.put( TIMESTAMP, dfm.format( now ) );

        log.debug( "Applying stylesheet" );

        try
        {
            TransformerTask tt = new TransformerTask( sheet, xsltParams );

            tt.transform( source, result, dhisResolver );
            log.debug( "Transform successful" );
        }
        catch ( Exception ex )
        {
            throw new ImportException( "Failed to transform stream", ex );
        }
    }
}
