package org.hisp.dhis.importexport.importer;


import org.amplecode.quick.BatchHandler;
import org.hisp.dhis.hr.DataType;
import org.hisp.dhis.hr.DataTypeService;
import org.hisp.dhis.importexport.HrGroupMemberType;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.Importer;
import org.hisp.dhis.importexport.mapping.HrNameMappingUtil;

/*
 * Copyright (c) 2004-2005, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the <ORGANIZATION> nor the names of its contributors may
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

/**
 *
 * @author John Francis Mukulu <john.f.mukulu@gmail.com>
 * @version created 15-Sep-2010
 */
public class DataTypeImporter
    extends AbstractHrImporter<DataType> implements Importer<DataType>
{
    protected DataTypeService dataTypeService;

    public DataTypeImporter()
    {
    }

    public DataTypeImporter( BatchHandler<DataType> batchHandler, DataTypeService dataTypeService )
    {
        this.batchHandler = batchHandler;
        this.dataTypeService = dataTypeService;
    }

    @Override
    public void importObject( DataType object, ImportParams params )
    {
        HrNameMappingUtil.addDataTypeMapping( object.getId(), object.getName() );

        read( object, HrGroupMemberType.NONE, params );
    }

    @Override
    protected void importUnique( DataType object )
    {
        batchHandler.addObject( object );
    }

    @Override
    protected void importMatching( DataType object, DataType match )
    {
        throw new UnsupportedOperationException( "DataType can only be unique or duplicate" );
    }

    @Override
    protected DataType getMatching( DataType object )
    {
        return dataTypeService.getDataTypeByName( object.getName() );
    }

    @Override
    protected boolean isIdentical( DataType object, DataType existing )
    {
        return object.getName().equals( existing.getName() );
    }
}
