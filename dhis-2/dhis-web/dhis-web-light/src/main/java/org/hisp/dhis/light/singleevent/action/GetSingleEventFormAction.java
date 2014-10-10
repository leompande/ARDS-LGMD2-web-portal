package org.hisp.dhis.light.singleevent.action;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.user.UserService;
import org.hisp.dhis.util.SessionUtils;

import com.opensymphony.xwork2.Action;

public class GetSingleEventFormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private PatientDataValueService patientDataValueService;

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
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
    // Input Output
    // -------------------------------------------------------------------------

    private Integer organisationUnitId;

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    public Integer getOrganisationUnitId()
    {
        return this.organisationUnitId;
    }

    private Integer programId;

    public Integer getProgramId()
    {
        return programId;
    }

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    private String eventName;

    public String getEventName()
    {
        return this.eventName;
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

    private Integer instId;

    public void setInstId( Integer instId )
    {
        this.instId = instId;
    }

    public Integer getInstId()
    {
        return this.instId;
    }

    private boolean update;

    public void setUpdate( boolean update )
    {
        this.update = update;
    }

    public boolean getUpdate()
    {
        return this.update;
    }

    private ArrayList<ProgramStageDataElement> programStageDataElements = new ArrayList<ProgramStageDataElement>();

    public ArrayList<ProgramStageDataElement> getProgramStageDataElements()
    {
        return this.programStageDataElements;
    }

    static final Comparator<ProgramStageDataElement> OrderBySortOrder = new Comparator<ProgramStageDataElement>()
    {
        public int compare( ProgramStageDataElement i1, ProgramStageDataElement i2 )
        {
            return i1.getSortOrder().compareTo( i2.getSortOrder() );
        }
    };

    private Map<String, String> prevDataValues = new HashMap<String, String>();

    public Map<String, String> getPrevDataValues()
    {
        return prevDataValues;
    }

    private String searchResult;

    public void setSearchResult( String searchResult )
    {
        this.searchResult = searchResult;
    }

    private int dataElementIdForSearching;

    public void setDataElementIdForSearching( int dataElementIdForSearching )
    {
        this.dataElementIdForSearching = dataElementIdForSearching;
    }

    private String isEditing;

    public String getIsEditing()
    {
        return isEditing;
    }

    public void setIsEditing( String isEditing )
    {
        this.isEditing = isEditing;
    }

    private int programStageInstanceId;

    public int getProgramStageInstanceId()
    {
        return programStageInstanceId;
    }

    public void setProgramStageInstanceId( int programStageInstanceId )
    {
        this.programStageInstanceId = programStageInstanceId;
    }

    private Patient patient;

    public Patient getPatient()
    {
        return patient;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String execute()
        throws Exception
    {
        Program program = programService.getProgram( programId );
        this.patient = patientService.getPatient( this.patientId );
        eventName = program.getName();
        ProgramStage programStage = program.getProgramStages().iterator().next();
        programStageDataElements = new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );
        Collections.sort( programStageDataElements, OrderBySortOrder );

        if ( SessionUtils.getSessionVar( "prevDataValues" ) != null )
        {
            this.prevDataValues = (Map<String, String>) SessionUtils.getSessionVar( "prevDataValues" );
        }
        if ( searchResult != null && !searchResult.equals( "0" ) )
        {
            this.prevDataValues.put( "DE" + this.dataElementIdForSearching, searchResult );
        }
        else if ( searchResult == null )
        {
            // For editing if user finished the form
            if ( programInstanceService.getProgramInstances( this.patient, program ).size() != 0 )
            {
                List<ProgramInstance> proInstanceList = (List<ProgramInstance>) programInstanceService
                    .getProgramInstances( this.patient, program );

                ProgramInstance proInstance = null;

                if ( proInstanceList.size() != 0 )
                {
                    proInstance = proInstanceList.iterator().next();
                }

                ProgramStageInstance proStageInstance = programStageInstanceService.getProgramStageInstance(
                    proInstance, programStage );

                this.programStageInstanceId = proStageInstance.getId();

                for ( ProgramStageDataElement each : programStageDataElements )
                {
                    DataElement dataElement = each.getDataElement();

                    PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( proStageInstance,
                        dataElement );

                    if ( patientDataValue != null )
                    {
                        this.prevDataValues.put( "DE" + dataElement.getId(), patientDataValue.getValue() );
                    }
                }
            }
        }

        return SUCCESS;
    }

}
