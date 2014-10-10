package org.hisp.dhis.patientattributevalue;

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
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeOption;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
public class PatientAttributeValue
    implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -4469496681709547707L;

    public static final String UNKNOWN = " ";

    private PatientAttribute patientAttribute;

    private Patient patient;

    private String value;

    private PatientAttributeOption patientAttributeOption;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public PatientAttributeValue()
    {
    }

    public PatientAttributeValue( PatientAttribute patientAttribute, Patient patient )
    {
        this.patientAttribute = patientAttribute;
        this.patient = patient;
    }

    public PatientAttributeValue( PatientAttribute patientAttribute, Patient patient, String value )
    {
        this.patientAttribute = patientAttribute;
        this.patient = patient;
        this.value = value;
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------


    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( patient == null) ? 0 : patient.hashCode() );
        result = prime * result + ( ( patientAttribute == null) ? 0 : patientAttribute.hashCode() );
        result = prime * result + ( ( patientAttributeOption == null) ? 0 : patientAttributeOption.hashCode() );
        result = prime * result + ( ( value == null) ? 0 : value.hashCode() );
        return result;
    }

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
        
        if ( !getClass().isAssignableFrom( object.getClass() ) )
        {
            return false;
        }
        
        final PatientAttributeValue other = (PatientAttributeValue) object;
        
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
        
        if ( patientAttribute == null )
        {
            if ( other.patientAttribute != null )
            {
                return false;
            }
        }
        else if ( !patientAttribute.equals( other.patientAttribute ) )
        {
            return false;
        }
        
        if ( patientAttributeOption == null )
        {
            if ( other.patientAttributeOption != null )
            {
                return false;
            }
        }
        else if ( !patientAttributeOption.equals( other.patientAttributeOption ) )
        {
            return false;
        }
        
        if ( value == null )
        {
            if ( other.value != null )
            {
                return false;
            }
        }
        else if ( !value.equals( other.value ) )
        {
            return false;
        }
        
        return true;
    }
    
    @Override
    public String toString()
    {
        return "[Patient attribute=" + patientAttribute +
            ", patient=" + patient +
            ", value='" + value + "'" +
            ", attribute option=" + patientAttributeOption + "]";
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public PatientAttribute getPatientAttribute()
    {
        return patientAttribute;
    }

    public void setPatientAttribute( PatientAttribute patientAttribute )
    {
        this.patientAttribute = patientAttribute;
    }

    public Patient getPatient()
    {
        return patient;
    }

    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public PatientAttributeOption getPatientAttributeOption()
    {
        return patientAttributeOption;
    }

    public void setPatientAttributeOption( PatientAttributeOption patientAttributeOption )
    {
        this.patientAttributeOption = patientAttributeOption;
    }
}
