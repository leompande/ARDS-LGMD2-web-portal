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
import org.hisp.dhis.dataelement.DataElementCategory;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataElementCategoryBatchHandler
    extends AbstractBatchHandler<DataElementCategory>
{
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public DataElementCategoryBatchHandler( JdbcConfiguration config )
    {
        super( config, false, false );
    }
    
    // -------------------------------------------------------------------------
    // AbstractBatchHandler implementation
    // -------------------------------------------------------------------------

    protected void setTableName()
    {
        statementBuilder.setTableName( "dataelementcategory" );
    }

    @Override
    protected void setAutoIncrementColumn()
    {   
        statementBuilder.setAutoIncrementColumn( "categoryid" );
    }

    @Override
    protected void setIdentifierColumns()
    {
        statementBuilder.setIdentifierColumn( "categoryid" );
    }

    @Override
    protected void setIdentifierValues( DataElementCategory category )
    {                
        statementBuilder.setIdentifierValue( category.getId() );
    }

    protected void setUniqueColumns()
    {
        statementBuilder.setUniqueColumn( "name" );
    }

    protected void setUniqueValues( DataElementCategory category )
    {                
        statementBuilder.setUniqueValue( category.getName() );
    }
    
    protected void setColumns()
    {
        statementBuilder.setColumn( "uid" );
        statementBuilder.setColumn( "code" );
        statementBuilder.setColumn( "name" );
        statementBuilder.setColumn( "conceptid" );
    }
    
    protected void setValues( DataElementCategory category )
    {
        statementBuilder.setValue( category.getUid() );
        statementBuilder.setValue( category.getCode() );
        statementBuilder.setValue( category.getName() );
        statementBuilder.setValue( category.getConcept().getId() );
    }
}
