/*
 * Copyright (c) 2004-2009, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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
 * @author Bob Jolliffe
 * @version $$Id$$
 */

package org.hisp.dhis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.component.LifeCycle;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * @author Bob Jolliffe
 */
public class WebAppServer extends Thread
{
    public static final String DHIS_DIR = "/webapps/dhis";

    public static final String JETTY_PORT_CONF = "/conf/jetty.port";

    public static final int DEFAULT_JETTY_PORT = 8080;

    private static final Log log = LogFactory.getLog( WebAppServer.class );

    protected Server server;

    protected Connector connector;

    public WebAppServer()
    {
        server = new Server();
        connector = new SelectChannelConnector();
    }

    public void init( String installDir, LifeCycle.Listener serverListener )
        throws Exception
    {
        try
        {
            int portFromConfig = this.getPortFromConfig( installDir + JETTY_PORT_CONF );
            connector.setPort( portFromConfig );
            log.info( "Loading DHIS 2 on port: " + portFromConfig );
        }
        catch ( Exception ex )
        {
            log.info( "Couldn't load port number from " + installDir + JETTY_PORT_CONF );
            connector.setPort( DEFAULT_JETTY_PORT );
            log.info( "Loading DHIS 2 on port: " + DEFAULT_JETTY_PORT );
        }

        server.setConnectors( new Connector[] { connector } );
        server.addLifeCycleListener( serverListener );

        loadDHISContext(installDir+DHIS_DIR);
    }

    public void loadDHISContext(String webappPath)
    {
        WebAppContext dhisWebApp = new WebAppContext();
        dhisWebApp.setMaxFormContentSize( 5000000 );
        dhisWebApp.setWar( webappPath );
        log.info( "Setting DHIS 2 web app context to: " + webappPath );

        server.setHandler( dhisWebApp );
    }

    public void run()
    {
        try
        {
            log.debug("Server thread starting");
            server.start();
            log.debug("Server thread exiting");
        } catch ( Exception ex )
        {
            log.error( "Server wouldn't start : " + ex);
        }
    }

    public void shutdown()
        throws Exception
    {
        server.stop();
    }

    public int getConnectorPort()
    {
        return connector.getPort();
    }

    private int getPortFromConfig( String conf )
        throws FileNotFoundException, IOException
    {
        Reader r = new BufferedReader( new FileReader( conf ) );
        char[] cbuf = new char[10];
        r.read( cbuf );
        String numstr = String.copyValueOf( cbuf );
        Integer port = Integer.valueOf( numstr.trim() );
        return port.intValue();
    }
}
