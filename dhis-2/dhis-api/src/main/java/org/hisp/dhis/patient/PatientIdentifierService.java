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

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public interface PatientIdentifierService
{
    String ID = PatientIdentifierService.class.getName();

    /**
     * Adds an {@link PatientIdentifier}
     * 
     * @param patientIdentifier The to PatientIdentifier add.
     * 
     * @return A generated unique id of the added {@link PatientIdentifier}.
     */
    int savePatientIdentifier( PatientIdentifier patientIdentifier );

    /**
     * Deletes a {@link PatientIdentifier}.
     * 
     * @param patientIdentifier the PatientIdentifier to delete.
     */
    void deletePatientIdentifier( PatientIdentifier patientIdentifier );

    /**
     * Deletes a {@link PatientIdentifier}.
     * 
     * @param patientIdentifier the PatientIdentifier to delete.
     */
    void updatePatientIdentifier( PatientIdentifier patientIdentifier );

    /**
     * Returns a {@link PatientIdentifier}.
     * 
     * @param id the id of the PatientIdentifier to return.
     * 
     * @return the PatientIdentifier with the given id
     */
    PatientIdentifier getPatientIdentifier( int id );

    /**
     * Returns all {@link PatientIdentifier}
     * 
     * @return a collection of all PatientIdentifier, or an empty collection if
     *         there are no PatientAttributeGroups.
     */
    Collection<PatientIdentifier> getAllPatientIdentifiers();

    /**
     * Retrieve patient identifiers of patient
     * 
     * @param patient Patient
     * 
     * @return PatientIdentifier list
     */
    PatientIdentifier getPatientIdentifier( Patient patient );

    /**
     * Retrieve patient identifiers by type
     * 
     * @param identifierType PatientIdentifierType
     * 
     * @return PatientIdentifier list
     */
    Collection<PatientIdentifier> getPatientIdentifiersByType( PatientIdentifierType identifierType );

    /**
     * Search patient identifiers by identifier (performs partial search )
     * 
     * @param identifier A string for searching by identifier
     * 
     * @return PatientIdentifier list
     */
    Collection<PatientIdentifier> getPatientIdentifiersByIdentifier( String identifier );

    /**
     * Search a patient identifier of a patient by identifier
     * 
     * @param identifier An identifier string for searching
     * @param patient Patient
     * 
     * @return PatientIdentifier
     */
    PatientIdentifier getPatientIdentifier( String identifier, Patient patient );

    /**
     * Retrieve PatientIdentifier of a patient by identifier type
     * 
     * @param identifierType PatientIdentifierType
     * @param patient Patient
     * 
     * @return PatientIdentifier
     */
    PatientIdentifier getPatientIdentifier( PatientIdentifierType identifierType, Patient patient );

    /**
     * Retrieve patient identifiers of a patient
     * 
     * @param patient Patient
     * 
     * @return PatientIdentifier list
     */
    Collection<PatientIdentifier> getPatientIdentifiers( Patient patient );

    /**
     * Retrieve first patient based on type and identifier
     * 
     * @param type PatientIdentifierType
     * @param identifier Identifier of patient
     * 
     * @return PatientIdentifier
     */
    PatientIdentifier get( PatientIdentifierType type, String identifier );

    /**
     * Retrieve patients based on type and identifier. We need this since we
     * have allowed identifiers with duplicate values in the past. This returns
     * a list instead. We need this since we have allowed identifiers with
     * duplicate values in the past. This returns a list instead.
     * 
     * @param type PatientIdentifierType
     * @param identifier Identifier of patient
     * 
     * @return PatientIdentifier list
     */
    Collection<PatientIdentifier> getAll( PatientIdentifierType type, String identifier );

    /**
     * Retrieve a patient based on identifier type and a identifier value
     * 
     * @param identifierType PatientIdentifierType
     * @param value Identifier value
     */
    Patient getPatient( PatientIdentifierType identifierType, String identifier );

    /**
     * Retrieve patients based on identifier value with limit result (performs
     * partial search )
     * 
     * @param identifier identifier value
     * @param min
     * @param max
     * 
     * @result Patient list
     */
    Collection<Patient> getPatientsByIdentifier( String identifier, Integer min, Integer max );

    /**
     * Get the number of patient who has a identifier value (performs partial
     * search )
     * 
     * @param identifier Identifier value
     * 
     * @return The number of patients
     */
    int countGetPatientsByIdentifier( String identifier );

    /**
     * Retrieve patient identifiers based on identifier types
     * 
     * @param identifierTypes PatientIdentifierType collection
     * @param patient Patient
     * 
     * @return PatientIdentifier list
     */
    Collection<PatientIdentifier> getPatientIdentifiers( Collection<PatientIdentifierType> identifierTypes,
        Patient patient );
}
