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
import org.hisp.dhis.completeness.DataSetCompletenessResult;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataSetCompletenessResultBatchHandler
    extends AbstractBatchHandler<DataSetCompletenessResult>
{
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    public DataSetCompletenessResultBatchHandler( JdbcConfiguration config )
    {
        super( config, true, true );
    }

    // -------------------------------------------------------------------------
    // AbstractBatchHandler implementation
    // -------------------------------------------------------------------------

    protected void setTableName()
    {
        statementBuilder.setTableName( "aggregateddatasetcompleteness" );
    }
    
    @Override
    protected void setIdentifierColumns()
    {
        statementBuilder.setIdentifierColumn( "datasetid" );
        statementBuilder.setIdentifierColumn( "periodid" );
        statementBuilder.setIdentifierColumn( "organisationunitid" );
    }
    
    @Override
    protected void setIdentifierValues( DataSetCompletenessResult result )
    {
        statementBuilder.setIdentifierValue( result.getDataSetId() );
        statementBuilder.setIdentifierValue( result.getPeriodId() );
        statementBuilder.setIdentifierValue( result.getOrganisationUnitId() );
    }

    @Override
    protected void setUniqueColumns()
    {
        statementBuilder.setUniqueColumn( "datasetid" );
        statementBuilder.setUniqueColumn( "periodid" );
        statementBuilder.setUniqueColumn( "organisationunitid" );
    }
    
    @Override
    protected void setUniqueValues( DataSetCompletenessResult result )
    {
        statementBuilder.setUniqueValue( result.getDataSetId() );
        statementBuilder.setUniqueValue( result.getPeriodId() );
        statementBuilder.setUniqueValue( result.getOrganisationUnitId() );
    }
    
    protected void setColumns()
    {
        statementBuilder.setColumn( "datasetid" );
        statementBuilder.setColumn( "periodid" );
        statementBuilder.setColumn( "periodname" );
        statementBuilder.setColumn( "organisationunitid" );
        statementBuilder.setColumn( "sources" );
        statementBuilder.setColumn( "registrations" );
        statementBuilder.setColumn( "registrationsOnTime" );
        statementBuilder.setColumn( "value" );
        statementBuilder.setColumn( "valueOnTime" );
    }
    
    protected void setValues( DataSetCompletenessResult result )
    {        
        statementBuilder.setValue( result.getDataSetId() );
        statementBuilder.setValue( result.getPeriodId() );
        statementBuilder.setValue( result.getPeriodName() );
        statementBuilder.setValue( result.getOrganisationUnitId() );
        statementBuilder.setValue( result.getSources() );
        statementBuilder.setValue( result.getRegistrations() );
        statementBuilder.setValue( result.getRegistrationsOnTime() );
        statementBuilder.setValue( result.getPercentage() );
        statementBuilder.setValue( result.getPercentageOnTime() );
    }
}
