package org.hisp.dhis.program;

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

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.validation.ValidationCriteria;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
public interface ProgramService
{
    String ID = ProgramService.class.getName();

    /**
     * Adds an {@link Program}
     * 
     * @param program The to Program add.
     * 
     * @return A generated unique id of the added {@link Program}.
     */
    int addProgram( Program program );

    /**
     * Updates an {@link Program}.
     * 
     * @param program the Program to update.
     */
    void updateProgram( Program program );

    /**
     * Deletes a {@link Program}. All {@link ProgramStage},
     * {@link ProgramInstance} and {@link ProgramStageInstance} belong to this
     * program are removed
     * 
     * @param program the Program to delete.
     */
    void deleteProgram( Program program );

    /**
     * Returns a {@link Program}.
     * 
     * @param id the id of the Program to return.
     * 
     * @return the Program with the given id
     */
    Program getProgram( int id );

    /**
     * Returns a {@link Program} with a given name.
     * 
     * @param name the name of the Program to return.
     * @return the Program with the given name, or null if no match.
     */
    Program getProgramByName( String name );

    /**
     * Returns all {@link Program}.
     * 
     * @return a collection of all Program, or an empty collection if there are
     *         no Programs.
     */
    Collection<Program> getAllPrograms();

    /**
     * Get all {@link Program} belong to a orgunit
     * 
     * @param organisationUnit {@link OrganisationUnit}
     * 
     * @return The program list
     */
    Collection<Program> getPrograms( OrganisationUnit organisationUnit );

    /**
     * Get {@link Program} by the current user.
     * 
     * @return The program list the current user
     */
    Collection<Program> getProgramsByCurrentUser();

    /**
     * Get {@link Program} by the current user and a certain type
     * 
     * @param type The type of program. There are three types, include Multi
     *        events with registration, Single event with registration and
     *        Single event without registration.
     * 
     * @return Program list by a type specified
     */
    Collection<Program> getProgramsByCurrentUser( int type );

    /**
     * Get {@link Program} included in the expression of a
     * {@link ValidationCriteria}
     * 
     * @param validationCriteria {@link ValidationCriteria}
     * 
     * @return Program list
     */
    Collection<Program> getPrograms( ValidationCriteria validationCriteria );

    /**
     * Get {@link Program} by a type
     * 
     * @param type The type of program. There are three types, include Multi
     *        events with registration, Single event with registration and
     *        Single event without registration
     * 
     * @return Program list by a type specified
     */
    Collection<Program> getPrograms( int type );

    /**
     * Get {@link Program} assigned to an {@link OrganisationUnit} by a type
     * 
     * @param type The type of program. There are three types, include Multi
     *        events with registration, Single event with registration and
     *        Single event without registration
     * @param orgunit Where programs assigned
     * 
     * @return Program list by a type specified
     */
    Collection<Program> getPrograms( int type, OrganisationUnit orgunit );

    /**
     * Returns the {@link Program} with the given UID.
     * 
     * @param uid the UID.
     * @return the Program with the given UID, or null if no match.
     */
    Program getProgram( String uid );

    /**
     * Get {@link Program} which are displayed on all {@link OrganisationUnit}
     * for searching or enrolling a person
     * 
     * @param displayOnAllOrgunit Optional flag to specify programs can
     *        displayed for searching or enrolling (<code>true</code> ) or
     *        cannot be used on the orgunit (<code>false</code>) instances.
     * @param orgunit {@link OrganisationUnit}
     * 
     * @return Program list
     */
    Collection<Program> getProgramsByDisplayOnAllOrgunit( boolean displayOnAllOrgunit, OrganisationUnit orgunit );

    /**
     * Get {@link Program} belong to an orgunit by the current user
     * 
     * @param organisationUnit {@link OrganisationUnit}
     */
    Collection<Program> getProgramsByCurrentUser( OrganisationUnit organisationUnit );
}
