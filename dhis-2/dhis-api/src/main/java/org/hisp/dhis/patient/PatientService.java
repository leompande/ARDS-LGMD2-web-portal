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
import java.util.List;
import java.util.Set;

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.validation.ValidationCriteria;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */

public interface PatientService
{
    String ID = PatientService.class.getName();

    public static final int ERROR_NONE = 0;

    public static final int ERROR_DUPLICATE_IDENTIFIER = 1;

    public static final int ERROR_ENROLLMENT = 2;

    /**
     * Adds an {@link Patient}
     * 
     * @param patient The to Patient add.
     * 
     * @return A generated unique id of the added {@link Patient}.
     */
    int savePatient( Patient patient );

    /**
     * Deletes a {@link Patient}.
     * 
     * @param patient the Patient to delete.
     */
    void deletePatient( Patient patient );

    /**
     * Updates a {@link Patient}.
     * 
     * @param patient the Patient to update.
     */
    void updatePatient( Patient patient );

    /**
     * Returns a {@link Patient}.
     * 
     * @param id the id of the PatientAttribute to return.
     * 
     * @return the PatientAttribute with the given id
     */
    Patient getPatient( int id );

    /**
     * Returns the {@link PatientAttribute} with the given UID.
     * 
     * @param uid the UID.
     * @return the PatientAttribute with the given UID, or null if no match.
     */
    Patient getPatient( String uid );

    /**
     * Returns all {@link Patient}
     * 
     * @return a collection of all Patient, or an empty collection if there are
     *         no Patients.
     */
    Collection<Patient> getAllPatients();

    /**
     * Retrieve patients for mobile base on identifier value
     * 
     * @param searchText value
     * @param orgUnitId
     * 
     * @return Patient List
     */
    Collection<Patient> getPatientsForMobile( String searchText, int orgUnitId );

    /**
     * Retrieve patients base on organization unit with result limited
     * 
     * @param organisationUnit organisationUnit
     * @param min
     * @param max
     * 
     * @return Patient List
     */
    Collection<Patient> getPatients( OrganisationUnit organisationUnit, Integer min, Integer max );

    /**
     * Retrieve patients who enrolled into a program with active status
     * 
     * @param program Program
     * @return Patient list
     */
    Collection<Patient> getPatients( Program program );

    /**
     * Retrieve patients registered in a orgunit and enrolled into a program
     * with active status
     * 
     * @param organisationUnit
     * @param program
     * @return
     */
    Collection<Patient> getPatients( OrganisationUnit organisationUnit, Program program );

    /**
     * Retrieve patients base on PatientIdentifierType or Attribute or Patient's
     * name
     * 
     * @param identifierTypeId
     * @param attributeId
     * @param value
     * @return
     */
    Collection<Patient> getPatient( Integer identifierTypeId, Integer attributeId, String value );

    /**
     * Search patients base on OrganisationUnit and Program with result limited
     * name
     * 
     * @param organisationUnit
     * @param program
     * @param min
     * @param max
     * @return
     */
    Collection<Patient> getPatients( OrganisationUnit organisationUnit, Program program, Integer min, Integer max );

    /**
     * Sort the result by PatientAttribute
     * 
     * @param patients
     * @param patientAttribute
     * @return Patient List
     */
    Collection<Patient> sortPatientsByAttribute( Collection<Patient> patients, PatientAttribute patientAttribute );

    /**
     * Get patients who has the same representative
     * 
     * @params patient The representatives
     * 
     * @return Patient List
     * **/
    Collection<Patient> getRepresentatives( Patient patient );
    
    /**
     * Register a new patient
     * 
     * @param patient Patient
     * @param representativeId The id of patient who is representative
     * @param relationshipTypeId The id of relationship type defined
     * @param attributeValues Set of attribute values
     * 
     * @return The error code after registering patient
     */
    int createPatient( Patient patient, Integer representativeId, Integer relationshipTypeId,
        Set<PatientAttributeValue> attributeValues );

    /**
     * Update information of an patient existed
     * 
     * @param patient Patient
     * @param representativeId The id of representative of this patient
     * @param relationshipTypeId The id of relationship type of this person
     * @param valuesForSave The patient attribute values for adding
     * @param valuesForUpdate The patient attribute values for updating
     * @param valuesForDelete The patient attribute values for deleting
     * 
     */
    void updatePatient( Patient patient, Integer representativeId, Integer relationshipTypeId,
        List<PatientAttributeValue> valuesForSave, List<PatientAttributeValue> valuesForUpdate,
        Collection<PatientAttributeValue> valuesForDelete );

    /**
     * Get the number of patients who registered into an organisation unit
     * 
     * @param organisationUnit Organisation Unit
     * 
     * @return The number of patients
     */
    int countGetPatientsByOrgUnit( OrganisationUnit organisationUnit );

    /**
     * Get the number of patients who registered into an organisation unit and
     * enrolled into a program
     * 
     * @param organisationUnit Organisation Unit
     * @param program Program
     * 
     * @return The number of patients
     */
    int countGetPatientsByOrgUnitProgram( OrganisationUnit organisationUnit, Program program );

    /**
     * Cache value from String to the value type based on property
     * 
     * @param property Property name of patient
     * @param value Value
     * @param format I18nFormat
     * 
     * @return An object
     */
    Object getObjectValue( String property, String value, I18nFormat format );

    /**
     * Search patients by attribute values, identifiers and/or a program which
     * patients enrolled into
     * 
     * @param searchKeys The key for searching patients by attribute values,
     *        identifiers and/or a program
     * @param orgunit Organisation unit where patients registered
     * @param followup Only getting patients with program risked if this
     *        property is true. And getting patients without program risked if
     *        its value is false
     * @param patientAttributes The attribute values of these attribute are
     *        displayed into result
     * @param identifierTypes The identifiers are displayed into the result
     * @param statusEnrollment The status of program of patients. There are
     *        three status, includes Active enrollments only, Completed
     *        enrollments only and Active and completed enrollments
     * @param min
     * @param max
     * 
     * @return An object
     */
    Collection<Patient> searchPatients( List<String> searchKeys, Collection<OrganisationUnit> orgunit,
        Boolean followup, Collection<PatientAttribute> patientAttributes,
        Collection<PatientIdentifierType> identifierTypes, Integer statusEnrollment, Integer min, Integer max );

    /**
     * Get the number of patients who meet the criteria for searching
     * 
     * @param searchKeys The key for searching patients by attribute values,
     *        identifiers and/or a program
     * @param orgunit Organisation unit where patients registered
     * @param followup Only getting patients with program risked if this
     *        property is true. And getting patients without program risked if
     *        its value is false
     * @param statusEnrollment The status of program of patients. There are
     *        three status, includes Active enrollments only, Completed
     *        enrollments only and Active and completed enrollments
     * 
     * @return The number of patients
     */
    int countSearchPatients( List<String> searchKeys, Collection<OrganisationUnit> orgunit, Boolean followup,
        Integer statusEnrollment );

    /**
     * Get phone numbers of persons who meet the criteria for searching *
     * 
     * @param searchKeys The key for searching patients by attribute values,
     *        identifiers and/or a program
     * @param orgunit Organisation unit where patients registered
     * @param followup Only getting patients with program risked if this
     *        property is true. And getting patients without program risked if
     *        its value is false
     * @param statusEnrollment The status of program of patients. There are
     *        three status, includes Active enrollments only, Completed
     *        enrollments only and Active and completed enrollments
     * @param min
     * @param max
     * 
     * @return List of patient
     */
    Collection<String> getPatientPhoneNumbers( List<String> searchKeys, Collection<OrganisationUnit> orgunit,
        Boolean followup, Integer statusEnrollment, Integer min, Integer max );

    /**
     * Get events which meet the criteria for searching
     * 
     * @param searchKeys The key for searching patients by attribute values,
     *        identifiers and/or a program
     * @param orgunit Organisation unit where patients registered
     * @param followup Only getting patients with program risked if this
     *        property is true. And getting patients without program risked if
     *        its value is false
     * @param statusEnrollment The status of program of patients. There are
     *        three status, includes Active enrollments only, Completed
     *        enrollments only and Active and completed enrollments
     * @parma min
     * @param max
     * 
     * @return List of patient
     */
    List<Integer> getProgramStageInstances( List<String> searchKeys, Collection<OrganisationUnit> orgunit,
        Boolean followup, Integer statusEnrollment, Integer min, Integer max );

    /**
     * Get visit schedule of person who meet the criteria for searching
     * 
     * @param searchKeys The key for searching patients by attribute values,
     *        identifiers and/or a program
     * @param orgunit Organisation unit where patients registered
     * @param followup Only getting patients with program risked if this
     *        property is true. And getting patients without program risked if
     *        its value is false
     * @param statusEnrollment The status of program of patients. There are
     *        three status, includes Active enrollments only, Completed
     *        enrollments only and Active and completed enrollments
     * @parma min
     * @param max
     * 
     * @return Grid
     */
    Grid getScheduledEventsReport( List<String> searchKeys, Collection<OrganisationUnit> orgunits, Boolean followup,
        Integer statusEnrollment, Integer min, Integer max, I18n i18n );

    /**
     * Search patients by phone number (performs partial search)
     * 
     * @param phoneNumber The string for searching by phone number
     * @param min
     * @param max
     * 
     * @return List of patient
     */
    Collection<Patient> getPatientsByPhone( String phoneNumber, Integer min, Integer max );

    /**
     * Get events of patients who meet the criteria for searching
     * 
     * @param program Program. It's is used for getting identifier-types of this
     *        program and put identifiers of patients into the result
     * @param searchKeys The key for searching patients by attribute values,
     *        identifiers and/or a program
     * @param orgunit Organisation unit where patients registered
     * @param followup Only getting patients with program risked if this
     *        property is true. And getting patients without program risked if
     *        its value is false
     * @param statusEnrollment The status of program of patients. There are
     *        three status, includes Active enrollments only, Completed
     *        enrollments only and Active and completed enrollments
     * @param i18n I18n
     * 
     * @return Grid
     */
    Grid getTrackingEventsReport( Program program, List<String> searchKeys, Collection<OrganisationUnit> orgunits,
        Boolean followup, Integer statusEnrollment, I18n i18n );

    /**
     * Validate patient identifiers and validation criteria by program before
     * registering or updating information
     * 
     * @param patient Patient object
     * @param program Program which person needs to enroll. If this parameter
     *        is null, the system check identifiers of the patient
     * @param format I18nFormat
     * 
     * @return Error code 0 : Validation is OK 1 : The identifier is duplicated
     *         2 : Violate validation criteria of the program
     */
    int validatePatient( Patient patient, Program program , I18nFormat format);
    
    /**
     * Validate patient enrollment
     * 
     * @param patient Patient object
     * @param program Program which person needs to enroll. If this parameter
     *        is null, the system check identifiers of the patient
     * @param format I18nFormat
     * 
     * @return Error code 0 : Validation is OK 1 : The identifier is duplicated
     *         2 : Violate validation criteria of the program
     */
    ValidationCriteria validateEnrollment( Patient patient, Program program, I18nFormat format );
    
    /**
     * Retrieve patients for mobile base on identifier value
     * 
     * @param searchText
     *            value
     * @param orgUnitId
     * @param patientAttributeId
     * @return Patient List
     */

    Collection<Patient> searchPatientsForMobile(String searchText,
                    int orgUnitId, int patientAttributeId);

    /**
     * Search patients by patient attribute value (performs partial search)
     * 
     * @param patient
     *            attribute value The string for searching by patient attribute
     *            value
     * @param min
     * @param max
     * 
     * @return List of patient
     */
    Collection<Patient> getPatientsByAttributeValue(String searchText, int patientAttributeId,
                    Integer min, Integer max);
}
