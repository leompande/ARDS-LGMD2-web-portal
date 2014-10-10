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

import org.hisp.dhis.organisationunit.OrganisationUnit;


/**
 * @author John Francis Mukulu <john.f.mukulu@gmail.com>
 * @version $Id$
 */

public interface AggregateAttributeService {
	
	String ID = AggregateAttributeService.class.getName();

    // -------------------------------------------------------------------------
    // AggregateAttribute
    // -------------------------------------------------------------------------
    
    int saveAggregateAttribute( AggregateAttribute aggregateAttribute );

    void updateAggregateAttribute( AggregateAttribute aggregateAttribute );

    void deleteAggregateAttribute( AggregateAttribute AggregateAttribute );

    AggregateAttribute getAggregateAttribute( int id );

    AggregateAttribute getAggregateAttributeByName( String name );
    
    Collection<AggregateAttribute> getAllAggregateAttribute();
    
    /**
     * Retrieves the AggregateAttribute with the given identifiers.
     * 
     * @param identifiers the identifiers of the AggregateAttribute to retrieve.
     * @return a Collection of AggregateAttribute.
     */
    Collection<AggregateAttribute> getAggregateAttribute( final Collection<Integer> identifiers );
    
    /**
     * Retrieves the AttributeOptions with the given identifiers.
     * 
     * @param identifiers the identifiers of the AttributeOptions to retrieve.
     * @return a Collection of AttributeOptions.
     */
    Collection<AttributeOptions> getAttributeOptions( Collection<Integer> identifiers );
    
    /**
     * Retrieves the AttributeOptions with the given name.
     * @param name the name.
     * @return the AttributeOption with the given name.
     */
    AttributeOptions getAttributeOptionByName( String name );
    
    /**
     * Returns all AttributeOptions.
     * 
     * @return a collection of all AttributeOptions, or an empty collection if there
     *         are no AttributeOptions.
     */
    Collection<AttributeOptions> getAllAttributeOptions();
    
    /**
     * Retrieves the Criterias with the given identifiers.
     * 
     * @param identifiers the identifiers of the Criterias to retrieve.
     * @return a Collection of Criterias.
     */
    Collection<Criteria> getCriterias( Collection<Integer> identifiers );
    
    /**
     * Retrieves the Criterias with the given name.
     * @param name the name.
     * @return the Criteria with the given name.
     */
    Criteria getCriteriaByName( String name );
    
    /**
     * Returns all Criterias.
     * 
     * @return a collection of all Criterias, or an empty collection if there
     *         are no Criterias.
     */
    Collection<Criteria> getAllCriterias();
    
    /**
     * 
     * 
     * @return a number of  person with certain attributes 
     */
    void getCountPersonByAggregateAttribute(AggregateAttribute aggregateAttribute, OrganisationUnit organisationUnit);
}
