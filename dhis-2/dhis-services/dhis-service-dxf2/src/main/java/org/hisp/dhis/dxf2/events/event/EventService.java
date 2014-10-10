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

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.dxf2.events.person.Person;
import org.hisp.dhis.dxf2.importsummary.ImportSummaries;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.dxf2.metadata.ImportOptions;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.scheduling.TaskId;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public interface EventService
{
    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    Events getEvents( Program program, OrganisationUnit organisationUnit );

    Events getEvents( Program program, OrganisationUnit organisationUnit, Date startDate, Date endDate );

    Events getEvents( Program program, OrganisationUnit organisationUnit, Person person, Date startDate, Date endDate );

    Events getEvents( ProgramStage programStage, OrganisationUnit organisationUnit );

    Events getEvents( ProgramStage programStage, OrganisationUnit organisationUnit, Date startDate, Date endDate );

    Events getEvents( ProgramStage programStage, OrganisationUnit organisationUnit, Person person, Date startDate, Date endDate );

    Events getEvents( Program program, ProgramStage programStage, OrganisationUnit organisationUnit );

    Events getEvents( Program program, ProgramStage programStage, OrganisationUnit organisationUnit, Person person );

    Events getEvents( Program program, ProgramStage programStage, OrganisationUnit organisationUnit, Date startDate, Date endDate );

    Events getEvents( Program program, ProgramStage programStage, OrganisationUnit organisationUnit, Person person, Date startDate, Date endDate );

    Events getEvents( List<Program> programs, List<ProgramStage> programStages, List<OrganisationUnit> organisationUnits, Date startDate, Date endDate );

    Events getEvents( List<Program> programs, List<ProgramStage> programStages, List<OrganisationUnit> organisationUnits, Person person, Date startDate, Date endDate );

    Event getEvent( String uid );

    Event getEvent( ProgramStageInstance programStageInstance );

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    ImportSummary saveEvent( Event event );

    ImportSummary saveEvent( Event event, ImportOptions importOptions );

    ImportSummary saveEventXml( InputStream inputStream ) throws IOException;

    ImportSummary saveEventXml( InputStream inputStream, ImportOptions importOptions ) throws IOException;

    ImportSummaries saveEventsXml( InputStream inputStream ) throws IOException;

    ImportSummaries saveEventsXml( InputStream inputStream, ImportOptions importOptions ) throws IOException;

    ImportSummaries saveEventsXml( InputStream inputStream, TaskId taskId, ImportOptions importOptions ) throws IOException;

    ImportSummary saveEventJson( InputStream inputStream ) throws IOException;

    ImportSummary saveEventJson( InputStream inputStream, ImportOptions importOptions ) throws IOException;

    ImportSummaries saveEventsJson( InputStream inputStream ) throws IOException;

    ImportSummaries saveEventsJson( InputStream inputStream, ImportOptions importOptions ) throws IOException;

    ImportSummaries saveEventsJson( InputStream inputStream, TaskId taskId, ImportOptions importOptions ) throws IOException;

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    void updateEvent( Event event );

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    void deleteEvent( Event event );
}
