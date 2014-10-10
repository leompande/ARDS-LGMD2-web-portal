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
public interface PatientAttributeService
{
    String ID = PatientAttributeService.class.getName();

    /**
     * Adds an {@link PatientAttribute}
     * 
     * @param patientAttribute The to PatientAttribute add.
     * 
     * @return A generated unique id of the added {@link PatientAttribute}.
     */
    int savePatientAttribute( PatientAttribute patientAttribute );

    /**
     * Deletes a {@link PatientAttribute}.
     * 
     * @param patientAttribute the PatientAttribute to delete.
     */
    void deletePatientAttribute( PatientAttribute patientAttribute );

    /**
     * Updates an {@link PatientAttribute}.
     * 
     * @param patientAttribute the PatientAttribute to update.
     */
    void updatePatientAttribute( PatientAttribute patientAttribute );

    /**
     * Returns a {@link PatientAttribute}.
     * 
     * @param id the id of the PatientAttribute to return.
     * 
     * @return the PatientAttribute with the given id
     */
    PatientAttribute getPatientAttribute( int id );

    /**
     * Returns the {@link PatientAttribute} with the given UID.
     * 
     * @param uid the UID.
     * @return the PatientAttribute with the given UID, or null if no match.
     */
    PatientAttribute getPatientAttribute( String uid );

    /**
     * Returns a {@link PatientAttribute} with a given name.
     * 
     * @param name the name of the PatientAttribute to return.
     * 
     * @return the PatientAttribute with the given name, or null if no match.
     */
    PatientAttribute getPatientAttributeByName( String name );

    /**
     * Returns all {@link PatientAttribute}
     * 
     * @return a collection of all PatientAttribute, or an empty collection if
     *         there are no PatientAttributes.
     */
    Collection<PatientAttribute> getAllPatientAttributes();

    /**
     * Get patient attributes by value type
     * 
     * @param valueType Value type
     * 
     * @return List of patient attributes
     */
    Collection<PatientAttribute> getPatientAttributesByValueType( String valueType );

    /**
     * Get mandatory patient attributes without groups
     * 
     * @return List of patient attributes
     */
    Collection<PatientAttribute> getOptionalPatientAttributesWithoutGroup();

    /**
     * Get patient attributes by mandatory option
     * 
     * @param mandatory True/False value
     */
    Collection<PatientAttribute> getPatientAttributesByMandatory( boolean mandatory );

    /**
     * Get patient attributes by groupBy option
     * 
     * @return PatientAttribute with groupby as true
     */
    PatientAttribute getPatientAttributeByGroupBy();

    /**
     * Get patient attributes without groups
     * 
     * @return List of patient attributes without group
     */
    Collection<PatientAttribute> getPatientAttributesWithoutGroup();

    /**
     * Get patient attributes which are displayed in visit schedule
     * 
     * @param displayOnVisitSchedule True/False value
     * 
     * @return List of patient attributes
     */
    Collection<PatientAttribute> getPatientAttributesByDisplayOnVisitSchedule( boolean displayOnVisitSchedule );

    /**
     * Get patient attributes which are displayed in visit schedule
     * 
     * @param displayInListNoProgram True/False value
     * 
     * @return List of patient attributes
     */
    Collection<PatientAttribute> getPatientAttributesWithoutProgram();

    /**
     * Get patient attributes which are displayed in visit schedule
     * 
     * @param displayInListNoProgram True/False value
     * 
     * @return List of patient attributes
     */
    Collection<PatientAttribute> getPatientAttributesDisplayed( boolean displayInListNoProgram );

}
