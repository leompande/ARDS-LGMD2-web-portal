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
import org.hisp.dhis.aggregation.AggregatedIndicatorValue;

/**
 * @author Lars Helge Overland
 */
public class AggregatedOrgUnitIndicatorValueBatchHandler
    extends AbstractBatchHandler<AggregatedIndicatorValue>
{
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public AggregatedOrgUnitIndicatorValueBatchHandler( JdbcConfiguration config )
    {
        super( config, true, true );
    }

    // -------------------------------------------------------------------------
    // AbstractBatchHandler implementation
    // -------------------------------------------------------------------------

    protected void setTableName()
    {
        statementBuilder.setTableName( "aggregatedorgunitindicatorvalue" );
    }

    protected void setUniqueColumns()
    {
        statementBuilder.setUniqueColumn( "indicatorid" );
        statementBuilder.setUniqueColumn( "periodid" );
        statementBuilder.setUniqueColumn( "organisationunitid" );
        statementBuilder.setUniqueColumn( "organisationunitgroupid" );
    }
    
    protected void setUniqueValues( AggregatedIndicatorValue value )
    {        
        statementBuilder.setUniqueValue( value.getIndicatorId() );
        statementBuilder.setUniqueValue( value.getPeriodId() );
        statementBuilder.setUniqueValue( value.getOrganisationUnitId() );
        statementBuilder.setUniqueValue( value.getOrganisationUnitGroupId() );
    }
    
    protected void setColumns()
    {
        statementBuilder.setColumn( "indicatorid" );
        statementBuilder.setColumn( "periodid" );
        statementBuilder.setColumn( "periodtypeid" );
        statementBuilder.setColumn( "organisationunitid" );
        statementBuilder.setColumn( "organisationunitgroupid" );
        statementBuilder.setColumn( "level" );
        statementBuilder.setColumn( "annualized" );
        statementBuilder.setColumn( "factor" );
        statementBuilder.setColumn( "value" );
        statementBuilder.setColumn( "numeratorvalue" );
        statementBuilder.setColumn( "denominatorvalue" );
    }

    protected void setValues( AggregatedIndicatorValue value )
    {        
        statementBuilder.setValue( value.getIndicatorId() );
        statementBuilder.setValue( value.getPeriodId() );
        statementBuilder.setValue( value.getPeriodTypeId() );
        statementBuilder.setValue( value.getOrganisationUnitId() );
        statementBuilder.setValue( value.getOrganisationUnitGroupId() );
        statementBuilder.setValue( value.getLevel() );
        statementBuilder.setValue( value.getAnnualized() );
        statementBuilder.setValue( value.getFactor() );
        statementBuilder.setValue( value.getValue() );
        statementBuilder.setValue( value.getNumeratorValue() );
        statementBuilder.setValue( value.getDenominatorValue() );
    }
}
