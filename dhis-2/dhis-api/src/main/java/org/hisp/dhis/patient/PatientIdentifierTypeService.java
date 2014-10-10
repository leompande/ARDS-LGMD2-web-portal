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
 * @author Abyot Asalefew
 * @version $Id$
 */
public interface PatientIdentifierTypeService
{
    String ID = PatientIdentifierTypeService.class.getName();

    /**
     * Adds an {@link PatientIdentifierType}
     * 
     * @param patientIdentifierType The to PatientIdentifierType add.
     * 
     * @return A generated unique id of the added {@link PatientIdentifierType}.
     */
    int savePatientIdentifierType( PatientIdentifierType patientIdentifierType );

    /**
     * Deletes a {@link PatientIdentifierType}.
     * 
     * @param patientIdentifierType the PatientIdentifierType to delete.
     */
    void deletePatientIdentifierType( PatientIdentifierType patientIdentifierType );

    /**
     * Deletes a {@link PatientIdentifierType}.
     * 
     * @param patientIdentifierType the PatientIdentifierType to delete.
     */
    void updatePatientIdentifierType( PatientIdentifierType patientIdentifierType );

    /**
     * Returns a {@link PatientIdentifierType}.
     * 
     * @param id the id of the PatientIdentifierType to return.
     * 
     * @return the PatientIdentifierType with the given id
     */
    PatientIdentifierType getPatientIdentifierType( int id );

    /**
     * Returns all {@link PatientIdentifierType}
     * 
     * @return a collection of all PatientIdentifierType, or an empty collection
     *         if there are no PatientAttributeGroups.
     */
    Collection<PatientIdentifierType> getAllPatientIdentifierTypes();

    /**
     * Returns a {@link PatientIdentifierType} with a given name.
     * 
     * @param name the name of the PatientIdentifierType to return.
     * 
     * @return the PatientIdentifierType with the given name, or null if no
     *         match.
     */
    PatientIdentifierType getPatientIdentifierType( String name );

    /**
     * Returns the {@link PatientIdentifierType} with the given UID.
     * 
     * @param uid the UID.
     * @return the PatientIdentifierType with the given UID, or null if no
     *         match.
     */
    PatientIdentifierType getPatientIdentifierTypeByUid( String uid );

    /**
     * Retrieve patient identifier types based on mandatory option
     * 
     * @param mandatory True/False value
     * 
     * @return PatientIdentifierType list
     */
    Collection<PatientIdentifierType> getPatientIdentifierTypes( boolean mandatory );

    /**
     * Get patient identifier type which are displayed in list
     * 
     * @param displayInListNoProgram True/False value
     * 
     * @return List of patient attributes
     */
    Collection<PatientIdentifierType> getPatientIdentifierTypeDisplayed( boolean displayInListNoProgram );
}
