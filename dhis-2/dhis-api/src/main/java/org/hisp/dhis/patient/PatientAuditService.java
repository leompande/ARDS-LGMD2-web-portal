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
import java.util.Date;

/**
 * @author Chau Thu Tran
 * 
 * @version PatientAuditService.java 9:01:49 AM Sep 26, 2012 $
 */
public interface PatientAuditService
{
    String ID = PatientAuditService.class.getName();

    /**
     * Adds an {@link PatientAudit}
     * 
     * @param patientAudit The to PatientAudit add.
     * 
     * @return A generated unique id of the added {@link PatientAudit}.
     */
    int savePatientAudit( PatientAudit patientAudit );

    /**
     * Deletes a {@link PatientAudit}.
     * 
     * @param patientAudit the PatientAudit to delete.
     */
    void deletePatientAudit( PatientAudit patientAudit );

    /**
     * Returns a {@link PatientAudit}.
     * 
     * @param id the id of the PatientAudit to return.
     * 
     * @return the PatientAudit with the given id
     */
    PatientAudit getPatientAudit( int id );

    /**
     * Returns all {@link PatientAudit}
     * 
     * @return a collection of all PatientAudit, or an empty collection if
     *         there are no PatientAttributeGroups.
     */
    Collection<PatientAudit> getAllPatientAudit();

    /**
     * Get all patient audits of a patient
     * 
     * @param patient Patient
     * 
     * @return List of PatientAudit
     */
    Collection<PatientAudit> getPatientAudits( Patient patient );

    /**
     * Get patient audit of a patient
     * 
     * @param patientId The id of patient
     * @param visitor The user who accessed to see a certain information of the
     *        patient
     * @param date The data this user visited
     * @param accessedModule The module this user accessed
     * 
     * @return PatientAudit
     */
    PatientAudit getPatientAudit( Integer patientId, String visitor, Date date, String accessedModule );

}
