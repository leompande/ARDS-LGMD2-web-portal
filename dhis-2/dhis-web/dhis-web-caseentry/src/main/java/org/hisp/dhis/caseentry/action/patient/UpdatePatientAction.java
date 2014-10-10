package org.hisp.dhis.caseentry.action.patient;

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
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
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
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class UpdatePatientAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientService patientService;

    private PatientAttributeService patientAttributeService;

    private PatientAttributeValueService patientAttributeValueService;

    private PatientIdentifierService patientIdentifierService;

    private PatientIdentifierTypeService patientIdentifierTypeService;

    private PatientAttributeOptionService patientAttributeOptionService;

    private OrganisationUnitSelectionManager selectionManager;

    private I18nFormat format;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    private Integer representativeId;

    private Integer relationshipTypeId;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Patient patient;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        OrganisationUnit organisationUnit = selectionManager.getSelectedOrganisationUnit();

        patient = patientService.getPatient( id );

        // ---------------------------------------------------------------------
        // Set location
        // ---------------------------------------------------------------------

        patient.setOrganisationUnit( organisationUnit );

        // ---------------------------------------------------------------------
        // Save Patient Attributes
        // ---------------------------------------------------------------------

        HttpServletRequest request = ServletActionContext.getRequest();

        Collection<PatientAttribute> attributes = patientAttributeService.getAllPatientAttributes();

        List<PatientAttributeValue> valuesForSave = new ArrayList<PatientAttributeValue>();
        List<PatientAttributeValue> valuesForUpdate = new ArrayList<PatientAttributeValue>();
        Collection<PatientAttributeValue> valuesForDelete = null;

        PatientAttributeValue attributeValue = null;

        if ( attributes != null && attributes.size() > 0 )
        {
            valuesForDelete = patientAttributeValueService.getPatientAttributeValues( patient );

            for ( PatientAttribute attribute : attributes )
            {
                String value = request.getParameter( AddPatientAction.PREFIX_ATTRIBUTE + attribute.getId() );

                if ( StringUtils.isNotBlank( value ) )
                {
                    if ( attribute.getValueType().equals( PatientAttribute.TYPE_AGE ) )
                    {
                        value = format.formatDate( PatientAttribute.getDateFromAge( Integer.parseInt( value ) ) );
                    }

                    attributeValue = patientAttributeValueService.getPatientAttributeValue( patient, attribute );

                    if ( attributeValue == null )
                    {
                        attributeValue = new PatientAttributeValue();
                        attributeValue.setPatient( patient );
                        attributeValue.setPatientAttribute( attribute );
                        attributeValue.setValue( value.trim() );

                        if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                        {
                            PatientAttributeOption option = patientAttributeOptionService
                                .get( Integer.parseInt( value ) );
                            if ( option != null )
                            {
                                attributeValue.setPatientAttributeOption( option );
                                attributeValue.setValue( option.getName() );
                            }
                        }
                        valuesForSave.add( attributeValue );
                    }
                    else
                    {
                        if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                        {
                            PatientAttributeOption option = patientAttributeOptionService.get( NumberUtils.toInt(
                                value, 0 ) );
                            if ( option != null )
                            {
                                attributeValue.setPatientAttributeOption( option );
                                attributeValue.setValue( option.getName() );
                            }
                        }
                        else
                        {
                            attributeValue.setValue( value.trim() );
                        }

                        valuesForUpdate.add( attributeValue );
                        valuesForDelete.remove( attributeValue );
                    }
                }
            }
        }

        patientService.updatePatient( patient, representativeId, relationshipTypeId, valuesForSave, valuesForUpdate,
            valuesForDelete );

        // ---------------------------------------------------------------------
        // Save PatientIdentifier
        // ---------------------------------------------------------------------

        String value = null;

        Collection<PatientIdentifierType> identifierTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();

        PatientIdentifier identifier = null;

        if ( identifierTypes != null && identifierTypes.size() > 0 )
        {
            for ( PatientIdentifierType identifierType : identifierTypes )
            {
                value = request.getParameter( AddPatientAction.PREFIX_IDENTIFIER + identifierType.getId() );

                identifier = patientIdentifierService.getPatientIdentifier( identifierType, patient );

                if ( StringUtils.isNotBlank( value ) )
                {
                    value = value.trim();

                    if ( identifier == null )
                    {
                        identifier = new PatientIdentifier();
                        identifier.setIdentifierType( identifierType );
                        identifier.setPatient( patient );
                        identifier.setIdentifier( value );
                        patientIdentifierService.savePatientIdentifier( identifier );

                        patient.getIdentifiers().add( identifier );
                    }
                    else
                    {
                        identifier.setIdentifier( value );
                        patientIdentifierService.updatePatientIdentifier( identifier );

                        patient.getIdentifiers().add( identifier );
                    }
                }
                else if ( identifier != null )
                {
                    patient.getIdentifiers().remove( identifier );
                    patientIdentifierService.deletePatientIdentifier( identifier );
                }
            }
        }

        patientService.updatePatient( patient );

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Getter/Setter
    // -------------------------------------------------------------------------

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public Patient getPatient()
    {
        return patient;
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

}
