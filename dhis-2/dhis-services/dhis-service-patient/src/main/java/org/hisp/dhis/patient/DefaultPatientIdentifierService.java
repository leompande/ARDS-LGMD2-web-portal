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

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
@Transactional
public class DefaultPatientIdentifierService
    implements PatientIdentifierService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientIdentifierStore patientIdentifierStore;

    public void setPatientIdentifierStore( PatientIdentifierStore patientIdentifierStore )
    {
        this.patientIdentifierStore = patientIdentifierStore;
    }

    // -------------------------------------------------------------------------
    // PatientIdentifier
    // -------------------------------------------------------------------------

    public int savePatientIdentifier( PatientIdentifier patientIdentifier )
    {
        return patientIdentifierStore.save( patientIdentifier );
    }

    public void deletePatientIdentifier( PatientIdentifier patientIdentifier )
    {
        patientIdentifierStore.delete( patientIdentifier );
    }

    public void updatePatientIdentifier( PatientIdentifier patientIdentifier )
    {
        patientIdentifierStore.update( patientIdentifier );
    }

    public PatientIdentifier getPatientIdentifier( int id )
    {
        return patientIdentifierStore.get( id );
    }

    public PatientIdentifier getPatientIdentifier( Patient patient )
    {
        return patientIdentifierStore.get( patient );
    }

    public Collection<PatientIdentifier> getAllPatientIdentifiers()
    {
        return patientIdentifierStore.getAll();
    }

    public Collection<PatientIdentifier> getPatientIdentifiersByType( PatientIdentifierType identifierType )
    {
        return patientIdentifierStore.getByType( identifierType );
    }

    public Collection<PatientIdentifier> getPatientIdentifiersByIdentifier( String identifier )
    {
        return patientIdentifierStore.getByIdentifier( identifier );
    }

    public PatientIdentifier getPatientIdentifier( String identifier, Patient patient )
    {
        return patientIdentifierStore.getPatientIdentifier( identifier, patient );
    }

    public PatientIdentifier getPatientIdentifier( PatientIdentifierType identifierType, Patient patient )
    {
        return patientIdentifierStore.getPatientIdentifier( identifierType, patient );
    }

    public Collection<PatientIdentifier> getPatientIdentifiers( Patient patient )
    {
        return patientIdentifierStore.getPatientIdentifiers( patient );
    }

    public PatientIdentifier get( PatientIdentifierType type, String identifier )
    {
        return patientIdentifierStore.get( type, identifier );
    }

    public Collection<PatientIdentifier> getAll( PatientIdentifierType type, String identifier )
    {
        return patientIdentifierStore.getAll( type, identifier );
    }

    public Patient getPatient( PatientIdentifierType identifierType, String value )
    {
        return patientIdentifierStore.getPatient( identifierType, value );
    }

    public Collection<Patient> getPatientsByIdentifier( String identifier, Integer min, Integer max )
    {
        return patientIdentifierStore.getPatientsByIdentifier( identifier, min, max );
    }

    public int countGetPatientsByIdentifier( String identifier )
    {
        return patientIdentifierStore.countGetPatientsByIdentifier( identifier );
    }

    public Collection<PatientIdentifier> getPatientIdentifiers( Collection<PatientIdentifierType> identifierTypes,
        Patient patient )
    {
        return patientIdentifierStore.get( identifierTypes, patient );
    }
    
}
