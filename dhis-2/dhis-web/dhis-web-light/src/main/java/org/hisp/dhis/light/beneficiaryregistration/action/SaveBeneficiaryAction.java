package org.hisp.dhis.light.beneficiaryregistration.action;

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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.StrutsStatics;
import org.hisp.dhis.light.utils.ValueUtils;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
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
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.util.ContextUtils;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;

public class SaveBeneficiaryAction
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

    private OrganisationUnitService organisationUnitService;

    public OrganisationUnitService getOrganisationUnitService()
    {
        return organisationUnitService;
    }

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
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

    private ProgramService programService;

    public ProgramService getProgramService()
    {
        return programService;
    }

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer orgUnitId;

    public Integer getOrgUnitId()
    {
        return orgUnitId;
    }

    public void setOrgUnitId( Integer orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    private String patientFullName;

    public String getPatientFullName()
    {
        return patientFullName;
    }

    public void setPatientFullName( String patientFullName )
    {
        this.patientFullName = patientFullName;
    }

    private String gender;

    public String getGender()
    {
        return gender;
    }

    public void setGender( String gender )
    {
        this.gender = gender;
    }

    private String dateOfBirth;

    public String getDateOfBirth()
    {
        return dateOfBirth;
    }

    public void setDateOfBirth( String dateOfBirth )
    {
        this.dateOfBirth = dateOfBirth;
    }

    public boolean validated;

    public boolean isValidated()
    {
        return validated;
    }

    public void setValidated( boolean validated )
    {
        this.validated = validated;
    }

    public Map<String, String> validationMap = new HashMap<String, String>();

    public Map<String, String> getValidationMap()
    {
        return validationMap;
    }

    public void setValidationMap( Map<String, String> validationMap )
    {
        this.validationMap = validationMap;
    }

    public Map<String, String> previousValues = new HashMap<String, String>();

    public Map<String, String> getPreviousValues()
    {
        return previousValues;
    }

    public void setPreviousValues( Map<String, String> previousValues )
    {
        this.previousValues = previousValues;
    }

    private String dobType;

    public String getDobType()
    {
        return dobType;
    }

    public void setDobType( String dobType )
    {
        this.dobType = dobType;
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

    private String phoneNumber;

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber( String phoneNumber )
    {
        this.phoneNumber = phoneNumber;
    }

    // Register patient on-the-fly

    private Integer originalPatientId;

    public Integer getOriginalPatientId()
    {
        return originalPatientId;
    }

    public void setOriginalPatientId( Integer originalPatientId )
    {
        this.originalPatientId = originalPatientId;
    }

    private Integer relationshipTypeId;

    public Integer getRelationshipTypeId()
    {
        return relationshipTypeId;
    }

    public void setRelationshipTypeId( Integer relationshipTypeId )
    {
        this.relationshipTypeId = relationshipTypeId;
    }

    @Override
    public String execute()
        throws Exception
    {
        Patient patient = new Patient();
        Set<PatientIdentifier> patientIdentifierSet = new HashSet<PatientIdentifier>();
        Set<PatientAttribute> patientAttributeSet = new HashSet<PatientAttribute>();
        Set<PatientAttributeValue> patientAttributeValues = new HashSet<PatientAttributeValue>();

        patientIdentifierTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();
        patientAttributes = patientAttributeService.getAllPatientAttributes();
        Collection<Program> programs = programService.getAllPrograms();

        for ( Program program : programs )
        {
            patientIdentifierTypes.removeAll( program.getIdentifierTypes() );
            patientAttributes.removeAll( program.getAttributes() );
        }

        patient.setOrganisationUnit( organisationUnitService.getOrganisationUnit( orgUnitId ) );

        if ( this.patientFullName.trim().length() < 7 )
        {
            validationMap.put( "fullName", "is_invalid_name_length" );
        }
        else
        {
            patient.setName( patientFullName.trim() );
        }

        HttpServletRequest request = (HttpServletRequest) ActionContext.getContext().get( StrutsStatics.HTTP_REQUEST );
        Map<String, String> parameterMap = ContextUtils.getParameterMap( request );

        // Add Identifier and Attributes
        Collection<PatientIdentifierType> patientIdentifierTypes = patientIdentifierTypeService
            .getAllPatientIdentifierTypes();
        Collection<PatientAttribute> patientAttributes = patientAttributeService.getAllPatientAttributes();

        for ( Program program : programs )
        {
            patientIdentifierTypes.removeAll( program.getIdentifierTypes() );
            patientAttributes.removeAll( program.getAttributes() );
        }

        for ( PatientIdentifierType patientIdentifierType : patientIdentifierTypes )
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
                    patientIdentifierSet.add( patientIdentifier );
                }

                this.previousValues.put( key, value );
            }
        }

        for ( PatientAttribute patientAttribute : patientAttributes )
        {
            patientAttributeSet.add( patientAttribute );

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
                        PatientAttributeOption option = patientAttributeOptionService
                            .get( NumberUtils.toInt( value, 0 ) );

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

        if ( this.validationMap.size() > 0 )
        {
            this.validated = false;
            this.previousValues.put( "fullName", this.patientFullName );
            this.previousValues.put( "gender", this.gender );
            this.previousValues.put( "dob", this.dateOfBirth );
            this.previousValues.put( "dobType", this.dobType );
            this.previousValues.put( "phoneNumber", this.phoneNumber );
            return ERROR;
        }

        patient.setIdentifiers( patientIdentifierSet );
        patientId = patientService.createPatient( patient, null, null, patientAttributeValues );
        validated = true;

        if ( this.originalPatientId != null )
        {
            return "redirect";
        }

        return SUCCESS;
    }
}
