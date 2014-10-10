package org.hisp.dhis.light.namebaseddataentry.action;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.light.utils.NamebasedUtils;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageSection;

import com.opensymphony.xwork2.Action;

/**
 * @author Nguyen Kim Lai
 * 
 * @version $ GetProgramStageSectionAction.java Oct 10, 2012 $
 */
public class GetProgramStageSectionAction
    implements Action
{
    private static final String REDIRECT = "redirect";

    private static final String REDIRECT_COMPLETED_FORM = "redirectCompletedForm";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private NamebasedUtils util;

    public void setUtil( NamebasedUtils util )
    {
        this.util = util;
    }

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer programInstanceId;

    public Integer getProgramInstanceId()
    {
        return programInstanceId;
    }

    public void setProgramInstanceId( Integer programInstanceId )
    {
        this.programInstanceId = programInstanceId;
    }

    private Integer programStageInstanceId;

    public Integer getProgramStageInstanceId()
    {
        return programStageInstanceId;
    }

    public void setProgramStageInstanceId( Integer programStageInstanceId )
    {
        this.programStageInstanceId = programStageInstanceId;
    }

    private Integer patientId;

    public Integer getPatientId()
    {
        return patientId;
    }

    public void setPatientId( Integer patientId )
    {
        this.patientId = patientId;
    }

    private Patient patient;

    public Patient getPatient()
    {
        return patient;
    }

    private Integer programId;

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    public Integer getProgramId()
    {
        return programId;
    }

    private Integer programStageId;

    public void setProgramStageId( Integer programStageId )
    {
        this.programStageId = programStageId;
    }

    public Integer getProgramStageId()
    {
        return programStageId;
    }

    private Integer orgUnitId;

    public void setOrgUnitId( Integer orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    public Integer getOrgUnitId()
    {
        return this.orgUnitId;
    }

    private ProgramStage programStage;

    public ProgramStage getProgramStage()
    {
        return this.programStage;
    }

    private List<ProgramStageSection> listOfProgramStageSections;

    public List<ProgramStageSection> getListOfProgramStageSections()
    {
        return listOfProgramStageSections;
    }

    private List<ProgramStageDataElement> listOfProgramStageDataElement;

    public List<ProgramStageDataElement> getListOfProgramStageDataElement()
    {
        return listOfProgramStageDataElement;
    }

    public DateFormat getDateFormat()
    {
        return new SimpleDateFormat( "yyyy-MM-dd" );
    }

    public ProgramStageInstance programStageInstance;

    public ProgramStageInstance getProgramStageInstance()
    {
        return programStageInstance;
    }

    private Program program;

    public Program getProgram()
    {
        return this.program;
    }

    private boolean validated;

    public boolean isValidated()
    {
        return validated;
    }

    public void setValidated( boolean validated )
    {
        this.validated = validated;
    }

    public String sectionName;

    public String getSectionName()
    {
        return sectionName;
    }

    public Integer programStageSectionId;

    public void setProgramStageSectionId( Integer programStageSectionId )
    {
        this.programStageSectionId = programStageSectionId;
    }

    @Override
    public String execute()
        throws Exception
    {

        program = programService.getProgram( programId );

        programStageInstance = programStageInstanceService.getProgramStageInstance( programStageInstanceId );

        patient = patientService.getPatient( patientId );

        programStage = util.getProgramStage( programId, programStageId );

        this.listOfProgramStageSections = new ArrayList<ProgramStageSection>( programStage.getProgramStageSections() );

        if ( programStageSectionId != null && programStageSectionId != 0 )
        {
            for ( ProgramStageSection each : this.listOfProgramStageSections )
            {
                if ( each.getId() == programStageSectionId )
                {
                    sectionName = each.getName();
                    break;
                }
            }
        }

        if ( this.listOfProgramStageSections.size() == 0 && programStageInstance.isCompleted() == true
            && programStage.getBlockEntryForm() )
        {
            return REDIRECT_COMPLETED_FORM;
        }
        else if ( this.listOfProgramStageSections.size() == 0 )
        {
            return REDIRECT;
        }

        return SUCCESS;
    }

}
