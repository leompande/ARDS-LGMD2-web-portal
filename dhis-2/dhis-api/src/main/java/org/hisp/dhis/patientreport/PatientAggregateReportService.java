package org.hisp.dhis.patientreport;

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

import org.hisp.dhis.user.User;

/**
 * @author Chau Thu Tran
 * 
 * @version PatientAggregateReportService.java 11:35:43 AM Jan 10, 2013 $
 */
public interface PatientAggregateReportService
{
    /**
     * Adds an {@link PatientAggregateReport}
     * 
     * @param patientAggregateReport The to PatientAggregateReport add.
     * 
     * @return A generated unique id of the added {@link PatientAggregateReport}
     *         .
     */
    int addPatientAggregateReport( PatientAggregateReport patientAggregateReport );

    /**
     * Updates an {@link PatientAggregateReport}.
     * 
     * @param patientAggregateReport the PatientAggregateReport to update.
     */
    void updatePatientAggregateReport( PatientAggregateReport patientAggregateReport );

    /**
     * Returns a {@link patientAggregateReport}.
     * 
     * @param id the id of the patientAggregateReport to return.
     * 
     * @return the patientAggregateReport with the given id
     */
    PatientAggregateReport getPatientAggregateReport( int id );

    /**
     * Returns the {@link patientAggregateReport} with the given UID.
     * 
     * @param uid the UID.
     * @return the patientAggregateReport with the given UID, or null if no
     *         match.
     */
    PatientAggregateReport getPatientAggregateReportByUid( String uid );

    /**
     * Returns a {@link patientAggregateReport} with a given name.
     * 
     * @param name the name of the patientAggregateReport to return.
     * 
     * @return the patientAggregateReport with the given name, or null if no
     *         match.
     */
    PatientAggregateReport getPatientAggregateReport( String name );

    /**
     * Deletes a {@link patientAggregateReport}.
     * 
     * @param patientAggregateReport the patientAggregateReport to delete.
     */
    void deletePatientAggregateReport( PatientAggregateReport patientAggregateReport );

    /**
     * Returns all {@link patientAggregateReport}
     * 
     * @return a collection of all patientAggregateReport, or an empty
     *         collection if there are no PatientAttributes.
     */
    Collection<PatientAggregateReport> getAllPatientAggregateReports();

    /**
     * Retrieve aggregate report favorites by name (performs partial search)
     * which a user can read with result limited
     * 
     * @param user User
     * @param query A string for searching by name
     * @param min
     * @param max
     * 
     * @return PatientAggregateReport list
     */
    Collection<PatientAggregateReport> getPatientAggregateReports( User user, String query, Integer min, Integer max );

    /**
     * Get the number of aggregate report favorites which a user can read and
     * have the names meet the search string
     * 
     * @param user User
     * @param query A string for searching by name
     * 
     * @return A number
     */
    int countPatientAggregateReportList( User user, String query );

}
