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
import java.util.regex.Pattern;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.user.User;

/**
 * @author Chau Thu Tran
 * 
 * @version PatientRegistrationFormService.java 9:35:44 AM Jan 31, 2013 $
 */
public interface PatientRegistrationFormService
{
    final Pattern INPUT_PATTERN = Pattern.compile( "(<input.*?/>)", Pattern.DOTALL );

    final Pattern FIXED_ATTRIBUTE_PATTERN = Pattern.compile( "fixedattributeid=\"(\\w+)\"" );

    final Pattern IDENTIFIER_PATTERN = Pattern.compile( "identifierid=\"(\\w+)\"" );

    final Pattern DYNAMIC_ATTRIBUTE_PATTERN = Pattern.compile( "attributeid=\"(\\w+)\"" );

    final Pattern PROGRAM_PATTERN = Pattern.compile( "programid=\"(\\w+)\"" );

    final Pattern VALUE_TAG_PATTERN = Pattern.compile( "value=\"(.*?)\"", Pattern.DOTALL );

    final Pattern TITLE_TAG_PATTERN = Pattern.compile( "title=\"(.*?)\"", Pattern.DOTALL );

    final Pattern SUGGESTED_VALUE_PATTERN = Pattern.compile( "suggested=('|\")(\\w*)('|\")" );

    final Pattern CLASS_PATTERN = Pattern.compile( "class=('|\")(\\w*)('|\")" );

    final Pattern STYLE_PATTERN = Pattern.compile( "style=('|\")([\\w|\\d\\:\\;]+)('|\")" );

    // --------------------------------------------------------------------------
    // ProgramDataEntryService
    // --------------------------------------------------------------------------

    /**
     * Adds an {@link PatientRegistrationForm}
     * 
     * @param registrationForm The to PatientRegistrationForm add.
     * 
     * @return A generated unique id of the added {@link PatientRegistrationForm}.
     */
    int savePatientRegistrationForm( PatientRegistrationForm registrationForm );

    /**
     * Deletes a {@link PatientRegistrationForm}.
     * 
     * @param registrationForm the PatientRegistrationForm to delete.
     */
    void deletePatientRegistrationForm( PatientRegistrationForm registrationForm );

    /**
     * Updates an {@link PatientRegistrationForm}.
     * 
     * @param patientAttribute the PatientRegistrationForm to update.
     */
    void updatePatientRegistrationForm( PatientRegistrationForm registrationForm );

    /**
     * Returns a {@link PatientRegistrationForm}.
     * 
     * @param id the id of the PatientRegistrationForm to return.
     * 
     * @return the PatientRegistrationForm with the given id
     */
    PatientRegistrationForm getPatientRegistrationForm( int id );

    /**
     * Returns all {@link PatientRegistrationForm}
     * 
     * @return a collection of all PatientRegistrationForm, or an empty collection if
     *         there are no PatientRegistrationForms.
     */
    Collection<PatientRegistrationForm> getAllPatientRegistrationForms();

    /**
     * Get custom registration form of a program
     * 
     * @param program Program
     * 
     * @return PatientRegistrationForm
     */
    PatientRegistrationForm getPatientRegistrationForm( Program program );

    /**
     * Get custom registration form which doesn't belong to any program
     * 
     * @return PatientRegistrationForm
     */
    PatientRegistrationForm getCommonPatientRegistrationForm();

    /**
     * Prepares the custom registration by injecting required javascripts and
     * drop down lists.
     * 
     * @param htmlCode the HTML code of the data entry form.
     * @param program Program which include a custom entry form
     * @param healthWorkers DHIS users list
     * @param patient Patient
     * @param programInstance Program-instance of the patient
     * @param i18n the i18n object
     * @param format the I18nFormat object
     * 
     * @return HTML code for the form.
     */
    String prepareDataEntryFormForAdd( String htmlCode, Program program, Collection<User> healthWorkers,
        Patient patient, ProgramInstance programInstance, I18n i18n, I18nFormat format );

}
