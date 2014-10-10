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

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * 
 * @version SetEventStatusAction.java 1:13:45 PM Sep 7, 2012 $
 */
public class SetEventStatusAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer programStageInstanceId;

    public void setProgramStageInstanceId( Integer programStageInstanceId )
    {
        this.programStageInstanceId = programStageInstanceId;
    }

    private Integer status;

    public void setStatus( Integer status )
    {
        this.status = status;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        ProgramStageInstance programStageInstance = programStageInstanceService
            .getProgramStageInstance( programStageInstanceId );

        switch ( status.intValue() )
        {
        case ProgramStageInstance.COMPLETED_STATUS:
            programStageInstanceService.completeProgramStageInstance( programStageInstance, format );
            break;
        case ProgramStageInstance.VISITED_STATUS:
            programStageInstance.setCompleted( false );
            programStageInstance.setStatus( ProgramStageInstance.ACTIVE_STATUS );
            programStageInstanceService.updateProgramStageInstance( programStageInstance );
            break;
        case ProgramStageInstance.LATE_VISIT_STATUS:
            programStageInstance.setCompleted( false );
            programStageInstance.setStatus(  ProgramStageInstance.ACTIVE_STATUS );
            programStageInstanceService.updateProgramStageInstance( programStageInstance );
            break;
        case ProgramStageInstance.FUTURE_VISIT_STATUS:
            programStageInstance.setCompleted( false );
            programStageInstance.setStatus(  ProgramStageInstance.ACTIVE_STATUS );
            programStageInstanceService.updateProgramStageInstance( programStageInstance );
            break;
        case ProgramStageInstance.SKIPPED_STATUS:
            programStageInstance.setStatus( status );
            programStageInstanceService.updateProgramStageInstance( programStageInstance );
            break;
        default:
            break;
        }

        return SUCCESS;
    }
}
