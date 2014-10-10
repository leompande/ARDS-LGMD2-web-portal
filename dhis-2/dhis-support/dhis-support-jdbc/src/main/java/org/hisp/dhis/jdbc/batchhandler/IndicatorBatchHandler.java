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
import org.hisp.dhis.indicator.Indicator;

/**
 * @author Lars Helge Overland
 * @version $Id: IndicatorBatchHandler.java 5811 2008-10-03 18:36:11Z larshelg $
 */
public class IndicatorBatchHandler
    extends AbstractBatchHandler<Indicator>
{
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    public IndicatorBatchHandler( JdbcConfiguration config )
    {
        super( config, false, false );
    }

    // -------------------------------------------------------------------------
    // AbstractBatchHandler implementation
    // -------------------------------------------------------------------------

    protected void setTableName()
    {
        statementBuilder.setTableName( "indicator" );
    }
    
    @Override
    protected void setAutoIncrementColumn()
    {
        statementBuilder.setAutoIncrementColumn( "indicatorid" );
    }    

    @Override
    protected void setIdentifierColumns()
    {
        statementBuilder.setIdentifierColumn( "indicatorid" );
    }
    
    @Override
    protected void setIdentifierValues( Indicator indicator )
    {   
        statementBuilder.setIdentifierValue( indicator.getId() );
    }

    protected void setUniqueColumns()
    {
        statementBuilder.setUniqueColumn( "name" );
        statementBuilder.setUniqueColumn( "shortname" );
        statementBuilder.setUniqueColumn( "code" );
    }
    
    protected void setUniqueValues( Indicator indicator )
    {        
        statementBuilder.setUniqueValue( indicator.getName() );
        statementBuilder.setUniqueValue( indicator.getShortName() );
        statementBuilder.setUniqueValue( indicator.getCode() );
    }
    
    protected void setColumns()
    {
        statementBuilder.setColumn( "uid" );
        statementBuilder.setColumn( "name" );
        statementBuilder.setColumn( "shortname" );
        statementBuilder.setColumn( "code" );
        statementBuilder.setColumn( "description" );
        statementBuilder.setColumn( "annualized" );
        statementBuilder.setColumn( "indicatortypeid" );
        statementBuilder.setColumn( "numerator" );
        statementBuilder.setColumn( "numeratordescription" );
        statementBuilder.setColumn( "denominator" );
        statementBuilder.setColumn( "denominatordescription" );
        statementBuilder.setColumn( "sortorder" );
    }
    
    protected void setValues( Indicator indicator )
    {
        statementBuilder.setValue( indicator.getUid() );
        statementBuilder.setValue( indicator.getName() );
        statementBuilder.setValue( indicator.getShortName() );
        statementBuilder.setValue( indicator.getCode() );
        statementBuilder.setValue( indicator.getDescription() );
        statementBuilder.setValue( indicator.isAnnualized() );
        statementBuilder.setValue( indicator.getIndicatorType().getId() );
        statementBuilder.setValue( indicator.getNumerator() );
        statementBuilder.setValue( indicator.getNumeratorDescription() );
        statementBuilder.setValue( indicator.getDenominator() );
        statementBuilder.setValue( indicator.getDenominatorDescription() );
        statementBuilder.setValue( indicator.getSortOrder() );
    }
}
