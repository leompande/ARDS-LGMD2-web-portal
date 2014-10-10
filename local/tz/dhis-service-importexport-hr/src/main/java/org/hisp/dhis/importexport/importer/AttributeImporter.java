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
import org.hisp.dhis.hr.Attribute;
import org.hisp.dhis.hr.AttributeService;
import org.hisp.dhis.importexport.HrGroupMemberType;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.Importer;
import org.hisp.dhis.importexport.mapping.HrNameMappingUtil;

/**
 * @author John Francis Mukulu<john.f.mukulu@gmail.com>
 * @version $Id$
 */
public class AttributeImporter
    extends AbstractHrImporter<Attribute> implements Importer<Attribute>
{
    protected AttributeService attributeService;

    public AttributeImporter()
    {
    }
    
    public AttributeImporter( BatchHandler<Attribute> batchHandler, AttributeService attributeService )
    {
        this.batchHandler = batchHandler;
        this.attributeService = attributeService;
    }
    
    @Override
    public void importObject( Attribute object, ImportParams params )
    {
    	HrNameMappingUtil.addAttributeMapping(object.getId(), object.getName());
        
        read( object, HrGroupMemberType.NONE, params );
    }

    @Override
    protected void importUnique( Attribute object )
    {
        batchHandler.addObject( object );        
    }

    @Override
    protected void importMatching( Attribute object, Attribute match )
    {
    	match.setId( object.getId() );
        match.setName( object.getName() );
        match.setDescription(object.getDescription());
        match.setCaption(object.getCaption());
        match.setCompulsory(object.getCompulsory());
        match.setIsUnique(object.getIsUnique());
        match.setHistory(object.getHistory());
        match.setDataType(object.getDataType());
        match.setInputType(object.getInputType());
        attributeService.updateAttribute(match);
    }

    @Override
    protected Attribute getMatching( Attribute object )
    {
        return attributeService.getAttributeByName( object.getName() );
    }

    @Override
    protected boolean isIdentical( Attribute object, Attribute existing )
    {
        return object.getName().equals( existing.getName() );
    }
}
