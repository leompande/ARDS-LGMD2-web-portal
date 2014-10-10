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

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GetProgramStageListAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientService patientService;

    public PatientService getPatientService()
    {
        return patientService;
    }

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Set<ProgramStageInstance> programStageInstances;

    public Set<ProgramStageInstance> getProgramStageInstances()
    {
        return programStageInstances;
    }

    public void setProgramStageInstances( Set<ProgramStageInstance> programStageInstances )
    {
        this.programStageInstances = programStageInstances;
    }

    private int programInstanceId;

    public int getProgramInstanceId()
    {
        return programInstanceId;
    }

    public void setProgramInstanceId( int programInstanceId )
    {
        this.programInstanceId = programInstanceId;
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

    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }

    private int programId;

    public int getProgramId()
    {
        return programId;
    }

    public void setProgramId( int programId )
    {
        this.programId = programId;
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

    private Set<ProgramStage> repeatableStages;

    public Set<ProgramStage> getRepeatableStages()
    {
        return repeatableStages;
    }

    public void setRepeatableStages( Set<ProgramStage> repeatableStages )
    {
        this.repeatableStages = repeatableStages;
    }

    private Map<Integer, ProgramStage> exclusedRepeatableStages;

    public Map<Integer, ProgramStage> getExclusedRepeatableStages()
    {
        return exclusedRepeatableStages;
    }

    public void setExclusedRepeatableStages( Map<Integer, ProgramStage> exclusedRepeatableStages )
    {
        this.exclusedRepeatableStages = exclusedRepeatableStages;
    }

    public DateFormat getDateFormat()
    {
        return new SimpleDateFormat( "yyyy-MM-dd" );
    }
    
    private ProgramInstance programInstance;
    
    public ProgramInstance getProgramInstance()
    {
        return programInstance;
    }

    public void setProgramInstance( ProgramInstance programInstance )
    {
        this.programInstance = programInstance;
    }

    @Override
    public String execute()
        throws Exception
    {
        programInstance = programInstanceService.getProgramInstance( programInstanceId );
        
        exclusedRepeatableStages = new HashMap<Integer, ProgramStage>();
        patient = patientService.getPatient( patientId );
        programStageInstances = programInstance.getProgramStageInstances();
        repeatableStages = new HashSet<ProgramStage>();
        
        Set<ProgramStage> programStages = programInstance.getProgram().getProgramStages();

        for ( ProgramStage programStage : programStages )
        {
            if ( programStage.getIrregular() )
            {
                repeatableStages.add( programStage );
            }
        }

        for ( ProgramStageInstance programStageInstance : programStageInstances )
        {
            
            ProgramStage programStage = programStageInstance.getProgramStage();
            if ( programStage.getIrregular() && !programStageInstance.isCompleted() )
            {
                exclusedRepeatableStages.put( programStage.getId(), programStage );
            }
        }

        return SUCCESS;
    }

}
