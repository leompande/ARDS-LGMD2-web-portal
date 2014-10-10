package org.hisp.dhis.program;

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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.common.view.WithoutOrganisationUnitsView;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientReminder;
import org.hisp.dhis.relationship.RelationshipType;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.validation.ValidationCriteria;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author Abyot Asalefew
 */
@JacksonXmlRootElement( localName = "program", namespace = DxfNamespaces.DXF_2_0 )
public class Program
    extends BaseIdentifiableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -2581751965520009382L;

    public static final List<String> TYPE_LOOKUP = Arrays.asList( "", "MULTIPLE_EVENTS_WITH_REGISTRATION",
        "SINGLE_EVENT_WITH_REGISTRATION", "SINGLE_EVENT_WITHOUT_REGISTRATION" );

    public static final int MULTIPLE_EVENTS_WITH_REGISTRATION = 1;

    public static final int SINGLE_EVENT_WITH_REGISTRATION = 2;

    public static final int SINGLE_EVENT_WITHOUT_REGISTRATION = 3;

    private String description;

    private Integer version;

    /**
     * Description of Date of Enrollment This description is differ from each
     * program
     */
    private String dateOfEnrollmentDescription;

    /**
     * Description of Date of Incident This description is differ from each
     * program
     */
    private String dateOfIncidentDescription;

    private Set<OrganisationUnit> organisationUnits = new HashSet<OrganisationUnit>();

    private Set<ProgramInstance> programInstances = new HashSet<ProgramInstance>();

    private Set<ProgramStage> programStages = new HashSet<ProgramStage>();

    private Set<ValidationCriteria> patientValidationCriteria = new HashSet<ValidationCriteria>();

    private Integer type;

    private Boolean displayIncidentDate = true;

    private Boolean ignoreOverdueEvents = false;

    private Set<ProgramPatientIdentifierType> programPatientIdentifierTypes = new HashSet<ProgramPatientIdentifierType>();

    private Set<ProgramPatientAttribute> programPatientAttributes = new HashSet<ProgramPatientAttribute>();

    private Set<UserAuthorityGroup> userRoles = new HashSet<UserAuthorityGroup>();

    private Boolean onlyEnrollOnce = false;

    private Set<PatientReminder> patientReminders = new HashSet<PatientReminder>();

    /**
     * All OrganisationUnitGroup that register data with this program.
     */
    private Set<OrganisationUnitGroup> organisationUnitGroups = new HashSet<OrganisationUnitGroup>();

    /**
     * Allow enrolling person to all orgunit no matter what the program is
     * assigned for the orgunit or not
     */
    private Boolean displayOnAllOrgunit = true;

    private Boolean selectEnrollmentDatesInFuture = false;

    private Boolean selectIncidentDatesInFuture = false;

    private String relationshipText;

    private RelationshipType relationshipType;

    private Boolean relationshipFromA = false;

    private Program relatedProgram;

    private Boolean dataEntryMethod = false;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Program()
    {
    }

    public Program( String name, String description )
    {
        this.name = name;
        this.description = description;
    }

    // -------------------------------------------------------------------------
    // Logic methods
    // -------------------------------------------------------------------------

    /**
     * Returns all data elements which are part of the stages of this program.
     */
    public Set<DataElement> getAllDataElements()
    {
        Set<DataElement> elements = new HashSet<DataElement>();

        for ( ProgramStage stage : programStages )
        {
            for ( ProgramStageDataElement element : stage.getProgramStageDataElements() )
            {
                elements.add( element.getDataElement() );
            }
        }

        return elements;
    }

    /**
     * Returns PatientIdentifierTypes from ProgramPatientIdentifierTypes.
     */
    public List<PatientIdentifierType> getIdentifierTypes()
    {
        List<PatientIdentifierType> types = new ArrayList<PatientIdentifierType>();

        for ( ProgramPatientIdentifierType type : programPatientIdentifierTypes )
        {
            types.add( type.getPatientIdentifierType() );
        }

        return types;
    }

    /**
     * Returns PatientAttributes from ProgramPatientAttributes.
     */
    public List<PatientAttribute> getAttributes()
    {
        List<PatientAttribute> attributes = new ArrayList<PatientAttribute>();

        for ( ProgramPatientAttribute attribute : programPatientAttributes )
        {
            attributes.add( attribute.getPatientAttribute() );
        }

        return attributes;
    }

    public ProgramStage getProgramStageByStage( int stage )
    {
        int count = 1;

        for ( ProgramStage programStage : programStages )
        {
            if ( count == stage )
            {
                return programStage;
            }

            count++;
        }

        return null;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Integer getVersion()
    {
        return version;
    }

    public void setVersion( Integer version )
    {
        this.version = version;
    }

    @JsonProperty( value = "organisationUnits" )
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlElementWrapper( localName = "organisationUnits", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "organisationUnit", namespace = DxfNamespaces.DXF_2_0 )
    public Set<OrganisationUnit> getOrganisationUnits()
    {
        return organisationUnits;
    }

    public void setOrganisationUnits( Set<OrganisationUnit> organisationUnits )
    {
        this.organisationUnits = organisationUnits;
    }

    @JsonProperty( value = "programInstances" )
    @JsonView( { DetailedView.class, ExportView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlElementWrapper( localName = "programInstances", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "programInstance", namespace = DxfNamespaces.DXF_2_0 )
    public Set<ProgramInstance> getProgramInstances()
    {
        return programInstances;
    }

    public void setProgramInstances( Set<ProgramInstance> programInstances )
    {
        this.programInstances = programInstances;
    }

    @JsonProperty( value = "programStages" )
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class, ExportView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlElementWrapper( localName = "programStages", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "programStage", namespace = DxfNamespaces.DXF_2_0 )
    public Set<ProgramStage> getProgramStages()
    {
        return programStages;
    }

    public void setProgramStages( Set<ProgramStage> programStages )
    {
        this.programStages = programStages;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getDateOfEnrollmentDescription()
    {
        return dateOfEnrollmentDescription;
    }

    public void setDateOfEnrollmentDescription( String dateOfEnrollmentDescription )
    {
        this.dateOfEnrollmentDescription = dateOfEnrollmentDescription;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getDateOfIncidentDescription()
    {
        return dateOfIncidentDescription;
    }

    public void setDateOfIncidentDescription( String dateOfIncidentDescription )
    {
        this.dateOfIncidentDescription = dateOfIncidentDescription;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getKind()
    {
        return TYPE_LOOKUP.get( type );
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Integer getType()
    {
        return type;
    }

    public void setType( Integer type )
    {
        this.type = type;
    }

    @JsonProperty( value = "validationCriterias" )
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class, ExportView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlElementWrapper( localName = "validationCriterias", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "validationCriteria", namespace = DxfNamespaces.DXF_2_0 )
    public Set<ValidationCriteria> getPatientValidationCriteria()
    {
        return patientValidationCriteria;
    }

    public void setPatientValidationCriteria( Set<ValidationCriteria> patientValidationCriteria )
    {
        this.patientValidationCriteria = patientValidationCriteria;
    }

    @JsonProperty( value = "programPersonIdentifierTypes" )
    @JsonView( { DetailedView.class, ExportView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlElementWrapper( localName = "programPersonIdentifierTypes", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "programPersonIdentifierType", namespace = DxfNamespaces.DXF_2_0 )
    public Set<ProgramPatientIdentifierType> getProgramPatientIdentifierTypes()
    {
        return programPatientIdentifierTypes;
    }

    public void setProgramPatientIdentifierTypes( Set<ProgramPatientIdentifierType> programPatientIdentifierTypes )
    {
        this.programPatientIdentifierTypes = programPatientIdentifierTypes;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Boolean getDisplayIncidentDate()
    {
        return displayIncidentDate;
    }

    public void setDisplayIncidentDate( Boolean displayIncidentDate )
    {
        this.displayIncidentDate = displayIncidentDate;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    private Object getValueFromPatient( String property, Patient patient )
        throws Exception
    {
        return Patient.class.getMethod( "get" + property ).invoke( patient );
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Boolean getIgnoreOverdueEvents()
    {
        return ignoreOverdueEvents;
    }

    public void setIgnoreOverdueEvents( Boolean ignoreOverdueEvents )
    {
        this.ignoreOverdueEvents = ignoreOverdueEvents;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isSingleEvent()
    {
        return type != null && (SINGLE_EVENT_WITH_REGISTRATION == type || SINGLE_EVENT_WITHOUT_REGISTRATION == type);
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isRegistration()
    {
        return type != null && (SINGLE_EVENT_WITH_REGISTRATION == type || MULTIPLE_EVENTS_WITH_REGISTRATION == type);
    }

    @JsonProperty
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class, ExportView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlElementWrapper( localName = "userRoles", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "userRole", namespace = DxfNamespaces.DXF_2_0 )
    public Set<UserAuthorityGroup> getUserRoles()
    {
        return userRoles;
    }

    public void setUserRoles( Set<UserAuthorityGroup> userRoles )
    {
        this.userRoles = userRoles;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Boolean getOnlyEnrollOnce()
    {
        return onlyEnrollOnce;
    }

    public void setOnlyEnrollOnce( Boolean onlyEnrollOnce )
    {
        this.onlyEnrollOnce = onlyEnrollOnce;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Set<PatientReminder> getPatientReminders()
    {
        return patientReminders;
    }

    public void setPatientReminders( Set<PatientReminder> patientReminders )
    {
        this.patientReminders = patientReminders;
    }

    @JsonProperty
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class, ExportView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlElementWrapper( localName = "organisationUnitGroups", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "organisationUnitGroup", namespace = DxfNamespaces.DXF_2_0 )
    public Set<OrganisationUnitGroup> getOrganisationUnitGroups()
    {
        return organisationUnitGroups;
    }

    public void setOrganisationUnitGroups( Set<OrganisationUnitGroup> organisationUnitGroups )
    {
        this.organisationUnitGroups = organisationUnitGroups;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Boolean getDisplayOnAllOrgunit()
    {
        return displayOnAllOrgunit;
    }

    public void setDisplayOnAllOrgunit( Boolean displayOnAllOrgunit )
    {
        this.displayOnAllOrgunit = displayOnAllOrgunit;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Boolean getSelectEnrollmentDatesInFuture()
    {
        return selectEnrollmentDatesInFuture;
    }

    public void setSelectEnrollmentDatesInFuture( Boolean selectEnrollmentDatesInFuture )
    {
        this.selectEnrollmentDatesInFuture = selectEnrollmentDatesInFuture;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Boolean getSelectIncidentDatesInFuture()
    {
        return selectIncidentDatesInFuture;
    }

    public void setSelectIncidentDatesInFuture( Boolean selectIncidentDatesInFuture )
    {
        this.selectIncidentDatesInFuture = selectIncidentDatesInFuture;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getRelationshipText()
    {
        return relationshipText;
    }

    public void setRelationshipText( String relationshipText )
    {
        this.relationshipText = relationshipText;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public RelationshipType getRelationshipType()
    {
        return relationshipType;
    }

    public void setRelationshipType( RelationshipType relationshipType )
    {
        this.relationshipType = relationshipType;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Program getRelatedProgram()
    {
        return relatedProgram;
    }

    public void setRelatedProgram( Program relatedProgram )
    {
        this.relatedProgram = relatedProgram;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Boolean getRelationshipFromA()
    {
        return relationshipFromA;
    }

    public void setRelationshipFromA( Boolean relationshipFromA )
    {
        this.relationshipFromA = relationshipFromA;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Boolean getDataEntryMethod()
    {
        return dataEntryMethod;
    }

    public void setDataEntryMethod( Boolean dataEntryMethod )
    {
        this.dataEntryMethod = dataEntryMethod;
    }

    @JsonProperty( value = "programPersonAttributes" )
    @JsonView( { DetailedView.class, ExportView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlElementWrapper( localName = "programPersonAttributes", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "programPersonAttribute", namespace = DxfNamespaces.DXF_2_0 )
    public Set<ProgramPatientAttribute> getProgramPatientAttributes()
    {
        return programPatientAttributes;
    }

    public void setProgramPatientAttributes( Set<ProgramPatientAttribute> programPatientAttributes )
    {
        this.programPatientAttributes = programPatientAttributes;
    }
}
