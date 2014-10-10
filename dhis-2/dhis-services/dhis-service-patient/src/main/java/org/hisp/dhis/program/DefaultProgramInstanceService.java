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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientReminder;
import org.hisp.dhis.patient.PatientReminderService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.patientcomment.PatientComment;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.sms.SmsSender;
import org.hisp.dhis.sms.SmsServiceException;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew
 */
@Transactional
public class DefaultProgramInstanceService
    implements ProgramInstanceService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramInstanceStore programInstanceStore;

    public void setProgramInstanceStore( ProgramInstanceStore programInstanceStore )
    {
        this.programInstanceStore = programInstanceStore;
    }

    private PatientAttributeValueService patientAttributeValueService;

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    private PatientDataValueService patientDataValueService;

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
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

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    public int addProgramInstance( ProgramInstance programInstance )
    {
        return programInstanceStore.save( programInstance );
    }

    public void deleteProgramInstance( ProgramInstance programInstance )
    {
        programInstanceStore.delete( programInstance );
    }

    public Collection<ProgramInstance> getAllProgramInstances()
    {
        return programInstanceStore.getAll();
    }

    public ProgramInstance getProgramInstance( int id )
    {
        return programInstanceStore.get( id );
    }

    @Override
    public ProgramInstance getProgramInstance( String id )
    {
        return programInstanceStore.getByUid( id );
    }

    public Collection<ProgramInstance> getProgramInstances( Integer status )
    {
        return programInstanceStore.getByStatus( status );
    }

    public void updateProgramInstance( ProgramInstance programInstance )
    {
        programInstanceStore.update( programInstance );
    }

    public Collection<ProgramInstance> getProgramInstances( Program program )
    {
        return programInstanceStore.get( program );
    }

    public Collection<ProgramInstance> getProgramInstances( Collection<Program> programs )
    {
        return programInstanceStore.get( programs );
    }

    public Collection<ProgramInstance> getProgramInstances( Collection<Program> programs,
        OrganisationUnit organisationUnit )
    {
        return programInstanceStore.get( programs, organisationUnit );
    }

    public Collection<ProgramInstance> getProgramInstances( Collection<Program> programs,
        OrganisationUnit organisationUnit, int status )
    {
        return programInstanceStore.get( programs, organisationUnit, status );
    }

    public Collection<ProgramInstance> getProgramInstances( Collection<Program> programs, Integer status )
    {
        return programInstanceStore.get( programs, status );
    }

    public Collection<ProgramInstance> getProgramInstances( Program program, Integer status )
    {
        return programInstanceStore.get( program, status );
    }

    public Collection<ProgramInstance> getProgramInstances( Patient patient, Integer status )
    {
        return programInstanceStore.get( patient, status );
    }

    public Collection<ProgramInstance> getProgramInstances( Patient patient, Program program )
    {
        return programInstanceStore.get( patient, program );
    }

    public Collection<ProgramInstance> getProgramInstances( Patient patient, Program program, Integer status )
    {
        return programInstanceStore.get( patient, program, status );
    }

    public Collection<ProgramInstance> getProgramInstances( Program program, OrganisationUnit organisationUnit,
        Integer min, Integer max )
    {
        return programInstanceStore.get( program, organisationUnit, min, max );
    }

    public Collection<ProgramInstance> getProgramInstances( Program program, OrganisationUnit organisationUnit,
        Date startDate, Date endDate )
    {
        return programInstanceStore.get( program, organisationUnit, startDate, endDate );
    }

    public Collection<ProgramInstance> getProgramInstances( Program program, Collection<Integer> orgunitIds,
        Date startDate, Date endDate, Integer min, Integer max )
    {
        return programInstanceStore.get( program, orgunitIds, startDate, endDate, min, max );
    }

    public int countProgramInstances( Program program, OrganisationUnit organisationUnit )
    {
        return programInstanceStore.count( program, organisationUnit );
    }

    public int countProgramInstances( Program program, Collection<Integer> orgunitIds, Date startDate, Date endDate )
    {
        return programInstanceStore.count( program, orgunitIds, startDate, endDate );
    }

    public List<Grid> getProgramInstanceReport( Patient patient, I18n i18n, I18nFormat format )
    {
        List<Grid> grids = new ArrayList<Grid>();

        // ---------------------------------------------------------------------
        // Attribues
        // ---------------------------------------------------------------------

        Collection<Program> programs = programService
            .getProgramsByCurrentUser( Program.MULTIPLE_EVENTS_WITH_REGISTRATION );
        programs.addAll( programService.getProgramsByCurrentUser( Program.SINGLE_EVENT_WITH_REGISTRATION ) );

        Collection<PatientAttributeValue> attributeValues = patientAttributeValueService
            .getPatientAttributeValues( patient );
        Iterator<PatientAttributeValue> iterAttribute = attributeValues.iterator();

        for ( Program program : programs )
        {
            List<PatientAttribute> atttributes = program.getAttributes();
            while ( iterAttribute.hasNext() )
            {
                PatientAttributeValue attributeValue = iterAttribute.next();
                if ( atttributes.contains( attributeValue.getPatientAttribute() ) )
                {
                    iterAttribute.remove();
                }
            }
        }

        // ---------------------------------------------------------------------
        // Identifier
        // ---------------------------------------------------------------------

        Collection<PatientIdentifier> identifiers = patient.getIdentifiers();
        Iterator<PatientIdentifier> iterIdentifier = identifiers.iterator();

        for ( Program program : programs )
        {
            List<PatientIdentifierType> identifierTypes = program.getIdentifierTypes();

            while ( iterIdentifier.hasNext() )
            {
                PatientIdentifier identifier = iterIdentifier.next();
                if ( !identifierTypes.contains( identifier.getIdentifierType() ) )
                {
                    iterIdentifier.remove();
                }
            }
        }

        // ---------------------------------------------------------------------
        // Get registered personal patient data
        // ---------------------------------------------------------------------

        if ( attributeValues.size() > 0 || identifiers.size() > 0 )
        {

            Grid attrGrid = new ListGrid();

            if ( patient.getName() == null )
            {
                attrGrid.setTitle( "" );
            }
            else
            {
                attrGrid.setTitle( patient.getName() );
            }
            attrGrid.setSubtitle( "" );

            attrGrid.addHeader( new GridHeader( i18n.getString( "name" ), false, true ) );
            attrGrid.addHeader( new GridHeader( i18n.getString( "value" ), false, true ) );
            attrGrid.addHeader( new GridHeader( "", true, false ) );

            for ( PatientAttributeValue attributeValue : attributeValues )
            {
                attrGrid.addRow();
                attrGrid.addValue( attributeValue.getPatientAttribute().getDisplayName() );
                String value = attributeValue.getValue();
                if ( attributeValue.getPatientAttribute().getValueType().equals( PatientAttribute.TYPE_BOOL ) )
                {
                    value = i18n.getString( value );
                }
                else
                {
                    if ( attributeValue.getPatientAttribute().getValueType().equals( PatientAttribute.TYPE_AGE )
                        && value != null )
                    {
                        Date date = format.parseDate( value );
                        value = PatientAttribute.getAgeFromDate( date ) + "";
                    }
                }

                attrGrid.addValue( value );
            }

            for ( PatientIdentifier identifier : identifiers )
            {
                attrGrid.addRow();
                PatientIdentifierType idType = identifier.getIdentifierType();
                if ( idType != null )
                {
                    attrGrid.addValue( idType.getName() );
                }
                else
                {
                    attrGrid.addValue( i18n.getString( "system_identifier" ) );

                }
                attrGrid.addValue( identifier.getIdentifier() );
            }

            grids.add( attrGrid );
        }

        // ---------------------------------------------------------------------
        // Get all program data registered
        // ---------------------------------------------------------------------

        Collection<ProgramInstance> programInstances = patient.getProgramInstances();

        if ( programInstances.size() > 0 )
        {
            for ( ProgramInstance programInstance : programInstances )
            {
                if ( programs.contains( programInstance.getProgram() ) )
                {
                    Grid gridProgram = getProgramInstanceReport( programInstance, i18n, format );

                    grids.add( gridProgram );
                }
            }
        }

        return grids;
    }

    public Grid getProgramInstanceReport( ProgramInstance programInstance, I18n i18n, I18nFormat format )
    {
        Grid grid = new ListGrid();

        // ---------------------------------------------------------------------
        // Get all program data registered
        // ---------------------------------------------------------------------

        grid.setTitle( programInstance.getProgram().getName() );
        grid.setSubtitle( "" );

        // ---------------------------------------------------------------------
        // Headers
        // ---------------------------------------------------------------------

        grid.addHeader( new GridHeader( "", false, false ) );
        grid.addHeader( new GridHeader( "", false, false ) );

        // ---------------------------------------------------------------------
        // Grids for program-stage-instance
        // ---------------------------------------------------------------------

        grid.addRow();
        grid.addValue( programInstance.getProgram().getDateOfEnrollmentDescription() );
        grid.addValue( format.formatDate( programInstance.getEnrollmentDate() ) );

        // Get patient-identifiers which belong to the program

        Patient patient = programInstance.getPatient();

        List<PatientIdentifierType> identifierTypes = programInstance.getProgram().getIdentifierTypes();

        Collection<PatientIdentifier> identifiers = patient.getIdentifiers();

        if ( identifierTypes != null && identifiers.size() > 0 )
        {
            for ( PatientIdentifierType identifierType : identifierTypes )
            {
                for ( PatientIdentifier identifier : identifiers )
                {
                    if ( identifier.getIdentifierType() != null
                        && identifier.getIdentifierType().equals( identifierType ) )
                    {
                        grid.addRow();
                        grid.addValue( identifierType.getDisplayName() );
                        grid.addValue( identifier.getIdentifier() );
                    }
                    else if ( identifier.getIdentifierType() == null )
                    {
                        grid.addRow();
                        grid.addValue( i18n.getString( "system_identifier" ) );
                        grid.addValue( identifier.getIdentifier() );
                    }
                }
            }
        }

        // Get patient-attribute-values which belong to the program

        Collection<PatientAttribute> atttributes = programInstance.getProgram().getAttributes();

        for ( PatientAttribute attrtibute : atttributes )
        {
            PatientAttributeValue attributeValue = patientAttributeValueService.getPatientAttributeValue( patient,
                attrtibute );
            if ( attributeValue != null )
            {
                grid.addRow();
                grid.addValue( attrtibute.getDisplayName() );
                grid.addValue( attributeValue.getValue() );
            }
        }

        // Get patient comments for the program instance

        Set<PatientComment> patientComments = programInstance.getPatientComments();

        for ( PatientComment patientComment : patientComments )
        {
            grid.addRow();
            grid.addValue( i18n.getString( "comment" ) + " " + i18n.getString( "on" ) + " "
                + format.formatDateTime( patientComment.getCreatedDate() ) );
            grid.addValue( patientComment.getCommentText() );
        }

        // Get sms of the program-instance

        List<OutboundSms> messasges = programInstance.getOutboundSms();

        for ( OutboundSms messasge : messasges )
        {
            grid.addRow();
            grid.addValue( i18n.getString( "message" ) + " " + i18n.getString( "on" ) + " "
                + format.formatDateTime( messasge.getDate() ) );
            grid.addValue( messasge.getMessage() );
        }

        // Get message conversations of the program-instance

        List<MessageConversation> conversations = programInstance.getMessageConversations();

        for ( MessageConversation conversation : conversations )
        {
            grid.addRow();
            grid.addValue( i18n.getString( "message" ) + " " + i18n.getString( "on" ) + " "
                + format.formatDateTime( conversation.getLastUpdated() ) );
            grid.addValue( conversation.getMessages().get( 0 ) );
        }

        // Program-instance attributes

        if ( programInstance.getProgram().getDisplayIncidentDate() != null
            && programInstance.getProgram().getDisplayIncidentDate() )
        {
            grid.addRow();
            grid.addValue( programInstance.getProgram().getDateOfIncidentDescription() );
            grid.addValue( format.formatDate( programInstance.getDateOfIncident() ) );
        }

        getProgramStageInstancesReport( grid, programInstance, format, i18n );

        return grid;
    }

    public int countProgramInstancesByStatus( Integer status, Program program, Collection<Integer> orgunitIds,
        Date startDate, Date endDate )
    {
        return programInstanceStore.countByStatus( status, program, orgunitIds, startDate, endDate );
    }

    public Collection<ProgramInstance> getProgramInstancesByStatus( Integer status, Program program,
        Collection<Integer> orgunitIds, Date startDate, Date endDate )
    {
        return programInstanceStore.getByStatus( status, program, orgunitIds, startDate, endDate );
    }

    public Collection<SchedulingProgramObject> getScheduleMesssages()
    {
        Collection<SchedulingProgramObject> result = programInstanceStore
            .getSendMesssageEvents( PatientReminder.ENROLLEMENT_DATE_TO_COMPARE );

        result.addAll( programInstanceStore.getSendMesssageEvents( PatientReminder.INCIDENT_DATE_TO_COMPARE ) );

        return result;
    }

    public Collection<OutboundSms> sendMessages( ProgramInstance programInstance, int status, I18nFormat format )
    {
        Patient patient = programInstance.getPatient();
        Collection<OutboundSms> outboundSmsList = new HashSet<OutboundSms>();

        Collection<PatientReminder> reminders = programInstance.getProgram().getPatientReminders();

        for ( PatientReminder rm : reminders )
        {
            if ( rm != null
                && rm.getWhenToSend() != null
                && rm.getWhenToSend() == status
                && (rm.getMessageType() == PatientReminder.MESSAGE_TYPE_DIRECT_SMS || rm.getMessageType() == PatientReminder.MESSAGE_TYPE_BOTH) )
            {
                OutboundSms outboundSms = sendProgramMessage( rm, programInstance, patient, format );

                if ( outboundSms != null )
                {
                    outboundSmsList.add( outboundSms );
                }
            }
        }

        return outboundSmsList;
    }

    @Override
    public Collection<MessageConversation> sendMessageConversations( ProgramInstance programInstance, int status,
        I18nFormat format )
    {
        Collection<MessageConversation> messageConversations = new HashSet<MessageConversation>();

        Collection<PatientReminder> reminders = programInstance.getProgram().getPatientReminders();
        for ( PatientReminder rm : reminders )
        {
            if ( rm != null
                && rm.getWhenToSend() != null
                && rm.getWhenToSend() == status
                && (rm.getMessageType() == PatientReminder.MESSAGE_TYPE_DHIS_MESSAGE || rm.getMessageType() == PatientReminder.MESSAGE_TYPE_BOTH) )
            {
                int id = messageService.sendMessage( programInstance.getProgram().getDisplayName(),
                    patientReminderService.getMessageFromTemplate( rm, programInstance, format ), null,
                    patientReminderService.getUsers( rm, programInstance.getPatient() ), null, false, true );
                messageConversations.add( messageService.getMessageConversation( id ) );
            }
        }

        return messageConversations;
    }

    @Override
    public ProgramInstance enrollPatient( Patient patient, Program program, Date enrollmentDate, Date dateOfIncident,
        OrganisationUnit organisationUnit, I18nFormat format )
    {
        // ---------------------------------------------------------------------
        // Add program instance
        // ---------------------------------------------------------------------

        ProgramInstance programInstance = new ProgramInstance();

        programInstance.enrollPatient( patient, program );

        if ( enrollmentDate != null )
        {
            programInstance.setEnrollmentDate( enrollmentDate );
        }
        else
        {
            programInstance.setEnrollmentDate( new Date() );
        }

        if ( dateOfIncident != null )
        {
            programInstance.setDateOfIncident( dateOfIncident );
        }
        else
        {
            programInstance.setDateOfIncident( new Date() );
        }

        programInstance.setStatus( ProgramInstance.STATUS_ACTIVE );
        addProgramInstance( programInstance );

        // ---------------------------------------------------------------------
        // Generate events for program instance
        // ---------------------------------------------------------------------

        for ( ProgramStage programStage : program.getProgramStages() )
        {
            if ( programStage.getAutoGenerateEvent() )
            {
                ProgramStageInstance programStageInstance = generateEvent( programInstance, programStage,
                    programInstance.getEnrollmentDate(), programInstance.getDateOfIncident(), organisationUnit );

                if ( programStageInstance != null )
                {
                    programStageInstanceService.addProgramStageInstance( programStageInstance );
                }
            }
        }

        // -----------------------------------------------------------------
        // Send messages after enrolling in program
        // -----------------------------------------------------------------

        List<OutboundSms> outboundSms = programInstance.getOutboundSms();

        if ( outboundSms == null )
        {
            outboundSms = new ArrayList<OutboundSms>();
        }

        outboundSms.addAll( sendMessages( programInstance, PatientReminder.SEND_WHEN_TO_EMROLLEMENT, format ) );

        // -----------------------------------------------------------------
        // Send message when to completed the program
        // -----------------------------------------------------------------

        List<MessageConversation> messages = programInstance.getMessageConversations();

        if ( messages == null )
        {
            messages = new ArrayList<MessageConversation>();
        }

        messages.addAll( sendMessageConversations( programInstance, PatientReminder.SEND_WHEN_TO_EMROLLEMENT, format ) );

        updateProgramInstance( programInstance );

        return programInstance;
    }

    @Override
    public boolean canAutoCompleteProgramInstanceStatus( ProgramInstance programInstance )
    {
        Set<ProgramStageInstance> stageInstances = programInstance.getProgramStageInstances();

        for ( ProgramStageInstance stageInstance : stageInstances )
        {
            if ( !stageInstance.isCompleted() || stageInstance.getProgramStage().getIrregular() )
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public void completeProgramInstanceStatus( ProgramInstance programInstance, I18nFormat format )
    {
        // ---------------------------------------------------------------------
        // Send sms-message when to completed the program
        // ---------------------------------------------------------------------

        List<OutboundSms> outboundSms = programInstance.getOutboundSms();

        if ( outboundSms == null )
        {
            outboundSms = new ArrayList<OutboundSms>();
        }

        outboundSms.addAll( sendMessages( programInstance, PatientReminder.SEND_WHEN_TO_C0MPLETED_PROGRAM, format ) );

        // -----------------------------------------------------------------
        // Send DHIS message when to completed the program
        // -----------------------------------------------------------------

        List<MessageConversation> messageConversations = programInstance.getMessageConversations();

        if ( messageConversations == null )
        {
            messageConversations = new ArrayList<MessageConversation>();
        }

        messageConversations.addAll( sendMessageConversations( programInstance,
            PatientReminder.SEND_WHEN_TO_C0MPLETED_PROGRAM, format ) );

        // -----------------------------------------------------------------
        // Update program-instance
        // -----------------------------------------------------------------

        programInstance.setStatus( ProgramInstance.STATUS_COMPLETED );
        programInstance.setEndDate( new Date() );

        updateProgramInstance( programInstance );
    }

    public void cancelProgramInstanceStatus( ProgramInstance programInstance )
    {
        // ---------------------------------------------------------------------
        // Set status of the program-instance
        // ---------------------------------------------------------------------

        Calendar today = Calendar.getInstance();
        PeriodType.clearTimeOfDay( today );
        Date currentDate = today.getTime();

        programInstance.setEndDate( currentDate );
        programInstance.setStatus( ProgramInstance.STATUS_CANCELLED );
        updateProgramInstance( programInstance );

        // ---------------------------------------------------------------------
        // Set statuses of the program-stage-instances
        // ---------------------------------------------------------------------

        for ( ProgramStageInstance programStageInstance : programInstance.getProgramStageInstances() )
        {
            if ( programStageInstance.getExecutionDate() == null )
            {
                // ---------------------------------------------------------------------
                // Set status as skipped for overdue events
                // ---------------------------------------------------------------------
                if ( programStageInstance.getDueDate().before( currentDate ) )
                {
                    programStageInstance.setStatus( ProgramStageInstance.SKIPPED_STATUS );
                    programStageInstanceService.updateProgramStageInstance( programStageInstance );
                }

                // ---------------------------------------------------------------------
                // Remove scheduled events
                // ---------------------------------------------------------------------
                else
                {
                    programStageInstanceService.deleteProgramStageInstance( programStageInstance );
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private ProgramStageInstance generateEvent( ProgramInstance programInstance, ProgramStage programStage,
        Date enrollmentDate, Date dateOfIncident, OrganisationUnit orgunit )
    {
        ProgramStageInstance programStageInstance = null;

        Date currentDate = new Date();
        Date dateCreatedEvent;

        if ( programStage.getGeneratedByEnrollmentDate() )
        {
            dateCreatedEvent = enrollmentDate;
        }
        else
        {
            dateCreatedEvent = dateOfIncident;
        }

        Date dueDate = DateUtils.getDateAfterAddition( dateCreatedEvent, programStage.getMinDaysFromStart() );

        if ( !programInstance.getProgram().getIgnoreOverdueEvents() || dueDate.before( currentDate ) )
        {
            programStageInstance = new ProgramStageInstance();
            programStageInstance.setProgramInstance( programInstance );
            programStageInstance.setProgramStage( programStage );
            programStageInstance.setDueDate( dueDate );

            if ( programInstance.getProgram().isSingleEvent() )
            {
                programStageInstance.setOrganisationUnit( orgunit );
                programStageInstance.setExecutionDate( dueDate );
            }
        }

        return programStageInstance;
    }

    private void getProgramStageInstancesReport( Grid grid, ProgramInstance programInstance, I18nFormat format,
        I18n i18n )
    {
        Collection<ProgramStageInstance> programStageInstances = programInstance.getProgramStageInstances();

        for ( ProgramStageInstance programStageInstance : programStageInstances )
        {
            grid.addRow();
            grid.addValue( "" );
            grid.addValue( "" );

            grid.addRow();
            grid.addValue( programStageInstance.getProgramStage().getName() );
            grid.addValue( "" );

            // -----------------------------------------------------------------
            // due-date && report-date
            // -----------------------------------------------------------------

            grid.addRow();
            grid.addValue( i18n.getString( "due_date" ) );
            grid.addValue( format.formatDate( programStageInstance.getDueDate() ) );

            if ( programStageInstance.getExecutionDate() != null )
            {
                grid.addRow();
                grid.addValue( programStageInstance.getProgramStage().getReportDateDescription() );
                grid.addValue( format.formatDate( programStageInstance.getExecutionDate() ) );
            }

            // SMS messages

            List<OutboundSms> messasges = programStageInstance.getOutboundSms();

            for ( OutboundSms messasge : messasges )
            {
                grid.addRow();
                grid.addValue( i18n.getString( "messsage" ) + " " + i18n.getString( "on" ) + " "
                    + format.formatDateTime( messasge.getDate() ) );
                grid.addValue( messasge.getMessage() );
            }

            // -----------------------------------------------------------------
            // Values
            // -----------------------------------------------------------------

            Collection<PatientDataValue> patientDataValues = patientDataValueService
                .getPatientDataValues( programStageInstance );

            for ( PatientDataValue patientDataValue : patientDataValues )
            {
                DataElement dataElement = patientDataValue.getDataElement();

                grid.addRow();
                grid.addValue( dataElement.getFormNameFallback() );

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
    }

    private OutboundSms sendProgramMessage( PatientReminder patientReminder, ProgramInstance programInstance,
        Patient patient, I18nFormat format )
    {
        Set<String> phoneNumbers = patientReminderService.getPhonenumbers( patientReminder, patient );
        OutboundSms outboundSms = null;

        if ( phoneNumbers.size() > 0 )
        {
            String msg = patientReminderService.getMessageFromTemplate( patientReminder, programInstance, format );

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
