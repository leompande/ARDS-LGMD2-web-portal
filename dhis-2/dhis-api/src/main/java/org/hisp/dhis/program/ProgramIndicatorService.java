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
import java.util.Map;

/**
 * @author Chau Thu Tran
 * @version $ ProgramIndicatorService.java Apr 16, 2013 1:11:07 PM $
 */
public interface ProgramIndicatorService
{
    /**
     * Adds an {@link ProgramIndicator}
     * 
     * @param programIndicator The to ProgramIndicator add.
     * 
     * @return A generated unique id of the added {@link ProgramIndicator}.
     */
    int addProgramIndicator( ProgramIndicator programIndicator );

    /**
     * Updates an {@link ProgramIndicator}.
     * 
     * @param programIndicator the ProgramIndicator to update.
     */
    void updateProgramIndicator( ProgramIndicator programIndicator );

    /**
     * Deletes a {@link ProgramIndicator}.
     * 
     * @param programIndicator the ProgramIndicator to delete.
     */
    void deleteProgramIndicator( ProgramIndicator programIndicator );

    /**
     * Returns a {@link ProgramIndicator}.
     * 
     * @param id the id of the ProgramIndicator to return.
     * 
     * @return the ProgramIndicator with the given id
     */
    ProgramIndicator getProgramIndicator( int id );

    /**
     * Returns a {@link ProgramIndicator} with a given name.
     * 
     * @param name the name of the ProgramIndicator to return.
     * @return the ProgramIndicator with the given name, or null if no match.
     */
    ProgramIndicator getProgramIndicator( String name );

    /**
     * Returns a {@link ProgramIndicator} with a given short name.
     * 
     * @param name the name of the ProgramIndicator to return.
     * @return the ProgramIndicator with the given short name, or null if no
     *         match.
     */
    ProgramIndicator getProgramIndicatorByShortName( String shortName );

    /**
     * Returns the {@link ProgramIndicator} with the given UID.
     * 
     * @param uid the UID.
     * @return the ProgramIndicator with the given UID, or null if no match.
     */
    ProgramIndicator getProgramIndicatorByUid( String uid );

    /**
     * Returns all {@link ProgramIndicator}.
     * 
     * @return a collection of all ProgramIndicator, or an empty collection if
     *         there are no ProgramIndicators.
     */
    Collection<ProgramIndicator> getAllProgramIndicators();

    /**
     * Get {@link ProgramIndicator} of a program
     * 
     * @param program Program
     * 
     * @return ProgramIndicators belong to the program
     */
    Collection<ProgramIndicator> getProgramIndicators( Program program );

    /**
     * Calculate an program indicator value based on program instance and an
     * indicator defined for a patient
     * 
     * @param programInstance ProgramInstance
     * @param programIndicator ProgramIndicator
     * 
     * @return Indicator value
     */
    String getProgramIndicatorValue( ProgramInstance programInstance, ProgramIndicator programIndicator );

    /**
     * Get indicator values of all program indicators defined for a patient
     * 
     * @param programInstance ProgramInstance
     * 
     * @return Map<Indicator name, Indicator value>
     */
    Map<String, String> getProgramIndicatorValues( ProgramInstance programInstance );

    /**
     * Get description of an indicator expression
     * 
     * @param expression A expression string
     * 
     * @return The description
     */
    String getExpressionDescription( String expression );

}
