package org.hisp.dhis.caseentry.action.reminder;

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
import java.util.List;
import java.util.Set;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.patient.PatientReminder;
import org.hisp.dhis.patient.PatientReminderService;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.sms.SmsSender;
import org.hisp.dhis.sms.SmsServiceException;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * 
 * @version SendSmsAction.java 11:17:37 AM Aug 9, 2012 $
 */
public class SendSmsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SmsSender smsSender;

    public void setSmsSender( SmsSender smsSender )
    {
        this.smsSender = smsSender;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
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

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer programStageInstanceId;

    public void setProgramStageInstanceId( Integer programStageInstanceId )
    {
        this.programStageInstanceId = programStageInstanceId;
    }

    private Integer programInstanceId;

    public void setProgramInstanceId( Integer programInstanceId )
    {
        this.programInstanceId = programInstanceId;
    }

    private String msg;

    public void setMsg( String msg )
    {
        this.msg = msg;
    }

    private int sendTo;

    public void setSendTo( int sendTo )
    {
        this.sendTo = sendTo;
    }

    private String message = "";

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        if ( programStageInstanceId != null )
        {
            sendSMSToEvent();
        }
        else if ( programInstanceId != null )
        {
            sendSMSToProgram();
        }

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String sendSMSToEvent()
    {
        ProgramStageInstance programStageInstance = programStageInstanceService
            .getProgramStageInstance( programStageInstanceId );

        PatientReminder patientReminder = new PatientReminder();
        patientReminder.setTemplateMessage( msg );
        patientReminder.setSendTo( sendTo );

        Set<String> phoneNumbers = patientReminderService.getPhonenumbers( patientReminder, programStageInstance
            .getProgramInstance().getPatient() );

        try
        {
            OutboundSms outboundSms = new OutboundSms();
            outboundSms.setMessage( msg );
            outboundSms.setRecipients( phoneNumbers );
            outboundSms.setSender( currentUserService.getCurrentUsername() );
            smsSender.sendMessage( outboundSms, null );

            List<OutboundSms> outboundSmsList = programStageInstance.getOutboundSms();
            if ( outboundSmsList == null )
            {
                outboundSmsList = new ArrayList<OutboundSms>();
            }
            outboundSmsList.add( outboundSms );

            programStageInstance.setOutboundSms( outboundSmsList );
            programStageInstanceService.updateProgramStageInstance( programStageInstance );
            message = i18n.getString( "message_is_sent" + " " + phoneNumbers );
            return ERROR;
        }
        catch ( SmsServiceException e )
        {
            message = e.getMessage();

            return ERROR;
        }
    }

    private String sendSMSToProgram()
    {
        ProgramInstance programInstance = programInstanceService
            .getProgramInstance( programInstanceId );

        PatientReminder patientReminder = new PatientReminder();
        patientReminder.setTemplateMessage( msg );
        patientReminder.setSendTo( sendTo );

        Set<String> phoneNumbers = patientReminderService.getPhonenumbers( patientReminder, programInstance.getPatient() );

        try
        {
            OutboundSms outboundSms = new OutboundSms();
            outboundSms.setMessage( msg );
            outboundSms.setRecipients( phoneNumbers );
            outboundSms.setSender( currentUserService.getCurrentUsername() );
            smsSender.sendMessage( outboundSms, null );

            List<OutboundSms> outboundSmsList = programInstance.getOutboundSms();
            if ( outboundSmsList == null )
            {
                outboundSmsList = new ArrayList<OutboundSms>();
            }
            outboundSmsList.add( outboundSms );

            programInstance.setOutboundSms( outboundSmsList );
            programInstanceService.updateProgramInstance( programInstance );
            message = i18n.getString( "message_is_sent" + " " + phoneNumbers );

            return SUCCESS;
        }
        catch ( SmsServiceException e )
        {
            message = e.getMessage();

            return ERROR;
        }
    }
}
