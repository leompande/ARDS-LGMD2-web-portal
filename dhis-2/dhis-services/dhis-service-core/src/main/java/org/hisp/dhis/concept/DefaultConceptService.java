package org.hisp.dhis.concept;

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

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
@Transactional
public class DefaultConceptService
    implements ConceptService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericIdentifiableObjectStore<Concept> conceptStore;

    public void setConceptStore( GenericIdentifiableObjectStore<Concept> conceptStore )
    {
        this.conceptStore = conceptStore;
    }

    // -------------------------------------------------------------------------
    // Concept
    // -------------------------------------------------------------------------

    public int saveConcept( Concept concept )
    {
        return conceptStore.save( concept );
    }

    public void updateConcept( Concept concept )
    {
        conceptStore.update( concept );
    }

    public void deleteConcept( Concept concept )
    {
        conceptStore.delete( concept );
    }

    public Concept getConcept( int conceptId )
    {
        return conceptStore.get( conceptId );
    }

    public Concept getConceptByName( String conceptName )
    {
        return conceptStore.getByName( conceptName );
    }

    public Collection<Concept> getAllConcepts()
    {
        return conceptStore.getAll();
    }

    public void generateDefaultConcept()
    {
        Concept defaultConcept = new Concept( "default" );
        
        conceptStore.save( defaultConcept );
    }

    @Override
    public int getConceptCount()
    {
        return conceptStore.getCount();
    }

    @Override
    public int getConceptCountByName( String name )
    {
        return conceptStore.getCountLikeName( name );
    }

    @Override
    public Collection<Concept> getConceptsBetween( int first, int max )
    {
        return conceptStore.getAllOrderedName( first, max );
    }

    @Override
    public Collection<Concept> getConceptsBetweenByName( String name, int first, int max )
    {
        return conceptStore.getAllLikeNameOrderedName( name, first, max );
    }
}
