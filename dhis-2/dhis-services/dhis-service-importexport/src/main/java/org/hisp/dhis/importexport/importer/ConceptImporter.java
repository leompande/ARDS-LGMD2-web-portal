package org.hisp.dhis.importexport.importer;


import org.amplecode.quick.BatchHandler;
import org.hisp.dhis.concept.Concept;
import org.hisp.dhis.concept.ConceptService;
import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.Importer;
import org.hisp.dhis.importexport.mapping.NameMappingUtil;

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

/**
 *
 * @author bobj
 * @version created 15-Sep-2010
 */
public class ConceptImporter
    extends AbstractImporter<Concept> implements Importer<Concept>
{
    protected ConceptService conceptService;

    public ConceptImporter()
    {
    }

    public ConceptImporter( BatchHandler<Concept> batchHandler, ConceptService conceptService )
    {
        this.batchHandler = batchHandler;
        this.conceptService = conceptService;
    }

    @Override
    public void importObject( Concept object, ImportParams params )
    {
        NameMappingUtil.addConceptMapping( object.getId(), object.getName() );

        read( object, GroupMemberType.NONE, params );
    }

    @Override
    protected void importUnique( Concept object )
    {
        batchHandler.addObject( object );
    }

    @Override
    protected void importMatching( Concept object, Concept match )
    {
        log.info( object.getName() + ": Concept can only be unique or duplicate" );
    }

    @Override
    protected Concept getMatching( Concept object )
    {
        return conceptService.getConceptByName( object.getName() );
    }

    @Override
    protected boolean isIdentical( Concept object, Concept existing )
    {
        return object.getName().equals( existing.getName() );
    }
}
