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
import org.hisp.dhis.hr.AttributeGroup;
import org.hisp.dhis.hr.AttributeGroupService;
import org.hisp.dhis.importexport.HrGroupMemberType;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.Importer;
import org.hisp.dhis.importexport.mapping.HrNameMappingUtil;

/**
 * @author John Francis Mukulu<john.f.mukulu@gmail.com>
 * @version $Id$
 */
public class AttributeGroupImporter
    extends AbstractHrImporter<AttributeGroup> implements Importer<AttributeGroup>
{
    protected AttributeGroupService attributeGroupService;

    public AttributeGroupImporter()
    {
    }
    
    public AttributeGroupImporter( BatchHandler<AttributeGroup> batchHandler, AttributeGroupService attributeGroupService )
    {
        this.batchHandler = batchHandler;
        this.attributeGroupService = attributeGroupService;
    }
    
    @Override
    public void importObject( AttributeGroup object, ImportParams params )
    {
    	HrNameMappingUtil.addAttributeGroupMapping(object.getId(), object.getName());
        
        read( object, HrGroupMemberType.NONE, params );
    }

    @Override
    protected void importUnique( AttributeGroup object )
    {
        batchHandler.addObject( object );        
    }

    @Override
    protected void importMatching( AttributeGroup object, AttributeGroup match )
    {
    	match.setId( object.getId() );
        match.setName( object.getName() );
        attributeGroupService.updateAttributeGroup(match);
    }

    @Override
    protected AttributeGroup getMatching( AttributeGroup object )
    {
        return attributeGroupService.getAttributeGroupByName(object.getName());
    }

    @Override
    protected boolean isIdentical( AttributeGroup object, AttributeGroup existing )
    {
        return object.getName().equals( existing.getName() );
    }
}
