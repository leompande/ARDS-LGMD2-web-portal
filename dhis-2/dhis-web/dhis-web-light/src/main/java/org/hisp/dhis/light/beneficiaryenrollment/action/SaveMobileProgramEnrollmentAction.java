package org.hisp.dhis.light.beneficiaryenrollment.action;

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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.StrutsStatics;
import org.hisp.dhis.light.utils.FormUtils;
import org.hisp.dhis.light.utils.ValueUtils;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeOption;
import org.hisp.dhis.patient.PatientAttributeOptionService;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.util.ContextUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;

public class SaveMobileProgramEnrollmentAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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

    private FormUtils formUtils;

    public FormUtils getFormUtils()
    {
        return formUtils;
    }

    public void setFormUtils( FormUtils formUtils )
    {
        this.formUtils = formUtils;
    }

    private PatientIdentifierTypeService patientIdentifierTypeService;

    public PatientIdentifierTypeService getPatientIdentifierTypeService()
    {
        return patientIdentifierTypeService;
    }

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    private PatientIdentifierService patientIdentifierService;

    public PatientIdentifierService getPatientIdentifierService()
    {
        return patientIdentifierService;
    }

    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }

    private PatientAttributeService patientAttributeService;

    public PatientAttributeService getPatientAttributeService()
    {
        return patientAttributeService;
    }

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    private PatientAttributeOptionService patientAttributeOptionService;

    public PatientAttributeOptionService getPatientAttributeOptionService()
    {
        return patientAttributeOptionService;
    }

    public void setPatientAttributeOptionService( PatientAttributeOptionService patientAttributeOptionService )
    {
        this.patientAttributeOptionService = patientAttributeOptionService;
    }

    private PatientAttributeValueService patientAttributeValueService;

    public PatientAttributeValueService getPatientAttributeValueService()
    {
        return patientAttributeValueService;
    }

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer patientId;

    public Integer getPatientId()
    {
        return patientId;
    }

    public void setPatientId( Integer patientId )
    {
        this.patientId = patientId;
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

    private Patient patient;

    public Patient getPatient()
    {
        return patient;
    }

    private Program program;

    public Program getProgram()
    {
        return program;
    }

    private String enrollmentDate;

    public void setEnrollmentDate( String enrollmentDate )
    {
        this.enrollmentDate = enrollmentDate;
    }

    private String incidentDate;

    public String getIncidentDate()
    {
        return incidentDate;
    }

    public void setIncidentDate( String incidentDate )
    {
        this.incidentDate = incidentDate;
    }

    private Map<String, String> validationMap = new HashMap<String, String>();

    public Map<String, String> getValidationMap()
    {
        return validationMap;
    }

    public void setValidationMap( Map<String, String> validationMap )
    {
        this.validationMap = validationMap;
    }

    private Map<String, String> previousValues = new HashMap<String, String>();

    public Map<String, String> getPreviousValues()
    {
        return previousValues;
    }

    public void setPreviousValues( Map<String, String> previousValues )
    {
        this.previousValues = previousValues;
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

    private Collection<PatientIdentifierType> patientIdentifierTypes;

    public Collection<PatientIdentifierType> getPatientIdentifierTypes()
    {
        return patientIdentifierTypes;
    }

    public void setPatientIdentifierTypes( Collection<PatientIdentifierType> patientIdentifierTypes )
    {
        this.patientIdentifierTypes = patientIdentifierTypes;
    }

    private Collection<PatientAttribute> patientAttributes;

    public Collection<PatientAttribute> getPatientAttributes()
    {
        return patientAttributes;
    }

    public void setPatientAttributes( Collection<PatientAttribute> patientAttributes )
    {
        this.patientAttributes = patientAttributes;
    }

    public String execute()
        throws Exception
    {
        patient = patientService.getPatient( patientId );
        program = programService.getProgram( programId );
        patientAttributes = program.getAttributes();
        patientIdentifierTypes = program.getIdentifierTypes();

        List<PatientAttributeValue> patientAttributeValues = new ArrayList<PatientAttributeValue>();

        HttpServletRequest request = (HttpServletRequest) ActionContext.getContext().get( StrutsStatics.HTTP_REQUEST );
        Map<String, String> parameterMap = ContextUtils.getParameterMap( request );
        DateTimeFormatter sdf = ISODateTimeFormat.yearMonthDay();

        if ( !ValueUtils.isDate( enrollmentDate ) )
        {
            validationMap.put( "enrollmentDate", "is_invalid_date" );
        }

        if ( !ValueUtils.isDate( incidentDate ) )
        {
            validationMap.put( "incidentDate", "is_invalid_date" );
        }

        // Handle Attribute and Identifier

        for ( PatientIdentifierType patientIdentifierType : patientIdentifierTypes )
        {
            {
                String key = "IDT" + patientIdentifierType.getId();
                String value = parameterMap.get( key );

                PatientIdentifier duplicateId = null;

                if ( value != null && !value.isEmpty() )
                {
                    duplicateId = patientIdentifierService.get( patientIdentifierType, value );
                }

                if ( value != null )
                {
                    if ( patientIdentifierType.isMandatory() && value.trim().equals( "" ) )
                    {
                        this.validationMap.put( key, "is_mandatory" );
                    }
                    else if ( patientIdentifierType.getType().equals( "number" ) && !MathUtils.isNumeric( value ) )
                    {
                        this.validationMap.put( key, "is_invalid_number" );
                    }
                    else if ( duplicateId != null )
                    {
                        this.validationMap.put( key, "is_duplicate" );
                    }
                    else
                    {
                        PatientIdentifier patientIdentifier = new PatientIdentifier();
                        patientIdentifier.setIdentifierType( patientIdentifierType );
                        patientIdentifier.setPatient( patient );
                        patientIdentifier.setIdentifier( value.trim() );
                        patient.getIdentifiers().add( patientIdentifier );
                    }

                    this.previousValues.put( key, value );
                }
            }
        }

        for ( PatientAttribute patientAttribute : patientAttributes )
        {
            {
                String key = "AT" + patientAttribute.getId();
                String value = parameterMap.get( key ).trim();

                if ( value != null )
                {
                    if ( patientAttribute.isMandatory() && value.trim().equals( "" ) )
                    {
                        this.validationMap.put( key, "is_mandatory" );
                    }
                    else if ( value.trim().length() > 0
                        && patientAttribute.getValueType().equals( PatientAttribute.TYPE_INT )
                        && !MathUtils.isInteger( value ) )
                    {
                        this.validationMap.put( key, "is_invalid_number" );
                    }
                    else if ( value.trim().length() > 0
                        && patientAttribute.getValueType().equals( PatientAttribute.TYPE_DATE )
                        && !ValueUtils.isDate( value ) )
                    {
                        this.validationMap.put( key, "is_invalid_date" );
                    }
                    else
                    {
                        PatientAttributeValue patientAttributeValue = new PatientAttributeValue();

                        if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( patientAttribute.getValueType() ) )
                        {
                            PatientAttributeOption option = patientAttributeOptionService.get( NumberUtils.toInt(
                                value, 0 ) );

                            if ( option != null )
                            {
                                patientAttributeValue.setPatientAttributeOption( option );
                            }
                        }

                        patientAttributeValue.setPatient( patient );
                        patientAttributeValue.setPatientAttribute( patientAttribute );
                        patientAttributeValue.setValue( value.trim() );
                        patientAttributeValues.add( patientAttributeValue );
                    }

                    this.previousValues.put( key, value );
                }
            }
        }

        if ( validationMap.size() > 0 )
        {
            previousValues.put( "enrollmentDate", enrollmentDate );
            previousValues.put( "incidentDate", incidentDate );
            validated = false;
            return ERROR;
        }

        this.saveAttributeValue( patientAttributeValues );
        patientService.updatePatient( patient );

        Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( patient, program,
            ProgramInstance.STATUS_ACTIVE );

        ProgramInstance programInstance = null;

        if ( programInstances.iterator().hasNext() )
        {
            programInstance = programInstances.iterator().next();
        }

        if ( programInstance == null )
        {
            programInstance = new ProgramInstance();
            programInstance.setEnrollmentDate( sdf.parseDateTime( enrollmentDate ).toDate() );
            programInstance.setDateOfIncident( sdf.parseDateTime( incidentDate ).toDate() );
            programInstance.setProgram( program );
            programInstance.setPatient( patient );
            programInstance.setStatus( ProgramInstance.STATUS_ACTIVE );

            programInstanceService.addProgramInstance( programInstance );

            patientService.updatePatient( patient );

            for ( ProgramStage programStage : program.getProgramStages() )
            {
                if ( programStage.getAutoGenerateEvent() )
                {
                    ProgramStageInstance programStageInstance = new ProgramStageInstance();
                    programStageInstance.setProgramInstance( programInstance );
                    programStageInstance.setProgramStage( programStage );

                    Date dateCreatedEvent = sdf.parseDateTime( incidentDate ).toDate();
                    if ( programStage.getGeneratedByEnrollmentDate() )
                    {
                        dateCreatedEvent = sdf.parseDateTime( enrollmentDate ).toDate();
                    }

                    Date dueDate = DateUtils
                        .getDateAfterAddition( dateCreatedEvent, programStage.getMinDaysFromStart() );

                    programStageInstance.setDueDate( dueDate );

                    if ( program.isSingleEvent() )
                    {
                        programStageInstance.setExecutionDate( dueDate );
                    }

                    programStageInstanceService.addProgramStageInstance( programStageInstance );
                }
            }
        }
        else
        {
            programInstance.setEnrollmentDate( sdf.parseDateTime( enrollmentDate ).toDate() );
            programInstance.setDateOfIncident( sdf.parseDateTime( incidentDate ).toDate() );

            programInstanceService.updateProgramInstance( programInstance );

            for ( ProgramStageInstance programStageInstance : programInstance.getProgramStageInstances() )
            {
                if ( !programStageInstance.isCompleted()
                    || programStageInstance.getStatus() != ProgramStageInstance.SKIPPED_STATUS )
                {
                    Date dueDate = DateUtils.getDateAfterAddition( sdf.parseDateTime( incidentDate ).toDate(),
                        programStageInstance.getProgramStage().getMinDaysFromStart() );

                    programStageInstance.setDueDate( dueDate );

                    programStageInstanceService.updateProgramStageInstance( programStageInstance );
                }
            }
        }

        validated = true;
        return SUCCESS;
    }

    private void saveAttributeValue( List<PatientAttributeValue> patientAttributeValues )
    {
        for ( PatientAttributeValue patientAttributeValue : patientAttributeValues )
        {
            patientAttributeValueService.savePatientAttributeValue( patientAttributeValue );
        }

    }

}
