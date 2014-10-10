package org.hisp.dhis.patient;

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

import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.program.ProgramInstance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author Abyot Asalefew Gizaw
 */
@JacksonXmlRootElement( localName = "person", namespace = DxfNamespaces.DXF_2_0 )
public class Patient
    extends BaseIdentifiableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 884114994005945275L;

    public static String SEARCH_SAPERATE = "_";

    public static String PREFIX_IDENTIFIER_TYPE = "iden";

    public static String PREFIX_PATIENT_ATTRIBUTE = "attr";

    public static String PREFIX_PROGRAM = "prg";

    public static String PREFIX_PROGRAM_INSTANCE = "pi";

    public static String PREFIX_PROGRAM_EVENT_BY_STATUS = "stat";

    public static String PREFIX_PROGRAM_STAGE = "prgst";

    private Set<PatientIdentifier> identifiers = new HashSet<PatientIdentifier>();

    private Set<PatientAttributeValue> attributeValues = new HashSet<PatientAttributeValue>();

    private Set<ProgramInstance> programInstances = new HashSet<ProgramInstance>();

    private OrganisationUnit organisationUnit;

    private Patient representative;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Patient()
    {
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

    public Set<PatientIdentifier> getIdentifiers()
    {
        return identifiers;
    }

    public void setIdentifiers( Set<PatientIdentifier> identifiers )
    {
        this.identifiers = identifiers;
    }

    public Set<PatientAttributeValue> getAttributeValues()
    {
        return attributeValues;
    }

    public void setAttributeValues( Set<PatientAttributeValue> attributeValues )
    {
        this.attributeValues = attributeValues;
    }

    public Set<ProgramInstance> getProgramInstances()
    {
        return programInstances;
    }

    public void setProgramInstances( Set<ProgramInstance> programInstances )
    {
        this.programInstances = programInstances;
    }

    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Patient getRepresentative()
    {
        return representative;
    }

    public void setRepresentative( Patient representative )
    {
        this.representative = representative;
    }

    // -------------------------------------------------------------------------
    // Convenience method
    // -------------------------------------------------------------------------

    public void addIdentifier( PatientIdentifier identifier )
    {
        identifiers.add( identifier );
        identifier.setPatient( this );
    }

    public void removeIdentifier( PatientIdentifier identifier )
    {
        identifiers.remove( identifier );
        identifier.setPatient( null );
    }

    public void addAttributeValue( PatientAttributeValue attributeValue )
    {
        attributeValues.add( attributeValue );
        attributeValue.setPatient( this );
    }

    public void removeAttributeValue( PatientAttributeValue attributeValue )
    {
        attributeValues.remove( attributeValue );
        attributeValue.setPatient( null );
    }

}
