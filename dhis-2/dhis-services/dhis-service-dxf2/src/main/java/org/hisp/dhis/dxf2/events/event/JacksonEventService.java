package org.hisp.dhis.dxf2.events.event;

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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hisp.dhis.dxf2.importsummary.ImportSummaries;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.dxf2.metadata.ImportOptions;
import org.hisp.dhis.dxf2.timer.SystemNanoTimer;
import org.hisp.dhis.dxf2.timer.Timer;
import org.hisp.dhis.scheduling.TaskId;
import org.hisp.dhis.system.notification.NotificationLevel;
import org.hisp.dhis.system.notification.Notifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Implementation of EventService that uses Jackson for serialization and deserialization.
 *
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Transactional
public class JacksonEventService extends AbstractEventService
{
    private static final Log log = LogFactory.getLog( JacksonEventService.class );

    @Autowired
    private Notifier notifier;

    @Autowired
    private SessionFactory sessionFactory;

    // -------------------------------------------------------------------------
    // EventService Impl
    // -------------------------------------------------------------------------

    private final static ObjectMapper xmlMapper = new XmlMapper();

    private final static ObjectMapper jsonMapper = new ObjectMapper();

    @SuppressWarnings( "unchecked" )
    private static <T> T fromXml( InputStream inputStream, Class<?> clazz ) throws IOException
    {
        return (T) xmlMapper.readValue( inputStream, clazz );
    }

    @SuppressWarnings( "unchecked" )
    private static <T> T fromXml( String input, Class<?> clazz ) throws IOException
    {
        return (T) xmlMapper.readValue( input, clazz );
    }

    @SuppressWarnings( "unchecked" )
    private static <T> T fromJson( InputStream inputStream, Class<?> clazz ) throws IOException
    {
        return (T) jsonMapper.readValue( inputStream, clazz );
    }

    @SuppressWarnings( "unchecked" )
    private static <T> T fromJson( String input, Class<?> clazz ) throws IOException
    {
        return (T) jsonMapper.readValue( input, clazz );
    }

    static
    {
        xmlMapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true );
        xmlMapper.configure( DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true );
        xmlMapper.configure( DeserializationFeature.WRAP_EXCEPTIONS, true );
        jsonMapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true );
        jsonMapper.configure( DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true );
        jsonMapper.configure( DeserializationFeature.WRAP_EXCEPTIONS, true );
    }

    @Override
    public ImportSummaries saveEventsXml( InputStream inputStream ) throws IOException
    {
        return saveEventsXml( inputStream, null, null );
    }

    @Override
    public ImportSummaries saveEventsXml( InputStream inputStream, ImportOptions importOptions ) throws IOException
    {
        return saveEventsXml( inputStream, null, importOptions );
    }

    @Override
    public ImportSummaries saveEventsXml( InputStream inputStream, TaskId taskId, ImportOptions importOptions ) throws IOException
    {
        ImportSummaries importSummaries = new ImportSummaries();

        String input = StreamUtils.copyToString( inputStream, Charset.forName( "UTF-8" ) );

        notifier.clear( taskId ).notify( taskId, "Importing events" );

        Timer timer = new SystemNanoTimer().start();

        try
        {
            Events events = fromXml( input, Events.class );

            int counter = 0;

            for ( Event event : events.getEvents() )
            {
                importSummaries.addImportSummary( saveEvent( event, importOptions ) );

                if ( counter % 100 == 0 )
                {
                    sessionFactory.getCurrentSession().flush();
                    sessionFactory.getCurrentSession().clear();
                }

                counter++;
            }
        }
        catch ( Exception ex )
        {
            Event event = fromXml( input, Event.class );
            importSummaries.addImportSummary( saveEvent( event, importOptions ) );
        }

        if ( taskId != null )
        {
            notifier.notify( taskId, NotificationLevel.INFO, "Import done. Completed in " + timer.toString() + ".", true ).
                addTaskSummary( taskId, importSummaries );
        }
        else
        {
            log.info( "Import done. Completed in " + timer.toString() + "." );
        }

        return importSummaries;
    }

    @Override
    public ImportSummary saveEventXml( InputStream inputStream ) throws IOException
    {
        return saveEventXml( inputStream, null );
    }

    @Override
    public ImportSummary saveEventXml( InputStream inputStream, ImportOptions importOptions ) throws IOException
    {
        Event event = fromXml( inputStream, Event.class );
        return saveEvent( event, importOptions );
    }

    @Override
    public ImportSummaries saveEventsJson( InputStream inputStream ) throws IOException
    {
        return saveEventsJson( inputStream, null, null );
    }

    @Override
    public ImportSummaries saveEventsJson( InputStream inputStream, ImportOptions importOptions ) throws IOException
    {
        return saveEventsJson( inputStream, null, importOptions );
    }

    @Override
    public ImportSummaries saveEventsJson( InputStream inputStream, TaskId taskId, ImportOptions importOptions ) throws IOException
    {
        ImportSummaries importSummaries = new ImportSummaries();

        String input = StreamUtils.copyToString( inputStream, Charset.forName( "UTF-8" ) );

        notifier.clear( taskId ).notify( taskId, "Importing events" );

        Timer timer = new SystemNanoTimer().start();

        try
        {
            Events events = fromJson( input, Events.class );

            int counter = 0;

            for ( Event event : events.getEvents() )
            {
                importSummaries.addImportSummary( saveEvent( event, importOptions ) );

                if ( counter % 100 == 0 )
                {
                    sessionFactory.getCurrentSession().flush();
                    sessionFactory.getCurrentSession().clear();
                }

                counter++;
            }
        }
        catch ( Exception ex )
        {
            Event event = fromJson( input, Event.class );
            importSummaries.addImportSummary( saveEvent( event, importOptions ) );
        }

        if ( taskId != null )
        {
            notifier.notify( taskId, NotificationLevel.INFO, "Import done. Completed in " + timer.toString() + ".", true ).
                addTaskSummary( taskId, importSummaries );
        }
        else
        {
            log.info( "Import done. Completed in " + timer.toString() + "." );
        }

        return importSummaries;
    }

    @Override
    public ImportSummary saveEventJson( InputStream inputStream ) throws IOException
    {
        return saveEventJson( inputStream, null );
    }

    @Override
    public ImportSummary saveEventJson( InputStream inputStream, ImportOptions importOptions ) throws IOException
    {
        Event event = fromJson( inputStream, Event.class );
        return saveEvent( event, importOptions );
    }
}
