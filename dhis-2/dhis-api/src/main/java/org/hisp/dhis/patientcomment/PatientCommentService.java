package org.hisp.dhis.patientcomment;

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
 * @author Chau Thu Tran
 * 
 * @version CommentService.java 9:27:31 AM Aug 17, 2012 $
 */
public interface PatientCommentService
{
    String ID = PatientCommentService.class.getName();

    /**
     * Adds an {@link PatientComment}
     * 
     * @param patientComment The to PatientComment add.
     * 
     * @return A generated unique id of the added {@link PatientComment}.
     */
    int addPatientComment( PatientComment patientComment );

    /**
     * Deletes a {@link PatientComment}.
     * 
     * @param patientComment the PatientComment to delete.
     */
    void deletePatientComment( PatientComment patientComment );

    /**
     * Updates an {@link PatientComment}.
     * 
     * @param patientComment the PatientComment to update.
     */
    void updatePatientComment( PatientComment patientComment );

    /**
     * Returns a {@link PatientComment}.
     * 
     * @param id the id of the PatientComment to return.
     * 
     * @return the PatientComment with the given id
     */
    PatientComment getPatientComment( int id );

    /**
     * Returns all {@link PatientComment}
     * 
     * @return a collection of all PatientComment, or an empty collection if
     *         there are no PatientAttributes.
     */
    Collection<PatientComment> getAllPatientComments();

}
