package org.hisp.dhis.program;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.message.MessageConversation;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientReminder;
import org.hisp.dhis.patient.PatientReminderService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.sms.SmsSender;
import org.hisp.dhis.sms.SmsServiceException;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
@Transactional
public class DefaultProgramStageInstanceService
    implements ProgramStageInstanceService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramStageInstanceStore programStageInstanceStore;

    public void setProgramStageInstanceStore( ProgramStageInstanceStore programStageInstanceStore )
    {
        this.programStageInstanceStore = programStageInstanceStore;
    }

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    private PatientDataValueService patientDataValueService;

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    private SmsSender smsSender;

    public void setSmsSender( SmsSender smsSender )
    {
        this.smsSender = smsSender;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private PatientReminderService patientReminderService;

    public void setPatientReminderService( PatientReminderService patientReminderService )
    {
        this.patientReminderService = patientReminderService;
    }

    private MessageService messageService;

    public void setMessageService( MessageService messageService )
    {
        this.messageService = messageService;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    public int addProgramStageInstance( ProgramStageInstance programStageInstance )
    {
        programStageInstance.setAutoFields();
        return programStageInstanceStore.save( programStageInstance );
    }

    @Override
    public void deleteProgramStageInstance( ProgramStageInstance programStageInstance )
    {
        programStageInstanceStore.delete( programStageInstance );
    }

    @Override
    public ProgramStageInstance getProgramStageInstance( int id )
    {
        return programStageInstanceStore.get( id );
    }

    @Override
    public ProgramStageInstance getProgramStageInstance( String uid )
    {
        return programStageInstanceStore.getByUid( uid );
    }

    @Override
    public ProgramStageInstance getProgramStageInstance( ProgramInstance programInstance, ProgramStage programStage )
    {
        return programStageInstanceStore.get( programInstance, programStage );
    }

    @Override
    public Collection<ProgramStageInstance> getProgramStageInstances( ProgramInstance programInstance,
        ProgramStage programStage )
    {
        return programStageInstanceStore.getAll( programInstance, programStage );
    }

    @Override
    public void updateProgramStageInstance( ProgramStageInstance programStageInstance )
    {
        programStageInstance.setAutoFields();
        programStageInstanceStore.update( programStageInstance );
    }

    @Override
    public Map<Integer, Integer> statusProgramStageInstances( Collection<ProgramStageInstance> programStageInstances )
    {
        Map<Integer, Integer> colorMap = new HashMap<Integer, Integer>();

        for ( ProgramStageInstance programStageInstance : programStageInstances )
        {
            colorMap.put( programStageInstance.getId(), programStageInstance.getEventStatus() );
        }

        return colorMap;
    }

    @Override
    public Collection<ProgramStageInstance> getProgramStageInstances( Collection<ProgramInstance> programInstances,
        boolean completed )
    {
        return programStageInstanceStore.get( programInstances, completed );
    }

    @Override
    public List<ProgramStageInstance> getProgramStageInstances( Patient patient, Boolean completed )
    {
        return programStageInstanceStore.get( patient, completed );
    }

    @Override
    public List<Grid> getProgramStageInstancesReport( ProgramInstance programInstance, I18nFormat format, I18n i18n )
    {
        List<Grid> grids = new ArrayList<Grid>();

        Collection<ProgramStageInstance> programStageInstances = programInstance.getProgramStageInstances();

        for ( ProgramStageInstance programStageInstance : programStageInstances )
        {
            Grid grid = new ListGrid();

            // -----------------------------------------------------------------
            // Title
            // -----------------------------------------------------------------

            Date executionDate = programStageInstance.getExecutionDate();
            String executionDateValue = (executionDate != null) ? format.formatDate( programStageInstance
                .getExecutionDate() ) : "[" + i18n.getString( "none" ) + "]";

            grid.setTitle( programStageInstance.getProgramStage().getName() );
            grid.setSubtitle( i18n.getString( "due_date" ) + ": "
                + format.formatDate( programStageInstance.getDueDate() ) + " - " + i18n.getString( "report_date" )
                + ": " + executionDateValue );

            // -----------------------------------------------------------------
            // Headers
            // -----------------------------------------------------------------

            grid.addHeader( new GridHeader( i18n.getString( "name" ), false, true ) );
            grid.addHeader( new GridHeader( i18n.getString( "value" ), false, true ) );

            // -----------------------------------------------------------------
            // Values
            // -----------------------------------------------------------------

            Collection<PatientDataValue> patientDataValues = patientDataValueService
                .getPatientDataValues( programStageInstance );

            if ( executionDate == null || patientDataValues == null || patientDataValues.size() == 0 )
            {
                grid.addRow();
                grid.addValue( "[" + i18n.getString( "none" ) + "]" );
                grid.addValue( "" );
            }
            else
            {
                for ( PatientDataValue patientDataValue : patientDataValues )
                {
                    DataElement dataElement = patientDataValue.getDataElement();

                    grid.addRow();
                    grid.addValue( dataElement.getName() );

                    if ( dataElement.getType().equals( DataElement.VALUE_TYPE_BOOL ) )
                    {
                        grid.addValue( i18n.getString( patientDataValue.getValue() ) );
                    }
                    else
                    {
                        grid.addValue( patientDataValue.getValue() );
                    }
                }
            }

            grids.add( grid );
        }

        return grids;
    }

    @Override
    public void removeEmptyEvents( ProgramStage programStage, OrganisationUnit organisationUnit )
    {
        programStageInstanceStore.removeEmptyEvents( programStage, organisationUnit );
    }

    @Override
    public void updateProgramStageInstances( Collection<Integer> programStageInstanceIds, OutboundSms outboundSms )
    {
        programStageInstanceStore.update( programStageInstanceIds, outboundSms );
    }

    @Override
    public Collection<SchedulingProgramObject> getSendMesssageEvents()
    {
        return programStageInstanceStore.getSendMesssageEvents();
    }

    @Override
    public Collection<ProgramStageInstance> getProgramStageInstances( Program program, Collection<Integer> orgunitIds,
        Date startDate, Date endDate, Boolean completed )
    {
        return programStageInstanceStore.get( program, orgunitIds, startDate, endDate, completed );
    }

    @Override
    public Grid getStatisticalReport( Program program, Collection<Integer> orgunitIds, Date startDate, Date endDate,
        I18n i18n, I18nFormat format )
    {
        Grid grid = new ListGrid();
        grid.setTitle( i18n.getString( "program_overview" ) + " - " + program.getDisplayName() );
        grid.setSubtitle( i18n.getString( "from" ) + " " + format.formatDate( startDate ) + "  "
            + i18n.getString( "to" ) + " " + format.formatDate( endDate ) );

        grid.addHeader( new GridHeader( "", false, true ) );
        grid.addHeader( new GridHeader( "", false, false ) );
        grid.addHeader( new GridHeader( "", false, false ) );
        grid.addHeader( new GridHeader( "", false, false ) );
        grid.addHeader( new GridHeader( "", false, false ) );
        grid.addHeader( new GridHeader( "", false, false ) );
        grid.addHeader( new GridHeader( "", false, false ) );
        grid.addHeader( new GridHeader( "", false, false ) );

        // Total new enrollments in the period

        int total = programInstanceService.countProgramInstances( program, orgunitIds, startDate, endDate );
        grid.addRow();
        grid.addValue( i18n.getString( "total_new_enrollments_in_this_period" ) );
        grid.addValue( total );
        grid.addValue( "" );
        grid.addValue( "" );
        grid.addValue( "" );
        grid.addValue( "" );
        grid.addValue( "" );
        grid.addValue( "" );

        // Total programs completed in this period

        int totalCompleted = programInstanceService.countProgramInstancesByStatus( ProgramInstance.STATUS_COMPLETED,
            program, orgunitIds, startDate, endDate );
        grid.addRow();
        grid.addValue( i18n.getString( "total_programs_completed_in_this_period" ) );
        grid.addValue( totalCompleted );
        grid.addValue( "" );
        grid.addValue( "" );
        grid.addValue( "" );
        grid.addValue( "" );
        grid.addValue( "" );
        grid.addValue( "" );

        // Total programs discontinued (un-enrollments)

        int totalDiscontinued = programInstanceService.countProgramInstancesByStatus( ProgramInstance.STATUS_CANCELLED,
            program, orgunitIds, startDate, endDate );
        grid.addRow();
        grid.addValue( i18n.getString( "total_programs_discontinued_unenrollments" ) );
        grid.addValue( totalDiscontinued );
        grid.addValue( "" );
        grid.addValue( "" );
        grid.addValue( "" );
        grid.addValue( "" );
        grid.addValue( "" );
        grid.addValue( "" );

        // Average number of stages for complete programs

        grid.addRow();
        grid.addValue( i18n.getString( "average_number_of_stages_for_complete_programs" ) );
        double percent = 0.0;
        if ( totalCompleted != 0 )
        {
            int stageCompleted = averageNumberCompletedProgramInstance( program, orgunitIds, startDate, endDate,
                ProgramInstance.STATUS_ACTIVE );
            percent = (stageCompleted + 0.0) / totalCompleted;
        }
        grid.addValue( format.formatValue( percent ) );
        grid.addValue( "" );
        grid.addValue( "" );
        grid.addValue( "" );
        grid.addValue( "" );
        grid.addValue( "" );
        grid.addValue( "" );

        // Add empty row

        grid.addRow();
        grid.addValue( "" );
        grid.addValue( "" );
        grid.addValue( "" );
        grid.addValue( "" );
        grid.addValue( "" );
        grid.addValue( "" );
        grid.addValue( "" );
        grid.addValue( "" );

        // Summary by stage

        grid.addRow();
        grid.addValue( i18n.getString( "summary_by_stage" ) );
        grid.addValue( "" );
        grid.addValue( "" );
        grid.addValue( "" );
        grid.addValue( "" );
        grid.addValue( "" );
        grid.addValue( "" );
        grid.addValue( "" );

        // Add titles for stage details

        grid.addRow();
        grid.addValue( i18n.getString( "program_stages" ) );
        grid.addValue( i18n.getString( "visits_scheduled_all" ) );
        grid.addValue( i18n.getString( "visits_done" ) );
        grid.addValue( i18n.getString( "visits_done_percent" ) );
        grid.addValue( i18n.getString( "forms_completed" ) );
        grid.addValue( i18n.getString( "forms_completed_percent" ) );
        grid.addValue( i18n.getString( "visits_overdue" ) );
        grid.addValue( i18n.getString( "visits_overdue_percent" ) );

        // Add values for stage details

        for ( ProgramStage programStage : program.getProgramStages() )
        {
            grid.addRow();
            grid.addValue( programStage.getDisplayName() );

            // Visits scheduled (All)
            int totalAll = programStageInstanceStore.count( programStage, orgunitIds, startDate, endDate, null );
            grid.addValue( totalAll );

            // Visits done (#) = Incomplete + Complete stages.
            int totalCompletedEvent = programStageInstanceStore.count( programStage, orgunitIds, startDate, endDate,
                true );
            int totalVisit = totalCompletedEvent
                + programStageInstanceStore.count( programStage, orgunitIds, startDate, endDate, false );
            grid.addValue( totalVisit );

            // Visits done (%)

            percent = 0.0;
            if ( totalAll != 0 )
            {
                percent = (totalVisit + 0.0) * 100 / totalAll;
            }
            grid.addValue( format.formatValue( percent ) + "%" );

            // Forms completed (#) = Program stage instances where the user has
            // clicked complete.

            grid.addValue( totalCompletedEvent );

            // Forms completed (%)
            if ( totalAll != 0 )
            {
                percent = (totalCompletedEvent + 0.0) * 100 / totalAll;
            }
            grid.addValue( format.formatValue( percent ) + "%" );

            // Visits overdue (#)

            int overdue = this.getOverDueEventCount( programStage, orgunitIds, startDate, endDate );
            grid.addValue( overdue );

            // Visits overdue (%)

            percent = 0.0;
            if ( totalAll != 0 )
            {
                percent = (overdue + 0.0) * 100 / totalAll;
            }
            grid.addValue( format.formatValue( percent ) + "%" );
        }

        return grid;
    }

    @Override
    public List<ProgramStageInstance> getStatisticalProgramStageDetailsReport( ProgramStage programStage,
        Collection<Integer> orgunitIds, Date startDate, Date endDate, int status, Integer min, Integer max )
    {
        return programStageInstanceStore.getStatisticalProgramStageDetailsReport( programStage, orgunitIds, startDate,
            endDate, status, min, max );
    }

    @Override
    public int getOverDueEventCount( ProgramStage programStage, Collection<Integer> orgunitIds, Date startDate,
        Date endDate )
    {
        return programStageInstanceStore.getOverDueCount( programStage, orgunitIds, startDate, endDate );
    }

    @Override
    public int averageNumberCompletedProgramInstance( Program program, Collection<Integer> orgunitIds, Date startDate,
        Date endDate, int status )
    {
        return programStageInstanceStore.averageNumberCompleted( program, orgunitIds, startDate, endDate, status );
    }

    @Override
    public Collection<Integer> getOrganisationUnitIds( Date startDate, Date endDate )
    {
        return programStageInstanceStore.getOrgunitIds( startDate, endDate );
    }

    @Override
    public Grid getCompletenessProgramStageInstance( Collection<Integer> orgunitIds, Program program, String startDate,
        String endDate, I18n i18n )
    {
        return programStageInstanceStore.getCompleteness( orgunitIds, program, startDate, endDate, i18n );
    }

    @Override
    public Collection<OutboundSms> sendMessages( ProgramStageInstance programStageInstance, int status,
        I18nFormat format )
    {
        Patient patient = programStageInstance.getProgramInstance().getPatient();
        Collection<OutboundSms> outboundSmsList = new HashSet<OutboundSms>();

        Collection<PatientReminder> reminders = programStageInstance.getProgramStage().getPatientReminders();
        for ( PatientReminder rm : reminders )
        {
            if ( rm != null
                && rm.getWhenToSend() != null
                && rm.getWhenToSend() == status
                && (rm.getMessageType() == PatientReminder.MESSAGE_TYPE_DIRECT_SMS || rm.getMessageType() == PatientReminder.MESSAGE_TYPE_BOTH) )
            {
                OutboundSms outboundSms = sendEventMessage( rm, programStageInstance, patient, format );
                if ( outboundSms != null )
                {
                    outboundSmsList.add( outboundSms );
                }
            }
        }

        return outboundSmsList;
    }

    @Override
    public Collection<MessageConversation> sendMessageConversations( ProgramStageInstance programStageInstance,
        int status, I18nFormat format )
    {
        Collection<MessageConversation> messageConversations = new HashSet<MessageConversation>();

        Collection<PatientReminder> reminders = programStageInstance.getProgramStage().getPatientReminders();
        for ( PatientReminder rm : reminders )
        {
            if ( rm != null
                && rm.getWhenToSend() != null
                && rm.getWhenToSend() == status
                && (rm.getMessageType() == PatientReminder.MESSAGE_TYPE_DHIS_MESSAGE || rm.getMessageType() == PatientReminder.MESSAGE_TYPE_BOTH) )
            {
                int id = messageService.sendMessage( programStageInstance.getProgramStage().getDisplayName(),
                    patientReminderService.getMessageFromTemplate( rm, programStageInstance, format ), null,
                    patientReminderService.getUsers( rm, programStageInstance.getProgramInstance().getPatient() ),
                    null, false, true );
                messageConversations.add( messageService.getMessageConversation( id ) );
            }
        }

        return messageConversations;
    }

    @Override
    public void completeProgramStageInstance( ProgramStageInstance programStageInstance, I18nFormat format )
    {
        programStageInstance.setCompleted( true );

        Calendar today = Calendar.getInstance();
        PeriodType.clearTimeOfDay( today );
        Date date = today.getTime();

        programStageInstance.setStatus( ProgramStageInstance.COMPLETED_STATUS );
        programStageInstance.setCompletedDate( date );
        programStageInstance.setCompletedUser( currentUserService.getCurrentUsername() );

        // ---------------------------------------------------------------------
        // Send sms-message when to completed the event
        // ---------------------------------------------------------------------

        List<OutboundSms> outboundSms = programStageInstance.getOutboundSms();

        if ( outboundSms == null )
        {
            outboundSms = new ArrayList<OutboundSms>();
        }

        outboundSms.addAll( sendMessages( programStageInstance, PatientReminder.SEND_WHEN_TO_C0MPLETED_EVENT, format ) );

        // ---------------------------------------------------------------------
        // Send DHIS message when to completed the event
        // ---------------------------------------------------------------------

        List<MessageConversation> messageConversations = programStageInstance.getMessageConversations();

        if ( messageConversations == null )
        {
            messageConversations = new ArrayList<MessageConversation>();
        }

        messageConversations.addAll( sendMessageConversations( programStageInstance,
            PatientReminder.SEND_WHEN_TO_C0MPLETED_EVENT, format ) );

        // ---------------------------------------------------------------------
        // Update the event
        // ---------------------------------------------------------------------

        updateProgramStageInstance( programStageInstance );
    }

    @Override
    public void setExecutionDate( ProgramStageInstance programStageInstance, Date executionDate,
        OrganisationUnit organisationUnit )
    {
        programStageInstance.setExecutionDate( executionDate );
        programStageInstance.setOrganisationUnit( organisationUnit );

        if ( programStageInstance.getProgramInstance().getProgram().isSingleEvent() )
        {
            programStageInstance.setDueDate( executionDate );
        }

        updateProgramStageInstance( programStageInstance );
    }

    /**
     * For the first case of an anonymous program, the program-instance doesn't
     * exist, So system has to create a program-instance and
     * program-stage-instance. The similar thing happens for single event with
     * registration.
     */
    @Override
    public ProgramStageInstance createProgramStageInstance( Patient patient, Program program, Date executionDate,
        OrganisationUnit organisationUnit )
    {
        ProgramStage programStage = null;

        if ( program.getProgramStages() != null )
        {
            programStage = program.getProgramStages().iterator().next();
        }

        int type = program.getType();
        ProgramInstance programInstance = null;

        if ( type == Program.SINGLE_EVENT_WITH_REGISTRATION )
        {
            // Add a new program-instance
            programInstance = new ProgramInstance();
            programInstance.setEnrollmentDate( executionDate );
            programInstance.setDateOfIncident( executionDate );
            programInstance.setProgram( program );
            programInstance.setStatus( ProgramInstance.STATUS_ACTIVE );

            programInstance.setPatient( patient );

            programInstanceService.addProgramInstance( programInstance );
        }
        else if ( type == Program.SINGLE_EVENT_WITHOUT_REGISTRATION )
        {
            Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( program );
            if ( programInstances == null || programInstances.size() == 0 )
            {
                // Add a new program-instance if it doesn't exist
                programInstance = new ProgramInstance();
                programInstance.setEnrollmentDate( executionDate );
                programInstance.setDateOfIncident( executionDate );
                programInstance.setProgram( program );
                programInstance.setStatus( ProgramInstance.STATUS_ACTIVE );
                programInstanceService.addProgramInstance( programInstance );
            }
            else
            {
                programInstance = programInstanceService.getProgramInstances( program ).iterator().next();
            }
        }

        // Add a new program-stage-instance
        ProgramStageInstance programStageInstance = new ProgramStageInstance();
        programStageInstance.setProgramInstance( programInstance );
        programStageInstance.setProgramStage( programStage );
        programStageInstance.setDueDate( executionDate );
        programStageInstance.setExecutionDate( executionDate );
        programStageInstance.setOrganisationUnit( organisationUnit );

        addProgramStageInstance( programStageInstance );

        return programStageInstance;
    }

    @Override
    public Grid searchEvents( ProgramStage programStage, List<TabularEventColumn> columns,
        Collection<Integer> organisationUnits, Date startDate, Date endDate, Boolean completed, Integer min,
        Integer max, I18n i18n )
    {
        return programStageInstanceStore.searchEvent( programStage, organisationUnits, columns, startDate, endDate,
            completed, min, max, i18n );
    }

    @Override
    public int searchEventsCount( ProgramStage programStage, List<TabularEventColumn> columns,
        Collection<Integer> organisationUnits, Boolean completed, Date startDate, Date endDate )
    {
        return programStageInstanceStore.searchEventsCount( programStage, columns, organisationUnits, startDate,
            endDate, completed );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private OutboundSms sendEventMessage( PatientReminder patientReminder, ProgramStageInstance programStageInstance,
        Patient patient, I18nFormat format )
    {
        Set<String> phoneNumbers = patientReminderService.getPhonenumbers( patientReminder, patient );
        OutboundSms outboundSms = null;

        if ( phoneNumbers.size() > 0 )
        {
            String msg = patientReminderService.getMessageFromTemplate( patientReminder, programStageInstance, format );
            try
            {
                outboundSms = new OutboundSms();
                outboundSms.setMessage( msg );
                outboundSms.setRecipients( phoneNumbers );
                outboundSms.setSender( currentUserService.getCurrentUsername() );
                smsSender.sendMessage( outboundSms, null );
            }
            catch ( SmsServiceException e )
            {
                e.printStackTrace();
            }
        }

        return outboundSms;
    }

}