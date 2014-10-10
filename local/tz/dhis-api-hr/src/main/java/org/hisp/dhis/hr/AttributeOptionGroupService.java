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

package org.hisp.dhis.hr;

import java.util.Collection;

/**
 * @author Wilfred Felix Senyoni
 *         John Francis Mukulu <john.f.mukulu@gmail.com>
 * @version $Id$
 */

public interface AttributeOptionGroupService {
	
	String ID = AttributeOptionGroupService.class.getName();

    // -------------------------------------------------------------------------
    // AttributeGroup
    // -------------------------------------------------------------------------
    
    int saveAttributeOptionGroup( AttributeOptionGroup attributeOptionGroup );

    void updateAttributeOptionGroup( AttributeOptionGroup attributeOptionGroup );

    void deleteAttributeOptionGroup( AttributeOptionGroup attributeOptionGroup );

    AttributeOptionGroup getAttributeOptionGroup( int id );

    AttributeOptionGroup getAttributeOptionGroupByName( String name );
    
    Collection<AttributeOptionGroup> getAllAttributeOptionGroup();
    
    /**
     * Retrieves the AttributeOptionGroups with the given identifiers.
     * 
     * @param identifiers the identifiers of the AttributeOptionGroups to retrieve.
     * @return a Collection of AttributeOptionGroups.
     */
    public Collection<AttributeOptionGroup> getAttributeOptionGroups( final Collection<Integer> identifiers );
    
    /**
     * Retrieves the Group AttributeOptions with the given identifiers.
     * 
     * @param identifiers the identifiers of the GroupAttributeOptions to retrieve.
     * @return a Collection of AttributeOptions.
     */
    Collection<AttributeOptions> getGroupAttributeOptions( Collection<Integer> identifiers );
    
    /**
     * Retrieves the Group AttributeOptions with the given name.
     * @param name the name.
     * @return the AttributeOptions with the given name.
     */
    AttributeOptions getGroupAttributeOptionByName( String name );
    
    /**
     * Returns all Group AttributeOptions.
     * 
     * @return a collection of all AttributeOptions, or an empty collection if there
     *         are no AttributeOptions.
     */
    Collection<AttributeOptions> getAllGroupAttributeOptions();
}
