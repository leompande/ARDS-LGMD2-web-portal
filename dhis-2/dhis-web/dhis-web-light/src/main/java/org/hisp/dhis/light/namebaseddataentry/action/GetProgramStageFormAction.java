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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hisp.dhis.api.mobile.model.Activity;
import org.hisp.dhis.api.mobile.model.ActivityPlan;
import org.hisp.dhis.light.utils.NamebasedUtils;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageSection;
import org.hisp.dhis.program.ProgramStageSectionService;
import org.hisp.dhis.user.UserService;
import com.opensymphony.xwork2.Action;

public class GetProgramStageFormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private NamebasedUtils util;

    public void setUtil( NamebasedUtils util )
    {
        this.util = util;
    }

    public NamebasedUtils getUtil()
    {
        return util;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    private PatientDataValueService patientDataValueService;

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private ProgramStageSectionService programStageSectionService;

    public void setProgramStageSectionService( ProgramStageSectionService programStageSectionService )
    {
        this.programStageSectionService = programStageSectionService;
    }
    
    private UserService userService;
    
    public UserService getUserService()
    {
        return userService;
    }

    public void setUserService( UserService userService )
    {
        this.userService = userService;
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

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return this.organisationUnit;
    }

    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

    private ProgramStage programStage;

    public ProgramStage getProgramStage()
    {
        return this.programStage;
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

    private ActivityPlan activityPlan;

    public ActivityPlan getActivityPlan()
    {
        return this.activityPlan;
    }

    private List<Activity> activities;

    public List<Activity> getActivities()
    {
        return this.activities;
    }

    private Program program;

    public Program getProgram()
    {
        return this.program;
    }

    private boolean current;

    private Map<String, String> prevDataValues = new HashMap<String, String>();

    public Map<String, String> getPrevDataValues()
    {
        return prevDataValues;
    }

    private Patient patient;

    public Patient getPatient()
    {
        return patient;
    }

    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }

    private Integer programStageSectionId;

    public void setProgramStageSectionId( Integer programStageSectionId )
    {
        this.programStageSectionId = programStageSectionId;
    }

    public Integer getProgramStageSectionId()
    {
        return programStageSectionId;
    }

    private List<ProgramStageSection> listOfProgramStageSections;

    public List<ProgramStageSection> getListOfProgramStageSections()
    {
        return listOfProgramStageSections;
    }

    private ProgramStageSection programStageSection;

    public ProgramStageSection getProgramStageSection()
    {
        return programStageSection;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public boolean isCurrent()
    {
        return current;
    }

    public void setCurrent( boolean current )
    {
        this.current = current;
    }

    private List<ProgramStageDataElement> dataElements;

    public List<ProgramStageDataElement> getDataElements()
    {
        return dataElements;
    }

    public void setDataElements( List<ProgramStageDataElement> dataElements )
    {
        this.dataElements = dataElements;
    }

    @Override
    public String execute()
        throws Exception
    {
        prevDataValues.clear();
        programStage = util.getProgramStage( programId, programStageId );
        patient = patientService.getPatient( patientId );

        if ( programStageSectionId != null && programStageSectionId != 0 )
        {
            this.programStageSection = programStageSectionService.getProgramStageSection( this.programStageSectionId );
            dataElements = programStageSection.getProgramStageDataElements();
        }
        else
        {
            dataElements = new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );
        }

        program = programStageInstanceService.getProgramStageInstance( programStageInstanceId ).getProgramInstance()
            .getProgram();
        Collection<PatientDataValue> patientDataValues = patientDataValueService
            .getPatientDataValues( programStageInstanceService.getProgramStageInstance( programStageInstanceId ) );
        for ( PatientDataValue patientDataValue : patientDataValues )
        {
            prevDataValues.put( "DE" + patientDataValue.getDataElement().getId(), patientDataValue.getValue() );
            if ( patientDataValue.getProvidedElsewhere() != null )
            {
                prevDataValues.put( "CB" + patientDataValue.getDataElement().getId(), patientDataValue
                    .getProvidedElsewhere().toString() );
            }
        }
        return SUCCESS;
    }
}
