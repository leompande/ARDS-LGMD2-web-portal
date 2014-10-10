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

import com.fasterxml.jackson.core.type.TypeReference;
import com.opensymphony.xwork2.Action;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class UploadAnonymousEventAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private ProgramService programService;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private ProgramStageInstanceService programStageInstanceService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private PatientDataValueService patientDataValueService;

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private String message;

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public String execute()
        throws Exception
    {
        ServletInputStream inputStream = ServletActionContext.getRequest().getInputStream();

        Map<String, Object> input = JacksonUtils.getJsonMapper().readValue( inputStream,
            new TypeReference<HashMap<String, Object>>()
            {
            } );

        Map<String, Object> executionDate = (Map<String, Object>) input.get( "executionDate" );

        Date date = format.parseDate( (String) executionDate.get( "executionDate" ) );

        Boolean completed = null;

        try
        {
            String completeString = (String) executionDate.get( "completed" );

            if ( completeString != null )
            {
                completed = Boolean.parseBoolean( completeString );
            }
        }
        catch ( ClassCastException ignored )
        {
        }

        Double longitude = null;
        Double latitude = null;
        Map<String, String> coord = (Map<String, String>) input.get( "coordinate" );

        if ( coord != null )
        {
            try
            {
                longitude = Double.parseDouble( coord.get( "longitude" ) );
                latitude = Double.parseDouble( coord.get( "latitude" ) );
            }
            catch ( NullPointerException ignored )
            {
            }

        }

        Integer programId;
        Integer organisationUnitId;
        Integer programStageInstanceId;
        ProgramStageInstance programStageInstance;

        try
        {
            programStageInstanceId = Integer.parseInt( (String) executionDate.get( "programStageInstanceId" ) );
        }
        catch ( NumberFormatException e )
        {
            programStageInstanceId = null;
        }

        if ( programStageInstanceId != null )
        {
            programStageInstance = programStageInstanceService.getProgramStageInstance( programStageInstanceId );
            updateExecutionDate( programStageInstance, date, completed, longitude, latitude );
        }
        else
        {
            try
            {
                programId = Integer.parseInt( (String) executionDate.get( "programId" ) );
            }
            catch ( NumberFormatException e )
            {
                message = e.getMessage();
                return ERROR;
            }

            try
            {
                organisationUnitId = Integer.parseInt( (String) executionDate.get( "organisationUnitId" ) );
            }
            catch ( NumberFormatException e )
            {
                OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( (String) executionDate.get( "organisationUnitId" ) );

                if ( organisationUnit == null )
                {
                    message = e.getMessage();
                    return ERROR;
                }

                organisationUnitId = organisationUnit.getId();
            }

            if ( date == null )
            {
                return INPUT;
            }

            programStageInstance = saveExecutionDate( programId, organisationUnitId, date, completed, longitude, latitude );
        }

        Map<String, Object> values = (Map<String, Object>) input.get( "values" );

        if ( values != null )
        {
            for ( String dataElementUid : values.keySet() )
            {
                Map<String, Object> valueMap = (Map<String, Object>) values.get( dataElementUid );
                String value = (String) valueMap.get( "value" );
                Boolean providedElsewhere = (Boolean) valueMap.get( "providedElsewhere" );

                DataElement dataElement = dataElementService.getDataElement( dataElementUid );

                saveDataValue( programStageInstance, dataElement, value, providedElsewhere );
            }
        }

        return SUCCESS;
    }

    private void updateExecutionDate( ProgramStageInstance programStageInstance, Date date, Boolean completed,
        Double longitude, Double latitude )
    {
        if ( date != null )
        {
            programStageInstance.setDueDate( date );
            programStageInstance.setExecutionDate( date );
        }

        if ( completed != null )
        {
            programStageInstance.setCompleted( completed );
            programStageInstance.setCompletedDate( new Date() );
            programStageInstance.setCompletedUser( currentUserService.getCurrentUsername() );
        }

        if ( programStageInstance.getProgramStage().getCaptureCoordinates() )
        {
            if ( longitude != null && latitude != null )
            {
                programStageInstance.setLongitude( longitude );
                programStageInstance.setLatitude( latitude );
            }
        }

        message = programStageInstance.getId() + "";

        programStageInstanceService.updateProgramStageInstance( programStageInstance );
    }

    private ProgramStageInstance saveExecutionDate( Integer programId, Integer organisationUnitId, Date date,
        Boolean completed, Double longitude, Double latitude )
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );
        Program program = programService.getProgram( programId );
        ProgramStage programStage = program.getProgramStages().iterator().next();
        ProgramInstance programInstance = programInstanceService.getProgramInstances( program ).iterator().next();

        ProgramStageInstance programStageInstance = new ProgramStageInstance();
        programStageInstance.setProgramInstance( programInstance );
        programStageInstance.setProgramStage( programStage );
        programStageInstance.setDueDate( date );
        programStageInstance.setExecutionDate( date );
        programStageInstance.setOrganisationUnit( organisationUnit );

        if ( completed != null )
        {
            programStageInstance.setCompleted( completed );
            programStageInstance.setCompletedDate( new Date() );
            programStageInstance.setCompletedUser( currentUserService.getCurrentUsername() );
        }

        if ( programStage.getCaptureCoordinates() )
        {
            if ( longitude != null && latitude != null )
            {
                programStageInstance.setLongitude( longitude );
                programStageInstance.setLatitude( latitude );
            }
        }

        programStageInstanceService.addProgramStageInstance( programStageInstance );

        message = programStageInstance.getId() + "";

        return programStageInstance;
    }

    private void saveDataValue( ProgramStageInstance programStageInstance, DataElement dataElement, String value,
        Boolean providedElsewhere )
    {
        String storedBy = currentUserService.getCurrentUsername();

        if ( value != null && value.trim().length() == 0 )
        {
            value = null;
        }

        PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance,
            dataElement );

        if ( value != null )
        {
            if ( patientDataValue == null )
            {
                patientDataValue = new PatientDataValue( programStageInstance, dataElement, new Date(), value );
                patientDataValue.setStoredBy( storedBy );
                patientDataValue.setProvidedElsewhere( providedElsewhere );

                patientDataValueService.savePatientDataValue( patientDataValue );
            }
            else
            {
                patientDataValue.setValue( value );
                patientDataValue.setTimestamp( new Date() );
                patientDataValue.setProvidedElsewhere( providedElsewhere );
                patientDataValue.setStoredBy( storedBy );

                patientDataValueService.updatePatientDataValue( patientDataValue );
            }
        }
        else if ( patientDataValue != null )
        {
            patientDataValueService.deletePatientDataValue( patientDataValue );
        }
    }
}
