package org.hisp.dhis.api.controller.event;

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

import org.hisp.dhis.api.controller.WebOptions;
import org.hisp.dhis.api.controller.exception.NotFoundException;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dxf2.events.event.Event;
import org.hisp.dhis.dxf2.events.event.EventService;
import org.hisp.dhis.dxf2.events.event.Events;
import org.hisp.dhis.dxf2.events.event.ImportEventTask;
import org.hisp.dhis.dxf2.events.person.Person;
import org.hisp.dhis.dxf2.events.person.PersonService;
import org.hisp.dhis.dxf2.importsummary.ImportStatus;
import org.hisp.dhis.dxf2.importsummary.ImportSummaries;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.dxf2.metadata.ImportOptions;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.scheduling.TaskCategory;
import org.hisp.dhis.scheduling.TaskId;
import org.hisp.dhis.system.scheduling.Scheduler;
import org.hisp.dhis.system.util.StreamUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = EventController.RESOURCE_PATH )
public class EventController
{
    public static final String RESOURCE_PATH = "/events";

    //--------------------------------------------------------------------------
    // Dependencies
    //--------------------------------------------------------------------------

    @Autowired
    private IdentifiableObjectManager manager;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private EventService eventService;

    @Autowired
    private PersonService personService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    @RequestMapping( value = "", method = RequestMethod.GET )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_PATIENT_DATAVALUE_ADD')" )
    public String getEvents(
        @RequestParam( value = "program", required = false ) String programUid,
        @RequestParam( value = "programStage", required = false ) String programStageUid,
        @RequestParam( value = "person", required = false ) String personUid,
        @RequestParam( value = "orgUnit", required = false ) String orgUnitUid,
        @RequestParam( value = "includeChildren", required = false, defaultValue = "false" ) boolean includeChildren,
        @RequestParam( value = "includeDescendants", required = false, defaultValue = "false" ) boolean includeDescendants,
        @RequestParam( required = false ) @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date startDate,
        @RequestParam( required = false ) @DateTimeFormat( pattern = "yyyy-MM-dd" ) Date endDate,
        @RequestParam Map<String, String> parameters, Model model, HttpServletRequest request ) throws NotFoundException
    {
        WebOptions options = new WebOptions( parameters );
        Program program = manager.get( Program.class, programUid );
        ProgramStage programStage = manager.get( ProgramStage.class, programStageUid );
        List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>();
        OrganisationUnit rootOrganisationUnit;
        Person person = null;

        if ( personUid != null )
        {
            person = personService.getPerson( personUid );

            if ( person == null )
            {
                throw new NotFoundException( "Person", personUid );
            }
        }

        rootOrganisationUnit = manager.get( OrganisationUnit.class, orgUnitUid );

        if ( rootOrganisationUnit == null )
        {
            try
            {
                rootOrganisationUnit = manager.get( OrganisationUnit.class, Integer.parseInt( orgUnitUid ) );
            }
            catch ( NumberFormatException ignored )
            {
            }
        }

        if ( rootOrganisationUnit == null && person != null )
        {
            Events events = eventService.getEvents( Arrays.asList( program ), Arrays.asList( programStage ), null, person, startDate, endDate );

            model.addAttribute( "model", events );
            model.addAttribute( "viewClass", options.getViewClass( "detailed" ) );

            return "events";
        }

        if ( rootOrganisationUnit == null )
        {
            throw new NotFoundException( "OrganisationUnit", programUid );
        }

        if ( includeDescendants )
        {
            organisationUnits.addAll( organisationUnitService.getOrganisationUnitsWithChildren( rootOrganisationUnit.getUid() ) );
        }
        else if ( includeChildren )
        {
            organisationUnits.add( rootOrganisationUnit );
            organisationUnits.addAll( rootOrganisationUnit.getChildren() );
        }
        else
        {
            organisationUnits.add( rootOrganisationUnit );
        }

        Events events = eventService.getEvents( Arrays.asList( program ), Arrays.asList( programStage ), organisationUnits, person, startDate, endDate );

        if ( options.hasLinks() )
        {
            for ( Event event : events.getEvents() )
            {
                event.setHref( ContextUtils.getRootPath( request ) + RESOURCE_PATH + "/" + event.getEvent() );
            }
        }

        model.addAttribute( "model", events );
        model.addAttribute( "viewClass", options.getViewClass( "detailed" ) );

        return "events";
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_PATIENT_DATAVALUE_ADD')" )
    public String getEvent( @PathVariable( "uid" ) String uid, @RequestParam Map<String, String> parameters,
        Model model, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        Event event = eventService.getEvent( uid );

        if ( event == null )
        {
            ContextUtils.notFoundResponse( response, "Event not found for uid: " + uid );
            return null;
        }

        if ( options.hasLinks() )
        {
            event.setHref( ContextUtils.getRootPath( request ) + RESOURCE_PATH + "/" + uid );
        }

        model.addAttribute( "model", event );
        model.addAttribute( "viewClass", options.getViewClass( "detailed" ) );

        return "event";
    }

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.POST, consumes = "application/xml" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_PATIENT_DATAVALUE_ADD')" )
    public void postXmlEvent( HttpServletResponse response, HttpServletRequest request, ImportOptions importOptions ) throws Exception
    {
        InputStream inputStream = StreamUtils.wrapAndCheckCompressionFormat( request.getInputStream() );

        if ( !importOptions.isAsync() )
        {
            ImportSummaries importSummaries = eventService.saveEventsXml( inputStream, importOptions );

            for ( ImportSummary importSummary : importSummaries.getImportSummaries() )
            {
                if ( !importOptions.isDryRun() )
                {
                    if ( !importSummary.getStatus().equals( ImportStatus.ERROR ) )
                    {
                        importSummary.setHref( ContextUtils.getRootPath( request ) + RESOURCE_PATH + "/" + importSummary.getReference() );
                    }
                }
            }

            if ( importSummaries.getImportSummaries().size() == 1 )
            {
                ImportSummary importSummary = importSummaries.getImportSummaries().get( 0 );

                if ( !importOptions.isDryRun() )
                {
                    if ( !importSummary.getStatus().equals( ImportStatus.ERROR ) )
                    {
                        response.setHeader( "Location", ContextUtils.getRootPath( request ) + RESOURCE_PATH + "/" + importSummary.getReference() );
                    }
                }
            }

            JacksonUtils.toXml( response.getOutputStream(), importSummaries );
        }
        else
        {
            TaskId taskId = new TaskId( TaskCategory.EVENT_IMPORT, currentUserService.getCurrentUser() );
            scheduler.executeTask( new ImportEventTask( inputStream, eventService, importOptions, taskId, false ) );
            response.setHeader( "Location", ContextUtils.getRootPath( request ) + "/system/tasks/" + TaskCategory.EVENT_IMPORT );
            response.setStatus( HttpServletResponse.SC_NO_CONTENT );
        }
    }

    @RequestMapping( method = RequestMethod.POST, consumes = "application/json" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_PATIENT_DATAVALUE_ADD')" )
    public void postJsonEvent( HttpServletResponse response, HttpServletRequest request, ImportOptions importOptions ) throws Exception
    {
        InputStream inputStream = StreamUtils.wrapAndCheckCompressionFormat( request.getInputStream() );

        if ( !importOptions.isAsync() )
        {
            ImportSummaries importSummaries = eventService.saveEventsJson( inputStream, importOptions );

            for ( ImportSummary importSummary : importSummaries.getImportSummaries() )
            {
                if ( !importOptions.isDryRun() )
                {
                    if ( !importSummary.getStatus().equals( ImportStatus.ERROR ) )
                    {
                        importSummary.setHref( ContextUtils.getRootPath( request ) + RESOURCE_PATH + "/" + importSummary.getReference() );
                    }
                }
            }

            if ( importSummaries.getImportSummaries().size() == 1 )
            {
                ImportSummary importSummary = importSummaries.getImportSummaries().get( 0 );

                if ( !importOptions.isDryRun() )
                {
                    if ( !importSummary.getStatus().equals( ImportStatus.ERROR ) )
                    {
                        response.setHeader( "Location", ContextUtils.getRootPath( request ) + RESOURCE_PATH + "/" + importSummary.getReference() );
                    }
                }
            }

            JacksonUtils.toJson( response.getOutputStream(), importSummaries );
        }
        else
        {
            TaskId taskId = new TaskId( TaskCategory.EVENT_IMPORT, currentUserService.getCurrentUser() );
            scheduler.executeTask( new ImportEventTask( inputStream, eventService, importOptions, taskId, true ) );
            response.setHeader( "Location", ContextUtils.getRootPath( request ) + "/system/tasks/" + TaskCategory.EVENT_IMPORT );
            response.setStatus( HttpServletResponse.SC_NO_CONTENT );
        }

    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, consumes = { "application/xml", "text/xml" } )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_PATIENT_DATAVALUE_ADD')" )
    public void putXmlEvent( HttpServletResponse response, HttpServletRequest request, @PathVariable( "uid" ) String uid ) throws IOException
    {
        Event event = eventService.getEvent( uid );

        if ( event == null )
        {
            ContextUtils.notFoundResponse( response, "Event not found for uid: " + uid );
            return;
        }

        Event updatedEvent = JacksonUtils.fromXml( request.getInputStream(), Event.class );
        updatedEvent.setEvent( uid );

        eventService.updateEvent( updatedEvent );
        ContextUtils.okResponse( response, "Event updated: " + uid );
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, consumes = "application/json" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_PATIENT_DATAVALUE_ADD')" )
    public void putJsonEvent( HttpServletResponse response, HttpServletRequest request, @PathVariable( "uid" ) String uid ) throws IOException
    {
        Event event = eventService.getEvent( uid );

        if ( event == null )
        {
            ContextUtils.notFoundResponse( response, "Event not found for uid: " + uid );
            return;
        }

        Event updatedEvent = JacksonUtils.fromJson( request.getInputStream(), Event.class );
        updatedEvent.setEvent( uid );

        eventService.updateEvent( updatedEvent );
        ContextUtils.okResponse( response, "Event updated: " + uid );
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.DELETE )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_PATIENT_DATAVALUE_DELETE')" )
    public void deleteEvent( HttpServletResponse response, @PathVariable( "uid" ) String uid )
    {
        Event event = eventService.getEvent( uid );

        if ( event == null )
        {
            ContextUtils.notFoundResponse( response, "Event not found for uid: " + uid );
            return;
        }

        eventService.deleteEvent( event );
    }
}