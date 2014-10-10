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
 * 		   John Francis Mukulu <john.f.mukulu@gmail.com>
 * @version $Id$
 */

public interface AttributeGroupService {
	
	String ID = AttributeGroupService.class.getName();

    // -------------------------------------------------------------------------
    // AttributeGroup
    // -------------------------------------------------------------------------
    
    int saveAttributeGroup( AttributeGroup attributeGroup );

    void updateAttributeGroup( AttributeGroup attributeGroup );

    void deleteAttributeGroup( AttributeGroup attributeGroup );

    AttributeGroup getAttributeGroup( int id );

    AttributeGroup getAttributeGroupByName( String name );
    
    Collection<AttributeGroup> getAllAttributeGroup();
    
    /**
     * Retrieves the AttributeGroups with the given identifiers.
     * 
     * @param identifiers the identifiers of the AttributeGroups to retrieve.
     * @return a Collection of AttributeGroups.
     */
    Collection<AttributeGroup> getAttributeGroups( final Collection<Integer> identifiers );
    
    /**
     * Retrieves the GroupAttributes with the given identifiers.
     * 
     * @param identifiers the identifiers of the GroupAttribute to retrieve.
     * @return a Collection of Attributes.
     */
    Collection<Attribute> getGroupAttributes( Collection<Integer> identifiers );
    
    /**
     * Retrieves the GroupAttribute with the given name.
     * @param name the name.
     * @return the Attribute with the given name.
     */
    Attribute getGroupAttributeByName( String name );
    
    /**
     * Returns all GroupAttributes.
     * 
     * @return a collection of all Attributes, or an empty collection if there
     *         are no Attributes.
     */
    Collection<Attribute> getAllGroupAttributes();

}
