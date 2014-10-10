package org.hisp.dhis.importexport;

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
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipFile;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.amplecode.staxwax.factory.XMLFactory;
import org.amplecode.staxwax.reader.XMLReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.ProcessState;
import org.hisp.dhis.importexport.dxf.converter.DXFConverter;
import org.hisp.dhis.importexport.xml.XMLPreConverter;
import org.hisp.dhis.importexport.zip.ZipAnalyzer;
import org.hisp.dhis.system.process.OutputHolderState;
import org.hisp.dhis.system.util.StreamUtils;

/**
 * @author bobj
 */
public class DefaultImportService
    implements ImportService
{

    private final Log log = LogFactory.getLog( DefaultImportService.class );

    static public final String DXF1URI = "http://dhis2.org/schema/dxf/1.0";
    static public final String DXF2URI = "http://dhis2.org/schema/dxf/2.0";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private XMLPreConverter preConverter;

    public void setPreConverter( XMLPreConverter preConverter )
    {
        this.preConverter = preConverter;
    }

    private DXFConverter converter;

    public void setConverter( DXFConverter converter )
    {
        this.converter = converter;
    }

    // -------------------------------------------------------------------------
    // ImportService implementation
    // -------------------------------------------------------------------------

    @Override
    public void importData( ImportParams params, InputStream inputStream )
        throws ImportException
    {
        importData( params, inputStream, new OutputHolderState() );
    }

    @Override
    public void importData( ImportParams params, InputStream inputStream, ProcessState state )
        throws ImportException
    {
        log.info( "Importing stream" );

        File tempZipFile = null;
        ZipFile zipFile = null;
        BufferedInputStream xmlDataStream = null; // The InputStream carrying the XML to be imported

        BufferedInputStream bufin = new BufferedInputStream( inputStream );

        try
        {
            if ( StreamUtils.isZip( bufin ) ) // If it's a ZIP file we must figure out what kind of package it is
            {                
                log.info( "Zip file detected" );
                
                tempZipFile = File.createTempFile( "IMPORT_", "_ZIP" );
                
                BufferedOutputStream ostream = new BufferedOutputStream( new FileOutputStream( tempZipFile ) ); // Save it to disk                

                StreamUtils.streamcopy( bufin, ostream );

                bufin.close();

                log.info( "Saved zipstream to file: " + tempZipFile.getAbsolutePath() );

                zipFile = new ZipFile( tempZipFile );

                xmlDataStream = new BufferedInputStream( ZipAnalyzer.getTransformableStream( zipFile ) );

            }
            else
            {
                if ( StreamUtils.isGZip( bufin ) )
                {
                    xmlDataStream = new BufferedInputStream( new GZIPInputStream( bufin ) ); // Pass through the uncompressed stream
                }
                else
                {
                    xmlDataStream = bufin; // Assume uncompressed XML and keep moving
                }
            }
        }
        catch ( Exception ex )
        {
            log.error( ex );
            throw new ImportException( "Error processing input stream", ex );
        }

        QName documentRootName = preConverter.getDocumentRoot( xmlDataStream );

        XMLReader dxfReader = null;
        File transformOutput = null;
        BufferedInputStream dxfInStream = null;

        try
        {
            String rootLocalName = documentRootName.getLocalPart();
            String rootNameSpace = documentRootName.getNamespaceURI();

            if ( rootLocalName.equals( DXFConverter.DXFROOT ) )
            {                
                log.info( "Importing DXF native stream" ); // Native DXF stream, no transform required
                dxfReader = XMLFactory.getXMLReader( xmlDataStream );
            }
            else
            {
                log.info( "Transforming stream" );
                transformOutput = File.createTempFile( "TRANSFORM_", ".xml" );
                Source source = new StreamSource( xmlDataStream );
                FileOutputStream transformOutStream = new FileOutputStream( transformOutput );
                StreamResult result = new StreamResult( transformOutStream );
                String xsltIdentifierTag = documentRootName.toString();
                log.debug( "Tag for transformer: " + xsltIdentifierTag );

                preConverter.transform( source, result, xsltIdentifierTag, tempZipFile, null );
                transformOutStream.flush();
                transformOutStream.close();

                log.info( "Transform successful" );
                dxfInStream = 
                    new BufferedInputStream(new FileInputStream( transformOutput ));
                rootNameSpace = preConverter.getDocumentRoot( dxfInStream ).getNamespaceURI();
                dxfReader = XMLFactory.getXMLReader( dxfInStream );                
            }

            log.debug( "Sending DXFv1 to converter" );
            converter.read( dxfReader, params, state );
        }
        catch ( IOException ex )
        {
            log.error( ex );
            
            throw new ImportException( "Failed to import stream", ex );
        }
        finally
        {
            if ( dxfReader != null ) // Clean up whatever streams might be open
            {
                dxfReader.closeReader();
            }
            if ( transformOutput != null )
            {
                transformOutput.delete();
            }
            if ( tempZipFile != null )
            {
                tempZipFile.delete();
            }

            try
            {
                if ( dxfInStream != null )
                {
                    dxfInStream.close();
                }
                if ( zipFile != null )
                {
                    zipFile.close();
                }
            }
            catch ( IOException ex )
            {
                log.warn( "Failed to close zipfile" );
            }
        }
    }
}
