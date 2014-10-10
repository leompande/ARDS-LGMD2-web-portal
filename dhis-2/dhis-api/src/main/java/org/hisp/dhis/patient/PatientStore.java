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

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.validation.ValidationCriteria;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public interface PatientStore
    extends GenericIdentifiableObjectStore<Patient>
{
    final String ID = PatientStore.class.getName();

    final int MAX_RESULTS = 50000;

    /**
     * Search patients who registered in a certain organisation unit
     * 
     * @param organisationUnit Organisation unit where patients registered
     * @param min
     * @param max
     * 
     * @return List of patients
     */
    Collection<Patient> getByOrgUnit( OrganisationUnit organisationUnit, Integer min, Integer max );

    /**
     * Search patients registered into a certain organisation unit and enrolled
     * into a program with active status
     * 
     * @param organisationUnit Organisation unit where patients registered
     * @param program Program. It's is used for getting identifier-types of this
     *        program and put identifiers of patients into the result
     * @param min
     * @param max
     * 
     * @return List of patients
     */
    Collection<Patient> getByOrgUnitProgram( OrganisationUnit organisationUnit, Program program, Integer min,
        Integer max );

    List<Patient> query( TrackedEntityQueryParams params );

    /**
     * Search patient who has the same representative
     * 
     * @param patient Representative
     * 
     * @return List of patients
     */
    Collection<Patient> getRepresentatives( Patient patient );

    /**
     * Search the number of patients who registered into an organisation unit
     * 
     * @param organisationUnit Organisation unit
     * 
     * @return The number of patients
     */
    int countListPatientByOrgunit( OrganisationUnit organisationUnit );

    /**
     * Get the number of patients by full name (performs partial search )
     * 
     * @param name A string for searching by full name
     * 
     * @return The number of patients
     */
    int countGetPatientsByName( String name );

    /**
     * Get the number of patients who registered into a certain organisation
     * unit and enrolled into a program with active status
     * 
     * @param organisationUnit Organisation unit where patients registered
     * @param program Program. It's is used for getting identifier-types of this
     *        program and put identifiers of patients into the result
     * 
     * @return The number of patients
     */
    int countGetPatientsByOrgUnitProgram( OrganisationUnit organisationUnit, Program program );

    /**
     * Get number of patients who meet the criteria for searching
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
    int countSearch( List<String> searchKeys, Collection<OrganisationUnit> orgunit, Boolean followup,
        Integer statusEnrollment );

    /**
     * Search patients by phone number (performs partial search)
     * 
     * @param phoneNumber The string for searching by phone number
     * @param min
     * @param max
     * 
     * @return List of patient
     */
    Collection<Patient> getByPhoneNumber( String phoneNumber, Integer min, Integer max );

    /**
     * Search events of patients who meet the criteria for searching
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
     * @return List of patients
     */
    Collection<Patient> search( List<String> searchKeys, Collection<OrganisationUnit> orgunit, Boolean followup,
        Collection<PatientAttribute> patientAttributes, Collection<PatientIdentifierType> identifierTypes,
        Integer statusEnrollment, Integer min, Integer max );

    /**
     * Search events which meet the criteria for searching
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
     * @return List of patients
     */
    List<Integer> getProgramStageInstances( List<String> searchKeys, Collection<OrganisationUnit> orgunits,
        Boolean followup, Collection<PatientAttribute> patientAttributes,
        Collection<PatientIdentifierType> identifierTypes, Integer statusEnrollment, Integer min, Integer max );

    /**
     * Search patients who enrolled into a program with active status
     * 
     * @param program Program
     * @param min
     * @param max
     * 
     *        return List of patients
     */
    Collection<Patient> getByProgram( Program program, Integer min, Integer max );

    /**
     * Search events of patients who meet the criteria for searching
     * 
     * @param grid Grid with headers
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
     * @return Grid
     */
    Grid getPatientEventReport( Grid grid, List<String> searchKeys, Collection<OrganisationUnit> orgunit,
        Boolean followup, Collection<PatientAttribute> patientAttributes,
        Collection<PatientIdentifierType> identifierTypes, Integer statusEnrollment, Integer min, Integer max );

    /**
     * Validate patient identifiers and validation criteria by program before
     * registering / updating information
     * 
     * @param patient Patient object
     * @param program Program which person needs to enroll. If this parameter is
     *        null, the system check identifiers of the patient
     * @param format I18nFormat
     * 
     * @return Error code 0 : Validation is OK 1 : The identifier is duplicated
     *         2 : Violate validation criteria of the program
     */
    int validate( Patient patient, Program program, I18nFormat format );

    /**
     * Validate patient enrollment
     * 
     * @param patient Patient object
     * @param program Program which person needs to enroll. If this parameter is
     *        null, the system check identifiers of the patient
     * @param format I18nFormat
     * 
     * @return Error code 0 : Validation is OK 1 : The identifier is duplicated
     *         2 : Violate validation criteria of the program
     */
    ValidationCriteria validateEnrollment( Patient patient, Program program, I18nFormat format );

    Collection<Patient> getByPatientAttributeValue( String searchText, int patientAttributeId, Integer min, Integer max );
}
