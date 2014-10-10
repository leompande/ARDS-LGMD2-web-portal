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

/**
 * @author Chau Thu Tran
 * @version $Id$
 */
public interface PatientAttributeGroupService
{
    String ID = PatientAttributeGroupService.class.getName();

    /**
     * Adds an {@link PatientAttributeGroup}
     * 
     * @param patientAttributeGroup The to PatientAttributeGroup add.
     * 
     * @return A generated unique id of the added {@link PatientAttributeGroup}.
     */
    int savePatientAttributeGroup( PatientAttributeGroup patientAttributeGroup );

    /**
     * Deletes a {@link PatientAttributeGroup}.
     * 
     * @param patientAttributeGroup the PatientAttributeGroup to delete.
     */
    void deletePatientAttributeGroup( PatientAttributeGroup patientAttributeGroup );

    /**
     * Updates a {@link PatientAttributeGroup}.
     * 
     * @param patientAttributeGroup the PatientAttributeGroup to update.
     */
    void updatePatientAttributeGroup( PatientAttributeGroup patientAttributeGroup );

    /**
     * Returns a {@link PatientAttributeGroup}.
     * 
     * @param id the id of the PatientAttributeGroup to return.
     * 
     * @return the PatientAttributeGroup with the given id
     */
    PatientAttributeGroup getPatientAttributeGroup( int id );

    /**
     * Returns a {@link PatientAttributeGroup} with a given name.
     * 
     * @param name the name of the PatientAttributeGroup to return.
     * 
     * @return the PatientAttributeGroup with the given name, or null if no match.
     */
    PatientAttributeGroup getPatientAttributeGroupByName( String name );

    /**
     * Returns all {@link PatientAttributeGroup}
     * 
     * @return a collection of all PatientAttributeGroup, or an empty collection if there are
     *         no PatientAttributeGroups.
     */
    Collection<PatientAttributeGroup> getAllPatientAttributeGroups();

    /**
     * Get {@link PatientAttribute} by a {@link PatientAttributeGroup}
     * 
     * @param patientAttributeGroup {@link PatientAttributeGroup}
     * 
     * @return PatientAttribute list
     */
    List<PatientAttribute> getPatientAttributes( PatientAttributeGroup patientAttributeGroup );
}
