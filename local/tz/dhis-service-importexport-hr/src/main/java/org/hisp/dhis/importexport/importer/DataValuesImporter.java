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
import org.hisp.dhis.hr.DataValues;
import org.hisp.dhis.hr.DataValuesService;
import org.hisp.dhis.importexport.HrGroupMemberType;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.Importer;
import org.hisp.dhis.importexport.mapping.HrNameMappingUtil;
import org.hisp.dhis.system.util.DateUtils;

/**
 * @author John Francis Mukulu<john.f.mukulu@gmail.com>
 * @version $Id$
 */
public class DataValuesImporter
    extends AbstractHrImporter<DataValues> implements Importer<DataValues>
{
    protected DataValuesService dataValuesService;

    public DataValuesImporter()
    {
    }
    
    public DataValuesImporter( BatchHandler<DataValues> batchHandler, DataValuesService dataValuesService )
    {
        this.batchHandler = batchHandler;
        this.dataValuesService = dataValuesService;
    }
    
    @Override
    public void importObject( DataValues object, ImportParams params )
    {
        
        read( object, HrGroupMemberType.NONE, params );
    }

    @Override
    protected void importUnique( DataValues object )
    {
        batchHandler.addObject( object );        
    }

    @Override
    protected void importMatching( DataValues object, DataValues match )
    {
    	match.setId( object.getId() );
        match.setValue( object.getValue() );
        match.setStoredBy(object.getStoredBy());
        match.setTimestamp(object.getTimestamp());
        match.setPerson(object.getPerson());
        match.setAttribute(object.getAttribute());
        dataValuesService.updateDataValues(match);
    }

    @Override
    protected DataValues getMatching( DataValues object )
    {
        return dataValuesService.getDataValues(object.getId());
    }

    @Override
    protected boolean isIdentical( DataValues object, DataValues existing )
    {
        return object.getValue().equals( existing.getValue() );
    }
}
