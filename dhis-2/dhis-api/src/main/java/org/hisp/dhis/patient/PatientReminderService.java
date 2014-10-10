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

import java.util.Set;

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.user.User;

/**
 * @author Chau Thu Tran
 * 
 * @version $ PatientReminderService.java Aug 7, 2013 9:53:19 AM $
 */
public interface PatientReminderService
{
    /**
     * Returns a {@link PatientReminder}.
     * 
     * @param id the id of the PatientReminder to return.
     * 
     * @return the PatientReminder with the given id
     */
    PatientReminder getPatientReminder( int id );

    /**
     * Returns a {@link PatientReminder} with a given name.
     * 
     * @param name the name of the PatientReminder to return.
     * 
     * @return the PatientReminder with the given name, or null if no match.
     */
    PatientReminder getPatientReminderByName( String name );

    /**
     * Get message for sending to a patient from program-instance template
     * defined
     * 
     * @param patientReminder PatientReminder object
     * @param programInstance ProgramInstance
     * @param format I18nFormat object
     * 
     * @return A message for an program instance.
     */
    String getMessageFromTemplate( PatientReminder patientReminder, ProgramInstance programInstance, I18nFormat format );

    /**
     * Get message for sending to a patient from program-stage-instance template
     * defined
     * 
     * @param patientReminder PatientReminder object
     * @param programStageInstance ProgramStageInstance
     * @param format I18nFormat object
     * 
     * @return A message for an program instance.
     */
    String getMessageFromTemplate( PatientReminder patientReminder, ProgramStageInstance programStageInstance,
        I18nFormat format );

    /**
     * Retrieve the phone numbers for sending sms based on a template defined
     * 
     * @param patientReminder PatientReminder
     * @param patient Patient
     * 
     * @return The list of the phone numbers ( patient attribute phone numbers,
     *         orgunit phone numbers, phone numbers of DHIS users at the orgunit
     *         OR phone numbers of DHIS users in a user group.
     */
    Set<String> getPhonenumbers( PatientReminder patientReminder, Patient patient );

    /**
     * Retrieve DHIS users from a template which is defined to send messages to
     * DHIS users
     * 
     * @param patientReminder PatientReminder
     * @param patient Patient
     * 
     * @return The list of DHIS users
     */
    Set<User> getUsers( PatientReminder patientReminder, Patient patient );
}
