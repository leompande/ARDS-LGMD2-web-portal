package org.hisp.dhis.caseentry.action.caseentry;

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
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;

public class GetDataRecordsAction
    extends ActionPagingSupport<Patient>
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private Integer programId;

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    private List<String> searchTexts = new ArrayList<String>();

    public void setSearchTexts( List<String> searchTexts )
    {
        this.searchTexts = searchTexts;
    }

    private Integer total;

    public Integer getTotal()
    {
        return total;
    }

    private Map<Patient, ProgramInstance> programInstanceMap = new HashMap<Patient, ProgramInstance>();

    public Map<Patient, ProgramInstance> getProgramInstanceMap()
    {
        return programInstanceMap;
    }

    private Collection<Patient> patients;

    public Collection<Patient> getPatients()
    {
        return patients;
    }

    private List<ProgramStageInstance> programStageInstances = new ArrayList<ProgramStageInstance>();

    public List<ProgramStageInstance> getProgramStageInstances()
    {
        return programStageInstances;
    }

    private List<PatientIdentifierType> identifierTypes;

    public List<PatientIdentifierType> getIdentifierTypes()
    {
        return identifierTypes;
    }

    private List<PatientAttribute> attributes = new ArrayList<PatientAttribute>();

    public List<PatientAttribute> getAttributes()
    {
        return attributes;
    }

    private Program program;

    public Program getProgram()
    {
        return program;
    }

    private String type;

    public void setType( String type )
    {
        this.type = type;
    }

    private Grid grid;

    public Grid getGrid()
    {
        return grid;
    }

    private Boolean followup;

    public void setFollowup( Boolean followup )
    {
        this.followup = followup;
    }

    private Boolean trackingReport;

    public void setTrackingReport( Boolean trackingReport )
    {
        this.trackingReport = trackingReport;
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        OrganisationUnit orgunit = selectionManager.getSelectedOrganisationUnit();

        Collection<OrganisationUnit> orgunits = new HashSet<OrganisationUnit>();
        orgunits.add( orgunit );

        if ( programId != null )
        {
            program = programService.getProgram( programId );
        }

        if ( searchTexts.size() > 0 )
        {
            if ( type == null )
            {
                total = patientService.countSearchPatients( searchTexts, orgunits, followup,
                    ProgramInstance.STATUS_ACTIVE );
                this.paging = createPaging( total );

                List<Integer> stageInstanceIds = patientService.getProgramStageInstances( searchTexts, orgunits,
                    followup, ProgramInstance.STATUS_ACTIVE, paging.getStartPos(), paging.getPageSize() );

                for ( Integer stageInstanceId : stageInstanceIds )
                {
                    ProgramStageInstance programStageInstance = programStageInstanceService
                        .getProgramStageInstance( stageInstanceId );
                    programStageInstances.add( programStageInstance );
                }
            }
            else if ( trackingReport != null && trackingReport )
            {
                grid = patientService.getTrackingEventsReport( program, searchTexts, orgunits, followup,
                    ProgramInstance.STATUS_ACTIVE, i18n );
            }
            else
            {
                grid = patientService.getScheduledEventsReport( searchTexts, orgunits, followup,
                    ProgramInstance.STATUS_ACTIVE, null, null, i18n );
            }
        }

        return type == null ? SUCCESS : type;
    }
}
