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
 * @version $PatientTabularReportService.java May 7, 2012 1:01:39 PM$
 */
public interface PatientTabularReportService
{
    /**
     * Adds/Updates an {@link PatientTabularReport}
     * 
     * @param patientTabularReport The to PatientTabularReport add.
     * 
     * @return A generated unique id of the added {@link PatientTabularReport} .
     */
    int saveOrUpdate( PatientTabularReport patientTabularReport );

    /**
     * Returns a {@link patientTabularReport}.
     * 
     * @param id the id of the patientTabularReport to return.
     * 
     * @return the patientTabularReport with the given id
     */
    PatientTabularReport getPatientTabularReport( int id );

    /**
     * Returns a {@link patientTabularReport} with a given name.
     * 
     * @param name the name of the patientTabularReport to return.
     * 
     * @return the patientTabularReport with the given name, or null if no
     *         match.
     */
    PatientTabularReport getPatientTabularReport( String name );

    /**
     * Returns the {@link patientTabularReport} with the given UID.
     * 
     * @param uid the UID.
     * @return the patientTabularReport with the given UID, or null if no match.
     */
    PatientTabularReport getPatientTabularReportByUid( String uid );

    /**
     * Deletes a {@link patientTabularReport}.
     * 
     * @param patientTabularReport the patientTabularReport to delete.
     */
    void deletePatientTabularReport( PatientTabularReport patientTabularReport );

    /**
     * Returns all {@link patientTabularReport}
     * 
     * @return a collection of all patientTabularReport, or an empty collection
     *         if there are no PatientAttributes.
     */
    Collection<PatientTabularReport> getAllTabularReports();

    /**
     * Retrieve case-based report favorites by name (performs partial search)
     * which a user can read with result limited
     * 
     * @param user User
     * @param query A string for searching by name
     * @param min
     * @param max
     * 
     * @return PatientAggregateReport list
     */
    Collection<PatientTabularReport> getPatientTabularReports( User user, String query, Integer min, Integer max );

    /**
     * Get the number of case-based report favorites which a user can read and
     * have the names meet the search string
     * 
     * @param user User
     * @param query A string for searching by name
     * 
     * @return A number
     */
    int countPatientTabularReportList( User user, String query );

}
