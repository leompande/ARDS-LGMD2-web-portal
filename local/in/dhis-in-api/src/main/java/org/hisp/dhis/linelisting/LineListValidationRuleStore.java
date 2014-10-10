package org.hisp.dhis.linelisting;

/*
 * Copyright (c) 2004-2007, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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

public interface LineListValidationRuleStore
{
    String ID = LineListValidationRuleStore.class.getName();

    // -------------------------------------------------------------------------
    // LineListValidationRule
    // -------------------------------------------------------------------------

    /**
     * Adds a LineListValidationRule to the database.
     * 
     * @param LineListValidationRule the LineListValidationRule to add.
     * @return the generated unique identifier for the LineListValidationRule.
     */
    int addLineListValidationRule( LineListValidationRule LineListValidationRule );

    /**
     * Delete a LineListValidationRule with the given identifiers from the database.
     * 
     * @param id the LineListValidationRule to delete.
     */
    void deleteLineListValidationRule( LineListValidationRule LineListValidationRule );

    /**
     * Update a LineListValidationRule with the given identifiers.
     * 
     * @param LineListValidationRule the LineListValidationRule to update.
     */
    void updateLineListValidationRule( LineListValidationRule LineListValidationRule );

    /**
     * Get LineListValidationRule with the given identifier.
     * 
     * @param id the unique identifier of the LineListValidationRule.
     * @return the LineListValidationRule or null if it doesn't exist.
     */
    LineListValidationRule getLineListValidationRule( int id );

    /**
     * Get all LineListValidationRules.
     * 
     * @return a Collection of LineListValidationRule or null if it there are no validation rules.
     */    
    Collection<LineListValidationRule> getAllLineListValidationRules();

    /**
     * Get a LineListValidationRule with the given name.
     * 
     * @param name the name of the LineListValidationRule.
     */
    LineListValidationRule getLineListValidationRuleByName( String name );
    
}
