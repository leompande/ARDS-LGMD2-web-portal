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

import java.io.IOException;
import java.io.InputStream;

import org.amplecode.staxwax.framework.XPathFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.external.location.LocationManager;
import org.hisp.dhis.external.location.LocationManagerException;
import org.hisp.dhis.importexport.ImportException;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Node;

/**
 * An XSLT locator based on the dhis LocationManager
 * 
 * It depends on a simple XML transformers configration file which maps tags to
 * stylesheets.
 * 
 * @author bobj
 * @version created 30-Apr-2010
 */
public class LocManagerXSLTLocator
    implements XSLTLocator
{
    private static final Log log = LogFactory.getLog( LocManagerXSLTLocator.class );

    private static final String TRANSFORMER_MAP = "transform/transforms.xml";

    private LocationManager locationManager;

    public void setLocationManager( LocationManager locationManager )
    {
        this.locationManager = locationManager;
    }

    @Override
    public InputStream getTransformerByTag( String identifier )
        throws ImportException
    {
        Node transformerNode = null;
        
        String xpath = "/transforms/transform[@tag='" + identifier + "']/xslt";
        log.debug( "xpath search: " + xpath );
        
        // ---------------------------------------------------------------------
        // Search file system
        // ---------------------------------------------------------------------

        try
        {
            transformerNode = XPathFilter.findNode( locationManager.getInputStream( TRANSFORMER_MAP ), xpath );
        }
        catch ( LocationManagerException ex )
        {
            // Not found, proceed to search in classpath
        }
        
        if ( transformerNode != null )
        {
            log.info( "Loading transformer from file system: " + transformerNode.getTextContent() );
            
            try
            {
                return locationManager.getInputStream( "transform/" + transformerNode.getTextContent() );
            }
            catch ( LocationManagerException ex )
            {
                throw new ImportException( "Transformer mapped for format but could not be found on file system: " + transformerNode.getTextContent() );
            }
        }
        
        // ---------------------------------------------------------------------
        // Search classpath
        // ---------------------------------------------------------------------

        try
        {
            transformerNode = XPathFilter.findNode( new ClassPathResource( TRANSFORMER_MAP ).getInputStream(), xpath );
            
            log.info( "Loading transformer from classpath: " + transformerNode.getTextContent() );
            
            return new ClassPathResource( "transform/" + transformerNode.getTextContent() ).getInputStream();
        }
        catch ( IOException ex )
        {
            throw new ImportException( "No transformer configured for this format: " + identifier );
        }
    }
}
