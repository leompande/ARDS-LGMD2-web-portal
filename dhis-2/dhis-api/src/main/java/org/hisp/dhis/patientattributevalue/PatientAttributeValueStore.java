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

import java.util.Collection;

import org.hisp.dhis.common.GenericStore;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeOption;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
public interface PatientAttributeValueStore
    extends GenericStore<PatientAttributeValue>
{
    String ID = PatientAttributeValueStore.class.getName();

    /**
     * Adds an {@link PatientAttribute}
     * 
     * @param patientAttribute The to PatientAttribute add.
     * 
     */
    void saveVoid( PatientAttributeValue patientAttributeValue );

    /**
     * Deletes all {@link PatientAttributeValue} of a patient
     * 
     * @param patient {@link Patient}
     * 
     * @return The error code. If the code is 0, deleting success
     */
    int deleteByPatient( Patient patient );

    /**
     * Deletes a {@link PatientAttribute}.
     * 
     * @param patientAttribute the PatientAttribute to delete.
     */
    int deleteByAttribute( PatientAttribute patientAttribute );

    /**
     * Retrieve a {@link PatientAttributeValue} on a {@link Patient} and
     * {@link PatientAttribute}
     * 
     * @param patientAttribute {@link PatientAttribute}
     * 
     * @return PatientAttributeValue
     */
    PatientAttributeValue get( Patient patient, PatientAttribute patientAttribute );

    /**
     * Retrieve {@link PatientAttributeValue} of a {@link Patient}
     * 
     * @param patient Patient
     * 
     * @return PatientAttributeValue list
     */
    Collection<PatientAttributeValue> get( Patient patient );

    /**
     * Retrieve {@link PatientAttributeValue} of a {@link Patient}
     * 
     * @param patient Patient
     * 
     * @return PatientAttributeValue list
     */
    Collection<PatientAttributeValue> get( PatientAttribute patientAttribute );

    /**
     * Retrieve {@link PatientAttributeValue} of a patient list
     * 
     * @param patients Patient list
     * 
     * @return PatientAttributeValue list
     */
    Collection<PatientAttributeValue> get( Collection<Patient> patients );

    /**
     * Search PatientAttributeValue objects by a PatientAttribute and a attribute
     * value (performs partial search )
     * 
     * @param patientAttribute PatientAttribute
     * @param searchText A string for searching by attribute values
     * 
     * @return PatientAttributeValue list
     */
    Collection<PatientAttributeValue> searchByValue( PatientAttribute patientAttribute, String searchText );

    /**
     * Get the number of {@link PatientAttributeOption} in all
     * {@link PatientAttribute}
     * 
     * @param attributeOption PatientAttributeOption
     * 
     * @return The number of PatientAttributeOptions
     */
    int countByPatientAttributeOption( PatientAttributeOption attributeOption );

    /**
     * Retrieve patients who have the same value on an attribute
     * 
     * @param attribute PatientAttribute
     * @param value An attribute value for searching
     * 
     * @return Patient list
     */
    Collection<Patient> getPatient( PatientAttribute attribute, String value );

    /**
     * Update patient attribute values which belong to the pre-defined attribute
     * when a value pre-defined of this attribute is modified
     * 
     * @param patientAttributeOption PatientAttributeOption
     */
    void updatePatientAttributeValues( PatientAttributeOption patientAttributeOption );

}
