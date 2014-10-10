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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientAttributeGroupService;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patient.PatientRegistrationForm;
import org.hisp.dhis.patient.PatientRegistrationFormService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patient.comparator.PatientAttributeGroupSortOrderComparator;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.relationship.Relationship;
import org.hisp.dhis.relationship.RelationshipService;
import org.hisp.dhis.relationship.RelationshipType;
import org.hisp.dhis.relationship.RelationshipTypeService;
import org.hisp.dhis.user.User;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class GetPatientAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientService patientService;

    private ProgramService programService;

    private PatientAttributeValueService patientAttributeValueService;

    private PatientIdentifierTypeService patientIdentifierTypeService;

    private RelationshipService relationshipService;

    private RelationshipTypeService relationshipTypeService;

    private PatientRegistrationFormService patientRegistrationFormService;

    private PatientAttributeGroupService attributeGroupService;

    private PatientAttributeService attributeService;

    @Autowired
    private ProgramInstanceService programInstanceService;

    private I18n i18n;

    private I18nFormat format;

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Collection<RelationshipType> relationshipTypes;

    private int id;

    private Patient patient;

    private Collection<Program> programs;

    private Map<Integer, String> patientAttributeValueMap = new HashMap<Integer, String>();

    private Collection<PatientAttribute> noGroupAttributes = new HashSet<PatientAttribute>();

    private List<PatientAttributeGroup> attributeGroups;

    private Collection<PatientIdentifierType> identifierTypes;

    private Map<Integer, String> identiferMap;

    private String childContactName;

    private String childContactType;

    private String systemIdentifier;

    private Relationship relationship;

    private Map<Integer, Collection<PatientAttribute>> attributeGroupsMap = new HashMap<Integer, Collection<PatientAttribute>>();

    private Collection<User> healthWorkers;

    private Integer programId;

    private Map<String, List<PatientAttribute>> attributesMap = new HashMap<String, List<PatientAttribute>>();

    private PatientRegistrationForm patientRegistrationForm;

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    private String customRegistrationForm;

    public String getCustomRegistrationForm()
    {
        return customRegistrationForm;
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

    public void setAttributeService( PatientAttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    public Map<String, List<PatientAttribute>> getAttributesMap()
    {
        return attributesMap;
    }

    public PatientRegistrationForm getPatientRegistrationForm()
    {
        return patientRegistrationForm;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        relationshipTypes = relationshipTypeService.getAllRelationshipTypes();
        patient = patientService.getPatient( id );

        // Get system identifier

        for ( PatientIdentifier identifier : patient.getIdentifiers() )
        {
            if ( identifier.getIdentifierType() == null )
            {
                systemIdentifier = identifier.getIdentifier();
                break;
            }
        }

        healthWorkers = patient.getOrganisationUnit().getUsers();
        Program program = null;

        if ( programId == null )
        {
            patientRegistrationForm = patientRegistrationFormService.getCommonPatientRegistrationForm();

            if ( patientRegistrationForm != null && patientRegistrationForm.getDataEntryForm() != null )
            {
                customRegistrationForm = patientRegistrationFormService.prepareDataEntryFormForAdd(
                    patientRegistrationForm.getDataEntryForm().getHtmlCode(), patientRegistrationForm.getProgram(),
                    healthWorkers, patient, null, i18n, format );
            }
        }
        else
        {
            program = programService.getProgram( programId );
            patientRegistrationForm = patientRegistrationFormService.getPatientRegistrationForm( program );

            Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( patient,
                program, ProgramInstance.STATUS_ACTIVE );
            ProgramInstance programIntance = null;
            if ( programInstances != null )
            {
                programIntance = programInstances.iterator().next();
            }

            if ( patientRegistrationForm != null && patientRegistrationForm.getDataEntryForm() != null )
            {
                customRegistrationForm = patientRegistrationFormService.prepareDataEntryFormForAdd(
                    patientRegistrationForm.getDataEntryForm().getHtmlCode(), patientRegistrationForm.getProgram(),
                    healthWorkers, patient, programIntance, i18n, format );
            }
        }

        List<PatientAttribute> attributes = new ArrayList<PatientAttribute>();

        if ( customRegistrationForm == null )
        {
            attributeGroups = new ArrayList<PatientAttributeGroup>(
                attributeGroupService.getAllPatientAttributeGroups() );
            Collections.sort( attributeGroups, new PatientAttributeGroupSortOrderComparator() );

            if ( program == null )
            {
                identifierTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();
                attributes = new ArrayList<PatientAttribute>( attributeService.getAllPatientAttributes() );
                Collection<Program> programs = programService.getAllPrograms();
                for ( Program p : programs )
                {
                    for ( PatientIdentifierType identifierType : p.getIdentifierTypes() )
                    {
                        if ( !identifierType.getDisplayInListNoProgram() )
                        {
                            identifierTypes.remove( identifierType ); // TODO
                                                                      // bad
                        }
                    }

                    for ( PatientAttribute attribute : p.getAttributes() )
                    {
                        if ( !attribute.getDisplayInListNoProgram() )
                        {
                            attributes.remove( attribute );
                        }
                    }
                }
            }
            else
            {
                identifierTypes = program.getIdentifierTypes();
                attributes = program.getAttributes();
            }

            for ( PatientAttribute attribute : attributes )
            {
                PatientAttributeGroup patientAttributeGroup = attribute.getPatientAttributeGroup();
                String groupName = (patientAttributeGroup == null) ? "" : patientAttributeGroup.getDisplayName();
                if ( attributesMap.containsKey( groupName ) )
                {
                    List<PatientAttribute> attrs = attributesMap.get( groupName );
                    attrs.add( attribute );
                }
                else
                {
                    List<PatientAttribute> attrs = new ArrayList<PatientAttribute>();
                    attrs.add( attribute );
                    attributesMap.put( groupName, attrs );
                }
            }

        }

        // -------------------------------------------------------------------------
        // Get data
        // -------------------------------------------------------------------------

        identiferMap = new HashMap<Integer, String>();

        PatientIdentifierType idType = null;
        Patient representative = patient.getRepresentative();
        if ( representative != null )
        {
            relationship = relationshipService.getRelationship( representative, patient );

            for ( PatientIdentifier representativeIdentifier : representative.getIdentifiers() )
            {
                if ( representativeIdentifier.getIdentifierType() != null
                    && representativeIdentifier.getIdentifierType().isRelated() )
                {
                    identiferMap.put( representativeIdentifier.getIdentifierType().getId(),
                        representativeIdentifier.getIdentifier() );
                }
            }
        }

        for ( PatientIdentifier identifier : patient.getIdentifiers() )
        {
            idType = identifier.getIdentifierType();

            if ( idType != null )
            {
                identiferMap.put( identifier.getIdentifierType().getId(), identifier.getIdentifier() );
            }
        }

        // -------------------------------------------------------------------------
        // Get patient-attribute values
        // -------------------------------------------------------------------------

        Collection<PatientAttributeValue> patientAttributeValues = patientAttributeValueService
            .getPatientAttributeValues( patient );

        for ( PatientAttributeValue patientAttributeValue : patientAttributeValues )
        {
            String value = patientAttributeValue.getValue();

            if ( patientAttributeValue.getPatientAttribute().getValueType().equals( PatientAttribute.TYPE_AGE )
                && value != null )
            {
                Date date = format.parseDate( value );
                value = PatientAttribute.getAgeFromDate( date ) + "";
            }

            patientAttributeValueMap.put( patientAttributeValue.getPatientAttribute().getId(), value );
        }

        return SUCCESS;

    }

    // -----------------------------------------------------------------------------
    // Getter / Setter
    // -----------------------------------------------------------------------------

    public void setPatientRegistrationFormService( PatientRegistrationFormService patientRegistrationFormService )
    {
        this.patientRegistrationFormService = patientRegistrationFormService;
    }

    public void setAttributeGroupService( PatientAttributeGroupService attributeGroupService )
    {
        this.attributeGroupService = attributeGroupService;
    }

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    public void setRelationshipTypeService( RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
    }

    public Collection<RelationshipType> getRelationshipTypes()
    {
        return relationshipTypes;
    }

    public Collection<User> getHealthWorkers()
    {
        return healthWorkers;
    }

    public Map<Integer, Collection<PatientAttribute>> getAttributeGroupsMap()
    {
        return attributeGroupsMap;
    }

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    public Relationship getRelationship()
    {
        return relationship;
    }

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    public void setRelationshipService( RelationshipService relationshipService )
    {
        this.relationshipService = relationshipService;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public Patient getPatient()
    {
        return patient;
    }

    public Collection<Program> getPrograms()
    {
        return programs;
    }

    public Map<Integer, String> getPatientAttributeValueMap()
    {
        return patientAttributeValueMap;
    }

    public Collection<PatientAttribute> getNoGroupAttributes()
    {
        return noGroupAttributes;
    }

    public List<PatientAttributeGroup> getAttributeGroups()
    {
        return attributeGroups;
    }

    public Collection<PatientIdentifierType> getIdentifierTypes()
    {
        return identifierTypes;
    }

    public Map<Integer, String> getIdentiferMap()
    {
        return identiferMap;
    }

    public String getChildContactName()
    {
        return childContactName;
    }

    public String getChildContactType()
    {
        return childContactType;
    }

    public String getSystemIdentifier()
    {
        return systemIdentifier;
    }

}
