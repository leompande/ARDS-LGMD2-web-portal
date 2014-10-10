package org.hisp.dhis.relationship;

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

import org.hisp.dhis.patient.Patient;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
@Transactional
public class DefaultRelationshipService
    implements RelationshipService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private RelationshipStore relationshipStore;

    public void setRelationshipStore( RelationshipStore relationshipStore )
    {
        this.relationshipStore = relationshipStore;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    public void deleteRelationship( Relationship relationship )
    {
        relationshipStore.delete( relationship );
    }

    public Collection<Relationship> getAllRelationships()
    {
        return relationshipStore.getAll();
    }

    public Relationship getRelationship( int id )
    {
        return relationshipStore.get( id );
    }

    public Collection<Relationship> getRelationshipsForPatient( Patient patient )
    {
        return relationshipStore.getForPatient( patient );
    }

    public int saveRelationship( Relationship relationship )
    {
        return relationshipStore.save( relationship );
    }

    public void updateRelationship( Relationship relationship )
    {
        relationshipStore.update( relationship );
    }

    public Collection<Relationship> getRelationshipsByRelationshipType( RelationshipType relationshipType )
    {
        return relationshipStore.getByRelationshipType( relationshipType );
    }

    public Collection<Relationship> getRelationships( Patient patientA, RelationshipType relationshipType )
    {
        return relationshipStore.get( patientA, relationshipType );
    }

    public Relationship getRelationship( Patient patientA, Patient patientB, RelationshipType relationshipType )
    {
        return relationshipStore.get( patientA, patientB, relationshipType );
    }
    
    public Relationship getRelationship( Patient patientA, Patient patientB )
    {
        return relationshipStore.get( patientA, patientB );
    }
}
