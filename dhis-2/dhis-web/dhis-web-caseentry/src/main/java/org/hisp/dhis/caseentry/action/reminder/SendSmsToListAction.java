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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.sms.SmsSender;
import org.hisp.dhis.sms.SmsServiceException;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * 
 * @version SendSmsToListAction.java 13:29:34 AM Aug 16, 2012 $
 */
public class SendSmsToListAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitSelectionManager selectionManager;

    private PatientService patientService;

    private SmsSender smsSender;

    private ProgramStageInstanceService programStageInstanceService;

    private CurrentUserService currentUserService;

    private I18n i18n;

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private List<String> searchTexts = new ArrayList<String>();

    private Boolean searchBySelectedOrgunit;

    private Boolean searchByUserOrgunits;

    private Boolean followup;

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    public void setSmsSender( SmsSender smsSender )
    {
        this.smsSender = smsSender;
    }

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    public void setFollowup( Boolean followup )
    {
        this.followup = followup;
    }

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    public void setSearchBySelectedOrgunit( Boolean searchBySelectedOrgunit )
    {
        this.searchBySelectedOrgunit = searchBySelectedOrgunit;
    }

    public void setSearchByUserOrgunits( Boolean searchByUserOrgunits )
    {
        this.searchByUserOrgunits = searchByUserOrgunits;
    }

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    public void setSearchTexts( List<String> searchTexts )
    {
        this.searchTexts = searchTexts;
    }

    private String msg;

    public void setMsg( String msg )
    {
        this.msg = msg;
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
        OrganisationUnit organisationUnit = selectionManager.getSelectedOrganisationUnit();
        Collection<OrganisationUnit> orgunits = new HashSet<OrganisationUnit>();

        if ( searchByUserOrgunits )
        {
            Collection<OrganisationUnit> userOrgunits = currentUserService.getCurrentUser().getOrganisationUnits();
            orgunits.addAll( userOrgunits );
        }
        else if ( searchBySelectedOrgunit )
        {
            orgunits.add( organisationUnit );
        }
        else
        {
            organisationUnit = null;
        }

        Collection<Integer> programStageInstanceIds = patientService.getProgramStageInstances( searchTexts, orgunits,
            followup, ProgramInstance.STATUS_ACTIVE, null, null );

        Set<String> phoneNumberList = new HashSet<String>( patientService.getPatientPhoneNumbers( searchTexts,
            orgunits, followup, ProgramInstance.STATUS_ACTIVE, null, null ) );
        try
        {
            OutboundSms outboundSms = new OutboundSms();
            outboundSms.setMessage( msg );
            outboundSms.setRecipients( phoneNumberList );
            outboundSms.setSender( currentUserService.getCurrentUsername() );

            smsSender.sendMessage( outboundSms, null );

            programStageInstanceService.updateProgramStageInstances( programStageInstanceIds, outboundSms );

            message = i18n.getString( "sent_message_success" );
        }
        catch ( SmsServiceException e )
        {
            message = e.getMessage();
        }

        return SUCCESS;
    }
}
