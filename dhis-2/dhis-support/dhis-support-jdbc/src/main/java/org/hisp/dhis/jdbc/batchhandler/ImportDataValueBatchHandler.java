package org.hisp.dhis.jdbc.batchhandler;

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

import org.amplecode.quick.JdbcConfiguration;
import org.amplecode.quick.batchhandler.AbstractBatchHandler;
import org.hisp.dhis.importexport.ImportDataValue;

/**
 * @author Lars Helge Overland
 * @version $Id: ImportDataValueBatchHandler.java 5062 2008-05-01 18:10:35Z larshelg $
 */
public class ImportDataValueBatchHandler
    extends AbstractBatchHandler<ImportDataValue>
{
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    public ImportDataValueBatchHandler( JdbcConfiguration config )
    {
        super( config, true, true );
    }

    // -------------------------------------------------------------------------
    // AbstractBatchHandler implementation
    // -------------------------------------------------------------------------

    protected void setTableName()
    {
        statementBuilder.setTableName( "importdatavalue" );
    }

    @Override
    protected void setIdentifierColumns()
    {
        statementBuilder.setIdentifierColumn( "dataelementid" );
        statementBuilder.setIdentifierColumn( "categoryoptioncomboid" );
        statementBuilder.setIdentifierColumn( "periodid" );
        statementBuilder.setIdentifierColumn( "sourceid" );
    }
    
    @Override
    protected void setIdentifierValues( ImportDataValue value )
    {        
        statementBuilder.setIdentifierValue( value.getDataElementId() );
        statementBuilder.setIdentifierValue( value.getCategoryOptionComboId() );
        statementBuilder.setIdentifierValue( value.getPeriodId() );
        statementBuilder.setIdentifierValue( value.getSourceId() );
    }
    
    protected void setUniqueColumns()
    {
        statementBuilder.setUniqueColumn( "dataelementid" );
        statementBuilder.setUniqueColumn( "categoryoptioncomboid" );
        statementBuilder.setUniqueColumn( "periodid" );
        statementBuilder.setUniqueColumn( "sourceid" );
    }
    
    protected void setUniqueValues( ImportDataValue value )
    {        
        statementBuilder.setUniqueValue( value.getDataElementId() );
        statementBuilder.setUniqueValue( value.getCategoryOptionComboId() );
        statementBuilder.setUniqueValue( value.getPeriodId() );
        statementBuilder.setUniqueValue( value.getSourceId() );
    }
    
    protected void setColumns()
    {
        statementBuilder.setColumn( "dataelementid" );
        statementBuilder.setColumn( "categoryoptioncomboid" );
        statementBuilder.setColumn( "periodid" );
        statementBuilder.setColumn( "sourceid" );
        statementBuilder.setColumn( "value" );
        statementBuilder.setColumn( "storedby" );
        statementBuilder.setColumn( "lastupdated" );
        statementBuilder.setColumn( "comment" );
        statementBuilder.setColumn( "status" );
    }
    
    protected void setValues( ImportDataValue value )
    {        
        statementBuilder.setValue( value.getDataElementId() );
        statementBuilder.setValue( value.getCategoryOptionComboId() );
        statementBuilder.setValue( value.getPeriodId() );
        statementBuilder.setValue( value.getSourceId() );
        statementBuilder.setValue( value.getValue() );
        statementBuilder.setValue( value.getStoredBy() );
        statementBuilder.setValue( value.getTimestamp() );
        statementBuilder.setValue( value.getComment() );
        statementBuilder.setValue( value.getStatus() );
    }
}
