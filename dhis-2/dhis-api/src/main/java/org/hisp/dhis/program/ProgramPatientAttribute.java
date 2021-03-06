/*
 * Copyright (c) 2004-2013, University of Oslo
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

package org.hisp.dhis.program;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.common.view.WithoutOrganisationUnitsView;
import org.hisp.dhis.patient.PatientAttribute;

import java.io.Serializable;

/**
 * @author Chau Thu Tran
 * @version $ ProgramPatientAttribute.java Jan 7, 2014 9:16:05 AM $
 */
@JacksonXmlRootElement( localName = "programPersonAttribute", namespace = DxfNamespaces.DXF_2_0 )
public class ProgramPatientAttribute
    implements Serializable
{
    private static final long serialVersionUID = -2420475559273198337L;

    private int id;

    private PatientAttribute patientAttribute;

    private Integer sortOrder;

    private boolean displayedInList;


    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ProgramPatientAttribute()
    {
    }

    public ProgramPatientAttribute( PatientAttribute patientAttribute, int sortOrder, boolean displayedInList )
    {
        this.patientAttribute = patientAttribute;
        this.sortOrder = sortOrder;
        this.displayedInList = displayedInList;
    }

    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + patientAttribute.hashCode();
        result = result * prime + sortOrder;

        return result;
    }

    @Override
    public boolean equals( Object object )
    {
        if ( object == null )
        {
            return false;
        }

        if ( getClass() != object.getClass() )
        {
            return false;
        }

        final ProgramPatientAttribute other = (ProgramPatientAttribute) object;

        return patientAttribute.equals( other.getPatientAttribute() ) && sortOrder == other.getSortOrder();
    }

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class, ExportView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlProperty( localName = "personAttribute", namespace = DxfNamespaces.DXF_2_0 )
    public PatientAttribute getPatientAttribute()
    {
        return patientAttribute;
    }

    public void setPatientAttribute( PatientAttribute patientAttribute )
    {
        this.patientAttribute = patientAttribute;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder( Integer sortOrder )
    {
        this.sortOrder = sortOrder;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, WithoutOrganisationUnitsView.class } )
    @JacksonXmlProperty( localName = "displayedInList", namespace = DxfNamespaces.DXF_2_0 )
    public Boolean getDisplayedInList()
    {
        return displayedInList;
    }

    public void setDisplayedInList( Boolean displayedInList )
    {
        this.displayedInList = displayedInList;
    }
}
