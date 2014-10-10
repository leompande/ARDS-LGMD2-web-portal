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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.StrutsStatics;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.light.utils.NamebasedUtils;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
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
import org.hisp.dhis.program.ProgramStageDataElementService;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.user.UserService;
import org.hisp.dhis.util.ContextUtils;
import org.hisp.dhis.util.SessionUtils;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;

public class SaveSingleEventAction
    implements Action
{
    private static final String REDIRECT = "redirect";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
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

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private PatientDataValueService patientDataValueService;

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private NamebasedUtils util;

    public NamebasedUtils getUtil()
    {
        return util;
    }

    public void setUtil( NamebasedUtils util )
    {
        this.util = util;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private ProgramStageDataElementService programStageDataElementService;

    public ProgramStageDataElementService getProgramStageDataElementService()
    {
        return programStageDataElementService;
    }

    public void setProgramStageDataElementService( ProgramStageDataElementService programStageDataElementService )
    {
        this.programStageDataElementService = programStageDataElementService;
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

    private Map<String, String> typeViolations = new HashMap<String, String>();

    public Map<String, String> getTypeViolations()
    {
        return typeViolations;
    }

    private Map<String, String> prevDataValues = new HashMap<String, String>();

    public Map<String, String> getPrevDataValues()
    {
        return prevDataValues;
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

    private String eventName;

    public String getEventName()
    {
        return this.eventName;
    }

    private Integer organisationUnitId;

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    public Integer getOrganisationUnitId()
    {
        return this.organisationUnitId;
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

    private Integer instId;

    public void setInstId( Integer instId )
    {
        this.instId = instId;
    }

    public Integer getInstId()
    {
        return this.instId;
    }

    private List<String> dynForm = new ArrayList<String>();

    public void setDynForm( List<String> dynForm )
    {
        this.dynForm = dynForm;
    }

    public List<String> getDynForm()
    {
        return dynForm;
    }

    private String resultString;

    public void setResultString( String resultString )
    {
        this.resultString = resultString;
    }

    public String getResultString()
    {
        return this.resultString;
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

    private String keyword;

    public String getKeyword()
    {
        return keyword;
    }

    private int dataElementIdForSearching;

    public int getDataElementIdForSearching()
    {
        return dataElementIdForSearching;
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

    @Override
    @SuppressWarnings( "unchecked" )
    public String execute()
        throws Exception
    {

        Program program = programService.getProgram( programId );
        eventName = program.getName();

        Patient patient = patientService.getPatient( patientId );
        ProgramStage programStage = program.getProgramStages().iterator().next();
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );

        programStageDataElements = new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );
        Collections.sort( programStageDataElements, OrderBySortOrder );

        HttpServletRequest request = (HttpServletRequest) ActionContext.getContext().get(
            StrutsStatics.HTTP_REQUEST );
        Map<String, String> parameterMap = ContextUtils.getParameterMap( request );

        typeViolations.clear();

        prevDataValues.clear();

        if ( SessionUtils.getSessionVar( "prevDataValues" ) == null )
        {
            SessionUtils.setSessionVar( "prevDataValues", this.prevDataValues );
        }
        else
        {
            this.prevDataValues = (Map<String, String>) SessionUtils.getSessionVar( "prevDataValues" );
        }

        // -------------------------------------------------------------------------
        // Validation
        // -------------------------------------------------------------------------

        for ( String key : parameterMap.keySet() )
        {
            if ( key.startsWith( "DE" ) )
            {
                Integer dataElementId = Integer.parseInt( key.substring( 2, key.length() ) );

                String value = parameterMap.get( key );

                DataElement dataElement = dataElementService.getDataElement( dataElementId );

                ProgramStageDataElement programStageDataElement = programStageDataElementService.get( programStage,
                    dataElement );

                value = value.trim();

                Boolean valueIsEmpty = (value == null || value.length() == 0);

                if ( !valueIsEmpty )
                {
                    String typeViolation = util.getTypeViolation( dataElement, value );

                    if ( typeViolation != null )
                    {
                        typeViolations.put( key, typeViolation );
                    }
                    prevDataValues.put( key, value );
                }
                else if ( valueIsEmpty && programStageDataElement.isCompulsory() )
                {
                    typeViolations.put( key, "is_empty" );
                    prevDataValues.put( key, value );
                }
            }
        }

        if ( !typeViolations.isEmpty() )
        {
            return ERROR;
        }

        if ( isSearching( parameterMap ) == true )
        {
            return REDIRECT;
        }

        if ( this.isEditing.equals( "true" ) )
        {
            ProgramStageInstance programStageInstance = programStageInstanceService
                .getProgramStageInstance( this.programStageInstanceId );
            ProgramInstance programInstance = programStageInstance.getProgramInstance();

            for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
            {
                DataElement dataElement = programStageDataElement.getDataElement();

                PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance,
                    dataElement );

                String id = "DE" + dataElement.getId();

                String value = parameterMap.get( id );

                if ( patientDataValue == null && value != null )
                {

                    patientDataValue = new PatientDataValue( programStageInstance, dataElement, new Date(), value );

                    patientDataValue.setProvidedElsewhere( false );

                    patientDataValueService.savePatientDataValue( patientDataValue );
                }
                if ( patientDataValue != null && value == null )
                {
                    patientDataValueService.deletePatientDataValue( patientDataValue );
                }
                else if ( patientDataValue != null && value != null )
                {
                    if ( value.trim().equals( "" ) )
                    {
                        patientDataValueService.deletePatientDataValue( patientDataValue );
                    }
                    else
                    {
                        patientDataValue.setValue( value );

                        patientDataValue.setTimestamp( new Date() );

                        patientDataValue.setProvidedElsewhere( false );

                        patientDataValueService.updatePatientDataValue( patientDataValue );
                    }
                }
            }
            programStageInstance.setCompleted( true );
            programStageInstanceService.updateProgramStageInstance( programStageInstance );

            programInstance.setStatus( ProgramInstance.STATUS_COMPLETED );
            programInstanceService.updateProgramInstance( programInstance );
        }
        else
        {
            ProgramInstance programInstance = new ProgramInstance();
            programInstance.setEnrollmentDate( new Date() );
            programInstance.setDateOfIncident( new Date() );
            programInstance.setProgram( program );
            programInstance.setPatient( patient );
            programInstance.setStatus( ProgramInstance.STATUS_COMPLETED );
            programInstanceService.addProgramInstance( programInstance );

            ProgramStageInstance programStageInstance = new ProgramStageInstance();
            programStageInstance.setOrganisationUnit( organisationUnit );
            programStageInstance.setProgramInstance( programInstance );
            programStageInstance.setProgramStage( programStage );
            programStageInstance.setDueDate( new Date() );
            programStageInstance.setExecutionDate( new Date() );
            programStageInstance.setCompleted( true );
            programStageInstanceService.addProgramStageInstance( programStageInstance );

            for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
            {
                DataElement dataElement = programStageDataElement.getDataElement();

                PatientDataValue patientDataValue = new PatientDataValue();

                patientDataValue.setDataElement( dataElement );

                String id = "DE" + dataElement.getId();

                String value = parameterMap.get( id );

                if ( value != null && !value.trim().equals( "" ) )
                {

                    patientDataValue.setValue( parameterMap.get( id ) );

                    patientDataValue.setProgramStageInstance( programStageInstance );

                    patientDataValue.setProvidedElsewhere( false );

                    patientDataValue.setTimestamp( new Date() );

                    patientDataValueService.savePatientDataValue( patientDataValue );
                }
            }
        }
        SessionUtils.removeSessionVar( "prevDataValues" );

        return SUCCESS;
    }

    public boolean isSearching( Map<String, String> parameterMap )
    {
        boolean isCorrect = false;
        for ( ProgramStageDataElement each : this.programStageDataElements )
        {
            DataElement dataElement = each.getDataElement();

            if ( dataElement.getOptionSet() != null && dataElement.getOptionSet().getOptions().size() > 15 )
            {
                this.keyword = parameterMap.get( "DE" + dataElement.getId() ).trim();

                dataElementIdForSearching = dataElement.getId();

                // if( !parameterMap.get( "preDE"+dataElement.getId() ).equals(
                // parameterMap.get( "DE"+dataElement.getId() )))
                for ( String option : dataElement.getOptionSet().getOptions() )
                {
                    if ( option != null )
                    {
                        if ( option.equals( this.keyword ) )
                        {
                            isCorrect = true;
                        }
                    }
                }
                if ( isCorrect == false && !this.keyword.isEmpty() )
                    return true;
                else
                    isCorrect = false;
            }
        }
        return false;
    }

}
