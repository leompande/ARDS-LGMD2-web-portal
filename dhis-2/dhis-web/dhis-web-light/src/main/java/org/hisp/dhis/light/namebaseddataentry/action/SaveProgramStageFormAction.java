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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.StrutsStatics;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.light.utils.NamebasedUtils;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramExpressionService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageDataElementService;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageSection;
import org.hisp.dhis.program.ProgramStageSectionService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.program.ProgramValidation;
import org.hisp.dhis.program.ProgramValidationResult;
import org.hisp.dhis.program.ProgramValidationService;
import org.hisp.dhis.user.UserService;
import org.hisp.dhis.util.ContextUtils;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;

public class SaveProgramStageFormAction
    implements Action
{
    private static final String SUCCESS_AND_BACK_TO_PROGRAMSTAGE = "success_back_to_programStage";

    private static final String REGISTER_NEXT_DUEDATE = "register_next_duedate";

    private static final String SUCCESS_AND_BACK_TO_PROGRAMSTAGE_SECTION = "success_back_to_programStageSection";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private UserService userService;

    public UserService getUserService()
    {
        return userService;
    }

    public void setUserService( UserService userService )
    {
        this.userService = userService;
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

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    private ProgramStageDataElementService programStageDataElementService;

    public void setProgramStageDataElementService( ProgramStageDataElementService programStageDataElementService )
    {
        this.programStageDataElementService = programStageDataElementService;
    }

    private PatientDataValueService patientDataValueService;

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    private ProgramValidationService programValidationService;

    public void setProgramValidationService( ProgramValidationService programValidationService )
    {
        this.programValidationService = programValidationService;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    private ProgramStageSectionService programStageSectionService;

    public void setProgramStageSectionService( ProgramStageSectionService programStageSectionService )
    {
        this.programStageSectionService = programStageSectionService;
    }

    private ProgramExpressionService programExpressionService;

    public void setProgramExpressionService( ProgramExpressionService programExpressionService )
    {
        this.programExpressionService = programExpressionService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private int orgUnitId;

    public void setOrgUnitId( int orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    public int getOrgUnitId()
    {
        return orgUnitId;
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

    private Integer programInstanceId;

    public Integer getProgramInstanceId()
    {
        return programInstanceId;
    }

    public void setProgramInstanceId( Integer programInstanceId )
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

    private ProgramStage programStage;

    public ProgramStage getProgramStage()

    {
        return programStage;
    }

    private ProgramStageDataElement programStageDataElement;

    public ProgramStageDataElement getProgramStageDataElement()
    {
        return programStageDataElement;
    }

    private boolean current;

    public void setCurrent( boolean current )
    {
        this.current = current;
    }

    public boolean getCurrent()
    {
        return current;
    }

    private List<ProgramStageDataElement> dataElements;

    public List<ProgramStageDataElement> getDataElements()
    {
        return dataElements;
    }

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

    private Program program;

    public Program getProgram()
    {
        return program;
    }

    public void setProgram( Program program )
    {
        this.program = program;
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

    private Map<Integer, String> leftsideFormulaMap = new HashMap<Integer, String>();

    public Map<Integer, String> getLeftsideFormulaMap()
    {
        return leftsideFormulaMap;
    }

    public void setLeftsideFormulaMap( Map<Integer, String> leftsideFormulaMap )
    {
        this.leftsideFormulaMap = leftsideFormulaMap;
    }

    private Map<Integer, String> rightsideFormulaMap = new HashMap<Integer, String>();

    public Map<Integer, String> getRightsideFormulaMap()
    {
        return rightsideFormulaMap;
    }

    public void setRightsideFormulaMap( Map<Integer, String> rightsideFormulaMap )
    {
        this.rightsideFormulaMap = rightsideFormulaMap;
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

    public ProgramStageSection programStageSection;

    public ProgramStageSection getProgramStageSection()
    {
        return programStageSection;
    }

    private Boolean validated;

    public void setValidated( Boolean validated )
    {
        this.validated = validated;
    }

    public Boolean getValidated()
    {
        return validated;
    }

    private List<ProgramValidationResult> programValidationResults;

    public List<ProgramValidationResult> getProgramValidationResults()
    {
        return programValidationResults;
    }

    @Override
    public String execute()
        throws Exception
    {
        programStage = util.getProgramStage( programId, programStageId );
        program = programStageService.getProgramStage( programStageId ).getProgram();

        org.hisp.dhis.program.ProgramStage dhisProgramStage = programStageService.getProgramStage( programStageId );

        ProgramStageInstance programStageInstance = programStageInstanceService
            .getProgramStageInstance( programStageInstanceId );

        List<PatientDataValue> patientDataValues = new ArrayList<PatientDataValue>();

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

        HttpServletRequest request = (HttpServletRequest) ActionContext.getContext().get( StrutsStatics.HTTP_REQUEST );
        Map<String, String> parameterMap = ContextUtils.getParameterMap( request );

        // List<DataValue> dataValues = new ArrayList<DataValue>();

        typeViolations.clear();
        prevDataValues.clear();

        for ( String key : parameterMap.keySet() )
        {
            if ( key.startsWith( "DE" ) )
            {
                Integer dataElementId = Integer.parseInt( key.substring( 2, key.length() ) );
                String value = parameterMap.get( key );

                org.hisp.dhis.dataelement.DataElement dataElement = dataElementService.getDataElement( dataElementId );
                ProgramStageDataElement programStageDataElement = programStageDataElementService.get( dhisProgramStage,
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
                }
                else if ( valueIsEmpty && programStageDataElement.isCompulsory() )
                {
                    typeViolations.put( key, "is_empty" );
                }

                prevDataValues.put( key, value );
                prevDataValues.put( "CB" + dataElement.getId(), parameterMap.get( "CB" + dataElement.getId() ) );

                // build patient data value
                PatientDataValue patientDataValue = new PatientDataValue( programStageInstance, dataElement,
                    new Date(), value );

                String providedElseWhereValue = parameterMap.get( "CB" + dataElementId );

                if ( providedElseWhereValue != null )
                {
                    patientDataValue.setProvidedElsewhere( Boolean.parseBoolean( providedElseWhereValue ) );
                }

                patientDataValues.add( patientDataValue );
            }
        }

        // Check type violation
        if ( !typeViolations.isEmpty() )
        {
            return ERROR;
        }

        // Save patient data value
        this.savePatientDataValues( patientDataValues, programStageInstance );

        // Check validation rule
        this.runProgramValidation(
            programValidationService.getProgramValidation( programStageInstance.getProgramStage() ),
            programStageInstance );

        if ( programValidationResults.size() > 0 )
        {
            return ERROR;
        }

        if ( dhisProgramStage.getIrregular() )
        {
            return REGISTER_NEXT_DUEDATE;
        }

        validated = true;

        if ( programStageSectionId != null && programStageSectionId != 0 )
        {
            return SUCCESS_AND_BACK_TO_PROGRAMSTAGE_SECTION;
        }

        if ( orgUnitId != 0 )
        {
            return SUCCESS;
        }
        else
        {
            return SUCCESS_AND_BACK_TO_PROGRAMSTAGE;
        }
    }

    private void savePatientDataValues( List<PatientDataValue> patientDataValues,
        ProgramStageInstance programStageInstance )
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );

        for ( PatientDataValue patientDataValue : patientDataValues )
        {
            PatientDataValue previousPatientDataValue = patientDataValueService.getPatientDataValue(
                patientDataValue.getProgramStageInstance(), patientDataValue.getDataElement() );

            if ( previousPatientDataValue == null )
            {
                if ( patientDataValue.getValue() != null && !patientDataValue.getValue().trim().equals( "" ) )
                    patientDataValueService.savePatientDataValue( patientDataValue );
            }
            else
            {
                if ( patientDataValue.getValue().trim().equals( "" ) )
                {
                    patientDataValueService.deletePatientDataValue( previousPatientDataValue );
                }
                else
                {
                    previousPatientDataValue.setValue( patientDataValue.getValue() );
                    previousPatientDataValue.setTimestamp( new Date() );
                    previousPatientDataValue.setProvidedElsewhere( patientDataValue.getProvidedElsewhere() );
                    patientDataValueService.updatePatientDataValue( previousPatientDataValue );
                }
            }

        }

        if ( programStageSectionId != null && programStageSectionId != 0 )
        {
            programStageInstance.setCompleted( false );
        }
        else
        {
            programStageInstance.setCompleted( true );
            programStageInstance.setOrganisationUnit( organisationUnit );
        }
        programStageInstance.setExecutionDate( new Date() );
        programStageInstanceService.updateProgramStageInstance( programStageInstance );

    }

    private void runProgramValidation( Collection<ProgramValidation> validations,
        ProgramStageInstance programStageInstance )
    {
        programValidationResults = new ArrayList<ProgramValidationResult>();

        if ( validations != null )
        {
            Collection<ProgramValidationResult> validationResults = programValidationService.validate( validations,
                programStageInstance );

            for ( ProgramValidationResult validationResult : validationResults )
            {
                if ( validationResult != null )
                {
                    programValidationResults.add( validationResult );

                    leftsideFormulaMap.put(
                        validationResult.getProgramValidation().getId(),
                        programExpressionService.getExpressionDescription( validationResult.getProgramValidation()
                            .getLeftSide().getExpression() ) );

                    rightsideFormulaMap.put(
                        validationResult.getProgramValidation().getId(),
                        programExpressionService.getExpressionDescription( validationResult.getProgramValidation()
                            .getRightSide().getExpression() ) );
                }
            }
        }
    }
}
