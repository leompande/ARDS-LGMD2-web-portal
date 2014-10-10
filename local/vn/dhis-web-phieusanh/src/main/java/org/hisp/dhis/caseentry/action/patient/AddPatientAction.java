/*
 * Copyright (c) 2004-2009, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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

package org.hisp.dhis.caseentry.action.patient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.caseentry.idgen.PatientIdentifierGenerator;
import org.hisp.dhis.caseentry.state.SelectedStateManager;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
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
import org.hisp.dhis.phieusanh.action.OrganisationUnitService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class AddPatientAction
    implements Action
{
    public static final String PREFIX_ATTRIBUTE = "attr";

    public static final String PREFIX_IDENTIFIER = "iden";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private I18nFormat format;

    private PatientService patientService;

    private PatientIdentifierService patientIdentifierService;

    private PatientIdentifierTypeService patientIdentifierTypeService;

    private OrganisationUnitSelectionManager selectionManager;

    private SelectedStateManager selectedStateManager;

    private PatientAttributeService patientAttributeService;

    private PatientAttributeOptionService patientAttributeOptionService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String fullName;

    private String birthDate;

    private Integer age;

    private Boolean verified;

    private String gender;

    private String phoneNumber;

    private String registrationDate;

    private boolean underAge;

    private Integer representativeId;

    private Integer relationshipTypeId;

    private String message;

    private Integer registeredUnit;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        // ---------------------------------------------------------------------
        // Prepare values
        // ---------------------------------------------------------------------

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( registeredUnit );

        Patient patient = new Patient();

        verified = (verified == null) ? false : verified;

        // ---------------------------------------------------------------------
        // Set FirstName, MiddleName, LastName by FullName
        // ---------------------------------------------------------------------

        fullName = fullName.trim();

        int startIndex = fullName.indexOf( ' ' );
        int endIndex = fullName.lastIndexOf( ' ' );

        String firstName = fullName.toString();
        String middleName = "";
        String lastName = "";

        if ( fullName.indexOf( ' ' ) != -1 )
        {
            firstName = fullName.substring( 0, startIndex );
            if ( startIndex == endIndex )
            {
                middleName = "";
                lastName = fullName.substring( startIndex + 1, fullName.length() );
            }
            else
            {
                middleName = fullName.substring( startIndex + 1, endIndex );
                lastName = fullName.substring( endIndex + 1, fullName.length() );
            }
        }

        patient.setFirstName( firstName );
        patient.setMiddleName( middleName );
        patient.setLastName( lastName );

        // ---------------------------------------------------------------------
        // Set Other information for patient
        // ---------------------------------------------------------------------

        patient.setGender( gender );
        patient.setIsDead( false );
        patient.setPhoneNumber( phoneNumber );
        patient.setUnderAge( underAge );
        patient.setOrganisationUnit( organisationUnit );

        Character dobType = (verified) ? 'V' : 'D';

        if ( !verified && age != null )
        {
            dobType = 'A';
        }

        if ( dobType == Patient.DOB_TYPE_VERIFIED || dobType == Patient.DOB_TYPE_DECLARED )
        {
            birthDate = birthDate.trim();
            patient.setBirthDate( format.parseDate( birthDate ) );
        }
        else
        {
            patient.setBirthDateFromAge( age.intValue(), Patient.AGE_TYPE_YEAR );
        }

        patient.setDobType( dobType );

        patient.setRegistrationDate( format.parseDate( registrationDate ) );

        // -----------------------------------------------------------------------------
        // Prepare Patient Identifiers
        // -----------------------------------------------------------------------------

        HttpServletRequest request = ServletActionContext.getRequest();

        Collection<PatientIdentifierType> identifierTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();

        String value = null;

        PatientIdentifier pIdentifier = null;

        if ( identifierTypes != null && identifierTypes.size() > 0 )
        {
            for ( PatientIdentifierType identifierType : identifierTypes )
            {

                value = request.getParameter( PREFIX_IDENTIFIER + identifierType.getId() );

                if ( StringUtils.isNotBlank( value ) )
                {
                    pIdentifier = new PatientIdentifier();
                    pIdentifier.setIdentifierType( identifierType );
                    pIdentifier.setPatient( patient );
                    pIdentifier.setIdentifier( value.trim() );
                    patient.getIdentifiers().add( pIdentifier );
                }
            }
        }

        // --------------------------------------------------------------------------------
        // Generate system id with this format :
        // (BirthDate)(Gender)(XXXXXX)(checkdigit)
        // PatientIdentifierType will be null
        // --------------------------------------------------------------------------------

        String identifier = PatientIdentifierGenerator.getNewIdentifier( patient.getBirthDate(), patient.getGender() );

        PatientIdentifier systemGenerateIdentifier = patientIdentifierService.get( null, identifier );
        while ( systemGenerateIdentifier != null )
        {
            identifier = PatientIdentifierGenerator.getNewIdentifier( patient.getBirthDate(), patient.getGender() );
            systemGenerateIdentifier = patientIdentifierService.get( null, identifier );
        }

        systemGenerateIdentifier = new PatientIdentifier();
        systemGenerateIdentifier.setIdentifier( identifier );
        systemGenerateIdentifier.setPatient( patient );

        patient.getIdentifiers().add( systemGenerateIdentifier );

        selectedStateManager.clearListAll();
        selectedStateManager.clearSearchingAttributeId();
        selectedStateManager.setSearchText( systemGenerateIdentifier.getIdentifier() );

        // -----------------------------------------------------------------------------
        // Prepare Patient Attributes
        // -----------------------------------------------------------------------------

        Collection<PatientAttribute> attributes = patientAttributeService.getAllPatientAttributes();

        List<PatientAttributeValue> patientAttributeValues = new ArrayList<PatientAttributeValue>();

        PatientAttributeValue attributeValue = null;

        if ( attributes != null && attributes.size() > 0 )
        {
            for ( PatientAttribute attribute : attributes )
            {
                value = request.getParameter( PREFIX_ATTRIBUTE + attribute.getId() );
                if ( StringUtils.isNotBlank( value ) )
                {
                    if ( !patient.getAttributes().contains( attribute ) )
                    {
                        patient.getAttributes().add( attribute );
                    }

                    attributeValue = new PatientAttributeValue();
                    attributeValue.setPatient( patient );
                    attributeValue.setPatientAttribute( attribute );

                    if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                    {
                        PatientAttributeOption option = patientAttributeOptionService
                            .get( NumberUtils.toInt( value, 0 ) );
                        if ( option != null )
                        {
                            attributeValue.setPatientAttributeOption( option );
                            attributeValue.setValue( option.getName() );
                        }
                        else
                        {
                            // Someone deleted this option ...
                        }
                    }
                    else
                    {
                        attributeValue.setValue( value.trim() );
                    }

                    patientAttributeValues.add( attributeValue );
                }
            }
        }

        // -------------------------------------------------------------------------
        // Save patient
        // -------------------------------------------------------------------------

        Integer id = patientService.createPatient( patient, representativeId, relationshipTypeId,
            patientAttributeValues );

        message = id + "_" + systemGenerateIdentifier.getIdentifier();

        return SUCCESS;
    }

    // -----------------------------------------------------------------------------
    // Getter/Setter
    // -----------------------------------------------------------------------------

    public String getMessage()
    {
        return message;
    }

    public void setVerified( Boolean verified )
    {
        this.verified = verified;
    }

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    public void setBirthDate( String birthDate )
    {
        this.birthDate = birthDate;
    }

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    public void setFullName( String fullName )
    {
        this.fullName = fullName;
    }

    public void setAge( Integer age )
    {
        this.age = age;
    }

    public void setRegistrationDate( String registrationDate )
    {
        this.registrationDate = registrationDate;
    }

    public void setGender( String gender )
    {
        this.gender = gender;
    }

    public void setPhoneNumber( String phoneNumber )
    {
        this.phoneNumber = phoneNumber;
    }

    public void setPatientAttributeOptionService( PatientAttributeOptionService patientAttributeOptionService )
    {
        this.patientAttributeOptionService = patientAttributeOptionService;
    }

    public void setRepresentativeId( Integer representativeId )
    {
        this.representativeId = representativeId;
    }

    public void setRelationshipTypeId( Integer relationshipTypeId )
    {
        this.relationshipTypeId = relationshipTypeId;
    }

    public void setUnderAge( boolean underAge )
    {
        this.underAge = underAge;
    }

    public void setRegisteredUnit( Integer registeredUnit )
    {
        this.registeredUnit = registeredUnit;
    }
}
