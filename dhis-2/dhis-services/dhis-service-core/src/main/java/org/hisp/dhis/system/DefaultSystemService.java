package org.hisp.dhis.system;

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

import org.apache.commons.io.IOUtils;
import org.hisp.dhis.configuration.Configuration;
import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.external.location.LocationManager;
import org.hisp.dhis.external.location.LocationManagerException;
import org.hisp.dhis.system.database.DatabaseInfoProvider;
import org.hisp.dhis.system.util.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @author Lars Helge Overland
 */
public class DefaultSystemService
    implements SystemService
{
    @Autowired
    private LocationManager locationManager;

    @Autowired
    private DatabaseInfoProvider databaseInfoProvider;

    @Autowired
    private ConfigurationService configurationService;

    private SystemInfo systemInfo = null;

    // -------------------------------------------------------------------------
    // SystemService implementation
    // -------------------------------------------------------------------------

    @Override
    public SystemInfo getSystemInfo()
    {
        if ( systemInfo != null )
        {
            return systemInfo;
        }

        systemInfo = new SystemInfo();

        // ---------------------------------------------------------------------
        // Version
        // ---------------------------------------------------------------------

        ClassPathResource resource = new ClassPathResource( "build.properties" );

        if ( resource.isReadable() )
        {
            InputStream in = null;

            try
            {
                in = resource.getInputStream();

                Properties properties = new Properties();

                properties.load( in );

                systemInfo.setVersion( properties.getProperty( "build.version" ) );
                systemInfo.setRevision( properties.getProperty( "build.revision" ) );

                String buildTime = properties.getProperty( "build.time" );

                DateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

                systemInfo.setBuildTime( dateFormat.parse( buildTime ) );
            }
            catch ( IOException ex )
            {
                // Do nothing
            }
            catch ( ParseException ex )
            {
                // Do nothing
            }
            finally
            {
                IOUtils.closeQuietly( in );
            }
        }

        // ---------------------------------------------------------------------
        // External directory
        // ---------------------------------------------------------------------

        systemInfo.setEnvironmentVariable( locationManager.getEnvironmentVariable() );

        try
        {
            File directory = locationManager.getExternalDirectory();

            systemInfo.setExternalDirectory( directory.getAbsolutePath() );
        }
        catch ( LocationManagerException ex )
        {
            systemInfo.setExternalDirectory( "Not set" );
        }

        // ---------------------------------------------------------------------
        // Database
        // ---------------------------------------------------------------------

        systemInfo.setDatabaseInfo( databaseInfoProvider.getDatabaseInfo() );

        // ---------------------------------------------------------------------
        // System env variables and properties
        // ---------------------------------------------------------------------

        try
        {
            systemInfo.setJavaOpts( System.getenv( "JAVA_OPTS" ) );
        }
        catch ( SecurityException ex )
        {
            systemInfo.setJavaOpts( "Unknown" );
        }

        Properties props = System.getProperties();

        systemInfo.setJavaIoTmpDir( props.getProperty( "java.io.tmpdir" ) );
        systemInfo.setJavaVersion( props.getProperty( "java.version" ) );
        systemInfo.setJavaVendor( props.getProperty( "java.vendor" ) );
        systemInfo.setOsName( props.getProperty( "os.name" ) );
        systemInfo.setOsArchitecture( props.getProperty( "os.arch" ) );
        systemInfo.setOsVersion( props.getProperty( "os.version" ) );

        systemInfo.setMemoryInfo( SystemUtils.getMemoryString() );
        systemInfo.setCpuCores( SystemUtils.getCpuCores() );
        systemInfo.setServerDate( new Date() );

        Configuration config = configurationService.getConfiguration();

        systemInfo.setSystemId( config.getSystemId() );

        return systemInfo;
    }
}
