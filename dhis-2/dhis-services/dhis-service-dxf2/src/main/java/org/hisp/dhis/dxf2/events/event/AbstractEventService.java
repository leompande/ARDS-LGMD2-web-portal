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


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dxf2.events.person.Person;
import org.hisp.dhis.dxf2.importsummary.ImportConflict;
import org.hisp.dhis.dxf2.importsummary.ImportStatus;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.dxf2.metadata.ImportOptions;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientcomment.PatientComment;
import org.hisp.dhis.patientcomment.PatientCommentService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.ValidationUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public abstract class AbstractEventService
    implements EventService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private ProgramService programService;

    @Autowired
    private ProgramStageService programStageService;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private ProgramStageInstanceService programStageInstanceService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private DataElementService dataElementService;

    @Autowired
    protected CurrentUserService currentUserService;

    @Autowired
    private PatientDataValueService patientDataValueService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientCommentService patientCommentService;

    @Autowired
    private IdentifiableObjectManager manager;

    @Autowired
    private EventStore eventStore;

    @Autowired
    private I18nManager i18nManager;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public ImportSummary saveEvent( Event event )
    {
        return saveEvent( event, null );
    }

    @Override
    @Transactional
    public ImportSummary saveEvent( Event event, ImportOptions importOptions )
    {
        Program program = programService.getProgram( event.getProgram() );
        ProgramInstance programInstance;
        ProgramStage programStage = programStageService.getProgramStage( event.getProgramStage() );
        ProgramStageInstance programStageInstance = null;

        if ( program == null )
        {
            return new ImportSummary( ImportStatus.ERROR, "Event.program does not point to a valid program" );
        }

        if ( programStage == null && !program.isSingleEvent() )
        {
            return new ImportSummary( ImportStatus.ERROR,
                "Event.programStage does not point to a valid programStage, and program is multi stage" );
        }
        else if ( programStage == null )
        {
            programStage = program.getProgramStageByStage( 1 );
        }

        Assert.notNull( program );
        Assert.notNull( programStage );

        if ( verifyProgramAccess( program ) )
        {
            return new ImportSummary( ImportStatus.ERROR,
                "Current user does not have permission to access this program." );
        }

        if ( program.isRegistration() )
        {
            if ( event.getPerson() == null )
            {
                return new ImportSummary( ImportStatus.ERROR,
                    "No Event.person was provided for registration based program." );
            }

            Patient patient = patientService.getPatient( event.getPerson() );

            if ( patient == null )
            {
                return new ImportSummary( ImportStatus.ERROR, "Event.person does not point to a valid person." );
            }

            List<ProgramInstance> programInstances = new ArrayList<ProgramInstance>(
                programInstanceService.getProgramInstances( patient, program, ProgramInstance.STATUS_ACTIVE ) );

            if ( programInstances.isEmpty() )
            {
                return new ImportSummary( ImportStatus.ERROR, "Person " + patient.getUid()
                    + " is not enrolled in program " + program.getUid() );
            }
            else if ( programInstances.size() > 1 )
            {
                return new ImportSummary( ImportStatus.ERROR, "Person " + patient.getUid()
                    + " have multiple active enrollments into program " + program.getUid()
                    + " please check and correct your database." );
            }

            programInstance = programInstances.get( 0 );

            if ( program.isSingleEvent() )
            {
                List<ProgramStageInstance> programStageInstances = new ArrayList<ProgramStageInstance>(
                    programStageInstanceService.getProgramStageInstances( programInstances, false ) );

                if ( programStageInstances.isEmpty() )
                {
                    return new ImportSummary( ImportStatus.ERROR, "Person " + patient.getUid()
                        + " is not enrolled in programStage " + programStage.getUid() );
                }
                else if ( programStageInstances.size() > 1 )
                {
                    return new ImportSummary( ImportStatus.ERROR, "Person " + patient.getUid()
                        + " have multiple active enrollments into programStage " + programStage.getUid()
                        + " please check and correct your database for multiple active stages." );
                }

                programStageInstance = programStageInstances.get( 0 );
            }
            else
            {
                if ( !programStage.getIrregular() )
                {
                    programStageInstance = programStageInstanceService.getProgramStageInstance( programInstance,
                        programStage );
                }

                else
                {
                    if ( event.getEvent() != null )
                    {
                        programStageInstance = programStageInstanceService.getProgramStageInstance( event.getEvent() );

                        if ( programStageInstance == null )
                        {
                            return new ImportSummary( ImportStatus.ERROR, "Event.event did not point to a valid event" );
                        }
                    }
                }
            }
        }
        else
        {
            List<ProgramInstance> programInstances = new ArrayList<ProgramInstance>(
                programInstanceService.getProgramInstances( program, ProgramInstance.STATUS_ACTIVE ) );

            if ( programInstances.isEmpty() )
            {
                return new ImportSummary( ImportStatus.ERROR,
                    "No active event exists for single event no registration program " + program.getUid()
                        + ", please check and correct your database." );
            }
            else if ( programInstances.size() > 1 )
            {
                return new ImportSummary( ImportStatus.ERROR,
                    "Multiple active events exists for single event no registration program " + program.getUid()
                        + ", please check and correct your database." );
            }

            programInstance = programInstances.get( 0 );

            if ( event.getEvent() != null )
            {
                programStageInstance = programStageInstanceService.getProgramStageInstance( event.getEvent() );

                if ( programStageInstance == null )
                {
                    return new ImportSummary( ImportStatus.ERROR, "Event.event did not point to a valid event" );
                }
            }
        }

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( event.getOrgUnit() );

        if ( organisationUnit == null )
        {
            organisationUnit = organisationUnitService.getOrganisationUnitByCode( event.getOrgUnit() );
        }

        if ( organisationUnit == null )
        {
            organisationUnit = organisationUnitService.getOrganisationUnitByUuid( event.getOrgUnit() );
        }

        if ( organisationUnit == null )
        {
            List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>(
                organisationUnitService.getOrganisationUnitByName( event.getOrgUnit() ) );

            if ( organisationUnits.size() == 1 )
            {
                organisationUnit = organisationUnits.get( 0 );
            }
        }

        if ( organisationUnit == null )
        {
            return new ImportSummary( ImportStatus.ERROR, "Event.orgUnit does not point to a valid organisation unit." );
        }

        if ( verifyProgramOrganisationUnitAssociation( program, organisationUnit ) )
        {
            return new ImportSummary( ImportStatus.ERROR, "Program is not assigned to this organisation unit." );
        }

        return saveEvent( program, programInstance, programStage, programStageInstance, organisationUnit, event,
            importOptions );
    }

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    @Override
    public Events getEvents( Program program, OrganisationUnit organisationUnit )
    {
        List<Event> eventList = eventStore.getAll( program, organisationUnit );
        Events events = new Events();
        events.setEvents( eventList );

        return events;
    }

    @Override
    public Events getEvents( Program program, OrganisationUnit organisationUnit, Date startDate, Date endDate )
    {
        List<Event> eventList = eventStore.getAll( program, organisationUnit, startDate, endDate );
        Events events = new Events();
        events.setEvents( eventList );

        return events;
    }

    @Override
    public Events getEvents( Program program, OrganisationUnit organisationUnit, Person person, Date startDate,
        Date endDate )
    {
        List<Event> eventList = eventStore.getAll( program, organisationUnit, person, startDate, endDate );
        Events events = new Events();
        events.setEvents( eventList );

        return events;
    }

    @Override
    public Events getEvents( ProgramStage programStage, OrganisationUnit organisationUnit )
    {
        List<Event> eventList = eventStore.getAll( programStage, organisationUnit );
        Events events = new Events();
        events.setEvents( eventList );

        return events;
    }

    @Override
    public Events getEvents( ProgramStage programStage, OrganisationUnit organisationUnit, Date startDate, Date endDate )
    {
        List<Event> eventList = eventStore.getAll( programStage, organisationUnit, startDate, endDate );
        Events events = new Events();
        events.setEvents( eventList );

        return events;
    }

    @Override
    public Events getEvents( ProgramStage programStage, OrganisationUnit organisationUnit, Person person,
        Date startDate, Date endDate )
    {
        List<Event> eventList = eventStore.getAll( programStage, organisationUnit, person, startDate, endDate );
        Events events = new Events();
        events.setEvents( eventList );

        return events;
    }

    @Override
    public Events getEvents( Program program, ProgramStage programStage, OrganisationUnit organisationUnit )
    {
        List<Event> eventList = eventStore.getAll( program, programStage, organisationUnit );
        Events events = new Events();
        events.setEvents( eventList );

        return events;
    }

    @Override
    public Events getEvents( Program program, ProgramStage programStage, OrganisationUnit organisationUnit,
        Person person )
    {
        List<Event> eventList = eventStore.getAll( program, programStage, organisationUnit, person );
        Events events = new Events();
        events.setEvents( eventList );

        return events;
    }

    @Override
    public Events getEvents( Program program, ProgramStage programStage, OrganisationUnit organisationUnit,
        Date startDate, Date endDate )
    {
        List<Event> eventList = eventStore.getAll( program, programStage, organisationUnit, startDate, endDate );
        Events events = new Events();
        events.setEvents( eventList );

        return events;
    }

    @Override
    public Events getEvents( Program program, ProgramStage programStage, OrganisationUnit organisationUnit,
        Person person, Date startDate, Date endDate )
    {
        List<Event> eventList = eventStore.getAll( program, programStage, organisationUnit, person, startDate, endDate );
        Events events = new Events();
        events.setEvents( eventList );

        return events;
    }

    @Override
    public Events getEvents( List<Program> programs, List<ProgramStage> programStages,
        List<OrganisationUnit> organisationUnits, Date startDate, Date endDate )
    {
        List<Event> eventList = eventStore.getAll( programs, programStages, organisationUnits, startDate, endDate );
        Events events = new Events();
        events.setEvents( eventList );

        return events;
    }

    @Override
    public Events getEvents( List<Program> programs, List<ProgramStage> programStages, List<OrganisationUnit> organisationUnits, Person person, Date startDate, Date endDate )
    {
        List<Event> eventList = eventStore.getAll( programs, programStages, organisationUnits, person, startDate, endDate );
        Events events = new Events();
        events.setEvents( eventList );

        return events;
    }

    @Override
    public Event getEvent( String uid )
    {
        ProgramStageInstance psi = programStageInstanceService.getProgramStageInstance( uid );

        return psi != null ? convertProgramStageInstance( psi ) : null;
    }

    @Override
    public Event getEvent( ProgramStageInstance programStageInstance )
    {
        return convertProgramStageInstance( programStageInstance );
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    @Override
    public void updateEvent( Event event )
    {
        ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( event
            .getEvent() );

        if ( programStageInstance == null )
        {
            return;
        }

        OrganisationUnit organisationUnit;

        if ( event.getOrgUnit() != null )
        {
            organisationUnit = organisationUnitService.getOrganisationUnit( event.getOrgUnit() );
        }
        else
        {
            organisationUnit = programStageInstance.getOrganisationUnit();
        }

        Date date = new Date();

        if ( event.getEventDate() != null )
        {
            date = DateUtils.getMediumDate( event.getEventDate() );
        }

        String storedBy = getStoredBy( event, null );

        if ( event.getStatus() == EventStatus.ACTIVE )
        {
            programStageInstance.setCompleted( false );
            programStageInstance.setStatus( ProgramStageInstance.ACTIVE_STATUS );
            programStageInstance.setCompletedDate( null );
            programStageInstance.setCompletedUser( null );
        }
        else if ( event.getStatus() == EventStatus.COMPLETED )
        {
            programStageInstance.setStatus( ProgramStageInstance.COMPLETED_STATUS );
            programStageInstance.setCompletedDate( date );
            programStageInstance.setCompletedUser( storedBy );

            if ( !programStageInstance.isCompleted() )
            {
                programStageInstanceService.completeProgramStageInstance( programStageInstance, i18nManager.getI18nFormat() );
            }
        }

        programStageInstance.setDueDate( date );
        programStageInstance.setExecutionDate( date );
        programStageInstance.setOrganisationUnit( organisationUnit );

        programStageInstanceService.updateProgramStageInstance( programStageInstance );

        ProgramInstance programInstance = programStageInstance.getProgramInstance();

        savePatientCommentFromEvent( programInstance, event, storedBy );

        Set<PatientDataValue> patientDataValues = new HashSet<PatientDataValue>(
            patientDataValueService.getPatientDataValues( programStageInstance ) );

        for ( DataValue value : event.getDataValues() )
        {
            DataElement dataElement = dataElementService.getDataElement( value.getDataElement() );

            PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance,
                dataElement );

            if ( patientDataValue != null )
            {
                patientDataValue.setValue( value.getValue() );
                patientDataValue.setProvidedElsewhere( value.getProvidedElsewhere() );
                patientDataValueService.updatePatientDataValue( patientDataValue );

                patientDataValues.remove( patientDataValue );
            }
            else
            {
                saveDataValue( programStageInstance, event.getStoredBy(), dataElement, value.getValue(),
                    value.getProvidedElsewhere() );
            }
        }

        for ( PatientDataValue value : patientDataValues )
        {
            patientDataValueService.deletePatientDataValue( value );
        }
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    @Override
    public void deleteEvent( Event event )
    {
        ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( event
            .getEvent() );

        if ( programStageInstance != null )
        {
            programStageInstanceService.deleteProgramStageInstance( programStageInstance );
        }
    }

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------

    private Event convertProgramStageInstance( ProgramStageInstance programStageInstance )
    {
        if ( programStageInstance == null )
        {
            return null;
        }

        Event event = new Event();

        event.setEvent( programStageInstance.getUid() );

        if ( programStageInstance.getProgramInstance().getPatient() != null )
        {
            event.setPerson( programStageInstance.getProgramInstance().getPatient().getUid() );
        }

        event.setStatus( EventStatus.fromInt( programStageInstance.getStatus() ) );
        event.setEventDate( DateUtils.getLongDateString( programStageInstance.getExecutionDate() ) );
        event.setStoredBy( programStageInstance.getCompletedUser() );
        event.setOrgUnit( programStageInstance.getOrganisationUnit().getUid() );
        event.setProgram( programStageInstance.getProgramInstance().getProgram().getUid() );
        event.setProgramStage( programStageInstance.getProgramStage().getUid() );

        if ( programStageInstance.getProgramInstance().getPatient() != null )
        {
            event.setPerson( programStageInstance.getProgramInstance().getPatient().getUid() );
        }

        if ( programStageInstance.getProgramStage().getCaptureCoordinates() )
        {
            Coordinate coordinate = null;

            if ( programStageInstance.getLongitude() != null && programStageInstance.getLongitude() != null )
            {
                coordinate = new Coordinate( programStageInstance.getLongitude(), programStageInstance.getLatitude() );

                try
                {
                    List<Double> list = objectMapper.readValue( coordinate.getCoordinateString(), new TypeReference<List<Double>>()
                    {
                    } );

                    coordinate.setLongitude( list.get( 0 ) );
                    coordinate.setLatitude( list.get( 1 ) );
                }
                catch ( IOException ignored )
                {
                }
            }

            if ( coordinate != null && coordinate.isValid() )
            {
                event.setCoordinate( coordinate );
            }
        }

        Collection<PatientDataValue> patientDataValues = patientDataValueService
            .getPatientDataValues( programStageInstance );

        for ( PatientDataValue patientDataValue : patientDataValues )
        {
            DataValue value = new DataValue();
            value.setDataElement( patientDataValue.getDataElement().getUid() );
            value.setValue( patientDataValue.getValue() );
            value.setProvidedElsewhere( patientDataValue.getProvidedElsewhere() );
            value.setStoredBy( patientDataValue.getStoredBy() );

            event.getDataValues().add( value );
        }

        ProgramInstance programInstance = programStageInstance.getProgramInstance();

        Collection<PatientComment> patientComments = programInstance.getPatientComments();

        for ( PatientComment patientComment : patientComments )
        {
            Note note = new Note();
            note.setValue( patientComment.getCommentText() );
            note.setStoredBy( patientComment.getCreator() );
            note.setStoredDate( patientComment.getCreatedDate().toString() );
            event.getNotes().add( note );
        }

        return event;
    }

    private boolean verifyProgramOrganisationUnitAssociation( Program program, OrganisationUnit organisationUnit )
    {
        boolean assignedToOrganisationUnit = false;

        if ( program.getOrganisationUnits().contains( organisationUnit ) )
        {
            assignedToOrganisationUnit = true;
        }
        else
        {
            for ( OrganisationUnitGroup organisationUnitGroup : program.getOrganisationUnitGroups() )
            {
                if ( organisationUnitGroup.getMembers().contains( organisationUnit ) )
                {
                    assignedToOrganisationUnit = true;
                    break;
                }
            }
        }

        return !assignedToOrganisationUnit;
    }

    private boolean verifyProgramAccess( Program program )
    {
        Collection<Program> programsByCurrentUser = programService.getProgramsByCurrentUser();
        return !programsByCurrentUser.contains( program );
    }

    private boolean validateDataValue( DataElement dataElement, String value, ImportSummary importSummary )
    {
        String status = ValidationUtils.dataValueIsValid( value, dataElement );

        if ( status != null )
        {
            importSummary.getConflicts().add( new ImportConflict( dataElement.getUid(), status ) );
            importSummary.getDataValueCount().incrementIgnored();
            return false;
        }

        return true;
    }

    private String getStoredBy( Event event, ImportSummary importSummary )
    {
        String storedBy = event.getStoredBy();

        if ( storedBy == null )
        {
            storedBy = currentUserService.getCurrentUsername();
        }
        else if ( storedBy.length() >= 31 )
        {
            if ( importSummary != null )
            {
                importSummary.getConflicts().add(
                    new ImportConflict( "storedBy", storedBy
                        + " is more than 31 characters, using current username instead." ) );
            }
            storedBy = currentUserService.getCurrentUsername();
        }
        return storedBy;
    }

    private void saveDataValue( ProgramStageInstance programStageInstance, String storedBy, DataElement dataElement,
        String value, Boolean providedElsewhere )
    {
        if ( value != null && value.trim().length() == 0 )
        {
            value = null;
        }

        PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance,
            dataElement );

        if ( value != null )
        {
            if ( patientDataValue == null )
            {
                patientDataValue = new PatientDataValue( programStageInstance, dataElement, new Date(), value );
                patientDataValue.setStoredBy( storedBy );
                patientDataValue.setProvidedElsewhere( providedElsewhere );

                patientDataValueService.savePatientDataValue( patientDataValue );
            }
            else
            {
                patientDataValue.setValue( value );
                patientDataValue.setTimestamp( new Date() );
                patientDataValue.setProvidedElsewhere( providedElsewhere );
                patientDataValue.setStoredBy( storedBy );

                patientDataValueService.updatePatientDataValue( patientDataValue );
            }
        }
        else if ( patientDataValue != null )
        {
            patientDataValueService.deletePatientDataValue( patientDataValue );
        }
    }

    private ProgramStageInstance createProgramStageInstance( ProgramStage programStage,
        ProgramInstance programInstance, OrganisationUnit organisationUnit, Date date, Boolean completed,
        Coordinate coordinate, String storedBy )
    {
        ProgramStageInstance programStageInstance = new ProgramStageInstance();
        updateProgramStageInstance( programStage, programInstance, organisationUnit, date, completed, coordinate,
            storedBy, programStageInstance );

        return programStageInstance;
    }

    private void updateProgramStageInstance( ProgramStage programStage, ProgramInstance programInstance,
        OrganisationUnit organisationUnit, Date date, Boolean completed, Coordinate coordinate, String storedBy,
        ProgramStageInstance programStageInstance )
    {
        programStageInstance.setProgramInstance( programInstance );
        programStageInstance.setProgramStage( programStage );
        programStageInstance.setDueDate( date );
        programStageInstance.setExecutionDate( date );
        programStageInstance.setOrganisationUnit( organisationUnit );

        if ( programStage.getCaptureCoordinates() )
        {
            if ( coordinate != null && coordinate.isValid() )
            {
                programStageInstance.setLongitude( coordinate.getLongitude() );
                programStageInstance.setLatitude( coordinate.getLatitude() );
            }
        }

        programStageInstance.setCompleted( completed );

        if ( programStageInstance.getId() == 0 )
        {
            programStageInstanceService.addProgramStageInstance( programStageInstance );
        }

        if ( programStageInstance.isCompleted() )
        {
            programStageInstance.setStatus( ProgramStageInstance.COMPLETED_STATUS );
            programStageInstance.setCompletedDate( new Date() );
            programStageInstance.setCompletedUser( storedBy );
            programStageInstanceService.completeProgramStageInstance( programStageInstance, i18nManager.getI18nFormat() );
        }
    }

    private ImportSummary saveEvent( Program program, ProgramInstance programInstance, ProgramStage programStage,
        ProgramStageInstance programStageInstance, OrganisationUnit organisationUnit, Event event,
        ImportOptions importOptions )
    {
        Assert.notNull( program );
        Assert.notNull( programInstance );
        Assert.notNull( programStage );

        ImportSummary importSummary = new ImportSummary();
        importSummary.setStatus( ImportStatus.SUCCESS );
        boolean dryRun = importOptions != null && importOptions.isDryRun();

        Date eventDate = DateUtils.getMediumDate( event.getEventDate() );

        if ( eventDate == null )
        {
            return new ImportSummary( ImportStatus.ERROR, "Event.eventDate is not in a valid format." );
        }

        String storedBy = getStoredBy( event, importSummary );

        if ( !dryRun )
        {
            if ( programStageInstance == null )
            {
                programStageInstance = createProgramStageInstance( programStage, programInstance, organisationUnit,
                    eventDate, EventStatus.COMPLETED.equals( event.getStatus() ), event.getCoordinate(), storedBy );
            }
            else
            {
                updateProgramStageInstance( programStage, programInstance, organisationUnit, eventDate,
                    EventStatus.COMPLETED.equals( event.getStatus() ), event.getCoordinate(), storedBy,
                    programStageInstance );
            }

            savePatientCommentFromEvent( programInstance, event, storedBy );

            importSummary.setReference( programStageInstance.getUid() );
        }

        for ( DataValue dataValue : event.getDataValues() )
        {
            DataElement dataElement = dataElementService.getDataElement( dataValue.getDataElement() );

            if ( dataElement == null )
            {
                importSummary.getConflicts().add(
                    new ImportConflict( "dataElement", dataValue.getDataElement() + " is not a valid dataElementId." ) );
                importSummary.getDataValueCount().incrementIgnored();
            }
            else
            {
                if ( validateDataValue( dataElement, dataValue.getValue(), importSummary ) )
                {
                    String dataValueStoredBy = dataValue.getStoredBy() != null ? dataValue.getStoredBy() : storedBy;

                    if ( !dryRun )
                    {
                        saveDataValue( programStageInstance, dataValueStoredBy, dataElement, dataValue.getValue(),
                            dataValue.getProvidedElsewhere() );
                    }

                    importSummary.getDataValueCount().incrementImported();
                }
            }
        }

        return importSummary;
    }

    private void savePatientCommentFromEvent( ProgramInstance programInstance, Event event, String storedBy )
    {
        for ( Note note : event.getNotes() )
        {
            PatientComment patientComment = new PatientComment();
            patientComment.setCreator( storedBy );
            patientComment.setCreatedDate( new Date() );
            patientComment.setCommentText( note.getValue() );

            patientCommentService.addPatientComment( patientComment );

            programInstance.getPatientComments().add( patientComment );

            programInstanceService.updateProgramInstance( programInstance );
        }
    }
}
