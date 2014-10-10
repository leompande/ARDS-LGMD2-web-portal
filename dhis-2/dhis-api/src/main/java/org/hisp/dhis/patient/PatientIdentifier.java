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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.view.DetailedView;

/**
 * @author Abyot Asalefew Gizaw
 */
@JacksonXmlRootElement( localName = "personIdentifier", namespace = DxfNamespaces.DXF_2_0 )
public class PatientIdentifier
    extends BaseIdentifiableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 5576120173066869531L;

    public static final int IDENTIFIER_INDEX_LENGTH = 5;

    private PatientIdentifierType identifierType;

    private Patient patient;

    private String identifier;

    public PatientIdentifier()
    {
        setAutoFields();
    }

    public PatientIdentifier( PatientIdentifierType identifierType, Patient patient, String identifier )
    {
        setAutoFields();
        this.identifierType = identifierType;
        this.patient = patient;
        this.identifier = identifier;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ( ( identifierType == null) ? 0 : identifierType.hashCode() );
        result = prime * result + ( ( patient == null) ? 0 : patient.hashCode() );
        result = prime * result + ( ( identifier == null) ? 0 : identifier.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object object )
    {
        if ( this == object )
        {
            return true;
        }
        
        if ( !super.equals( object ) )
        {
            return false;
        }
        
        if ( getClass() != object.getClass() )
        {
            return false;
        }
        
        final PatientIdentifier other = (PatientIdentifier) object;
        
        if ( identifierType == null )
        {
            if ( other.identifierType != null )
            {
                return false;
            }
        }
        else if ( !identifierType.equals( other.identifierType ) )
        {
            return false;
        }
        
        if ( patient == null )
        {
            if ( other.patient != null )
            {
                return false;
            }
        }
        else if ( !patient.equals( other.patient ) )
        {
            return false;
        }

        if ( identifier == null )
        {
            if ( other.identifier != null )
            {
                return false;
            }
        }
        else if ( !identifier.equals( other.identifier ) )
        {
            return false;
        }
        
        return true;
    }

    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Patient getPatient()
    {
        return patient;
    }

    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }

    @JsonProperty
    @JsonView( { DetailedView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getIdentifier()
    {
        return identifier;
    }

    public void setIdentifier( String identifier )
    {
        this.identifier = identifier;
    }

    @JsonProperty( "personIdentifier" )
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class } )
    @JacksonXmlProperty( localName = "personIdentifier", namespace = DxfNamespaces.DXF_2_0 )
    public PatientIdentifierType getIdentifierType()
    {
        return identifierType;
    }

    public void setIdentifierType( PatientIdentifierType identifierType )
    {
        this.identifierType = identifierType;
    }
}