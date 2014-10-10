package org.hisp.dhis.relationship;

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

import java.io.Serializable;

import org.hisp.dhis.patient.Patient;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
public class Relationship
    implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 3818815755138507997L;

    private int id;

    private Patient patientA;

    private RelationshipType relationshipType;

    private Patient patientB;    
    

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Relationship()
    {
    }

    public Relationship( Patient patientA, RelationshipType relationshipType, Patient patientB )
    {
        this.patientA = patientA;
        this.relationshipType = relationshipType;
        this.patientB = patientB;
    }

    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public boolean equals( Object object )
    {
        if ( this == object )
        {
            return true;
        }

        if ( object == null )
        {
            return false;
        }

        if ( getClass() != object.getClass() )
        {
            return false;
        }

        final Relationship other = (Relationship) object;

        return patientA.equals( other.getPatientA() ) && relationshipType.equals( other.getRelationshipType() )
            && patientB.equals( other.getPatientB() );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + patientA.hashCode();
        result = result * prime + patientB.hashCode();
        result = result * prime + relationshipType.hashCode();

        return result;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    /**
     * @return the id
     */
    public int getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId( int id )
    {
        this.id = id;
    }

    /**
     * @return the patientA
     */
    public Patient getPatientA()
    {
        return patientA;
    }

    /**
     * @param patientA the patientA to set
     */
    public void setPatientA( Patient patientA )
    {
        this.patientA = patientA;
    }

    /**
     * @return the relationshipType
     */
    public RelationshipType getRelationshipType()
    {
        return relationshipType;
    }

    /**
     * @param relationshipType the relationshipType to set
     */
    public void setRelationshipType( RelationshipType relationshipType )
    {
        this.relationshipType = relationshipType;
    }

    /**
     * @return the patientB
     */
    public Patient getPatientB()
    {
        return patientB;
    }

    /**
     * @param patientB the patientB to set
     */
    public void setPatientB( Patient patientB )
    {
        this.patientB = patientB;
    }

}
