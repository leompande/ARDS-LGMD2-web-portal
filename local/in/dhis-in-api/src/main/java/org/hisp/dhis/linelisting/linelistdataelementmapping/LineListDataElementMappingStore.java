/*
 * Copyright (c) 2004-2012, University of Oslo
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
package org.hisp.dhis.linelisting.linelistdataelementmapping;

import java.util.Collection;

/**
 * @author Mithilesh Kumar Thakur
 * 
 * @version LineListDataElementMappingStore.java Oct 12, 2010 12:34:58 PM
 */
public interface LineListDataElementMappingStore
{
    String ID = LineListDataElementMappingStore.class.getName();

    /**
     * Adds a new LineListDataElementMapping to the database.
     * 
     * @param lineListDataElementMapping The new LineListDataElementMapping to
     *        add.
     * @return The generated identifier for this LineListDataElementMapping.
     */
    int addLineListDataElementMapping( LineListDataElementMapping lineListDataElementMapping );

    /**
     * Updates an LineListDataElementMapping.
     * 
     * @param lineListDataElementMapping The LineListDataElementMapping to
     *        update.
     */
    void updateLineListDataElementMapping( LineListDataElementMapping lineListDataElementMapping );

    /**
     * Deletes an LineListDataElementMapping from the database.
     * 
     * @param id Identifier of the LineListDataElementMapping to delete.
     */
    void deleteLineListDataElementMapping( LineListDataElementMapping lineListDataElementMapping );

    /**
     * Gets the LineListDataElementMapping with the given identifier.
     * 
     * @param id The identifier.
     * @return An LineListDataElementMapping with the given identifier.
     */
    LineListDataElementMapping getLineListDataElementMapping( int id );

    /**
     * Gets all LineListDataElementMapping.
     * 
     * @return A collection with all the LineListDataElementMappings.
     */
    Collection<LineListDataElementMapping> getAllLineListDataElementMappings();
}
