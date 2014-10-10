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
package org.hisp.dhis.detarget;

import java.util.Collection;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version DeTargetStore.java Jan 12, 2011 2:54:47 PM
 */
public interface DeTargetStore
{
    String ID = DeTargetStore.class.getName();

    // -------------------------------------------------------------------------
    // DeTraget
    // -------------------------------------------------------------------------

    /**
     * Adds a DeTarget.
     * 
     * @param survey The DeTarget to add.
     * @return The generated unique identifier for this DeTarget.
     */
    int addDeTarget( DeTarget  deTarget );

    /**
     * Updates a DeTarget.
     * 
     * @param survey The DeTarget to update.
     */
    void updateDeTarget( DeTarget deTarget );

    /**
     * Deletes a DeTarget.
     * 
     * @param DeTarget The DeTarget to delete.
     */
    int deleteDeTarget( DeTarget deTarget );

    /**
     * Get a DeTarget
     * 
     * @param id The unique identifier for the DeTarget to get.
     * @return The DeTarget with the given id or null if it does not exist.
     */
    DeTarget getDeTarget( int id );

    /**
     * Returns a DeTarget with the given name.
     * 
     * @param name The name.
     * @return A DeTarget with the given name.
     */
    DeTarget getDeTargetByName( String name );

    /**
     * Returns the DeTarget with the given short name.
     * 
     * @param shortName The short name.
     * @return The DeTarget with the given short name.
     */
    DeTarget getDeTargetByShortName( String shortName );
            
    /**
     * Returns all DeTarget associated with the specified source.
     */
    Collection<DeTarget> getDeTargetsBySource( OrganisationUnit source );
    
    /**
     * Returns all DeTarget associated with the specified indicator.
     */
    Collection<DeTargetMember> getDeTargetsByDataElementAndCategoryOptionCombo( DataElement dataelement ,DataElementCategoryOptionCombo deoptioncombo );

    /**
     * Get all DeTargets.
     * 
     * @return A collection containing all Surveys.
     */
    Collection<DeTarget> getAllDeTargets();
    
 

    
    // -------------------------------------------------------------------------
    // DeTargetMember
    // -------------------------------------------------------------------------
    
    /**
     * Adds a DeTargetMember.
     * 
     * @param survey The DeTarget to add.
     * @return The generated unique identifier for this DeTarget.
     */
    void addDeTargetMember( DeTargetMember  deTargetMember );

    /**
     * Updates a DeTargetMember.
     * 
     * @param DeTargetMember The DeTargetMember to update.
     */
    void updateDeTargetMember( DeTargetMember deTargetMember );

    /**
     * Deletes a DeTargetMember.
     * 
     * @param DeTargetMember The DeTargetMember to delete.
     */
   // int deleteDeTargetMember( DeTarget deTarget ,DataElement dataelement ,DataElementCategoryOptionCombo deoptioncombo );
    
    /**
     * Deletes a DeTarget.
     * 
     * @param survey The Survey to delete.
     */
    int deleteDeTargetMember( DeTargetMember  deTargetMember );
    
    /**
     * Returns Collection of DeTarget members.
     * 
     * @return List of DeTarget
     */
    Collection<DeTargetMember> getDeTargetMembers( DeTarget deTarget );
    
}
