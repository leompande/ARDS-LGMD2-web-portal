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
import org.hisp.dhis.dataelement.DataElement;

/**
 * @author Lars Helge Overland
 * @version $Id: DataElementBatchHandler.java 5242 2008-05-25 09:23:25Z larshelg $
 */
public class DataElementBatchHandler
    extends AbstractBatchHandler<DataElement>
{
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    public DataElementBatchHandler( JdbcConfiguration config )
    {
        super( config, false, false );
    }

    // -------------------------------------------------------------------------
    // AbstractBatchHandler implementation
    // -------------------------------------------------------------------------

    protected void setTableName()
    {
        statementBuilder.setTableName( "dataelement" );
    }

    @Override
    protected void setAutoIncrementColumn()
    {
        statementBuilder.setAutoIncrementColumn( "dataelementid" );
    }

    @Override
    protected void setIdentifierColumns()
    {
        statementBuilder.setIdentifierColumn( "dataelementid" );
    }

    @Override
    protected void setIdentifierValues( DataElement dataElement )
    {        
        statementBuilder.setIdentifierValue( dataElement.getId() );
    }

    protected void setUniqueColumns()
    {
        statementBuilder.setUniqueColumn( "name" );
        statementBuilder.setUniqueColumn( "shortname" );
        statementBuilder.setUniqueColumn( "code" );
    }

    protected void setUniqueValues( DataElement dataElement )
    {
        statementBuilder.setUniqueValue( dataElement.getName() );
        statementBuilder.setUniqueValue( dataElement.getShortName() );
        statementBuilder.setUniqueValue( dataElement.getCode() );
    }
    
    protected void setColumns()
    {
        statementBuilder.setColumn( "uid" );
        statementBuilder.setColumn( "name" );
        statementBuilder.setColumn( "shortname" );
        statementBuilder.setColumn( "code" );
        statementBuilder.setColumn( "description" );
        statementBuilder.setColumn( "active" );
        statementBuilder.setColumn( "valuetype" );
        statementBuilder.setColumn( "domaintype" );
        statementBuilder.setColumn( "aggregationtype" );
        statementBuilder.setColumn( "categorycomboid" );
        statementBuilder.setColumn( "sortorder" );
        statementBuilder.setColumn( "zeroissignificant" );
    }
    
    protected void setValues( DataElement dataElement )
    {
        statementBuilder.setValue( dataElement.getUid() );
        statementBuilder.setValue( dataElement.getName() );
        statementBuilder.setValue( dataElement.getShortName() );
        statementBuilder.setValue( dataElement.getCode() );
        statementBuilder.setValue( dataElement.getDescription() );
        statementBuilder.setValue( dataElement.isActive() );
        statementBuilder.setValue( dataElement.getType() );
        statementBuilder.setValue( dataElement.getDomainType() );
        statementBuilder.setValue( dataElement.getAggregationOperator() );
        statementBuilder.setValue( dataElement.getCategoryCombo().getId() );
        statementBuilder.setValue( dataElement.getSortOrder() );
        statementBuilder.setValue( dataElement.isZeroIsSignificant() );
    }
}
