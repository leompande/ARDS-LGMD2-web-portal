package org.hisp.dhis.importexport.importer;

/*
 * Copyright (c) 2004-2012, University of Oslo, University Of Dar es salaam
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

import org.amplecode.quick.BatchHandler;
import org.hisp.dhis.hr.AttributeOptions;
import org.hisp.dhis.hr.AttributeOptionsService;
import org.hisp.dhis.importexport.HrGroupMemberType;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.Importer;
import org.hisp.dhis.importexport.mapping.HrNameMappingUtil;

/**
 * @author John Francis Mukulu<john.f.mukulu@gmail.com>
 * @version $Id$
 */
public class AttributeOptionsImporter
    extends AbstractHrImporter<AttributeOptions> implements Importer<AttributeOptions>
{
    protected AttributeOptionsService attributeOptionsService;

    public AttributeOptionsImporter()
    {
    }
    
    public AttributeOptionsImporter( BatchHandler<AttributeOptions> batchHandler, AttributeOptionsService attributeOptionsService )
    {
        this.batchHandler = batchHandler;
        this.attributeOptionsService = attributeOptionsService;
    }
    
    @Override
    public void importObject( AttributeOptions object, ImportParams params )
    {
    	HrNameMappingUtil.addAttributeOptionsMapping(object.getId(), object.getValue());
        
        read( object, HrGroupMemberType.NONE, params );
    }

    @Override
    protected void importUnique( AttributeOptions object )
    {
        batchHandler.addObject( object );        
    }

    @Override
    protected void importMatching( AttributeOptions object, AttributeOptions match )
    {
    	match.setId( object.getId() );
    	match.setAttribute(object.getAttribute());
        match.setValue( object.getValue() );
        attributeOptionsService.updateAttributeOptions(match);
    }

    @Override
    protected AttributeOptions getMatching( AttributeOptions object )
    {
        return attributeOptionsService.getAttributeOptionsByValue(object.getValue());
    }

    @Override
    protected boolean isIdentical( AttributeOptions object, AttributeOptions existing )
    {
        return object.getValue().equals( existing.getValue() );
    }
}
